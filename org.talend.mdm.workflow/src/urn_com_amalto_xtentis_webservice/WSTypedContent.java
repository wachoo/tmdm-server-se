
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSTypedContent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSTypedContent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wsBytes" type="{urn-com-amalto-xtentis-webservice}WSByteArray" minOccurs="0"/>
 *         &lt;element name="contentType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSTypedContent", propOrder = {
    "url",
    "wsBytes",
    "contentType"
})
public class WSTypedContent {

    @XmlElementRef(name = "url", type = JAXBElement.class)
    protected JAXBElement<String> url;
    @XmlElementRef(name = "wsBytes", type = JAXBElement.class)
    protected JAXBElement<WSByteArray> wsBytes;
    @XmlElement(required = true)
    protected String contentType;

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUrl(JAXBElement<String> value) {
        this.url = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the wsBytes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WSByteArray }{@code >}
     *     
     */
    public JAXBElement<WSByteArray> getWsBytes() {
        return wsBytes;
    }

    /**
     * Sets the value of the wsBytes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WSByteArray }{@code >}
     *     
     */
    public void setWsBytes(JAXBElement<WSByteArray> value) {
        this.wsBytes = ((JAXBElement<WSByteArray> ) value);
    }

    /**
     * Gets the value of the contentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the value of the contentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentType(String value) {
        this.contentType = value;
    }

}
