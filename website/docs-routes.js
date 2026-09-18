export const docsPath = (section, page) => {
  const pageSlug = page.routeSlug ?? page.slug;
  if (section.slug === pageSlug) return `/docs/${section.slug}/`;
  return `/docs/${section.slug}/${pageSlug}/`;
};

export const docsMarkdownPath = (section, page) => {
  const pageSlug = page.routeSlug ?? page.slug;
  if (section.slug === pageSlug) return `/docs/${section.slug}.md`;
  return `/docs/${section.slug}/${pageSlug}.md`;
};

export const findDocsPage = (navigation, slug) => navigation.sections
  .flatMap(section => section.pages.map(page => ({ section, page })))
  .find(entry => entry.page.slug === slug);
