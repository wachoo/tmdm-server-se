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
		
		this.itemTree = new Ext.tree.TreePanel(
		     {
				animate : "false",
				loader : new Ext.tree.TreeLoader({dataUrl:''}),
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
	
	initData : function(cluster,concept,keys) {
		this.itemTree.loader.dataUrl="/talendmdm/secure/widget/ItemTreeServlet?cluster="+cluster+"&concept="+concept+"&keys="+keys;
		this.itemTree.getRootNode().reload();
	    this.itemTree.expandAll();
	}
});

