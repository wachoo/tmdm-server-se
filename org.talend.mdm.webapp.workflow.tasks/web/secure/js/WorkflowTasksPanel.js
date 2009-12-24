Ext.namespace('amalto.workflowtasks');
amalto.workflowtasks.WorkflowTasksPanel = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.workflowtasks.WorkflowTasksPanel.superclass.constructor.call(this);
};
Ext.extend(amalto.workflowtasks.WorkflowTasksPanel, Ext.Panel, {
	initPageSize:20,
	initUIComponents : function() {
		
		this.recordType = Ext.data.Record.create([

		  {name: "processDefineUUIDValue", type: "string"},
		  {name: "processInstanceUUIDValue", type: "string"},
		  {name: "activityUUIDValue", type: "string"},
		  {name: "processTag", type: "string"},
		  {name: "activityName", type: "string"},
		  {name: "activityReadyDate", type: "string"},
		  {name: "activityState", type: "string"}
		  
		 ]);
		
		this.store1 = new Ext.data.Store({
			 proxy: new Ext.data.DWRProxy(WorkflowTasksInterface.getTasksPrintList, true),
	         reader: new Ext.data.ListRangeReader( 
				{id:'activityUUIDValue', totalProperty:'totalSize',root: 'data'}, this.recordType),
	         remoteSort: false
		});
		
		this.store1.on('beforeload', 
		            function(button, event) {
						this.onBeforeloadStore();
					}.createDelegate(this)
        );
		
		this.gridPanel1 = new Ext.grid.GridPanel({
			id:"workflowTasksGridPanel",
			store : this.store1,
			columns : [{
				hidden : false,
				sortable : true,
				dataIndex : "processTag",
				header : "Process"
			}, {
				hidden : false,
				sortable : true,
				dataIndex : "activityName",
				header : "Task Name"
			}, {
				hidden : false,
				sortable : true,
				dataIndex : "activityReadyDate",
				header : "Ready Date"
			}, {
				hidden : false,
				sortable : true,
				dataIndex : "activityState",
				header : "Task State",
				renderer:this.changeColor
			}],
			viewConfig: {forceFit:true},
			selModel : new Ext.grid.RowSelectionModel({}),
			listeners:
   	   	    {
   	    			'rowdblclick' : function(grid,rowIndex, e ){
   	    				
   	    				      this.onRowdblclick(grid,rowIndex, e);

   	    			    }.createDelegate(this)
   	    	
   	   	    },
			bbar : new Ext.PagingToolbar({
				id : "workflowTasksGirdPagingToolbar",
				store : this.store1,
				emptyMsg : "No data to display",
				displayMsg : "Displaying {0} - {1} of {2}",
				displayInfo: true,
				xtype : "paging",
				pageSize : this.initPageSize,
				items:[ 
		        	new Ext.Toolbar.Separator(),
		        	new Ext.Toolbar.TextItem("Number of lines per page : "),
		        	new Ext.form.TextField({
    					id:'lineMaxItems',
    					value:this.initPageSize,
    					width:30,
    					listeners: {
		                	'specialkey': function(a, e) {
					            if(e.getKey() == e.ENTER) {
			                		var lineMax = DWRUtil.getValue('lineMaxItems');
									if(lineMax==null || lineMax=="")lineMax=20;
									Ext.getCmp("workflowTasksGirdPagingToolbar").pageSize=parseInt(lineMax);
									Ext.getCmp("workflowTasksGridPanel").store.reload({params:{start:0, limit:lineMax}});
					            } 
							}
		                }
		            })
		        ]
				
			})
		});
		
		Ext.apply(this, {
			id : "WorkflowTasksPanel",
			title : "Workflow Tasks List",
			layout : "fit",
			items : [this.gridPanel1],
			closable: true
		});
		
	},
	
	onRowdblclick : function (grid,rowIndex, e){
		
		                var record=grid.getStore().getAt(rowIndex);
   	    				
   	    				var processDefineUUID=record.data.processDefineUUIDValue;
   	    				var processInstanceUUID=record.data.processInstanceUUIDValue;
   	    				var activityUUID=record.data.activityUUIDValue;
   	    				var activityState=record.data.activityState;
   	    				var isReadonly=true;
   	    				if(activityState!=null&&activityState=='READY'){
   	    					isReadonly=false;
   	    				}
   	    				
   	    				var dataFieldsDefinition;
   	    				DWREngine.setAsync(false); 
                        WorkflowTasksInterface.getDataFields(processDefineUUID,processInstanceUUID,activityUUID,language,
                          function(result){
                            dataFieldsDefinition=result;
                          }
                        );
		                DWREngine.setAsync(true);
   	    				
   	    				var tabPanel = amalto.core.getTabPanel();
				   	    var	taskForm=tabPanel.getItem(activityUUID);
						if( taskForm== undefined){
											
							taskForm=new amalto.workflowtasks.WorkflowTaskFormPanel({'processDefineUUID':processDefineUUID,
							                                                         'processInstanceUUID':processInstanceUUID,
							                                                         'activityUUID':activityUUID,
							                                                         'isReadonly':isReadonly,
							                                                         'dataFieldsDefinition':dataFieldsDefinition,
							                                                         'tasksStore':this.store1});
							tabPanel.add(taskForm);							
						
						}
								        
					    taskForm.show();
						taskForm.doLayout();
						amalto.core.doLayout();
						
						taskForm.initData();
	},
	
	onBeforeloadStore : function(){
    	    var criteria=".*";	
            Ext.apply(this.store1.baseParams,{
              regex: criteria
            });
    },
	
	initData : function(){
		
        DWREngine.setAsync(false); 
        WorkflowTasksInterface.doubleLogin(function(){});
		DWREngine.setAsync(true);
		
		this.store1.load({params:{start:0, limit:this.initPageSize}});
    },
    
    changeColor : function (value){
		if (value == 'READY') {
            return "<span style='color:green;font-weight:bold;'>"+value+"</span>";
	    } else {
	        return "<span style='color:gray'>"+value+"</span>";
	    }
	}
});
