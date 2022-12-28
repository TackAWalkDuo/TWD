const form = window.document.getElementById('secessionForm');

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

// 탈퇴하기 버튼 눌렀을 때
form.onsubmit = (e) => {
    e.preventDefault();
    Warning.hide();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    xhr.open('POST', './secession');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        Warning.show('정말로 회원탈퇴를 하시겠습니까?');
                        break;
                    default:
                        Warning.show('알 수 없는 이유로 회원탈퇴를 실패하였습니다.');
                }
            } else {
                Warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
}




