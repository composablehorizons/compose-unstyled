import GithubSlugger from 'github-slugger';

const headingTags = ['h2', 'h3', 'h4', 'h5', 'h6'];
const slugger = new GithubSlugger();

function hasLink(nodes) {
  return nodes.some(node => node.tagName === 'a' || (node.children && hasLink(node.children)));
}

function headingText(nodes) {
  return nodes.map(node => node.value || (node.children ? headingText(node.children) : '')).join('');
}

export default {
  name: 'heading-links',
  element: {
    filter: headingTags,
    visit(node, ctx) {
      if (hasLink(node.children || [])) return;

      const existingId = node.properties?.id;
      const id = typeof existingId === 'string' && existingId.length > 0
        ? existingId
        : slugger.slug(ctx.textContent(node));
      ctx.setProperty(node, 'id', id);

      return {
        ...node,
        children: [{
          type: 'element',
          tagName: 'a',
          properties: {
            className: ['docs-heading-link'],
            href: `#${id}`,
            'aria-label': `Link to ${headingText(node.children).trim()}`,
          },
          children: node.children,
        }],
      };
    },
  },
};
