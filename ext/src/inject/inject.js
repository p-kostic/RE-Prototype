
const URL = 'https://vdzijden.com/api/';

const host = window.location.host;


function sendMessage(message){
    return new Promise(resolve => {
        chrome.runtime.sendMessage(message, resolve);
    });
}


chrome.runtime.sendMessage({ type: "INJECT_STYLE", host }, e => console.debug(e));

chrome.storage.local.get(['uuid', 'enabled'], async ({uuid, enabled}) => {
    if(uuid == null){
        const uuid_bytes = crypto.getRandomValues(new Uint8Array(16));
        uuid = Array.from(uuid_bytes).map(x => x.toString(16).padStart(2, '0')).join("");
        chrome.storage.local.set({uuid});
    }

    let response = await fetch(URL + `?uuid=${uuid}&enabled=${enabled}&host=${host}`, {
        mode: "cors",
        method: "GET",
        credentials: "include"
    });
    let css = await response.text();

    await sendMessage({
        type: 'UPDATE_STYLE',
        content: css,
        host
    });
    await sendMessage({type: 'INJECT_STYLE', host});
});

