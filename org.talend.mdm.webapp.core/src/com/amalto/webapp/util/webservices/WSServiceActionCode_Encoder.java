// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation 
// Generated source version: 1.1.2

package com.amalto.webapp.util.webservices;


import com.sun.xml.rpc.encoding.simpletype.*;
import javax.xml.namespace.QName;
import com.sun.xml.rpc.streaming.*;

public class WSServiceActionCode_Encoder extends SimpleTypeEncoderBase {
    
    private static final SimpleTypeEncoder encoder = XSDStringEncoder.getInstance();
    private static final WSServiceActionCode_Encoder instance = new WSServiceActionCode_Encoder();
    
    private WSServiceActionCode_Encoder() {
    }
    
    public static SimpleTypeEncoder getInstance() {
        return instance;
    }
    
    public String objectToString(Object obj, XMLWriter writer) throws Exception {
        java.lang.String value = ((WSServiceActionCode)obj).getValue();
        return encoder.objectToString(value, writer);
    }
    
    public Object stringToObject(String str, XMLReader reader) throws Exception {
        return WSServiceActionCode.fromValue((java.lang.String)encoder.stringToObject(str, reader));
    }
    
}