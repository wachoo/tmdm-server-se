
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSConceptRevisionMap complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSConceptRevisionMap">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mapEntry" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="concept" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="revision" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSConceptRevisionMap", propOrder = {
    "mapEntry"
})
public class WSConceptRevisionMap {

    protected List<WSConceptRevisionMap.MapEntry> mapEntry;

    /**
     * Gets the value of the mapEntry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mapEntry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMapEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSConceptRevisionMap.MapEntry }
     * 
     * 
     */
    public List<WSConceptRevisionMap.MapEntry> getMapEntry() {
        if (mapEntry == null) {
            mapEntry = new ArrayList<WSConceptRevisionMap.MapEntry>();
        }
        return this.mapEntry;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="concept" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="revision" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "concept",
        "revision"
    })
    public static class MapEntry {

        @XmlElement(required = true)
        protected String concept;
        @XmlElement(required = true)
        protected String revision;

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

    }

}
