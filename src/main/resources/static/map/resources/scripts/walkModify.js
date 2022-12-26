const map = window.document.getElementById("map");
const walkArticle = window.document.getElementById("walkArticle");
const imageContainerElement = walkArticle.querySelector('[rel="imageContainer"]');

let mapObject;

// 클릭한 위치를 표시할 마커입니다
let marker = new kakao.maps.Marker;
// 클릭한 위치에 대한 주소를 표시할 인포윈도우입니다;
let infoWindow = new kakao.maps.InfoWindow({zindex: 10});
// 주소-좌표 변환 객체를 생성합니다
let geocoder = new kakao.maps.services.Geocoder();
// 좌표에 따른 주소를 담을 변수.
let detailAddr

let initializationMarker = true;            // 수정하기 페이지 처음들어 왔을 때 마커 표시
let initializationThumbnail = true;         // 수정하기 페이지 처음들어 왔을 때 썸네일 표시

// 수정하기 버튼을 눌렀을때 이미지가 변하지 않았다면 Controller 에서 작업을 하지 않기 위해서.
let imageModifyFlag = false;

if (initializationThumbnail) {
    initializationThumbnail = false;
    const url = new URL(window.location.href);
    const searchParams = url.searchParams;
    walkArticle.querySelector('[rel="thumbnail"]')
        .setAttribute("src", `/bbs/thumbnail?index=${searchParams.get("index")}`);

}

//지도 활성화.
const loadMap = (lat, lng) => {
    lat ??= walkArticle.querySelector('[rel="lat"]').value;
    lng ??= walkArticle.querySelector('[rel="lng"]').value;

    mapObject = new kakao.maps.Map(map, { //지도를 생성할 때 필요한 기본 옵션
        center: new kakao.maps.LatLng(lat, lng), //지도의 중심좌표.
        level: 3 //지도의 레벨(확대, 축소 정도)
    }); //지도 생성 및 객체 리턴

    if(initializationMarker){
        initializationMarker = false;
        marker.setPosition(new kakao.maps
            .LatLng(walkArticle.querySelector('[rel="lat"]').value,
                walkArticle.querySelector('[rel="lng"]').value));         // 생성 할 marker 위치 지정
        marker.setMap(mapObject);           // 생성 한 marker 지도에 setting
        marker.setDraggable(true);          // marker 드래그 가능.
    }

    // 지도 클릭시 클릭지점에 maker 생성.
    kakao.maps.event.addListener(mapObject, 'click', function (mouseEvent) {
        let latLng = mouseEvent.latLng;     // 마우스 클릭지점의  위도 및 경도

        // marker 위쪽에 주소 표시
        searchDetailAddrFromCoords(latLng, function (result, status) {
            if (status === kakao.maps.services.Status.OK) {

                walkArticle['address'].value = result[0].address.address_name;   // 검색한 주소 정보.
                detailAddr = '<div>지번 주소 : ' + result[0].address.address_name + '</div>';
                let content = '<div class="bAddr" id="bAddr">' + detailAddr + '</div>';
                // 인포윈도우에 클릭한 위치에 대한 법정동 상세 주소정보를 표시합니다
                infoWindow.setContent(content);
                infoWindow.open(mapObject, marker);

                // 주소창의 style
                window.document.getElementById("bAddr").parentElement.parentElement.style.border = "none";
                window.document.getElementById("bAddr").parentElement.parentElement.style.backgroundColor = "rgba(234,234,234, 80%)";
            }
        });

        marker.setPosition(latLng);         // 생성 할 marker 위치 지정
        marker.setMap(mapObject);           // 생성 한 marker 지도에 setting
        marker.setDraggable(true);          // marker 드래그 가능.

        walkArticle['lat'].value = latLng.getLat();   // 위도
        walkArticle['lng'].value = latLng.getLng();   // 경도


        mapObject.setCenter(latLng);         // 마커의 위치가 지도의 중심이 될수 있도록 지도이동.

        console.log(latLng);

    });

    // marker 를 드래그 했을 경우 끝나는 지점에서의 위도 경도 지정.
    kakao.maps.event.addListener(marker, 'dragend', () => {
        // marker 위쪽에 주소 표시
        searchDetailAddrFromCoords(marker.getPosition(), function (result, status) {
            if (status === kakao.maps.services.Status.OK) {

                // let detailAddr = !!result[0].road_address ? '<div>도로명주소 : ' + result[0].road_address.address_name + '</div>' : '';
                walkArticle['address'].value = result[0].address.address_name;   // 검색한 주소 정보.
                detailAddr = '<div>지번 주소 : ' + result[0].address.address_name + '</div>';
                let content = '<div class="bAddr" id="bAddr">' + detailAddr + '</div>';
                // 인포윈도우에 클릭한 위치에 대한 법정동 상세 주소정보를 표시합니다
                infoWindow.setContent(content);
                infoWindow.open(mapObject, marker);

                // 주소창의 style
                window.document.getElementById("bAddr").parentElement.parentElement.style.border = "none";
                window.document.getElementById("bAddr").parentElement.parentElement.style.backgroundColor = "rgba(234,234,234, 80%)";
            }
        });

        walkArticle['lat'].value = marker.getPosition().getLat();   // 위도
        walkArticle['lng'].value = marker.getPosition().getLng();   // 경도

        mapObject.setCenter(marker.getPosition());      // 마커의 위치가 지도의 중심이 될수 있도록 지도이동.

        console.log(marker.getPosition());
    });

}

