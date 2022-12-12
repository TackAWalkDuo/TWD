let editor;
ClassicEditor
    .create(form['content'])
    .then( e => editor = e );