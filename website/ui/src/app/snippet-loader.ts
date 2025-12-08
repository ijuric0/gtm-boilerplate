import { environment } from '../environments/environment';

declare global {
  interface Window {
    dataLayer: any[];
    gtag: (...args: any[]) => void;
  }
}

export function loadGtmScripts(): void {
  function getCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) return match[2];
    return null;
  }

  const tagType = getCookie('tag-type');
  let loadGtag = false;
  let scriptDomain = 'https://www.googletagmanager.com';
  let useFirstParty = false;

  switch (tagType) {
    case 'gtag':
      loadGtag = true;
      break;

    case 'gtag-gtg':
      loadGtag = true;
      useFirstParty = true;
      scriptDomain = environment.firstPartyUrl;
      break;

    case 'gtm-gtg':
      loadGtag = false;
      useFirstParty = true;
      scriptDomain = environment.firstPartyUrl;
      break;

    default:
      loadGtag = false;
      useFirstParty = false;
      scriptDomain = 'https://www.googletagmanager.com';
      break;
  }

  console.log(`🚀 Loading ${loadGtag ? 'GTAG' : 'GTM'} (${useFirstParty ? 'First-Party' : 'Default'}) 🚀 `);

  if (loadGtag) {
    const libScript = document.createElement('script');
    libScript.async = true;
    libScript.src = `${scriptDomain}/gtag/js?id=${environment.gtagId}`;
    document.head.insertBefore(libScript, document.head.firstChild);

    const configScript = document.createElement('script');

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
    (function(w: any, d: Document, s: string, l: string, i: string) {
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
