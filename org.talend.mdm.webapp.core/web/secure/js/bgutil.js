/**
 * Declare a constructor function to which we can add real functions.
 * @constructor
 */
function BGUtil() { }

/**
 * get the the values of the option elements (selected or not) within a select
 */
BGUtil.getOptions = function(ele)
{
    var orig = ele;
    ele = $(ele);
    if (ele == null)
    {
        alert("getOptions() can't find an element with id: " + orig + ".");
        return;
    }

    if (! DWRUtil._isHTMLElement(ele, "select")) {
        alert("getOptions() only work with select elements");
        return;
	}    	

	var reply = new Array();
	for (var i=0; i<ele.options.length; i++)
    {
    	reply[i] = ele.options[i].value;
    }

    return reply;

};
BGUtil.getOptionsSelected = function(ele)
{
    var orig = ele;
    ele = $(ele);
    if (ele == null)
    {
        alert("getOptions() can't find an element with id: " + orig + ".");
        return;
    }

    if (! DWRUtil._isHTMLElement(ele, "select")) {
        alert("getOptions() only work with select elements");
        return;
	}    	

	var reply = new Array();
	for (var i=0; i<ele.options.length; i++)
    {
    	if(ele.options[i].selected==true)
    		reply.push(ele.options[i].value);
    }

    return reply;

};

/**
 * open a new window and write the generated html
 */
BGUtil.newWindow = function(html)
{
	mywin = window.open(
		'',
		'Connector', 
		'toolbar=0,location=0, directories=0,status=0,menubar=0,scrollbars=1,resizable=0,copyhistory=0,width=285,height=255,screenX=500,screenY=100' );
	mywin.document.write(html);

	/*
	var results =document.documentElement.innerHTML;
	var match = "<";
	var re = new RegExp("<", "g");
	var newresults = results.replace(re, "&lt;");
*/
}

BGUtil.useAmaltoLoadingMessage=function(message) {
  var loadingMessage;
  if (message) loadingMessage = message;
  else loadingMessage = "Loading";

  DWREngine.setPreHook(function() {
    var disabledZone = $('disabledZone');
    if (!disabledZone) {
    	var screenX,screenY;
	    if(navigator.appName == "Microsoft Internet Explorer"){
	    	screenY = document.body.offsetHeight
			screenX = window.screen.availWidth
		} else { 
			screenY = window.outerHeight;
			screenX = window.outerWidth;
		}

	    var text = document.createTextNode(loadingMessage);
	    var img = document.createElement('img');
	    img.src='/images/o-logo-32.gif';

	    var messageZone = document.createElement('div');
	    messageZone.setAttribute('id', 'messageZone');
	    messageZone.style.position = "absolute";
	    messageZone.style.top = "0px";
	    messageZone.style.right = "0px";
	    messageZone.style.background = "#E8E8E8";
	    messageZone.style.color = "white";
	    messageZone.style.fontFamily = "Arial,Helvetica,sans-serif";
	    messageZone.style.padding = "4px";
   	    //messageZone.appendChild(text);
   	    messageZone.appendChild(img);

	    disabledZone = document.createElement('div');
	    disabledZone.setAttribute('id', 'disabledZone');
	    disabledZone.style.position = "absolute";
	    disabledZone.style.zIndex = "1000";
   	    disabledZone.style.left = "0px";
	    disabledZone.style.top =  "0px";
	    //disabledZone.style.left = ((screenX/2))+"px";
	    //disabledZone.style.top =  ((screenY/2))+"px";
	    disabledZone.style.width = "100%";
	    disabledZone.style.height = "100%";
	    disabledZone.appendChild(messageZone);
	    
	    document.body.appendChild(disabledZone);
	    
   }  else {
      $('messageZone').innerHTML = loadingMessage;
      disabledZone.style.visibility = 'visible';
    }
  });

  DWREngine.setPostHook(function() {
    $('disabledZone').style.visibility = 'hidden';
  });
}


/*
var reqXML;
    
function LoadXMLDoc(callback, url){ 
  if (window.XMLHttpRequest){ //Mozilla, Firefox, Opera 8.01, Safari
    reqXML = new XMLHttpRequest(); 
    reqXML.onreadystatechange = BuildXMLResults(reqXML); 
    reqXML.open("GET", url, true); 
    reqXML.send(null); 
  }
  else if(window.ActiveXObject){ //IE
    reqXML = new ActiveXObject("Microsoft.XMLHTTP"); 
    if (reqXML) { 
      reqXML.onreadystatechange = BuildXMLResults(reqXML); 
      reqXML.open("GET", url, true); 
      reqXML.send(); 
    } 
  }
  else{ //Older Browsers
    alert("Your Browser does not support Ajax!");
  }
} 

function BuildXMLResults(reqXML){
  if(reqXML.readyState == 4){ //completed state
    if(reqXML.status != 200){ //We got an error page
               //display server code not be accessed
      alert("There was a problem retrieving the XML data:\n" + reqXML.statusText);
    }		
    //reqXML.responseText
  }
}
*/