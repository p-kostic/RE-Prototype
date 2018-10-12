
function setEnabled(enabled){
    return new Promise(resolve => {
        chrome.storage.local.set({enabled: !!enabled}, () => resolve());
    });
}

function getEnabled(enabled){
    return new Promise(resolve => {
        chrome.storage.local.get("enabled", r => resolve(r.enabled));
    });
}

async function toggleEnabled(){
    let enabled = await getEnabled();
    enabled = enabled == null || !enabled;
    await setEnabled(enabled);
    return enabled;
}

function csskey(host){
    return `css_${host}`;
}

function setCss(css, host){
    return new Promise(resolve => {
        chrome.storage.local.set({[csskey(host)]: css}, () => resolve());
    })
}

function getCss(host){
    return new Promise(resolve => {
        chrome.storage.local.get([csskey(host)], r => resolve(r[csskey(host)]));
    });
}

function injectCSS(tab, css){
    return new Promise (resolve => {
        chrome.tabs.insertCSS(tab, {code: css}, () => { resolve(true); });
    });
}

async function handleMessage(request, sender, sendResponse){
    switch(request.type) {
        case 'UPDATE_STYLE':
            await setCss(request.content, request.host);
            return true;

        case 'INJECT_STYLE':
            const enabled = await getEnabled();
            const tab = sender.tab.id;
            const css = await getCss(request.host);
            console.log(css);
            if(css != null && enabled) {
                return await injectCSS(tab, css);
            }
            return false;

        case 'TOGGLE_STYLE':
            const response = await toggleEnabled();
            return response;
    }
}

//example of using a message handler from the inject scripts
chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        handleMessage(request, sender).then(response => { sendResponse(response); });
        return true;
    }
);

