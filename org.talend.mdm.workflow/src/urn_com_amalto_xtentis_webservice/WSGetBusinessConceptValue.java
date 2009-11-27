
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Returns the value for a business concept based on its key
 * 			
 * 
 * <p>Java class for WSGetBusinessConceptValue complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSGetBusinessConceptValue">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsDataClusterPK" type="{urn-com-amalto-xtentis-webservice}WSDataClusterPK"/>
 *         &lt;element name="wsBusinessConceptPK" type="{urn-com-amalto-xtentis-webservice}WSBusinessConceptPK"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSGetBusinessConceptValue", propOrder = {
    "wsDataClusterPK",
    "wsBusinessConceptPK"
})
public class WSGetBusinessConceptValue {

    @XmlElement(required = true)
    protected WSDataClusterPK wsDataClusterPK;
    @XmlElement(required = true)
    protected WSBusinessConceptPK wsBusinessConceptPK;

    /**
     * Gets the value of the wsDataClusterPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSDataClusterPK }
     *     
     */
    public WSDataClusterPK getWsDataClusterPK() {
        return wsDataClusterPK;
    }

    /**
     * Sets the value of the wsDataClusterPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDataClusterPK }
     *     
     */
    public void setWsDataClusterPK(WSDataClusterPK value) {
        this.wsDataClusterPK = value;
    }

    /**
     * Gets the value of the wsBusinessConceptPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSBusinessConceptPK }
     *     
     */
    public WSBusinessConceptPK getWsBusinessConceptPK() {
        return wsBusinessConceptPK;
    }

    /**
     * Sets the value of the wsBusinessConceptPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSBusinessConceptPK }
     *     
     */
    public void setWsBusinessConceptPK(WSBusinessConceptPK value) {
        this.wsBusinessConceptPK = value;
    }

}
