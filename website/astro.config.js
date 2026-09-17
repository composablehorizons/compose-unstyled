import { defineConfig } from 'astro/config';
import { satteri } from '@astrojs/markdown-satteri';
import { site, base } from './site.config.js';
import externalLinks from './plugins/external-links.js';
import codePanels, { codeTitleTransformer } from './plugins/code-blocks.js';
import headingLinks from './plugins/heading-links.js';

export default defineConfig({
  site,
  base,
  trailingSlash: 'ignore',
  markdown: {
    processor: satteri({ hastPlugins: [externalLinks, headingLinks, codePanels] }),
    syntaxHighlight: 'shiki',
    shikiConfig: {
      themes: { light: 'github-light', dark: 'github-dark' },
      defaultColor: false,
      transformers: [codeTitleTransformer],
    },
  },
});
