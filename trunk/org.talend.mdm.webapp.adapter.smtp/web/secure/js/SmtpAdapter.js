amalto.namespace("amalto.smtp");

		
amalto.smtp.SmtpAdapter = function () {
	
	loadResource("/smtp/secure/dwr/interface/SmtpAdapterInterface.js", "" );
	
	loadResource("/smtp/secure/js/tryform.js", "" );
	
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
	var SERVICE_CONTROL={
		'fr':'Contrôle de l\'adapteur',
		'en':'Adapter Control'
	}
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
	var TRYME={
		'fr':'Try Me',
		'en':'Try Me'
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

	var SMTP_ADAPTER={
			'fr':'Adaptateur SMTP',
			'en':'SMTP Adapter'
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
	

	function displaysmtpAdapterMainPanel() {
	

		var tabPanel = amalto.core.getTabPanel();
		var panel = tabPanel.getItem('smtpForm');
		if( panel == undefined){		
			panel = new Ext.FormPanel({						
				id: 'smtpForm',
				deferredRender: false,
				closable: true,
				title: SMTP_ADAPTER[language],
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
							fieldLabel: SERVER[language],
							regex:new RegExp("[^ ].+"),
					    	regexText:SERVER[language],
					    	readOnly:false,
					    	value:"localhost"
						},
						{
							name: 'smtpPort',
							id:'smtpPort',
							selectOnFocus: true,
							minLength:2,
							allowBlank: false,
							fieldLabel: PORT[language],
							regex:new RegExp("[1-9][0-9]+"),
					    	regexText:PORT_TIP[language],
					    	readOnly:false,
					    	value:"25"
						},
						{
							name: 'smtpUsername',
							id:'smtpUsername',
							minLength:0,
							allowBlank: true,
							fieldLabel: USERNAME[language],
							regex:new RegExp("[^ ]{3}.*"),
					    	regexText:USERNAME_TIP[language],
					    	readOnly:false,
					    	value:""
						},
						{
							name: 'smtpPassword',
							id:'smtpPassword',
							minLength:0,
							allowBlank: true,
							fieldLabel: PASSWORD[language],
							regex:new RegExp("[^ ].*"),
					    	regexText:PASSWORD_TIP[language],
					    	readOnly:false,
					    	inputType:'password',
					    	value:""
						},
						{
							name: 'smtpBCC',
							id:'smtpBCC',
							minLength:0,
							allowBlank: true,
							fieldLabel: BCC[language],
							regex:new RegExp("[^ ].*"),
					    	regexText:BCC_TIP[language],
					    	readOnly:false,
					    	value:""
						}
				],
//				tbar: [
//            		STATUS[language]+":",
//            		'<span style="font-weight:bold;" id="smtpStatus"/>',
//            		new Ext.Toolbar.Separator(),
//    	            new Ext.Toolbar.Button({
//    	            	id:'btn-start',
//    	            	text: START[language], 
//    					handler: function(){
//    						startListening();
//    					}
//    	            }), 
//    	            new Ext.Toolbar.Separator(),
//    	            new Ext.Toolbar.Button({
//    	            	id:'btn-stop',
//    	            	text: STOP[language], 
//    					handler: function(){
//    						stopListening();
//    					}
//    	            }),
//    	            new Ext.Toolbar.Separator(),
//    	            new Ext.Toolbar.Button({
//    	            	id:'btn-stop',
//    	            	text: STATUS[language]+"?", 
//    					handler: function(){
//    						getStatus();
//    					}
//    	            })
//	            ],
	            buttons: [
			    	{
				        text: SAVE[language],
						handler: function(){
							saveConfiguration(function(dwrMessage) {
								Ext.MessageBox.show({
							        title:SUCCESS[language], 
							        msg:dwrMessage,
							        width:500,
							        buttons: Ext.Msg.OK
								});			
							}); 
				        }//handler function
			    	},
			    	{
				        text: TRYME[language],
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
	
	

	
 	return {
		init: function() {displaysmtpAdapterMainPanel();},
		getStatus: function(){getStatus();},
		start: function(){start();},
		stop: function(){stop();}
 	}
}();
