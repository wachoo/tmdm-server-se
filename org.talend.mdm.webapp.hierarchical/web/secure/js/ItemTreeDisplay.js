/**
 * not in use
 */
Ext.namespace('amalto.hierarchical');
amalto.hierarchical.ItemTreeDisplay = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.hierarchical.ItemTreeDisplay.superclass.constructor.call(this);
};
Ext.extend(amalto.hierarchical.ItemTreeDisplay, Ext.Panel, {
	initUIComponents : function() {
		
		this.itemTree = new Ext.tree.TreePanel(
		     {
				animate : "false",
				loader : new Ext.tree.TreeLoader({dataUrl : "/hierarchical/secure/ItemTreeServlet?keys="+this.keys}),
				xtype : "treepanel",
				root : new Ext.tree.AsyncTreeNode({
					expandable : true,
					text : "Data",
					draggable : false,
					id : "0"
				}),
				autoScroll : true
			}
		);

		Ext.apply(this, {
			title : "Item Tree",
			layout : "fit",
			items : [this.itemTree],
			id : "ItemTreeDisplayPanel",
			closable : true
		});

	},
	
	initData : function(ikeys) {
		this.itemTree.loader.dataUrl="/hierarchical/secure/ItemTreeServlet?keys="+ikeys;
		
		this.itemTree.getRootNode().reload();
	    this.itemTree.expandAll();
	}
});

