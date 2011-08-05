package org.talend.mdm.webapp.adaptor.smtp2.shared;

@SuppressWarnings("deprecation")
public class SmtpConfigurationBean_FieldSerializer {
  private static native java.lang.String getSmtpBCC(org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instance) /*-{
    return instance.@org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean::smtpBCC;
  }-*/;
  
  private static native void  setSmtpBCC(org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instance, java.lang.String value) /*-{
    instance.@org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean::smtpBCC = value;
  }-*/;
  
  private static native java.lang.String getSmtpPassword(org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instance) /*-{
    return instance.@org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean::smtpPassword;
  }-*/;
  
  private static native void  setSmtpPassword(org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instance, java.lang.String value) /*-{
    instance.@org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean::smtpPassword = value;
  }-*/;
  
  private static native int getSmtpPort(org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instance) /*-{
    return instance.@org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean::smtpPort;
  }-*/;
  
  private static native void  setSmtpPort(org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instance, int value) /*-{
    instance.@org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean::smtpPort = value;
  }-*/;
  
  private static native java.lang.String getSmtpServer(org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instance) /*-{
    return instance.@org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean::smtpServer;
  }-*/;
  
  private static native void  setSmtpServer(org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instance, java.lang.String value) /*-{
    instance.@org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean::smtpServer = value;
  }-*/;
  
  private static native java.lang.String getSmtpUsername(org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instance) /*-{
    return instance.@org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean::smtpUsername;
  }-*/;
  
  private static native void  setSmtpUsername(org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instance, java.lang.String value) /*-{
    instance.@org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean::smtpUsername = value;
  }-*/;
  
  public static void deserialize(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader, org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instance) throws com.google.gwt.user.client.rpc.SerializationException{
    setSmtpBCC(instance, streamReader.readString());
    setSmtpPassword(instance, streamReader.readString());
    setSmtpPort(instance, streamReader.readInt());
    setSmtpServer(instance, streamReader.readString());
    setSmtpUsername(instance, streamReader.readString());
    
    com.extjs.gxt.ui.client.data.BaseModelData_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instantiate(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader) throws com.google.gwt.user.client.rpc.SerializationException/*-{
    return @org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean::new()();
  }-*/;
  
  public static void serialize(com.google.gwt.user.client.rpc.SerializationStreamWriter streamWriter, org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean instance) throws com.google.gwt.user.client.rpc.SerializationException {
    streamWriter.writeString(getSmtpBCC(instance));
    streamWriter.writeString(getSmtpPassword(instance));
    streamWriter.writeInt(getSmtpPort(instance));
    streamWriter.writeString(getSmtpServer(instance));
    streamWriter.writeString(getSmtpUsername(instance));
    
    com.extjs.gxt.ui.client.data.BaseModelData_FieldSerializer.serialize(streamWriter, instance);
  }
  
}
