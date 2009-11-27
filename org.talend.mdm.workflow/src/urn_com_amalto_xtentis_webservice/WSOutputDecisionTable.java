
package urn_com_amalto_xtentis_webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Used by the WSProcess...UsingTransformer
 * 				The decision Tables specifies what happens to the ouptut variables
 * 				NONE: the variables is present in the pipeline with its content
 * 				DISCARD: the variable and its content is removed from the pipeline before the web service returns
 * 				PROJECT(DataCluster,DataModel[,overwrite]): the content is projected to DataCluster after having been validated by DataModel. 
 * 				"overwrite" specifies whether to overwrite an existing item and is true by default. 
 * 				Possible values are true or false
 * 			
 * 
 * <p>Java class for WSOutputDecisionTable complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSOutputDecisionTable">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="decisions" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="outputVariableName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="decision" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "WSOutputDecisionTable", propOrder = {
    "decisions"
})
public class WSOutputDecisionTable {

    @XmlElement(required = true)
    protected List<WSOutputDecisionTable.Decisions> decisions;

    /**
     * Gets the value of the decisions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the decisions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDecisions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSOutputDecisionTable.Decisions }
     * 
     * 
     */
    public List<WSOutputDecisionTable.Decisions> getDecisions() {
        if (decisions == null) {
            decisions = new ArrayList<WSOutputDecisionTable.Decisions>();
        }
        return this.decisions;
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
     *         &lt;element name="outputVariableName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="decision" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "outputVariableName",
        "decision"
    })
    public static class Decisions {

        @XmlElement(required = true, nillable = true)
        protected String outputVariableName;
        @XmlElement(required = true)
        protected String decision;

        /**
         * Gets the value of the outputVariableName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOutputVariableName() {
            return outputVariableName;
        }

        /**
         * Sets the value of the outputVariableName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOutputVariableName(String value) {
            this.outputVariableName = value;
        }

        /**
         * Gets the value of the decision property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDecision() {
            return decision;
        }

        /**
         * Sets the value of the decision property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDecision(String value) {
            this.decision = value;
        }

    }

}
