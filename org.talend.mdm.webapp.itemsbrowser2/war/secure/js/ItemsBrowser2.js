amalto.namespace("amalto.itemsbrowser2");

amalto.itemsbrowser2.ItemsBrowser2 = function() {

	var itemsBrowser2Panel;

	function initUIAndData() {
		// init UI
		var tabPanel = amalto.core.getTabPanel();
		itemsBrowser2Panel = tabPanel.getItem('itemsBrowser2Panel');

		if (itemsBrowser2Panel == undefined) {

			itemsBrowser2Panel = new Ext.Panel({
				id : "itemsBrowser2Panel",
				title : "Browse Records v4",
				layout : "fit",
				closable : true,
				html : '<div id="talend_itemsbrowser2_ItemsBrowser2"></div>'

			});

			
			
			tabPanel.add(itemsBrowser2Panel);
			
			itemsBrowser2Panel.show();
            itemsBrowser2Panel.doLayout();
            amalto.core.doLayout();
			
			org_talend_mdm_webapp_itemsbrowser2_InBoundService_renderUI();
				
		}else{
			
			itemsBrowser2Panel.show();
            itemsBrowser2Panel.doLayout();
            amalto.core.doLayout();
		}

		
	};


	function getCurrentDataCluster() {
		var cluster = "Unknown";
		DWREngine.setAsync(false);
		ItemsBrowser2Interface.getCluster(function(result) {
					cluster = result;
				});
	    DWREngine.setAsync(true); 
		return cluster;
	}

	return {

		init : function() {
			initUIAndData();
		},
		in_getCurrentDataCluster : function() {
			return getCurrentDataCluster();
		}
	}
}();
