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
	
	var MESSAGE_MULTI_SHOW = {
		'fr':'clés possibles trouvées',
		'en':'possible keys are found'
	};
	var MESSAGE_SINGLE_SHOW = {
		'fr':'clé possible trouvée',
		'en':'possible key is found'
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
	
	var DATE_OPERS = {
			'fr':{
		           EQUALS:"est égal à",
		           LOWER_THAN:"est antérieure à",
		           GREATER_THAN:"est postérieure à"
	             },
 			'en':{
		           EQUALS:"equals",
		           LOWER_THAN:"is before",
		           GREATER_THAN:"is after"
	             }
	};
	
	var NUMBER_OPERS = {
			'fr':{
					EQUALS:"est égal à",
					NOT_EQUALS:"n'est pas égal à",
					GREATER_THAN:"est supérieur à",
					GREATER_THAN_OR_EQUAL:"est supérieur ou égal à",
					LOWER_THAN:"est inférieur à",
					LOWER_THAN_OR_EQUAL:"est inférieur ou égal à",
					STRICTCONTAINS:"contient la phrase"
		         },
			'en':{
					EQUALS:"is equal to",
					NOT_EQUALS:"is not equal to",
					GREATER_THAN:"is greater than",
					GREATER_THAN_OR_EQUAL:"is greater or equals",
					LOWER_THAN:"is lower than",
					LOWER_THAN_OR_EQUAL:"is lower or equals",
					STRICTCONTAINS:"contains the sentence"
				  }	
	};
	
	var BOOLEAN_OPERS = {
			'fr':{
		           EQUALSTRUE:"est égal à vrai",
		           EQUALSFALSE:"est égal à faux"
	             },
	        'en' :{
			         EQUALSTRUE:"is equal to true",
			         EQUALSFALSE:"is equal to false"
	              }
	};
	
	var BOOLEAN_MAP =
	{
	   'EQUALSTRUE'	 : 'equals true',
	   'EQUALSFALSE' : 'equals false'
	};
	
	var EQUAL_OPERS = 
	{
		'fr' : 'est égal à'	,
		'en' : 'equals'
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
	var errorDesc={
		'fr':'L\'item ne peut pas être sauvegardé, il y a une (des) erreur(s).',
		'en':'The item can not be saved, it contains error(s).'
	}
	var DELETE_ALERT={
		'fr':'The last item can not be removed!',
		'en':'The last item can not be removed!'
	}
	
	var CHOICE_ALLTOGETHER_ALERT={
			'fr': 'Only one choice item can be set value ',
			'en': 'Only one choice item can be set value '
	}

	var CHOICE_NONE_ALERT={
			'fr': 'No choice item is set value',
			'en': 'No choice item is set value'
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
	var itemsPredicates = [];
	var currentPredicate = [];
	var newItem = [];
	var itemTreeList = [];

	var keys = [];
	var updateFlag = [];
	var lastUpdatedInputFlag = [];
	
	var treeCount=1; 
	
	var criteriaCount = 1;
	
	/** The foreign key search window */
	var foreignKeyWindow;
	/** The node date pickerwindow */
	var nodeDatePickerWindow;
	/** The node upload file window */
	var uploadFileWindow;
	//var errorDesc = "The item can not be saved, it contains error(s). See details below:";
	var itemNodes = [];
	var map = [];
	var sortIndex=0;
	var sortUporDown="ASC";
	var isUp = true;
	var _criterias = new Array();
	var _searchCriteriaResult = "";
	
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
			ItemsBrowserInterface.getMetaDataTypes(getItemsPredicateList, viewName);
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
		_criterias = [];
		_viewItems = result;
		$('labelItemsCriteria').style.display = "block";
		DWRUtil.setValue('itemsCriterias','<span id="itemsCriteria1"><select id="itemsSearchField1" onChange="amalto.itemsbrowser.ItemsBrowser.updateOperatorList(\'1\');amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
						'<select id="itemsSearchOperator1" onChange="amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
						'<input id="itemsSearchValue1" type="text" value="*"  style="display:none;" onkeypress="DWRUtil.onReturn(event, amalto.itemsbrowser.ItemsBrowser.displayItems);"/> ' +
						'<span id="itemSearchCalendar1" style="display:none;cursor:pointer;padding-left:4px;padding-right:4px" onclick="javascript:amalto.itemsbrowser.ItemsBrowser.showDatePicker(\'itemsSearchValue1\' , \'-1\', \'date\')"><img src="img/genericUI/date-picker.gif"/></span>' +
						'<input id="itemSearchCriteriaForAnd1" type="radio" name="itemSearchCriteria1" onclick="amalto.itemsbrowser.ItemsBrowser.itemsCriteriaWithConstraints(\'itemsCriteria1\', \'1\', \'AND\');"> AND '+
						'<input id="itemSearchCriteriaForOR1" type="radio" name="itemSearchCriteria1" onclick="amalto.itemsbrowser.ItemsBrowser.itemsCriteriaWithConstraints(\'itemsCriteria1\', \'1\', \'OR\');"> OR '+
						'<br/></span>'
						);	
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
	    newCriteriaItemSet(0, 'AND');
	    outPutCriteriaResult();
		updateOperatorList(1)
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
	
	function addItemsCriteria(criteriaParent, idx, and){	
		if (isNewCriteria(idx, and) == false) return;
		criteriaCount ++;
		var tpl = new Ext.DomHelper.Template(
						'<span id="itemsCriteria{id}">' +
						'<select id="itemsSearchField{id}" onChange="amalto.itemsbrowser.ItemsBrowser.updateOperatorList(\'{id}\');amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
						'<select id="itemsSearchOperator{id}" onChange="amalto.itemsbrowser.ItemsBrowser.outPutCriteriaResult();"></select>' +
						'<input id="itemsSearchValue{id}" type="text"  onkeypress="DWRUtil.onReturn(event, amalto.itemsbrowser.ItemsBrowser.displayItems);"/>  ' +
						'<span id="itemSearchCalendar{id}" style="display:none;cursor:pointer;padding-left:4px;padding-right:4px" onclick="javascript:amalto.itemsbrowser.ItemsBrowser.showDatePicker(\'itemsSearchValue{id}\' , \'-1\', \'date\')"><img src="img/genericUI/date-picker.gif"/></span>' +
						'<input id="itemSearchCriteriaForAnd{id}" type="radio" name="itemSearchCriteria{id}" onclick="amalto.itemsbrowser.ItemsBrowser.itemsCriteriaWithConstraints(\'itemsCriteria{id}\', \'{id}\', \'AND\');"> AND '+
						'<input id="itemSearchCriteriaForOR{id}" type="radio" name="itemSearchCriteria{id}" onclick="amalto.itemsbrowser.ItemsBrowser.itemsCriteriaWithConstraints(\'itemsCriteria{id}\', \'{id}\', \'OR\');"> OR '+
						'<span onClick="amalto.itemsbrowser.ItemsBrowser.removeItemsCriteria(\'{id}\');"><img src="img/genericUI/remove-element.gif"/></span> '  +
						'<br/></span>'
						);
		var actulId = parseInt(idx) + 1;
		tpl.insertAfter(criteriaParent,{id:actulId});
		DWRUtil.addOptions('itemsSearchOperator'+actulId,OPERATORS[language]);
		DWRUtil.addOptions('itemsSearchField'+actulId,itemsElements);
		
		newCriteriaItemSet(idx, and);
		updateOperatorList(actulId);
	}
	
	function isNewCriteria(idx, and)
	{	
		var parentIdx = parseInt(idx) -1;
		for (var id = parentIdx; id >= 0; id--)
		{
			if (_criterias[id] != null)
			{
				_criterias[id][1] = ' ';
				_criterias[id][2] = and;
				_criterias[id][3] = ' ';
				break;
			}
		}
		
		for (var ic = parseInt(idx); ic < _criterias.length; ic++)
		{
			if (_criterias[ic] != undefined)
				return false;
		}

		return true;
	}
	
	function newCriteriaItemSet(idx, and)
	{	
		var actulId = parseInt(idx) + 1;
		
		var criteria = DWRUtil.getValue('itemsSearchField' + actulId) + ' '
				+ DWRUtil.getValue('itemsSearchOperator' + actulId) + ' '
				+ DWRUtil.getValue('itemsSearchValue' + actulId);
		
		if (_criterias[idx] == undefined)
		{
			_criterias[idx] = [];
			_criterias[idx][0] = criteria;	
		}
	}
	
	function removeItemsCriteria(id){
		//criteria.splice(parseInt(id),1);
		var criteriaId = "itemsCriteria"+id;
		$('itemsCriterias').removeChild($(criteriaId));
		
		id = parseInt(id);
		_criterias[id-1] = null;
		for (var subid = id-2; subid >= 0 ; subid--)
		{
			if (_criterias[subid] != undefined)
			{
				var last = true;
				for (var l = subid+1; l < _criterias.length; l++)
				{
					if (_criterias[l] != undefined)
					{
						last = false;
						break;
					}
				}
				if (last == true)
				{
					_criterias[subid][2] = null;
					_criterias[subid][3] = null;
					subid = subid +1;
					$('itemSearchCriteriaForAnd' + subid).checked = false;
					$('itemSearchCriteriaForOR' + subid).checked = false;
				}

				criteriaCount--;
				break;
			}
		}
		
		 outPutCriteriaResult();
	}
	
	function itemsCriteriaWithConstraints(criteriaParent, id, and){
		addItemsCriteria(criteriaParent, id, and);
		outPutCriteriaResult();
	}
	
	function getItemsPredicateList(result)
	{
		itemsPredicates = result;
		updateOperatorList(1);
		currentPredicate = [];
	}
	
	function updateCurrentPredicate(id)
	{
		currentPredicate[id] = "";
		var search = $('itemsSearchField' + id).value;
		var delimeter = search.indexOf("/");
		if(delimeter != -1)
		{
			search = search.substring(delimeter + 1);
		}
		if(itemsPredicates[search] == null)return;
		var predicateValues =  itemsPredicates[search][0];
		if(predicateValues == 'xsd:boolean')
		{
			currentPredicate[id] = 'boolean';
		}
		else if(predicateValues == 'foreign key')
		{
			currentPredicate[id] = 'foreign key';
		}
		else if(predicateValues == 'enumeration')
		{
			currentPredicate[id] = 'enumeration';
		}
	}
	
	function updateOperatorList(id){
        if($('itemsSearchField' + id) == null)return;
		var search = $('itemsSearchField' + id).value;
		var delimeter = search.indexOf("/");
		if(delimeter != -1)
		{
			search = search.substring(delimeter + 1);
		}
        if(itemsPredicates[search] == null) return;
		var predicateValues =  itemsPredicates[search][0];
		var itemsSearchValuex = "itemsSearchValue" + id;
		DWRUtil.removeAllOptions('itemsSearchOperator' + id);
		$('itemSearchCalendar' + id).style.display = 'none';
		currentPredicate[id] = "";
		
		if(predicateValues == 'xsd:string' || predicateValues == 'xsd:normalizedString' || predicateValues == 'xsd:token')
		{
			DWRUtil.addOptions('itemsSearchOperator' + id ,OPERATORS[language]);
			$(itemsSearchValuex).value = "*";
			$(itemsSearchValuex).style.display = 'inline';
		}
		else if(predicateValues == 'xsd:date' || predicateValues == 'xsd:time' || predicateValues == 'xsd:dateTime')
		{
			DWRUtil.addOptions('itemsSearchOperator' + id ,DATE_OPERS[language]);
			$('itemSearchCalendar' + id).style.display = 'inline';
			$(itemsSearchValuex).style.display = 'inline';
		}
		else if (predicateValues == 'xsd:double'
			    || predicateValues == 'xsd:float'
				|| predicateValues == 'xsd:integer'
				|| predicateValues == 'xsd:decimal'
				|| predicateValues == 'xsd:byte'
				|| predicateValues == 'xsd:int'
				|| predicateValues == 'xsd:long'
				|| predicateValues == 'xsd:negativeInteger'
				|| predicateValues == 'xsd:nonNegativeInteger'
				|| predicateValues == 'xsd:nonPositiveInteger'
				|| predicateValues == 'xsd:positiveInteger'
				|| predicateValues == 'xsd:short'
				|| predicateValues == 'xsd:unsignedLong'
				|| predicateValues == 'xsd:unsignedInt'
				|| predicateValues == 'xsd:unsignedShort'
				|| predicateValues == 'xsd:unsignedByte')
		{
			DWRUtil.addOptions('itemsSearchOperator' + id ,NUMBER_OPERS[language]);
			$(itemsSearchValuex).style.display = 'inline';
		}
		else if(predicateValues == 'xsd:boolean')
		{
			var booleanPredicates = ['true' , 'false']; 
			var prefix = EQUAL_OPERS[language];
		    for(var i = 0; i < booleanPredicates.length; i++)
		    {
		    	booleanPredicates[i] = prefix + " " +  booleanPredicates[i];
		    }
			DWRUtil.addOptions('itemsSearchOperator' + id ,BOOLEAN_OPERS[language]);
			$(itemsSearchValuex).style.display = 'none';
			currentPredicate[id] = 'boolean';
		}
		else if(predicateValues == 'foreign key')
		{
			var foreignValues = itemsPredicates[search];
			var foreignPredicates = []; 
			var prefix = EQUAL_OPERS[language];
			for(var i = 1; i < foreignValues.length; i++)
			{
				foreignPredicates[i -1] = prefix + " " + foreignValues[i];
			}
			DWRUtil.addOptions('itemsSearchOperator' + id ,foreignPredicates);
			$(itemsSearchValuex).style.display = "none";
			currentPredicate[id] = 'foreign key';
		}
		else if(predicateValues == 'enumeration')
		{
			var enumValues = itemsPredicates[search];
			var enumPredicates = [];
			var prefix = EQUAL_OPERS[language];
			for(var i = 1; i < enumValues.length; i++)
			{
				enumPredicates[i -1] = prefix + " " + enumValues[i];
			}
			DWRUtil.addOptions('itemsSearchOperator' + id ,enumPredicates);
			$(itemsSearchValuex).style.display = "none";
			currentPredicate[id] = 'enumeration';
		}
	}
	

	
	function outPutCriteriaResult(){
		var cpy = new Array();
		for (var idx = 0; idx < _criterias.length; idx++)
		{
			if (_criterias[idx] == undefined)
				continue;
			var actulID = idx+1;
			var criteria = DWRUtil.getValue('itemsSearchField' + actulID) + ' '
					+ convertSearchValueInEnglish(actulID) + ' ';
			var searchValue = $('itemsSearchValue' + actulID).style.display == "inline" ?  DWRUtil.getValue('itemsSearchValue' + actulID) : "";
			_criterias[idx][0] = criteria + searchValue;
		}
		
		for (var findex = 0; findex < _criterias.length; findex++)
		{
			if (_criterias[findex] != undefined)
			{
				for (var sindex = 0; sindex <_criterias[findex].length; sindex++)
				{
					var indx = cpy.length;
					if (_criterias[findex][sindex] != null) {
						cpy[indx] = _criterias[findex][sindex];
					}
				}
			}
		}
		
		var cruise = true;
		var sign = 0;
		
		while (cruise) {
			for ( var i = sign; i < cpy.length; i++) {
				if (cpy[i] == 'AND') {
					if (cpy[i - 2] == null) {
						cpy.unshift('(');
						break;
					} else if (cpy[i - 4] != '(' && cpy[i - 4] != 'AND') {
						if (cpy[i - 3] != '(') {
							cpy.splice(i - 2, 0, '(');
							break;
						}
					}

					if (cpy[i + 4] == 'AND') {
						sign = i + 2;
						continue;
					} else {
						if (cpy[i + 4] == 'OR') {
							cpy.splice(i + 3, 0, ')');
							break;
						} else if (cpy[i + 4] == null) {
							cpy.push(')');
							cruise = false;
							break;
						}
					}
				}

				if (i == cpy.length - 1) {
					cruise = false;
					break;
				}
			}
		}
		
		_searchCriteriaResult = cpy.join('');
   }
	

	function convertSearchValueInEnglish(id)
	{
		var searchValue = $('itemsSearchValue' + id).value;
		var operValue = $('itemsSearchOperator' + id).value;

		if(currentPredicate[id] == 'boolean')
		{
			searchValue = BOOLEAN_MAP[operValue];
		}
		else if((currentPredicate[id] == 'foreign key') || (currentPredicate[id] == 'enumeration'))
		{
			var cpyOper = operValue;
			var equalInFr = 'est égal à';
			var v = cpyOper.indexOf(equalInFr);
			
            if(v == 0)
			{
				var res = "equals " + cpyOper.substring(v + 11);
				searchValue = res;
			}
            else
            {
            	searchValue = cpyOper;
            }

			
		}
		else
		{
			searchValue = operValue;
		}
		
		
		return searchValue;
	}
	
	function displayItems(){
		updateCurrentPredicate(1);
		outPutCriteriaResult();
		var viewName = DWRUtil.getValue('viewItemsSelect');
		if(viewName!=LABEL_SELECT_DATAOBJECT[language] && viewName!=""){	
			amalto.core.working();
			var columnsHeader = [];
			ItemsBrowserInterface.getViewables(viewName, language, function(result){		
				columnsHeader = result;
				displayItems2(columnsHeader,20);
				
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
	        	Ext.Msg.alert("error",error);
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
	
	    
		//Build a comma-dash separated list of search criteria  searchCriteriaResult
	    var criteria = _searchCriteriaResult;	

		var store = new Ext.data.Store({
			    proxy: new Ext.data.HttpProxy({
		        	url: '/itemsbrowser/secure/ItemsRemotePaging'
		        }),
		        //sortInfo:{field: _viewItems2.keys[sortIndex], direction: sortUporDown},
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
	        		hidden :true,
	        		xtype:'button',
	        		disabled:$('item-new-btn').disabled,
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
//										for(var i=0; i<itemPK.length; i++) {
//											ids += (ids==""?"":"@"); 
//											ids += itemPK[i];			
//										}							    	
										var treeIndex=1;
										if(_dataObject==null) _dataObject=_dataObject2;
										ItemsBrowserInterface.getUriArray(_dataObject, itemPK, function(picUriArray){
											var uriArray = [];
											uriArray=picUriArray;
											for (var index = 0; index < uriArray.length; index++) {
												var picUri=uriArray[index]
											     if(picUri!=""){
													 var pos=picUri.indexOf('?');
									 				 var uri=picUri.substring("/imageserver/".length,pos); 
													 Ext.Ajax.request({  
													       	 url:'/imageserver/secure/ImageDeleteServlet?uri='+uri,
													         method: 'post',  
													         callback: function(options, success, response) {  
													         }  
													 });
											     }//end if
			                               }//end for
			                            });//end callback									
										ItemsBrowserInterface.deleteItem(_dataObject, itemPK, function(result){
											if(result.lastIndexOf("ERROR")>-1){
												var err1=result.substring(7);
												Ext.MessageBox.alert("ERROR", err1);
												return;
											}
											amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);
																				
										});
					        		}
					        		//display
					        		displayItems();
					    		}				    			
				    		});	//comfirm		        			        		
		        		}
		        	}
	        	},
	        	new Ext.Toolbar.Separator(),
	        	{text:BUTTON_LOGICAL_DEL[language],
	        		id:'btn-logicaldelete',
	        		xtype:'button',
	        		disabled:$('item-new-btn').disabled,
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
//							    	var ids="";
//									for(var i=0; i<itemPK.length; i++) {
//										ids += (ids==""?"":"@"); 
//										ids += itemPK[i];			
//									}							    	
									var treeIndex=1;
									if(_dataObject==null) _dataObject=_dataObject2;
									ItemsBrowserInterface.logicalDeleteItem(_dataObject, itemPK, path, function(result){
											if(result.lastIndexOf("ERROR")>-1){
												var err1=result.substring(7);
												Ext.MessageBox.alert("ERROR", err1);
												return;
											}											
									});									
				        		}		
				        		displayItems();	
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
//		        'headerclick':function( g, columnIndex, e){
//		   						//alert(columnIndex);
//		                		var lineMax = DWRUtil.getValue('lineMaxItems');
//		                		if(lineMax==null || lineMax=="") 
//										lineMax=50;
//								sortIndex = columnIndex;
//								if(isUp){
//									isUp=false;
//									sortUporDown = "ASC";
//								}
//								else{
//									isUp = true;
//									sortUporDown = "DESC";
//								}
//								
//								store.load({limit:pageSize,field: _viewItems2.keys[sortIndex], direction: sortUporDown, viewName: viewName,criteria:criteria});
//		                	}
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
										lineMax=20;
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

		store.load({params:{start:0,limit:pageSize}});
		amalto.core.ready();

		store.on('load',function(){
			grid.render();
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
		
	    displayItemDetails2(itemPK2, dataObject, false ,displayItems);
	}
	
	function displayItemDetails4Duplicate(itemPK2, dataObject){
		
	    displayItemDetails2(itemPK2, dataObject, true, displayItems);
	    
	}
	
	function displayItemDetails4Reference(itemPK2, dataObject, refreshCB){
		
		DWREngine.setAsync(false); 
        ItemsBrowserInterface.prepareSessionForItemDetails(dataObject,language,function(status){});
		DWREngine.setAsync(true);
	    displayItemDetails2(itemPK2, dataObject, false, refreshCB);
	    
	}
	
	function displayItemDetails2(itemPK2, dataObject, isDuplicate, refreshCB){
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
		map[treeIndex] = [];
		if(dataObject==null) dataObject=_dataObject;
		ItemsBrowserInterface.getRootNode(dataObject, language, function(rootNode){
			
			var tabPanel = amalto.core.getTabPanel();
			var contentPanel=tabPanel.getItem('itemDetailsdiv'+treeIndex);
			if(contentPanel == undefined){
					
				var smartView = '';
				if(newItem[treeIndex]==false ) {
					smartView = '<iframe width="100%" height="100%" frameborder=0 scrolling=auto src="/itemsbrowser/secure/SmartViewServlet?ids='+ids+'&concept='+dataObject+'&language='+language+'">';
				}	
	
				//update the div structure
				var html =
				   '<div id="errorDesc'  + treeIndex + '" style="display:none;color:red;font-weight:bold;font-size:11px;padding-left:25px;padding-top:5px"><img src="img/genericUI/errorstate.gif" style="vertical-align:middle"/><span style="padding-left:10px;text-align:center;vertical-align:middle;">'
			             + errorDesc[language] + '</span></div>' +
				    '<div id="errorDetail' + treeIndex + '" style="display:none;color:red;font-weight:bold;font-size:11px;padding-left:65px"></div></br>'+
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
    						var length = map[treeIndex].length;
    						map[treeIndex][length + i] = tmp;
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
    						var length = map[treeIndex].length;
    						map[treeIndex][length + i] = tmp;
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
        			logicalDelItem(ids, dataObject, treeIndex,refreshCB);
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
    				saveItemWithoutQuit(ids,dataObject,treeIndex,refreshCB);
    			};
    			
    			tbDetail.saveItemAndQuitHandler = function(){			
    				saveItemAndQuit(ids,dataObject,treeIndex,refreshCB);
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
    			
    		
    			contentPanel = new Ext.Panel({
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
		
	    $('errorDetail' + treeIndex).style.display = "none";
	    
		if(node.updateValue(value)==false){
			ItemsBrowserInterface.updateNode(id, value, treeIndex, function() {
						// amalto.core.ready();
			
			$('errorDesc' + treeIndex).style.display = "block";
			return;
					});
		} 
		else
		{
		    if (updateItemNodesBeforeSaving(treeIndex) == true) {
				$('errorDesc' + treeIndex).style.display = "block";
			}
			else
			 $('errorDesc' + treeIndex).style.display = "none";
		}
		
		ItemsBrowserInterface.updateNode(id,value,treeIndex,function(result){
			amalto.core.ready(result);
		});
		amalto.core.ready();
		
		//set isdirty=true
		var itempanel = amalto.core.getTabPanel().activeTab;
		if(itempanel){
			itempanel.isdirty=true;
		}		
	}
	
	function setlastUpdatedInputFlag(id, treeIndex){
		//alert("flag");
		lastUpdatedInputFlag[treeIndex] = id;
	}
	//add by lym fix bug 0009620;
	function getSiblingsLength(node){
		var siblingLength = 0;
		if(node.parent != null){
			for(var i = 0;i < node.parent.children.length;i++){
				if(node.parent.children[i].itemData.name == node.itemData.name)
					siblingLength++;
			}
		}
		return siblingLength;
	}
	
	function cloneNode2(siblingId, hasIcon, treeIndex){
		var itemTree = itemTreeList[treeIndex];
		var siblingNode = itemTree.getNodeByIndex(siblingId);
		//modified by ymli. If the Items is more than maxOccurs, alert and return
		if(siblingNode.itemData.maxOccurs>=0&&siblingNode.parent!=null && getSiblingsLength(siblingNode)>=siblingNode.itemData.maxOccurs){
			Ext.MessageBox.alert("Status",siblingNode.itemData.maxOccurs+" "+siblingNode.itemData.name+"(s) at most");
			return;
		}
		var nodeCount = YAHOO.widget.TreeView.nodeCount;
	
		var newNode = new amalto.itemsbrowser.ItemNode(siblingNode.itemData,true,treeIndex,siblingNode.parent,false,true);
		newNode.updateNodeId(nodeCount);
		newNode.updateValue(" ");
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
	function showEditWindow(nodeIndex, treeIndex, nodeType){
		var tree=itemTreeList[treeIndex];
		var node=tree.getNodeByIndex(nodeIndex);
		var values=new Array();
		if(node.itemData.value!=null) values=node.itemData.value.split('@@');
		 var form = new Ext.form.FormPanel({
		        baseCls: 'x-plain',
		        labelWidth: 55,
		        url:'save-form.php',
		        defaultType: 'textfield',

		        items: [{
		            fieldLabel: 'Name',
		            id: 'name',
		            value: values[0]==null?"":values[0],
		            anchor:'100%'  
		        },{
		            fieldLabel: 'Url',
		            id: 'url',
		            value: values[1]==null?"http://":values[1],
		            anchor: '100%'  
		        }]
		    });

		    var window = new Ext.Window({
		        title: 'Edit Url',
		        width: 300,
		        height:200,
		        minWidth: 300,
		        minHeight: 200,
		        layout: 'fit',
		        plain:true,
		        bodyStyle:'padding:5px;',
		        buttonAlign:'center',
		        items: form,

		        buttons: [{
		            text: 'Save',
		            handler: function() {
		        	DWRUtil.setValue(nodeIndex+"Value",Ext.getCmp('name').getValue()+"@@"+Ext.getCmp('url').getValue());
		        	DWRUtil.setValue('showUrl',"<a target='_blank' href='"+ Ext.getCmp('url').getValue()+ "'>"+Ext.getCmp('name').getValue()+"</a>");
		        	updateNode(nodeIndex,treeIndex);
		        	window.destroy();
		        		
		           }
		        },{
		            text: 'Cancel',
		            handler: function() {
		        	window.destroy();
		        }
		        }]
		    });

		    window.show();
	}
	function editNode2(siblingId, hasIcon, treeIndex){
		var itemTree = itemTreeList[treeIndex];
		var siblingNode = itemTree.getNodeByIndex(siblingId);
		//modified by ymli. If the Items is more than maxOccurs, alert and return
		if(siblingNode.itemData.maxOccurs>=0&&siblingNode.parent!=null && getSiblingsLength(siblingNode)>=siblingNode.itemData.maxOccurs){
			Ext.MessageBox.alert("Status",siblingNode.itemData.maxOccurs+" "+siblingNode.itemData.name+"(s) at most");
			return;
		}
		var nodeCount = YAHOO.widget.TreeView.nodeCount;
	
		var newNode = new amalto.itemsbrowser.ItemNode(siblingNode.itemData,true,treeIndex,siblingNode.parent,false,true);
		newNode.updateNodeId(nodeCount);
		newNode.updateValue(" ");
	
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

	function removeNode2(id, treeIndex) {
		updateFlag[treeIndex] = true;
		var value = "";
		if (Ext.get(id + "Value"))
			value = DWRUtil.getValue(id + "Value");
		var itemTree = itemTreeList[treeIndex];
		// add by ymli
		// modified by ymli. If the Items is less than minOccurs, alert and
		// return
		var node = itemTree.getNodeByIndex(id);
		var siblingLength = getSiblingsLength(node);
		if (node.parent != null
				&& siblingLength<= node.itemData.minOccurs) {
			Ext.MessageBox.alert("Status",node.itemData.minOccurs + " " + node.itemData.name
					+ "(s) at least");
			return;
		} else if (siblingLength <= 1) {
			Ext.MessageBox.alert("Status",DELETE_ALERT[language]);
			return;
		}
		itemNodes.remove(itemTree.getNodeByIndex(id));
		itemTree.removeNode(itemTree.getNodeByIndex(id), true);
		ItemsBrowserInterface.removeNode(id, treeIndex, value,
				function(result) {
					amalto.core.ready(result);
				});
		itemTree.getRoot().refresh();
		amalto.core.ready();
	}

	function saveItemAndQuit(ids, dataObject, treeIndex, refreshCB) {
		// alert(DWRUtil.toDescriptiveString(itemPK2,2)+" "+dataObject+"
		// "+treeIndex);

		// saveItem(ids,dataObject,treeIndex);
		saveItem(ids, dataObject, treeIndex, function() {
					var itempanel = amalto.core.getTabPanel().activeTab;
					if (itempanel) {
						itempanel.isdirty = false;
					}
					amalto.core.getTabPanel().remove('itemDetailsdiv'
							+ treeIndex);
					refreshCB.call();
				});

	}
	
	function saveItemWithoutQuit(ids,dataObject,treeIndex,refreshCB){
		saveItem(ids,dataObject,treeIndex,function(){
				  //amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);
			      refreshCB.call();
					//set isdirty=true
					var itempanel = amalto.core.getTabPanel().activeTab;
					if(itempanel){
						itempanel.isdirty=false;
					}				      
				 });
	}
	
	function updateItemNodesBeforeSaving(treeIndex)
	{
	  var nodes = map[treeIndex];
	  var error = false;
	  for (var i = 0; i < nodes.length; i++) {
	  	var node = nodes[i];
	  	if (node!=null && node instanceof amalto.itemsbrowser.ItemNode)
	  	{
	  		if (node.itemData.choice == false && node.update() == false)
	  		  error = true;
	  		else if(node.itemData.choice == true)
	  		{
	  			var childNodesWithinChoice = node.parent.children;
	  			var allValid = true;
	  			for (var m = 0; m < childNodesWithinChoice.length; m++){
	  				if (childNodesWithinChoice[m] instanceof amalto.itemsbrowser.ItemNode){
	  					if (childNodesWithinChoice[m].update() == false){
	  						allValid = false;
	  					}
	  				}
	  			}
	  			if (allValid == true){
	  				var inputState = 0;
	  				for (var m = 0; m < childNodesWithinChoice.length; m++){
		  				if (childNodesWithinChoice[m] instanceof amalto.itemsbrowser.ItemNode){
		  					var nodeValue =  DWRUtil.getValue(childNodesWithinChoice[m].itemData.nodeId + "Value");
		  					if (nodeValue != "")
		  						inputState += 1;
		  				}
	  				}
	  				
	  				for (var n = 0; n < childNodesWithinChoice.length; n++){
		  				if (inputState == 0){
		  					childNodesWithinChoice[n].displayErrorMessage(childNodesWithinChoice[n].itemData.nodeId, CHOICE_NONE_ALERT[language]);
		  					error = true;
		  				}
		  				else if(inputState <= childNodesWithinChoice.length && inputState > 1){
		  					childNodesWithinChoice[n].displayErrorMessage(childNodesWithinChoice[n].itemData.nodeId, CHOICE_ALLTOGETHER_ALERT[language]);
		  					error = true;
		  				}
	  				}
	  			}
	  		}
	  	}
	  	   
	  }
	  return error;
	}
	
	function saveItem(ids,dataObject,treeIndex,callbackOnSuccess){
		if(navigator.appName=="Microsoft Internet Explorer" && lastUpdatedInputFlag[treeIndex]!=null) 

		{
					updateNode(lastUpdatedInputFlag[treeIndex], treeIndex);
				}
				
			    if (updateItemNodesBeforeSaving(treeIndex) == true) {
					$('errorDesc'+ treeIndex).style.display = "block";
					$('errorDetail'+ treeIndex).style.display = "none";
					return;
				}
				
			    $('errorDesc'+ treeIndex).style.display = "none";
			    $('errorDetail'+ treeIndex).style.display = "none";
				ItemsBrowserInterface.checkIfDocumentExists(keys[treeIndex], dataObject, function(result){
					if(result==true) {
//						if(!Ext.MessageBox.confirm(MSG_CONFIRM_SAVE_ITEM[language])) return;
						Ext.Msg.confirm("confirm",MSG_CONFIRM_SAVE_ITEM[language],function re(en){
							if(en=="no")
								return;});
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
		                   Ext.MessageBox.alert("Status",result.substring(8));
		                }else{
					       if(callbackOnSuccess)callbackOnSuccess();   
						}
						
					},
					errorHandler:function(errorString, exception) {//on exception
					var reg = /\[(.*?):(.*?)\]/gi;
					var errorsArray = errorString.match(reg);
					if(errorsArray!=null){
						for(var i=0;i<errorsArray.length;i++){
							  if(errorsArray[i].indexOf("[")>=0){
								  errorsArray[i] = errorsArray[i].replace("[","").trim();
							  }
							  if(errorsArray[i].indexOf("]")>=0){
							      errorsArray[i] = errorsArray[i].replace("]"," ").trim();
							  }
						   }
						   var flag=false;
						   var defualtErrorMsg="";
						   for(var i=0;i<errorsArray.length;i++){
							   if(language==errorsArray[i].split(":")[0].toLowerCase()&&errorsArray[i].split(":")[1]!=null&&errorsArray[i].split(":")[0].trim()!=""){
								   errorString=errorsArray[i].substr(errorsArray[i].indexOf(":")+1);
								   flag=true;
							   }
							   if("en"==errorsArray[i].split(":")[0].toLowerCase()){
//								   defualtErrorMsg=errorsArray[i].split(":",1)[1];
								   defualtErrorMsg=errorsArray[i].substr(errorsArray[i].indexOf(":")+1);
							   	
							   }
						   }
						   if(!flag){
							   errorString=defualtErrorMsg;
						   }
					   }
					   var error = itemTreeList[treeIndex];
		               $('errorDesc'+ treeIndex).style.display = "block";
		                var reCat = /\[Error\].*\n/gi;
		                var innerHml ="";
		             	var arrMactches = errorString.match(reCat);
		             	if (arrMactches != null)
			             	for (var i=0; i < arrMactches.length ; i++)
							{
							  innerHml +=arrMactches[i];
							  if (i < arrMactches.length-1)
							   innerHml += '<br/>';
							}
						else
						 innerHml += errorString +'<br/>'
						$('errorDetail' + treeIndex).style.display = "block";
						$('errorDetail' + treeIndex).innerHTML = innerHml;
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
			ItemsBrowserInterface.getUriArray(dataObject, itemPK, function(picUriArray){
				var uriArray = [];
				uriArray=picUriArray;
				for (var index = 0; index < uriArray.length; index++) {
					var picUri=uriArray[index]
				if(picUri!=""){
					 var pos=picUri.indexOf('?');
	 				 var uri=picUri.substring("/imageserver/".length,pos); 
				     Ext.Ajax.request({  
				       	 url:'/imageserver/secure/ImageDeleteServlet?uri='+uri,
				         method: 'post',  
				         callback: function(options, success, response) {  
				         }  
				     });    
				  }
			   }
			});
			ItemsBrowserInterface.deleteItem(dataObject, itemPK, function(result){
				if(result.lastIndexOf("ERROR")>-1){
					var err1=result.substring(7);
					//Ext.MessageBox.alert("ERROR", err1);
					$('errorDetail' + treeIndex).style.display = "block";
					$('errorDetail' + treeIndex).innerHTML ="<br/>"+err1+"<br/>";					
					return;
				}
				else if(result.lastIndexOf)
				var itempanel = amalto.core.getTabPanel().activeTab;
				if(itempanel){
					itempanel.isdirty=false;
				}				
				amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);
				amalto.core.ready(result);
				displayItems();
				
				if(result)Ext.MessageBox.alert('Status', result);
			});		
		}});		
	}
	
	function logicalDelOneItem(ids, dataObject, treeIndex, path, refreshCB){
		var tmp = "";
		var itemPK = ids.split('@');
		for(var i=0; i<itemPK.length; i++) {
			tmp += " "+itemPK[i];
		}
		ItemsBrowserInterface.logicalDeleteItem(dataObject, itemPK, path, function(result){
			if(result.lastIndexOf("ERROR")>-1){
				var err1=result.substring(7);
				//Ext.MessageBox.alert("ERROR", err1);
				$('errorDetail' + treeIndex).style.display = "block";
				$('errorDetail' + treeIndex).innerHTML ="<br/>"+err1+"<br/>";					
				return;
			}			
			var itempanel = amalto.core.getTabPanel().activeTab;
			if(itempanel){
				itempanel.isdirty=false;
			}			
			amalto.core.getTabPanel().remove('itemDetailsdiv'+treeIndex);
			amalto.core.ready(result);
			//displayItems();
			refreshCB.call();
			if(result)Ext.MessageBox.alert('Status', result);				
		});
	}
	
	function logicalDelItem(ids, dataObject, treeIndex, refreshCB){
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
			logicalDelOneItem(ids,dataObject, treeIndex,path,refreshCB);
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
	
	function showDatePicker(nodeId, treeIndex, nodeType) {
		if (nodeDatePickerWindow) {
			nodeDatePickerWindow.hide();
			nodeDatePickerWindow.destroy();
		}

		var inputText = nodeId + "Value";
		if(treeIndex == -1)
		{
			inputText = nodeId;
		}
		
		var nodeDatePickerPanel = new Ext.form.FormPanel({
					baseCls : 'x-plain',
					labelAlign : 'right',
					// layout:'fit',
					defaultType : 'datefield',
					items : [{
								id : 'date111',
								xtype : 'datepicker',
								fieldLabel : 'nodeDatePickerPanel ',
								name : 'datePicker',
								layout : 'fit',
								inputType : 'textfield',
								listeners : {

									select : function(src, date) {
										var setValue = date.format("Y-m-d");
										if (nodeType == "dateTime") {
											setValue += "T00:00:00"
										};
										DWRUtil.setValue(inputText, setValue);
										if(treeIndex != -1)
										{
										  updateNode(nodeId, treeIndex);	
										}
										nodeDatePickerWindow.hide();
										nodeDatePickerWindow.destroy();										
									}
								}
							}]
				});
		nodeDatePickerWindow = new Ext.Window({
					title : 'Date Picker',
					width : 205,
					height : 240,
					layout : 'fit',
					plain : true,
					bodyStyle : 'padding:1px;',
					buttonAlign : 'center',
					items : nodeDatePickerPanel
				});

		// var value1 = nodeDatePickerPanel.getForm().getValues('date111');
		var initValue = $(inputText).value;
		if (initValue != null && initValue != "") {
			if (initValue.indexOf('T') != -1)
				initValue = initValue.substring(0, initValue.indexOf('T'));
			// nodeDatePicker.setValue(Date.parseDate(initValue,"Y-m-d"));
		}

		nodeDatePickerWindow.show();
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
				    if($(nodeId+'showPicture'))
				       $(nodeId+'showPicture').src='img/genericUI/no_image.gif';		            
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
		            Ext.Msg.alert('Success', 'File upload sucessfully!');
		            if($(nodeId+'showPicture'))$(nodeId+'showPicture').src=url;
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
	    
		//ItemsBrowserInterface.countForeignKey(xpathForeignKey, function(count){
		ItemsBrowserInterface.countForeignKey_filter(xpathForeignKey, function(count){
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
                width: 280,
                resizable:true, 
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
                	DWRUtil.setValue(nodeId+'Value', record.get("keys"));//+"--"+record.get("infos"));
                	updateNode(nodeId,treeIndex);
		        	foreignKeyWindow.hide();
		        }
		    });

			
			foreignKeyWindow = new Ext.Window({
                layout:'fit',
                width:300,
                height: 150,
                resizable:true, 
                closeAction:'hide',
                plain: true,
                title: TITLE_WINDOW_FK[language]+'<br/>('+count+' '+(count>1?MESSAGE_MULTI_SHOW[language]+')':MESSAGE_SINGLE_SHOW[language]+')'),
                
                items: [new Ext.form.FormPanel({
                	labelAlign: 'top',
                    items: [
                        new Ext.Panel({
                       	 	html: '', 
               		 		border: false
               		 	}),
	                  	combo
	                ]
                })]

            });
			
			foreignKeyWindow.on('show', function() {
				var combo = Ext.getCmp('foreign-key-filter');
				combo.focus(true, 100);
				//combo.setSize(foreignKeyWindow.getSize());
				combo.reset();
				if(count<500) {
					
					combo.setRawValue("");
					combo.doQuery(".*",true);
					combo.focus();
		            combo.expand();

				}
			});

			/*foreignKeyWindow.on('syncSize', function() {
				var combo = Ext.getCmp('foreign-key-filter');
				combo.focus(true, 100);
				combo.reset();
				if(count<500) {
					combo.setSize(foreignKeyWindow.getSize());
					combo.setRawValue(".*");
					combo.doQuery(".*",true);
					combo.focus();
		            combo.expand();
				}
			});*/
			foreignKeyWindow.show(this);
			
		});
		
		
	}
	
	var fnLoadData2;
	
		
	function browseForeignKey(nodeId, foreignKeyXpath){
		//Check if have a Primary key made of multiple Item Ids or a single one
		var keys = DWRUtil.getValue(nodeId+'Value');
		var keyValue = keys;
		
		//edit by ymli.fix bug 0009625: Made the foreign key like "{key} - {info}" 
		//revert it
	/*	if(DWRUtil.getValue(nodeId+'Value').match(/(.*?)--(.*?)/)){
			var r = "";
			r=keys.match(/(.*?)--(.*?)/);
			keyValue = r[1];//.replace(/\{|\}/g,"");
		}*/
		/*if(DWRUtil.getValue(nodeId+'Value').match(/\[(.*?)\]/g)!=null){
			var tmp =  DWRUtil.getValue(nodeId+'Value').replace(/\[(.*?)\]/g,"$1###").split("###");
			var itemPK =tmp.splice(0,tmp.length-1);
		} else{
			var itemPK = [DWRUtil.getValue(nodeId+'Value')];
		}*/
		if(keyValue.match(/\[(.*?)\]/g)!=null){
		     var result = new Array();
			 var aggregate = 0;
			 var cordon = 0;
		     for(var i = 0; i < keyValue.length; i++)
			 {
		        var ch = keyValue.charAt(i);
				if(ch == '[')
				{
				 aggregate++;
				 if(aggregate == 1)
				 {
				   cordon = i;
				 }
				}
				else if(ch == ']')
				{
		          aggregate--;
				  if(aggregate == 0)
				  {
					 result.push(keyValue.substring(cordon+1, i));
				  }
				}
			 }
			var itemPK =result;
		} else{
			var itemPK = [keyValue];
		}
		
		var dataObject = foreignKeyXpath.split("/")[0];
		if(dataObject.split("[")[0] != null)
		{
			dataObject = dataObject.split("[")[0];
		}
		if(itemPK =="")
			Ext.Msg.alert("Warning","The concept does not exist.");
		else
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
		itemsCriteriaWithConstraints:function(criteriaParent, id, add){itemsCriteriaWithConstraints(criteriaParent, id, add);},
		outPutCriteriaResult:function(){outPutCriteriaResult();},
		convertSearchValueInEnglish:function(id){convertSearchValueInEnglish(id);},
		updateOperatorList:function(id){updateOperatorList(id);},
		updateNode:function(id,treeIndex){updateNode(id,treeIndex);},
		setlastUpdatedInputFlagPublic:function(id,treeIndex){setlastUpdatedInputFlag(id,treeIndex);},
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
		editItemDetails:function(itemPK,dataObject,refreshCB){displayItemDetails4Reference(itemPK,dataObject,refreshCB);},
		filterForeignKey:function(string0, string1, id){filterForeignKey(string0, string1, id);},
		getSiblingsLength:function(node){getSiblingsLength(node);},
		showEditWindow:function(nodeIndex, treeIndex, nodeType){showEditWindow(nodeIndex, treeIndex, nodeType);}
 	}
}();
