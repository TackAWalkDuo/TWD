// form
const orderForm = window.document.getElementById('orderForm');
// 주문 수량 (input란)
const infoNum = window.document.querySelector('[rel="infoNumber"]');
// 제품 재고량
const quantity = window.document.querySelector('[rel="productQuantity"]')?.value;
// 버튼 비활성화
const setDisabled = () => orderForm['minusButton'].setAttribute('disabled', 'disabled');
// 버튼 활성화
const removeDisabled = () => orderForm['minusButton'].removeAttribute('disabled');
// 아이템별 가격
const productPrice = window.document.querySelector('[rel="infoPrice"]')?.value;
// 총 주문 수량
const chargeQuantity = window.document.querySelector('[rel="chargeQuantity"]');
// 총 주문 금액
const chargePrice = window.document.querySelector('[rel="chargePrice"]');
// 구매하기 버튼
const orderButton = window.document.querySelector('[rel="orderButton"]');
// 장바구니 담기
const addCart = window.document.querySelector('[rel="addCart"]');

const isSoldOut = window.document.querySelector('[rel="isSoldOut"]');

//  품절이 아닐 경우에만 실행
if(isSoldOut === null){
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

// 삭제
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

// 장바구니
    addCart?.addEventListener('click', e => {
        e.preventDefault();
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("salePrice", (orderForm['infoPrice'].value.replace('원', '')) / orderForm['infoNumber'].value);
        formData.append("quantity", orderForm['infoNumber'].value);
        formData.append("maxQuantity", quantity);
        xhr.open('POST', window.location.href);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success' :
                            if (confirm('장바구니 등록이 완료되었습니다. \n\n확인 : 장바구니로 이동 \n취소 : 쇼핑 계속하기')) {
                                window.location.href = `./cart`
                            } else {
                                window.location.href;
                            }
                            break;

                        case 'cart_not_signed':
                            alert('로그인 후 이용해 주세요.');
                            window.location.href ='/member/login'
                            break;

                        case 'cart_not_allowed' :
                            alert('존재하지 않는 상품입니다.');
                            break;

                        case 'cart_duplicated' :
                            alert('이미 장바구니에 등록된 상품입니다.');
                            break;

                        default :
                            alert('알 수 없는 이유로 장바구니에 등록하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                    }

                } else {
                    alert('서버 연결 실패');
                }
            }
        };
        xhr.send(formData);
    });


    orderButton?.addEventListener('click', e => {
        e.preventDefault();
        if (!confirm("해당 상품을 주문하시겠습니까?")) {
            return;
        }
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        const index = orderForm.querySelector('[rel="index"]').innerText;
        formData.append("productIndex", index);
        formData.append("quantity", orderForm['infoNumber'].value);
        xhr.open('POST', '/shop/payment');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success' :
                            alert('주문 성공');
                            window.location.href = `./payment`
                            break;
                        default :
                            alert('실패');
                    }

                } else {
                    alert('연결실패');
                }
            }
        };
        xhr.send(formData);
    })
}













