amalto.namespace("amalto.loggingadapter");
		
amalto.loggingadapter.LoggingAdapter = function () {
	
	loadResource("/loggingadapter/secure/dwr/interface/LoggingSmtpInterface.js", "" );
	
	var LOGGING_ADAPTER={
		'fr':'Capture des erreurs',
		'en':'Errors Capture'
	}
	var SERVICE_CONTROL={
		'fr':'Adaptateur Log4J',
		'en':'Log4J Adapter'
	}
	var START={
		'fr':'Démarrer',
		'en':'Start'
	}
	var STOP={
		'fr':'Arrêter',
		'en':'Stop'
	}
	var STATUS={
		'fr':'Statut',
		'en':'Status'
	}
	var TRYME={
	    'fr':'Try Me',
		'en':'Try Me'	
	}
	var THRESHOLD={
		'fr':'Niveau',
		'en':'Threshold'
	}
	
	//DWRAction Connectivity
	var PLEASE_WAIT= {
		'fr':' Le serveur traite votre demande. Merci de patienter....',
		'en': 'The Server is processing your request. Please wait....'
	}
	var FIELDS_DO_NOT_MATCH = {
		'fr': 'Un ou plusieurs champs ne respectent pas leur format',
		'en': 'One or more fields do not match their requested pattern'
	}
	var CONNECTION_ERROR = {
		'fr': 'Une erreur est survenue lors de la connexion au serveur',
		'en': 'An error occured connecting to the server'
	}
	var SERVER_ERROR = {
		'fr': 'Le serveur a envoyé une erreur',
		'en': 'The server sent an error'
	}
	var UNDEFINED_ERROR = {
		'fr': 'Une erreur inconnue (probablement javascript) est survenue',
		'en': 'An undefined (likely javascript) error occured'
	}
	var SAVE={
		'fr':'Sauvegarder',
		'en':'Save'
	}
	var SAVING ={
			'fr':'Sauvegarde en cours',
			'en':'Saving'
	}
	var LOADING ={
			'fr':'Chargement en cours',
			'en':'Loading'
	}	
	var ERROR ={
			'fr':'Erreur',
			'en':'Error'
	}
	var SUCCESS ={
			'fr':'Succès',
			'en':'Erreur'
	}
	
	//Smtp Form
	var SMTP_CONTROL={
		'fr':'Paramètres SMTP',
		'en':'SMTP Parameters'
	}
	var SERVER={
			'fr':'Serveur smtp',
			'en':'Smtp Server'
	}
	var SERVER_TIP={
			'fr':'Entrez le nom ou l\'addresse IP d\'un serveur SMTP',
			'en':'Name or IP address of an SMTP server'
	}
	var PORT={
			'fr':'Port',
			'en':'Listener Port'
	}
	var PORT_TIP={
			'fr':'Le port d\'écoute du serveur SMTP',
			'en':'Listening port of the SMTP server'
	}
	var USERNAME={
			'fr':'Identifiant',
			'en':'Username'
	}
	var USERNAME_TIP={
			'fr':'Identifiant SMTP. Optionnel',
			'en':'SMTP username. Optional'
	}
	var PASSWORD={
			'fr':'Mot de passe',
			'en':'Password'
	}
	var PASSWORD_TIP={
			'fr':'Mot de passe SMTP. Optionnel',
			'en':'SMTP password. Optional'
	}
	var BCC={
			'fr':'Copies Cachées',
			'en':'Blind Copies'
	}
	var BCC_TIP={
			'fr':'Addresses e-mail des copies cachées systématiques',
			'en':'email adrreses systematically Blind Copied'
	}
	var SMTPSAVED={
		'fr':'Nouveaux paramètres SMTP enregistrés',
		'en':'New SMTP parameters saved.'
	}

	function displayLoggingAdapterMainPanel() {
		
		var tabPanel = amalto.core.getTabPanel();
		var panel = tabPanel.getItem('logging-main-panel'); 
		if(panel == undefined){		
			panel = new Ext.Panel({
				id:'logging-main-panel',
				title:LOGGING_ADAPTER[language],
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
			    		title: SERVICE_CONTROL[language],
						border:false,
						bodyBorder:false,
						autoScroll: true,	
						//header:true,
						height:170,		
						split:true,
						labelWidth:150,
						buttonAlign:'left',
						bodyStyle:'padding:5px',						
						html:'<div class="left" >'+STATUS[language]+'</div><div class="leftField" id="status-logging"></div><br/><br/>'+
							'<div class="left">Port</div><div class="leftField"><input type="text" value="" id="log4jport" class="x-form-text x-form-field"/></div><br/><br/>'+
							'<div class="left">'+THRESHOLD[language]+'</div><div class="leftField"><select id="threshold"></select></div><br/><br/>'+
							'<div style="display:none">'+
								  'Xtentis user name<input type="text" value="" id="xtentisusername" size="30"/>'+
								  'Xtentis password<input type="password" id="xtentispassword" value="" size="30"/>'+
								  'log file name<input type="file" value="" id="logfilename" size="30"/>'+
							'</div>'+
							'	  <input type="hidden" value="" id="pattern" size="30"/>',
							//items:[{xtype:'texfield',inputType:'hidden'}],
						buttons:[
							new Ext.Button({
								text:START[language],
								handler:startLogging
							}),	
							new Ext.Button({
								text:STOP[language],
								handler:stop
							}),	
							new Ext.Button({
								text:STATUS[language],
								handler:getStatus
							}),
							new Ext.Button({
								text:TRYME[language],
								handler:tryMe
							})
						]
					}),
        			new Ext.FormPanel({						
        				id: 'loggingsmtpForm',
        				deferredRender: false,
        				region:'center',
        				closable: true,
        				title: SMTP_CONTROL[language],
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
        							fieldLabel: SERVER[language],
        							regex:new RegExp("[^ ].+"),
        					    	regexText:SERVER[language],
        					    	readOnly:false,
        					    	value:"localhost"
        						},
        						{
        							name: 'loggingsmtpPort',
        							id:'loggingsmtpPort',
        							selectOnFocus: true,
        							minLength:2,
        							allowBlank: false,
        							fieldLabel: PORT[language],
        							regex:/^[0-9]+$/,
        					    	regexText:PORT_TIP[language],
        					    	readOnly:false,
        					    	value:"25"
        						},
        						{
        							name: 'loggingsmtpUsername',
        							id:'loggingsmtpUsername',
        							minLength:0,
        							allowBlank: true,
        							fieldLabel: USERNAME[language],
        							regex:new RegExp("[^ ]{3}.*"),
        					    	regexText:USERNAME_TIP[language],
        					    	readOnly:false,
        					    	value:""
        						},
        						{
        							name: 'loggingsmtpPassword',
        							id:'loggingsmtpPassword',
        							minLength:0,
        							allowBlank: true,
        							fieldLabel: PASSWORD[language],
        							regex:new RegExp("[^ ].*"),
        					    	regexText:PASSWORD_TIP[language],
        					    	readOnly:false,
        					    	value:""
        						},
        						{
        							name: 'loggingsmtpBCC',
        							id:'loggingsmtpBCC',
        							minLength:0,
        							allowBlank: true,
        							fieldLabel: BCC[language],
        							regex:new RegExp("[^ ].*"),
        					    	regexText:BCC_TIP[language],
        					    	readOnly:false,
        					    	value:""
        						}
        				],
        	            buttons: [
        	  			    	{
        	  				        text: SAVE[language],
        	  						handler: function(){
        	  							saveSMTPConfiguration(function(dwrMessage) {
        	  								Ext.MessageBox.show({
        	  							        title:SUCCESS[language], 
        	  							        msg:SMTPSAVED[language],
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
    			waitMsg: PLEASE_WAIT[language],
    			waitTitle: LOADING[language],							
    			failure:	function(form,action) {
    				if (action.failureType === Ext.form.Action.CONNECT_FAILURE) {
    					Ext.MessageBox.show({
    						title:ERROR[language], 
    						msg:CONNECTION_ERROR[language]+'\n\n'+action.response,
    						icon:Ext.MessageBox.ERROR,
    						width:500,buttons: Ext.Msg.OK
    						});
    				} else if (action.failureType === Ext.form.Action.LOAD_FAILURE) {
    					Ext.MessageBox.show({
    						title:ERROR[language], 
    						msg: SERVER_ERROR[language]+': \''+action.dwrMessage+'\'',
    						icon:Ext.MessageBox.ERROR,
    						width:500,buttons: Ext.Msg.OK
    					});
    				} else {
    				    alert(DWRUtil.toDescriptiveString(action,3));
    					Ext.MessageBox.show({
    						title:ERROR[language],
    						msg:UNDEFINED_ERROR[language],
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
					waitMsg: PLEASE_WAIT[language],
					waitTitle: SAVING[language],							
					failure:	function(form,action) {
						if (action.failureType === Ext.form.Action.CLIENT_INVALID) {
							Ext.MessageBox.show({
								title:ERROR[language], 
								msg:FIELDS_DO_NOT_MATCH[language],
								icon:Ext.MessageBox.ERROR,
								width:500,buttons: Ext.Msg.OK
								});
						} else if (action.failureType === Ext.form.Action.CONNECT_FAILURE) {
							Ext.MessageBox.show({
								title:ERROR[language], 
								msg:CONNECTION_ERROR[language]+'\n\n'+action.response,
								icon:Ext.MessageBox.ERROR,
								width:500,buttons: Ext.Msg.OK
								});
						} else if (action.failureType === Ext.form.Action.SERVER_INVALID) {
							Ext.MessageBox.show({
								title:ERROR[language], 
								msg:SERVER_ERROR[language]+': \''+action.dwrMessage+'\'',
								icon:Ext.MessageBox.ERROR,
								width:500,buttons: Ext.Msg.OK
							});
						} else {
						    alert(DWRUtil.toDescriptiveString(action,3));
							Ext.MessageBox.show({
								title:ERROR[language], 
								msg:UNDEFINED_ERROR[language],
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
		init: function() {displayLoggingAdapterMainPanel();},
		getStatus: function(){getStatus();},
		start: function(){start();},
		stop: function(){stop();}
 	}
}();
