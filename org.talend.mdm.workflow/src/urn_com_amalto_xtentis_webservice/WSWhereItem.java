
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				An And or Or or WhereCondition
 * 			
 * 
 * <p>Java class for WSWhereItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSWhereItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="whereCondition" type="{urn-com-amalto-xtentis-webservice}WSWhereCondition" minOccurs="0"/>
 *         &lt;element name="whereAnd" type="{urn-com-amalto-xtentis-webservice}WSWhereAnd" minOccurs="0"/>
 *         &lt;element name="whereOr" type="{urn-com-amalto-xtentis-webservice}WSWhereOr" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSWhereItem", propOrder = {
    "whereCondition",
    "whereAnd",
    "whereOr"
})
public class WSWhereItem {

    protected WSWhereCondition whereCondition;
    protected WSWhereAnd whereAnd;
    protected WSWhereOr whereOr;

    /**
     * Gets the value of the whereCondition property.
     * 
     * @return
     *     possible object is
     *     {@link WSWhereCondition }
     *     
     */
    public WSWhereCondition getWhereCondition() {
        return whereCondition;
    }

    /**
     * Sets the value of the whereCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSWhereCondition }
     *     
     */
    public void setWhereCondition(WSWhereCondition value) {
        this.whereCondition = value;
    }

    /**
     * Gets the value of the whereAnd property.
     * 
     * @return
     *     possible object is
     *     {@link WSWhereAnd }
     *     
     */
    public WSWhereAnd getWhereAnd() {
        return whereAnd;
    }

    /**
     * Sets the value of the whereAnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSWhereAnd }
     *     
     */
    public void setWhereAnd(WSWhereAnd value) {
        this.whereAnd = value;
    }

    /**
     * Gets the value of the whereOr property.
     * 
     * @return
     *     possible object is
     *     {@link WSWhereOr }
     *     
     */
    public WSWhereOr getWhereOr() {
        return whereOr;
    }

    /**
     * Sets the value of the whereOr property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSWhereOr }
     *     
     */
    public void setWhereOr(WSWhereOr value) {
        this.whereOr = value;
    }

}
