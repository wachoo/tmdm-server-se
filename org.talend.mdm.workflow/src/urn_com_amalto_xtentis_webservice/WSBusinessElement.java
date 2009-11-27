
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				A "sub-element" in a Complex Template 
 * 				xsd types can be used as business template
 * 			
 * 
 * <p>Java class for WSBusinessElement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSBusinessElement">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="minOccurs" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="maxOccurs" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="businessTemplate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="wsForeignKey" type="{urn-com-amalto-xtentis-webservice}WSKey" minOccurs="0"/>
 *         &lt;element name="wsLabel" type="{urn-com-amalto-xtentis-webservice}WSI18nString" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="wsDescription" type="{urn-com-amalto-xtentis-webservice}WSI18nString" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSBusinessElement", propOrder = {
    "name",
    "minOccurs",
    "maxOccurs",
    "businessTemplate",
    "wsForeignKey",
    "wsLabel",
    "wsDescription"
})
public class WSBusinessElement {

    @XmlElement(required = true)
    protected String name;
    protected int minOccurs;
    protected int maxOccurs;
    @XmlElement(required = true)
    protected String businessTemplate;
    protected WSKey wsForeignKey;
    protected List<WSI18NString> wsLabel;
    protected List<WSI18NString> wsDescription;

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
     * Gets the value of the minOccurs property.
     * 
     */
    public int getMinOccurs() {
        return minOccurs;
    }

    /**
     * Sets the value of the minOccurs property.
     * 
     */
    public void setMinOccurs(int value) {
        this.minOccurs = value;
    }

    /**
     * Gets the value of the maxOccurs property.
     * 
     */
    public int getMaxOccurs() {
        return maxOccurs;
    }

    /**
     * Sets the value of the maxOccurs property.
     * 
     */
    public void setMaxOccurs(int value) {
        this.maxOccurs = value;
    }

    /**
     * Gets the value of the businessTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessTemplate() {
        return businessTemplate;
    }

    /**
     * Sets the value of the businessTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessTemplate(String value) {
        this.businessTemplate = value;
    }

    /**
     * Gets the value of the wsForeignKey property.
     * 
     * @return
     *     possible object is
     *     {@link WSKey }
     *     
     */
    public WSKey getWsForeignKey() {
        return wsForeignKey;
    }

    /**
     * Sets the value of the wsForeignKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSKey }
     *     
     */
    public void setWsForeignKey(WSKey value) {
        this.wsForeignKey = value;
    }

    /**
     * Gets the value of the wsLabel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wsLabel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWsLabel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSI18NString }
     * 
     * 
     */
    public List<WSI18NString> getWsLabel() {
        if (wsLabel == null) {
            wsLabel = new ArrayList<WSI18NString>();
        }
        return this.wsLabel;
    }

    /**
     * Gets the value of the wsDescription property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wsDescription property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWsDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSI18NString }
     * 
     * 
     */
    public List<WSI18NString> getWsDescription() {
        if (wsDescription == null) {
            wsDescription = new ArrayList<WSI18NString>();
        }
        return this.wsDescription;
    }

}
