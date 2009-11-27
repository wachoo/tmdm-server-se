/**
* Implements a special node fot the item browser 
 * @namespace YAHOO.widget
 * @class ItemNode
 * @extends YAHOO.widget.HTMLNode
 * @constructor
 * @param oData {object} a string or object containing the data that will
 * be used to render this node
 * @param oParent {YAHOO.widget.Node} this node's parent node
 * @param expanded {boolean} the initial expanded/collapsed state
 * @param hasIcon {boolean} specifies whether or not leaf nodes should
 * have an icon
 */
 
//amalto.namespace("amalto.itemsbrowser");

amalto.itemsbrowser.ItemNode = function(itemData, newItem, treeIndex, oParent, expanded, hasIcon) {
    //if (oData) 
    {
        this.init(null, oParent, expanded);
        this.initContent(itemData, newItem,treeIndex, hasIcon);
        this.itemData = itemData;
        this.newItem = newItem;
        this.treeIndex = treeIndex;
        this.hasIcon = hasIcon;
    }
};

YAHOO.extend(amalto.itemsbrowser.ItemNode, YAHOO.widget.Node, {

    /**
     * The CSS class for the html content container.  Defaults to ygtvhtml, but
     * can be overridden to provide a custom presentation for a specific node.
     * @property contentStyle
     * @type string
     */
    contentStyle: "ygtvhtml",

    /**
     * The generated id that will contain the data passed in by the implementer.
     * @property contentElId
     * @type string
     */
    contentElId: null,

    /**
     * The HTML content to use for this node's display
     * @property content
     * @type string
     */
    content: null,

	itemData: null,
	newItem: null,
	treeIndex: null,	
	hasIcon: null,
	
	result : null,
	
    /**
     * Sets up the node label
     * @property initContent
     * @param {object} An html string or object containing an html property
     * @param {boolean} hasIcon determines if the node will be rendered with an
     * icon or not
     */
    initContent: function(itemData, newItem,treeIndex, hasIcon) {	
    	var KEY_DEFAULT_vALUE = {
			'en':'Generated upon save',
			'fr':'Généré lors de la sauvegarde'
		}
		var html = [];		
		
		/*var choice = '';
		if(result[i].choice==true) {
			choice = ' <input type="radio" name="'+index+'"/>';
		}*/
			
		var cloneNodeImg = '';
		var removeNodeImg = '';
		var type='text'; //default is text
		//modify by ymli. If itemData.parent is not readonly, it can be add or delete
		var tmpStatusItems=true;
				tmpStatusItems = (itemData.parent != null && itemData.parent.readOnly == false || itemData.readOnly==false) ;
				
		if((itemData.maxOccurs<0 || itemData.maxOccurs>1) && tmpStatusItems){
			cloneNodeImg = '<span style="cursor: pointer;" onclick="amalto.itemsbrowser.ItemsBrowser.cloneNode2(\''+itemData.nodeId+'\',false,'+treeIndex+')">' +
					' <img src="img/genericUI/add-element.gif"/></span>';
			removeNodeImg = '<span style="cursor: pointer;" onclick="amalto.itemsbrowser.ItemsBrowser.removeNode2(\''+itemData.nodeId+'\','+treeIndex+')">' +
					' <img src="img/genericUI/remove-element.gif"/></span>';
		}
		if(itemData.typeName!=null&&(itemData.typeName=="PICTURE" || itemData.typeName=="URL")){
			type='hidden';
		}
		
		var mandatory = "";
		this.result = null;
		var check = this.checkMinOccurs(itemData,null);
		if(itemData.readOnly==false && itemData.minOccurs==1 && check) mandatory='<span style="color:red">*</span>';
		// (itemData.parent==null || (itemData.parent!=null && itemData.parent.minOccurs>=1))
		var descInfo = "";
		if(itemData.description!=null)descInfo='<img src="img/genericUI/information_icon.gif" ext:qtitle="Description" ext:qtip="'+itemData.description+'"/>';
		if(itemData.type=="simple"){
				
			var readOnly = "";
			var readOnlyStyle = "";
			
			 //(itemData.key==true && newItem==false);
			
			var tmpStatus=true;
				tmpStatus = (itemData.parent != null && itemData.parent.readOnly == true && itemData.readOnly==true) ;
			//alert("before: "+tmpStatus);
			if(tmpStatus||itemData.typeName=="UUID"||itemData.typeName=="AUTO_INCREMENT"){
				//alert("after: "+tmpStatus);
				readOnlyStyle = readOnly = "READONLY";
			}
			var nullParentStatus = true;
			nullParentStatus = (itemData.parent==null&&(itemData.readOnly==true) || (itemData.key==true&&(itemData.typeName=="UUID"||itemData.typeName=="AUTO_INCREMENT")));
			if(nullParentStatus){
				readOnlyStyle = readOnly = "READONLY";
			}
			var foreignKeyImg = '';
			if(itemData.foreignKey != null) {
				//modify by ymli, if the parent or itself is writable, the foreign key can be set
				var tmpStatus=true;
				tmpStatus = (itemData.parent != null && itemData.parent.readOnly == false) ;
				if(itemData.readOnly==false||tmpStatus||itemData.typeName=="UUID"||itemData.typeName=="AUTO_INCREMENT"){
					//for a foreign key, direct edit is disabled.
					readOnly = "READONLY";
					readOnlyStyle = "ForeignKey";
					foreignKeyImg = '' +
						'<span style="cursor: pointer;" ' +
						'onclick="amalto.itemsbrowser.ItemsBrowser.chooseForeignKey('+itemData.nodeId+',\''+itemData.foreignKey+'\',\''+itemData.foreignKeyInfo+'\','+treeIndex+')" >' +
						' <img src="img/genericUI/magnifier_plus.gif"/></span>';						
				}
			foreignKeyImg += ''+
						'<span style="cursor: pointer;" ' +
						'onclick="amalto.itemsbrowser.ItemsBrowser.browseForeignKey('+itemData.nodeId+',\''+itemData.foreignKey+'\')" >' +
						' <img src="img/genericUI/magnifier.gif"/></span>';
			}
			
			var value = "";
			if(itemData.value!=null) value = itemData.value;
			if(newItem==true&&(itemData.typeName=="UUID"||itemData.typeName=="AUTO_INCREMENT")){
				value = KEY_DEFAULT_vALUE[language];
				mandatory='<span style="color:red">*</span>';
			}
			var typeStatus = true;
			typeStatus = value.length<70 ||(itemData.typeName=="PICTURE")||(itemData.typeName=="URL")

			// select list
			if(itemData.readOnly==false && itemData.enumeration.length!=0) {
					var options = '<option value=""></option>';
					for(var k=0; k<itemData.enumeration.length; k++) {
						if(itemData.enumeration[k]==itemData.value) var selected = "selected";
						else var selected = "";
						options +='<option value="'+itemData.enumeration[k]+'" '+selected+'>'+itemData.enumeration[k]+'</option>';
					}
					var input = ' ' +
						'<select onchange="amalto.itemsbrowser.ItemsBrowser.updateNode(\''+itemData.nodeId+'\','+treeIndex+');" id="'+itemData.nodeId+'Value">' +
						options+
						'</select>';
				}
			//input text
			else if(typeStatus) {					
					var input=' ' +
						' <input class="inputTree'+readOnlyStyle+'" '+readOnly+' ' +
						//TODO'onFocus="amalto.itemsbrowser.ItemsBrowser.setlastUpdatedInputFlagPublic(\''+itemData.nodeId+'\','+treeIndex+');" ' +
						'onchange="amalto.itemsbrowser.ItemsBrowser.updateNode(\''+itemData.nodeId+'\','+treeIndex+');" size="72" type="'+ type+ '"  ' +
						'id="'+itemData.nodeId+'Value" value="'+value+'"'+'/>';
			}
			//input hidden
//			else if(itemData.typeName!=null && itemData.typeName=="URL"){
//				var input=' ' +'<input type="text" id="'+itemData.nodeId+'" value="'+value+'"'+'/>';
//			}
			//text area
			else {
				
				var input = ' ' +
						'<textarea class="inputTree'+readOnlyStyle+'" '+readOnly+' ' +
						'onblur="amalto.itemsbrowser.ItemsBrowser.updateNode(\''+itemData.nodeId+'\','+treeIndex+');" id="'+itemData.nodeId+'Value" ' +
						'rows="4" cols="69" type="text">'+value+'</textarea>';
			}
			

			html[html.length] = '<div style="display:inline"><div style="width:180;float:left;font-size:13px">'+itemData.name+' '+mandatory+' '+descInfo+'</div>';
			if(itemData.typeName!=null&&(itemData.typeName=="date"||itemData.typeName=="dateTime")){//DATE		
				html[html.length] = input;
				var tmpStatus=true;
				tmpStatus = (itemData.parent != null && itemData.parent.readOnly == false) ;
				if(itemData.readOnly==false||tmpStatus)
			   			html[html.length]  = '<span style="cursor:pointer;padding-left:4px;" onclick="javascript:amalto.itemsbrowser.ItemsBrowser.showDatePicker(\''+itemData.nodeId+'\','+treeIndex+',\''+itemData.typeName+'\')"><img src="img/genericUI/date-picker.gif"/></span>'+'</div>';
			}else if(itemData.typeName!=null&&(itemData.typeName=="PICTURE")){//PICTURE
				   html[html.length] = input;
				   //show picture
				   if(value.length>0){
				 		html[html.length] = '<span style="cursor: pointer;"> '+	' <img alt="Picture" id="showPicture" src="'+ itemData.value+ '"/></span>';	
				 	}else{				 		
				 		html[html.length] = '<span style="cursor: pointer;"> '+	' <img alt="Picture" id="showPicture" src="img/genericUI/no_image.gif"/></span>';	
				 	}					
					//remove picture
				var tmpStatus=true;
				tmpStatus = (itemData.parent != null && itemData.parent.readOnly == false) ;
				if(itemData.readOnly==false||tmpStatus){
					html[html.length]='<span style="cursor:pointer;padding-left:4px;" onclick="amalto.itemsbrowser.ItemsBrowser.removePicture(\''+itemData.nodeId+'\','+treeIndex+')">' +
					'<img alt="Remove the picture" src="img/genericUI/clear-icon.gif"/></span>';									
				    html[html.length] ='<span style="cursor:pointer;padding-left:4px;" onclick="javascript:amalto.itemsbrowser.ItemsBrowser.showUploadFile(\''+itemData.nodeId+'\','+treeIndex+',\''+itemData.typeName+'\')"><img alt="Select a picture" src="img/genericUI/image_add.png"/></span>'+'</div>';
				}			
			}else if(itemData.typeName!=null&&(itemData.typeName=="URL")){//URL
				   html[html.length] = ' ' +'<input type="hidden" id="'+itemData.nodeId+'Value" value="'+value+'"'+'/>';
				   if(value.length>0){
				 		html[html.length] ='<span style="cursor: pointer;"><label id="showUrl"><a target="_blank" href=\'' + itemData.value.trim().split("@@")[1]+ '\'>'+itemData.value.trim().split("@@")[0]+'</a></label></span>';	
				   }else{
					   html[html.length] ='<span style="cursor: pointer;"><label id="showUrl"></label></span>';
				   }
				   html[html.length] ='<span style="cursor: pointer;" onclick="amalto.itemsbrowser.ItemsBrowser.showEditWindow('+itemData.nodeId+','+treeIndex+',\''+itemData.typeName+'\')">' +
					' <img src="img/genericUI/add-element.gif"/></span>'+'</div>';
			}else{
			       html[html.length] = input +'</div>';
			}
			
			html[html.length] = '<div style="display:inline"><span id="'+itemData.nodeId+'ValidateBadge" style="background-image:url(img/genericUI/validateBadge.gif);background-repeat:no-repeat;background-position:bottom;width:16px;height:16px;padding-left:4px;display:none"></span>'+'</div>' ;
			html[html.length] = '<span id="'+itemData.nodeId+'OpenDetails" style="cursor:pointer;padding-left:4px;" onclick="amalto.itemsbrowser.ItemsBrowser.displayXsdDetails(\''+itemData.nodeId+'\')" >';
			html[html.length] = '<img src="img/genericUI/open-detail2.gif"/></span>' ;
			html[html.length] = 		cloneNodeImg+' '+removeNodeImg+' '+foreignKeyImg ;
			
			html[html.length] = '<div style="display:inline"><div id="'+itemData.nodeId+'ErrorMessage" style="padding-left:180px;display:none" ></div>';
			html[html.length] = '	<div class="detailLabel" id="'+itemData.nodeId+'XsdDetails" style="display:none">';
			html[html.length] = '		XML tag : '+itemData.xmlTag+'<br/> ' ;
			html[html.length] = '		Type : '+itemData.typeName+'<br/>' ;
			
			
			var restrictions = itemData.restrictions;
			for(var i=0; i<restrictions.length; i++) {
				html[html.length] = '		Facet : ' +restrictions[i].name+' '+restrictions[i].value+'<br/>';
			}			
			
			html[html.length] = '		Documentation : '+itemData.documentation+'<br/>' ;
			html[html.length] = '		Label : '+itemData.labelOtherLanguage ;
			html[html.length] = '	</div>' ;
		}

		else { //complex type
			
			html[html.length] = '<div style="display:inline"><div style="width:180;float:left;font-size:13px">'+itemData.name+' '+mandatory+' '+descInfo+'</div>' ;
			html[html.length] = 	'<input type="text" size="72" class="dotted-line" READONLY /></div>' ;
			html[html.length] = 	'<span id="'+itemData.nodeId+'OpenDetails" onclick="amalto.itemsbrowser.ItemsBrowser.displayXsdDetails(\''+itemData.nodeId+'\')" >' ;
			html[html.length] = 	' <img src="img/genericUI/open-detail2.gif"/></span>';
			html[html.length] = 	cloneNodeImg+' '+removeNodeImg + '<br/>';

			html[html.length] = 	'<div class="detailLabel" id="'+itemData.nodeId+'XsdDetails" style="display:none">' ;
			html[html.length] = 	'XML tag : '+itemData.xmlTag+'<br/> ' ;
			html[html.length] = 	'Type : '+itemData.typeName+'<br/>' ;
			html[html.length] = 	'Documentation : '+itemData.documentation+'<br/>' ;
			html[html.length] = 	'Label : '+itemData.labelOtherLanguage ;
			html[html.length] = 	'</div>'

		}


        this.html = html.join("");
        this.contentElId = "ygtvcontentel" + this.index;
        this.hasIcon = hasIcon;
		this.data = this.html;
        //this.logger = new YAHOO.widget.LogWriter(this.toString());
    },

    /**
     * Returns the outer html element for this node's content
     * @method getContentEl
     * @return {HTMLElement} the element
     */
    getContentEl: function() {
        return document.getElementById(this.contentElId);
    },
    
  
    
	/*
	 * Return true if the new value is valid against the restrictions
	 * and update the inner value of the node to the new value
	 * otherwise return false 
	 */
	updateValue: function(value){
		/*if(this.itemData.typeName=="boolean"){
			value = Boolean(value);
			alert(Boolean(value)+" "+!!value);
		}
		if(this.itemData.typeName!="string"
		&& this.itemData.typeName!="" 
		&& typeof(value)!=this.itemData.typeName){
			alert("mauvais type "+typeof(value));
		}*/
		
		var ERROR_MESSAGE_VALIDATEDOUBLE = {
			'en' : ' is not a valid value for double ',
			'fr' : ' is not a valid value for double '
		}
		
		
		
		this.resetErrorMessage(this.itemData.nodeId);
		
		if(this.itemData.restrictions!=null){	
			for(var i=0;i<this.itemData.restrictions.length;i++){
				var errorMessage = this.itemData.facetErrorMsg[language];
				//var checkParentminOIsReturn = null
				this.result = null
				var check = this.checkMinOccurs(this.itemData,null);
				if(value.length==0&& this.itemData.minOccurs>=1&& check){//(this.itemData.parent==null || (this.itemData.parent!=null && this.itemData.parent.minOccurs>=1))){
				if (errorMessage == null){
				   errorMessage = "The value does not comply with the facet defined in the model: "
							+ "minOccurs"
							+": "
							+this.itemData.minOccurs;
					this.displayErrorMessage(this.itemData.nodeId,errorMessage);
					return false;
				}
			}
			if(this.itemData.restrictions[i].name!="whiteSpace")
				if (errorMessage == null)
				   errorMessage = "The value does not comply with the facet defined in the model: "
							+ this.itemData.restrictions[i].name
							+ ":"
							+ this.itemData.restrictions[i].value;
					
							
				if(this.itemData.restrictions[i].name=="minLength" 
				&& value.length<parseInt(this.itemData.restrictions[i].value)){
					this.displayErrorMessage(this.itemData.nodeId,errorMessage);
					return false;
				}
				if(this.itemData.restrictions[i].name=="maxLength" 
				&& value.length>parseInt(this.itemData.restrictions[i].value)){
					this.displayErrorMessage(this.itemData.nodeId,errorMessage);
					return false;
				}
				if(this.itemData.restrictions[i].name=="minExclusive" )
				{
					if (isNaN(value))
					{
						errorMessage = this.itemData.name + " " + ERROR_MESSAGE_VALIDATEDOUBLE[language];
						this.displayErrorMessage(this.itemData.nodeId,errorMessage);
						return false;
					}
					else if (parseFloat(value)<=parseFloat(this.itemData.restrictions[i].value))
					{
					    this.displayErrorMessage(this.itemData.nodeId,errorMessage);
					    return false;
					}
				}
				if(this.itemData.restrictions[i].name=="maxExclusive")
				{
					if (isNaN(value))
					{
						errorMessage = this.itemData.name + " " + ERROR_MESSAGE_VALIDATEDOUBLE[language];
						this.displayErrorMessage(this.itemData.nodeId,errorMessage);
						return false;
					}
					else if (parseFloat(value) >=parseFloat(this.itemData.restrictions[i].value))
					{
						this.displayErrorMessage(this.itemData.nodeId,errorMessage);
						return false;
					}

				}
				if(this.itemData.restrictions[i].name=="minInclusive" ){
					if (isNaN(value))
					{
						errorMessage = this.itemData.name + " " + ERROR_MESSAGE_VALIDATEDOUBLE[language];
						this.displayErrorMessage(this.itemData.nodeId,errorMessage);
						return false;
					}
					else if (parseFloat(value) < parseFloat(this.itemData.restrictions[i].value))
					{
						this.displayErrorMessage(this.itemData.nodeId,errorMessage);
						return false;
					}
				}
				if(this.itemData.restrictions[i].name=="maxInclusive"){
					if (isNaN(value))
					{
						errorMessage = this.itemData.name + " " + ERROR_MESSAGE_VALIDATEDOUBLE[language];
						this.displayErrorMessage(this.itemData.nodeId,errorMessage);
						return false;
					}
					else if (parseFloat(value) > parseFloat(this.itemData.restrictions[i].value))
					{
						this.displayErrorMessage(this.itemData.nodeId,errorMessage);
						return false;
					}

				}
				
				// var checkParentminOIsReturn = null;
				this.result = null;
				var check = this.checkMinOccurs(this.itemData,null);
				if (this.itemData.readOnly==false && this.itemData.minOccurs>=1 && (value == null || value == "") && check && this.itemData.choice == false)
				{
					this.displayErrorMessage(this.itemData.nodeId,errorMessage);
					return false;
				}
				if(this.itemData.restrictions[i].name=="pattern"){
					var patrn=new RegExp(this.itemData.restrictions[i].value);
					if(!patrn.exec(value)){
						var msg="\""+this.itemData.name+"\" de pattern est \""+this.itemData.restrictions[i].value+"\".";
						this.displayErrorMessage(this.itemData.nodeId,errorMessage);
						return false;
					}
				}
				if(this.itemData.restrictions[i].name=="fractionDigits"){
					if (isNaN(value))
					{
						errorMessage = this.itemData.name + " " + ERROR_MESSAGE_VALIDATEDOUBLE[language];
						this.displayErrorMessage(this.itemData.nodeId,errorMessage);
						return false;
					}
				}
			}
		}
		this.itemData.value = value;
		this.initContent(this.itemData, this.newItem,this.treeIndex, this.hasIcon);
		return true;

	},
	
	resetErrorMessage : function(nodeId) {
		// var errorMessageDivId =nodeId+"ErrorMessage";
		// if($(errorMessageDivId))$(errorMessageDivId).style.display = "none";
		if ($(nodeId + "Value") != null) {
			$(nodeId + "Value").style.border = "solid 1px";
			if ($(nodeId + "Value").readOnly) {
				$(nodeId + "Value").style.background = "#F4F4F4";
			} else {
				$(nodeId + "Value").style.background = "";
			}
			$(nodeId + "ValidateBadge").style.display = "none";
		}
	},
	/**
	 * @author ymli fix bug 0009642
	 * if the parent or grant ... parent's minOccurs >=1, the onde is non-mandatory
	 * @param {} node
	 * @param {} checkParentminOIsReturn
	 * @return {Boolean}
	 */
	 checkMinOccurs:function(node,checkParentminOIsReturn){
	 	if(checkParentminOIsReturn==null && this.result == null ){
	 		var itemNode = node.parent;
    		if(itemNode==null){
    			checkParentminOIsReturn = true;
    			this.result=true;
    			return true;
    		}
    		else if(itemNode!=null && itemNode.minOccurs==0){
    			checkParentminOIsReturn = false;
    			this.result=false;
    			return false;
    		}
    			 else if(itemNode!=null && itemNode.minOccurs>=1)
    					this.checkMinOccurs(itemNode,checkParentminOIsReturn);
	 	}
	 	return this.result;
    },
	
	displayErrorMessage: function(nodeId,msg){
        //var errorMessageDivId = nodeId+"ErrorMessage";
        //$(errorMessageDivId).style.display = "block";
        //$(errorMessageDivId).innerHTML='<font color="red">'+msg+'</font>';
		$(nodeId+"Value").style.border = "1pt solid red";
		$(nodeId+"Value").style.background = "#FFF2EC";
		
		$(nodeId+"ValidateBadge").style.display = "inline-block";
		//new Ext.ToolTip({  
		//target: nodeId+"ValidateBadge",  
		//bodyStyle: 'background-color:#FFF2EC',
		//html: msg,
		//title:'Error',
		//autoHide: false,  
		//closable: false
		//});
		$(nodeId+"ValidateBadge").qtip=msg;
		$(nodeId+"ValidateBadge").qclass='x-form-invalid-tip';
	},
	
	updateNodeId: function(nodeId){
		this.itemData.nodeId = nodeId;
		this.initContent(this.itemData, this.newItem,this.treeIndex, this.hasIcon);
	},

    /*
     * retrieve the value from text field, and compare it to the restrictions
     */
    update: function(update){
    	var itemNode = this.itemData.value;
    	if (this.itemData.type == "complex")
    	    return true;
		itemNode = DWRUtil.getValue(this.itemData.nodeId + "Value");
    	return this.updateValue(itemNode);
    },
    
    // overrides YAHOO.widget.Node
    getNodeHtml: function() {
       // this.logger.log("Generating html");
        var sb = [];

        sb[sb.length] = '<table border="0" cellpadding="0" cellspacing="0">';
        sb[sb.length] = '<tr>';

        for (var i=0;i<this.depth;++i) {
            sb[sb.length] = '<td class="' + this.getDepthStyle(i) + '">&#160;</td>';
        }

        if (this.hasIcon) {
            sb[sb.length] = '<td';
            sb[sb.length] = ' id="' + this.getToggleElId() + '"';
            sb[sb.length] = ' class="' + this.getStyle() + '"';
            sb[sb.length] = ' onclick="javascript:' + this.getToggleLink() + '"';
            if (this.hasChildren(true)) {
                sb[sb.length] = ' onmouseover="this.className=';
                sb[sb.length] = 'YAHOO.widget.TreeView.getNode(\'';
                sb[sb.length] = this.tree.id + '\',' + this.index +  ').getHoverStyle()"';
                sb[sb.length] = ' onmouseout="this.className=';
                sb[sb.length] = 'YAHOO.widget.TreeView.getNode(\'';
                sb[sb.length] = this.tree.id + '\',' + this.index +  ').getStyle()"';
            }
            sb[sb.length] = '>&#160;</td>';
        }

        sb[sb.length] = '<td';
        sb[sb.length] = ' id="' + this.contentElId + '"';
        sb[sb.length] = ' class="' + this.contentStyle + '"';
        sb[sb.length] = ' >';
        sb[sb.length] = this.html;
        sb[sb.length] = '</td>';
        sb[sb.length] = '</tr>';
        sb[sb.length] = '</table>';

        return sb.join("");
    },

    toString: function() {
        return "HTMLNode (" + this.index + ")";
    }

});