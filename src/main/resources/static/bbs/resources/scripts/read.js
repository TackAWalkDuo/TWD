const form = window.document.getElementById('form');
let imageScale = window.document.getElementById('imageScale');
const commentContainer = window.document.getElementById('commentContainer');
const commentMinePicForm = window.document.getElementById('commentMinePicForm');
const likeA = window.document.getElementById('likeA');


const loadComments = () => {
    commentContainer.innerText = '';
    const url = new URL(window.location.href);
    const searchParams = url.searchParams;
    const aid = searchParams.get('aid');
    const xhr = new XMLHttpRequest();
    xhr.open('GET', `./comment?index=${aid}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseArray = JSON.parse(xhr.responseText);
                const appendComment = (commentObject, isSub) => {
                    const commentHtmlText = `<div class="comment ${isSub ? 'sub' : ''}" rel="comment">
                                    <div class="comment head">
                                        <div class="speciesPicContainer">
                                            <img class="speciesPic" src="/resources/images/icons8-jake-150.png" alt="#">
                                        </div>
                                        <div class="head">
                                            <span class="writer">${commentObject['nickname']}</span>
                                            <span class="dt">${commentObject['writtenOn']}</span>
                                            <span class="action-container">
                                                 ${commentObject['signed'] === true ? '<a href="#" class="action reply"  rel="actionReply">답글달기</a>' : ''}
                                                ${commentObject['mine'] === true ? '<a href="#" class="action modify" rel="actionModify">수정</a>' : ''}
                                                ${commentObject['mine'] === true ? '<a href="#" class="action delete" rel="actionDelete">삭제</a>' : ''}
                                                ${commentObject['mine'] === true ? '<a href="#" class="action cancel" rel="actionCancel">취소</a>' : ''}
                                            </span>
                                        </div>
                                    </div>
                                    <div class="body">
                                        <div class="content">
                                            <span class="text">${commentObject['content']}</span>
                                            <div class="like ${commentObject['liked'] === true ? 'visible' : ''}" rel="likeComment">
                                                <a href="#" class="toggle" ${commentObject['signed'] === true ? '' : 'prohibited'} rel="likeToggle"><i class="fa-solid fa-heart"></i></a><span
                                                    class="count" rel='likeCommentCount'>${commentObject['likeCommentCount']}</span>
                                            </div>
                                        </div>
                                        <form class="modify-form" rel="modifyForm">
                                            <label class="label">
                                                <span hidden>댓글수정</span>
                                                <textarea class="--object-input" maxlength="300" name="content"
                                                          placeholder="댓글을 입력해 주세요"></textarea>
                                            </label>
                                            <input class="--object-button" type="submit" value="수정">
                                        </form>
                                    </div>
                                </div>
                                <form class="reply-form" rel="replyForm">
                                    <div id="replyMinePicForm" class="reply-mine-pic-form">
                                        <div class="speciesPicContainer">
                                            <img class="speciesPic" src="/resources/images/icons8-jake-150.png" alt="#">
                                        </div>
                                        <div id="replyMineForm" class="reply-mine-form">
                                            <div class="head">
                                                <span class="writer">관리자</span>
                                                <span class="action-container">
                                                <a href="#" class="action cancel" rel="replyActionCancel">취소</a>
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
                    const modifyFormElement = dom.querySelector('[rel="modifyForm"]');
                    const replyFormElement = dom.querySelector('[rel="replyForm"]');
                    const likeToggleElement = dom.querySelector('[rel="likeToggle"]');
                    const likedCommentElement = dom.querySelector('[rel="likeComment"]')

                    dom.querySelector('[rel="actionReply"]')?.addEventListener('click', e => {
                        e.preventDefault();
                        replyFormElement.classList.add('visible');
                        replyFormElement['content'].focus();
                    });
                    dom.querySelector('[rel="actionModify"]')?.addEventListener('click', e => {
                        e.preventDefault();
                        modifyFormElement.classList.add('visible');
                        modifyFormElement['content'].focus();
                    });
                    dom.querySelector('[rel="actionCancel"]')?.addEventListener('click', e => {
                        e.preventDefault();

                        modifyFormElement.classList.remove('visible');
                    });

                    dom.querySelector('[rel="replyActionCancel"]')?.addEventListener('click', e => {
                        e.preventDefault();

                        replyFormElement.classList.remove('visible');
                    });


                    //대댓글 작성
                    replyFormElement.onsubmit = e => {
                        e.preventDefault();
                        if (replyFormElement['content'].value === '') {
                            replyFormElement['content'].focus();
                            return false;
                        }
                        const xhr = new XMLHttpRequest();
                        const formData = new FormData();
                        formData.append('articleIndex', commentMinePicForm['aid'].value);
                        formData.append('commentIndex', commentObject['index']);
                        formData.append('content', replyFormElement['content'].value);
                        xhr.open('POST', `./comment`);
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === XMLHttpRequest.DONE) {
                                if (xhr.status >= 200 && xhr.status < 300) {
                                    const responseObject = JSON.parse(xhr.responseText);
                                    switch (responseObject['result']) {
                                        case 'not_signed':
                                            showDialog.show('로그인이 되어있지 않습니다.');
                                            break;
                                        case 'success':
                                            alert('성공');
                                            loadComments();
                                            break;
                                        default:
                                            showDialog.show('작성에 실패하엿습니다.');
                                    }
                                } else {

                                }
                            }
                        };
                        xhr.send(formData);
                    };

                    //comment 삭제
                    dom.querySelector('[rel="actionDelete"]')?.addEventListener('click', e => {
                        e.preventDefault();
                        if (!confirm('정말로 댓글을 삭제할까요?')) {
                            return;
                        }
                        const xhr = new XMLHttpRequest();
                        const formData = new FormData();
                        formData.append('index', commentObject['index']);
                        formData.append('userEmail', commentObject['userEmail']);
                        xhr.open('DELETE', './comment');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === XMLHttpRequest.DONE) {
                                if (xhr.status >= 200 && xhr.status < 300) {
                                    const responseObject = JSON.parse(xhr.responseText);
                                    switch (responseObject['result']) {
                                        case 'success':
                                            loadComments();
                                            break;
                                        case 'not_signed':
                                            showDialog.show('로그인이 되어있지 않습니다.');
                                            break;
                                        case 'not_same':
                                            showDialog.show('자신의 댓글이 아닙니다.')
                                            break;
                                        default:
                                            showDialog.show('알수없는 이유로 실패하였습니다');
                                    }
                                } else {
                                    showDialog.show('알수없는 이유로 실패하였습니다');
                                }
                            }
                        };
                        xhr.send(formData);
                    });

                    //comment 수정
                    modifyFormElement.onsubmit = e => {
                        e.preventDefault();
                        if (modifyFormElement['content'].value === '') {
                            modifyFormElement['content'].focus();
                            return false;
                        }
                        const xhr = new XMLHttpRequest();
                        const formData = new FormData();
                        formData.append('index', commentObject['index']);
                        formData.append('content', modifyFormElement['content'].value);
                        formData.append('userEmail', commentObject['userEmail']);
                        xhr.open('POST', './comment-modify');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === XMLHttpRequest.DONE) {
                                if (xhr.status >= 200 && xhr.status < 300) {
                                    const responseObject = JSON.parse(xhr.responseText);
                                    switch (responseObject['result']) {
                                        case 'success' :
                                            loadComments();
                                            break;
                                        case 'no_such_comment' :
                                            showDialog.show("게시글을 찾을 수 없습니다.");
                                            break;
                                        case 'not_signed' :
                                            showDialog.show("로그인 정보가 일치하지 않습니다.");
                                            break;
                                        case 'not_same' :
                                            showDialog.show("작성자가 아닙니다.");
                                            break;
                                        default:
                                            showDialog.show("수정에 실패했습니다.");
                                    }
                                } else {
                                    showDialog.show('알수없는 이유로 실패하였습니다');
                                }
                            }
                        };
                        xhr.send(formData);
                    };

                    likeToggleElement.addEventListener('click', e => {
                            e.preventDefault();
                            const xhr = new XMLHttpRequest();
                            const formData = new FormData();
                            formData.append('commentIndex', commentObject['index']);
                            xhr.open('POST', './comment-liked');
                            xhr.onreadystatechange = () => {
                                if (xhr.readyState === XMLHttpRequest.DONE) {
                                    if (xhr.status >= 200 && xhr.status < 300) {
                                        const responseObject = JSON.parse(xhr.responseText);
                                        switch (responseObject['result']) {
                                            case 'success':
                                                if (commentObject['liked'] === true) {
                                                    likedCommentElement.classList.remove('visible');
                                                } else if (!commentObject['liked'] === true) {
                                                    likedCommentElement.classList.add('visible');
                                                }
                                                loadComments();
                                                break;
                                            case 'not_allowed':
                                                showDialog.notLogin();
                                                break;
                                            default:
                                                alert('하트 실패');
                                        }
                                    } else {
                                        // console.log('좋아요 실행을 실패했습니다(서버)');
                                    }
                                }
                            }
                            ;
                            xhr.send(formData);
                        }
                    );


                    commentContainer.append(commentElement, replyFormElement);
                }

                const appendReplyOf = parentComment => {
                    const replyArray = responseArray.filter(x => x['commentIndex'] === parentComment['index']);

                    for (let replyObject of replyArray) {
                        appendComment(replyObject, true);
                        appendReplyOf(replyObject);
                        // 구문이 끝나기전 다시한번 요청 대댓글이없다면 []값 이라서 구문 재기정지.
                    }
                };
                for (let commentObject of responseArray.filter(x => !x['commentIndex'])) {
                    appendComment(commentObject, false);
                    appendReplyOf(commentObject);

                }

            }
        }
    }
    ;
    xhr.send();
};

