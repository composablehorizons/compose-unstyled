export const site = process.env.SITE_URL || 'http://localhost:4321';
export const base = `/${(process.env.BASE_PATH || '').replace(/^\/+|\/+$/g, '')}`;
export const sitePath = (path) => `${base === '/' ? '' : base}/${path.replace(/^\/+/, '')}`;
export const siteUrl = (path) => new URL(sitePath(path), site).href;
