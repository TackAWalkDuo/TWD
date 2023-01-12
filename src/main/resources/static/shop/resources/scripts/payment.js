const form = window.document.getElementById('form');
const cartItem = window.document.querySelectorAll('[rel="cart-item"]');
const cancelButton = window.document.querySelectorAll('[rel="cancelButton"]');

//TODO 상태 텍스트 변경.
let initialization = true;
if(initialization) {
    cartItem.forEach(x => {
        // x.querySelector()
        //배송 상태 innerText 변경.
    })
    initialization = false;
}




cartItem.forEach(x => {
    x.querySelector('[rel="cancelButton"]')?.addEventListener('click', e => {
        e.preventDefault();
        if (!confirm("정말로 주문을 취소할까요?")) {
            return;
        }
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("index", x.querySelector('[rel="index"]').innerText);
        xhr.open('DELETE', window.location.href);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success':
                            alert('주문 취소 성공');
                            window.location.reload();
                            break;
                        default:
                            alert('실패');
                    }
                } else {
                    alert('연결 실패');
                }
            }
        }
        xhr.send(formData);
    })
})



