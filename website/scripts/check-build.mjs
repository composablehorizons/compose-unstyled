import assert from 'node:assert/strict';
import { existsSync, readFileSync, readdirSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { sitePath, siteUrl } from '../site.config.mjs';

const dist = fileURLToPath(new URL('../dist/', import.meta.url));
const files = readdirSync(dist, { recursive: true });
const pages = files.filter(file => file.endsWith('.html') &&
  (file === 'index.html' || file.startsWith('docs/')));
assert(pages.length > 2, 'Documentation pages were not generated');
let links = 0;

for (const file of pages) {
  const html = readFileSync(path.join(dist, file), 'utf8');
  for (const [tag] of html.matchAll(/<[a-z][^>]*>/gi)) {
    for (const [, value] of tag.matchAll(/\b(?:href|src)=["']([^"']+)["']/g)) {
      if (!value.startsWith('/') || value.startsWith('//')) continue;
      const pathname = new URL(value, 'https://build.invalid').pathname;
      assert(pathname.startsWith(sitePath('/')), `${file}: wrong base path: ${value}`);
      const target = path.join(dist, decodeURIComponent(pathname.slice(sitePath('/').length)));
      assert(existsSync(target), `${file}: missing target: ${value}`);
      links++;
    }
  }
}

assert(readFileSync(path.join(dist, 'llms.txt'), 'utf8').includes(siteUrl('/docs/')),
  'LLM links must use the deployment URL');
assert(files.some(file => file.startsWith('composeunstyled-v2-demos/') && file.endsWith('.wasm')),
  'The demo WebAssembly bundle is missing');
const demo = readFileSync(path.join(dist, 'composeunstyled-v2-demos/index.html'), 'utf8');
for (const [, value] of demo.matchAll(/\b(?:href|src)=["']([^"']+)["']/g)) {
  assert(existsSync(path.join(dist, 'composeunstyled-v2-demos', value)), `Missing demo asset: ${value}`);
}
console.log(`Verified ${pages.length} pages, ${links} local links, LLM URLs, and demo assets.`);
