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

function toggleLoading(button, spinner, logContent) {
    if (logContent.stopLoading == "true") {
        logContent.stopLoading = "false";
        button.value = "Stop Loading";
        loadLog(logContent, spinner, "log");
    } else {
        logContent.stopLoading = "true";
        button.value = "Load";
    }
}

function loadLog(logContent, spinner, url) {
    var headers = {};
    spinner.style.display = "";
    new Ajax.Request(url, {
        method : "get",
        parameters : {
            "position" : logContent.position
        },
        requestHeaders : headers,
        onComplete : function(rsp, _) {

            var stickToBottom = scroller.isSticking();
            var text = rsp.responseText;
            if (text != "") {
                var p = document.createElement("DIV");
                logContent.appendChild(p);
                if (p.outerHTML) {
                    p.outerHTML = '<pre>' + text.escapeHTML() + '</pre>';
                    p = logContent.lastChild;
                } else
                    p.innerHTML = text.escapeHTML();
                if (stickToBottom)
                    scroller.scrollToBottom();
            }
            logContent.position = rsp.getResponseHeader("X-Log-Position");
            if (rsp.getResponseHeader("X-Log-Load") == "true"
                    && logContent.stopLoading != "true")
                setTimeout(function() {
                    loadLog(logContent, spinner, url);
                }, 1000);
            else
                spinner.style.display = "none";
        }
    });
}