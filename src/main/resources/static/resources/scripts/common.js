// const Cover = {
//     show: (text) => {
//         const cover = window.document.getElementById('cover');
//         cover.querySelector('[rel="text"]').innerText = text;
//         cover.classList.add('visible');
//     },
//     hide: () => {
//         window.document.getElementById('cover').classList.remove('visible');
//     }
// };


// const selected = document.querySelectorAll('a');
// selected.addEventListener('click', () => {
//     selected.classList.add('selected');
// });


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

// if (document.location.href.match('notice')){
//     selected.item(0).classList.add('selected');
// }else if (document.location.href.match('free')){
//     selected.item(1).classList.add('selected');
// }else if (document.location.href.match('map')){
//     selected.item(2).classList.add('selected');
// }else if (document.location.href.match('shop')){
//     selected.item(3).classList.add('selected');
// }

// selected.item(1).addEventListener('click',()=>{
//     selected.item(1).classList.add('selected');
// })

// const selected = document.getElementById('selected');
// const s1 = document.getElementById('aa');
// const s2 = document.getElementById('bb');
// const s3 = document.getElementById('cc');
//
//     if (document.location.href.match('/map')) {
//         s1.classList.add('selected');
//     } else if (document.location.href.match('/shop')){
//         s2.classList.add('selected');
//     } else {
//         s3.classList.add('selected');
//     }

// const selected = document.querySelector('.--link')
// const c1 = selected.nextElementSibling;
// const c2 = selected.nextElementSibling.nextElementSibling;
// const c3 = selected.nextElementSibling.nextElementSibling.nextElementSibling;
//
// if (document.location.href.match('/map')) {
//     c1.classList.add('selected');
// } else if (document.location.href.match('/shop')){
//     c3.classList.add('selected');
// }


