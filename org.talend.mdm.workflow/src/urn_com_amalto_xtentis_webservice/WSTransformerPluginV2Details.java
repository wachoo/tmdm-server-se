
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSTransformerPluginV2Details complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSTransformerPluginV2Details">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inputVariableDescriptors" type="{urn-com-amalto-xtentis-webservice}WSTransformerPluginV2VariableDescriptor" maxOccurs="unbounded"/>
 *         &lt;element name="outputVariableDescriptors" type="{urn-com-amalto-xtentis-webservice}WSTransformerPluginV2VariableDescriptor" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="documentation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parametersSchema" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSTransformerPluginV2Details", propOrder = {
    "inputVariableDescriptors",
    "outputVariableDescriptors",
    "description",
    "documentation",
    "parametersSchema"
})
public class WSTransformerPluginV2Details {

    @XmlElement(required = true)
    protected List<WSTransformerPluginV2VariableDescriptor> inputVariableDescriptors;
    protected List<WSTransformerPluginV2VariableDescriptor> outputVariableDescriptors;
    @XmlElement(required = true, nillable = true)
    protected String description;
    @XmlElement(required = true, nillable = true)
    protected String documentation;
    @XmlElement(required = true, nillable = true)
    protected String parametersSchema;

    /**
     * Gets the value of the inputVariableDescriptors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inputVariableDescriptors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInputVariableDescriptors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSTransformerPluginV2VariableDescriptor }
     * 
     * 
     */
    public List<WSTransformerPluginV2VariableDescriptor> getInputVariableDescriptors() {
        if (inputVariableDescriptors == null) {
            inputVariableDescriptors = new ArrayList<WSTransformerPluginV2VariableDescriptor>();
        }
        return this.inputVariableDescriptors;
    }

    /**
     * Gets the value of the outputVariableDescriptors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outputVariableDescriptors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutputVariableDescriptors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSTransformerPluginV2VariableDescriptor }
     * 
     * 
     */
    public List<WSTransformerPluginV2VariableDescriptor> getOutputVariableDescriptors() {
        if (outputVariableDescriptors == null) {
            outputVariableDescriptors = new ArrayList<WSTransformerPluginV2VariableDescriptor>();
        }
        return this.outputVariableDescriptors;
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
     * Gets the value of the documentation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Sets the value of the documentation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentation(String value) {
        this.documentation = value;
    }

    /**
     * Gets the value of the parametersSchema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParametersSchema() {
        return parametersSchema;
    }

    /**
     * Sets the value of the parametersSchema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParametersSchema(String value) {
        this.parametersSchema = value;
    }

}
