const map = window.document.getElementById("map");
const list = window.document.getElementById("list");
const container = window.document.getElementById("container");
const detailContainer = window.document.getElementById("detailContainer");
const reviewForm = window.document.getElementById("reviewForm");
const reviewContainer = reviewForm.querySelector('[rel="reviewContainer"]');
const likeIcon = detailContainer.querySelector('[rel="likeIcon"]');
const imageContainerElement = reviewForm.querySelector('[rel="imageContainer"]');
const modifyMenuTopElement = window.document.getElementById("modifyMenuTop");
const loginUserEmailElement = window.document.getElementById("loginUserEmail");
const adminElement = window.document.getElementById("adminFlag");

let mapObject;
let places = [];        // db 에서 list 를 가져와서 담아줄 변수.

//list 의 게시글 또는 marker 클릭 시 해당 게시글을 보여줌.
detailContainer.show = (placeObject, placeElement) => {
    // list.querySelectorAll('[rel="item"]').forEach(x => x.remove());
    detailContainer.classList.add("visible");
    detailContainer.querySelector('[rel="title"]').innerText = placeObject['title'];
    detailContainer.querySelector('[rel="placeImage"]').setAttribute("src", `/bbs/thumbnail?index=${placeObject['index']}`);
    detailContainer.querySelector('[rel="commentCounter"]').innerText = placeObject['commentCount'];
    detailContainer.querySelector('[rel="likeCounter"]').innerText = placeObject['likeCount'];
    detailContainer.querySelector('[rel="addressText"]').innerText = placeObject['address'];
    //detail 에서 주소를 누를 경우 kakao map 에서 직접 검색
    detailContainer.querySelector('[rel="addressText"]')
        .setAttribute("href", `https://map.kakao.com/link/search/${placeObject['address']}`);
    detailContainer.querySelector('[rel="descriptionText"]').innerText = placeObject['content'];

    //로그인이 안되있을 경우 좋아요를 누를 수 없도록 처리.
    if (!placeObject['signed']) {
        likeIcon.classList.add("prohibited");
    }

    // 이전 게시글의 mine 이 남아 있을 경우를 대비한. mine 삭제 조치.
    likeIcon.classList.remove("mine");

    // 로그인된 계정으로 좋아요를 눌렀을 경우.
    if (placeObject['mine']) {
        likeIcon.classList.add("mine");
    }

    // 다른 게시글을 볼때 modifyMenuTopElement 가 활성화 되어 있는 상황에 대한 예외처리
    modifyMenuTopElement.classList.remove("visible");
    modifyMenuTopElement.querySelector('[rel="articleDelete"]').classList.remove("visible");
    modifyMenuTopElement.querySelector('[rel="articleModify"]').classList.remove("visible");

    // 로그인이 안되있는 상태일때  loginUserEmailElement 의 undefined 처리.
    if ((loginUserEmailElement !== null) &&
        ((placeObject['userEmail'] === loginUserEmailElement.value) || adminElement?.value === 'true')) {
        modifyMenuTopElement.classList.add("visible");
        modifyMenuTopElement.querySelector('[rel="articleDelete"]').classList.add("visible");

        if (placeObject['userEmail'] === loginUserEmailElement.value) {
            modifyMenuTopElement.querySelector('[rel="articleModify"]').classList.add("visible");
        }

    }

    // list 에서 선택한 게시글의 index 번호 저장.
    reviewForm['articleIndex'].value = placeObject['index'];
    modifyMenuTopElement.querySelector('[rel="articleModify"]')
        .setAttribute("href", `./modify?index=${placeObject['index']}`)

    //view count up
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("index", placeObject['index']);
    xhr.open("POST", './view');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success' :
                        detailContainer.querySelector('[rel="view"]').innerText = responseObject['view'];
                        placeElement.querySelector('[rel="view"]').innerText = responseObject['view'];
                        break;
                    default:
                }
            }
        }
    };
    xhr.send(formData);

    if (!container.classList.contains("fold")) container.classList.add("fold");

    foldChangeIcon(container.classList.contains("fold"));

    //댓글 불러오기.
    loadReview(placeObject['index']);

    mapObject.setLevel(3) // 클릭 할 경우 지도 확대 레벨 변경
    mapObject.setCenter(new kakao.maps.LatLng(placeObject['latitude'], placeObject['longitude'])); // 현재 지도를 클릭한 지점을 중심으로 변경
    list.innerHTML = ''; // level 을 변경할 경우 zoom_changed event 가 발생하기 때문에 list를 한번 초기화해줍니다.
    setTimeout(() => {
        loadPlaces();
    }, 500);

}
// 현재 보고 있는 게시글 list 또는 marker 를 클릭하면 닫힘.
detailContainer.hide = () => {
    detailContainer.classList.remove("visible");
    detailContainer.querySelector('[rel="addressText"]').innerText = '';  // address 를 기준으로 하기때문에 기준점만 초기화

    foldChangeIcon(container.classList.contains("fold"));

}

