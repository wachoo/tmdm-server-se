// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation ��1.1.2_01������� R40��
// Generated source version: 1.1.2

package com.amalto.webapp.util.webservices;

import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.encoding.literal.*;
import com.sun.xml.rpc.encoding.literal.DetailFragmentDeserializer;
import com.sun.xml.rpc.encoding.simpletype.*;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.encoding.soap.SOAP12Constants;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;

public class WSHashMap_LiteralSerializer extends LiteralObjectSerializerBase implements Initializable  {
    private static final QName ns1_entry_QNAME = new QName("", "entry");
    private static final QName ns2_WSStartProcessInstance$2d$variable$2d$entry_TYPE_QNAME = new QName("urn-com-amalto-xtentis-webservice", "WSStartProcessInstance-variable-entry");
    private CombinedSerializer ns2_myWSStartProcessInstanceVariableEntry_LiteralSerializer;
    
    public WSHashMap_LiteralSerializer(QName type, String encodingStyle) {
        this(type, encodingStyle, false);
    }
    
    public WSHashMap_LiteralSerializer(QName type, String encodingStyle, boolean encodeType) {
        super(type, true, encodingStyle, encodeType);
    }
    
    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        ns2_myWSStartProcessInstanceVariableEntry_LiteralSerializer = (CombinedSerializer)registry.getSerializer("", com.amalto.webapp.util.webservices.WSStartProcessInstanceVariableEntry.class, ns2_WSStartProcessInstance$2d$variable$2d$entry_TYPE_QNAME);
    }
    
    public Object doDeserialize(XMLReader reader,
        SOAPDeserializationContext context) throws Exception {
        com.amalto.webapp.util.webservices.WSHashMap instance = new com.amalto.webapp.util.webservices.WSHashMap();
        Object member=null;
        QName elementName;
        List values;
        Object value;
        
        reader.nextElementContent();
        elementName = reader.getName();
        if ((reader.getState() == XMLReader.START) && (elementName.equals(ns1_entry_QNAME))) {
            values = new ArrayList();
            for(;;) {
                elementName = reader.getName();
                if ((reader.getState() == XMLReader.START) && (elementName.equals(ns1_entry_QNAME))) {
                    value = ns2_myWSStartProcessInstanceVariableEntry_LiteralSerializer.deserialize(ns1_entry_QNAME, reader, context);
                    values.add(value);
                    reader.nextElementContent();
                } else {
                    break;
                }
            }
            member = new com.amalto.webapp.util.webservices.WSStartProcessInstanceVariableEntry[values.size()];
            member = values.toArray((Object[]) member);
            instance.setEntry((com.amalto.webapp.util.webservices.WSStartProcessInstanceVariableEntry[])member);
        }
        else if(!(reader.getState() == XMLReader.END)) {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }
        
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return (Object)instance;
    }
    
    public void doSerializeAttributes(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        com.amalto.webapp.util.webservices.WSHashMap instance = (com.amalto.webapp.util.webservices.WSHashMap)obj;
        
    }
    public void doSerialize(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        com.amalto.webapp.util.webservices.WSHashMap instance = (com.amalto.webapp.util.webservices.WSHashMap)obj;
        
        if (instance.getEntry() != null) {
            for (int i = 0; i < instance.getEntry().length; ++i) {
                ns2_myWSStartProcessInstanceVariableEntry_LiteralSerializer.serialize(instance.getEntry()[i], ns1_entry_QNAME, null, writer, context);
            }
        }
    }
}
