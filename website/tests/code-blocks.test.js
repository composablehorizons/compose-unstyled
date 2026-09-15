import { test, expect } from 'bun:test';
import codePanels from '../plugins/code-blocks.js';

function render(expandable) {
  return codePanels().element.visit({
    type: 'element', tagName: 'pre',
    properties: { 'data-code-expandable': expandable, tabindex: 0 },
    children: [{ type: 'element', tagName: 'code', properties: {}, children: [{ type: 'text', value: 'val example = 1' }] }],
  });
}

test('expandable code is collapsed before JavaScript and toolbar space is reserved', () => {
  const panel = render(true);
  expect(panel.properties['data-collapsed']).toBe('true');
  expect(panel.children[1].children[0].properties.tabindex).toBe(-1);
  const toolbar = panel.children[0].value;
  expect(toolbar).toContain('aria-expanded="false"');
  expect(toolbar).toContain('aria-label="Expand code"');
  expect(toolbar).not.toContain(' hidden');
  expect(toolbar).toContain('disabled');
});

test('unflagged code stays full height and has no expansion control', () => {
  const panel = render(false);
  expect(panel.properties['data-collapsed']).toBe('false');
  expect(panel.children[1].children[0].properties.tabindex).toBe(0);
  expect(panel.children[0].value).not.toContain('data-code-toggle');
  expect(panel.children[0].value).toContain('data-code-copy');
});
