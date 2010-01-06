
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSTemplateType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSTemplateType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="SIMPLE"/>
 *     &lt;enumeration value="COMPLEX"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSTemplateType")
@XmlEnum
public enum WSTemplateType {

    SIMPLE,
    COMPLEX;

    public String value() {
        return name();
    }

    public static WSTemplateType fromValue(String v) {
        return valueOf(v);
    }

}
