/*
 * @include  "/com.amalto.webapp.core/web/secure/ext.ux/DWRProxy.js"
 * @include  "/com.amalto.webapp.core/web/secure/js/core.js"
 */
Ext.namespace('amalto.universemanager');
amalto.universemanager.UniverseWizardPanel = function(config) {
	Ext.applyIf(this, config);
	this.declareVariables();
	this.initUIComponents();
	amalto.universemanager.UniverseWizardPanel.superclass.constructor.call(this);
};
Ext.extend(amalto.universemanager.UniverseWizardPanel, Ext.Panel, {
	
	
	declareVariables : function() {
		this.beginStep=0;
	    this.endStep=5;//TODO[extends]: change end step
	    //status var
	    this.step=this.beginStep
	    
	    //wizard pages
	    this.basicPage=new amalto.universemanager.BasicInfoOfUniverseWizardPage({'parentContainer':this});//required start page with context 
	    this.itemPage=new amalto.universemanager.SelectItemsWizardPage();
	    this.pubPage=new amalto.universemanager.SelectRoutingRuleAndTransformerWizardPage();
	    this.spPage=new amalto.universemanager.SelectStoredProcedureWizardPage();
	    this.aclPage=new amalto.universemanager.DefineAccessControlListWizardPage();
	    this.summaryPage=new amalto.universemanager.WizzardSummaryPage();
	    //TODO[extends]: add more wizzard pages
	},
		
	initUIComponents : function() {
		this.wizardPanel=new Ext.Panel({
				border : false,
				layout : "card",
				activeItem: this.beginStep, // index or id
				bbar: ['->',{
					text : "Step",
					xtype : "tbtext"
				},{
					name : "card-status",
					xtype : "textfield",
					value : this.step,
					width : 25,
					listeners: {
		                	'specialkey': function(a, e) {
		                		this.onJumpStep(a, e);
				             }.createDelegate(this)
		                }
				},{
					text : "of " + (this.endStep+1),
					xtype : "tbtext"
				},{
				    xtype : "tbspacer"
			    }, {
				    id: 'card-prev',
				    text: '&laquo; Previous',
				    handler : function(button, event) {
					    this.onPrevClick(button, event);
				    }.createDelegate(this)
				},{
				    id: 'card-next',
				    text: 'Next &raquo;',
				    handler : function(button, event) {
					    this.onNextClick(button, event);
				    }.createDelegate(this)
				},{
				    id: 'cards-execute',
				    text: 'Execute',
				    handler : function(button, event) {
					    this.onExecuteClick(button, event);
				    }.createDelegate(this)
				}],
				items: [{
				    id: 'card-0',
				    items: [this.basicPage]
				},{
				    id: 'card-1',
				    items: [this.itemPage]
				},{
				    id: 'card-2',
				    items: [this.pubPage]
				},{
				    id: 'card-3',
				    items: [this.spPage]
				},{
				    id: 'card-4',
				    items: [this.aclPage]
				},{
				    id: 'card-5',
				    items: [this.summaryPage]
				}]//TODO[extends]: add more wizzard pages
			});
			
		Ext.apply(this, {
			layout : "fit",
			title : "Universe Wizard",
			id : "UniverseWizardPanel",
			closable:true,
			items : [this.wizardPanel]
		});
	},
	
	initData:function(){
		
		Ext.MessageBox.show({
           msg: 'Initialize wizzard pages, please wait... ',
           progressText: 'Initializing...',
           width:300,
           wait:true,
           waitConfig: {interval:200}
        });
        
        //reset session
        DWREngine.setAsync(false); 
        UniverseManagerInterface.resetUniverseQuickStartStoreAndContext(function(){
	    	   //nothing
		});
		DWREngine.setAsync(true);
		
        //reset data
		this.basicPage.updateUniverseNameField('');
		this.basicPage.updateUniverseDescField('');
		this.basicPage.updateSourceUniverseField('');
		this.resetStoreFieldsWithoutBasic();
		
		//status
		this.updateChangeStepStatus();
		
		Ext.MessageBox.hide();

	},
	
	resetStoreFieldsWithoutBasic:function(){
		//TODO[extends]: reset store fields text
		this.itemPage.resetData();
		this.pubPage.resetData();
		this.spPage.resetData();
		this.aclPage.resetData();
		
	},
	
	reloadContextAndChangeData : function(newSourceUniverseName){

		Ext.MessageBox.alert('Info', "'Source Universe Name' has been changed. </BR>All data fields will be reset. ",function showResult(btn)
	    {  
	      this.doReloadContextAndChangeData(btn,newSourceUniverseName);
	    }.createDelegate(this)); 
	},
	
	doReloadContextAndChangeData:function(btn,newSourceUniverseName){
		if(btn=="ok"){
	         Ext.MessageBox.show({
	           msg: 'Updating context and Resetting data, please wait... ',
	           progressText: 'Updating...',
	           width:300,
	           wait:true,
	           waitConfig: {interval:200}
	         });
	        
	         DWREngine.setAsync(false); 
	         UniverseManagerInterface.resetUniverseContextInSession(newSourceUniverseName,function(){});
			 DWREngine.setAsync(true);
			 
			 //TODO[extends]: update client side context-related-data
		     this.itemPage.refreshItemBoxsTree();
		     this.pubPage.reloadStores();
		     this.aclPage.reloadStore();
		     
		     DWREngine.setAsync(false);
		     UniverseManagerInterface.resetUniverseStoreWithoutBasicInfos(function(){});
			 DWREngine.setAsync(true);
		     
		     this.resetStoreFieldsWithoutBasic();
		     
		     Ext.MessageBox.hide();
	       }
	},
	
	onPrevClick:function(button, event){
		var fromStep=this.step;
		var toStep=this.step-1;
		this.changeStep(fromStep,toStep);
	},
	
	onNextClick:function(button, event){
		var fromStep=this.step;
		var toStep=this.step+1;
		this.changeStep(fromStep,toStep);
	},
	
	updateChangeStepStatus:function(){
		
		DWRUtil.setValue("card-status",this.step+1);
		
		if(this.step<=this.beginStep){
			Ext.getCmp("card-prev").setDisabled(true);
			Ext.getCmp("card-next").setDisabled(false);
		}else if(this.step>=this.endStep){
			Ext.getCmp("card-prev").setDisabled(false);
			Ext.getCmp("card-next").setDisabled(true);
		}else {
			Ext.getCmp("card-prev").setDisabled(false);
			Ext.getCmp("card-next").setDisabled(false);
		}
		
		if(this.step==this.endStep){
			Ext.getCmp("cards-execute").setDisabled(false);
		}else{
			Ext.getCmp("cards-execute").setDisabled(true);
		}
		
	},
	
	
	onExecuteClick:function(button, event){
		//SAVE AND EXECUTE IN BACKEND
		Ext.MessageBox.show({
	           msg: 'Creating new universe and synchronizing customized data , please wait... ',
	           progressText: 'Creating...',
	           width:300,
	           wait:true,
	           waitConfig: {interval:200}
	         });
	         
		UniverseManagerInterface.createNewUniverse(function(status){
		    if(status==true){
		    	
		    	var timer=setInterval(function(){
					UniverseManagerInterface.getStatus(function(syncStatus){
						if(syncStatus){
							if('COMPLETED' == syncStatus.value){
								clearInterval(timer);
								Ext.MessageBox.hide();
								Ext.MessageBox.alert('Info', "Congratulations, new universe has been created successfully! ",function showResult(btn)
							    {  
							       if(btn=="ok"){
							       	 amalto.core.getTabPanel().remove('UniverseWizardPanel');
							       }
							    });
							}else if('FAILED' == syncStatus.value||'STOPPING' == syncStatus.value||'DOES NOT EXIST' == syncStatus.value){
								clearInterval(timer);
								Ext.MessageBox.hide();
								Ext.MessageBox.alert('Sorry', "Exception happened while synchronizing data: "+syncStatus.message);
							}
						}			
					});
					
				},1000);
		    	
		    }else{
		    	Ext.MessageBox.hide();
		    	Ext.MessageBox.alert('Sorry', "Failed to create new universe! ");
		    }
		});

	},
	
	onJumpStep:function(a, e){
		
		var cstatus=DWRUtil.getValue("card-status");
		if(cstatus-1<this.beginStep||cstatus-1>this.endStep){
			Ext.MessageBox.alert('Sorry', "Out of range! ");
			DWRUtil.setValue("card-status",this.step+1);
			return;
		}
		
		var fromStep=this.step;
		var toStep=cstatus-1;
		this.changeStep(fromStep,toStep);
	},
	
	changeStep:function(fromStep,toStep){
		var invalidReport=this.validatePreviousWizzardPages(toStep);
		if(invalidReport==""){
			//commit fromPage
			this.commitFromWizzardPage(fromStep);//synch
			
			this.wizardPanel.getLayout().setActiveItem(toStep);
			this.step=toStep;
			this.updateChangeStepStatus();
			
			//refresh toPage
			this.refreshToWizzardPage(toStep);//synch
		}else{
			Ext.MessageBox.alert('Sorry', invalidReport);
		}
	},
	
	validatePreviousWizzardPages:function(curStep){
		
		var invalidReport="";
		
		for (var index = 0; index < curStep; index++) {
			//TODO[extends]: add more wizzard page validate here
			switch(index)
			{
			case 0:
			    var partialReport="";
				
				var universeName=this.basicPage.getUniverseNameFieldValue();
				//var universeDesc=this.basicPage.getUniverseDescFieldValue();
				var sourceUniverse=this.basicPage.getSourceUniverseFieldValue();
				
				if(universeName=='')partialReport+="required: 'Universe Name'</br>";
				//if(universeDesc=='')partialReport+="required: 'Universe Description'</br>";
				if(sourceUniverse==''){
				   partialReport+="required: 'Source Universe'</br>";
				}else if(!this.basicPage.checkSourceUniverse()){
				   partialReport+="duplicate: 'Source Universe' name is already existed. </br>";
				}
				
				if(partialReport!=""){
				   invalidReport+="Step One is invalid!                                  </br>";
				   invalidReport+=partialReport;
				}
				break;
			default:
			}
		}
		
		return invalidReport;
	},
	
	commitFromWizzardPage:function(fromStep){
		//TODO[extends]: add more wizzard page commit action here
		switch(fromStep)
			{
			case 0: 
				break;	
			case 1:
			    this.itemPage.commitData();
				break;
			case 2:
			    this.pubPage.commitData();
			    break;
			case 3:	
			    this.spPage.commitData();
			    break;
			case 4:	
			    this.aclPage.commitData();
			    break;
			default:
		}
		
		
	},
	
	refreshToWizzardPage:function(toStep){
		//TODO[extends]: add more wizzard page refresh action here
		switch(toStep)
			{
			case 1:
			    this.itemPage.refreshData();
				break;
			case 2:	
			    this.pubPage.refreshData();
			    break;
			case 3:	
			    this.spPage.refreshData();
			    break;
			case 4:	
			    this.aclPage.refreshData();
			    break;
			case 5:	
			    this.summaryPage.reBuildSummary();
			    break;    
			default:
			}
	}
	
});