// detailContainer 의 닫기 버튼을 눌렀을 경우.
detailContainer.querySelector('[rel="closeDetail"]').addEventListener('click', (e) => {
    e.preventDefault();
    detailContainer.hide();
})

//지도 활성화.
const loadMap = (lat, lng) => {
    lat ??= 33.450701;
    lng ??= 126.570667;
    mapObject = new kakao.maps.Map(map, { //지도를 생성할 때 필요한 기본 옵션
        center: new kakao.maps.LatLng(lat, lng), //지도의 중심좌표.
        level: 6 //지도의 레벨(확대, 축소 정도)
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
    list.innerHTML = '';          // html 예시 삭제.
    if (!ne || !sw) {
        const bounds = mapObject.getBounds();
        ne = bounds.getNorthEast();
        sw = bounds.getSouthWest();
    }

    const xhr = new XMLHttpRequest();
    //                                   min = 현재 페이지의 촤측하단 위도,경도            max = 현재 페이지의 우측상단 위도, 경도
    xhr.open('GET', `./place?minLat=${sw['Ma']}&minLng=${sw['La']}&maxLat=${ne['Ma']}&maxLng=${ne['La']}`);

    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            preventMapLoad = false;
            if (xhr.status >= 200 && xhr.status < 300) {
                const placeArray = JSON.parse(xhr.responseText);
                places = placeArray;
                for (const placeObject of placeArray) {
                    const position = new kakao.maps.LatLng(
                        placeObject['latitude'],
                        placeObject['longitude']);
                    const marker = new kakao.maps.Marker({
                        position: position,
                        clickable: true,
                    });

                    // 현재 지도안에 있는 값들을 등록된 순서대로 list 에 작성해서 추가합니다.
                    const placeHtml = `
                    <li class="item" rel="item">
                        <input type="hidden" name="latitude">
                        <input type="hidden" name="longitude">
                        <span class="top">
                        <img alt="" rel="image" class="image" src="/resources/images/Ninave2.jpg">
                           <span class="info">
                               <span class="name" rel="name">${placeObject['title']}</span>
                               <span class="interest-container">
                                   <span class="view-container">
                                       <i class="fa-solid fa-paw"></i>
                                       <span class="view" rel="view">${placeObject['view']}</span>
                                   </span>
                                   <span class="review-container">
                                       <i class="fa-solid fa-comment"></i>
                                       <span class="review-counter">${placeObject['commentCount']}</span>
                                   </span>
                                   <span class="like-container">
                                       <i class="like-icon icon fa-solid fa-heart"></i>
                                       <span class="like-counter">${placeObject['likeCount']}</span>
                                   </span>
                               </span>
                           </span>
                           
                        </span>
                        <span class="address bottom">${placeObject['address']}</span>
                    <\li>`;
                    const placeElement = new DOMParser()
                        .parseFromString(placeHtml, `text/html`)
                        .querySelector('[rel="item"]');

                    //현재 표시 되는 게시글읠 thumbnail
                    const imageElement = placeElement.querySelector('[rel="image"]');
                    imageElement.setAttribute('alt', '');
                    imageElement.setAttribute('src', `/bbs/thumbnail?index=${placeObject['index']}`);

                    //marker 클릭할 경우.
                    kakao.maps.event.addListener(marker, 'click', () => {
                        if (detailContainer.querySelector('[rel="addressText"]').innerText === (placeObject['address'])) {
                            detailContainer.hide();
                        } else {
                            detailContainer.show(placeObject, placeElement);
                        }

                    });
                    marker.setMap(mapObject);

                    // list 의 게시글을 클릭 했을 경우.
                    placeElement.addEventListener('click', () => {
                        if (detailContainer.querySelector('[rel="addressText"]').innerText === (placeObject['address'])) {
                            detailContainer.hide();
                        } else {
                            detailContainer.show(placeObject, placeElement);
                        }
                    });

                    list.append(placeElement);
                }
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


//이미지 찾기에서 이미지를 선택할 경우. (댓글 작성)
reviewForm['images'].addEventListener('input', () => {

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

// 로그인 상태에서 좋아요 버튼 클릭시.
likeIcon.addEventListener('click', () => {
    if (!likeIcon.classList.contains("prohibited")) {
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("articleIndex", reviewForm['articleIndex'].value);

        xhr.open("POST", "/bbs/article-liked");
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success' :
                            // DB 값은 변경 되어 있지만 현재 보이는 페이지에는 적용되어 있지 않기 때문에 임의로 변경.
                            if (likeIcon.classList.contains("mine")) {
                                likeIcon.classList.remove("mine");
                                detailContainer.querySelector("[rel='likeCounter']").innerText =
                                    Number(detailContainer.querySelector("[rel='likeCounter']").innerText) - 1;
                            } else {
                                likeIcon.classList.add("mine");
                                detailContainer.querySelector("[rel='likeCounter']").innerText =
                                    Number(detailContainer.querySelector("[rel='likeCounter']").innerText) + 1;
                            }
                            break;
                        default:
                            showDialog.show("알 수 없는 이유로 연결에 실패했습니다.");
                    }
                }
            }
        };
        xhr.send(formData);
    }
});


//리뷰 저장
reviewForm.onsubmit = e => {
    e.preventDefault()
    if (reviewForm['content'] === null) {
        showDialog.show("리뷰 내용을 입력해주세요.");
        return false;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('content', reviewForm['content'].value);
    formData.append('articleIndex', reviewForm['articleIndex'].value);
    for (let file of reviewForm['images'].files) {
        formData.append('images', file);
    }

    xhr.open('POST', '/bbs/comment');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success' :
                        loadReview(reviewForm['articleIndex'].value);
                        //작성이 완료되 었으니 작성칸을 비워줍니다.
                        reviewForm['content'].value = "";
                        imageContainerElement.innerHTML = "";
                        // DB 에 저장은 됬지만 현재 페이지에 적용되지 않아 임의로 숫자를 조정.
                        detailContainer.querySelector('[rel="commentCounter"]').innerText =
                            Number(detailContainer.querySelector('[rel="commentCounter"]').innerText) + 1;
                        list.querySelector(".review-counter").innerText =
                            Number(detailContainer.querySelector('[rel="commentCounter"]').innerText);

                        break;
                    case 'not_signed':
                        showDialog.notLogin();
                        break;
                    default:
                        showDialog.show("알 수 없는 이유로 연결에 실패했습니다.");
                }
            } else {
                showDialog.show("알 수 없는 이유로 연결에 실패했습니다.");
            }
        }
    };
    xhr.send(formData);
};

