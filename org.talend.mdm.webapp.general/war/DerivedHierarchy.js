talend.ehierarchical = {};
talend.ehierarchical.DerivedHierarchy = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/hierarchy/hierarchy.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();