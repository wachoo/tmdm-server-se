talend.updatereport = {};
talend.updatereport.UpdateReport = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/updatereport/UpdateReport.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();