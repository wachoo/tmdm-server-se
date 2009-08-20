Ext.namespace('amalto.hierarchical');
amalto.hierarchical.HierarchicalViewEdit = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.hierarchical.HierarchicalViewEdit.superclass.constructor.call(this);
};
Ext.extend(amalto.hierarchical.HierarchicalViewEdit, Ext.Panel, {
	initUIComponents : function() {
		
		this.hierarchicalTree = new Ext.tree.TreePanel({
				animate : "false",
				loader : new Ext.tree.TreeLoader({dataUrl : "/hierarchical/secure/HierarchicalTreeLoadServlet"}),
				root : new Ext.tree.AsyncTreeNode({
					expandable : true,
					text : "Root",
					draggable : false,
					allowDrop : false,
					id : "0"
				}),
				xtype : "treepanel",
				tbar : new Ext.Toolbar([{
					handler : function(button, event) {
						this.onSaveChangesClick(button, event);
					}.createDelegate(this),
					text : "Save Changes"
				}]),
				containerScroll : "true",
				enableDD: true,
				//dropConfig: {appendOnly:true}
				listeners: {
				            'dblclick': function(node, e){
				            	              if(node && node.isLeaf()){
				            	                    //TODO
				                                    alert(node.id);
				            	                }
				                        },
				            'nodedrop': function(dropEvent){
				            	              
				            	              var keys=dropEvent.dropNode.id;
				            	              var xpath=dropEvent.dropNode.attributes.xpath;
				            	              var newText=dropEvent.target.text;
				            	              
				            	              HierarchicalViewInterface.recordChanges(keys,xpath,newText,function(status){
											    if(status==false)Ext.MessageBox.alert('Sorry', "This change has not affected the actual data item! ");
										      });
				                        }            
				                                          
	                       }
			});
		
		Ext.apply(this, {
			title : "Hierarchical View Edit",
			layout : "fit",
			items : [this.hierarchicalTree],
			id : "HierarchicalViewEdit",
			closable:true,
			autoScroll:true
		});
	},
	
	initData: function(){
    	 
	    HierarchicalViewInterface.resetHierarchicalTreeUpdateHistory(function(){
	    	   //nothing
        });
	    this.reloadHierarchicalTreeData();
    },
	
	reloadHierarchicalTreeData: function(){
    	 
	    this.hierarchicalTree.getRootNode().reload();
	    this.hierarchicalTree.expandAll();
	   
    },
    
    onSaveChangesClick: function(button, event){
    	Ext.MessageBox.show({
           msg: 'Saving your data, please wait...',
           progressText: 'Saving...',
           width:300,
           wait:true,
           waitConfig: {interval:200}
       });
    	HierarchicalViewInterface.saveChanges(function(data){
    		   Ext.MessageBox.hide();
	    	   Ext.MessageBox.alert('Status', data);
        });
    }
});
