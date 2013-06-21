amalto.namespace("amalto.loggingadapter");
		
amalto.loggingadapter.LoggingAdapter = function () {
	
	loadResource("/loggingadapter/secure/dwr/interface/LoggingSmtpInterface.js", "" );

	function displayLoggingAdapterMainPanel() {
		
		var tabPanel = amalto.core.getTabPanel();
		var panel = tabPanel.getItem('logging-main-panel'); 
		if(panel == undefined){		
			panel = new Ext.Panel({
				id:'logging-main-panel',
				title:amalto.loggingadapter.bundle.getMsg('LOGGING_ADAPTER'),
	    		layout:'border',
				autoScroll: false,
				border: false,
				bodyborder: true,
				bodyBorder:false,
				closable: true,
				items:[
					new Ext.Panel({
						id: 'logging-ctrl-panel',
						region:'north',
			    		title: amalto.loggingadapter.bundle.getMsg('SERVICE_CONTROL'),
						border:false,
						bodyBorder:false,
						autoScroll: true,	
						//header:true,
						height:170,		
						split:true,
						labelWidth:150,
						buttonAlign:'left',
						bodyStyle:'padding:5px',						
						html:'<div class="left" >'+amalto.loggingadapter.bundle.getMsg('STATUS')+'</div><div class="leftField" id="status-logging"></div><br/><br/>'+
							'<div class="left">Port</div><div class="leftField"><input type="text" value="" id="log4jport" class="x-form-text x-form-field"/></div><br/><br/>'+
							'<div class="left">'+amalto.loggingadapter.bundle.getMsg('THRESHOLD')+'</div><div class="leftField"><select id="threshold"></select></div><br/><br/>'+
							'<div style="display:none">'+
								  'Xtentis user name<input type="text" value="" id="xtentisusername" size="30"/>'+
								  'Xtentis password<input type="password" id="xtentispassword" value="" size="30"/>'+
								  'log file name<input type="file" value="" id="logfilename" size="30"/>'+
							'</div>'+
							'	  <input type="hidden" value="" id="pattern" size="30"/>',
							//items:[{xtype:'texfield',inputType:'hidden'}],
						buttons:[
							new Ext.Button({
								text:amalto.loggingadapter.bundle.getMsg('START'),
								handler:startLogging
							}),	
							new Ext.Button({
								text:amalto.loggingadapter.bundle.getMsg('STOP'),
								handler:stop
							}),	
							new Ext.Button({
								text:amalto.loggingadapter.bundle.getMsg('STATUS'),
								handler:getStatus
							}),
							new Ext.Button({
								text:amalto.loggingadapter.bundle.getMsg('TRYME'),
								handler:tryMe
							})
						]
					}),
        			new Ext.FormPanel({						
        				id: 'loggingsmtpForm',
        				deferredRender: false,
        				region:'center',
        				closable: true,
        				title: amalto.loggingadapter.bundle.getMsg('SMTP_CONTROL'),
        				border: false,
        				bodyborder: false,
        				autoScroll:true,
        				labelWidth:150,
          				bodyStyle:'padding:15px',
          				buttonAlign:'left',
          				defaultType: 'textfield',
        				defaults: {
        			        // applied to each contained item
        			        width: 300,
        					selectOnFocus: true
        			       // msgTarget: 'side'
        			    },
        				items: [
        						{
        							name: 'loggingsmtpServer',
        							id:'loggingsmtpServer',
        							minLength:2,
        							allowBlank: false,
        							fieldLabel: amalto.loggingadapter.bundle.getMsg('SERVER'),
        							regex:new RegExp("[^ ].+"),
        					    	regexText:amalto.loggingadapter.bundle.getMsg('SERVER_TIP'),
        					    	readOnly:false,
        					    	value:"localhost"
        						},
        						{
        							name: 'loggingsmtpPort',
        							id:'loggingsmtpPort',
        							selectOnFocus: true,
        							minLength:2,
        							allowBlank: false,
        							fieldLabel: amalto.loggingadapter.bundle.getMsg('PORT'),
        							regex:/^[0-9]+$/,
        					    	regexText:amalto.loggingadapter.bundle.getMsg('PORT_TIP'),
        					    	readOnly:false,
        					    	value:"25"
        						},
        						{
        							name: 'loggingsmtpUsername',
        							id:'loggingsmtpUsername',
        							minLength:0,
        							allowBlank: true,
        							fieldLabel: amalto.loggingadapter.bundle.getMsg('USERNAME'),
        							regex:new RegExp("[^ ]{3}.*"),
        					    	regexText:amalto.loggingadapter.bundle.getMsg('USERNAME_TIP'),
        					    	readOnly:false,
        					    	value:""
        						},
        						{
        							name: 'loggingsmtpPassword',
        							id:'loggingsmtpPassword',
        							minLength:0,
        							allowBlank: true,
        							fieldLabel: amalto.loggingadapter.bundle.getMsg('PASSWORD'),
        							regex:new RegExp("[^ ].*"),
        					    	regexText:amalto.loggingadapter.bundle.getMsg('PASSWORD_TIP'),
        					    	readOnly:false,
        					    	value:""
        						},
        						{
        							name: 'loggingsmtpBCC',
        							id:'loggingsmtpBCC',
        							minLength:0,
        							allowBlank: true,
        							fieldLabel: amalto.loggingadapter.bundle.getMsg('BCC'),
        							regex:new RegExp("[^ ].*"),
        					    	regexText:amalto.loggingadapter.bundle.getMsg('BCC_TIP'),
        					    	readOnly:false,
        					    	value:""
        						}
        				],
        	            buttons: [
        	  			    	{
        	  				        text: amalto.loggingadapter.bundle.getMsg('SAVE'),
        	  						handler: function(){
        	  							saveSMTPConfiguration(function(dwrMessage) {
        	  								Ext.MessageBox.show({
        	  							        title:amalto.loggingadapter.bundle.getMsg('SUCCESS'), 
        	  							        msg:amalto.loggingadapter.bundle.getMsg('SMTPSAVED'),
        	  							        width:500,
        	  							        buttons: Ext.Msg.OK
        	  								});			
        	  							}); 
        	  				        }//handler function
        	  			    	}
        	  			    ]
        			})
				]
			});
		    tabPanel.add(panel);  
		    LoggingAdapterInterface.getLog4gPriorities(language, function(result){
		    	DWRUtil.addOptions('threshold',result);
		    });
		}
		panel.show();
		amalto.core.doLayout();
		
	    getConfiguration();
	    loadSMTPConfiguration();
	    

	    
	    
	}
	
	function startLogging() {
		
		var config = {
			port:$('log4jport').value,
			threshold: $('threshold').value,
			pattern:$('pattern').value,
			xtentisusername: $('xtentisusername').value,
			xtentispassword: $('xtentispassword').value,
			logfilename: $('logfilename').value
			
		}	
		LoggingAdapterInterface.start(config, function(status){
			DWRUtil.setValue('status-logging', status);
		});
	}
	
	
	function stop() {
		LoggingAdapterInterface.stop(function(status){
			DWRUtil.setValue('status-logging', status);
		});
	}
	
	function getStatus() {
		LoggingAdapterInterface.getStatus(function(status){
			DWRUtil.setValue('status-logging', status);
		});
	}
	
	function tryMe() {
		LoggingAdapterInterface.tryMe();
	}
	
	
	function getConfiguration(){
	    LoggingAdapterInterface.getConfiguration(function(result){
		    DWRUtil.setValue('log4jport', result.port);
		    //Ext.getCmp('threshold').setValue(result.threshold);
		    DWRUtil.setValue('threshold',result.threshold);
		    DWRUtil.setValue('xtentisusername', result.xtentisusername);
		    DWRUtil.setValue('xtentispassword', result.xtentispassword);
		    DWRUtil.setValue('logfilename', result.logfilename);
		    DWRUtil.setValue('pattern',result.pattern);
			//setTimeout("getStatus();",100);
			getStatus();    	
	    });
	}
	
	
	/*****************************************************
	 *  SMTP Stuff
	 ****************************************************/
	
	function loadSMTPConfiguration(){
        Ext.getCmp('loggingsmtpForm').getForm().doAction(
		    "DWRLoad",
		    {
    			timeout: 20,
    			dwrFunction: LoggingSmtpInterface.loadConfiguration,
    			waitMsg: amalto.loggingadapter.bundle.getMsg('PLEASE_WAIT'),
    			waitTitle: amalto.loggingadapter.bundle.getMsg('LOADING'),							
    			failure:	function(form,action) {
    				if (action.failureType === Ext.form.Action.CONNECT_FAILURE) {
    					Ext.MessageBox.show({
    						title:amalto.loggingadapter.bundle.getMsg('ERROR'), 
    						msg:amalto.loggingadapter.bundle.getMsg('CONNECTION_ERROR')+'\n\n'+action.response,
    						icon:Ext.MessageBox.ERROR,
    						width:500,buttons: Ext.Msg.OK
    						});
    				} else if (action.failureType === Ext.form.Action.LOAD_FAILURE) {
    					Ext.MessageBox.show({
    						title:amalto.loggingadapter.bundle.getMsg('ERROR'), 
    						msg:amalto.loggingadapter.bundle.getMsg('SERVER_ERROR')+': \''+action.dwrMessage+'\'',
    						icon:Ext.MessageBox.ERROR,
    						width:500,buttons: Ext.Msg.OK
    					});
    				} else {
    				    alert(DWRUtil.toDescriptiveString(action,3));
    					Ext.MessageBox.show({
    						title:amalto.loggingadapter.bundle.getMsg('ERROR'),
    						msg:amalto.loggingadapter.bundle.getMsg('UNDEFINED_ERROR'),
    						icon:Ext.MessageBox.ERROR,
    						width:500,buttons: Ext.Msg.OK
    					});
    				}
    			},
    			success: function(form,action) {
    				//do nothing
    			}
    		}
    	);	//submit		
	}

	function saveSMTPConfiguration(callback) {
		Ext.getCmp('loggingsmtpForm').getForm().doAction(
			    "DWRSubmit",
			    {
					timeout: 20,
					dwrFunction: LoggingSmtpInterface.saveConfiguration,
					waitMsg: amalto.loggingadapter.bundle.getMsg('PLEASE_WAIT'),
					waitTitle: amalto.loggingadapter.bundle.getMsg('SAVING'),							
					failure:	function(form,action) {
						if (action.failureType === Ext.form.Action.CLIENT_INVALID) {
							Ext.MessageBox.show({
								title:amalto.loggingadapter.bundle.getMsg('ERROR'), 
								msg:amalto.loggingadapter.bundle.getMsg('FIELDS_DO_NOT_MATCH'),
								icon:Ext.MessageBox.ERROR,
								width:500,buttons: Ext.Msg.OK
								});
						} else if (action.failureType === Ext.form.Action.CONNECT_FAILURE) {
							Ext.MessageBox.show({
								title:amalto.loggingadapter.bundle.getMsg('ERROR'), 
								msg:amalto.loggingadapter.bundle.getMsg('CONNECTION_ERROR')+'\n\n'+action.response,
								icon:Ext.MessageBox.ERROR,
								width:500,buttons: Ext.Msg.OK
								});
						} else if (action.failureType === Ext.form.Action.SERVER_INVALID) {
							Ext.MessageBox.show({
								title:amalto.loggingadapter.bundle.getMsg('ERROR'), 
								msg:amalto.loggingadapter.bundle.getMsg('SERVER_ERROR')+': \''+action.dwrMessage+'\'',
								icon:Ext.MessageBox.ERROR,
								width:500,buttons: Ext.Msg.OK
							});
						} else {
						    alert(DWRUtil.toDescriptiveString(action,3));
							Ext.MessageBox.show({
								title:amalto.loggingadapter.bundle.getMsg('ERROR'), 
								msg:amalto.loggingadapter.bundle.getMsg('UNDEFINED_ERROR'),
								icon:Ext.MessageBox.ERROR,
								width:500,buttons: Ext.Msg.OK
							});
						}
					},
					success: function(form,action) {
						callback.call(action, action.dwrMessage);
					}
				}
			);	//submit						 
		
	}
	

	
 	return {
		init: function() {
			amalto.loggingadapter.bundle = new Ext.i18n.Bundle({bundle:'LoggingAdapterMessages', path:'/loggingadapter/secure/resources', lang:language});
			amalto.loggingadapter.bundle.onReady(function(){
				displayLoggingAdapterMainPanel();
			});
		},
		getStatus: function(){getStatus();},
		start: function(){start();},
		stop: function(){stop();}
 	}
}();
