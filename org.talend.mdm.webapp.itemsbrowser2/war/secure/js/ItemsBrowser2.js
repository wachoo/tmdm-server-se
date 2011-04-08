amalto.namespace("amalto.itemsbrowser2");

amalto.itemsbrowser2.ItemsBrowser2 = function() {
	
	var TITLE_BROWSER_PANEL =    {
        'fr' : 'Accès aux données',
        'en' : 'Browse Records'
    };
	
	var MSG_RENDERER_ERROR = {
        'fr' : 'ItemsBrowser colorer erreurs',
        'en' : 'ItemsBrowser rendering errors'
	};

	var itemsBrowser2Panel;
	
	var resizeViewPort = function(component , adjWidth, adjHeight, rawWidth, rawHeight){
		if (window.org_talend_mdm_webapp_itemsbrowser2_client_ItemsView_onResizeViewPort != undefined){
			window.org_talend_mdm_webapp_itemsbrowser2_client_ItemsView_onResizeViewPort();
		}
	};
	

	function initUIAndData() {
		ItemsBrowser2Interface.isAvailable(function(flag){
			
			if(flag){
				// init UI
                var tabPanel = amalto.core.getTabPanel();
                itemsBrowser2Panel = tabPanel.getItem('itemsBrowser2Panel');
        
                if (itemsBrowser2Panel == undefined) {
        
                    itemsBrowser2Panel = new Ext.Panel({
                        id : "itemsBrowser2Panel",
                        title : TITLE_BROWSER_PANEL[language],
                        layout : "fit",
                        closable : true,
                        html : '<div id="talend_itemsbrowser2_ItemsBrowser2" class="itemsbrowser2"></div>'
        
                    });

                    tabPanel.add(itemsBrowser2Panel);
        
                    itemsBrowser2Panel.show();
                    itemsBrowser2Panel.doLayout();
                    amalto.core.doLayout();
                    if (window.org_talend_mdm_webapp_itemsbrowser2_InBoundService_renderUI){
                    	window.org_talend_mdm_webapp_itemsbrowser2_InBoundService_renderUI();
                    } else {
                    	window.alert(MSG_RENDERER_ERROR[language]);
                    }
                    tabPanel.un("resize", resizeViewPort);
                    tabPanel.on("resize", resizeViewPort);
                } else {
        
                    itemsBrowser2Panel.show();
                    itemsBrowser2Panel.doLayout();
                    amalto.core.doLayout();
                }
			}
			
		});
		
	};

	function getCurrentLanguage() {
		return language;
	};

	function openItemBrowser(ids, conceptName, refreshCB) {
		var isdArray;
		if (ids != null && ids != "")			
			isdArray = ids.split(".");
		amalto.itemsbrowser.ItemsBrowser.editItemDetails(isdArray, conceptName,	refreshCB);
	};

	function renderFormWindow(itemPK2, dataObject, isDuplicate, refreshCB, formWindow, isDetail, rendered, enableQuit) {
		var ids = itemPK2.split(".");
		amalto.itemsbrowser.ItemsBrowser.renderFormWindow(ids, dataObject, isDuplicate, refreshCB, formWindow, isDetail, rendered, enableQuit); 
	};
	
	return {

		init : function() {
			initUIAndData();
		},
		getLanguage : function() {
			return getCurrentLanguage();
		},
		openItemBrowser : function(ids, conceptName, refreshCB) {
			openItemBrowser(ids, conceptName, refreshCB);
		},
		renderFormWindow : function(itemPK2, dataObject, isDuplicate, refreshCB, formWindow, isDetail, rendered, enableQuit) {
			renderFormWindow(itemPK2, dataObject, isDuplicate, refreshCB, formWindow, isDetail, rendered, enableQuit);
		}
	}
}();
