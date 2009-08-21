
/********************************************************************
 * name space : {companyName}.{context}.{application}Local
 ********************************************************************/
amalto.SynchronizationAction.SynchronizationActionLocal=function(){
	    
    
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
				  'LABEL_SYSTEM_INFO_GROUP':'Remote system information',
	    		  'LABEL_SERVER_URL':'Server URL',   
	              'LABEL_USER_NAME':'UserName',
                  'LABEL_PASSWORD':'Password',
                  'LABEL_SYNCHRONIZATION_NAME':'Synchronization Name:',
                  'START_FULL_BUTTON':'Start Full',
                  'START_DIFFERENT_BUTTON':'Start Different',
                  'STOP_BUTTON':'Stop',
                  'RESET_BUTTON':'Reset',
                  'TOOLTIP_START_SYNCHRONIZATION':'Start synchronization',
                  'TOOLTIP_START_DIFFERENCE_SYNCHRONIZATION':'Start Different synchronization',
                  'TOOLTIP_STOP_SYNCHRONIZATION':'Stop synchronization',
                  'TOOLTIP_RESET_SYNCHRONIZATION':'Reset synchronization',
                  'MAIN_PANEL_TITLE':'Synchronization Action'
                 };
                 
            locale_fr= {     
            		 'LABEL_SYSTEM_INFO_GROUP':'Informations du système distant',
            		 'LABEL_SERVER_URL':'URL du serveur',   
            		 'LABEL_USER_NAME':'Utilisateur',
                     'LABEL_PASSWORD':'Mot de passe',
                     'LABEL_SYNCHRONIZATION_NAME':'Synchronization Name:',
                     'START_FULL_BUTTON':'Synchro complète',
                     'START_DIFFERENT_BUTTON':'Synchro différentielle',
                     'STOP_BUTTON':'Stop',
                     'RESET_BUTTON':'Reset',
                     'TOOLTIP_START_SYNCHRONIZATION':'Start synchronization',
                     'TOOLTIP_START_DIFFERENCE_SYNCHRONIZATION':'Start Different synchronization',
                     'TOOLTIP_STOP_SYNCHRONIZATION':'Stop synchronization',
                     'TOOLTIP_RESET_SYNCHRONIZATION':'Reset synchronization',
                     'MAIN_PANEL_TITLE':'Action de Syschronization'
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
