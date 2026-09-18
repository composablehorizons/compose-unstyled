import { copyFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const dist = fileURLToPath(new URL('../dist/', import.meta.url));
await copyFile(path.join(dist, 'sitemap-index.xml'), path.join(dist, 'sitemap.xml'));
