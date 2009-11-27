
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSSynchronizationPlanActionCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSSynchronizationPlanActionCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="START_FULL"/>
 *     &lt;enumeration value="START_DIFFERENTIAL"/>
 *     &lt;enumeration value="STOP"/>
 *     &lt;enumeration value="RESET"/>
 *     &lt;enumeration value="STATUS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSSynchronizationPlanActionCode")
@XmlEnum
public enum WSSynchronizationPlanActionCode {

    START_FULL,
    START_DIFFERENTIAL,
    STOP,
    RESET,
    STATUS;

    public String value() {
        return name();
    }

    public static WSSynchronizationPlanActionCode fromValue(String v) {
        return valueOf(v);
    }

}
