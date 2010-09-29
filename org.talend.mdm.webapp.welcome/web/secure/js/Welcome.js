amalto.namespace("amalto.welcome");

amalto.welcome.Welcome = function () {
	loadResource("/welcome/secure/css/Welcome.css", "" );
	
	var WELCOME_TITLE = {
		'fr':'Bienvenue',
		'en':'Welcome'
	}
	
	var WELCOME_PANEL = {
		'fr':'Bienvenue dans le tableau de bord de Talend MDM :',
		'en':'Welcome to the Talend MDM dashboard:'
	}
	
	var WELCOME_DESCRIPTION = {
		'fr':'Bienvenue dans Talend MDM.',
		'en':'Welcome to Talend MDM.'
	}
	
	var BROWSE_ITEMS = {
		'fr':'Accès aux données',
		'en':'Browse Records'
	}
	
	var JOURNAL = {
		'fr':'Journal',
		'en':'Journal'
	}
	
	var LICENSE_SOON_MESSAGE = {
		'fr': 'La license expirera dans {0} jours. Veuillez contacter votre commercial Talend.',
		'en': 'The License will expire in {0} days. Please contact your Talend account manager.'
	}
	
	var LICENSE_DES_NUMBERS = {
		'fr':'Vous avez actuellement souscrit à une license pour {0} utilisateurs.',
		'en':'You have currently subscribed to a license with {0} users.'
	}
	
	var LICENSE_DES_CON = {
		'fr':'Si vous avez besoin d\'ajouter des utilisateurs, vous pouvez contacter votre commercial Talend.',
		'en':'If you need to add users to your license, you may contact your Talend account manager.'
	}
	
	var Loading_Task_Msg = {
		'fr':'Chargement de la liste de tâches en cours, veuillez patienter...',
		'en':'Loading workflow task list, please wait...'
	}
	
	var Loading_Alert_Msg = {
		'fr':'Chargement de la liste des alertes en cours, veuillez patienter...',
		'en':'Loading list of alerts, please wait...'
	}
	
	var welcomePanel;	
	var messageArea;
	var startMessagePL;
	var alertMessageLB;
	var taskMessagePL;
	var startPanel;
	var alertsPanel;
	var taskPanel;
	var descriptionPanel;
	var admins;
	var normals;
	var viewers;
	var nbusers;
	var hidenTaskPL = true;
	var hidenAlertsPL = true;
	
	/********************************************************************
	 * Action show welcome
	 ********************************************************************/
	
	/**
	 * display welcome page.
	 */
	 function displayWelcome(){
	  	showWelcomePanel();
	  	applyAlertsMessage(language);
	  	applyTaskMessage();
	 }
	 
	 /**
	  * open the page by specify context and application.
	  */
	 function openPages(context, application) {
		 //update display of menu in menu Panel
//		 YAHOO.util.Dom.removeClass(Ext.get('menus').dom.getElementsByTagName('a'), 'selected');
//		 YAHOO.util.Dom.addClass(a.id, 'selected');
		 
		 if("license" != context) {
			 LayoutInterface.isExpired({
				callback:function(isExpired){
				Ext.MessageBox.hide();
					if(!isExpired) {
						amalto.core.loadMainScript(context, application,
							function() {
								var initFunction = "amalto." + context + "." + application + ".init()";
								setTimeout(initFunction,'50');
							}
						);
					}
				},
				errorHandler:function(errorString, exception) { 
					Ext.MessageBox.hide();
					alert('Error:'+ errorString);
				}  
			 });
		 }
		 else {
			 amalto.core.loadMainScript(context, application,
				function() {
					var initFunction = "amalto." + context + "." + application + ".init()";
					setTimeout(initFunction,'50');
				}
			 );
		 }
	 }
	 
	 /**
	  * show welcome panel.
	  */
	 function showWelcomePanel() {
		 //@temp yguo, should get label of link from menu object.
	 	var tabPanel = amalto.core.getTabPanel();

	 	if(tabPanel.getItem('welcome') == undefined) {
	 		startMessagePL = new Ext.Panel({
	 			id: 'startMessagePL',
				border : false,
				header : false,
				split : true,
				style : 'font-weight:bold;',
				height : 30,
				collapsible : false,
				bodyborder : true,
				bodyStyle : "background-color:#F1F1F1;",
				colspan: 2,
				items : [{
					id : 'startMessageLB',
					xtype : 'label',
					style : 'padding-left: 20px;',
					text : 'Here are a few links to help you get started with Talend MDM:'
				}]
	 		});
	 		
	 		var startFieldPL = new Ext.Panel({
	 			id : 'startFieldPL',
	 			layout : 'table',
	 			layoutConfig: {
			        columns: 3
			    },
			    border : false,
	 			items : [
						{
							width : 20,
							rowspan: 2,
							border : false
						},
						new Ext.form.FieldSet({
							 title : 'Useful Links',
							 width : 500,
							 colspan: 2,
							 style : 'font-size:80%;',
							 autoHeight : true,
							 style : 'font-size:80%;',
							 html : '<span id="ItemsBrowser" style="padding-right:8px;cursor: pointer; width:150;" onclick="amalto.welcome.Welcome.openPages(\'itemsbrowser\', \'ItemsBrowser\');">' + 
								   '<IMG SRC=\"/talendmdm/secure/img/menu/browse.png\"/>' + ' ' + BROWSE_ITEMS[language] + '</span>' + '<br/>' +
								   '<span id="Journal" style="padding-right:8px;cursor: pointer; width:150;" onclick="amalto.welcome.Welcome.openPages(\'updatereport\', \'UpdateReport\');">' + 
								   '<IMG SRC=\"/talendmdm/secure/img/menu/updatereport.png\"/>' + ' ' + JOURNAL[language] + '</span>'
						})
	 			         ]
	 		});
	 		
			startPanel = new Ext.Panel({
				id: 'startPL',
				iconCls : 'start_icon',
				title : 'Getting Started',
				height : 145,
				border: true,
				header:true,
				split:true,
				collapsible: true,
				bodyborder: true,
				items : [
				         startMessagePL,
				         startFieldPL
				         ]
			});
			
			hidenAlertsPL = isHiddenLicense();
			
			
			alertMessageLB = new Ext.Panel({
	 			id: 'alertMessageLB',
				border : false,
				header : false,
				split : true,
				height : 30,
				colspan : 2,
				collapsible : false,
				style : 'font-weight:bold;',
				bodyborder : true,
				bodyStyle : "background-color:#F1F1F1;",
				items :[
				        {
				        	id : 'alertsMessage',
				        	xtype : 'label',
				        	style : 'padding-left: 20px;'
				        }]
	 		});
			
			var alertsFieldPL = new Ext.Panel({
				id : 'alertsFieldPL',
				layout : 'table',
				layoutConfig : {
				 columns: 3
				},
				border : false,
				items : [
						{
							width : 20,
							rowspan: 2,
							border : false
						},
						new Ext.form.FieldSet({
							title : 'Alerts',
							id : 'alertsFields',
							width : 500,
							style : 'font-size: 80%;',
							colspan : 2,
							autoHeight : true,
							html : '<span id="licenseAlert" style="padding-right:8px;cursor: pointer;" onclick="amalto.welcome.Welcome.openPages(\'license\', \'License\');"></span>'
						})
				         ]
			});
			
			alertsPanel = new Ext.Panel({
				id: 'alertsPL',
				iconCls : 'alert_icon',
				hidden : hidenAlertsPL,
				title : 'Alerts',
				border: true,
				height:120,
				header:true,
				split:true,
				collapsible: true,
				bodyborder: true,
				items : [alertMessageLB, alertsFieldPL]
			});
			
			hidenTaskPL = isHiddenTask();
			
			taskMessagePL = new Ext.Panel({
	 			id: 'taskMessagePL',
				border : false,
				header : false,
				split : true,
				style : 'font-weight:bold;',
				colspan : 2,
				height : 30,
				collapsible : false,
				bodyborder : true,
				bodyStyle : "background-color:#F1F1F1;",
				items : [{
					id : 'taskMessageLB',
					xtype : 'label',
					style : 'padding-left: 20px;'
				}]
	 		});
			
			var taskFieldPL = new Ext.Panel({
				id : 'taskFieldPL',
				layout : 'table',
				border : false,
				layoutConfig : {
				 columns: 3
				},
				items : [
						{
							width : 20,
							rowspan: 2,
							border : false
						},
						new Ext.form.FieldSet({
							title : 'Tasks',
							id : 'tasksField',
							width : 500,
							autoHeight : true,
							style : 'font-size:80%;',
							html : '<span id="workflowtasks" style="padding-right:8px;cursor: pointer;" onclick="amalto.welcome.Welcome.openPages(\'workflowtasks\', \'WorkflowTasks\');"></span>'
						})
				         ]
			});
			
			taskPanel = new Ext.Panel({
				id: 'taskPL',
				iconCls : 'task_list_icon',
				hidden : hidenTaskPL,
				title : 'Tasks',
				border: true,
				height:120,
				header:true,
				split:true,
				collapsible: true,
				bodyborder: true,
				items : [taskMessagePL, taskFieldPL]
			});
			
			descriptionPanel = new Ext.Panel({
				id : 'messagePL',
				iconCls : 'description_icon',
				title : 'Description',
				border: true,
				layout : 'table',
				height:120,
				bodyStyle : "background-color:#F1F1F1;padding:5px",
				layoutConfig: {
			        columns: 2
			    },
				header:true,
				split:true,
				collapsible: true,
				bodyborder: true,
				items :[{
					rowspan: 4
				},{
					id : 'descriptionWelcome',
					xtype : 'label',
					style : 'font-size:80%;',
					text : WELCOME_DESCRIPTION[language],
					colspan: 2
				},{
					id : 'startDescription',
					xtype : 'label',
					style : 'font-size:80%;',
					text : 'You can start with "Getting Started" panel.',
					colspan: 2
				},{
					id : 'descriptionNumbers',
					xtype : 'label',
					style : 'font-size:80%;',
					hidden : hidenAlertsPL,
					colspan: 2
				},{
					id : 'descriptionCon',
					xtype : 'label',
					style : 'font-size:80%;',
					text : LICENSE_DES_CON[language],
					hidden : hidenAlertsPL,
					colspan: 2
				}]
			});
			
			welcomePanel = new Ext.Panel({
				id: 'welcome',
				title: WELCOME_TITLE[language],
				deferredRender: false,
				autoScroll: false,
				border: false,
				bodyBorder:false,
				closable: true,
				items: 
				[new Ext.Panel({
					 id : 'welcomePl',
					 title : WELCOME_PANEL[language],
					 border : true,
					 header : true,
					 split : true,
					 collapsible : false,
					 bodyborder : true,
					 items : [startPanel, alertsPanel, taskPanel]
				 })]
			});
			
//			setLabels(language);
			if(!hidenTaskPL) {
				applyTaskMessage();
			}
			
			if(!hidenAlertsPL) {
				applyAlertsMessage(language);
			}
		}		
		
		tabPanel.add(welcomePanel); 
		welcomePanel.show();
		welcomePanel.doLayout();
		amalto.core.doLayout();
	 }
	 
	 /**
	  * set all label of links.
	  */
	 function setLabels(language) {
		 WelcomeInterface.getLabels(language, function(result) {
			 if(Ext.getCmp("workflowtasks")) {
				 DWRUtil.setValue("workflowtasks", '<IMG SRC=\"/talendmdm/secure/img/menu/workflowtasks.png\"/>' + '  ' + result["WorkflowTasks"]);
			 }
		 });
	 }
	 
	 /**
	  * build all license message.
	  */
	 function applyAlertsMessage(language) {
		 var licenseMessage;
		 var alertMessageLB = Ext.getCmp("alertsMessage");
		 alertMessageLB.setText(Loading_Alert_Msg[language]);
		 
		 WelcomeInterface.getLicenseMsg(language, function(result) {
			 if(result.data.license) {
				 if(!result.data.licenseValid) {
					 licenseMessage = "License expired, please update your license by license wizard.";
				 }
				 else {
					 Ext.getCmp("alertsFields").setVisible(false);
					 Ext.getCmp("alertsMessage").setText("No alerts.");
					 return;
				 }
			 }
			 else {
				 licenseMessage = "No license available, please input your valid license by license wizard.";
			 }
			 
			 var licenseAlert = Ext.getCmp("licenseAlert");
			 DWRUtil.setValue("licenseAlert",  '<IMG SRC=\"/talendmdm/secure/img/genericUI/alert-icon.png\"/>' + ' ' + licenseMessage);
			 alertMessageLB.setText("You can handle alerts by the following links.");
		 });
	 }
	 
	 /**
	  * build all message for workflow task.
	  */
	 function applyTaskMessage() {
		 var taskMessage;
		 var taskMessageLB = Ext.getCmp("taskMessageLB");
		 taskMessageLB.setText(Loading_Task_Msg[language]);
		 
		 WelcomeInterface.getTaskMsg(function(result){
			 taskMessage = "You have " + result + " workflow task ready to handle.";
			 
			 if(Ext.getCmp("tasksField")) {
				 DWRUtil.setValue("workflowtasks", '<IMG SRC=\"/talendmdm/secure/img/genericUI/task-list-icon.png\"/>' + ' ' + taskMessage);
			 
				 if(result == "0") {
					 taskMessageLB.setText("No tasks.");
					 Ext.getCmp("tasksField").setVisible(false);
				 }
				 else {
					 taskMessageLB.setText("You can handle tasks by the following links.");
				 }
			 }
		 });
	 }
	 
	 /**
	  * get all description.
	  */
	 function applyDescriptionMessage(number) {
		 var numberDescription = LICENSE_DES_NUMBERS[language].replace("{0}", number);
		 Ext.getCmp("descriptionNumbers").setText(numberDescription);
	 }
	 
	 /**
	  * check if is show license.
	  */
	 function isHiddenLicense() {
		 var hidden = true;
		 DWREngine.setAsync(false);
		 
		 WelcomeInterface.isHiddenLicense(function(result){
			 hidden = result;
		 });
		 
		 DWREngine.setAsync(true);
		 return hidden;
	 }
	 
	 /**
	  * check if is show license.
	  */
	 function isHiddenTask() {
		 var hiddenTask = true;
		 DWREngine.setAsync(false);
		 
		 WelcomeInterface.isHiddenTask(function(result){
			 hiddenTask = result;
		 });
		 
		 DWREngine.setAsync(true);
		 return hiddenTask;
	 }
	 
 	return {
		init: function() {displayWelcome(); },
		getTask : function(){getTask()},
		openPages : function(context, application){openPages(context, application)},
		getLicense : function(){getLicense();},
		getStartItems : function(){getStartItems();}
 	}

}();