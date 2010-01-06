
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Restore one Item instance through revision
 * 			
 * 
 * <p>Java class for WSVersioningRestoreItemByRevision complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSVersioningRestoreItemByRevision">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="versioningSystemName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="wsItemPK" type="{urn-com-amalto-xtentis-webservice}WSItemPK"/>
 *         &lt;element name="revision" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSVersioningRestoreItemByRevision", propOrder = {
    "versioningSystemName",
    "wsItemPK",
    "revision"
})
public class WSVersioningRestoreItemByRevision {

    @XmlElement(required = true, nillable = true)
    protected String versioningSystemName;
    @XmlElement(required = true)
    protected WSItemPK wsItemPK;
    @XmlElement(required = true, nillable = true)
    protected String revision;

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

    /**
     * Gets the value of the revision property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Sets the value of the revision property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRevision(String value) {
        this.revision = value;
    }

}
