
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSTransformerContext complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSTransformerContext">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsTransformerPK" type="{urn-com-amalto-xtentis-webservice}WSTransformerV2PK"/>
 *         &lt;element name="pipeline">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="pipelineItem" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="variable" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="wsTypedContent" type="{urn-com-amalto-xtentis-webservice}WSTypedContent"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="projectedItemPKs" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="wsItemPOJOPK" type="{urn-com-amalto-xtentis-webservice}WSItemPK" maxOccurs="unbounded"/>
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
@XmlType(name = "WSTransformerContext", propOrder = {
    "wsTransformerPK",
    "pipeline",
    "projectedItemPKs"
})
public class WSTransformerContext {

    @XmlElement(required = true)
    protected WSTransformerV2PK wsTransformerPK;
    @XmlElement(required = true)
    protected WSTransformerContext.Pipeline pipeline;
    protected WSTransformerContext.ProjectedItemPKs projectedItemPKs;

    /**
     * Gets the value of the wsTransformerPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSTransformerV2PK }
     *     
     */
    public WSTransformerV2PK getWsTransformerPK() {
        return wsTransformerPK;
    }

    /**
     * Sets the value of the wsTransformerPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSTransformerV2PK }
     *     
     */
    public void setWsTransformerPK(WSTransformerV2PK value) {
        this.wsTransformerPK = value;
    }

    /**
     * Gets the value of the pipeline property.
     * 
     * @return
     *     possible object is
     *     {@link WSTransformerContext.Pipeline }
     *     
     */
    public WSTransformerContext.Pipeline getPipeline() {
        return pipeline;
    }

    /**
     * Sets the value of the pipeline property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSTransformerContext.Pipeline }
     *     
     */
    public void setPipeline(WSTransformerContext.Pipeline value) {
        this.pipeline = value;
    }

    /**
     * Gets the value of the projectedItemPKs property.
     * 
     * @return
     *     possible object is
     *     {@link WSTransformerContext.ProjectedItemPKs }
     *     
     */
    public WSTransformerContext.ProjectedItemPKs getProjectedItemPKs() {
        return projectedItemPKs;
    }

    /**
     * Sets the value of the projectedItemPKs property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSTransformerContext.ProjectedItemPKs }
     *     
     */
    public void setProjectedItemPKs(WSTransformerContext.ProjectedItemPKs value) {
        this.projectedItemPKs = value;
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
     *         &lt;element name="pipelineItem" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="variable" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="wsTypedContent" type="{urn-com-amalto-xtentis-webservice}WSTypedContent"/>
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
    @XmlType(name = "", propOrder = {
        "pipelineItem"
    })
    public static class Pipeline {

        protected List<WSTransformerContext.Pipeline.PipelineItem> pipelineItem;

        /**
         * Gets the value of the pipelineItem property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the pipelineItem property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPipelineItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WSTransformerContext.Pipeline.PipelineItem }
         * 
         * 
         */
        public List<WSTransformerContext.Pipeline.PipelineItem> getPipelineItem() {
            if (pipelineItem == null) {
                pipelineItem = new ArrayList<WSTransformerContext.Pipeline.PipelineItem>();
            }
            return this.pipelineItem;
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
         *         &lt;element name="variable" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="wsTypedContent" type="{urn-com-amalto-xtentis-webservice}WSTypedContent"/>
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
            "variable",
            "wsTypedContent"
        })
        public static class PipelineItem {

            @XmlElement(required = true)
            protected String variable;
            @XmlElement(required = true, nillable = true)
            protected WSTypedContent wsTypedContent;

            /**
             * Gets the value of the variable property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getVariable() {
                return variable;
            }

            /**
             * Sets the value of the variable property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setVariable(String value) {
                this.variable = value;
            }

            /**
             * Gets the value of the wsTypedContent property.
             * 
             * @return
             *     possible object is
             *     {@link WSTypedContent }
             *     
             */
            public WSTypedContent getWsTypedContent() {
                return wsTypedContent;
            }

            /**
             * Sets the value of the wsTypedContent property.
             * 
             * @param value
             *     allowed object is
             *     {@link WSTypedContent }
             *     
             */
            public void setWsTypedContent(WSTypedContent value) {
                this.wsTypedContent = value;
            }

        }

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
     *         &lt;element name="wsItemPOJOPK" type="{urn-com-amalto-xtentis-webservice}WSItemPK" maxOccurs="unbounded"/>
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
        "wsItemPOJOPK"
    })
    public static class ProjectedItemPKs {

        @XmlElement(required = true)
        protected List<WSItemPK> wsItemPOJOPK;

        /**
         * Gets the value of the wsItemPOJOPK property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the wsItemPOJOPK property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getWsItemPOJOPK().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WSItemPK }
         * 
         * 
         */
        public List<WSItemPK> getWsItemPOJOPK() {
            if (wsItemPOJOPK == null) {
                wsItemPOJOPK = new ArrayList<WSItemPK>();
            }
            return this.wsItemPOJOPK;
        }

    }

}
