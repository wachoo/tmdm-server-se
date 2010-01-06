
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSConnectorFunction.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSConnectorFunction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="GET_STATUS"/>
 *     &lt;enumeration value="START"/>
 *     &lt;enumeration value="STOP"/>
 *     &lt;enumeration value="PULL"/>
 *     &lt;enumeration value="PUSH"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSConnectorFunction")
@XmlEnum
public enum WSConnectorFunction {

    GET_STATUS,
    START,
    STOP,
    PULL,
    PUSH;

    public String value() {
        return name();
    }

    public static WSConnectorFunction fromValue(String v) {
        return valueOf(v);
    }

}
