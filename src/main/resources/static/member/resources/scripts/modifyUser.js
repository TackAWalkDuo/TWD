const form = window.document.getElementById('form');
// '여' 체크 했을때 견종선택 나오도록

let initialization = false;

if (!initialization) {
    //생년월일 중 월 선택.
    for(let i = 1; i < form['birthMonth'].length; i++) {
        if(form['birthMonth'][i].value === form['initBirthMonth'].value) {
            form['birthMonth'][i].selected = true;
        }
    }

    //애견 보유 여부
    form['initHaveDog'].value === 'notHave' ?
        form.querySelector('[rel="notHaveDog"]').checked = true :
        initializationHaveDog();

    //성별 여부 기본값 check
    form['initGender'].value === 'man' ?
        form.querySelector('[rel="genderMan"]').checked = true
        : form.querySelector('[rel="genderWoman"]').checked = true;
    initialization = true;
}

function initializationHaveDog() {
    window.document.getElementById('checkSpecies').style.display = "table-row";
    form.querySelector('[rel="haveDog"]').checked = true;

    form['initSpecies'].value === '소형견' ?
        form.querySelector('[rel="small"]').checked = true :
        (form['initSpecies'].value === '중형견' ?
                form.querySelector('[rel="middle"]').checked = true :
                form.querySelector('[rel="big"]').checked = true
        );
}

form.querySelector('[rel="haveDog"]').addEventListener('click', () => {
    document.getElementById('checkSpecies').style.display = "table-row";
});

// '부' 체크 했을때 견종선택 안나오도록
form.querySelector('[rel="notHaveDog"]').addEventListener('click', () => {
    document.getElementById('checkSpecies').style.display = "none";
});

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

// 이전 버튼
form.querySelector('[rel="beforeButton"]').addEventListener('click', () => {
    if (window.history.length > 1) {
        window.history.back();
    }
});

// 다음 버튼
form.querySelector('[rel="nextButton"]').addEventListener('click', () => {
    // 다 입력후 다음버튼을 눌렀을 때
    if (form.classList.contains('step1')) {
        form.querySelector('[rel="warning"]').classList.remove('visible');
        form.querySelector('[rel="stepText"]').innerText = '개인정보 입력';
        form.classList.remove('step1');// step1 글씨는 사라지게 됨
        form.classList.add('step2');
    }
    else if (form.classList.contains('step2')) {
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

        xhr.open('PATCH', window.location.href);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success':
                            form.querySelector('[rel="stepText"]').innerText = '정보수정 완료';
                            form.querySelector('[rel="nextButton"]').innerText = '홈화면 가기';
                            form.classList.remove('step2');
                            form.classList.add('step3');
                            break;
                        case 'no_such_user':
                            Warning.show('회원정보를 수정할 수 없습니다.');
                            break;
                        case 'not_allowed':
                            Warning.show('회원정보를 수정할 수 있는 권한이 없거나 로그아웃 되었습니다. 확인 후 다시 시도해 주세요.');
                            break;
                        default:
                            Warning.show('알 수 없는 이유로 정보수정에 실패하였습니다. 잠시 후 다시 시도해 주세요.');
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