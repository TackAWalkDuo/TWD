let editor;
ClassicEditor
    .create(form['content'])
    .then( e => editor = e );

const form = window.document.getElementById('form');

form.onsubmit = e => {
    e.preventDefault();
    Warning.hide();

    if (form['title'].value === '') {
        alert('제목을 입력해주세요.');
        form['title'].focus();
        return false;
    }
    if (form['content'].value === '') {
        alert('내용을 입력해주세요.');
        editor.focus();
        return false;
    }
    // Cover.show('게시글을 작성하는 중입니다.')
    const xhr = new XMLHttpRequest();
    const formData = new FormData;
    formData.append('bid', form['bid'].value);
    formData.append('title', form['title'].value);
    formData.append('content', editor.getData());
    // formData.append('boardId',form['id'].value);
    xhr.open('POST', './write');
    // './write?bid=' + form['id'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'not_allowed':
                        Warning.show('게시글을 작성할 수 있는 권한이 없거나 로그아웃 되었습니다. 확인 후 다시 시도해 주세요.');
                        break;
                    case 'success':
                        // window.location.href = '/read?aid=' + responseObject['aid'];
                        const aid = responseObject['aid'];
                        window.location.href = `read?aid=${aid}`;
                        break;
                    default:
                        Warning.show('알수 없는 이유로 게시글을 작성하지 못하였습니다. 잠시 후 다시 시도해 주세요.')
                }
            } else {
                Warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
};
