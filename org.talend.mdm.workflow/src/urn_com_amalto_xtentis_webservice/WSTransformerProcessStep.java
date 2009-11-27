
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSTransformerProcessStep complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSTransformerProcessStep">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PluginJNDI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parameters" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="inputMappings" type="{urn-com-amalto-xtentis-webservice}WSTransformerVariablesMapping" maxOccurs="unbounded"/>
 *         &lt;element name="outputMappings" type="{urn-com-amalto-xtentis-webservice}WSTransformerVariablesMapping" maxOccurs="unbounded"/>
 *         &lt;element name="disabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSTransformerProcessStep", propOrder = {
    "pluginJNDI",
    "description",
    "parameters",
    "inputMappings",
    "outputMappings",
    "disabled"
})
public class WSTransformerProcessStep {

    @XmlElement(name = "PluginJNDI", required = true)
    protected String pluginJNDI;
    @XmlElement(required = true, nillable = true)
    protected String description;
    @XmlElement(required = true, nillable = true)
    protected String parameters;
    @XmlElement(required = true, nillable = true)
    protected List<WSTransformerVariablesMapping> inputMappings;
    @XmlElement(required = true, nillable = true)
    protected List<WSTransformerVariablesMapping> outputMappings;
    protected Boolean disabled;

    /**
     * Gets the value of the pluginJNDI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPluginJNDI() {
        return pluginJNDI;
    }

    /**
     * Sets the value of the pluginJNDI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPluginJNDI(String value) {
        this.pluginJNDI = value;
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
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParameters(String value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the inputMappings property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inputMappings property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInputMappings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSTransformerVariablesMapping }
     * 
     * 
     */
    public List<WSTransformerVariablesMapping> getInputMappings() {
        if (inputMappings == null) {
            inputMappings = new ArrayList<WSTransformerVariablesMapping>();
        }
        return this.inputMappings;
    }

    /**
     * Gets the value of the outputMappings property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outputMappings property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutputMappings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSTransformerVariablesMapping }
     * 
     * 
     */
    public List<WSTransformerVariablesMapping> getOutputMappings() {
        if (outputMappings == null) {
            outputMappings = new ArrayList<WSTransformerVariablesMapping>();
        }
        return this.outputMappings;
    }

    /**
     * Gets the value of the disabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets the value of the disabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisabled(Boolean value) {
        this.disabled = value;
    }

}
