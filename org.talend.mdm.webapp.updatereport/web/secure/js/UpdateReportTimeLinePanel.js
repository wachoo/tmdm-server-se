var timeLinePanelHeight = false;
var jsonData4Timeline = false;
var initDate4Timeline = false;

function callback(msg){

	var obj = msg.split("@||@");
	
	if(obj.length == 2){
		jsonData4Timeline=eval('('+obj[0]+')');  
		initDate4Timeline = obj[1];
	}
		
	renderTimeline(jsonData4Timeline, initDate4Timeline);
};

function renderTimeline(jsonData, initDate){
	var tl_el = document.getElementById("tl");
    var eventSource1 = new Timeline.DefaultEventSource();
    
    var theme1 = Timeline.ClassicTheme.create();
    theme1.autoWidth = true; 
    theme1.timeline_start = new Date(Date.UTC(1950, 0, 1));
    theme1.timeline_stop  = new Date(Date.UTC(2020, 0, 1));
    

    var theme2 = Timeline.ClassicTheme.create();
    theme2.event.tape.height = 6; 
    theme2.event.track.height = theme2.event.tape.height + 10;

    var d = Timeline.DateTime.parseGregorianDateTime(initDate)
    var bandInfos = [
        Timeline.createBandInfo({
            width:          timeLinePanelHeight*0.6, 
            intervalUnit:   Timeline.DateTime.HOUR, 
            intervalPixels: 240,
            eventSource:    eventSource1,
            date:           d,          
            theme:          theme1,
            layout:         'original'  
        }),
        Timeline.createBandInfo({
            width:          timeLinePanelHeight*0.3, 
            intervalUnit:   Timeline.DateTime.DAY, 
            intervalPixels: 150,
            eventSource:    eventSource1,
            date:           d,
            theme:          theme2,
            layout:         'overview'  
        }),
        Timeline.createBandInfo({
            width:          timeLinePanelHeight*0.1, 
            intervalUnit:   Timeline.DateTime.YEAR, 
            intervalPixels: 110,
            eventSource:    eventSource1,
            date:           d,
            theme:          theme2,
            layout:         'overview',  
            syncWith:       0,
            highlight:      true
        })
    ];
    bandInfos[1].syncWith = 0;
    bandInfos[2].syncWith = 0;
    bandInfos[1].highlight = true;
    bandInfos[2].highlight = true;
        
    Timeline.create(tl_el, bandInfos, Timeline.HORIZONTAL);
    eventSource1.loadJSON(jsonData, document.location.href);
}

function showDialog(ids, key, concept, dataCluster, dataModel){
	closeTimelineBubble();
	var tabPanel = amalto.core.getTabPanel();
	var dataLogViewer=tabPanel.getItem(ids);
	if( dataLogViewer== undefined){
		
		dataLogViewer=new amalto.updatereport.DataLogViewer(
			{'ids':ids,'key':key,'concept':concept,'dataCluster':dataCluster,'dataModel':dataModel});
		tabPanel.add(dataLogViewer);							
	}
    
    dataLogViewer.show();
	dataLogViewer.doLayout();
	amalto.core.doLayout();
};