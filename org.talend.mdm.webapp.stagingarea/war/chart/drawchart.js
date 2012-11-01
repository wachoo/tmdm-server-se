$(function () {
    var chart;
    
    $(document).ready(function () {
    	
    	// Build the chart
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'chartcontainer',
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: ''
            },
            legend: {
            	enabled: false
            },
            credits: {
            	text: ''
            },
            tooltip: {
        	    pointFormat: '<b>{point.percentage}%</b>',
            	percentageDecimals: 1
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false
                    },
                    showInLegend: true
                }
            },
            series: [{
                type: 'pie'
            }]
        });
        if (window.parent && window.parent.chartReady){
        	var opt = {
        			updateData: function(waitstr, waiting, invalidStr, invalid, validStr, valid){
        				var serie = chart.series[0];
        				var data = [[waitstr, waiting],[invalidStr, invalid],[validStr, valid]];
        				serie.setData(data);
        			}
        	};
        	window.parent.chartReady(opt);
        }
    });
    
});