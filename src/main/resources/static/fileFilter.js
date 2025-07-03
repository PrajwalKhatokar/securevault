// fileFilter.js
document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('fileSearchInput');
    const table = document.getElementById('filesTable');
    const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');

    searchInput.addEventListener('keyup', function () {
        const filter = searchInput.value.toLowerCase();

        for (let i = 0; i < rows.length; i++) {
            const fileNameCell = rows[i].getElementsByTagName('td')[0];
            if (fileNameCell) {
                const fileNameText = fileNameCell.textContent || fileNameCell.innerText;
                if (fileNameText.toLowerCase().indexOf(filter) > -1) {
                    rows[i].style.display = '';
                } else {
                    rows[i].style.display = 'none';
                }
            }
        }
    });
});
