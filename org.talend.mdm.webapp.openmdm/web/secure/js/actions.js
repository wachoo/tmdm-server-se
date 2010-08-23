/**
 * Loads and excutes actions of the east panel
 */

amalto.namespace("amalto");

 
amalto.actions = function () {
	
	var SAVE = {
		'fr':'Sauvegarder',
		'en':'Save'
	}
	var STATUS =  {
       'fr' : 'Statut',
       'en' : 'Status'
    } 
	
	/********************************************************************
	 * 
	 * PRIVATE Properties and Methods
	 * 
	 ********************************************************************/
	function loadActionsPrivate(){
		
		//load cluster and model
		Ext.getCmp('actions').collapse();
		Ext.getCmp('actions').add(
			new Ext.FormPanel({
				id:'config',
				border:false,
				labelAlign:'top',
				items:[
					new Ext.form.ComboBox({
						id:'datacluster-select',
						store: new Ext.data.Store({
							proxy: new Ext.data.SimpleDWRProxy(ActionsInterface.getClusters),
				        	reader: new Ext.data.MapReader()
						}),
						displayField: 'key',
						valueField: 'key',
						fieldLabel:'Data Container',
					  	loadingText:'Loading...',
			         	mode:'remote',
			          	triggerAction:'all',
			          	tpl:'<tpl for="."><div class="x-combo-list-item" qtip="{value}">{key}</div></tpl>', 
			          	editable:false
					}),
					new Ext.form.ComboBox({
						id:'datamodel-select',
						store: new Ext.data.Store({
							proxy: new Ext.data.SimpleDWRProxy(ActionsInterface.getModels),
				        	reader: new Ext.data.MapReader()
						}),
						displayField: 'key',
						valueField: 'key',
						fieldLabel:'Data Model',
					  	loadingText:'Loading...',
			         	mode:'remote',
			          	triggerAction:'all',
			          	tpl:'<tpl for="."><div class="x-combo-list-item" qtip="{value}">{key}</div></tpl>',
			          	editable:false
					}),
					new Ext.Button({
						text:SAVE[language],
						handler:saveConfig
					})
				]
			})
		);
		
		amalto.core.doLayout();
		
		//reset client cache
		DWRUtil.setValue('datacluster-select',null);
		DWRUtil.setValue('datamodel-select',null);
		
		ActionsInterface.getCluster(function(result){
			DWRUtil.setValue('datacluster-select',result);
		});			
	
		ActionsInterface.getModel(function(result){
			DWRUtil.setValue('datamodel-select',result);
		});
	}
	
	function saveConfig(){
		var cluster = DWRUtil.getValue('datacluster-select');
		var model = DWRUtil.getValue('datamodel-select');
		ActionsInterface.setClusterAndModel(cluster,model,function(result){
			Ext.Msg.alert(STATUS[language],"  "+result);
			var tabPanel = amalto.core.getTabPanel();
			tabPanel.items.each(function(item){
                        if(item.closable){
                            tabPanel.remove(item);
                        }            
            });
		});
	}

	/********************************************************************
	 * 
	 * PUBLIC Properties and Methods
	 * 
	 ********************************************************************/
	return  { 
		
		loadActions: function() {
			loadActionsPrivate();
		}
						
	}//PUBLIC
	
}();