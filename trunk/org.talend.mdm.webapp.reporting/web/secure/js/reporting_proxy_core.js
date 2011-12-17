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

if(![].indexOf){
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

String.prototype.endWith=function(str){
    if(str==null||str==""||this.length==0||str.length>this.length)
      return false;
    if(this.substring(this.length-str.length)==str)
      return true;
    else
      return false;
    return true;
};

String.prototype.startWith=function(str){
    if(str==null||str==""||this.length==0||str.length>this.length)
      return false;
    if(this.substr(0,str.length)==str)
      return true;
    else
      return false;
    return true;
};


var g_oHtmlEncodeElement;

function htmlEscape(text)
{
    g_oHtmlEncodeElement = g_oHtmlEncodeElement || document.createElement("div");
    g_oHtmlEncodeElement.innerText = g_oHtmlEncodeElement.textContent = text;
    return g_oHtmlEncodeElement.innerHTML;
}

function htmlUnescape(html)
{
    g_oHtmlEncodeElement = g_oHtmlEncodeElement || document.createElement("div");
    g_oHtmlEncodeElement.textContent = g_oHtmlEncodeElement.innerText = "";
    g_oHtmlEncodeElement.innerHTML = html;
    return g_oHtmlEncodeElement.innerText || g_oHtmlEncodeElement.textContent;
}

function isArray(value) {
      return value &&
      typeof value === 'object' &&
      typeof value.length === 'number' &&
      typeof value.splice === 'function' &&
      !(value.propertyIsEnumerable('length'));
}

Ext.namespace("Ext.ux");
Ext.ux.comboBoxRenderer = function(combo) {
  return function(value) {
    var idx = combo.store.find(combo.valueField, value);
    var rec = combo.store.getAt(idx);
	return (rec == null ? '' : rec.get(combo.displayField) );    
  };
}

Ext.ux.LocaleMap = function(M) {
	   this.map = M || {};
	};
Ext.extend(Ext.ux.LocaleMap, Ext.util.Observable, {
	   get : function(key) {
	      //var value = this.map[key] || (key + ' not found!');
	      var value = this.map[key] || ''; 
	      if(arguments.length > 1 && value.toString().indexOf('{') >= 0) {
	         value = new Ext.Template(value).apply(Array.prototype.slice.call(arguments, 1));
	      }
	      return value;
	   }
});

Ext.BLANK_IMAGE_URL = '/core/secure/ext-2.2/resources/images/default/s.gif';

var LOGOUT = {
	'fr':'D&eacute;connexion',
	'en':'Logout'
}

var LOADING={
	'fr':'Chargement...',
	'en':'Loading...'
}

var CLOSECONFIRM={
	'fr':'Si vous fermez l\'onglet maintenant, vos modifications seront perdues. Êtes-vous sûr de vouloir continuer ?',
	'en':'If you close the tab now, your changes will be lost. Are you sure you want to proceed?'
}

/********************************************************************
* Hot Loads a Resource
*********************************************************************/
function loadScript(scriptID, src, verifiedObjectName, callbackFirstTime, callbackFollowingTimes) {
	//log("loadScript loading "+src);		
	//first check if it already loaded
	if ( isDefined('window',verifiedObjectName)) {
		//log("Script "+scriptID+" already loaded");
		if (callbackFollowingTimes != undefined) callbackFollowingTimes.call();
		return;
	}
	//Begin by creating a new Loader instance:
	var loader = new YAHOO.util.YUILoader();
    //log("Trying to fetch "+getServerPath()+"/"+src);
	//Add the module to YUILoader
    loader.addModule({
        name: scriptID, //module name; must be unique
        type: "js", //can be "js" or "css"
        fullpath: "../../../../"+src, //can use a fullpath instead
        varName: verifiedObjectName //replaces the verifier function in 2.4.0
        //verifier: checkScript //deprecated for 2.4.0  - the verifier function we just defined
		//requires: ['yahoo', 'event'] //if this module had dependencies, we could define here
    });
	//include the new script
    loader.require(scriptID); 
    //new for 2.4.0 - specify call back function
    loader.onSuccess = callbackFirstTime;
	//Insert Script on the page, passing in our callback:
    loader.insert(); //do not pass callback in 2.4.0 - use onSuccess
};


/**
 * Synchronously Load a Javascript Or Css 
 */
function loadResource(pathFromRootContext,verifiedObjectName) {
	log('Load resource '+pathFromRootContext);
	var type ='html';
	if(pathFromRootContext.substr(pathFromRootContext.length-3,3) == 'css' ) type = 'css';
	if(pathFromRootContext.substr(pathFromRootContext.length-2,2) == 'js' ) type = 'js';
	var completed = false; 

	function loaderCallback() {
		completed = true;
	}
	
	function load() {
		if ( (type=='js') && isDefined('window',verifiedObjectName)) return;
		//Begin by creating a new Loader instance:
		var loader = new YAHOO.util.YUILoader();
		//Add the module to YUILoader
	    loader.addModule({
	        name: pathFromRootContext, //module name; must be unique
	        type: type,
	        fullpath: "../../../../"+pathFromRootContext, //can use a fullpath instead
	        varName: verifiedObjectName //replaces the verifier function in 2.4.0 - ignored if css
	    });
		//include the new script
	    loader.require(pathFromRootContext); 
	    //new for 2.4.0 - specify call back function
	    loader.onSuccess = loaderCallback;
		//Insert Script on the page, passing in our callback:
	    loader.insert(); //do not pass callback in 2.4.0 - use onSuccess
	}
	
	function wait() {
		if (! completed) setTimeout(wait, '200');
	} 
	
	load();
	wait();
};

//Synchronously Load a Javascript Or Css with callback

function loadResource(pathFromRootContext,verifiedObjectName,loaderCallback) {
	//log('Load resource '+pathFromRootContext);
	var type ='html';
	if(pathFromRootContext.substr(pathFromRootContext.length-3,3) == 'css' ) type = 'css';
	if(pathFromRootContext.substr(pathFromRootContext.length-2,2) == 'js' ) type = 'js';
	
	function loaderFailure() {
		Ext.Msg.alert("ERROR",  "There was an error loading resource from server side! ")
	}
	
	function load() {
		if ( (type=='js') && isDefined('window',verifiedObjectName)) return;
		//Begin by creating a new Loader instance:
		var loader = new YAHOO.util.YUILoader();
		//Add the module to YUILoader
	    loader.addModule({
	        name: pathFromRootContext, //module name; must be unique
	        type: type,
	        fullpath: "../../../../"+pathFromRootContext, //can use a fullpath instead
	        varName: verifiedObjectName //replaces the verifier function in 2.4.0 - ignored if css
	    });
		//include the new script
	    loader.require(pathFromRootContext); 
	    //new for 2.4.0 - specify call back function
	    loader.onSuccess = loaderCallback;
	    loader.onFailure = loaderFailure;
		//Insert Script on the page, passing in our callback:
	    loader.insert(); //do not pass callback in 2.4.0 - use onSuccess
	}
	
	load();
};


/********************************************************************
* Utilities / defined / Local
*********************************************************************/				


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

function initLocaleMap(language,locales){
	  var localesMap = new Ext.ux.LocaleMap(locales);
	  var localeMap = new Ext.ux.LocaleMap(localesMap.get(language));
	  return localeMap;
	}
		
				
/********************************************************************
* Logging Stuff
*********************************************************************/				

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
   /* if ((debugWindow == null) || (debugWindow.closed)) {
      debugWindow = window.open("","debugconsole","scrollbars=yes,resizable=yes,height=100,width=300");
      debugWindow.document.open("text/html", "replace");
    }
    debugWindow.document.writeln('<br/>'+msg);
    debugWindow.scrollTo(0,10000);
    debugWindow.focus();
    // debugWindow.document.close();  // uncomment this if you want to see only last message , not all the previous messages
     */
};


