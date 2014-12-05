/**
 */
Ext.namespace('amalto.widget');
amalto.widget.ItemTreeDisplay = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.widget.ItemTreeDisplay.superclass.constructor.call(this);
};
Ext.extend(amalto.widget.ItemTreeDisplay, Ext.Panel, {
	
	initUIComponents : function() {
		
		this.itemTree = new Ext.tree.ColumnTree(
		     {
				animate : "false",
				loader : new Ext.tree.TreeLoader({
				    dataUrl:'',
				    uiProviders:{
                      'col': Ext.tree.ColumnNodeUI
                    }   
				}),
				xtype : "treepanel",
				root : new Ext.tree.AsyncTreeNode({
					expandable : true,
					text : "Data",
					draggable : false,
					id : "-1"
				}),
				autoScroll : true,
				layout:'fit',
        		tbar: [{
                        text:'Save Changes',
                        tooltip: 'Save Changes',
                        //iconCls:'save-icon',
                        listeners: {
                            'click': function() {
                                var json = this.itemTree.toJsonString(null,
                                    function(key, val) {
                                        return (key == 'leaf' || key =='elemText'|| key == 'elemValue');
                                    }, {
                                        elemText: 'text',
                                        elemValue: 'value'
                                    }
                                );
                                alert(this.cluster+"."+this.concept+"."+this.keys)
                                alert(json);
                            },
                            scope:this
                        }
                     },{
                       xtype:'tbseparator'
                     },{
                        text:'Add Group',
                        tooltip: 'Add Group',
                        //iconCls:'folder-icon',
                        listeners: {
                            'click' : function(){
                                var selectedItem = this.itemTree.getSelectionModel().getSelectedNode();
                                if (!selectedItem) {
                                    selectedItem = this.itemTree.getRootNode(); 
                                }
                            
                                handleCreate = function (btn, text, cBoxes){
                                	//FIXME:"ok" had better change to a constant
                                    if(btn == 'ok' && text) {
                                        var newNode = new Ext.tree.TreeNode({
                                                elemText: text,
                                                //elemValue: '',
                                                leaf: false,
                                                expandable: true,
                                                uiProvider: Ext.tree.ColumnNodeUI
                                        });
                                        if(selectedItem.isLeaf()) {
                                            selectedItem.parentNode.insertBefore(newNode, selectedItem.nextSibling);
                                        } else {
                                            selectedItem.insertBefore(newNode, selectedItem.firstChild);
                                        }
                                    }
                                }
                                Ext.MessageBox.show({
                                    title:'Add new group',
                                    msg: 'Name of the group:',
                                    buttons: Ext.MessageBox.OKCANCEL,
                                    prompt:true,
                                    fn: handleCreate
                                });
                            },
                            scope:this
                        }
                    },{
                        xtype:'tbseparator'
                    },{
                        text:'Add Element',
                        tooltip: 'Add Element',
                        //iconCls:'page-icon',
                        listeners: {
                            'click' : function(){
                                var selectedItem = this.itemTree.getSelectionModel().getSelectedNode();
                                if (!selectedItem) {
                                    Ext.Msg.alert('Warning', 'Please select an element after which you want to add a new one. ');
                                    return false;
                                }
                            
                                handleCreate = function (btn, text, cBoxes){
                                	//FIXME:"ok" had better change to a constant
                                    if(btn == 'ok' && text) {
                                        var newNode = new Ext.tree.TreeNode({
                                                elemText: text,
                                                elemValue: '',
                                                leaf: true,
                                                allowChildren:false,
                                                uiProvider: Ext.tree.ColumnNodeUI
                                        });
                                        if(selectedItem.isLeaf()) {
                                            selectedItem.parentNode.insertBefore(newNode, selectedItem.nextSibling);
                                        } else {
                                            selectedItem.insertBefore(newNode, selectedItem.firstChild);
                                        }
                                    }
                                }
                                
                                Ext.MessageBox.show({
                                    title:'Add new element',
                                    msg: 'Name of the element:',
                                    buttons: Ext.MessageBox.OKCANCEL,
                                    prompt:true,
                                    fn: handleCreate
                                });
                            },
                            scope:this
                        }
                        
                    },{
                        xtype:'tbseparator'
                    },{
                        text:'Delete Node',
                        tooltip: 'Delete Node',
                        //iconCls:'delete-icon',
                        listeners: {
                            'click' : function(){
                                var selectedItem = this.itemTree.getSelectionModel().getSelectedNode();
                               
                                if (!selectedItem) {
                                    Ext.Msg.alert('Warning', 'Please select a node to delete. ');
                                    return false;
                                }else if (selectedItem.id=="-1") {
                                    Ext.Msg.alert('Warning', 'The root node can not be deleted. ');
                                    return false;
                                }
                            
                                handleDelete = function (btn){
                                	//FIXME:"ok" had better change to a constant
                                    if(btn == 'ok') {
                                        selectedItem.remove();
                                    }
                                }
                                Ext.MessageBox.show({
                                    title:'Confirm your action',
                                    msg: 'Are you sure you want to delete this node and its children? ',
                                    buttons: Ext.MessageBox.OKCANCEL,
                                    fn: handleDelete
                                });
                            },
                            scope:this
                        }
                     }],
				  
				columns:[{
                    header:'Element',
                    width:350,
                    dataIndex:'elemText'
                },{
                    header:'Value',
                    width:100,
                    dataIndex:'elemValue'
                }],
                useArrows: true,
                enableDD: true
			}
		);
		
		this.treeEditor = new Ext.tree.ColumnTreeEditor(  
              this.itemTree,  
              {allowBlank: true}  
        );
        
        this.treeEditor.on("beforestartedit", function(treeEditor){  
        	  
              var tempNode = treeEditor.editNode;
              var colIndex = treeEditor.editColIndex;
              if(tempNode.isLeaf()||(!tempNode.isLeaf()&&colIndex=='elemText')){  
                 return true;  
              }else{  
                 return false;     
              }  
        });  
   
//        this.treeEditor.on("complete", function(treeEditor){  
//            alert(treeEditor.editNode.text);  
//        });

		Ext.apply(this, {
			title : this.cluster+"."+this.concept+"."+this.keys,
			layout : "fit",
			items : [this.itemTree],
			id : "ItemTreeDisplayPanel",
			closable : true
		});

	},
	
	initData : function() {
//		this.cluster=cluster;
//		this.concept=concept;
//		this.keys=keys;
		this.itemTree.loader.dataUrl="/talendmdm/secure/widget/ItemTreeServlet?cluster="+this.cluster+"&concept="+this.concept+"&keys="+this.keys;
		this.itemTree.getRootNode().setText(this.concept);
		this.itemTree.getRootNode().reload();
	    this.itemTree.expandAll();
	}
});



