import { readFile, writeFile, mkdir, rm, cp, readdir } from 'node:fs/promises';
import { execFileSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { sitePath, siteUrl } from '../site.config.js';
import { docsMarkdownPath, docsPath, findDocsPage } from '../docs-routes.js';
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
const removeGeneratedPages = async (directory) => {
  for (const entry of await readdir(directory, { withFileTypes: true })) {
    const entryPath = path.join(directory, entry.name);
    if (entry.isDirectory()) await removeGeneratedPages(entryPath);
    else if (entry.name.endsWith('.md')) await rm(entryPath);
  }
};
await removeGeneratedPages(contentDir);
await rm(path.join(publicDir, 'docs'), { recursive: true, force: true });
await mkdir(path.join(publicDir, 'docs'), { recursive: true });

const escape = text => text.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;');
const withoutDemoMetadata = source => source
  .replace(/^import com\.composeunstyled\.demo\.UnstyledDemo\s*\n/m, '')
  .replace(/^@UnstyledDemo\([\s\S]*?\)\s*\n/m, '');
const componentsSection = navigation.sections.find(section => section.title === 'Components');
const components = componentsSection.pages;
const docsPages = navigation.sections.flatMap(section => section.pages.map(page => ({ section, page })));
const docsPathForSlug = (slug) => {
  const entry = findDocsPage(navigation, slug);
  return entry ? docsPath(entry.section, entry.page) : `/docs/${slug}/`;
};
const componentLinks = components.map(page => `[${page.title}](${docsPath(componentsSection, page)})`).join('\n\n');
const componentList = `<ul>${components.map(page => `<li><a href="${docsPath(componentsSection, page)}">${escape(page.title)}</a></li>`).join('')}</ul>`;
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
      .replace(/\]\(([A-Za-z0-9._-]+)\.md(#[^)]*)?\)/g, (_, slug, hash = '') => `](${docsPathForSlug(slug)}${hash})`)
      .replace(/\]\(\/docs\/([A-Za-z0-9._-]+)\/?(#[^)]*)?\)/g, (_, slug, hash = '') => `](${docsPathForSlug(slug)}${hash})`);
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
    const pageSlug = page.routeSlug ?? page.slug;
    const pageFile = section.slug === pageSlug
      ? path.join(section.slug, 'index.md')
      : path.join(section.slug, `${pageSlug}.md`);
    const markdownPath = docsMarkdownPath(section, page);
    await mkdir(path.dirname(path.join(contentDir, pageFile)), { recursive: true });
    await writeFile(path.join(contentDir, pageFile), `---\n${stringify({
      layout: '../../../layouts/DocsLayout.astro',
      title: metadata.title,
      ...(metadata.seoTitle ? { seoTitle: metadata.seoTitle } : {}),
      description: metadata.description,
      markdownUrl: sitePath(markdownPath),
    })}---\n${htmlBody}`);
    for (const entry of docsPages) {
      markdownBody = markdownBody.replaceAll(
        `](${docsPath(entry.section, entry.page)}`,
        `](${siteUrl(docsMarkdownPath(entry.section, entry.page))}`,
      );
    }
    markdownBody = markdownBody.replace(/\b(src|href)="\//g, (_, attr) => `${attr}="${siteUrl('/')}`);
    const markdown = `${match[0]}${markdownBody}`;
    const markdownFile = path.join(publicDir, markdownPath);
    await mkdir(path.dirname(markdownFile), { recursive: true });
    await writeFile(markdownFile, markdown);
    llms.push(`- [${metadata.title}](${siteUrl(markdownPath)}): ${metadata.description || metadata.title}`);
    full.push(markdown);
    count++;
  }
  llms.push('');
}

await writeFile(path.join(publicDir, 'llms.txt'), llms.join('\n'));
await writeFile(path.join(publicDir, 'llms-full.txt'), full.join('\n\n---\n\n'));
for (const [source, target] of [[path.join(root, 'docs/assets'), 'composeunstyled-v2-assets']]) {
  await rm(path.join(publicDir, target), { recursive: true, force: true });
  await cp(source, path.join(publicDir, target), { recursive: true });
}
console.log(`Prepared ${count} docs pages, Kotlin examples, demo assets, and LLM files for ${version}.`);
