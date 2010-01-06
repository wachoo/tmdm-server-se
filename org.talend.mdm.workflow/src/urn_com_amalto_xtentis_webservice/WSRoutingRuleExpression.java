
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSRoutingRuleExpression complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSRoutingRuleExpression">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="xpath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wsOperator" type="{urn-com-amalto-xtentis-webservice}WSRoutingRuleOperator" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSRoutingRuleExpression", propOrder = {
    "name",
    "xpath",
    "wsOperator",
    "value"
})
public class WSRoutingRuleExpression {

    @XmlElementRef(name = "name", type = JAXBElement.class)
    protected JAXBElement<String> name;
    @XmlElementRef(name = "xpath", type = JAXBElement.class)
    protected JAXBElement<String> xpath;
    @XmlElementRef(name = "wsOperator", type = JAXBElement.class)
    protected JAXBElement<WSRoutingRuleOperator> wsOperator;
    @XmlElementRef(name = "value", type = JAXBElement.class)
    protected JAXBElement<String> value;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setName(JAXBElement<String> value) {
        this.name = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the xpath property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getXpath() {
        return xpath;
    }

    /**
     * Sets the value of the xpath property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setXpath(JAXBElement<String> value) {
        this.xpath = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the wsOperator property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WSRoutingRuleOperator }{@code >}
     *     
     */
    public JAXBElement<WSRoutingRuleOperator> getWsOperator() {
        return wsOperator;
    }

    /**
     * Sets the value of the wsOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WSRoutingRuleOperator }{@code >}
     *     
     */
    public void setWsOperator(JAXBElement<WSRoutingRuleOperator> value) {
        this.wsOperator = ((JAXBElement<WSRoutingRuleOperator> ) value);
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setValue(JAXBElement<String> value) {
        this.value = ((JAXBElement<String> ) value);
    }

}
