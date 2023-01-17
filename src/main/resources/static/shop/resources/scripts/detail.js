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

const reviewAnchor = window.document.getElementById("reviewAnchor");


//  품절이 아닐 경우에만 실행
if (isSoldOut === null) {
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
                            window.location.href = '/member/login'
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
        formData.append("index", window.document.querySelector('[rel="index"]').innerText);
        formData.append("salePrice", (orderForm['infoPrice'].value.replace('원', '')) / orderForm['infoNumber'].value);
        formData.append("quantity", orderForm['infoNumber'].value);
        formData.append("maxQuantity", quantity);
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

//todo 리뷰 js
//리뷰
const reviewContainer = window.document.getElementById('reviewContainer');
const adminElement = window.document.getElementById("adminFlag");

const loadReview = () => {
    reviewContainer.innerText = '';
    const url = new URL(window.location.href);
    const searchParams = url.searchParams;
    const xhr = new XMLHttpRequest();
    const aid = searchParams.get('aid');
    xhr.open('GET', `./comment?index=${aid}`)
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            const responseArray = JSON.parse(xhr.responseText);
            reviewAnchor.innerText = "리뷰("+ responseArray.length +")";
            for (const reviewObject of responseArray) {
                const itemHtml = `<div class="review-main liked mine" rel="review">
                <div class="review head">
                    <div class="speciesPicContainer">
                        <img class="speciesPic" src="/resources/images/icons8-jake-150.png" alt="#">
                    </div>
                    <div class="head">
                        <span class="writer">${reviewObject['nickname']}</span>
                        <span class="dt">${reviewObject['writtenOn']}</span>
                        <span class="action-container">
${(reviewObject['mine'] === true) || (adminElement != null && adminElement.value) ?
                    '<a href="#" class="action modify" rel="actionModify">수정</a>' : ''} ${(reviewObject['mine'] === true) || (adminElement != null && adminElement.value) ? 
                    '<a href="#" class="action delete" rel="actionDelete">삭제</a>' : ''}
</span>
                        
                    </div>
                </div>
                <div class="body">
                    <ul class="review-container" rel="reviewContainer">
                        <li class="item" rel="item">
                            <span class="item-title" rel="itemname">${reviewObject['commentTitle']}</span>
                            <div class="image-container" rel="imageContainer"></div>
                        </li>
                    </ul>
                    <div class="content">
                        <span class="text">${reviewObject['content']}</span>
                        <span class="like-content">이삼품이 도움이 되셧습니까?</span>
                        <span class="like ${reviewObject['liked'] === true ? 'visible' : ''}" rel="likeComment">
                            <a href="#" class="toggle" ${reviewObject['signed'] === true ? '' : 'prohibited'} rel="likeToggle"><i class="fa-regular fa-thumbs-up"></i></a><span
                                class="count">${reviewObject['likeCommentCount']}</span>
                        </span>
                    </div>
                </div>
            </div>`
                const domParser = new DOMParser();
                const dom = domParser.parseFromString(itemHtml, 'text/html');
                const reviewElement = dom.querySelector('[rel="review"]');
                const modifyFormElement = dom.querySelector('[rel="actionModify"]');
                const likeToggleElement = dom.querySelector('[rel="likeToggle"]');
                const likedCommentElement = dom.querySelector('[rel="likeComment"]')
                const imageContainerElement = dom.querySelector('[rel="imageContainer"]');

                if (reviewObject['imageIndexes'].length > 0) {
                    for (const imageIndex of reviewObject['imageIndexes']) {
                        const imageElement = document.createElement('img');
                        imageElement.setAttribute('alt', '');
                        imageElement.setAttribute('src', `/bbs/commentImage?index=${imageIndex}`);
                        imageElement.classList.add('image');
                        imageContainerElement.append(imageElement);
                    }
                } else {
                    imageContainerElement.remove();
                }

                dom.querySelector('[rel="actionDelete"]')?.addEventListener('click', () => {
                    if (!confirm('정말로 댓글을 삭제할까요?')) {
                        return;
                    }
                    const xhr = new XMLHttpRequest();
                    const formData = new FormData();

                    formData.append("index", reviewObject['index']);
                    formData.append("userEmail", reviewObject['userEmail']);

                    xhr.open("DELETE", "/bbs/comment");
                    xhr.onreadystatechange = () => {
                        if (xhr.readyState === XMLHttpRequest.DONE) {
                            if (xhr.status >= 200 && xhr.status < 300) {
                                const responseObject = JSON.parse(xhr.responseText);
                                switch (responseObject['result']) {
                                    case 'success' :
                                        alert('삭제가 완료되었습니다.');
                                        loadReview();
                                        break;
                                    case 'not_signed':
                                        alert("로그인해주세요.");
                                        break;
                                    case 'not_same':
                                        alert("삭제 권한이 없습니다.");
                                        break;
                                    default:
                                        alert("알 수 없는 이유로 삭제에 실패했습니다.");
                                }
                            }
                        } else {

                        }
                    };
                    xhr.send(formData);
                });


                likeToggleElement.addEventListener('click', e => {
                    e.preventDefault();
                    const xhr = new XMLHttpRequest();
                    const formData = new FormData();
                    formData.append('commentIndex', reviewObject['index']);
                    xhr.open('POST', './comment-liked');
                    xhr.onreadystatechange = () => {
                        if (xhr.readyState === XMLHttpRequest.DONE) {
                            if (xhr.status >= 200 && xhr.status < 300) {
                                const responseObject = JSON.parse(xhr.responseText);
                                switch (responseObject['result']) {
                                    case 'success':
                                        if (reviewObject['liked'] === true) {
                                            likedCommentElement.classList.remove('visible');
                                        } else if (!reviewObject['liked'] === true) {
                                            likedCommentElement.classList.add('visible');
                                        }
                                        loadReview();
                                        break;
                                    case 'not_allowed':
                                        alert('로그인이 안되어있음');
                                        break;
                                    default:
                                        alert('하트 실패');
                                }
                            } else {
                                console.log('좋아요 실행을 실패했습니다(서버)');
                            }
                        }
                    }
                    ;
                    xhr.send(formData);
                });

            modifyFormElement?.addEventListener('click', () => {
                    const writeUser = window.document.getElementById("writeUser").value;
                    const loginUser = window.document.getElementById("loginUser")?.value;

                    writeUser === loginUser
                        ? window.location.href = `./modifyReview?aid=${reviewObject['index'].value}`
                        : showDialog.show("수정 권한이 없습니다.");
                });


                reviewContainer.append(reviewElement);
            }
        }
    };
    xhr.send();
}

loadReview();
