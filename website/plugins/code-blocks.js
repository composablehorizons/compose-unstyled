import { codeToolbar, readCodeAttribute } from '../scripts/code-toolbar.js';

export const codeTitleTransformer = {
  name: 'code-metadata',
  pre(node) {
    const meta = this.options.meta?.__raw;
    const flags = (meta || '').replace(/"[^"]*"|'[^']*'/g, '');
    node.properties['data-code-expandable'] = /(?:^|\s)expandable(?:\s|$)/.test(flags);
    for (const attribute of ['title', 'githubUrl']) {
      const value = readCodeAttribute(meta, attribute);
      if (value) node.properties[`data-code-${attribute}`] = value;
    }
  },
};

export default function codePanels() {
  let index = 0;
  return {
    name: 'code-panels',
    element: {
      filter: ['pre'],
      visit(node) {
        if (!node.children?.some(child => child.tagName === 'code')) return;
        const expandable = node.properties?.['data-code-expandable'] === true;
        const codeId = `code-source-${++index}`;
        const toolbar = codeToolbar({
          title: node.properties?.['data-code-title'],
          githubUrl: node.properties?.['data-code-githubUrl'],
          codeId,
          expandable,
        });
        return {
          type: 'element', tagName: 'div', properties: { className: ['code-panel'], 'data-code-panel': true, 'data-collapsed': String(expandable) },
          children: [
            { type: 'raw', value: toolbar },
            { type: 'element', tagName: 'div', properties: { className: ['code-source'], id: codeId }, children: [
              { ...node, properties: { ...node.properties, tabindex: expandable ? -1 : 0 } },
              ...(expandable ? [{
                type: 'element', tagName: 'button',
                properties: { type: 'button', className: ['code-bottom-toggle'], 'data-code-bottom-toggle': true, 'aria-expanded': 'false', 'aria-controls': codeId, disabled: true },
                children: [{ type: 'text', value: 'Show Code' }],
              }] : []),
            ] },
            { type: 'element', tagName: 'p', properties: { className: ['code-status'], role: 'status', 'aria-live': 'polite' }, children: [] },
          ],
        };
      },
    },
  };
}
