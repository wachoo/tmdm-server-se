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

String.prototype.trim = function () {
	return this.replace(/^\s*(\S*(\s+\S+)*)\s*$/, "$1");
};


/********************************************************************
* Hot Loads a Resource
*********************************************************************/
/**
 * 
 */
function loadScript(scriptID, src, verifiedObjectName, callbackFirstTime, callbackFollowingTimes) {
	log("loadScript loading "+src);		
	//first check if it already loaded
	if ( isDefined('window',verifiedObjectName)) {
		log("Script "+scriptID+" already loaded");
		if (callbackFollowingTimes != undefined) callbackFollowingTimes.call();
		return;
	}
	//Begin by creating a new Loader instance:
	var loader = new YAHOO.util.YUILoader();
	//add a map to count the number of reies
	var tries = 0;
	//use a verifier method that will verify when the added module is completely loaded.
	function checkScript(name, loaderCallback) {
		log(tries+" - checking if "+scriptID+" is loaded using "+verifiedObjectName);
        if (! isDefined('window',verifiedObjectName)) {
        	//not loaded yet
        	//check the number of tries;
        	if (++tries==50) {
        		alert('ERROR: unable to load the script named: '+name);
        	} else {
	 			//console.log("Verifier determined thatScript Utility is not yet loaded; will try back in 50ms.", "info", "");
	            setTimeout(
	            	function() {
	                    checkScript(name, loaderCallback);
	                }, 
	                500
	            );
			}	            
        } else {
 			log("Verified! "+scriptID+" is loaded.");
			loaderCallback();
        }
    }
    log("Trying to fetch "+getServerPath()+"/"+src);
	//Add the module to YUILoader
    loader.addModule({
        name: scriptID, //module name; must be unique
        type: "js", //can be "js" or "css"
        fullpath: getServerPath()+"/"+src, //can use a fullpath instead, including the server name
        verifier: checkScript //the verifier function we just defined
		//requires: ['yahoo', 'event'] //if this module had dependencies, we could define here
    });
	//include the new script
    loader.require(scriptID); 
	//Insert Script on the page, passing in our callback:
    loader.insert(callbackFirstTime);
};

 function getContextPath() {
	return document.getElementById('contextPath').value;
};

function getServerPath() {
	return document.getElementById('serverPath').value;
};

		
function isDefined(objectName, variables){
	d=variables.split(".");
	ev = objectName;
    for (j= 0; j<d.length; j=j+1) {
    	ev+="['"+d[j]+"']";
    }
	return (typeof eval(ev) != 'undefined');
};
		
var debugWindow = null;
				
function log(msg) {
	log(msg,"debug");
};

function log(msg,level) {
	if (isDefined('window','console')) {
		console.log(msg,level,'');
		return;
	}
	//for less capable browsers....
    if ((debugWindow == null) || (debugWindow.closed)) {
      debugWindow = window.open("","debugconsole","scrollbars=yes,resizable=yes,height=100,width=300");
      debugWindow.document.open("text/html", "replace");
    }
    debugWindow.document.writeln('<br/>'+msg);
    debugWindow.scrollTo(0,10000);
    debugWindow.focus();
    // debugWindow.document.close();  // uncomment this if you want to see only last message , not all the previous messages
};

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
amalto.namespace("amalto");

/**
  * The namespace of a module is amalto.b2een.modulename where modulename is the Application Name stripped of spaces 
 */

amalto.namespace("amalto.core");

/**
 * See the YAHOO Module pattern @  http://yuiblog.com/blog/2007/06/12/module-pattern/
 */

