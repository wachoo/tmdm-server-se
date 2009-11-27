
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSSynchronizationPlanStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSSynchronizationPlanStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsStatusCode" type="{urn-com-amalto-xtentis-webservice}WSSynchronizationPlanStatusCode"/>
 *         &lt;element name="statusMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lastRunStarted" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="lastRunStopped" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSSynchronizationPlanStatus", propOrder = {
    "wsStatusCode",
    "statusMessage",
    "lastRunStarted",
    "lastRunStopped"
})
public class WSSynchronizationPlanStatus {

    @XmlElement(required = true)
    protected WSSynchronizationPlanStatusCode wsStatusCode;
    @XmlElement(required = true, nillable = true)
    protected String statusMessage;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastRunStarted;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastRunStopped;

    /**
     * Gets the value of the wsStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link WSSynchronizationPlanStatusCode }
     *     
     */
    public WSSynchronizationPlanStatusCode getWsStatusCode() {
        return wsStatusCode;
    }

    /**
     * Sets the value of the wsStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSSynchronizationPlanStatusCode }
     *     
     */
    public void setWsStatusCode(WSSynchronizationPlanStatusCode value) {
        this.wsStatusCode = value;
    }

    /**
     * Gets the value of the statusMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the value of the statusMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusMessage(String value) {
        this.statusMessage = value;
    }

    /**
     * Gets the value of the lastRunStarted property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastRunStarted() {
        return lastRunStarted;
    }

    /**
     * Sets the value of the lastRunStarted property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastRunStarted(XMLGregorianCalendar value) {
        this.lastRunStarted = value;
    }

    /**
     * Gets the value of the lastRunStopped property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastRunStopped() {
        return lastRunStopped;
    }

    /**
     * Sets the value of the lastRunStopped property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastRunStopped(XMLGregorianCalendar value) {
        this.lastRunStopped = value;
    }

}
