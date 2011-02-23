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

amalto.itemsbrowser.ItemNode = function(itemData, newItem, treeIndex, oParent, expanded, hasIcon,isReadOnlyinItem) {
    //if (oData) 
    {
        this.init(null, oParent, expanded);
        this.initContent(itemData, newItem,treeIndex, hasIcon,isReadOnlyinItem);
        this.itemData = itemData;
        this.newItem = newItem;
        this.treeIndex = treeIndex;
        this.hasIcon = hasIcon;
        this.isReadOnlyinItem = isReadOnlyinItem;
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
    initContent: function(itemData, newItem,treeIndex, hasIcon,isReadOnlyinItem) {	
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
					' <img src="img/genericUI/add-element.gif" title="'+ PLUSMUL_TT[language] +'"/></span>';
			removeNodeImg = '<span style="cursor: pointer;" onclick="amalto.itemsbrowser.ItemsBrowser.removeNode2(\''+itemData.nodeId+'\','+treeIndex+')">' +
					' <img src="img/genericUI/remove-element.gif" title="'+ DELMUL_TT[language] +'"/></span>';
		}
		if(itemData.typeName!=null&&(itemData.typeName=="PICTURE" || itemData.typeName=="URL")){
			type='hidden';
		}
		
		var mandatory = "";
		this.result = null;
		var check = this.checkMinOccurs(itemData,null);
		var ancestor = this.checkAncestorMinOCcurs(itemData);
		if(ancestor) check = true;
		if(itemData.key||(itemData.readOnly==false && (itemData.minOccurs>=1||ancestor) )) mandatory='<span style="color:red">*</span>';
		// (itemData.parent==null || (itemData.parent!=null && itemData.parent.minOccurs>=1))
		var descInfo = "";
		if(itemData.description!=null)descInfo='<img src="img/genericUI/information_icon.gif" ext:qtitle="Description" ext:qtip="'+itemData.description+'"/>';
		var polymSelector = "";
		if(itemData.polymiorphism&&itemData.subTypes.length>0){
            var options = '<option value="'+itemData.typeName+'"></option>';
            for(var k=0; k<itemData.subTypes.length; k++) {
                 if(itemData.subTypes[k]==itemData.realType) var selected = "selected";
                 else var selected = "";
                 options +='<option value="'+itemData.subTypes[k]+'" '+selected+'>'+itemData.subTypes[k]+'</option>';
            }
            polymSelector = '<div style="display:inline">' +
                        '<select onchange="amalto.itemsbrowser.ItemsBrowser.reloadNode(\''+itemData.nodeId+'\','+treeIndex+');" id="'+itemData.nodeId+'TypeSelector">' +
                        options+
                        '</select>'+
                        '</div>';
        }
		if(itemData.type=="simple"){
				
			var readOnly = "";
			var readOnlyStyle = "";
			
			 //(itemData.key==true && newItem==false);
			
			var tmpStatus=true;
				tmpStatus = (itemData.parent != null && itemData.parent.readOnly == true && itemData.readOnly==true) ;
			//alert("before: "+tmpStatus);
			if(isReadOnlyinItem||tmpStatus||itemData.typeName=="UUID"||itemData.typeName=="AUTO_INCREMENT"||itemData.typeName=="date"||itemData.typeName=="dateTime"){
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
					foreignKeyImg = '' +
					   '<span style="cursor:pointer;padding-left:4px;" onclick="amalto.itemsbrowser.ItemsBrowser.removeForeignKey(\''+itemData.nodeId+'\','+treeIndex+')">' +
					   '<img title="' + DEL_TT[language] + '" src="img/genericUI/clear-icon.gif"/></span>';
					foreignKeyImg += '' +
						'<span style="cursor: pointer;" ' +
						'onclick="amalto.itemsbrowser.ItemsBrowser.chooseForeignKey('+itemData.nodeId+',\''+itemData.foreignKey+'\',\''+itemData.foreignKeyInfo+'\',\''+itemData.fkFilter+'\','+treeIndex+',false)" >' +
						' <img src="img/genericUI/magnifier_plus.gif" title="' + MAGPLUS_TT[language] + '"/></span>';
				
				var fkDataObject =  itemData.foreignKey.split("/")[0];	
				foreignKeyImg += '' +
					'<span id = "' + fkDataObject +'" style="cursor: pointer;" ' +
					'onclick="amalto.itemsbrowser.ItemsBrowser.displayItemDetails(' + null +',\'' + fkDataObject +'\')" >' +
					' <img src="img/genericUI/add-element.gif" title="'+ PLUSFKS_TT[language] +'"/></span>';
					
				}   
				foreignKeyImg += ''+
						'<span style="cursor: pointer;" ' +
						'onclick="amalto.itemsbrowser.ItemsBrowser.browseForeignKey('+itemData.nodeId+',\''+(itemData.usingforeignKey==null?itemData.foreignKey:itemData.usingforeignKey)+'\',\''+treeIndex+'\')" >' +
						' <img src="img/genericUI/magnifier.gif" title="' + MAG_TT[language] + '"/></span>';
						
				//move this logical to the click-listener of the button 'Add Foreign Key'
			    //This logical seems useless now
//				ItemsBrowserInterface.getRootNode(fkDataObject, language, function(fkNode) {
//					if(fkNode!=null && fkNode.readOnly != false && fkDataObject!=null) {
//						$(fkDataObject).style.display = 'none';
//					}
//				}); 
			}
			
			var value = "";
			if(itemData.value!=null) value = itemData.value;
			if(itemData.valueInfo != null && itemData.retrieveFKinfos && itemData.valueInfo != null) value = itemData.valueInfo;
			if(newItem==true&&(itemData.typeName=="UUID"||itemData.typeName=="AUTO_INCREMENT")){
				value = KEY_DEFAULT_vALUE[language];
				mandatory='<span style="color:red">*</span>';
			}
			var typeStatus = true;
			typeStatus = htmlUnescape(value).length<70 ||(itemData.typeName=="PICTURE")||(itemData.typeName=="URL")

			// select list
			if((itemData.readOnly == false && !isReadOnlyinItem) && itemData.enumeration.length!=0) {
					var options = '<option value=""></option>';
					for(var k=0; k<itemData.enumeration.length; k++) {
						if(itemData.enumeration[k]==itemData.value) var selected = "selected";
						else var selected = "";
						options +='<option value="'+itemData.enumeration[k]+'" '+selected+'>'+itemData.enumeration[k]+'</option>';
					}
					var input = ' ' +
						'<select onchange="amalto.itemsbrowser.ItemsBrowser.updateNode(\''+itemData.nodeId+'\','+treeIndex+');" id="'+itemData.nodeId+'Value">' +
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
                        options +='<option value="'+itemData.enumeration[k]+'" '+selected+'>'+itemData.enumeration[k]+'</option>';
                    }
                    var input = ' ' +
                        '<select onchange="selectedIndex='+initIndex+'" id="'+itemData.nodeId+'Value" '+' class="inputTree'+readOnlyStyle+'" '+readOnly+' >' +
                        options+
                        '</select>';
			}
			//input text
			else if(typeStatus) {					
					var input=' ' +
						' <input class="inputTree'+readOnlyStyle+'" '+readOnly+' ' +
						//TODO'onFocus="amalto.itemsbrowser.ItemsBrowser.setlastUpdatedInputFlagPublic(\''+itemData.nodeId+'\','+treeIndex+');" ' +
						'onchange="amalto.itemsbrowser.ItemsBrowser.updateNode(\''+itemData.nodeId+'\','+treeIndex+',\''+itemData.displayFomats[1]+'\',\''+itemData.typeName+'\');"'+
						/*' onfocus="amalto.itemsbrowser.ItemsBrowser.getRealValue(\''+itemData.nodeId+'\','+treeIndex+');"'+
						' onblur="amalto.itemsbrowser.ItemsBrowser.setFormatValue(\''+itemData.nodeId+'\','+treeIndex+',\''+itemData.displayFomats[1]+'\');"'+*/
						' size="72" type="'+ type+ '"  ' +
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
			if(itemData.typeName!=null&&itemData.typeName=="boolean"){
				value=String(value=='true');
				html[html.length] = '<input type="checkbox" id="'+itemData.nodeId+'Value" value="'+value+'" '
				                    +  (value=='true'?' checked':' ')
				                    +' onchange="this.value=String(this.checked);amalto.itemsbrowser.ItemsBrowser.updateNode(\''+itemData.nodeId+'\','+treeIndex+');"'
				                    +' />';
				//FIXME:empty also means false
			}else if(itemData.typeName!=null&&(itemData.typeName=="date"||itemData.typeName=="dateTime")){//DATE		
				html[html.length] = input;
				var tmpStatus=true;
				tmpStatus = (itemData.parent != null && itemData.parent.readOnly == false) ;
				var clearDate = '<span style="cursor:pointer;padding-left:4px;" onclick="amalto.itemsbrowser.ItemsBrowser.removeForeignKey(\''+itemData.nodeId+'\','+treeIndex+')">' +
                       '<img title="'+ DEL_TT[language] +'" src="img/genericUI/clear-icon.gif"/></span>';
				if((itemData.readOnly == false && !isReadOnlyinItem) || tmpStatus)
			   			html[html.length]  = clearDate +
			   			'<span style="cursor:pointer;padding-left:4px;" onclick="javascript:amalto.itemsbrowser.ItemsBrowser.showDatePicker(\''+itemData.nodeId+'\','+treeIndex+',\''+itemData.typeName+'\',\''+itemData.displayFomats[1]+'\')">'+
			   			'<img src="img/genericUI/date-picker.gif"/></span>'+'</div>';
			}else if(itemData.typeName!=null&&(itemData.typeName=="PICTURE")){//PICTURE
				   html[html.length] = input;
				   //show picture
				   if(value.length>0){
				 		html[html.length] = '<span style="cursor: pointer;"> '+	' <img title="'+ PIC_TT[language] +'" id="'+itemData.nodeId+'showPicture" src="'+ itemData.value+ '"/></span>';	
				 	}else{				 		
				 		html[html.length] = '<span style="cursor: pointer;"> '+	' <img title="'+ PIC_TT[language] +'" id="'+itemData.nodeId+'showPicture" src="img/genericUI/no_image.gif"/></span>';	
				 	}					
					//remove picture
				var tmpStatus=true;
				tmpStatus = (itemData.parent != null && itemData.parent.readOnly == false) ;
				if((itemData.readOnly == false && !isReadOnlyinItem) ||tmpStatus){
					html[html.length]='<span style="cursor:pointer;padding-left:4px;" onclick="amalto.itemsbrowser.ItemsBrowser.removePicture(\''+itemData.nodeId+'\','+treeIndex+')">' +
					'<img title="'+ REMPIC_TT[language] +'" src="img/genericUI/clear-icon.gif"/></span>';									
				    html[html.length] ='<span style="cursor:pointer;padding-left:4px;" onclick="javascript:amalto.itemsbrowser.ItemsBrowser.showUploadFile(\''+itemData.nodeId+'\','+treeIndex+',\''+itemData.typeName+'\')"><img title="'+ SELPIC_TT[language] +'" src="img/genericUI/image_add.png"/></span>'+'</div>';
				}			
			}else if(itemData.typeName!=null&&(itemData.typeName=="URL")){//URL
				   html[html.length] = ' ' +'<input type="hidden" id="'+itemData.nodeId+'Value" value="'+value+'"'+'/>';
				   var showUrlIndex = "showUrl" + itemData.nodeId;
				   
				   if(value.length>0){
				 		html[html.length] ='<span style="cursor: pointer;"><label id="' + showUrlIndex + '"><a target="_blank" href=\'' + itemData.value.trim().split("@@")[1]+ '\'>'+itemData.value.trim().split("@@")[0]+'</a></label></span>';	
				   }else{
					   html[html.length] ='<span style="cursor: pointer;"><label id="' + showUrlIndex + '"></label></span>';
				   }
				   
				   if(!itemData.readOnly) {
					   html[html.length] ='<span style="cursor: pointer;" onclick="amalto.itemsbrowser.ItemsBrowser.showEditWindow('+itemData.nodeId+','+treeIndex+',\''+itemData.typeName+'\')">' +
						' <img src="img/genericUI/add-element.gif"/></span>'+'</div>';
				   }
			}else{
			       html[html.length] = input +'</div>';
			}
			
			html[html.length] = '<div style="display:inline"><span id="'+itemData.nodeId+'ValidateBadge" style="background-image:url(img/genericUI/validateBadge.gif);background-repeat:no-repeat;background-position:bottom;width:16px;height:16px;padding-left:4px;display:none"></span>'+'</div>' ;
//			html[html.length] = '<span id="'+itemData.nodeId+'OpenDetails" style="cursor:pointer;padding-left:4px;" onclick="amalto.itemsbrowser.ItemsBrowser.displayXsdDetails(\''+itemData.nodeId+'\')" >';
//			html[html.length] = '<img src="img/genericUI/open-detail2.gif" title="'+ XSDDET_TT[language] +'"/></span>' ;
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
//			html[html.length] = 	'<span id="'+itemData.nodeId+'OpenDetails" onclick="amalto.itemsbrowser.ItemsBrowser.displayXsdDetails(\''+itemData.nodeId+'\')" >' ;
//			html[html.length] = 	' <img src="img/genericUI/open-detail2.gif" title="'+ XSDDET_TT[language] +'"/></span>';
			html[html.length] = 	cloneNodeImg+' '+removeNodeImg + '<br/>';
			
			if(itemData.polymiorphism&&itemData.subTypes.length>0)
			   html[html.length] =     '<span style="width:180;float:left;font-size:13px">'+USE_EXTENSION_LABEL[language]+'</span>'+' '+polymSelector + '<br/>';

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
    
  
    
/*    updateValue: function(value){
    	
    	this.resetErrorMessage(this.itemData.nodeId); 
    	var localResult="";
    	var id =this.itemData.nodeId;
    	var index = this.index;
        ItemsBrowserInterface.validateNode(id,value,function(result){
        	localResult = result;
            if(!(localResult=="null")){
                this.displayErrorMessage(this.itemData.nodeId,localResult);
                return false;
            }
           this.itemData.nodeId = index;
           this.initContent(this.itemData, this.newItem,this.treeIndex, this.hasIcon,this.isReadOnlyinItem);
           return true;
        });
     
    },*/
    
/*	
	 * Return true if the new value is valid against the restrictions
	 * and update the inner value of the node to the new value
	 * otherwise return false 
	 
	updateValue111: function(value){
		if(this.itemData.typeName=="boolean"){
			value = Boolean(value);
			alert(Boolean(value)+" "+!!value);
		}
		if(this.itemData.typeName!="string"
		&& this.itemData.typeName!="" 
		&& typeof(value)!=this.itemData.typeName){
			alert("mauvais type "+typeof(value));
		}
		
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
				//add by ymli; The item should not be empty if it is key.
				if(value.length == 0 && this.itemData.key){
					if (errorMessage == null){
                   errorMessage = "The value does not comply with the facet defined in the model: "
                            + "Key should not be empty";
                    this.displayErrorMessage(this.itemData.nodeId,errorMessage);
                    return false;
                }
				}
				
				var check = this.checkMinOccurs(this.itemData,null);
				var ancestor = this.checkAncestorMinOCcurs(this.itemData);
				if(ancestor) check = true;
				if(value.length==0 && (this.itemData.minOccurs>=1||ancestor) && check){//(this.itemData.parent==null || (this.itemData.parent!=null && this.itemData.parent.minOccurs>=1))){
				if (errorMessage == null){
					if(this.itemData.minOccurs>=1)
				         errorMessage = "The value does not comply with the facet defined in the model: "
							+ "minOccurs"
							+": "
							+this.itemData.minOccurs;
				   else if(ancestor){
				    	errorMessage = "This item is mandatory!";
				    }			
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
					
				if(this.itemData.minOccurs >=1 || (this.itemData.minOccurs == 0 && value != ""))
				{
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
					if(this.itemData.restrictions[i].name=="length" && value.length!=parseInt(this.itemData.restrictions[i].value)){
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
					
					
				}
				
				// var checkParentminOIsReturn = null;
				this.result = null;
				var check = this.checkMinOccurs(this.itemData,null);
				var ancestor = this.checkAncestorMinOCcurs(this.itemData);
				if(ancestor) check = true;
				if (this.itemData.readOnly==false && (this.itemData.minOccurs>=1||ancestor) && (value == null || value == ""||value.length==0) &&  check && this.itemData.choice == false)
				{
					this.displayErrorMessage(this.itemData.nodeId,errorMessage);
					return false;
				}
				if(this.itemData.minOccurs >=1 || (this.itemData.minOccurs == 0 && value != ""))
				{
					if(this.itemData.restrictions[i].name=="pattern"){
						var patrn=new RegExp(this.itemData.restrictions[i].value);
						if(!patrn.exec(value)){
						//if(value.match(this.itemData.restrictions[i].value)==null){
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
		}
		//add by ymli.fix the bug:0010576		
		this.itemData.nodeId = this.index
				
		this.initContent(this.itemData, this.newItem,this.treeIndex, this.hasIcon,this.isReadOnlyinItem);
		return true;

	},*/
	
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
    			/*checkParentminOIsReturn = true;
    			this.result=true;
    			return true;*/
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
		$(nodeId+"ValidateBadge").qtip=htmlEscape(msg);
		$(nodeId+"ValidateBadge").qclass='x-form-invalid-tip';
	},
	
	updateNodeId: function(nodeId){
		this.itemData.nodeId = nodeId;
		this.initContent(this.itemData, this.newItem,this.treeIndex, this.hasIcon,this.isReadOnlyinItem);
	},

    /*
     * retrieve the value from text field, and compare it to the restrictions
     */
/*    update: function(update){
    	var itemNode = this.itemData.value;
    	if (this.itemData.type == "complex")
    	    return true;
    	if($(this.itemData.nodeId + "Value")){   
		itemNode = DWRUtil.getValue(this.itemData.nodeId + "Value");
    	return this.updateValue(itemNode);
    	} 
    },*/
    
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