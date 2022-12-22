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

let emailAuthIndex = null;
setInterval(() => {
    if (emailAuthIndex === null) {
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('index', emailAuthIndex);
    xhr.open('POST', './recoverPasswordEmail');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                console.log(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        form['code'].value = responseObject['code'];
                        form['salt'].value = responseObject['salt'];

                        form.querySelector('[rel="messageRow"]').classList.remove('visible');
                        form.querySelector('[rel="passwordRow"]').classList.add('visible');
                        form['password'].focus();
                        emailAuthIndex = null;
                        break;
                    default:
                }
            }
        }
    };
    xhr.send(formData);
}, 1000);
// 1초마다 한번씩 '오'가 찍혀나온다 (콘솔)


form['emailSend'].addEventListener('click', () => {
    Warning.hide();
    if (form['email'].value === '') {
        Warning.show('이메일을 입력해 주세요');
        form['email'].focus();
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    xhr.open('POST', './recoverPassword');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                console.log(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        emailAuthIndex = responseObject['index'];
                        form['email'].setAttribute('disabled', 'disabled');
                        form['emailSend'].setAttribute('disabled', 'disabled');
                        form.querySelector('[rel="messageRow"]').classList.add('visible');
                        break;
                    default:
                        Warning.show('해당 이메일을 사용하는 계정을 찾을 수 없습니다.');
                        form['email'].focus();
                        form['email'].select();
                }
            } else {
                Warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
});

// 비밀번호 재설정하기 버튼
form['recover'].addEventListener('click', () => {
    Warning.hide();
    if (form['password'].value === '') {
        Warning.show('새로운 비밀번호를 입력해 주세요.');
        form['password'].focus();
        return;
    }
    if (form['password'].value !== form['passwordCheck'].value) {
        Warning.show('비밀번호가 서로 일치하지 않습니다.');
        form['passwordCheck'].focus();
        form['passwordCheck'].select();
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    formData.append('code', form['code'].value);
    formData.append('salt', form['salt'].value);
    formData.append('password', form['password'].value);
    xhr.open('PATCH', './recoverPassword');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        alert('비밀번호를 성공적으로 재설정하였습니다.\n\n확인을 누르면 로그인 페이지로 이동합니다.');
                        window.location.href = 'login';
                        break;
                    default:
                        Warning.show('비밀번호를 재설정하지 못하였습니다. 세션이 만료되었을 수 도 있습니다. 잠시 후 다시 시도해 주세요.');
                }
            } else {
                Warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
});