//댓글 불러오기
const loadReview = (articleIndex) => {
        reviewContainer.innerHTML = '';
        const xhr = new XMLHttpRequest();
        const formData = new FormData();

        formData.append("aid", reviewForm['articleIndex'].value);
        xhr.open("GET", `/bbs/comment?index=${articleIndex}`);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseArray = JSON.parse(xhr.responseText);
                    for (const reviewObject of responseArray) {
                        const itemHtml = `
                    <li class="item" rel="item">
                        <div class="title">
                            <span class="nickname" rel="nickname">${reviewObject['nickname']}</span>
                            <span class="time">${reviewObject['writtenOn']}</span>
                        </div> 
                        <div class="basic modifyMenu">
                        <input type="hidden" rel="commentIndex" value="${reviewObject['index']}">
                        <!-- 로그인 되지 않았을 때 value 를 사용하게 되면 오류가 뜨기 때문에 오류 처리-->
                         ${reviewObject['userEmail'] === (loginUserEmailElement === null ?
                            '' : loginUserEmailElement.value) ?
                            `<a class="basic modify-button" rel="actionModify" href="#">수정</a>` : ` `}
                         ${(reviewObject['userEmail'] === (loginUserEmailElement === null ?
                            '' : loginUserEmailElement.value)) || adminElement.value ?
                            `<a class="basic delete-button" rel="actionDelete" href="#">삭제</a>` : ` `}
                         </div>
                        <div class="image-container basic" rel="imageContainer"></div>
                        <span class="content basic" rel="contentContainer">${reviewObject['content']}</span>
                        
                        <input hidden multiple accept="image/" rel="imagesModify" name="imagesModify" type="file">
                        <div class="modify modifyMenu">
                            <a class="modify-button modify modifyText" rel="edit" href="#">수정하기</a>
                            <a class="modify modifyCancel" rel="modifyCancel" href="#">취소</a>
                        </div>
                        <div class="modify image-button-container">
                            <a href="#" class="image-select-button modify" rel="imageModifySelectButton">이미지 선택...</a>
                            <a href="#" class="image-delete-button modify" rel="imageModifyDeleteButton">이미지 삭제</a>
                        </div>
                        <div class="image-container modify" rel="imageContainerModify"></div>
                        <input class="content modify" rel="modifyContent">
                    </li>`;
                        const itemElement = new DOMParser().parseFromString(itemHtml, 'text/html').querySelector('[rel="item"]');
                        const imageContainerElement = itemElement.querySelector('[rel="imageContainer"]');

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
                        reviewContainer.append(itemElement);

                        //댓글 삭제  (댓글 작성자와 로그인 유저가 다를 경우에 오류 해결 구문)
                        if (reviewObject['userEmail'] === (loginUserEmailElement === null ?
                            '' : loginUserEmailElement.value) || adminElement?.value === 'true') {
                            itemElement.querySelector('[rel="actionDelete"]').addEventListener('click', () => {
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
                                                    loadReview(reviewForm['articleIndex'].value);
                                                    break;
                                                case 'not_signed':
                                                    showDialog.notLogin()
                                                    break;
                                                case 'not_same':
                                                    showDialog.show("삭제 권한이 없습니다.");
                                                    break;
                                                default:
                                                    showDialog.show("알 수 없는 이유로 삭제에 실패했습니다.");
                                            }
                                        }
                                    } else {

                                    }
                                };
                                xhr.send(formData);
                            });
                        }


                        const commentImageSelect = itemElement.querySelector('[rel="imagesModify"]');

                        // 수정하기 버튼을 눌렀을때 이미지가 변하지 않았다면 Controller 에서 작업을 하지 않기 위해서.
                        let imageModifyFlag = false;

                        //댓글 수정하기 눌렀을 경우의 이미지 선택 메뉴.
                        itemElement.querySelector('[rel="imageModifySelectButton"]').addEventListener('click', e => {
                            e.preventDefault();
                            commentImageSelect.click();
                        });

                        const basicElement = itemElement.querySelectorAll('.basic');
                        const modifyElementAll = itemElement.querySelectorAll('.modify');
                        const imageContainerModifyElement = itemElement.querySelector('[rel="imageContainerModify"]');

                        // 이미지 찾기에서 이미지를 선택할 경우. (댓글 수정)
                        commentImageSelect.addEventListener('input', () => {
                            imageContainerModifyElement.querySelectorAll('img.image').forEach(x => x.remove());

                            for (let file of commentImageSelect.files) {
                                const imageSrc = URL.createObjectURL(file);
                                const imgElement = document.createElement('img');
                                imgElement.classList.add('image');
                                imgElement.setAttribute('src', imageSrc);
                                imageContainerModifyElement.append(imgElement);
                            }
                            imageModifyFlag = true;
                        });

                        itemElement.querySelector('[rel="imageModifyDeleteButton"]').addEventListener('click', () => {
                            imageModifyFlag = true;
                            commentImageSelect.files = null;
                            imageContainerModifyElement.querySelectorAll('img.image').forEach(x => x.remove());
                        });

                        // 댓글 '수정하기' 버튼을 누를경우
                        itemElement.querySelector('[rel="edit"]').addEventListener('click', (e) => {
                            e.preventDefault();
                            const xhr = new XMLHttpRequest();

                            const formData = new FormData();
                            formData.append("userEmail", reviewObject['userEmail']);
                            formData.append("content", itemElement.querySelector('[rel="modifyContent"]').value);
                            formData.append("index", itemElement.querySelector('[rel="commentIndex"]').value);
                            formData.append("modifyFlag", imageModifyFlag);


                            for (let file of commentImageSelect.files) {
                                formData.append('images', file);
                            }

                            //댓글 수정
                            xhr.open("POST", '/bbs/comment-modify');
                            xhr.onreadystatechange = () => {
                                if (xhr.readyState === XMLHttpRequest.DONE) {
                                    if (xhr.status >= 200 && xhr.status < 300) {
                                        const responseObject = JSON.parse(xhr.responseText);
                                        switch (responseObject['result']) {
                                            case 'success' :
                                                loadReview(reviewForm['articleIndex'].value);
                                                break;
                                            case 'no_such_comment' :
                                                showDialog.show("게시글을 찾을 수 없습니다.");
                                                break;
                                            case 'not_signed' :
                                                showDialog.show("로그인 정보가 일치하지 않습니다.");

                                                break;
                                            case 'not_same' :
                                                showDialog.show("작성자가 아닙니다.");

                                                break;
                                            default:
                                                showDialog.show("수정에 실패했습니다.");
                                        }
                                    } else {
                                        showDialog.show("서버와 통신을 실패했습니다.");
                                    }
                                }
                            };
                            xhr.send(formData);
                        });


                        //수정하기 누를 경우 숨겨 둔 수정하기 댓글창
                        const modifyElement = itemElement.querySelector('[rel="actionModify"]');
                        modifyElement?.addEventListener('click', (e) => {
                            e.preventDefault();

                            for (let element of basicElement) {      // 원래 댓글 숨김
                                element.classList.add("modifying");
                            }
                            for (let element of modifyElementAll) {     // 수정 화면 꺼냄.
                                element.classList.add("modifying");
                            }

                            //기존 댓글 이미지 setting
                            const imageModifyContainerElement = itemElement.querySelector('[rel="imageContainerModify"]');
                            if (reviewObject['imageIndexes'].length > 0) {
                                for (const imageIndex of reviewObject['imageIndexes']) {
                                    const imageElement = document.createElement('img');
                                    imageElement.setAttribute('alt', '');
                                    imageElement.setAttribute('src', `/bbs/commentImage?index=${imageIndex}`);
                                    imageElement.classList.add('image');
                                    imageModifyContainerElement.append(imageElement);
                                }
                            }

                            // 기존 댓글을 input 창에 넣고 글의 끝에 커서 이동.
                            itemElement.querySelector('[rel="modifyContent"]').focus();
                            itemElement.querySelector('[rel="modifyContent"]').value = reviewObject['content'];

                        });

                        itemElement.querySelector('[rel="modifyCancel"]').addEventListener('click', () => {
                            //메뉴확인.
                            for (element of basicElement) {      // 원래 댓글 꺼냄
                                element.classList.remove("modifying");
                            }
                            for (element of modifyElementAll) {     // 수정 화면 숨김.
                                element.classList.remove("modifying");
                            }
                            // 수정하기에서 추가한 이미지 지움.
                            imageContainerModifyElement.querySelectorAll('img.image').forEach(x => x.remove());
                        });

                    }
                } else {
                    showDialog.show("서버와 통신을 실패했습니다.");
                }
            }
        };
        xhr.send(formData);
    }
