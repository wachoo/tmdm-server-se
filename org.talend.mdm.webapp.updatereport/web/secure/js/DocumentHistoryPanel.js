Ext.namespace('amalto.updatereport');
amalto.updatereport.DocumentHistoryPanel = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.updatereport.DocumentHistoryPanel.superclass.constructor.call(this);
};



var RESTORE = {
		'fr' : 'Restaurer',
		'en' : 'Restore'
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
			        text: RESTORE[language],
                    date:this.date,
                    key:this.key,
                    concept:this.concept,
                    dataCluster:this.dataCluster,
                    dataModel:this.dataModel,
                    action:this.action,
                    parentPanelId:this.parentPanelId,
			        listeners: { 'render' : function(button) {
                            UpdateReportInterface.isAdminUser(function(data) {
                                if(!data) {
                                    button.disable(true);
                                }
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
			    	id: "treepanel" + this.id,
			        anchor: '100%, 80%',
                    xtype : "treepanel",
                    animate: false,
                    cls: 'document-history-panel-tree',
                    loader : new Ext.ux.XmlTreeLoader({
                        dataUrl : "/updatereport/secure/documentHistory?date="+this.date+"&dataCluster="+this.dataCluster+"&dataModel="+this.dataModel+"&concept="+this.concept+"&revision=&action="+this.action+"&key="+this.key+"&ids="+this.ids,
                        preloadChildren: true
                    }),
                    root : new Ext.tree.AsyncTreeNode({
                        expandable : true,
                        expanded : true,
                        text: 'Document',
                        draggable : false,
                        id : this.id,
                        listeners:{
                        	'collapse':function(node){
                        		var treeType = node.id;
                        		if(treeType == 1)
                            		treeType = 2;
                            	else
                            		treeType = 1;
                        		
                        		var treePanel = Ext.getCmp("treepanel" + treeType);
                            	treePanel.getNodeById(treeType).collapse();
                        		
                        	},
                        	'expand':function(node){
                        		var treeType = node.id;
                        		if(treeType == 1)
                            		treeType = 2;
                            	else
                            		treeType = 1;

                        		var treePanel = Ext.getCmp("treepanel" + treeType);
                            	treePanel.getNodeById(treeType).expand();
                        	}
                        }
                    }),
                    autoScroll : "true",
                    containerScroll : "false"
                }
            ]
        });
    }
});

Ext.reg('talend.documenthistorypanel', amalto.updatereport.DocumentHistoryPanel);