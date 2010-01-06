
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSTransformerVariablesMapping complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSTransformerVariablesMapping">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pipelineVariable" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pluginVariable" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="hardCoding" type="{urn-com-amalto-xtentis-webservice}WSTypedContent" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSTransformerVariablesMapping", propOrder = {
    "pipelineVariable",
    "pluginVariable",
    "hardCoding"
})
public class WSTransformerVariablesMapping {

    @XmlElement(required = true, nillable = true)
    protected String pipelineVariable;
    @XmlElement(required = true)
    protected String pluginVariable;
    @XmlElementRef(name = "hardCoding", type = JAXBElement.class)
    protected JAXBElement<WSTypedContent> hardCoding;

    /**
     * Gets the value of the pipelineVariable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPipelineVariable() {
        return pipelineVariable;
    }

    /**
     * Sets the value of the pipelineVariable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPipelineVariable(String value) {
        this.pipelineVariable = value;
    }

    /**
     * Gets the value of the pluginVariable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPluginVariable() {
        return pluginVariable;
    }

    /**
     * Sets the value of the pluginVariable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPluginVariable(String value) {
        this.pluginVariable = value;
    }

    /**
     * Gets the value of the hardCoding property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WSTypedContent }{@code >}
     *     
     */
    public JAXBElement<WSTypedContent> getHardCoding() {
        return hardCoding;
    }

    /**
     * Sets the value of the hardCoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WSTypedContent }{@code >}
     *     
     */
    public void setHardCoding(JAXBElement<WSTypedContent> value) {
        this.hardCoding = ((JAXBElement<WSTypedContent> ) value);
    }

}
