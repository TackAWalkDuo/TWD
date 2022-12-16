const form = window.document.getElementById('form');
let imageScale = window.document.getElementById('imageScale');

form.querySelector('[rel="thumbnailImage"]').addEventListener('click', e => {
    e.preventDefault();
    imageScale.classList.add('visible');
    // imageScale.classList.contains('visible') ?
    //     imageScale.classList.remove('visible')
    //     : imageScale.classList.add('visible');

});

window.document.getElementById('imageScale').addEventListener('click', e => {
    e.preventDefault();
    imageScale.classList.remove('visible');
    // imageScale.classList.contains('visible') ?
    //     imageScale.classList.remove('visible')
    //     : imageScale.classList.add('visible');
});