function AutoScroller(scroller) {
    // get the height of the viewport.
    // See http://www.howtocreate.co.uk/tutorials/javascript/browserwindow
    function getViewportHeight() {
        if (typeof (window.innerWidth) == 'number') {
            // Non-IE
            return window.innerHeight;
        } else if (document.documentElement
                && (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
            // IE 6+ in 'standards compliant mode'
            return document.documentElement.clientHeight;
        } else if (document.body
                && (document.body.clientWidth || document.body.clientHeight)) {
            // IE 4 compatible
            return document.body.clientHeight;
        }
        return null;
    }

    return {
        bottomThreshold : 25,
        scrollContainer : scroller,

        getCurrentHeight : function() {
            return scroller.scrollHeight;
        },

        isSticking : function() {
            var scrollDiv = this.scrollContainer;
            var currentHeight = this.getCurrentHeight();

            var height = getViewportHeight();
            var diff = currentHeight - scrollDiv.scrollTop - height;

            return diff < this.bottomThreshold;
        },

        scrollToBottom : function() {
            var scrollDiv = this.scrollContainer;
            scrollDiv.scrollTop = this.getCurrentHeight();
        }
    };
}

var logToggle;
var logSpinner;
var logContent;
var logScroller;
var logUrl;
var FETCH_PERIOD = 1000;
var MAX_LINES = 5000;

function initLog(url, content, scroller, spinner, toggle) {
    logUrl = url;
    logContent = content;
    logScroller = new AutoScroller(scroller);
    logSpinner = spinner;
    logToggle = toggle;
    logContent.position = -1; //tail by default
    logContent.lines = 0;
    startLoading();
}

function toggleLoading() {
    if (logContent.stopLoading == "true") {
        startLoading();
    } else {
        stopLoading();
    }
}

function startLoading() {
    logContent.stopLoading = "false";
    logToggle.value = "Pause";
    logSpinner.style.display = "";
    loadLog();
}

function stopLoading() {
    logContent.stopLoading = "true";
    logToggle.disable();
}

function onStopLoading() {
    logToggle.value = "Resume";
    logToggle.enable();
    logSpinner.style.display = "none";
    logContent.stopLoading = "true";
}

function loadLog() {
    new Ajax.Request(logUrl, {
        method : "get",
        parameters : {
            "position" : logContent.position,
            "maxLines" : MAX_LINES
        },
        requestHeaders : {},
        onComplete : function(rsp, _) {
            var rspStatus = rsp.getStatus();
            if (rspStatus == 200) {
                var rspPosition = rsp.getHeader("X-Log-Position");
                if (rspPosition != null) { // just in case??
                    logContent.position = rspPosition;
                    var stickToBottom = logScroller.isSticking();

                    var lines = parseInt(rsp.getHeader("X-Log-Lines"));
                    if (logContent.lines + lines > MAX_LINES)
                        clearLog();
                    logContent.lines = logContent.lines + lines;

                    var text = rsp.responseText;
                    if (text != "") {
                        var p = document.createElement("DIV");
                        logContent.appendChild(p);
                        if (p.outerHTML) {
                            p.outerHTML = '<pre>' + text.escapeHTML()
                                    + '</pre>';
                            p = logContent.lastChild;
                        } else
                            p.innerHTML = text.escapeHTML();
                        if (stickToBottom)
                            logScroller.scrollToBottom();
                    }
                }

                if (logContent.stopLoading != "true")
                    setTimeout(function() {
                        loadLog();
                    }, FETCH_PERIOD);
                else
                    onStopLoading();
            } else {
                // Stop when encountering a failure
                onStopLoading();
                alert("An error occured while retrieving log!");
            }
        }
    });
}

function clearLog() {
    while (logContent.hasChildNodes()) {
        logContent.removeChild(logContent.lastChild);
    }
    logContent.lines = 0;
}