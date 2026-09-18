import { expect, test } from 'bun:test';
import { docsMarkdownPath, docsPath } from '../docs-routes.js';

test('builds canonical documentation routes from sections', () => {
  expect(docsPath({ slug: 'components' }, { slug: 'button' })).toBe('/docs/components/button/');
  expect(docsPath({ slug: 'components' }, { slug: 'components' })).toBe('/docs/components/');
  expect(docsPath({ slug: 'getting-started' }, { slug: 'overview', routeSlug: 'about' })).toBe('/docs/getting-started/about/');
});

test('builds Markdown links for canonical documentation routes', () => {
  expect(docsMarkdownPath({ slug: 'components' }, { slug: 'button' })).toBe('/docs/components/button.md');
  expect(docsMarkdownPath({ slug: 'components' }, { slug: 'components' })).toBe('/docs/components.md');
});
