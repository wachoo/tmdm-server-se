
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSUniverse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSUniverse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="xtentisObjectsRevisionIDs" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="xtentisObjectName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="revisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="defaultItemsRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="itemsRevisionIDs" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="conceptPattern" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="revisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "WSUniverse", propOrder = {
    "name",
    "description",
    "xtentisObjectsRevisionIDs",
    "defaultItemsRevisionID",
    "itemsRevisionIDs"
})
public class WSUniverse {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true, nillable = true)
    protected String description;
    protected List<WSUniverse.XtentisObjectsRevisionIDs> xtentisObjectsRevisionIDs;
    @XmlElement(required = true, nillable = true)
    protected String defaultItemsRevisionID;
    protected List<WSUniverse.ItemsRevisionIDs> itemsRevisionIDs;

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
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the xtentisObjectsRevisionIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xtentisObjectsRevisionIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXtentisObjectsRevisionIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSUniverse.XtentisObjectsRevisionIDs }
     * 
     * 
     */
    public List<WSUniverse.XtentisObjectsRevisionIDs> getXtentisObjectsRevisionIDs() {
        if (xtentisObjectsRevisionIDs == null) {
            xtentisObjectsRevisionIDs = new ArrayList<WSUniverse.XtentisObjectsRevisionIDs>();
        }
        return this.xtentisObjectsRevisionIDs;
    }

    /**
     * Gets the value of the defaultItemsRevisionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultItemsRevisionID() {
        return defaultItemsRevisionID;
    }

    /**
     * Sets the value of the defaultItemsRevisionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultItemsRevisionID(String value) {
        this.defaultItemsRevisionID = value;
    }

    /**
     * Gets the value of the itemsRevisionIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itemsRevisionIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItemsRevisionIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSUniverse.ItemsRevisionIDs }
     * 
     * 
     */
    public List<WSUniverse.ItemsRevisionIDs> getItemsRevisionIDs() {
        if (itemsRevisionIDs == null) {
            itemsRevisionIDs = new ArrayList<WSUniverse.ItemsRevisionIDs>();
        }
        return this.itemsRevisionIDs;
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
     *         &lt;element name="conceptPattern" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="revisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "conceptPattern",
        "revisionID"
    })
    public static class ItemsRevisionIDs {

        @XmlElement(required = true)
        protected String conceptPattern;
        @XmlElement(required = true, nillable = true)
        protected String revisionID;

        /**
         * Gets the value of the conceptPattern property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getConceptPattern() {
            return conceptPattern;
        }

        /**
         * Sets the value of the conceptPattern property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setConceptPattern(String value) {
            this.conceptPattern = value;
        }

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
     *         &lt;element name="xtentisObjectName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="revisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "xtentisObjectName",
        "revisionID"
    })
    public static class XtentisObjectsRevisionIDs {

        @XmlElement(required = true)
        protected String xtentisObjectName;
        @XmlElement(required = true, nillable = true)
        protected String revisionID;

        /**
         * Gets the value of the xtentisObjectName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getXtentisObjectName() {
            return xtentisObjectName;
        }

        /**
         * Sets the value of the xtentisObjectName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setXtentisObjectName(String value) {
            this.xtentisObjectName = value;
        }

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

    }

}
