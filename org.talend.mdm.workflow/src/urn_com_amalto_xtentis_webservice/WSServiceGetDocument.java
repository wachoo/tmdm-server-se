
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSServiceGetDocument complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSServiceGetDocument">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="configure" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="document" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="configureSchema" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="defaultConfig" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSServiceGetDocument", propOrder = {
    "description",
    "configure",
    "document",
    "configureSchema",
    "defaultConfig"
})
public class WSServiceGetDocument {

    @XmlElement(required = true, nillable = true)
    protected String description;
    @XmlElement(required = true, nillable = true)
    protected String configure;
    @XmlElement(required = true, nillable = true)
    protected String document;
    @XmlElement(required = true, nillable = true)
    protected String configureSchema;
    @XmlElement(required = true, nillable = true)
    protected String defaultConfig;

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
     * Gets the value of the configure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfigure() {
        return configure;
    }

    /**
     * Sets the value of the configure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfigure(String value) {
        this.configure = value;
    }

    /**
     * Gets the value of the document property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocument(String value) {
        this.document = value;
    }

    /**
     * Gets the value of the configureSchema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfigureSchema() {
        return configureSchema;
    }

    /**
     * Sets the value of the configureSchema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfigureSchema(String value) {
        this.configureSchema = value;
    }

    /**
     * Gets the value of the defaultConfig property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultConfig() {
        return defaultConfig;
    }

    /**
     * Sets the value of the defaultConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultConfig(String value) {
        this.defaultConfig = value;
    }

}
