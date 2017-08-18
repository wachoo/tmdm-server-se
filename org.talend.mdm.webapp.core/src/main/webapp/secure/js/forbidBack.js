// Forbid Backspace button on Keyboard
function forbidBackSpace(e) {
	var ev = e || window.event;
	var obj = ev.target || ev.srcElement;
	var t = obj.type || obj.getAttribute('type');
	var vReadOnly = obj.readOnly;
	var vDisabled = obj.disabled;

	vReadOnly = (vReadOnly == undefined) ? false : vReadOnly;
	vDisabled = (vDisabled == undefined) ? true : vDisabled;

	var flag1 = ev.keyCode == 8
			&& (t == "password" || t == "text" || t == "textarea")
			&& (vReadOnly == true || vDisabled == true);

	var flag2 = ev.keyCode == 8 && t != "password" && t != "text"
			&& t != "textarea";

	if (flag2 || flag1)
		return false;
}

// For Firefox, Opera
document.onkeypress = forbidBackSpace;
// For IE, Chrome
document.onkeydown = forbidBackSpace;

// Forbid Back button of browser
window.onpopstate = function(e) {
	window.history.pushState('forward', null, '');
	window.history.forward(1);
};
// IE needs the following 2 lines
window.history.pushState('forward', null, '');
window.history.forward(1);