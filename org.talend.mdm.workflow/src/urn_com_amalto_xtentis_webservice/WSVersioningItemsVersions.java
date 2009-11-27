
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Versions of a list of items
 * 			
 * 
 * <p>Java class for WSVersioningItemsVersions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSVersioningItemsVersions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="items" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="wsItemPK" type="{urn-com-amalto-xtentis-webservice}WSItemPK"/>
 *                   &lt;element name="wsVersionEntries" type="{urn-com-amalto-xtentis-webservice}WSVersioningHistoryEntry" maxOccurs="unbounded"/>
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
@XmlType(name = "WSVersioningItemsVersions", propOrder = {
    "items"
})
public class WSVersioningItemsVersions {

    @XmlElement(required = true)
    protected List<WSVersioningItemsVersions.Items> items;

    /**
     * Gets the value of the items property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the items property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSVersioningItemsVersions.Items }
     * 
     * 
     */
    public List<WSVersioningItemsVersions.Items> getItems() {
        if (items == null) {
            items = new ArrayList<WSVersioningItemsVersions.Items>();
        }
        return this.items;
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
     *         &lt;element name="wsItemPK" type="{urn-com-amalto-xtentis-webservice}WSItemPK"/>
     *         &lt;element name="wsVersionEntries" type="{urn-com-amalto-xtentis-webservice}WSVersioningHistoryEntry" maxOccurs="unbounded"/>
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
        "wsItemPK",
        "wsVersionEntries"
    })
    public static class Items {

        @XmlElement(required = true)
        protected WSItemPK wsItemPK;
        @XmlElement(required = true)
        protected List<WSVersioningHistoryEntry> wsVersionEntries;

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
         * Gets the value of the wsVersionEntries property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the wsVersionEntries property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getWsVersionEntries().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WSVersioningHistoryEntry }
         * 
         * 
         */
        public List<WSVersioningHistoryEntry> getWsVersionEntries() {
            if (wsVersionEntries == null) {
                wsVersionEntries = new ArrayList<WSVersioningHistoryEntry>();
            }
            return this.wsVersionEntries;
        }

    }

}
