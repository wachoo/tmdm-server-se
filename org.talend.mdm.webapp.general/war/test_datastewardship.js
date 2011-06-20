amalto = {};
amalto.datastewardship = {};
amalto.datastewardship.Datastewardship = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/org.talend.datastewardship/MainPanel.html?locale=" + language + "&j_force=true"+"&s=".concat(Math.random());
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();
