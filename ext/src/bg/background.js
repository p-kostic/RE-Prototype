
const API_URL = 'http://vdzijden.com/api/';

function set(key, value){
    return new Promise(resolve => {
        chrome.storage.local.set({[key]: value}, () => resolve());
    });
}

function get(key){
    return new Promise(resolve => {
        chrome.storage.local.get([key], r => resolve(r[key]));
    });
}

async function toggleEnabled(){
    let enabled = await get("enabled");
    enabled = enabled == null || !enabled;
    await set("enabled", enabled);
    return enabled;
}

async function getUUID(){
    let uuid = await get("uuid");
    if(uuid == null){
        const uuid_bytes = crypto.getRandomValues(new Uint8Array(16));
        uuid = Array.from(uuid_bytes).map(x => x.toString(16).padStart(2, '0')).join("");
        await set('uuid', uuid);
    }
    return uuid;
}

function csskey(host){
    return `css_${host}`;
}

async function setCss(host, css){
    return await set(csskey(host), css);
}

async function getCss(host){
    return await get(csskey(host));
}

function insertCSS(tab, css){
    return new Promise (resolve => {
        chrome.tabs.insertCSS(tab, {code: css, runAt: "document_start"}, () => { resolve(true); });
    });
}

async function fetchCSS(host){
    const uuid = await getUUID();
    const enabled = await get('enabled');
    let response = await fetch(API_URL + `?uuid=${uuid}&enabled=${enabled}&host=${host}`, {
        mode: "cors",
        method: "GET",
        credentials: "include"
    });
    return await response.text();
}

async function updateStyle(host){
    const css = await fetchCSS(host);
    await setCss(host, css);
}

async function injectStyle(tab, host){
    const enabled = await get('enabled');
    const css = await getCss(host);
    if(css != null && enabled) {
        return await insertCSS(tab, css);
    }
    return false;
}

async function handleMessage(request, sender, sendResponse){
    switch(request.type) {
        case 'UPDATE_STYLE':
            await updateStyle(request.host);
            return true;

        case 'INJECT_STYLE':
            return await injectStyle(sender.tab.id, request.host);

        case 'TOGGLE_STYLE':
            const response = await toggleEnabled();
            return response;
    }
}

chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        console.debug("Received message: ", {request, sender});
        handleMessage(request, sender)
            .then(response => { sendResponse(response); });
        return true;
    }
);

chrome.webNavigation.onCommitted.addListener(async (obj) => {
    const {tabId, url} = obj;
    const host = new URL(url).host;
    const inj = injectStyle(tabId, host);
    const upd = updateStyle(host);
    await Promise.all([inj, upd]);
    await injectStyle(tabId, host);
});
