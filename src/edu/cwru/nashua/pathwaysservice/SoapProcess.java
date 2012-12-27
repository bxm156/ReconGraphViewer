
package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SoapProcess complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SoapProcess">
 *   &lt;complexContent>
 *     &lt;extension base="{http://nashua.cwru.edu/PathwaysService/}SoapObject">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{http://microsoft.com/wsdl/types/}guid"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Reversible" type="{http://nashua.cwru.edu/PathwaysService/}Tribool"/>
 *         &lt;element name="Location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProcessNotes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GenericProcessID" type="{http://microsoft.com/wsdl/types/}guid"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoapProcess", propOrder = {
    "id",
    "name",
    "reversible",
    "location",
    "processNotes",
    "genericProcessID"
})
public class SoapProcess
    extends SoapObject
{

    @XmlElement(name = "ID", required = true)
    protected String id;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Reversible", required = true)
    protected Tribool reversible;
    @XmlElement(name = "Location")
    protected String location;
    @XmlElement(name = "ProcessNotes")
    protected String processNotes;
    @XmlElement(name = "GenericProcessID", required = true)
    protected String genericProcessID;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the reversible property.
     * 
     * @return
     *     possible object is
     *     {@link Tribool }
     *     
     */
    public Tribool getReversible() {
        return reversible;
    }

    /**
     * Sets the value of the reversible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tribool }
     *     
     */
    public void setReversible(Tribool value) {
        this.reversible = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Gets the value of the processNotes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessNotes() {
        return processNotes;
    }

    /**
     * Sets the value of the processNotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessNotes(String value) {
        this.processNotes = value;
    }

    /**
     * Gets the value of the genericProcessID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenericProcessID() {
        return genericProcessID;
    }

    /**
     * Sets the value of the genericProcessID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenericProcessID(String value) {
        this.genericProcessID = value;
    }

}
