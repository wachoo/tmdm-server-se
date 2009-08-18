/********************************************************************
 * 
 * GLOBAL PROTOTYPES and FUNCTIONS
 * 
 ********************************************************************/

String.prototype.ellipse = function(maxLength){
    if(this.length > maxLength){
        return this.substr(0, maxLength-5) + '...';
    }
    return this;
}
String.prototype.replaceAll=function(s1, s2) { 
	return this.replace(new RegExp(s1,"g"), s2); 
}

if(!Array.indexOf){
    Array.prototype.indexOf = function(obj, start){
        for(var i=(start||0); i<this.length; i++){
            if(this[i]==obj){
                return i;
            }
        }
    }
}



/********************************************************************
 * 
 * amalto Namespace
 * 
 ********************************************************************/


/* Taken From YAHOO */
var amalto = function(){
	return {
		namespace: function() {
		    var a=arguments, o=null, i, j, d;
		    for (i=0; i<a.length; i=i+1) {
		        d=a[i].split(".");
		        o=amalto;
		
		        // amalto is implied, so it is ignored if it is included
		        for (j=(d[0] == "amalto") ? 1 : 0; j<d.length; j=j+1) {
		            o[d[j]]=o[d[j]] || {};
		            o=o[d[j]];
		        }
		    }
		    return o;
		}
	}
}();

/**
 * Amalto Namespaces
 */
amalto.namespace("amalto.b2box");


/**
  * The namespace of a module is amalto.b2box.modulename where modulename is the Application Name stripped of spaces 
 */
 
amalto.namespace("amalto.b2box.core");


/**
 * See the YAHOO Module pattern @  http://yuiblog.com/blog/2007/06/12/module-pattern/
 */
 
