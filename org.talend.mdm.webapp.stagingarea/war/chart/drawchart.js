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
        				document.getElementById("chartcontainer").style.display = "block";
        				document.getElementById("nodata").style.display = "none";
        				var serie = chart.series[0];
        				var data = [[waitstr, waiting],[invalidStr, invalid],[validStr, valid]];
        				serie.setData(data);
        			},
        			clearChart: function(title){
        				document.getElementById("chartcontainer").style.display = "none";
        				document.getElementById("nodata").style.display = "";
        			}
        	};
        	window.parent.chartReady(opt);
        }
    });
    
});