amalto.namespace("amalto.apidoc");

amalto.apidoc.RestApiDoc = function() {

	function initUIAndData() {
		var baseCurrentUrl = stripQueryStringAndHashFromPath(this.location.toString());
		var theWindow = window.open(baseCurrentUrl + "/../api/rest/", "TalendMDMRESTAPIdocumentation");
		theWindow.focus();
	};
	
	return {
		init : function() {
			initUIAndData();
		}
	}
	
	function stripQueryStringAndHashFromPath(url) {
		  return url.split("?")[0].split("#")[0];
	}
}();