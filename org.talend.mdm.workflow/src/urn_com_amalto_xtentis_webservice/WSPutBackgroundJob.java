
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutBackgroundJob complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutBackgroundJob">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsBackgroundJob" type="{urn-com-amalto-xtentis-webservice}WSBackgroundJob"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutBackgroundJob", propOrder = {
    "wsBackgroundJob"
})
public class WSPutBackgroundJob {

    @XmlElement(required = true)
    protected WSBackgroundJob wsBackgroundJob;

    /**
     * Gets the value of the wsBackgroundJob property.
     * 
     * @return
     *     possible object is
     *     {@link WSBackgroundJob }
     *     
     */
    public WSBackgroundJob getWsBackgroundJob() {
        return wsBackgroundJob;
    }

    /**
     * Sets the value of the wsBackgroundJob property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSBackgroundJob }
     *     
     */
    public void setWsBackgroundJob(WSBackgroundJob value) {
        this.wsBackgroundJob = value;
    }

}
