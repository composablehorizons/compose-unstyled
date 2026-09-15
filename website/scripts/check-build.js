import assert from 'node:assert/strict';
import { existsSync, readFileSync, readdirSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { site, sitePath, siteUrl } from '../site.config.js';
import { sharedSkikoUrl } from './skiko-runtime.js';

const dist = fileURLToPath(new URL('../dist/', import.meta.url));
const files = readdirSync(dist, { recursive: true });
const pages = files.filter(file => file.endsWith('.html') &&
  (file === 'index.html' || file.startsWith('docs/')));
assert(pages.length > 2, 'Documentation pages were not generated');
let links = 0;

for (const file of pages) {
  const html = readFileSync(path.join(dist, file), 'utf8');
  for (const [tag] of html.matchAll(/<[a-z][^>]*>/gi)) {
    if (/^<a\s/i.test(tag)) {
      const attributes = Object.fromEntries([...tag.matchAll(/\b(href|target|rel)=["']([^"']*)["']/g)].map(([, name, value]) => [name, value]));
      const url = new URL(attributes.href || '', site);
      if (['http:', 'https:'].includes(url.protocol) && url.origin !== new URL(site).origin) {
        assert.equal(attributes.target, '_blank', `${file}: external link must open in a new tab: ${url}`);
        const rel = new Set((attributes.rel || '').split(/\s+/));
        assert(rel.has('noopener') && rel.has('noreferrer'), `${file}: external link is missing safe rel attributes: ${url}`);
      }
    }
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
  if (new URL(value, 'https://build.invalid').origin !== 'https://build.invalid') continue;
  assert(existsSync(path.join(dist, 'composeunstyled-v2-demos', value)), `Missing demo asset: ${value}`);
}
assert(demo.includes(sharedSkikoUrl), 'The demo must use the shared Skiko runtime');
console.log(`Verified ${pages.length} pages, ${links} local links, LLM URLs, and demo assets.`);
