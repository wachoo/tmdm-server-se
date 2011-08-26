talend.SynchronizationAction = {};
talend.SynchronizationAction.SynchronizationAction = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/SynchronizationAction/SynchronizationAction.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();