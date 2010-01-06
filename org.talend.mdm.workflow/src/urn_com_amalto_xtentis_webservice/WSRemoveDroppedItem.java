
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Remove a dropped item from items trash
 * 			
 * 
 * <p>Java class for WSRemoveDroppedItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSRemoveDroppedItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsDroppedItemPK" type="{urn-com-amalto-xtentis-webservice}WSDroppedItemPK"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSRemoveDroppedItem", propOrder = {
    "wsDroppedItemPK"
})
public class WSRemoveDroppedItem {

    @XmlElement(required = true)
    protected WSDroppedItemPK wsDroppedItemPK;

    /**
     * Gets the value of the wsDroppedItemPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSDroppedItemPK }
     *     
     */
    public WSDroppedItemPK getWsDroppedItemPK() {
        return wsDroppedItemPK;
    }

    /**
     * Sets the value of the wsDroppedItemPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDroppedItemPK }
     *     
     */
    public void setWsDroppedItemPK(WSDroppedItemPK value) {
        this.wsDroppedItemPK = value;
    }

}
