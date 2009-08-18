/*
 * @include  "/talend.ext.service.schedule/web/secure/js/SchedulePlan.js"
 */
 
Ext.namespace('amalto.SrvSchedule');
amalto.SrvSchedule.ScheduleManager = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.SrvSchedule.ScheduleManager.superclass.constructor
			.call(this);
};
Ext.extend(amalto.SrvSchedule.ScheduleManager, Ext.Panel, {
	initUIComponents : function() {
		
		this.recordType1 = Ext.data.Record.create([
		
		  {name: "schedulePlanId", type: "string"},
		  {name: "schedulePlanStatus", type: "string"},
		  {name: "schedulePlanDesc", type: "string"},
		  {name: "serviceName", type: "string"},
		  {name: "methodName", type: "string"},
		  {name: "parameters", type: "string"},
		  {name: "mode", type: "string"}
		 ]);
		
		this.store1 = new Ext.data.Store({
			proxy: new Ext.data.DWRProxy(SrvScheduleInterface.getSchedulePlanList, false),
	        reader: new Ext.data.ListRangeReader( 
				{id:'schedulePlanId', totalProperty:'totalSize',root: 'data'}, this.recordType1),
	        remoteSort: false,
	        autoLoad: true
		});
		
		this.checkboxSelectionModel1=new Ext.grid.CheckboxSelectionModel({singleSelect:true});

		this.gridPanel1 = new Ext.grid.GridPanel({
			store : this.store1,
			height:430,
			title : "Schedule Plans",
			selModel : this.checkboxSelectionModel1,
			border : false,
			columns : [
			new Ext.grid.RowNumberer(),
			this.checkboxSelectionModel1,
			{
				hidden : false,
				header : "ID",
				dataIndex : "schedulePlanId",
				sortable : true,
				width : 200
			}, {
				hidden : false,
				header : "Status",
				dataIndex : "schedulePlanStatus",
				sortable : true,
				width : 100,
				renderer:this.changeColor
			}, {
				hidden : false,
				header : "Description",
				dataIndex : "schedulePlanDesc",
				width : 500,
				sortable : true
				
			}],
			listeners:
   	   	    {
   	    			'rowdblclick' : function(grid,rowIndex, e ){
   	    				
   	    				this.onRowdblclick(grid,rowIndex, e);

   	    			}.createDelegate(this)
   	   	    }
		});

		Ext.apply(this, {
			title : "Service Schedule",
			layout : "anchor",
			items : [{
				items : [{					
					width : 150,
					fieldLabel : "Scheduler Status",
					xtype : "textfield",
					readOnly : true,
					//disabled : true,
					name : "status-srvscheduler",
					id : "status-srvscheduler"
				}],
				region : "",
				border : false,
				buttons : [{
					handler : function(button, event) {
						this.onStartClick(button, event);
					}.createDelegate(this),
					text : "Start"
				}, {
					handler : function(button, event) {
						this.onStandbyClick(button, event);
					}.createDelegate(this),
					text : "Standby"
				}, {
					handler : function(button, event) {
						this.onShutdownClick(button, event);
					}.createDelegate(this),
					text : "Shutdown"
				}, {
					handler : function(button, event) {
						this.onNewPlanClick(button, event);
					}.createDelegate(this),
					text : "New Plan"
				}, {
					handler : function(button, event) {
						this.onDeletePlanClick(button, event);
					}.createDelegate(this),
					text : "Delete Plan"
				}],
				buttonAlign : "left",
				bodyBorder : true,
				bodyStyle:'padding:5px',
				layout : "form"
			}, this.gridPanel1],
			tbar : new Ext.Toolbar([]),
			id : "SrvScheduleManager",
			closable:true
		});
	},
	
	changeColor : function (value){
		if (value == 'scheduling') {
            return "<span style='color:green;font-weight:bold;'>"+value+"</span>";
	    } else {
	        return "<span style='color:red;font-weight:bold;'>"+value+"</span>";
	    }
	},
	
	onRowdblclick : function (grid,rowIndex, e){
		var record=grid.getStore().getAt(rowIndex);
   	    var schedulePlanId = record.data.schedulePlanId;
   	    var schedulePlanStatus = record.data.schedulePlanStatus;
   	    var schedulePlanDesc = record.data.schedulePlanDesc;
   	    var serviceName = record.data.serviceName;
   	    var methodName = record.data.methodName;
   	    var parameters = record.data.parameters;
   	    var mode = record.data.mode;
   	    
   	    var pid = "Service Schedule Plan";
   	    
   	    var tabPanel = amalto.core.getTabPanel();
   	    var	srvPlan=tabPanel.getItem(pid);
   	    if( srvPlan == undefined){
   	       srvPlan=new amalto.SrvSchedule.SchedulePlan({'pid':pid,'srcStore':this.store1});
		   tabPanel.add(srvPlan);
   	    }
   	       srvPlan.show();
		   srvPlan.doLayout();
		   amalto.core.doLayout();
		   
		   srvPlan.resetData(schedulePlanId,schedulePlanStatus,schedulePlanDesc,serviceName,methodName,parameters,mode);
	},
	
	onDeletePlanClick : function(button, event){
		var sel=this.checkboxSelectionModel1.getSelections();
		if(sel.length==0){
			Ext.MessageBox.alert('Sorry', "Please select one plan at least! ");
			return;
		}
		
		for(var j=0; j<sel.length; j++)
		{
			var selectPlanId=sel[j].get('schedulePlanId');
			var schedulePlanStatus=sel[j].get('schedulePlanStatus');
			
			if(schedulePlanStatus=="scheduling"){
			    Ext.MessageBox.alert('Sorry', "Please unschedule the plan '"+selectPlanId+"' first! ");
			    continue;
			}
			
			SrvScheduleInterface.deleteSchedulePlan(selectPlanId,function(status){
			   this.deleteSchedulePlanFB(status);
		    }.createDelegate(this));
		}
	},
	
	deleteSchedulePlanFB : function(status){
		if(status==true){
	   	  Ext.MessageBox.alert('Info', "The plan deleted successfully! ");
	   	  this.store1.reload();
	   	}else{
	   	  Ext.MessageBox.alert('Sorry', "The plan deleted failed! ");
	   	}
	},
	
	onNewPlanClick : function(button, event){

		var pid = "Service Schedule Plan";
   	    				
   	    var tabPanel = amalto.core.getTabPanel();
   	    var	srvPlan=tabPanel.getItem(pid);
		if( srvPlan== undefined){
							
			srvPlan=new amalto.SrvSchedule.SchedulePlan({'pid':pid,'srcStore':this.store1});
			tabPanel.add(srvPlan);							
		
		}
				        
	    srvPlan.show();
		srvPlan.doLayout();
		amalto.core.doLayout();
		
		srvPlan.initData();
						
    },
    
    initData : function(){
	   this.updateStatusLabel();
    },
    
    updateStatusLabel : function(){
	   SrvScheduleInterface.getServiceSchedulerStatus(function(status){
	   	    DWRUtil.setValue('status-srvscheduler', status);
		});
    },
    
    onStartClick : function(button, event){
	   SrvScheduleInterface.startServiceScheduler(function(){
            this.updateStatusLabel();
		}.createDelegate(this));
    },
    
    onStandbyClick : function(button, event){
	   SrvScheduleInterface.standbyServiceScheduler(function(){
            this.updateStatusLabel();
		}.createDelegate(this));
    },
    
    onShutdownClick : function(button, event){
	   SrvScheduleInterface.shutdownServiceScheduler(function(){
            this.updateStatusLabel();
		}.createDelegate(this));
    }
   
});
