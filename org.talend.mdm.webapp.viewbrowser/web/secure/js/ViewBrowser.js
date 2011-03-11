amalto.namespace("amalto.viewbrowser");

amalto.viewbrowser.ViewBrowser = function () {
	 
	var BROWSE_VIEWS = {
	'en':'Browse views' ,
	'fr':'Accès aux vues'
	}	
	
	var  TITLE_SEARCH_RESULT =    {
    'fr' : 'Résultats',
    'en' : 'Search results'
	}
	var  TITLE_SEARCH_PANEL =    {
	    'fr' : 'Recherche',
	    'en' : 'Search panel'
	}
	var  LABEL_CRITERIA =    {
	    'fr' : 'Critères de recherche',
	    'en' : 'Search criteria'
	}
	var  LABEL_VIEW =    {
	    'fr' : 'Vues',
	    'en' : 'Views'
	}
	
	var LABEL_FILTER = {
		'fr' : 'Filtres',
		'en' : 'Filters'
	}
	
	var  BUTTON_SEARCH =    {
	    'fr' : 'Rechercher',
	    'en' : 'Search'
	}
	
	var  LABEL_LINES_PER_PAGE =    {
	    'fr' : 'Nombre de lignes par page',
	    'en' : 'Number of lines per page'
	}
	
	var  LABEL_NO_RESULT =    {
	    'fr' : 'Pas de résultat',
	    'en' : 'No result'
	}
	var LABEL_LINE={
		'fr':'ligne',
		'en':'line'
	}
	
	var LABEL_SELECT_VIEW = {
		'fr':'Sélectionnez une vue',
		'en':'Select a view'
	}

	var OPERATORS = {
		'fr':{
	   			CONTAINS:"contient le(s) mot(s)",
	   			EQUALS:"est égal à",
	   			NOT_EQUALS:"n'est pas égal à",
	   			GREATER_THAN:"est supérieur à",
	   			GREATER_THAN_OR_EQUAL:"est supérieur ou égal à",
	   			LOWER_THAN:"est inférieur à",
	   			LOWER_THAN_OR_EQUAL:"est inférieur ou égal à",
	   			STARTSWITH:"contient un mot commençant par",
	   			STRICTCONTAINS:"contient la phrase"
	   		},
		'en':{
	   			CONTAINS:"contains the word(s)",
	   			EQUALS:"is equal to",
	   			NOT_EQUALS:"is not equal to",
	   			GREATER_THAN:"is greater than",
	   			GREATER_THAN_OR_EQUAL:"is greater or equals",
	   			LOWER_THAN:"is lower than",
	   			LOWER_THAN_OR_EQUAL:"is lower or equals",
	   			STARTSWITH:"contains a word starting with",
	   			STRICTCONTAINS:"contains the sentence"
	   		}	
	}
	
	/********************************************************************
	 * Action BROWSE VIEW
	 ********************************************************************/
	var view = new Array();
	var criteriaCount=1;
	var SEARCHABLES = new Array();
	var CRITERIAS = new Array();
    
	var gridContainerPanel;
	
	var pageSize = 20;
	
	function browseViews(){
		showViewsPanel();
		//populate list
		amalto.core.working();
		ViewBrowserInterface.getViewsList(language,getViewsListCB);
		
	}
	
	function getViewsListCB(result){
		//alert(DWRUtil.toDescriptiveString(result));
		var tmp = [LABEL_SELECT_VIEW[language]];
		DWRUtil.removeAllOptions('viewSelect');
		DWRUtil.addOptions('viewSelect',tmp);
		DWRUtil.addOptions('viewSelect',result);
		amalto.core.ready();
	}
	
	function getView(){
		var viewName = DWRUtil.getValue('viewSelect');
		amalto.core.working('');
		if(viewName!=LABEL_SELECT_VIEW[language]){	
			ViewBrowserInterface.getView(getViewCB,viewName, language);
		}
		else{
			$('labelCriteria').style.display = "none";
			DWRUtil.setValue('criterias',"");
			amalto.core.ready('');
		}	
	}
	
	function getViewCB(result){
		view = result;
		//DWRUtil.setValue('viewInfos',view.description);
		$('labelCriteria').style.display = "block";
		DWRUtil.setValue('criterias','<span id="criteria1"><select id="viewfield1"><option value="">Any field</option></select>' +
						'<select id="viewoperator1"></select>' +
						'<input id="value1" type="text" value="*" onkeypress="DWRUtil.onReturn(event, amalto.viewbrowser.ViewBrowser.displayView);" /> ' +
						'<span onClick="amalto.viewbrowser.ViewBrowser.addCriteria(\'criteria1\');"><img src="img/genericUI/add.png"/></span>'+
						'<br/></span>');	
	
		DWRUtil.addOptions('viewoperator1',OPERATORS[language]);
		DWRUtil.removeAllOptions('viewfield1');
		//var tmp = ["Any field"];
		//DWRUtil.addOptions('field1',tmp);
		DWRUtil.addOptions('viewfield1',view.searchables);
		/*for(var i=2; i<criteriaCount+1; i++) {
			try{
				//DWRUtil.removeAllOptions('field'+i);
				DWRUtil.addOptions('field'+i,view.searchables);
			}
			catch(e){continue;}
		}*/
		SEARCHABLES = view.searchables;
		$('view-search-btn').disabled = false;
		amalto.core.ready('');
	}
	
	function getFields(){
		
	}
	
	function addCriteria(criteriaParent){
		criteriaCount ++;
		
		var tpl = new Ext.DomHelper.Template(
						'<span id="criteria{id}">' +
						'<select id="viewfield{id}"></select>' +
						'<select id="viewoperator{id}"></select>' +
						'<input id="value{id}" type="text"  value="*" onkeypress="DWRUtil.onReturn(event, amalto.viewbrowser.ViewBrowser.displayView);"/>  ' +
						'<span onClick="amalto.viewbrowser.ViewBrowser.addCriteria(\'criteria{id}\');"><img src="img/genericUI/add.png"/></span> ' +
						'<span onClick="amalto.viewbrowser.ViewBrowser.removeCriteria(\'{id}\');"><img src="img/genericUI/delete.png"/></span> ' +
						'<br/></span>');
		//criteria[criteriaCount]=criteriaCount;
		var id2 = parseInt(criteriaCount+1);
		tpl.insertAfter(criteriaParent,{id:criteriaCount});
		DWRUtil.addOptions('viewoperator'+criteriaCount,OPERATORS[language]);
		DWRUtil.addOptions('viewfield'+criteriaCount,SEARCHABLES);
		
	}
	
	function removeCriteria(id){
		//criteria.splice(parseInt(id),1);
		var criteriaId = "criteria"+id;
		$('criterias').removeChild($(criteriaId));
		
	}
	
	function showViewsPanel() {
			var tabPanel = amalto.core.getTabPanel();
			var viewBrowsePanel=tabPanel.getItem('viewBrowser');
			if(viewBrowsePanel == undefined){
			
			var html = 
					'<div id="fieldset" class="ylayout-inactive-content">' +
						'<div>'+LABEL_VIEW[language]+' : <select id="viewSelect" onChange="amalto.viewbrowser.ViewBrowser.getView();"><option value="">Loading...</option></select>' +
						'<span id="viewInfos"></span></div>' +
						'<span id="labelCriteria" style="display:none">'+LABEL_CRITERIA[language] +' : </span>'+
						'<div id="criterias">' +
						'</div>' +
						'<br/><input type="button" id="view-search-btn" disabled="true" value="'+BUTTON_SEARCH[language]+'" onClick="amalto.viewbrowser.ViewBrowser.displayView()"/>' +
					'</div>'+
			        '<div id="preview" class="ylayout-inactive-content">'+
			            '<div id="data-grid-view-tb"></div>'+
			            '<div id="data-grid-view" class="ygrid-mso"></div>'+
			        '</div>';
	
							

			gridContainerPanel = new Ext.Panel({
				id: 'view-list',
	    		region: 'center',
	    		layout:'fit',
				border: false,
				//height:300,
				header:true,
				split:true,
				collapsible: false,
				bodyborder: true
			});
				
			viewBrowsePanel = new Ext.Panel({
				id: 'viewBrowser',
				title: BROWSE_VIEWS[language],
				deferredRender: false,
				layout:'border',
				autoScroll: false,
				border: false,
				bodyBorder:false,
				//bodyborder: true,
				closable: true,
				items: 
				[	
					new Ext.Panel({
						id: 'view-search',
			    		title: TITLE_SEARCH_PANEL[language],
			    		region: 'north',
						layout:'fit',
						border:false,
						bodyBorder:false,
						autoScroll: true,	
						collapsible: true,
						header:true,
						bodyStyle:'padding:5px',
						height:200,		
						split:true,
						html: html
					}),
				    gridContainerPanel
				]
			});				
			tabPanel.add(viewBrowsePanel); 
			}		
			viewBrowsePanel.show();
			viewBrowsePanel.doLayout();
			amalto.core.doLayout();
	}
	
	function displayViewEnter(e){
		e.preventDefault();
		displayView(20);
	}
	
	function displayView2(pageSize){
		var viewName = DWRUtil.getValue('viewSelect');
		ViewBrowserInterface.getViewables(viewName, language, function(columnsHeader){		
			displayView2(columnsHeader,20);
		});
	}
	
	function displayView(pageSize ) {	
		amalto.core.working('');
		var viewName = DWRUtil.getValue('viewSelect');
		if(viewName==LABEL_SELECT_VIEW[language]){
			return;
		}
		
		var tmpFields = new Array;
		tmpFields.push("id");
	    for(var i=0; i<view.viewablesXpath.length; i++) { 
	    	var tmp = "/"+view.viewablesXpath[i];
	    	tmpFields.push(tmp);
	    }
	   
	    var schema = {
		    root: 'view',
	    	totalProperty: 'TotalCount',
	    	id: 'id',
			fields: tmpFields
		};
		
		var criteria = "";
	    var nodeList = $('criterias').childNodes;
		for(var i=0; i<nodeList.length; i++) {
			var id = nodeList[i].id.substring(8);
			criteria += DWRUtil.getValue('viewfield'+id)+"#"+DWRUtil.getValue('viewoperator'+id)+"#"+(DWRUtil.getValue('value'+id)!=''?DWRUtil.getValue('value'+id):'*')+"#,";
	
		}
	  	var myColumns = new Array();
		//myColumns[0]={header: "No", sortable: false,width:30};
		myColumns.push({
				header: "id", 
				sortable: true,
				dataIndex: "id" ,
				width:50,
				hidden:true
			});
		for(var k=0;k<view.viewablesXpath.length;k++){
			myColumns.push({
				header: view.viewables[view.viewablesXpath[k]], 
				sortable: true,
				dataIndex: tmpFields[k+1]
			});
		}
		
		var cm = new Ext.grid.ColumnModel(myColumns);
		
		
		var store = new Ext.data.Store({
		    proxy: new Ext.data.HttpProxy({
	        	url: '/viewbrowser/secure/ViewRemotePaging'
	        }),
	        baseParams:{viewName: viewName,criteria:criteria},
	        reader: new Ext.data.JsonReader(schema),
	        remoteSort: true
	    });
		store.on("loadexception",function(obj, options, response, e) {
			if (response.responseText != null)
				Ext.MessageBox.alert("Error", response.responseText);	
			else
				Ext.MessageBox.alert("Error",'Exception occurred while loading item list! ');

	    });
		
		var grid = new Ext.grid.GridPanel({
			id:'view-grid',
		    store: store,
		    //height:200,
		    //autoHeight:true,
		    autoScroll:true,
		    loadMask:true, 
		    columns: myColumns,
			enableColumnMove:true,
			border:false,
			closable:true,
			loadMask:true, 
			//forceFit:true,
		    viewConfig: {
		    	autoFill:true,
		        forceFit: false
		    },
		    listeners: {
		    	'rowclick': function(g, rowIndex, e){
		    		
		    				    try{
		    				    	var record = g.getStore().getAt(rowIndex);
							    	var id = record.id;
							    	displayDocument(id);
						        }
						        catch(error){
						        	alert(error);
						        }
		    		
		                	}
	    	},
			tbar: new Ext.PagingToolbar({
		        pageSize: parseInt(pageSize),
		        store: store,
		        displayInfo: true,
		        displayMsg: 'Displaying items {0} - {1} of {2}',
		        emptyMsg: "No items to display",
		        items:[ 
		        	new Ext.Toolbar.Separator(),
		        	new Ext.Toolbar.TextItem(LABEL_LINES_PER_PAGE[language]+" : "),
		        	new Ext.form.TextField({
						id:'lineMaxView',
						value:pageSize,
						width:30,
						listeners: {
					                	'specialkey': function(a, e) {
								            if(e.getKey() == e.ENTER) {
						                		pageSize = DWRUtil.getValue('lineMaxView');
												if(pageSize==null || pageSize=="") 
													pageSize=20;
												displayView(pageSize);
								            } 
										},
										'change':function(field,newValue,oldValue){
											
											if(newValue != oldValue){
												var pageSize = newValue;
											    if(pageSize==null || pageSize=="") 
											    	pageSize=20;
											    displayView(pageSize);
											}
										
										}
					                }
		        })/*,
		        	new Ext.Toolbar.Spacer(),
		        	 new Ext.Toolbar.Button({
		                text: 'Display',
		                handler: function(){
		                		var lineMax = DWRUtil.getValue('lineMaxItems');
								if(lineMax==null || lineMax=="") 
									lineMax=15;
								displayItems2(columnsHeader,lineMax);
							}
		            })*/]
		    })
		});
	
		
		//layoutCenterPanel = Ext.getCmp('items-list');
		if(Ext.get('view-grid')!=undefined) {
			gridContainerPanel.remove('view-grid');
		}		
		gridContainerPanel.insert(0,grid);
		//grid.render();
		amalto.core.doLayout();
		grid.setHeight(gridContainerPanel.getInnerHeight());
		store.setDefaultSort(view.viewablesXpath[0],"ASC");
		store.load({params:{start:0, limit:pageSize}});

	
	  amalto.core.ready(''); 
	}   
	
	function displayDocument(id){
		
		var tabPanel = amalto.core.getTabPanel();
		var contentPanel=tabPanel.getItem('viewDetailsdiv'+id);
		if( contentPanel== undefined){
			
			var html =
					'<div id="treeDiv'+id+'" style="font:13px tahoma, verdana, helvetica;"></div>' +
					'<div id="sourceDiv'+id+'" style="font:13px tahoma, verdana, helvetica; padding:5px"></div>';
				
				
			contentPanel = new Ext.Panel({
					id:'viewDetailsdiv'+id, 
					title: "View detail", 
					layout:'fit',
					tbar:[
						{text: "Show tree", handler: function(){showTree(id);}},
						new Ext.Toolbar.Separator(),
						{text: "Show source", handler: function(){showSource(id)}}
					],
					autoScroll:true,
					html:html,
					closable:true
				});
				
			tabPanel.add(contentPanel); 
			
		}else{
			
		}
			
		contentPanel.show();
		//contentPanel.doLayout();
		amalto.core.doLayout();
		loadTree(id);
		
		/*

		//TODO IMPROVE THE WAY WE GET THE VIEW ROW
		if(panel) panel.destroy();
		var theBody = 
				'<div>' +
					'<div id="view_panel-tb"></div>' +
				'</div>' +
				'<div id="treeDiv"></div>' +
				'<div id="sourceDiv"></div>'+
				'<div id="chartDiv"></div>' ;
	    // Instantiate the Dialog
	   
		panel = new YAHOO.widget.Panel(
		         "panel", 
				 {   
				 	y:0,	
				 	width: "700px",
				   	visible: true,
				   	draggable: true,
				   	close: true,
				   	zIndex:10000,
				   x:(document.body.offsetWidth/2)-350,
				   y:200
				 } 
			);
		
	    panel.setBody(theBody);  
	    panel.render(document.body); 
	    	
	    var tb = new Ext.Toolbar('view_panel-tb');
		tb.addButton({text: "Show tree", className: 'tb-button tb-button-nude', click: loadTree});
		tb.addSeparator();
		tb.addButton({text: "Show source", className: 'tb-button tb-button-nude', click: showSource});
*/
	}
	
	function showSource(id){
		ViewBrowserInterface.getXML(id, function(result){
			$('treeDiv'+id).style.display = "none";
			//DWRUtil.setValue('treeDiv'+id,"");
			DWRUtil.setValue('sourceDiv'+id,result);	
		});
	}
	
	function showTree(id){
		$('treeDiv'+id).style.display = "block";
		DWRUtil.setValue('sourceDiv'+id,"");	
	}

	
	function loadTree(id){
		$('treeDiv'+id).style.display = "block";
		fnLoadData = function(oNode,fnCallback){
			/*alert(oNode.data);
			alert(oNode.index);*/
			//alert(oNode.getElId());
	
			ViewBrowserInterface.getNode(oNode.getElId(),YAHOO.widget.TreeView.nodeCount,function(result){
				if(result==null) {
					fnCallback();
					return;
				}
				for(var i=0; i<result.length; i++) {
					if(result[i].expandable==true)
						var expanded = false;
					else
						var expanded = true;
					var tmp = new YAHOO.widget.HTMLNode('<span style="font:13px;">'+result[i].name+' : '+result[i].value+'</span>',oNode,false,true);
					tmp.setDynamicLoad(fnLoadData,1);
					//tmp.expand();
				}
				fnCallback();
				}
			);		
		};
		//if(tree) tree._deleteNode();
		var tree= new YAHOO.widget.TreeView("treeDiv"+id);
		tree.setDynamicLoad(fnLoadData);
		var root = tree.getRoot();
		var node1 = new YAHOO.widget.HTMLNode('<span style="font:13px;">Result</span>',root,true,true);
		ViewBrowserInterface.setTree(id,node1.getElId(), function(){
				node1.setDynamicLoad(fnLoadData,1);
				node1.expand();
				tree.draw();	
		});
			
		DWRUtil.setValue('sourceDiv'+id,"");
	
	}
	
	return {
		init: function() {browseViews(); },
		getView: function() {getView();},
		addCriteria: function(a) {addCriteria(a)},
		removeCriteria: function(a) {removeCriteria(a)},
		displayView: function() {displayView(20);}
	}
	
	
	
	
}();