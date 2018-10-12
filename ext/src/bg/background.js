
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

function setCss(css){
    return new Promise(resolve => {
        chrome.storage.local.set({css}, () => resolve());
    })
}

function getCss(){
    return new Promise(resolve => {
        chrome.storage.local.get(["css"], r => resolve(r.css));
    });
}

async function handleMessage(request, sender, sendResponse){
    switch(request.type) {
        case 'UPDATE_STYLE': 
            await setCss(request.content);
            return true;
        case 'INJECT_STYLE':
            const enabled = await getEnabled();
            const tab = sender.tab.id;
            const css = await getCss();
            if(enabled) {
                chrome.tabs.insertCSS(tab, {code: css}, () => {sendResponse(true)});
            }
            else {
                sendResponse(false);
            }
            break;
        case 'TOGGLE_STYLE':
            const response = await toggleEnabled();
            sendResponse(response);
            break;
    }
}

//example of using a message handler from the inject scripts
chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        handleMessage(request, sender, sendResponse);
        return true;
    }
);
