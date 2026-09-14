import { readFileSync } from 'node:fs';
import { defineConfig } from 'astro/config';
import { site, base } from './site.config.mjs';
import starlight from '@astrojs/starlight';
import { parse } from 'yaml';

const navigation = parse(readFileSync(new URL('../docs/docs.yml', import.meta.url), 'utf8'));

export default defineConfig({
  site,
  base,
  integrations: [starlight({
    title: 'Compose Unstyled',
    description: 'Build your own design system with Compose.',
    customCss: ['./src/styles/docs.css'],
    components: {
      PageTitle: './src/components/DocsTitle.astro',
      Sidebar: './src/components/DocsSidebar.astro',
      ThemeProvider: './src/components/LightTheme.astro',
      ThemeSelect: './src/components/NoThemeSelect.astro',
    },
    expressiveCode: {
      themes: ['one-dark-pro'],
      styleOverrides: {
        borderRadius: '0.5rem',
        codeFontFamily: 'JetBrainsMono, ui-monospace, monospace',
        codeFontSize: '0.875rem',
      },
    },
    social: [{ icon: 'github', label: 'GitHub', href: 'https://github.com/composablehorizons/compose-unstyled' }],
    sidebar: [
      ...navigation.sections.map(section => ({
        label: section.title,
        items: section.pages.map(page => ({ label: page.title, link: `/docs/${page.slug}/` })),
      })),
      { label: 'LLM documentation', link: '/llms.txt' },
    ],
  })],
});
