/*<![CDATA[*/ /*]]>*/
// <script th:inline="javascript">
//
// </script>

const orderForm = window.document.getElementById('orderForm');
let infoNum = window.document.querySelector('[rel="infoNumber"]');
const quantity = window.document.querySelector('[rel="productQuantity"]').value;
const setDisabled = () => orderForm['minusButton'].setAttribute('disabled', 'disabled');
const removeDisabled = () => orderForm['minusButton'].removeAttribute('disabled');
const productPrice = window.document.querySelector('[rel="infoPrice"]').value;

// +버튼 누를시 제품 갯수 1씩 더함, 가격 올라감
orderForm['plusButton'].onclick = () => {
    infoNum.value = parseInt(infoNum.value) + 1;
    orderForm['infoPrice'].value = parseInt(orderForm['infoPrice'].value) + parseInt(productPrice) + '원';
    if (infoNum.value > 1) {
        removeDisabled();
    }
    if (parseInt(infoNum.value) > quantity) {
        alert('주문 수량이 재고량을 초과했습니다.');
        infoNum.value = 1;
        setDisabled();
    }
}

// -버튼 누를시 제품 갯수 1씩 뺌, 가격 내려감
orderForm['minusButton'].onclick = () => {
    infoNum.value = parseInt(infoNum.value) - 1;
    orderForm['infoPrice'].value = parseInt(orderForm['infoPrice'].value) - parseInt(productPrice) + '원';
    if (infoNum.value > 1) {
        removeDisabled();
        // orderForm['minusButton'].removeAttribute('disabled');
    }
    if (parseInt(infoNum.value) === 1) {
        setDisabled();
        // orderForm['minusButton'].setAttribute('disabled', 'disabled');
    }
}


// infoNumber 값이 1 미만일 때 경고창 출력 및 갯수 1개로 초기화
infoNum.onkeyup = e => {
    // 방법 1 : 숫자 입력을 제외한 모든 키보드 입력 자체를 제한한다
    // 방법 2 : 숫자를 제외하고 모든 키보드 입력을 무시한다.
    if ((e.keyCode >= 48 && e.keyCode <= 57) || e.keyCode === 8 || e.keyCode === 13) {
        alert('설정한 키 값');
        // e.stopPropagation();
        // e.preventDefault();
        // return false;
    } else {
        alert('다른 키 값');
    }

    if (infoNum.value < 1) {
        alert('1개 이상부터 구매하실 수 있습니다.');
        infoNum.value = 1;
        orderForm['infoPrice'].value = parseInt(orderForm['infoPrice'].value) * parseInt(infoNum.value) + '원';
        setDisabled();
    }
    if (infoNum.value > 1) {
        orderForm['infoPrice'].value = parseInt(orderForm['infoPrice'].value) * parseInt(infoNum.value) + '원';
        removeDisabled();
        // orderForm['minusButton'].removeAttribute('disabled');
    }
    if (parseInt(infoNum.value) > quantity) {
        alert('주문 수량이 재고량을 초과했습니다.');
        infoNum.value = 1;
        orderForm['infoPrice'].value = productPrice;
        setDisabled();
    }
}













