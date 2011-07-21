talend.ItemsTrash = {};
talend.ItemsTrash.ItemsTrash = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/ItemsTrash/ItemsTrash.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();