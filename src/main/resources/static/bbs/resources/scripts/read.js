const form = window.document.getElementById('form');
let imageScale = window.document.getElementById('imageScale');
const commentContainer = window.document.getElementById('commentContainer');
const commentMinePicForm = window.document.getElementById('commentMinePicForm');

const loadComments = () => {
    commentContainer.innerText = '';
    const url = new URL(window.location.href);
    const searchParams = url.searchParams;
    const aid = searchParams.get('aid');
    const xhr = new XMLHttpRequest();
    // formData.append('aid',searchParams.get('aid'));
    //http://localhost:8080/bbs/read?aid=10
    console.log(aid);
    xhr.open('GET', `./comment?index=${aid}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseArray = JSON.parse(xhr.responseText);
                for (const reviewObject of responseArray) {
                    // console.log(responseArray.length);  //responseArray.length =댓글의 갯수
                    const appendComment = (commentObject) => {
                        const commentHtmlText = `<div class="comment liked mine" rel="comment">
                                    <div class="comment head">
                                        <div class="speciesPicContainer">
                                            <img class="speciesPic" src="/resources/images/icons8-jake-150.png" alt="#">
                                        </div>
                                        <div class="head">
                                            <span class="writer">관리자</span>
                                            <span class="dt">2022-01-01 00:00:00</span>
                                            <span class="action-container">
                                                <a href="#" class="action reply">답글달기</a>
                                                <a href="#" class="action modify">수정</a>
                                                <a href="#" class="action delete">삭제</a>
                                                <a href="#" class="action cancel">취소</a>
                                            </span>
                                        </div>
                                    </div>
                                    <div class="body">
                                        <div class="content">
                                            <span class="text">나만 몰랐던건가...?ㅎ</span>
                                            <div class="like">
                                                <a href="#" class="toggle"><i class="fa-solid fa-heart"></i></a><span
                                                    class="count"> 9,999</span>
                                            </div>
                                        </div>
                                        <form class="modify-form">
                                            <label class="label">
                                                <span hidden>댓글수정</span>
                                                <textarea class="--object-input" maxlength="300" name="content"
                                                          placeholder="댓글을 입력해 주세요"></textarea>
                                            </label>
                                            <input class="--object-button" type="submit" value="수정">
                                        </form>
                                    </div>
                                </div>
                                <form class="reply-form" id="replyForm">
                                    <div id="replyMinePicForm" class="reply-mine-pic-form">
                                        <div class="speciesPicContainer">
                                            <img class="speciesPic" src="/resources/images/icons8-jake-150.png" alt="#">
                                        </div>
                                        <div id="replyMineForm" class="reply-mine-form">
                                            <div class="head">
                                                <span class="writer">관리자</span>
                                                <span class="action-container">
                                                <a href="#" class="action cancel">취소</a>
                                                </span>
                                            </div>
                                            <div class="body">
                                                <label class="label">
                                                    <span hidden>답글작성</span>
                                                    <textarea class="--object-input" maxlength="300" name="content"
                                                              placeholder="답글을 입력해 주세요"></textarea>
                                                </label>
                                                <input class="--object-button" type="submit" value="작성">
                                            </div>
                                        </div>
                                    </div>
                                </form>
                                `
                        const domParser = new DOMParser();
                        const dom = domParser.parseFromString(commentHtmlText, 'text/html');
                        const commentElement = dom.querySelector('[rel="comment"]');

                        commentContainer.append(commentElement);
                        for (let commentObject of responseArray.filter(x => !x['commentIndex'])) {
                            appendComment(commentObject);
                        }
                    }
                }
            }
        }

    };
    xhr.send();
};

loadComments();

if (commentMinePicForm !== null) {
    commentMinePicForm.onsubmit = e => {
        e.preventDefault()
        if (commentMinePicForm['content'] === '') {
            alert('댓글을 입력해주세요')
            commentMinePicForm['content'].focus();
            return false;
        }
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('articleIndex', commentMinePicForm['aid'].value);
        formData.append('content', commentMinePicForm['content'].value);
        xhr.open('POST', './comment');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success':
                            alert('성공');
                            loadComments();
                            break;
                        default:
                            showDialog.show('알수없는 이유로 댓글을 작성하지 못하였습니다.\n\n 잠시후에 다시시도해 주세요.')
                    }
                } else {
                    showDialog.show('서버와 통신하지 못하였습니다.')
                }
            }
        };
        xhr.send(formData);
    }
}



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

    console.log(form['aid'].value);
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('articleIndex', form['aid'].value);
    xhr.open('POST', '/bbs/article-liked');
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
deleteArticle.addEventListener('click', e => {
    e.preventDefault();
    if (!confirm('정말로 게시글을 삭제할까요')) {
        return;
    }
    const xhr = new XMLHttpRequest();
    // const formData = new FormData();
    xhr.open('DELETE', window.location.href);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
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