amalto.b2box.core = function () {

	/********************************************************************
	 * 
	 * PRIVATE Properties and Methods
	 * 
	 ********************************************************************/
	
	//loaded javascripts kept as context.modulename 
	//modulename is the application name stirpped of spaces
	var loadedScripts = new Array();

	/**
	* Layout
	*/
	//Panels
	var statusPanel;
	
	var activePanelId;
	var oldActivePanelId;
	
	var initUI = function() {
		language = DWRUtil.getValue('languageSelect');
		// initialize state manager, we will use cookies
		YAHOO.ext.state.Manager.setProvider(new YAHOO.ext.state.CookieProvider());
		
		// create the main layout
		amalto.b2box.core.layout = new YAHOO.ext.BorderLayout(document.body, {
		    north: {
		        split:false,
		        initialSize: 65,
		        titlebar: false
		    },
		    west: {
		        split:true,
		        initialSize: 120,
		        minSize: 80,
		        maxSize: 200,
		        titlebar: true,
		        collapsible: true,
		        animate: true,
		        autoScroll:false,
		        useShim:true,
		        cmargins: {top:0,bottom:2,right:2,left:2}
		    },
		    east: {
		        split:true,
		        initialSize: 120,
		        minSize: 50,
		        maxSize: 300,
		        titlebar: true,
		        collapsible: true,
		        animate: true,
		        autoScroll:false,
		        useShim:true,
		        collapsed:true,
		        cmargins: {top:0,bottom:2,right:2,left:2}
		    },
		    south: {
		        split:false,
		        initialSize: 22,
		        titlebar: false,
		        collapsible: false,
		        animate: false
		    },
		    center: {
		        titlebar: false,
		        autoScroll:false,
		        tabPosition: 'top',
		        closeOnTab: true,
		        alwaysShowTabs: true,
		        resizeTabs: true
		    }
		});
		// tell the layout not to perform layouts until we're done adding everything
		amalto.b2box.core.layout.beginUpdate();
		amalto.b2box.core.layout.add('north', new YAHOO.ext.ContentPanel('header'));
		
		amalto.b2box.core.layout.getRegion('center').on(
			'panelactivated', 
			function(region,panel){
				oldActivePanelId=activePanelId;
				activePanelId=panel.getId();
			}
		);
		amalto.b2box.core.layout.getRegion('center').on(
			'panelremoved', 
			function(region){
				alert("Panel removed: "+activePanelId +". Will attemp to display: "+ oldActivePanelId);
				region.showPanel(oldActivePanelId)
			}
		);
		
		//Initialize the logout button
		var logoutBtn = new YAHOO.ext.Button('logout-btn',{handler: logout, text: BUTTON_LOGOUT[language]}); 
		
		// initialize the statusbar
		statusPanel = new YAHOO.ext.ContentPanel('status');
		south = amalto.b2box.core.layout.getRegion('south');
		south.add(statusPanel);
		
		// create the menu toolbar
		amalto.b2box.core.layout.add('west', new YAHOO.ext.ContentPanel('actions', {title: 'Menu', fitToFrame:true}));
		
		//Disabled for the b2box
		/*
		//create the model and cluster toolbar if user is technical admin
		CommonInterface.isTechnicalAdmin(function(result){
			if(result==false) return;
			layout.add('east', new YAHOO.ext.ContentPanel('config', {title: 'Cluster & model', fitToFrame:true, resizeEl:'config-body'}));
			YAHOO.ext.DomHelper.append('config-body', {tag: 'div', html: 'Data cluster'});
			YAHOO.ext.DomHelper.append('config-body', {tag: 'select', id : 'datacluster-select', action: ''});
			YAHOO.ext.DomHelper.append('config-body', {tag: 'div', html: 'Data model'});
			YAHOO.ext.DomHelper.append('config-body', {tag: 'select', id : 'datamodel-select', action: ''});
			YAHOO.ext.DomHelper.append('config-body', {tag: 'div', id: 'save-btn'});
			new YAHOO.ext.Button('save-btn',{handler: saveConfig, text: 'Save'}); 
		});	
		*/
			
	   //load actions
		loadActions();
	
	    //update status panel back
		statusPanel.getEl().addClass('done');
		statusPanel.setContent('');
		
		// restore any state information
		amalto.b2box.core.layout.restoreState();
		amalto.b2box.core.layout.endUpdate();
		
		//add username info
		CommonInterface.getUsername(function(result){
			DWRUtil.setValue('username-div',LABEL_WELCOME[language]+", "+result);
		});
	}
	
	
	/*
	var working = function(){
		statusPanel.getEl().addClass('done');
		setTimeout("statusPanel.setContent('')",3000);
	}
	*/
	
	var saveConfig = function(){
		var cluster = DWRUtil.getValue('datacluster-select');
		var model = DWRUtil.getValue('datamodel-select');
		CommonInterface.setClusterAndModel(cluster,model,function(result){
			alert(result);
		});
	}
	
	/**
	 * Actions handling
	 */
	var ACTIONS;
	
	var loadActions = function() {
		var actions = new Array();
		CommonInterface.getMenus(
			language,
			function(result){
				var actions = eval("(" +result+")");
				
				var pactions = getEl('actions');
			    // delegate clicks on the lists
			    pactions.mon('click', actionClicked);
			    
			    //create actions template
			    var tplClosed = new YAHOO.ext.DomHelper.Template(
			    	'<a id="action-{id}" href="{url}"><span id="action-{id}-span" class="body">{name}<br><span class="desc">{desc}</span></span></a>'+
			    	'<div id="action-{id}-div" class="sub-body" style="display: none;"/>'
			    );
			   	var tplNone = new YAHOO.ext.DomHelper.Template(
			    	'<a id="action-{id}" href="{url}"><span class="body">{name}<br><span class="desc">{desc}</span></span></a>'
			    );
			    
			    var previousLevel = 1;
			    var doms = new Array();
			    doms[0] = pactions.dom;
	  
			    // go through the actions and add them to the list
			    for(var index=0; index<actions.length; index++) {
			    	var action = actions[index];
			    	if (action.application=='') { 		    		//a menu with submenus
			    		tplClosed.append(doms[action.level-1], action);
			    		//update doms
			    		var divId = 'action-'+action.id+'-div';
			    		doms[action.level] =getEl(divId).dom;
			    		//make IE display the image centered by loading the image now (rather than on initialisation)
			    		$("action-"+action.id+"-span").style.background="url(img/b2box/tree-closed.gif) no-repeat 0px 9px;";
			    	} else {
			    		tplNone.append(doms[action.level-1], action);
			    	}
			    }
				 ACTIONS = actions;			
			}
		);
	}//loadActions
	
	//var centerPanel = null;
	
	var actionClicked = function(e) {
		// find the "a" element that was clicked
		var a = e.findTarget(null, 'a');
		if(a){
			e.preventDefault();
			//update status panel
			//statusPanel.setContent('Loading action ' + a.name + '...');
	        statusPanel.getEl().removeClass('done');
	
			//clean up center layout
	//		var divs =  YAHOO.util.Dom.  getEl('centerdiv').
	//		for (div in divs) div.style.display = 'none';
			
			//update display of action in action Panel
			YAHOO.util.Dom.removeClass(getEl('actions').dom.getElementsByTagName('a'), 'selected');
			YAHOO.util.Dom.addClass(a.id, 'selected');  //.substr(5)
			
			//run the action associated
			
			var actionId = a.id.substr(7);
			var action = ACTIONS[actionId-1];
			
			if (action.application == '') {
				//A menu Holder
				if ($("action-"+action.id+"-div").style.display=="none") {
					$("action-"+action.id+"-div").style.display="block";
					$("action-"+action.id+"-span").style.background="url(img/b2box/tree-opened.gif) no-repeat 0px 9px;";
				} else {
					$("action-"+action.id+"-div").style.display="none";
					$("action-"+action.id+"-span").style.background="url(img/b2box/tree-closed.gif) no-repeat 0px 9px;";				
				}
			}else {
				//load the script if necessary
				amalto.b2box.core.loadMainScript(
					action.context,
					action.application,
					function() {
						var initFunction = "amalto.b2box."+action.context+"."+action.application.replace(/\s/g,'')+".init()";
						setTimeout(initFunction,'50');
						amalto.b2box.core.ready();		
					}
				);			
			}//else   
		}//findTarget
	}
	
	/**
	 * Action LOGOUT
	 */
	var logout = function() {
		document.location='/'+amalto.b2box.core.ROOT_CONTEXT+'/secure/?action=logout';
	}
	
	/*
	 * Hot Loads a javascript
	 */
	 /*
	var loadScript = function(scriptID, src) {
		var loaded = false;
		for(var index=0; index<loadedScripts.length; index++) {
			if (loadedScripts[index]==scriptID) {
				loaded = true;
				break;
			}
		}
		if (loaded) return;
		var head = document.getElementsByTagName("head")[0];
		var mainScript = document.createElement('script');
		mainScript.id = scriptID;
		mainScript.type = 'text/javascript';
		mainScript.src = src;
		head.appendChild(mainScript);
		loadedScripts.push(scriptID);	
	}
	*/
	
	
	var loadScript = function(scriptID, src, verifiedObjectName, callbackFirstTime, callbackFollowingTimes) {
		if (amalto.b2box.core.isDefined('window','console')) console.log("loadScript loading "+src, "info", "amalto.b2box.core");		
		//first check if it already loaded
		if ( amalto.b2box.core.isDefined('window',verifiedObjectName)) {
			if (amalto.b2box.core.isDefined('window','console')) console.log("Script "+scriptID+" already loaded", "info", "amalto.b2box.core");
			if (callbackFollowingTimes != undefined) callbackFollowingTimes.call();
			return;
		}
		//Begin by creating a new Loader instance:
		var loader = new YAHOO.util.YUILoader();
		//add a map to count the number of reies
		var tries = 0;
		//use a verifier method that will verify when the added module is completely loaded.
		function checkScript(name, loaderCallback) {
			if (amalto.b2box.core.isDefined('window','console')) console.log(tries+" - checking if "+scriptID+" is loaded using "+verifiedObjectName, "info", "amalto.b2box.core ");
	        if (! amalto.b2box.core.isDefined('window',verifiedObjectName)) {
	        	//not loaded yet
	        	//check the number of tries;
	        	if (++tries==50) {
	        		alert('ERROR: unable to load the script named: '+name);
	        	} else {
		 			//console.log("Verifier determined thatScript Utility is not yet loaded; will try back in 50ms.", "info", "amalto.b2box.core");
		            setTimeout(
		            	function() {
		                    checkScript(name, loaderCallback);
		                }, 
		                50
		            );
				}	            
	        } else {
	 			if (amalto.b2box.core.isDefined('window','console')) console.log("Verified! "+scriptID+" is loaded.", "info", "core");
				loaderCallback();
	        }
	    }
		//Add the module to YUILoader
	    loader.addModule({
	        name: "amalto.b2box."+scriptID, //module name; must be unique
	        type: "js", //can be "js" or "css"
	        fullpath: amalto.b2box.core.getServerPath()+"/"+src, //can use a fullpath instead, including the server name
	        verifier: checkScript //the verifier function we just defined
			//requires: ['yahoo', 'event'] //if this module had dependencies, we could define here
	    });
		//include the new script
	    loader.require("amalto.b2box."+scriptID); 
		//Insert Script on the page, passing in our callback:
	    loader.insert(callbackFirstTime);
	};
	
	

	/********************************************************************
	 * 
	 * PUBLIC Properties and Methods
	 * 
	 ********************************************************************/
	return  { 
		ROOT_CONTEXT: "b2box3",
		language: "en",
		layout: "",
		
		isDefined: function(objectName, variables){
			d=variables.split(".");
			ev = objectName;
	        for (j= 0; j<d.length; j=j+1) {
	        	ev+="['"+d[j]+"']";
	        }
			return (typeof eval(ev) != 'undefined');
		},
		
		getContextPath: function() {
			return document.getElementById('contextPath').value;
		},
		
		getServerPath: function() {
			return document.getElementById('serverPath').value;
		},
		
		working: function(){
			statusPanel.getEl().removeClass('done');
			statusPanel.setContent('');
		},
	
		ready: function(){
			statusPanel.getEl().addClass('done');
			statusPanel.setContent('');
		},
			
		loadMainScript: function(context, application, callback) {
			var app = application.replace(/\s/g,'');
			var scriptID = context+"."+app;
			var src = '/'+context+'/secure/js/'+app+'.js';
			//register the intermediate context namespace to avoid issues
			amalto.namespace("amalto.b2box."+context);
			loadScript(scriptID,src,"amalto.b2box."+context+"."+app, callback, callback);
		},

		loadDWRScript: function(context, interfaceName, callback) {
			var app = interfaceName;
			var scriptID = "dwr."+context+"."+app;
			var src = '/'+context+'/secure/dwr/interface/'+app+'Interface.js';
			loadScript(scriptID,src,app+"Interface",callback, callback);
		},
		
		init: function () {
			//enable the log to the console
			//YAHOO.widget.Logger.enableBrowserConsole();
 			// redirect to login page when session expired
			DWREngine.setTextHtmlHandler(
				function() {
					window.location.href="/"+amalto.b2box.core.ROOT_CONTEXT+"/secure/";
				}
			);
			initUI();
		},//init()
		
		switchLanguage: function() {
			language = DWRUtil.getValue('languageSelect');
			CommonInterface.setLanguage(
				language,
				function(){
					window.location.href="/"+amalto.b2box.core.ROOT_CONTEXT+"/secure";
				}
			);
			initUI();	
		}
		
	}//PUBLIC
	
}();

 
YAHOO.util.Event.on(window, 'load', amalto.b2box.core.init);



