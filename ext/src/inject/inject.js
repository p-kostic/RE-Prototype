
const URL = 'https://178.128.252.36/api/';

chrome.runtime.sendMessage({ type: "INJECT_STYLE" });

fetch(URL, {mode: "cors", method: "GET"})
    .then(response => response.text())
    .then(css => {
        chrome.runtime.sendMessage({
            type: 'UPDATE_STYLE', 
            content: css
        }, css => {
            chrome.runtime.sendMessage({type: 'INJECT_STYLE'});
        });
    });



