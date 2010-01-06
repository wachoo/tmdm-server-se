
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Get history for one or more Items instances
 * 			
 * 
 * <p>Java class for WSVersioningGetItemHistory complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSVersioningGetItemHistory">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="versioningSystemName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="wsItemPK" type="{urn-com-amalto-xtentis-webservice}WSItemPK"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSVersioningGetItemHistory", propOrder = {
    "versioningSystemName",
    "wsItemPK"
})
public class WSVersioningGetItemHistory {

    @XmlElement(required = true, nillable = true)
    protected String versioningSystemName;
    @XmlElement(required = true)
    protected WSItemPK wsItemPK;

    /**
     * Gets the value of the versioningSystemName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersioningSystemName() {
        return versioningSystemName;
    }

    /**
     * Sets the value of the versioningSystemName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersioningSystemName(String value) {
        this.versioningSystemName = value;
    }

    /**
     * Gets the value of the wsItemPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSItemPK }
     *     
     */
    public WSItemPK getWsItemPK() {
        return wsItemPK;
    }

    /**
     * Sets the value of the wsItemPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSItemPK }
     *     
     */
    public void setWsItemPK(WSItemPK value) {
        this.wsItemPK = value;
    }

}
