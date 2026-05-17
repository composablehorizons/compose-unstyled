import fs from 'node:fs';
import path from 'node:path';

const root = process.cwd();
const docsPagesDir = path.join(root, 'docs/pages');
const apiDescriptionsPath = path.join(root, 'docs/api-descriptions.json');
const outputPagesDir = process.argv[2]
  ? path.resolve(process.argv[2])
  : path.join(root, 'build/generated/compose-unstyled-docs/pages');

const apiReferences = {
  'bottom-sheet': [
    source('composeunstyled-bottom-sheet/src/commonMain/kotlin/com/composeunstyled/BottomSheet.kt', [
      fn('rememberBottomSheetState'),
      cls('BottomSheetState'),
      fn('UnstyledBottomSheet', 'BottomSheet'),
      fn('Sheet', 'BottomSheetScope.Sheet'),
      fn('DragIndication', 'BottomSheetScope.DragIndication'),
    ]),
  ],
  'modal-bottom-sheet': [
    source('composeunstyled-modal-bottom-sheet/src/commonMain/kotlin/com/composeunstyled/ModalBottomSheet.kt', [
      fn('rememberModalBottomSheetState'),
      cls('ModalBottomSheetState'),
      fn('UnstyledModalBottomSheet'),
      fn('Sheet', 'ModalBottomSheetScope.Sheet'),
      fn('DragIndication', 'ModalBottomSheetScope.DragIndication'),
    ]),
  ],
  button: [source('composeunstyled-button/src/commonMain/kotlin/com/composeunstyled/Button.kt', [fn('UnstyledButton')])],
  checkbox: [
    source('composeunstyled-checkbox/src/commonMain/kotlin/com/composeunstyled/CheckBox.kt', [
      fn('UnstyledCheckbox'),
      fn('CheckedIndicator', 'CheckboxScope.CheckedIndicator'),
    ]),
  ],
  tristatecheckbox: [
    source('composeunstyled-tri-state-checkbox/src/commonMain/kotlin/com/composeunstyled/TriStateCheckBox.kt', [
      fn('UnstyledTriStateCheckbox'),
      cls('TriStateCheckboxScope'),
      fn('StateIndicator', 'TriStateCheckboxScope.StateIndicator'),
    ]),
  ],
  dialog: [
    source('composeunstyled-dialog/src/commonMain/kotlin/com/composeunstyled/Dialog.kt', [
      fn('UnstyledDialog'),
      fn('DialogPanel', 'DialogScope.DialogPanel'),
    ]),
    source('composeunstyled-scrim/src/commonMain/kotlin/com/composeunstyled/Scrim.kt', [fn('Scrim', 'ModalScope.Scrim')]),
  ],
  modal: [
    source('composeunstyled-modal/src/commonMain/kotlin/com/composeunstyled/Modal.kt', [
      cls('ModalState'),
      fn('rememberModalState'),
      fn('Modal'),
    ]),
  ],
  scrim: [
    source('composeunstyled-scrim/src/commonMain/kotlin/com/composeunstyled/Scrim.kt', [
      fn('Scrim', 'ModalScope.Scrim'),
    ]),
  ],
  disclosure: [
    source('composeunstyled-disclosure/src/commonMain/kotlin/com/composeunstyled/Disclosure.kt', [
      fn('UnstyledDisclosure'),
      fn('DisclosureButton', 'DisclosureScope.DisclosureButton'),
      fn('DisclosedContent', 'DisclosureScope.DisclosedContent'),
    ]),
  ],
  'dropdown-menu': [
    source('composeunstyled-dropdown-menu/src/commonMain/kotlin/com/composeunstyled/DropdownMenu.kt', [
      fn('UnstyledDropdownMenu'),
      fn('DropdownMenuPanel', 'DropdownMenuScope.DropdownMenuPanel'),
      fn('MenuItem', 'DropdownMenuPanelScope.MenuItem'),
    ]),
  ],
  icon: [source('composeunstyled-icon/src/commonMain/kotlin/com/composeunstyled/Icon.kt', [fn('UnstyledIcon')])],
  progressindicator: [
    source('composeunstyled-progress/src/commonMain/kotlin/com/composeunstyled/ProgressIndicator.kt', [
      fn('UnstyledProgress'),
      cls('ProgressScope'),
      fn('Indicator', 'ProgressScope.Indicator'),
    ]),
  ],
  radiogroup: [
    source('composeunstyled-radio-group/src/commonMain/kotlin/com/composeunstyled/RadioGroup.kt', [
      fn('UnstyledRadioGroup'),
      fn('RadioButton', 'RadioGroupScope.RadioButton'),
      fn('SelectedIndicator', 'RadioButtonScope.SelectedIndicator'),
    ]),
  ],
  scrollarea: [
    source('composeunstyled-scrollbars/src/commonMain/kotlin/com/composeunstyled/Scrollbars.kt', [
      fn('rememberScrollbarState'),
      fn('UnstyledVerticalScrollbar'),
      fn('UnstyledHorizontalScrollbar'),
      fn('Thumb', 'ScrollbarScope.Thumb'),
    ]),
  ],
  portal: [
    source('composeunstyled-portal/src/commonMain/kotlin/com/composeunstyled/Portal.kt', [
      fn('PortalHost'),
      fn('Portal'),
    ]),
  ],
  separators: [
    source('composeunstyled-separators/src/commonMain/kotlin/com/composeunstyled/Separators.kt', [
      fn('UnstyledHorizontalSeparator'),
      fn('UnstyledVerticalSeparator'),
    ]),
  ],
  slider: [
    source('composeunstyled-slider/src/commonMain/kotlin/com/composeunstyled/Slider.kt', [
      cls('SliderState'),
      fn('UnstyledSlider'),
    ]),
  ],
  stack: [source('composeunstyled-stack/src/commonMain/kotlin/com/composeunstyled/Stack.kt', [fn('Stack')])],
  tabgroup: [
    source('composeunstyled-tab-group/src/commonMain/kotlin/com/composeunstyled/TabGroup.kt', [
      fn('UnstyledTabGroup'),
      fn('TabList', 'TabGroupScope.TabList'),
      fn('Tab', 'TabListScope.Tab'),
      fn('TabPanel', 'TabGroupScope.TabPanel'),
    ]),
  ],
  textfield: [
    source('composeunstyled-text-field/src/commonMain/kotlin/com/composeunstyled/TextField.kt', [
      fn('UnstyledTextField'),
      fn('TextInput', 'TextFieldScope.TextInput'),
    ]),
  ],
  toggleswitch: [
    source('composeunstyled-toggle-switch/src/commonMain/kotlin/com/composeunstyled/ToggleSwitch.kt', [
      fn('UnstyledSwitch'),
      cls('SwitchScope'),
      fn('SwitchThumb', 'SwitchScope.SwitchThumb'),
    ]),
  ],
  tooltip: [
    source('composeunstyled-tooltip/src/commonMain/kotlin/com/composeunstyled/Tooltip.kt', [
      fn('TooltipHost'),
      fn('UnstyledTooltip'),
      cls('TooltipPlacement'),
      fn('TooltipPanel', 'TooltipScope.TooltipPanel'),
    ]),
  ],
  typography: [
    source('composeunstyled-theming/src/commonMain/kotlin/com/composeunstyled/theme/Text.kt', [fn('Text')]),
  ],
  'android-xml-themes': [
    source('composeunstyled-theming/src/androidMain/kotlin/com/composeunstyled/theme/XmlTheme.kt', [
      fn('resolveThemeColor'),
      fn('resolveThemeDp'),
      fn('resolveThemeSp'),
      fn('resolveThemePx'),
      fn('resolveThemeInt'),
      fn('resolveThemeFloat'),
      fn('resolveThemeString'),
      fn('resolveThemeBoolean'),
      fn('resolveThemeTextAppearance'),
    ]),
  ],
  'platform-themes': [
    source('composeunstyled-platformtheme/src/commonMain/kotlin/com/composeunstyled/platformtheme/PlatformTheme.kt', [
      fn('buildPlatformTheme'),
      fn('platformIndication'),
      fn('interactiveSize', 'Modifier.interactiveSize'),
    ]),
  ],
  buildModifier: [
    source('composeunstyled-build-modifier/src/commonMain/kotlin/com/composeunstyled/BuildModifier.kt', [fn('buildModifier')]),
  ],
  focusRing: [
    source('composeunstyled-focus-ring/src/commonMain/kotlin/com/composeunstyled/FocusRing.kt', [fn('focusRing', 'Modifier.focusRing')]),
  ],
  outline: [
    source('composeunstyled-outline/src/commonMain/kotlin/com/composeunstyled/Outline.kt', [fn('outline', 'Modifier.outline')]),
  ],
  'window-container-size': [
    source('composeunstyled-window-container-size/src/commonMain/kotlin/com/composeunstyled/WindowContainerSize.kt', [fn('rememberWindowContainerSize')], { optional: true }),
  ],
  defaultMinimumComponentInteractiveSize: [
    source('composeunstyled-primitives/src/commonMain/kotlin/com/composeunstyled/DefaultMinimumComponentInteractiveSize.kt', [fn('defaultMinimumComponentInteractiveSize')], { optional: true }),
  ],
  coloredindication: [
    source('composeunstyled-colored-indication/src/commonMain/kotlin/com/composeunstyled/theme/ColoredIndication.kt', [
      fn('rememberColoredIndication'),
      cls('ColoredIndication'),
    ]),
  ],
};

