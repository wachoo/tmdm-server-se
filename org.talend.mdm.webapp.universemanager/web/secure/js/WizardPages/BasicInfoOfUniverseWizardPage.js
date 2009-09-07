Ext.namespace('amalto.universemanager');
amalto.universemanager.BasicInfoOfUniverseWizardPage = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.universemanager.BasicInfoOfUniverseWizardPage.superclass.constructor
			.call(this);
};
Ext.extend(amalto.universemanager.BasicInfoOfUniverseWizardPage, Ext.Panel, {
	initUIComponents : function() {
		
		this.sourceUniverseStore = new Ext.data.Store({
         proxy: new Ext.data.DWRProxy(UniverseManagerInterface.getAllUniversePksList , false),
         reader:new Ext.data.ListRangeReader({
              totalProperty: 'totalSize',
              id: 'value',
              root: 'data'
          }, Ext.data.Record.create([
                    {name: 'value',mapping:'value',type:'string'},
                    {name: 'text',mapping:'text',type:'string'}
                   ])
           )
       });

		Ext.apply(this, {
			layout : "fit",
			border : false,
			items : [{
				autoScroll : true,
				labelWidth : 150,
				title : "Create a Data Revision/Universe",
				layout : "form",
				height : 300,
				autoScroll : true,
				items : [
				this.universeNameField=new Ext.form.TextField({
					name : "universeName",
					width : 200,
					fieldLabel : "Universe Name",
					xtype : "textfield",
					listeners : {
                               'change' : function(field,newValue,oldValue) {
                               	              this.onStoreFieldChanged(field,newValue,oldValue);
						                  }.createDelegate(this)
                                }
				}),
				this.universeDescField=new Ext.form.TextArea({
					name : "universeDesc",
					width : 400,
					fieldLabel : "Universe Description",
					xtype : "textarea",
					listeners : {
                               'change' : function(field,newValue,oldValue) {
                               	              this.onStoreFieldChanged(field,newValue,oldValue);
						                  }.createDelegate(this)
                                }
				}),
				this.sourceUniverseField=new Ext.form.ComboBox({
					name : "sourceUniverseName",
					fieldLabel : "Source Universe",
					xtype : "combo",
					store: this.sourceUniverseStore,
			        valueField : "value",
			        displayField: "text",
			        typeAhead: true,
			        forceSelection: true,
			        triggerAction: 'all',
			        editable : false,
			        listeners : {
			                    
                               'change' : function(field,newValue,oldValue) {
                               	              this.onContextFieldChanged(field,newValue,oldValue);
						                  }.createDelegate(this)
                                }
				})],
				xtype : "fieldset"
			}]
		});

	},
	
	onContextFieldChanged : function(field,newValue,oldValue){
		
        this.onStoreFieldChanged(field,newValue,oldValue);
        //reload
        this.parentContainer.reloadContextAndChangeData(newValue);
	},
	
	
	onStoreFieldChanged : function(field,newValue,oldValue){
		
        UniverseManagerInterface.synchroFieldChangesWithStore(field.name,newValue,function(){
	    	   //nop
	    	});
	},
	
	updateSourceUniverseField : function(newValue){
		this.sourceUniverseField.setValue(newValue);
	},
	
	updateUniverseNameField : function(newValue){
		this.universeNameField.setValue(newValue);
	},
	
	updateUniverseDescField : function(newValue){
		this.universeDescField.setValue(newValue);
	},
	
	getUniverseNameFieldValue : function(){
		return this.universeNameField.getValue();
	},
	
	getUniverseDescFieldValue : function(){
		return this.universeDescField.getValue();
	},
	
	getSourceUniverseFieldValue : function(){
		return this.sourceUniverseField.getValue();
	},
	
	checkSourceUniverse : function(){
		var newUniverseName=this.getUniverseNameFieldValue();
		var universeNames=this.sourceUniverseStore.data.keys;
		for (var index = 0; index < universeNames.length; index++) {
			if(newUniverseName==universeNames[index])return false;
		}
		return true;
	}
});
