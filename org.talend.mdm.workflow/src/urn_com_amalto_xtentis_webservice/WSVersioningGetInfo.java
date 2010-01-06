
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Get Information about the Versioning System
 * 			
 * 
 * <p>Java class for WSVersioningGetInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSVersioningGetInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="versioningSystemName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSVersioningGetInfo", propOrder = {
    "versioningSystemName"
})
public class WSVersioningGetInfo {

    @XmlElement(required = true, nillable = true)
    protected String versioningSystemName;

    /**
     * Gets the value of the versioningSystemName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersioningSystemName() {
        return versioningSystemName;
    }

    /**
     * Sets the value of the versioningSystemName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersioningSystemName(String value) {
        this.versioningSystemName = value;
    }

}
