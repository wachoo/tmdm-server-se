
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSExecuteTransformerV2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSExecuteTransformerV2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsTransformerContext" type="{urn-com-amalto-xtentis-webservice}WSTransformerContext"/>
 *         &lt;element name="wsTypedContent" type="{urn-com-amalto-xtentis-webservice}WSTypedContent" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSExecuteTransformerV2", propOrder = {
    "wsTransformerContext",
    "wsTypedContent"
})
public class WSExecuteTransformerV2 {

    @XmlElement(required = true, nillable = true)
    protected WSTransformerContext wsTransformerContext;
    @XmlElementRef(name = "wsTypedContent", type = JAXBElement.class)
    protected JAXBElement<WSTypedContent> wsTypedContent;

    /**
     * Gets the value of the wsTransformerContext property.
     * 
     * @return
     *     possible object is
     *     {@link WSTransformerContext }
     *     
     */
    public WSTransformerContext getWsTransformerContext() {
        return wsTransformerContext;
    }

    /**
     * Sets the value of the wsTransformerContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSTransformerContext }
     *     
     */
    public void setWsTransformerContext(WSTransformerContext value) {
        this.wsTransformerContext = value;
    }

    /**
     * Gets the value of the wsTypedContent property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WSTypedContent }{@code >}
     *     
     */
    public JAXBElement<WSTypedContent> getWsTypedContent() {
        return wsTypedContent;
    }

    /**
     * Sets the value of the wsTypedContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WSTypedContent }{@code >}
     *     
     */
    public void setWsTypedContent(JAXBElement<WSTypedContent> value) {
        this.wsTypedContent = ((JAXBElement<WSTypedContent> ) value);
    }

}
