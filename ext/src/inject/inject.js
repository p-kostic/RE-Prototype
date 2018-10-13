const host = window.location.host;

function sendMessage(message){
    return new Promise(resolve => {
        chrome.runtime.sendMessage(message, resolve);
    });
}


(async () => {
    const inj = sendMessage({type: "INJECT_STYLE", host});
    const upd = sendMessage({
        type: 'UPDATE_STYLE',
        host
    });

    await Promise.all([inj, upd]);
    await sendMessage({type: 'INJECT_STYLE', host});
})();

