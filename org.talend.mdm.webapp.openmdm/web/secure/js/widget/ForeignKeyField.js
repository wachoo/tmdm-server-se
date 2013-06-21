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
    
    isMandatory : false,
    xpathForeignKey : "",
    xpathForeignKeyInfo : "",
    trigger1Class : 'x-form-clear-trigger',
    trigger2Class : 'x-form-search-trigger',
    hideTrigger : false,
    hasAdd : true,
    taskForeignKeyWindow : "",
    foreignKeyCombo : "",
    taskForeignKeytore : "",
    showDeleteButton:true,
    fkFilter : "",
    parseFkFilter : null,
    retrieveFKinfos : false,
    enableKeyEvents : true,
    value : "",
    valueCpy : "",
    textCpy : "",
    onTrigger1Click : function() {
        this.el.dom.value = '';
    },
	
    //pop up a dialog
    onTrigger2Click : function () {
	    if (typeof this.parseFkFilter == "function"){
	    	this.parseFkFilter(this.fkFilter, function(newFkFilter){
	    		this.showFkDialog(newFkFilter);
	    	}.createDelegate(this));
	    } else {
	    	window.alert("must set parseFkFilter function for ForeignKeyField");
	    }
	},

	showFkDialog : function(newFkFilter){
		if (amalto.widget.ForeignKeyField.i18nBundleReady){
			this._showFkDialog(newFkFilter);
		} else {
			amalto.workflowtasks.bundle = new Ext.i18n.Bundle({bundle:'ForeignKeyFieldMessages', path:'/talendmdm/secure/resources', lang:language});
			amalto.workflowtasks.bundle.onReady(function(){
				this._showFkDialog(newFkFilter);
				amalto.widget.ForeignKeyField.i18nBundleReady = true;
			}.createDelegate(this));
		}
	},

	_showFkDialog : function(newFkFilter){
    	var  pos = this.el.getXY();
	    var dwrpasm = [this.xpathForeignKey, this.xpathForeignKeyInfo, newFkFilter];
	    var retrieve = "true" == this.retrieveFKinfos + "";
	    WidgetInterface.countForeignKey_filter(this.xpathForeignKey, this.xpathForeignKeyInfo, newFkFilter, function(count) {
	    	if(this.taskForeignKeyWindow) {
	    		this.taskForeignKeyWindow.hide();
	    		this.taskForeignKeyWindow.destroy();
	    	}
	    	
            this.improvedProxy = new Ext.ux.data.ImprovedDWRProxy({
                    dwrFunction: WidgetInterface.getForeignKeyList,
                    dwrAdditional: dwrpasm
                });
                
			this.taskForeignKeytore = new Ext.data.Store({
				proxy: this.improvedProxy,
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
                fieldLabel : amalto.workflowtasks.bundle.getMsg('FILTER'),
                id : 'task-foreign-key-filter',
		        store : this.taskForeignKeytore,
		        allowBlank : !this.isMandatory,
		        displayField : 'title',
		        typeAhead : false,
		        loadingText : 'Searching...',
		        pageSize : 20,
		        minChars : 0,
		        hideTrigger : true,
		        tpl : resultTpl,
		        listAlign : 'tl-bl',
		        itemSelector : 'div.search-item'
		    });
		    
		    this.foreignKeyCombo.on('select', function(combo, record, index) {
		    	this.taskForeignKeyWindow.hide();
		    	this.taskForeignKeyWindow.destroy();
		    	var value = record.get("keys");
		    	var text = "true" == retrieve + "" ? record.get("infos") : value; 
		    	this.setValue(value);
		    	this.setText(text);
		    }.createDelegate(this));
		    
		    
		    this.foreignKeyCombo.on('keydown', this.keySearch, this, true); 
		    
		    this.foreignKeyCombo.on('beforequery', function(){
		    	this.focus();
		    	this.expand();
            }); 
		    
            this.improvedProxy.on('load', function(dwrAdditional,response){
                var json = eval("("+response+")");
                var count = json['count'];
                count = parseInt(count)
                var window =  Ext.getCmp('task-foreign-key-window');
                if(window!= null){
                    var title = amalto.workflowtasks.bundle.getMsg('TITLE_WINDOW_FK')+'<br/>('+count+' '+(count>1?amalto.workflowtasks.bundle.getMsg('MESSAGE_MULTI_SHOW')+')':amalto.workflowtasks.bundle.getMsg('MESSAGE_SINGLE_SHOW')+')');
                    window.setTitle(title);
                }
              }
            )
		    this.taskForeignKeyWindow = new Ext.Window({
		    	id : 'task-foreign-key-window',
                layout : 'fit',
                width : 300,
                height : 150,
                resizable : true, 
                closeAction : 'hide',
                plain : true,
                title : amalto.workflowtasks.bundle.getMsg('TITLE_WINDOW_FK') + '<br/>(' + count + ' ' + (count > 1 ? 
                		amalto.workflowtasks.bundle.getMsg('MESSAGE_MULTI_SHOW') + ')' : amalto.workflowtasks.bundle.getMsg('MESSAGE_SINGLE_SHOW') + ')'),
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
				foreignKeyCombo.getEl().dom.style.top = 0;
				if(count < 500) {
					window.setTimeout(function(){
						foreignKeyCombo.setRawValue("");
						foreignKeyCombo.doQuery(".*", true);
					}, 1);
				}
			});
			
			this.taskForeignKeyWindow.on('hide', function() {
		    	this.taskForeignKeyWindow.destroy();
			}.createDelegate(this));

			this.taskForeignKeyWindow.setPosition([pos[0], pos[1]+23]);
			this.taskForeignKeyWindow.show();
		}.createDelegate(this)
		);
	},

	 onRender : function(ct, position){
		   Ext.form.TriggerField.superclass.onRender.call(this, ct, position);
		 this.wrap = this.wrap({cls: "x-form-field-wrap"});
		 this.trigger = this.wrap.createChild(this.triggerConfig ||
		 {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass});
		 var hide = this.hideTrigger || this.readOnly;
		 this.trigger.setDisplayed(!hide);
		 if(!hide){
			 this.initTrigger();
			 this.initTriggerAppearance();
		 }

		 if(!this.width){
		 this.wrap.setWidth(this.el.getWidth()+this.trigger.getWidth());
		 }
	}, 
	
    afterRender : function(){
		if (Ext.isIE && document.documentMode < 8){
			Ext.form.TwinTriggerField.superclass.afterRender.call(this);
		}
    },
	
    onKeyPress : function(e){
		this.value = "";
    },
    
	 wrap: function(config, returnDom){
	     
	     config = {tag: "span",cls: "x-form-field-wrap"};
	     
	     var newEl = Ext.DomHelper.insertBefore(this.el.dom, config, !returnDom);
	     newEl.dom ? newEl.dom.appendChild(this.el.dom) : newEl.appendChild(this.el.dom);
	     return newEl;
	 },
	 
	    initTriggerAppearance : function(){ 
    	 var ts = this.trigger.select('.x-form-trigger', true);
    	 ts.elements[0].setStyle("height", "21px");
    	 ts.elements[1].setStyle("height", "21px");
    	 ts.elements[0].dom.setAttribute("align","absmiddle");
    	 ts.elements[1].dom.setAttribute("align","absmiddle");
    	 if(this.showDeleteButton == false)
    	 {
        	ts.elements[0].setStyle("display","none");
    	 }
    },
    
    setForeignKey : function(fk, fkinfo, fkFilter){
    	this.xpathForeignKey = fk;
    	this.xpathForeignKeyInfo = fkinfo;
    	this.fkFilter = fkFilter;
    },
    
    setValue : function(value) {
		this.value = value;
		this.valueCpy = value;
	},
    
	setText : function(text) {
		this.el.dom.value = text;
		this.textCpy = text;
	},
	
	getValue : function() {
		return this.value;
	},
	
	getTextOrg : function(){
		return this.textCpy;
	},
	
	getValueOrg : function() {
		return this.valueCpy;
	}
});
Ext.reg('foreignfeyfield', amalto.widget.ForeignKeyField);