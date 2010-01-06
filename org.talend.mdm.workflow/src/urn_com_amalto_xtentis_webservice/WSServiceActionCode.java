
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSServiceActionCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSServiceActionCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="START"/>
 *     &lt;enumeration value="STOP"/>
 *     &lt;enumeration value="STATUS"/>
 *     &lt;enumeration value="EXECUTE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSServiceActionCode")
@XmlEnum
public enum WSServiceActionCode {

    START,
    STOP,
    STATUS,
    EXECUTE;

    public String value() {
        return name();
    }

    public static WSServiceActionCode fromValue(String v) {
        return valueOf(v);
    }

}
