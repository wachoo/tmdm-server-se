/*
 * @include  "/com.amalto.webapp.core/web/secure/ext.ux/DWRProxy.js"
 * @include  "/com.amalto.webapp.core/web/secure/js/core.js"
 */
Ext.namespace('amalto.hierarchical');
amalto.hierarchical.HierarchicalViewDisplay = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.hierarchical.HierarchicalViewDisplay.superclass.constructor
			.call(this);
};
Ext.extend(amalto.hierarchical.HierarchicalViewDisplay, Ext.Panel, {
	initUIComponents : function() {
	   
	   this.dataObjectStore = new Ext.data.Store({
         proxy: new Ext.data.DWRProxy(HierarchicalViewInterface.getDataObjectsList , true),
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
        
        this.dataObjectStore.on('beforeload', 
		            function(button, event) {
						this.onBeforeloadDataObjectStore();
					}.createDelegate(this)
        );
        
        
        this.possiblePivotsStore = new Ext.data.Store({
         proxy: new Ext.data.DWRProxy(HierarchicalViewInterface.getPossiblePivotsList , true),
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
        
        this.possiblePivotsStore.on('beforeload', 
		            function(button, event) {
						this.onBeforeloadPossiblePivotsStore();
					}.createDelegate(this)
        );
        
        this.titleStore = new Ext.data.Store({
         proxy: new Ext.data.DWRProxy(HierarchicalViewInterface.getTitleList , true),
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
        
        this.titleStore.on('beforeload', 
		            function(button, event) {
						this.onBeforeloadTitleStore();
					}.createDelegate(this)
        );
        
        this.filterOperationStore = new Ext.data.Store({
          proxy: new Ext.data.MemoryProxy([
                ['CONTAINS','contains the word(s)'],
                ['EQUALS','is equal to'],
                ['NOT_EQUALS','is not equal to'],
                ['GREATER_THAN','is greater than'],
                ['GREATER_THAN_OR_EQUAL','is greater or equals'],
                ['LOWER_THAN','is lower than'],
                ['LOWER_THAN_OR_EQUAL','is lower or equals'],
                ['STARTSWITH','contains a word starting with'],
                ['STRICTCONTAINS','contains the sentence']
          ]),
          reader: new Ext.data.ArrayReader({}, [
              {name: 'value',mapping: 0, type: 'string'},
              {name: 'text',mapping: 1}
          ])
       });
        
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

		this.editorGridPanel1 = new Ext.grid.EditorGridPanel({
			store : this.store1,
			width : 500,
			height : 115,
			title : "",
			selModel : new Ext.grid.RowSelectionModel({}),
			border : true,
			clicksToEdit:1,
			tbar : new Ext.Toolbar([{
				handler : function(button, event) {
					this.onAddFilterClick(button, event);
				}.createDelegate(this),
				text : "Add Filter"
			}]),
			listeners:
   	   	        {
		   			cellclick: function(g,rowIndex,columnIndex,e) {
					     this.onDeleteFilterOperation(g,rowIndex,columnIndex,e);
				    }.createDelegate(this)		   
   	   	        },
			columns : [{
				hidden : false,
				header : "Field",
				dataIndex : "Field",
				sortable : true,
				editor:new Ext.form.ComboBox({
				   store: this.titleStore,
				   valueField : "value",
			       displayField: "text",
			       editable : false
				})
			}, {
				hidden : false,
				header : "Operator",
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
				header : "Value",
				dataIndex : "Value",
				sortable : true,
				editor:new Ext.form.TextField()
			}, {
			    hidden : false,
				header : "Delete",
				dataIndex : "Delete",
				sortable : true, 
				renderer:this.deleteFiterRenderer,
				width:80
			}]
		});
				
		this.hierarchicalTree = new Ext.tree.TreePanel(
		   {
				animate : "true",
				height : 300,
				title : "Display Panel",
				loader : this.hierarchicalTreeLoader = new Ext.tree.TreeLoader({
					dataUrl : "/hierarchical/secure/HierarchicalTreeLoadServlet"
				}),
				root : new Ext.tree.AsyncTreeNode({
					expandable : true,
					text : "Root",
					draggable : false,
					id : "0"
				}),
				xtype : "treepanel",
				containerScroll : "true",
				tbar : new Ext.Toolbar([
				{
					text : "Sort Order",
				    //xtype : "tbsplit",
					//iconCls: 'bmenu',  // <-- icon
					handler : function(button, event) {
						this.onSortButtonClick(button, event);
					}.createDelegate(this),
				    menu: new Ext.menu.Menu({
						        items: [
						            '<b class="menu-title">Choose Field</b>',
						            this.sortOrderField1 = new Ext.menu.Item({
						                text: 'Field1',
						                menu: {
						                    items: [
						                        // stick any markup in a menu
						                        '<b class="menu-title">Choose Order</b>',
						                        this.sortOrderField1Ascending = new Ext.menu.CheckItem({
						                            text: 'Ascending',
						                            checked: true,
						                            group: 'field1',
						                            checkHandler: function(item,checked) {
														this.onSortItemCheck(item,checked);
													}.createDelegate(this)
						                        }), 
						                        this.sortOrderField1Descending = new Ext.menu.CheckItem({
						                            text: 'Descending',
						                            checked: false,
						                            group: 'field1',
						                            checkHandler: function(item,checked) {
														this.onSortItemCheck(item,checked);
													}.createDelegate(this)
						                        })
						                    ]
						                }
						            }),
						            this.sortOrderField2 = new Ext.menu.Item({
						                text: 'Field2',
						                menu: {
						                    items: [
						                        // stick any markup in a menu
						                        '<b class="menu-title">Choose Order</b>',
						                        this.sortOrderField2Ascending = new Ext.menu.CheckItem({
						                            text: 'Ascending',
						                            checked: true,
						                            group: 'field2',
						                            checkHandler: function(item,checked) {
														this.onSortItemCheck(item,checked);
													}.createDelegate(this)
						                        }), 
						                        this.sortOrderField2Descending = new Ext.menu.CheckItem({
						                            text: 'Descending',
						                            checked: false,
						                            group: 'field2',
						                            checkHandler: function(item,checked) {
														this.onSortItemCheck(item,checked);
													}.createDelegate(this)
						                        })
						                    ]
						                }
						            }),
						            this.sortOrderField3 = new Ext.menu.Item({
						                text: 'Field3',
						                menu: {
						                    items: [
						                        // stick any markup in a menu
						                        '<b class="menu-title">Choose Order</b>',
						                        this.sortOrderField3Ascending = new Ext.menu.CheckItem({
						                            text: 'Ascending',
						                            checked: true,
						                            group: 'field3',
						                            checkHandler: function(item,checked) {
														this.onSortItemCheck(item,checked);
													}.createDelegate(this)
						                        }), 
						                        this.sortOrderField3Descending = new Ext.menu.CheckItem({
						                            text: 'Descending',
						                            checked: false,
						                            group: 'field3',
						                            checkHandler: function(item,checked) {
														this.onSortItemCheck(item,checked);
													}.createDelegate(this)
						                        })
						                    ]
						                }
						            })
						        ]
				     })  // assign menu by instance
				}, {
					name : "orderExprText",
					hidden : true,
					xtype : "textfield",
					value : "",
					listeners: {
		                	'specialkey': function(a, e) {
		                		this.onEnterKeyClick(a, e);
				             }.createDelegate(this)
		                }
				}, {
					text : "Max size:",
					xtype : "tbtext"
				}, {
					name : "maxSizeText",
					xtype : "textfield",
					value : "-1",
					listeners: {
		                	'specialkey': function(a, e) {
		                		this.onEnterKeyClick(a, e);
				             }.createDelegate(this)
		                }
				}, {
					xtype : "tbfill"
				}, {
					handler : function(button, event) {
						this.onEditClick(button, event);
					}.createDelegate(this),
					text : "Edit"
				}]),
				rootVisible : true,
				autoScroll : true,
				listeners: {
				            'dblclick': function(node, e){
				            	                            this.onTreeLeafNodeClick(node, e);
				                                          }.createDelegate(this)
	                       }
			}
		);
		
		this.hierarchicalTreeLoader.on('loadexception', function(loader, node, response)
		    {
		    	if(response.responseText!=''&&response.responseText.indexOf('[Exception]')==0){
		    		alert(response.responseText);
    		        return false;
		    	}
		    } 
		,this);

		Ext.apply(this, {
			title : "Hierarchical View Display",
			layout : "anchor",
			items : [{
				collapsible : true,
				frame : false,
				title : "Search Panel",
				layout : "form",
				items : [
				{
					xtype : "hidden",
					name : "reportNameRecordField",
					value : ""
				}, {
					xtype : "hidden",
					name : "isSharedRecordField",
					value : ""
				}, {
					fieldLabel : "Data Object",
					editable : false,
					xtype : "combo",
					id : "dataObjectCmp",
					name : "dataObjectCmp",
					store: this.dataObjectStore,
					valueField : "value",
			        displayField: "text",
			        typeAhead: true,
			        forceSelection: true,
			        triggerAction: 'all',
			        listeners : {
                               'select' : function(combo,record,index) {
                               	                      this.reloadRelatedComboStore();
                                                     }.createDelegate(this)
                                }
				}, {
					fieldLabel : "Pivot",
					editable : false,
					xtype : "combo",
					id : "pivotCmp",
					name : "pivotCmp",
					store: this.possiblePivotsStore,
					displayField:'text',
					valueField:'value',   
					typeAhead: true,
					triggerAction: 'all',
					forceSelection:true,
					listeners : {
                               'beforequery' : function(queryEvent) {
                               	                     var dataObjectLabel=DWRUtil.getValue('dataObjectCmp');
											    	 if(dataObjectLabel==''){
											    	    	Ext.MessageBox.alert('Sorry', "Please select a 'Data Object' First! ");
											                return false;
											    	   }
                                                    },
                                                    
                               'select' : function(combo,record,index) {
                               	
                               	                      var pivotText=DWRUtil.getValue('pivotCmp');
                               	                      if(pivotText!=''&&pivotText.indexOf("->")==-1)DWRUtil.setValue('orderExprText',"ASC-ASC");
                               	                      if(pivotText!=''&&pivotText.indexOf("->")!=-1)DWRUtil.setValue('orderExprText',"ASC-ASC-ASC");
                               	                      
                               	                      
                                                    }
                                }
				}, {
					fieldLabel : "Title Field",
					editable : false,
					xtype : "combo",
					name : "titleFieldCmp",
					id : "titleFieldCmp",
					store: this.titleStore,
					displayField:'text',
					valueField:'value',   
					typeAhead: true,
					triggerAction: 'all',
					forceSelection:true,
					listeners : {
                               'beforequery' : function(queryEvent) {
                               	                     var dataObjectLabel=DWRUtil.getValue('dataObjectCmp');
											    	 if(dataObjectLabel==''){
											    	    	Ext.MessageBox.alert('Sorry', "Please select a 'Data Object' First! ");
											                return false;
											    	   }
                                                    }
                                }
				}, {
					fieldLabel : "Filters",
					isFormField:true,
					layout : "column",
					items : [this.editorGridPanel1],
					border : false
				}],
				border : false,
				xtype : "form",
				buttons : [{
					handler : function(button, event) {
						this.onSearchClick();
					}.createDelegate(this),
					text : "Search"
				},{
					handler : function(button, event) {
						this.onSaveReportClick();
					}.createDelegate(this),
					text : "Save Report"
				},{
					handler : function(button, event) {
						this.onLoadReportClick();
					}.createDelegate(this),
					text : "Load Report"
				}],
				buttonAlign : "left",
				bodyStyle:'padding:5px'
			},this.hierarchicalTree],
			id : "hierarchicalViewDisplay",
			closable:true
		});
	},
	
	onSortItemCheck: function (item,checked){
       
       if(item.checked){
         //alert(item.group+"/"+item.text);
       	
         var orderExprText=DWRUtil.getValue('orderExprText');
         var orderExprArray=this.orderExprText2Array(orderExprText);
         if(item.group=='field1'){
         	if(item.text=='Ascending'){
         		orderExprArray[0]='ASC';
         	}else if(item.text=='Descending'){
         		orderExprArray[0]='DESC';
         	}
         }else if(item.group=='field2'){
         	if(item.text=='Ascending'){
         		orderExprArray[1]='ASC';
         	}else if(item.text=='Descending'){
         		orderExprArray[1]='DESC';
         	}	
         }else if(item.group=='field3'){
         	if(item.text=='Ascending'){
         		orderExprArray[2]='ASC';
         	}else if(item.text=='Descending'){
         		orderExprArray[2]='DESC';
         	}
         }
         
         var exprText=this.orderExprArray2Text(orderExprArray);
         DWRUtil.setValue('orderExprText',exprText);
         
       }
    },

	
	onSortButtonClick : function(button, event){

	   	var pivotLabel=DWRUtil.getValue('pivotCmp');
	   	var titleLabel=DWRUtil.getValue('titleFieldCmp');
	   	
	   	//check
//	   	var invalidFields="";
//    	if(pivotLabel=='')invalidFields+="'Pivot' ";
//    	if(titleLabel=='')invalidFields+="'Title Field' ";
//    	if(invalidFields!=""){
//    		Ext.MessageBox.alert('Sorry', "Please input "+invalidFields+"first! ");
//		    return false;
//    	}
	   	
    	//init label
    	if(pivotLabel==''||titleLabel==''){
    		this.sortOrderField1.setVisible(false);
    	    this.sortOrderField2.setVisible(false);
    	    this.sortOrderField3.setVisible(false);
    	}else{
    		//TODO filter some reserved words
    		if(pivotLabel.indexOf("->")!=-1){
    			
    		  var labels=pivotLabel.split("->");
    		  this.sortOrderField1.setVisible(true);
    		  this.sortOrderField2.setVisible(true);
    		  
    		  this.sortOrderField1.setText(labels[1]);
    	      this.sortOrderField2.setText(labels[0]);
    	      
    		}else{
    		  this.sortOrderField1.setVisible(false);	
    		  this.sortOrderField2.setVisible(true);
    		  this.sortOrderField2.setText(pivotLabel);
    		}
    	    this.sortOrderField3.setVisible(true);
	   	    this.sortOrderField3.setText(titleLabel);
    	}
    	
    	//init value
    	var orderExprText=DWRUtil.getValue('orderExprText');
    	this.setOrderFields(this.orderExprText2Array(orderExprText));
	},
	
	setOrderFields: function(array){
		for (var index = 0; index < array.length; index++) {
			if(array[index]!=undefined&&array[index]!=null&&array[index].length>0){
				if(array[index]=='ASC'){
					switch(index)
						{
						case 0:
						  this.sortOrderField1Ascending.setChecked(true);
						  break;
						case 1:
						  this.sortOrderField2Ascending.setChecked(true);
						  break;
						case 2:
						  this.sortOrderField3Ascending.setChecked(true);
						  break;  
						default:
						}
				}else if(array[index]=='DESC'){
					switch(index)
						{
						case 0:
						  this.sortOrderField1Descending.setChecked(true);
						  break;
						case 1:
						  this.sortOrderField2Descending.setChecked(true);
						  break;
						case 2:
						  this.sortOrderField3Descending.setChecked(true);
						  break;  
						default:
						}
				}
			}
		}
	},
	
	orderExprText2Array: function(orderExprText){
		
		var orderExprArray=new Array(3);
		orderExprText=orderExprText.trim();
		if(orderExprText!=''){
			var tmpArray=orderExprText.split('-');
			if(tmpArray!=null){
				if(tmpArray.length==2){
				orderExprArray[0]='';
				orderExprArray[1]=tmpArray[0];
				orderExprArray[2]=tmpArray[1];
				}else if(tmpArray.length==3){
				orderExprArray[0]=tmpArray[0];
				orderExprArray[1]=tmpArray[1];
				orderExprArray[2]=tmpArray[2];
				}
			}
		}
		return orderExprArray;
	},
	
	orderExprArray2Text: function(array){
		var expr='';
		if(array!=null){
		  	for (var index = 0; index < array.length; index++) {
		  		if(array[index]!=undefined&&array[index]!=null&&array[index].length>0){
		  			expr+=array[index];
		  			if(index<array.length-1)expr+='-';
		  		}
		  	}
		}
		return expr;
	},
	
	initData : function(){
		
		HierarchicalViewInterface.resetHierarchicalTreeCriterion(function(){
	    	   //nothing
        });
	},
	
	deleteFiterRenderer : function(){
		return "<img src='img/genericUI/trash.gif' style='cursor:pointer' border=\"0\" />";
	},
	
	onTreeLeafNodeClick : function(node, e){
		if(node && node.isLeaf()){
							
				var tabPanel = amalto.core.getTabPanel();
				var itemTreeDisplayPanel = tabPanel.getItem('ItemTreeDisplayPanel');
				
				if(itemTreeDisplayPanel == undefined){
					itemTreeDisplayPanel=new amalto.hierarchical.ItemTreeDisplay({'keys':node.id});			
					tabPanel.add(itemTreeDisplayPanel);
				}
		        
		        itemTreeDisplayPanel.show();
				itemTreeDisplayPanel.doLayout();
				amalto.core.doLayout();
				
				itemTreeDisplayPanel.initData(node.id);
		}
	},
	
	onBeforeloadDataObjectStore : function(){
            Ext.apply(this.dataObjectStore.baseParams,{
              start: 0,	
              limit: 0,
              regex: language
            });
    },
    
    onBeforeloadPossiblePivotsStore : function(){
    	    var dataObjectValue=Ext.getCmp('dataObjectCmp').value;
    	    var input=dataObjectValue+"&"+language;
            Ext.apply(this.possiblePivotsStore.baseParams,{
              start: 0,	
              limit: 0,
              regex: input
            });
    },
    
    onBeforeloadTitleStore : function(){
    	    var dataObjectValue=Ext.getCmp('dataObjectCmp').value;
    	    var input=dataObjectValue+"&"+language;
            Ext.apply(this.titleStore.baseParams,{
              start: 0,	
              limit: 0,
              regex: input
            });
    },
    
    reloadRelatedComboStore : function(){
    	    DWRUtil.setValue('pivotCmp','');
    	    DWRUtil.setValue('titleFieldCmp','');
            this.possiblePivotsStore.reload();
            this.titleStore.reload();
            
            this.store1.removeAll();
            this.store1.commitChanges();
    },
    
    onAddFilterClick : function(button, event){

    	var titleFieldLabel=DWRUtil.getValue('titleFieldCmp');
    	if(titleFieldLabel==''){
				Ext.MessageBox.alert('Sorry', "Please select a 'Title Field' First! ");
				return false;
		}
		var titleFieldValue=Ext.getCmp('titleFieldCmp').value;
    	// access the Record constructor through the grid's store
        var Filter = this.store1.recordType;
        var f = new Filter({
                    Field: titleFieldValue,
                    Operator: 'CONTAINS',
                    Value: '.*'
                });
                
        this.editorGridPanel1.stopEditing();
        this.store1.insert(0, f);
        this.editorGridPanel1.startEditing(0, 0);

    },
    
    onDeleteFilterOperation: function(g,rowIndex,columnIndex,e){
    	var record = g.getStore().getAt(rowIndex);
		if(columnIndex==3){	
		   	this.store1.remove(record);
		}
    },
    
    onEnterKeyClick : function(a, e){
    	if(e.getKey() == e.ENTER) {
	       this.onSearchClick();
	    } 
    },
    
    onSearchClick: function(){
    	//check
    	var dataObjectLabel=DWRUtil.getValue('dataObjectCmp');
    	var pivotLabel=DWRUtil.getValue('pivotCmp');
    	var titleLabel=DWRUtil.getValue('titleFieldCmp');
    	var invalidFields="";
    	if(dataObjectLabel=='')invalidFields+="'Data Object' ";
    	if(pivotLabel=='')invalidFields+="'Pivot' ";
    	if(titleLabel=='')invalidFields+="'Title Field' ";
    	if(invalidFields!=""){
    		Ext.MessageBox.alert('Sorry', "Please input "+invalidFields+"first! ");
		    return false;
    	}
    	//parse
    	var dataObjectValue=Ext.getCmp('dataObjectCmp').value;
    	var pivotValue=Ext.getCmp('pivotCmp').value;
    	var titleValue=Ext.getCmp('titleFieldCmp').value;
    	this.store1.commitChanges();
    	if(this.store1.getCount()>0){
    		var foa=new Array();
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
    	var orderExprText=DWRUtil.getValue('orderExprText');
    	var maxSizeText=DWRUtil.getValue('maxSizeText');
    	
    	HierarchicalViewInterface.updateHierarchicalTreeCriterion(dataObjectValue,pivotValue,titleValue,foa,orderExprText,maxSizeText,function(data){
	    	    this.reloadHierarchicalTree(data);
        }.createDelegate(this));
	     
    },
    
    onSaveReportClick: function(){
    	
    	    
            var passValidate=true;
			var dataObjectLabel=DWRUtil.getValue('dataObjectCmp');
			if(dataObjectLabel=='')passValidate=false;
			if(!passValidate){
				//Ext.MessageBox.alert('Sorry', "Please create a new report or load a alreay exist report first! ");
				alert("Please create a new report or load a alreay exist report first! ");
				return;
			}
			
    		if(this.saveReportWindow){
				 this.saveReportWindow.hide();
				 this.saveReportWindow.destroy();
			}
			

		    var saveReportPanel = new Ext.form.FormPanel({
		         baseCls: 'x-plain',
			     labelAlign: 'left',     
			     //labelWidth: 60,           
		         //layout:'fit', 
			     xtype : "form",
			     items : [{
			     	name:"hierarchicalReportShared",
					fieldLabel : "Shared",
					xtype : "checkbox",
					checked : DWRUtil.getValue('isSharedRecordField')
				 },{
				 	name:"hierarchicalReportName",
					fieldLabel : "Report Name",
					xtype : "textfield",
					allowBlank : false,
					value : DWRUtil.getValue('reportNameRecordField')
				 }]
		    });
		    
		    this.saveReportWindow = new Ext.Window({
		        title: "Save Report",
		        width: 320,
		        height:130,
		        layout: 'fit',
		        plain:true,
		        bodyStyle:'padding:5px;',
		        buttonAlign:'center',
		        items: saveReportPanel,
		        modal:true,
			    buttons: [{
		            text: "Save",
		            handler: function(){
							       this.onSaveReportWindowExecuteClick();
						        }.createDelegate(this)
		        }]
		    });
		
		    this.saveReportWindow.show();

    },
    
    onSaveReportWindowExecuteClick: function(){
    	
    	var reportName = DWRUtil.getValue('hierarchicalReportName');
		if(reportName==''){
		        alert("Report Name can't be empty! ");
		        return;
		}
		var isSharedReport = DWRUtil.getValue('hierarchicalReportShared');
		//parse
    	var dataObjectValue=Ext.getCmp('dataObjectCmp').value;
    	var pivotValue=Ext.getCmp('pivotCmp').value;
    	var titleValue=Ext.getCmp('titleFieldCmp').value;
    	this.store1.commitChanges();
    	if(this.store1.getCount()>0){
    		var foa=new Array();
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
    	var orderExprText=DWRUtil.getValue('orderExprText');
    	var maxSizeText=DWRUtil.getValue('maxSizeText');
    	
    	HierarchicalViewInterface.saveHierarchicalReport(reportName,isSharedReport,dataObjectValue,pivotValue,titleValue,foa,orderExprText,maxSizeText,function(state){
    	    this.onSavedHierarchicalReport(state,reportName,isSharedReport);
		}.createDelegate(this));
    },
    
    onSavedHierarchicalReport: function(state,reportName,isSharedReport){
    	if(state==true){
    	    	this.saveReportWindow.hide();
				this.saveReportWindow.destroy();
				Ext.MessageBox.alert('Info', "The hierarchical-report has been saved successfully! ");
    	    }else{
    	    	this.saveReportWindow.hide();
				this.saveReportWindow.destroy();
    	    	Ext.MessageBox.alert('Sorry', "Saved a hierarchical-report failed! ");
    	    }
    },
    
    recordReportProperties: function(reportName,isSharedReport){
    	DWRUtil.setValue('reportNameRecordField',reportName);
		if(isSharedReport==true||isSharedReport=='true'){
			DWRUtil.setValue('isSharedRecordField','true');
		}else{
			DWRUtil.setValue('isSharedRecordField','');
		}
    },

    onLoadReportClick: function(){
    	
    	    if(this.loadReportWindow){
				 this.loadReportWindow.hide();
				 this.loadReportWindow.destroy();
			}
			
		    var loadReportPanel = new Ext.form.FormPanel({
		         baseCls: 'x-plain',
			     labelAlign: 'left',     
			     //labelWidth: 60,           
		         //layout:'fit', 
			     xtype : "form",
			     items : [{
			     	name : "loadedHierarchicalReportNames",
					fieldLabel : "Reports",
					xtype : "combo",
					store: new Ext.data.Store({
										proxy: new Ext.data.SimpleDWRProxy(HierarchicalViewInterface.getReportsName),
							        	reader: new Ext.data.MapReader()
									}),
					displayField: 'value',
					valueField: 'key',
					mode:'remote',
					triggerAction:'all',
					editable:false
//					listeners:{
//					          		'select' : function( combo, record, index ){
//								                	var key = record.data.key;
//								                	getReporting(key);
//					          		           }
//					          } 		           
				 }]
		    });
		    
		    this.loadReportWindow = new Ext.Window({
		        title: "Load Report",
		        width: 320,
		        height:110,
		        layout: 'fit',
		        plain:true,
		        bodyStyle:'padding:5px;',
		        buttonAlign:'center',
		        items: loadReportPanel,
		        modal:true,
			    buttons: [{
		            text: "Load",
		            handler: function(){
							       this.onLoadReportWindowExecuteClick();
						        }.createDelegate(this)
		        }]
		    });
		
		    this.loadReportWindow.show();
    },
    
    onLoadReportWindowExecuteClick: function(){
    	
    	var reportName = DWRUtil.getValue('loadedHierarchicalReportNames');
    	if(reportName==''){
    		this.recordReportProperties('','');
    		this.loadReportWindow.hide();
			this.loadReportWindow.destroy();
    	}else{
    		HierarchicalViewInterface.getReport(reportName,function(data){
                 this.onHierarchicalGetReport(data);
		   }.createDelegate(this));
    	}	
    	
    },
    
    onHierarchicalGetReport: function(data){
    	if(data!=null){
	    	   	   this.recordReportProperties(data.reportName,data.shared);   
	    	   	   
	    	   	   Ext.getCmp('dataObjectCmp').setValue(data.dataObjectName);
	    	       Ext.getCmp('pivotCmp').setValue(data.pivotPath);
	    	       Ext.getCmp('titleFieldCmp').setValue(data.titleFieldPath);
	    	       //set filters
	    	       if(data.filters!=null&&data.filters.filterItems!=null&&data.filters.filterItems.length>0){
	    	       	  
	    	       	  this.store1.removeAll();
                      this.store1.commitChanges();
                      
	    	       	  var Filter = this.store1.recordType;
	    	       	  var dataFiltersArray=data.filters.filterItems;
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
	    	       //set addition info
	    	       var newOrderExpr='';
	    	       if(data.pivotDirectionsExpr!=null&&data.indexDirectionsExpr!=null&&data.indexDirectionsExpr.length>0){
	    	       newOrderExpr=data.pivotDirectionsExpr+"-"+data.indexDirectionsExpr;
	    	       DWRUtil.setValue('orderExprText',newOrderExpr);
	    	       }
	    	       DWRUtil.setValue('maxSizeText',data.limit);
	    	       	
	    	       this.loadReportWindow.hide();
			       this.loadReportWindow.destroy();
	    }
    },
    
    reloadHierarchicalTree: function(data){
    	 if(data==true){
	    	    this.hierarchicalTree.getRootNode().reload();
	    	    this.hierarchicalTree.expandAll();
	     }
    },
    
    onEditClick: function(button, event){
    	
    	HierarchicalViewInterface.checkHierarchicalTreeCriterion(function(data){
	    	    if(data==true){
	    	    	var tabPanel = amalto.core.getTabPanel();
					var hierarchicalViewEditPanel = tabPanel.getItem('HierarchicalViewEdit');
					
					if(hierarchicalViewEditPanel == undefined){
						hierarchicalViewEditPanel=new amalto.hierarchical.HierarchicalViewEdit();			
						tabPanel.add(hierarchicalViewEditPanel);
					}
			        
			        hierarchicalViewEditPanel.show();
					hierarchicalViewEditPanel.doLayout();
					amalto.core.doLayout();
					
					hierarchicalViewEditPanel.initData();	
	    	    }else{
	    	    	Ext.MessageBox.alert('Sorry', "Please get a 'Result Set' First! ");
	    	    }
        });
    }
});
