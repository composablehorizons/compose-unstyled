import checkIcon from '../assets/icons/check.svg?raw';

for (const panel of document.querySelectorAll<HTMLElement>('[data-code-panel]')) {
  const source = panel.querySelector<HTMLElement>('.code-source')!;
  const code = source.querySelector('code')!;
  const toggle = panel.querySelector<HTMLButtonElement>('[data-code-toggle]');
  const bottomToggle = panel.querySelector<HTMLButtonElement>('[data-code-bottom-toggle]');
  const copy = panel.querySelector<HTMLButtonElement>('[data-code-copy]')!;
  const status = panel.querySelector<HTMLElement>('.code-status')!;

  const setExpanded = (expanded: boolean) => {
    if (!toggle) return;
    panel.dataset.collapsed = String(!expanded);
    toggle.setAttribute('aria-expanded', String(expanded));
    const label = expanded ? 'Collapse code' : 'Expand code';
    toggle.setAttribute('aria-label', label);
    toggle.title = label;
    if (bottomToggle) {
      bottomToggle.textContent = expanded ? 'Hide Code' : 'Show Code';
      bottomToggle.setAttribute('aria-expanded', String(expanded));
    }
    const pre = source.querySelector('pre')!;
    pre.tabIndex = expanded ? 0 : -1;
  };
  if (toggle) {
    toggle.disabled = false;
    toggle.addEventListener('click', () => setExpanded(toggle.getAttribute('aria-expanded') !== 'true'));
    if (bottomToggle) {
      bottomToggle.disabled = false;
      bottomToggle.addEventListener('click', event => {
        event.stopPropagation();
        setExpanded(panel.dataset.collapsed === 'true');
      });
    }
    source.addEventListener('click', () => {
      if (panel.dataset.collapsed === 'true') setExpanded(true);
    });
  }
  const copyIcon = copy.innerHTML;
  let confirmationTimer: ReturnType<typeof setTimeout> | undefined;
  const resetCopy = () => {
    copy.innerHTML = copyIcon;
    delete copy.dataset.copied;
    copy.setAttribute('aria-label', 'Copy code');
    copy.title = 'Copy code';
  };
  copy.disabled = false;
  copy.addEventListener('click', async () => {
    copy.disabled = true;
    status.textContent = '';
    delete status.dataset.copied;
    clearTimeout(confirmationTimer);
    resetCopy();
    try {
      await navigator.clipboard.writeText(code.textContent || '');
      copy.innerHTML = `<span aria-hidden="true">${checkIcon.replace(/<!--[\s\S]*?-->/g, '')}</span>`;
      copy.dataset.copied = 'true';
      copy.setAttribute('aria-label', 'Copied');
      copy.title = 'Copied';
      status.dataset.copied = 'true';
      status.textContent = 'Code copied.';
      confirmationTimer = setTimeout(() => {
        resetCopy();
        status.textContent = '';
      }, 1800);
    } catch {
      setExpanded(true);
      status.textContent = 'Could not copy. Select the code to copy it manually.';
    } finally {
      copy.disabled = false;
    }
  });
}
