
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Returns items based on the cluster pk and a search
 * 			
 * 
 * <p>Java class for WSGetItems complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSGetItems">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsDataClusterPK" type="{urn-com-amalto-xtentis-webservice}WSDataClusterPK"/>
 *         &lt;element name="conceptName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="whereItem" type="{urn-com-amalto-xtentis-webservice}WSWhereItem"/>
 *         &lt;element name="spellTreshold" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="skip" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="maxItems" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSGetItems", propOrder = {
    "wsDataClusterPK",
    "conceptName",
    "whereItem",
    "spellTreshold",
    "skip",
    "maxItems"
})
public class WSGetItems {

    @XmlElement(required = true)
    protected WSDataClusterPK wsDataClusterPK;
    @XmlElement(required = true)
    protected String conceptName;
    @XmlElement(required = true, nillable = true)
    protected WSWhereItem whereItem;
    protected int spellTreshold;
    protected int skip;
    protected int maxItems;

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
     * Gets the value of the conceptName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConceptName() {
        return conceptName;
    }

    /**
     * Sets the value of the conceptName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConceptName(String value) {
        this.conceptName = value;
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

}
