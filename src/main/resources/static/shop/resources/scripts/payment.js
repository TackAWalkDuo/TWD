const form = window.document.getElementById('form');
const cartItem = window.document.querySelectorAll('[rel="cart-item"]');
const cancelButton = window.document.querySelectorAll('[rel="cancelButton"]');

// //TODO 상태 텍스트 변경.
let initialization = true;
if (initialization) {
    cartItem.forEach(x => {
        // 0 : 결제완료 1: 배송중 2: 배송완료
        const deliveryStatus = x.querySelector('[rel="deliveryStatus"]').innerText;
        const deliveryText = x.querySelector('[rel="deliveryStatusText"]');
        if (deliveryStatus === '0') {
            deliveryText.innerText = '결제완료';
        }
        else if (deliveryStatus === '1') {
            deliveryText.innerText = '배송중';
        }
        else if (deliveryStatus === '2') {
            deliveryText.innerText = '배송완료';
        }
    });
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



