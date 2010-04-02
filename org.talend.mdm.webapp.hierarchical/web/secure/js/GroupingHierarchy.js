/*
 * @include  "/com.amalto.webapp.core/web/secure/ext.ux/DWRProxy.js"
 * @include  "/com.amalto.webapp.core/web/secure/js/core.js"
 */
 
amalto.namespace("amalto.hierarchical");

amalto.hierarchical.GroupingHierarchy = function () {
	
	loadResource("/hierarchical/secure/js/HierarchicalViewLocal.js", "amalto.hierarchical.HierarchicalViewLocal" );
	
    loadResource("/hierarchical/secure/js/HierarchicalViewDisplay.js", "");
    
    loadResource("/talendmdm/secure/js/widget/FieldsWhereConditionPanel.js", "");
    
    function initUIAndData(){
		var tabPanel = amalto.core.getTabPanel();
		hierarchicalViewDisplayPanel = tabPanel.getItem('hierarchicalViewDisplay');
		
		if(hierarchicalViewDisplayPanel == undefined){
			amalto.hierarchical.HierarchicalViewLocal.init();
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
	
	function makeDirty(){
		var itempanel = amalto.core.getTabPanel().activeTab;
		if(itempanel){
			itempanel.isdirty=true;
		}
	};
	
	function cleanDirty(){
		var itempanel = amalto.core.getTabPanel().activeTab;
		if(itempanel){
			itempanel.isdirty=false;
		}
	};
	
 	return {
 		
		init: function() {openHierarchicalViewDisplayPanel();},
		makeDirty: function() {makeDirty();},
		cleanDirty: function() {cleanDirty();}
		
 	}
}();
