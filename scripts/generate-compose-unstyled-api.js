import fs from 'node:fs';
import path from 'node:path';

const root = process.cwd();
const docsPagesDir = path.join(root, 'docs/pages');
const outputPagesDir = process.argv[2]
  ? path.resolve(process.argv[2])
  : path.join(root, 'build/generated/compose-unstyled-docs/pages');

let publicKotlinSources;

fs.rmSync(outputPagesDir, { recursive: true, force: true });
fs.mkdirSync(outputPagesDir, { recursive: true });

for (const entry of fs.readdirSync(docsPagesDir, { withFileTypes: true })) {
  if (!entry.isFile() || !entry.name.endsWith('.md')) continue;

  const inputPath = path.join(docsPagesDir, entry.name);
  const outputPath = path.join(outputPagesDir, entry.name);
  let page = fs.readFileSync(inputPath, 'utf8');

  page = page.replace(/<ApiReference\s+([^>]*?)\/>/g, (_, attributes) => {
    const marker = parseAttributes(attributes);
    if (marker.declaration) {
      return renderApiDeclaration(marker.declaration, marker.title).trimEnd();
    }

    if (!marker.declaration) {
      throw new Error(`Invalid API reference marker in ${entry.name}.`);
    }
    return renderApiDeclaration(marker.declaration, marker.title).trimEnd();
  });

  fs.writeFileSync(outputPath, page.endsWith('\n') ? page : `${page}\n`);
}

function parseAttributes(attributes) {
  return Object.fromEntries(
    [...attributes.matchAll(/([A-Za-z][A-Za-z0-9]*)="([^"]+)"/g)].map(([, name, value]) => [name, value]),
  );
}

function renderApiDeclaration(declaration, title) {
  const declarationSources = publicApiSources().flatMap((source) => {
    if (!declaration.startsWith(`${source.packageName}.`)) return [];
    const [receiver, name] = splitDeclaration(declaration.slice(source.packageName.length + 1));
    const functionDeclarationRows = functionRows(source.kotlin, name, receiver);
    const isFunction = functionDeclarationRows.length > 0;
    const declarationRows = isFunction ? functionDeclarationRows : classRows(source.kotlin, name);
    return declarationRows.length > 0
      ? [{
        declarationRows,
        description: isFunction ? undefined : classDescription(source.kotlin, name),
        title: declaration.slice(source.packageName.length + 1),
      }]
      : [];
  });
  const declarationRows = uniqueRows(declarationSources.flatMap((source) => source.declarationRows));
  if (declarationRows.length === 0) {
    throw new Error(`Could not generate public API reference '${declaration}'.`);
  }
  return renderTable(
    title ?? declarationSources[0].title,
    declarationRows,
    declarationSources.map((source) => source.description).find(Boolean),
  );
}

function publicApiSources() {
  if (publicKotlinSources) return publicKotlinSources;
  publicKotlinSources = fs.readdirSync(root, { withFileTypes: true })
    .filter((entry) => entry.isDirectory() && entry.name.startsWith('composeunstyled'))
    .flatMap((entry) => {
      const sourceSets = path.join(root, entry.name, 'src');
      return fs.existsSync(sourceSets)
        ? fs.readdirSync(sourceSets, { withFileTypes: true })
          .filter((sourceSet) => sourceSet.isDirectory() && sourceSet.name.endsWith('Main'))
          .flatMap((sourceSet) => kotlinFiles(path.join(sourceSets, sourceSet.name, 'kotlin')))
        : [];
    })
    .map((file) => {
      const kotlin = fs.readFileSync(file, 'utf8');
      const packageName = /^package\s+([A-Za-z0-9_.]+)/m.exec(kotlin)?.[1];
      return packageName ? { kotlin, packageName } : undefined;
    })
    .filter(Boolean);
  return publicKotlinSources;
}

