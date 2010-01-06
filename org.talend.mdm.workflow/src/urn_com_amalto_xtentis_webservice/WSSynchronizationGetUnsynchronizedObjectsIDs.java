
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSSynchronizationGetUnsynchronizedObjectsIDs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSSynchronizationGetUnsynchronizedObjectsIDs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="revisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="objectName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="instancePattern" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="synchronizationPlanName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSSynchronizationGetUnsynchronizedObjectsIDs", propOrder = {
    "revisionID",
    "objectName",
    "instancePattern",
    "synchronizationPlanName"
})
public class WSSynchronizationGetUnsynchronizedObjectsIDs {

    @XmlElement(required = true, nillable = true)
    protected String revisionID;
    @XmlElement(required = true)
    protected String objectName;
    @XmlElement(required = true, nillable = true)
    protected String instancePattern;
    @XmlElement(required = true, nillable = true)
    protected String synchronizationPlanName;

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
     * Gets the value of the instancePattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstancePattern() {
        return instancePattern;
    }

    /**
     * Sets the value of the instancePattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstancePattern(String value) {
        this.instancePattern = value;
    }

    /**
     * Gets the value of the synchronizationPlanName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSynchronizationPlanName() {
        return synchronizationPlanName;
    }

    /**
     * Sets the value of the synchronizationPlanName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSynchronizationPlanName(String value) {
        this.synchronizationPlanName = value;
    }

}
