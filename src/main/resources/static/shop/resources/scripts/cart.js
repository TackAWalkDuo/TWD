const form = window.document.getElementById('form');

// const checkAll = window.document.getElementsByName('checkAll');
const modifyButton = window.document.querySelectorAll('[rel="modifyButton"]');
const modifyContainer = window.document.getElementById('modifyContainer');
const modifyFrame = window.document.getElementById('modifyFrame');
const cancelButton = window.document.querySelector('[rel="cancelButton"]');
const modifyCancelButton = window.document.querySelector('[rel="modifyCancelButton"]');
const orderForm = window.document.querySelector('[rel="orderForm"]');
const plusButton = window.document.querySelector('[rel="plusButton"]');
const minusButton = window.document.querySelector('[rel="minusButton"]');


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
const infoPrice = window.document.querySelector('[rel="infoPrice"]');

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
        infoPrice.value = x.querySelector('[rel="salePrice"]').innerText * infoNumber.value;
        if (infoNumber.value > 1) {
            alert('1보다 크다');
            minusButton.removeAttribute('disabled');
            // removeDisabled();
        }


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

const modifyFunction = (x) => {
    modifyContainer.classList.add('visible');
    const quantity = window.document.querySelector('[rel="productQuantity"]').value;
    const setDisabled = () => orderForm['minusButton'].setAttribute('disabled', 'disabled');


    // const setDisabled = () => x.querySelector('[rel="minusButton"]').setAttribute('disabled', 'disabled');
    // const removeDisabled = () => x.querySelector('[rel="minusButton"]').removeAttribute('disabled');

    const productPrice = window.document.querySelector('[rel="infoPrice"]').value;
    const chargeQuantity = window.document.querySelector('[rel="chargeQuantity"]');
    const chargePrice = window.document.querySelector('[rel="chargePrice"]');
    const modifyConfirmButton = window.document.querySelector('[rel="modifyConfirmButton"]');
    infoNumber.value = x.querySelector('[rel="productQuantity"]').innerText;
    console.log("function up");
    console.log(x);

    plusButton.addEventListener('click', (e) => {
        e.preventDefault();
        // 값이 1보다 클 때 -버튼 활성화
        if (infoNumber.value > 1) {
            minusButton.removeAttribute('disabled');
            // removeDisabled();
        }
        infoNumber.value = parseInt(infoNumber.value) + 1;
        console.log("plusButton");
        infoPrice.value = infoNumber.value * x.querySelector('[rel="salePrice"]').innerText;
        console.log(infoNumber.value);
        // orderForm['infoPrice'].value = x.querySelector("infoNumber") * 1000;
        chargePrice.innerText = infoNumber.value * x.querySelector('[rel="salePrice"]').innerText + '원';
        chargeQuantity.innerText = '총 수량 ' + infoNumber.value + '개';
        console.log(chargeQuantity);


        // 주문수량이 재고량보다 많을 때 경고창 출력 및 갯수 1개로 초기화
        if (parseInt(infoNumber.value) > quantity) {
            alert('주문 수량이 재고량을 초과했습니다.');
            infoNumber.value = 1;
            setDisabled();
        }
        console.log('내부 수량' + infoNumber.value);
        console.log('외부수량 ' + x.querySelector('[rel="productQuantity"]').innerText);
        x.querySelector('[rel="productQuantity"]').innerText = infoNumber.value;
        x.querySelector('[rel="itemPriceSum"]').innerText = parseInt(infoNumber.value) * 3000;
    });

    console.log("function down");

    modifyConfirmButton.addEventListener('click', e => {
        alert('클릭함');
        e.preventDefault();
        // x.querySelector('[rel="productQuantity"]').innerText = infoNumber.value;
        // modifyContainer.classList.remove('visible');
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("index", x.querySelector('[rel="cartIndex"]').innerText);
        formData.append("quantity", infoNumber.value);
        xhr.open('PATCH', window.location.href);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success' :
                            alert('성공');
                            window.location.reload();
                            break;

                        default :
                            alert('으악');
                    }

                } else {
                    alert('멸망');
                }
            }
        };
        xhr.send(formData);
        //success->>
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


