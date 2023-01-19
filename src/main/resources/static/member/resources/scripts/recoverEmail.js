const form = window.document.getElementById('form');

const Warning = {
    getElement: () => form.querySelector('[rel="warningRow"]'),
    show: (text) => {
        const warningRow = Warning.getElement();
        warningRow.querySelector('.text').innerText = text;
        warningRow.classList.add('visible');
    },
    hide: () => Warning.getElement().classList.remove('visible')
};

// xButton 눌렀을 때
window.document.getElementById('xButton').addEventListener('click', () => {
    if (window.history.length > 1) {
        window.history.back();
    } else {
        window.location.href = '/member';
    }
});

form['findButton'].addEventListener('click', () => {
    Warning.hide();
    if (form['name'].value === '') {
        Warning.show('이름을 입력해주세요.');
        form['name'].focus();
        return;
    }
    if (form['contact'].value === '') {
        Warning.show('연락처를 입력해주세요.');
        form['contact'].focus();
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('name', form['name'].value);
    formData.append('contact', form['contact'].value);
    xhr.open('POST', './recoverEmail');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        form['name'].setAttribute('disabled', 'disabled');
                        form['contact'].setAttribute('disabled', 'disabled');
                        form['findButton'].setAttribute('disabled', 'disabled');
                        form.querySelector('[rel="messageRow"]').classList.add('visible');

                        form.querySelector('[rel="message"]').innerText = responseObject['email'];
                        form.querySelector('[rel="loginRow"]').classList.add('visible');
                        break;
                    default:
                        Warning.show('입력한 정보와 일치하는 회원이 없습니다.');
                        form['name'].focus();
                        form['name'].select();
                }
            } else {
                showDialog.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
});

// 로그인 버튼 눌렀을 때
window.document.getElementById('loginButton').addEventListener('click', () => {
    window.location.href = '/member/login';
});
