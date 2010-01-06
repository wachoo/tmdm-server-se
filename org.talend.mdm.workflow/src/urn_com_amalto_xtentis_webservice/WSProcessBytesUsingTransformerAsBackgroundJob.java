
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Process Bytes after transformation in a Transformer
 * 				and using a DecisionTable for the ouput variables
 * 				Uses a Background Job. Call getBackGroundJob to follow the Job Process
 * 			
 * 
 * <p>Java class for WSProcessBytesUsingTransformerAsBackgroundJob complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSProcessBytesUsingTransformerAsBackgroundJob">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsBytes" type="{urn-com-amalto-xtentis-webservice}WSByteArray"/>
 *         &lt;element name="contentType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="wsTransformerPK" type="{urn-com-amalto-xtentis-webservice}WSTransformerPK"/>
 *         &lt;element name="wsOutputDecisionTable" type="{urn-com-amalto-xtentis-webservice}WSOutputDecisionTable"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSProcessBytesUsingTransformerAsBackgroundJob", propOrder = {
    "wsBytes",
    "contentType",
    "wsTransformerPK",
    "wsOutputDecisionTable"
})
public class WSProcessBytesUsingTransformerAsBackgroundJob {

    @XmlElement(required = true)
    protected WSByteArray wsBytes;
    @XmlElement(required = true, nillable = true)
    protected String contentType;
    @XmlElement(required = true)
    protected WSTransformerPK wsTransformerPK;
    @XmlElement(required = true)
    protected WSOutputDecisionTable wsOutputDecisionTable;

    /**
     * Gets the value of the wsBytes property.
     * 
     * @return
     *     possible object is
     *     {@link WSByteArray }
     *     
     */
    public WSByteArray getWsBytes() {
        return wsBytes;
    }

    /**
     * Sets the value of the wsBytes property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSByteArray }
     *     
     */
    public void setWsBytes(WSByteArray value) {
        this.wsBytes = value;
    }

    /**
     * Gets the value of the contentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the value of the contentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentType(String value) {
        this.contentType = value;
    }

    /**
     * Gets the value of the wsTransformerPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSTransformerPK }
     *     
     */
    public WSTransformerPK getWsTransformerPK() {
        return wsTransformerPK;
    }

    /**
     * Sets the value of the wsTransformerPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSTransformerPK }
     *     
     */
    public void setWsTransformerPK(WSTransformerPK value) {
        this.wsTransformerPK = value;
    }

    /**
     * Gets the value of the wsOutputDecisionTable property.
     * 
     * @return
     *     possible object is
     *     {@link WSOutputDecisionTable }
     *     
     */
    public WSOutputDecisionTable getWsOutputDecisionTable() {
        return wsOutputDecisionTable;
    }

    /**
     * Sets the value of the wsOutputDecisionTable property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSOutputDecisionTable }
     *     
     */
    public void setWsOutputDecisionTable(WSOutputDecisionTable value) {
        this.wsOutputDecisionTable = value;
    }

}
