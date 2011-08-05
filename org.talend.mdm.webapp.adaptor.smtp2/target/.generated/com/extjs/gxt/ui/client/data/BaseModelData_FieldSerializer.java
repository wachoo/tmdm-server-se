package com.extjs.gxt.ui.client.data;

@SuppressWarnings("deprecation")
public class BaseModelData_FieldSerializer {
  public static void deserialize(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader, com.extjs.gxt.ui.client.data.BaseModelData instance) throws com.google.gwt.user.client.rpc.SerializationException{
    instance.allowNestedValues = streamReader.readBoolean();
    instance.map = (com.extjs.gxt.ui.client.data.RpcMap) streamReader.readObject();
    
  }
  
  public static native com.extjs.gxt.ui.client.data.BaseModelData instantiate(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader) throws com.google.gwt.user.client.rpc.SerializationException/*-{
    return @com.extjs.gxt.ui.client.data.BaseModelData::new()();
  }-*/;
  
  public static void serialize(com.google.gwt.user.client.rpc.SerializationStreamWriter streamWriter, com.extjs.gxt.ui.client.data.BaseModelData instance) throws com.google.gwt.user.client.rpc.SerializationException {
    streamWriter.writeBoolean(instance.allowNestedValues);
    streamWriter.writeObject(instance.map);
    
  }
  
}