const apiDescriptions = fs.existsSync(apiDescriptionsPath)
  ? JSON.parse(fs.readFileSync(apiDescriptionsPath, 'utf8'))
  : {};

function source(file, declarations, options = {}) {
  return { file, declarations, optional: options.optional === true };
}

function fn(name, title = name) {
  return { kind: 'function', name, title };
}

function cls(name, title = name) {
  return { kind: 'class', name, title };
}

fs.rmSync(outputPagesDir, { recursive: true, force: true });
fs.mkdirSync(outputPagesDir, { recursive: true });

for (const entry of fs.readdirSync(docsPagesDir, { withFileTypes: true })) {
  if (!entry.isFile() || !entry.name.endsWith('.md')) continue;

  const inputPath = path.join(docsPagesDir, entry.name);
  const outputPath = path.join(outputPagesDir, entry.name);
  let page = fs.readFileSync(inputPath, 'utf8');

  page = page.replace(/<ApiReference\s+id="([A-Za-z0-9._-]+)"\s*\/>/g, (_, id) => {
    const references = apiReferences[id];
    if (!references) {
      throw new Error(`No API reference generator registered for '${id}' in ${entry.name}`);
    }
    return renderApiReference(id, references).trimEnd();
  });

  fs.writeFileSync(outputPath, page.endsWith('\n') ? page : `${page}\n`);
}

