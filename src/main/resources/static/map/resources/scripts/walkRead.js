const map = window.document.getElementById("map");
const list = window.document.getElementById("list");
const detailContainer = window.document.getElementById("detailContainer");
const reviewForm = window.document.getElementById("reviewForm");

let mapObject;
let places = [];        // db 에서 list 를 가져와서 담아줄 변수.

detailContainer.show = (placeObject) => {
    detailContainer.classList.add("visible");
    detailContainer.querySelector('[rel="title"]').innerText = placeObject['title'];
    detailContainer.querySelector('[rel="placeImage"]').setAttribute("src", `/bbs/thumbnail?index=${placeObject['index']}`);
    detailContainer.querySelector('[rel="view"]').innerText = placeObject['view'];
    detailContainer.querySelector('[rel="commentCounter"]').innerText = placeObject['commentCount'];
    detailContainer.querySelector('[rel="likeCounter"]').innerText = placeObject['likeCount'];
    detailContainer.querySelector('[rel="addressText"]').innerText = placeObject['address'];
    detailContainer.querySelector('[rel="descriptionText"]').innerText = placeObject['content'];
    reviewForm['articleIndex'].innerText = placeObject['index'];
}
detailContainer.hide = () => {
    detailContainer.classList.remove("visible");
    detailContainer.querySelector('[rel="addressText"]').innerText = '';  // address 를 기준으로 하기때문에 기준점만 초기화
}

detailContainer.querySelector('[rel="closeDetail"]').addEventListener('click', () => {
    detailContainer.hide();
})

//지도 활성화.
const loadMap = (lat, lng) => {
    lat ??= 33.450701;
    lng ??= 126.570667;
    mapObject = new kakao.maps.Map(map, { //지도를 생성할 때 필요한 기본 옵션
        center: new kakao.maps.LatLng(lat, lng), //지도의 중심좌표.
        level: 3 //지도의 레벨(확대, 축소 정도)
    }); //지도 생성 및 객체 리턴
    kakao.maps.event.addListener(mapObject, 'dragend', () => {
        loadPlaces();
    });
    kakao.maps.event.addListener(mapObject, 'zoom_changed', () => {
        loadPlaces();
    });
    loadPlaces();
};

// 현재 지도 내에서 표시할 수 있는 좌표가 있는 게시글을 list 에 표시
const loadPlaces = (ne, sw) => {
    if (!ne || !sw) {
        const bounds = mapObject.getBounds();
        ne = bounds.getNorthEast();
        sw = bounds.getSouthWest();
    }
    list.innerHTML = '';
    const xhr = new XMLHttpRequest();
    //                                   min = 현재 페이지의 촤측하단 위도,경도            max = 현재 페이지의 우측상단 위도, 경도
    xhr.open('GET', `./place?minLat=${sw['Ma']}&minLng=${sw['La']}&maxLat=${ne['Ma']}&maxLng=${ne['La']}`);

    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const placeArray = JSON.parse(xhr.responseText);
                console.log("check js")
                places = placeArray;
                for (const placeObject of placeArray) {
                    const position = new kakao.maps.LatLng(
                        placeObject['latitude'],
                        placeObject['longitude']);
                    const marker = new kakao.maps.Marker({
                        position: position,
                        clickable: true,
                    });

                    const placeHtml = `
                    <li class="item" rel="item">
                        <input type="hidden" name="latitude">
                        <input type="hidden" name="longitude">
                        <span class="top">
                           <span class="info">
                               <span class="name" rel="name">${placeObject['title']}</span>
                               <span class="interest-container">
                                   <span class="view-container">
                                       <i class="fa-solid fa-eye"></i>
                                       <span class="view">${placeObject['view']}</span>
                                   </span>
                                   <span class="review-container">
                                       <i class="fa-solid fa-comment"></i>
                                       <span class="review-counter">${placeObject['commentCount']}</span>
                                   </span>
                                   <span class="like-container">
                                       <i class="icon fa-solid fa-heart"></i>
                                       <span class="like-counter">${placeObject['likeCount']}</span>
                                   </span>
                               </span>
                           </span>
                           <img alt="" rel="image" class="image" src="/resources/images/Ninave2.jpg">
                        </span>
                        <span class="address bottom">${placeObject['address']}</span>
                    <\li>`;
                    const placeElement = new DOMParser()
                        .parseFromString(placeHtml, `text/html`)
                        .querySelector('[rel="item"]');
                    const imageElement = placeElement.querySelector('[rel="image"]');

                    imageElement.setAttribute('alt', '');
                    imageElement.setAttribute('src', `/bbs/thumbnail?index=${placeObject['index']}`);

                    const latLng = new kakao.maps.LatLng(placeObject['latitude'], placeObject['longitude']);
                    kakao.maps.event.addListener(marker, 'click', () => {
                        mapObject.setCenter(latLng);
                        loadPlaces();
                        if (detailContainer.querySelector('[rel="addressText"]').innerText === (placeObject['address'])) {
                            detailContainer.hide();
                        } else {
                            detailContainer.show(placeObject);
                        }

                    });
                    marker.setMap(mapObject);

                    placeElement.addEventListener('click', () => {
                        mapObject.setCenter(latLng);
                        loadPlaces();
                        if (detailContainer.querySelector('[rel="addressText"]').innerText === (placeObject['address'])) {
                            detailContainer.hide();
                        } else {
                            detailContainer.show(placeObject);
                        }

                    });

                    list.append(placeElement);
                }
            } else {
            }
        }
    };
    xhr.send();
};




//ip를 통해서 현재 위치 확인 권한 설정후 현재 위치를 중심으로 지도 표시
navigator.geolocation.getCurrentPosition(e => {
    loadMap(e['coords']['latitude'], e['coords']['longitude']);
}, () => {
    loadMap();
});

//이미지 찾기 버튼을 누를 경우 파일 검색창 활성화.
reviewForm.querySelector('[rel="imageSelectButton"]').addEventListener('click', e => {
    e.preventDefault();
    reviewForm['images'].click();
});


//이미지 찾기에서 이미지를 선택할 경우.
reviewForm['images'].addEventListener('input', () => {
    const imageContainerElement = reviewForm.querySelector('[rel="imageContainer"]');
    imageContainerElement.querySelectorAll('img.image').forEach(x => x.remove());
    if (reviewForm['images'].files.length > 0) {
        reviewForm.querySelector('[rel="noImage"]').classList.add('hidden')
    } else {
        reviewForm.querySelector('[rel="noImage"]').classList.remove('hidden')
    }
    for (let file of reviewForm['images'].files) {
        const imageSrc = URL.createObjectURL(file);
        const imgElement = document.createElement('img');
        imgElement.classList.add('image');
        imgElement.setAttribute('src', imageSrc);
        imageContainerElement.append(imgElement);
    }
});

//리뷰 저장
reviewForm.onsubmit = e => {
    e.preventDefault()
    if(reviewForm['content'] === null) {
        alert("리뷰 내용을 입력해주세요.")
        return false;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('content', reviewForm['content'].value);
    formData.append('articleIndex', reviewForm['articleIndex'].value);
    for (let file of reviewForm['images'].files) {
        formData.append('images', file);
    }

}























