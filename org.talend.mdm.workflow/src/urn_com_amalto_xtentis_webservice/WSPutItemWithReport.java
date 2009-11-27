
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Puts an item in the xml storage with update report
 * 			
 * 
 * <p>Java class for WSPutItemWithReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutItemWithReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsPutItem" type="{urn-com-amalto-xtentis-webservice}WSPutItem"/>
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="invokeBeforeSaving" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutItemWithReport", propOrder = {
    "wsPutItem",
    "source",
    "invokeBeforeSaving"
})
public class WSPutItemWithReport {

    @XmlElement(required = true)
    protected WSPutItem wsPutItem;
    @XmlElement(required = true)
    protected String source;
    @XmlElement(defaultValue = "true")
    protected Boolean invokeBeforeSaving;

    /**
     * Gets the value of the wsPutItem property.
     * 
     * @return
     *     possible object is
     *     {@link WSPutItem }
     *     
     */
    public WSPutItem getWsPutItem() {
        return wsPutItem;
    }

    /**
     * Sets the value of the wsPutItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSPutItem }
     *     
     */
    public void setWsPutItem(WSPutItem value) {
        this.wsPutItem = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the invokeBeforeSaving property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInvokeBeforeSaving() {
        return invokeBeforeSaving;
    }

    /**
     * Sets the value of the invokeBeforeSaving property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInvokeBeforeSaving(Boolean value) {
        this.invokeBeforeSaving = value;
    }

}
