$(document).ready(function () {
  $('#transactions').DataTable();
});

function updateCategory(evt) {
  $.post('/transaction/update', {id: evt.target.id, cat: evt.target.value, csrfToken: evt.target.dataset.csrf});
}

$('select').on('change', updateCategory);
