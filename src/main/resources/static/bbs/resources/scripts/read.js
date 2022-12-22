const form = window.document.getElementById('form');
let imageScale = window.document.getElementById('imageScale');
const freeSelect = window.document.querySelector("[rel='freeSelect']");

const showDialog = {
    getElement: () => form.querySelector('[rel="dialog"]'),
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
        console.log(text);
    }
};

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

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('articleIndex', form['aid'].value);
    xhr.open('POST', './article-liked');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        console.log('하트성공');
                        if (likeA.classList.contains('visible')) {
                            likeA.classList.remove('visible');
                            form.querySelector("[rel='likeCount']").innerText = Number(form.querySelector("[rel='likeCount']").innerText) - 1;
                        } else if (!likeA.classList.contains('visible')) {
                            likeA.classList.add('visible');
                            form.querySelector("[rel='likeCount']").innerText = Number(form.querySelector("[rel='likeCount']").innerText) + 1;

                            // form.querySelector("[rel='likeCount']").innerText = "fuckyou";
                        }
                        break;
                    case 'not_allowed':
                        showDialog.show("로그인이 되어있지 않습니다.");
                        break;
                    case 'no_such_board':
                        showDialog.show("게시글을 찾을수 없습니다.");
                        break;
                    default:
                        showDialog.show("알수없는 이유로 실패하였습니다.");
                }
            } else {
                console.log('좋아요 실행을 실패했습니다(서버)');
            }
        }
    };
    xhr.send(formData);
});

const deleteArticle = window.document.getElementById('deleteArticle');
deleteArticle.addEventListener('click',e => {
    e.preventDefault();
    if(!confirm('정말로 게시글을 삭제할까요')){
        return;
    }
    const xhr = new XMLHttpRequest();
    // const formData = new FormData();
    xhr.open('DELETE',window.location.href);
    xhr.onreadystatechange = () =>{
        if(xhr.readyState === XMLHttpRequest.DONE){
            if(xhr.status >= 200 && xhr.status<300){
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        const bid = responseObject['bid'];
                        window.location.href = `./list?bid=${bid}`;
                        break;
                    case 'not_allowed':
                        showDialog.show("로그인 아이디가 일치하지 않습니다.");
                        break;
                    case 'no_such_article':
                        showDialog.show("게시글을 찾을 수 없습니다.");
                        break;
                    default:
                        showDialog.show("알수없는 삭제하지 못하였습니다.\n 잠시후에 다시시도해 주세요.");
                }
            } else {
                console.log('좋아요 실행을 실패했습니다(서버)');
            }
        }
    };
    xhr.send();
});
