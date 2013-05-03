Ext.namespace('amalto.updatereport');
amalto.updatereport.HistoryViewer = function(config) {

	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.updatereport.HistoryViewer.superclass.constructor.call(this);

};

var panelId = "datachangesviewer";

Ext.extend(amalto.updatereport.HistoryViewer, Ext.Panel, {
	initUIComponents : function() {
	    Ext.apply(this, {
			layout : 'border',
			title : amalto.updatereport.bundle.getMsg('MAIN_TITLE'),
			id : panelId,
			closable:true,
			border : true,
			defaults: {			    
			    split: true,
			    bodyStyle: 'padding:15px'
			},
			items:[{
                    title:amalto.updatereport.bundle.getMsg('UPDATE_REPORT_DETAILS'),
                    region: 'north',
                    cmargins: '0 5 0 0',
                    height: 200,
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
                    parentPanelId:panelId,
                    title:amalto.updatereport.bundle.getMsg('BEFORE'),
                    region: 'west',
                    width: '50%',
                    margins: '5 0 0 0',
                    cmargins: '5 5 0 0',
                    ids:this.ids,
                    date:this.date,
                    key:this.key,
                    operationType : this.operationType,
                    concept:this.concept,
                    dataCluster:this.dataCluster,
                    dataModel:this.dataModel,
                    action:'before',
                    autoScroll : "true"                    
                }, {
                    xtype: "talend.documenthistorypanel",
                    cellCls: 'history-viewer-changes',
                    id:2,
                    region: 'center',
                    width: '50%',
                    margins: '5 0 0 0',
                    parentPanelId:panelId,
                    title:amalto.updatereport.bundle.getMsg('AFTER'),
                    ids:this.ids,
                    date:this.date,
                    key:this.key,
                    operationType : this.operationType,
                    concept:this.concept,
                    dataCluster:this.dataCluster,
                    dataModel:this.dataModel,
                    action:'current',
                    autoScroll : "true"                    
                }
            ],
            tbar: ['->',{
		        text : amalto.updatereport.bundle.getMsg('OPEN_RECORD'),
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
