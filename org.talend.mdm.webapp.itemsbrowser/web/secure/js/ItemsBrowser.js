amalto.namespace("amalto.itemsbrowser");

amalto.itemsbrowser.ItemsBrowser = function () {

	loadResource("/core/secure/yui-2.4.0/build/treeview/treeview.js", "YAHOO.widget.TreeView" );
		
	loadResource("/core/secure/yui-2.4.0/build/treeview/assets/treeview.css", "" );
	
	loadResource("/itemsbrowser/secure/css/ItemsBrowser.css", "" );
	
	loadResource("/itemsbrowser/secure/js/ImprovedDWRProxy.js", "");
	
	/********************************************************************
	 * Localization
	 ********************************************************************/
	 
	var MSG_LOADING={
		'fr':'Chargement...',
		'en':'Loading...'
	};
	var MSG_READY={
		'fr':'Terminé.',
		'en':'Ready.'
	};
	var BROWSE_ITEMS = {
		'en':'Browse items',
		'fr': 'Accès aux données'
	};

	
	var LABEL_DISPLAYING = {
		'fr':'Items affichés : ',
		'en':'Displaying items'
	};
	var LABEL_OF = {
		'fr':'sur',
		'en':'of'
	};
	 
	var ITEM_DETAILS = {
		'fr':'Détail de l\'item',
		'en':'Item details'
	};
	
	var  TITLE_SEARCH_RESULT =    {
	    'fr' : 'Résultats',
	    'en' : 'Search results'
	};
	var  TITLE_SEARCH_PANEL =    {
	    'fr' : 'Recherche',
	    'en' : 'Search panel'
	};
	var  LABEL_CRITERIA =    {
	    'fr' : 'Critères de recherche',
	    'en' : 'Search criteria'
	};
	var  LABEL_VIEW =    {
	    'fr' : 'Vues',
	    'en' : 'Views'
	};
	
	var LABEL_FILTER = {
		'fr' : 'Filtres',
		'en' : 'Filters'
	};
	
	var  BUTTON_SEARCH =    {
	    'fr' : 'Rechercher',
	    'en' : 'Search'
	};
	
	var  LABEL_LINES_PER_PAGE =    {
	    'fr' : 'Nombre de lignes par page',
	    'en' : 'Number of lines per page'
	};
	
	var  LABEL_NO_RESULT =    {
	    'fr' : 'Pas de résultat',
	    'en' : 'No result'
	};
	var LABEL_LINE={
		'fr':'ligne',
		'en':'line'
	};
	
	var LABEL_SELECT_VIEW = {
		'fr':'Sélectionnez une vue',
		'en':'Select a view'
	};
	
	var LABEL_SELECT_DATAOBJECT={
		'fr':'Sélectionnez un objet de données',
		'en':'Select a data object'
	};
	
	var LABEL_SELECT = {
		'fr':'Sélectionnez',
		'en':'Select'
	};
	
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
	};
	
	var OPERATOR_UNDEFINED = {
	    'fr' : 'cliquer pour choisir',
	    'en' : 'click to select'	
	};
	
	
	var  MSG_CONFIRM_DELETE_ITEM =    {
	    'fr' : 'Voulez vous réellement effacer cet item : ',
	    'en' : 'Do you really want to delete this item : '
	};
	var  MSG_CONFIRM_DELETE_ITEMS =    {
	    'fr' : 'Do you really want to delete the selected items ',
	    'en' : 'Do you really want to delete the selected items '
	};	
	var  MSG_CONFIRM_LOGICAL_DELETE_ITEM =    {
	    'fr' : 'Please input the path to delete element(s): ',
	    'en' : 'Please input the path to delete element(s): '
	};
	
	var  MSG_CONFIRM_TITLE_LOGICAL_DELETE_ITEM =    {
	    'fr' : 'path name: ',
	    'en' : 'path name: '
	};
	
	var MSG_CONFIRM_SAVE_ITEM = {
		'fr' : 'Cet item existe déjà. Souhaitez-vous l\'écraser ?',
		'en' : 'This item exists. Do you want to overwrite it ?'
	};
	
	
	var BUTTON_DISPLAY = {
		'fr':'Visualiser',
		'en':'Display'
	};
	
	var BUTTON_NEW_ITEM = {
		'fr':'Créer un item',
		'en':'New item'
	};
	
	var BUTTON_SAVE = {
		'fr':'Sauvegarder',
		'en':'Save'
	};
	
	var BUTTON_SAVE_AND_QUIT = {
		'fr':'Sauvegarder et fermer',
		'en':'Save and close'
	};
	
	var BUTTON_PERSONALIZED_VIEW = {
		'fr':'Vue personalisée',
		'en':'Personalized view'	
	};
	
	var BUTTON_TREE_VIEW = {
		'fr':'Vue arbre',
		'en':'Tree view'
	};
	
	var BUTTON_EXPORT = {
		'fr':'Exporter vers Excel',
		'en':'Export to Excel'
	};
	
	var BUTTON_DELETE = {
		'fr':'Supprimer',
		'en':'Delete'
	};
	
	var BUTTON_DUPLICATE = {
		'fr':'Dupliquer',
		'en':'Duplicate'
	};
	
	var BUTTON_JOURNAL = {
		'fr':'Journal',
		'en':'Journal'
	};
	
	var BUTTON_LOGICAL_DEL = {
		'fr':'Logical delete',
		'en':'Logical delete'
	};
	
	var BUTTON_CREATE_USER = {
		'fr':'Créer un utilisateur',
		'en':'Add a new user'
	};
	
	
	
	var LABEL_DATAOBJECT = {
		'fr':'Objet de données',
		'en':'Data object'
	};
	
	
	var config_cal = {
		'fr':{
	   			MONTHS_LONG:["Janvier","F&eacute;vrier","Mars","Avril","Mai","Juin","Juillet","Aout","Septembre","Octobre","Novembre","D&eacute;cembre"],
	   			WEEKDAYS_SHORT:[]
	   		},
		'en':null		
	};
	
	var ALERT_NO_CHANGE={
		'fr':'Pas de changement depuis la dernière sauvegarde',
		'en':'No change since last save'
	};	
	
	var SELECTED_FIELD = {
		'fr':'champ sélectionné',
		'en':'selected field'
	}
	var SELECTED_FIELDS = {
		'fr':'champs séléctionnés',
		'en':'selected fields'
	};
	
	var BUTTON_PRINT = {
		'fr':'Imprimer',
		'en':'Print'
	};
	
	var TOO_MANY_RESULTS = {
		'fr' : 'items ont été trouvés.<br><br> Lancer tout de même la recherche? <br>Cette opération peut être très longue.',
		'en' : 'items found. <br><br>Do you still want to launch the search? <br>This could take a long time.'
	};
	
	var SEARCH_RESULT = {
		'fr':'Résultat de la recherche',
		'en':'Search result'
	};
	
	var TITLE_WINDOW_FK ={
		'fr':'Choix de la clé étrangère',
		'en':'Choose a foreign key'
	};
	
	var FILTER = {
		'fr':'Saisissez un critère de recherche puis sélectionnez une valeur dans la liste déroulante',
		'en':'Fill the box with a key-word then select a value'
	};
	var SELECT = {
		'fr':'Valider',
		'en':'Select'
	};
	var CLOSE = {
		'fr':'Fermer',
		'en':'Close'
	};
	var BUTTON_SEARCH ={
		'fr':'Rechercher',
		'en':'Search'
	};
	var EDIT_ITEM_TOOLTIP={
		'fr':'Tips: The field with * is required.',
		'en':'Tips: The field with * is required.'
	};
	var PHYSICALLY_DELETE_TOOLTIP={
		'fr':'delete the item physically',
		'en':'delete the item physically'
	};
	var LOGICALLY_DELETE_TOOLTIP={
		'fr':'delete the item logically',
		'en':'delete the item logically'
	};
	var SAVE_TOOLTIP={
		'fr':'Save this object',
		'en':'Save this object'
	}; 		
	var SAVEANDCLOSE_TOOLTIP={
		'fr':'Save this object and close the tab',
		'en':'Save this object and close the tab'
	};
	var DUPLICATE_TOOLTIP={
		'fr':'clone the selected item',
		'en':'clone the selected item'
	};
	var JOURNAL_TOOLTIP={
		'fr':'browse a journal for this item',
		'en':'browse a journal for this item'
	};
	
	var UPLOAD_FILE={
		'fr':'Upload Picture',
		'en':'Upload Picture'
	};
	var UPLOAD={
		'fr':'Upload',
		'en':'Upload'
	};
	var RESET={
		'fr':'Reset',
		'en':'Reset'
	};
	var FILE_NAME={
		'fr':'File Name',
		'en':'File Name'
	}

	/*****************
	 * EXT 2.0
	 *****************/
	var itemsBrowserPanel;
	var gridContainerPanel;
	/********************************************************************
	 * Action show items
	 ********************************************************************/
	var _viewItems = [];
	var _viewItems2 = [];
	var _gridItems ;
	var _itemDetailsPanel = null;
	var _dataObject;
	var _dataObject2;
	var dataObjectLocalized ="";
	var _tbItems; 
	var itemsElements;
	var newItem = [];
	var itemTreeList = [];

	var keys = [];
	var updateFlag = [];
	var lastUpdatedInputFlag = [];
	
	var treeCount=1; 
	
	var criteriaCount = 1;
	
	/** The foreign key search window */
	var foreignKeyWindow;
	/** The node date picker */
	var nodeDatePicker;
	/** The node upload file window */
	var uploadFileWindow;
	var errorDesc = "The item can not be saved, it contains error(s). See details below:";
	var itemNodes = [];
	
	function browseItems(){
		showItemsPanel();
		//populate list
		amalto.core.working('');
		ItemsBrowserInterface.getViewsList(getViewsItemsListCB,language);
	}
	
	function showItemsPanel() {	
		var tabPanel = amalto.core.getTabPanel();
		itemsBrowserPanel = tabPanel.getItem('itemsBrowser');
		
		if(itemsBrowserPanel == undefined){
				
			gridContainerPanel = new Ext.Panel({
				id: 'items-list',
	    		region: 'center',
	    		layout:'fit',
				border: false,
				header:true,
				split:true,
				closable:true,
				collapsible: false
			});
				
			itemsBrowserPanel = new Ext.Panel({
				id: 'itemsBrowser',
				title: BROWSE_ITEMS[language],
				layout:'border',
				autoScroll: false,
				border: false,
				bodyBorder:false,
				closable: true,
				items: 
				[	
					new Ext.Panel({
						id: 'items-search',
			    		title: TITLE_SEARCH_PANEL[language],
			    		region: 'north',
						layout:'fit',
						border:false,
						bodyBorder:false,
						autoScroll: true,	
						collapsible: true,
						header:true,
						closable:true,
						bodyStyle:'padding:5px',
						height:200,		
						split:true,
						html: '' +
						'<div>'+LABEL_DATAOBJECT[language]+' : <select id="viewItemsSelect" onChange="amalto.itemsbrowser.ItemsBrowser.getViewItems();"><option value="">'+MSG_LOADING[language]+'</option></select>' +
						'<span id="viewItemsInfos"></span></div>' +
						'<span id="labelItemsCriteria" style="display:none">'+LABEL_CRITERIA[language] +' : </span>'+
						'<div id="itemsCriterias">' +
						'</div>' +
						'<br/>' +
						'<input id="item-search-btn" type="button" value="'+BUTTON_SEARCH[language]+'" disabled="true" onClick="amalto.itemsbrowser.ItemsBrowser.displayItems();"/>' +
						'<input id="item-new-btn" type="button" value="'+BUTTON_NEW_ITEM[language]+'" disabled="true"  onClick="amalto.itemsbrowser.ItemsBrowser.displayItemDetails();"/>',
						border: true,
						bodyborder: true
					}),
				    gridContainerPanel
				]
			});			
			tabPanel.add(itemsBrowserPanel); 
		}//if
		
		itemsBrowserPanel.show();
		itemsBrowserPanel.doLayout();
		amalto.core.doLayout();

				
		$('item-search-btn').disabled = true;
		$('item-new-btn').disabled = true;	
		DWRUtil.setValue('itemsCriterias',"");
		$('labelItemsCriteria').style.display = "none";
		
	}
	
	function getViewsItemsListCB(result){
		var tmp = [LABEL_SELECT_DATAOBJECT[language]];
		DWRUtil.removeAllOptions('viewItemsSelect');
		DWRUtil.addOptions('viewItemsSelect',tmp);
		DWRUtil.addOptions('viewItemsSelect',result);
		amalto.core.ready('');
	}
	
	function getViewItems(){
		var viewName = DWRUtil.getValue('viewItemsSelect');
		amalto.core.working();
		if(viewName!=LABEL_SELECT_DATAOBJECT[language]){	
			ItemsBrowserInterface.getView(getViewItemsCB,viewName, language);
		}
		else{
			$('item-search-btn').disabled = true;
			$('item-new-btn').disabled = true;		
			$('labelItemsCriteria').style.display = "none";
			DWRUtil.setValue('itemsCriterias',"");
			amalto.core.ready();
		}
	}
	
	function getViewItemsCB(result){
		_viewItems = []; 
		_viewItems = result;
		$('labelItemsCriteria').style.display = "block";
		DWRUtil.setValue('itemsCriterias','<span id="itemsCriteria1"><select id="itemsSearchField1"></select>' +
						'<select id="itemsSearchOperator1"></select>' +
						'<input id="itemsSearchValue1" type="text" value="*"  onkeypress="DWRUtil.onReturn(event, amalto.itemsbrowser.ItemsBrowser.displayItems);"/> ' +
						'<span onClick="amalto.itemsbrowser.ItemsBrowser.addItemsCriteria(\'itemsCriteria1\');"><img src="img/genericUI/add-element.gif"/></span><br/></span>');	
		DWRUtil.addOptions('itemsSearchOperator1',OPERATORS[language]);
		DWRUtil.removeAllOptions('itemsSearchField1');
		$('item-search-btn').disabled = false;

		var viewName = DWRUtil.getValue('viewItemsSelect');
		var tmp = viewName.replace("Browse_items_","");
		_dataObject = tmp.replace(/#.*/,"");
	
		// get root node to know if user can create item
		ItemsBrowserInterface.getRootNode(_dataObject,language, function(rootNode){
//			if(!rootNode.readOnly) $('item-new-btn').disabled = false;
//			else $('item-new-btn').disabled = true;
			$('item-new-btn').disabled=rootNode.readOnly;
			if($('btn-logicaldelete'))$('btn-logicaldelete').disabled=$('item-new-btn').disabled;
			if($('btn-delete'))$('btn-delete').disabled=$('item-new-btn').disabled;			
		});
		//empty grid when another view is selected
		if(_gridItems){    
		    //_gridItems.destroy(false);
		    //DWRUtil.setValue('itemsResultCount','');
	    }
	    DWRUtil.addOptions('itemsSearchField1',_viewItems.searchables);
	    itemsElements = _viewItems.searchables;
	    dataObjectLocalized = _viewItems.descriptionLocalized;
		//getElements();
		amalto.core.ready();
	}
	
	
	function getElements(){
		var viewName = DWRUtil.getValue('viewItemsSelect');
		ItemsBrowserInterface.getSearchables(viewName,language,function(result) {
			//alert(DWRUtil.toDescriptiveString(result,3));	
			itemsElements = result;
			DWRUtil.addOptions('itemsSearchField1',result);
		});
	}
	
	function addItemsCriteria(criteriaParent){	
		criteriaCount ++;
		var tpl = new Ext.DomHelper.Template(
						'<span id="itemsCriteria{id}">' +
						'<select id="itemsSearchField{id}"></select>' +
						'<select id="itemsSearchOperator{id}"></select>' +
						'<input id="itemsSearchValue{id}" type="text"  onkeypress="DWRUtil.onReturn(event, amalto.itemsbrowser.ItemsBrowser.displayItems);"/>  ' +
						'<span onClick="amalto.itemsbrowser.ItemsBrowser.addItemsCriteria(\'itemsCriteria{id}\');"><img src="img/genericUI/add-element.gif"/></span> ' +
						'<span onClick="amalto.itemsbrowser.ItemsBrowser.removeItemsCriteria(\'{id}\');"><img src="img/genericUI/remove-element.gif"/></span> ' +
						'<br/></span>');
		tpl.insertAfter(criteriaParent,{id:criteriaCount});
		DWRUtil.addOptions('itemsSearchOperator'+criteriaCount,OPERATORS[language]);
		DWRUtil.addOptions('itemsSearchField'+criteriaCount,itemsElements);
	}
	
	function removeItemsCriteria(id){
		//criteria.splice(parseInt(id),1);
		var criteriaId = "itemsCriteria"+id;
		$('itemsCriterias').removeChild($(criteriaId));
	}
	
	
	
	function displayItems(){
		var viewName = DWRUtil.getValue('viewItemsSelect');
		if(viewName!=LABEL_SELECT_DATAOBJECT[language] && viewName!=""){	
			amalto.core.working();
			var columnsHeader = [];
			ItemsBrowserInterface.getViewables(viewName, language, function(result){		
				columnsHeader = result;
				displayItems2(columnsHeader,50);
				
				//delete/logicaldelete should be the same as new buttton
				$('btn-logicaldelete').disabled=$('item-new-btn').disabled;
				$('btn-delete').disabled=$('item-new-btn').disabled;		
			});	
		}
	
	}
	
	
	function displayItems2(columnsHeader, pageSize) {
		_dataObject2 = _dataObject;
		_viewItems2 = _viewItems;
		var itemPK = [];
		amalto.core.working();
		var viewName = DWRUtil.getValue('viewItemsSelect');	
		if(_gridItems){    
		    _gridItems.destroy(false);
	    }
	
		var displayDoc = function(grid, rowIndex, columnIndex, e){
		    try{
		    	for(var i=0; i<_viewItems2.keys.length; i++) {
		    		itemPK[i] = grid.dataModel.getRow(rowIndex)[i];
		    	}
		    	displayItemDetails(itemPK,_dataObject2);
	        }
	        catch(error){
	        	alert(error);
	        }
	    };

	   
	    var tmpFields = new Array();
	    for(var i=0; i<_viewItems2.viewables.length; i++) {
	    	var tmp = "/"+_viewItems2.viewables[i];
	    	tmpFields.push(tmp);
	    }
	    
	    var myColumns = [];	    
	    var sm2 = new Ext.grid.CheckboxSelectionModel();
	    //myColumns.push(new Ext.grid.RowNumberer());
	    myColumns.push(sm2);
		for(var k=0;k<_viewItems2.viewables.length;k++){
			myColumns.push({
				header: columnsHeader[k], 
				sortable: true,
				width:100,
				dataIndex: tmpFields[k]
				
			});
		}
		
		var cm = new Ext.grid.ColumnModel(myColumns);		
		//cm.defaultWidth = 200;
	   	cm.defaultSortable = true;
	   	
		var schema = {
		    root: 'items',
	    	totalProperty: 'TotalCount',
	    	id: 'nothing',
			fields: tmpFields
		};
	
	    
		//Build a comma-dash separated list of search criteria
	    var criteria = "";
	    var nodeList = $('itemsCriterias').childNodes;
		for(var i=0; i<nodeList.length; i++) {
			var id = nodeList[i].id.substring(13);
			criteria += DWRUtil.getValue('itemsSearchField'+id)+"#"+DWRUtil.getValue('itemsSearchOperator'+id)+"#"+DWRUtil.getValue('itemsSearchValue'+id)+"#,";
		}

		var store = new Ext.data.Store({
			    proxy: new Ext.data.HttpProxy({
		        	url: '/itemsbrowser/secure/ItemsRemotePaging'
		        }),
		        sortInfo:{field: _viewItems2.keys[0], direction: "ASC"},
		        baseParams:{viewName: viewName,criteria:criteria},
		        reader: new Ext.data.JsonReader(schema),
		        remoteSort: true
		    });
		    
		var grid = new Ext.grid.GridPanel({
			id:'items-grid',
		    store: store,
		    autoScroll:true,
		    //columns: myColumns,
		    cm:cm,
			enableColumnMove:true,
			closable:true,
			border:false,
		    viewConfig: {
		    	autoFill:true,
		        forceFit: false
		    },
		    sm: sm2,
	        // inline buttons
	        bbar: [
	        	{text:BUTTON_DELETE[language],
	        		id:'btn-delete',
	        		xtype:'button',
	        		tooltip:PHYSICALLY_DELETE_TOOLTIP[language],
		        	listeners:{
		        		'click':function(){
			        		var sel=sm2.getSelections();
			        		if(sel.length==0) return;
				    		Ext.MessageBox.confirm("confirm",MSG_CONFIRM_DELETE_ITEMS[language]+ " ?",function re(en){
					    		if(en=="yes"){
					        		for(var j=0; j<sel.length; j++){
					        			//get ItemPK
					        			var itemPK=[];
								    	for(var i=0; i<_viewItems2.keys.length; i++) {
								    		itemPK[i] = sel[j].get(_viewItems2.keys[i]);
								    	}
								    	var ids="";
										for(var i=0; i<itemPK.length; i++) {
											ids += (ids==""?"":"@"); 
											ids += itemPK[i];			
										}							    	
										var treeIndex=1;
										if(_dataObject==null) _dataObject=_dataObject2;
										ItemsBrowserInterface.deleteItem(_dataObject, ids, function(result){			
											amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);
											displayItems();										
										});
					        		}				    		
					    		}				    			
				    		});			        			        		
		        		}
		        	}
	        	},
	        	new Ext.Toolbar.Separator(),
	        	{text:BUTTON_LOGICAL_DEL[language],
	        		id:'btn-logicaldelete',
	        		xtype:'button',
	        		tooltip:LOGICALLY_DELETE_TOOLTIP[language],
	        		listeners:{
		        		'click':function(){
			        		var sel=sm2.getSelections();
			        		if(sel.length==0) return;
							Ext.Msg.show({
							   title: MSG_CONFIRM_TITLE_LOGICAL_DELETE_ITEM[language],
							   msg:  MSG_CONFIRM_LOGICAL_DELETE_ITEM[language],
							   buttons: Ext.Msg.OKCANCEL,
							   fn: doLogicalDelete,
							   prompt:true,
							   value: '/',
							   width:300
							});
							
							function doLogicalDelete(btn, path){
						       if (btn == 'cancel') {
									return;
								}
										        		
				        		for(var j=0; j<sel.length; j++){
				        			//get ItemPK
				        			var itemPK=[];
							    	for(var i=0; i<_viewItems2.keys.length; i++) {
							    		itemPK[i] = sel[j].get(_viewItems2.keys[i]);
							    	}
							    	var ids="";
									for(var i=0; i<itemPK.length; i++) {
										ids += (ids==""?"":"@"); 
										ids += itemPK[i];			
									}							    	
									var treeIndex=1;
									if(_dataObject==null) _dataObject=_dataObject2;
									ItemsBrowserInterface.logicalDeleteItem(_dataObject, ids, path, function(result){
										displayItems();
//										if(result)
//										Ext.MessageBox.alert('Status', result);				
									});									
				        		}							
							};			        				        		
		        		}
		        	}
	        	}
	        ],
	        buttonAlign:'left',		    
		    listeners: {
		    	'rowdblclick': function(g, rowIndex, e){
								//alert("keys "+DWRUtil.toDescriptiveString(_viewItems2.keys,3));		    					
						    	for(var i=0; i<_viewItems2.keys.length; i++) {
						    		itemPK[i] = g.getStore().getAt(rowIndex).get(_viewItems2.keys[i]);
						    	}
						    	displayItemDetails(itemPK,_dataObject2);
		                	}
	    	},
			tbar: new Ext.PagingToolbar({
		        pageSize: parseInt(pageSize),
		        store: store,
		        displayInfo: true,
		        displayMsg: LABEL_DISPLAYING[language]+' {0} - {1} '+LABEL_OF[language]+' {2}',
		        emptyMsg: LABEL_NO_RESULT[language],
		        items:[ 
		        	new Ext.Toolbar.Separator(),
		        	new Ext.Toolbar.TextItem(LABEL_LINES_PER_PAGE[language]+" : "),
		        	new Ext.form.TextField({
    					id:'lineMaxItems',
    					value:pageSize,
    					width:30,
    					listeners: {
		                	'specialkey': function(a, e) {
					            if(e.getKey() == e.ENTER) {
			                		var lineMax = DWRUtil.getValue('lineMaxItems');
									if(lineMax==null || lineMax=="") 
										lineMax=50;
									displayItems2(columnsHeader,lineMax);
					            } 
							}
		                }
		            })
		        ]
		    })
		});
	
		
		if(Ext.get('items-grid')!=undefined) {
			gridContainerPanel.remove('items-grid');
		}		
		gridContainerPanel.insert(0,grid);
		//grid.render();
		itemsBrowserPanel.doLayout();  
		amalto.core.doLayout();
		grid.setHeight(gridContainerPanel.getInnerHeight());	
		
		ItemsBrowserInterface.countItems(criteria, _dataObject, function(result){
			if(result>1000) {
				Ext.MessageBox.buttonText.yes = "Oui"; //french
				Ext.MessageBox.buttonText.no = "Non"; //french
				Ext.MessageBox.show({
					title:SEARCH_RESULT[language],
				   	msg:result+" "+TOO_MANY_RESULTS[language],
				   	buttons: Ext.Msg.YESNO,
				   	fn: function(btn){
		                	if(btn!="yes") return;
		                	store.load({params:{start:0, limit:pageSize}});
		                },
				   	animEl: 'elId',
				   	icon: Ext.MessageBox.QUESTION
				});
				
			}
			else store.load({params:{start:0, limit:pageSize}});
			amalto.core.ready();
		});
			

	}   
	
	
	/********
	  Toolbars management
	*********/
	
	
	// options
	var O_TREE_VIEW 	= 1;
	var O_PERSO_VIEW 	= 2;
	var O_PRINT 		= 4;
	var O_SAVE 			= 8;
	var O_SAVE_QUIT 	= 16;
	var O_DELETE 		= 32;
	var O_LOGICAL_DEL   = 64;
	var O_DUPLICATE     = 128;
	var O_JOURNAL       = 256;
	
	// modes
	var M_TREE_VIEW		= 1;
	var M_PERSO_VIEW	= 2;
	
	function clearToolBar(toolbar)
	{
		if (toolbar.tr.childNodes)
		{
			while(toolbar.tr.childNodes.length > 0)
			{
				toolbar.tr.removeChild(toolbar.tr.childNodes[0]);
			}
		}
	}
	
	function initToolBar2(toolbar, mode){
		
	}
	
	
	function initToolBar(toolbar, mode)
	{
		
		clearToolBar(toolbar);
		toolbar.currentMode = mode;
		var nbButtons = 0;
	
		var options = 0;
		switch(mode)
		{
			case M_TREE_VIEW:
				options |= (toolbar.baseOptions & O_PERSO_VIEW);
				options |= (toolbar.baseOptions & O_SAVE);
				options |= (toolbar.baseOptions & O_SAVE_QUIT);
				options |= (toolbar.baseOptions & O_DELETE);
				options |= (toolbar.baseOptions & O_LOGICAL_DEL);
				options |= (toolbar.baseOptions & O_DUPLICATE);
				options |= (toolbar.baseOptions & O_JOURNAL);
				
			break;
			case M_PERSO_VIEW:
				options |= O_TREE_VIEW;
				options |= (toolbar.baseOptions & O_PRINT);
				options |= (toolbar.baseOptions & O_DELETE);
			break;
		}
		
	
		// tree view
		if ( (options&O_TREE_VIEW)==O_TREE_VIEW )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton(new Ext.Toolbar.Button({text: BUTTON_TREE_VIEW[language], handler:toolbar.displayTreeHandler }));
			nbButtons++;
		}
		
		// perso view
		if ( (options&O_PERSO_VIEW)==O_PERSO_VIEW )
		{
		
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton({text: BUTTON_PERSONALIZED_VIEW[language],  handler:toolbar.displaySmartViewHandler });
			nbButtons++;
		}
	// print
		if ( (options&O_PRINT)==O_PRINT )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton({text: BUTTON_PRINT[language], className: 'tb-button tb-button-nude', handler: toolbar.printHandler});
			nbButtons++;
		}

		
		// save
		if ( (options&O_SAVE)==O_SAVE )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {tooltip:SAVE_TOOLTIP[language],text: BUTTON_SAVE[language], className: 'tb-button tb-button-nude', handler: toolbar.saveItemHandler});
			nbButtons++;
		}
	
		// save and quit
		if ( (options&O_SAVE_QUIT)==O_SAVE_QUIT )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {tooltip:SAVEANDCLOSE_TOOLTIP[language],text: BUTTON_SAVE_AND_QUIT[language], className: 'tb-button tb-button-nude', handler: toolbar.saveItemAndQuitHandler});
			nbButtons++;
		}
		
		// delete
		if ( (options&O_DELETE)==O_DELETE )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {tooltip:PHYSICALLY_DELETE_TOOLTIP[language],text: BUTTON_DELETE[language], className: 'tb-button tb-button-nude', handler: toolbar.deleteItemHandler});
			nbButtons++;
		}
		
		// logical delete
		if ( (options&O_LOGICAL_DEL)==O_LOGICAL_DEL )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {tooltip:LOGICALLY_DELETE_TOOLTIP[language],text: BUTTON_LOGICAL_DEL[language], className: 'tb-button tb-button-nude', handler: toolbar.logicalDelItemHandler});
			nbButtons++;
		}
		
		// duplicate
		if ( (options&O_DUPLICATE)==O_DUPLICATE )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {tooltip:DUPLICATE_TOOLTIP[language],text: BUTTON_DUPLICATE[language], className: 'tb-button tb-button-nude', handler: toolbar.duplicateItemHandler});
			nbButtons++;
		}
		
		// journal
		if ( (options&O_JOURNAL)==O_JOURNAL )
		{
			if (nbButtons>0)
			{
				toolbar.addSeparator();
				nbButtons++;
			}
	
			toolbar.addButton( {tooltip:JOURNAL_TOOLTIP[language],text: BUTTON_JOURNAL[language], className: 'tb-button tb-button-nude', handler: toolbar.journalItemHandler});
			nbButtons++;
		}
	}
	
	
	/*********************************************************
	 *            ITEM DETAIL
	 *********************************************************/
	
	//var itemTree;
	var itemTreeFK;
	
	function displayItemDetails(itemPK2, dataObject){
		
	    displayItemDetails2(itemPK2, dataObject, false);
	}
	
	function displayItemDetails4Duplicate(itemPK2, dataObject){
		
	    displayItemDetails2(itemPK2, dataObject, true);
	    
	}
	
	function displayItemDetails2(itemPK2, dataObject, isDuplicate){
		loadResource("/itemsbrowser/secure/js/ItemNode.js", "amalto.itemsbrowser.ItemNode" );
		//alert("display items "+DWRUtil.toDescriptiveString(itemPK2,2)+" "+ dataObject);
		amalto.core.working();
		itemNodes = [];
		treeCount++;	
		var treeIndex = treeCount;
		
		var ids = "";
		
		
		if(itemPK2==null) {
			newItem[treeIndex] = true;
		}
		else {
			newItem[treeIndex] = false;
			for(var i=0; i<itemPK2.length; i++) {
				ids += (ids==""?"":"@"); 
				ids += itemPK2[i];			
			}
		}
		
		//add for duplicate case
		if(isDuplicate){
		   newItem[treeIndex] = true;
		}
	
	
		lastUpdatedInputFlag[treeIndex] = null;
		updateFlag[treeIndex] = 0;
	
		keys[treeIndex] = [];
		if(dataObject==null) dataObject=_dataObject;
		ItemsBrowserInterface.getRootNode(dataObject, language, function(rootNode){
			
			var tabPanel = amalto.core.getTabPanel();
			if(tabPanel.getItem('itemDetailsdiv'+treeIndex) == undefined){
					
				var smartView = '';
				if(newItem[treeIndex]==false ) {
					smartView = '<iframe width="100%" height="100%" frameborder=0 scrolling=auto src="/itemsbrowser/secure/SmartViewServlet?ids='+ids+'&concept='+dataObject+'&language='+language+'">';
				}	
	
				//update the div structure
				var html =
				   '<div id="errorDesc" style="display:none;color:red;font-weight:bold;padding-left:25px"><img src="img/genericUI/icon-error.gif" style="vertical-align:middle"/><span style="padding-left:10px;text-align:center;vertical-align:middle;">'
			             + errorDesc + '</span></div>' +
				    '<div id="errorDetail" style="display:none;color:red;font-weight:bold;padding-left:65px"></div></br>'+
					'<div>' +
					'		<span id="itemDetails'+treeIndex+'" class="itemTree"></span>' +
					'		<span id="smartView'+treeIndex+'" style="display=none;">'+smartView+'</span>' +
					'</div>' ;
										

				var tbDetail = new Ext.Toolbar({id:'item-tb'});
				tbDetail.baseOptions = 0;
				tbDetail.ids = ids;
				tbDetail.dataObject = dataObject;
				tbDetail.treeIndex = treeIndex;
					
				var myTitle = "";
				if(_dataObject!=null) myTitle=_dataObject;
		
				if(dataObject!=null) myTitle = dataObject;		
		
				if(rootNode.name!=null)	myTitle = rootNode.name;	
		
				if(itemPK2!=null) {
					for(var i=0; i<itemPK2.length; i++) {
						myTitle +=" "+itemPK2[i];
					}	
				}
	
    			if(dataObject==null) dataObject=_dataObject;	
    			
    			var addOptions;
    			
    			var itemTree= new YAHOO.widget.TreeView("itemDetails"+treeIndex);
    			itemTreeList[treeIndex] = itemTree;
    		    
    			//add for duplicate case
				if(isDuplicate){
					
				   var fnLoadData = function(oNode, fnCallback) {
    				//getChildren(oNode.index,fnCallback, false, newItem, itemTree, treeIndex);
    				ItemsBrowserInterface.getChildrenWithKeyMask(oNode.index, YAHOO.widget.TreeView.nodeCount, language, false, treeIndex,true,function(result){
    					if(result==null) {
    						fnCallback();
    						return;
    					}
    	
    					for(var i=0; i<result.length; i++) {
    						var readOnly = (result[i].readOnly==true || (result[i].key==true && newItem[treeIndex]==false));
    						if (!readOnly)
    						{
    							//var tbDetail = tabPanel.getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
    							if (!(tbDetail.baseOptions&O_SAVE))
    							{
    								//case new
    								tbDetail.baseOptions |= O_SAVE|O_SAVE_QUIT; 
    								initToolBar(tbDetail, tbDetail.currentMode);
    							}
    						}
    	
    						var tmp = new amalto.itemsbrowser.ItemNode(result[i],newItem[treeIndex],treeIndex,
    									itemTree.getNodeByIndex(oNode.index),false,true);
    						//new Ext.form.TextField({applyTo:result[i].nodeId+'Value'});
    						if(result[i].type=="simple") tmp.setDynamicLoad();
    						else tmp.setDynamicLoad(fnLoadData, 1);
    						itemNodes[i] = tmp;
    					}
    					fnCallback();
        			});
        		  };
        		  
				}else{
					
					var fnLoadData = function(oNode, fnCallback) {
    				//getChildren(oNode.index,fnCallback, false, newItem, itemTree, treeIndex);
    				ItemsBrowserInterface.getChildren(oNode.index, YAHOO.widget.TreeView.nodeCount, language, false, treeIndex,function(result){
    					if(result==null) {
    						fnCallback();
    						return;
    					}
    	
    					for(var i=0; i<result.length; i++) {
    						var readOnly = (result[i].readOnly==true || (result[i].key==true && newItem[treeIndex]==false));
    						if (!readOnly)
    						{
    							//var tbDetail = tabPanel.getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
    							if (!(tbDetail.baseOptions&O_SAVE))
    							{
    								//case new
    								tbDetail.baseOptions |= O_SAVE|O_SAVE_QUIT; 
    								initToolBar(tbDetail, tbDetail.currentMode);
    							}
    						}
    	
    						var tmp = new amalto.itemsbrowser.ItemNode(result[i],newItem[treeIndex],treeIndex,
    									itemTree.getNodeByIndex(oNode.index),false,true);
    						//new Ext.form.TextField({applyTo:result[i].nodeId+'Value'});
    						if(result[i].type=="simple") tmp.setDynamicLoad();
    						else tmp.setDynamicLoad(fnLoadData, 1);
    						itemNodes[i] = tmp;
    					}
    					fnCallback();
        			});
        		  };
				
				}
    				
    			
    			
        		var root = itemTree.getRoot();	
        		var nameTmp = dataObject;
        		var descInfo = "";
        		
        		if(rootNode.name!=null) nameTmp = '<div style="width:180;float:left;font-size:22px;font-weight:bold">'+rootNode.name+'</div>';
        		if(rootNode.description!=null&&rootNode.description!="")descInfo=' <img src="img/genericUI/information_icon.gif" ext:qtitle="Description" ext:qtip="'+rootNode.description+'"/>';
        		nameTmp=nameTmp+descInfo;
        		var node1 = new YAHOO.widget.HTMLNode(nameTmp,root,false, true);
        		
        	
        		tbDetail.deleteItemHandler = function() {
        			deleteItem(ids, dataObject, treeIndex);
        		};
        		
        		tbDetail.logicalDelItemHandler = function() {
        			logicalDelItem(ids, dataObject, treeIndex);
        		};
        		
        		tbDetail.duplicateItemHandler = function() {
        			duplicateItem(ids, dataObject);
        		};
        		
        		tbDetail.journalItemHandler = function() {
        			journalItem(ids, dataObject);
        		};
        		
        		ItemsBrowserInterface.checkIfTransformerExists(dataObject, language, function(result){
        			
        			var mode = M_TREE_VIEW;
        			//var tb = tabPanel.getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
        			var tb = tbDetail;
        			if(result==true && newItem[treeIndex]==false) {
        				
        				mode = M_PERSO_VIEW;
        				
        				tbDetail.displayTreeHandler = function(){	
        					getTree(ids,''+dataObject,treeIndex);
        				};				
        				
        				tbDetail.printHandler = function(){			
        					printSmartView(ids, dataObject);
        				};
        	
        				tb.baseOptions |= O_PRINT | O_PERSO_VIEW;
        	
        				$('smartView'+treeIndex).style.display='block';
        				$('itemDetails'+treeIndex).style.display='none';
        			}
        	
        			initToolBar(tb, mode);
        			
        		});
    	
    			tbDetail.saveItemHandler = function(){			
    				saveItemWithoutQuit(ids,dataObject,treeIndex);
    			};
    			
    			tbDetail.saveItemAndQuitHandler = function(){			
    				saveItemAndQuit(ids,dataObject,treeIndex);
    			};
    		
    			//case edit and no editable
    			if(rootNode.readOnly==false && newItem[treeIndex]==false) {
    				tbDetail.baseOptions |= O_DELETE|O_LOGICAL_DEL|O_DUPLICATE|O_JOURNAL;	
    			}		
    	
    			//add for duplicate case
				if(isDuplicate){
					ItemsBrowserInterface.setTree(dataObject, itemPK2, node1.index, false, treeIndex, function(result){
    				node1.setDynamicLoad(fnLoadData,1);
    				node1.expand();
    				itemTree.draw();
    				ItemsBrowserInterface.updateKeyNodesToEmptyInItemDocument(treeIndex);
    			    });
				}else{
					ItemsBrowserInterface.setTree(dataObject, itemPK2, node1.index, false, treeIndex, function(result){
    				node1.setDynamicLoad(fnLoadData,1);
    				node1.expand();
    				itemTree.draw();
    			    });
				}
    			
    		
    			var contentPanel = new Ext.Panel({
    				id:'itemDetailsdiv'+treeIndex, 
    				title: myTitle, 
    				//activeTab: 0,
    				//tabPosition:'bottom',
    				//layout:'border',
    				autoScroll:true,
    				tbar: tbDetail,
    				html:html,
    				closable:true,
    				bbar : new Ext.Toolbar([{
						text : EDIT_ITEM_TOOLTIP[language],
						xtype : "tbtext"
					}])
    			});
		
			}
			tabPanel.add(contentPanel); 
			contentPanel.show();
			contentPanel.doLayout();
		    amalto.core.doLayout();
		});
		
		amalto.core.ready();
		
	}
	
	
	function updateNode(id, treeIndex){
		updateFlag[treeIndex] = 1;
		var itemTree = itemTreeList[treeIndex];
		//var data = itemTree.getNodeByIndex(id).data;	
		var node = itemTree.getNodeByIndex(id);
	
		var value = DWRUtil.getValue(id+"Value");
		if(node.itemData.key==true){
			keys[treeIndex][node.itemData.keyIndex] = value;
		}
		
	    $('errorDetail').style.display = "none";
	    
		if(node.updateValue(value)==false){
			ItemsBrowserInterface.updateNode(id, value, treeIndex, function() {
						// amalto.core.ready();
			
			$('errorDesc').style.display = "block";
			return;
					});
		} 
		else
		{
		    if (updateItemNodesBeforeSaving() == true) {
				$('errorDesc').style.display = "block";
			}
			else
			 $('errorDesc').style.display = "none";
		}
		
		ItemsBrowserInterface.updateNode(id,value,treeIndex,function(result){
			amalto.core.ready(result);
		});
		amalto.core.ready();
	}
	
	function setlastUpdatedInputFlag(id, treeIndex){
		//alert("flag");
		lastUpdatedInputFlag[treeIndex] = id;
	}
	
	function cloneNode2(siblingId, hasIcon, treeIndex){
		var itemTree = itemTreeList[treeIndex];
		var siblingNode = itemTree.getNodeByIndex(siblingId);
		var nodeCount = YAHOO.widget.TreeView.nodeCount;
	
		var newNode = new amalto.itemsbrowser.ItemNode(siblingNode.itemData,true,treeIndex,siblingNode.parent,false,true);
		newNode.updateNodeId(nodeCount);
		newNode.updateValue("");
		ItemsBrowserInterface.cloneNode(siblingId,newNode.index, treeIndex,function(result){
			amalto.core.ready(result);
		});
		newNode.insertAfter(siblingNode);
		//newNode.appendTo(siblingNode.parent);
		itemTree.getRoot().childrenRendered=false;
		
		var fnLoadData = function(oNode,fnCallback){
			//getChildren(oNode.index,fnCallback, false, newItem, itemTree, treeIndex);
			ItemsBrowserInterface.getChildren(oNode.index, YAHOO.widget.TreeView.nodeCount, language, false, treeIndex,function(result){
				if(result==null) {
					fnCallback();
					return;
				}
				for(var i=0; i<result.length; i++) {					
					var tmp = new amalto.itemsbrowser.ItemNode(result[i],newItem[treeIndex],treeIndex,itemTree.getNodeByIndex(oNode.index),false,true);
				if(result[i].type=="simple") tmp.setDynamicLoad();
				else tmp.setDynamicLoad(fnLoadData,1);
			}
			fnCallback();
			});
		};
		
		newNode.setDynamicLoad(fnLoadData);
		siblingNode.parent.refresh();
		amalto.core.ready();
	}
	
	function removeNode2(id, treeIndex){
		updateFlag[treeIndex] = true;
		var value = "";
		if(Ext.get(id+"Value"))
			value = DWRUtil.getValue(id+"Value");
		var itemTree = itemTreeList[treeIndex];
		itemTree.removeNode(itemTree.getNodeByIndex(id),true);
		ItemsBrowserInterface.removeNode(id,treeIndex, value,function(result){
				amalto.core.ready(result);
			});
		itemTree.getRoot().refresh();
		amalto.core.ready();
	}
	
		 
	function saveItemAndQuit(ids,dataObject,treeIndex){
		//alert(DWRUtil.toDescriptiveString(itemPK2,2)+" "+dataObject+" "+treeIndex);

		//saveItem(ids,dataObject,treeIndex);
		saveItem(ids,dataObject,treeIndex,function(){
				  amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);
			      displayItems();
				 });
		
			
		
	}
	
	function saveItemWithoutQuit(ids,dataObject,treeIndex){
		saveItem(ids,dataObject,treeIndex,function(){
				  //amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);
			      displayItems();
				 });
	}
	
	function updateItemNodesBeforeSaving()
	{
	  var error = false;
	  for (var i = 0; i < itemNodes.length; i++) {
	  	var node = itemNodes[i];
	  	if (node.update() == false)
	  	   error = true;
	  }
	  return error;
	}
	
	function saveItem(ids,dataObject,treeIndex,callbackOnSuccess){
		if(navigator.appName=="Microsoft Internet Explorer" && lastUpdatedInputFlag[treeIndex]!=null) {
			updateNode(lastUpdatedInputFlag[treeIndex], treeIndex);
		}
		
	    if (updateItemNodesBeforeSaving() == true) {
			$('errorDesc').style.display = "block";
			$('errorDetail').style.display = "none";
			return;
		}
		
	    $('errorDesc').style.display = "none";
	    $('errorDetail').style.display = "none";
		ItemsBrowserInterface.checkIfDocumentExists(keys[treeIndex], dataObject, function(result){
			if(result==true) {
				if(!confirm(MSG_CONFIRM_SAVE_ITEM[language])) return;
			}
			var itemPK = ids.split('@');
			ItemsBrowserInterface.saveItem(itemPK,dataObject, newItem[treeIndex],treeIndex,{
			callback:function(result){ 
				amalto.core.ready(result);
				if(result=="ERROR_2"){
					amalto.core.ready(ALERT_NO_CHANGE[language]);
					//alert(ALERT_NO_CHANGE[language]);
				}else if(result.indexOf('ERROR_3:')==0){
					//add for before saving transformer check
                    amalto.core.ready(result.substring(8));
                    alert(result.substring(8));
                }else{
			       if(callbackOnSuccess)callbackOnSuccess();   
				}
				
			},
			errorHandler:function(errorString, exception) {//on exception  
//              alert(''+ errorString);
				var error = itemTreeList[treeIndex];
               $('errorDesc').style.display = "block";
                var reCat = /\[Error\].*\n/gi;
                var innerHml ="";
             	var arrMactches = errorString.match(reCat);
             	for (var i=0; i < arrMactches.length ; i++)
				{
				  innerHml +=arrMactches[i];
				  if (i < arrMactches.length-1)
				   innerHml += '<br/>';
				}
				$('errorDetail').style.display = "block";
				$('errorDetail').innerHTML = innerHml;
            }
           });
			amalto.core.ready();
		});

	}
	
	function deleteItem(ids, dataObject, treeIndex) {
		//var viewName = DWRUtil.getValue('viewItemsSelect');
		//var dataObject = viewName.replace("Browse_items_",""); 
		var tmp = "";
		var itemPK = ids.split('@');
		for(var i=0; i<itemPK.length; i++) {
			tmp += " "+itemPK[i];
		}
		Ext.MessageBox.confirm("confirm",MSG_CONFIRM_DELETE_ITEM[language]+ " ?",function re(en){
		if(en=="yes"){
			ItemsBrowserInterface.deleteItem(dataObject, itemPK, function(result){			
				amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);
				amalto.core.ready(result);
				displayItems();				
			});		
		}});		
	}
	function logicalDelOneItem(ids, dataObject, treeIndex){
		var tmp = "";
		var itemPK = ids.split('@');
		for(var i=0; i<itemPK.length; i++) {
			tmp += " "+itemPK[i];
		}
		ItemsBrowserInterface.logicalDeleteItem(dataObject, itemPK, path, function(result){
			amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);
			amalto.core.ready(result);
			displayItems();
			if(result)
			Ext.MessageBox.alert('Status', result);				
		});
	}
	
	function logicalDelItem(ids, dataObject, treeIndex){
		var tmp = "";
		var itemPK = ids.split('@');
		
		Ext.Msg.show({
		   title: MSG_CONFIRM_TITLE_LOGICAL_DELETE_ITEM[language],
		   msg:  MSG_CONFIRM_LOGICAL_DELETE_ITEM[language],
		   buttons: Ext.Msg.OKCANCEL,
		   fn: doLogicalDel,
		   prompt:true,
		   value: '/',
		   width:300
		});
		
		function doLogicalDel(btn, path){
	       if (btn == 'cancel') {
				return;
			}
			logicalDelOneItem(ids,dataObject, treeIndex);
		};
	}
	
	function duplicateItem(ids, dataObject){
		if(ids){
		var itemPK = ids.split('@');
		displayItemDetails4Duplicate(itemPK,dataObject,true);
	    }
	}
	
	
	function journalItem(ids, dataObject){
		if(ids.indexOf("@")>0)ids=ids.replaceAll("@",".");
	    amalto.updatereport.UpdateReport.browseUpdateReportWithSearchCriteria(dataObject,ids);	
	}	

	/**
	 * Search for foreign keys and return a map that can be used 
	 * to fill the Foreign Key Search Combo
	 */
	function filterForeignKey(xpathForeignKey, xpathInfoForeignKey, nodeId, value){
		var value =DWRUtil.getValue('foreignkey-filter-'+nodeId);
		amalto.core.working();
		Ext.getCmp('foreignkey-search-button-'+nodeId).disable();
		ItemsBrowserInterface.getForeignKeyList(xpathForeignKey, xpathInfoForeignKey, value, function(results){
			DWRUtil.removeAllOptions('foreignkey-list-'+nodeId);
	    	DWRUtil.addOptions('foreignkey-list-'+nodeId,results);
	    	amalto.core.ready();
	    	Ext.getCmp('foreignkey-search-button-'+nodeId).enable();
		});
	}
	
	function showDatePicker(nodeId, treeIndex, nodeType){
		if(nodeDatePicker){
			 nodeDatePicker.hide();
			 nodeDatePicker.destroy();
		}
		
		var inputText=nodeId+"Value";
		nodeDatePicker = new Ext.DatePicker({  
					     applyTo:inputText,  
					     renderTo:inputText,   
					     hidden:"true",  
					     format:"Y-m-d"  
	     }); 
	     
	     nodeDatePicker.on("select",function(src,date){
	     	     var setValue=date.format("Y-m-d");
	     	     if(nodeType=="dateTime"){setValue+="T00:00:00"};
			     DWRUtil.setValue(inputText,setValue);
			     updateNode(nodeId,treeIndex);
			     nodeDatePicker.hide();
		 });
		 
		 var initValue=$(inputText).value;
		 if(initValue!=null&&initValue!=""){
		 	if(initValue.indexOf('T')!=-1)initValue=initValue.substring(0,initValue.indexOf('T'));
		 	nodeDatePicker.setValue(Date.parseDate(initValue,"Y-m-d"));
		 }
		 
		 nodeDatePicker.show(this);
	}
	
	function removePicture(nodeId, treeIndex){
	var url=$(nodeId+"Value").value;
	var pos=url.indexOf('?');
	var uri=url.substring("/imageserver/".length,pos);
	
    var form = new Ext.form.FormPanel({
        baseCls: 'x-plain',
        layout:'fit',
        method: 'POST',   
        frame:true, 
        url:'/imageserver/secure/ImageDeleteServlet?uri='+uri,
        defaultType: 'textfield',

        items: [{
            xtype:'label',
            text: 'Are you sure to delete?'
        }]
    });	
    var window = new Ext.Window({
        title: 'Confirm',
        width: 200,
        height:120,
        layout: 'fit',
        plain:true,
        bodyStyle:'padding:5px;',
        buttonAlign:'center',
        items: form,
        buttons: [{
            text: 'Yes',
	        handler: function() {          	        	
	        form.getForm().submit({    
		        success: function(form, action){		          	           
		            Ext.Msg.alert('Sucess', 'Picture delete sucessfully!');		 
				    var inputText=nodeId+"Value";	
				    DWRUtil.setValue(inputText,'');
				    updateNode(nodeId,treeIndex);
				    if($('showPicture'))
				       $('showPicture').src='img/genericUI/no_image.gif';		            
		           window.hide();
		        },    
		       failure: function(form, action){  
		       	 var text=action.result;
	          	Ext.Msg.alert('Failed', text.message);
	          	window.hide();
		       },
		       waitMsg : 'Deleting...'
		     });    
		    }              
        },{
            text: 'No',
            handler:function(){
            	window.hide();
            }
        }]
    });

    window.show();	       
	}
	
	function showUploadFile(nodeId, treeIndex, nodeType){

	if(uploadFileWindow){
		 uploadFileWindow.hide();
		 uploadFileWindow.destroy();
	}
    var uploadFilePanel = new Ext.form.FormPanel({
          baseCls: 'x-plain',
	     labelAlign: 'right',    	      
	     labelWidth: 60,           
         //layout:'fit',
         defaultType: 'textfield',
	     url: '/imageserver/secure/ImageUploadServlet',//fileUploadServlet    
	     fileUpload: true,   
	     items: [{    
	        xtype: 'textfield',    
	        fieldLabel: FILE_NAME[language],    
	        name: 'imageFile',
	        height:20,
	        inputType: 'file'//file type
	      }]
        
    });

    uploadFileWindow = new Ext.Window({
        title: UPLOAD_FILE[language],
        width: 320,
        height:120,
        layout: 'fit',
        plain:true,
        bodyStyle:'padding:5px;',
        buttonAlign:'center',
        items: uploadFilePanel,

	    buttons: [{    
	        text: UPLOAD[language],    
	        handler: function() {    
	        uploadFilePanel.getForm().submit({    
		        success: function(uploadFilePanel, action){
		           var text=action.response.responseText;
		           var url='/imageserver/'+eval('('+text+')').message+"?width=150&height=90&preserveAspectRatio=true";		
		           var inputText=nodeId+"Value";	
				    DWRUtil.setValue(inputText,url);
				    updateNode(nodeId,treeIndex);		           
		            Ext.Msg.alert('Sucess', 'File upload sucessfully!');
		            if($('showPicture'))$('showPicture').src=url;
		            uploadFileWindow.hide();
		        },    
		       failure: function(form, action){    
	          	Ext.Msg.alert('Failed', action.result.message);    
		       },
		       waitMsg : 'Uploading...'
		     });    
		    }    
	 	 },{
            text: RESET[language],
            handler: function(){
                uploadFilePanel.getForm().reset();
            }
        }]    
    });

    uploadFileWindow.show();
	}
	var panel;
	function chooseForeignKey(nodeId, xpathForeignKey, xpathInfoForeignKey, treeIndex) {
		
		amalto.core.working('Running...');
	    amalto.core.ready();
	    
		ItemsBrowserInterface.countForeignKey(xpathForeignKey, function(count){
			//Display a pop-up window to search for foreign keys
			if(foreignKeyWindow){
			    foreignKeyWindow.hide();
			    foreignKeyWindow.destroy();
			}
				
			var store = new Ext.data.Store({
				proxy: new Ext.ux.data.ImprovedDWRProxy({
			        dwrFunction: ItemsBrowserInterface.getForeignKeyList,
			        dwrAdditional: [xpathForeignKey, xpathInfoForeignKey ] //, Ext.getCmp('foreign-key-filter').getValue()]}
				}),
		        reader: new Ext.data.JsonReader({
		            root: 'rows',
		            totalProperty: 'count',
		            id: 'keys'
		        }, [
		            {name: 'keys', mapping: 'keys'},
		            {name: 'infos', mapping: 'infos'}
		        ])
			});
			


		    // Custom rendering Template
		    var resultTpl = new Ext.XTemplate(
			        '<tpl for="."><div class="search-item" style="font-size: 0.8em">',
			            '<h3>{infos}</h3>',
			            '{keys}',
			        '</div></tpl>'
			    );
		    
		    var combo = new Ext.form.ComboBox({
                width: 250,
                fieldLabel: FILTER[language],
                id: 'foreign-key-filter',
		        store: store,
		        displayField:'title',
		        typeAhead: false,
		        loadingText: 'Searching...',
		        pageSize:100,
		        minChars: 2,
		        hideTrigger: true,
		        tpl: resultTpl,
		        listAlign: 'tl-bl',
		        itemSelector: 'div.search-item',
		        onSelect: function(record){
                    this.collapse();
                	DWRUtil.setValue(nodeId+'Value', record.get("keys"));
                	updateNode(nodeId,treeIndex);
		        	foreignKeyWindow.hide();
		        }
		    });

			
			foreignKeyWindow = new Ext.Window({
                layout:'fit',
                width:300,
                height: 150,
                closeAction:'hide',
                plain: true,
                title: TITLE_WINDOW_FK[language]+'<br/>('+count+' '+(count>1?'clés possibles trouvées)':'clé possible trouvée)'),
                
                items: [new Ext.form.FormPanel({
                	labelAlign: 'top',
                    items: [
                        new Ext.Panel({html: '', border: false}),
	                    combo
	                ]
                })]

            });
			
			foreignKeyWindow.on('show', function() {
				var combo = Ext.getCmp('foreign-key-filter');
				combo.focus(true, 100);
				combo.reset();
				if(count<500) {
					combo.doQuery(".*",true);
//						combo.setRawValue("");
				}
			});

			
			foreignKeyWindow.show(this);
			
		});
		
		
	}
	
	var fnLoadData2;
	
		
	function browseForeignKey(nodeId, foreignKeyXpath){
		//Check if have a Primary key made of multiple Item Ids or a single one
		if(DWRUtil.getValue(nodeId+'Value').match(/\[(.*?)\]/g)!=null){
			var tmp =  DWRUtil.getValue(nodeId+'Value').replace(/\[(.*?)\]/g,"$1###").split("###");
			var itemPK =tmp.splice(0,tmp.length-1);
		} else{
			var itemPK = [DWRUtil.getValue(nodeId+'Value')];
		}
		
		var dataObject = foreignKeyXpath.split("/")[0];
		displayItemDetails(itemPK,dataObject);
	}
	
	function browseForeignKey2(nodeId, foreignKeyXpath){
		amalto.core.working('Running...');
		var itemPK = DWRUtil.getValue(nodeId+'Value');
		if(panel) panel.destroy();
		var theBody = '<div id="itemFKTree"></div>';	
		panel = new YAHOO.widget.Panel(
		         "itemFKTreecontainer", 
				 {   
				 //y:500,		
				  width: "800px",		 
				   visible: true,
				   draggable: true,
				   close: true,
				   zIndex:10000,
				   x:(document.body.offsetWidth/2)-350,
				   y:200
				 } );
	    panel.setBody(theBody);  
	    panel.render(document.body); 
	    amalto.core.ready();
	
		var addOptions;
		var dataObject = foreignKeyXpath.split("/")[0];
		fnLoadData2 = function(oNode,fnCallback){
			getChildren(oNode.index,fnCallback, true, false, null,0);
		
		};
		//if(tree) tree._deleteNode();
		itemTreeFK= new YAHOO.widget.TreeView("itemFKTree");	
		var root = itemTreeFK.getRoot();

		ItemsBrowserInterface.getRootNode(dataObject,language, function(rootNode){
			var nameTmp = dataObject;
			if(rootNode.name!=null) nameTmp = rootNode.name;
			var node2 = new YAHOO.widget.HTMLNode(nameTmp,root,false, true);
			//itemTreeFK.setDynamicLoad(fnLoadData2);
			ItemsBrowserInterface.setTree(dataObject,itemPK,node2.index,true,"",function(result){
			node2.setDynamicLoad(fnLoadData2);
			node2.expand();
			itemTreeFK.draw();
			});
		}
		);
	}
	
	function setForeignKey(nodeId,treeIndex){	
		var fk= DWRUtil.getValue('foreignKeyList');
		DWRUtil.setValue(nodeId+'Value',fk);
		panel.destroy();
		updateNode(nodeId,treeIndex);
	}
	
	function displayXsdDetails(id){
		var divId = id+"XsdDetails";
		var openerId = id+"OpenDetails";
		if($(divId).style.display == "none"){
			$(divId).style.display = "block";
			DWRUtil.setValue(openerId," <img src=\"img/genericUI/close-detail2.gif\"/>");
		}
			
		else{
			$(divId).style.display = "none";
			DWRUtil.setValue(openerId," <img src=\"img/genericUI/open-detail2.gif\"/>");
		}		
	}
	
	function getSmartView(ids, dataObject, treeIndex){
		var tbDetail = amalto.core.getTabPanel().getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
	
		tbDetail.displayTreeHandler = function(){	
			getTree(ids,''+dataObject,treeIndex);
		};
		
		tbDetail.printHandler = function() {			
			printSmartView(ids,dataObject);
		};
		
		$('smartView'+treeIndex).style.display='block';
		$('itemDetails'+treeIndex).style.display='none';
		
		// updating toolbar
	
		initToolBar(tbDetail, M_PERSO_VIEW);
	}
	
	function getTree(ids,dataObject, treeIndex){
		var tbDetail = amalto.core.getTabPanel().getComponent('itemDetailsdiv'+treeIndex).getTopToolbar();
		tbDetail.displaySmartViewHandler = function(){	
			getSmartView(ids,''+dataObject,treeIndex);
		};
		
		$('itemDetails'+treeIndex).style.display='block';
		$('smartView'+treeIndex).style.display='none';
		
		// updating toolbar
		initToolBar(tbDetail, M_TREE_VIEW);
	
	}
	
	
	function printSmartView(ids,dataObject){
		window.open('/itemsbrowser/secure/SmartViewServlet?ids='+ids+'&concept='+dataObject+'&language='+language,'Print','toolbar=no,location=no,directories=no,menubar=yes,scrollbars=yes,resizable=yes');
	}
	


 	return {
		init: function() {browseItems(); },
		getViewItems:function() {getViewItems();},
		displayItems:function() {displayItems();},
		addItemsCriteria:function(criteriaParent) {addItemsCriteria(criteriaParent);},
		removeItemsCriteria:function(id){removeItemsCriteria(id);},
		updateNode:function(id,treeIndex){updateNode(id,treeIndex);},
		setlastUpdatedInputFlagPublic:function(id,treeIndex){alert("setlastUpdatedInputFlagPublic");setlastUpdatedInputFlag(id,treeIndex);},
		browseForeignKey:function(nodeId, foreignKeyXpath){browseForeignKey(nodeId, foreignKeyXpath);},
		showDatePicker:function(nodeId,treeIndex,nodeType){showDatePicker(nodeId,treeIndex,nodeType);},
		showUploadFile: function (nodeId, treeIndex, nodeType){showUploadFile(nodeId, treeIndex, nodeType)},
		removePicture: function (nodeId, treeIndex){removePicture(nodeId, treeIndex)},
		chooseForeignKey:function(nodeId,xpath,xpathInfo,treeIndex){chooseForeignKey(nodeId,xpath,xpathInfo,treeIndex);},
		cloneNode2:function(siblingId,hasIcon,treeIndex){cloneNode2(siblingId,hasIcon,treeIndex)},
		removeNode2:function(id,treeIndex){removeNode2(id,treeIndex)},
		displayXsdDetails:function(id){displayXsdDetails(id)},
		setForeignKey:function(nodeId,treeIndex){setForeignKey(nodeId,treeIndex)},
		displayItemDetails:function(){displayItemDetails();},
		filterForeignKey:function(string0, string1, id){filterForeignKey(string0, string1, id);}
 	}
}();
