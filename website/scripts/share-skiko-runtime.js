import { createHash } from 'node:crypto';
import { readdir, readFile, unlink, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { sharedSkikoUrl } from './skiko-runtime.js';

const knownSkikoRuntimeHashes = new Set([
  '089052ba37e2a6e8345117bf6fe0f5730cfc8de1eb4c2bbc8921cf8e457ae3cb',
]);

function escapeRegExp(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

function fail(message) {
  throw new Error(`share-skiko-runtime: ${message}`);
}

async function findSkikoRuntime(directory, composeAppSource) {
  const entries = await readdir(directory, { withFileTypes: true });
  for (const entry of entries) {
    if (!entry.isFile() || !entry.name.endsWith('.wasm') || !composeAppSource.includes(entry.name)) {
      continue;
    }
    const contents = await readFile(path.join(directory, entry.name));
    const hash = createHash('sha256').update(contents).digest('hex');
    if (knownSkikoRuntimeHashes.has(hash)) {
      return entry.name;
    }
  }
  fail(`could not find a supported Skiko runtime in ${directory}`);
}

function wasmPreloadUrls(composeAppSource) {
  const urls = new Set([sharedSkikoUrl]);
  for (const match of composeAppSource.matchAll(/(?:[A-Za-z_$][A-Za-z0-9_$]*\.)+p\s*\+\s*"([^"]+\.wasm)"/g)) {
    urls.add(match[1]);
  }
  return [...urls];
}

function addPreloads(indexHtml, composeAppSource) {
  const scriptPreloads = ['composeApp.js']
    .map((href) => `    <link rel="preload" href="${href}" as="script">`);
  const wasmPreloads = wasmPreloadUrls(composeAppSource)
    .map((href) => `    <link rel="preload" href="${href}" as="fetch" type="application/wasm" crossorigin>`);
  const block = [
    '    <!-- wasm-preloads:start -->',
    ...scriptPreloads,
    ...wasmPreloads,
    '    <!-- wasm-preloads:end -->',
  ].join('\n');
  const withoutExistingBlock = indexHtml.replace(
    /\n?    <!-- wasm-preloads:start -->.*?    <!-- wasm-preloads:end -->\n?/s,
    '\n',
  );

  if (!withoutExistingBlock.includes('</head>')) {
    fail('bundle index.html has no closing head tag');
  }
  return withoutExistingBlock.replace('</head>', `${block}\n  </head>`);
}

export async function shareSkikoRuntime(directory) {
  const composeAppPath = path.join(directory, 'composeApp.js');
  const indexPath = path.join(directory, 'index.html');
  const composeAppSource = await readFile(composeAppPath, 'utf8');
  const localRuntime = await findSkikoRuntime(directory, composeAppSource);
  const runtimeReference = new RegExp(
    `[A-Za-z_$][A-Za-z0-9_$]{0,100}\\.p\\s*\\+\\s*"${escapeRegExp(localRuntime)}"`,
    'g',
  );
  const rewrittenSource = composeAppSource
    .replace(runtimeReference, JSON.stringify(sharedSkikoUrl))
    .replaceAll(`"${localRuntime}"`, JSON.stringify(sharedSkikoUrl))
    .replaceAll('"skiko.wasm"', JSON.stringify(sharedSkikoUrl));

  if (!rewrittenSource.includes(sharedSkikoUrl)) {
    fail(`could not rewrite ${localRuntime} in composeApp.js`);
  }

  await writeFile(composeAppPath, rewrittenSource);
  await unlink(path.join(directory, localRuntime));
  await writeFile(indexPath, addPreloads(await readFile(indexPath, 'utf8'), rewrittenSource));
  console.log(`Using shared Skiko runtime: ${sharedSkikoUrl}`);
}
