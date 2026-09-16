import { readFileSync, readdirSync, mkdirSync, writeFileSync, existsSync } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

// Tokenize the supported Kotlin declaration syntax without matching comments or string contents.
function tokenize(source, file) {
  const tokens = [];
  let i = 0;
  while (i < source.length) {
    if (/\s/.test(source[i])) { i++; continue; }
    if (source.startsWith('//', i)) {
      const end = source.indexOf('\n', i);
      i = end < 0 ? source.length : end;
      continue;
    }
    if (source.startsWith('/*', i)) {
      let depth = 1;
      i += 2;
      while (depth && i < source.length) {
        if (source.startsWith('/*', i)) { depth++; i += 2; }
        else if (source.startsWith('*/', i)) { depth--; i += 2; }
        else i++;
      }
      if (depth) throw new Error(`${file}: unclosed comment`);
      continue;
    }
    if (source.startsWith('"""', i)) {
      const end = source.indexOf('"""', i + 3);
      if (end < 0) throw new Error(`${file}: unclosed raw string`);
      tokens.push(source.slice(i, end + 3));
      i = end + 3;
      continue;
    }
    if (source[i] === '"' || source[i] === "'") {
      const start = i;
      const quote = source[i++];
      while (i < source.length && source[i] !== quote) {
        i += source[i] === '\\' ? 2 : 1;
      }
      if (i >= source.length) throw new Error(`${file}: unclosed string`);
      tokens.push(source.slice(start, ++i));
      continue;
    }
    const identifier = source.slice(i).match(/^[A-Za-z_][A-Za-z0-9_]*/);
    if (identifier) { tokens.push(identifier[0]); i += identifier[0].length; }
    else tokens.push(source[i++]);
  }
  return tokens;
}

function parseDemoArguments(args, fail) {
  const argumentsByName = new Map();
  let positionalId;
  let start = 0;
  let nesting = 0;
  const addArgument = end => {
    const argument = args.slice(start, end);
    if (!argument.length) fail('@UnstyledDemo contains an empty argument');
    if (argument.length === 1 && /^".*"$/.test(argument[0])) {
      if (positionalId || argumentsByName.has('id')) fail('@UnstyledDemo contains more than one id');
      positionalId = argument[0];
      return;
    }
    if (argument.length < 3 || argument[1] !== '=') fail('invalid @UnstyledDemo argument');
    const [name, , ...value] = argument;
    if (argumentsByName.has(name)) fail(`duplicate @UnstyledDemo argument: ${name}`);
    argumentsByName.set(name, value);
  };

  args.forEach((token, index) => {
    if (token === '(') nesting++;
    if (token === ')') nesting--;
    if (token === ',' && nesting === 0) {
      addArgument(index);
      start = index + 1;
    }
  });
  if (start < args.length) addArgument(args.length);

  const idToken = positionalId ?? argumentsByName.get('id')?.[0];
  const id = idToken?.match(/^"([a-z][a-z0-9]*(?:-[a-z0-9]+)*)"$/)?.[1];
  if (!id) fail('@UnstyledDemo requires a literal kebab-case id');

  const name = argumentsByName.get('name');
  if (name && (name.length !== 1 || !/^".*"$/.test(name[0]))) fail('name must be a string literal');

  return {
    id,
    title: name?.[0].slice(1, -1),
    contentAlignment: (() => {
      const value = argumentsByName.get('contentAlignment');
      if (!value) return 'Center';
      if (value.length !== 3 || value[0] !== 'DemoContentAlignment' || value[1] !== '.' || !/^[A-Z][A-Za-z0-9]*$/.test(value[2])) {
        fail('contentAlignment must be a DemoContentAlignment value');
      }
      return value[2];
    })(),
  };
}

