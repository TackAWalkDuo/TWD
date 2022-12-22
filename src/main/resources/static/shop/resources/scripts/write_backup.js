const form = window.document.getElementById('form');


// 판매가격 자동 연산
let cal = () =>{
    let firstNum = form['cost'].value;
    let secondNum = form['discount'].value;
    if (parseInt(secondNum) === 0){
        return form['price'].value = firstNum + '원';
    }
    form['price'].value = Math.floor((firstNum * ((100 - secondNum) * 0.01)) / 10) * 10 + '원';
}


let editor;
ClassicEditor
    .create(form['content'], {
        simpleUpload : {
            uploadUrl : './image'
        }
    })
    .then(e => editor = e);

// 돌아가기 버튼 누를시
form['back'].addEventListener('click', () => window.history.length < 2 ? window.close() : window.history.back());

// "상품 이미지 선택..." 클릭시 이미지 선택창 호출
form.querySelector('[rel="imageSelectButton"]')
    .addEventListener('click', e => {
        e.preventDefault();
        form['images'].click();
    });

// 이미지 선택창 이미지 클릭 및 등록시
form['images'].addEventListener('input', () => {
    const imageContainerElement = form.querySelector('[rel="imageContainer"]');
    imageContainerElement.querySelectorAll('img.image').forEach(x => x.remove());
    for (let file of form['images'].files) {
        const imageSrc = URL.createObjectURL(file);
        const imgElement = document.createElement('img');
        imgElement.classList.add('image');
        imgElement.setAttribute('src', imageSrc);
        imageContainerElement.append(imgElement);
    }
});

// 등록시
form.onsubmit = e => {
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    // ArticleEntity 등록
    formData.append("title", form['title'].value);
    formData.append("content", form['content'].value);

    // SaleProductEntity 등록
    formData.append("quantity", form['quantity'].value);
    formData.append("cost", form['cost'].value);

    formData.append("discount", form['discount'].value);
    formData.append("price", form['price'].value.replaceAll('원',''));

    // formData.append("profit", form['profit'].value);
    formData.append("categoryText", form['category'].value); // html select 태그의 option value 값으로 지정
    formData.append("text", form['text'].value);
    formData.append("deliveryFee", form['delivery'].value);

    // image 등록
    for (let file of form['images'].files) {
        formData.append("images", file);
    }

    xhr.open('POST', './write');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success' :
                        if(confirm('상품이 등록되었습니다. \n\n확인 : 메인 페이지로 이동 \n취소 : 상품 등록 페이지로 이동')){
                            window.location.href=`./main`
                        }else {
                            window.location.href=`./write`
                        }
                        // alert('상품 등록 성공. 확인 버튼 클릭시 상품 등록 페이지로 이동합니다.');
                        break;

                    default :
                        alert('로그인 후 다시 시도해 주세요.');
                }
            } else {
                alert('알 수 없는 이유로 상품 등록에 실패했습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
}
