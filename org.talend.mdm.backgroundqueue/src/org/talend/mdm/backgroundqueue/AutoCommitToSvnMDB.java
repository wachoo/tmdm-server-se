package org.talend.mdm.backgroundqueue;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.amalto.core.ejb.AutoCommitToSvnMsg;
import com.amalto.core.ejb.AutoCommitToSvnSendBean;
import com.amalto.core.enterpriseutil.EnterpriseUtil;
import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJOPK;
import com.amalto.core.util.Util;

/**
 * @author achen
 * @ejb.bean        name="AutoCommitToSvnMDB"  
 *                  description="Auto Commit to Svn when putitem Data-Modification Message Bean"  
 *                  local-jndi-name="amalto/remote/core/autocommittosvn"  
 *                  transaction-type="Container"  
 *                  --message-selector=""  
 *                  acknowledge-mode="Auto-acknowledge"  
 *                  destination-type="javax.jms.Queue"  
 *   
 * @ejb.transaction type="NotSupported"  
 *   
 * @jboss.message-driven   connection-factory-jndi-name="ConnectionFactory"  
 *                         destination-jndi-name="queue/B"  
 *   
 * @jboss.pool   initial-beans-in-free-pool="1"  
 *                  max-beans-in-free-pool="1"  
 */                 
public class AutoCommitToSvnMDB implements MessageDrivenBean, MessageListener {

	    private MessageDrivenContext ctx = null;
	    private QueueConnection conn;
	    private QueueSession session;
	    
	    public AutoCommitToSvnMDB()
	    {
	       
	    }
	    
	    public void setMessageDrivenContext(MessageDrivenContext ctx)
	    {
	        this.ctx = ctx;

	    }
	    
	    public void ejbCreate()
	    {
	        try {
	            setupPTP();
	        } catch (Exception e) {
	            throw new EJBException("Failed to init AutoCommitToSvnMDB", e);
	        }
	    }

	    public void ejbRemove()
	    {
	        ctx = null;
	        try {
	            if (session != null) {
	                session.close();
	            }
	            if (conn != null) {
	                conn.close();
	            }
	        } catch(JMSException e) {
	            e.printStackTrace();
	        }
	    }
	                
	    public void onMessage(Message msg)
	    {
	    	TextMessage tm = null;
	    	if(msg instanceof TextMessage){		      
		        try {
		            tm = (TextMessage) msg;
		            Logger.getLogger(this.getClass()).info("AutoCommitToSvnMDB.onMessage, msg="+tm.getText());
		            AutoCommitToSvnMsg msg1=AutoCommitToSvnMsg.unmarshal(tm.getText());
		            EnterpriseUtil.getVersioningSystemCtrlLocal().commitItem(new VersioningSystemPOJOPK(msg1.getVersionSystemPk()), msg1.getItemPk(), msg1.getComment());
		            //Queue dest = (Queue) msg.getJMSReplyTo();
		            //sendReply(text, dest);
		        } catch(Throwable t) {
		        	Logger.getLogger(this.getClass()).info("Svn server not up, save faild message to db");
		        	try{
		        		Util.getXmlServerCtrlLocal().putDocumentFromString(tm.getText(), tm.getJMSMessageID(), AutoCommitToSvnSendBean.FailedAutoCommitSvnMessage, null);
		        	}catch(Exception e){}
		        }
	    	}
	    }
	                
	    private void setupPTP()
	        throws JMSException, NamingException
	    {
	        InitialContext iniCtx = new InitialContext();
	        Object tmp = iniCtx.lookup("ConnectionFactory");
	        QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
	        conn = qcf.createQueueConnection();
	        session = conn.createQueueSession(false,
	                                          QueueSession.AUTO_ACKNOWLEDGE);
	        conn.start();
	    }

	    private void sendReply(String text, Queue dest)
	        throws JMSException
	    {
	    	Logger.getLogger(this.getClass()).info("AutoCommitToSvnMDB.sendReply" + ", dest="+dest);
	        QueueSender sender = session.createSender(dest);
	        TextMessage tm = session.createTextMessage(text);
	        sender.send(tm);
	        sender.close();
	    }
}
