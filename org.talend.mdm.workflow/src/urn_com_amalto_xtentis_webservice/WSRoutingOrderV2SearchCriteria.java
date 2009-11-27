
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSRoutingOrderV2SearchCriteria complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSRoutingOrderV2SearchCriteria">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="status" type="{urn-com-amalto-xtentis-webservice}WSRoutingOrderV2Status"/>
 *         &lt;element name="anyFieldContains" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nameContains" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="timeCreatedMin" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timeCreatedMax" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timeScheduledMin" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timeScheduledMax" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timeLastRunStartedMin" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timeLastRunStartedMax" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timeLastRunCompletedMin" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timeLastRunCompletedMax" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="itemPKConceptContains" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="itemPKIDFieldsContain" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="serviceJNDIContains" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="serviceParametersContain" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="messageContain" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSRoutingOrderV2SearchCriteria", propOrder = {
    "status",
    "anyFieldContains",
    "nameContains",
    "timeCreatedMin",
    "timeCreatedMax",
    "timeScheduledMin",
    "timeScheduledMax",
    "timeLastRunStartedMin",
    "timeLastRunStartedMax",
    "timeLastRunCompletedMin",
    "timeLastRunCompletedMax",
    "itemPKConceptContains",
    "itemPKIDFieldsContain",
    "serviceJNDIContains",
    "serviceParametersContain",
    "messageContain"
})
public class WSRoutingOrderV2SearchCriteria {

    @XmlElement(required = true)
    protected WSRoutingOrderV2Status status;
    @XmlElement(required = true, nillable = true)
    protected String anyFieldContains;
    @XmlElement(required = true, nillable = true)
    protected String nameContains;
    protected long timeCreatedMin;
    protected long timeCreatedMax;
    protected long timeScheduledMin;
    protected long timeScheduledMax;
    protected long timeLastRunStartedMin;
    protected long timeLastRunStartedMax;
    protected long timeLastRunCompletedMin;
    protected long timeLastRunCompletedMax;
    @XmlElement(required = true, nillable = true)
    protected String itemPKConceptContains;
    @XmlElement(required = true, nillable = true)
    protected String itemPKIDFieldsContain;
    @XmlElement(required = true, nillable = true)
    protected String serviceJNDIContains;
    @XmlElement(required = true, nillable = true)
    protected String serviceParametersContain;
    @XmlElement(required = true, nillable = true)
    protected String messageContain;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link WSRoutingOrderV2Status }
     *     
     */
    public WSRoutingOrderV2Status getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSRoutingOrderV2Status }
     *     
     */
    public void setStatus(WSRoutingOrderV2Status value) {
        this.status = value;
    }

    /**
     * Gets the value of the anyFieldContains property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnyFieldContains() {
        return anyFieldContains;
    }

    /**
     * Sets the value of the anyFieldContains property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnyFieldContains(String value) {
        this.anyFieldContains = value;
    }

    /**
     * Gets the value of the nameContains property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameContains() {
        return nameContains;
    }

    /**
     * Sets the value of the nameContains property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameContains(String value) {
        this.nameContains = value;
    }

    /**
     * Gets the value of the timeCreatedMin property.
     * 
     */
    public long getTimeCreatedMin() {
        return timeCreatedMin;
    }

    /**
     * Sets the value of the timeCreatedMin property.
     * 
     */
    public void setTimeCreatedMin(long value) {
        this.timeCreatedMin = value;
    }

    /**
     * Gets the value of the timeCreatedMax property.
     * 
     */
    public long getTimeCreatedMax() {
        return timeCreatedMax;
    }

    /**
     * Sets the value of the timeCreatedMax property.
     * 
     */
    public void setTimeCreatedMax(long value) {
        this.timeCreatedMax = value;
    }

    /**
     * Gets the value of the timeScheduledMin property.
     * 
     */
    public long getTimeScheduledMin() {
        return timeScheduledMin;
    }

    /**
     * Sets the value of the timeScheduledMin property.
     * 
     */
    public void setTimeScheduledMin(long value) {
        this.timeScheduledMin = value;
    }

    /**
     * Gets the value of the timeScheduledMax property.
     * 
     */
    public long getTimeScheduledMax() {
        return timeScheduledMax;
    }

    /**
     * Sets the value of the timeScheduledMax property.
     * 
     */
    public void setTimeScheduledMax(long value) {
        this.timeScheduledMax = value;
    }

    /**
     * Gets the value of the timeLastRunStartedMin property.
     * 
     */
    public long getTimeLastRunStartedMin() {
        return timeLastRunStartedMin;
    }

    /**
     * Sets the value of the timeLastRunStartedMin property.
     * 
     */
    public void setTimeLastRunStartedMin(long value) {
        this.timeLastRunStartedMin = value;
    }

    /**
     * Gets the value of the timeLastRunStartedMax property.
     * 
     */
    public long getTimeLastRunStartedMax() {
        return timeLastRunStartedMax;
    }

    /**
     * Sets the value of the timeLastRunStartedMax property.
     * 
     */
    public void setTimeLastRunStartedMax(long value) {
        this.timeLastRunStartedMax = value;
    }

    /**
     * Gets the value of the timeLastRunCompletedMin property.
     * 
     */
    public long getTimeLastRunCompletedMin() {
        return timeLastRunCompletedMin;
    }

    /**
     * Sets the value of the timeLastRunCompletedMin property.
     * 
     */
    public void setTimeLastRunCompletedMin(long value) {
        this.timeLastRunCompletedMin = value;
    }

    /**
     * Gets the value of the timeLastRunCompletedMax property.
     * 
     */
    public long getTimeLastRunCompletedMax() {
        return timeLastRunCompletedMax;
    }

    /**
     * Sets the value of the timeLastRunCompletedMax property.
     * 
     */
    public void setTimeLastRunCompletedMax(long value) {
        this.timeLastRunCompletedMax = value;
    }

    /**
     * Gets the value of the itemPKConceptContains property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemPKConceptContains() {
        return itemPKConceptContains;
    }

    /**
     * Sets the value of the itemPKConceptContains property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemPKConceptContains(String value) {
        this.itemPKConceptContains = value;
    }

    /**
     * Gets the value of the itemPKIDFieldsContain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemPKIDFieldsContain() {
        return itemPKIDFieldsContain;
    }

    /**
     * Sets the value of the itemPKIDFieldsContain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemPKIDFieldsContain(String value) {
        this.itemPKIDFieldsContain = value;
    }

    /**
     * Gets the value of the serviceJNDIContains property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceJNDIContains() {
        return serviceJNDIContains;
    }

    /**
     * Sets the value of the serviceJNDIContains property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceJNDIContains(String value) {
        this.serviceJNDIContains = value;
    }

    /**
     * Gets the value of the serviceParametersContain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceParametersContain() {
        return serviceParametersContain;
    }

    /**
     * Sets the value of the serviceParametersContain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceParametersContain(String value) {
        this.serviceParametersContain = value;
    }

    /**
     * Gets the value of the messageContain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageContain() {
        return messageContain;
    }

    /**
     * Sets the value of the messageContain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageContain(String value) {
        this.messageContain = value;
    }

}
