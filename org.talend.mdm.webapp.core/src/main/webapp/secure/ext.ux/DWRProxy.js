/**************************************************************************************
 * 					Ext.data.DWRProxy
 * 
 * best practice : used with the ListRangeReader for a grid paged
 * http://extjs.com/forum/showthread.php?t=5586
 **************************************************************************************/

Ext.data.DWRProxy = function(dwrCall, pagingAndSort, dwrCallback){
  Ext.data.DWRProxy.superclass.constructor.call(this);
  this.dwrCall = dwrCall;
  this.dwrCallback = dwrCallback;
  //this.args = args;
	this.pagingAndSort = (pagingAndSort!=undefined ? pagingAndSort : true);
};

Ext.extend(Ext.data.DWRProxy, Ext.data.DataProxy, {
  load : function(params, reader, callback, scope, arg) {
    if(this.fireEvent("beforeload", this, params) !== false) {
      var sort;
      //if(params.sort && params.dir) sort = params.sort + ' ' + params.dir;
      //else sort = '';
      var delegate = this.loadResponse.createDelegate(this, [reader, callback, scope, arg], 1);
      var callParams = new Array();
      if(arg.arg) {
        callParams = arg.arg.slice();
			}
			
			if(this.pagingAndSort) {
      	callParams.push(params.start);
      	callParams.push(params.limit);
      	callParams.push(params.sort);
      	callParams.push(params.dir);
      	if(params.regex!=null&&params.regex!=undefined){
      	callParams.push(params.regex);
      	}
			}
			
      callParams.push(delegate);
      this.dwrCall.apply(this, callParams);
    } else {
      callback.call(scope || this, null, arg, false);
    }
  },

  loadResponse : function(listRange, reader, callback, scope, arg) {
    var result;
    try {
      if (typeof this.dwrCallback == "function"){
          this.dwrCallback(listRange);
      }
      result = reader.read(listRange);
    } catch(e) {
      this.fireEvent("loadexception", this, null, response, e);
      callback.call(scope, null, arg, false);
      return;
    }
    callback.call(scope, result, arg, true);
  },

  update : function(dataSet){},

  updateResponse : function(dataSet)
  {}
});

/**************************************************************************************
 * 					Ext.data.SimpleDWRProxy 
 * 
 * best practice : used with the MapReader for combobox
 * http://extjs.com/forum/showthread.php?t=19529
 **************************************************************************************/
Ext.data.SimpleDWRProxy = function (f) {
	Ext.data.DWRProxy.superclass.constructor.call(this);
	this.func = f;
};

Ext.extend(Ext.data.SimpleDWRProxy, Ext.data.DataProxy, {
	load : function(params, reader, loadCallback, scope, arg) {
		var dataProxy = this;
		dataProxy.fireEvent("beforeload", dataProxy, params);
		var args = [];
		for (var param in params) {
			args[args.length] = params[param];
		}
		args[args.length] = {
			callback: function(response) {
				dataProxy.fireEvent("load", dataProxy, response, loadCallback);
				var records = reader.read(response);
		        loadCallback.call(scope, records, arg, true);
			},
  			exceptionHandler: function(message) {
		    	dataProxy.fireEvent("loadexception", dataProxy, response, loadCallback, e);
		        loadCallback.call(scope, null, arg, false);
		    }
		};
		
		this.func.apply(this, args);
	}
});


/**************************************************************************************
 * 					Ext.data.ListRangeReader
 **************************************************************************************/
 
Ext.data.ListRangeReader = function(meta, recordType){
    Ext.data.ListRangeReader.superclass.constructor.call(this, meta, recordType);
    this.recordType = recordType;
};
Ext.extend(Ext.data.ListRangeReader, Ext.data.DataReader, {
  getJsonAccessor: function(){
      var re = /[\[\.]/;
      return function(expr) {
          try {
              return(re.test(expr))
                  ? new Function("obj", "return obj." + expr)
                  : function(obj){
                      return obj[expr];
                  };
          } catch(e){}
          return Ext.emptyFn;
      };
  }(),
	
	read : function(o){
		var recordType = this.recordType, fields = recordType.prototype.fields;

		//Generate extraction functions for the totalProperty, the root, the id, and for each field
		if (!this.ef) {
			if(this.meta.totalProperty) {
				this.getTotal = this.getJsonAccessor(this.meta.totalProperty);
			}
		
			if(this.meta.successProperty) {
				this.getSuccess = this.getJsonAccessor(this.meta.successProperty);
			}

			if (this.meta.id) {
				var g = this.getJsonAccessor(this.meta.id);
				this.getId = function(rec) {
					var r = g(rec);
					return (r === undefined || r === "") ? null : r;
				};
			} else {
				this.getId = function(){return null;};
			}
			this.ef = [];
			for(var i = 0; i < fields.length; i++){
				f = fields.items[i];
				var map = (f.mapping !== undefined && f.mapping !== null) ? f.mapping : f.name;
				this.ef[i] = this.getJsonAccessor(map);
			}
		}

   	var records = [];
   	var root = o.data, c = root.length, totalRecords = c, success = true;

   	if(this.meta.totalProperty){
	    var v = parseInt(this.getTotal(o), 10);
			if(!isNaN(v)){
				totalRecords = v;
			}
		}

		if(this.meta.successProperty){
			var v = this.getSuccess(o);
			if(v === false || v === 'false'){
				success = false;
			}
		}

		for(var i = 0; i < c; i++){
	    var n = root[i];
      var values = {};
      var id = this.getId(n);
      for(var j = 0; j < fields.length; j++){
				f = fields.items[j];
        var v = this.ef[j](n);						
        values[f.name] = f.convert((v !== undefined) ? v : f.defaultValue);
      }
      var record = new recordType(values, id);
      records[i] = record;
    }

    return {
       success : success,
       records : records,
       totalRecords : totalRecords
    };
  }
});


/**************************************************************************************
 * 					Ext.data.ObjectReader
 **************************************************************************************/
Ext.data.ObjectReader = function(meta, recordType){
    Ext.data.ObjectReader.superclass.constructor.call(this, meta, recordType);
};
Ext.extend(Ext.data.ObjectReader, Ext.data.DataReader, {
    read : function(response){
	var sid = this.meta ? this.meta.id : null;
    	var recordType = this.recordType, fields = recordType.prototype.fields;
    	var records = [];
    	var root = response;
	    for(var i = 0; i < root.length; i++){
		    var obj = root[i];
	        var values = {};
	        var id = obj[sid];
	        
	        for(var j = 0, jlen = fields.length; j < jlen; j++){
                var f = fields.items[j];
                var k = f.mapping !== undefined && f.mapping !== null ? f.mapping : f.name;

                var v = obj[k] !== undefined ? obj[k] : f.defaultValue;
                v = f.convert(v);
                values[f.name] = v;
                
            }
	        var record = new recordType(values, id);
	        records[records.length] = record;
	    }
	    return {
	        records : records,
	        totalRecords : records.length
	    };
    }
});


/**************************************************************************************
 * 					Ext.data.MapReader
 **************************************************************************************/
 
Ext.data.MapReader = function(){
    Ext.data.MapReader.superclass.constructor.call(this, null, [
		{name: 'key', mapping: 'key'},
		{name: 'value', mapping: 'value'}
	]);
};
Ext.extend(Ext.data.MapReader, Ext.data.DataReader, {
    read : function(response) {
    	var records = [];
 		for (var dataItem in response) {
	        var record = new this.recordType({ key: dataItem, value: response[dataItem] }, null);
	        records[records.length] = record;
	    }
	    return {
	        records : records,
	        totalRecords : records.length
	    };
    }
});