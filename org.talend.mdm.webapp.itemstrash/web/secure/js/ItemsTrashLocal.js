
/********************************************************************
 * name space : {companyName}.{context}.{application}Local
 ********************************************************************/
amalto.ItemsTrash.ItemsTrashLocal=function(){
	    
    
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
	    		  'dataClusterName':'Data Container',   
	              'revisionID':'RevisionID',
                  'conceptName':'Entity',
                  'Ids':'Ids',
                  'partPath':'Part path',
                  'UserName':'User Name',
                  'Date':'Date',
                  'delete':'Delete',
                  'restore':'Restore',
                  'title':'Recycle bin',
                  'search':'search',
                  'serarch_tooltip':'First four fields can be input as search text',
                  'delete_conform':'Are you sure to delete the Record physically?',
                  'restore_conform':'Are you sure to restore the Record?',
                  'lines_per_page':'Number of lines per page'
                 };
                 
            locale_fr= {     
      			  'dataClusterName':'Data Container',   
	              'revisionID':'ID de révision',
                  'conceptName':'Entité',
                  'Ids':'Identifiants',
                  'partPath':'Chemin partiel',
                  'UserName':'Utilisateur',
                  'Date':'Date',
                  'delete':'Supprimer',
                  'restore':'Restaurer',
                  'title':'Corbeille',
                  'search':'Recherche',
                  'serarch_tooltip':'Les quatre premiers champs sont des champs de recherche texte',
                  'delete_conform':'Are you sure to delete the Enregistrement physically?',
                  'restore_conform':'Are you sure to restore the Enregistrement?',
                  'lines_per_page':'Number of lines per page'
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
