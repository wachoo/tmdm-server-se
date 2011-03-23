amalto.namespace("amalto.itemsbrowser");

amalto.itemsbrowser.SearchEntityPanel = function(config) {	
	Ext.applyIf(this, config);	
	var lineageEntities = config.lineageEntities;
	this.initUIComponents();
	amalto.itemsbrowser.SearchEntityPanel.superclass.constructor.call(this);
	var entityCB = Ext.getCmp("entityCB");
	entityCB.setValue(this.lineageEntities[0]);
};

Ext.extend(amalto.itemsbrowser.SearchEntityPanel, Ext.Panel, {
    initPageSize : 20,
    criteria : "",
	initUIComponents : function() {
	    
	 Ext.apply(Ext.form.VTypes, {  
		         dateRange: function(val, field){  
		             if(field.dateRange){  
		                 var beginId = field.dateRange.begin;  
		                 this.beginField = Ext.getCmp(beginId);  
		                 var endId = field.dateRange.end;  
		                 this.endField = Ext.getCmp(endId);  
		                 var beginDate = this.beginField.getValue();  
		                 var toDate = this.endField.getValue();  
		             } 
		             
		             if(beginDate!=""&&toDate!=""){
		            	 if(beginDate <= toDate){  
			                 return true;  
			             }else{  
			                 return false;  
			             }
		             }
		             
		             return true;
		         },    
		         dateRangeText: 'Start Data can not be greater than End Date '  
		     });  

		this.recordType = Ext.data.Record.create([
            {name: "date", mapping : "date", type: "string"},
            {name: "entity", mapping : "entity", type: "string"},
            {name: "key", mapping : "key", type: "string"}
		 ]);
		 
		this.store1 = new Ext.data.Store({
			 proxy: new Ext.data.DWRProxy(ItemsBrowserInterface.getItems, true),
	         reader: new Ext.data.ListRangeReader( 
				{id:'keys', totalProperty:'totalSize',root: 'data'}, this.recordType),
	         remoteSort: false
		});
		
		this.store1.on('beforeload', 
		            function(button, event) {
						this.onBeforeloadStore();
					}.createDelegate(this)
        );

		this.gridPanel1 = new Ext.grid.GridPanel({
			id:"searchEntityGridPanel",
			store : this.store1,
			border: false,
			loadMask:true, 
			layout : "fit",
			region : "center",
			selModel : new Ext.grid.RowSelectionModel({}),
			columns : [
				{
					hidden : false,
					header : "Date",
					dataIndex : "date",
					sortable : true
				},
				{
					hidden : false,
					header : "Entity",
					dataIndex : "entity",
					sortable : true
				},
				{
					hidden : false,
					header :  "Key",
					dataIndex :"key",
					sortable : true
				}
			],
			listeners:
   	   	    {
   	    			'rowdblclick' : function(grid,rowIndex, e ){
   	    				var record=grid.getStore().getAt(rowIndex);
   	    				var ids = record.data.ids;
   	    				var tabPanel = amalto.core.getTabPanel();
   	    				//@yguo, should do something
						amalto.core.doLayout();
   	    				
   	    				
   	    			}
   	   	    },
			bbar : new Ext.PagingToolbar({
				id:"searchEntityPagingToolbar",
				displayMsg : "displayMsg",
				displayInfo: true,
				store : this.store1,
				xtype : "paging",
				emptyMsg :"emptyMsg",
				pageSize : this.initPageSize,
				items:[ 
		        	new Ext.Toolbar.Separator(),
		        	new Ext.Toolbar.TextItem("Number of lines per page :"),
		        	new Ext.form.TextField({
    					id:'updateRLineMaxItems',
    					value:this.initPageSize,
    					width:30,
    					listeners: {
		                	'specialkey': function(a, e) {
					            if(e.getKey() == e.ENTER) {
			                		var lineMax = DWRUtil.getValue('updateRLineMaxItems');
									if(lineMax==null || lineMax=="")lineMax=20;
									Ext.getCmp("searchEntityPagingToolbar").pageSize=parseInt(lineMax);
									Ext.getCmp("searchEntityGridPanel").store.reload({params:{start:0, limit:lineMax}});
					            } 
							},
							'change':function(field,newValue,oldValue){
                                
                                if(newValue != oldValue){
                                    lineMax = newValue;
                                    if(lineMax==null || lineMax=="") 
                                        lineMax=20;
                                    Ext.getCmp("searchEntityPagingToolbar").pageSize=parseInt(lineMax);
                                    Ext.getCmp("searchEntityGridPanel").store.reload({params:{start:0, limit:lineMax}});
                                }
                            
                            }
		                }
		            })
		        ]
			})
		});
		
		Ext.apply(this, {			
			layout : "border",
			title : "Search Entity",
			items : [this.gridPanel1, {
				frame : false,
				height : 150,
				layout : "fit",
				split : true,
				title : "Search Panel",
				collapsible : true,
				border: false,
				items : [{
					height : 30,
					layout : "column",
					items : [{
						columnWidth : ".5",
						layout : "form",
						items : [{
							id : 'entityCB',
							name : "entity",
							fieldLabel : "entity",
							store: this.lineageEntities,
							xtype : "combo",
							allowBlank : false,
							editable: false,
							triggerAction : 'all',
							listeners : {
                               'specialkey' : function(field, event) {
                               	                  this.onSearchKeyClick(field, event);
                                              }.createDelegate(this)
                                        }
						}, 
						{
							id : "fromDate",
							name : "fromDate",
							fieldLabel : "From",
							xtype : "datefield",
							format : "Y-m-d H:i:s",
							width: 150,
							readOnly : false,
							listeners : {
                               'specialkey' : function(field, event) {
                               	                  this.onSearchKeyClick(field, event);
                                              }.createDelegate(this)
                                        },
                           vtype: 'dateRange',
                           dateRange: {begin: 'fromDate', end: 'toDate'}
						},
						{
							name : "keyWords",
							//emptyText : "Select a source...",
							fieldLabel :"keyWords",
							xtype : "textfield",
							displayField:'text',
					        valueField:'value',   
					        typeAhead: true,
					        triggerAction: 'all',
					        mode: 'local',
					        listeners : {
                               'specialkey' : function(field, event) {
                               	                  this.onSearchKeyClick(field, event);
                                              }.createDelegate(this)
                                        }
						}],
						border : false
					}, {
						columnWidth : ".5",
						layout : "form",
						items : [{
							name : "key",
							fieldLabel : "key",
							xtype : "textfield",
							listeners : {
                               'specialkey' : function(field, event) {
                               	                  this.onSearchKeyClick(field, event);
                                              }.createDelegate(this)
                                        }
						}, 
						{
							id : "toDate",
							name : "toDate",
							fieldLabel : "To",
							xtype : "datefield",
							format : "Y-m-d H:i:s",
							width: 150,
							readOnly : false,
							listeners : {
                               'specialkey' : function(field, event) {
                               	                  this.onSearchKeyClick(field, event);
                                              }.createDelegate(this)
                                        },
                            vtype: 'dateRange',
                            dateRange: {begin: 'fromDate', end: 'toDate'}
						}],
						border : false
					}],
					border : false
				}],
				region : "north",
				bodyStyle:'padding:5px',
				buttons : [{
					handler : function(button, event) {
						this.onResetBtnClick(button, event);
					}.createDelegate(this),
					text : "reset"
				},{
					handler : function(button, event) {
						this.onSearchBtnClick(button, event);
					}.createDelegate(this),
					text : "search"
				},{	
					handler: function() {					
							var curcriteria = this.getRequestParam();
							//@yguo, should export the grid 
							this.exporting(curcriteria);
					}.createDelegate(this),
					text : "export"
			}]
			}],
			id : "SearchEntityPanel",
			closable:true,
			border:false
		});	
	},
	
	exporting:function(myParams){
		var cluster = DWRUtil.getValue('datacluster-select');
		
		window.location.href="/itemsbrowser/secure/ExportingServlet?cluster=" + cluster + "&params=" + myParams;	
	},
    
	initListData : function(itemsBroswer){
		this.isItemsBrowser = itemsBroswer;
		this.store1.load({params:{start:0, limit:this.initPageSize}});
		
    },
    
    isItemsBrowser : false,
    
    doSearchList : function(itemsBrowser){
    	this.isItemsBrowser = itemsBrowser;
		var pageSize=Ext.getCmp("searchEntityPagingToolbar").pageSize;
		this.store1.reload({params:{start:0, limit:pageSize}});
    },
    
    onSearchBtnClick : function(button, event){
    	
		this.doSearchList(false);
		
    },
    
    onSearchKeyClick : function(field, event){
    	
    	if (event.getKey() == Ext.EventObject.ENTER) {
	      this.doSearchList(false);
	    }
		
    },
    
    setSearchCriteria : function(conceptValue,keyValue,keyWordsValue,startDateValue,endDateValue){
		if(conceptValue!='')DWRUtil.setValue('entity',conceptValue);
		if(keyValue!='')DWRUtil.setValue('key',keyValue);
		if(keyWordsValue!='')DWRUtil.setValue('keyWords',keyWordsValue);
		if(startDateValue!='')DWRUtil.setValue('fromDate',startDateValue);
		if(endDateValue!='')DWRUtil.setValue('toDate',endDateValue);
		
    },
    
    onResetBtnClick : function(button, event){
		DWRUtil.setValue('key','');
		DWRUtil.setValue('keyWords','');
        DWRUtil.setValue('fromDate','');
        DWRUtil.setValue('toDate','');
        this.criteria = "";
    },
    
    getRequestParam : function(){
    	var requestParam="";

		var entity = DWRUtil.getValue('entity');
		if(entity!="")requestParam+=",entity:'"+entity+"'";
		var key = DWRUtil.getValue('key');
		if(key!="")requestParam+=",key:'"+key+"'";
		var keyWords = DWRUtil.getValue('keyWords');
		if(keyWords!="")requestParam+=",keyWords:'"+keyWords+"'";
		var fromDate = DWRUtil.getValue('fromDate');
		if(fromDate!="")requestParam+=",fromDate:'"+fromDate+"'";
		var toDate = DWRUtil.getValue('toDate');
		if(toDate!="")requestParam+=",toDate:'"+toDate+"'";
		if(this.isItemsBrowser == true) requestParam += ",itemsBrowser:'" + this.isItemsBrowser +"'";

		if(requestParam!=""){
		requestParam=requestParam.substring(1)
		requestParam="{"+requestParam+"}";
		}
		
		return requestParam;
    },
    
    onBeforeloadStore : function(){    	    	
   	   	 	this.criteria=this.getRequestParam();
   	   	 	//alert(criteria);
            Ext.apply(this.store1.baseParams,{
              regex: this.criteria
            });
    }
}); 
