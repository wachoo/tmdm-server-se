// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.2_01, construire R40)
// Generated source version: 1.1.2

package talend.core.transformer.plugin.v2.tiscall.webservices;

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

public class Args_LiteralSerializer extends LiteralObjectSerializerBase implements Initializable  {
    private static final QName ns1_item_QNAME = new QName("http://talend.org", "item");
    private static final QName ns2_string_TYPE_QNAME = SchemaConstants.QNAME_TYPE_STRING;
    private CombinedSerializer ns2_myns2_string__java_lang_String_String_Serializer;
    
    public Args_LiteralSerializer(QName type, String encodingStyle) {
        this(type, encodingStyle, false);
    }
    
    public Args_LiteralSerializer(QName type, String encodingStyle, boolean encodeType) {
        super(type, true, encodingStyle, encodeType);
    }
    
    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        ns2_myns2_string__java_lang_String_String_Serializer = (CombinedSerializer)registry.getSerializer("", java.lang.String.class, ns2_string_TYPE_QNAME);
    }
    
    public Object doDeserialize(XMLReader reader,
        SOAPDeserializationContext context) throws Exception {
        talend.core.transformer.plugin.v2.tiscall.webservices.Args instance = new talend.core.transformer.plugin.v2.tiscall.webservices.Args();
        Object member=null;
        QName elementName;
        List values;
        Object value;
        
        reader.nextElementContent();
        elementName = reader.getName();
        if ((reader.getState() == XMLReader.START) && (elementName.equals(ns1_item_QNAME))) {
            values = new ArrayList();
            for(;;) {
                elementName = reader.getName();
                if ((reader.getState() == XMLReader.START) && (elementName.equals(ns1_item_QNAME))) {
                    value = ns2_myns2_string__java_lang_String_String_Serializer.deserialize(ns1_item_QNAME, reader, context);
                    if (value == null) {
                        throw new DeserializationException("literal.unexpectedNull");
                    }
                    values.add(value);
                    reader.nextElementContent();
                } else {
                    break;
                }
            }
            member = new java.lang.String[values.size()];
            member = values.toArray((Object[]) member);
            instance.setItem((java.lang.String[])member);
        }
        else {
            instance.setItem(new java.lang.String[0]);
        }
        
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return (Object)instance;
    }
    
    public void doSerializeAttributes(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        talend.core.transformer.plugin.v2.tiscall.webservices.Args instance = (talend.core.transformer.plugin.v2.tiscall.webservices.Args)obj;
        
    }
    public void doSerialize(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        talend.core.transformer.plugin.v2.tiscall.webservices.Args instance = (talend.core.transformer.plugin.v2.tiscall.webservices.Args)obj;
        
        if (instance.getItem() != null) {
            for (int i = 0; i < instance.getItem().length; ++i) {
                ns2_myns2_string__java_lang_String_String_Serializer.serialize(instance.getItem()[i], ns1_item_QNAME, null, writer, context);
            }
        }
    }
}
