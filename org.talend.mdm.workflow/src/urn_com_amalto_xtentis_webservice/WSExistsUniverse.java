
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSExistsUniverse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSExistsUniverse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsUniversePK" type="{urn-com-amalto-xtentis-webservice}WSUniversePK"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSExistsUniverse", propOrder = {
    "wsUniversePK"
})
public class WSExistsUniverse {

    @XmlElement(required = true)
    protected WSUniversePK wsUniversePK;

    /**
     * Gets the value of the wsUniversePK property.
     * 
     * @return
     *     possible object is
     *     {@link WSUniversePK }
     *     
     */
    public WSUniversePK getWsUniversePK() {
        return wsUniversePK;
    }

    /**
     * Sets the value of the wsUniversePK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSUniversePK }
     *     
     */
    public void setWsUniversePK(WSUniversePK value) {
        this.wsUniversePK = value;
    }

}
