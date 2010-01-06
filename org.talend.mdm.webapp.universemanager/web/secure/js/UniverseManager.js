/*
 * @include  "/com.amalto.webapp.core/web/secure/ext.ux/DWRProxy.js"
 * @include  "/com.amalto.webapp.core/web/secure/js/core.js"
 */
 
amalto.namespace("amalto.universemanager");

amalto.universemanager.UniverseManager = function () {
	
	loadResource("/universemanager/secure/js/TreeCheckNodeUI.js", "");
	
    loadResource("/universemanager/secure/js/UniverseManagerPanel.js", "");
    
    loadResource("/universemanager/secure/js/UniverseWizardPanel.js", "");
    
    //TODO had better change to dynamic load mode
    //TODO[extends]: import more wizzard pages here
    loadResource("/universemanager/secure/js/WizardPages/BasicInfoOfUniverseWizardPage.js","");
    loadResource("/universemanager/secure/js/WizardPages/SelectItemsWizardPage.js","");
    loadResource("/universemanager/secure/js/WizardPages/SelectRoutingRuleAndTransformerWizardPage.js","");
    loadResource("/universemanager/secure/js/WizardPages/SelectStoredProcedureWizardPage.js","");
    loadResource("/universemanager/secure/js/WizardPages/DefineAccessControlListWizardPage.js","");
    loadResource("/universemanager/secure/js/WizardPages/WizzardSummaryPage.js","");
    
    var universeManagerPanel;
    
    function initUI(){
    	
		var tabPanel = amalto.core.getTabPanel();
		universeManagerPanel = tabPanel.getItem('UniverseManagerPanel');
		
		if(universeManagerPanel == undefined){

			universeManagerPanel=new amalto.universemanager.UniverseManagerPanel();			
			tabPanel.add(universeManagerPanel);
		}
        
        universeManagerPanel.show();
		universeManagerPanel.doLayout();
		amalto.core.doLayout();
    };
	
	function browseUniverseManager(){
		//init UI
		initUI();
	};

 	return {
 		
		init: function() {browseUniverseManager();}
		
 	}
}();
