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
					dataUrl : "/updatereport/secure/updateReportDetails?ids="+this.ids
				}),
				xtype : "treepanel",
				root : new Ext.tree.AsyncTreeNode({
					expandable : true,
					expanded : true,
					text : "Update",
					draggable : false,
					id : "0"
				}),
				containerScroll : "true"
			}],
			closable:true,
			id:this.ids
		});
	}
	
});
