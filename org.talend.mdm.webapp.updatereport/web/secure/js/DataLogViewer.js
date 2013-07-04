Ext.namespace('amalto.updatereport');
amalto.updatereport.DataLogViewer = function(config) {
	
	
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.updatereport.DataLogViewer.superclass.constructor.call(this);
	
};
Ext.extend(amalto.updatereport.DataLogViewer, Ext.Panel, {
	initUIComponents : function() {
		Ext.apply(this, {
			layout : "fit",
			title : "Data Changes Viewer",
			items : [{
				animate : "false",
				layout : "fit",
				loader : new Ext.tree.TreeLoader({
					dataUrl : "/updatereport/secure/updateReportDetails",
					baseParams : {
						ids : this.ids
					} 
				}),
				xtype : "treepanel",
				root : new Ext.tree.AsyncTreeNode({
					expandable : true,
					expanded : true,
					text : "Update",
					draggable : false,
					id : "0"
				}),
				autoScroll : "true",
				containerScroll : "true"
			}],
			closable:true,
			id:this.ids,
			key:this.key,
			dataCluster:this.dataCluster,
			dataModel:this.dataModel,
			concept:this.concept,
			tbar: ['->',{
		        text : "Open Record",
		        iconCls:'report_bt_openRecord',
		        handler : function() {
		        	DWREngine.setAsync(false);
		        	var result = true;
		        	
		        	UpdateReportInterface.checkDCAndDM(this.dataCluster, this.dataModel, function(data) {
		        		result = data
		        	});
		        	
		        	if(result) {
		        		amalto.itemsbrowser.ItemsBrowser.editItemDetails(amalto.updatereport.bundle.getMsg('JOURNAL_NAME'), this.key.split('\.'), this.concept, function(){});
		        	}
		        	else {
		        		Ext.MessageBox.alert("Error", "Please select the corresponding Data Container and Data Model.");
		        	}
		        	DWREngine.setAsync(true);
		        	}.createDelegate(this)
		        }]

		});
	}
});