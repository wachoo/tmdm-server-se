
/********************************************************************
 * name space : {companyName}.{context}.{application}Local
 ********************************************************************/
amalto.updatereport.UpdateReportLocal=function(){
	    
    
    var locale_en;
    var locale_fr;
    var locales;
    var localeMap;
    
    return {
        init : function(){
        	
		    /********************************************************************
			 * Localization custom begin
			 ********************************************************************/
			 
	    	locale_en = {
				'dataCluster' : 'Data Container',
				'dataModel' : 'Data model',
				'concept' : 'Entity',
				'key' : 'Key',
				'revisionID' : 'Revision ID',
				'operationType' : 'OperationType',
				'timeInMillis' : 'Operation time',
				'source' : 'Source',
				'userName' : 'User name',
				'title':'Journal',
				'start_date':'Start Date',
				'end_date': 'End Date',
				'searchPanel_tile':'Search Panel',
				'reset':'Reset',
				'search':'search',
				'emptyMsg':'No data to display',
				'lines_per_page':'Number of lines per page',
				'displayMsg':'Displaying items {0} - {1} of {2}'
			};

			locale_fr = {
				'dataCluster' : 'Data Container',
				'dataModel' : 'Modèle de données',
				'concept' : 'Entité',
				'key' : 'Clé',
				'revisionID' : 'ID de révision',
				'operationType' : 'Type opération',
				'timeInMillis' : 'Date opération',
				'source' : 'Source',
				'userName' : 'Utilisateur',
				'title':'Journal',
				'start_date':'Date de début',
				'end_date': 'Date de fin',
				'searchPanel_tile':'Onglet de recherche',
				'reset':'Reset',
				'search':'Recherche',
				'emptyMsg':'No data to display',
				'lines_per_page':'Number of lines per page',
				'displayMsg':'Displaying items {0} - {1} of {2}'
			};
                 
            /*******************************************************************
			 * Localization custom end
			 ******************************************************************/     
                   
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
