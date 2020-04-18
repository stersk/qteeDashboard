var table;
var alertBtn;
var selections = [];

$(document).ready(function() {
    //table = $('#fresh-table');
    table = $('#table');

    initTable();
})

function initTable() {
    table.bootstrapTable('destroy').bootstrapTable({
      height: 550,
      locale: 'uk-UA',
      columns: [
         {
           title: '',
           field: 'deliveryService',
           align: 'center',
           valign: 'middle',
           formatter: 'deliveryServiceFormatter',
           sortable: true
         },{
           field: 'declarationNumber',
           title: 'Номер',
           align: 'left',
           valign: 'middle',
           sortable: true,
           visible: true,
           switchable: true
         },{
            field: 'date',
            title: 'Дата',
            align: 'left',
            valign: 'middle',
            sortable: true,
            visible: true,
            switchable: true
          },{
            field: 'id',
            title: 'Id',
            align: 'left',
            valign: 'middle',
            visible: false,
            switchable: false,
            sortable: true
          }, {
            field: 'customer',
            title: 'Клієнт',
            sortable: true,
            align: 'left'
          }, {
            field: 'phone',
            title: 'Телефон',
            align: 'left',
            sortable: true,
            clickToSelect: false
          }, {
            field: 'address',
            title: 'Адреса доставки',
            sortable: true,
            align: 'left'
          }, {
            title: 'Сумма',
            field: 'sum',
            align: 'left',
            valign: 'middle',
            sortable: true
          }
        ]
    })

    var dropdownElement = $('.fixed-table-toolbar button.dropdown-toggle', table).first();
    dropdownElement.attr('data-offset', '100');
    dropdownElement.dropdown('update');
}

function responseHandler(res) {
    $.each(res.rows, function (i, row) {
      row.state = $.inArray(row.id, selections) !== -1
    })
    return res
}

function deliveryServiceFormatter(value, row, index, field) {
    return [
      '<img class="fit-picture" width="20" height="20" src="' + value + '">',
    ].join('')
}