/********************************************************************
 * amalto Namespace
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
  * The namespace of a module is amalto.b2box.modulename where modulename is the Application Name stripped of spaces 
 */
 
//amalto.namespace("amalto");


/**
 * See the YAHOO Module pattern @  http://yuiblog.com/blog/2007/06/12/module-pattern/
 */
 
amalto.core = function () {

	/********************************************************************
	 * 
	 * PRIVATE Properties and Methods
	 * 
	 ********************************************************************/
	

	/************
	* Layout
	*************/
	var contentPanel;
	
	var tabPanel = function(){
		var tabs = {};
		return {
			getItem: function (id){
				return tabs[id];
			},
			add: function(comp){
				var id = comp.getItemId();
				tabs[id] = comp;
				contentPanel.add(comp);
			}
		}
	}();
	
	function initUI() {
		if (window.parent.getLanguage){
			language = window.parent.getLanguage();
		} else {
			language = "en";	
		}
		// initialize state manager, we will use cookies
		Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
		
		// Init the Quick Tips.  Any tag-based quick tips will start working.
		Ext.QuickTips.init();

		// Apply a set of config properties to the singleton
		Ext.apply(Ext.QuickTips.getQuickTip(), {
		    maxWidth: 200,
		    minWidth: 100,
		    showDelay: 50,
		    trackMouse: false
		});

		contentPanel = new Ext.Panel({
			layout : 'fit',
			renderTo : "centerdiv"
		});
		
		contentPanel.setSize(document.body.clientWidth, document.body.clientHeight);

		window.onresize = function (){
			contentPanel.setSize(document.body.clientWidth, document.body.clientHeight);
		}
		
		loadWelcome();

		contentPanel.doLayout(); 
	}


	function loadWelcome() {
		amalto.core.loadMainScript("reporting", "Reporting",
				function() {
					var initFunction = "amalto.reporting.Reporting.init()";
					setTimeout(initFunction,'50');
				}
		);
	}

	/********************************************************************
	 * 
	 * PUBLIC Properties and Methods
	 * 
	 ********************************************************************/
	return  { 
		ROOT_CONTEXT: "core",
		language: "en",
		LOG_LEVEL: "DEBUG", //DEBUG,  INFO,  NONE
		layout: "",
		debugWindow: null,

		/*************************
	 	* Status Display
	 	*************************/
		
		working: function(message){
			Ext.get('statusdiv').removeClass('ready');
			Ext.get('statusdiv').addClass('working');
			Ext.get('statusdiv').update(message);
			//Ext.get('status').insertAfter("beforeEnd",'<p>'+message+'</p>');
		},
		ready: function(message){
			if ((message==null) || (message=='')) message='';
			Ext.get('statusdiv').removeClass('working');
			Ext.get('statusdiv').addClass('ready');
			Ext.get('statusdiv').update(message);
			setTimeout(function() {Ext.get('status').update('');},4000);
			//Ext.get('status').insertAfter("beforeEnd",'<p>'+message+'</p>');
		},	
			
		loadMainScript: function(context, application, callback) {
			var app = application.replace(/\s/g,'');
			var scriptID = context+"."+app;
			var src = '/'+context+'/secure/js/'+app+'.js';
			//register the intermediate context namespace to avoid issues
			amalto.namespace("amalto."+context);
			loadScript(scriptID,src,"amalto."+context+"."+app, callback, callback);
		},

		loadDWRScript: function(context, interfaceName, callback) {
			var app = interfaceName;
			var scriptID = "dwr."+context+"."+app;
			var src = '/'+context+'/secure/dwr/interface/'+app+'Interface.js';
			loadScript(scriptID,src,app+"Interface",callback, callback);
		},
		
		/**
		 * Returns the center Tab Panel component
	 	*/
		getTabPanel: function() {
			return tabPanel;
		},
		
		/**
		 * Forces the wiewport to re layout
	 	*/
		doLayout: function() {
			contentPanel.doLayout();
		},
		
		
		init: function () {
			initUI();
		}
		
	}//PUBLIC
	
}();

 
YAHOO.util.Event.on(window, 'load', amalto.core.init);



