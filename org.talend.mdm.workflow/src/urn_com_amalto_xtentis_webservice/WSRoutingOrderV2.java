
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSRoutingOrderV2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSRoutingOrderV2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="status" type="{urn-com-amalto-xtentis-webservice}WSRoutingOrderV2Status"/>
 *         &lt;element name="timeCreated" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timeScheduled" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timeLastRunStarted" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timeLastRunCompleted" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="wsItemPK" type="{urn-com-amalto-xtentis-webservice}WSItemPK"/>
 *         &lt;element name="serviceJNDI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="serviceParameters" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bindingUniverseName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bindingUserToken" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSRoutingOrderV2", propOrder = {
    "name",
    "status",
    "timeCreated",
    "timeScheduled",
    "timeLastRunStarted",
    "timeLastRunCompleted",
    "wsItemPK",
    "serviceJNDI",
    "serviceParameters",
    "message",
    "bindingUniverseName",
    "bindingUserToken"
})
public class WSRoutingOrderV2 {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected WSRoutingOrderV2Status status;
    protected long timeCreated;
    protected long timeScheduled;
    protected long timeLastRunStarted;
    protected long timeLastRunCompleted;
    @XmlElement(required = true)
    protected WSItemPK wsItemPK;
    @XmlElement(required = true)
    protected String serviceJNDI;
    @XmlElement(required = true, nillable = true)
    protected String serviceParameters;
    protected String message;
    protected String bindingUniverseName;
    protected String bindingUserToken;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

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
     * Gets the value of the timeCreated property.
     * 
     */
    public long getTimeCreated() {
        return timeCreated;
    }

    /**
     * Sets the value of the timeCreated property.
     * 
     */
    public void setTimeCreated(long value) {
        this.timeCreated = value;
    }

    /**
     * Gets the value of the timeScheduled property.
     * 
     */
    public long getTimeScheduled() {
        return timeScheduled;
    }

    /**
     * Sets the value of the timeScheduled property.
     * 
     */
    public void setTimeScheduled(long value) {
        this.timeScheduled = value;
    }

    /**
     * Gets the value of the timeLastRunStarted property.
     * 
     */
    public long getTimeLastRunStarted() {
        return timeLastRunStarted;
    }

    /**
     * Sets the value of the timeLastRunStarted property.
     * 
     */
    public void setTimeLastRunStarted(long value) {
        this.timeLastRunStarted = value;
    }

    /**
     * Gets the value of the timeLastRunCompleted property.
     * 
     */
    public long getTimeLastRunCompleted() {
        return timeLastRunCompleted;
    }

    /**
     * Sets the value of the timeLastRunCompleted property.
     * 
     */
    public void setTimeLastRunCompleted(long value) {
        this.timeLastRunCompleted = value;
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
     * Gets the value of the serviceJNDI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceJNDI() {
        return serviceJNDI;
    }

    /**
     * Sets the value of the serviceJNDI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceJNDI(String value) {
        this.serviceJNDI = value;
    }

    /**
     * Gets the value of the serviceParameters property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceParameters() {
        return serviceParameters;
    }

    /**
     * Sets the value of the serviceParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceParameters(String value) {
        this.serviceParameters = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the bindingUniverseName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBindingUniverseName() {
        return bindingUniverseName;
    }

    /**
     * Sets the value of the bindingUniverseName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBindingUniverseName(String value) {
        this.bindingUniverseName = value;
    }

    /**
     * Gets the value of the bindingUserToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBindingUserToken() {
        return bindingUserToken;
    }

    /**
     * Sets the value of the bindingUserToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBindingUserToken(String value) {
        this.bindingUserToken = value;
    }

}
