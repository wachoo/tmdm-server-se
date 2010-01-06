
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Use this object to write where conditions.
 * 			
 * 
 * <p>Java class for WSWhereCondition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSWhereCondition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="leftPath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="operator" type="{urn-com-amalto-xtentis-webservice}WSWhereOperator" minOccurs="0"/>
 *         &lt;element name="rightValueOrPath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stringPredicate" type="{urn-com-amalto-xtentis-webservice}WSStringPredicate" minOccurs="0"/>
 *         &lt;element name="spellCheck" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSWhereCondition", propOrder = {
    "leftPath",
    "operator",
    "rightValueOrPath",
    "stringPredicate",
    "spellCheck"
})
public class WSWhereCondition {

    @XmlElementRef(name = "leftPath", type = JAXBElement.class)
    protected JAXBElement<String> leftPath;
    @XmlElementRef(name = "operator", type = JAXBElement.class)
    protected JAXBElement<WSWhereOperator> operator;
    @XmlElementRef(name = "rightValueOrPath", type = JAXBElement.class)
    protected JAXBElement<String> rightValueOrPath;
    @XmlElementRef(name = "stringPredicate", type = JAXBElement.class)
    protected JAXBElement<WSStringPredicate> stringPredicate;
    protected boolean spellCheck;

    /**
     * Gets the value of the leftPath property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLeftPath() {
        return leftPath;
    }

    /**
     * Sets the value of the leftPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLeftPath(JAXBElement<String> value) {
        this.leftPath = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the operator property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WSWhereOperator }{@code >}
     *     
     */
    public JAXBElement<WSWhereOperator> getOperator() {
        return operator;
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WSWhereOperator }{@code >}
     *     
     */
    public void setOperator(JAXBElement<WSWhereOperator> value) {
        this.operator = ((JAXBElement<WSWhereOperator> ) value);
    }

    /**
     * Gets the value of the rightValueOrPath property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRightValueOrPath() {
        return rightValueOrPath;
    }

    /**
     * Sets the value of the rightValueOrPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRightValueOrPath(JAXBElement<String> value) {
        this.rightValueOrPath = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the stringPredicate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WSStringPredicate }{@code >}
     *     
     */
    public JAXBElement<WSStringPredicate> getStringPredicate() {
        return stringPredicate;
    }

    /**
     * Sets the value of the stringPredicate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WSStringPredicate }{@code >}
     *     
     */
    public void setStringPredicate(JAXBElement<WSStringPredicate> value) {
        this.stringPredicate = ((JAXBElement<WSStringPredicate> ) value);
    }

    /**
     * Gets the value of the spellCheck property.
     * 
     */
    public boolean isSpellCheck() {
        return spellCheck;
    }

    /**
     * Sets the value of the spellCheck property.
     * 
     */
    public void setSpellCheck(boolean value) {
        this.spellCheck = value;
    }

}
