
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutView complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutView">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsView" type="{urn-com-amalto-xtentis-webservice}WSView"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutView", propOrder = {
    "wsView"
})
public class WSPutView {

    @XmlElement(required = true)
    protected WSView wsView;

    /**
     * Gets the value of the wsView property.
     * 
     * @return
     *     possible object is
     *     {@link WSView }
     *     
     */
    public WSView getWsView() {
        return wsView;
    }

    /**
     * Sets the value of the wsView property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSView }
     *     
     */
    public void setWsView(WSView value) {
        this.wsView = value;
    }

}