export function discoverDemos(sources) {
  const demos = [];
  const ids = new Set();
  for (const { file, source } of sources) {
    const tokens = tokenize(source, file);
    const packageMatch = source.match(/^package\s+([A-Za-z_][\w]*(?:\.[A-Za-z_][\w]*)*)\s*;?\s*$/m);
    let depth = 0;
    let annotations = [];
    let modifiers = [];
    const fail = message => { throw new Error(`${file}: ${message}`); };
    for (let i = 0; i < tokens.length; i++) {
      const token = tokens[i];
      if (token === '@') {
        let name = tokens[++i];
        while (tokens[i + 1] === '.') { i += 2; name += `.${tokens[i]}`; }
        const args = [];
        if (tokens[i + 1] === '(') {
          i += 2;
          let nesting = 1;
          for (; i < tokens.length; i++) {
            if (tokens[i] === '(') nesting++;
            if (tokens[i] === ')' && --nesting === 0) break;
            args.push(tokens[i]);
          }
          if (nesting) fail('unclosed annotation arguments');
        }
        const simpleName = name?.split('.').at(-1);
        if (simpleName === 'UnstyledDemo' && depth !== 0) fail('@UnstyledDemo requires a top-level function');
        if (depth === 0) annotations.push({ name: simpleName, args });
        continue;
      }
      if (token === '{') { depth++; annotations = []; modifiers = []; continue; }
      if (token === '}') { depth--; continue; }
      if (depth !== 0) continue;
      if (['public', 'internal', 'private', 'protected', 'suspend', 'inline', 'expect', 'actual', 'external', 'tailrec', 'operator', 'infix', 'override'].includes(token)) {
        modifiers.push(token);
        continue;
      }
      const demoAnnotations = annotations.filter(a => a.name === 'UnstyledDemo');
      if (token === 'fun' && demoAnnotations.length) {
        if (demoAnnotations.length !== 1) fail('use one @UnstyledDemo per function');
        if (!annotations.some(a => a.name === 'Composable')) fail('@UnstyledDemo requires @Composable');
        if (!annotations.some(a => a.name === 'Preview')) fail('@UnstyledDemo requires @Preview');
        if (modifiers.some(m => !['public', 'internal'].includes(m))) fail('demo functions must be public or internal, non-suspending, and non-generic');
        const name = tokens[i + 1];
        if (!/^[A-Za-z_][A-Za-z0-9_]*$/.test(name) || tokens[i + 2] !== '(' || tokens[i + 3] !== ')') {
          fail('demo functions must have no receiver, type parameters, or value parameters');
        }
        let body = i + 4;
        if (tokens[body] === ':' && tokens[body + 1] === 'Unit') body += 2;
        if (!['{', '='].includes(tokens[body])) fail('demo functions must return Unit and have a body');
        const metadata = parseDemoArguments(demoAnnotations[0].args, fail);
        const { id } = metadata;
        if (ids.has(id)) fail(`duplicate demo id: ${id}`);
        if (!packageMatch) fail('demo functions require an explicit package');
        ids.add(id);
        demos.push({ ...metadata, name, packageName: packageMatch[1], file });
      }
      if (['fun', 'class', 'object', 'interface', 'val', 'var', 'typealias'].includes(token)) {
        if (demoAnnotations.length && token !== 'fun') fail('@UnstyledDemo requires a function');
        annotations = [];
        modifiers = [];
      }
    }
  }
  return demos.sort((a, b) => a.id.localeCompare(b.id, 'en'));
}

export function renderRegistry(demos) {
  const entries = demos.map(demo => {
    const title = demo.title ?? demo.name.replace(/Demo$/, '').replace(/([a-z0-9])([A-Z])/g, '$1 $2');
    return `  DemoItem(\n    name = "${title}",\n    id = "${demo.id}",\n    demo = { ${demo.packageName}.${demo.name}() },\n    presentation = DemoPresentation(\n      contentAlignment = DemoContentAlignment.${demo.contentAlignment},\n    ),\n  ),`;
  });
  return `// Generated by scripts/generate-demo-registry.js. Do not edit.\npackage com.composeunstyled.demo\n\ninternal val generatedDemos: List<DemoItem> = listOf(\n${entries.join('\n')}\n)\n`;
}

export function renderDemoSourceMap(demos) {
  const entries = demos.map(demo => `${demo.id}=demo/src/commonMain/kotlin/${demo.file}`);
  return `# Generated by scripts/generate-demo-registry.js. Do not edit.\n${entries.join('\n')}\n`;
}

if (process.argv[1] && path.resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  const root = fileURLToPath(new URL('../', import.meta.url));
  const sourceDir = path.join(root, 'demo/src/commonMain/kotlin');
  const output = path.resolve(process.argv[2] || path.join(root, 'demo/build/generated/demo-registry/GeneratedDemoRegistry.kt'));
  const sourceMapOutput = path.resolve(process.argv[3] || path.join(root, 'demo/build/generated/demo-registry/DemoSourceMap.properties'));
  const sources = readdirSync(sourceDir, { recursive: true }).filter(file => file.endsWith('.kt')).sort()
    .map(file => ({ file, source: readFileSync(path.join(sourceDir, file), 'utf8') }));
  const demos = discoverDemos(sources);
  const generated = renderRegistry(demos);
  mkdirSync(path.dirname(output), { recursive: true });
  if (!existsSync(output) || readFileSync(output, 'utf8') !== generated) writeFileSync(output, generated);
  const sourceMap = renderDemoSourceMap(demos);
  mkdirSync(path.dirname(sourceMapOutput), { recursive: true });
  if (!existsSync(sourceMapOutput) || readFileSync(sourceMapOutput, 'utf8') !== sourceMap) writeFileSync(sourceMapOutput, sourceMap);
  console.log(`Generated registry for ${demos.length} annotated demos.`);
}
