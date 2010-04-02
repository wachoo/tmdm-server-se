
/********************************************************************
 * name space : {companyName}.{context}.{application}Local
 ********************************************************************/
amalto.hierarchical.HierarchicalViewLocal=function(){
	    
    
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
	    		  'Hierarchical_View_Title':'Grouping Hierarchy',
	    		  'Search_Panel_Title':'Search Panel',
	    		  'Search_Field_Entity':'Entity',
	    		  'Search_Field_Pivot':'Pivot',
	    		  'Search_Field_Title':'Display Field',
	    		  'Search_Field_Filters':'Filters',
	    		  'Search_Field_Filters_Column_Field':'Field',
	    		  'Search_Field_Filters_Column_Operator':'Operator',
	    		  'Search_Field_Filters_Column_Value':'Value',
	    		  'Search_Field_Filters_Column_Delete':'Delete',
	    		  'Filter_Operation_CONTAINS':'contains the word(s)',
	    		  'Filter_Operation_EQUALS':'is equal to',
	    		  'Filter_Operation_NOT_EQUALS':'is not equal to',
	    		  'Filter_Operation_GREATER_THAN':'is greater than',
	    		  'Filter_Operation_GREATER_THAN_OR_EQUAL':'is greater or equals',
	    		  'Filter_Operation_LOWER_THAN':'is lower than',
	    		  'Filter_Operation_LOWER_THAN_OR_EQUAL':'is lower or equals',
	    		  'Filter_Operation_STARTSWITH':'contains a word starting with',
	    		  'Filter_Operation_STRICTCONTAINS':'contains the sentence',
	    		  'Button_Search':'Run Search',
	    		  'Button_Save_Report':'Save Report',
	    		  'Button_Load_Report':'Load Report',
	    		  'Button_Add_Filter':'Add Filter',
	    		  'Button_Save_Changes':'Apply Changes',
	    		  'Button_Cancel_Changes':'Cancel Changes',
	    		  'Button_Sort_Order':'Sort Order',
	    		  'Sort_Choose_Field':'Choose Fields',
	    		  'Sort_Choose_Order':'Choose Order',
	    		  'Sort_Ascending':'Ascending',
	    		  'Sort_Descending':'Descending',
	    		  'Records_MAX_SIZE':'Number of records (-1 = no limit):',
	    		  'Tree_Root_Default_Name':'Result',
	    		  'Messagebox_Info':'Information',
	    		  'Messagebox_Error':'Error',
	    		  'Messagebox_Status':'Status',
	    		  'Messagebox_Exception':'Exception',
	    		  'Messagebox_Prompt_Begin':'Please input ',
	    		  'Messagebox_Prompt_End':'first ',
	    		  'Message_Record_Changes_False':'This change has not affected the actual data item! ',
	    		  'Message_Missing_Entity':'Please select a \'Entity\' First! ',
	    		  'Message_Missing_Title':'Please select a \'Display Field\' First! ',
	    		  'Messagebox_Report_Empty':'Please create a new report or load a alreay exist report first! ',
	    		  'Messagebox_Report_Name_Empty':'Report Name can not be empty! ',
	    		  'Messagebox_Report_Save_Success':'The hierarchical-report has been saved successfully! ',
	    		  'Messagebox_Report_Save_Fail':'Saved a hierarchical-report failed! ',
	    		  'Message_No_Result_Set':'Please get a \'Result Set\' First! ',
	    		  'Messagebox_Progress_Saving_Title':'Saving...',
	    		  'Messagebox_Progress_Saving_Content':'Saving your data, please wait...',
	    		  'Load_Report_Title':'Load Report',
	    		  'Reports_Title':'Reports',
	    		  'Load_Report_Button':'Load'
                 };
                 
            locale_fr= {     
            	  'Hierarchical_View_Title':'Groupement Hierarchy',
  	    		  'Search_Panel_Title':'Critères de recherche',
  	    		  'Search_Field_Entity':'Entité',
  	    		  'Search_Field_Pivot':'Pivot',
  	    		  'Search_Field_Title':'Champ affiché',
  	    		  'Search_Field_Filters':'Filtres',
  	    		  'Search_Field_Filters_Column_Field':'Champ',
  	    		  'Search_Field_Filters_Column_Operator':'Opérateur',
  	    		  'Search_Field_Filters_Column_Value':'Valeur',
  	    		  'Search_Field_Filters_Column_Delete':'Supprimer',
  	    		  'Filter_Operation_CONTAINS':'contient les mot(s)',
  	    		  'Filter_Operation_EQUALS':'est égal à',
  	    		  'Filter_Operation_NOT_EQUALS':'n\'est pas égal à',
  	    		  'Filter_Operation_GREATER_THAN':'est supérieur à',
  	    		  'Filter_Operation_GREATER_THAN_OR_EQUAL':'est supérieur ou égal à',
  	    		  'Filter_Operation_LOWER_THAN':'est inférieur à',
  	    		  'Filter_Operation_LOWER_THAN_OR_EQUAL':'est inférieur ou égal à',
  	    		  'Filter_Operation_STARTSWITH':'contient un mot commençant par',
  	    		  'Filter_Operation_STRICTCONTAINS':'contient la phrase',
  	    		  'Button_Search':'Lancer la recherche',
  	    		  'Button_Save_Report':'Sauve ce rapport',
  	    		  'Button_Load_Report':'Charge un rapport',
  	    		  'Button_Add_Filter':'Ajouter un filtre',
  	    		  'Button_Save_Changes':'Appliquer les changements',
  	    		  'Button_Cancel_Changes':'Annuler les changements',
  	    		  'Button_Sort_Order':'Tri',
  	    		  'Sort_Choose_Field':'Champs',
  	    		  'Sort_Choose_Order':'Sens',
  	    		  'Sort_Ascending':'Croissant',
  	    		  'Sort_Descending':'Décroissant',
  	    		  'Records_MAX_SIZE':'Nombre d\'enregistrements (-1 = illimité):',
  	    		  'Tree_Root_Default_Name':'Résultat',
  	    		  'Messagebox_Info':'Information',
  	    		  'Messagebox_Error':'Erreur',
  	    		  'Messagebox_Status':'Statut',
  	    		  'Messagebox_Exception':'Exception',
  	    		  'Messagebox_Prompt_Begin':'Les éléments suivants : ',
  	    		  'Messagebox_Prompt_End':'doivent obligatoirement être renseignés ',
  	    		  'Message_Record_Changes_False':'This change has not affected the actual data item! ',
  	    		  'Message_Missing_Entity':'Vous devez d\'abord choisir une \'Entité\'. ',
  	    		  'Message_Missing_Title':'Vous devez d\'abord choisir un \'Champ affiché\' ',
  	    		  'Messagebox_Report_Empty':'Vous devez soit créer un nouveau rapport, soit charger un rapport existant ',
  	    		  'Messagebox_Report_Name_Empty':'Le nom du rapport doit être renseigné ',
  	    		  'Messagebox_Report_Save_Success':'Ce rapport a été sauvegardé sur le serveur ',
  	    		  'Messagebox_Report_Save_Fail':'Une erreur s\'est produite lors de la sauvegarde du rapport ',
  	    		  'Message_No_Result_Set':'Please get a \'Result Set\' First! ',
  	    		  'Messagebox_Progress_Saving_Title':'Sauvegarde en cours...',
  	    		  'Messagebox_Progress_Saving_Content':'Vos changements sont en train d\'être appliqués, patientez un instant...',
  	    		  'Load_Report_Title':'Chargement d\'un rapport',
  	    		  'Reports_Title':'Rapports',
  	    		  'Load_Report_Button':'Charger'
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
