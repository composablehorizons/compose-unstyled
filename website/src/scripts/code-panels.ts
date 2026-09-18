import checkIcon from '../assets/icons/check.svg?raw';

for (const panel of document.querySelectorAll<HTMLElement>('[data-code-panel]')) {
  const source = panel.querySelector<HTMLElement>('.code-source')!;
  const toggle = panel.querySelector<HTMLButtonElement>('[data-code-toggle]');
  const bottomToggle = panel.querySelector<HTMLButtonElement>('[data-code-bottom-toggle]');
  const copy = panel.querySelector<HTMLButtonElement>('[data-code-copy]')!;
  const status = panel.querySelector<HTMLElement>('.code-status')!;

  const tabs = [...panel.querySelectorAll<HTMLButtonElement>('[data-code-tab]')];
  const sources = [...panel.querySelectorAll<HTMLElement>('.code-source')];
  const storageKey = tabs.length === 0 ? undefined : `code-tabs:${location.pathname}:${tabs[0].id}`;
  const storedTab = () => {
    try {
      return storageKey ? Number(localStorage.getItem(storageKey)) : undefined;
    } catch {
      return undefined;
    }
  };
  const selectTab = (selectedIndex: number) => {
    tabs.forEach((tab, index) => {
      const selected = index === selectedIndex;
      tab.setAttribute('aria-selected', String(selected));
      tab.tabIndex = selected ? 0 : -1;
      sources[index].hidden = !selected;
    });
    try {
      if (storageKey) localStorage.setItem(storageKey, String(selectedIndex));
    } catch {
      // The tabs remain usable when browser storage is unavailable.
    }
  };
  tabs.forEach((tab, index) => {
    tab.addEventListener('click', () => selectTab(index));
    tab.addEventListener('keydown', event => {
      const offsets: Record<string, number> = { ArrowRight: 1, ArrowLeft: -1 };
      const targetIndex = event.key === 'Home'
        ? 0
        : event.key === 'End'
          ? tabs.length - 1
          : offsets[event.key] === undefined
            ? undefined
            : (index + offsets[event.key] + tabs.length) % tabs.length;
      if (targetIndex === undefined) return;
      event.preventDefault();
      selectTab(targetIndex);
      tabs[targetIndex].focus();
    });
  });
  if (storageKey) {
    const selectedIndex = storedTab();
    if (selectedIndex !== undefined && Number.isInteger(selectedIndex) && selectedIndex >= 0 && selectedIndex < tabs.length) {
      selectTab(selectedIndex);
    }
  }

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
      const visibleCode = panel.querySelector<HTMLElement>('.code-source:not([hidden]) code')!;
      await navigator.clipboard.writeText(visibleCode.textContent || '');
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
