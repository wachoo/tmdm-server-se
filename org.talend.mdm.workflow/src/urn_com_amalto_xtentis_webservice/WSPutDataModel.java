
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Creates - updates a data model
 * 			
 * 
 * <p>Java class for WSPutDataModel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutDataModel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsDataModel" type="{urn-com-amalto-xtentis-webservice}WSDataModel"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutDataModel", propOrder = {
    "wsDataModel"
})
public class WSPutDataModel {

    @XmlElement(required = true)
    protected WSDataModel wsDataModel;

    /**
     * Gets the value of the wsDataModel property.
     * 
     * @return
     *     possible object is
     *     {@link WSDataModel }
     *     
     */
    public WSDataModel getWsDataModel() {
        return wsDataModel;
    }

    /**
     * Sets the value of the wsDataModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDataModel }
     *     
     */
    public void setWsDataModel(WSDataModel value) {
        this.wsDataModel = value;
    }

}
