talend.welcome = {};
talend.welcome.Welcome = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "test2.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();