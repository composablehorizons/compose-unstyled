const alertTypes = new Map([
  ['NOTE', 'Note'],
  ['TIP', 'Tip'],
  ['IMPORTANT', 'Important'],
  ['WARNING', 'Warning'],
  ['CAUTION', 'Caution'],
]);

export default {
  name: 'github-alerts',
  element: {
    filter: ['blockquote'],
    visit(node) {
      const firstParagraph = node.children?.find(child => child.tagName === 'p');
      const firstChild = firstParagraph?.children?.[0];
      if (firstChild?.type !== 'text') return;

      const match = firstChild.value.match(/^\[!([A-Z]+)\]\s*/);
      const title = match && alertTypes.get(match[1]);
      if (!title) return;

      const cleanedChildren = firstParagraph.children
        .map((child, index) => index === 0 ? { ...child, value: child.value.slice(match[0].length) } : child)
        .filter(child => child.value !== '');
      const children = node.children
        .map(child => child === firstParagraph ? { ...firstParagraph, children: cleanedChildren } : child)
        .filter(child => child !== firstParagraph || cleanedChildren.length > 0);

      return {
        ...node,
        properties: {
          ...node.properties,
          className: ['docs-alert', `docs-alert-${match[1].toLowerCase()}`],
          'aria-label': title,
        },
        children: [
          {
            type: 'element',
            tagName: 'p',
            properties: { className: ['docs-alert-title'] },
            children: [{ type: 'text', value: title }],
          },
          ...children,
        ],
      };
    },
  },
};
