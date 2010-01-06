
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSWorkflowGetTaskList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSWorkflowGetTaskList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="processinstanceuuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSWorkflowGetTaskList", propOrder = {

})
public class WSWorkflowGetTaskList {

    @XmlElement(required = true)
    protected String processinstanceuuid;

    /**
     * Gets the value of the processinstanceuuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessinstanceuuid() {
        return processinstanceuuid;
    }

    /**
     * Sets the value of the processinstanceuuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessinstanceuuid(String value) {
        this.processinstanceuuid = value;
    }

}
