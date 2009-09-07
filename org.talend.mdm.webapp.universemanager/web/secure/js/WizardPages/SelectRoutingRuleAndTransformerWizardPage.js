Ext.namespace('amalto.universemanager');
amalto.universemanager.SelectRoutingRuleAndTransformerWizardPage = function(
		config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.universemanager.SelectRoutingRuleAndTransformerWizardPage.superclass.constructor
			.call(this);
};
Ext.extend(amalto.universemanager.SelectRoutingRuleAndTransformerWizardPage,
		Ext.Panel, {
			initUIComponents : function() {
				
				this.selModel2 = new Ext.grid.CheckboxSelectionModel();
				
				this.store2 = new Ext.data.Store({
							proxy : new Ext.data.DWRProxy(
									UniverseManagerInterface.getSourceTransformList,
									false),
							reader : new Ext.data.ListRangeReader({
										id : 'transformName',
										totalProperty : 'totalSize',
										root : 'data'
									}, Ext.data.Record.create([{
												name : "transformName",
												type : "string"
											},{
												name : "localRevisionID",
												type : "string"
											}])),
							remoteSort : false
						});

				this.gridPanel2 = new Ext.grid.GridPanel({
							width : 600,
							height : 250,
							store : this.store2,
							selModel : this.selModel2,
							autoScroll : true,
							autoHeight: true,
							tbar : new Ext.Toolbar([
//								    {
//										handler : function(button, event) {
//											this.onImportClick(button, event);
//										}.createDelegate(this),
//										text : "Import Dependencies"
//									}
									]),
							columns : [
								    this.selModel2,
								    {
										width : 370,
										hidden : false,
										header : "Transformers",
										dataIndex : "transformName",
										sortable : true
									}, {
										hidden : false,
										header : "Revision",
										dataIndex : "localRevisionID",
										sortable : true
									}]
						});
						
				this.selModel1 = new Ext.grid.CheckboxSelectionModel();		

				this.store1 = new Ext.data.Store({
							proxy : new Ext.data.DWRProxy(
									UniverseManagerInterface.getSourceRoutingRuleList,
									false),
							reader : new Ext.data.ListRangeReader({
										id : 'routingRuleName',
										totalProperty : 'totalSize',
										root : 'data'
									}, Ext.data.Record.create([{
												name : "routingRuleName",
												type : "string"
											},{
												name : "localRevisionID",
												type : "string"
											}])),
							remoteSort : false
						});

				this.gridPanel1 = new Ext.grid.GridPanel({
							width : 600,
							height : 150,
							store : this.store1,
							selModel : this.selModel1,
							autoScroll : true,
							autoHeight: true,
							columns : [
								    this.selModel1,
								    {
										width : 370,
										hidden : false,
										header : "Routing Rule",
										dataIndex : "routingRuleName",
										sortable : true
									}, {
										hidden : false,
										header : "Revision",
										dataIndex : "localRevisionID",
										sortable : true
									}
//									{
//										hidden : false,
//										header : "Deactivate",
//										dataIndex : "Deactivate",
//										sortable : true
//									}
									]
						});

				Ext.apply(this, {
							layout : "fit",
							items : [{
								autoScroll : true,
								title : "Routing rules and transformer",
								layout : "form",
								height : 500,
								items : [new Ext.Panel({
											autoScroll : true,
											layout : "fit",
											border : false,
											items : [this.gridPanel1,
													this.gridPanel2]

										})],
								xtype : "fieldset"
							}],
							border : false
						});
			},

			reloadStores : function() {
				this.store1.reload();
				this.store2.reload();
			},
			
			resetData : function() {
				this.selModel1.clearSelections();
				this.selModel2.clearSelections();
			},
			
			commitData : function() {
				//routing rule
				var sels1=this.selModel1.getSelections();
				var selRoutingRules=new Array(sels1.length);
				for (var index = 0; index < sels1.length; index++) {
					selRoutingRules[index]=sels1[index].data;
				}
				//DWREngine.setAsync(false);
		        UniverseManagerInterface.recordRoutingRuleEnties(selRoutingRules,function(){});
			    //DWREngine.setAsync(true);
		        
		        //transformer
		        var sels2=this.selModel2.getSelections();
				var selTransformers=new Array(sels2.length);
				for (var index = 0; index < sels2.length; index++) {
					selTransformers[index]=sels2[index].data;
				}
				//DWREngine.setAsync(false);
		        UniverseManagerInterface.recordTransformerEnties(selTransformers,function(){});
			    //DWREngine.setAsync(true);
			},
			
			refreshData : function() {
				
				//routing rule
				DWREngine.setAsync(false);
				var routingRuleIDs;
		        UniverseManagerInterface.initRoutinngRuleEnties(function(data){
		           routingRuleIDs=data;
		        });
			    DWREngine.setAsync(true);
			    
			    if(routingRuleIDs==undefined||routingRuleIDs==null){
			    	this.selModel1.selectAll();
			    }else if(routingRuleIDs.length==0){
			    	this.selModel1.clearSelections();
			    }else{
			    	//get records from id
			    	var records=new Array(routingRuleIDs.length);
			    	for (var index = 0; index < routingRuleIDs.length; index++) {
			    		var record = this.store1.getById(routingRuleIDs[index]);
			    		records[index]=record;
			    	}
			    	
			    	this.selModel1.selectRecords(records,false);
			    }
			    
			    //transformer
			    DWREngine.setAsync(false);
				var transformerIDs;
		        UniverseManagerInterface.initTransformerEnties(function(data){
		           transformerIDs=data;
		        });
			    DWREngine.setAsync(true);
			    
			    if(transformerIDs==undefined||transformerIDs==null){
			    	this.selModel2.selectAll();
			    }else if(transformerIDs.length==0){
			    	this.selModel2.clearSelections();
			    }else{
			    	//get records from id
			    	var records=new Array(transformerIDs.length);
			    	for (var index = 0; index < transformerIDs.length; index++) {
			    		var record = this.store2.getById(transformerIDs[index]);
			    		records[index]=record;
			    	}
			    	
			    	this.selModel2.selectRecords(records,false);
			    }
			    
			}
		});
