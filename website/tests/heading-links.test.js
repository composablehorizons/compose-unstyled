import { expect, test } from 'bun:test';
import headingLinks from '../plugins/heading-links.js';

function render(heading) {
  return headingLinks.element.visit(heading, {
    setProperty(node, property, value) {
      node.properties[property] = value;
    },
    textContent(node) {
      return node.children.map(child => child.value).join('');
    },
  });
}

test('renders documentation headings as links to their fragment', () => {
  const heading = render({
    type: 'element', tagName: 'h2', properties: {},
    children: [{ type: 'text', value: 'Installation' }],
  });

  const link = heading.children[0];
  expect(link.tagName).toBe('a');
  expect(link.properties.href).toBe('#installation');
  expect(link.properties['aria-label']).toBe('Link to Installation');
  expect(link.children).toEqual([{ type: 'text', value: 'Installation' }]);
});

test('preserves headings that already contain links', () => {
  const heading = {
    type: 'element', tagName: 'h3', properties: { id: 'api' },
    children: [{ type: 'element', tagName: 'a', properties: { href: '/api' }, children: [{ type: 'text', value: 'API' }] }],
  };

  expect(render(heading)).toBeUndefined();
});
