
const API_URL = 'http://vdzijden.com/api/';

const SECONDS = 1000;
const MINUTES = 60 * SECONDS;
const HOURS = 60 * MINUTES;


const PROMPT_RATE = 1/1;

const prompts = [];

function timeout(n){
    new Promise((resolve, reject) => setTimeout(() => reject(), n));
}

async function sendMessage(message){
    return await handleMessage(message);
}

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

async function getEnabled(){
    let enabled = await get("enabled");
    if(enabled == null){
        await set("enabled", true);
        return await get("enabled");
    }
    return enabled;
}

async function toggleEnabled(){
    let enabled = !(await getEnabled());
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
    const t = new Date().toTimeString().split(' ', 2).join(' ').replace(' GMT', '');
    const uuid = await getUUID();
    const enabled = await getEnabled();
    const v = await get("variant");
    let response = await fetch(API_URL + `?uuid=${uuid}&enabled=${enabled}&host=${host}&localtime=${t}&variant=${v}`, {
        mode: "cors",
        method: "GET"
    });
    if(response.status >= 400){
        throw new Error(response.statusText);
    }
    return await response.text();
}

async function updateStyle(host){
    try {
        const css = await fetchCSS(host);
        await setCss(host, css);
    }
    catch(e) {
        console.warn(e);
    }
}

async function injectStyle(tab, host){
    const enabled = await get('enabled');
    const css = await getCss(host);
    if(css != null && enabled) {
        return await insertCSS(tab, css);
    }
    return false;
}
function getTab(tabId) {
    return new Promise(resolve => {
        chrome.tabs.get(tabId, r => resolve(r));
    });

}
function getWindow(windowId){
    return new Promise(resolve => {
        chrome.windows.get(windowId, r => resolve(r));
    });
}


function dismissAllPrompts(prompts_){
    prompts_ = prompts_ == null ? prompts : prompts_;
    while(prompts_.length > 0){
        try{
            chrome.windows.remove(prompts_.pop());
        } catch(e) {console.log(e)};
    }
}

async function hasDomain(host){
    return (await getCss(host)) != null;
}

async function feedbackPrompt(parentWindowId, host, force){
    const timeout = await get('timeout');
    if(!force){
        if(timeout == null){
            await set("timeout", 5);
            return;
        }
        const enabled = await getEnabled();

        const exists = await hasDomain(host);
        if(!exists) {
            console.log("Not exists: ", host)
            return;
        }
        if(timeout > 0 || !enabled){
            await set('timeout', timeout - 1);
            return;
        }
    }

    dismissAllPrompts(Array.from(prompts));

    const parentWin = await getWindow(parentWindowId);
    const centerX = (parentWin.left + parentWin.width / 2);
    const centerY = (parentWin.top + parentWin.height / 2);
    const w = 450;
    const h = 550;
    chrome.windows.create(
        {
            url: 'src/fb/fb.html',
            type: 'popup',
            focused: true,
            width: w | 0,
            height: h | 0,
            left: (centerX - w / 2) | 0,
            top: (centerY - h / 2) | 0,
        },
        function(window) {
            sendMessage({type: "REGISTER_PROMPT", windowId: window.id});
        }
    );
}

async function handleMessage(request, sender){
    console.log("Received message: ", {request, sender});
    switch(request.type) {
        case 'UPDATE_STYLE':
            await updateStyle(request.host);
            return true;

        case 'UPDATE_VARIANT':
            await set("variant", request.variant);
            return true;

        case 'INJECT_STYLE':
            return await injectStyle(sender.tab.id, request.host);

        case 'INJECT_STYLE_FOR_TAB':
            return await injectStyle(request.tabId, request.host);

        case 'TOGGLE_STYLE':
            {
                const enabled = await toggleEnabled();
                if(!enabled) chrome.tabs.create({url: "src/fb/fb-disable.html", selected: true});
                return enabled;
            }

        case 'FEEDBACK_PROMPT':
            return await feedbackPrompt(request.windowId, request.host, false);

        case 'PROMPT_FORM_SUBMITTED':
            await set('timeout', (25 + Math.random() * 25) | 0);
            dismissAllPrompts();
            return true;

        case 'PROMPT_DISMISS':
            {
                const curTimeout = await get('timeout');
                await set('timeout', Math.max(curTimeout, 15));
            }
            return dismissAllPrompts();

        case 'REGISTER_PROMPT':
            prompts.push(request.windowId)
            return true;
    }
}

chrome.windows.onFocusChanged.addListener(windowId => {
    return;
    const dels = [];
    for(let [promptId, details] of Object.entries(prompts)){
        const lifetime = Date.now() - details.registered_at;
        if(promptId !== windowId && !details.userInterest && lifetime >= 1000){
            chrome.windows.remove(promptId|0);
            dels.push(promptId);
        }
    }
    for(let del of dels){
        delete prompts[del];
    }
});

chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        console.debug("Received message: ", {request, sender});
        handleMessage(request, sender)
            .then(response => { sendResponse(response); })
            .catch(e => {console.log(e)});
        return true;a
    }
);

chrome.webNavigation.onCommitted.addListener(async (obj) => {
    try {
        const {tabId, url, transitionType} = obj;
        const urlobj = new URL(url);
        const host = urlobj.host;

        if(urlobj.protocol.match(/^https?/) == null){
            return;
        }

        if (transitionType === "auto_subframe") {
            return;
        }

        const ms = [
            sendMessage({
                type: "INJECT_STYLE_FOR_TAB",
                tabId,
                host
            }),
            sendMessage({
                type: "UPDATE_STYLE",
                host
            })
        ];

        if(Math.random() < PROMPT_RATE){
            const windowId = (await getTab(tabId)).windowId;
            await sendMessage({
                type: "FEEDBACK_PROMPT",
                windowId,
                host
            });
        }

        await Promise.all(ms);
        await sendMessage({
            type: "INJECT_STYLE_FOR_TAB",
            tabId,
            host
        });

    }
    catch(e) {
        console.error(e);
        /* Gotta catch em aa-aaall! */
    }
});

