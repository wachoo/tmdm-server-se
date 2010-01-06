
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				A process definition UUID
 * 			
 * 
 * <p>Java class for WSWorkflowProcessDefinitionUUID complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSWorkflowProcessDefinitionUUID">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="processName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="processVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSWorkflowProcessDefinitionUUID", propOrder = {

})
public class WSWorkflowProcessDefinitionUUID {

    @XmlElement(required = true)
    protected String processName;
    @XmlElement(required = true)
    protected String processVersion;

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessName() {
        return processName;
    }

    /**
     * Sets the value of the processName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessName(String value) {
        this.processName = value;
    }

    /**
     * Gets the value of the processVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessVersion() {
        return processVersion;
    }

    /**
     * Sets the value of the processVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessVersion(String value) {
        this.processVersion = value;
    }

}
