import { execFileSync } from 'node:child_process';
import { mkdtempSync, mkdirSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import path from 'node:path';
import { expect, test } from 'bun:test';

const generator = path.resolve('scripts/generate-compose-unstyled-api.js');

test('generates ordered API references from fully qualified declarations', () => {
  const fixture = mkdtempSync(path.join(tmpdir(), 'compose-unstyled-api-'));
  try {
    const docs = path.join(fixture, 'docs/pages');
    const source = path.join(fixture, 'composeunstyled-example/src/commonMain/kotlin/com/example');
    mkdirSync(docs, { recursive: true });
    mkdirSync(source, { recursive: true });
    writeFileSync(
      path.join(docs, 'example.md'),
      `## API Reference\n\n<ApiReference declaration="com.example.ExampleScope.Item" />\n\n<ApiReference declaration="com.example.UnstyledExample" />\n`,
    );
    writeFileSync(
      path.join(source, 'Example.kt'),
      `package com.example\n\nclass ExampleScope<T>\n\nfun <T> ExampleScope<T>.Item(label: String) {}\n\nfun UnstyledExample(enabled: Boolean) {}\n\nprivate fun PrivateExample() {}\n`,
    );

    execFileSync('bun', [generator], { cwd: fixture, stdio: 'pipe' });

    const generated = readFileSync(
      path.join(fixture, 'build/generated/compose-unstyled-docs/pages/example.md'),
      'utf8',
    );
    expect(generated.indexOf('### ExampleScope.Item')).toBeLessThan(generated.indexOf('### UnstyledExample'));
    expect(generated).toContain('| `label` | `String` |  |');
    expect(generated).toContain('| `enabled` | `Boolean` |  |');
  } finally {
    rmSync(fixture, { recursive: true, force: true });
  }
});

test('rejects a non-public API reference', () => {
  const fixture = mkdtempSync(path.join(tmpdir(), 'compose-unstyled-api-'));
  try {
    const docs = path.join(fixture, 'docs/pages');
    const source = path.join(fixture, 'composeunstyled-example/src/commonMain/kotlin/com/example');
    mkdirSync(docs, { recursive: true });
    mkdirSync(source, { recursive: true });
    writeFileSync(
      path.join(docs, 'example.md'),
      '<ApiReference declaration="com.example.PrivateExample" />\n',
    );
    writeFileSync(path.join(source, 'Example.kt'), 'package com.example\n\nprivate fun PrivateExample() {}\n');

    expect(() => execFileSync('bun', [generator], { cwd: fixture, stdio: 'pipe' })).toThrow(
      "Could not generate public API reference 'com.example.PrivateExample'",
    );
  } finally {
    rmSync(fixture, { recursive: true, force: true });
  }
});
