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

// xButton 눌렀을 때
window.document.getElementById('xButton').addEventListener('click', () => {
    if (window.history.length > 1) {
        window.history.back();
    } else {
        window.location.href = '/member';
    }
});

const EmailWarning = {
    show: (text) => {
        const emailWarning = form.querySelector('[rel="emailWarning"]');
        emailWarning.innerText = text;
        emailWarning.classList.add('visible');
    },
    hide: () => {
        form.querySelector('[rel="emailWarning"]').classList.remove('visible');
    }
};

// 이전 버튼
form.querySelector('[rel="beforeButton"]').addEventListener('click', () => {
    if (window.history.length > 1) {
        window.history.back();
    }
});

// 다음 버튼
form.querySelector('[rel="nextButton"]').addEventListener('click', () => {
    // 다 입력후 다음버튼을 눌렀을 때
    form.querySelector('[rel="warning"]').classList.remove('visible');
    if (form.classList.contains('step1')) {
        if (!form['termAgree'].checked) {
            // 만약 서비스 약관 동의에 체크를 하지 않았다면
            form.querySelector('[rel="warningText"]').innerText = '서비스 이용약관 및 개인정보 처리방침을 읽고 동의해 주세요.';
            //서비스 이용약관 및 개인정보 처리방침을 읽고 동의해 주세요. 자리에
            // innerText로 인해서 글씨가 바뀌게 된다. (경고 메시지 출력)
            form.querySelector('[rel="warning"]').classList.add('visible');
            // 클래스가 추가된다.
            return;
        }
        form.querySelector('[rel="stepText"]').innerText = '개인정보 입력';
        form.classList.remove('step1');// step1 글씨는 사라지게 됨
        form.classList.add('step2');
    } else if (form.classList.contains('step2')) {
        if (!form['emailSend'].disabled || !form['emailVerify'].disabled) {
            Warning.show('이메일 인증을 완료해 주세요.');
            return;
        }
        if (form['password'].value === '') {
            Warning.show('비밀번호를 입력해주세요.')
            form['password'].focus();
            return;
        }
        if (form['password'].value !== form['passwordCheck'].value) {
            Warning.show('비밀번호가 서로 일치하지 않습니다.')
            form['password'].focus();
            return;
        }
        if (form['name'].value === '') {
            Warning.show('이름을 입력해주세요.')
            form['name'].focus();
            return;
        }
        if (form['nickname'].value === '') {
            Warning.show('닉네임을 입력해주세요.')
            form['nickname'].focus();
            return;
        }
        if (form['contact'].value === '') {
            Warning.show('연락처를 입력해주세요.')
            form['contact'].focus();
            return;
        }
        if (form['birthYear'].value === '') {
            Warning.show('출생년도(년도)를 입력해주세요.')
            form['birthYear'].focus();
            return;
        }

        const birthMonth = document.getElementById("birthMonth");
        const selectedValue = birthMonth.options[birthMonth.selectedIndex].value;
        if (selectedValue === '월') {
            Warning.show('출생년도(월)을 입력해주세요.')
            form['birthMonth'].focus();
            return;
        }
        if (form['birthDay'].value === '') {
            Warning.show('출생년도(일)을 입력해주세요.')
            form['birthDay'].focus();
            return;
        }

        const checkGenderMan = document.querySelector('[rel="genderMan"]');
        const checkGenderWoman = document.querySelector('[rel="genderWoman"]');

        if (checkGenderMan.checked === false && checkGenderWoman.checked === false) {
            Warning.show('성별을 체크해주세요.')
            return;
        }

        const checkHaveDog = document.querySelector('[rel="havedog"]');
        const checkHaveNotDog = document.querySelector('[rel="notHaveDog"]');
        if (checkHaveDog.checked === false && checkHaveNotDog.checked === false) {
            Warning.show('애완견 여/부를 체크해 주세요.')
            return;
        }

        const checkSmall = document.querySelector('[rel="small"]');
        const checkMiddle = document.querySelector('[rel="middle"]');
        const checkBig = document.querySelector('[rel="big"]');
        if (checkHaveDog.checked === true && checkSmall.checked === false && checkMiddle.checked === false && checkBig.checked === false) {
            Warning.show('견종선택을 체크해 주세요.')
            return;
        }

        if (form['addressPostal'].value === '' || form['addressPrimary'].value === '') {
            Warning.show('주소 찾기를 통해 주소를 입력해 주세요.')
            return;
        }

        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('email', form['email'].value);
        formData.append('code', form['emailAuthCode'].value);
        formData.append('salt', form['emailAuthSalt'].value);
        formData.append('password', form['password'].value);
        formData.append('nickname', form['nickname'].value);
        formData.append('name', form['name'].value);
        formData.append('contact', form['contact'].value);
        formData.append('birthYear', form['birthYear'].value);
        formData.append('birthMonth', form['birthMonth'].value);
        formData.append('birthDay', form['birthDay'].value);
        formData.append('gender', form['gender'].value);
        formData.append('haveDog', form['haveDog'].value);
        formData.append('species', form['species'].value);
        formData.append('addressPostal', form['addressPostal'].value);
        formData.append('addressPrimary', form['addressPrimary'].value);
        formData.append('addressSecondary', form['addressSecondary'].value);

        xhr.open('POST', './register');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success':
                            form.querySelector('[rel="stepText"]').innerText = '회원가입 완료';
                            form.querySelector('[rel="nextButton"]').innerText = '로그인하러 가기';
                            form.classList.remove('step2');
                            form.classList.add('step3');
                            break;
                        case 'email_not_verified':
                            showDialog.show('이메일 인증이 완료되지 않았습니다.');
                            break;
                        default:
                            showDialog.show('알 수 없는 이유로 회원가입에 실패하였습니다. 잠시 후 다시 시도해 주세요.');
                    }
                } else {
                    showDialog.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            }
        };
        xhr.send(formData);
    } else if (form.classList.contains('step3')) {
        window.location.href = 'login';
    }
});

