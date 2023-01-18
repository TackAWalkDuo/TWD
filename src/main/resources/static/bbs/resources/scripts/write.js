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

form['back'].addEventListener('click', () => {
    window.history.back();
});

form.onsubmit = e => {
    e.preventDefault();

    if (form['title'].value === '') {
        alert('제목을 입력해주세요!!!');
        form['title'].focus();
        return false;
    }
    if (form['content'].value === '') {
        alert('내용을 입력해주세요.');
        editor.focus();
        return false;
    }
    if (form['criterion'].value === 'menu'){
        alert('목록을 선택해주세요');
        return false;
    }


    // Cover.show('게시글을 작성하는 중입니다.')
    const xhr = new XMLHttpRequest();
    const formData = new FormData;
    for (let file of form['images'].files) {
        formData.append('images', file);
    }
    formData.append('boardId', form['criterion'].value);
    formData.append('title', form['title'].value);
    formData.append('content', editor.getData());
    xhr.open('POST', './write');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            // Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'not_allowed':
                        showDialog.show('게시글을 작성할 수 있는 권한이 없거나 로그아웃 되었습니다. 확인 후 다시 시도해 주세요.');
                        break;
                    case 'success':
                        // window.location.href = '/read?aid=' + responseObject['aid'];
                        const aid = responseObject['aid'];
                        window.location.href = `read?aid=${aid}`;
                        break;
                    default:
                        showDialog.show('알수 없는 이유로 게시글을 작성하지 못하였습니다. 잠시 후 다시 시도해 주세요.')
                }
            } else {
                console.log('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
};

const showDialog = {
    getElement: () => window.document.querySelector('[rel="dialog"]'),
    show: (text) => {
        const dialog = showDialog.getElement();
        dialog.querySelector('.text').innerText = text;
        dialog.classList.add('visible');
        // dialog.querySelector("#cancel").addEventListener("click", () => {
        //     dialog.classList.remove('visible');
        // });
        dialog.querySelector("#ok").addEventListener("click", () => {
            dialog.classList.remove('visible');
        });
    },
    notLogin: () =>{
        const dialog = showDialog.getElement();
        dialog.querySelector('.text').innerText = "권한이 없습니다.";
        dialog.classList.add('visible');
        dialog.querySelector("#ok").addEventListener("click", () => {
            dialog.classList.remove('visible');
            window.location.href = `/member/login`;
        });
    }
};