// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation 
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

public class WSPipeline_LiteralSerializer extends LiteralObjectSerializerBase implements Initializable  {
    private static final QName ns1_typedContentEntry_QNAME = new QName("", "typedContentEntry");
    private static final QName ns2_WSPipeline$2d$typedContentEntry_TYPE_QNAME = new QName("urn-com-amalto-xtentis-webservice", "WSPipeline-typedContentEntry");
    private CombinedSerializer ns2_myWSPipelineTypedContentEntry_LiteralSerializer;
    
    public WSPipeline_LiteralSerializer(QName type, String encodingStyle) {
        this(type, encodingStyle, false);
    }
    
    public WSPipeline_LiteralSerializer(QName type, String encodingStyle, boolean encodeType) {
        super(type, true, encodingStyle, encodeType);
    }
    
    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        ns2_myWSPipelineTypedContentEntry_LiteralSerializer = (CombinedSerializer)registry.getSerializer("", com.amalto.webapp.util.webservices.WSPipelineTypedContentEntry.class, ns2_WSPipeline$2d$typedContentEntry_TYPE_QNAME);
    }
    
    public Object doDeserialize(XMLReader reader,
        SOAPDeserializationContext context) throws Exception {
        com.amalto.webapp.util.webservices.WSPipeline instance = new com.amalto.webapp.util.webservices.WSPipeline();
        Object member=null;
        QName elementName;
        List values;
        Object value;
        
        reader.nextElementContent();
        elementName = reader.getName();
        if ((reader.getState() == XMLReader.START) && (elementName.equals(ns1_typedContentEntry_QNAME))) {
            values = new ArrayList();
            for(;;) {
                elementName = reader.getName();
                if ((reader.getState() == XMLReader.START) && (elementName.equals(ns1_typedContentEntry_QNAME))) {
                    value = ns2_myWSPipelineTypedContentEntry_LiteralSerializer.deserialize(ns1_typedContentEntry_QNAME, reader, context);
                    values.add(value);
                    reader.nextElementContent();
                } else {
                    break;
                }
            }
            member = new com.amalto.webapp.util.webservices.WSPipelineTypedContentEntry[values.size()];
            member = values.toArray((Object[]) member);
            instance.setTypedContentEntry((com.amalto.webapp.util.webservices.WSPipelineTypedContentEntry[])member);
        }
        else if(!(reader.getState() == XMLReader.END)) {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }
        
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return (Object)instance;
    }
    
    public void doSerializeAttributes(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        com.amalto.webapp.util.webservices.WSPipeline instance = (com.amalto.webapp.util.webservices.WSPipeline)obj;
        
    }
    public void doSerialize(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        com.amalto.webapp.util.webservices.WSPipeline instance = (com.amalto.webapp.util.webservices.WSPipeline)obj;
        
        if (instance.getTypedContentEntry() != null) {
            for (int i = 0; i < instance.getTypedContentEntry().length; ++i) {
                ns2_myWSPipelineTypedContentEntry_LiteralSerializer.serialize(instance.getTypedContentEntry()[i], ns1_typedContentEntry_QNAME, null, writer, context);
            }
        }
    }
}