import { site } from '../site.config.mjs';

const siteOrigin = new URL(site).origin;

export default {
  name: 'external-links',
  element: {
    filter: ['a'],
    visit(node, ctx) {
      const href = node.properties?.href;
      if (!href || !URL.canParse(href, site)) return;
      const url = new URL(href, site);
      if (!['http:', 'https:'].includes(url.protocol) || url.origin === siteOrigin) return;
      ctx.setProperty(node, 'target', '_blank');
      const rel = node.properties.rel || [];
      ctx.setProperty(node, 'rel', [...new Set([
        ...(Array.isArray(rel) ? rel : rel.split(/\s+/)),
        'noopener', 'noreferrer',
      ])]);
    },
  },
};
