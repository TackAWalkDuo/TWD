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
        // if (form['nickname'].value === '') {
        //     Warning.show('닉네임을 입력해주세요.')
        //     form['nickname'].focus();
        //     return;
        // }
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
        // formData.append('email', form['email'].value);
        formData.append('email', form['emailAuthCode'].value);
        // formData.append('salt', form['emailAuthSalt'].value);
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
                            alert('카카오로 회원가입을 성공하였습니다. 로그인 페이지로 이동합니다.');
                            window.close();
                            break;
                        default:
                            Warning.show('알 수 없는 이유로 카카오 회원가입에 실패하였습니다. 잠시 후 다시 시도해 주세요.');
                    }
                } else {
                    Warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
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
    document.getElementById('checkSpecies').style.display = "flex";
});

// '부' 체크 했을때 견종선택 안나오도록
form.querySelector('[rel="notHaveDog"]').addEventListener('click', () => {
    document.getElementById('checkSpecies').style.display = "none";
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