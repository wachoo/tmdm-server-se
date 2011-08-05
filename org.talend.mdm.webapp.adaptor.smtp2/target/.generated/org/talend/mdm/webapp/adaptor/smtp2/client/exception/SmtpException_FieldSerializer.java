package org.talend.mdm.webapp.adaptor.smtp2.client.exception;

@SuppressWarnings("deprecation")
public class SmtpException_FieldSerializer {
  public static void deserialize(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader, org.talend.mdm.webapp.adaptor.smtp2.client.exception.SmtpException instance) throws com.google.gwt.user.client.rpc.SerializationException{
    
    com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native org.talend.mdm.webapp.adaptor.smtp2.client.exception.SmtpException instantiate(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader) throws com.google.gwt.user.client.rpc.SerializationException/*-{
    return @org.talend.mdm.webapp.adaptor.smtp2.client.exception.SmtpException::new()();
  }-*/;
  
  public static void serialize(com.google.gwt.user.client.rpc.SerializationStreamWriter streamWriter, org.talend.mdm.webapp.adaptor.smtp2.client.exception.SmtpException instance) throws com.google.gwt.user.client.rpc.SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer.serialize(streamWriter, instance);
  }
  
}
