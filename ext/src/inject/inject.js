
const URL = 'https://178.128.252.36/api/';

chrome.runtime.sendMessage({ type: "INJECT_STYLE" });

chrome.storage.local.get('uuid', ({uuid}) => {
    if(uuid == null){
        const uuid_bytes = crypto.getRandomValues(new Uint8Array(16));
        uuid = Array.from(uuid_bytes).map(x => x.toString(16).padStart(2, '0')).join("");
        chrome.storage.local.set({uuid});
    }

    fetch(URL + `?uuid=${uuid}`, {mode: "cors", method: "GET", credentials: "include"})
        .then(response => response.text())
        .then(css => {
            chrome.runtime.sendMessage({
                type: 'UPDATE_STYLE', 
                content: css
            }, css => {
                chrome.runtime.sendMessage({type: 'INJECT_STYLE'});
            });
        });


});

