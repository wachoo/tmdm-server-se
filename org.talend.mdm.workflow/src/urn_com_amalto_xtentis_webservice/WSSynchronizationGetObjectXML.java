
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSSynchronizationGetObjectXML complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSSynchronizationGetObjectXML">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="revisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="objectName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="uniqueID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSSynchronizationGetObjectXML", propOrder = {
    "revisionID",
    "objectName",
    "uniqueID"
})
public class WSSynchronizationGetObjectXML {

    @XmlElement(required = true, nillable = true)
    protected String revisionID;
    @XmlElement(required = true)
    protected String objectName;
    @XmlElement(required = true)
    protected String uniqueID;

    /**
     * Gets the value of the revisionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRevisionID() {
        return revisionID;
    }

    /**
     * Sets the value of the revisionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRevisionID(String value) {
        this.revisionID = value;
    }

    /**
     * Gets the value of the objectName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Sets the value of the objectName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectName(String value) {
        this.objectName = value;
    }

    /**
     * Gets the value of the uniqueID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUniqueID() {
        return uniqueID;
    }

    /**
     * Sets the value of the uniqueID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUniqueID(String value) {
        this.uniqueID = value;
    }

}
