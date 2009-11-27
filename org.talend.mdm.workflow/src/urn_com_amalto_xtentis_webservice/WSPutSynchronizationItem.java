
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutSynchronizationItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutSynchronizationItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsSynchronizationItem" type="{urn-com-amalto-xtentis-webservice}WSSynchronizationItem"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutSynchronizationItem", propOrder = {
    "wsSynchronizationItem"
})
public class WSPutSynchronizationItem {

    @XmlElement(required = true)
    protected WSSynchronizationItem wsSynchronizationItem;

    /**
     * Gets the value of the wsSynchronizationItem property.
     * 
     * @return
     *     possible object is
     *     {@link WSSynchronizationItem }
     *     
     */
    public WSSynchronizationItem getWsSynchronizationItem() {
        return wsSynchronizationItem;
    }

    /**
     * Sets the value of the wsSynchronizationItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSSynchronizationItem }
     *     
     */
    public void setWsSynchronizationItem(WSSynchronizationItem value) {
        this.wsSynchronizationItem = value;
    }

}
