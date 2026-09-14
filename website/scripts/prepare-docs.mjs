import { readFile, writeFile, mkdir, rm, cp, access, readdir } from 'node:fs/promises';
import { execFileSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { sitePath, siteUrl } from '../site.config.mjs';
import { parse, stringify } from 'yaml';

const website = fileURLToPath(new URL('../', import.meta.url));
const root = path.resolve(website, '..');
const read = (file) => readFile(path.join(root, file), 'utf8');
const navigation = parse(await read('docs/docs.yml'));
const sources = parse(await read('docs/sources.yml')).demos;
const version = (await read('gradle/libs.versions.toml')).match(/^unstyled\s*=\s*"([^"]+)"/m)?.[1];
if (!version) throw new Error('Missing library version');
const demoDistribution = path.join(root, 'demo/build/dist/wasmJs/productionExecutable');
try {
  await access(path.join(demoDistribution, 'index.html'));
} catch {
  throw new Error('Build the demo once with ./gradlew :demo:wasmJsBrowserDistribution before preparing the site.');
}

const generated = path.join(root, 'build/generated/website-docs/pages');
execFileSync('bun', ['scripts/generate-compose-unstyled-api.mjs', generated], { cwd: root, stdio: 'inherit' });
const contentDir = path.join(website, 'src/pages/docs');
const publicDir = path.join(website, 'public');
await mkdir(contentDir, { recursive: true });
for (const file of await readdir(contentDir)) {
  if (file.endsWith('.md')) await rm(path.join(contentDir, file));
}
await rm(path.join(publicDir, 'docs'), { recursive: true, force: true });
await mkdir(path.join(publicDir, 'docs'), { recursive: true });

const escape = text => text.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;');
const primitives = navigation.sections.find(section => section.title === 'Primitives').pages;
const componentLinks = primitives.map(page => `[${page.title}](/docs/${page.slug}/)`).join('\n\n');
const componentList = `<ul>${primitives.map(page => `<li><a href="/docs/${page.slug}/">${escape(page.title)}</a></li>`).join('')}</ul>`;
const llms = [`# Compose Unstyled ${version}`, '', '> Renderless components for Jetpack Compose and Compose Multiplatform.', '', `These docs describe version ${version}. Examples use this version's APIs.`, ''];
const full = [`# Compose Unstyled ${version}`, ''];
let count = 0;

for (const section of navigation.sections) {
  llms.push(`## ${section.title}`, '');
  for (const page of section.pages) {
    const input = await readFile(path.join(generated, `${page.slug}.md`), 'utf8');
    const match = input.match(/^---\r?\n([\s\S]*?)\r?\n---\r?\n/);
    if (!match) throw new Error(`Missing frontmatter: ${page.slug}`);
    const metadata = parse(match[1]);
    const body = input.slice(match[0].length)
      .replaceAll('{{compose_unstyled_version}}', version)
      .replaceAll('/compose-unstyled/docs/', '/docs/')
      .replaceAll('](/docs/androidx.', '](https://composables.com/docs/androidx.')
      .replace(/\]\(([A-Za-z0-9._-]+)\.md(#[^)]*)?\)/g, (_, slug, hash = '') => `](/docs/${slug}/${hash})`);
    let htmlBody = body.replaceAll('{{unstyled_component_grid}}', componentList);
    let markdownBody = body.replaceAll('{{unstyled_component_grid}}', componentLinks);

    for (const marker of body.matchAll(/<UnstyledDemo\s+id="([A-Za-z0-9._-]+)"\s*\/>/g)) {
      const id = marker[1];
      const file = sources.files[id];
      if (!file) throw new Error(`Unknown demo: ${id}`);
      const source = (await read(`${sources.root}/${file}`))
        .replace(/^\s*\/\*[\s\S]*?\*\/\s*/, '')
        .replace(/^\s*package\s+[A-Za-z0-9_.]+\s*\n+/, '').trim();
      const code = `\n\n\`\`\`kotlin\n${source}\n\`\`\`\n\n`;
      htmlBody = htmlBody.replace(marker[0], `<iframe src="/composeunstyled-v2-demos/index.html?id=${id}" title="${escape(page.title)} interactive demo" width="600" height="450" loading="lazy"></iframe>\n\n<p><a href="/composeunstyled-v2-demos/index.html?id=${id}">OPEN DEMO</a></p>\n\n<details>\n<summary>VIEW KOTLIN SOURCE</summary>\n${code}<a href="https://github.com/composablehorizons/compose-unstyled/blob/main/${sources.root}/${file}">OPEN SOURCE ON GITHUB</a>\n</details>\n`);
      markdownBody = markdownBody.replace(marker[0], `### Example: ${id}\n${code}`);
    }

    if (/<ApiReference|<UnstyledDemo|\{\{/.test(htmlBody)) throw new Error(`Unresolved marker: ${page.slug}`);
    // Generated content bypasses Astro's link handling, so prefix its local URLs here.
    htmlBody = htmlBody
      .replace(/\]\(\/(?!\/)/g, `](${sitePath('/')}`)
      .replace(/\b(src|href)="\/(?!\/)/g, (_, attr) => `${attr}="${sitePath('/')}`);
    await writeFile(path.join(contentDir, `${page.slug}.md`), `---\n${stringify({
      layout: '../../layouts/DocsLayout.astro',
      title: metadata.title,
      description: metadata.description,
      markdownUrl: sitePath(`/docs/${page.slug}.md`),
    })}---\n${htmlBody}`);
    markdownBody = markdownBody
      .replace(/\]\(\/docs\/([^/)]+)\/(#[^)]*)?\)/g, (_, slug, hash = '') => `](${siteUrl(`/docs/${slug}.md`)}${hash})`)
      .replace(/\b(src|href)="\//g, (_, attr) => `${attr}="${siteUrl('/')}`);
    const markdown = `# ${metadata.title}\n\nCompose Unstyled ${version}\n\n${markdownBody}`;
    await writeFile(path.join(publicDir, 'docs', `${page.slug}.md`), markdown);
    llms.push(`- [${metadata.title}](${siteUrl(`/docs/${page.slug}.md`)}): ${metadata.description || metadata.title}`);
    full.push(markdown);
    count++;
  }
  llms.push('');
}

await writeFile(path.join(publicDir, 'llms.txt'), llms.join('\n'));
await writeFile(path.join(publicDir, 'llms-full.txt'), full.join('\n\n---\n\n'));
for (const [source, target] of [
  [demoDistribution, 'composeunstyled-v2-demos'],
  [path.join(root, 'docs/assets'), 'composeunstyled-v2-assets'],
]) {
  await rm(path.join(publicDir, target), { recursive: true, force: true });
  await cp(source, path.join(publicDir, target), { recursive: true });
}
console.log(`Prepared ${count} docs pages, Kotlin examples, demo assets, and LLM files for ${version}.`);
