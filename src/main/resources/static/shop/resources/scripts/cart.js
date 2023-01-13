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
const maxQuantity = window.document.querySelectorAll('[rel="maxQuantity"]');
const salePrice = window.document.querySelector('[rel="salePrice"]');
const totalPrice = window.document.querySelector('[rel="totalPrice"]');
const deliveryFee = window.document.querySelector('[rel="deliveryFee"]');
const orderButton = window.document.querySelector('[rel="orderButton"]');

cancelButton?.addEventListener('click', () => {
    window.location.reload();
});
modifyCancelButton?.addEventListener('click', () => {
    window.location.reload();
});


// 전체 선택 체크시 모든 항목 체크
// selectAll = checkAll => {
//     const checkboxes
//         = document.getElementsByName('checkBox');
//     checkboxes.forEach((checkbox) => {
//         checkbox.checked = checkAll.checked;
//     })
// }

const selectAll = window.document.querySelector('[rel="selectAll"]');
const checkboxes
    = document.getElementsByName('checkBox');
// selectAll.addEventListener('click', checkAll => {
//     alert('선택됨');
//     alert('체크박스 갯수?' + checkboxes.length);
//     checkboxes.forEach((checkbox) => {
//         checkbox.checked = checkAll.checked;
//     })
// })

// 각 tr의 주문수정 버튼 누를시 해당 td의 값들을 주문수정 창에 전달한다.
const cartItem = window.document.querySelectorAll('[rel="cart-item"]');
const modifyTitle = window.document.querySelector('[rel="modifyTitle"]');
const modifyImage = window.document.querySelector('[rel="modifyImage"]');
const modifyPrice = window.document.querySelector('[rel="modifyPrice"]');
const productQuantity = window.document.querySelectorAll('[rel="productQuantity"]');
const infoNumber = window.document.querySelector('[rel="infoNumber"]');
const infoPrice = window.document.querySelector('[rel="infoPrice"]');
const deleteButton = window.document.querySelector('[rel="deleteButton"]');

selectAll?.addEventListener('click', () => {
    if (selectAll.checked) {
        cartItem.forEach(x => {
            salePrice.innerText = Number(salePrice.innerText) + Number(x.querySelector('[rel="itemPriceSum"]').innerText);
            x.querySelector('[rel="checkBox"]').checked = true;
        });
    } else {
        cartItem.forEach(x => {
            salePrice.innerText = Number(salePrice.innerText) - Number(x.querySelector('[rel="itemPriceSum"]').innerText);
            x.querySelector('[rel="checkBox"]').checked = false;
        });
    }
    totalPrice.innerText = Number(salePrice.innerText) + Number(deliveryFee.innerText);
});


