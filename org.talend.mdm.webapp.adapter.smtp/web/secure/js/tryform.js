Ext.namespace('amalto.smtp');
amalto.smtp.tryform = function(config) {
	Ext.applyIf(this, config);
	this.initUIComponents();
	amalto.smtp.tryform.superclass.constructor.call(this);
};
Ext.extend(amalto.smtp.tryform, Ext.Panel, {
	initUIComponents : function() {
		Ext.apply(this, {
			id : "tryMailPanel",
			layout : "form",
			title : "Mail Sender",
			bodyStyle:'padding:15px',
			autoScroll:true,
			closable: true,
			buttonAlign:"left",
			items : [{
				width : 300,
				fieldLabel : "From",
				xtype : "textfield",
				name : "mailFrom",
				id : "mailFrom"
			}, {
				width : 300,
				fieldLabel : "To",
				xtype : "textfield",
				name : "mailTo",
				id : "mailTo"
			}, {
				width : 450,
				fieldLabel : "CC",
				xtype : "textfield",
				name : "mailCC",
				id : "mailCC"
			}, {
				width : 450,
				fieldLabel : "BCC",
				xtype : "textfield",
				name : "mailBCC",
				id : "mailBCC"
			}, {
				width : 500,
				fieldLabel : "Subject",
				xtype : "textfield",
				name : "mailSubject",
				id : "mailSubject"
			}, {
				width : 500,
				height : 200,
				fieldLabel : "Body",
				xtype : "textarea",
				name : "mailBody",
				id : "mailBody"
			}],
			buttons : [{
				handler : function(button, event) {
					this.onSendBtnClick(button, event);
				}.createDelegate(this),
				text : "Send"
			}]
		});
	},
	onSendBtnClick: function(button, event) {
		var from = DWRUtil.getValue('mailFrom');
		var to = DWRUtil.getValue('mailTo');
		var cc = DWRUtil.getValue('mailCC');
		var bcc = DWRUtil.getValue('mailBCC');
		var subject = DWRUtil.getValue('mailSubject');
		var body = DWRUtil.getValue('mailBody');
		
		if(from==''||to==''){
		               Ext.MessageBox.show({
    						title:'Warning', 
    						msg: "'From' or 'To' fields can not be empty!",
    						icon:Ext.MessageBox.WARNING,
    						width:300,
    						buttons: Ext.Msg.OK
    					});
    					return;
		}
    					
		SmtpAdapterInterface.sendSampleEmail(from,to,cc,bcc,subject,body,
		function(status){
			if(status==true){
				Ext.MessageBox.show({
    						title:'Info', 
    						msg: "Email has been sent successfully! ",
    						icon:Ext.MessageBox.INFO,
    						width:300,
    						buttons: Ext.Msg.OK
    					});
				
			}else{
				Ext.MessageBox.show({
    						title:'Error', 
    						msg: "Email sent unsuccessfully! ",
    						icon:Ext.MessageBox.ERROR,
    						width:300,
    						buttons: Ext.Msg.ERROR
    					});
			}
		});
	}
});
