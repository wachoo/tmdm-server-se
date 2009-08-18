/*
 * @include  "/com.amalto.webapp.core/web/secure/ext.ux/DWRProxy.js"
 * @include  "/com.amalto.webapp.core/web/secure/js/core.js"
 */
Ext.namespace('amalto.SrvSchedule');
amalto.SrvSchedule.SchedulePlan = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.SrvSchedule.SchedulePlan.superclass.constructor.call(this);
};
Ext.extend(amalto.SrvSchedule.SchedulePlan, Ext.Panel, {
	
	modifyLock:false, 
	
	initUIComponents : function() {
		
	  this.serviceNameStore = new Ext.data.Store({
         proxy: new Ext.data.DWRProxy(SrvScheduleInterface.getRuntimeServiceNames , false),
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
       
       this.methodNameStore = new Ext.data.Store({
         proxy: new Ext.data.DWRProxy(SrvScheduleInterface.getAvailableMethodNames , false),
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
			title : this.pid,
			id : this.pid,
			layout : "form",
			autoScroll : true,
			closable:true,
			items : [{
				height : 140,
				layout : "form",
				title : "Basic Information",
				items : [{
					width : 250,
					fieldLabel : "Schedule Plan ID",
					xtype : "textfield",
					readOnly : true,
					name : "schedulePlanId"
					//id : "schedulePlanId"
				}, {
					fieldLabel : "Status",
					xtype : "textfield",
					readOnly : true,
					name : "schedulePlanStatus"
					//id : "schedulePlanStatus"
				}, {
					width : 450,
					height : 50,
					fieldLabel : "Description",
					xtype : "textarea",
					name : "schedulePlanDesc"
					//id : "schedulePlanDesc"
				}],
				xtype : "fieldset"
			}, {
				height : 340,
				title : "Invoke Information",
				layout : "form",
				items : [{
					editable : false,
					fieldLabel : "Service Name",
					xtype : "combo",
					name : "serviceName",
					//id : "serviceName",
					store: this.serviceNameStore,
			        valueField : "value",
			        displayField: "text",
			        typeAhead: true,
			        forceSelection: true,
			        triggerAction: 'all',
			        listeners : {
                               'select' : function() {
                               	                     this.loadTemplateOfParameter();
                                                     }.createDelegate(this)
                                }
				}, {
					editable : false,
					fieldLabel : "Method Name",
					xtype : "combo",
					name : "methodName",
					//id : "methodName",
					store: this.methodNameStore,
			        valueField : "value",
			        displayField: "text",
			        typeAhead: true,
			        forceSelection: true,
			        triggerAction: 'all',
			        listeners : {
                               'select' : function() {
                               	                     this.loadTemplateOfParameter();
                                                     }.createDelegate(this)
                                }
				}, {
					width : 550,
					height : 250,
					fieldLabel : "Parameters",
					xtype : "textarea",
					name : "parameters"
					//id : "parameters"
				}],
				xtype : "fieldset"
			}, {
				height : 60,
				layout : "form",
				title : "Schedule Information",
				items : [{
					width : 200,
					fieldLabel : "Mode",
					xtype : "textfield",
					name : "mode",
					//id : "mode",
					value : "0 0 0 * * ?"
				}],
				xtype : "fieldset"
			}],
			buttons : [{
				handler : function(button, event) {
					this.onInvokeClick(button, event);
				}.createDelegate(this),
				text : "Invoke"
			}, {
				handler : function(button, event) {
					this.onSaveClick(button, event);
				}.createDelegate(this),
				text : "Save"
			}, {
				handler : function(button, event) {
					this.onScheduleClick(button, event);
				}.createDelegate(this),
				text : "Schedule"
			}, {
				handler : function(button, event) {
					this.onUnscheduleClick(button, event);
				}.createDelegate(this),
				text : "Unschedule"
			}]
		});
	},
	
	initData : function(){
		
	   //reset
	   DWRUtil.setValue('schedulePlanId',"");
	   DWRUtil.setValue('schedulePlanStatus',"");
	   DWRUtil.setValue('schedulePlanDesc',"");
	   DWRUtil.setValue('serviceName',"");
	   DWRUtil.setValue('methodName',"");
	   DWRUtil.setValue('parameters',"");
	   DWRUtil.setValue('mode',"0 0 0 * * ?");	

	   this.updateLockStatus();
	   
    },
    
    resetData : function(iSchedulePlanId,iSchedulePlanStatus,iSchedulePlanDesc,iServiceName,iMethodName,iParameters,iMode){
       //reset
	   DWRUtil.setValue('schedulePlanId',"");
	   DWRUtil.setValue('schedulePlanStatus',"");
	   DWRUtil.setValue('schedulePlanDesc',"");
	   DWRUtil.setValue('serviceName',"");
	   DWRUtil.setValue('methodName',"");
	   DWRUtil.setValue('parameters',"");
	   DWRUtil.setValue('mode',"0 0 0 * * ?");
		
	   DWRUtil.setValue('schedulePlanId',iSchedulePlanId);
	   DWRUtil.setValue('schedulePlanStatus',iSchedulePlanStatus);
	   DWRUtil.setValue('schedulePlanDesc',iSchedulePlanDesc);
	   DWRUtil.setValue('serviceName',iServiceName);
	   DWRUtil.setValue('methodName',iMethodName);
	   DWRUtil.setValue('parameters',iParameters);
	   DWRUtil.setValue('mode',iMode);
	   
	   this.updateLockStatus();
    },
    
    updateLockStatus : function(){
	   var schedulePlanStatus = DWRUtil.getValue('schedulePlanStatus');
	   if(schedulePlanStatus=="scheduling"){
	      this.modifyLock=true;
	   }else if(schedulePlanStatus==""||schedulePlanStatus=="unschedule"){
	   	  this.modifyLock=false;
	   }
    },
    
    updateLockStatusDirectly : function(status){
	      this.modifyLock=status;
    },
    
    loadTemplateOfParameter : function(){
		var serviceName = DWRUtil.getValue('serviceName');
		var methodName = DWRUtil.getValue('methodName');
	    if(serviceName!=""&&methodName!=""){
	    	SrvScheduleInterface.loadParameterTemplate(serviceName,methodName,function(data){
	    	    DWRUtil.setValue('parameters',data);
	    	});
	    }
    },
    
    onInvokeClick : function(button, event){
    	//check parameters
    	var fields="";
    	var serviceName = DWRUtil.getValue('serviceName');
		var methodName = DWRUtil.getValue('methodName');
		var parameters = DWRUtil.getValue('parameters');
		if(serviceName=="")fields+="'Service Name' ";
		if(methodName=="")fields+="'Method Name' ";
		if(fields!=""){
		   Ext.MessageBox.alert('Sorry', fields+"can not be empty! ");
		   return;
		}
		
		SrvScheduleInterface.invokeService(serviceName,methodName,parameters,function(data){
		             this.onInvokeServiceFB(data);
        }.createDelegate(this));
		
    },
    
    onInvokeServiceFB : function(data){
    	//TODO ADD A PROCESS BAR HERE
    	
    	if(data==""){
    	   Ext.MessageBox.alert('Info', "INVOKE SUCCESS! ");
    	   return;
    	}

    	if(this.invokeResultConsole){
			    this.invokeResultConsole.hide();
			    this.invokeResultConsole.destroy();
		}	
	    
		this.invokeResultConsole=new Ext.Window({
			                layout:'fit',
			                width:500,
                            height:500,
		                    title:"Invoke Result",
			                closeAction:'hide',
			                plain: true,
                            maximizable:true,
                            items: [new Ext.Panel({
                            	        layout:'fit',
					                    items: [
						                    {
						                    	
												xtype : "textarea",
												readOnly : true,
												name : "ResultConsole",
												//id : "ResultConsole",
												value : data
												//style:"background-color:yellow"
											}
						                ]
					                })]
                            //modal:true
                            
                      });
         this.invokeResultConsole.show();
			
    },
    
    onSaveClick :  function(button, event){
    	//alert(this.modifyLock);
    	if(this.modifyLock){
    	   Ext.MessageBox.alert('Sorry', "This operation has been locked, since this plan is under scheduling! ");
		   return;
    	}
    	
    	//check parameters
    	var fields="";
    	
    	var schedulePlanId = DWRUtil.getValue('schedulePlanId');
    	var schedulePlanStatus = DWRUtil.getValue('schedulePlanStatus');
    	var schedulePlanDesc = DWRUtil.getValue('schedulePlanDesc');
    	
    	var serviceName = DWRUtil.getValue('serviceName');
		var methodName = DWRUtil.getValue('methodName');
		var parameters = DWRUtil.getValue('parameters');
		
		var mode = DWRUtil.getValue('mode');
		
		if(serviceName=="")fields+="'Service Name' ";
		if(methodName=="")fields+="'Method Name' ";
		if(mode=="")fields+="'Mode' ";
		if(fields!=""){
		   Ext.MessageBox.alert('Sorry', fields+"can not be empty! ");
		   return;
		}
		
		SrvScheduleInterface.saveServiceSchedulePlan(schedulePlanId,schedulePlanStatus,schedulePlanDesc,serviceName,methodName,parameters,mode,function(data){
		             
			         this.saveServiceSchedulePlanFB(data);
			         
        }.createDelegate(this));
    	
    },
    
    saveServiceSchedulePlanFB : function(data){
    	
    	//update status
		DWRUtil.setValue('schedulePlanId',data[0]);
		DWRUtil.setValue('schedulePlanStatus',data[1]);
		DWRUtil.setValue('parameters',data[2]);
		             
		Ext.MessageBox.alert('Info', "The plan has been saved successfully!");
		
		if(this.srcStore!=undefined&&this.srcStore!=null)this.srcStore.reload();
		             
    },
  
    onScheduleClick :  function(button, event){
    	
    	var schedulePlanId = DWRUtil.getValue('schedulePlanId');
    	if(schedulePlanId==""){
    		Ext.MessageBox.alert('Sorry', "SchedulePlanId is empty, please save this plan first! ");
    		return;
    	}
    	
    	//check status
    	var schedulePlanStatus = DWRUtil.getValue('schedulePlanStatus');
    	if(schedulePlanStatus=="scheduling"){
    		Ext.MessageBox.alert('Sorry', "This schedule plan is already under scheduling! ");
    		return;
    	}
    	
//    	//check parameters
//    	var fields="";
//    	var serviceName = DWRUtil.getValue('serviceName');
//		var methodName = DWRUtil.getValue('methodName');
//		var parameters = DWRUtil.getValue('parameters');
//		var mode = DWRUtil.getValue('mode');
//		if(serviceName=="")fields+="'Service Name' ";
//		if(methodName=="")fields+="'Method Name' ";
//		if(mode=="")fields+="'Mode' ";
//		if(fields!=""){
//		   Ext.MessageBox.alert('Sorry', fields+"can not be empty! ");
//		   return;
//		}
		
		SrvScheduleInterface.scheduleServiceSchedulePlan(schedulePlanId,function(data){
			         this.onScheduleClickFB(data);     
        }.createDelegate(this));
       
    },
    
    onScheduleClickFB : function(data){
    	             if(data==true){
		             	//update status
                        DWRUtil.setValue('schedulePlanStatus','scheduling');
		             	Ext.MessageBox.alert('Info', "The plan has been scheduled successfully!");
		             	this.updateLockStatusDirectly(true);
		             	if(this.srcStore!=undefined&&this.srcStore!=null)this.srcStore.reload();
		             }else{
		             	Ext.MessageBox.alert('Error', "Scheduling this plan failed!");
		             	return;
		             }
    },
    
    onUnscheduleClick:  function(button, event){
    	
    	
    	var schedulePlanId = DWRUtil.getValue('schedulePlanId');
    	if(schedulePlanId==""){
    		Ext.MessageBox.alert('Sorry', "SchedulePlanId is empty, please save this plan first! ");
    		return;
    	}
    	
    	//check status
    	var schedulePlanStatus = DWRUtil.getValue('schedulePlanStatus');
    	if(schedulePlanStatus==""||schedulePlanStatus=="unschedule"){
    		Ext.MessageBox.alert('Sorry', "This schedule plan has already been unscheduled! ");
    		return;
    	}
    	
    	SrvScheduleInterface.unScheduleServiceSchedulePlan(schedulePlanId,function(data){
    		         this.onUnscheduleClickFB(data);
        }.createDelegate(this));
        
    },
    
    onUnscheduleClickFB : function(data){
    	             if(data==true){
		             	//update status
                        DWRUtil.setValue('schedulePlanStatus','unschedule');
		             	Ext.MessageBox.alert('Info', "The plan has been unscheduled successfully!");
		             	this.updateLockStatusDirectly(false);
		             	if(this.srcStore!=undefined&&this.srcStore!=null)this.srcStore.reload();
		             }else{
		             	Ext.MessageBox.alert('Error', "Unscheduling this plan failed!");
		             	return;
		             }
    }

});
