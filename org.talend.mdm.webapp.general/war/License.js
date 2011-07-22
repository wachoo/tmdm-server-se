talend.license = {};
talend.license.License = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "licensemanager/LicenseManager.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();