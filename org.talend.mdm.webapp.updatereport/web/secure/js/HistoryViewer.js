Ext.namespace('amalto.updatereport');
amalto.updatereport.HistoryViewer = function(config) {

	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.updatereport.HistoryViewer.superclass.constructor.call(this);

};

Ext.extend(amalto.updatereport.HistoryViewer, Ext.Panel, {
	initUIComponents : function() {
	    Ext.apply(this, {
			layout : 'classtable',
			title : "Data Changes Viewer",
			id : "datachangesviewer",
			closable:true,
			border:true,
			layoutConfig: {columns:2, cls: 'history-viewer'},
			items:[{
                    title:'Update report details',
                    colspan: 2,
                    cellCls: 'history-viewer-header',
                    animate : "false",
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
                    autoScroll : "true",
                    containerScroll : "true"
                }, {
                    xtype: "talend.documenthistorypanel",
                    cellCls: 'history-viewer-changes',
                    id:1,
                    title:'Before',
                    date:this.date,
                    key:this.key,
                    concept:this.concept,
                    dataCluster:this.dataCluster,
                    dataModel:this.dataModel,
                    action:'before'
                }, {
                    xtype: "talend.documenthistorypanel",
                    cellCls: 'history-viewer-changes',
                    id:2,
                    title:'After',
                    date:this.date,
                    key:this.key,
                    concept:this.concept,
                    dataCluster:this.dataCluster,
                    dataModel:this.dataModel,
                    action:'current'
                }
            ]
		});
	}
});
