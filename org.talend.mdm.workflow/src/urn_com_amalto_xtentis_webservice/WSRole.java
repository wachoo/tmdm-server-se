
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSRole complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSRole">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="specification" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="objectType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="admin" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="instance" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="instanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="writable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                             &lt;element name="parameter" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
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
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSRole", propOrder = {
    "name",
    "description",
    "specification"
})
public class WSRole {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true, nillable = true)
    protected String description;
    @XmlElement(nillable = true)
    protected List<WSRole.Specification> specification;

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
     * Gets the value of the specification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the specification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSRole.Specification }
     * 
     * 
     */
    public List<WSRole.Specification> getSpecification() {
        if (specification == null) {
            specification = new ArrayList<WSRole.Specification>();
        }
        return this.specification;
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
     *         &lt;element name="objectType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="admin" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="instance" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="instanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="writable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *                   &lt;element name="parameter" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
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
        "objectType",
        "admin",
        "instance"
    })
    public static class Specification {

        @XmlElement(required = true)
        protected String objectType;
        protected boolean admin;
        protected List<WSRole.Specification.Instance> instance;

        /**
         * Gets the value of the objectType property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getObjectType() {
            return objectType;
        }

        /**
         * Sets the value of the objectType property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setObjectType(String value) {
            this.objectType = value;
        }

        /**
         * Gets the value of the admin property.
         * 
         */
        public boolean isAdmin() {
            return admin;
        }

        /**
         * Sets the value of the admin property.
         * 
         */
        public void setAdmin(boolean value) {
            this.admin = value;
        }

        /**
         * Gets the value of the instance property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the instance property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInstance().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WSRole.Specification.Instance }
         * 
         * 
         */
        public List<WSRole.Specification.Instance> getInstance() {
            if (instance == null) {
                instance = new ArrayList<WSRole.Specification.Instance>();
            }
            return this.instance;
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
         *         &lt;element name="instanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="writable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
         *         &lt;element name="parameter" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
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
            "instanceName",
            "writable",
            "parameter"
        })
        public static class Instance {

            @XmlElement(required = true)
            protected String instanceName;
            protected boolean writable;
            protected List<String> parameter;

            /**
             * Gets the value of the instanceName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInstanceName() {
                return instanceName;
            }

            /**
             * Sets the value of the instanceName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInstanceName(String value) {
                this.instanceName = value;
            }

            /**
             * Gets the value of the writable property.
             * 
             */
            public boolean isWritable() {
                return writable;
            }

            /**
             * Sets the value of the writable property.
             * 
             */
            public void setWritable(boolean value) {
                this.writable = value;
            }

            /**
             * Gets the value of the parameter property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the parameter property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getParameter().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            public List<String> getParameter() {
                if (parameter == null) {
                    parameter = new ArrayList<String>();
                }
                return this.parameter;
            }

        }

    }

}
