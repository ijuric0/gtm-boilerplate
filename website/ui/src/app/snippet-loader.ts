import { environment } from '../environments/environment';

declare global {
  interface Window {
    dataLayer: any[];
  }
}

export function loadGtmScripts(): void {
  function getCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) return match[2];
    return null;
  }

  const loadGtag = getCookie('gtag') === 'true';
  const useFirstParty = getCookie('gtg') === 'true';
  const scriptDomain = useFirstParty ? environment.firstPartyUrl : 'https://www.googletagmanager.com';

  if (loadGtag) {
    console.log(`🚀 Loading GTAG (${useFirstParty ? 'First party' : '3rd Party'}) 🚀`);

    const libScript = document.createElement('script');
    libScript.async = true;
    libScript.src = `${scriptDomain}/gtag/js?id=${environment.gtagId}`;
    document.head.insertBefore(libScript, document.head.firstChild);

    const configScript = document.createElement('script');
    configScript.id = 'gtag-init';

    const configParams: any = {};
    if (useFirstParty) {
      configParams.server_container_url = environment.serverContainerUrl;
    }

    const scriptContent = [
      `window.dataLayer = window.dataLayer || [];`,
      `function gtag(){dataLayer.push(arguments);}`,
      `gtag('js', new Date());`,
      `gtag('config', '${environment.gtagId}', ${JSON.stringify(configParams)});`
    ].join('\n');

    configScript.textContent = scriptContent;

    if (libScript.parentNode) {
      libScript.parentNode.insertBefore(configScript, libScript.nextSibling);
    }

  } else {
    console.log(`🚀 Loading GTM (${useFirstParty ? 'First party' : '3rd party'}) 🚀`);

    (function (w: any, d: Document, s: string, l: string, i: string) {
      w[l] = w[l] || [];
      w[l].push({
        'gtm.start': new Date().getTime(),
        event: 'gtm.js'
      });
      const f = d.getElementsByTagName(s)[0];
      const j = d.createElement(s) as HTMLScriptElement;
      const dl = l !== 'dataLayer' ? '&l=' + l : '';
      j.async = true;
      j.src = `${scriptDomain}/gtm.js?id=${i}${dl}`;
      if (f && f.parentNode) {
        f.parentNode.insertBefore(j, f);
      }
    })(window, document, 'script', 'dataLayer', environment.gtmContainerId);
  }
}