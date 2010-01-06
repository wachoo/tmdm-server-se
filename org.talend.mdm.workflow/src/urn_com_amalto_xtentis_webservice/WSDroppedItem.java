
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSDroppedItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSDroppedItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="revisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="wsDataClusterPK" type="{urn-com-amalto-xtentis-webservice}WSDataClusterPK"/>
 *         &lt;element name="uniqueId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="conceptName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ids" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="partPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="insertionUserName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="insertionTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="projection" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSDroppedItem", propOrder = {
    "revisionID",
    "wsDataClusterPK",
    "uniqueId",
    "conceptName",
    "ids",
    "partPath",
    "insertionUserName",
    "insertionTime",
    "projection"
})
public class WSDroppedItem {

    @XmlElement(required = true, nillable = true)
    protected String revisionID;
    @XmlElement(required = true)
    protected WSDataClusterPK wsDataClusterPK;
    @XmlElement(required = true)
    protected String uniqueId;
    @XmlElement(required = true, nillable = true)
    protected String conceptName;
    @XmlElement(required = true, nillable = true)
    protected List<String> ids;
    @XmlElement(required = true)
    protected String partPath;
    @XmlElement(required = true, nillable = true)
    protected String insertionUserName;
    @XmlElement(required = true, type = Long.class, nillable = true)
    protected Long insertionTime;
    @XmlElement(required = true)
    protected String projection;

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
     * Gets the value of the wsDataClusterPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSDataClusterPK }
     *     
     */
    public WSDataClusterPK getWsDataClusterPK() {
        return wsDataClusterPK;
    }

    /**
     * Sets the value of the wsDataClusterPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDataClusterPK }
     *     
     */
    public void setWsDataClusterPK(WSDataClusterPK value) {
        this.wsDataClusterPK = value;
    }

    /**
     * Gets the value of the uniqueId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Sets the value of the uniqueId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUniqueId(String value) {
        this.uniqueId = value;
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
     * Gets the value of the ids property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ids property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getIds() {
        if (ids == null) {
            ids = new ArrayList<String>();
        }
        return this.ids;
    }

    /**
     * Gets the value of the partPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartPath() {
        return partPath;
    }

    /**
     * Sets the value of the partPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartPath(String value) {
        this.partPath = value;
    }

    /**
     * Gets the value of the insertionUserName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsertionUserName() {
        return insertionUserName;
    }

    /**
     * Sets the value of the insertionUserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsertionUserName(String value) {
        this.insertionUserName = value;
    }

    /**
     * Gets the value of the insertionTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getInsertionTime() {
        return insertionTime;
    }

    /**
     * Sets the value of the insertionTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setInsertionTime(Long value) {
        this.insertionTime = value;
    }

    /**
     * Gets the value of the projection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjection() {
        return projection;
    }

    /**
     * Sets the value of the projection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjection(String value) {
        this.projection = value;
    }

}
