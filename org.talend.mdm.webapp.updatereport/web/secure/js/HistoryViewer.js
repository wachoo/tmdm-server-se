Ext.namespace('amalto.updatereport');
amalto.updatereport.HistoryViewer = function(config) {

	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.updatereport.HistoryViewer.superclass.constructor.call(this);

};

Ext.extend(amalto.updatereport.HistoryViewer, Ext.Panel, {
	initUIComponents : function() {
	    Ext.apply(this, {
			layout : 'table',
			title : "Data Changes Viewer",
			id : "datachangesviewer",
			closable:true,
			border:false,
			layoutConfig: {columns:2},
			defaults:{border: false},
			items:[{
                    title:'Update report details',
                    colspan: 2,
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
                },{
                    title:'Before',
                    loader : new Ext.ux.XmlTreeLoader({
                        dataUrl : "/updatereport/secure/documentHistory?date="+this.date+"&dataCluster="+this.dataCluster+"&dataModel="+this.dataModel+"&concept="+this.concept+"&revision=&action=before&key="+this.key,
                        preloadChildren: true
                    }),
                    xtype : "treepanel",
                    root : new Ext.tree.AsyncTreeNode({
                        expandable : true,
                        expanded : true,
                        text : "Update",
                        draggable : false,
                        id : "1"
                    }),
                    autoScroll : "true",
                    containerScroll : "false"
                },{
                    title:'Current',
                    loader : new Ext.ux.XmlTreeLoader({
                        dataUrl : "/updatereport/secure/documentHistory?date="+this.date+"&dataCluster="+this.dataCluster+"&dataModel="+this.dataModel+"&concept="+this.concept+"&revision=&action=current&key="+this.key,
                        preloadChildren: true
                    }),
                    xtype : "treepanel",
                    root : new Ext.tree.AsyncTreeNode({
                        expandable : true,
                        expanded : true,
                        text : "Update",
                        draggable : false,
                        id : "2"
                    }),
                    autoScroll : "true",
                    containerScroll : "false"
                }
            ]
		});
	}
});
