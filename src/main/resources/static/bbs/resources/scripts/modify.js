const form = window.document.getElementById('form');

let editor;
ClassicEditor
    .create(form['content'], {
        simpleUpload : {
            uploadUrl : './image'
        }
    })
    .then(e => editor = e);

form.querySelector('[rel="contentImageContainer"]').addEventListener('click', e => {
    e.preventDefault();
    form['images'].click();
});

form['images'].addEventListener('input', () => {
    const imageContainerElement = form.querySelector('[rel="contentImageContainer"]');
    imageContainerElement.querySelectorAll('img.image').forEach(x => x.remove());
    if (form['images'].files.length > 0) {
        form.querySelector('[rel="imageSelectButton"]').setAttribute('hidden','hidden');
        form.querySelector('[rel="noImage"]').setAttribute('hidden','hidden');
    } else {
        form.querySelector('[rel="noImage"]').classList.remove('hidden')
    }
    for (let file of form['images'].files) {
        const imageSrc = URL.createObjectURL(file);
        const imgElement = document.createElement('img');
        imgElement.classList.add('image');
        imgElement.setAttribute('src', imageSrc);
        imageContainerElement.append(imgElement);
    }
});

form.onsubmit = e => {
    e.preventDefault();

    if (form['title'].value === '') {
        alert('제목을 입력해주세요.');
        form['title'].focus();
        return false;
    }
    if (form['content'].value === '') {
        alert('내용(수정)을 입력해주세요.');
        editor.focus();
        return false;
    }


    // Cover.show('게시글을 작성하는 중입니다.')
    const xhr = new XMLHttpRequest();
    const formData = new FormData;
    for (let file of form['images'].files) {
        formData.append('images', file);
    }
    formData.append('boardId', form['bid'].value);
    formData.append('title', form['title'].value);
    formData.append('content', editor.getData());
    formData.append('aid', form['aid'].value);
    xhr.open('PATCH', "./modify");
    // './write?bid=' + form['id'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'no_such_article':
                        console.log('게시글을 수정할 수 없습니다. 게시글이 존재하지 않습니다.');
                        break;
                    case 'not_allowed':
                        console.log('게시글을 수정할 수 있는 권한이 없거나 로그아웃 되었습니다. 확인 후 다시 시도해 주세요.');
                        break;
                    case 'success':
                        // window.location.href = '/read?aid=' + responseObject['aid'];
                        confirm('수정하시겠습니까?');
                        const aid = responseObject['aid'];
                        window.location.href = `read?aid=${aid}`;
                        break;
                    default:
                        console.log('알수 없는 이유로 게시글을 작성하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            } else {
                console.log('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
};

