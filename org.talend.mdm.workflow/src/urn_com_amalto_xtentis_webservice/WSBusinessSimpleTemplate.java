
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Can be used as a simple alternative to avoid loading a schema xsd:simpleType Element
 * 				@see putBusinessTemplateSchema
 * 				xsd types MUST be used as xsdbasetype
 * 			
 * 
 * <p>Java class for WSBusinessSimpleTemplate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSBusinessSimpleTemplate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="xsdBaseType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSBusinessSimpleTemplate", propOrder = {
    "name",
    "xsdBaseType",
    "pattern"
})
public class WSBusinessSimpleTemplate {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String xsdBaseType;
    protected String pattern;

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
     * Gets the value of the xsdBaseType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXsdBaseType() {
        return xsdBaseType;
    }

    /**
     * Sets the value of the xsdBaseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXsdBaseType(String value) {
        this.xsdBaseType = value;
    }

    /**
     * Gets the value of the pattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets the value of the pattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPattern(String value) {
        this.pattern = value;
    }

}
