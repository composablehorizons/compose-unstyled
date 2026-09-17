import type { APIRoute } from 'astro';
import { siteUrl } from '../../site.config.js';

export const GET: APIRoute = () => new Response(`User-agent: *\nAllow: /\n\nSitemap: ${siteUrl('/sitemap-index.xml')}\n`, {
  headers: { 'Content-Type': 'text/plain' },
});
