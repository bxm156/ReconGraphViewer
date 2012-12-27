
package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="collapsedPathwayGuids" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expandedPathwayGuids" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="genericProcessGuids" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="moleculeGuids" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="layout" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "collapsedPathwayGuids",
    "expandedPathwayGuids",
    "genericProcessGuids",
    "moleculeGuids",
    "layout",
    "key"
})
@XmlRootElement(name = "StoreLayout")
public class StoreLayout {

    protected String collapsedPathwayGuids;
    protected String expandedPathwayGuids;
    protected String genericProcessGuids;
    protected String moleculeGuids;
    protected String layout;
    protected String key;

    /**
     * Gets the value of the collapsedPathwayGuids property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCollapsedPathwayGuids() {
        return collapsedPathwayGuids;
    }

    /**
     * Sets the value of the collapsedPathwayGuids property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCollapsedPathwayGuids(String value) {
        this.collapsedPathwayGuids = value;
    }

    /**
     * Gets the value of the expandedPathwayGuids property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpandedPathwayGuids() {
        return expandedPathwayGuids;
    }

    /**
     * Sets the value of the expandedPathwayGuids property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpandedPathwayGuids(String value) {
        this.expandedPathwayGuids = value;
    }

    /**
     * Gets the value of the genericProcessGuids property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenericProcessGuids() {
        return genericProcessGuids;
    }

    /**
     * Sets the value of the genericProcessGuids property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenericProcessGuids(String value) {
        this.genericProcessGuids = value;
    }

    /**
     * Gets the value of the moleculeGuids property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMoleculeGuids() {
        return moleculeGuids;
    }

    /**
     * Sets the value of the moleculeGuids property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMoleculeGuids(String value) {
        this.moleculeGuids = value;
    }

    /**
     * Gets the value of the layout property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayout() {
        return layout;
    }

    /**
     * Sets the value of the layout property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayout(String value) {
        this.layout = value;
    }

        /**
     * Gets the value of the key property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey(String value) {
        this.key = value;
    }

}
