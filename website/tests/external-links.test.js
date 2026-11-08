import { expect, test } from 'bun:test';
import { withRef } from '../external-links.js';
import externalLinks from '../plugins/external-links.js';
import { codeToolbar } from '../scripts/code-toolbar.js';

function visit(href) {
  const node = { type: 'element', tagName: 'a', properties: { href }, children: [] };
  externalLinks.element.visit(node, {
    setProperty(target, property, value) {
      target.properties[property] = value;
    },
  });
  return node.properties;
}

test('adds the site as ref, keeping the query and fragment', () => {
  expect(withRef('https://robomori.com')).toBe('https://robomori.com/?ref=composeunstyled.com');
  expect(withRef('https://example.com/a?b=c#L33')).toBe('https://example.com/a?b=c&ref=composeunstyled.com#L33');
});

test('adds ref to external markdown links', () => {
  expect(visit('https://example.com/a').href).toBe('https://example.com/a?ref=composeunstyled.com');
});

test('leaves internal and non-web markdown links unchanged', () => {
  expect(visit('/docs/').href).toBe('/docs/');
  expect(visit('#install').href).toBe('#install');
  expect(visit('mailto:hello@example.com').href).toBe('mailto:hello@example.com');
});

test('adds ref to the code toolbar GitHub link', () => {
  const toolbar = codeToolbar({ title: 'Demo.kt', githubUrl: 'https://github.com/a/b/blob/main/Demo.kt', codeId: 'code' });
  expect(toolbar).toContain('href="https://github.com/a/b/blob/main/Demo.kt?ref=composeunstyled.com"');
});
