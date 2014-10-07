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

amalto.itemsbrowser.ItemNode = function(itemData, newItem, treeIndex, oParent, expanded, hasIcon,isReadOnlyinItem,parentLink) {
    //if (oData) 
    {
    	this.init(null, oParent, expanded);
    	this.initContent(itemData, newItem,treeIndex, hasIcon,isReadOnlyinItem,parentLink);
        this.itemData = itemData;
        this.newItem = newItem;
        this.treeIndex = treeIndex;
        this.hasIcon = hasIcon;
        this.isReadOnlyinItem = isReadOnlyinItem;
        this.parentLink = parentLink;        
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
	isReadOnlyinItem:null,
	result : null,
	
    /**
     * Sets up the node label
     * @property initContent
     * @param {object} An html string or object containing an html property
     * @param {boolean} hasIcon determines if the node will be rendered with an
     * icon or not
     */
    initContent: function(itemData, newItem,treeIndex, hasIcon,isReadOnlyinItem,parentLink) {	
		/********************************************************************
		 * Localization
		 ********************************************************************/
		var DEL_TT={
			'fr':'Supprimer cette valeur',
			'en':'Remove this value'
		};
		
		var MAGPLUS_TT = {
			'fr': 'Sélectionner la relation',
			'en': 'Select the relationship'
		};
		
		var MAG_TT = {
			'fr': 'Ouvrir l\'enregistrement lié',
			'en': 'Open the related record'
		};
		
		var PLUSMUL_TT = {
			'fr':'Ajouter une occurrence',
			'en':'Add an occurrence'
		};
		
		var DEEPPLUSMUL_TT = {
				'fr':'Cloner cette occurrence',
				'en':'Clone this occurrence'
			};
		
		var PLUSFKS_TT = {
			'fr':'Nouvel enregistrement pour cette relation',
			'en':'Create a new record for this relation'
		};
		
		var DELMUL_TT = {
			'fr':'Effacer une occurrence',
			'en':'Delete an occurrence'
		};
		var PIC_TT = {
			'fr':'Image',
			'en':'Picture'
		};
		var REMPIC_TT = {
			'fr':'Supprimer l\'image',
			'en':'Remove the picture'
		};
		var SELPIC_TT = {
			'fr':'Sélectionner une image',
			'en':'Select a picture'
		};
		var XSDDET_TT = {
			'fr':'Afficher les détails XSD',
			'en':'Display XSD details'
		};		
		var KEY_DEFAULT_vALUE = {
			'en':'(Auto)',
			'fr':'(Auto)'
		};
		var USE_EXTENSION_LABEL = {
            'en':'Use Extension',
            'fr':'Utilisez Extension'
        };
		var html = [];		
	
		var cloneNodeImg = '';
		var removeNodeImg = '';
		var deepCloneNodeImg = '';
		var type='text'; //default is text
		//modify by ymli. If itemData.parent is not readonly, it can be add or delete
		var tmpStatusItems=true;
				tmpStatusItems = (itemData.parent != null && itemData.parent.readOnly == false || itemData.readOnly==false) ;
				
		if((itemData.maxOccurs<0 || itemData.maxOccurs>1) && tmpStatusItems){
			cloneNodeImg = '<span style="cursor: pointer;" onclick="amalto.itemsbrowser.ItemsBrowser.cloneNode2(\''+this.index+'\',false,'+treeIndex+',\''+parentLink["conceptName"]+'\', false)" >' +
					' <img src="img/genericUI/add.png" title="'+ PLUSMUL_TT[language] +'"/></span>';			
			removeNodeImg = '<span style="cursor: pointer;" onclick="amalto.itemsbrowser.ItemsBrowser.removeNode2(\''+this.index+'\','+treeIndex+')">' +
					' <img src="img/genericUI/delete.png" title="'+ DELMUL_TT[language] +'"/></span>';
			deepCloneNodeImg = '<span style="cursor: pointer;" onclick="amalto.itemsbrowser.ItemsBrowser.cloneNode2(\''+this.index+'\',false,'+treeIndex+',\''+parentLink["conceptName"]+'\', true)" >' +
			' <img src="img/genericUI/add-group.png" title="'+ DEEPPLUSMUL_TT[language] +'"/></span>';
		}
		if(itemData.typeName!=null&&(itemData.typeName=="PICTURE" || itemData.typeName=="URL")){
			type='hidden';
		}
		
		var mandatory = '<span style="color:red; visibility: hidden;">*</span>';
		this.result = null;
		//var check = this.checkMinOccurs(itemData,null);
		//var ancestor = this.checkAncestorMinOCcurs(itemData);
		//if(ancestor) check = true;
		if(itemData.key||(itemData.minOccurs>=1  )) mandatory='<span style="color:red">*</span>';
		// (itemData.parent==null || (itemData.parent!=null && itemData.parent.minOccurs>=1))
		var descInfo = '<img src="img/genericUI/information_icon.png" style="visibility: hidden"/>';
		if(itemData.description!=null)descInfo='<img src="img/genericUI/information_icon.png" ext:qtitle="Description" ext:qtip="'+itemData.description+'"/>';
		var polymSelector = "";
		if(itemData.polymiorphism&&itemData.subTypes.length>0){
			
            var options = '';
            if(!(itemData.abstract == true)) {
            	options += '<option value="'+itemData.typeName+'"></option>';	
            }
            for(var k=0; k<itemData.subTypes.length; k++) {
                 if(itemData.subTypes[k].name==itemData.realType) var selected = "selected";
                 else var selected = "";
                 options +='<option value="'+itemData.subTypes[k].name+'" '+selected+'>'+itemData.subTypes[k].label+'</option>';
            }
            polymSelector = '<div style="display:inline">' +
                        '<select onchange="amalto.itemsbrowser.ItemsBrowser.reloadNode(\''+this.index+'\','+treeIndex+');" class="selectTreeREADONLY" id="'+this.index+'TypeSelector">' +
                        options+
                        '</select>'+
                        '</div>';
        }
		if(itemData.type=="simple"){
				
			var readOnly = "";
			var readOnlyStyle = "";
			
			 //(itemData.key==true && newItem==false);
			
			var tmpStatus=  false;	
			if ((itemData.parent != null && itemData.parent.readOnly == true) || itemData.readOnly==true)
				tmpStatus = true;			
			//alert("before: "+tmpStatus);
			if(isReadOnlyinItem||tmpStatus||itemData.typeName=="UUID"||itemData.typeName=="AUTO_INCREMENT"){
				//alert("after: "+tmpStatus);
				readOnlyStyle = readOnly = "READONLY";
			}
			var nullParentStatus = true;
			nullParentStatus = (isReadOnlyinItem||itemData.parent==null&&(itemData.readOnly==true) || (itemData.key==true&&(itemData.typeName=="UUID"||itemData.typeName=="AUTO_INCREMENT")));
			if(nullParentStatus){
				readOnlyStyle = readOnly = "READONLY";
			}
			var foreignKeyImg = '';
			if(itemData.foreignKey != null) {
				//modify by ymli, if the parent or itself is writable, the foreign key can be set
				
				var tmpStatus=true;
				tmpStatus = (itemData.parent != null && itemData.parent.readOnly == false) ;
				
				
				if(!isReadOnlyinItem &&(itemData.readOnly==false||tmpStatus||itemData.typeName=="UUID"||itemData.typeName=="AUTO_INCREMENT")){
					//for a foreign key, direct edit is disabled.
					readOnly = "READONLY";
					readOnlyStyle = "ForeignKey";
					var swit = false;
					
					if(parentLink == undefined) {
						parentLink = [];
					}
					
					foreignKeyImg += '' +
						'<span style="cursor: pointer;" ' +
						'onclick="amalto.itemsbrowser.ItemsBrowser.chooseForeignKey('+this.index+',\''+itemData.foreignKey+'\',\''+itemData.foreignKeyInfo+'\',\''+itemData.fkFilter+'\','+treeIndex+','+swit+',\''+parentLink["conceptName"]+'\')" >' +
						' <img src="img/genericUI/link_edit.png" title="' + MAGPLUS_TT[language] + '"/></span>';
				
					var fkDataObject =  itemData.foreignKey.split("/")[0];	
					foreignKeyImg += '' +
					'<span id = "' + fkDataObject +'" style="cursor: pointer;" ' +
					'onclick="amalto.itemsbrowser.ItemsBrowser.displayItemDetails(' + null +',\'' + fkDataObject +'\')" >' +
					' <img src="img/genericUI/link_add.png" title="'+ PLUSFKS_TT[language] +'"/></span>';
					foreignKeyImg += '' +
					'<span style="cursor:pointer;padding-left:4px;" onclick="amalto.itemsbrowser.ItemsBrowser.removeForeignKey(\''+this.index+'\','+treeIndex+')">' +
					'<img title="' + DEL_TT[language] + '" src="img/genericUI/link_delete.png"/></span>';	
				}  
				
				foreignKeyImg += ''+
						'<span style="cursor: pointer;" ' +
						'onclick="amalto.itemsbrowser.ItemsBrowser.browseForeignKey('+this.index+',\''+(itemData.usingforeignKey==null?itemData.foreignKey:itemData.usingforeignKey)+'\',\''+treeIndex+'\',\''+htmlEscape(jsStringEscape(parentLink["title"]), true)+'\',\''+parentLink["ids"]+'\',\''+parentLink["conceptName"]+'\',\''+parentLink["isWindow"]+'\')" >' +
						' <img src="img/genericUI/link_go.png" title="' + MAG_TT[language] + '"/></span>';
			}
			
			var value = "";
			if(itemData.value!=null) value = itemData.value;
			if(itemData.valueInfo != null && itemData.retrieveFKinfos && itemData.valueInfo != null) value = itemData.valueInfo;
			if(newItem==true&&(itemData.typeName=="UUID"||itemData.typeName=="AUTO_INCREMENT")){
				value = KEY_DEFAULT_vALUE[language];
				mandatory='<span style="color:red">*</span>';
			}
			
			var typeTextArea= false;
			typeTextArea = value.length>=70 && (itemData.typeName!="PICTURE") && (itemData.typeName!="URL")

			// select list
			if((itemData.readOnly == false && !isReadOnlyinItem) && itemData.enumeration.length!=0) {
					var options = '<option value=""></option>';
					for(var k=0; k<itemData.enumeration.length; k++) {
						if(itemData.enumeration[k]==itemData.value) var selected = "selected";
						else var selected = "";
						options +='<option value="'+htmlEscape(itemData.enumeration[k], true)+'" '+selected+'>'+htmlEscape(itemData.enumeration[k])+'</option>';
					}
					var input = ' ' +
						'<select onchange="amalto.itemsbrowser.ItemsBrowser.updateNode(\''+this.index+'\','+treeIndex+');" class="selectTree" id="'+this.index+'Value">' +
					//'<select onchange="amalto.itemsbrowser.ItemsBrowser.updateNode(\''+itemData.nodeId+'\','+treeIndex+');" id="'+itemData.nodeId+'Value">' +
						options+
						'</select>';
			}else if((itemData.readOnly == true || isReadOnlyinItem) && itemData.enumeration.length>0){
                    var options = '<option value=""></option>';
                    var initIndex=0;
                    for(var k=0; k<itemData.enumeration.length; k++) {
                        if(itemData.enumeration[k]==itemData.value){
                          var selected = "selected";
                          initIndex=k+1;
                        }
                        else var selected = "";
                        options +='<option value="'+htmlEscape(itemData.enumeration[k], trunk)+'" '+selected+'>'+htmlEscape(itemData.enumeration[k])+'</option>';
                    }
                    var input = ' ' +
                        '<select onchange="selectedIndex='+initIndex+'" id="'+this.index+'Value" '+' class="selectTree'+readOnlyStyle+'" '+readOnly+' >' +
                        options+
                        '</select>';
			}
			//input text
			else if(!typeTextArea) {
				var displayValue = value;
				if(itemData.foreignKey != null)
					if(parentLink != undefined)
						if(parentLink["cloneNode2"] != undefined)
							if(parentLink["cloneNode2"])
								displayValue = "";	
				
					var input=' ' +
						' <input class="inputTree'+readOnlyStyle+'" '+readOnly+' ' +
						//TODO'onFocus="amalto.itemsbrowser.ItemsBrowser.setlastUpdatedInputFlagPublic(\''+itemData.nodeId+'\','+treeIndex+');" ' +
						'onchange="amalto.itemsbrowser.ItemsBrowser.updateNode(\''+this.index+'\','+treeIndex+',\''+itemData.displayFomats[1]+'\',\''+itemData.typeName+'\');"'+
						/*' onfocus="amalto.itemsbrowser.ItemsBrowser.getRealValue(\''+itemData.nodeId+'\','+treeIndex+');"'+
						' onblur="amalto.itemsbrowser.ItemsBrowser.setFormatValue(\''+itemData.nodeId+'\','+treeIndex+',\''+itemData.displayFomats[1]+'\');"'+*/
						' size="72px" type="'+ type+ '"  ' +
						'id="'+this.index+'Value" value="'+htmlEscape(displayValue, true)+'"'+'/>';
			}
			//input hidden
//			else if(itemData.typeName!=null && itemData.typeName=="URL"){
//				var input=' ' +'<input type="text" id="'+itemData.nodeId+'" value="'+htmlEscape(value, true)+'"'+'/>';
//			}
			//text area
			else {
				
				var input = ' ' +
						'<textarea class="textAreaTree'+readOnlyStyle+'" '+readOnly+' ' +
						'onblur="amalto.itemsbrowser.ItemsBrowser.updateNode(\''+this.index+'\','+treeIndex+');" id="'+this.index+'Value" ' +
						'rows="4" cols="69" type="text">'+htmlEscape(value)+'</textarea>';
			}
			
			var labelValue = itemData.name;
			html[html.length] = '<div style="display:inline"><div class="inputLabel" title="' + itemData.name + '">'+labelValue+' '+mandatory+' '+descInfo+'</div>';
			if(itemData.typeName!=null&&itemData.typeName=="boolean"){
				//value=String(value=='true');
				if (value == 'false')
					value = '';
				var disabledStyle = (readOnly == "READONLY") ? 'disabled ="disabled"': "";
				html[html.length] = '<input type="checkbox" id="'+this.index+'Value" value="'+htmlEscape(value, true)+'" ' + disabledStyle
				                    +  (value=='true'?' checked':' ')
				                    +' onclick="this.value=String(this.checked);amalto.itemsbrowser.ItemsBrowser.updateNode(\''+this.index+'\','+treeIndex+');"'
				                    +' />';
				//FIXME:empty also means false
			}else if(itemData.typeName!=null&&(itemData.typeName=="date"||itemData.typeName=="dateTime")){//DATE		
				html[html.length] = input;
				var tmpStatus=true;
				tmpStatus = (itemData.parent != null && itemData.parent.readOnly == false) ;
				var clearDate = '<span style="cursor:pointer;padding-left:4px;" onclick="amalto.itemsbrowser.ItemsBrowser.removeForeignKey(\''+this.index+'\','+treeIndex+')">' +
                       '<img title="'+ DEL_TT[language] +'" src="img/genericUI/delete.png"/></span>';
				if((itemData.readOnly == false && !isReadOnlyinItem) || tmpStatus)
			   			html[html.length]  = clearDate +
			   			'<span style="cursor:pointer;padding-left:4px;" onclick="javascript:amalto.itemsbrowser.ItemsBrowser.showDatePicker(\''+this.index+'\','+treeIndex+',\''+itemData.typeName+'\',\''+itemData.displayFomats[1]+'\')">'+
			   			'<img src="img/genericUI/date-picker.gif"/></span>'+'</div>';
			}else if(itemData.typeName!=null&&(itemData.typeName=="PICTURE")){//PICTURE
				   html[html.length] = input;
				   //show picture
				   if(value.length>0){
				 		html[html.length] = '<span style="cursor: pointer;"> '+	' <img title="'+ PIC_TT[language] +'" id="'+this.index+'showPicture" src="'+ htmlEscape(itemData.value, true)+ '"/></span>';	
				 	}else{				 		
				 		html[html.length] = '<span style="cursor: pointer;"> '+	' <img title="'+ PIC_TT[language] +'" id="'+this.index+'showPicture" src="img/genericUI/no_image.gif"/></span>';	
				 	}					
					//remove picture
				var tmpStatus=true;
				tmpStatus = (itemData.parent != null && itemData.parent.readOnly == false) ;
				if((itemData.readOnly == false && !isReadOnlyinItem) ||tmpStatus){
					html[html.length] ='<span style="cursor:pointer;padding-left:4px;" onclick="javascript:amalto.itemsbrowser.ItemsBrowser.showUploadFile(\''+this.index+'\','+treeIndex+',\''+itemData.typeName+'\')">' +
					'<img title="'+ SELPIC_TT[language] +'" src="img/genericUI/picture_add.png"/></span>'+'</div>';
					html[html.length]='<span style="cursor:pointer;padding-left:4px;" onclick="amalto.itemsbrowser.ItemsBrowser.removePicture(\''+this.index+'\','+treeIndex+')">' +
					'<img title="'+ REMPIC_TT[language] +'" src="img/genericUI/picture_delete.png"/></span>';
				}			
			}else if(itemData.typeName!=null&&(itemData.typeName=="URL")){//URL
				   html[html.length] = ' ' +'<input type="hidden" id="'+this.index+'Value" value="'+htmlEscape(value, true)+'"'+'/>';
				   var showUrlIndex = "showUrl" + this.index;
				   
				   if(value.length>0){
				 		html[html.length] ='<span style="cursor: pointer;"><label id="' + showUrlIndex + '"><a target="_blank" href=\'' + htmlEscape(itemData.value.trim().split("@@")[1], true)+ '\'>'+htmlEscape(itemData.value.trim().split("@@")[0])+'</a></label></span>';	
				   }else{
					   html[html.length] ='<span style="cursor: pointer;"><label id="' + showUrlIndex + '"></label></span>';
				   }
				   
				   if(!itemData.readOnly) {
					   html[html.length] ='<span style="cursor: pointer;" onclick="amalto.itemsbrowser.ItemsBrowser.showEditWindow('+this.index+','+treeIndex+',\''+itemData.typeName+'\')">' +
						' <img src="img/genericUI/link_edit.png"/></span>'+'</div>';
				   }
			}else{
			       html[html.length] = input +'</div>';
			}
			
			html[html.length] = '<div style="display:inline"><span id="'+this.index+'ValidateBadge" style="background-image:url(img/genericUI/validateBadge.gif);background-repeat:no-repeat;background-position:bottom;width:16px;height:16px;padding-left:4px;display:none"></span>'+'</div>' ;
			html[html.length] = 		cloneNodeImg+' '+removeNodeImg  +' '+foreignKeyImg ;
			
			html[html.length] = '<div style="display:inline"><div id="'+this.index+'ErrorMessage" style="padding-left:180px;display:none" ></div>';
			html[html.length] = '	<div class="detailLabel" id="'+this.index+'XsdDetails" style="display:none">';
			html[html.length] = '		XML tag : '+itemData.xmlTag+'<br/> ' ;
			html[html.length] = '		Type : '+itemData.typeName+'<br/>' ;
			
			
			var restrictions = itemData.restrictions;
			for(var i=0; i<restrictions.length; i++) {
				html[html.length] = '		Facet : ' +restrictions[i].name+' '+htmlEscape(restrictions[i].value)+'<br/>';
			}			
			
			html[html.length] = '		Documentation : '+itemData.documentation+'<br/>' ;
			html[html.length] = '		Label : '+itemData.labelOtherLanguage ;
			html[html.length] = '	</div>' ;
		}

		else { //complex type
			var labelValue = itemData.name;
			html[html.length] = '<div style="display:inline"><div class="inputLabel" title="' + itemData.name + '">'+labelValue+' '+mandatory+' '+descInfo+'</div>' ;
			html[html.length] = 	cloneNodeImg+' '+removeNodeImg + ' ' + deepCloneNodeImg + '<br/>';
			
			if(itemData.polymiorphism&&itemData.subTypes.length>0)
			   html[html.length] =     '<span class="inputLabel">'+USE_EXTENSION_LABEL[language]+'</span>'+' '+polymSelector + '<br/>';

			html[html.length] = 	'<div class="detailLabel" id="'+this.index+'XsdDetails" style="display:none">' ;
			html[html.length] = 	'XML tag : '+itemData.xmlTag+'<br/> ' ;
			html[html.length] = 	'Type : '+itemData.typeName+'<br/>' ;
			html[html.length] = 	'Documentation : '+itemData.documentation+'<br/>' ;
			html[html.length] = 	'Label : '+itemData.labelOtherLanguage ;
			html[html.length] = 	'</div>'

		}


        this.html = html.join("");
        this.contentElId = "ygtvcontentel" + this.index;
        this.hasIcon = hasIcon;
		this.data = this.html
        this.itemData = itemData;
    },

    /**
     * Returns the outer html element for this node's content
     * @method getContentEl
     * @return {HTMLElement} the element
     */
    getContentEl: function() {
        return document.getElementById(this.contentElId);
    },
	
	resetErrorMessage : function(nodeId) {
		if ($(nodeId + "Value") != null) {		
			if($(nodeId + "Value").originalStyleChanged == true) {	
				
				$(nodeId + "Value").className = $(nodeId + "Value").originalClassName;
				
				$(nodeId + "ValidateBadge").style.display = "none";		
				
			}
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
    			return false;
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
	/**
	 * add by ymli. check if the ancestor  node is mandatory.
	 * @param {} itemNode
	 * @return {Boolean}
	 */
    checkAncestorMinOCcurs:function(itemNode){
    	if(itemNode.parent==null && itemNode.minOccurs>=1){
    	   return true;
    	}
    	else if(itemNode.parent==null && itemNode.minOccurs==0){
    		return false;
    	}
    	else{
    	   return this.checkAncestorMinOCcurs(itemNode.parent);
    	}
    },
    
	displayErrorMessage: function(nodeId,msg){
		
		$(nodeId + "Value").originalStyleChanged = true;
		
		$(nodeId + "Value").originalClassName = $(nodeId + "Value").className;			
		
		$(nodeId+"Value").className = 'errorValue';	
		
		$(nodeId+"ValidateBadge").style.display = "inline-block";
		$(nodeId+"ValidateBadge").qtip=htmlEscape(msg);
		$(nodeId+"ValidateBadge").qclass='x-form-invalid-tip';
	},
	
	updateNodeId: function(nodeId){
		this.itemData.nodeId = nodeId;
		this.initContent(this.itemData, this.newItem,this.treeIndex, this.hasIcon,this.isReadOnlyinItem, this.parentLink);
	},
	
	updateNodeValue: function(updatedValue){
		this.itemData.value = updatedValue;
		this.initContent(this.itemData, this.newItem,this.treeIndex, this.hasIcon,this.isReadOnlyinItem, this.parentLink);
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
    },
    
    // overrides
    getHtml: function() {

        this.childrenRendered = false;

        var style = ' style="display:';
        if(this.itemData.visible == false){
        	style +='none;"';
        }
        else{
        	style +='block;"';
        }
        var sb = [];
        sb[sb.length] = '<div class="ygtvitem" id="' + this.getElId() + '"' + style + '>';
        sb[sb.length] = this.getNodeHtml();
        sb[sb.length] = this.getChildrenHtml();
        sb[sb.length] = '</div>';
        return sb.join("");
    }
});