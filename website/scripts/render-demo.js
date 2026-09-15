import { escapeHtml } from './code-toolbar.js';

export function renderDemo({ id, title, code, revision }) {
  const revisionQuery = revision ? `&amp;v=${revision}` : '';
  return `<div class="docs-demo" data-demo>
<iframe src="/composeunstyled-v2-demos/index.html?id=${id}${revisionQuery}" title="${escapeHtml(title)} interactive demo" width="600" height="450" loading="lazy"></iframe>

${code}
</div>\n`;
}
