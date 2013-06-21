amalto.namespace("amalto.smtp");

		
amalto.smtp.SmtpAdapter = function () {
	
	loadResource("/smtp/secure/dwr/interface/SmtpAdapterInterface.js", "" );
	
	loadResource("/smtp/secure/js/tryform.js", "" );

	function displaysmtpAdapterMainPanel() {
	

		var tabPanel = amalto.core.getTabPanel();
		var panel = tabPanel.getItem('smtpForm');
		if( panel == undefined){		
			panel = new Ext.FormPanel({						
				id: 'smtpForm',
				deferredRender: false,
				closable: true,
				title: amalto.smtp.bundle.getMsg('SMTP_ADAPTER'),
				border: false,
				bodyborder: false,
				autoScroll:true,
				labelWidth:150,
  				bodyStyle:'padding:15px',
  				defaultType: 'textfield',
  				buttonAlign:"left",
				defaults: {
			        // applied to each contained item
			        width: 300,
					selectOnFocus: true
			       // msgTarget: 'side'
			    },
				items: [
						{
							name: 'smtpServer',
							id:'smtpServer',
							minLength:2,
							allowBlank: false,
							fieldLabel: amalto.smtp.bundle.getMsg('SERVER'),
							regex:new RegExp("[^ ].+"),
					    	regexText:amalto.smtp.bundle.getMsg('SERVER_TIP'),
					    	readOnly:false,
					    	value:"localhost"
						},
						{
							name: 'smtpPort',
							id:'smtpPort',
							selectOnFocus: true,
							minLength:2,
							allowBlank: false,
							fieldLabel: amalto.smtp.bundle.getMsg('PORT'),
							regex:new RegExp("[1-9][0-9]+"),
					    	regexText:amalto.smtp.bundle.getMsg('PORT_TIP'),
					    	readOnly:false,
					    	value:"25"
						},
						{
							name: 'smtpUsername',
							id:'smtpUsername',
							minLength:0,
							allowBlank: true,
							fieldLabel: amalto.smtp.bundle.getMsg('USERNAME'),
							regex:new RegExp("[^ ]{3}.*"),
					    	regexText:amalto.smtp.bundle.getMsg('USERNAME_TIP'),
					    	readOnly:false,
					    	value:""
						},
						{
							name: 'smtpPassword',
							id:'smtpPassword',
							minLength:0,
							allowBlank: true,
							fieldLabel: amalto.smtp.bundle.getMsg('PASSWORD'),
							regex:new RegExp("[^ ].*"),
					    	regexText:amalto.smtp.bundle.getMsg('PASSWORD_TIP'),
					    	readOnly:false,
					    	inputType:'password',
					    	value:""
						},
						{
							name: 'smtpBCC',
							id:'smtpBCC',
							minLength:0,
							allowBlank: true,
							fieldLabel: amalto.smtp.bundle.getMsg('BCC'),
							regex:new RegExp("[^ ].*"),
					    	regexText:amalto.smtp.bundle.getMsg('BCC_TIP'),
					    	readOnly:false,
					    	value:""
						}
				],
//				tbar: [
//            		amalto.smtp.bundle.getMsg('STATUS')+":",
//            		'<span style="font-weight:bold;" id="smtpStatus"/>',
//            		new Ext.Toolbar.Separator(),
//    	            new Ext.Toolbar.Button({
//    	            	id:'btn-start',
//    	            	text: amalto.smtp.bundle.getMsg('START'), 
//    					handler: function(){
//    						startListening();
//    					}
//    	            }), 
//    	            new Ext.Toolbar.Separator(),
//    	            new Ext.Toolbar.Button({
//    	            	id:'btn-stop',
//    	            	text: amalto.smtp.bundle.getMsg('STOP'), 
//    					handler: function(){
//    						stopListening();
//    					}
//    	            }),
//    	            new Ext.Toolbar.Separator(),
//    	            new Ext.Toolbar.Button({
//    	            	id:'btn-stop',
//    	            	text: amalto.smtp.bundle.getMsg('STATUS')+"?", 
//    					handler: function(){
//    						getStatus();
//    					}
//    	            })
//	            ],
	            buttons: [
			    	{
				        text: amalto.smtp.bundle.getMsg('SAVE'),
						handler: function(){
							saveConfiguration(function(dwrMessage) {
								Ext.MessageBox.show({
							        title:amalto.smtp.bundle.getMsg('SUCCESS'), 
							        msg:dwrMessage,
							        width:500,
							        buttons: Ext.Msg.OK
								});			
							}); 
				        }//handler function
			    	},
			    	{
				        text: amalto.smtp.bundle.getMsg('TRYME'),
						handler: function(){
							openTryMePanel(); 
				        }//handler function
			    	}
			    ]
            });	
						
    	    
    	}
    	
    	tabPanel.add(panel); 
    	panel.show();
    	amalto.core.doLayout();
		loadConfiguration();
	    
	}
	
	function openTryMePanel(){
		var tabPanel = amalto.core.getTabPanel();
   	    var tryMailPanel=tabPanel.getItem('tryMailPanel');
		if( tryMailPanel == undefined){
							
				tryMailPanel=new amalto.smtp.tryform();
				tabPanel.add(tryMailPanel);							
		
		}
				        
		tryMailPanel.show();
		tryMailPanel.doLayout();
		amalto.core.doLayout();
	}
	
	
	function saveConfiguration(callback) {
		Ext.getCmp('smtpForm').getForm().doAction(
			    "DWRSubmit",
			    {
					timeout: 20,
					dwrFunction: SmtpAdapterInterface.saveConfiguration,
					waitMsg: amalto.smtp.bundle.getMsg('PLEASE_WAIT'),
					waitTitle: amalto.smtp.bundle.getMsg('SAVING'),							
					failure:	function(form,action) {
						if (action.failureType === Ext.form.Action.CLIENT_INVALID) {
							Ext.MessageBox.show({
								title:amalto.smtp.bundle.getMsg('ERROR'), 
								msg:amalto.smtp.bundle.getMsg('FIELDS_DO_NOT_MATCH'),
								icon:Ext.MessageBox.ERROR,
								width:500,buttons: Ext.Msg.OK
								});
						} else if (action.failureType === Ext.form.Action.CONNECT_FAILURE) {
							Ext.MessageBox.show({
								title:amalto.smtp.bundle.getMsg('ERROR'), 
								msg:amalto.smtp.bundle.getMsg('CONNECTION_ERROR')+'\n\n'+action.response,
								icon:Ext.MessageBox.ERROR,
								width:500,buttons: Ext.Msg.OK
								});
						} else if (action.failureType === Ext.form.Action.SERVER_INVALID) {
							Ext.MessageBox.show({
								title:amalto.smtp.bundle.getMsg('ERROR'), 
								msg:amalto.smtp.bundle.getMsg('SERVER_ERROR')+': \''+action.dwrMessage+'\'',
								icon:Ext.MessageBox.ERROR,
								width:500,buttons: Ext.Msg.OK
							});
						} else {
						    alert(DWRUtil.toDescriptiveString(action,3));
							Ext.MessageBox.show({
								title:amalto.smtp.bundle.getMsg('ERROR'), 
								msg:amalto.smtp.bundle.getMsg('UNDEFINED_ERROR'),
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
	
	
	function startListening() {
		saveConfiguration(function(dwrMessage) {
			SmtpAdapterInterface.start(function(status){
				Ext.get('smtpStatus').update(status);
			});
		});
	}
	
	
	function stopListening() {
		SmtpAdapterInterface.stop(function(status){
			Ext.get('smtpStatus').update(status);
		});
	}
	
	function getStatus() {
		SmtpAdapterInterface.getStatus(function(status){
			Ext.get('smtpStatus').update(status);;
		});
	}

	
	function loadConfiguration(){
        Ext.getCmp('smtpForm').getForm().doAction(
		    "DWRLoad",
		    {
    			timeout: 20,
    			dwrFunction: SmtpAdapterInterface.loadConfiguration,
    			waitMsg: amalto.smtp.bundle.getMsg('PLEASE_WAIT'),
    			waitTitle: amalto.smtp.bundle.getMsg('LOADING'),							
    			failure:	function(form,action) {
    				if (action.failureType === Ext.form.Action.CONNECT_FAILURE) {
    					Ext.MessageBox.show({
    						title:amalto.smtp.bundle.getMsg('ERROR'), 
    						msg:amalto.smtp.bundle.getMsg('CONNECTION_ERROR')+'\n\n'+action.response,
    						icon:Ext.MessageBox.ERROR,
    						width:500,buttons: Ext.Msg.OK
    						});
    				} else if (action.failureType === Ext.form.Action.LOAD_FAILURE) {
    					Ext.MessageBox.show({
    						title:amalto.smtp.bundle.getMsg('ERROR'), 
    						msg: amalto.smtp.bundle.getMsg('SERVER_ERROR')+': \''+action.dwrMessage+'\'',
    						icon:Ext.MessageBox.ERROR,
    						width:500,buttons: Ext.Msg.OK
    					});
    				} else {
    				    alert(DWRUtil.toDescriptiveString(action,3));
    					Ext.MessageBox.show({
    						title:amalto.smtp.bundle.getMsg('ERROR'),
    						msg:amalto.smtp.bundle.getMsg('UNDEFINED_ERROR'),
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
	
	

	
 	return {
		init: function() {
			amalto.smtp.bundle = new Ext.i18n.Bundle({bundle:'SmtpAdapterMessages', path:'/smtp/secure/resources', lang:language});
			amalto.smtp.bundle.onReady(function(){
				displaysmtpAdapterMainPanel();
			});
		},
		getStatus: function(){getStatus();},
		start: function(){start();},
		stop: function(){stop();}
 	}
}();
