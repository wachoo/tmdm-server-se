
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Put a versioning System Configuration
 * 			
 * 
 * <p>Java class for WSPutVersioningSystemConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutVersioningSystemConfiguration">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="versioningSystemConfiguration" type="{urn-com-amalto-xtentis-webservice}WSVersioningSystemConfiguration"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutVersioningSystemConfiguration", propOrder = {
    "versioningSystemConfiguration"
})
public class WSPutVersioningSystemConfiguration {

    @XmlElement(required = true)
    protected WSVersioningSystemConfiguration versioningSystemConfiguration;

    /**
     * Gets the value of the versioningSystemConfiguration property.
     * 
     * @return
     *     possible object is
     *     {@link WSVersioningSystemConfiguration }
     *     
     */
    public WSVersioningSystemConfiguration getVersioningSystemConfiguration() {
        return versioningSystemConfiguration;
    }

    /**
     * Sets the value of the versioningSystemConfiguration property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSVersioningSystemConfiguration }
     *     
     */
    public void setVersioningSystemConfiguration(WSVersioningSystemConfiguration value) {
        this.versioningSystemConfiguration = value;
    }

}
