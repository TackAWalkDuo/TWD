const map = window.document.getElementById("map");

let mapObject;

//지도 활성화.
const loadMap = (lat, lng) => {
    lat ??= 33.450701;
    lng ??= 126.570667;
    mapObject = new kakao.maps.Map(map, { //지도를 생성할 때 필요한 기본 옵션
        center: new kakao.maps.LatLng(lat, lng), //지도의 중심좌표.
        level: 3 //지도의 레벨(확대, 축소 정도)
    }); //지도 생성 및 객체 리턴
};


//ip를 통해서 현재 위치 확인 권한 설정후 현재 위치를 중심으로 지도 표시
navigator.geolocation.getCurrentPosition(e => {
    loadMap(e['coords']['latitude'], e['coords']['longitude']);
}, () => {
    loadMap();
});





