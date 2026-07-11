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
