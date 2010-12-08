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
	
	var START_TITLE = {
		'fr':'Démarrage',
		'en':'Getting Started'
	}
	
	var START_DESCRIPTION = {
		'fr':'Vous pouvez commencer par le panneau \"Démarrage\".',
		'en':'You can start with \"Getting Started\" panel.'
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
	
	var NO_LICENSE_MESSAGE = {
		'fr': 'Aucune license disponible, veuillez entrer une license valide.',
		'en': 'No license available, please input your valid license.'
	}
	
	var LICENSE_EXPIRED_MESSAGE = {
		'fr': 'La license a expiré, veuillez renouveler votre license.',
		'en': 'License expired, please update your license.'
	}
	
	var Loading_Task_Msg = {
		'fr':'Chargement de la liste de tâches en cours, veuillez patienter...',
		'en':'Loading workflow task list, please wait...'
	}
	
	var Loading_Alert_Msg = {
		'fr':'Chargement de la liste des alertes en cours, veuillez patienter...',
		'en':'Loading list of alerts, please wait...'
	}
	
	var USEFUL_LINKS = {
		'fr':'Liens Utiles',
		'en':'Useful links'
	}
	
	var USEFUL_LINKS_DESCRIPTION = {
		'fr':'Liens utiles pour démarrer avec Talend MDM:',
		'en':'Here are a few links to help you get started with Talend MDM:'
	}

	var TASKS_TITLE = {
		'fr':'Tâches',
		'en':'Tasks'
	}
	
	var PROCESS_TITLE = {
		'fr':'Tâches',
		'en':'Processes'
	}

	var ALERTS_TITLE = {
		'fr':'Alertes',
		'en':'Alerts'
	}

	var NO_TASKS = {
		'fr':'Aucune tâche.',
		'en':'No tasks.'
	}
	
	var NO_STANDALONE_PROCESS = {
		'fr':'Aucun processus autonome.',
		'en':'No standalone processes.'
	}

	var NO_ALERTS = {
		'fr':'Aucune alerte.',
		'en':'No alerts.'
	}
	
	var WAITING_TASK_PREFIX = {
		'fr':'Vous avez ',
		'en':'You have '
	}
	
	var WAITING_TASK_SUFFIX = {
		'fr':' tâche(s) de workflow en attente.',
		'en':' workflow task(s) ready to handle.'
	}
	
	var TASKS_DESCRIPTION = {
		'fr':'Gérez vos tâches via les liens suivants.',
		'en':'You can handle tasks by the following links.'
	}
	
	var ALERTS_DESCRIPTION = {
		'fr':'Gérez vos alertes via les liens suivants.',
		'en':'You can handle alerts by the following links.'
	}
	
	var PROCESS_DESCRIPTION = {
		'fr':'Lancez vos processus autonomes via les liens suivants.',
		'en':'You can run the following standalone processes.'
	}
	
	var welcomePanel;	
	var messageArea;
	var startMessagePL;
	var startPanel;
	var alertMessageLB;
	var alertsPanel;
	var taskMessagePL;
	var taskPanel;
	var processMessagePL;
	var processPanel;
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
	  	applyProcess();
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
					text : USEFUL_LINKS_DESCRIPTION[language]
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
							 title : USEFUL_LINKS[language],
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
				title : START_TITLE[language],
				height : 145,
				border: true,
				header:true,
				split:true,
				collapsible: true,
				bodyborder: true,
				items : [startMessagePL, startFieldPL],
		        tools:[{
				    id:'refresh',
				    qtip: 'Refresh',
				    handler: function(event, toolEl, panel){
				    }
		        }]
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
							title : ALERTS_TITLE[language],
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
				title : ALERTS_TITLE[language],
				border: true,
				height:120,
				header:true,
				split:true,
				collapsible: true,
				bodyborder: true,
				items : [alertMessageLB, alertsFieldPL],
				tools:[{
				    id:'refresh',
				    qtip: 'Refresh',
				    handler: function(event, toolEl, panel){
						applyAlertsMessage(language);
				    }
				}]
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
							title : TASKS_TITLE[language],
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
				title : TASKS_TITLE[language],
				border: true,
				height:120,
				header:true,
				split:true,
				collapsible: true,
				bodyborder: true,
				items : [taskMessagePL, taskFieldPL],
				tools:[{
				    id:'refresh',
				    qtip: 'Refresh',
				    handler: function(event, toolEl, panel){
						applyTaskMessage();
				    }
				}]
			});
			
			processMessagePL = new Ext.Panel({
	 			id: 'processMessagePL',
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
					id : 'processMessageLB',
					xtype : 'label',
					style : 'padding-left: 20px;'
				}]
	 		});
			
			var processFieldPL = new Ext.Panel({
				id : 'processFieldPL',
				layout : 'table',
				border : false,
				autoHeight : true,
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
							title : PROCESS_TITLE[language],
							id : 'processField',
							width : 500,
							autoHeight : true,
							style : 'font-size:80%;'
							})
				         ]
			});
			
			processPanel = new Ext.Panel({
				id : 'processPL',
				iconCls : 'transformer',
				title : 'Processes',
				border: true,
				height:120,
				autoHeight : true,
				layoutConfig: {
			        columns: 2
			    },
				header:true,
				split:true,
				collapsible: true,
				bodyborder: true,
				tools:[{
				    id:'refresh',
				    qtip: 'Refresh',
				    handler: function(event, toolEl, panel){
						applyProcess();
				    }
				}],
				items : [processMessagePL, processFieldPL]
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
					 items : [startPanel, alertsPanel, taskPanel, processPanel]
				 })]
			});
			
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
					 licenseMessage = LICENSE_EXPIRED_MESSAGE[language];
				 }
				 else {
					 Ext.getCmp("alertsFields").setVisible(false);
					 Ext.getCmp("alertsMessage").setText(NO_ALERTS[language]);
					 return;
				 }
			 }
			 else {
				 licenseMessage = NO_LICENSE_MESSAGE[language];
			 }
			 
			 var licenseAlert = Ext.getCmp("licenseAlert");
			 DWRUtil.setValue("licenseAlert",  '<IMG SRC=\"/talendmdm/secure/img/genericUI/alert-icon.png\"/>' + ' ' + licenseMessage);
			 alertMessageLB.setText(ALERTS_DESCRIPTION[language]);
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
			 taskMessage = WAITING_TASK_PREFIX[language] + result +  WAITING_TASK_SUFFIX[language];
			 
			 if(Ext.getCmp("tasksField")) {
				 DWRUtil.setValue("workflowtasks", '<IMG SRC=\"/talendmdm/secure/img/genericUI/task-list-icon.png\"/>' + ' ' + taskMessage);
			 
				 if(result == "0") {
					 taskMessageLB.setText(NO_TASKS[language]);
					 Ext.getCmp("tasksField").setVisible(false);
				 }
				 else {
					 taskMessageLB.setText(TASKS_DESCRIPTION[language]);
				 }
			 }
		 });
	 }
	 
	 /**
	  * get all processes.
	  */
	 function applyProcess() {
		 var processMessageLB = Ext.getCmp("processMessageLB");
		 
		 WelcomeInterface.getStandaloneProcess(function(result) {
			 clearProcessesSpan();
			 
			 if(result.length == "0") {
				 processMessageLB.setText(NO_STANDALONE_PROCESS[language]);
				 Ext.getCmp("processField").setVisible(false);
			 }
			 else {
				 processMessageLB.setText(PROCESS_DESCRIPTION[language]);
				 Ext.getCmp("processField").setVisible(true);
				 buildProcessSpan(result);
			 }
		 });
	 }
	 
	 /**
	  * clear all component in processField.
	  */
	 function clearProcessesSpan() {
		 var processesSpan = Ext.getCmp("processField");
		 var items = processesSpan.items.items;
		 processesSpan.items.clear();
		 
		 if(items != undefined && items.length != 0) {
			 for(var i = 0; i < items.length; i++) {
				 processesSpan.remove(items[i]);
			 }
		 }
		 
		 processesSpan.doLayout(true);
	 }
	 
	 /**
	  * build processes fieldSet.
	  */
	 function buildProcessSpan(processes) {
		 var processesSpan = Ext.getCmp("processField");
		 
		 for(var i = 0; i < processes.length; i++) {
			 var processBtn = {
				 id : processes[i] + "Btn",
				 xtype:'button',
				 name:processes[i], 
				 iconCls: "launch_process",
				 listeners : {
				 	'click':function(){runProcess(this.name);}
			     }
			 };
			 
			 var processText = {
				 id : processes[i] + "label",
				 xtype:'label',
			     text: processes[i].replace("Runnable#", "")
			 };
			 
			 var processPL = {
			     xtype : 'panel',
			     layout : 'table',
			     border : false,
			     items : [processBtn, processText]
			 };
			 
			 processesSpan.add(processPL);
		 }
		 
		 processesSpan.doLayout(true);
	 }
	 
	 function runProcess(processPK) {
		 WelcomeInterface.runProcess(processPK, function(result) {
			 //@temp yguo, reaction to user by result.
		 });
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