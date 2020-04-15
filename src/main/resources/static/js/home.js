var $table = $('#fresh-table');

$(function () {
  $table.bootstrapTable({
    classes: 'table table-hover table-striped',
    toolbar: '.toolbar',

    search: true,
    showRefresh: true,
    showToggle: true,
    showColumns: true,
    pagination: true,
    striped: true,
    sortable: true,
    pageSize: 8,
    pageList: [8, 10, 25, 50, 100],

    formatShowingRows: function (pageFrom, pageTo, totalRows) {
      return ''
    },
    formatRecordsPerPage: function (pageNumber) {
      return pageNumber + ' rows visible'
    }
  })

  $alertBtn.click(function () {
    alert('You pressed on Alert')
  })
});