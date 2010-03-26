/*
 * Ext JS Library 2.0.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

/**
 * @class amalto.widget.ForeignKeyField
 * @extends Ext.form.TriggerField
 * Provides a ForeignKeyField input field with a foreignkey dropdown and automatic.
* @constructor
* Create a new ForeignKeyField
* @param {Object} config
 */
/**
 * The custom search field
 */
Ext.namespace('amalto.widget');
amalto.widget.ForeignKeyField = Ext.extend(Ext.form.TwinTriggerField, {
    initComponent : function(){
		amalto.widget.ForeignKeyField.superclass.initComponent.call(this);
    },
    
    xpathForeignKey : "",
    xpathForeignKeyInfo : "",
    trigger1Class : 'x-form-clear-trigger',
    trigger2Class : 'x-form-search-trigger',
    hideTrigger1:false,
    hasAdd : true,
    taskForeignKeyWindow : "",
    foreignKeyCombo : "",
    taskForeignKeytore : "",
    
    onTrigger1Click : function() {
        this.el.dom.value = '';
    },
	
    //pop up a dialog
    onTrigger2Click : function () {
	    var FILTER = {
			'fr':'Saisissez un critère de recherche puis sélectionnez une valeur dans la liste déroulante',
			'en':'Fill the box with a key-word then select a value'
		};
    	
    	var TITLE_WINDOW_FK = {
			'fr':'Choix de la clé étrangère',
			'en':'Choose a foreign key'
		};
    	var MESSAGE_MULTI_SHOW = {
			'fr':'clés possibles trouvées',
			'en':'possible keys are found'
		};
    	var MESSAGE_SINGLE_SHOW = {
			'fr':'clé possible trouvée',
			'en':'possible key is found'
		};
    	var  pos = this.el.getXY();
	    var dwrpasm = [this.xpathForeignKey, this.xpathForeignKeyInfo];
	    WidgetInterface.countForeignKey_filter(this.xpathForeignKey, function(count) {
	    	if(this.taskForeignKeyWindow) {
	    		this.taskForeignKeyWindow.hide();
	    		this.taskForeignKeyWindow.destroy();
	    	}
	    	
			this.taskForeignKeytore = new Ext.data.Store({
				proxy: new Ext.ux.data.ImprovedDWRProxy({
			        dwrFunction : WidgetInterface.getForeignKeyList,
			        dwrAdditional : dwrpasm
				}),
		        reader: new Ext.data.JsonReader({
		            root : 'rows',
		            totalProperty : 'count',
		            id : 'keys'
		        }, [
		            {name : 'keys', mapping : 'keys'},
		            {name : 'infos', mapping : 'infos'}
		        ])
			});
			
		    // Custom rendering Template
		    var resultTpl = new Ext.XTemplate(
			        '<tpl for="."><div class="search-item" style="font-size: 0.8em">',
			            '<h3>{infos}</h3>',
			            '{keys}',
			        '</div></tpl>'
			    );
		    
		    this.foreignKeyCombo = new Ext.form.ComboBox({
                width : 280,
                resizable : true, 
                fieldLabel : FILTER[language],
                id : 'task-foreign-key-filter',
		        store : this.taskForeignKeytore,
		        displayField : 'title',
		        typeAhead : false,
		        loadingText : 'Searching...',
		        pageSize : 100,
		        minChars : 2,
		        hideTrigger : true,
		        tpl : resultTpl,
		        listAlign : 'tl-bl',
		        itemSelector : 'div.search-item'
		    });
		    
		    this.foreignKeyCombo.on('select', function(combo, record, index) {
		    	this.taskForeignKeyWindow.hide();
		    	this.taskForeignKeyWindow.destroy();
		    	var value = record.get("keys");
		    	this.setValue(value);
		    }.createDelegate(this));
		    
		    
		    this.taskForeignKeyWindow = new Ext.Window({
                layout : 'fit',
                width : 300,
                height : 150,
                resizable : true, 
                closeAction : 'hide',
                plain : true,
                title : TITLE_WINDOW_FK[language] + '<br/>(' + count + ' ' + (count > 1 ? 
                			MESSAGE_MULTI_SHOW[language] + ')' : MESSAGE_SINGLE_SHOW[language] + ')'),
                items : [new Ext.form.FormPanel({
                	labelAlign : 'top',
                    items: [new Ext.Panel({html: '', border: false}),
                            this.foreignKeyCombo
	                ]
                })]
            });
			
			this.taskForeignKeyWindow.on('show', function() {
				var foreignKeyCombo = Ext.getCmp('task-foreign-key-filter');
				foreignKeyCombo.focus(true, 100);
				foreignKeyCombo.reset();
				
				if(count < 500) {
					foreignKeyCombo.setRawValue("");
					foreignKeyCombo.doQuery(".*", true);
					foreignKeyCombo.focus();
					foreignKeyCombo.expand();
				}
			});
			
			this.taskForeignKeyWindow.on('hide', function() {
		    	this.taskForeignKeyWindow.destroy();
			}.createDelegate(this));

			this.taskForeignKeyWindow.show();
			this.taskForeignKeyWindow.el.setXY([pos[0], pos[1]+23]);
		}.createDelegate(this)
		);
	},

	 onRender : function(ct, position){
		   Ext.form.TriggerField.superclass.onRender.call(this, ct, position);
		 this.wrap = this.wrap({cls: "x-form-field-wrap"});
		 this.trigger = this.wrap.createChild(this.triggerConfig ||
		 {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass});
		 if(this.hideTrigger){
		 this.trigger.setDisplayed(false);
		 }
		 this.initTrigger();
		 if(!this.width){
		 this.wrap.setWidth(this.el.getWidth()+this.trigger.getWidth());
		 }
	}, 

	 wrap: function(config, returnDom){
	     
	     config = {tag: "span",cls: "x-form-field-wrap"};
	     
	     var newEl = Ext.DomHelper.insertBefore(this.el.dom, config, !returnDom);
	     newEl.dom ? newEl.dom.appendChild(this.el.dom) : newEl.appendChild(this.el.dom);
	     return newEl;
	 },
	 
	setValue : function(value) {
		this.el.dom.value = value;
	}
});
Ext.reg('foreignfeyfield', amalto.widget.ForeignKeyField);