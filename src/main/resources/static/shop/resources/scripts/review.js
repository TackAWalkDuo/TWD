const form = window.document.getElementById('form');

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

// 등록하기 버튼
form.querySelector('[rel="registerButton"]').addEventListener('click', () => {
    const checkReviewGood = document.querySelector('[rel="reviewGood"]');
    const checkReviewCommon = document.querySelector('[rel="reviewCommon"]');
    const checkReviewBad = document.querySelector('[rel="reviewBad"]');

    if (checkReviewGood.checked === false && checkReviewCommon.checked === false &&
        checkReviewBad.checked === false) {
        alert('리뷰 평가를 체크해 주세요.');
        return;
    }

    if (form['content'].value === '') {
        alert('상세 리뷰를 작성해 주세요.');
        form['content'].focus();
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    for (let file of form['images'].files) {
        formData.append('images', file);
    }

    formData.append('review', form['review'].value);
    formData.append('content', form['content'].value);
    formData.append('commentTitle', form['review'].value);

    xhr.open('POST', window.location.href);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        alert('리뷰 등록이 완료되었습니다.');
                        window.location.href = `/shop/detail?aid=${responseObject['aid']}`;
                        break;
                    default:
                showDialog.show('알 수 없는 이유로 리뷰를 등록하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            } else {
                showDialog.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
});

