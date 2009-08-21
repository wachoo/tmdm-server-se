amalto.namespace("amalto.SynchronizationAction");

//loadResource("/SynchronizationAction/secure/js/Cookies.js", "" );

//contectid.applicaionid
amalto.SynchronizationAction.SynchronizationAction = function() {
	//declare application local
    loadResource("/SynchronizationAction/secure/js/SynchronizationActionLocal.js", "amalto.SynchronizationAction.SynchronizationActionLocal" );
    
    loadResource("/SynchronizationAction/secure/css/SynchronizationAction.css", "" );
    
	var SELECT_SYNCHRONIZATION='Select a Synchronization Name';
	
   
	function getSyncInfo(){
		if ($('serverURL').value=='') {
			//alert ('Server URL');
			$('serverURL').focus();
			return;
		}
		if ($('username').value=='') {
			//alert ('UserName');
			$('username').focus();
			return;
		}	
		if ($('password').value=='') {
			//alert ('password');
			$('password').focus();
			return;
		}
		
		var syncInfo = {
			serverURL:$('serverURL').value.trim(),
			username:$('username').value.trim(),
			password:$('password').value.trim(),
			syncName:$('syncName').value.trim()
		};
		return syncInfo;
	}

	function initSyncNames(){
		var syncinfo=getSyncInfo();
		if(syncinfo){
			
			SynchronizationActionInterface.getSyncNames(syncinfo,function(syncs){
				if(syncs){
					var syncnames=syncs;
					var tmp=[SELECT_SYNCHRONIZATION];
					DWRUtil.removeAllOptions("syncName");
					DWRUtil.addOptions("syncName",tmp);
					if(!syncnames)return;
					DWRUtil.addOptions("syncName",syncnames);
				}
			});		
		}
	};

	function updateStatus(syncStatus){
		if(syncStatus){
			synccode=syncStatus;
			Ext.getCmp('sync_status').getEl().update('[' + syncStatus.value + '] ' + syncStatus.message);
			if('RUNNING' == syncStatus.value || 'SCHEDULED' == syncStatus.value){
	    		Ext.getCmp('startFullButton').disable();
	    		Ext.getCmp('startDifferentButton').disable();
	    		Ext.getCmp('stopButton').enable();
	    		Ext.getCmp('resetButton').disable();
			}
			else if ("STOPPING" == syncStatus.value) {
	    		Ext.getCmp('startFullButton').disable();
	    		Ext.getCmp('startDifferentButton').disable();
	    		Ext.getCmp('stopButton').disable();
	    		Ext.getCmp('resetButton').enable();
	    		
	    	} else {
	    		Ext.getCmp('startFullButton').enable();
	    		Ext.getCmp('startDifferentButton').enable();
	    		Ext.getCmp('stopButton').disable();
	    		Ext.getCmp('resetButton').disable();
	    	}
		}
	};

	var synccode;
	
	function refreshStatus(syncInfo){
		var timer=setInterval(function(){
			SynchronizationActionInterface.getStatus(syncInfo,function(syncStatus){
				if(syncStatus==null){
					clearInterval(timer);
					return;
				}
				updateStatus(syncStatus);
			});
			if(synccode){
				if(!('RUNNING' == synccode.value || 'SCHEDULED' == synccode.value)){
					clearInterval(timer);
					updateStatus(synccode);
				}
			}			
		},1000);		
	};
	
	var store;
	function saveURLs(){
		
		if(store.indexOfId($('serverURL').value) ==-1){
			store.add([new Ext.data.Record({'id':$('serverURL').value,'name':$('serverURL').value})]);			
		}
		var urls="";
		var len=store.data.items.length;
		for(var i=0; i<len; i++){
			if(i<len-1){
				urls=urls+store.data.items[i].data.id+";";
			}else{
				urls=urls+store.data.items[i].data.id;
			}
		}		
		SynchronizationActionInterface.saveURLs(urls,function(){});
	};	
	
	function show() {
		Ext.QuickTips.init();
   		var recordType = Ext.data.Record.create([	  
		  	{name:"id"},{name:"name"}  
		  ]);

	    store = new Ext.data.Store({
	    proxy: new Ext.data.DWRProxy(SynchronizationActionInterface.getSavedURLs, false),
	    reader: new Ext.data.ListRangeReader( 
				{id:'id', totalProperty:'totalSize'}, recordType),
	    remoteSort: false
	  });
	  
  	    store.load({params:{start:0, limit:22}, arg:[]});
		var topPanel = new Ext.form.FormPanel(
		{
			region : 'center',
			border : false,
			bodyStyle : "padding: 8px; background-color: transparent;",
			labelAlign : "left",
			labelWidth : 150,
			autoScroll : true,
			defaultType : "textfield",
			buttonAlign : 'left',
			items : [ 
			          new Ext.form.FieldSet( {
				title : amalto.SynchronizationAction.SynchronizationActionLocal.get('LABEL_SYSTEM_INFO_GROUP'),
				autoHeight : true,
				defaultType : 'textfield',
				items : [ 
						{
							fieldLabel : amalto.SynchronizationAction.SynchronizationActionLocal.get('LABEL_SERVER_URL'),	
						    store: store,
						    displayField:'name',
						    typeAhead: true,
						    id : 'serverURL',
						    mode: 'local',
						    forceSelection: false,
						    triggerAction: 'all',						    
						    selectOnFocus:true,
							xtype:'combo',
							width : 400
						},
						{
					fieldLabel : amalto.SynchronizationAction.SynchronizationActionLocal.get('LABEL_USER_NAME'),
					id : 'username',
					selectOnFocus:true,
					allowBlank:false,					
					width : 400
				}, {
					fieldLabel :  amalto.SynchronizationAction.SynchronizationActionLocal.get('LABEL_PASSWORD'),
					id : 'password',
					selectOnFocus:true,
					allowBlank:false,
					inputType:'password',
					width : 400,
			        listeners:{
			        	'blur': function(){										
							initSyncNames();
			    		}
					}
				}]
			}),
			new Ext.Panel({
					width : 400,
					border:false,
					html:'<div> '+ amalto.SynchronizationAction.SynchronizationActionLocal.get('LABEL_SYNCHRONIZATION_NAME') + '    ' + '<select id="syncName" ></select></div>' 
				}) 
			
			, {
				id : 'sync_status',
				xtype : 'box',
				autoEl : {
					cn : ''
				}
			} ],
			buttons : [ 							
					{	
						xtype:'button',
						id:'startFullButton',
						text : '<b>' + amalto.SynchronizationAction.SynchronizationActionLocal.get('START_FULL_BUTTON') + '</b>',
						handler : function() {
							var syncInfo=getSyncInfo();
							if(syncInfo){
								saveURLs();
								SynchronizationActionInterface.startFull(refreshStatus(syncInfo),syncInfo);
							}
						},
						tooltip : amalto.SynchronizationAction.SynchronizationActionLocal.get('TOOLTIP_START_SYNCHRONIZATION')
					},
					{	xtype:'button',
						id:'startDifferentButton',
						text : '<b>' + amalto.SynchronizationAction.SynchronizationActionLocal.get('START_DIFFERENT_BUTTON') + '</b>',
						handler : function() {
							var syncInfo=getSyncInfo();
							if(syncInfo){
								saveURLs();
								SynchronizationActionInterface.startDifferent(refreshStatus(syncInfo),syncInfo);
							}
						},
						tooltip : amalto.SynchronizationAction.SynchronizationActionLocal.get('TOOLTIP_START_DIFFERENCE_SYNCHRONIZATION')
					},
					{	xtype:'button',
						id:'stopButton',
						text : '<b>' + amalto.SynchronizationAction.SynchronizationActionLocal.get('STOP_BUTTON') + '</b>',
						disabled:true,
						handler : function() {
							var syncInfo=getSyncInfo();
							if(syncInfo)
							SynchronizationActionInterface.stop(refreshStatus(syncInfo),syncInfo);
						},
						tooltip : amalto.SynchronizationAction.SynchronizationActionLocal.get('TOOLTIP_STOP_SYNCHRONIZATION')
					},
					{	xtype:'button',
						id:'resetButton',
						text : '<b>' + amalto.SynchronizationAction.SynchronizationActionLocal.get('RESET_BUTTON') + '</b>',
						disabled:true,
						handler : function() {
							var syncInfo=getSyncInfo();
							if(syncInfo)
							SynchronizationActionInterface.reset(refreshStatus(syncInfo),syncInfo);
						},
						tooltip : amalto.SynchronizationAction.SynchronizationActionLocal.get('TOOLTIP_RESET_SYNCHRONIZATION')
					}
					],
			listeners:{
				'render':function(){
					//if(Cookies.get('serverURL'))Ext.getCmp('serverURL').setValue(Cookies.get('serverURL'));
					//if(Cookies.get('username'))Ext.getCmp('username').setValue(Cookies.get('username'));
					
				}
			}		
		});
		
		 mainPanel = new Ext.Panel( {
			bodyStyle : 'padding:5px 5px 1',
			id:'synchronizationaction',
			border : true,
			closable:true,
			title : amalto.SynchronizationAction.SynchronizationActionLocal.get('MAIN_PANEL_TITLE'),
			collapsible : true,
			items : [ topPanel ]
		});
				
	};
	var mainPanel;

   function showSync(){
    	var tabPanel = amalto.core.getTabPanel();
    	if(tabPanel.getItem('synchronizationaction') == undefined){
			show();			
    	}
    	 //refreshStatus();
		  tabPanel.add(mainPanel);
		  mainPanel.show();
		  //syncPanel.doLayout();
		  amalto.core.doLayout();   	
    };
    
    return {
    	//public 
        init : function(){
    	amalto.SynchronizationAction.SynchronizationActionLocal.init();
    		showSync();		  
        }
    };
}();


//Ext.onReady(SynchronizationAction.init, SynchronizationAction, true);