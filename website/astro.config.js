import { defineConfig } from 'astro/config';
import { satteri } from '@astrojs/markdown-satteri';
import sitemap from '@astrojs/sitemap';
import { site, base } from './site.config.js';
import externalLinks from './plugins/external-links.js';
import codePanels, { codeTitleTransformer } from './plugins/code-blocks.js';
import githubAlerts from './plugins/github-alerts.js';
import headingLinks from './plugins/heading-links.js';

export default defineConfig({
  site,
  base,
  trailingSlash: 'ignore',
  integrations: [sitemap({
    filter: (page) => {
      const pathname = new URL(page).pathname;
      return pathname === '/docs/components/' || /^\/docs\/[^/]+\/$/.test(pathname) === false;
    },
  })],
  markdown: {
    processor: satteri({ hastPlugins: [externalLinks, headingLinks, githubAlerts, codePanels] }),
    syntaxHighlight: 'shiki',
    shikiConfig: {
      themes: { light: 'github-light', dark: 'github-dark' },
      defaultColor: false,
      transformers: [codeTitleTransformer],
    },
  },
});
