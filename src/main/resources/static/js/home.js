var table;
var alertBtn;
var selections = [];

$(document).ready(function() {
    //table = $('#fresh-table');
    table = $('#table');
    alertBtn = $('#alertBtn');

    alertBtn.click(function () {
        alert('You pressed on Alert')
    });

    initTable();
})

function initTable() {
    table.bootstrapTable('destroy').bootstrapTable({
      height: 550,
      columns: [
          [{
            field: 'state',
            checkbox: true,
            rowspan: 2,
            align: 'center',
            valign: 'middle'
          }, {
            title: 'Item ID',
            field: 'id',
            rowspan: 2,
            align: 'center',
            valign: 'middle',
            sortable: true
          }, {
            title: 'Item Detail',
            colspan: 3,
            align: 'center'
          }],
          [{
            field: 'name',
            title: 'Item Name',
            sortable: true,
            align: 'center'
          }, {
            field: 'price',
            title: 'Item Price',
            sortable: true,
            align: 'center'
          }, {
            field: 'operate',
            title: 'Item Operate',
            align: 'center',
            clickToSelect: false,
            events: window.operateEvents
          }]
        ]
    })
}

function responseHandler(res) {
    $.each(res.rows, function (i, row) {
      row.state = $.inArray(row.id, selections) !== -1
    })
    return res
}
