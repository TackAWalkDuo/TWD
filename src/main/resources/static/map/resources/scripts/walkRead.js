const map = window.document.getElementById("map");
const list = window.document.getElementById("list");

let mapObject;
let places = [];        // db 에서 list 를 가져와서 담아줄 변수.

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
                    // kakao.maps.event.addListener(marker, 'click', () => {
                    //     detailContainer.show(placeObject);
                    // });

                    marker.setMap(mapObject);

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
                           <img alt="" class="image" src="/resources/images/Ninave2.jpg">
                        </span>
                        <span class="address bottom">${placeObject['address']}</span>
                    <\li>`;
                    const placeElement = new DOMParser()
                        .parseFromString(placeHtml, `text/html`)
                        .querySelector('[rel="item"]');

                    placeElement.addEventListener('click', () => {
                        const latLng = new kakao.maps.LatLng(placeObject['latitude'], placeObject['longitude']);
                        mapObject.setCenter(latLng);
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





