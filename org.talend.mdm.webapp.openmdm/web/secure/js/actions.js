/**
 * Loads and excutes actions of the east panel
 */

amalto.namespace("amalto");

 
amalto.actions = function () {
	var DATA_MODEL = {
		'fr':'Modèle de données',
		'en':'Data Model'
	}
	var DATA_CONTAINER = {
		'fr':'Conteneur de données',
		'en':'Data Container'
	}	
	var SAVE = {
		'fr':'Sauvegarder',
		'en':'Save'
	}
	var STATUS =  {
       'fr' : 'Statut',
       'en' : 'Status'
    }
	var STATUS_MSG_SUCCESS =  {
       'fr' : 'L\'action s\'est déroulée avec succès.',
       'en' : 'Action completed successfully.'
    }
	var STATUS_MSG_FAILURE =  {
	   'fr' : 'L\'action a échouée!',
	   'en' : 'Action failed!'
	}
	
	/***************************************************************************
	 * 
	 * PRIVATE Properties and Methods
	 * 
	 **************************************************************************/
	function loadActionsPrivate(){
		
		// load cluster and model
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
						fieldLabel: DATA_CONTAINER[language],
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
						fieldLabel: DATA_MODEL[language],
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
		
		// reset client cache
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
			if(result=="DONE")
				Ext.Msg.alert(STATUS[language],STATUS_MSG_SUCCESS[language]);
			else
				Ext.Msg.alert(STATUS[language],STATUS_MSG_FAILURE[language] + ' ' + result);	
				
			var tabPanel = amalto.core.getTabPanel();
			tabPanel.items.each(function(item){
                        if(item.closable){
                        	if((item.id=='welcome')||(item.id=='tdscPanel')){
                        		
                        	}else{
                        		tabPanel.remove(item);
                        	}
                            
                        }            
            });
		});
	}

	/***************************************************************************
	 * 
	 * PUBLIC Properties and Methods
	 * 
	 **************************************************************************/
	return  { 
		
		loadActions: function() {
			loadActionsPrivate();
		}
						
	}// PUBLIC
	
}();