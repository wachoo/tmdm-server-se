
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutBusinessConcept complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutBusinessConcept">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsDataModelPK" type="{urn-com-amalto-xtentis-webservice}WSDataModelPK"/>
 *         &lt;element name="businessConcept" type="{urn-com-amalto-xtentis-webservice}WSBusinessConcept"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutBusinessConcept", propOrder = {
    "wsDataModelPK",
    "businessConcept"
})
public class WSPutBusinessConcept {

    @XmlElement(required = true)
    protected WSDataModelPK wsDataModelPK;
    @XmlElement(required = true)
    protected WSBusinessConcept businessConcept;

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
     * Gets the value of the businessConcept property.
     * 
     * @return
     *     possible object is
     *     {@link WSBusinessConcept }
     *     
     */
    public WSBusinessConcept getBusinessConcept() {
        return businessConcept;
    }

    /**
     * Sets the value of the businessConcept property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSBusinessConcept }
     *     
     */
    public void setBusinessConcept(WSBusinessConcept value) {
        this.businessConcept = value;
    }

}
