
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutTransformer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutTransformer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsTransformer" type="{urn-com-amalto-xtentis-webservice}WSTransformer"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutTransformer", propOrder = {
    "wsTransformer"
})
public class WSPutTransformer {

    @XmlElement(required = true)
    protected WSTransformer wsTransformer;

    /**
     * Gets the value of the wsTransformer property.
     * 
     * @return
     *     possible object is
     *     {@link WSTransformer }
     *     
     */
    public WSTransformer getWsTransformer() {
        return wsTransformer;
    }

    /**
     * Sets the value of the wsTransformer property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSTransformer }
     *     
     */
    public void setWsTransformer(WSTransformer value) {
        this.wsTransformer = value;
    }

}
