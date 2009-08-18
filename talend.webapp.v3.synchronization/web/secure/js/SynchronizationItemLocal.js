
/********************************************************************
 * name space : {companyName}.{context}.{application}Local
 ********************************************************************/
amalto.SynchronizationItem.SynchronizationItemLocal=function(){
	    
    
    var locale_en;
    var locale_fr;
    var locales;
    var localeMap;
    
    return {
        init : function(){
        	
		    /********************************************************************
			 * Localization custom begin
			 ********************************************************************/
			 
	    	locale_en= {
	    		  'GRID_TITLE':'Synchronization Item',   
	              'BUTTON_SEARCH':'Search',
                  'GRID_COLUMN_1':'Item PK',
                  'GRID_COLUMN_2':'Local Revision ID',
                  'GRID_COLUMN_3':'Last Run Synchronization Name',
                  'GRID_COLUMN_4':'Status',
                  'LABEL_DISPLAYING':'Displaying items',
                  'LABEL_OF':'of',
                  'LABEL_NO_RESULT':'No result',
                  'LABEL_LINES_PER_PAGE':'Number of lines per page',
                  
                  'SYNC_PANEL_TITLE':'Synchronization conflict data',
                  'RESOLVE_BUTTON':'Resolve',
                  'DIALOG_CONFIRM_TITLE':'Confirm',
                  'RESOLVE_BUTTON_CONFIRM':'Are you sure you want to <b>Resolve</b> the conflict data now ?',
                  'RESOLVE_ACTION_TITLE':'Saving...',
                  'RESOLVE_ACTION_WAITING':'Saving your data, please wait...',
                  'DIALOG_STATUS_TITLE':'Status',
                  'DIALOG_STATUS_MESSAGE':'Changes saved successfully.',
                  'TARGET_TREE_TITLE':'Local data',
                  'REMOTE_TREE_TITLE':'Remote data',
                  'ADD_BUTTON':'Add',
                  'ADD_BUTTON_CONFIRM':'Are you sure you want to <b>Add</b> remote tree selected item as child of target tree selected Item ?',
                  'DIALOG_WARNING_TITLE':'Warning',
                  'ADD_BUTTON_WARNING':'You must select one item of <b>remote</b> tree and <b>local</b> tree!',
                  'ADD_BUTTON_TOOLTIP':'<b>Add</b><br/>Add remote tree selected item as child of local tree selected item',
                  'REPLACE_BUTTON':'Replace',
                  'REPLACE_BUTTON_CONFIRM':'Are you sure you want to <b>Replace</b> local selected Item with remote selected Item ?',
                  'REPLACE_BUTTON_WARNING':'You must select one item of remote tree and local tree!',
                  'REPLACE_BUTTON_TOOLTIP':'<b>Replace</b><br/>Replace remote tree selected item with local tree selected item',
                  'LEFT_ACTION':'Upper level',
                  'RIGHT_ACTION':'Lower level',
                  'UP_ACTION':'Up',
                  'DOWN_ACTION':'Down',
                  'DELETE_ACTION':'Delete',
                  'DELETE_ACTION_CONFIRM':'Are you sure you want to delete the selected item?',
                  'REMOTE_COMBO_EMPTY':'Select a remote data...'
                 };
                 
            locale_fr= {     
                  'GRID_TITLE':'Item de Synchronization',   
	              'BUTTON_SEARCH':'Rechercher',
                  'GRID_COLUMN_1':'Clé primaire',
                  'GRID_COLUMN_2':'ID Révision local',
                  'GRID_COLUMN_3':'Nom dernière synchronisation',
                  'GRID_COLUMN_4':'Etat',
                  'LABEL_DISPLAYING':'Items affichés',
                  'LABEL_OF':'sur',
                  'LABEL_NO_RESULT':'Pas de résultat',
                  'LABEL_LINES_PER_PAGE':'Nombre de lignes par page',
                  
                  'SYNC_PANEL_TITLE':'Synchronization conflict data',
                  'RESOLVE_BUTTON':'Résoudre',
                  'DIALOG_CONFIRM_TITLE':'Confirmer',
                  'RESOLVE_BUTTON_CONFIRM':'Are you sure you want to <b>Resolve</b> the conflict data now ?',
                  'RESOLVE_ACTION_TITLE':'Saving...',
                  'RESOLVE_ACTION_WAITING':'Saving your data, please wait...',
                  'DIALOG_STATUS_TITLE':'Status',
                  'DIALOG_STATUS_MESSAGE':'Changes saved successfully.',
                  'TARGET_TREE_TITLE':'Local data',
                  'REMOTE_TREE_TITLE':'Remote data',
                  'ADD_BUTTON':'Add',
                  'ADD_BUTTON_CONFIRM':'Are you sure you want to <b>Add</b> remote tree selected item as child of target tree selected Item ?',
                  'DIALOG_WARNING_TITLE':'Warning',
                  'ADD_BUTTON_WARNING':'You must select one item of <b>remote</b> tree and <b>local</b> tree!',
                  'ADD_BUTTON_TOOLTIP':'<b>Add</b><br/>Add remote tree selected item as child of local tree selected item',
                  'REPLACE_BUTTON':'Replace',
                  'REPLACE_BUTTON_CONFIRM':'Are you sure you want to <b>Replace</b> local selected Item with remote selected Item ?',
                  'REPLACE_BUTTON_WARNING':'You must select one item of remote tree and local tree!',
                  'REPLACE_BUTTON_TOOLTIP':'<b>Replace</b><br/>Replace remote tree selected item with local tree selected item',
                  'LEFT_ACTION':'Upper level',
                  'RIGHT_ACTION':'Lower level',
                  'UP_ACTION':'Up',
                  'DOWN_ACTION':'Down',
                  'DELETE_ACTION':'Delete',
                  'DELETE_ACTION_CONFIRM':'Are you sure you want to delete the selected item?',
                  'REMOTE_COMBO_EMPTY':'Select a remote data...'
                 };
                 
            /********************************************************************
			 * Localization custom end
			 ********************************************************************/     
                   
            locales={'en':locale_en,'fr':locale_fr};
           
            localeMap=initLocaleMap(language,locales);
            
        },
        
        
        get: function(keys) {
        	var expr="localeMap.get(";
        	for(var index=0; index<arguments.length; index++) {
        		if(index<arguments.length-1){
        			expr+=("'"+arguments[index]+"',");
        		}else{
        			expr+=("'"+arguments[index]+"'");
        		}
        	}
        	expr+=(")");
        	return eval(expr);
        } 
         	
    };
}();
