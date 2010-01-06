
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutMenu complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutMenu">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsMenu" type="{urn-com-amalto-xtentis-webservice}WSMenu"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutMenu", propOrder = {
    "wsMenu"
})
public class WSPutMenu {

    @XmlElement(required = true)
    protected WSMenu wsMenu;

    /**
     * Gets the value of the wsMenu property.
     * 
     * @return
     *     possible object is
     *     {@link WSMenu }
     *     
     */
    public WSMenu getWsMenu() {
        return wsMenu;
    }

    /**
     * Sets the value of the wsMenu property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSMenu }
     *     
     */
    public void setWsMenu(WSMenu value) {
        this.wsMenu = value;
    }

}
