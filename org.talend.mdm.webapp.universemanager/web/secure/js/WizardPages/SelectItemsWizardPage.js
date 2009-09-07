Ext.namespace('amalto.universemanager');
amalto.universemanager.SelectItemsWizardPage = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.universemanager.SelectItemsWizardPage.superclass.constructor.call(this);
};
Ext.extend(amalto.universemanager.SelectItemsWizardPage, Ext.Panel, {
	initUIComponents : function() {

		Ext.apply(this, {
			layout : "fit",
			items : [{
				autoScroll : true,
				title : "Select the items to copy",
				layout : "form",
				height : 500,
				items : [
			    this.itemBoxsTree = new Ext.tree.TreePanel({
					animate : "true",
					loader : new Ext.tree.TreeLoader({
					    dataUrl : "",
					    baseAttrs: { uiProvider: Ext.ux.TreeCheckNodeUI }
				    }),
					border : false,
					xtype : "treepanel",
					root : new Ext.tree.AsyncTreeNode({
						expandable : true,
						text : "Clusters",
						draggable : false,
						id : "0"
					}),
					rootVisible: true,
					containerScroll : "true",
					setModel:new Ext.grid.RowSelectionModel({}),
					checkModel:"childCascade"
					//listeners:{'render':function(tree){tree.getRootNode().expand(); }}
				})],
				xtype : "fieldset"
			}],
			border : false
		});
		
		this.itemBoxsTree.on("check",function(node,checked){
			
			if(node && node.isLeaf()){
			   //alert(node.id+" = "+checked);
			   UniverseManagerInterface.updateItemBoxTreeNode(node.id,checked,function(){});
			}
			
		});
	},
	
	refreshItemBoxsTree : function(){
		this.itemBoxsTree.loader.dataUrl="/universemanager/secure/ItemsCatalogLoaderServelt";
		this.itemBoxsTree.getRootNode().reload();
	    this.itemBoxsTree.expandAll();
	},
	
	refreshData : function() {
		//nothing	   	    
	},
	
	commitData : function(){
	    //nothing
	},
	
	resetData : function() {
		//TODO	client reset maybe not necessary	
	}
});
