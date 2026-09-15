import { spawn, execFileSync } from 'node:child_process';
import { watch } from 'node:fs';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const website = fileURLToPath(new URL('../', import.meta.url));
const root = path.resolve(website, '..');

const prepareDocs = () => {
  execFileSync('bun', ['run', 'prepare:docs'], { cwd: website, stdio: 'inherit' });
};

prepareDocs();

const astro = spawn(path.join(website, 'node_modules/.bin/astro'), ['dev', '--host', '127.0.0.1', '--port', '4321'], {
  cwd: website,
  stdio: ['inherit', 'pipe', 'pipe'],
});
let demoBuild;
let demoBuildStarted = false;
let demoBuildQueued = false;
let demoBuildTimer;
let docsTimer;
let isPreparingDocs = false;
let docsRefreshQueued = false;

const writeAstroOutput = (stream) => (chunk) => {
  stream.write(chunk);
  if (!demoBuildStarted && /(ready in|Dev server running)/.test(chunk.toString())) {
    demoBuildStarted = true;
    scheduleDemoBuild();
  }
};

astro.stdout.on('data', writeAstroOutput(process.stdout));
astro.stderr.on('data', writeAstroOutput(process.stderr));

const refreshDemoAssets = () => {
  try {
    execFileSync('bun', ['scripts/refresh-demo-assets.js'], { cwd: website, stdio: 'inherit' });
    return true;
  } catch (error) {
    console.error('Demo assets were not refreshed.');
    console.error(error);
    return false;
  }
};

const refreshDemo = () => {
  if (refreshDemoAssets()) scheduleDocsPreparation();
};

const buildDemo = () => {
  if (demoBuild) {
    demoBuildQueued = true;
    return;
  }

  console.log('Building development demo…');
  demoBuild = spawn(
    './gradlew',
    [':demo:wasmJsBrowserDevelopmentExecutableDistribution', '--console=plain'],
    { cwd: root, stdio: 'inherit' },
  );
  demoBuild.on('close', (code) => {
    demoBuild = undefined;
    if (code === 0) refreshDemo();
    else console.error(`Demo build failed with exit code ${code}.`);

    if (demoBuildQueued) {
      demoBuildQueued = false;
      scheduleDemoBuild();
    }
  });
};

const scheduleDemoBuild = () => {
  clearTimeout(demoBuildTimer);
  demoBuildTimer = setTimeout(buildDemo, 100);
};

const scheduleDocsPreparation = () => {
  clearTimeout(docsTimer);
  docsTimer = setTimeout(() => {
    if (isPreparingDocs) {
      docsRefreshQueued = true;
      return;
    }

    console.log('Refreshing docs…');
    isPreparingDocs = true;
    try {
      prepareDocs();
    } finally {
      isPreparingDocs = false;
      if (docsRefreshQueued) {
        docsRefreshQueued = false;
        scheduleDocsPreparation();
      }
    }
  }, 100);
};

const docsPagesWatcher = watch(path.join(root, 'docs/pages'), { recursive: true }, (_, file) => {
  if (file?.endsWith('.md')) scheduleDocsPreparation();
});
const docsNavigationWatcher = watch(path.join(root, 'docs/docs.yml'), scheduleDocsPreparation);
const docsAssetsWatcher = watch(path.join(root, 'docs/assets'), { recursive: true }, scheduleDocsPreparation);
const demoSourceWatcher = watch(path.join(root, 'demo/src'), { recursive: true }, (_, file) => {
  if (file?.endsWith('.kt')) scheduleDemoBuild();
});

for (const signal of ['SIGINT', 'SIGTERM']) {
  process.on(signal, () => {
    docsPagesWatcher.close();
    docsNavigationWatcher.close();
    docsAssetsWatcher.close();
    demoSourceWatcher.close();
    demoBuild?.kill(signal);
    astro.kill(signal);
    process.exit(0);
  });
}
