const form = window.document.getElementById('loginForm');

const loginButton = document.getElementById('kakaoLoginButton');
// 카카오 로그인하기 버튼
const loginContainer = document.getElementById('loginContainer');

const Warning = {
    getElement: () => form.querySelector('[rel="warningRow"]'),
    show: (text) => {
        const warningRow = Warning.getElement();
        warningRow.querySelector('.warningText').innerText = text;
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

// 로그인 버튼 눌렀을 때
form.onsubmit = (e) => {
    e.preventDefault();
    Warning.hide();
    if (form['email'].value === '') {
        Warning.show("이메일을 입력해 주세요.");
        form['email'].focus();
        return false;
    }
    if (form['password'].value === '') {
        Warning.show("비밀번호를 입력해 주세요.");
        form['password'].focus();
        return false;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    formData.append('password', form['password'].value);
    xhr.open('POST', './login');
    console.log("확인");
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        console.log(document.referrer);
                        if (window.history.length > 2) window.location.href = document.referrer;
                        else {
                            window.location.href = '/';
                        }
                        break;
                    default:
                        Warning.show('이메일 혹은 비밀번호가 올바르지 않습니다.');
                        form['email'].focus();
                        form['email'].select();
                }
            } else {
                Warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
}

// 카카오로 로그인하기 눌렀을 때
loginButton?.addEventListener('click', e => {
    e.preventDefault();
    loginContainer.classList.add('visible');
    // window.location.href = '/member/register';
    window.open('https://kauth.kakao.com/oauth/authorize?client_id=6da80eef1101bb3318ba1f6bde584ab1&redirect_uri=http://localhost:8080/member/kakao&response_type=code', '로그인', 'width=500; height=750');

});




