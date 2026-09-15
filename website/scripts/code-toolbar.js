import { readFileSync } from 'node:fs';

export const escapeHtml = text => text.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;');
export const readCodeAttribute = (meta, name) => {
  const match = meta?.match(new RegExp(`(?:^|\\s)${name}=(?:"([^"]*)"|'([^']*)')`));
  return match ? match[1] ?? match[2] : undefined;
};
export const fileType = title => title.endsWith('.kts') ? 'gradle' : title.endsWith('.kt') ? 'kotlin' : undefined;
const icon = name => `<span aria-hidden="true">${readFileSync(new URL(`../src/assets/icons/${name}.svg`, import.meta.url), 'utf8').replace(/<!--[\s\S]*?-->/g, '').trim()}</span>`;
const icons = Object.fromEntries(['copy', 'maximize', 'minimize', 'github'].map(name => [name, icon(name)]));

export function codeToolbar({ title = '', githubUrl, codeId, expandable = false }) {
  if (githubUrl) {
    const url = new URL(githubUrl);
    if (url.protocol !== 'https:' || url.hostname !== 'github.com') throw new Error(`githubUrl must be an HTTPS GitHub URL: ${githubUrl}`);
  }
  const language = fileType(title);
  return `<div class="code-toolbar">
<span class="code-filename"${language ? ` data-filetype="${language}"` : ''}>${escapeHtml(title)}</span>
<div class="code-actions">
${githubUrl ? `<a href="${escapeHtml(githubUrl)}" target="_blank" rel="noopener noreferrer" aria-label="View source on GitHub" title="View source on GitHub">${icons.github}</a>` : ''}
${expandable ? `<button type="button" data-code-toggle aria-label="Expand code" title="Expand code" aria-expanded="false" aria-controls="${codeId}" disabled><span class="code-expand-icon">${icons.maximize}</span><span class="code-collapse-icon">${icons.minimize}</span></button>` : ''}
<button type="button" data-code-copy aria-label="Copy code" title="Copy code" disabled>${icons.copy}</button>
</div>
</div>`;
}
