
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Counts the number of occurrences of the count path matching the whereItem
 * 			
 * 
 * <p>Java class for WSCount complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSCount">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsDataClusterPK" type="{urn-com-amalto-xtentis-webservice}WSDataClusterPK"/>
 *         &lt;element name="countPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="whereItem" type="{urn-com-amalto-xtentis-webservice}WSWhereItem"/>
 *         &lt;element name="spellTreshold" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSCount", propOrder = {
    "wsDataClusterPK",
    "countPath",
    "whereItem",
    "spellTreshold"
})
public class WSCount {

    @XmlElement(required = true)
    protected WSDataClusterPK wsDataClusterPK;
    @XmlElement(required = true, nillable = true)
    protected String countPath;
    @XmlElement(required = true, nillable = true)
    protected WSWhereItem whereItem;
    protected int spellTreshold;

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
     * Gets the value of the countPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountPath() {
        return countPath;
    }

    /**
     * Sets the value of the countPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountPath(String value) {
        this.countPath = value;
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

}
