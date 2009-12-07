/*
 * @include  "/com.amalto.webapp.core/web/secure/ext.ux/DWRProxy.js"
 * @include  "/com.amalto.webapp.core/web/secure/js/core.js"
 */
 
amalto.namespace("amalto.ItemsTrash");
//contextid.appid
amalto.ItemsTrash.ItemsTrash=function(){
	 loadResource("/ItemsTrash/secure/js/ItemsTrashLocal.js", "amalto.ItemsTrash.ItemsTrashLocal" );
     loadResource("/ItemsTrash/secure/css/ItemsTrash.css", "" );

    var recordType = Ext.data.Record.create([
   
	  {name:"itemPK"},
	  {name:"uniqueId"},
	  {name:"conceptName"},
	  {name:"uniqueId"},
	  {name:"ids"},
	  {name:"partPath"},
	  {name:"insertionTime"},
	  {name:"insertionUserName"},
	  {name:"projection"},
	  {name:"revisionID"},
	  {name: null, type: "string"},
	  {name: null, type: "string"}
	  ]);

     var store = new Ext.data.Store({
	    proxy: new Ext.data.DWRProxy(ItemsTrashInterface.getTrashItems, true),
	    reader: new Ext.data.ListRangeReader( 
				{id:'uniqueId', totalProperty:'totalSize',root: 'data'}, recordType),
	    remoteSort: false
	  });
    var grid;
    var pageSize =22;
    var pagingToolbar;
    
    	
		function deleteItem(){
			return "<img src='img/genericUI/delete.gif' border=\"0\" />";
		}
		function restore(){
			return "<img src='img/genericUI/restore.gif' border=\"0\" />";
		}
    
    function showTrashItems(){
		var lineMax = DWRUtil.getValue('lineMaxItems');
		var criteria = DWRUtil.getValue('trash-criteria');
		if(lineMax==null || lineMax=="")
			lineMax=50;		
		pageSize=lineMax;
		pagingToolbar.pageSize=parseInt(pageSize);		    	
		store.load({params:{start:0, limit:pageSize}});
    };

    function toDelete(pk,partPath,revisionID,conceptName,ids,projection){
    	Ext.MessageBox.confirm("confirm",amalto.ItemsTrash.ItemsTrashLocal.get("delete_conform"),function de(e){
    		if(e.toLocaleString()=="yes"){
    				if(projection!=""){
    					var startPos=projection.indexOf("/imageserver/")+"/imageserver/".length;
    					var endPos=projection.indexOf('?');
    					if(startPos!=-1&&endPos!=-1){
    					var uri=projection.substring(startPos,endPos); 
        Ext.Ajax.request({  
           	url:'/imageserver/secure/ImageDeleteServlet?uri='+uri,
             method: 'post',  
             callback: function(options, success, response) {  
             }  
        });    }
    				}
    			ItemsTrashInterface.removeDroppedItem(pk,partPath,revisionID,conceptName,ids,showTrashItems);
    			}
    		}) ;
    }
    function toRestore(pk,partPath,revisionID,conceptName,ids){
    	
    	Ext.MessageBox.confirm("confirm",amalto.ItemsTrash.ItemsTrashLocal.get("restore_conform"),function re(en){
    		if(en=="yes")
    			ItemsTrashInterface.recoverDroppedItem(pk,partPath,revisionID,conceptName,ids,showTrashItems);
    		});
    }

    function show(){
    	var myColumns = [
    	//{header: "No", width: 25, sortable: true},
		{header: amalto.ItemsTrash.ItemsTrashLocal.get('dataClusterName'),  sortable: true,dataIndex: 'itemPK'}, 
		{header: amalto.ItemsTrash.ItemsTrashLocal.get('revisionID'),   sortable: true,dataIndex: 'revisionID'}, 
		{header: amalto.ItemsTrash.ItemsTrashLocal.get('conceptName'),   sortable: true,dataIndex: 'conceptName'},
		{header: amalto.ItemsTrash.ItemsTrashLocal.get('Ids'),  sortable: true,dataIndex: 'ids'},
		{header: amalto.ItemsTrash.ItemsTrashLocal.get('partPath'), sortable: true,dataIndex: 'partPath'},
		{header: amalto.ItemsTrash.ItemsTrashLocal.get('UserName'), sortable: true,dataIndex: 'insertionUserName'},
		{header: amalto.ItemsTrash.ItemsTrashLocal.get('Date'),  sortable: true,dataIndex: 'insertionTime'},
		{header: amalto.ItemsTrash.ItemsTrashLocal.get('delete'), sortable: false, renderer:deleteItem},
		{header: amalto.ItemsTrash.ItemsTrashLocal.get('restore'), sortable: false, renderer:restore},
		{header: amalto.ItemsTrash.ItemsTrashLocal.get('projection'),   sortable: true, dataIndex: 'projection', hidden:true}
	    ];
   	    var columnModel = new Ext.grid.ColumnModel(myColumns);
   	    columnModel.defaultWidth;
   	
   		Ext.QuickTips.init();
   	    // create the Grid
   	    grid = new Ext.grid.GridPanel({
   			    store: store,
   			    cm: columnModel,
   	    		viewConfig: {
   			    	autoFill:true,
   			        forceFit: true
   			    },
   	   	        id:'trashDataGrid',
   	   	        closable:true,
   	   	        stripeRows: true,
   	   	        height:350,
   	   	        width:600,				   	   	      
   	   	        title: amalto.ItemsTrash.ItemsTrashLocal.get('title'),
	   	   	    viewConfig: {
	   	   	        forceFit: true
	   	   	    },

   	   	        listeners:
   	   	        {
		   						cellclick: function(g, rowIndex,  columnIndex, e){
		   						var record = g.getStore().getAt(rowIndex);
		   						if(columnIndex==7){
		   							
		   							toDelete(record.data.itemPK,record.data.partPath,record.data.revisionID,record.data.conceptName,record.data.ids,record.data.projection);
		   						}
		   						else{ 
		   							if(columnIndex==8){
		   								toRestore(record.data.itemPK,record.data.partPath,record.data.revisionID,record.data.conceptName,record.data.ids);
		   							}
		   						}
		   						
		                	} 	
		   
   	   	        },
				tbar:[
						new Ext.form.TextField({
							id:'trash-criteria',
							emptyText:'*',
							listeners: {
			                	'specialkey': function(a, e) {
						            if(e.getKey() == e.ENTER) {
						            	showTrashItems();
						            } 
								}
			                }
			                
						}),
						new Ext.Toolbar.Button({
							text:amalto.ItemsTrash.ItemsTrashLocal.get('search'),
							tooltip:amalto.ItemsTrash.ItemsTrashLocal.get('serarch_tooltip'),
							handler:showTrashItems
						})
					],
				bbar:[
					       
				   pagingToolbar=new Ext.PagingToolbar({
								id:'trash-pagingtoolbar',
								pageSize: parseInt(pageSize),
						        store: store,
						        displayInfo: true,
						       // displayMsg: 'Displaying items'+' {0} - {1} '+'of'+' {2}',
						        emptyMsg: 'No result',
						        width: 800,
						        items:[ 
						        	new Ext.Toolbar.Separator(),
						        	new Ext.Toolbar.TextItem(amalto.ItemsTrash.ItemsTrashLocal.get('lines_per_page')+" : "),
						        	new Ext.form.TextField({
				    					id:'lineMaxItems',
				    					value:pageSize,
				    					width:30,
				    					//disabled:true,
				    					listeners: {
						                	'specialkey': function(a, ee1) {
//						                		alert("test "+ee.getKey());
						                		
									            if(ee1.getKey() == ee1.ENTER) {
									            	
													showTrashItems();													
									            } 
											}
						                }
						            })
						        ]//items
						    })
		             ]
   	   	    }); 
   	   	
	   	
   	   	store.on('beforeload', function(){
   	   	 var criteria;	
   	   	 if(Ext.get('trash-criteria')!=null){
   	   	 	criteria= DWRUtil.getValue('trash-criteria');
   	   	 }else{
   	   	 	criteria="*";
   	   	 }
   	   	  
         Ext.apply(this.baseParams,{
          regex: criteria
         });
        });
           	
		store.load({params:{start:0, limit:pageSize}});
    };
    return {
        init : function(){
        	amalto.ItemsTrash.ItemsTrashLocal.init();
        	
        	var tabPanel = amalto.core.getTabPanel();
	    	Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
	    	if(tabPanel.getItem('trashDataGrid') == undefined){
	    		show();		   		
	    	}else{
	    		showTrashItems();
	    	}
	      tabPanel.add(grid);
		  grid.show();
		  amalto.core.doLayout(); 	   		
	    	 
        } 	//init
    };//return
}();//ItemStrashPlan

