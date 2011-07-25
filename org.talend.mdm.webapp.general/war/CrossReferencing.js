talend.crossreferencing = {};
talend.crossreferencing.CrossReferencing = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/crossreference/Crossreference.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();