/*
 * @include  "/com.amalto.webapp.core/web/secure/ext.ux/DWRProxy.js"
 * @include  "/com.amalto.webapp.core/web/secure/js/core.js"
 */
 
amalto.namespace("amalto.hierarchical");

amalto.hierarchical.HierarchicalView = function () {
	
    loadResource("/hierarchical/secure/js/HierarchicalViewDisplay.js", "");
    
    loadResource("/hierarchical/secure/js/MultiSelectTreePanel.js", "");
    
    function initUIAndData(){
		var tabPanel = amalto.core.getTabPanel();
		hierarchicalViewDisplayPanel = tabPanel.getItem('hierarchicalViewDisplay');
		
		if(hierarchicalViewDisplayPanel == undefined){
			hierarchicalViewDisplayPanel=new amalto.hierarchical.HierarchicalViewDisplay();			
			tabPanel.add(hierarchicalViewDisplayPanel);
		}
        
        hierarchicalViewDisplayPanel.show();
		hierarchicalViewDisplayPanel.doLayout();
		amalto.core.doLayout();
		
		hierarchicalViewDisplayPanel.initData();
		
    };
	
	function openHierarchicalViewDisplayPanel(){
		//init UI
		initUIAndData();
		
		
	};
	
 	return {
 		
		init: function() {openHierarchicalViewDisplayPanel();}
		
 	}
}();
