
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSRoutingEngineV2Action complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSRoutingEngineV2Action">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsAction" type="{urn-com-amalto-xtentis-webservice}WSRoutingEngineV2ActionCode"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSRoutingEngineV2Action", propOrder = {
    "wsAction"
})
public class WSRoutingEngineV2Action {

    @XmlElement(required = true)
    protected WSRoutingEngineV2ActionCode wsAction;

    /**
     * Gets the value of the wsAction property.
     * 
     * @return
     *     possible object is
     *     {@link WSRoutingEngineV2ActionCode }
     *     
     */
    public WSRoutingEngineV2ActionCode getWsAction() {
        return wsAction;
    }

    /**
     * Sets the value of the wsAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSRoutingEngineV2ActionCode }
     *     
     */
    public void setWsAction(WSRoutingEngineV2ActionCode value) {
        this.wsAction = value;
    }

}
