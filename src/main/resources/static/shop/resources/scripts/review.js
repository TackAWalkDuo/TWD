const form = window.document.getElementById('form');
const Warning = {
    // 묶어준다.
    show: (text) => {
        form.querySelector('[rel="warningText"]').innerText = text;
        form.querySelector('[rel="warning"]').classList.add('visible');
    },
    hide: () => {
        form.querySelector('[rel="warning"]').classList.remove('visible');
    }
}

// 취소하기 버튼
form.querySelector('[rel="cancleButton"]').addEventListener('click', () => {
    if (window.history.length > 1) {
        window.history.back();
    }
});

// 등록하기 버튼
form.querySelector('[rel="registerButton"]').addEventListener('click', () => {

    const checkReviewGood = document.querySelector('[rel="reviewGood"]');
    const checkReviewCommon = document.querySelector('[rel="reviewCommon"]');
    const checkReviewBad = document.querySelector('[rel="reviewBad"]');

    if (checkReviewGood.checked === false && checkReviewCommon.checked === false &&
        checkReviewBad.checked === false) {
        Warning.show('리뷰 평가를 체크해 주세요.')
        return;
    }

    if (form['content'].value === '') {
        Warning.show('상세리뷰를 입력해주세요.')
        form['content'].focus();
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('review', form['review'].value);
    formData.append('content', form['content'].value);
    formData.append('photo', form['photo'].value);

    xhr.open('POST', './review');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        Warning.show('리뷰 등록이 완료되었습니다.')
                        break;
                    default:
                        Warning.show('알 수 없는 이유로 회원가입에 실패하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            } else {
                Warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
});
