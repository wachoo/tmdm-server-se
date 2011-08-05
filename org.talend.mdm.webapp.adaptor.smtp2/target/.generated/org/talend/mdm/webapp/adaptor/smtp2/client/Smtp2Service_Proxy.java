package org.talend.mdm.webapp.adaptor.smtp2.client;

import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.impl.RequestCallbackAdapter.ResponseReader;
import com.google.gwt.core.client.impl.Impl;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamWriter;

public class Smtp2Service_Proxy extends RemoteServiceProxy implements org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2ServiceAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service";
  private static final String SERIALIZATION_POLICY ="EC1E4CFA1EE832FD063A82CD097AAFA7";
  private static final org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer SERIALIZER = new org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer();
  
  public Smtp2Service_Proxy() {
    super(GWT.getModuleBaseURL(),
      "Smtp2Service", 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void getStatus(com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("Smtp2Service_Proxy.getStatus", requestId, "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("getStatus");
      streamWriter.writeInt(0);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("Smtp2Service_Proxy.getStatus", requestId, "requestSerialized"));
      doInvoke(ResponseReader.STRING, "Smtp2Service_Proxy.getStatus", requestId, payload, callback);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void loadConfiguration(com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("Smtp2Service_Proxy.loadConfiguration", requestId, "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("loadConfiguration");
      streamWriter.writeInt(0);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("Smtp2Service_Proxy.loadConfiguration", requestId, "requestSerialized"));
      doInvoke(ResponseReader.OBJECT, "Smtp2Service_Proxy.loadConfiguration", requestId, payload, callback);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void saveConfiguration(org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean config, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("Smtp2Service_Proxy.saveConfiguration", requestId, "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("saveConfiguration");
      streamWriter.writeInt(1);
      streamWriter.writeString("org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean/2371396060");
      streamWriter.writeObject(config);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("Smtp2Service_Proxy.saveConfiguration", requestId, "requestSerialized"));
      doInvoke(ResponseReader.VOID, "Smtp2Service_Proxy.saveConfiguration", requestId, payload, callback);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void sendSampleEmail(java.lang.String from, java.lang.String to, java.lang.String cc, java.lang.String bcc, java.lang.String subject, java.lang.String body, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("Smtp2Service_Proxy.sendSampleEmail", requestId, "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("sendSampleEmail");
      streamWriter.writeInt(6);
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString(from);
      streamWriter.writeString(to);
      streamWriter.writeString(cc);
      streamWriter.writeString(bcc);
      streamWriter.writeString(subject);
      streamWriter.writeString(body);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("Smtp2Service_Proxy.sendSampleEmail", requestId, "requestSerialized"));
      doInvoke(ResponseReader.STRING, "Smtp2Service_Proxy.sendSampleEmail", requestId, payload, callback);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void start(com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("Smtp2Service_Proxy.start", requestId, "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("start");
      streamWriter.writeInt(0);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("Smtp2Service_Proxy.start", requestId, "requestSerialized"));
      doInvoke(ResponseReader.STRING, "Smtp2Service_Proxy.start", requestId, payload, callback);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void stop(com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("Smtp2Service_Proxy.stop", requestId, "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("stop");
      streamWriter.writeInt(0);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("Smtp2Service_Proxy.stop", requestId, "requestSerialized"));
      doInvoke(ResponseReader.STRING, "Smtp2Service_Proxy.stop", requestId, payload, callback);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
}