function kotlinFiles(directory) {
  if (!fs.existsSync(directory)) return [];
  return fs.readdirSync(directory, { withFileTypes: true }).flatMap((entry) => {
    const file = path.join(directory, entry.name);
    if (entry.isDirectory()) return kotlinFiles(file);
    return entry.isFile() && entry.name.endsWith('.kt') ? [file] : [];
  });
}

function splitDeclaration(declaration) {
  const separator = declaration.lastIndexOf('.');
  return separator < 0
    ? [undefined, declaration]
    : [declaration.slice(0, separator), declaration.slice(separator + 1)];
}

function renderTable(title, rows, description) {
  return [
    `### ${title}`,
    '',
    ...(description ? [description, ''] : []),
    '| Parameter | Type | Description |',
    '|-----------|------|-------------|',
    ...rows.map((row) => {
      return `| \`${escapePipes(row.name)}\` | \`${escapePipes(row.type)}\` | ${row.description ?? ''} |`;
    }),
  ].join('\n');
}

function classDescription(kotlin, name) {
  const sources = Array.isArray(kotlin) ? kotlin : [kotlin];
  for (const source of sources) {
    const pattern = new RegExp(`(?:[A-Za-z]+\\s+)*(?:class|interface)\\s+${escapeRegex(name)}\\b`, 'g');
    const match = pattern.exec(source);
    if (match) return attachedKDoc(source, match.index).summary;
  }
  return undefined;
}

function functionRows(kotlin, name, receiver) {
  const sources = Array.isArray(kotlin) ? kotlin : [kotlin];
  const signatures = sources.flatMap((source) => findCallableBlocks(source, 'fun', name, receiver));
  const rows = [];
  for (const signature of signatures) {
    const parameters = parameterRows(signature.parameters, signature.tags.param);
    if (parameters.length > 0) {
      rows.push(...parameters);
    } else {
      rows.push({ name: 'returns', type: signature.returnType || 'Unit', description: signature.description });
    }
  }
  return uniqueRows(rows);
}

function classRows(kotlin, name) {
  const sources = Array.isArray(kotlin) ? kotlin : [kotlin];
  let blocks = sources.flatMap((source) => findCallableBlocks(source, 'class', name));
  if (blocks.length === 0) {
    blocks = sources.flatMap((source) => {
      const body = findClassBody(source, name);
      return body ? [{ parameters: '', body, tags: { param: {}, property: {} } }] : [];
    });
  }
  const rows = [];
  for (const block of blocks) {
    const constructorRows = constructorPropertyRows(block.parameters, block.tags.property);
    rows.push(...constructorRows);
    if (block.body) {
      const propertyRegex = /^\s*(?:override\s+)?(?:val|var)\s+([A-Za-z_][A-Za-z0-9_]*)\s*:\s*([^\n=]+?)(?:\s+by\b|\s*=|\s*$)/gm;
      for (const match of block.body.matchAll(propertyRegex)) {
        if (match[1].startsWith('_')) continue;
        rows.push({ name: match[1], type: cleanupType(match[2]), description: attachedKDoc(block.body, match.index).summary });
      }
      const inferredStateRegex = /^\s*(?:override\s+)?(?:val|var)\s+([A-Za-z_][A-Za-z0-9_]*)\s+by\s+mutableStateOf\(([^)]*)\)/gm;
      for (const match of block.body.matchAll(inferredStateRegex)) {
        if (match[1].startsWith('_')) continue;
        rows.push({ name: match[1], type: inferLiteralType(match[2]), description: attachedKDoc(block.body, match.index).summary });
      }
      const methodRegex = /^\s*(suspend\s+)?fun\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(([^)]*)\)\s*(?::\s*([^{=\n]+))?/gm;
      for (const match of block.body.matchAll(methodRegex)) {
        const params = parameterRows(match[3]).map((row) => row.type).join(', ');
        const returnType = cleanupType(match[4] ?? 'Unit');
        const type = `${match[1] ?? ''}(${params}) -> ${returnType}`.trim();
        rows.push({ name: `${match[1] ?? ''}fun ${match[2]}()`.trim(), type, description: attachedKDoc(block.body, match.index).summary });
      }
    }
    if (constructorRows.length === 0 && rows.length === 0) {
      rows.push(...parameterRows(block.parameters, block.tags.param));
    }
  }
  return uniqueRows(rows);
}

