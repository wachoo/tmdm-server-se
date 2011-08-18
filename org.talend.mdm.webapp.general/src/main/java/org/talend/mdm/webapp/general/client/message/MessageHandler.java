package org.talend.mdm.webapp.general.client.message;


public interface MessageHandler {

    void handlerMessage(String msgId, Object data);
}
