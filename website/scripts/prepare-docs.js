import { readFile, writeFile, mkdir, rm, cp, readdir } from 'node:fs/promises';
import { execFileSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { sitePath, siteUrl } from '../site.config.js';
import { parse, stringify } from 'yaml';
import { renderDemo } from './render-demo.js';

const website = fileURLToPath(new URL('../', import.meta.url));
const root = path.resolve(website, '..');
const read = (file) => readFile(path.join(root, file), 'utf8');
const navigation = parse(await read('docs/docs.yml'));
const version = (await read('gradle/libs.versions.toml')).match(/^unstyled\s*=\s*"([^"]+)"/m)?.[1];
if (!version) throw new Error('Missing library version');
const demoSourceMap = path.join(root, 'demo/build/generated/demo-registry/DemoSourceMap.properties');
execFileSync('bun', ['scripts/generate-demo-registry.js'], { cwd: root, stdio: 'inherit' });
const demoSources = new Map(
  (await readFile(demoSourceMap, 'utf8'))
    .split('\n')
    .filter(line => line && !line.startsWith('#'))
    .map(line => line.split('=', 2)),
);

const generated = path.join(root, 'build/generated/website-docs/pages');
execFileSync('bun', ['scripts/generate-compose-unstyled-api.js', generated], { cwd: root, stdio: 'inherit' });
const contentDir = path.join(website, 'src/pages/docs');
const publicDir = path.join(website, 'public');
const demoRevision = await readFile(path.join(publicDir, 'composeunstyled-v2-demos/.revision'), 'utf8')
  .then(revision => revision.trim())
  .catch(() => undefined);
await mkdir(contentDir, { recursive: true });
await rm(path.join(publicDir, 'docs'), { recursive: true, force: true });
await mkdir(path.join(publicDir, 'docs'), { recursive: true });

const escape = text => text.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;');
const withoutDemoMetadata = source => source
  .replace(/^import com\.composeunstyled\.demo\.UnstyledDemo\s*\n/m, '')
  .replace(/^@UnstyledDemo\([\s\S]*?\)\s*\n/m, '');
const primitives = navigation.sections.find(section => section.title === 'Primitives').pages;
const componentLinks = primitives.map(page => `[${page.title}](/docs/${page.slug}/)`).join('\n\n');
const componentList = `<ul>${primitives.map(page => `<li><a href="/docs/${page.slug}/">${escape(page.title)}</a></li>`).join('')}</ul>`;
const llms = [`# Compose Unstyled ${version}`, '', '> Renderless components for Jetpack Compose and Compose Multiplatform.', '', `These docs describe version ${version}. Examples use this version's APIs.`, ''];
const full = [`# Compose Unstyled ${version}`, ''];
const generatedPages = new Set();
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
      const sourcePath = demoSources.get(id);
      if (!sourcePath) throw new Error(`Unknown demo: ${id}`);
      const file = path.basename(sourcePath);
      const source = (await read(sourcePath))
        .replace(/^\s*\/\*[\s\S]*?\*\/\s*/, '')
        .replace(/^\s*package\s+[A-Za-z0-9_.]+\s*\n+/, '');
      const codeSource = withoutDemoMetadata(source).trim();
      const code = `\n\n\`\`\`kotlin expandable title="${file}" githubUrl="https://github.com/composablehorizons/compose-unstyled/blob/main/${sourcePath}"\n${codeSource}\n\`\`\`\n\n`;
      htmlBody = htmlBody.replace(marker[0], renderDemo({
        id, title: page.title, code, revision: demoRevision,
      }));
      markdownBody = markdownBody.replace(marker[0], code);
    }

    if (/<ApiReference|<UnstyledDemo|\{\{/.test(htmlBody)) throw new Error(`Unresolved marker: ${page.slug}`);
    // Generated content bypasses Astro's link handling, so prefix its local URLs here.
    htmlBody = htmlBody
      .replace(/\]\(\/(?!\/)/g, `](${sitePath('/')}`)
      .replace(/\b(src|href)="\/(?!\/)/g, (_, attr) => `${attr}="${sitePath('/')}`);
    await writeFile(path.join(contentDir, `${page.slug}.md`), `---\n${stringify({
      layout: '../../layouts/DocsLayout.astro',
      title: metadata.title,
      ...(metadata.seoTitle ? { seoTitle: metadata.seoTitle } : {}),
      description: metadata.description,
      markdownUrl: sitePath(`/docs/${page.slug}.md`),
    })}---\n${htmlBody}`);
    generatedPages.add(`${page.slug}.md`);
    markdownBody = markdownBody
      .replace(/\]\(\/docs\/([^/)]+)\/(#[^)]*)?\)/g, (_, slug, hash = '') => `](${siteUrl(`/docs/${slug}.md`)}${hash})`)
      .replace(/\b(src|href)="\//g, (_, attr) => `${attr}="${siteUrl('/')}`);
    const markdown = `${match[0]}${markdownBody}`;
    await writeFile(path.join(publicDir, 'docs', `${page.slug}.md`), markdown);
    llms.push(`- [${metadata.title}](${siteUrl(`/docs/${page.slug}.md`)}): ${metadata.description || metadata.title}`);
    full.push(markdown);
    count++;
  }
  llms.push('');
}

for (const file of await readdir(contentDir)) {
  if (file.endsWith('.md') && !generatedPages.has(file)) await rm(path.join(contentDir, file));
}

await writeFile(path.join(publicDir, 'llms.txt'), llms.join('\n'));
await writeFile(path.join(publicDir, 'llms-full.txt'), full.join('\n\n---\n\n'));
for (const [source, target] of [[path.join(root, 'docs/assets'), 'composeunstyled-v2-assets']]) {
  await rm(path.join(publicDir, target), { recursive: true, force: true });
  await cp(source, path.join(publicDir, target), { recursive: true });
}
console.log(`Prepared ${count} docs pages, Kotlin examples, demo assets, and LLM files for ${version}.`);
