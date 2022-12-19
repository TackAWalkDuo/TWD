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

const likeA = window.document.getElementById('likeA');
likeA.addEventListener('click', e => {
    e.preventDefault();
    if(likeA.classList.contains('liked')) {
        likeA.classList.remove('visible');
    }else{
        likeA.classList.add('visible');
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('articleIndex',form['aid'].value);
    xhr.open('POST', './article-liked');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        console.log('하트성공');
                        break;
                    case 'NOT_ALLOWED':
                        alert('로그인하고 눌러주세요');
                        break;
                    case 'NO_SUCH_BOARD':
                        alert('게시글을 찾을수 없습니다.');
                        break;
                    default:
                        alert('하트 실패');
                }
            } else {
                console.log('좋아요 실행을 실패했습니다(서버)');
            }
        }
    };
    xhr.send(formData);
});
