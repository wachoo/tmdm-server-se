Ext.namespace('amalto.universemanager');
amalto.universemanager.WizzardSummaryPage = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.universemanager.WizzardSummaryPage.superclass.constructor.call(this);
};
Ext.extend(amalto.universemanager.WizzardSummaryPage, Ext.Panel, {
	initUIComponents : function() {
		this.store1 = new Ext.data.Store({
							proxy : new Ext.data.DWRProxy(
									UniverseManagerInterface.getUniverseSummaryList,
									false),
							reader : new Ext.data.ListRangeReader({
										id : 'keys',
										totalProperty : 'totalSize',
										root : 'data'
									}, Ext.data.Record.create([{
												name : "objectName",
												type : "string"
											},{
												name : "revisionID",
												type : "string"
											}])),
							remoteSort : false
						});

		this.gridPanel1 = new Ext.grid.GridPanel({
			height:500,
			border : false,
			store : this.store1,
			title : "Universe Summary",
			selModel : new Ext.grid.RowSelectionModel({}),
			columns : [{
				hidden : false,
				header : "Objects",
				dataIndex : "objectName",
				sortable : true
			}, {
				hidden : false,
				header : "Revision",
				dataIndex : "revisionID",
				sortable : true
			}]
		});

		Ext.apply(this, {
			layout : "fit",
			border : false,
			items : [this.gridPanel1]
		});
	},
	
	reBuildSummary : function() {
				this.store1.reload();
			}
});
