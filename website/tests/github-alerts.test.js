import { expect, test } from 'bun:test';
import githubAlerts from '../plugins/github-alerts.js';

function render(content) {
  return githubAlerts.element.visit({
    type: 'element', tagName: 'blockquote', properties: {},
    children: [{
      type: 'element', tagName: 'p', properties: {},
      children: [{ type: 'text', value: content }],
    }],
  });
}

test('renders a GitHub note alert without its source marker', () => {
  const alert = render('[!NOTE] Use the current theme builder.');

  expect(alert.properties.className).toEqual(['docs-alert', 'docs-alert-note']);
  expect(alert.properties['aria-label']).toBe('Note');
  expect(alert.children[0].children[0].value).toBe('Note');
  expect(alert.children[1].children[0].value).toBe('Use the current theme builder.');
});

test('renders a deprecated alert', () => {
  const alert = render('[!DEPRECATED] This will be removed in 3.0.');

  expect(alert.properties.className).toEqual(['docs-alert', 'docs-alert-deprecated']);
  expect(alert.properties['aria-label']).toBe('Deprecated');
  expect(alert.children[0].children[0].value).toBe('Deprecated');
});

test('leaves ordinary blockquotes unchanged', () => {
  expect(render('Regular quote.')).toBeUndefined();
});
