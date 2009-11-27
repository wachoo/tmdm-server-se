
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSGetUniverseByRevisionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSGetUniverseByRevisionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="ITEM"/>
 *     &lt;enumeration value="OBJECT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSGetUniverseByRevisionType")
@XmlEnum
public enum WSGetUniverseByRevisionType {

    ITEM,
    OBJECT;

    public String value() {
        return name();
    }

    public static WSGetUniverseByRevisionType fromValue(String v) {
        return valueOf(v);
    }

}
