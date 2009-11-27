
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				History of an item
 * 			
 * 
 * <p>Java class for WSVersioningItemHistory complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSVersioningItemHistory">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsItemPK" type="{urn-com-amalto-xtentis-webservice}WSItemPK"/>
 *         &lt;element name="wsHistoryEntries" type="{urn-com-amalto-xtentis-webservice}WSVersioningHistoryEntry" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSVersioningItemHistory", propOrder = {
    "wsItemPK",
    "wsHistoryEntries"
})
public class WSVersioningItemHistory {

    @XmlElement(required = true)
    protected WSItemPK wsItemPK;
    @XmlElement(required = true)
    protected List<WSVersioningHistoryEntry> wsHistoryEntries;

    /**
     * Gets the value of the wsItemPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSItemPK }
     *     
     */
    public WSItemPK getWsItemPK() {
        return wsItemPK;
    }

    /**
     * Sets the value of the wsItemPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSItemPK }
     *     
     */
    public void setWsItemPK(WSItemPK value) {
        this.wsItemPK = value;
    }

    /**
     * Gets the value of the wsHistoryEntries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wsHistoryEntries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWsHistoryEntries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSVersioningHistoryEntry }
     * 
     * 
     */
    public List<WSVersioningHistoryEntry> getWsHistoryEntries() {
        if (wsHistoryEntries == null) {
            wsHistoryEntries = new ArrayList<WSVersioningHistoryEntry>();
        }
        return this.wsHistoryEntries;
    }

}