deleteButton?.addEventListener('click', e => {
    e.preventDefault();
    let selectCheck = false;    // 선택된 제품이 없는지 확인하기 위해서.

    cartItem.forEach(x => {     // 상품 선택 유무를 확인하기위한 구문.
        if (x.querySelector('[rel="checkBox"]').checked) {
            selectCheck = true;
        }
    })

    if (!selectCheck) {
        alert("선택된 상품이 없습니다.");
        return;
    }

    if (!confirm("정말로 장바구니를 삭제할까요?")) {
        return;
    }

    //TODO index 배열로 담아서 수정.
    cartItem.forEach(x => {
        if (x.querySelector('[rel="checkBox"]').checked) {
            selectCheck = true;

            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append("index", x.querySelector('[rel="cartIndex"]').innerText);
            xhr.open('DELETE', window.location.href);
            xhr.onreadystatechange = () => {
                if (xhr.readyState === XMLHttpRequest.DONE) {
                    if (xhr.status >= 200 && xhr.status < 300) {
                        const responseObject = JSON.parse(xhr.responseText);
                        switch (responseObject['result']) {
                            case 'success':
                                alert('장바구니 삭제 성공');
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
        }
    })


})


cartItem.forEach(x => {
    x.querySelector('[rel="checkBox"]').addEventListener('click', () => {
        selectAll.checked = false;          // 전체선택중 하나 이상이 체크가 해제될 경우 전체 체크 해제
        const priceSum = x.querySelector('[rel="itemPriceSum"]').innerText;

        if (x.querySelector('[rel="checkBox"]').checked) {//선택된 경우
            salePrice.innerText = Number(salePrice.innerText) + Number(priceSum);
        } else { // 선택되지 않은 경우
            salePrice.innerText = Number(salePrice.innerText) - Number(priceSum);
        }
        totalPrice.innerText = Number(salePrice.innerText) + Number(deliveryFee.innerText);


    })

    x.querySelector('[rel="modifyButton"]')?.addEventListener('click', () => {
        // alert(x.querySelector('[rel="productIndex"]').innerText);
        // alert(x.querySelector('[rel="proId"]').innerText);
        modifyTitle.innerText = x.querySelector('[rel="proId"]').innerText;
        modifyImage.setAttribute('src', `/bbs/thumbnail?index=${x.querySelector('[rel="productIndex"]').innerText}`);
        infoNumber.value = x.querySelector('[rel="productQuantity"]').innerText;
        console.log(infoNumber.value);
        console.log(productQuantity.value);
        // infoNumber.value = x.querySelector('[rel="productQuantity"]').innerText;
        alert("총 수량은?" + x.querySelector('[rel="maxQuantity"]').innerText);
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
const productPrice = window.document.querySelector('[rel="infoPrice"]')?.value;
const chargeQuantity = window.document.querySelector('[rel="chargeQuantity"]');
const chargePrice = window.document.querySelector('[rel="chargePrice"]');
const modifyConfirmButton = window.document.querySelector('[rel="modifyConfirmButton"]');

const modifyFunction = (x) => {
    infoPrice.value = x.querySelector('[rel="eachSalePrice"]').innerText * infoNumber.value;
    chargePrice.innerText = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText + '원';
    if (infoNumber.value > 1) {
        minusButton.removeAttribute('disabled');
    }
    chargePrice.innerText = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText + '원';
    modifyContainer.classList.add('visible');
    const quantity = window.document.querySelector('[rel="productQuantity"]').value;
    const setDisabled = () => orderForm['minusButton'].setAttribute('disabled', 'disabled');


    // const setDisabled = () => x.querySelector('[rel="minusButton"]').setAttribute('disabled', 'disabled');
    // const removeDisabled = () => x.querySelector('[rel="minusButton"]').removeAttribute('disabled');


    infoNumber.value = x.querySelector('[rel="productQuantity"]').innerText;
    console.log("function up");
    console.log(x);

    plusButton.addEventListener('click', (e) => {
        e.preventDefault();
        // 값이 1보다 클 때 -버튼 활성화
        infoNumber.value = parseInt(infoNumber.value) + 1;
        if (infoNumber.value > 1) {
            minusButton.removeAttribute('disabled');
            // removeDisabled();
        }
        console.log("plusButton");
        infoPrice.value = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText;
        // orderForm['infoPrice'].value = x.querySelector("infoNumber") * 1000;
        chargePrice.innerText = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText + '원';
        chargeQuantity.innerText = '총 수량 ' + infoNumber.value + '개';
        console.log(chargeQuantity);


        // 주문수량이 재고량보다 많을 때 경고창 출력 및 갯수 초기화
        if (parseInt(infoNumber.value) > x.querySelector('[rel="maxQuantity"]').innerText) {
            alert('주문 수량이 재고량을 초과했습니다.');
            infoNumber.value = x.querySelector('[rel="productQuantity"]').innerText;
            if (infoNumber.value > 1) {
                minusButton.removeAttribute('disabled');
            } else {
                minusButton.setAttribute('disabled', 'disabled');
            }
            infoPrice.value = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText;
            chargeQuantity.innerText = '총 수량 ' + infoNumber.value + '개';
            chargePrice.innerText = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText + '원';
        }
        // console.log('내부 수량' + infoNumber.value);
        // console.log('외부수량 ' + x.querySelector('[rel="productQuantity"]').innerText);
        // x.querySelector('[rel="productQuantity"]').innerText = infoNumber.value;
    });

    console.log("function down");

    minusButton.addEventListener('click', () => {
        // -버튼 누를시 갯수 1씩 빼고 가격 조정
        infoNumber.value = parseInt(infoNumber.value) - 1;
        infoPrice.value = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText;
        chargePrice.innerText = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText + '원';
        chargeQuantity.innerText = '총 수량 ' + infoNumber.value + '개';
        // 값이 1보다 클 때 -버튼 활성화
        if (infoNumber.value > 1) {
            minusButton.removeAttribute('disabled');
            // orderForm['minusButton'].removeAttribute('disabled');
        }
        // 값이 1일 때 -버튼 비활성화
        if (parseInt(infoNumber.value) === 1) {
            minusButton.setAttribute('disabled', 'disabled');
            // orderForm['minusButton'].setAttribute('disabled', 'disabled');
        }
    });

    // input란 값 변동시 실행
    infoNumber.onkeyup = e => {
        chargeQuantity.innerText = '총 수량 ' + infoNumber.value + '개';
        chargePrice.innerText = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText + '원';
        // input이 공란이면 총 갯수 0개로 지정
        if (infoNumber.value === '') {
            e.preventDefault();
            infoPrice.value = 0 + '원';
            chargeQuantity.innerText = '총 수량 ' + 0 + '개';
            chargePrice.innerText = 0 + '원';
            return;
        }
        // 숫자, backspace, enter제외 키 입력 방지
        // 48~57 : 숫자, 8 : BS, 38 : up, 40 : down
        if (!((e.keyCode >= 48 && e.keyCode <= 57) || e.keyCode === 8 || e.keyCode === 38 || e.keyCode === 40)) {
            e.preventDefault();
        }
        // infoNumber 값이 1 미만일 때 경고창 출력 및 갯수 1개로 초기화
        if (infoNumber.value < 1) {
            alert('1개 이상부터 구매하실 수 있습니다.');
            infoNumber.value = x.querySelector('[rel="productQuantity"]').innerText;
            if (infoNumber.value > 1) {
                minusButton.removeAttribute('disabled');
            } else {
                minusButton.setAttribute('disabled', 'disabled');
            }
            chargeQuantity.innerText = '총 수량 ' + infoNumber.value + '개';
            infoPrice.value = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText;
            chargePrice.innerText = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText + '원';
        }
        // 값이 1 이상일 때 가격 연산 및 값이 1일때 disabled 추가
        if (infoNumber.value >= 1) {
            infoPrice.value = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText;
            minusButton.removeAttribute('disabled');
            if (parseInt(infoNumber.value) === 1) {
                minusButton.setAttribute('disabled', 'disabled');
            }
        }
        // 주문수량이 재고량보다 많을 때 경고창 출력 및 갯수 초기화
        if (parseInt(infoNumber.value) > x.querySelector('[rel="maxQuantity"]').innerText) {
            alert('주문 수량이 재고량을 초과했습니다.');
            infoNumber.value = x.querySelector('[rel="productQuantity"]').innerText;
            if (infoNumber.value > 1) {
                minusButton.removeAttribute('disabled');
            } else {
                minusButton.setAttribute('disabled', 'disabled');
            }
            infoPrice.value = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText;
            chargeQuantity.innerText = '총 수량 ' + infoNumber.value + '개';
            chargePrice.innerText = infoNumber.value * x.querySelector('[rel="eachSalePrice"]').innerText + '원';
        }
    }

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
                            alert('수정 성공');
                            window.location.reload();
                            break;

                        default :
                            alert('수정 오류');
                    }

                } else {
                    alert('상품 갯수를 다시 확인해 주세요.');
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


orderButton?.addEventListener('click', e => {
    e.preventDefault();
    let orderCheck = false;
    let index = [];
    cartItem.forEach(x => {
        if (x.querySelector('[rel="checkBox"]').checked) {
            index.push(Number(x.querySelector('[rel="cartIndex"]').innerText));
            orderCheck = true;
        }
    })
    if (!orderCheck) {
        alert('선택된 상품이 없습니다');
        return;
    }

    if (!confirm("선택된 상품을 주문하시겠습니까?")) {
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    // formData.append("index", x.querySelector('[rel="cartIndex"]').innerText);
    formData.append("cartIndex", index);
    xhr.open('POST', window.location.href);
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




