Ext.namespace('amalto.universemanager');
amalto.universemanager.DefineAccessControlListWizardPage = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.universemanager.DefineAccessControlListWizardPage.superclass.constructor
			.call(this);
};
Ext.extend(amalto.universemanager.DefineAccessControlListWizardPage, Ext.Panel,
		{
			initUIComponents : function() {
				
				this.selModel1 = new Ext.grid.CheckboxSelectionModel();
				
				this.store1 = new Ext.data.Store({
							proxy : new Ext.data.DWRProxy(
									UniverseManagerInterface.getSourceRoleList,
									false),
							reader : new Ext.data.ListRangeReader({
										id : 'roleName',
										totalProperty : 'totalSize',
										root : 'data'
									}, Ext.data.Record.create([{
												name : "roleName",
												type : "string"
											},{
												name : "localRevisionID",
												type : "string"
											}])),
							remoteSort : false
						});

				this.gridPanel1 = new Ext.grid.GridPanel({
					width:600,
					height:200,
					autoScroll : true,
					autoHeight: true,
					store : this.store1,
					selModel : this.selModel1,
					columns : [
					this.selModel1,
					{
						width : 300,
						hidden : false,
						header : "Role Names",
						dataIndex : "roleName",
						sortable : true
					}, {
						hidden : false,
						header : "Revision",
						dataIndex : "localRevisionID",
						sortable : true
					}]
				});

				Ext.apply(this, {
					layout : "fit",
					border : false,
					items : [{
						autoScroll : true,
						title : "Access Control List",
						height: 500,
						layout : "form",
						items : [this.gridPanel1],
						xtype : "fieldset"
					}]
				});
			},
			
			reloadStore : function() {
				this.store1.reload();
			},
			
			refreshData : function() {
				
				
				DWREngine.setAsync(false);
				var roleIDs;
		        UniverseManagerInterface.initRoleEnties(function(data){
		           roleIDs=data;
		        });
			    DWREngine.setAsync(true);
			    
			    if(roleIDs==undefined||roleIDs==null){
			    	this.selModel1.selectAll();
			    }else if(roleIDs.length==0){
			    	this.selModel1.clearSelections();
			    }else{
			    	//get records from id
			    	var records=new Array(roleIDs.length);
			    	for (var index = 0; index < roleIDs.length; index++) {
			    		var record = this.store1.getById(roleIDs[index]);
			    		records[index]=record;
			    	}
			    	
			    	this.selModel1.selectRecords(records,false);
			    }
			    
			},
			
			commitData : function() {
				var sels=this.selModel1.getSelections();
				var selRoles=new Array(sels.length);
				for (var index = 0; index < sels.length; index++) {
					selRoles[index]=sels[index].data;
				}
				//DWREngine.setAsync(false);
		        UniverseManagerInterface.recordRoleEnties(selRoles,function(){});
			    //DWREngine.setAsync(true);
			},
			
			resetData : function() {
				this.selModel1.clearSelections();
			}
		});
