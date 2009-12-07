
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Puts an item in the xml storage with custom update report
 * 			
 * 
 * <p>Java class for WSPutItemWithCustomReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutItemWithCustomReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsPutItemWithReport" type="{urn-com-amalto-xtentis-webservice}WSPutItemWithReport"/>
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutItemWithCustomReport", propOrder = {
    "wsPutItemWithReport",
    "user"
})
public class WSPutItemWithCustomReport {

    @XmlElement(required = true)
    protected WSPutItemWithReport wsPutItemWithReport;
    @XmlElement(required = true, nillable = true)
    protected String user;

    /**
     * Gets the value of the wsPutItemWithReport property.
     * 
     * @return
     *     possible object is
     *     {@link WSPutItemWithReport }
     *     
     */
    public WSPutItemWithReport getWsPutItemWithReport() {
        return wsPutItemWithReport;
    }

    /**
     * Sets the value of the wsPutItemWithReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSPutItemWithReport }
     *     
     */
    public void setWsPutItemWithReport(WSPutItemWithReport value) {
        this.wsPutItemWithReport = value;
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

}
