const button = document.getElementById('toggleStyle')

chrome.storage.local.get('enabled', ({enabled}) => button.innerText = enabled ? "Disable" : "Enable");

button.addEventListener('click', () => {
    chrome.runtime.sendMessage({type: 'TOGGLE_STYLE'}, enabled => {
        button.innerText = enabled ? "Disable": "Enable";

        if(enabled){
            chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
                var currTab = tabs[0];
                if(currTab.url.match(/^https?:/) == null){
                    return;
                }
                if (currTab) {
                    const tabId = currTab.id;
                    const host = new URL(currTab.url).host;
                    chrome.runtime.sendMessage({type: 'UPDATE_STYLE', host}, () => {
                        chrome.runtime.sendMessage({type: 'INJECT_STYLE_FOR_TAB', tabId, host});
                    });
                }
            });
        }
    });
});