;

// 로그인 되었을경우에 삭제와 수정이 가능하게 하기위한 조건문
if (loginUserEmailElement !== null) {
    //게시글 삭제
    modifyMenuTopElement.querySelector('[rel="articleDelete"]').addEventListener('click', () => {
        if (!confirm('정말로 게시글을 삭제할까요?')) {
            return;
        }

        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("aid", reviewForm['articleIndex'].value);

        xhr.open("DELETE", `/bbs/read`)
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success' :
                            window.location.href = `/map/read`;
                            break;
                        case 'no_such_article' :
                            showDialog.show("게시글을 찾을 수 없습니다.");
                            break;
                        case 'not_allowed' :
                            showDialog.show("로그인 정보가 일치하지 않습니다.");
                            break;
                        default:
                            showDialog.show("알 수 없는 이우로 삭제에 실패했습니다.");
                    }
                } else {
                    showDialog.show("서버와 통신을 실패했습니다.");
                }
            }
        };
        xhr.send(formData);
    });
}

const foldElement = window.document.getElementById("fold");


//fold controller
foldElement.addEventListener('click', () => {

    if (container.classList.contains("fold")) {
        container.classList.remove("fold");
        detailContainer.classList.remove("visible");
    } else {
        container.classList.add("fold");
    }
    foldChangeIcon(container.classList.contains("fold"));
});

// 산책 게시판 list 닫기
function foldChangeIcon(flag) {
    const foldIcon = foldElement.querySelector('[rel="foldIcon"]');
    if (flag && foldIcon.classList.contains("fa-greater-than")) {
        foldIcon.classList.remove("fa-greater-than");
        foldIcon.classList.add("fa-less-than");
    }
    if (!flag) {
        foldIcon.classList.remove("fa-less-than");
        foldIcon.classList.add("fa-greater-than");
    }
}