function renderApiReference(id, references) {
  const sections = [];
  for (const reference of references) {
    const absoluteSource = path.join(root, reference.file);
    if (!fs.existsSync(absoluteSource)) {
      if (reference.optional) continue;
      throw new Error(`Missing API source for '${id}': ${reference.file}`);
    }
    const kotlin = stripComments(fs.readFileSync(absoluteSource, 'utf8'));
    for (const declaration of reference.declarations) {
      const rows = declaration.kind === 'class'
        ? classRows(kotlin, declaration.name)
        : functionRows(kotlin, declaration.name);
      if (rows.length === 0 && !reference.optional) {
        throw new Error(`Could not generate '${declaration.title}' from ${reference.file}`);
      }
      if (rows.length > 0) {
        sections.push(renderTable(id, declaration.title, rows));
      }
    }
  }
  return `## API Reference\n\n${sections.join('\n\n')}\n`;
}

function renderTable(id, title, rows) {
  return [
    `### ${title}`,
    '',
    '| Parameter | Type | Description |',
    '|-----------|------|-------------|',
    ...rows.map((row) => {
      const description = apiDescriptions[id]?.[`${title}.${row.name}`] ?? apiDescriptions[id]?.[row.name] ?? '';
      return `| \`${escapePipes(row.name)}\` | \`${escapePipes(row.type)}\` | ${description} |`;
    }),
  ].join('\n');
}

function functionRows(kotlin, name) {
  const signatures = findCallableBlocks(kotlin, 'fun', name);
  const rows = [];
  for (const signature of signatures) {
    rows.push(...parameterRows(signature.parameters));
  }
  return uniqueRows(rows);
}

