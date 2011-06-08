Ext.namespace('amalto.updatereport');
amalto.updatereport.DocumentHistoryPanel = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.updatereport.DocumentHistoryPanel.superclass.constructor.call(this);
};

Ext.extend(amalto.updatereport.DocumentHistoryPanel, Ext.Panel, {
    initUIComponents : function() {
	    Ext.apply(this, {
			layout: 'anchor',
            id:"documentHistoryPanel" + this.id,
			title: this.title,
			border: false,
			loadMask:true,
			autoShow: true,
			cls: 'document-history-panel',
			items: [{
			        xtype: 'button',
			        text: 'Restore',
                    date:this.date,
                    key:this.key,
                    concept:this.concept,
                    dataCluster:this.dataCluster,
                    dataModel:this.dataModel,
                    action:this.action,
                    parentPanelId:this.parentPanelId,
			        listeners: { 'render' : function(button) {
                            UpdateReportInterface.isAdminUser(function(data) {
                                button.disabled = !data;
		                    });
                        }
                    },
                    handler: function() {
                        Ext.Ajax.request({
                            failure: function(response, opts) {
                                alert("Failed to restore document");
                            },
                            url: "/updatereport/secure/documentRestore?date="+this.date+"&dataCluster="+this.dataCluster+"&dataModel="+this.dataModel+"&concept="+this.concept+"&revision=&action="+this.action+"&key="+this.key
                        });

                        // auto close panel that contains this document history panel
                        var tabPanel = amalto.core.getTabPanel();
                        var parentPanel=tabPanel.getItem(this.parentPanelId);
                        if(parentPanel) {
                            tabPanel.remove(parentPanel);
                        }
                    }
			    },{
			        anchor: '100%, 80%',
                    xtype : "treepanel",
                    cls: 'document-history-panel-tree',
                    loader : new Ext.ux.XmlTreeLoader({
                        dataUrl : "/updatereport/secure/documentHistory?date="+this.date+"&dataCluster="+this.dataCluster+"&dataModel="+this.dataModel+"&concept="+this.concept+"&revision=&action="+this.action+"&key="+this.key,
                        preloadChildren: true
                    }),
                    root : new Ext.tree.AsyncTreeNode({
                        expandable : true,
                        expanded : true,
                        text: 'Document',
                        draggable : false,
                        id : this.id
                    }),
                    autoScroll : "true",
                    containerScroll : "false"
                }
            ]
        });
    }
});

Ext.reg('talend.documenthistorypanel', amalto.updatereport.DocumentHistoryPanel);