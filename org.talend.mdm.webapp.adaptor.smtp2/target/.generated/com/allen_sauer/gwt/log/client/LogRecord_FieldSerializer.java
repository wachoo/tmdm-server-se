package com.allen_sauer.gwt.log.client;

@SuppressWarnings("deprecation")
public class LogRecord_FieldSerializer {
  private static native java.lang.String getCategory(com.allen_sauer.gwt.log.client.LogRecord instance) /*-{
    return instance.@com.allen_sauer.gwt.log.client.LogRecord::category;
  }-*/;
  
  private static native void  setCategory(com.allen_sauer.gwt.log.client.LogRecord instance, java.lang.String value) /*-{
    instance.@com.allen_sauer.gwt.log.client.LogRecord::category = value;
  }-*/;
  
  private static native int getLevel(com.allen_sauer.gwt.log.client.LogRecord instance) /*-{
    return instance.@com.allen_sauer.gwt.log.client.LogRecord::level;
  }-*/;
  
  private static native void  setLevel(com.allen_sauer.gwt.log.client.LogRecord instance, int value) /*-{
    instance.@com.allen_sauer.gwt.log.client.LogRecord::level = value;
  }-*/;
  
  private static native java.util.HashMap getMap(com.allen_sauer.gwt.log.client.LogRecord instance) /*-{
    return instance.@com.allen_sauer.gwt.log.client.LogRecord::map;
  }-*/;
  
  private static native void  setMap(com.allen_sauer.gwt.log.client.LogRecord instance, java.util.HashMap value) /*-{
    instance.@com.allen_sauer.gwt.log.client.LogRecord::map = value;
  }-*/;
  
  private static native java.lang.String getMessage(com.allen_sauer.gwt.log.client.LogRecord instance) /*-{
    return instance.@com.allen_sauer.gwt.log.client.LogRecord::message;
  }-*/;
  
  private static native void  setMessage(com.allen_sauer.gwt.log.client.LogRecord instance, java.lang.String value) /*-{
    instance.@com.allen_sauer.gwt.log.client.LogRecord::message = value;
  }-*/;
  
  private static native int getRecordSequence(com.allen_sauer.gwt.log.client.LogRecord instance) /*-{
    return instance.@com.allen_sauer.gwt.log.client.LogRecord::recordSequence;
  }-*/;
  
  private static native void  setRecordSequence(com.allen_sauer.gwt.log.client.LogRecord instance, int value) /*-{
    instance.@com.allen_sauer.gwt.log.client.LogRecord::recordSequence = value;
  }-*/;
  
  private static native com.allen_sauer.gwt.log.client.WrappedClientThrowable getWrappedClientThrowable(com.allen_sauer.gwt.log.client.LogRecord instance) /*-{
    return instance.@com.allen_sauer.gwt.log.client.LogRecord::wrappedClientThrowable;
  }-*/;
  
  private static native void  setWrappedClientThrowable(com.allen_sauer.gwt.log.client.LogRecord instance, com.allen_sauer.gwt.log.client.WrappedClientThrowable value) /*-{
    instance.@com.allen_sauer.gwt.log.client.LogRecord::wrappedClientThrowable = value;
  }-*/;
  
  public static void deserialize(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader, com.allen_sauer.gwt.log.client.LogRecord instance) throws com.google.gwt.user.client.rpc.SerializationException{
    setCategory(instance, streamReader.readString());
    setLevel(instance, streamReader.readInt());
    setMap(instance, (java.util.HashMap) streamReader.readObject());
    setMessage(instance, streamReader.readString());
    setRecordSequence(instance, streamReader.readInt());
    setWrappedClientThrowable(instance, (com.allen_sauer.gwt.log.client.WrappedClientThrowable) streamReader.readObject());
    
  }
  
  public static native com.allen_sauer.gwt.log.client.LogRecord instantiate(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader) throws com.google.gwt.user.client.rpc.SerializationException/*-{
    return @com.allen_sauer.gwt.log.client.LogRecord::new()();
  }-*/;
  
  public static void serialize(com.google.gwt.user.client.rpc.SerializationStreamWriter streamWriter, com.allen_sauer.gwt.log.client.LogRecord instance) throws com.google.gwt.user.client.rpc.SerializationException {
    streamWriter.writeString(getCategory(instance));
    streamWriter.writeInt(getLevel(instance));
    streamWriter.writeObject(getMap(instance));
    streamWriter.writeString(getMessage(instance));
    streamWriter.writeInt(getRecordSequence(instance));
    streamWriter.writeObject(getWrappedClientThrowable(instance));
    
  }
  
}
