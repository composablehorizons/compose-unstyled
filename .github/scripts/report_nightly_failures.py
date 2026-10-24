"""Report distinct nightly failures without posting repeated comments."""

import hashlib
import json
import os
import re
import subprocess
import time


LABEL = "nightly-failure"


def api(path, **fields):
    command = ["gh", "api", path]
    if fields:
        command += ["--method", "PATCH" if "/issues/" in path else "POST", "--input", "-"]
    result = subprocess.run(command, input=json.dumps(fields) if fields else None,
                            text=True, capture_output=True, check=True)
    return json.loads(result.stdout) if result.stdout else None


def pages(path):
    result = subprocess.run(["gh", "api", "--paginate", "--slurp", path],
                            text=True, capture_output=True, check=True)
    return json.loads(result.stdout)


def failures(job, log):
    # Job logs include timestamps; strip those and terminal color sequences first.
    lines = [re.sub(r"^\S+Z\s+", "", line).strip()
             for line in re.sub(r"\x1b\[[0-9;]*m", "", log).splitlines()]
    # A recovered download failure must not obscure the final build failure.
    retry = next((index for index in range(len(lines) - 1, -1, -1)
                  if lines[index].startswith(("Retrying Android SDK installation", "Retrying Android test dependency preparation"))), None)
    if retry is not None:
        lines = lines[retry + 1:]
    tests = {}
    for index, line in enumerate(lines):
        match = re.match(r"([\w.$]+) > (.+?)\s+FAILED\s*$", line)
        if match:
            test = re.sub(r"\[emulator-.*\]$", "", match[2])
            detail = "\n".join(lines[index:index + 4])
            tests[f"{match[1]}.{test}"] = detail
    if tests:
        return [(f"Test failure: {test}", f"test:{test}", detail)
                for test, detail in tests.items()]

    # Prefer the underlying cause over the generic shell exit-code wrapper.
    infrastructure_patterns = [r".*Received status code (?:429|5\d\d).*",
                               r".*Server returned HTTP response code: (?:429|5\d\d).*",
                               r".*Error on ZipFile.*", r".*Broken AVD system path.*",
                               r".*Read timed out.*", r".*Connect timed out.*",
                               r".*Android emulator setup failed after 3 attempts.*"]
    infrastructure_error = next((line for pattern in infrastructure_patterns for line in lines
                                 if re.fullmatch(pattern, line)), None)
    patterns = [r".*Received status code \d+.*", r".*Error on ZipFile.*",
                r".*Execution failed for task .*", r".*##\[error\].*"]
    error = infrastructure_error or next((line for pattern in patterns for line in lines
                  if re.fullmatch(pattern, line)), "Failed job; see linked logs for details.")
    index = lines.index(error) if error in lines else 0
    detail = "\n".join(lines[max(0, index - 3):index + 6]) if lines else error
    error = re.sub(r"https?://\S+", "<download URL>", error)
    error = re.sub(r"##\[error\]", "", error).strip()
    step = next((step["name"] for step in job.get("steps", [])
                 if step.get("conclusion") == "failure"), "Unknown step")
    category = "Infrastructure failure" if infrastructure_error or step in {
        "Install Android SDK", "Start Android emulator"
    } else "Build/job failure"
    return [(f"{category}: {job['name'].split(' / ')[0]}: {error}",
             f"job:{job['name']}:{step}:{error}", detail)]


def job_log(base, job_id):
    for attempt in range(3):
        result = subprocess.run(["gh", "api", f"{base}/actions/jobs/{job_id}/logs"],
                                text=True, capture_output=True)
        if result.returncode == 0 and result.stdout.strip():
            return result.stdout
        if attempt < 2:
            time.sleep(5)
    raise RuntimeError(f"Cannot fetch logs for job {job_id}; refusing to create a generic failure issue")


def marker(identity):
    return "<!-- nightly-failure:" + hashlib.sha256(identity.encode()).hexdigest() + " -->"


def report_body(identity, detail, occurrences, run_url, job_url):
    # Fence details so errors cannot accidentally become Markdown instructions.
    detail = detail.replace("```", "''' ")
    return (f"{marker(identity)}\n\nNightly checks detected this failure on `main`.\n\n"
            f"```text\n{detail[:6000]}\n```\n\n"
            f"Occurrences: {occurrences}\n\nLatest run: {run_url}\n\nFailed job: {job_url}\n\n"
            "Repeated occurrences update this report without adding comments. "
            "A passing run does not automatically close it.\n")


def main():
    repo = os.environ["GITHUB_REPOSITORY"]
    run_id = os.environ.get("NIGHTLY_RUN_ID", os.environ["GITHUB_RUN_ID"])
    base = f"repos/{repo}"
    jobs = [job for page in pages(f"{base}/actions/runs/{run_id}/jobs?per_page=100")
            for job in page["jobs"] if job["conclusion"] == "failure"]
    issues = [issue for page in pages(f"{base}/issues?state=open&labels={LABEL}&per_page=100")
              for issue in page if "pull_request" not in issue]
    # The label is already used by the old reporter; tolerate a new repository too.
    labels = [label for page in pages(f"{base}/labels?per_page=100") for label in page]
    if not any(label["name"] == LABEL for label in labels):
        api(f"{base}/labels", name=LABEL, color="d73a4a",
            description="Failure detected by nightly validation")
    run_url = f"https://github.com/{repo}/actions/runs/{run_id}"
    seen = set()
    for job in jobs:
        log = job_log(base, job["id"])
        for title, identity, detail in failures(job, log):
            if identity in seen:
                continue
            seen.add(identity)
            existing = next((issue for issue in issues
                             if marker(identity) in (issue.get("body") or "")), None)
            old_body = (existing.get("body") or "") if existing else ""
            count = re.search(r"^Occurrences: (\d+)$", old_body, re.MULTILINE)
            occurrences = int(count[1]) if count else 0
            if f"Latest run: {run_url}\n" not in old_body:
                occurrences += 1
            body = report_body(identity, detail, occurrences, run_url, job["html_url"])
            if existing:
                api(f"{base}/issues/{existing['number']}", body=body)
            else:
                api(f"{base}/issues", title=title[:256], body=body, labels=[LABEL])


if __name__ == "__main__":
    main()
