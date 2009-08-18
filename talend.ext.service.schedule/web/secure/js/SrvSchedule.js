/*
 * @include  "/com.amalto.webapp.core/web/secure/ext.ux/DWRProxy.js"
 * @include  "/com.amalto.webapp.core/web/secure/js/core.js"
 */
 
amalto.namespace("amalto.SrvSchedule");

amalto.SrvSchedule.SrvSchedule = function () {
	
    loadResource("/SrvSchedule/secure/js/ScheduleManager.js", "");
    
    loadResource("/SrvSchedule/secure/js/SchedulePlan.js", "");
    
    var srvScheduleManager;
    
    function initUIAndData(){
		var tabPanel = amalto.core.getTabPanel();
		srvScheduleManager = tabPanel.getItem('SrvScheduleManager');
		
		if(srvScheduleManager == undefined){
			srvScheduleManager=new amalto.SrvSchedule.ScheduleManager();			
			tabPanel.add(srvScheduleManager);
		}
        
        srvScheduleManager.show();
		srvScheduleManager.doLayout();
		amalto.core.doLayout();
		
		srvScheduleManager.initData();
    };
	
	function openSrvScheduleManagePanel(){
		//init UI
		initUIAndData();
		
		
	};
	
 	return {
 		
		init: function() {openSrvScheduleManagePanel();}
		
 	}
}();
