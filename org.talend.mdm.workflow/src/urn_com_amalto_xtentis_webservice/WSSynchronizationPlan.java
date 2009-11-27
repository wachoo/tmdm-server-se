
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSSynchronizationPlan complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSSynchronizationPlan">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="remoteSystemName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="remoteSystemURL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="remoteSystemUsername" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="remoteSystemPassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tisURL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tisUsername" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tisPassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tisParameters" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="xtentisObjectsSynchronizations" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="xtentisObjectName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="synchronizations" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="instancePattern" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="localRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="remoteRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="algorithm" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="itemsSynchronizations" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="conceptName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="idsPattern" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="localCluster" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="localRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="remoteCluster" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="remoteRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="algorithm" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "WSSynchronizationPlan", propOrder = {
    "name",
    "description",
    "remoteSystemName",
    "remoteSystemURL",
    "remoteSystemUsername",
    "remoteSystemPassword",
    "tisURL",
    "tisUsername",
    "tisPassword",
    "tisParameters",
    "xtentisObjectsSynchronizations",
    "itemsSynchronizations"
})
public class WSSynchronizationPlan {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true, nillable = true)
    protected String description;
    @XmlElement(required = true)
    protected String remoteSystemName;
    @XmlElement(required = true)
    protected String remoteSystemURL;
    @XmlElement(required = true, nillable = true)
    protected String remoteSystemUsername;
    @XmlElement(required = true, nillable = true)
    protected String remoteSystemPassword;
    @XmlElement(required = true, nillable = true)
    protected String tisURL;
    @XmlElement(required = true, nillable = true)
    protected String tisUsername;
    @XmlElement(required = true, nillable = true)
    protected String tisPassword;
    @XmlElement(required = true, nillable = true)
    protected String tisParameters;
    protected List<WSSynchronizationPlan.XtentisObjectsSynchronizations> xtentisObjectsSynchronizations;
    protected List<WSSynchronizationPlan.ItemsSynchronizations> itemsSynchronizations;

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
     * Gets the value of the remoteSystemURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteSystemURL() {
        return remoteSystemURL;
    }

    /**
     * Sets the value of the remoteSystemURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteSystemURL(String value) {
        this.remoteSystemURL = value;
    }

    /**
     * Gets the value of the remoteSystemUsername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteSystemUsername() {
        return remoteSystemUsername;
    }

    /**
     * Sets the value of the remoteSystemUsername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteSystemUsername(String value) {
        this.remoteSystemUsername = value;
    }

    /**
     * Gets the value of the remoteSystemPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteSystemPassword() {
        return remoteSystemPassword;
    }

    /**
     * Sets the value of the remoteSystemPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteSystemPassword(String value) {
        this.remoteSystemPassword = value;
    }

    /**
     * Gets the value of the tisURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTisURL() {
        return tisURL;
    }

    /**
     * Sets the value of the tisURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTisURL(String value) {
        this.tisURL = value;
    }

    /**
     * Gets the value of the tisUsername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTisUsername() {
        return tisUsername;
    }

    /**
     * Sets the value of the tisUsername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTisUsername(String value) {
        this.tisUsername = value;
    }

    /**
     * Gets the value of the tisPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTisPassword() {
        return tisPassword;
    }

    /**
     * Sets the value of the tisPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTisPassword(String value) {
        this.tisPassword = value;
    }

    /**
     * Gets the value of the tisParameters property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTisParameters() {
        return tisParameters;
    }

    /**
     * Sets the value of the tisParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTisParameters(String value) {
        this.tisParameters = value;
    }

    /**
     * Gets the value of the xtentisObjectsSynchronizations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xtentisObjectsSynchronizations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXtentisObjectsSynchronizations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSSynchronizationPlan.XtentisObjectsSynchronizations }
     * 
     * 
     */
    public List<WSSynchronizationPlan.XtentisObjectsSynchronizations> getXtentisObjectsSynchronizations() {
        if (xtentisObjectsSynchronizations == null) {
            xtentisObjectsSynchronizations = new ArrayList<WSSynchronizationPlan.XtentisObjectsSynchronizations>();
        }
        return this.xtentisObjectsSynchronizations;
    }

    /**
     * Gets the value of the itemsSynchronizations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itemsSynchronizations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItemsSynchronizations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSSynchronizationPlan.ItemsSynchronizations }
     * 
     * 
     */
    public List<WSSynchronizationPlan.ItemsSynchronizations> getItemsSynchronizations() {
        if (itemsSynchronizations == null) {
            itemsSynchronizations = new ArrayList<WSSynchronizationPlan.ItemsSynchronizations>();
        }
        return this.itemsSynchronizations;
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
     *         &lt;element name="conceptName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="idsPattern" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="localCluster" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="localRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="remoteCluster" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="remoteRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="algorithm" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "conceptName",
        "idsPattern",
        "localCluster",
        "localRevisionID",
        "remoteCluster",
        "remoteRevisionID",
        "algorithm"
    })
    public static class ItemsSynchronizations {

        @XmlElement(required = true)
        protected String conceptName;
        @XmlElement(required = true)
        protected String idsPattern;
        @XmlElement(required = true)
        protected String localCluster;
        @XmlElement(required = true, nillable = true)
        protected String localRevisionID;
        @XmlElement(required = true)
        protected String remoteCluster;
        @XmlElement(required = true, nillable = true)
        protected String remoteRevisionID;
        @XmlElement(required = true, nillable = true)
        protected String algorithm;

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
         * Gets the value of the idsPattern property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIdsPattern() {
            return idsPattern;
        }

        /**
         * Sets the value of the idsPattern property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIdsPattern(String value) {
            this.idsPattern = value;
        }

        /**
         * Gets the value of the localCluster property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLocalCluster() {
            return localCluster;
        }

        /**
         * Sets the value of the localCluster property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLocalCluster(String value) {
            this.localCluster = value;
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
         * Gets the value of the remoteCluster property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRemoteCluster() {
            return remoteCluster;
        }

        /**
         * Sets the value of the remoteCluster property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRemoteCluster(String value) {
            this.remoteCluster = value;
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
         * Gets the value of the algorithm property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAlgorithm() {
            return algorithm;
        }

        /**
         * Sets the value of the algorithm property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAlgorithm(String value) {
            this.algorithm = value;
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
     *         &lt;element name="synchronizations" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="instancePattern" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="localRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="remoteRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="algorithm" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    @XmlType(name = "", propOrder = {
        "xtentisObjectName",
        "synchronizations"
    })
    public static class XtentisObjectsSynchronizations {

        @XmlElement(required = true)
        protected String xtentisObjectName;
        @XmlElement(required = true)
        protected List<WSSynchronizationPlan.XtentisObjectsSynchronizations.Synchronizations> synchronizations;

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
         * Gets the value of the synchronizations property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the synchronizations property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSynchronizations().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WSSynchronizationPlan.XtentisObjectsSynchronizations.Synchronizations }
         * 
         * 
         */
        public List<WSSynchronizationPlan.XtentisObjectsSynchronizations.Synchronizations> getSynchronizations() {
            if (synchronizations == null) {
                synchronizations = new ArrayList<WSSynchronizationPlan.XtentisObjectsSynchronizations.Synchronizations>();
            }
            return this.synchronizations;
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
         *         &lt;element name="instancePattern" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="localRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="remoteRevisionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="algorithm" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "instancePattern",
            "localRevisionID",
            "remoteRevisionID",
            "algorithm"
        })
        public static class Synchronizations {

            @XmlElement(required = true)
            protected String instancePattern;
            @XmlElement(required = true, nillable = true)
            protected String localRevisionID;
            @XmlElement(required = true, nillable = true)
            protected String remoteRevisionID;
            @XmlElement(required = true, nillable = true)
            protected String algorithm;

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
             * Gets the value of the algorithm property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getAlgorithm() {
                return algorithm;
            }

            /**
             * Sets the value of the algorithm property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setAlgorithm(String value) {
                this.algorithm = value;
            }

        }

    }

}
