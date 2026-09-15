import { escapeHtml } from './code-toolbar.mjs';

export function renderDemo({ id, title, code }) {
  return `<div class="docs-demo" data-demo>
<iframe src="/composeunstyled-v2-demos/index.html?id=${id}" title="${escapeHtml(title)} interactive demo" width="600" height="450" loading="lazy"></iframe>

${code}
</div>\n`;
}
