
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				The pipeline after running a transformer
 * 			
 * 
 * <p>Java class for WSPipeline complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPipeline">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="typedContentEntry" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="output" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="wsExtractedContent" type="{urn-com-amalto-xtentis-webservice}WSExtractedContent"/>
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
@XmlType(name = "WSPipeline", propOrder = {
    "typedContentEntry"
})
public class WSPipeline {

    @XmlElement(required = true, nillable = true)
    protected List<WSPipeline.TypedContentEntry> typedContentEntry;

    /**
     * Gets the value of the typedContentEntry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the typedContentEntry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTypedContentEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSPipeline.TypedContentEntry }
     * 
     * 
     */
    public List<WSPipeline.TypedContentEntry> getTypedContentEntry() {
        if (typedContentEntry == null) {
            typedContentEntry = new ArrayList<WSPipeline.TypedContentEntry>();
        }
        return this.typedContentEntry;
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
     *         &lt;element name="output" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="wsExtractedContent" type="{urn-com-amalto-xtentis-webservice}WSExtractedContent"/>
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
        "output",
        "wsExtractedContent"
    })
    public static class TypedContentEntry {

        @XmlElement(required = true, nillable = true)
        protected String output;
        @XmlElement(required = true, nillable = true)
        protected WSExtractedContent wsExtractedContent;

        /**
         * Gets the value of the output property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOutput() {
            return output;
        }

        /**
         * Sets the value of the output property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOutput(String value) {
            this.output = value;
        }

        /**
         * Gets the value of the wsExtractedContent property.
         * 
         * @return
         *     possible object is
         *     {@link WSExtractedContent }
         *     
         */
        public WSExtractedContent getWsExtractedContent() {
            return wsExtractedContent;
        }

        /**
         * Sets the value of the wsExtractedContent property.
         * 
         * @param value
         *     allowed object is
         *     {@link WSExtractedContent }
         *     
         */
        public void setWsExtractedContent(WSExtractedContent value) {
            this.wsExtractedContent = value;
        }

    }

}
