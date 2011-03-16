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
				html : '<div id="talend_itemsbrowser2_ItemsBrowser2" style="height: 100%; overflow: auto;"></div>'

			});

			tabPanel.add(itemsBrowser2Panel);

			itemsBrowser2Panel.show();
			itemsBrowser2Panel.doLayout();
			amalto.core.doLayout();

			org_talend_mdm_webapp_itemsbrowser2_InBoundService_renderUI();

		} else {

			itemsBrowser2Panel.show();
			itemsBrowser2Panel.doLayout();
			amalto.core.doLayout();
		}

	};

	function getCurrentLanguage() {
		return language;
	};

	function openItemBrowser(ids, conceptName) {
		var isdArray = ids.split(".");
		amalto.itemsbrowser.ItemsBrowser.editItemDetails(isdArray, conceptName,
				function() {
				});
	};

	function renderFormWindow(itemPK2, dataObject, isDuplicate, refreshCB, formWindow) {
		var ids = itemPK2.split(".");
		amalto.itemsbrowser.ItemsBrowser.renderFormWindow(ids, dataObject, isDuplicate, refreshCB, formWindow); 
	};
	
	return {

		init : function() {
			initUIAndData();
		},
		getLanguage : function() {
			return getCurrentLanguage();
		},
		openItemBrowser : function(ids, conceptName) {
			openItemBrowser(ids, conceptName);
		},
		renderFormWindow : function(itemPK2, dataObject, isDuplicate, refreshCB, formWindow) {
			renderFormWindow(itemPK2, dataObject, isDuplicate, refreshCB, formWindow);
		}
	}
}();
