talend.reporting = {};
talend.reporting.Reporting = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/reporting/Reporting.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();