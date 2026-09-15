import { access, cp, rm, writeFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const website = fileURLToPath(new URL('../', import.meta.url));
const root = path.resolve(website, '..');
const distribution = process.env.DEMO_DISTRIBUTION ?? 'development';
const outputDirectory = {
  development: 'developmentExecutable',
  production: 'productionExecutable',
}[distribution];

if (!outputDirectory) {
  throw new Error(`Unknown demo distribution: ${distribution}`);
}

const demoDistribution = path.join(root, 'demo/build/dist/wasmJs', outputDirectory);
const target = path.join(website, 'public/composeunstyled-v2-demos');

await access(path.join(demoDistribution, 'index.html'));
await rm(target, { recursive: true, force: true });
await cp(demoDistribution, target, { recursive: true });
await writeFile(path.join(target, '.revision'), `${Date.now()}\n`);
console.log('Refreshed demo assets.');
