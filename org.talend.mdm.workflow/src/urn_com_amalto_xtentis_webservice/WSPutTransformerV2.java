
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutTransformerV2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutTransformerV2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsTransformerV2" type="{urn-com-amalto-xtentis-webservice}WSTransformerV2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutTransformerV2", propOrder = {
    "wsTransformerV2"
})
public class WSPutTransformerV2 {

    @XmlElement(required = true)
    protected WSTransformerV2 wsTransformerV2;

    /**
     * Gets the value of the wsTransformerV2 property.
     * 
     * @return
     *     possible object is
     *     {@link WSTransformerV2 }
     *     
     */
    public WSTransformerV2 getWsTransformerV2() {
        return wsTransformerV2;
    }

    /**
     * Sets the value of the wsTransformerV2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSTransformerV2 }
     *     
     */
    public void setWsTransformerV2(WSTransformerV2 value) {
        this.wsTransformerV2 = value;
    }

}
