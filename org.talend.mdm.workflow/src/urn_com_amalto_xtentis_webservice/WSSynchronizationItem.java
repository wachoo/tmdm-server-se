
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WSSynchronizationItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSSynchronizationItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsItemPK" type="{urn-com-amalto-xtentis-webservice}WSItemPK"/>
 *         &lt;element name="localRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lastRunPlan" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="status" type="{urn-com-amalto-xtentis-webservice}WSSynchronizationItemStatus"/>
 *         &lt;element name="resolvedProjection" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="remoteInstances" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="remoteSystemName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="remoteRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="xml" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="lastLocalSynchronizationTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSSynchronizationItem", propOrder = {
    "wsItemPK",
    "localRevisionID",
    "lastRunPlan",
    "status",
    "resolvedProjection",
    "remoteInstances"
})
public class WSSynchronizationItem {

    @XmlElement(required = true)
    protected WSItemPK wsItemPK;
    @XmlElement(required = true, nillable = true)
    protected String localRevisionID;
    @XmlElement(required = true, nillable = true)
    protected String lastRunPlan;
    @XmlElement(required = true)
    protected WSSynchronizationItemStatus status;
    @XmlElement(required = true, nillable = true)
    protected String resolvedProjection;
    protected List<WSSynchronizationItem.RemoteInstances> remoteInstances;

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
     * Gets the value of the localRevisionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalRevisionID() {
        return localRevisionID;
    }

    /**
     * Sets the value of the localRevisionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalRevisionID(String value) {
        this.localRevisionID = value;
    }

    /**
     * Gets the value of the lastRunPlan property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastRunPlan() {
        return lastRunPlan;
    }

    /**
     * Sets the value of the lastRunPlan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastRunPlan(String value) {
        this.lastRunPlan = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link WSSynchronizationItemStatus }
     *     
     */
    public WSSynchronizationItemStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSSynchronizationItemStatus }
     *     
     */
    public void setStatus(WSSynchronizationItemStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the resolvedProjection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResolvedProjection() {
        return resolvedProjection;
    }

    /**
     * Sets the value of the resolvedProjection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResolvedProjection(String value) {
        this.resolvedProjection = value;
    }

    /**
     * Gets the value of the remoteInstances property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the remoteInstances property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRemoteInstances().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSSynchronizationItem.RemoteInstances }
     * 
     * 
     */
    public List<WSSynchronizationItem.RemoteInstances> getRemoteInstances() {
        if (remoteInstances == null) {
            remoteInstances = new ArrayList<WSSynchronizationItem.RemoteInstances>();
        }
        return this.remoteInstances;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="remoteSystemName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="remoteRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="xml" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="lastLocalSynchronizationTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "remoteSystemName",
        "remoteRevisionID",
        "xml",
        "lastLocalSynchronizationTime"
    })
    public static class RemoteInstances {

        @XmlElement(required = true)
        protected String remoteSystemName;
        @XmlElement(required = true, nillable = true)
        protected String remoteRevisionID;
        @XmlElement(required = true, nillable = true)
        protected String xml;
        @XmlElement(required = true, nillable = true)
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar lastLocalSynchronizationTime;

        /**
         * Gets the value of the remoteSystemName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRemoteSystemName() {
            return remoteSystemName;
        }

        /**
         * Sets the value of the remoteSystemName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRemoteSystemName(String value) {
            this.remoteSystemName = value;
        }

        /**
         * Gets the value of the remoteRevisionID property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRemoteRevisionID() {
            return remoteRevisionID;
        }

        /**
         * Sets the value of the remoteRevisionID property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRemoteRevisionID(String value) {
            this.remoteRevisionID = value;
        }

        /**
         * Gets the value of the xml property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getXml() {
            return xml;
        }

        /**
         * Sets the value of the xml property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setXml(String value) {
            this.xml = value;
        }

        /**
         * Gets the value of the lastLocalSynchronizationTime property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getLastLocalSynchronizationTime() {
            return lastLocalSynchronizationTime;
        }

        /**
         * Sets the value of the lastLocalSynchronizationTime property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setLastLocalSynchronizationTime(XMLGregorianCalendar value) {
            this.lastLocalSynchronizationTime = value;
        }

    }

}