function constructorPropertyRows(parameters, descriptions) {
  return splitTopLevel(parameters, ',')
    .map((parameter) => parameter.trim())
    .filter((parameter) => /^(?:override\s+)?(?:val|var)\s+/.test(parameter))
    .map((parameter) => parameterRows(parameter, descriptions)[0])
    .filter(Boolean);
}

function inferLiteralType(value) {
  const trimmed = value.trim();
  if (/^-?\d+(?:\.\d+)?f$/i.test(trimmed)) return 'Float';
  if (/^-?\d+$/.test(trimmed)) return 'Int';
  if (trimmed === 'true' || trimmed === 'false') return 'Boolean';
  if (trimmed.startsWith('"')) return 'String';
  return 'Any?';
}

function findClassBody(kotlin, name) {
  const pattern = new RegExp(`(?:[A-Za-z]+\\s+)*(?:class|interface)\\s+${name}\\b`, 'g');
  const match = pattern.exec(kotlin);
  if (!match) return '';
  const bodyStart = kotlin.indexOf('{', match.index);
  if (bodyStart < 0) return '';
  const bodyEnd = findMatching(kotlin, bodyStart, '{', '}');
  return bodyEnd > bodyStart ? kotlin.slice(bodyStart + 1, bodyEnd) : '';
}

function findCallableBlocks(kotlin, keyword, name, receiver) {
  const classConstructor = keyword === 'class' ? '(?:\\s+[A-Za-z]+\\s+constructor)?' : '';
  const receiverPattern = receiver ? `${escapeRegex(receiver)}(?:<[^>]+>)?\\.` : '(?:[A-Za-z0-9_<>]+\\.)?';
  const pattern = new RegExp(`^\\s*(?:@[A-Za-z0-9_.()=,\\s]+\\n\\s*)*(?!private\\b|internal\\b)(?:[A-Za-z]+\\s+)*${keyword}(?:\\s*<[^>]+>)?\\s+${receiverPattern}${escapeRegex(name)}${classConstructor}\\s*\\(`, 'gm');
  const blocks = [];
  let match;
  while ((match = pattern.exec(kotlin)) !== null) {
    const parametersStart = kotlin.indexOf('(', match.index);
    const parametersEnd = findMatching(kotlin, parametersStart, '(', ')');
    if (parametersEnd < 0) continue;
    const parameters = kotlin.slice(parametersStart + 1, parametersEnd);
    const returnTypeMatch = /^\s*:\s*([^{=\n]+)/.exec(kotlin.slice(parametersEnd + 1));
    const returnType = returnTypeMatch ? cleanupType(returnTypeMatch[1]) : '';
    const bodyStart = kotlin.indexOf('{', parametersEnd);
    let body = '';
    if (bodyStart >= 0) {
      const bodyEnd = findMatching(kotlin, bodyStart, '{', '}');
      if (bodyEnd > bodyStart) body = kotlin.slice(bodyStart + 1, bodyEnd);
    }
    const kdoc = attachedKDoc(kotlin, match.index);
    blocks.push({ parameters, returnType, body, description: kdoc.summary, tags: kdoc.tags });
  }
  return blocks;
}

function escapeRegex(value) {
  return value.replace(/[|\\{}()[\]^$+*?.]/g, '\\$&');
}

function parameterRows(parameters, descriptions = {}) {
  return splitTopLevel(parameters, ',')
    .map((parameter) => parameter.trim())
    .filter(Boolean)
    .map((parameter) => parameter.replace(/@\w+(?:\([^)]*\))?\s*/g, '').trim())
    .filter((parameter) => parameter.includes(':'))
    .map((parameter) => {
      const colon = parameter.indexOf(':');
      const rawName = parameter.slice(0, colon).trim().replace(/^(?:(?:private|internal|public|override)\s+)*(?:val|var)\s+/, '');
      const rawType = parameter.slice(colon + 1).split('=')[0].trim().replace(/,$/, '');
      return { name: rawName, type: cleanupType(rawType), description: descriptions[rawName] };
    })
    .filter((row) => row.name && row.type);
}

