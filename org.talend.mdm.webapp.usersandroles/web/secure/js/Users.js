amalto.namespace("amalto.usersandroles");

amalto.usersandroles.Users = function () {


/********************************************************************
 * USERS 
 ********************************************************************/	
	var MANAGE_USERS = {
	'en':'Users',
	'fr':'Utilisateurs'
	}
	var BUTTON_SAVE = {
	'fr':'Sauvegarder',
	'en':'Save'
	}	
	var MSG_LOADING={
	'fr':'Chargement...',
	'en':'Loading...'
	}	
	var  TITLE_SEARCH_PANEL =    {
    'fr' : 'Recherche',
    'en' : 'Search panel'
	}
	var  TITLE_SEARCH_RESULT =    {
    'fr' : 'Résultats',
    'en' : 'Search results'
	}
	var  LABEL_CRITERIA =    {
    'fr' : 'Critères de recherche',
    'en' : 'Search criteria'
	}
	var  BUTTON_SEARCH =    {
    'fr' : 'Rechercher',
    'en' : 'Search'
	}


	var BUTTON_CREATE_USER = {
		'fr':'Créer un utilisateur',
		'en':'Add a new user'
	}
	var TITLE_NEW_USER = {
		'fr':'Nouvel utilisateur',
		'en':'New user'
	}	
	
	var STATUS_ACTIVE = {
		'fr':'Actif',
		'en':'Active'
	}	
		
	var STATUS_INACTIVE = {
		'fr':'Inactif',
		'en':'Inactive'
	}	
		
	var USER_NAME =  {
	    'fr' : 'Nom',
	    'en' : 'Name'
	}	
	var USER_FIRSTNAME =  {
	    'fr' : 'Prénom',
	    'en' : 'First name'
	}
	var USER_ID =  {
	    'fr' : 'Identifiant',
	    'en' : 'Identifier'
	}
	var USER_ROLE =  {
	    'fr' : 'R&ocirc;le(s)',
	    'en' : 'Role(s)'
	}     
	var USER_DIVISION =  {
	    'fr' : 'Société',
	    'en' : 'Company'
	}
	var STATUS =  {
	    'fr' : 'Statut',
	    'en' : 'Status'
	}           
	var USER_UPDATE =  {
	    'fr' : 'Modif.',
	    'en' : 'Update'
	}  	
	var USER_DELETE =  {
	    'fr' : 'Effacer',
	    'en' : 'Delete'
	} 
	var USER_PASSWORD = {
	    'fr' : 'Mot de passe',
	    'en' : 'Password'
	}
	var USER_PASSWORD2 = {
	    'fr' : 'Confirmez le mot de passe',
	    'en' : 'Confirm password'
	}
	
	var BUTTON_ADD_ROLE = {
		'fr':'Ajouter le rôle',
		'en':'Add this role'
	}
	var BUTTON_DELETE_ROLE = {
		'fr':'Effacer le rôle',
		'en':'Delete this role'
	}
	var ALERT_EMPTY_INPUT = {
		'fr':'Remplir ce champ',
		'en':'This field cannot be empty'
	}
	
	var ALERT_SUCCESSFULLY_SAVED={
		'fr':'Sauvegardé avec succès',
		'en':'Successfully saved'
	}
	var ALERT_ERROR={
		'fr':'Une erreur est survenue. L\'utilisateur n\'a pas été sauvegardé\n' +
		'Voulez vous réessayer ?\n' +
		'Cliquer sur annuler pour fermer l\'onglet ou sur OK pour recommencer',
		'en':'An error occured. The user is not saved\n' +
		'Do you want to try again?\n' +
		'Click cancel to close tab or OK to try again'
	}
	
	var ALERT_INPUT_GIVEN_NAME={
	    'fr':'Veuillez renseignez le prénom',
	    'en':'The given name cannot be empty'
	}
	var ALERT_INPUT_FAMILY_NAME={
	    'fr':'Veuillez renseigner le nom de famille',
	    'en':'The family name cannot be empty'
	}
	var ALERT_INPUT_EMAIL={
	    'fr':'Veuillez renseigner l\'email',
	    'en':'The email cannot be empty'
	}
	var ALERT_INPUT_ROLES={
	    'fr':'Veuillez ajouter au moins un role',
	    'en':'Please add at least a role'
	}
	
	var ALERT_DIFFERENT_PASSWORDS = {
		'fr':'Les mots de passe doivent être identiques',
		'en':'Passwords must be identical'
	}

	var ROLES={
		'fr':'Rôles',
		'en':'Roles'
	}

	var UNIVERSE={
		'fr':'Univers par défault',
		'en':'Default Universe'
	}
 
	/********************************************************************
	 * Action BROWSE USERS
	 ********************************************************************/
	var gridUsers;
	var dataModel;
	var panel;
	
	var usersPanel;
	
	var userProfile=null;
	
	
	function browseUsers(){
		showUsersPanel();
		displayUsers();
	}
	
	function showUsersPanel() {

		var tabPanel = amalto.core.getTabPanel();
		if(tabPanel.getItem('users') == undefined){
			usersPanel = new Ext.Panel({
				id: 'users',
				title: MANAGE_USERS[language],
				deferredRender: false,
				layout:'border',
				autoScroll: false,
				border: false,
				bodyBorder:false,
				closable: true,
				items: 
				[	
					new Ext.Panel({
						id: 'users-list',
			    		region: 'north',
			    		layout:'fit',
						border: false,
						height:200,
						header:true,
						split:true,
						collapsible: true,
						bodyborder: true,
						tbar:[
							new Ext.form.TextField({
								id:'user-criteria',
								//emptyText:LABEL_CRITERIA[language],
								emptyText:'*',
								listeners: {
				                	'specialkey': function(a, e) {
							            if(e.getKey() == e.ENTER) {
					                		displayUsers();
							            } 
									}
				                }
							}),
							new Ext.Toolbar.Button({
								text:BUTTON_SEARCH[language],
								handler:displayUsers
							}),
							new Ext.Toolbar.Separator(),
							new Ext.Toolbar.Button({
								text:BUTTON_CREATE_USER[language],
								handler:createUser
							})
						]
					}),
					new Ext.FormPanel({
						id:'user-detail',
						region:'center',
						border:false,
						bodyBorder:false,
						autoScroll: true,	
						split:true,
						title:TITLE_NEW_USER[language],
						header:true,
						labelWidth:150,
						buttonAlign:'left',
						bodyStyle:'padding:5px',
						defaults: {labelSeparator:''},
						items:[
							{
								xtype:'textfield',
								id:'nUserName',
								fieldLabel:USER_ID[language],
								width:200
							},
							{
								xtype:'textfield',
								id:'nPassword1',
								inputType:'password',
								fieldLabel:USER_PASSWORD[language],
								width:200
							},
							{
								xtype:'textfield',
								id:'nPassword2',
								inputType:'password',
								fieldLabel:USER_PASSWORD2[language],
								width:200
							},
							{
								xtype:'textfield',
								id:'nGivenName',
								fieldLabel:USER_FIRSTNAME[language],
								width:200
							},
							{
								xtype:'textfield',
								id:'nFamilyName',
								fieldLabel:USER_NAME[language],
								width:200
							},
							{
								xtype:'textfield',
								id:'nRealEmail',
								fieldLabel:'Email',
								width:200
							},
							{
								xtype:'textfield',
								id:'nCompany',
								fieldLabel:USER_DIVISION[language],
								width:200
							},
							{
								xtype:'textfield',
								id:'nUniverse',
								fieldLabel:UNIVERSE[language],
								width:200
							},
							{
								xtype:'checkbox',
								id:'nEnabled',
								fieldLabel:STATUS_ACTIVE[language]
							},
							new Ext.form.FieldSet({
								title:ROLES[language],
								autoHeight:true,
										html:'<select id="sRoles" class="leftField" style="width:300" onchange="amalto.usersandroles.Users.changeSrolesTpl()"><option value="-1"></option></select>' +
											'<input  id="addRole" type="button" onclick="amalto.usersandroles.Users.addRoleInBox();" ' +
											'value="'+BUTTON_ADD_ROLE[language]+'"  title="Ajouter le role."/><br/>' +
											'<select id="nRoles" class="leftField" style="width:280" size="5"></select>' +
											'<input  id="deleteRole" type="button" onclick="amalto.usersandroles.Users.deleteRoleSelected();" value="'+BUTTON_DELETE_ROLE[language]+'"  title="Effacer le role."/>'


							})
							
						],
						buttons:[
							{
								text:BUTTON_SAVE[language],
								handler:saveUser
							}
						]
					})		    
				]
			});	
		}		
		tabPanel.add(usersPanel); 
		usersPanel.show();
		//usersPanel.doLayout();
		amalto.core.doLayout();
	}
	
	function changeSrolesTpl(){
        var tipBody=Ext.query("*[class=x-tip-body]",Ext.getDom('sRolesTpl'))[0];
		tipBody.innerHTML=$('sRoles').options[$('sRoles').selectedIndex].value;
        
	}
	
	function displayUsersEnter(e){
		e.preventDefault();
		displayUsers();
	}
	
	
	function displayUsers(){
		var criteria = DWRUtil.getValue('user-criteria');


		if(gridUsers){
			gridUsers.destroy(false);
		}
	    
	    function renderStatus(val){
			if(val==true) return STATUS_ACTIVE[language];
			else return STATUS_INACTIVE[language];
		}
		
		function trash(){
			return "<img src='img/genericUI/trash.gif' border=\"0\" />";
		}
		
	    var recordType = Ext.data.Record.create([
		  //{name: "id", type: "int"},
		  {name: "familyName", type: "string"},
		  {name: "givenName", type: "string"},
		  {name: "userName", type: "string"},
		  {name: "roleNames", type: "string"},
		  {name: "company", type: "string"},
		  {name: "enabled", type: "boolean"},
		  {name: null, type: "string"}
			//{name: "first", mapping:"author.firstName", type: "string"},
			//{name: "last", mapping:"author.lastName", type: "string"}
		  ]);

		  var store = new Ext.data.Store({
		    proxy: new Ext.data.DWRProxy(UsersInterface.getUsers, false),
		    reader: new Ext.data.ListRangeReader( 
					{id:'userName', totalProperty:'totalSize'}, recordType),
		    remoteSort: false
		  });
	    
	    var myColumns = [
	    	//{header: "No", width: 25, sortable: true},
			{header: USER_NAME[language], width: 120, sortable: true,dataIndex: 'familyName'}, 
			{header: USER_FIRSTNAME[language], width: 165,  sortable: true,dataIndex: 'givenName'}, 
			{header: USER_ID[language], width: 135,  sortable: true,dataIndex: 'userName'},
			{header: USER_ROLE[language], width: 105, sortable: true,dataIndex: 'roleNames'}, 
			{header: USER_DIVISION[language], width: 85, sortable: true,dataIndex: 'company'},
			{header: STATUS[language], width: 85, sortable: true, renderer:renderStatus,dataIndex: 'enabled'}, 
			{header: USER_DELETE[language], width: 40, sortable: true, renderer:trash}
		];
    	var columnModel = new Ext.grid.ColumnModel(myColumns);
	    
	    var grid = new Ext.grid.GridPanel({
		    id: 'users-grid',
   		    border:false,
			height: 200,
		    store: store,
		    cm: columnModel,
    		viewConfig: {
		    	autoFill:true,
		        forceFit: true
		    },	
		    listeners: {
		   		cellclick: function(g, rowIndex,  columnIndex, e){
		   						var record = g.getStore().getAt(rowIndex);
		   						if(columnIndex==6){ //delete user
		   							deleteUser(record.id);
		   						}
		   						else{ //display user detail
						    		displayUserDetails(record.id);
		   						}
		                	} 	
		    }
	    });
	    
	    var panel = Ext.getCmp('users-list');
		if(Ext.get('users-grid')!=undefined) {
			panel.remove('users-grid');
		}		
		panel.add(grid);
		usersPanel.doLayout();  
		
		store.load({params:{start:0, limit:22}, arg:[criteria,language]});
			 store.on('load', function(){
		    //alert( store.getTotalCount()  )
	    });
	    
		UsersInterface.getRoles(language,function(results){
			if(Ext.get('sRoles')==undefined) return;
			DWRUtil.removeAllOptions('sRoles');
			//DWRUtil.addOptions('sRoles',[LABEL_SELECT[language]]);
			DWRUtil.addOptions('sRoles',results,'name');
			
			new Ext.ToolTip({
				          id:'sRolesTpl',
		                  target: 'sRoles',
				          html: $('sRoles').options[0].value
                        });
		});	
		
	    
	} 
	
	
	function createUser(){
		displayUserDetails(null);
	
	}
	
	
	function displayUserDetails(userID){
		if(userID==null) userID = TITLE_NEW_USER[language];
		Ext.getCmp('user-detail').setTitle(userID);
		

		
		if(userID!=TITLE_NEW_USER[language]) {
			//alert("modif");
			UsersInterface.getUser(userID, function(user){
				//alert(DWRUtil.toDescriptiveString(user,2));
				userProfile=user;
				DWRUtil.setValue('nUserName',user.userName);
				DWRUtil.setValue('nGivenName',user.givenName);
				DWRUtil.setValue('nFamilyName',user.familyName);
				DWRUtil.setValue('nRealEmail',user.realEmail);
				DWRUtil.setValue('nCompany',user.company);
				DWRUtil.removeAllOptions('nRoles');
				DWRUtil.addOptions('nRoles',user.roleNames);
				DWRUtil.setValue('nEnabled',user.enabled);
				DWRUtil.setValue('nUniverse',user.universe);
			});
		}
		else{
			DWRUtil.setValue('nUserName',"");
			DWRUtil.setValue('nGivenName',"");
			DWRUtil.setValue('nFamilyName',"");
			DWRUtil.setValue('nRealEmail',"");
			DWRUtil.setValue('nCompany',"");
			DWRUtil.removeAllOptions('nRoles');
			DWRUtil.setValue('nEnabled',"");	
			DWRUtil.setValue('nUniverse',"");	
		}
	}
	
	
	function getRoles(rowIndex,roles){
		if(panel) panel.destroy();
		var theBody = '<select id="role-div" ></select>' +
			'<input type="button" onclick="addRoleInBox('+rowIndex+');" value="Ajouter" /><br>' +
			'<select id="selected-role-div"multiple=true ></select>' +
			'<input type="button" onclick="deleteRoleSelected('+rowIndex+');" value="Effacer" />';
		
		panel = new YAHOO.widget.Panel(
		         "role-container", 
				 {   
				 //y:500,				 
				   visible: true,
				   draggable: true,
				   close: true,
				   zIndex:10000,
				   x:$('data-grid-users').offsetWidth/2,
				   y:$('data-grid-users').offsetHeight/2
				 } );
	    panel.setBody(theBody);  
	    panel.render(document.body);  
		UsersInterface.getRoles(function(results){
			DWRUtil.removeAllOptions("selected-role-div");
			DWRUtil.addOptions('role-div',results);
			DWRUtil.removeAllOptions("selected-role-div");
	        DWRUtil.addOptions("selected-role-div", roles);
		});
	}
	
	function addRoleInBox() {
		var index = $('sRoles').selectedIndex;
		if (index==-1) return;
		var name = $('sRoles').options[$('sRoles').selectedIndex].text;
		var sel = new Array();
		sel[0] = name;
		sel.sort();
		DWRUtil.addOptions("nRoles",sel);
	}
	
	function deleteRoleSelected() {
		var index = $('nRoles').selectedIndex;
		if (index==-1) return;
		$('nRoles').options[index] = null;
	}	
	
	function getRole() {
		var index = $("sRoles").selectedIndex;
		if (index==-1) return;
		var name = $("sRoles").options[$("sRoles").selectedIndex].value;
		UsersInterface.getRoleDescription(name,language,function(result){
			DWRUtil.setValue('sRoledescription',result);
		});
	}
	
	function saveUser(userName) {
	    
		var p1 = $('nPassword1').value;
		var p2 = $('nPassword2').value;
		if (p1 != p2) {
			alert (ALERT_DIFFERENT_PASSWORDS[language]);
			$('nPassword1').focus();
			return;
		}
		/*if (p1=='') {
			alert ('The password cannot be empty');
			$('nPassword1').focus();
			return;
		}*/
		var oldPassword = (userProfile!=null) ? userProfile.password : null;
	
		if($('nUserName').value=='') {
			alert ('Please fill this input');
			$('nUserName').focus();
			return;
		}
	
		if ($('nGivenName').value=='') {
			alert (ALERT_INPUT_GIVEN_NAME[language]);
			$('nGivenName').focus();
			return;
		}
		if ($('nFamilyName').value=='') {
			alert (ALERT_INPUT_FAMILY_NAME[language]);
			$('nFamilyName').focus();
			return;
		}
		if ($('nRealEmail').value=='') {
			alert (ALERT_INPUT_EMAIL[language]);
			$('nRealEmail').focus();
			return;
		}
		if(BGUtil.getOptions("nRoles").length==0) {
			alert(ALERT_INPUT_ROLES[language]);
			return;
		}
	
		var regDate;
			regDate = new Date();
	    
	    
		var newUser = {
			userName: $('nUserName').value,
			password:p1,
			givenName: $('nGivenName').value,
			familyName: $('nFamilyName').value,
			company: $('nCompany').value,
			realEmail: $('nRealEmail').value,
			registrationDate: regDate,
			enabled: $('nEnabled').checked,
			roleNames:BGUtil.getOptions("nRoles"),
			homePage:"Home",
			universe: $('nUniverse').value
		};
	    //alert(DWRUtil.toDescriptiveString(newUser,2));
		UsersInterface.saveUser(saveUserCB, newUser, oldPassword);
	}
	
	function saveUserCB(pk) {
		if (pk == null) {
			if(!confirm(ALERT_ERROR[language])){
				displayUsers();
				layout.remove('center','users-details-div');
			}
		} else {
			alert(ALERT_SUCCESSFULLY_SAVED[language]+" : "+pk);
			displayUsers();
			var tabPanel = amalto.core.getTabPanel();
			tabPanel.remove('users-details-div');
		}
	
	}
	
	function addRoleInBox2(rowIndex) {
		var index = $('role-div').selectedIndex;
		if (index==-1) return;
		var name = $('role-div').options[$('role-div').selectedIndex].value;
		var sel = new Array();
		sel[0] = name;
		sel.sort();
		DWRUtil.addOptions("selected-role-div",sel);
		updateCellRole(rowIndex);
	}
	
	function deleteRoleSelected2(rowIndex) {
		var index = $('selected-role-div').selectedIndex;
		if (index==-1) return;
		$('selected-role-div').options[index] = null;
		updateCellRole(rowIndex);
	}	
	
	function deleteUser(userID){
		if(confirm("Effacer?")) {
		  	UsersInterface.deleteUser(userID,function(result){
		  		displayUsers();
		  	});   		
		}
	   	else return;
	}
	

 	return {
		init: function() {browseUsers();},
		displayUsers:function(){displayUsers();},
		createUser:function(){createUser();},
		saveUser:function(a){saveUser(a)},
		addRoleInBox:function(){addRoleInBox()},
		changeSrolesTpl:function(){changeSrolesTpl()},
		deleteRoleSelected:function(){deleteRoleSelected()}
 	}
}();