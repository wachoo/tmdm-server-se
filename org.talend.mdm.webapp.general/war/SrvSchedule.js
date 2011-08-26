talend.SrvSchedule = {};
talend.SrvSchedule.SrvSchedule = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/SrvSchedule/SrvSchedule.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();