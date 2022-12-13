const form = window.document.getElementById('form');
let editor;
ClassicEditor
    .create(form['content'])
    .then( e => editor = e );

form['back'].addEventListener('click',() => window.history.length < 2 ? window.close() : window.history.back());

