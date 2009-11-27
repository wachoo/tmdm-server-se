
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSConnectorResponseCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSConnectorResponseCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="OK"/>
 *     &lt;enumeration value="STOPPED"/>
 *     &lt;enumeration value="ERROR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSConnectorResponseCode")
@XmlEnum
public enum WSConnectorResponseCode {

    OK,
    STOPPED,
    ERROR;

    public String value() {
        return name();
    }

    public static WSConnectorResponseCode fromValue(String v) {
        return valueOf(v);
    }

}
