package com.amalto.core.objects.configurationinfo.assemble;

import java.io.StringReader;

import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

import com.amalto.core.ejb.AutoCommitToSvnMsg;
import com.amalto.core.ejb.AutoCommitToSvnSendBean;
import com.amalto.core.ejb.local.AutoCommitToSvnSendBeanLocalHome;
import com.amalto.core.util.Util;

/**
 * resend the failed autocommittosvn message
 * @author achen
 *
 */
public class ResendFaildMessageSubProc extends AssembleSubProc{

	@Override
	public void run() throws Exception {
		String[] ids=Util.getXmlServerCtrlLocal().getAllDocumentsUniqueID(null, AutoCommitToSvnSendBean.FailedAutoCommitSvnMessage);
		for(String id: ids){
			String m=Util.getXmlServerCtrlLocal().getDocumentAsString(null, AutoCommitToSvnSendBean.FailedAutoCommitSvnMessage, id);
			AutoCommitToSvnMsg msg=(AutoCommitToSvnMsg)Unmarshaller.unmarshal(AutoCommitToSvnMsg.class, new InputSource(new StringReader(m)));
			AutoCommitToSvnSendBeanLocalHome h=(AutoCommitToSvnSendBeanLocalHome) Util.getLocalHome("amalto/local/core/autocommittosvnsend");
			try{
			h.create().sendMsg(msg.marshal());
			}catch(Exception e){
				//svn not up
				return;
			}
			Util.getXmlServerCtrlLocal().deleteDocument(null, AutoCommitToSvnSendBean.FailedAutoCommitSvnMessage, id);
		}

	}

}
