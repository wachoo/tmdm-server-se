
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSDeleteBusinessConcept complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSDeleteBusinessConcept">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsDataModelPK" type="{urn-com-amalto-xtentis-webservice}WSDataModelPK"/>
 *         &lt;element name="businessConceptName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSDeleteBusinessConcept", propOrder = {
    "wsDataModelPK",
    "businessConceptName"
})
public class WSDeleteBusinessConcept {

    @XmlElement(required = true)
    protected WSDataModelPK wsDataModelPK;
    @XmlElement(required = true)
    protected String businessConceptName;

    /**
     * Gets the value of the wsDataModelPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSDataModelPK }
     *     
     */
    public WSDataModelPK getWsDataModelPK() {
        return wsDataModelPK;
    }

    /**
     * Sets the value of the wsDataModelPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDataModelPK }
     *     
     */
    public void setWsDataModelPK(WSDataModelPK value) {
        this.wsDataModelPK = value;
    }

    /**
     * Gets the value of the businessConceptName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessConceptName() {
        return businessConceptName;
    }

    /**
     * Sets the value of the businessConceptName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessConceptName(String value) {
        this.businessConceptName = value;
    }

}
