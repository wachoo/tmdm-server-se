
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSGetConceptsInDataClusterWithRevisions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSGetConceptsInDataClusterWithRevisions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dataClusterPOJOPK" type="{urn-com-amalto-xtentis-webservice}WSDataClusterPK"/>
 *         &lt;element name="universePK" type="{urn-com-amalto-xtentis-webservice}WSUniversePK"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSGetConceptsInDataClusterWithRevisions", propOrder = {
    "dataClusterPOJOPK",
    "universePK"
})
public class WSGetConceptsInDataClusterWithRevisions {

    @XmlElement(required = true)
    protected WSDataClusterPK dataClusterPOJOPK;
    @XmlElement(required = true, nillable = true)
    protected WSUniversePK universePK;

    /**
     * Gets the value of the dataClusterPOJOPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSDataClusterPK }
     *     
     */
    public WSDataClusterPK getDataClusterPOJOPK() {
        return dataClusterPOJOPK;
    }

    /**
     * Sets the value of the dataClusterPOJOPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDataClusterPK }
     *     
     */
    public void setDataClusterPOJOPK(WSDataClusterPK value) {
        this.dataClusterPOJOPK = value;
    }

    /**
     * Gets the value of the universePK property.
     * 
     * @return
     *     possible object is
     *     {@link WSUniversePK }
     *     
     */
    public WSUniversePK getUniversePK() {
        return universePK;
    }

    /**
     * Sets the value of the universePK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSUniversePK }
     *     
     */
    public void setUniversePK(WSUniversePK value) {
        this.universePK = value;
    }

}
