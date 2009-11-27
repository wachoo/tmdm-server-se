
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Versions of an universe
 * 			
 * 
 * <p>Java class for WSVersioningUniverseVersions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSVersioningUniverseVersions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tagStructure" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="tagName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="lastDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="lastAuthor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="lastComment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="clusters" type="{urn-com-amalto-xtentis-webservice}WSStringArray"/>
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
@XmlType(name = "WSVersioningUniverseVersions", propOrder = {
    "tagStructure"
})
public class WSVersioningUniverseVersions {

    @XmlElement(required = true)
    protected List<WSVersioningUniverseVersions.TagStructure> tagStructure;

    /**
     * Gets the value of the tagStructure property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tagStructure property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTagStructure().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSVersioningUniverseVersions.TagStructure }
     * 
     * 
     */
    public List<WSVersioningUniverseVersions.TagStructure> getTagStructure() {
        if (tagStructure == null) {
            tagStructure = new ArrayList<WSVersioningUniverseVersions.TagStructure>();
        }
        return this.tagStructure;
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
     *         &lt;element name="tagName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="lastDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="lastAuthor" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="lastComment" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="clusters" type="{urn-com-amalto-xtentis-webservice}WSStringArray"/>
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
        "tagName",
        "lastDate",
        "lastAuthor",
        "lastComment",
        "clusters"
    })
    public static class TagStructure {

        @XmlElement(required = true)
        protected String tagName;
        @XmlElement(required = true, nillable = true)
        protected String lastDate;
        @XmlElement(required = true, nillable = true)
        protected String lastAuthor;
        @XmlElement(required = true, nillable = true)
        protected String lastComment;
        @XmlElement(required = true, nillable = true)
        protected WSStringArray clusters;

        /**
         * Gets the value of the tagName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTagName() {
            return tagName;
        }

        /**
         * Sets the value of the tagName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTagName(String value) {
            this.tagName = value;
        }

        /**
         * Gets the value of the lastDate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLastDate() {
            return lastDate;
        }

        /**
         * Sets the value of the lastDate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLastDate(String value) {
            this.lastDate = value;
        }

        /**
         * Gets the value of the lastAuthor property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLastAuthor() {
            return lastAuthor;
        }

        /**
         * Sets the value of the lastAuthor property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLastAuthor(String value) {
            this.lastAuthor = value;
        }

        /**
         * Gets the value of the lastComment property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLastComment() {
            return lastComment;
        }

        /**
         * Sets the value of the lastComment property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLastComment(String value) {
            this.lastComment = value;
        }

        /**
         * Gets the value of the clusters property.
         * 
         * @return
         *     possible object is
         *     {@link WSStringArray }
         *     
         */
        public WSStringArray getClusters() {
            return clusters;
        }

        /**
         * Sets the value of the clusters property.
         * 
         * @param value
         *     allowed object is
         *     {@link WSStringArray }
         *     
         */
        public void setClusters(WSStringArray value) {
            this.clusters = value;
        }

    }

}
