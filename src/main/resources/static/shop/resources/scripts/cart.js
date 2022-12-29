const form = window.document.getElementById('form');
// const checkAll = window.document.getElementsByName('checkAll');

// 전체 선택 체크시 모든 항목 체크
selectAll = checkAll =>  {
    const checkboxes
        = document.getElementsByName('checkBox');

    checkboxes.forEach((checkbox) => {
        checkbox.checked = checkAll.checked;
    })
}

