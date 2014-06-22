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
var HIERARCHY = {
    'fr' : 'Hiérarchie->',
    'en' : 'Hierarchy->'
};
Ext.extend(amalto.hierarchical.HierarchicalViewDisplay, Ext.Panel, {
	initUIComponents : function() {
	   
	   this.dataObjectStore = new Ext.data.Store({
         proxy: new Ext.data.DWRProxy(GroupingHierarchyInterface.getDataObjectsList , true),
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
         proxy: new Ext.data.DWRProxy(GroupingHierarchyInterface.getPossiblePivotsList , true),
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
         proxy: new Ext.data.DWRProxy(GroupingHierarchyInterface.getTitleList , true),
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
        
        
		this.editorGridPanel1 = new amalto.widget.FieldsWhereConditionPanel(
			{
			 i18n:language,
		     validateBeforeAddFilter: function() {
		     	var titleFieldLabel=DWRUtil.getValue('titleFieldCmp');
                if(titleFieldLabel==''){
                        Ext.MessageBox.alert(amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Error"), amalto.hierarchical.HierarchicalViewLocal.get("Message_Missing_Title"));
                        return false;
                }
                
                return true;
		     },
		     getInitField: function() {
                var titleFieldValue=Ext.getCmp('titleFieldCmp').value;
                return titleFieldValue;
             },
             getConceptForLoadingFieldStore : function(){
                var dataObjectValue=Ext.getCmp('dataObjectCmp').value;
                return dataObjectValue;
             }
		    });
		
		this.hierarchicalTree = new Ext.ux.MultiSelectTreePanel(
		   {
		   	    //title : "Display Panel",
		   	    xtype : "treepanel",
				region: 'center',
	    		layout:'fit',
	    		maskDisabled:true,
				containerScroll : "true",
				autoScroll : true,
				animate : "true",
				loader : this.hierarchicalTreeLoader = new Ext.tree.TreeLoader({
					dataUrl : "/hierarchical/secure/HierarchicalTreeLoadServlet"
				}),
				root : this.hierarchicalTreeRoot=new Ext.tree.AsyncTreeNode({
					expandable : true,
					text : amalto.hierarchical.HierarchicalViewLocal.get("Tree_Root_Default_Name"),
					draggable : false,
					allowDrop : false,
					id : "0"
				}),
				enableDD: true,
				rootVisible : true,
				listeners: {
				            'dblclick': function(node, e){
				            	                            this.onTreeLeafNodeClick(node, e);
				                                          }.createDelegate(this),
				                                          
				            'nodedrop': function(dropEvent){

				            	              if(dropEvent.dropNode.length>0){
				            	                 
				            	                 var nodes=dropEvent.dropNode;
				            	                 var keysArray=new Array(nodes.length);
				            	                 var xpathArray=new Array(nodes.length);
				            	                 
				            	                 for (var index = 0; index < nodes.length; index++) {
				            	                 	var keys=nodes[index].id;
						            	            var xpath=nodes[index].attributes.xpath;
						            	            var newText='';
						            	            if(!dropEvent.target.leaf){
						            	            	newText=dropEvent.target.text;
						            	            }else{
						            	            	//leaf case
						            	            	newText=dropEvent.target.parentNode.text;
						            	            }
						            	                 
						            	            keysArray[index]=keys;
						            	            xpathArray[index]=xpath;
				            	                 }
				            	                 
						            	        GroupingHierarchyInterface.recordChanges(keysArray,xpathArray,newText,function(status){
													if(status==true){
													  //make dirty
				            	                      amalto.hierarchical.GroupingHierarchy.makeDirty();	
													}else{
													  Ext.MessageBox.alert(amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Error"), amalto.hierarchical.HierarchicalViewLocal.get("Message_Record_Changes_False"));
													}
												});
				            	              }
				            	              
				                        }
	                       },
	            bbar : new Ext.Toolbar([{
					handler : function(button, event) {
						this.onSaveChangesClick(button, event);
					}.createDelegate(this),
					text : amalto.hierarchical.HierarchicalViewLocal.get("Button_Save_Changes")
				},{
				    xtype : "tbspacer"
			    },{
					handler : function(button, event) {
						this.onCancelChangesClick(button, event);
					}.createDelegate(this),
					text : amalto.hierarchical.HierarchicalViewLocal.get("Button_Cancel_Changes")
				}]),
				tbar : new Ext.Toolbar([
				{
					text : amalto.hierarchical.HierarchicalViewLocal.get("Button_Sort_Order"),
				    //xtype : "tbsplit",
					//iconCls: 'bmenu',  // <-- icon
					handler : function(button, event) {
						this.onSortButtonClick(button, event);
					}.createDelegate(this),
				    menu: new Ext.menu.Menu({
						        items: [
						            '<b class="menu-title">'+amalto.hierarchical.HierarchicalViewLocal.get("Sort_Choose_Field")+'</b>',
						            this.sortOrderField1 = new Ext.menu.Item({
						                text: 'Field1',
						                menu: {
						                    items: [
						                        // stick any markup in a menu
						                        '<b class="menu-title">'+amalto.hierarchical.HierarchicalViewLocal.get("Sort_Choose_Order")+'</b>',
						                        this.sortOrderField1Ascending = new Ext.menu.CheckItem({
						                            text: amalto.hierarchical.HierarchicalViewLocal.get("Sort_Ascending"),
						                            checked: true,
						                            group: 'field1',
						                            checkHandler: function(item,checked) {
														this.onSortItemCheck(item,checked);
													}.createDelegate(this)
						                        }), 
						                        this.sortOrderField1Descending = new Ext.menu.CheckItem({
						                            text: amalto.hierarchical.HierarchicalViewLocal.get("Sort_Descending"),
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
						                        '<b class="menu-title">'+amalto.hierarchical.HierarchicalViewLocal.get("Sort_Choose_Order")+'</b>',
						                        this.sortOrderField2Ascending = new Ext.menu.CheckItem({
						                            text: amalto.hierarchical.HierarchicalViewLocal.get("Sort_Ascending"),
						                            checked: true,
						                            group: 'field2',
						                            checkHandler: function(item,checked) {
														this.onSortItemCheck(item,checked);
													}.createDelegate(this)
						                        }), 
						                        this.sortOrderField2Descending = new Ext.menu.CheckItem({
						                            text: amalto.hierarchical.HierarchicalViewLocal.get("Sort_Descending"),
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
						                        '<b class="menu-title">'+amalto.hierarchical.HierarchicalViewLocal.get("Sort_Choose_Order")+'</b>',
						                        this.sortOrderField3Ascending = new Ext.menu.CheckItem({
						                            text: amalto.hierarchical.HierarchicalViewLocal.get("Sort_Ascending"),
						                            checked: true,
						                            group: 'field3',
						                            checkHandler: function(item,checked) {
														this.onSortItemCheck(item,checked);
													}.createDelegate(this)
						                        }), 
						                        this.sortOrderField3Descending = new Ext.menu.CheckItem({
						                            text: amalto.hierarchical.HierarchicalViewLocal.get("Sort_Descending"),
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
				},{
				    xtype : "tbspacer"
			    },{
					text : amalto.hierarchical.HierarchicalViewLocal.get("Records_MAX_SIZE"),
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
				}])
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
			id : "hierarchicalViewDisplay",
			title : amalto.hierarchical.HierarchicalViewLocal.get("Hierarchical_View_Title"),
			layout : "border",
			closable: true,
			items : [{
				region: 'north',
				layout:'form',
				height : 255,
				split:true,
				collapsible : true,
				collapseFirst : false,
				autoScroll: true,
				title : amalto.hierarchical.HierarchicalViewLocal.get("Search_Panel_Title"),
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
					fieldLabel : amalto.hierarchical.HierarchicalViewLocal.get("Search_Field_Entity"),
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
			        resizable:true,
			        listeners : {
                               'select' : function(combo,record,index) {
                               	                      this.reloadRelatedComboStore();
                                                     }.createDelegate(this)
                                }
				}, {
					fieldLabel : amalto.hierarchical.HierarchicalViewLocal.get("Search_Field_Pivot"),
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
					resizable:true,
					listeners : {
                               'beforequery' : function(queryEvent) {
                               	                     var dataObjectLabel=DWRUtil.getValue('dataObjectCmp');
											    	 if(dataObjectLabel==''){
											    	    	Ext.MessageBox.alert(amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Error"), amalto.hierarchical.HierarchicalViewLocal.get("Message_Missing_Entity"));
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
					fieldLabel : amalto.hierarchical.HierarchicalViewLocal.get("Search_Field_Title"),
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
					resizable:true,
					listeners : {
                               'beforequery' : function(queryEvent) {
                               	                     var dataObjectLabel=DWRUtil.getValue('dataObjectCmp');
											    	 if(dataObjectLabel==''){
											    	    	Ext.MessageBox.alert(amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Error"), amalto.hierarchical.HierarchicalViewLocal.get("Message_Missing_Entity"));
											                return false;
											    	   }
                                                    }
                                }
				}, {
					//by yguo, fix bug 0014789: Cannot close 'Grouping Hierarchy' tab. 
					//fieldLabel : amalto.hierarchical.HierarchicalViewLocal.get("Search_Field_Filters"),
					//isFormField : true,
					layout : "column",
					border : false,
					items : [this.editorGridPanel1]
				}],
				border : false,
				xtype : "form",
				buttons : [{
					handler : function(button, event) {
						this.onSearchClick();
					}.createDelegate(this),
					text : amalto.hierarchical.HierarchicalViewLocal.get("Button_Search")
				},{
					handler : function(button, event) {
						this.onSaveReportClick();
					}.createDelegate(this),
					text : amalto.hierarchical.HierarchicalViewLocal.get("Button_Save_Report")
				},{
					handler : function(button, event) {
						this.onLoadReportClick();
					}.createDelegate(this),
					text : amalto.hierarchical.HierarchicalViewLocal.get("Button_Load_Report")
				}],
				buttonAlign : "left",
				bodyStyle:'padding:5px'
			},this.hierarchicalTree]
		});
	},
	
	onSortItemCheck: function (item,checked){
       
       if(item.checked){
         //alert(item.group+"/"+item.text);
       	
         var orderExprText=DWRUtil.getValue('orderExprText');
         var orderExprArray=this.orderExprText2Array(orderExprText);
         if(item.group=='field1'){
         	if(item.text==amalto.hierarchical.HierarchicalViewLocal.get("Sort_Ascending")){
         		orderExprArray[0]='ASC';
         	}else if(item.text==amalto.hierarchical.HierarchicalViewLocal.get("Sort_Descending")){
         		orderExprArray[0]='DESC';
         	}
         }else if(item.group=='field2'){
         	if(item.text==amalto.hierarchical.HierarchicalViewLocal.get("Sort_Ascending")){
         		orderExprArray[1]='ASC';
         	}else if(item.text==amalto.hierarchical.HierarchicalViewLocal.get("Sort_Descending")){
         		orderExprArray[1]='DESC';
         	}	
         }else if(item.group=='field3'){
         	if(item.text==amalto.hierarchical.HierarchicalViewLocal.get("Sort_Ascending")){
         		orderExprArray[2]='ASC';
         	}else if(item.text==amalto.hierarchical.HierarchicalViewLocal.get("Sort_Descending")){
         		orderExprArray[2]='DESC';
         	}
         }
         
         var newExprText=this.orderExprArray2Text(orderExprArray);
         DWRUtil.setValue('orderExprText',newExprText);
         
         if(newExprText!=orderExprText){
         	//if changed auto search
    	    this.onSearchClick();
         }
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
		
		GroupingHierarchyInterface.resetHierarchicalTreeCriterion(function(){
	    	   //nothing
        });
	},

	
	onTreeLeafNodeClick : function(node, e){
		if(node && node.isLeaf()){
							
//				var tabPanel = amalto.core.getTabPanel();
//				var itemTreeDisplayPanel = tabPanel.getItem('ItemTreeDisplayPanel');
//				
//				if(itemTreeDisplayPanel == undefined){
//					itemTreeDisplayPanel=new amalto.hierarchical.ItemTreeDisplay({'keys':node.id});			
//					tabPanel.add(itemTreeDisplayPanel);
//				}
//		        
//		        itemTreeDisplayPanel.show();
//				itemTreeDisplayPanel.doLayout();
//				amalto.core.doLayout();
//				
//				itemTreeDisplayPanel.initData(node.id);
				
				var dataObjectName='';
				DWREngine.setAsync(false); 
		        GroupingHierarchyInterface.getDataObjectNameFromHierarchicalTreeCriterion(function(_dataObject){dataObjectName=_dataObject;});
				DWREngine.setAsync(true);
				var idArray = new Array(1); 
				idArray[0]=node.id;
				amalto.itemsbrowser.ItemsBrowser.editItemDetails(HIERARCHY[language], idArray, dataObjectName, function(){
					this.doRefreshAfterEdit()
				}.createDelegate(this));
		}
	},
	
	doRefreshAfterEdit : function(){
	   this.reloadHierarchicalTree(true);
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
            
            this.editorGridPanel1.refreshWherePanelStore();
    },
    
    
    onEnterKeyClick : function(a, e){
    	if(e.getKey() == e.ENTER) {
	       this.onSearchClick();
	    } 
    },
    
    onSearchClick: function(){
    	//check
    	this.hierarchicalTree.getEl().mask();
    	var dataObjectLabel=DWRUtil.getValue('dataObjectCmp');
    	var pivotLabel=DWRUtil.getValue('pivotCmp');
    	var titleLabel=DWRUtil.getValue('titleFieldCmp');
    	var invalidFields="";
    	if(dataObjectLabel=='')invalidFields+="'"+amalto.hierarchical.HierarchicalViewLocal.get("Search_Field_Entity")+"' ";
    	if(pivotLabel=='')invalidFields+="'"+amalto.hierarchical.HierarchicalViewLocal.get("Search_Field_Pivot")+"' ";
    	if(titleLabel=='')invalidFields+="'"+amalto.hierarchical.HierarchicalViewLocal.get("Search_Field_Title")+"' ";
    	if(invalidFields!=""){
    		Ext.MessageBox.alert(amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Error"), amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Prompt_Begin")+invalidFields+amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Prompt_End"));
    		this.hierarchicalTree.getEl().unmask();
		    return false;
    	}
    	//parse
    	var dataObjectValue=Ext.getCmp('dataObjectCmp').value;
    	var pivotValue=Ext.getCmp('pivotCmp').value;
    	var titleValue=Ext.getCmp('titleFieldCmp').value;
    	
    	var foa=this.editorGridPanel1.getWhereConditions();
    	
    	var orderExprText=DWRUtil.getValue('orderExprText');
    	var maxSizeText=DWRUtil.getValue('maxSizeText');
    	
    	GroupingHierarchyInterface.updateHierarchicalTreeCriterionAndUpdateHistory(dataObjectValue,pivotValue,titleValue,foa,orderExprText,maxSizeText,function(status){
    		    //alert(status);
	    	    this.reloadHierarchicalTree(status);
        }.createDelegate(this));
        
    },
    
    onSaveReportClick: function(){
    	
    	    
            var passValidate=true;
			var dataObjectLabel=DWRUtil.getValue('dataObjectCmp');
			if(dataObjectLabel=='')passValidate=false;
			if(!passValidate){
				//Ext.MessageBox.alert('Sorry', "Please create a new report or load a alreay exist report first! ");
				alert(amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Report_Empty"));
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
		        alert(amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Report_Name_Empty"));
		        return;
		}
		var isSharedReport = DWRUtil.getValue('hierarchicalReportShared');
		//parse
    	var dataObjectValue=Ext.getCmp('dataObjectCmp').value;
    	var pivotValue=Ext.getCmp('pivotCmp').value;
    	var titleValue=Ext.getCmp('titleFieldCmp').value;
    	var foa=this.editorGridPanel1.getWhereConditions();
    	var orderExprText=DWRUtil.getValue('orderExprText');
    	var maxSizeText=DWRUtil.getValue('maxSizeText');
    	
    	GroupingHierarchyInterface.saveHierarchicalReport(reportName,isSharedReport,dataObjectValue,pivotValue,titleValue,foa,orderExprText,maxSizeText,function(state){
    	    this.onSavedHierarchicalReport(state,reportName,isSharedReport);
		}.createDelegate(this));
    },
    
    onSavedHierarchicalReport: function(state,reportName,isSharedReport){
    	if(state==true){
    	    	this.saveReportWindow.hide();
				this.saveReportWindow.destroy();
				Ext.MessageBox.alert(amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Info"), amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Report_Save_Success"));
    	    }else{
    	    	this.saveReportWindow.hide();
				this.saveReportWindow.destroy();
    	    	Ext.MessageBox.alert(amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Error"), amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Report_Save_Fail"));
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
					fieldLabel : amalto.hierarchical.HierarchicalViewLocal.get("Reports_Title"),
					xtype : "combo",
					store: new Ext.data.Store({
										proxy: new Ext.data.SimpleDWRProxy(GroupingHierarchyInterface.getReportsName),
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
		        title: amalto.hierarchical.HierarchicalViewLocal.get("Load_Report_Title"),
		        width: 320,
		        height:110,
		        layout: 'fit',
		        plain:true,
		        bodyStyle:'padding:5px;',
		        buttonAlign:'center',
		        items: loadReportPanel,
		        modal:true,
			    buttons: [{
		            text: amalto.hierarchical.HierarchicalViewLocal.get("Load_Report_Button"),
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
    		GroupingHierarchyInterface.getReport(reportName,function(data){
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
	    	       	  
                      this.editorGridPanel1.refreshWherePanelStore();
                      
	    	       	  var dataFiltersArray=data.filters.filterItems;
	    	       	  this.editorGridPanel1.initWherePanelStore(dataFiltersArray);
	    	       	  
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
    
    reloadHierarchicalTree: function(isPreconditionOK){
    	 if(isPreconditionOK==true){
    	 	    //update root
    	 	    var dataObjectLabel=DWRUtil.getValue('dataObjectCmp');
    	 	    if(dataObjectLabel!=''){
    	 	    	this.hierarchicalTreeRoot.setText(dataObjectLabel);
    	 	    }
    	 	    //refresh tree
    	 	    this.hierarchicalTree.getRootNode().reload();
	    	    this.hierarchicalTree.expandAll();
	    	    this.hierarchicalTree.getEl().unmask(); 
	     }
    },
    
    onEditClick: function(button, event){
    	
    	GroupingHierarchyInterface.checkHierarchicalTreeCriterion(function(data){
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
	    	    	Ext.MessageBox.alert(amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Error"), amalto.hierarchical.HierarchicalViewLocal.get("Message_No_Result_Set"));
	    	    }
        });
    },
    
    onSaveChangesClick: function(button, event){
    	Ext.MessageBox.show({
           msg: amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Progress_Saving_Content"),
           progressText: amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Progress_Saving_Title"),
           width:300,
           wait:true,
           waitConfig: {interval:200}
        });
        
    	GroupingHierarchyInterface.saveChanges({
	    	callback:function(data){
	    		   //clean dirty
		           amalto.hierarchical.GroupingHierarchy.cleanDirty();
		           
	    		   Ext.MessageBox.hide();
		    	   Ext.MessageBox.alert(amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Status"), data);
	        },
	        errorHandler:function(errorString, exception) {  
	              alert(amalto.hierarchical.HierarchicalViewLocal.get("Messagebox_Exception")+':'+ errorString);
	              Ext.MessageBox.hide();
	        }
        });
        
    },
    
    onCancelChangesClick: function(button, event){
        this.reloadHierarchicalTree(true);
        //clean dirty
		amalto.hierarchical.GroupingHierarchy.cleanDirty();
    }
});
