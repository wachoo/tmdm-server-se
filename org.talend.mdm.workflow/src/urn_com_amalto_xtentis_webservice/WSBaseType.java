
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSBaseType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSBaseType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="string"/>
 *     &lt;enumeration value="int"/>
 *     &lt;enumeration value="token"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSBaseType")
@XmlEnum
public enum WSBaseType {

    @XmlEnumValue("string")
    STRING("string"),
    @XmlEnumValue("int")
    INT("int"),
    @XmlEnumValue("token")
    TOKEN("token");
    private final String value;

    WSBaseType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WSBaseType fromValue(String v) {
        for (WSBaseType c: WSBaseType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