// 좌표로 법정동 상세 주소 정보를 요청합니다
function searchDetailAddrFromCoords(coords, callback) {
    geocoder.coord2Address(coords.getLng(), coords.getLat(), callback);
}


//ip를 통해서 현재 위치 확인 권한 설정후 현재 위치를 중심으로 지도 표시
navigator.geolocation.getCurrentPosition(() => {
    loadMap(walkArticle.querySelector('[rel="lat"]').value,
        walkArticle.querySelector('[rel="lng"]').value);
}, () => {
    loadMap();
});

//이미지 찾기 버튼을 누를 경우 파일 검색창 활성화.
walkArticle.querySelector('[rel="imageSelectButton"]').addEventListener('click', e => {
    e.preventDefault();
    walkArticle['images'].click();
});


//이미지 찾기에서 이미지를 선택할 경우.
walkArticle['images'].addEventListener('input', () => {
    // const imageContainerElement = walkArticle.querySelector('[rel="imageContainer"]');
    imageContainerElement.querySelectorAll('img.image').forEach(x => x.remove());
    if (walkArticle['images'].files.length > 0) {
        walkArticle.querySelector('[rel="noImage"]').classList.add('hidden')
    } else {
        walkArticle.querySelector('[rel="noImage"]').classList.remove('hidden')
    }
    for (let file of walkArticle['images'].files) {
        const imageSrc = URL.createObjectURL(file);
        const imgElement = document.createElement('img');
        imgElement.classList.add('image');
        imgElement.setAttribute('src', imageSrc);
        imageContainerElement.append(imgElement);
    }
    console.log(walkArticle['images'].files);
    imageModifyFlag = true;
});

walkArticle.querySelector('[rel="imageDeleteButton"]').addEventListener('click', ()=> {
    walkArticle['images'].files = null;
    imageContainerElement.querySelectorAll('img.image').forEach(x => x.remove());
    imageModifyFlag = true;
})


// walk 게시글 수정. 버튼.
walkArticle.onsubmit = e => {
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();

    if (walkArticle['lat'].value === null || walkArticle['address'].value === null) {
        alert("추천할 좌표를 지도에서 선택해주세요.");
        return false;
    }
    if (walkArticle['place_title'].value === '') {
        alert("추천할 장소를 작성해주세요.");
        return false;
    }
    if (walkArticle['content'].value === '') {
        alert("어떤 이유로 추천하는 작성해주세요.");
        return false;
    }

    //ArticleEntity 로 받을 정보
    formData.append("title", walkArticle['place_title'].value);
    formData.append("content", walkArticle['content'].value);

    // Location 으로 받을 정보
    formData.append("latitude", walkArticle['lat'].value);
    formData.append("longitude", walkArticle['lng'].value);
    formData.append("address", walkArticle['address'].value);

    //MultipartFile 받을 정보.
    for (let file of walkArticle['images'].files) {
        formData.append('images', file);
    }

    xhr.open('POST', './walk-write');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        window.location.href = './walk-read';
                        break;
                    case 'not_signed':
                        alert("로그인 해주세요.");
                        window.location.href = '../../../member/login';
                        break;
                    default:
                        alert("응 실패");
                }
            } else {
                alert("응 연결 실패~");
            }
        }
    };
    xhr.send(formData);
};













