
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
				'dataModel' : 'Data Model',
				'concept' : 'Entity',
				'key' : 'Key',
				'revisionID' : 'Revision ID',
				'operationType' : 'Operation Type',
				'timeInMillis' : 'Operation Time',
				'source' : 'Source',
				'userName' : 'User Name',
				'title':'Journal',
				'start_date':'Start Date',
				'end_date': 'End Date',
				'searchPanel_tile':'Search Panel',
				'reset':'Reset',
				'search':'Search',
				'emptyMsg':'No data to display',
				'lines_per_page':'Number of lines per page',
				'displayMsg':'Displaying items {0} - {1} of {2}',
				'export':'Export'
			};

			locale_fr = {
				'dataCluster' : 'Data Container',
				'dataModel' : 'Data Model',
				'concept' : 'Entité',
				'key' : 'Clé',
				'revisionID' : 'ID de révision',
				'operationType' : 'Type d\'opération',
				'timeInMillis' : 'Date d\'opération',
				'source' : 'Source',
				'userName' : 'Utilisateur',
				'title':'Journal',
				'start_date':'Date de début',
				'end_date': 'Date de fin',
				'searchPanel_tile':'Panneau de recherche',
				'reset':'Réinitialise',
				'search':'Recherche',
				'emptyMsg':'Aucun donnée',
				'lines_per_page':'Nombre de lignes par page',
				'displayMsg':'Affichage enregistrements {0} - {1} sur {2}',
				'export':'Export'
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
