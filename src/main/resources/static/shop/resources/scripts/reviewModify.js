const form = window.document.getElementById('form');
const basicImageIndex = window.document.querySelectorAll('[rel="basicImageIndex"]');
const imageContainerElement = form.querySelector('[rel="contentImageContainer"]');


let initialization = true;
let imageModifyFlag = false;

if (initialization) {

    form['basicTitle'].value === 'good'
        ? form.querySelector('[rel="reviewGood"]').checked = true
        : (form['basicTitle'].value === 'common'
            ? form.querySelector('[rel="reviewCommon"]').checked = true
            : form.querySelector('[rel="reviewBad"]').checked = true);

    if (basicImageIndex.length !== 0) {
        form.querySelector('[rel="imageSelectButton"]').setAttribute('hidden', 'hidden');
        form.querySelector('[rel="noImage"]').setAttribute('hidden', 'hidden');

        basicImageIndex.forEach(x => {
            const imageElement = document.createElement('img');
            imageElement.setAttribute('alt', '');
            imageElement.setAttribute('src', `/bbs/commentImage?index=${x.value}`);
            imageElement.classList.add('image');
            imageContainerElement.append(imageElement);
        })
    }
    initialization = false;
}


// 취소하기 버튼
form.querySelector('[rel="cancleButton"]').addEventListener('click', () => {
    if (window.history.length > 1) {
        window.history.back();
    }
});

// 사진 첨부하기 눌렀을 때
form.querySelector('[rel="contentImageContainer"]').addEventListener('click', e => {
    e.preventDefault();
    form['images'].click();
    imageModifyFlag = true;
});

form.querySelector('[rel="contentImageContainer"]').addEventListener('blur', e => {
    imageContainerElement.querySelectorAll('img.image').forEach(x => x.remove());
    imageModifyFlag = true;
});

form['images'].addEventListener('input', () => {
    imageContainerElement.querySelectorAll('img.image').forEach(x => x.remove());
    if (form['images'].files.length > 0) {
        form.querySelector('[rel="imageSelectButton"]').setAttribute('hidden', 'hidden');
        form.querySelector('[rel="noImage"]').setAttribute('hidden', 'hidden');
    } else {
        form.querySelector('[rel="noImage"]').classList.remove('hidden')
    }
    for (let file of form['images'].files) {
        const imageSrc = URL.createObjectURL(file);
        const imgElement = document.createElement('img');
        imgElement.classList.add('image');
        imgElement.setAttribute('src', imageSrc);
        imageContainerElement.append(imgElement);
        imageModifyFlag = true;
    }
});

// 등록하기 버튼
form.querySelector('[rel="registerButton"]').addEventListener('click', () => {
    const checkReviewGood = document.querySelector('[rel="reviewGood"]');
    const checkReviewCommon = document.querySelector('[rel="reviewCommon"]');
    const checkReviewBad = document.querySelector('[rel="reviewBad"]');

    if (checkReviewGood.checked === false && checkReviewCommon.checked === false &&
        checkReviewBad.checked === false) {
        showDialog.show("리뷰 평가를 체크해 주세요.");
        return;
    }

    if (form['content'].value === '') {
        showDialog.show("상세 리뷰를 작성해 주세요.");

        form['content'].focus();
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    for (let file of form['images'].files) {
        formData.append('images', file);
    }

    formData.append('content', form['content'].value);
    formData.append('commentTitle', form['review'].value);
    formData.append('modifyFlag', imageModifyFlag);
    formData.append('userEmail', form['commentEmail'].value);

    const url = new URL(window.location.href);
    const searchParams = url.searchParams;
    const index = searchParams.get('index');
    formData.append('index', index);


    xhr.open('POST', '/bbs/comment-modify');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        alert('리뷰 수정이 완료되었습니다.');
                        window.location.href = `/shop/detail?aid=${form['aid'].value}`;
                        break;
                    default:
                        showDialog.show("알 수 없는 이유로 리뷰 수정에 실패하였습니다. 잠시 후 다시 시도해 주세요.");
                }
            } else {
                showDialog.show("서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.");
            }
        }
    };
    xhr.send(formData);
});

