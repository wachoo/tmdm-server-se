package org.talend.mdm.webapp.adaptor.smtp2.client;

import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.impl.Serializer;

public class Smtp2Service_TypeSerializer extends com.google.gwt.user.client.rpc.impl.SerializerBase {
  private static final MethodMap methodMap = JavaScriptObject.createObject().cast();
  private static final JsArrayString signatureMap = JavaScriptObject.createArray().cast();
  protected MethodMap getMethodMap() { return methodMap; }
  protected JsArrayString getSignatureMap() { return signatureMap; }
  
  static {
    registerMethods();
    registerSignatures();
  }
  private static native com.extjs.gxt.ui.client.core.FastMap create_com_extjs_gxt_ui_client_core_FastMap_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.extjs.gxt.ui.client.core.FastMap::new()();
  }-*/;
  
  private static native com.extjs.gxt.ui.client.core.FastSet create_com_extjs_gxt_ui_client_core_FastSet_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.extjs.gxt.ui.client.core.FastSet::new()();
  }-*/;
  
  private static native com.extjs.gxt.ui.client.data.RpcMap create_com_extjs_gxt_ui_client_data_RpcMap_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @com.extjs.gxt.ui.client.data.RpcMap::new()();
  }-*/;
  
  private static native java.util.ArrayList create_com_google_gwt_user_client_rpc_core_java_util_ArrayList_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @java.util.ArrayList::new()();
  }-*/;
  
  private static native java.util.HashMap create_com_google_gwt_user_client_rpc_core_java_util_HashMap_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @java.util.HashMap::new()();
  }-*/;
  
  private static native java.util.HashSet create_com_google_gwt_user_client_rpc_core_java_util_HashSet_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @java.util.HashSet::new()();
  }-*/;
  
  private static native java.util.IdentityHashMap create_com_google_gwt_user_client_rpc_core_java_util_IdentityHashMap_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @java.util.IdentityHashMap::new()();
  }-*/;
  
  private static native java.util.LinkedList create_com_google_gwt_user_client_rpc_core_java_util_LinkedList_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @java.util.LinkedList::new()();
  }-*/;
  
  private static native java.util.Vector create_com_google_gwt_user_client_rpc_core_java_util_Vector_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @java.util.Vector::new()();
  }-*/;
  
  private static native void registerSignatures() /*-{
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @com.extjs.gxt.ui.client.core.FastMap::class,
      "com.extjs.gxt.ui.client.core.FastMap/3488076414");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @com.extjs.gxt.ui.client.core.FastSet::class,
      "com.extjs.gxt.ui.client.core.FastSet/2139705081");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @com.extjs.gxt.ui.client.data.RpcMap::class,
      "com.extjs.gxt.ui.client.data.RpcMap/3441186752");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @com.google.gwt.http.client.RequestException::class,
      "com.google.gwt.http.client.RequestException/190587325");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @com.google.gwt.i18n.client.impl.DateRecord::class,
      "com.google.gwt.i18n.client.impl.DateRecord/112389920");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @com.google.gwt.i18n.client.impl.DateRecord[]::class,
      "[Lcom.google.gwt.i18n.client.impl.DateRecord;/2823890031");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @com.google.gwt.json.client.JSONException::class,
      "com.google.gwt.json.client.JSONException/2941795468");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException::class,
      "com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException/3936916533");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @com.google.gwt.user.client.rpc.SerializableException::class,
      "com.google.gwt.user.client.rpc.SerializableException/3047383460");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @com.google.gwt.user.client.rpc.SerializationException::class,
      "com.google.gwt.user.client.rpc.SerializationException/2836333220");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @com.google.gwt.user.client.rpc.ServiceDefTarget$NoServiceEntryPointSpecifiedException::class,
      "com.google.gwt.user.client.rpc.ServiceDefTarget$NoServiceEntryPointSpecifiedException/3408313447");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @com.google.gwt.xml.client.impl.DOMParseException::class,
      "com.google.gwt.xml.client.impl.DOMParseException/3799120635");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.io.IOException::class,
      "java.io.IOException/1159940531");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.ArithmeticException::class,
      "java.lang.ArithmeticException/1539622151");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.ArrayIndexOutOfBoundsException::class,
      "java.lang.ArrayIndexOutOfBoundsException/600550433");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.ArrayStoreException::class,
      "java.lang.ArrayStoreException/3540507190");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Boolean::class,
      "java.lang.Boolean/476441737");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Boolean[]::class,
      "[Ljava.lang.Boolean;/2507200464");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Byte::class,
      "java.lang.Byte/1571082439");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Byte[]::class,
      "[Ljava.lang.Byte;/3053163818");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.ClassCastException::class,
      "java.lang.ClassCastException/702295179");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Double::class,
      "java.lang.Double/858496421");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Double[]::class,
      "[Ljava.lang.Double;/2857560407");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Exception::class,
      "java.lang.Exception/1920171873");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Float::class,
      "java.lang.Float/1718559123");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Float[]::class,
      "[Ljava.lang.Float;/3956181278");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.IllegalArgumentException::class,
      "java.lang.IllegalArgumentException/1755012560");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.IllegalStateException::class,
      "java.lang.IllegalStateException/1972187323");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.IndexOutOfBoundsException::class,
      "java.lang.IndexOutOfBoundsException/2489527753");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Integer::class,
      "java.lang.Integer/3438268394");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Integer[]::class,
      "[Ljava.lang.Integer;/1574882222");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Long::class,
      "java.lang.Long/4227064769");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Long[]::class,
      "[Ljava.lang.Long;/97727328");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.NegativeArraySizeException::class,
      "java.lang.NegativeArraySizeException/3846860241");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.NullPointerException::class,
      "java.lang.NullPointerException/1463492344");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.NumberFormatException::class,
      "java.lang.NumberFormatException/3305228476");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.RuntimeException::class,
      "java.lang.RuntimeException/515124647");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Short::class,
      "java.lang.Short/551743396");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.Short[]::class,
      "[Ljava.lang.Short;/3779901635");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.String::class,
      "java.lang.String/2004016611");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.StringIndexOutOfBoundsException::class,
      "java.lang.StringIndexOutOfBoundsException/500777603");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.String[]::class,
      "[Ljava.lang.String;/2600011424");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.UnsupportedOperationException::class,
      "java.lang.UnsupportedOperationException/3744010015");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.lang.annotation.AnnotationTypeMismatchException::class,
      "java.lang.annotation.AnnotationTypeMismatchException/976205828");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.sql.Date::class,
      "java.sql.Date/3996530531");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.sql.Date[]::class,
      "[Ljava.sql.Date;/773242198");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.sql.Time::class,
      "java.sql.Time/831929183");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.sql.Time[]::class,
      "[Ljava.sql.Time;/3521163259");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.sql.Timestamp::class,
      "java.sql.Timestamp/1769758459");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.sql.Timestamp[]::class,
      "[Ljava.sql.Timestamp;/1267684111");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.ArrayList::class,
      "java.util.ArrayList/3821976829");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.Arrays$ArrayList::class,
      "java.util.Arrays$ArrayList/1243019747");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.ConcurrentModificationException::class,
      "java.util.ConcurrentModificationException/2717383897");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.Date::class,
      "java.util.Date/1659716317");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.Date[]::class,
      "[Ljava.util.Date;/3828292433");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.EmptyStackException::class,
      "java.util.EmptyStackException/89438517");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.HashMap::class,
      "java.util.HashMap/962170901");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.HashSet::class,
      "java.util.HashSet/1594477813");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.IdentityHashMap::class,
      "java.util.IdentityHashMap/3881143367");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.LinkedHashMap::class,
      "java.util.LinkedHashMap/1551059846");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.LinkedHashSet::class,
      "java.util.LinkedHashSet/3628722029");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.LinkedList::class,
      "java.util.LinkedList/1060625595");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.NoSuchElementException::class,
      "java.util.NoSuchElementException/1559248883");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.Stack::class,
      "java.util.Stack/1031431137");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.TooManyListenersException::class,
      "java.util.TooManyListenersException/2023078032");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.TreeMap::class,
      "java.util.TreeMap/1575826026");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.TreeSet::class,
      "java.util.TreeSet/1002270346");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @java.util.Vector::class,
      "java.util.Vector/3125574444");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @org.talend.mdm.webapp.adaptor.smtp2.client.exception.SmtpException::class,
      "org.talend.mdm.webapp.adaptor.smtp2.client.exception.SmtpException/764477007");
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerSignature(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/Class;Ljava/lang/String;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::signatureMap,
      @org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean::class,
      "org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean/2371396060");
    
  }-*/;
  
  private static native void registerMethods() /*-{
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "com.extjs.gxt.ui.client.core.FastMap/3488076414" , [
        @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::create_com_extjs_gxt_ui_client_core_FastMap_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.extjs.gxt.ui.client.core.FastMap_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/extjs/gxt/ui/client/core/FastMap;),
        @com.extjs.gxt.ui.client.core.FastMap_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/extjs/gxt/ui/client/core/FastMap;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "com.extjs.gxt.ui.client.core.FastSet/2139705081" , [
        @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::create_com_extjs_gxt_ui_client_core_FastSet_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.extjs.gxt.ui.client.core.FastSet_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/extjs/gxt/ui/client/core/FastSet;),
        @com.extjs.gxt.ui.client.core.FastSet_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/extjs/gxt/ui/client/core/FastSet;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "com.extjs.gxt.ui.client.data.RpcMap/3441186752" , [
        @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::create_com_extjs_gxt_ui_client_data_RpcMap_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.extjs.gxt.ui.client.data.RpcMap_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/extjs/gxt/ui/client/data/RpcMap;),
        @com.extjs.gxt.ui.client.data.RpcMap_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/extjs/gxt/ui/client/data/RpcMap;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "com.google.gwt.http.client.RequestException/190587325" , [
        @com.google.gwt.http.client.RequestException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.http.client.RequestException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/http/client/RequestException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "com.google.gwt.i18n.client.impl.DateRecord/112389920" , [
        @com.google.gwt.i18n.client.impl.DateRecord_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.i18n.client.impl.DateRecord_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/i18n/client/impl/DateRecord;),
        @com.google.gwt.i18n.client.impl.DateRecord_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/google/gwt/i18n/client/impl/DateRecord;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Lcom.google.gwt.i18n.client.impl.DateRecord;/2823890031" , [
        @com.google.gwt.i18n.client.impl.DateRecord_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.i18n.client.impl.DateRecord_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Lcom/google/gwt/i18n/client/impl/DateRecord;),
        @com.google.gwt.i18n.client.impl.DateRecord_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Lcom/google/gwt/i18n/client/impl/DateRecord;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "com.google.gwt.json.client.JSONException/2941795468" , [
        @com.google.gwt.json.client.JSONException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.json.client.JSONException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/json/client/JSONException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException/3936916533" , [
        @com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/user/client/rpc/IncompatibleRemoteServiceException;),
        @com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/google/gwt/user/client/rpc/IncompatibleRemoteServiceException;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "com.google.gwt.user.client.rpc.SerializableException/3047383460" , [
        @com.google.gwt.user.client.rpc.SerializableException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.SerializableException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/user/client/rpc/SerializableException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "com.google.gwt.user.client.rpc.SerializationException/2836333220" , [
        @com.google.gwt.user.client.rpc.SerializationException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.SerializationException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/user/client/rpc/SerializationException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "com.google.gwt.user.client.rpc.ServiceDefTarget$NoServiceEntryPointSpecifiedException/3408313447" , [
        @com.google.gwt.user.client.rpc.ServiceDefTarget_NoServiceEntryPointSpecifiedException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.ServiceDefTarget_NoServiceEntryPointSpecifiedException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/user/client/rpc/ServiceDefTarget$NoServiceEntryPointSpecifiedException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "com.google.gwt.xml.client.impl.DOMParseException/3799120635" , [
        @com.google.gwt.xml.client.impl.DOMParseException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.xml.client.impl.DOMParseException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/xml/client/impl/DOMParseException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.io.IOException/1159940531" , [
        @com.google.gwt.user.client.rpc.core.java.io.IOException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.io.IOException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/io/IOException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.ArithmeticException/1539622151" , [
        @com.google.gwt.user.client.rpc.core.java.lang.ArithmeticException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.ArithmeticException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/ArithmeticException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.ArrayIndexOutOfBoundsException/600550433" , [
        @com.google.gwt.user.client.rpc.core.java.lang.ArrayIndexOutOfBoundsException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.ArrayIndexOutOfBoundsException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/ArrayIndexOutOfBoundsException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.ArrayStoreException/3540507190" , [
        @com.google.gwt.user.client.rpc.core.java.lang.ArrayStoreException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.ArrayStoreException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/ArrayStoreException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.Boolean/476441737" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Boolean_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Boolean_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/Boolean;),
        @com.google.gwt.user.client.rpc.core.java.lang.Boolean_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/lang/Boolean;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Ljava.lang.Boolean;/2507200464" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Boolean_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Boolean_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Ljava/lang/Boolean;),
        @com.google.gwt.user.client.rpc.core.java.lang.Boolean_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Ljava/lang/Boolean;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.Byte/1571082439" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Byte_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Byte_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/Byte;),
        @com.google.gwt.user.client.rpc.core.java.lang.Byte_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/lang/Byte;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Ljava.lang.Byte;/3053163818" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Byte_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Byte_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Ljava/lang/Byte;),
        @com.google.gwt.user.client.rpc.core.java.lang.Byte_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Ljava/lang/Byte;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.ClassCastException/702295179" , [
        @com.google.gwt.user.client.rpc.core.java.lang.ClassCastException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.ClassCastException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/ClassCastException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.Double/858496421" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Double_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Double_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/Double;),
        @com.google.gwt.user.client.rpc.core.java.lang.Double_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/lang/Double;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Ljava.lang.Double;/2857560407" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Double_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Double_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Ljava/lang/Double;),
        @com.google.gwt.user.client.rpc.core.java.lang.Double_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Ljava/lang/Double;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.Exception/1920171873" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/Exception;),
        @com.google.gwt.user.client.rpc.core.java.lang.Exception_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/lang/Exception;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.Float/1718559123" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Float_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Float_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/Float;),
        @com.google.gwt.user.client.rpc.core.java.lang.Float_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/lang/Float;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Ljava.lang.Float;/3956181278" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Float_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Float_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Ljava/lang/Float;),
        @com.google.gwt.user.client.rpc.core.java.lang.Float_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Ljava/lang/Float;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.IllegalArgumentException/1755012560" , [
        @com.google.gwt.user.client.rpc.core.java.lang.IllegalArgumentException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.IllegalArgumentException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/IllegalArgumentException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.IllegalStateException/1972187323" , [
        @com.google.gwt.user.client.rpc.core.java.lang.IllegalStateException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.IllegalStateException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/IllegalStateException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.IndexOutOfBoundsException/2489527753" , [
        @com.google.gwt.user.client.rpc.core.java.lang.IndexOutOfBoundsException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.IndexOutOfBoundsException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/IndexOutOfBoundsException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.Integer/3438268394" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Integer_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Integer_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/Integer;),
        @com.google.gwt.user.client.rpc.core.java.lang.Integer_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/lang/Integer;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Ljava.lang.Integer;/1574882222" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Integer_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Integer_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Ljava/lang/Integer;),
        @com.google.gwt.user.client.rpc.core.java.lang.Integer_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Ljava/lang/Integer;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.Long/4227064769" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Long_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Long_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/Long;),
        @com.google.gwt.user.client.rpc.core.java.lang.Long_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/lang/Long;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Ljava.lang.Long;/97727328" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Long_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Long_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Ljava/lang/Long;),
        @com.google.gwt.user.client.rpc.core.java.lang.Long_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Ljava/lang/Long;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.NegativeArraySizeException/3846860241" , [
        @com.google.gwt.user.client.rpc.core.java.lang.NegativeArraySizeException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.NegativeArraySizeException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/NegativeArraySizeException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.NullPointerException/1463492344" , [
        @com.google.gwt.user.client.rpc.core.java.lang.NullPointerException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.NullPointerException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/NullPointerException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.NumberFormatException/3305228476" , [
        @com.google.gwt.user.client.rpc.core.java.lang.NumberFormatException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.NumberFormatException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/NumberFormatException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.RuntimeException/515124647" , [
        @com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/RuntimeException;),
        @com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/lang/RuntimeException;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.Short/551743396" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Short_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Short_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/Short;),
        @com.google.gwt.user.client.rpc.core.java.lang.Short_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/lang/Short;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Ljava.lang.Short;/3779901635" , [
        @com.google.gwt.user.client.rpc.core.java.lang.Short_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.Short_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Ljava/lang/Short;),
        @com.google.gwt.user.client.rpc.core.java.lang.Short_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Ljava/lang/Short;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.String/2004016611" , [
        @com.google.gwt.user.client.rpc.core.java.lang.String_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.String_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/String;),
        @com.google.gwt.user.client.rpc.core.java.lang.String_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/lang/String;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.StringIndexOutOfBoundsException/500777603" , [
        @com.google.gwt.user.client.rpc.core.java.lang.StringIndexOutOfBoundsException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.StringIndexOutOfBoundsException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/StringIndexOutOfBoundsException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Ljava.lang.String;/2600011424" , [
        @com.google.gwt.user.client.rpc.core.java.lang.String_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.String_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Ljava/lang/String;),
        @com.google.gwt.user.client.rpc.core.java.lang.String_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Ljava/lang/String;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.UnsupportedOperationException/3744010015" , [
        @com.google.gwt.user.client.rpc.core.java.lang.UnsupportedOperationException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.UnsupportedOperationException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/UnsupportedOperationException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.lang.annotation.AnnotationTypeMismatchException/976205828" , [
        @com.google.gwt.user.client.rpc.core.java.lang.annotation.AnnotationTypeMismatchException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.annotation.AnnotationTypeMismatchException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/annotation/AnnotationTypeMismatchException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.sql.Date/3996530531" , [
        @com.google.gwt.user.client.rpc.core.java.sql.Date_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.sql.Date_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/sql/Date;),
        @com.google.gwt.user.client.rpc.core.java.sql.Date_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/sql/Date;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Ljava.sql.Date;/773242198" , [
        @com.google.gwt.user.client.rpc.core.java.sql.Date_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.sql.Date_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Ljava/sql/Date;),
        @com.google.gwt.user.client.rpc.core.java.sql.Date_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Ljava/sql/Date;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.sql.Time/831929183" , [
        @com.google.gwt.user.client.rpc.core.java.sql.Time_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.sql.Time_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/sql/Time;),
        @com.google.gwt.user.client.rpc.core.java.sql.Time_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/sql/Time;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Ljava.sql.Time;/3521163259" , [
        @com.google.gwt.user.client.rpc.core.java.sql.Time_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.sql.Time_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Ljava/sql/Time;),
        @com.google.gwt.user.client.rpc.core.java.sql.Time_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Ljava/sql/Time;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.sql.Timestamp/1769758459" , [
        @com.google.gwt.user.client.rpc.core.java.sql.Timestamp_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.sql.Timestamp_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/sql/Timestamp;),
        @com.google.gwt.user.client.rpc.core.java.sql.Timestamp_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/sql/Timestamp;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Ljava.sql.Timestamp;/1267684111" , [
        @com.google.gwt.user.client.rpc.core.java.sql.Timestamp_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.sql.Timestamp_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Ljava/sql/Timestamp;),
        @com.google.gwt.user.client.rpc.core.java.sql.Timestamp_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Ljava/sql/Timestamp;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.ArrayList/3821976829" , [
        @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::create_com_google_gwt_user_client_rpc_core_java_util_ArrayList_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.ArrayList_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/ArrayList;),
        @com.google.gwt.user.client.rpc.core.java.util.ArrayList_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/ArrayList;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.Arrays$ArrayList/1243019747" , [
        @com.google.gwt.user.client.rpc.core.java.util.Arrays.ArrayList_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.Arrays.ArrayList_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/List;),
        @com.google.gwt.user.client.rpc.core.java.util.Arrays.ArrayList_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/List;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.ConcurrentModificationException/2717383897" , [
        @com.google.gwt.user.client.rpc.core.java.util.ConcurrentModificationException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.ConcurrentModificationException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/ConcurrentModificationException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.Date/1659716317" , [
        @com.google.gwt.user.client.rpc.core.java.util.Date_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.Date_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/Date;),
        @com.google.gwt.user.client.rpc.core.java.util.Date_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/Date;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "[Ljava.util.Date;/3828292433" , [
        @com.google.gwt.user.client.rpc.core.java.util.Date_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.Date_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Ljava/util/Date;),
        @com.google.gwt.user.client.rpc.core.java.util.Date_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Ljava/util/Date;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.EmptyStackException/89438517" , [
        @com.google.gwt.user.client.rpc.core.java.util.EmptyStackException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.EmptyStackException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/EmptyStackException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.HashMap/962170901" , [
        @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::create_com_google_gwt_user_client_rpc_core_java_util_HashMap_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.HashMap_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/HashMap;),
        @com.google.gwt.user.client.rpc.core.java.util.HashMap_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/HashMap;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.HashSet/1594477813" , [
        @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::create_com_google_gwt_user_client_rpc_core_java_util_HashSet_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.HashSet_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/HashSet;),
        @com.google.gwt.user.client.rpc.core.java.util.HashSet_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/HashSet;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.IdentityHashMap/3881143367" , [
        @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::create_com_google_gwt_user_client_rpc_core_java_util_IdentityHashMap_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.IdentityHashMap_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/IdentityHashMap;),
        @com.google.gwt.user.client.rpc.core.java.util.IdentityHashMap_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/IdentityHashMap;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.LinkedHashMap/1551059846" , [
        @com.google.gwt.user.client.rpc.core.java.util.LinkedHashMap_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.LinkedHashMap_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/LinkedHashMap;),
        @com.google.gwt.user.client.rpc.core.java.util.LinkedHashMap_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/LinkedHashMap;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.LinkedHashSet/3628722029" , [
        @com.google.gwt.user.client.rpc.core.java.util.LinkedHashSet_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.LinkedHashSet_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/LinkedHashSet;),
        @com.google.gwt.user.client.rpc.core.java.util.LinkedHashSet_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/LinkedHashSet;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.LinkedList/1060625595" , [
        @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::create_com_google_gwt_user_client_rpc_core_java_util_LinkedList_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.LinkedList_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/LinkedList;),
        @com.google.gwt.user.client.rpc.core.java.util.LinkedList_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/LinkedList;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.NoSuchElementException/1559248883" , [
        @com.google.gwt.user.client.rpc.core.java.util.NoSuchElementException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.NoSuchElementException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/NoSuchElementException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.Stack/1031431137" , [
        @com.google.gwt.user.client.rpc.core.java.util.Stack_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.Stack_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/Stack;),
        @com.google.gwt.user.client.rpc.core.java.util.Stack_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/Stack;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.TooManyListenersException/2023078032" , [
        @com.google.gwt.user.client.rpc.core.java.util.TooManyListenersException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.TooManyListenersException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/TooManyListenersException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.TreeMap/1575826026" , [
        @com.google.gwt.user.client.rpc.core.java.util.TreeMap_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.TreeMap_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/TreeMap;),
        @com.google.gwt.user.client.rpc.core.java.util.TreeMap_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/TreeMap;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.TreeSet/1002270346" , [
        @com.google.gwt.user.client.rpc.core.java.util.TreeSet_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.TreeSet_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/TreeSet;),
        @com.google.gwt.user.client.rpc.core.java.util.TreeSet_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/TreeSet;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "java.util.Vector/3125574444" , [
        @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::create_com_google_gwt_user_client_rpc_core_java_util_Vector_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.Vector_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/Vector;),
        @com.google.gwt.user.client.rpc.core.java.util.Vector_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/Vector;)
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "org.talend.mdm.webapp.adaptor.smtp2.client.exception.SmtpException/764477007" , [
        @org.talend.mdm.webapp.adaptor.smtp2.client.exception.SmtpException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @org.talend.mdm.webapp.adaptor.smtp2.client.exception.SmtpException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lorg/talend/mdm/webapp/adaptor/smtp2/client/exception/SmtpException;),
      ]);
    
    @com.google.gwt.user.client.rpc.impl.SerializerBase::registerMethods(Lcom/google/gwt/user/client/rpc/impl/SerializerBase$MethodMap;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(
      @org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service_TypeSerializer::methodMap,
      "org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean/2371396060" , [
        @org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lorg/talend/mdm/webapp/adaptor/smtp2/shared/SmtpConfigurationBean;),
        @org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lorg/talend/mdm/webapp/adaptor/smtp2/shared/SmtpConfigurationBean;)
      ]);
    
  }-*/;
  
  private static void raiseSerializationException(String msg) throws SerializationException {
    throw new SerializationException(msg);
  }
  
}
