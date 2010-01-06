
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSDeleteSynchronizationPlan complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSDeleteSynchronizationPlan">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsSynchronizationPlanPK" type="{urn-com-amalto-xtentis-webservice}WSSynchronizationPlanPK"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSDeleteSynchronizationPlan", propOrder = {
    "wsSynchronizationPlanPK"
})
public class WSDeleteSynchronizationPlan {

    @XmlElement(required = true)
    protected WSSynchronizationPlanPK wsSynchronizationPlanPK;

    /**
     * Gets the value of the wsSynchronizationPlanPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSSynchronizationPlanPK }
     *     
     */
    public WSSynchronizationPlanPK getWsSynchronizationPlanPK() {
        return wsSynchronizationPlanPK;
    }

    /**
     * Sets the value of the wsSynchronizationPlanPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSSynchronizationPlanPK }
     *     
     */
    public void setWsSynchronizationPlanPK(WSSynchronizationPlanPK value) {
        this.wsSynchronizationPlanPK = value;
    }

}
