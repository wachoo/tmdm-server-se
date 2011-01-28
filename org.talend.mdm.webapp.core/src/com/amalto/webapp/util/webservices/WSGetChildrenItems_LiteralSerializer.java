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

public class WSGetChildrenItems_LiteralSerializer extends LiteralObjectSerializerBase implements Initializable  {
    private static final QName ns1_clusterName_QNAME = new QName("", "clusterName");
    private static final QName ns3_string_TYPE_QNAME = SchemaConstants.QNAME_TYPE_STRING;
    private CombinedSerializer ns3_myns3_string__java_lang_String_String_Serializer;
    private static final QName ns1_conceptName_QNAME = new QName("", "conceptName");
    private static final QName ns1_PKXpaths_QNAME = new QName("", "PKXpaths");
    private static final QName ns2_WSStringArray_TYPE_QNAME = new QName("urn-com-amalto-xtentis-webservice", "WSStringArray");
    private CombinedSerializer ns2_myWSStringArray_LiteralSerializer;
    private static final QName ns1_FKXpath_QNAME = new QName("", "FKXpath");
    private static final QName ns1_labelXpath_QNAME = new QName("", "labelXpath");
    private static final QName ns1_fatherPK_QNAME = new QName("", "fatherPK");
    private static final QName ns1_whereItem_QNAME = new QName("", "whereItem");
    private static final QName ns2_WSWhereItem_TYPE_QNAME = new QName("urn-com-amalto-xtentis-webservice", "WSWhereItem");
    private CombinedSerializer ns2_myWSWhereItem_LiteralSerializer;
    private static final QName ns1_start_QNAME = new QName("", "start");
    private static final QName ns3_int_TYPE_QNAME = SchemaConstants.QNAME_TYPE_INT;
    private CombinedSerializer ns3_myns3__int__int_Int_Serializer;
    private static final QName ns1_limit_QNAME = new QName("", "limit");
    
    public WSGetChildrenItems_LiteralSerializer(QName type, String encodingStyle) {
        this(type, encodingStyle, false);
    }
    
