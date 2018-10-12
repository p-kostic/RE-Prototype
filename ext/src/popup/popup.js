const button = document.getElementById('toggleStyle')

chrome.storage.local.get('enabled', ({enabled}) => button.innerText = enabled ? "Disable" : "Enable");

button.addEventListener('click', () => {
    chrome.runtime.sendMessage({type: 'TOGGLE_STYLE'}, enabled => {
        button.innerText = enabled ? "Disable": "Enable";
    });
});
