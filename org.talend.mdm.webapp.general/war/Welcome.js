talend.welcome = {};
talend.welcome.Welcome = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/welcome/Welcome.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();