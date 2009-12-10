Ext.namespace('amalto.workflowtasks');
amalto.workflowtasks.WorkflowTaskFormPanel = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.workflowtasks.WorkflowTaskFormPanel.superclass.constructor
			.call(this);
};
Ext.extend(amalto.workflowtasks.WorkflowTaskFormPanel, Ext.Panel, {
	
	fieldRecords:{},
	
	initUIComponents : function() {
		
		var dataFieldsDefinitionSet=this.dataFieldsDefinition;
		var formItems=new Array(dataFieldsDefinitionSet.length);
		this.fieldRecords=new Array(dataFieldsDefinitionSet.length);
		for (var index = 0; index < dataFieldsDefinitionSet.length; index++) {
			var dataFieldDefinition=dataFieldsDefinitionSet[index];
			
			var formItem;
			if(!dataFieldDefinition.hidden){
				//process types
				
				var fieldDataType=dataFieldDefinition.dataTypeClassName;
				
				if(fieldDataType=="java.lang.Long"||fieldDataType=="java.lang.Double"){
					formItem=new Ext.form.NumberField({
				            id : dataFieldDefinition.name+"_"+this.activityUUID,
				            name : dataFieldDefinition.name+"_"+this.activityUUID,
							xtype : "numberfield",
							fieldLabel : dataFieldDefinition.name,
							width:300,
							readOnly:this.isReadonly,
							dataTypeClassName:dataFieldDefinition.dataTypeClassName,
							isActivityVariable:dataFieldDefinition.activityVariable,
							initialValue:dataFieldDefinition.initialValue
						 });
				}else if(fieldDataType=="java.lang.Boolean"){
					formItem=new Ext.form.Checkbox({
				            id : dataFieldDefinition.name+"_"+this.activityUUID,
				            name : dataFieldDefinition.name+"_"+this.activityUUID,
							xtype : "checkbox",
							fieldLabel : dataFieldDefinition.name,
							readOnly:this.isReadonly,
							dataTypeClassName:dataFieldDefinition.dataTypeClassName,
							isActivityVariable:dataFieldDefinition.activityVariable,
							initialValue:dataFieldDefinition.initialValue
						 });
				}else if(fieldDataType=="java.util.Date"){
					formItem=new Ext.form.DateField({
				            id : dataFieldDefinition.name+"_"+this.activityUUID,
				            name : dataFieldDefinition.name+"_"+this.activityUUID,
							xtype : "datefield",
							fieldLabel : dataFieldDefinition.name,
							format : "Y/m/d H:i:s",
							width:150,
							readOnly:this.isReadonly,
							dataTypeClassName:dataFieldDefinition.dataTypeClassName,
							isActivityVariable:dataFieldDefinition.activityVariable,
							initialValue:dataFieldDefinition.initialValue
						 });
				}else{
					//default java.lang.String
					if(!dataFieldDefinition.enumeration){
						
						formItem=new Ext.form.TextField({
				            id : dataFieldDefinition.name+"_"+this.activityUUID,
				            name : dataFieldDefinition.name+"_"+this.activityUUID,
							xtype : "textfield",
							//fieldLabel : dataFieldDefinition.label
							fieldLabel : dataFieldDefinition.name,
							width:300,
							readOnly:this.isReadonly,
							dataTypeClassName:dataFieldDefinition.dataTypeClassName,
							isActivityVariable:dataFieldDefinition.activityVariable,
							initialValue:dataFieldDefinition.initialValue
						 });
						 
					}else{
						//combo case
						var enumValues=dataFieldDefinition.enumerationValues;
						var comboData=new Array(enumValues.length);
						for (var j = 0; j < enumValues.length; j++) {
							comboData[j]=new Array(2);
							comboData[j][0]=enumValues[j];
							comboData[j][1]=enumValues[j];
						}
						
						formItem=new Ext.form.ComboBox({
				            id : dataFieldDefinition.name+"_"+this.activityUUID,
				            name : dataFieldDefinition.name+"_"+this.activityUUID,
							xtype : "combo",
							fieldLabel : dataFieldDefinition.name,
							typeAhead: true,
							triggerAction: 'all', 
							mode: 'local',
							store: new Ext.data.SimpleStore({
										fields: ['myId', 'displayText'],
										data : comboData
									}),
						    valueField: 'myId',
                            displayField: 'displayText',
							width:300,
							readOnly:this.isReadonly,
							dataTypeClassName:dataFieldDefinition.dataTypeClassName,
							isActivityVariable:dataFieldDefinition.activityVariable,
							initialValue:dataFieldDefinition.initialValue
						 });
					}
					
				}
				
			}else{
				formItem=new Ext.form.Hidden({
						    id : dataFieldDefinition.name+"_"+this.activityUUID,
				            name : dataFieldDefinition.name+"_"+this.activityUUID,
				            xtype:"hidden",
				            hidden:true,  
                            hiddenLabel:true,
                            dataTypeClassName:dataFieldDefinition.dataTypeClassName,
							isActivityVariable:dataFieldDefinition.activityVariable,
							initialValue:dataFieldDefinition.initialValue
						 });		
			}
						 
			formItems[index]=formItem;
			
			//record
			this.fieldRecords[index]=dataFieldDefinition.name;
		}
		
		Ext.apply(this, {
			id : this.activityUUID,
			title : this.activityUUID,
			layout : "form",
			items : formItems,
			buttonAlign : "left",
			buttons : [{
				text : "Submit",
				hidden:this.isReadonly,
				handler : function(button, event) {
					this.onSubmitClick(button, event);
				}.createDelegate(this)
			}],
			bodyStyle:'padding:15px',
			autoScroll : true,
			closable: true
		});
	},
	
	initData : function(){
		
       //alert(this.isReadonly+"\n"+this.processDefineUUID+"\n"+this.processInstanceUUID);
       WorkflowTasksInterface.getDataFieldsValues(this.processInstanceUUID,this.activityUUID,
                function(results){
                	this.getDataFieldsValuesCallback(results);
				}.createDelegate(this)
                      
       );
    },
    
    getDataFieldsValuesCallback : function(results){
    	
		for (var index = 0; index < results.length; index++) {
                      	  var result=results[index];
                      	  var varname=result.name;
                      	  var varvalue=result.value;
                      	  
                      	  var cmpId=varname+"_"+this.activityUUID;
                      	  if(Ext.getCmp(cmpId)!=undefined&&Ext.getCmp(cmpId)!=null){
                      	  	
                      	  	if(varvalue==null||varvalue==""){
                      	  		//process initValue
                      	  		var initvalue=Ext.getCmp(cmpId).initialValue;
                      	  		if(initvalue!="")varvalue=initvalue;
                      	  	}
                      	  	//process boolean
                      	  	if(varvalue!=null&&(varvalue=="true"||varvalue=="false"))varvalue=(varvalue=="true");
                      	  	DWRUtil.setValue(cmpId,varvalue);
                      	  }//end if
                      	                	  
        }//end for
    },
	
	refreshTasksStore : function(){
    	
		if(this.tasksStore!=undefined&&this.tasksStore!=null)this.tasksStore.reload();
		             
    },
    
    onSubmitClick : function(button, event){
    	
        var dataFieldsValues = {};
		for (var index = 0; index < this.fieldRecords.length; index++) {
			var fieldRecord=this.fieldRecords[index];
			//exist first
			var fieldValue=DWRUtil.getValue(fieldRecord+"_"+this.activityUUID);
			var fieldCmp=Ext.getCmp(fieldRecord+"_"+this.activityUUID);
			var fieldType=fieldCmp.dataTypeClassName;
			var fieldTag=String(fieldCmp.isActivityVariable);
			
			var dataFieldValueVO={
		        "name":fieldRecord,
		        "value":String(fieldValue),
		        "dataTypeClassName":fieldType,
		        "isActivitVariable":fieldTag
	        };
			
	        
			dataFieldsValues[index]=dataFieldValueVO;
		}
		
		WorkflowTasksInterface.submit(this.processInstanceUUID,this.activityUUID,dataFieldsValues,{
                callback:function(data){
                	this.onSubmitSuccess();
				}.createDelegate(this),
				errorHandler:function(message){
					this.onSubmitFail();
				}.createDelegate(this)
		});
    },
    
    closeThisPanel : function(){
    	var workflowTaskFormPanel = amalto.core.getTabPanel().activeTab;
		if (workflowTaskFormPanel) {
			amalto.core.getTabPanel().remove(this.activityUUID);
		}
    },
    
    onSubmitSuccess : function(){
    	alert("Successfully submitted! ");
    	this.refreshTasksStore();
		this.closeThisPanel();
    },
    
    onSubmitFail : function(){
    	alert("Submit failed! ");
		//this.closeThisPanel();
    }
    
});
