talend.workflowtasks = {};
talend.workflowtasks.WorkflowTasks  = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/workflowtasks/WorkflowTasks.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();