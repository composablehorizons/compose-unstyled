export const refDomain = 'composeunstyled.com';

export const withRef = (href) => {
  const url = new URL(href);
  url.searchParams.set('ref', refDomain);
  return url.href;
};
