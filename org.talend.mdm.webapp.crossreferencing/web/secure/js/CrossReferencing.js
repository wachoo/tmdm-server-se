amalto.namespace("amalto.crossreferencing");


amalto.crossreferencing.CrossReferencing = function () {

    loadResource("/crossreferencing/secure/dwr/interface/CrossReferencingInterface.js", "" );

    if(!Array.contains){
        Array.prototype.contains = function(obj){
            for(var i=0; i<this.length; i++){
                if(this[i]==obj){
                    return true;
                }
            }
        };
    }

    /**
     * EditorGrid validation plugin
     * Adds validation functions to the grid
     *
     * @author  Jozef Sakalos, aka Saki
     * @version 0.1
     *
     * Usage:
     * grid = new Ext.grid.EditorGrid({plugins:new GridValidator(), ...})
     */
    GridValidator = function(config) {

        // initialize plugin
        this.init = function(grid) {
            Ext.apply(grid, {
                /**
                 * Checks if a grid cell is valid
                 * @param {Integer} col Cell column index
                 * @param {Integer} row Cell row index
                 * @return {Boolean} true = valid, false = invalid
                 */
                isCellValid:function(col, row) {
                    if(!this.colModel.isCellEditable(col, row)) {
                        return true;
                    }
                    var ed = this.colModel.getCellEditor(col, row);
                    if(!ed) {
                        return true;
                    }
                    var record = this.store.getAt(row);
                    if(!record) {
                        return true;
                    }
                    var field = this.colModel.getDataIndex(col);
                    ed.field.setValue(record.data[field]);
                    return ed.field.isValid(true);
                } // end of function isCellValid

                /**
                 * Checks if grid has valid data
                 * @param {Boolean} editInvalid true to automatically start editing of the first invalid cell
                 * @return {Boolean} true = valid, false = invalid
                 */
                ,isValid:function(editInvalid) {
                    var cols = this.colModel.getColumnCount();
                    var rows = this.store.getCount();
                    var r, c;
                    var valid = true;
                    for(r = 0; r < rows; r++) {
                        for(c = 0; c < cols; c++) {
                            valid = this.isCellValid(c, r);
                            if(!valid) {
                                break;
                            }
                        }
                        if(!valid) {
                            break;
                        }
                    }
                    if(editInvalid && !valid) {
                        this.startEditing(r, c);
                    }
                    return valid;
                } // end of function isValid
            });
        }; // end of function init
    }; // GridValidator plugin end


	var LABEL_CROSS_REFERENCING = {
		'fr':'Transcodifications',
		'en':'Cross-referencing tables'
	}
	var LABEL_SELECT_TABLE={
		'fr':'Tables existantes',
		'en':'Select an existing table'
	}
	var BUTTON_NEW_CROSS_REF = {
		'fr':'Nouvelle table de transcodification',
		'en':'New cross-referencing table'
	}
	var BUTTON_IMPORT = {
		'fr':'Importer des données',
		'en':'Upload data file'
	}
	var BUTTON_DELETE = {
		'fr':'Supprimer',
		'en':'Delete'
	}
	var BUTTON_EDIT = {
		'fr':'Editer',
		'en':'Edit'
	}
	var BUTTON_ADD_ROW = {
		'fr':'Ajouter une ligne',
		'en':'Add row'
	}
	var BUTTON_DELETE_ROW={
		'fr':'Supprimer la sélection',
		'en':'Delete rows'
	}
	var SAVE = {
		'fr':'Sauvegarder',
		'en':'Save'
	}

	var LOADING={
	'fr':'Chargement...',
	'en':'Loading...'
	}

	var MSG_AT_LEAST_ONE_KEY =  {
    'fr' : 'La table doit avoir au moins une clé',
    'en' : 'The table must have at least one key'
	}

	var MSG_INVALID_TABLE_NAME =  {
	    'fr' : 'La table doit avoir un nom commencant par une lettre suivie de caractères alphanumériques',
	    'en' : 'The table name must start with a letter followed by aphanumeric characters'
	}

	var MSG_INVALID_FIELD_NAME =  {
	    'fr' : 'Le nom de champ n\'est pas valide, champ  ',
	    'en' : 'The field name is not valid, field '
	}

	var LABEL_FIELD={
		'fr':'Champ',
		'en':'Field'
	}

	var BTN_EDIT =  {
	    'fr' : 'Editer',
	    'en' : 'Edit'
	}

	var BTN_DELETE =  {
	    'fr' : 'Supprimer',
	    'en' : 'Delete'
	}

	var ADD_FIELD_BUTTON = {
	    'fr' : "Ajouter un champ" ,
	    'en' : "Add a field"
	}

	var UPLOAD_DATA_LEGEND = {
		'fr' : "Ajouter des donn&eacute;es &agrave; la table : ",
		'en' : "Upload data file to table : "
	}

	var HEADER_GRID_DELETE={
		'fr':"Supprimer",
		'en':"Delete"
	}

	var CONFIRM_DELETE_TRANSCO = {
		'en':"Are you sure you want to delete this cross-referencing table?",
		'fr':"\312tes-vous s\373r de vouloir supprimer cette table de transcodification?"
	}

	var CONFIRM_DELETE_ELEMENT = {
		'fr': "\312tes-vous s\373r de vouloir supprimer cet \351l\351ment?",
		'en': "Are you sure you want to delete this element ?"
	}

	var LEGEND1={
		'fr':"Tables de transcodification existantes",
		'en':"Cross-referencing"
	}

	var LABEL_DESCRIPTION={
		'fr':'Description',
		'en':'Description'
	}
	var LABEL_UPLOAD={
		'fr':'Importer des données',
		'en':'Upload data'
	}
	var LABEL_FILE={
		'fr':'Fichier',
		'en':'File'
	}
	var LABEL_FILE_TYPE={
		'fr':'Type de fichier',
		'en':'File type'
	}
	var LABEL_SEPARATOR={
		'fr':'Séparateur',
		'en':'Separator'
	}
	var LABEL_DELIMITER={
		'fr':'Délimiteur de texte',
		'en':'Text Delimiter'
	}
	var LABEL_ENCODING={
		'fr':'Encodage',
		'en':'Encoding'
	}
	var LABEL_HEADERS_ON_FIRST_LINE={
		'fr':'En-têtes sur 1ère ligne',
		'en':'Headres on First Line'
	}

	var BUTTON_SEND={
		'fr':'Envoyer',
		'en':'Submit'
	}
	var LABEL_NEW_CROSS_REF={
		'fr':'Nouvelle table de transcodification',
		'en':'New cross-referencing table'
	}
	var LABEL_TABLE_NAME={
		'fr':'Nom de la table',
		'en':'Table name'
	}
	var KEY={
		'fr':'Clé',
		'en':'Key'
	}

	var DISCARD={
		'fr':'Annuler vos changements?',
		'en':'Discard your changes?'
	}

	var FIELDS_DO_NOT_MATCH = {
		'fr': 'Un ou plusieurs champs ne respectent pas leur format<br/>',
		'en': 'One or more fields do not match their requested pattern<br/>'
	};

	var ERROR ={
			'fr':'Erreur',
			'en':'Error'
	};

	var concept;
	var tableDescription;
	var panelCrossRef;
	var grid = null;

	var editedRows = new Array();
	var newRows = new Array();
	var recordFields = new Array();

	var row = 1;

	function displayCrossRefMainPanel(){
		var tabPanel = amalto.core.getTabPanel();
		var panel;
		if(tabPanel.getItem('crossref-main-panel') == undefined){

			panelCrossRef = new Ext.Panel({
				id: 'crossref-main-panel',
				title: LABEL_CROSS_REFERENCING[language],
				layout:'border',
				autoScroll: false,
				border: false,
				bodyBorder:false,
				closable: true,
				items:
				[
					new Ext.Panel({
						id: 'center-panel-crossref',
						//title: TITLE_SEARCH_RESULT[language],
			    		region: 'center',
			    		layout:'fit',
						border: false,
						bodyborder: false,
						//height:300,
						header:true,
						//split:true,
						collapsible: false
					})
				],
				tbar:[
					new Ext.form.ComboBox({
						id:'comboBoxTablesName',
						emptyText:LABEL_SELECT_TABLE[language],
						store: new Ext.data.Store({
								proxy: new Ext.data.SimpleDWRProxy(CrossReferencingInterface.getCrossReferencingTableNames),
					        	reader: new Ext.data.MapReader()
							}),
						displayField: 'value',
						valueField: 'key',
					  	loadingText:LOADING[language],
			         	mode:'remote',
			          	triggerAction:'all',
			          	editable:false,
			          	listeners:{
			          		'select' : function( combo, record, index ){
			          				if(grid!=null && grid.getStore().getModifiedRecords().length>0){
			          					if(!confirm(DISCARD[language])){
			          						return;
			          					}
			          				}
				                	concept = record.data.key;
				                	CrossReferencingInterface.getTableDescription(concept,function(desc){
				                			tableDescription = desc;
										    getCrossReferencingContent();
				                	})
			          		}
			          	}
					}),
					new Ext.Toolbar.Spacer(),
					new Ext.Toolbar.Button({
				        text: BUTTON_EDIT[language],
						handler: getCrossReferencingContent
			    	}),
			    	new Ext.Toolbar.Separator(),
			    	new Ext.Toolbar.Button({
				        text: BUTTON_IMPORT[language],
						handler: uploadFile
			    	}),
			    	new Ext.Toolbar.Separator(),
			    	new Ext.Toolbar.Button({
				        text: BUTTON_DELETE[language],
						handler: deleteTransco
			    	}),
			    	new Ext.Toolbar.Separator(),
					new Ext.Toolbar.Button({
		            	text: BUTTON_NEW_CROSS_REF[language],
						handler: createCrossRef
					})
				]
			});
			tabPanel.add(panelCrossRef);
		}

		panelCrossRef.show();
		//panel.doLayout();
		amalto.core.doLayout();
	}

/************************************************************
ASG                DISPLAY TRANSCO TABLE
************************************************************/

	function getCrossReferencingContent(){
		var panel = Ext.getCmp('center-panel-crossref');
		panel.remove('main-crossref');
		if(tableDescription==null){
			amalto.core.doLayout();
			return;
		}
		CrossReferencingInterface.getCrossReferencingContent(getCrossReferencingContentCB,concept);
	}

	function getCrossReferencingContentCB(result){
		recordFields = new Array();
		for(var i=0; i<tableDescription.fields.length; i++) {
			recordFields.push({name:tableDescription.fields[i]});
		}
		recordFields.push({name:'trash'});
	    var store = new Ext.data.SimpleStore({
	        fields: recordFields
	    });

		//Customer render
		var trash = function (){
			return "<img src='img/genericUI/trash.gif' border=\"0\" />";
		};

		//Column model
		var myColumns = new Array();
		//myColumns[0]={header: "No", sortable: true, width:30};

	   // myColumns.push(new Ext.grid.CheckboxSelectionModel({singleSelect:false}));
	    //myColumns.push(new Ext.grid.RowNumberer());
		for(var k=0;k<tableDescription.fields.length;k++){
			//TODO	if key then no editor
			myColumns.push(
				{
					header: tableDescription.fields[k],
					dataIndex:tableDescription.fields[k],
					editor: new Ext.form.TextField(
						{allowBlank: (tableDescription.keys.contains(tableDescription.fields[k])?false:true)}
					),
					sortable: true
				}
			);
		}
		myColumns.push({header: HEADER_GRID_DELETE[language], dataIndex:"trash",width: 50, sortable: true,renderer:trash});


	    //Grid
	    grid = new Ext.grid.EditorGridPanel({
		    id: 'main-crossref',
   		    border:false,
			height: 200,
		    store: store,
		    cm: new Ext.grid.ColumnModel(myColumns),
    		viewConfig: {
		    	autoFill:true
		       // forceFit: true
		    },
		    plugins:new GridValidator(),
		    listeners: {
	                	cellclick: function(g, rowIndex,  columnIndex, e){
								if (columnIndex ==tableDescription.fields.length) {
									var index = newRows.indexOf(rowIndex);
									 if(index>-1){
									 	newRows.splice(index,1);
									 }
									 else{
									 	if (confirm(CONFIRM_DELETE_ELEMENT[language])){
									 		deleteRow(store.getAt(rowIndex));

									 	}
									 }

								}
	                	},
	                	afteredit: function(e){
	            		        if(!(newRows.indexOf(e.row)>-1 || editedRows.indexOf(e.row)>-1)){
						            editedRows.push(e.row);
						        }
	                	}
	        },
		    bbar:[
			    new Ext.Toolbar.Button({
			            	text: BUTTON_ADD_ROW[language],
							handler: addRowToTable
					}),
				new Ext.Toolbar.Separator(),
				new Ext.Toolbar.Button({
				            	text: SAVE[language],
								handler: saveGrid
					})
		    ]
	    });


	    var panel = Ext.getCmp('center-panel-crossref');
		if(Ext.get('main-crossref')!=undefined) {
			panel.remove('main-crossref');
		}
		panel.add(grid);
		amalto.core.doLayout();
		if(result!=null) store.loadData(result);
	}

/************************************************************
                EDIT TRANSCO TABLE
************************************************************/

	function deleteRow(record){
        var keys = tableDescription.keys;
        var fields = tableDescription.fields;

	    var selected=new Array();
    	var values = new Array();
    	for(var j=0; j<fields.length; j++) {
    		values.push(record.get(fields[j]));
    	}
    	CrossReferencingInterface.deleteDocument(concept,keys,fields,values,function(){
    		var store = grid.getStore();
    		store.remove(record);
    		//getCrossReferencingContent();
    	});
	}

	function addRowToTable(){
		var recordType = Ext.data.Record.create(recordFields);
		var orec = new recordType();
		var data = new Array();
		for(var i=0; i<recordFields.length; i++) {
			data[recordFields[i].name]="";
		}
        orec.data = data;
        orec.data.newRecord = true;
        orec.commit();
        grid.stopEditing();
        grid.getStore().add( orec);
        grid.startEditing(grid.getStore().getCount()-1, 0);
		newRows.push(grid.getStore().getCount()-1);
	}


	function saveGrid(){
	    var keys = tableDescription.keys;
	    var fields = tableDescription.fields;
	    var values = new Array();
	    var value ="";


		if(!grid.isValid(true)){
			Ext.MessageBox.show({
				title:ERROR[language],
				msg:FIELDS_DO_NOT_MATCH[language],
				icon:Ext.MessageBox.ERROR,
				width:500,buttons: Ext.Msg.OK
				});
			return;
		}

	    //rows to update
	    for(var i=0;i<editedRows.length;i++){
	    	var record = grid.getStore().getAt(editedRows[i]);
	        for(var j=0;j<tableDescription.fields.length;j++){
	            values[j]=record.get(tableDescription.fields[j]);
	        }
	        CrossReferencingInterface.updateDocument(concept,fields,values,function(result){
	        	if(result=="OK")
	        		amalto.core.ready('Successfully saved');
	        	});
	    }

	    //rows to insert
	    for(var i=0;i<newRows.length;i++){
	        var record = grid.getStore().getAt(newRows[i]);
	        for(var j=0;j<tableDescription.fields.length;j++){
	            values[j]=record.get(tableDescription.fields[j]);
	        }
	        CrossReferencingInterface.addDocument(concept,fields,values,function(result){
	        	if(result=="OK")
	        		amalto.core.ready('Successfully saved');
	        	});
	    }

	    //empty var
	    editedRows = new Array();
	    newRows = new Array();
	    grid.getStore().commitChanges();

	}

/************************************************************
                ADD DATA
************************************************************/
	function uploadFile(){
		var panel = Ext.getCmp('center-panel-crossref');
		if(Ext.get('main-crossref')!=undefined) {
			panel.remove('main-crossref');
		}
		if(tableDescription==null){
			amalto.core.doLayout();
			return;
		}
		var uploadPanel = new Ext.FormPanel({
			title:LABEL_UPLOAD[language],
			id:'main-crossref',
            url: '/crossreferencing/secure/upload',
			fileUpload: true,
			border:false,
			bodyBorder:false,
			autoScroll: true,
			labelWidth:150,
			buttonAlign:'left',
			bodyStyle:'padding:5px',
			defaults: {labelSeparator:''},
			items:[
				{
					name:'concept',
					xtype:'textfield',
					//fieldLabel:LABEL_CROSS_REFERENCING[language],
					readOnly:true,
					inputType:'hidden',
					value:tableDescription.name
				},
				{
					name:'fileToUpload',
					xtype:'textfield',
					fieldLabel:LABEL_FILE[language],
					inputType:'file'
				},
				new Ext.form.ComboBox({
            		id:'fileTypeCombo',
            		//name:'fileType',
                	fieldLabel: LABEL_FILE_TYPE[language],
                	store: new Ext.data.SimpleStore({
					  fields: ["key","value"],
					  data: [
			        		['excel','Excel'],
			        		['csv','CSV']
					  ]
					}),
					hiddenName:'fileType',
					emptyText: 'Select ...',
					valueField: "key",
					displayField: "value",
					mode:'local',
					triggerAction:'all',
					editable:false,
					listeners:{
		          		'select' : function( combo, record, index ){
			                	var value = record.data.key;
			                	if(value=="csv"){
			                		Ext.getCmp('sep').setDisabled(false);
			                		Ext.getCmp('encoding').setDisabled(false);
			                		Ext.getCmp('delimiter').setDisabled(false);
			                		//Ext.getCmp('headersOnFirstLine').setDisabled(false);
			                	}
			                	else{
			                		Ext.getCmp('sep').setDisabled(true);
			                		Ext.getCmp('encoding').setDisabled(true);
			                		Ext.getCmp('delimiter').setDisabled(true);
			                		//Ext.getCmp('headersOnFirstLine').setDisabled(true);
			                	}
			                }
            		}
        		}),
        		new Ext.form.Checkbox({
            		id:'headersOnFirstLine',
            		name:'headersOnFirstLine',
                	fieldLabel: LABEL_HEADERS_ON_FIRST_LINE[language],
                	checked: true,
					disabled:false
        		}),
				new Ext.form.ComboBox({
            		id:'sep',
            		name:'sep',
                	fieldLabel: LABEL_SEPARATOR[language],
                	store: new Ext.data.SimpleStore({
					  fields: ["key","value"],
					  data: [
			        		['comma','virgule'],
			        		['semicolon','point virgule']
					  ]
					}),
					valueField: "key",
					displayField: "key",
					mode:'local',
					disabled:true,
					triggerAction:'all',
					editable:false
        		}),
        		new Ext.form.ComboBox({
            		id:'delimiter',
            		name:'delimiter',
                	fieldLabel: LABEL_DELIMITER[language],
                	store: new Ext.data.SimpleStore({
					  fields: ["key","value"],
					  data: [
			        		['"','double quote'],
			        		["'",'single quote'],
			        		['','none']
					  ]
					}),
					valueField: "key",
					displayField: "key",
					mode:'local',
					disabled:true,
					triggerAction:'all',
					editable:false
        		}),
				new Ext.form.ComboBox({
            		id:'encoding',
            		name:'encoding',
                	fieldLabel: LABEL_ENCODING[language],
                	store: new Ext.data.SimpleStore({
					  fields: ["key","value"],
					  data: [
			        		['utf-8','utf-8 / Unicode 8'],
			        		['iso-8859-1','iso-8859-1 / Latin 1'],
			        		['iso-8859-15','iso-8859-15 / Latin 15'],
			        		['cp-1252','cp-1252 / Windows 1252']
					  ]
					}),
					valueField: "key",
					displayField: "key",
					mode:'local',
					disabled:true,
					triggerAction:'all',
					editable:false
        		})
			],
            buttons: [{
                text: BUTTON_SEND[language],
                handler: function() {
                    var form = uploadPanel.getForm();
                    if (form.isValid()) {
                        form.submit({
                            waitMsg: 'Sending...',
                            success: function(){this.uploaded; getCrossReferencingContent();},
                            failure:  function(response,options) {
							Ext.MessageBox.show({title:'Error', msg:'Registration failed: '+response.responseText,
							icon:Ext.MessageBox.ERROR,
							width:300,buttons: Ext.Msg.OK});
						},
                            scope: this
                        });

                    }
                },
                scope: this
            }]
		});

		panel.add(uploadPanel);
		panel.doLayout();
	}


/************************************************************
                CREATE DELETE TRANSCO TABLE
************************************************************/

	function deleteTransco(){
		if(tableDescription==null) return;
		if(confirm(CONFIRM_DELETE_TRANSCO[language])){
            CrossReferencingInterface.deleteCrossReferencingTable(concept, function(){
            	tableDescription = null;
            	Ext.getCmp('comboBoxTablesName').reset();
            	//Ext.getCmp('transcoInfo').reset();
            	Ext.getCmp('comboBoxTablesName').store.reload();
            	Ext.getCmp('center-panel-crossref').remove('main-crossref');
            });
        }
	}


	function createCrossRef(){
		row = 1;
		Ext.getCmp('comboBoxTablesName').reset();
		var tabPanel = amalto.core.getTabPanel();

//		Ext.QuickTips.init();
		var panel = new Ext.FormPanel({
			id:'main-crossref',
			title:LABEL_NEW_CROSS_REF[language],
			closable:true,
			border:false,
			bodyBorder:false,
			autoScroll: true,
			labelWidth:150,
			buttonAlign:'left',
			bodyStyle:'padding:5px',
			defaults: {labelSeparator:''},
			items:[
				{
					xtype:'textfield',
					id:'transcoName',
					fieldLabel:LABEL_TABLE_NAME[language]
				},
				{
					layout:'column',
					border:false,
					items:[
						{
							columnWidth:.49,
							border:false,
							layout:'form',
							labelWidth:150,
							defaults: {labelSeparator:''},
							items:[
								{
									xtype:'textfield',
									id:'field1',
									fieldLabel:LABEL_FIELD[language]+' 1'
								}
							]
						},{
							columnWidth:.49,
							border:false,
							layout:'form',
							labelWidth:40,
							defaults: {labelSeparator:''},
							items:[
								{
			                		xtype:'checkbox',
			                		id:'key1',
			                		fieldLabel:KEY[language],
			                		tooltip: 'This field will be a key'
								}
							]
						}
					]
				}/*,
				{
					layout:'column',
					border:false,
					items:[
						{
							columnWidth:.49,
							border:false,
							layout:'form',
							labelWidth:150,
							defaults: {labelSeparator:''},
							items:[
								{
									xtype:'textfield',
									id:'field2',
									fieldLabel:LABEL_FIELD[language]+' 2'
								}
							]
						},{
							columnWidth:.49,
							border:false,
							layout:'form',
							labelWidth:40,
							defaults: {labelSeparator:''},
							items:[
								{
			                		xtype:'checkbox',
			                		id:'key2',
			                		fieldLabel:KEY[language],
			                		tooltip: 'This field will be a key'
								}
							]
						}
					]
				}*/
			],
			buttons:[
				new Ext.Button({
					text:SAVE[language],
					handler:saveTransco
				}),
				new Ext.Toolbar.Separator(),
				new Ext.Button({
					text:ADD_FIELD_BUTTON[language],
					handler:addTranscoField
				})/*,
				new Ext.Toolbar.Separator(),
				new Ext.Button({
					text:'Effacer le dernier champ',
					handler:deleteLastField
				})*/
			]
		});


		if(Ext.get('center-panel-crossref')!=undefined) {
			Ext.getCmp('center-panel-crossref').remove('main-crossref');
		}
		Ext.getCmp('center-panel-crossref').add(panel);

		amalto.core.doLayout();
	}

	function addTranscoField(){
		row ++;
		var field = {
					layout:'column',
					border:false,
					items:[
						{
							columnWidth:.49,
							border:false,
							layout:'form',
							labelWidth:150,
							defaults: {labelSeparator:''},
							items:[
								{
									xtype:'textfield',
									id:'field'+row,
									fieldLabel:LABEL_FIELD[language]+' '+row
								}
							]
						},{
							columnWidth:.49,
							border:false,
							layout:'form',
							labelWidth:40,
							defaults: {labelSeparator:''},
							items:[
								{
			                		xtype:'checkbox',
			                		id:'key'+row,
			                		fieldLabel:KEY[language],
			                		tooltip: 'This field will be a key'
								}
							]
						}
					]
				};
		Ext.getCmp('main-crossref').add(field);
		Ext.getCmp('main-crossref').doLayout();
	}

	function deleteLastField(){
		Ext.getCmp('newCrossRef').remove(Ext.getCmp('field'+row));
		Ext.getCmp('newCrossRef').doLayout();
		row--;
	}

	function saveTransco(){
	    var fields = new Array();
	    var keys = new Array();
	    var transcoName = Ext.getCmp('transcoName').getValue();
	    //check concept
	    var re = new RegExp('[a-zA-Z][a-zA-Z0-9]*');
	    var m = re.exec(transcoName);
	    if (m == null) {
	        alert(MSG_INVALID_TABLE_NAME[language]);
	        return;
	    }
		var hasKey = false;
	    for (var i=1;i<row+1;i++){
	        value = DWRUtil.getValue("field"+i);
	        key = DWRUtil.getValue("key"+i);
	        m = re.exec(value);
	        if (m == null) {
	            alert(MSG_INVALID_FIELD_NAME[language]+i+': '+value);
	            return;
	        }
	        fields.push(value);
	        keys.push(key);
	        hasKey = hasKey || (key==true);
	    }
	    //check at least a key
		if(! hasKey){
			alert(MSG_AT_LEAST_ONE_KEY[language]);
		 	return;
		}
	    CrossReferencingInterface.putCrossReferencingTable(transcoName,fields,keys, function(){
	    	var tabPanel = amalto.core.getTabPanel();
//	    	Ext.getCmp('crossref-grid-container').remove('crossref-grid');
	    	Ext.getCmp('center-panel-crossref').remove('main-crossref');
			if(Ext.get('comboBoxTablesName')!=undefined){
	           	Ext.getCmp('comboBoxTablesName').reset();
    	       	Ext.getCmp('comboBoxTablesName').store.reload();
    	       	concept = transcoName;
		        CrossReferencingInterface.getTableDescription(transcoName,function(desc){
        			tableDescription = desc;
				    getCrossReferencingContent();
        		});
			}
	    });
	}


 	return {
		init: function() {
			displayCrossRefMainPanel();
		}
 	}
}();