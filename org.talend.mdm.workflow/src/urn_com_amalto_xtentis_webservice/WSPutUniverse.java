
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutUniverse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutUniverse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsUniverse" type="{urn-com-amalto-xtentis-webservice}WSUniverse"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutUniverse", propOrder = {
    "wsUniverse"
})
public class WSPutUniverse {

    @XmlElement(required = true)
    protected WSUniverse wsUniverse;

    /**
     * Gets the value of the wsUniverse property.
     * 
     * @return
     *     possible object is
     *     {@link WSUniverse }
     *     
     */
    public WSUniverse getWsUniverse() {
        return wsUniverse;
    }

    /**
     * Sets the value of the wsUniverse property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSUniverse }
     *     
     */
    public void setWsUniverse(WSUniverse value) {
        this.wsUniverse = value;
    }

}
