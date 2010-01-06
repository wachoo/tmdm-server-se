
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSRoutingRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSRoutingRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="synchronous" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="concept" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="serviceJNDI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parameters" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="wsRoutingRuleExpressions" type="{urn-com-amalto-xtentis-webservice}WSRoutingRuleExpression" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="condition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deactive" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSRoutingRule", propOrder = {
    "name",
    "description",
    "synchronous",
    "concept",
    "serviceJNDI",
    "parameters",
    "wsRoutingRuleExpressions",
    "condition",
    "deactive"
})
public class WSRoutingRule {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true, nillable = true)
    protected String description;
    protected boolean synchronous;
    @XmlElement(required = true, nillable = true)
    protected String concept;
    @XmlElement(required = true)
    protected String serviceJNDI;
    @XmlElement(required = true, nillable = true)
    protected String parameters;
    @XmlElement(nillable = true)
    protected List<WSRoutingRuleExpression> wsRoutingRuleExpressions;
    @XmlElementRef(name = "condition", type = JAXBElement.class)
    protected JAXBElement<String> condition;
    @XmlElementRef(name = "deactive", type = JAXBElement.class)
    protected JAXBElement<Boolean> deactive;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the synchronous property.
     * 
     */
    public boolean isSynchronous() {
        return synchronous;
    }

    /**
     * Sets the value of the synchronous property.
     * 
     */
    public void setSynchronous(boolean value) {
        this.synchronous = value;
    }

    /**
     * Gets the value of the concept property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConcept() {
        return concept;
    }

    /**
     * Sets the value of the concept property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConcept(String value) {
        this.concept = value;
    }

    /**
     * Gets the value of the serviceJNDI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceJNDI() {
        return serviceJNDI;
    }

    /**
     * Sets the value of the serviceJNDI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceJNDI(String value) {
        this.serviceJNDI = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParameters(String value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the wsRoutingRuleExpressions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wsRoutingRuleExpressions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWsRoutingRuleExpressions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSRoutingRuleExpression }
     * 
     * 
     */
    public List<WSRoutingRuleExpression> getWsRoutingRuleExpressions() {
        if (wsRoutingRuleExpressions == null) {
            wsRoutingRuleExpressions = new ArrayList<WSRoutingRuleExpression>();
        }
        return this.wsRoutingRuleExpressions;
    }

    /**
     * Gets the value of the condition property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCondition() {
        return condition;
    }

    /**
     * Sets the value of the condition property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCondition(JAXBElement<String> value) {
        this.condition = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the deactive property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getDeactive() {
        return deactive;
    }

    /**
     * Sets the value of the deactive property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setDeactive(JAXBElement<Boolean> value) {
        this.deactive = ((JAXBElement<Boolean> ) value);
    }

}
