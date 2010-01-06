
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSExecuteRoutingOrderV2Synchronously complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSExecuteRoutingOrderV2Synchronously">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="routingOrderV2PK" type="{urn-com-amalto-xtentis-webservice}WSRoutingOrderV2PK"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSExecuteRoutingOrderV2Synchronously", propOrder = {
    "routingOrderV2PK"
})
public class WSExecuteRoutingOrderV2Synchronously {

    @XmlElement(required = true)
    protected WSRoutingOrderV2PK routingOrderV2PK;

    /**
     * Gets the value of the routingOrderV2PK property.
     * 
     * @return
     *     possible object is
     *     {@link WSRoutingOrderV2PK }
     *     
     */
    public WSRoutingOrderV2PK getRoutingOrderV2PK() {
        return routingOrderV2PK;
    }

    /**
     * Sets the value of the routingOrderV2PK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSRoutingOrderV2PK }
     *     
     */
    public void setRoutingOrderV2PK(WSRoutingOrderV2PK value) {
        this.routingOrderV2PK = value;
    }

}
