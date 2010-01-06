
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSSynchronizationItemStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSSynchronizationItemStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="PENDING"/>
 *     &lt;enumeration value="MANUAL"/>
 *     &lt;enumeration value="RESOLVED"/>
 *     &lt;enumeration value="EXECUTED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSSynchronizationItemStatus")
@XmlEnum
public enum WSSynchronizationItemStatus {

    PENDING,
    MANUAL,
    RESOLVED,
    EXECUTED;

    public String value() {
        return name();
    }

    public static WSSynchronizationItemStatus fromValue(String v) {
        return valueOf(v);
    }

}