// '여' 체크 했을때 견종선택 나오도록
form.querySelector('[rel="haveDog"]').addEventListener('click', () => {
    document.getElementById('checkSpecies').style.display = "table-row";
});

// '부' 체크 했을때 견종선택 안나오도록
form.querySelector('[rel="notHaveDog"]').addEventListener('click', () => {
    document.getElementById('checkSpecies').style.display = "none";
});


// 인증번호 전송 눌렀을 때
form['emailSend'].addEventListener('click', () => {
    form.querySelector('[rel=emailWarning]').classList.remove('visible');
    if (form['email'].value === '이메일을 입력해주세요') {
        EmailWarning.show('')
        form['email'].focus();
        return;
    }
    if (!new RegExp('^(?=.{9,50}$)([\\da-zA-Z\\-_.]{4,})@([\\da-z\\-]{2,}\\.)?([\\da-z\\-]{2,})\\.([a-z]{2,15})(\\.[a-z]{2})?$').test(form['email'].value)) {
        EmailWarning.show('올바른 이메일 주소를 입력해 주세요.');
        form['email'].focus();
        return;
    }
    //Cover.show('인증번호를 전송하고 있습니다.\n잠시만 기다려 주세요.');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    xhr.open('POST', './email'); // post방식, RequestMapping의 ./email 은 POST를 요청할 도메인이다.
    xhr.onreadystatechange = () => {        // 이 함수는 추후에 실행된다.
        if (xhr.readyState === XMLHttpRequest.DONE) {
            //Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                // Http 코드가 200이상 300미만일 경우 성공. success 가 표시된다.
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        form.querySelector('[rel="emailWarning"]').innerText = '인증 번호를 전송하였습니다. 전송된 인증 번호는 5분간만 유효합니다.';
                        form.querySelector('[rel="emailWarning"]').classList.add('visible');
                        form['email'].setAttribute('disabled', 'disabled');
                        form['emailSend'].setAttribute('disabled', 'disabled');
                        form['emailAuthCode'].removeAttribute('disabled');
                        form['emailAuthCode'].focus();
                        form['emailAuthSalt'].value = responseObject['salt'];
                        form['emailVerify'].removeAttribute('disabled');
                        break;
                    case 'email_duplicated':
                        form.querySelector('[rel="emailWarning"]').innerText = '해당 이메일은 이미 사용 중입니다.';
                        form.querySelector('[rel="emailWarning"]').classList.add('visible');
                        form['email'].focus();
                        form['email'].select();
                        break;
                    default:
                        form.querySelector('[rel="emailWarning"]').innerText = '알 수 없는 이유로 인증 번호를 전송하지 못하였습니다. 잠시 후 다시 시도해 주세요.'
                        form.querySelector('[rel="emailWarning"]').classList.add('visible');
                        form['email'].focus();
                        form['email'].select();
                }
            } else {
                form.querySelector('[rel="emailWarning"]').innerText = '서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.';
                // 이너텍스트로 표시해준다.
                form.querySelector('[rel="emailWarning"]').classList.add('visible');
                // 클래스가 추가된다. (css로 가보면 visible일때 => block스타일로 보여주겠다고 해놨음)
            }
        }
    };
    xhr.send(formData);
    // 요청을 보낸 뒤에 readyState가 바뀐다.
    //formData를 꼭 적어줘야 요청이 실려가게 된다. (주의)
});

