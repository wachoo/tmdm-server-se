Ext.namespace("com.amalto.dwr");

/**
 * @class Ext.form.Action.DWRSubmit
 * @extends Ext.form.Action
 * A class which handles submission of data from {@link Ext.form.BasicForm Form}s through DWR
 * and processes the returned response.
 */
com.amalto.dwr.DWRSubmit = function(form, options){
    com.amalto.dwr.DWRSubmit.superclass.constructor.call(this, form, options);
};

Ext.extend(com.amalto.dwr.DWRSubmit, Ext.form.Action, {
    /**
    * @cfg {boolean} clientValidation Determines whether a Form's fields are validated
    * in a final call to {@link Ext.form.BasicForm#isValid isValid} prior to submission.
    * Pass <tt>false</tt> in the Form's submit options to prevent this. If not defined, pre-submission field validation
    * is performed.
    */
    type : 'DWRSubmit',
    
	/**
	 * @cfg {Function} dwrFunction The DWR function for this action calls during submit.
	 */
	dwrFunction: null,
	/**
	 * @cfg {String} dwrAdditional Additional parameters to be passed to the DWR function call on top of the Form fields and values
	 */
	dwrAdditional: null,
	/**
	 * @cfg {String} dwrmessage The message returned by the DWR Class ExtJSFormXXXResponse
	 */
	dwrMessage: '',
	
	
    // private
    run : function(){
		var action = this;
        var o = this.options;
        if(o.clientValidation === false || this.form.isValid()){
        	//calculate timeout - zero is no timeout for DWR
    	    var timeOut = (o.timeout && o.timeout*1000) || 0;
    	    //build the parameters passed to DWR as the for value + the additional parameters
    	    var parameters = this.form.getValues();
		    if (o.dwrAdditional != null) {
	    		if (o.dwrAdditional instanceof Array) {
	    			//we only send an array if we do not send form values
	    			if (parameters==null) {
	    				parameters = new Array();
		    			for (var i = 0; i < o.dwrAdditional.length; i++) {
		    				parameters.push(o.dwrAdditional[i]);
		    			}
	    			}
	    		} else {
	    			if (parameters==null) parameters = {};
	    			for (var key in o.dwrAdditional) {
	    				parameters[key]=o.dwrAdditional[key]
	    			}
	    		}
		    }
    	    //call the dwrFunction
        	o.dwrFunction.apply(Object, [
    			parameters,
				{
	 				async: true,
	 				callback: function(response) {
    					action.dwrMessage = response.message;
    					action.success.call(action,response);
    				},
					timeout: timeOut,
	 				errorHandler: function(response) {
    					action.failure.call(action,response);
    				}
	 			}
        	]);	
        }else if (o.clientValidation !== false){ // client validation failed
            this.failureType = Ext.form.Action.CLIENT_INVALID;
            this.form.afterAction(this, false);
        }
    },

    // private
    success : function(result){
    	if (result.data) {
            this.dwrData = result.data;
    	}
        if(result === true || result.success){
            this.form.afterAction(this, true);
            return;
        }
        if(result.errors){
            this.form.markInvalid(result.errors);
            this.failureType = Ext.form.Action.SERVER_INVALID;
        }
        this.form.afterAction(this, false);
    }
});


/**
 * @class com.amalto.dwr.DWRLoad
 * @extends Ext.form.Action
 * A class which handles loading of data from a server into the Fields of
 * an {@link Ext.form.BasicForm} through DWR.
 */
com.amalto.dwr.DWRLoad = function(form, options){
    com.amalto.dwr.DWRLoad.superclass.constructor.call(this, form, options);
    this.reader = this.form.reader;
};

Ext.extend(com.amalto.dwr.DWRLoad, Ext.form.Action, {
    // private
    type : 'DWRLoad',

	/**
	 * @cfg {Function} dwrFunction The DWR function for this action calls during submit.
	 */
	dwrFunction: null,
	/**
	 * @cfg {String} dwrAdditional Additional parameters to be passed to the DWR function call on top of the Form fields and values
	 */
	dwrAdditional: null,
	/**
	 * @cfg {String} dwrSendValues true to submit form values. Defaults to false
	 */
	dwrSendValues: false,
	/**
	 * @cfg {String} dwrmessage The message returned by the DWR Class ExtJSFormXXXResponse
	 */
	dwrMessage: '',
    
    // private
    run : function(){
		var action = this;
	    var o = this.options;
	    //calculate timeout - zero is no timeout for DWR
	    var timeOut = (o.timeout && o.timeout*1000) || 0;
  	    //build the parameters passed to DWR as the for value + the additional parameters
  	    var parameters = null;
  	    if(o.dwrSendValues)
	    	parameters = this.form.getValues();
	    if (o.dwrAdditional != null) {
    		if (o.dwrAdditional instanceof Array) {
    			//we only send an array if we do not send form values
    			if (parameters==null) {
    				parameters = new Array();
	    			for (var i = 0; i < o.dwrAdditional.length; i++) {
	    				parameters.push(o.dwrAdditional[i]);
	    			}
    			}
    		} else {
    			if (parameters==null) parameters = {};
    			for (var key in o.dwrAdditional) {
    				parameters[key]=o.dwrAdditional[key]
    			}
    		}
	    }
	    //call the dwrFunction
		o.dwrFunction.apply(Object, [
 			parameters,
			{
 				async: true,
 				callback: function(response) {
 					action.dwrMessage = response.message;
 					action.success.call(action,response);
 				},
				timeout: timeOut,
 				errorHandler: function(response) {
 					action.failure.call(action,response);
 				}
 			}
     	]);
    },

    // private
    success : function(result){
    	 if (result.data) {
            this.dwrData = result.data;
    	}
        if(!result.success || !result.data){
            this.failureType = Ext.form.Action.LOAD_FAILURE;
            this.form.afterAction(this, false);
            return;
        }
        this.form.clearInvalid();
        this.form.setValues(result.data);
        this.form.afterAction(this, true);
    }
});

Ext.form.Action.ACTION_TYPES.DWRSubmit= com.amalto.dwr.DWRSubmit;
Ext.form.Action.ACTION_TYPES.DWRLoad= com.amalto.dwr.DWRLoad;

