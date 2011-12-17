// Similar to a table layout but allow customization of CSS class (which the TableLayout doesn't do).
amalto.ClassTableLayout = Ext.extend(Ext.layout.TableLayout, {
    // overridden
    onLayout : function(ct, target){
        var cs = ct.items.items, len = cs.length, c, i;

        if(!this.table){
            target.addClass('x-table-layout-ct');

            this.table = target.createChild(
                {tag:'table', cls:this.cls, cellspacing: 0, cn: {tag: 'tbody'}}, null, true);

            this.renderAll(ct, target);
        }
    }
});

Ext.Container.LAYOUTS['classtable'] = amalto.ClassTableLayout;