form['emailVerify'].addEventListener('click', () => {
    if (form['emailAuthCode'].value === '') {
        EmailWarning.show('인증번호를 입력해 주세요.');
        form['emailAuthCode'].focus();
        return;
    }
    if (!new RegExp('^(\\d{6})$').test(form['emailAuthCode'].value)) {
        EmailWarning.show('올바른 인증 번호를 입력해 주세요.');
        form['emailAuthCode'].focus();
        form['emailAuthCode'].select();
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    formData.append('code', form['emailAuthCode'].value); // email의 name
    formData.append('salt', form['emailAuthSalt'].value);
    xhr.open('PATCH', './email');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            //Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'expired':
                        EmailWarning.show('인증 정보가 만료되었습니다. 다시 시도해 주세요');
                        form['email'].removeAttribute('disabled');
                        form['email'].focus();
                        form['email'].select();
                        form['emailSend'].removeAttribute('disabled');
                        form['emailAuthCode'].value = '';
                        form['emailAuthCode'].setAttribute('disabled', 'disabled');
                        form['emailAuthSalt'].value = '';
                        form['emailVerify'].setAttribute('disabled', 'disabled');
                        break;
                    case 'success':
                        EmailWarning.show('이메일이 정상적으로 인증되었습니다.');
                        form['emailAuthCode'].setAttribute('disabled', 'disabled');
                        form['emailVerify'].setAttribute('disabled', 'disabled');
                        form['password'].focus();
                        break;
                    default:
                        EmailWarning.show('인증 번호가 올바르지 않습니다.');
                        form['emailAuthCode'].focus();
                        form['emailAuthCode'].select();
                }
            } else {
                EmailWarning.show(
                    '서버와 통신하지 못했습니다.잠시 후 다시 시도해 주세요.');
            }

        }
    };
    xhr.send(formData);
});

// 우편번호 찾기 기능
form['addressFind'].addEventListener('click', () => {
    new daum.Postcode({
        oncomplete: e => {
            form.querySelector('[rel="addressFindPanel"]').classList.remove('visible');
            form['addressPostal'].value = e['zonecode'];
            form['addressPrimary'].value = e['address'];
            form['addressSecondary'].value = '';
            form['addressSecondary'].focus();
        }
    }).embed(form.querySelector('[rel="addressFindPanelDialog"]'));
    form.querySelector('[rel="addressFindPanel"]').classList.add('visible');
});

form.querySelector('[rel="addressFindPanel"]').addEventListener('click', () => {
    form.querySelector('[rel="addressFindPanel"]').classList.remove('visible');
});