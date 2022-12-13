const form = window.document.getElementById('form');
let editor;
ClassicEditor
    .create(form['content'])
    .then(e => editor = e);

form['back'].addEventListener('click', () => window.history.length < 2 ? window.close() : window.history.back());

// "상품 이미지 선택..." 클릭시 이미지 선택창 호출
form.querySelector('[rel="imageSelectButton"]')
    .addEventListener('click', e => {
        e.preventDefault();
        form['images'].click();
    });

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

form.onsubmit = e => {
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    // ArticleEntity
    formData.append("title", form['title'].value);
    formData.append("content", form['content'].value);
    // SaleProductEntity
    formData.append("quantity", form['quantity'].value);
    // formData.append("cost", form['cost'].value);
    // formData.append("discount", form['discount'].value);
    formData.append("price", form['price'].value);
    // formData.append("profit", form['profit'].value);
    formData.append("categoryIndex", form['category'].value);
    // image
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
                        alert('굳 잡');
                        break;

                    default :
                        alert('돌아가');
                }
            } else {
                alert('연결 실패');
            }
        }
    };
    xhr.send(formData);
}
