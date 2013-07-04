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
			        text: amalto.updatereport.bundle.getMsg('RESTORE'),
                    date:this.date,
                    key:this.key,
                    operationType:this.operationType,
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
                                if (('LOGIC_DELETE' == button.operationType && "current" == button.action) || 'CREATE' == button.operationType) {
                                	button.disable(false);
                                }
		                    });
                        }
                    },
                    handler: function(button) {
                    	Ext.MessageBox.show({
                    		title:amalto.updatereport.bundle.getMsg('info_title'),
                    		msg:amalto.updatereport.bundle.getMsg('restore_confirm'),
                    	    icon:Ext.MessageBox.QUESTION,
                    	    buttons:Ext.Msg.YESNO,
                    	    cls:'document-history-panel-messagebox',
                    		fn:function(btn) {
                        		if (btn == "yes") {
                                    Ext.Ajax.request({
                                        url: "/updatereport/secure/documentRestore",
                                        params: {
                                        	date: button.date,
                                        	dataCluster: button.dataCluster,
                                        	dataModel: button.dataModel,
                                        	concept: button.concept,
                                        	revision: "",
                                        	action: button.action,
                                        	key: button.key
                                        },
                                        failure: function(response, opts) {
                                            Ext.MessageBox.show({
                                            	title:amalto.updatereport.bundle.getMsg('error_title'),
                                        		msg:amalto.updatereport.bundle.getMsg('restore_failure'),                                        	
                                        	    icon:Ext.MessageBox.ERROR,
                                        	    buttons:Ext.Msg.OK,
                                            	fn:function(btn) {
                                            		if (btn == "ok") {
                                            			 var tabPanel = amalto.core.getTabPanel();
                                                         var parentPanel=tabPanel.getItem(button.parentPanelId);
                                                         if(parentPanel) {
                                                             tabPanel.remove(parentPanel);
                                                             var updateReportPanel = tabPanel.getItem('UpdateReportPanel');
                                                             if (updateReportPanel) {
                                                            	 updateReportPanel.refresh();
                                                             }
                                                         }
                                            		}
                                            	}
                                            });                                           
                                        },
                                        success: function(resp,opts) {
                                        	Ext.MessageBox.show({
                                            	title:amalto.updatereport.bundle.getMsg('info_title'),
                                        		msg:amalto.updatereport.bundle.getMsg('restore_success'),                                        	
                                        	    icon:Ext.MessageBox.INFO,
                                        	    buttons:Ext.Msg.OK,
                                            	fn:function(btn) {
                                            		if (btn == "ok") {
                                            			 var tabPanel = amalto.core.getTabPanel();
                                                         var parentPanel=tabPanel.getItem(button.parentPanelId);
                                                         if(parentPanel) {
                                                             tabPanel.remove(parentPanel);
                                                             var updateReportPanel = tabPanel.getItem('UpdateReportPanel');
                                                             if (updateReportPanel) {
                                                            	 updateReportPanel.refresh();
                                                             }
                                                         }
                                            		}
                                            	}
                                        	});
                                        }
                                    });
                        		}
                    		}
                    	});         	
                    }
			    },{
			    	id: "treepanel" + this.id,
			        anchor: '100%, 80%',
                    xtype : "treepanel",
                    animate: false,
                    cls: 'document-history-panel-tree',
                    loader : new Ext.ux.XmlTreeLoader({
                        dataUrl : "/updatereport/secure/documentHistory",
                        baseParams : {
                        	date : this.date,
                        	dataCluster : this.dataCluster,
                        	dataModel : this.dataModel,
                        	concept : this.concept,
                        	revision : "",
                        	action : this.action,
                        	key : this.key,
                        	ids : this.ids
                        },
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