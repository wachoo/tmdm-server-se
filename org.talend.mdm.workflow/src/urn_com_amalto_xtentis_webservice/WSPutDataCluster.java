
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Creates a data-cluster. Characteristics (stemming, etc...) of a data cluster cannot be changed
 * 			
 * 
 * <p>Java class for WSPutDataCluster complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutDataCluster">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsDataCluster" type="{urn-com-amalto-xtentis-webservice}WSDataCluster"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutDataCluster", propOrder = {
    "wsDataCluster"
})
public class WSPutDataCluster {

    @XmlElement(required = true)
    protected WSDataCluster wsDataCluster;

    /**
     * Gets the value of the wsDataCluster property.
     * 
     * @return
     *     possible object is
     *     {@link WSDataCluster }
     *     
     */
    public WSDataCluster getWsDataCluster() {
        return wsDataCluster;
    }

    /**
     * Sets the value of the wsDataCluster property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDataCluster }
     *     
     */
    public void setWsDataCluster(WSDataCluster value) {
        this.wsDataCluster = value;
    }

}
