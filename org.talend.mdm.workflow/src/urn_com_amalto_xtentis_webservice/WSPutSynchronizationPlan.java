
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutSynchronizationPlan complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutSynchronizationPlan">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsSynchronizationPlan" type="{urn-com-amalto-xtentis-webservice}WSSynchronizationPlan"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutSynchronizationPlan", propOrder = {
    "wsSynchronizationPlan"
})
public class WSPutSynchronizationPlan {

    @XmlElement(required = true)
    protected WSSynchronizationPlan wsSynchronizationPlan;

    /**
     * Gets the value of the wsSynchronizationPlan property.
     * 
     * @return
     *     possible object is
     *     {@link WSSynchronizationPlan }
     *     
     */
    public WSSynchronizationPlan getWsSynchronizationPlan() {
        return wsSynchronizationPlan;
    }

    /**
     * Sets the value of the wsSynchronizationPlan property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSSynchronizationPlan }
     *     
     */
    public void setWsSynchronizationPlan(WSSynchronizationPlan value) {
        this.wsSynchronizationPlan = value;
    }

}
