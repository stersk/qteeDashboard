var table;
var alertBtn;
var selections = [];

$(document).ready(function () {
    //table = $('#fresh-table');
    table = $('#table');

    initTable();
    initStatChart();
})

function initTable() {
    table.bootstrapTable('destroy').bootstrapTable({
        height: 550,
        locale: 'uk-UA',
        columns: [{
                title: '',
                field: 'deliveryService',
                align: 'center',
                valign: 'middle',
                formatter: 'deliveryServiceFormatter',
                sortable: true
            }, {
                field: 'declarationNumber',
                title: 'Номер',
                align: 'left',
                valign: 'middle',
                sortable: true,
                visible: true,
                switchable: true
            }, {
                field: 'date',
                title: 'Дата',
                align: 'left',
                valign: 'middle',
                sortable: true,
                visible: true,
                switchable: true
            }, {
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

function initStatChart() {
    var chartColor = "#FFFFFF";
    var ctx = document.getElementById('shipmentDayStatChart').getContext("2d");

    var gradientStroke = ctx.createLinearGradient(500, 0, 100, 0);
    gradientStroke.addColorStop(0, '#80b6f4');
    gradientStroke.addColorStop(1, chartColor);

    var gradientFill = ctx.createLinearGradient(0, 200, 0, 50);
    gradientFill.addColorStop(0, "rgba(128, 182, 244, 0)");
    gradientFill.addColorStop(1, "rgba(255, 255, 255, 0.24)");

    var myChart = new Chart(ctx, {
             type: 'bar',
                data: {
                  labels: ["1900", "1950", "1999", "2050"],
                  datasets: [{
                      label: "Сума",
                      yAxisID: "sum",
                      type: "line",
                      borderColor: chartColor,
                      pointBorderColor: chartColor,
                      pointBackgroundColor: "#1e3d60",
                      pointHoverBackgroundColor: "#1e3d60",
                      pointHoverBorderColor: chartColor,
                      pointBorderWidth: 1,
                      pointHoverRadius: 7,
                      pointHoverBorderWidth: 2,
                      pointRadius: 5,
                      fill: true,
                      backgroundColor: gradientFill,
                      borderWidth: 2,
                      data: [408,547,675,734]
                    }, {
                      label: "Кількість",
                      yAxisID: "count",
                      type: "bar",
                      backgroundColor: "rgba(0,0,0,0.2)",
                      data: [408,547,675,734],
                    }
                  ]
                },
            options: {
                layout: {
                    padding: {
                        left: 20,
                        right: 20,
                        top: 0,
                        bottom: 0
                    }
                },
                maintainAspectRatio: false,
                tooltips: {
                    backgroundColor: '#fff',
                    titleFontColor: '#333',
                    bodyFontColor: '#666',
                    bodySpacing: 4,
                    xPadding: 12,
                    mode: "nearest",
                    intersect: 0,
                    position: "nearest"
                },
                legend: {
                    position: "bottom",
                    fillStyle: "#FFF",
                    display: false
                },
                scales: {
                    yAxes: [{
                            id:"sum",
                            position: 'left',
                            ticks: {
                                fontColor: "rgba(255,255,255,0.4)",
                                fontStyle: "bold",
                                beginAtZero: true,
                                maxTicksLimit: 5,
                                padding: 10
                            },
                            gridLines: {
                                drawTicks: true,
                                drawBorder: false,
                                display: true,
                                color: "rgba(255,255,255,0.1)",
                                zeroLineColor: "transparent"
                            }
                        },{
                          id:"count",
                          position: 'right',
                          ticks: {
                              fontColor: "rgba(255,255,255,0.4)",
                              fontStyle: "bold",
                              beginAtZero: true,
                              maxTicksLimit: 5,
                              padding: 10
                          },
                          gridLines: {
                              drawTicks: true,
                              drawBorder: false,
                              display: true,
                              color: "rgba(255,255,255,0.1)",
                              zeroLineColor: "transparent"
                          }
                      }
                    ],
                    xAxes: [{
                            gridLines: {
                                zeroLineColor: "transparent",
                                display: false,

                            },
                            ticks: {
                                padding: 10,
                                fontColor: "rgba(255,255,255,0.4)",
                                fontStyle: "bold"
                            }
                        }
                    ]
                }
            }
        });
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
