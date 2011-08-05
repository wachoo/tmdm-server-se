talend.smtp = {};
talend.smtp.SmtpAdapter = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/smtp/smtp2.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();