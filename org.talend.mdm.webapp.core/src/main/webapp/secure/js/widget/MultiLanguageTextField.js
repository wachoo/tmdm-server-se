Ext.namespace('amalto.widget');
amalto.widget.MultiLanguageTextField = Ext.extend(Ext.Panel, {
	dataTextField : "",
	dataHiddenField : "",
	langArr : new Array(),
	languageIndex : -1,
		
	constructor: function(config) {
		Ext.applyIf(this, config);
		
		this.dataTextField = new Ext.form.TextField({
				id : this.name + "_" + this.activityUUID + "_field",
				name : this.name + "_" + this.activityUUID + "_field",
				allowBlank : !this.isMandatory,
				xtype : "textfield",
				fieldLabel : this.label,
				width : 300,
				readOnly : this.isReadonly || this.readonly,
				dataTypeClassName : this.dataTypeClassName,
				isActivityVariable : this.activityVariable,
				initialValue : this.initialValue,
				style : this.isReadonly || this.readonly ? "background-color: #F4F4F4; background-image:none;" : ""
			});
		
		this.dataHiddenField = new Ext.form.Hidden({
		   		id : this.name + "_" + this.activityUUID + "_hidden",
  		   		name : this.name + "_" + this.activityUUID + "_hidden",
  		   		xtype : "hidden",
  		   		hidden : true,
  		   		hiddenLabel : true,
  		   		dataTypeClassName : this.dataTypeClassName,
  		   		isActivityVariable : this.activityVariable,
  		   		initialValue : this.initialValue
			});
		
		Ext.apply(this, {
			id : this.name + "_" + this.activityUUID,
			type : "amalto.widget.MultiLanguageTextField",
			layout : "column",
			collapsible:false,
			width : 540,
			border:false,
			items : [{
					columnWidth : ".9",
					layout : "form",
					labelAlign : "left",
					labelWidth : 180,
					border:false,
					items : [this.dataTextField]
					},
					{
					columnWidth : ".1",
					layout : "fit",
					border:false,
					items : [this.dataHiddenField]
					}]
			});
	},
		
	setValue : function(value) {
		if(this.dataTypeLocalClassName == "MULTI_LINGUAL") {
			var val = this.getValueByLanguage(value);
			this.dataTextField.setValue(val);
			this.dataHiddenField.setValue(val);
		} else {
			this.dataTextField.setValue(value);
		}
	},
	
	getValue : function() {
		if(this.dataTypeLocalClassName == "MULTI_LINGUAL") {
			if(this.languageIndex == -1) {
				if(this.dataTextField.getValue() != "") {
					this.languageIndex = this.langArr.length;
					this.langArr[this.languageIndex] = language.toUpperCase() + ":" + this.dataTextField.getValue();
				}
			} else {
				this.langArr[this.languageIndex] = language.toUpperCase() + ":" + this.dataTextField.getValue();
			}
			
			var str = "";
			for(i = 0; i<this.langArr.length; i++) {
				str += "[";
				str += this.langArr[i];
				str += "]";
			}
			this.dataHiddenField.setValue(str);
			return str;
		} else {
			return this.dataTextField.getValue();
		}	
	},
	
	getValueByLanguage : function(value) {
		var languagePrefix = language.toUpperCase() + ":";
		if(value.charAt(0) == "[" && value.charAt(value.length - 1) == "]") {
			value = value.substring(1, value.length - 1);
			this.langArr = value.split("][");
			if(this.langArr != undefined) {
				if(this.langArr.length > 0) {
					for(i = 0; i<this.langArr.length; i++) {
						if(this.langArr[i].substring(0, 3) == languagePrefix){
							this.languageIndex = i;
							return this.langArr[i].substring(3, this.langArr[i].length);
						}
					}
				}
			}
		} else {
			return value;
		}
		return "";
	}
});