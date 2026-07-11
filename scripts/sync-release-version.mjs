import fs from 'node:fs';

const packageJson = JSON.parse(fs.readFileSync('package.json', 'utf8'));
const version = packageJson.version;

if (typeof version !== 'string' || !/^\d+\.\d+\.\d+(?:[-+][0-9A-Za-z.-]+)?$/.test(version)) {
  throw new Error(`Invalid package version: ${version}`);
}

const versionCatalogPath = 'gradle/libs.versions.toml';
const versionCatalog = fs.readFileSync(versionCatalogPath, 'utf8');
const updatedVersionCatalog = versionCatalog.replace(
  /^unstyled = ".*"$/m,
  `unstyled = "${version}"`,
);

if (updatedVersionCatalog === versionCatalog) {
  throw new Error(`Could not find the Compose Unstyled version in ${versionCatalogPath}`);
}

fs.writeFileSync(versionCatalogPath, updatedVersionCatalog);

const changelogPath = 'CHANGELOG.md';
const changelogHeader = `# Changelog

All notable changes to this project will be documented in this file.

This project uses [Changesets](https://github.com/changesets/changesets) to collect release notes
from pull requests.

## [Unreleased]`;
const date = new Date().toISOString().slice(0, 10);
const changelog = fs.readFileSync(changelogPath, 'utf8');
const legacyPreamblePattern = /All notable changes to this project will be documented in this file\.\n\nThe format is based on \[Keep a Changelog\]\(https:\/\/keepachangelog\.com\/en\/1\.0\.0\/\),\nand this project adheres to \[Semantic Versioning\]\(https:\/\/semver\.org\/spec\/v2\.0\.0\.html\)\.\n\n## \[Unreleased\]\n\n?/g;
const changesetsHeaderPattern = /^# Changelog\n\n?/gm;
const generatedVersionHeading = new RegExp(`^## ${escapeRegExp(version)}$`, 'm');
const normalizedVersionHeading = new RegExp(
  `^## \\[${escapeRegExp(version)}\\](?: - \\d{4}-\\d{2}-\\d{2})?$`,
  'm',
);

let changelogBody = changelog
  .replace(legacyPreamblePattern, '')
  .replace(changesetsHeaderPattern, '')
  .trimStart();

if (generatedVersionHeading.test(changelogBody)) {
  changelogBody = changelogBody.replace(
    generatedVersionHeading,
    `## [${version}] - ${date}`,
  );
} else if (normalizedVersionHeading.test(changelogBody)) {
  changelogBody = changelogBody.replace(
    normalizedVersionHeading,
    `## [${version}] - ${date}`,
  );
}

fs.writeFileSync(changelogPath, `${changelogHeader}\n\n${changelogBody}`);

function escapeRegExp(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}
