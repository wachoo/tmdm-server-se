
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Can be used as a simple alternative to avoid loading a schema xsd:simpleType or xsd:complexType Element
 * 				@see putBusinessTemplateSchema
 * 				one of simpletemplate or complextemplate must be filled in accordance with type
 * 			
 * 
 * <p>Java class for WSBusinessTemplate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSBusinessTemplate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type" type="{urn-com-amalto-xtentis-webservice}WSTemplateType"/>
 *         &lt;element name="simpleTemplate" type="{urn-com-amalto-xtentis-webservice}WSBusinessSimpleTemplate" minOccurs="0"/>
 *         &lt;element name="complexTemplate" type="{urn-com-amalto-xtentis-webservice}WSBusinessComplexTemplate" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSBusinessTemplate", propOrder = {
    "name",
    "type",
    "simpleTemplate",
    "complexTemplate"
})
public class WSBusinessTemplate {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected WSTemplateType type;
    protected WSBusinessSimpleTemplate simpleTemplate;
    protected WSBusinessComplexTemplate complexTemplate;

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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link WSTemplateType }
     *     
     */
    public WSTemplateType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSTemplateType }
     *     
     */
    public void setType(WSTemplateType value) {
        this.type = value;
    }

    /**
     * Gets the value of the simpleTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link WSBusinessSimpleTemplate }
     *     
     */
    public WSBusinessSimpleTemplate getSimpleTemplate() {
        return simpleTemplate;
    }

    /**
     * Sets the value of the simpleTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSBusinessSimpleTemplate }
     *     
     */
    public void setSimpleTemplate(WSBusinessSimpleTemplate value) {
        this.simpleTemplate = value;
    }

    /**
     * Gets the value of the complexTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link WSBusinessComplexTemplate }
     *     
     */
    public WSBusinessComplexTemplate getComplexTemplate() {
        return complexTemplate;
    }

    /**
     * Sets the value of the complexTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSBusinessComplexTemplate }
     *     
     */
    public void setComplexTemplate(WSBusinessComplexTemplate value) {
        this.complexTemplate = value;
    }

}