function classRows(kotlin, name) {
  let blocks = findCallableBlocks(kotlin, 'class', name);
  if (blocks.length === 0) {
    const body = findClassBody(kotlin, name);
    if (body) blocks = [{ parameters: '', body }];
  }
  const rows = [];
  for (const block of blocks) {
    const constructorRows = constructorPropertyRows(block.parameters);
    rows.push(...constructorRows);
    if (block.body) {
      const propertyRegex = /^\s*(?:override\s+)?(?:val|var)\s+([A-Za-z_][A-Za-z0-9_]*)\s*:\s*([^\n=]+?)(?:\s+by\b|\s*=|\s*$)/gm;
      for (const match of block.body.matchAll(propertyRegex)) {
        if (match[1].startsWith('_')) continue;
        rows.push({ name: match[1], type: cleanupType(match[2]) });
      }
      const inferredStateRegex = /^\s*(?:override\s+)?(?:val|var)\s+([A-Za-z_][A-Za-z0-9_]*)\s+by\s+mutableStateOf\(([^)]*)\)/gm;
      for (const match of block.body.matchAll(inferredStateRegex)) {
        if (match[1].startsWith('_')) continue;
        rows.push({ name: match[1], type: inferLiteralType(match[2]) });
      }
      const methodRegex = /^\s*(suspend\s+)?fun\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(([^)]*)\)\s*(?::\s*([^{=\n]+))?/gm;
      for (const match of block.body.matchAll(methodRegex)) {
        const params = parameterRows(match[3]).map((row) => row.type).join(', ');
        const returnType = cleanupType(match[4] ?? 'Unit');
        const type = `${match[1] ?? ''}(${params}) -> ${returnType}`.trim();
        rows.push({ name: `${match[1] ?? ''}fun ${match[2]}()`.trim(), type });
      }
    }
    if (constructorRows.length === 0 && rows.length === 0) {
      rows.push(...parameterRows(block.parameters));
    }
  }
  return uniqueRows(rows);
}

function constructorPropertyRows(parameters) {
  return splitTopLevel(parameters, ',')
    .map((parameter) => parameter.trim())
    .filter((parameter) => /^(?:override\s+)?(?:val|var)\s+/.test(parameter))
    .map((parameter) => parameterRows(parameter)[0])
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

function findCallableBlocks(kotlin, keyword, name) {
  const classConstructor = keyword === 'class' ? '(?:\\s+[A-Za-z]+\\s+constructor)?' : '';
  const pattern = new RegExp(`(?:@[A-Za-z0-9_.()=,\\s]+\\n\\s*)*(?:[A-Za-z]+\\s+)*${keyword}(?:\\s*<[^>]+>)?\\s+(?:[A-Za-z0-9_<>]+\\.)?${name}${classConstructor}\\s*\\(`, 'g');
  const blocks = [];
  let match;
  while ((match = pattern.exec(kotlin)) !== null) {
    const parametersStart = kotlin.indexOf('(', match.index);
    const parametersEnd = findMatching(kotlin, parametersStart, '(', ')');
    if (parametersEnd < 0) continue;
    const parameters = kotlin.slice(parametersStart + 1, parametersEnd);
    const bodyStart = kotlin.indexOf('{', parametersEnd);
    let body = '';
    if (bodyStart >= 0) {
      const bodyEnd = findMatching(kotlin, bodyStart, '{', '}');
      if (bodyEnd > bodyStart) body = kotlin.slice(bodyStart + 1, bodyEnd);
    }
    blocks.push({ parameters, body });
  }
  return blocks;
}

function parameterRows(parameters) {
  return splitTopLevel(parameters, ',')
    .map((parameter) => parameter.trim())
    .filter(Boolean)
    .map((parameter) => parameter.replace(/@\w+(?:\([^)]*\))?\s*/g, '').trim())
    .filter((parameter) => parameter.includes(':'))
    .map((parameter) => {
      const colon = parameter.indexOf(':');
      const rawName = parameter.slice(0, colon).trim().replace(/^(?:(?:private|internal|public|override)\s+)*(?:val|var)\s+/, '');
      const rawType = parameter.slice(colon + 1).split('=')[0].trim().replace(/,$/, '');
      return { name: rawName, type: cleanupType(rawType) };
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

function stripComments(text) {
  return text
    .replace(/\/\*[\s\S]*?\*\//g, '')
    .replace(/^\s*\/\/.*$/gm, '');
}

function cleanupType(type) {
  return type.replace(/\s+/g, ' ').trim();
}

function escapePipes(text) {
  return text.replace(/\|/g, '\\|');
}
