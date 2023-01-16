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
    if (!confirm('정말로 탈퇴하시겠습니까?')) {
        // 회원 탈퇴 버튼 클릭시 '정말로 탈퇴 하겠냐'는 컨펌
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    xhr.open('DELETE', './secession');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        alert('탈퇴 되었습니다. 그동안 서비스를 이용해주셔서 감사합니다.');
                        window.location.href = '/';
                        break;
                    case 'not_allowed':
                        alert('권한이 없습니다.');
                        break;
                    case 'no_such_user':
                        alert('일치하는 회원이 존재하지 않습니다.');
                        break;
                    default:
                        alert('알 수 없는 이유로 회원탈퇴를 실패하였습니다.');
                }
            } else {
                alert('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
}