loadComments();


// const autoResizeTextarea = (e) => {
//     let textarea = document.querySelector('.--object-input');
//
//     if (textarea) {
//         textarea.style.height = 'auto';
//         let height = textarea.scrollHeight; // 높이
//         textarea.style.height = `${height + 8}px`;
//     }
// };

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
                        case 'not_signed':
                            showDialog.notLogin();
                            // window.location.href = `/member/login`;
                            break;
                        case 'success':
                            alert('성공');
                            loadComments();
                            break;
                        default:
                            showDialog.show('알수없는 이유로 댓글을 작성하지 못하였습니다.\n\n 잠시후에 다시시도해 주세요.');
                    }
                } else {
                    showDialog.show('서버와 통신하지 못하였습니다.');
                }
            }
        };
        xhr.send(formData);
    }
}

//Dialog 구현
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


likeA.addEventListener('click', e => {
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('articleIndex', commentMinePicForm['aid'].value);
    xhr.open('POST', '/bbs/article-liked');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        if (likeA.classList.contains('visible')) {
                            likeA.classList.remove('visible');
                            form.querySelector("[rel='likeCount']").innerText = Number(form.querySelector("[rel='likeCount']").innerText) - 1;
                        } else if (!likeA.classList.contains('visible')) {
                            likeA.classList.add('visible');
                            form.querySelector("[rel='likeCount']").innerText = Number(form.querySelector("[rel='likeCount']").innerText) + 1;
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
                // console.log('좋아요 실행을 실패했습니다(서버)');
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
                // console.log('좋아요 실행을 실패했습니다(서버)');
                console.log('좋아요 실행을 실패했습니다(서버)');
            }
        }
    };
    xhr.send();
});

function resize(obj) {
    obj.style.height = "1px";
    obj.style.height = (12 + obj.scrollHeight) + "px";
}

window.document.getElementById("modifyArticle").addEventListener('click', () => {
    const writeUser = window.document.getElementById("writeUser").value;
    const loginUser = window.document.getElementById("loginUser")?.value;

    writeUser === loginUser
        ? window.location.href = `./modify?aid=${commentMinePicForm['aid'].value}`
        : showDialog.show("수정 권한이 없습니다.");
});