function splitTopLevel(text, separator) {
  const parts = [];
  let start = 0;
  let depth = 0;
  let quote = null;
  for (let index = 0; index < text.length; index += 1) {
    const char = text[index];
    if (quote) {
      if (char === quote && text[index - 1] !== '\\') quote = null;
      continue;
    }
    if (char === '"' || char === "'") {
      quote = char;
    } else if ('([{'.includes(char)) {
      depth += 1;
    } else if (')]}'.includes(char)) {
      depth -= 1;
    } else if (char === separator && depth === 0) {
      parts.push(text.slice(start, index));
      start = index + 1;
    }
  }
  parts.push(text.slice(start));
  return parts;
}

function findMatching(text, start, open, close) {
  let depth = 0;
  let quote = null;
  for (let index = start; index < text.length; index += 1) {
    const char = text[index];
    if (quote) {
      if (char === quote && text[index - 1] !== '\\') quote = null;
      continue;
    }
    if (char === '"' || char === "'") {
      quote = char;
    } else if (char === open) {
      depth += 1;
    } else if (char === close) {
      depth -= 1;
      if (depth === 0) return index;
    }
  }
  return -1;
}

function uniqueRows(rows) {
  const seen = new Set();
  return rows.filter((row) => {
    const key = `${row.name}:${row.type}`;
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
}

function attachedKDoc(text, declarationStart) {
  const beforeDeclaration = text.slice(0, declarationStart);
  const start = beforeDeclaration.lastIndexOf('/**');
  const end = beforeDeclaration.lastIndexOf('*/');
  if (start < 0 || end < start) return { summary: '', tags: { param: {}, property: {} } };
  const between = beforeDeclaration.slice(end + 2);
  if (!/^\s*(?:@[^\n]+\s*)*$/.test(between)) return { summary: '', tags: { param: {}, property: {} } };
  return parseKDoc(beforeDeclaration.slice(start, end + 2));
}

function parseKDoc(kdoc) {
  const lines = kdoc
    .slice(3, -2)
    .split('\n')
    .map((line) => line.replace(/^\s*\* ?/, '').trim());
  const tags = { param: {}, property: {} };
  const summary = [];
  for (const line of lines) {
    const tag = /^@(param|property)\s+([A-Za-z_][A-Za-z0-9_]*)\s*(.*)$/.exec(line);
    if (tag) {
      tags[tag[1]][tag[2]] = tag[3];
    } else if (!line.startsWith('@')) {
      summary.push(line);
    }
  }
  return {
    summary: formatKDocMarkdown(summary.filter(Boolean).join(' ')),
    tags: {
      param: Object.fromEntries(
        Object.entries(tags.param).map(([name, description]) => [name, formatKDocMarkdown(description)]),
      ),
      property: Object.fromEntries(
        Object.entries(tags.property).map(([name, description]) => [name, formatKDocMarkdown(description)]),
      ),
    },
  };
}

function formatKDocMarkdown(text) {
  return text.replace(/\[([A-Za-z_][A-Za-z0-9_.]*)\]/g, '`$1`');
}

function cleanupType(type) {
  return type.replace(/\s+/g, ' ').trim();
}

function escapePipes(text) {
  return text.replace(/\|/g, '\\|');
}
