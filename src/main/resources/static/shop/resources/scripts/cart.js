const form = window.document.getElementById('form');
// const checkAll = window.document.getElementsByName('checkAll');
const modifyButton = window.document.getElementById('modifyButton');
const modifyContainer = window.document.getElementById('modifyContainer');
const modifyFrame = window.document.getElementById('modifyFrame');
const cancelButton = window.document.querySelector('[rel="cancelButton"]');
const modifyCancelButton = window.document.querySelector('[rel="modifyCancelButton"]');

// 전체 선택 체크시 모든 항목 체크
selectAll = checkAll =>  {
    const checkboxes
        = document.getElementsByName('checkBox');

    checkboxes.forEach((checkbox) => {
        checkbox.checked = checkAll.checked;
    })
}

modifyButton.addEventListener('click', e => {
    e.preventDefault();
    modifyContainer.classList.add('visible');
});

modifyCancelButton.addEventListener('click', e => {
    e.preventDefault();
    modifyContainer.classList.remove('visible');
});

cancelButton.addEventListener('click', e => {
    e.preventDefault();
    modifyContainer.classList.remove('visible');
})


