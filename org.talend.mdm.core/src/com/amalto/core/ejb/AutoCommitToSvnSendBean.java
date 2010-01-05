package com.amalto.core.ejb;

import java.rmi.RemoteException;
import java.util.Properties;


import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.amalto.core.util.XtentisException;

/**
 * @author achen
 * 
 * @ejb.bean name="AutoCommitToSvnSendBean"
 *          display-name="Name for AutoCommitToSvnSendBean"
 *          description="Send Message to AutoCommitToSvnMDB"
 *          jndi-name="amalto/remote/core/autocommittosvnsend"
 * 		  	local-jndi-name = "amalto/local/core/autocommittosvnsend"
 *          type="Stateless"
 *          view-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 * 
 */

@SuppressWarnings("deprecation")
public class AutoCommitToSvnSendBean implements SessionBean {
    QueueConnection conn;
    QueueSession session;
    Queue que;
    public static final String FailedAutoCommitSvnMessage="FailedAutoCommitSvnMessage"; //datacluster to store failed autocommittosvn message
    public AutoCommitToSvnSendBean(){


    }
	public void ejbActivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void ejbPassivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void ejbRemove() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void setSessionContext(SessionContext arg0) throws EJBException,
			RemoteException {
		// TODO Auto-generated method stub
		
	}
	
    /**
     * Create method
     * @ejb.create-method  view-type = "local"
     */
    public void ejbCreate() throws javax.ejb.CreateException {
    	//setup queue 
    	try {
			setupPTP();
		} catch (Exception e1) {
			Logger.getLogger(this.getClass()).error("AutoCommitToSvnSend Queue session start faild: " + e1.getMessage());
		}
		//stop queue when system shutdown
    	Runtime.getRuntime().addShutdownHook(new Thread(){
    		@Override
    		public void run() {
    			try {
					stopConn();
				} catch (JMSException e) {
					Logger.getLogger(AutoCommitToSvnSendBean.class).error("AutoCommitToSvnSend Queue session stop faild: " + e.getMessage());
				}
    		}
    	});		
    }
   	    
    /**
     * Post Create method
     */
    public void ejbPostCreate() throws javax.ejb.CreateException {}
    /**
     * Send the saved item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
	public boolean sendMsg(String text)throws XtentisException{
        // Send a text msg
		try{
	        QueueSender send = session.createSender(que);
	        TextMessage tm = session.createTextMessage(text);
	        tm.setJMSReplyTo(que);
	        send.send(tm);
	        Logger.getLogger(this.getClass()).info("sendRecvAsync, sent text=" + tm.getText());
	        send.close();
		}catch(Exception e){
			Logger.getLogger(this.getClass()).error("sendMsg error: " + e.getMessage());
			return false;
		}
		return true;
	}
	
    public void setupPTP()throws Exception           
	{
	    InitialContext iniCtx = new InitialContext();
	    Object tmp = iniCtx.lookup("ConnectionFactory");
	    QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
	    conn = qcf.createQueueConnection();
	    que = (Queue) iniCtx.lookup("queue/B");
	    session = conn.createQueueSession(false,
	                                      QueueSession.AUTO_ACKNOWLEDGE);
	    conn.start();
	} 	
    
    public void stopConn()  throws JMSException
	{
	    conn.stop();
	    session.close();
	    conn.close();
	}    
}
