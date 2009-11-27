
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSSynchronizationGetUnsynchronizedItemPKs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSSynchronizationGetUnsynchronizedItemPKs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="revisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="wsDataClusterPOJOPK" type="{urn-com-amalto-xtentis-webservice}WSDataClusterPK"/>
 *         &lt;element name="conceptName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="instancePattern" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="synchronizationPlanName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="start" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSSynchronizationGetUnsynchronizedItemPKs", propOrder = {
    "revisionID",
    "wsDataClusterPOJOPK",
    "conceptName",
    "instancePattern",
    "synchronizationPlanName",
    "start",
    "limit"
})
public class WSSynchronizationGetUnsynchronizedItemPKs {

    @XmlElement(required = true, nillable = true)
    protected String revisionID;
    @XmlElement(required = true)
    protected WSDataClusterPK wsDataClusterPOJOPK;
    @XmlElement(required = true)
    protected String conceptName;
    @XmlElement(required = true, nillable = true)
    protected String instancePattern;
    @XmlElement(required = true, nillable = true)
    protected String synchronizationPlanName;
    protected int start;
    protected int limit;

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
     * Gets the value of the wsDataClusterPOJOPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSDataClusterPK }
     *     
     */
    public WSDataClusterPK getWsDataClusterPOJOPK() {
        return wsDataClusterPOJOPK;
    }

    /**
     * Sets the value of the wsDataClusterPOJOPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDataClusterPK }
     *     
     */
    public void setWsDataClusterPOJOPK(WSDataClusterPK value) {
        this.wsDataClusterPOJOPK = value;
    }

    /**
     * Gets the value of the conceptName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConceptName() {
        return conceptName;
    }

    /**
     * Sets the value of the conceptName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConceptName(String value) {
        this.conceptName = value;
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

    /**
     * Gets the value of the start property.
     * 
     */
    public int getStart() {
        return start;
    }

    /**
     * Sets the value of the start property.
     * 
     */
    public void setStart(int value) {
        this.start = value;
    }

    /**
     * Gets the value of the limit property.
     * 
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     */
    public void setLimit(int value) {
        this.limit = value;
    }

}
