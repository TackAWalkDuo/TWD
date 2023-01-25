// 현재 보고 있는 쇼핑 목록의 a태그 색깔 활성화
const aList = window.document.getElementById("list-selected").getElementsByTagName("li");
const listSelected = document.querySelectorAll('.a-select');

for (let i = 0; i < aList.length; i++) {
    const list = aList[i].className;
    if (url.match(list)) {
        listSelected.item(i).classList.add('selected');
        break;
    }
}