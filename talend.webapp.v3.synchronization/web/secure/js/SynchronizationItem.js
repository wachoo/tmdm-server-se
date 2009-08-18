/*
 * @include  "/com.amalto.webapp.core/web/secure/ext.ux/DWRProxy.js"
 * @include  "/com.amalto.webapp.core/web/secure/js/core.js"
 */
 
amalto.namespace("amalto.SynchronizationItem");

amalto.SynchronizationItem.SynchronizationItem=function(){
	
	//declare application local
    loadResource("/SynchronizationItem/secure/js/SynchronizationItemLocal.js", "amalto.SynchronizationItem.SynchronizationItemLocal" );
    
    loadResource("/SynchronizationItem/secure/css/SynchronizationItem.css", "" );

    var recordType = Ext.data.Record.create([
	  //{name: "id", type: "int"},
	  {name:"itemPOJOPK"},
	  {name: "itemPK"},
	  {name: "localRevisionID", type: "string"},
	  {name: "lastRunPlan", type: "string"},
	  {name: "status", type: "string"},
	  {name:"remoteIntances"},
	  {name:"remoteItemNames"},
	  {name:"remoteNodes"},
	  {name:"node"}
	  
	  ]);

     var store = new Ext.data.Store({
	    proxy: new Ext.data.DWRProxy(SynchronizationItemInterface.getSyncItems, true),
	    reader: new Ext.data.ListRangeReader( 
				{id:'itemPK', totalProperty:'totalSize',root: 'data'}, recordType),
	    remoteSort: true
	  });
    
    var grid;
    var pageSize =22;
    var pagingToolbar;
    
    function showSyncItems(){
		var lineMax = DWRUtil.getValue('lineMaxItems');
		if(lineMax==null || lineMax=="")
			lineMax=50;		
		pageSize=lineMax;
		pagingToolbar.pageSize=parseInt(pageSize);		    	
		store.load({params:{start:0, limit:pageSize}});
    };
    
    function show(){
    	var myColumns = [
    	//{header: "No", width: 25, sortable: true},
		{header: amalto.SynchronizationItem.SynchronizationItemLocal.get('GRID_COLUMN_1'), width: 100, sortable: true,dataIndex: 'itemPK'}, 
		{header: amalto.SynchronizationItem.SynchronizationItemLocal.get('GRID_COLUMN_2'), width: 100,  sortable: true,dataIndex: 'localRevisionID'}, 
		{header: amalto.SynchronizationItem.SynchronizationItemLocal.get('GRID_COLUMN_3'), width: 120,  sortable: true,dataIndex: 'lastRunPlan'},
		{header: amalto.SynchronizationItem.SynchronizationItemLocal.get('GRID_COLUMN_4'), width: 105, sortable: true,dataIndex: 'status'}
	    ];
   	    var columnModel = new Ext.grid.ColumnModel(myColumns);
   	
   		Ext.QuickTips.init();
   	    // create the Grid
   	    grid = new Ext.grid.GridPanel({
   			    store: store,
   			    cm: columnModel,
   	    		viewConfig: {
   			    	autoFill:true,
   			        forceFit: true
   			    },
   	   	        id:'syncDataGrid',
   	   	        closable:true,
   	   	        stripeRows: true,
   	   	        height:350,
   	   	        width:600,				   	   	      
   	   	        title:amalto.SynchronizationItem.SynchronizationItemLocal.get('GRID_TITLE'),
	   	   	    viewConfig: {
	   	   	        forceFit: true,
	
	   	   	        //Return CSS class to apply to rows depending upon data values
	   	   	        getRowClass: function(record, index) {	   	   	        	
	   	   	            var status = record.get('status');
	   	   	            if (status == 'MANUAL') {
	   	   	                return 'conflictItem';
	   	   	            } 
	   	   	        }
	   	   	    },

   	   	        listeners:
   	   	        {
   	    			'rowdblclick' : function(grid,rowIndex, e ){
   	    				loadResource("/SynchronizationItem/secure/js/SyncXMLPanel.js", "amalto.SynchronizationItem.SyncXMLPanel",function(){
   	    				   var record=grid.getStore().getAt(rowIndex);
   	    				   //if(record.data.status == 'MANUAL'){
   	    					   	    					
   	   	    			   var xmlData= amalto.SynchronizationItem.SyncXMLPanel(record.data,store);
   	   	    			   xmlData.init();  	    					
   	    				   //}
   	    				});
   	    				
   	    			}
   	   	        },
				tbar:[
						new Ext.form.TextField({
							id:'sync-criteria',
							//emptyText:LABEL_CRITERIA[language],
							emptyText:'*',
							listeners: {
			                	'specialkey': function(a, e) {
						            if(e.getKey() == e.ENTER) {
						            	showSyncItems();
						            } 
								}
			                }
						}),
						new Ext.Toolbar.Button({
							text:amalto.SynchronizationItem.SynchronizationItemLocal.get('BUTTON_SEARCH'),							
							handler:showSyncItems
						})
					],
				bbar:[
					       
				   pagingToolbar=new Ext.PagingToolbar({
								id:'sync-pagingtoolbar',
								pageSize: parseInt(pageSize),
						        store: store,
						        displayInfo: true,
						        displayMsg: amalto.SynchronizationItem.SynchronizationItemLocal.get('LABEL_DISPLAYING')+' {0} - {1} '+amalto.SynchronizationItem.SynchronizationItemLocal.get('LABEL_OF')+' {2}',
						        emptyMsg: amalto.SynchronizationItem.SynchronizationItemLocal.get('LABEL_NO_RESULT'),
						        width: 800,
						        items:[ 
						        	new Ext.Toolbar.Separator(),
						        	new Ext.Toolbar.TextItem(amalto.SynchronizationItem.SynchronizationItemLocal.get('LABEL_LINES_PER_PAGE')+" : "),
						        	new Ext.form.TextField({
				    					id:'lineMaxItems',
				    					value:pageSize,
				    					width:30,
				    					//disabled:true,
				    					listeners: {
						                	'specialkey': function(a, e) {
									            if(e.getKey() == e.ENTER) {
													
													showSyncItems();													
													//Ext.PagingToolbar toolbar=Ext.get('sync-pagingtoolbar');
													
									            } 
											}
						                }
						            })
						        ]
						    })
		             ]
   	   	    }); 
   	   	
   	   	
   	   	store.on('beforeload', function(){
   	   	 var criteria;	
   	   	 if(Ext.get('sync-criteria')!=null){
   	   	 	criteria= DWRUtil.getValue('sync-criteria');
   	   	 }else{
   	   	 	criteria="*";
   	   	 }
   	   	  
         Ext.apply(this.baseParams,{
          regex: criteria
         });
        });
             
		store.load({params:{start:0, limit:pageSize}});
		store.on('load', function(){
	    
		});  
    };
    return {
        init : function(){
        	//init application local
        	amalto.SynchronizationItem.SynchronizationItemLocal.init();
        	
	    	var tabPanel = amalto.core.getTabPanel();
	    	Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
	    	if(tabPanel.getItem('syncDataGrid') == undefined){
	    		show();		   		
	    	}else{
	    		showSyncItems();
	    	}
	      tabPanel.add(grid);
		  grid.show();
		  amalto.core.doLayout();   
        }  	
    };
}();

//Ext.onReady(SynchronizationItem.init, SynchronizationItem, true);