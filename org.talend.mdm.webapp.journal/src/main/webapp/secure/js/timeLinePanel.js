var timeLinePanelHeight = false;
var jsonData4Timeline = false;
var initDate4Timeline = false;
var lastLoadTime;
var currentTime;
var startTime;
var endTime;
var tempTime;
var oldTempTime;
var eventSource1;

var timeOutId = null;
var startIndex = 0;
var eventCache;
var searchStart;
var timelinePanelActive = false;
var pageSize;
var configStr;

function journalCallback(msg){

	startIndex = searchStart;
	eventCache = [];
	eventCache[startIndex] = true;
	
	var obj = msg.split("@||@");
	
	
	if(obj[2] == "true"){
		jsonData4Timeline=eval('('+obj[0]+')');  
	}else{
		jsonData4Timeline = false;
	}
	
	initDate4Timeline = obj[1];
	renderTimeline(jsonData4Timeline, initDate4Timeline);
};

function renderTimeline(jsonData, initDate){
	var tl_el = document.getElementById("tl");
	
	if(tl_el == null)
		return;
	if(!timelinePanelActive)
	    return;
    eventSource1 = new Timeline.DefaultEventSource();
    
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
        
    var timeLine = Timeline.create(tl_el, bandInfos, Timeline.HORIZONTAL);    
    eventSource1.loadJSON(jsonData, document.location.href);
    
    timeLine.getBand(1).addOnScrollListener(function(band) {    	
		startTime = band.getMinVisibleDate();
		endTime = band.getMaxVisibleDate();
	    tempTime = band.getCenterVisibleDate();
	    if (tempTime && oldTempTime){
	    	var start = null;
	    	if (tempTime.getTime() - oldTempTime.getTime() > 0){
	    		start = parseInt(startIndex) + parseInt(pageSize);
	    	} else if (tempTime.getTime() - oldTempTime.getTime() < 0){
	    		start = parseInt(startIndex) - parseInt(pageSize);
	    	}
	    	if (start != null && start >= 0){
	    		if (timeOutId != null){
					window.clearTimeout(timeOutId);
	    		}
	    		timeOutId = setTimeout('loadDate(' + start + ',' + pageSize + ')',200)
	    	}
	    }
	    oldTempTime = tempTime;
	    
    });
}

function journalLoadDateCallback(result){
	var obj = result.split("@||@");	
	
	if(obj[2] == "true"){
		result=eval('('+obj[0]+')');  
	}else{
		result = false;
	}
	if (!eventCache[startIndex]){
		eventSource1.loadJSON(result, document.location.href);
		eventCache[startIndex] = true;
	}
}

Date.prototype.pattern=function(fmt) {        
    var o = {        
    "M+" : this.getMonth()+1,      
    "d+" : this.getDate(),
    "h+" : this.getHours() == 0 ? 12 : this.getHours(),
    "H+" : this.getHours(),        
    "m+" : this.getMinutes(),     
    "s+" : this.getSeconds(),       
    "q+" : Math.floor((this.getMonth()+3)/3),     
    "S" : this.getMilliseconds()    
    };        
    var week = {        
    "0" : "\u65e5",        
    "1" : "\u4e00",        
    "2" : "\u4e8c",        
    "3" : "\u4e09",        
    "4" : "\u56db",        
    "5" : "\u4e94",        
    "6" : "\u516d"       
    };        
    if(/(y+)/.test(fmt)){        
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));        
    }        
    if(/(E+)/.test(fmt)){        
        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "\u661f\u671f" : "\u5468") : "")+week[this.getDay()+""]);        
    }        
    for(var k in o){        
        if(new RegExp("("+ k +")").test(fmt)){        
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));        
        }        
    }        
    return fmt;        
}  