
const URL = 'https://vdzijden.com/api/';

chrome.runtime.sendMessage({ type: "INJECT_STYLE" });

chrome.storage.local.get(['uuid', 'enabled'], ({uuid, enabled}) => {
    if(uuid == null){
        const uuid_bytes = crypto.getRandomValues(new Uint8Array(16));
        uuid = Array.from(uuid_bytes).map(x => x.toString(16).padStart(2, '0')).join("");
        chrome.storage.local.set({uuid});
    }

    fetch(URL + `?uuid=${uuid}&enabled=${enabled}&host=${window.location.host}`, {mode: "cors", method: "GET", credentials: "include"})
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