    public WSGetChildrenItems_LiteralSerializer(QName type, String encodingStyle, boolean encodeType) {
        super(type, true, encodingStyle, encodeType);
    }
    
    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        ns3_myns3_string__java_lang_String_String_Serializer = (CombinedSerializer)registry.getSerializer("", java.lang.String.class, ns3_string_TYPE_QNAME);
        ns2_myWSStringArray_LiteralSerializer = (CombinedSerializer)registry.getSerializer("", com.amalto.webapp.util.webservices.WSStringArray.class, ns2_WSStringArray_TYPE_QNAME);
        ns2_myWSWhereItem_LiteralSerializer = (CombinedSerializer)registry.getSerializer("", com.amalto.webapp.util.webservices.WSWhereItem.class, ns2_WSWhereItem_TYPE_QNAME);
        ns3_myns3__int__int_Int_Serializer = (CombinedSerializer)registry.getSerializer("", int.class, ns3_int_TYPE_QNAME);
    }
    
    public Object doDeserialize(XMLReader reader,
        SOAPDeserializationContext context) throws Exception {
        com.amalto.webapp.util.webservices.WSGetChildrenItems instance = new com.amalto.webapp.util.webservices.WSGetChildrenItems();
        Object member=null;
        QName elementName;
        List values;
        Object value;
        
        reader.nextElementContent();
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_clusterName_QNAME)) {
                member = ns3_myns3_string__java_lang_String_String_Serializer.deserialize(ns1_clusterName_QNAME, reader, context);
                if (member == null) {
                    throw new DeserializationException("literal.unexpectedNull");
                }
                instance.setClusterName((java.lang.String)member);
                reader.nextElementContent();
            } else {
                throw new DeserializationException("literal.unexpectedElementName", new Object[] { ns1_clusterName_QNAME, reader.getName() });
            }
        }
        else {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_conceptName_QNAME)) {
                member = ns3_myns3_string__java_lang_String_String_Serializer.deserialize(ns1_conceptName_QNAME, reader, context);
                if (member == null) {
                    throw new DeserializationException("literal.unexpectedNull");
                }
                instance.setConceptName((java.lang.String)member);
                reader.nextElementContent();
            } else {
                throw new DeserializationException("literal.unexpectedElementName", new Object[] { ns1_conceptName_QNAME, reader.getName() });
            }
        }
        else {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_PKXpaths_QNAME)) {
                member = ns2_myWSStringArray_LiteralSerializer.deserialize(ns1_PKXpaths_QNAME, reader, context);
                if (member == null) {
                    throw new DeserializationException("literal.unexpectedNull");
                }
                instance.setPKXpaths((com.amalto.webapp.util.webservices.WSStringArray)member);
                reader.nextElementContent();
            } else {
                throw new DeserializationException("literal.unexpectedElementName", new Object[] { ns1_PKXpaths_QNAME, reader.getName() });
            }
        }
        else {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_FKXpath_QNAME)) {
                member = ns3_myns3_string__java_lang_String_String_Serializer.deserialize(ns1_FKXpath_QNAME, reader, context);
                instance.setFKXpath((java.lang.String)member);
                reader.nextElementContent();
            }
        }
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_labelXpath_QNAME)) {
                member = ns3_myns3_string__java_lang_String_String_Serializer.deserialize(ns1_labelXpath_QNAME, reader, context);
                if (member == null) {
                    throw new DeserializationException("literal.unexpectedNull");
                }
                instance.setLabelXpath((java.lang.String)member);
                reader.nextElementContent();
            } else {
                throw new DeserializationException("literal.unexpectedElementName", new Object[] { ns1_labelXpath_QNAME, reader.getName() });
            }
        }
        else {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_fatherPK_QNAME)) {
                member = ns3_myns3_string__java_lang_String_String_Serializer.deserialize(ns1_fatherPK_QNAME, reader, context);
                instance.setFatherPK((java.lang.String)member);
                reader.nextElementContent();
            }
        }
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_whereItem_QNAME)) {
                member = ns2_myWSWhereItem_LiteralSerializer.deserialize(ns1_whereItem_QNAME, reader, context);
                instance.setWhereItem((com.amalto.webapp.util.webservices.WSWhereItem)member);
                reader.nextElementContent();
            }
        }
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_start_QNAME)) {
                member = ns3_myns3__int__int_Int_Serializer.deserialize(ns1_start_QNAME, reader, context);
                if (member == null) {
                    throw new DeserializationException("literal.unexpectedNull");
                }
                instance.setStart(((Integer)member).intValue());
                reader.nextElementContent();
            } else {
                throw new DeserializationException("literal.unexpectedElementName", new Object[] { ns1_start_QNAME, reader.getName() });
            }
        }
        else {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_limit_QNAME)) {
                member = ns3_myns3__int__int_Int_Serializer.deserialize(ns1_limit_QNAME, reader, context);
                if (member == null) {
                    throw new DeserializationException("literal.unexpectedNull");
                }
                instance.setLimit(((Integer)member).intValue());
                reader.nextElementContent();
            } else {
                throw new DeserializationException("literal.unexpectedElementName", new Object[] { ns1_limit_QNAME, reader.getName() });
            }
        }
        else {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }
        
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return (Object)instance;
    }
    
    public void doSerializeAttributes(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        com.amalto.webapp.util.webservices.WSGetChildrenItems instance = (com.amalto.webapp.util.webservices.WSGetChildrenItems)obj;
        
    }
    public void doSerialize(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        com.amalto.webapp.util.webservices.WSGetChildrenItems instance = (com.amalto.webapp.util.webservices.WSGetChildrenItems)obj;
        
        if (instance.getClusterName() == null) {
            throw new SerializationException("literal.unexpectedNull");
        }
        ns3_myns3_string__java_lang_String_String_Serializer.serialize(instance.getClusterName(), ns1_clusterName_QNAME, null, writer, context);
        if (instance.getConceptName() == null) {
            throw new SerializationException("literal.unexpectedNull");
        }
        ns3_myns3_string__java_lang_String_String_Serializer.serialize(instance.getConceptName(), ns1_conceptName_QNAME, null, writer, context);
        if (instance.getPKXpaths() == null) {
            throw new SerializationException("literal.unexpectedNull");
        }
        ns2_myWSStringArray_LiteralSerializer.serialize(instance.getPKXpaths(), ns1_PKXpaths_QNAME, null, writer, context);
        ns3_myns3_string__java_lang_String_String_Serializer.serialize(instance.getFKXpath(), ns1_FKXpath_QNAME, null, writer, context);
        if (instance.getLabelXpath() == null) {
            throw new SerializationException("literal.unexpectedNull");
        }
        ns3_myns3_string__java_lang_String_String_Serializer.serialize(instance.getLabelXpath(), ns1_labelXpath_QNAME, null, writer, context);
        ns3_myns3_string__java_lang_String_String_Serializer.serialize(instance.getFatherPK(), ns1_fatherPK_QNAME, null, writer, context);
        ns2_myWSWhereItem_LiteralSerializer.serialize(instance.getWhereItem(), ns1_whereItem_QNAME, null, writer, context);
        if (new Integer(instance.getStart()) == null) {
            throw new SerializationException("literal.unexpectedNull");
        }
        ns3_myns3__int__int_Int_Serializer.serialize(new Integer(instance.getStart()), ns1_start_QNAME, null, writer, context);
        if (new Integer(instance.getLimit()) == null) {
            throw new SerializationException("literal.unexpectedNull");
        }
        ns3_myns3__int__int_Int_Serializer.serialize(new Integer(instance.getLimit()), ns1_limit_QNAME, null, writer, context);
    }
}
