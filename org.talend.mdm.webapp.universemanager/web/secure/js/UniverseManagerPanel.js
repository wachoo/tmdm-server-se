Ext.namespace('amalto.universemanager');
amalto.universemanager.UniverseManagerPanel = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.universemanager.UniverseManagerPanel.superclass.constructor
			.call(this);
};
Ext.extend(amalto.universemanager.UniverseManagerPanel, Ext.Panel, {
	initUIComponents : function() {
		Ext.apply(this, {
			layout : "form",
			title : "Universe Manager",
			id : "UniverseManagerPanel",
			closable:true,
			bodyStyle:'padding:5px',
			items : [{
				collapsible : true,
				title : "Quick Start",
				bodyStyle:'padding:5px',
				items : [{
					title : "Create a new data universe",
					layout : "form",
					height : 110,
					items : [{
						html : "It's a wizzard for creating a Universe with a new revision of Data Model and using a new revision of Data Cluster.",
						xtype : "label",
						autoHeight : true,
						autoWidth : true
					}],
					xtype : "fieldset",
					buttons : [{
						handler : function(button, event) {
							this.onStartClick(button, event);
						}.createDelegate(this),
						text : "Start"
					}],
					buttonAlign : "right"
				}]
			}, {
				collapsible : true,
				title : "Manage Universe",
				bodyStyle:'padding:5px',
				items : [{
					title : "Being built...",
					layout : "form",
					xtype : "fieldset"
				}]
			}, {
				collapsible : true,
				title : "Manage Revision",
				bodyStyle:'padding:5px',
				items : [{
					title : "Being built...",
					layout : "form",
					xtype : "fieldset"
				}]
			}]
		});
	},
	
	onStartClick : function(button, event) {

		var tabPanel = amalto.core.getTabPanel();
		var universeWizardPanel = tabPanel.getItem('UniverseWizardPanel');
		
		if(universeWizardPanel == undefined){

			universeWizardPanel=new amalto.universemanager.UniverseWizardPanel();			
			tabPanel.add(universeWizardPanel);
		}
        
        universeWizardPanel.show();
		universeWizardPanel.doLayout();
		amalto.core.doLayout();
		
		universeWizardPanel.initData();
	}
});
