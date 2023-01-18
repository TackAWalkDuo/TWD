
// 현재 페이지의 a태그 색깔 활성화
const bbsList = window.document.getElementById("selected")?.getElementsByTagName("li");
const selected = document.querySelectorAll('.--link');
const url = document.location.href;

if (bbsList) {
    for (let i = 0; i < bbsList.length; i++) {
        const liClassList = bbsList[i].className.split(' ');
        if (url.match(liClassList[1])) {
            selected.item(i).classList.add('selected');
            break;
        }
    }
}

const showDialog = {
    getElement: () => window.document.querySelector('[rel="dialog"]'),
    show: (text) => {
        const dialog = showDialog.getElement();
        dialog.querySelector('.text').innerText = text;
        dialog.classList.add('visible');
        // dialog.querySelector("#cancel").addEventListener("click", () => {
        //     dialog.classList.remove('visible');
        // });
        dialog.querySelector("#ok").addEventListener("click", () => {
            dialog.classList.remove('visible');
        });
    },
    notLogin: () =>{
        const dialog = showDialog.getElement();
        dialog.querySelector('.text').innerText = "권한이 없습니다.";
        dialog.classList.add('visible');
        dialog.querySelector("#ok").addEventListener("click", () => {
            dialog.classList.remove('visible');
            window.location.href = `/member/login`;
        });
    }
};
