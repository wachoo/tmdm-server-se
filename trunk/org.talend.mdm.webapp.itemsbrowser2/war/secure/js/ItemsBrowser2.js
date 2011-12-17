amalto.namespace("amalto.itemsbrowser2");

amalto.itemsbrowser2.ItemsBrowser2 = function() {
		
	var TITLE_BROWSER_PANEL =    {
        'fr' : 'Accès aux données',
        'en' : 'Browse Records'
    };
	
	var MSG_RENDERER_ERROR = {
        'fr' : 'Une erreur de rendu est survenue!',
        'en' : 'Rendering error occured!'
	};
	

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
                var itemsBrowser2Panel = tabPanel.getItem('itemsBrowser2Panel');

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
                    if (window.top.org_talend_mdm_webapp_itemsbrowser2_InBoundService_renderUI){
                    	window.top.org_talend_mdm_webapp_itemsbrowser2_InBoundService_renderUI();
                    } else {
                    	window.alert(MSG_RENDERER_ERROR[language]);
                    }
                    itemsBrowser2Panel.on("resize", resizeViewPort);
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

	function renderFormWindow(itemPK2, dataObject, isDuplicate, handleCallback, formWindow, isDetail, enableQuit) {
		var ids = itemPK2.split(".");
		amalto.itemsbrowser.ItemsBrowser.renderFormWindow(ids, dataObject, isDuplicate, handleCallback, formWindow, isDetail, enableQuit); 
	};
	
	
	if(!Array.contains){
        Array.prototype.contains = function(obj){
            for(var i=0; i<this.length; i++){
                if(this[i]==obj){
                    return true;
                }
            }
        };
    }

    /**
     * EditorGrid validation plugin
     * Adds validation functions to the grid
     *
     * @author  Jozef Sakalos, aka Saki
     * @version 0.1
     *
     * Usage:
     * grid = new Ext.grid.EditorGrid({plugins:new GridValidator(), ...})
     */
    GridValidator = function(config) {

        // initialize plugin
        this.init = function(grid) {
            Ext.apply(grid, {
                /**
                 * Checks if a grid cell is valid
                 * @param {Integer} col Cell column index
                 * @param {Integer} row Cell row index
                 * @return {Boolean} true = valid, false = invalid
                 */
                isCellValid:function(col, row) {
                    if(!this.colModel.isCellEditable(col, row)) {
                        return true;
                    }
                    var ed = this.colModel.getCellEditor(col, row);
                    if(!ed) {
                        return true;
                    }
                    var record = this.store.getAt(row);
                    if(!record) {
                        return true;
                    }
                    var field = this.colModel.getDataIndex(col);
                    ed.field.setValue(record.data[field]);
                    return ed.field.isValid(true);
                } // end of function isCellValid

                /**
                 * Checks if grid has valid data
                 * @param {Boolean} editInvalid true to automatically start editing of the first invalid cell
                 * @return {Boolean} true = valid, false = invalid
                 */
                ,isValid:function(editInvalid) {
                    var cols = this.colModel.getColumnCount();
                    var rows = this.store.getCount();
                    var r, c;
                    var valid = true;
                    for(r = 0; r < rows; r++) {
                        for(c = 0; c < cols; c++) {
                            valid = this.isCellValid(c, r);
                            if(!valid) {
                                break;
                            }
                        }
                        if(!valid) {
                            break;
                        }
                    }
                    if(editInvalid && !valid) {
                        this.startEditing(r, c);
                    }
                    return valid;
                } // end of function isValid
            });
        }; // end of function init
    }; // GridValidator plugin end

	
	
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
		renderFormWindow : function(itemPK2, dataObject, isDuplicate, handleCallback, formWindow, isDetail, enableQuit) {
			renderFormWindow(itemPK2, dataObject, isDuplicate, handleCallback, formWindow, isDetail, enableQuit);
		}
	}
}();
