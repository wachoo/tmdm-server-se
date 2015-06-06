amalto.namespace("amalto.logviewer");

amalto.logviewer.LogViewer = function() {

	function initUIAndData() {
		var baseCurrentUrl = stripQueryStringAndHashFromPath(this.location.toString());
		var theWindow = window.open(baseCurrentUrl + "/../logviewer/logviewer.html", "Talend MDM Log Viewer");
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