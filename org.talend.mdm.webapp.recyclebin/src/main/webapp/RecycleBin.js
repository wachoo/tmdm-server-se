talend.recyclebin = {};
talend.recyclebin.RecycleBin = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/recyclebin/RecycleBin.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();