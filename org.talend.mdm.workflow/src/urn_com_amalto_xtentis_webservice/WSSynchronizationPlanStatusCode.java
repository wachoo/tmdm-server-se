
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSSynchronizationPlanStatusCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSSynchronizationPlanStatusCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="DOES_NOT_EXIST"/>
 *     &lt;enumeration value="COMPLETED"/>
 *     &lt;enumeration value="FAILED"/>
 *     &lt;enumeration value="RUNNING"/>
 *     &lt;enumeration value="SCHEDULED"/>
 *     &lt;enumeration value="STOPPING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSSynchronizationPlanStatusCode")
@XmlEnum
public enum WSSynchronizationPlanStatusCode {

    DOES_NOT_EXIST,
    COMPLETED,
    FAILED,
    RUNNING,
    SCHEDULED,
    STOPPING;

    public String value() {
        return name();
    }

    public static WSSynchronizationPlanStatusCode fromValue(String v) {
        return valueOf(v);
    }

}