amalto.core = function () {

	//The status panel at the bottom
	var statusPanel = null;

	//Message NO TRANSFORMER
	var MSG_NETWORK_CONNECTED= {
		en: 'Connected',
		fr: 'Connecté'
	};
	var MSG_NETWORK_NOT_CONNECTED= {
		en: 'Connected',
		fr: 'Connecté'
	};



	/********************************************************************
	 * Layout
	 ********************************************************************/
	
	function initUI() {



        Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
        
       var viewport = new Ext.Viewport({
            layout:'border',
            items:[
                new Ext.BoxComponent({ // raw
                    region:'north',
                    el: 'north',
                    height:32
                }),{
                    region:'south',
                    contentEl: 'south',
                    split:true,
                    height: 100,
                    minSize: 100,
                    maxSize: 200,
                    collapsible: true,
                    title:'South',
                    margins:'0 0 0 0'
                }, {
			        title: 'Actions',
			        xtype: 'panel',
			        region: 'east',
			        contentEl: 'actions', 
			       	width: 150,
			        titlebar: true,
			        collapsible: true,
			        animCollapse: true,   
			        split: true //not documented: renders the panel resizeable
			    },{
			        title: 'Menus',
			      //  xtype: 'panel',
			        region: 'west',
			        contentEl: 'menus', 
			       	width: 150,
			        titlebar: true,
			        collapsible: true,
			        animCollapse: true,
			        split: true //not documented: renders the panel resizeable
			    },
                new Ext.TabPanel({
                    region:'center',
                    deferredRender:false,
                    activeTab:0,
                    items:[{
                        contentEl:'center1',
                        title: 'Close Me',
                        closable:true,
                        autoScroll:true
                    },{
                        contentEl:'center2',
                        title: 'Center Panel',
                        autoScroll:true
                    }]
                })
             ]
        });
        Ext.get("hideit").on('click', function() {
           var w = Ext.getCmp('west-panel');
           w.collapsed ? w.expand() : w.collapse(); 
        });


//loadActions();
	       
	}


	/********************
	 * Actions/Menus handling
	 ********************/
	var MENUS;

	/*
	 * Load Actions/Menus
	*/	
	function loadMenus() {
		var menus = new Array();
		LayoutInterface.getMenus(
			"en",
			function(result){
				var menus = eval("(" +result+")");
				
				var pmenus = Ext.get('menus');
			    // delegate clicks on the lists
			    pmenus.mon('click', menuClicked);
			    
			    //create menus template
			    var tplClosed = new Ext.Template(
			    	'<a id="menu-{id}" href="{url}"><span id="menu-{id}-span" class="body">{name}<br><span class="desc">{desc}</span></span></a>'+
			    	'<div id="menu-{id}-div" class="sub-body" style="display: none;"/>'
			    );
			   	var tplNone = new Ext.Template(
			    	'<a id="menu-{id}" href="{url}"><span class="body">{name}<br><span class="desc">{desc}</span></span></a>'
			    );
			    
			    var previousLevel = 1;
			    var doms = new Array();
			    doms[0] = pmenus.dom;
	  
			    // go through the menus and add them to the list
			    for(var index=0; index<menus.length; index++) {
			    	var menu = menus[index];
			    	if (menu.application=='') { 		    		//a menu with submenus
			    		tplClosed.append(doms[menu.level-1], menu);
			    		//update doms
			    		var divId = 'menu-'+menu.id+'-div';
			    		doms[menu.level] =Ext.get(divId).dom;
			    		//make IE display the image centered by loading the image now (rather than on initialisation)
			    		$("menu-"+menu.id+"-span").style.background="url(img/b2box/tree-closed.gif) no-repeat 0px 9px;";
			    	} else {
			    		tplNone.append(doms[menu.level-1], menu);
			    	}
			    }
				 MENUS = menus;			
			}
		);
	}//loadMenus
	
	//var centerPanel = null;
	 
	
	/*
	 * The Action itself
	 */
	var menuClicked = function(e) {
		//stop defauklt bhaviour
		e.preventDefault();
		// find the "a" element that was clicked
		var a = e.getTarget('a',false);
		if(a){
			
			//update display of menu in menu Panel
			YAHOO.util.Dom.removeClass(Ext.get('menus').dom.getElementsByTagName('a'), 'selected');
			YAHOO.util.Dom.addClass(a.id, 'selected');  //.substr(5)
			
			//run the menu associated
			
			var menuId = a.id.substr(5);
			var menu = MENUS[menuId-1];
			
			if (menu.application == '') {
				//A menu Holder
				if ($("menu-"+menu.id+"-div").style.display=="none") {
					$("menu-"+menu.id+"-div").style.display="block";
					$("menu-"+menu.id+"-span").style.background="url(img/b2box/tree-opened.gif) no-repeat 0px 9px;";
				} else {
					$("menu-"+menu.id+"-div").style.display="none";
					$("menu-"+menu.id+"-span").style.background="url(img/b2box/tree-closed.gif) no-repeat 0px 9px;";				
				}
			}else {
				//load the script if necessary
				amalto.b2box.core.loadMainScript(
					menu.context,
					menu.application,
					function() {
						var initFunction = "amalto.b2box."+menu.context+"."+menu.application.replace(/\s/g,'')+".init()";
						setTimeout(initFunction,'50');
					}
				);			
			}//else   
		}//findTarget
	}
	

	/********************************************************************
	 * 
	 * PUBLIC Properties and Methods
	 * 
	 ********************************************************************/
	return  { 
		ROOT_CONTEXT: "b2box3",
		language: "en",
		LOG_LEVEL: "DEBUG", //DEBUG,  INFO,  NONE
		layout: "",
		debugWindow: null,

		
		/*************************
	 	* Status Display
	 	*************************/
		
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
		
		/*************************
	 	* Initialization
	 	*************************/
		
		logout:  function() {
			document.location.reload;
		},
		
		/**
		 * Returns the center Tab Panel component
	 	*/
		getTabPanel: function() {
			return mainViewport.getComponent('centerTabPanel');
		},
		
		/**
		 * Forces the wiewport to re layout
	 	*/
		doLayout: function() {
			mainViewport.doLayout();
		},
		
		
		init: function () {
			alert("test change");
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

//Ext.EventManager.onDocumentReady(amalto.core.init, amalto.core, true);

YAHOO.util.Event.on(window, 'load', amalto.core.init);