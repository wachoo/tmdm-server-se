
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSResolveSynchronizationItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSResolveSynchronizationItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsSynchronizationItemPK" type="{urn-com-amalto-xtentis-webservice}WSSynchronizationItemPK"/>
 *         &lt;element name="newProjection" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSResolveSynchronizationItem", propOrder = {
    "wsSynchronizationItemPK",
    "newProjection"
})
public class WSResolveSynchronizationItem {

    @XmlElement(required = true)
    protected WSSynchronizationItemPK wsSynchronizationItemPK;
    @XmlElement(required = true)
    protected String newProjection;

    /**
     * Gets the value of the wsSynchronizationItemPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSSynchronizationItemPK }
     *     
     */
    public WSSynchronizationItemPK getWsSynchronizationItemPK() {
        return wsSynchronizationItemPK;
    }

    /**
     * Sets the value of the wsSynchronizationItemPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSSynchronizationItemPK }
     *     
     */
    public void setWsSynchronizationItemPK(WSSynchronizationItemPK value) {
        this.wsSynchronizationItemPK = value;
    }

    /**
     * Gets the value of the newProjection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewProjection() {
        return newProjection;
    }

    /**
     * Sets the value of the newProjection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewProjection(String value) {
        this.newProjection = value;
    }

}
