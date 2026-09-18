import { codeToolbar, readCodeAttribute } from '../scripts/code-toolbar.js';

export const codeTitleTransformer = {
  name: 'code-metadata',
  pre(node) {
    const meta = this.options.meta?.__raw;
    const flags = (meta || '').replace(/"[^"]*"|'[^']*'/g, '');
    node.properties['data-code-expandable'] = /(?:^|\s)expandable(?:\s|$)/.test(flags);
    node.properties['data-code-tabbed'] = /(?:^|\s)tabbed(?:\s|$)/.test(flags);
    for (const attribute of ['title', 'githubUrl']) {
      const value = readCodeAttribute(meta, attribute);
      if (value) node.properties[`data-code-${attribute}`] = value;
    }
  },
};

const textContent = node => node.type === 'text'
  ? node.value
  : (node.children || []).map(textContent).join('');

function tabbedVariants(node) {
  const code = node.children?.find(child => child.tagName === 'code');
  const lines = code?.children?.filter(child => child.tagName === 'span');
  if (!code || !lines) return [];

  const variants = [];
  let current;
  for (const line of lines) {
    const match = textContent(line).match(/^\/\/ tab: (.+)$/);
    if (match) {
      current = { title: match[1], lines: [] };
      variants.push(current);
    } else if (current) {
      current.lines.push(line);
    }
  }
  return variants.map(variant => ({
    title: variant.title,
    node: {
      ...node,
      properties: { ...node.properties },
      children: [{
        ...code,
        children: variant.lines.flatMap((line, index) => index === variant.lines.length - 1
          ? [line]
          : [line, { type: 'text', value: '\n' }]),
      }],
    },
  }));
}

export default function codePanels() {
  let index = 0;
  return {
    name: 'code-panels',
    element: {
      filter: ['pre'],
      visit(node) {
        if (!node.children?.some(child => child.tagName === 'code')) return;
        const expandable = node.properties?.['data-code-expandable'] === true;
        const variants = node.properties?.['data-code-tabbed'] === true ? tabbedVariants(node) : [];
        const isTabbed = variants.length > 0;
        const codeId = `code-source-${++index}`;
        const toolbar = codeToolbar({
          title: node.properties?.['data-code-title'],
          githubUrl: node.properties?.['data-code-githubUrl'],
          codeId,
          expandable,
          tabs: variants.map(variant => variant.title),
        });
        const sources = isTabbed ? variants.map((variant, variantIndex) => ({
          type: 'element',
          tagName: 'div',
          properties: {
            className: ['code-source'],
            id: `${codeId}-${variantIndex}`,
            role: 'tabpanel',
            'aria-labelledby': `${codeId}-tab-${variantIndex}`,
            ...(variantIndex === 0 ? {} : { hidden: true }),
          },
          children: [{ ...variant.node, properties: { ...variant.node.properties, tabindex: 0 } }],
        })) : [{
          type: 'element', tagName: 'div', properties: { className: ['code-source'], id: codeId }, children: [
            { ...node, properties: { ...node.properties, tabindex: expandable ? -1 : 0 } },
            ...(expandable ? [{
              type: 'element', tagName: 'button',
              properties: { type: 'button', className: ['code-bottom-toggle'], 'data-code-bottom-toggle': true, 'aria-expanded': 'false', 'aria-controls': codeId, disabled: true },
              children: [{ type: 'text', value: 'Show Code' }],
            }] : []),
          ],
        }];
        return {
          type: 'element', tagName: 'div', properties: { className: ['code-panel'], 'data-code-panel': true, 'data-code-tabbed': isTabbed || undefined, 'data-collapsed': String(expandable) },
          children: [
            { type: 'raw', value: toolbar },
            ...sources,
            { type: 'element', tagName: 'p', properties: { className: ['code-status'], role: 'status', 'aria-live': 'polite' }, children: [] },
          ],
        };
      },
    },
  };
}
