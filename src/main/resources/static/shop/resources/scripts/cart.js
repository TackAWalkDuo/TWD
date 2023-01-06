const form = window.document.getElementById('form');

// const checkAll = window.document.getElementsByName('checkAll');
const modifyButton = window.document.querySelectorAll('[rel="modifyButton"]');
const modifyContainer = window.document.getElementById('modifyContainer');
const modifyFrame = window.document.getElementById('modifyFrame');
const cancelButton = window.document.querySelector('[rel="cancelButton"]');
const modifyCancelButton = window.document.querySelector('[rel="modifyCancelButton"]');

// 전체 선택 체크시 모든 항목 체크
selectAll = checkAll => {
    const checkboxes
        = document.getElementsByName('checkBox');

    checkboxes.forEach((checkbox) => {
        checkbox.checked = checkAll.checked;
    })
}

// 각 tr의 주문수정 버튼 누를시 해당 td의 값들을 주문수정 창에 전달한다.
const cartItem = window.document.querySelectorAll('[rel="cart-item"]');
const modifyTitle = window.document.querySelector('[rel="modifyTitle"]');
const modifyImage = window.document.querySelector('[rel="modifyImage"]');
const modifyPrice = window.document.querySelector('[rel="modifyPrice"]');
const productQuantity = window.document.querySelectorAll('[rel="productQuantity"]');
const infoNumber = window.document.querySelector('[rel="infoNumber"]');
cartItem.forEach(x => {
    x.querySelector('[rel="modifyButton"]').addEventListener('click', () => {
        // alert(x.querySelector('[rel="productIndex"]').innerText);
        // alert(x.querySelector('[rel="proId"]').innerText);
        modifyTitle.innerText = x.querySelector('[rel="proId"]').innerText;
        modifyImage.setAttribute('src', `/bbs/thumbnail?index=${x.querySelector('[rel="productIndex"]').innerText}`);
        console.log(infoNumber.value);
        console.log(productQuantity.value);
        // infoNumber.value = x.querySelector('[rel="productQuantity"]').innerText;
        modifyFunction(x);
        // modifyContainer.classList.add('visible');

        // if (modifyContainer.classList.contains('visible')){
        //     const orderForm = window.document.querySelector('[rel="orderForm"]');
        //     const plusButton = window.document.querySelector('[rel="plusButton"]');
        //     const infoNum = window.document.querySelector('[rel="infoNumber"]');
        //     const quantity = window.document.querySelector('[rel="productQuantity"]').value;
        //     const setDisabled = () => orderForm['minusButton'].setAttribute('disabled', 'disabled');
        //     const removeDisabled = () => orderForm['minusButton'].removeAttribute('disabled');
        //     const productPrice = window.document.querySelector('[rel="infoPrice"]').value;
        //     const chargeQuantity = window.document.querySelector('[rel="chargeQuantity"]');
        //
        //     orderForm['plusButton'].onclick = () => {
        //         infoNum.value = parseInt(infoNum.value) + 1;
        //         orderForm['infoPrice'].value = parseInt(orderForm['infoPrice'].value) + parseInt(productPrice) + '원';
        //         chargePrice.innerText = parseInt(infoNum.value) * parseInt(productPrice) + '원';
        //         chargeQuantity.innerText = '총 수량 ' + infoNum.value + '개';
        //         console.log(chargeQuantity);
        //         // 값이 1보다 클 때 +버튼 활성화
        //         if (infoNum.value > 1) {
        //             removeDisabled();
        //         }
        //         // 주문수량이 재고량보다 많을 때 경고창 출력 및 갯수 1개로 초기화
        //         if (parseInt(infoNum.value) > quantity) {
        //             alert('주문 수량이 재고량을 초과했습니다.');
        //             infoNum.value = 1;
        //             setDisabled();
        //         }
        //     };
        // }
    });
});


const orderForm = window.document.querySelector('[rel="orderForm"]');

const plusButton = window.document.querySelector('[rel="plusButton"]');
const infoNum = window.document.querySelector('[rel="infoNumber"]');

const modifyFunction = (x) => {
    modifyContainer.classList.add('visible');
    const quantity = window.document.querySelector('[rel="productQuantity"]').value;
    const setDisabled = () => orderForm['minusButton'].setAttribute('disabled', 'disabled');
    const removeDisabled = () => orderForm['minusButton'].removeAttribute('disabled');
    const productPrice = window.document.querySelector('[rel="infoPrice"]').value;
    const chargeQuantity = window.document.querySelector('[rel="chargeQuantity"]');
    const modifyConfirmButton = window.document.querySelector('[rel="modifyConfirmButton"]');
    infoNumber.value = x.querySelector('[rel="productQuantity"]').innerText;
    console.log("function up");
    plusButton.addEventListener('click', (e) => {
        e.preventDefault();
        infoNum.value = parseInt(infoNum.value) + 1;
        console.log("plusButton");
        // orderForm['infoPrice'].value = parseInt(orderForm['infoPrice'].value) + parseInt(productPrice) + '원';
        console.log(infoNum.value);
        // orderForm['infoPrice'].value = x.querySelector("infoNumber") * 1000;
        // chargePrice.innerText = parseInt(infoNum.value) * parseInt(productPrice) + '원';
        // chargeQuantity.innerText = '총 수량 ' + infoNum.value + '개';
        // console.log(chargeQuantity);
        // 값이 1보다 클 때 +버튼 활성화
        // if (infoNum.value > 1) {
        //     removeDisabled();
        // }
        // // 주문수량이 재고량보다 많을 때 경고창 출력 및 갯수 1개로 초기화
        // if (parseInt(infoNum.value) > quantity) {
        //     alert('주문 수량이 재고량을 초과했습니다.');
        //     infoNum.value = 1;
        //     setDisabled();
        // }
        console.log('내부 수량' + infoNum.value);
        console.log('외부수량 ' + x.querySelector('[rel="productQuantity"]').innerText);
        x.querySelector('[rel="productQuantity"]').innerText = infoNum.value;
        x.querySelector('[rel="itemPriceSum"]').innerText = parseInt(infoNum.value) * 3000;
    });
    console.log("function down");

    modifyConfirmButton.addEventListener('click', () => {
        alert('클릭함');
        // x.querySelector('[rel="productQuantity"]').innerText = infoNumber.value;
        productQuantity.innerText = 100;

        modifyContainer.classList.remove('visible');
        // infoNumber.value = x.querySelector('[rel="productQuantity"]').innerText;
    })
}

// modifyButton.forEach(x => {
//     x?.addEventListener('click', e => {
//         e.preventDefault();
//         alert(cartText.innerText);
//         infoTitle.innerText = cartText.item(i).innerText;
//         i++;
//         console.log(cartText.length);
//         modifyContainer.classList.add('visible');
//     });
// });

modifyCancelButton?.addEventListener('click', e => {
    e.preventDefault();
    modifyContainer.classList.remove('visible');
});

cancelButton?.addEventListener('click', e => {
    e.preventDefault();
    modifyContainer.classList.remove('visible');
})


