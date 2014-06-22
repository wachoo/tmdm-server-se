talend.welcome = {};
talend.welcome.Welcome = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/welcomeportal/WelcomePortal.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();