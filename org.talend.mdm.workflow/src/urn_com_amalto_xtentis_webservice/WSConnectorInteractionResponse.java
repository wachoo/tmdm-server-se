
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSConnectorInteractionResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSConnectorInteractionResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="code" type="{urn-com-amalto-xtentis-webservice}WSConnectorResponseCode"/>
 *         &lt;element name="parameters" type="{urn-com-amalto-xtentis-webservice}WSBase64KeyValue" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSConnectorInteractionResponse", propOrder = {
    "code",
    "parameters"
})
public class WSConnectorInteractionResponse {

    @XmlElement(required = true, nillable = true)
    protected WSConnectorResponseCode code;
    @XmlElement(nillable = true)
    protected List<WSBase64KeyValue> parameters;

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link WSConnectorResponseCode }
     *     
     */
    public WSConnectorResponseCode getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSConnectorResponseCode }
     *     
     */
    public void setCode(WSConnectorResponseCode value) {
        this.code = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSBase64KeyValue }
     * 
     * 
     */
    public List<WSBase64KeyValue> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<WSBase64KeyValue>();
        }
        return this.parameters;
    }

}
