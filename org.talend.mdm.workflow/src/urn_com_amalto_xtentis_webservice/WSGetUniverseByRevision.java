
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSGetUniverseByRevision complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSGetUniverseByRevision">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="namepattern" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="revision" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type" type="{urn-com-amalto-xtentis-webservice}WSGetUniverseByRevisionType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSGetUniverseByRevision", propOrder = {
    "namepattern",
    "revision",
    "type"
})
public class WSGetUniverseByRevision {

    @XmlElement(required = true)
    protected String namepattern;
    @XmlElement(required = true)
    protected String revision;
    @XmlElement(required = true)
    protected WSGetUniverseByRevisionType type;

    /**
     * Gets the value of the namepattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamepattern() {
        return namepattern;
    }

    /**
     * Sets the value of the namepattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamepattern(String value) {
        this.namepattern = value;
    }

    /**
     * Gets the value of the revision property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Sets the value of the revision property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRevision(String value) {
        this.revision = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link WSGetUniverseByRevisionType }
     *     
     */
    public WSGetUniverseByRevisionType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSGetUniverseByRevisionType }
     *     
     */
    public void setType(WSGetUniverseByRevisionType value) {
        this.type = value;
    }

}
