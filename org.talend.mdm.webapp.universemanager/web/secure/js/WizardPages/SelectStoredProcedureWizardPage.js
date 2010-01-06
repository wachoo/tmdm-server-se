Ext.namespace('amalto.universemanager');
amalto.universemanager.SelectStoredProcedureWizardPage = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.universemanager.SelectStoredProcedureWizardPage.superclass.constructor
			.call(this);
};
Ext.extend(amalto.universemanager.SelectStoredProcedureWizardPage, Ext.Panel, {
	initUIComponents : function() {

		Ext.apply(this, {
			layout : "fit",
			border : false, 
			items : [{
				title : "Stored Procedure",
				layout : "table",
				height : 300,
				items : [
				this.isCopyAll=new Ext.form.Checkbox({
					xtype : "checkbox",
					boxLabel : "Copy all stored procedure"
				})],
				xtype : "fieldset"
			}]
		});

	},
	
	commitData : function() {
				var checked=this.isCopyAll.getValue();
				//DWREngine.setAsync(false);
			    UniverseManagerInterface.recordSPEntry(checked,function(){});
				//DWREngine.setAsync(true);
			},
			
	refreshData : function() {
				
				
				DWREngine.setAsync(false);
				var status;
		        UniverseManagerInterface.initSPEntry(function(_status){
		           status=_status;
		        });
			    DWREngine.setAsync(true);
			    
			    this.isCopyAll.setValue(true);
			    if(status==-1){
			    	this.isCopyAll.setValue(true);
			    }else if(status==1){
			    	this.isCopyAll.setValue(true);
			    }else if(status==0){
			    	this.isCopyAll.setValue(false);
			    }
			    
			},
			
	resetData : function() {
				this.isCopyAll.setValue(false);
			}
	
});
