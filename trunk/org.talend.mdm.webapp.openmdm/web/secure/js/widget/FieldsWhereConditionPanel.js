Ext.namespace('amalto.widget');
amalto.widget.FieldsWhereConditionPanel = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.widget.FieldsWhereConditionPanel.superclass.constructor.call(this);
};
Ext.extend(amalto.widget.FieldsWhereConditionPanel, Ext.Panel, {
	
	//i18n:'en',
	
	Button_Add_Filter : {
        'fr' : 'Ajouter un filtre',
        'en' : 'Add Filter'
    },
    
    Search_Field_Filters_Column_Field : {
        'fr' : 'Champ',
        'en' : 'Field'
    },
    
    Search_Field_Filters_Column_Operator : {
        'fr' : 'Opérateur',
        'en' : 'Operator'
    },
    
    Search_Field_Filters_Column_Value : {
        'fr' : 'Valeur',
        'en' : 'Value'
    },
    
    Search_Field_Filters_Column_Delete : {
        'fr' : 'Supprimer',
        'en' : 'Delete'
    },
    
    Filter_Operation_CONTAINS : {
        'fr' : 'contient les mot(s)',
        'en' : 'contains the word(s)'
    },
    
    Filter_Operation_EQUALS : {
        'fr' : 'est égal à',
        'en' : 'is equal to'
    },
    
    Filter_Operation_NOT_EQUALS : {
        'fr' : 'n\'est pas égal à',
        'en' : 'is not equal to'
    },
    
    Filter_Operation_GREATER_THAN : {
        'fr' : 'est supérieur à',
        'en' : 'is greater than'
    },
    
    Filter_Operation_GREATER_THAN_OR_EQUAL : {
        'fr' : 'est supérieur ou égal à',
        'en' : 'is greater or equals'
    },
    
    Filter_Operation_LOWER_THAN : {
        'fr' : 'est inférieur à',
        'en' : 'is lower than'
    },
    
    Filter_Operation_LOWER_THAN_OR_EQUAL : {
        'fr' : 'est inférieur ou égal à',
        'en' : 'is lower or equals'
    },
    
    Filter_Operation_STARTSWITH : {
        'fr' : 'contient un mot commençant par',
        'en' : 'contains a word starting with'
    },
    
    Filter_Operation_STRICTCONTAINS : {
        'fr' : 'contient la phrase',
        'en' : 'contains the sentence'
    },
	
	initUIComponents : function() {
		
		this.store1 = new Ext.data.Store({
            reader : new Ext.data.JsonReader({
                total : "total",
                root : "root"
                //id : "id"
            }, [{
                mapping : "Field",
                name : "Field",
                type : "string"
            }, {
                mapping : "Operator",
                name : "Operator",
                type : "string"
            }, {
                mapping : "Value",
                name : "Value",
                type : "string"
            }]),
            proxy : new Ext.data.HttpProxy({})
        });
        
        this.dspFieldStore = new Ext.data.Store({
         proxy: new Ext.data.DWRProxy(WidgetInterface.getFieldList , true),
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
        
        this.dspFieldStore.on('beforeload', 
                    function(button, event) {
                        this.onBeforeloadDspFieldStore();
                    }.createDelegate(this)
        );
        
        this.filterOperationStore = new Ext.data.Store({
          proxy: new Ext.data.MemoryProxy([
                ['CONTAINS',this.Filter_Operation_CONTAINS[this.i18n]],
                ['EQUALS',this.Filter_Operation_EQUALS[this.i18n]],
                ['NOT_EQUALS',this.Filter_Operation_NOT_EQUALS[this.i18n]],
                ['GREATER_THAN',this.Filter_Operation_GREATER_THAN[this.i18n]],
                ['GREATER_THAN_OR_EQUAL',this.Filter_Operation_GREATER_THAN_OR_EQUAL[this.i18n]],
                ['LOWER_THAN',this.Filter_Operation_LOWER_THAN[this.i18n]],
                ['LOWER_THAN_OR_EQUAL',this.Filter_Operation_LOWER_THAN_OR_EQUAL[this.i18n]],
                ['STARTSWITH',this.Filter_Operation_STARTSWITH[this.i18n]],
                ['STRICTCONTAINS',this.Filter_Operation_STRICTCONTAINS[this.i18n]]
          ]),
          reader: new Ext.data.ArrayReader({}, [
              {name: 'value',mapping: 0, type: 'string'},
              {name: 'text',mapping: 1}
          ])
       });

        this.editorGridPanel1 = new Ext.grid.EditorGridPanel({
            store : this.store1,
            width : 500,
            height : 100,
            title : "",
            loadMask:true,
            selModel : new Ext.grid.RowSelectionModel({}),
            border : true,
            clicksToEdit:1,
            tbar : new Ext.Toolbar([{
                handler : function(button, event) {
                    this.onAddFilterClick(button, event);
                }.createDelegate(this),
                text : this.Button_Add_Filter[this.i18n]
            }]),
            listeners:
                {
                    cellclick: function(g,rowIndex,columnIndex,e) {
                         this.onDeleteFilterOperation(g,rowIndex,columnIndex,e);
                    }.createDelegate(this)         
                },
            columns : [{
                hidden : false,
                header : this.Search_Field_Filters_Column_Field[this.i18n],
                dataIndex : "Field",
                sortable : true,
                editor:new Ext.form.ComboBox({
                   store: this.dspFieldStore,
                   valueField : "value",
                   displayField: "text",
                   editable : false
                })
            }, {
                hidden : false,
                header : this.Search_Field_Filters_Column_Operator[this.i18n],
                dataIndex : "Operator",
                sortable : true,
                editor:new Ext.form.ComboBox({
                   store: this.filterOperationStore,
                   valueField : "value",
                   displayField: "text",
                   editable : false
                }),
                width:165
            }, {
                hidden : false,
                header : this.Search_Field_Filters_Column_Value[this.i18n],
                dataIndex : "Value",
                sortable : true,
                editor:new Ext.form.TextField()
            }, {
                hidden : false,
                header : this.Search_Field_Filters_Column_Delete[this.i18n],
                dataIndex : "Delete",
                sortable : true, 
                renderer:this.deleteFiterRenderer,
                width:80
            }]
        });
        
		Ext.apply(this, this.editorGridPanel1);

	},
	
	onAddFilterClick : function(button, event){

		if(this.validateBeforeAddFilter()){
			
			var initFieldValue=this.getInitField();
            // access the Record constructor through the grid's store
            var Filter = this.store1.recordType;
            var f = new Filter({
                        Field: initFieldValue,
                        Operator: 'CONTAINS',
                        Value: '.*'
                    });
                    
           // this.editorGridPanel1.stopEditing();
            this.store1.insert(0, f);
            //this.editorGridPanel1.startEditing(0, 0);
            this.dspFieldStore.reload();
		}
    },
    
//    validateBeforeAddFilter : function(){
//        return true;
//    },
//    
//    getInitField : function(){
//    	return '';
//    }
    
    onDeleteFilterOperation: function(g,rowIndex,columnIndex,e){
        var record = g.getStore().getAt(rowIndex);
        if(columnIndex==3){ 
            this.store1.remove(record);
        }
    },
    
    deleteFiterRenderer : function(){
        return "<img src='img/genericUI/trash.gif' style='cursor:pointer' border=\"0\" />";
    },
    
    onBeforeloadDspFieldStore : function(){
            var dataObjectValue=this.getConceptForLoadingFieldStore();
            var input=dataObjectValue+"&"+language;
            Ext.apply(this.dspFieldStore.baseParams,{
              start: 0, 
              limit: 0,
              regex: input
            });
    },
    
//    getConceptForLoadingFieldStore : function(){
//    	return '';
//    }
    
    refreshWherePanelStore : function(){
    	    this.store1.removeAll();
            this.store1.commitChanges();
    },
    
    getWhereConditions : function(){
    	var foa=new Array();
        this.store1.commitChanges();
        if(this.store1.getCount()>0){
            for (var index = 0; index < this.store1.getCount(); index++) {
                var record = this.store1.getAt(index);
                foa[index]=new Array();
                foa[index]={
                           'fieldPath':record.data.Field,
                           'operator':record.data.Operator,
                           'value':record.data.Value
                           };
            }
        }
        return foa;
    },
    
    initWherePanelStore : function(dataFiltersArray){
       var Filter = this.store1.recordType;
       for (var index = 0; index < dataFiltersArray.length; index++) {
                     var getFilterItem=dataFiltersArray[index];
                     var f = new Filter({
                     Field: getFilterItem.fieldPath,
                     Operator: getFilterItem.operator,
                     Value: getFilterItem.value
                           });
                     this.store1.insert(index, f);            
       }
       this.store1.commitChanges();	
    }	
    	
});
