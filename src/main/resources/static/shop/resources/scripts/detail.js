// form
const orderForm = window.document.getElementById('orderForm');
// 주문 수량 (input란)
const infoNum = window.document.querySelector('[rel="infoNumber"]');
// 제품 재고량
const quantity = window.document.querySelector('[rel="productQuantity"]').value;
// 버튼 비활성화
const setDisabled = () => orderForm['minusButton'].setAttribute('disabled', 'disabled');
// 버튼 활성화
const removeDisabled = () => orderForm['minusButton'].removeAttribute('disabled');
// 아이템별 가격
const productPrice = window.document.querySelector('[rel="infoPrice"]').value;
// 총 주문 수량
const chargeQuantity = window.document.querySelector('[rel="chargeQuantity"]');
// 총 주문 금액
const chargePrice = window.document.querySelector('[rel="chargePrice"]');


// +버튼 누를시 실행
orderForm['plusButton'].onclick = () => {
    // +버튼 누를시 제품 갯수 1씩 더함, 가격 조정
    infoNum.value = parseInt(infoNum.value) + 1;
    orderForm['infoPrice'].value = parseInt(orderForm['infoPrice'].value) + parseInt(productPrice) + '원';
    chargePrice.innerText = parseInt(infoNum.value) * parseInt(productPrice) + '원';
    chargeQuantity.innerText = '총 수량 ' + infoNum.value + '개';
    console.log(chargeQuantity);
    // 값이 1보다 클 때 +버튼 활성화
    if (infoNum.value > 1) {
        removeDisabled();
    }
    // 주문수량이 재고량보다 많을 때 경고창 출력 및 갯수 1개로 초기화
    if (parseInt(infoNum.value) > quantity) {
        alert('주문 수량이 재고량을 초과했습니다.');
        infoNum.value = 1;
        setDisabled();
    }
}

// -버튼 누를시 실행
orderForm['minusButton'].onclick = () => {
    // -버튼 누를시 갯수 1씩 빼고 가격 조정
    infoNum.value = parseInt(infoNum.value) - 1;
    orderForm['infoPrice'].value = parseInt(orderForm['infoPrice'].value) - parseInt(productPrice) + '원';
    chargePrice.innerText = parseInt(infoNum.value) * parseInt(productPrice) + '원';
    chargeQuantity.innerText = '총 수량 ' + infoNum.value + '개';
    // 값이 1보다 클 때 -버튼 활성화
    if (infoNum.value > 1) {
        removeDisabled();
        // orderForm['minusButton'].removeAttribute('disabled');
    }
    // 값이 1일 때 -버튼 비활성화
    if (parseInt(infoNum.value) === 1) {
        setDisabled();
        // orderForm['minusButton'].setAttribute('disabled', 'disabled');
    }
}

// input란 값 변동시 실행
infoNum.onkeyup = e => {
    chargeQuantity.innerText = '총 수량 ' + infoNum.value + '개';
    chargePrice.innerText = parseInt(infoNum.value) * parseInt(productPrice) + '원';
    // input이 공란이면 총 갯수 0개로 지정
    if (infoNum.value == '') {
        e.preventDefault();
        orderForm['infoPrice'].value = 0 + '원';
        chargeQuantity.innerText = '총 수량 ' + 0 + '개';
        chargePrice.innerText = 0 + '원';
        return;
    }
    // 숫자, backspace, enter제외 키 입력 방지
    if (!((e.keyCode >= 48 && e.keyCode <= 57) || e.keyCode === 8 || e.keyCode === 13 || e.keyCode === 38 || e.keyCode === 40)) {
        e.preventDefault();
    }
    // infoNumber 값이 1 미만일 때 경고창 출력 및 갯수 1개로 초기화
    if (infoNum.value < 1) {
        alert('1개 이상부터 구매하실 수 있습니다.');
        infoNum.value = 1;
        chargeQuantity.innerText = '총 수량 ' + infoNum.value + '개';
        orderForm['infoPrice'].value = parseInt(orderForm['infoPrice'].value) * parseInt(infoNum.value) + '원';
        chargePrice.innerText = parseInt(infoNum.value) * parseInt(productPrice) + '원';
        setDisabled();
    }
    // 값이 1 이상일 때 가격 연산 및 값이 1일때 disabled 추가
    if (infoNum.value >= 1) {
        orderForm['infoPrice'].value = parseInt(productPrice) * parseInt(infoNum.value) + '원';
        removeDisabled();
        if (parseInt(infoNum.value) === 1) {
            setDisabled();
        }
    }
    // 주문수량이 재고량보다 많을 때 경고창 출력 및 갯수 1개로 초기화
    if (parseInt(infoNum.value) > quantity) {
        alert('주문 수량이 재고량을 초과했습니다.');
        infoNum.value = 1;
        orderForm['infoPrice'].value = productPrice;
        chargeQuantity.innerText = '총 수량 ' + infoNum.value + '개';
        chargePrice.innerText = parseInt(infoNum.value) * parseInt(productPrice) + '원';
        setDisabled();
    }
}

const deleteButton = window.document.getElementById('deleteButton');
deleteButton?.addEventListener('click', e => {
    e.preventDefault();
    if (!confirm('정말로 상품을 삭제할까요?')) {
        return;
    }
    // Cover.show('게시글을 삭제하고 있습니다.\n잠시만 기다려주세요.');
    const xhr = new XMLHttpRequest();
    xhr.open('DELETE', window.location.href);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            // Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        const bid = responseObject['bid'];
                        window.location.href = `./list?bid=${bid}`;
                        break;
                    default:
                        alert('알수없는 이유로 게시글을 삭제하지 못하였습니다.\n\n 잠시후에 다시 시도해 주세요.');
                }
            } else {
                alert('서버와 통신하지 못하였습니다.');
            }
        }
    };
    xhr.send();
});













