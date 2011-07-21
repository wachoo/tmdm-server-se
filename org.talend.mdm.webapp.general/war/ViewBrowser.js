talend.viewbrowser = {};
talend.viewbrowser.ViewBrowser = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/viewbrowser/ViewBrowser.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();