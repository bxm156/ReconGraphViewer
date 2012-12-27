
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
 *         &lt;element name="openSection" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="organism" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="openNode1ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="openNode1Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="openNode2ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="openNode2Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="openNode3ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="openNode3Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="displayItemID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="displayItemType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "openSection",
    "organism",
    "openNode1ID",
    "openNode1Type",
    "openNode2ID",
    "openNode2Type",
    "openNode3ID",
    "openNode3Type",
    "displayItemID",
    "displayItemType"
})
@XmlRootElement(name = "MakeLinkToPage")
public class MakeLinkToPage {

    protected String openSection;
    protected String organism;
    protected String openNode1ID;
    protected String openNode1Type;
    protected String openNode2ID;
    protected String openNode2Type;
    protected String openNode3ID;
    protected String openNode3Type;
    protected String displayItemID;
    protected String displayItemType;

    /**
     * Gets the value of the openSection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpenSection() {
        return openSection;
    }

    /**
     * Sets the value of the openSection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpenSection(String value) {
        this.openSection = value;
    }

    /**
     * Gets the value of the organism property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganism() {
        return organism;
    }

    /**
     * Sets the value of the organism property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganism(String value) {
        this.organism = value;
    }

    /**
     * Gets the value of the openNode1ID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpenNode1ID() {
        return openNode1ID;
    }

    /**
     * Sets the value of the openNode1ID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpenNode1ID(String value) {
        this.openNode1ID = value;
    }

    /**
     * Gets the value of the openNode1Type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpenNode1Type() {
        return openNode1Type;
    }

    /**
     * Sets the value of the openNode1Type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpenNode1Type(String value) {
        this.openNode1Type = value;
    }

    /**
     * Gets the value of the openNode2ID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpenNode2ID() {
        return openNode2ID;
    }

    /**
     * Sets the value of the openNode2ID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpenNode2ID(String value) {
        this.openNode2ID = value;
    }

    /**
     * Gets the value of the openNode2Type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpenNode2Type() {
        return openNode2Type;
    }

    /**
     * Sets the value of the openNode2Type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpenNode2Type(String value) {
        this.openNode2Type = value;
    }

    /**
     * Gets the value of the openNode3ID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpenNode3ID() {
        return openNode3ID;
    }

    /**
     * Sets the value of the openNode3ID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpenNode3ID(String value) {
        this.openNode3ID = value;
    }

    /**
     * Gets the value of the openNode3Type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpenNode3Type() {
        return openNode3Type;
    }

    /**
     * Sets the value of the openNode3Type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpenNode3Type(String value) {
        this.openNode3Type = value;
    }

    /**
     * Gets the value of the displayItemID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayItemID() {
        return displayItemID;
    }

    /**
     * Sets the value of the displayItemID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayItemID(String value) {
        this.displayItemID = value;
    }

    /**
     * Gets the value of the displayItemType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayItemType() {
        return displayItemType;
    }

    /**
     * Sets the value of the displayItemType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayItemType(String value) {
        this.displayItemType = value;
    }

}
