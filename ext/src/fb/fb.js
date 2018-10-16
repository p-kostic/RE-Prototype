
chrome.storage.local.get('variant', ({variant}) => {
   document.body.classList.add(variant);
});

const dismissBtn = document.querySelector('#dismiss');

dismissBtn.addEventListener('click', e => {
    e.preventDefault();
    chrome.runtime.sendMessage({type: "PROMPT_DISMISS"});
});


document.addEventListener('change', e => {
    const name = e.target.name;
    const text = e.target.getAttribute('data-text');
    document.querySelector(`[data-form-field=${name}]`).innerText = text;
});

document.addEventListener('submit', e => {
    chrome.runtime.sendMessage({type: 'PROMPT_FORM_SUBMITTED'});
});
