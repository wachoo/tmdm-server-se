
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Performs a search and returns the values for the viewable paths
 * 				If the pivot is null, the first element of the viewable paths is used
 * 			
 * 
 * <p>Java class for WSXPathsSearch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSXPathsSearch">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsDataClusterPK" type="{urn-com-amalto-xtentis-webservice}WSDataClusterPK"/>
 *         &lt;element name="pivotPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="viewablePaths" type="{urn-com-amalto-xtentis-webservice}WSStringArray"/>
 *         &lt;element name="whereItem" type="{urn-com-amalto-xtentis-webservice}WSWhereItem"/>
 *         &lt;element name="spellTreshold" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="skip" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="maxItems" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="orderBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="direction" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSXPathsSearch", propOrder = {
    "wsDataClusterPK",
    "pivotPath",
    "viewablePaths",
    "whereItem",
    "spellTreshold",
    "skip",
    "maxItems",
    "orderBy",
    "direction"
})
public class WSXPathsSearch {

    @XmlElement(required = true)
    protected WSDataClusterPK wsDataClusterPK;
    @XmlElement(required = true, nillable = true)
    protected String pivotPath;
    @XmlElement(required = true)
    protected WSStringArray viewablePaths;
    @XmlElement(required = true, nillable = true)
    protected WSWhereItem whereItem;
    protected int spellTreshold;
    protected int skip;
    protected int maxItems;
    @XmlElementRef(name = "orderBy", type = JAXBElement.class)
    protected JAXBElement<String> orderBy;
    @XmlElementRef(name = "direction", type = JAXBElement.class)
    protected JAXBElement<String> direction;

    /**
     * Gets the value of the wsDataClusterPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSDataClusterPK }
     *     
     */
    public WSDataClusterPK getWsDataClusterPK() {
        return wsDataClusterPK;
    }

    /**
     * Sets the value of the wsDataClusterPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDataClusterPK }
     *     
     */
    public void setWsDataClusterPK(WSDataClusterPK value) {
        this.wsDataClusterPK = value;
    }

    /**
     * Gets the value of the pivotPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPivotPath() {
        return pivotPath;
    }

    /**
     * Sets the value of the pivotPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPivotPath(String value) {
        this.pivotPath = value;
    }

    /**
     * Gets the value of the viewablePaths property.
     * 
     * @return
     *     possible object is
     *     {@link WSStringArray }
     *     
     */
    public WSStringArray getViewablePaths() {
        return viewablePaths;
    }

    /**
     * Sets the value of the viewablePaths property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSStringArray }
     *     
     */
    public void setViewablePaths(WSStringArray value) {
        this.viewablePaths = value;
    }

    /**
     * Gets the value of the whereItem property.
     * 
     * @return
     *     possible object is
     *     {@link WSWhereItem }
     *     
     */
    public WSWhereItem getWhereItem() {
        return whereItem;
    }

    /**
     * Sets the value of the whereItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSWhereItem }
     *     
     */
    public void setWhereItem(WSWhereItem value) {
        this.whereItem = value;
    }

    /**
     * Gets the value of the spellTreshold property.
     * 
     */
    public int getSpellTreshold() {
        return spellTreshold;
    }

    /**
     * Sets the value of the spellTreshold property.
     * 
     */
    public void setSpellTreshold(int value) {
        this.spellTreshold = value;
    }

    /**
     * Gets the value of the skip property.
     * 
     */
    public int getSkip() {
        return skip;
    }

    /**
     * Sets the value of the skip property.
     * 
     */
    public void setSkip(int value) {
        this.skip = value;
    }

    /**
     * Gets the value of the maxItems property.
     * 
     */
    public int getMaxItems() {
        return maxItems;
    }

    /**
     * Sets the value of the maxItems property.
     * 
     */
    public void setMaxItems(int value) {
        this.maxItems = value;
    }

    /**
     * Gets the value of the orderBy property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOrderBy() {
        return orderBy;
    }

    /**
     * Sets the value of the orderBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOrderBy(JAXBElement<String> value) {
        this.orderBy = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the direction property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDirection() {
        return direction;
    }

    /**
     * Sets the value of the direction property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDirection(JAXBElement<String> value) {
        this.direction = ((JAXBElement<String> ) value);
    }

}
