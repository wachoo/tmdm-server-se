
package urn_com_amalto_xtentis_webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSMDMConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSMDMConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serverName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="serverPort" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="userName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="xdbDriver" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="xdbID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="xdbUrl" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="isupurl" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSMDMConfig", propOrder = {
    "serverName",
    "serverPort",
    "userName",
    "password",
    "xdbDriver",
    "xdbID",
    "xdbUrl",
    "isupurl"
})
public class WSMDMConfig {

    @XmlElement(required = true, nillable = true)
    protected String serverName;
    @XmlElement(required = true, nillable = true)
    protected String serverPort;
    @XmlElement(required = true, nillable = true)
    protected String userName;
    @XmlElement(required = true, nillable = true)
    protected String password;
    @XmlElement(required = true, nillable = true)
    protected String xdbDriver;
    @XmlElement(required = true, nillable = true)
    protected String xdbID;
    @XmlElement(required = true, nillable = true)
    protected String xdbUrl;
    @XmlElement(required = true, nillable = true)
    protected String isupurl;

    /**
     * Gets the value of the serverName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Sets the value of the serverName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServerName(String value) {
        this.serverName = value;
    }

    /**
     * Gets the value of the serverPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServerPort() {
        return serverPort;
    }

    /**
     * Sets the value of the serverPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServerPort(String value) {
        this.serverPort = value;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the xdbDriver property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXdbDriver() {
        return xdbDriver;
    }

    /**
     * Sets the value of the xdbDriver property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXdbDriver(String value) {
        this.xdbDriver = value;
    }

    /**
     * Gets the value of the xdbID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXdbID() {
        return xdbID;
    }

    /**
     * Sets the value of the xdbID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXdbID(String value) {
        this.xdbID = value;
    }

    /**
     * Gets the value of the xdbUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXdbUrl() {
        return xdbUrl;
    }

    /**
     * Sets the value of the xdbUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXdbUrl(String value) {
        this.xdbUrl = value;
    }

    /**
     * Gets the value of the isupurl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsupurl() {
        return isupurl;
    }

    /**
     * Sets the value of the isupurl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsupurl(String value) {
        this.isupurl = value;
    }

}
