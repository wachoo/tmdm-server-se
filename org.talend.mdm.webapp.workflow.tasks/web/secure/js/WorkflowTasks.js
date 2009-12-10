/*
 * @include  "/com.amalto.webapp.core/web/secure/ext.ux/DWRProxy.js"
 * @include  "/com.amalto.webapp.core/web/secure/js/core.js"
 */
 
amalto.namespace("amalto.workflowtasks");

amalto.workflowtasks.WorkflowTasks = function () {
	
	loadResource("/workflowtasks/secure/js/WorkflowTasksPanel.js", "");
	loadResource("/workflowtasks/secure/js/WorkflowTaskFormPanel.js", "");
	
    function initUIAndData(){
    	
		var tabPanel = amalto.core.getTabPanel();
	  	var workflowTasksPanel = tabPanel.getItem('WorkflowTasksPanel');
		
		if(workflowTasksPanel == undefined){
			workflowTasksPanel=new amalto.workflowtasks.WorkflowTasksPanel();			
			tabPanel.add(workflowTasksPanel);
		}
        
        workflowTasksPanel.show();
		workflowTasksPanel.doLayout();
		amalto.core.doLayout();
		
		workflowTasksPanel.initData();
		
    };
	
	
 	return {
 		
		init: function() {initUIAndData();}
		
 	}
}();
