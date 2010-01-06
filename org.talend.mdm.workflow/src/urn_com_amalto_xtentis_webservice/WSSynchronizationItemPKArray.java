
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSSynchronizationItemPKArray complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSSynchronizationItemPKArray">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsSynchronizationItemPK" type="{urn-com-amalto-xtentis-webservice}WSSynchronizationItemPK" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSSynchronizationItemPKArray", propOrder = {
    "wsSynchronizationItemPK"
})
public class WSSynchronizationItemPKArray {

    @XmlElement(required = true)
    protected List<WSSynchronizationItemPK> wsSynchronizationItemPK;

    /**
     * Gets the value of the wsSynchronizationItemPK property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wsSynchronizationItemPK property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWsSynchronizationItemPK().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSSynchronizationItemPK }
     * 
     * 
     */
    public List<WSSynchronizationItemPK> getWsSynchronizationItemPK() {
        if (wsSynchronizationItemPK == null) {
            wsSynchronizationItemPK = new ArrayList<WSSynchronizationItemPK>();
        }
        return this.wsSynchronizationItemPK;
    }

}
