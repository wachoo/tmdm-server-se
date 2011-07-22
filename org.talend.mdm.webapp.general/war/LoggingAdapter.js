talend.loggingadapter = {};
talend.loggingadapter.LoggingAdapter = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/loggingadapter/LoggingAdapter.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();