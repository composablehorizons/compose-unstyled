import { defineConfig } from 'astro/config';
import { satteri } from '@astrojs/markdown-satteri';
import { site, base } from './site.config.mjs';
import externalLinks from './plugins/external-links.mjs';
import codePanels, { codeTitleTransformer } from './plugins/code-blocks.mjs';

export default defineConfig({
  site,
  base,
  trailingSlash: 'ignore',
  markdown: {
    processor: satteri({ hastPlugins: [externalLinks, codePanels] }),
    syntaxHighlight: 'shiki',
    shikiConfig: {
      themes: { light: 'github-light', dark: 'github-dark' },
      defaultColor: false,
      transformers: [codeTitleTransformer],
    },
  },
});
