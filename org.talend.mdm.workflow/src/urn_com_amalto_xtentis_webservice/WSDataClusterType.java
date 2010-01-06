
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSDataClusterType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSDataClusterType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="CACHE"/>
 *     &lt;enumeration value="ITEMS"/>
 *     &lt;enumeration value="UNKNOWN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSDataClusterType")
@XmlEnum
public enum WSDataClusterType {

    CACHE,
    ITEMS,
    UNKNOWN;

    public String value() {
        return name();
    }

    public static WSDataClusterType fromValue(String v) {
        return valueOf(v);
    }

}
