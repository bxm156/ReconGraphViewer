
package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SoapPathway complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SoapPathway">
 *   &lt;complexContent>
 *     &lt;extension base="{http://nashua.cwru.edu/PathwaysService/}SoapObject">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{http://microsoft.com/wsdl/types/}guid"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PathwayStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PathwayNotes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoapCompartment", propOrder = {
    "id",
    "name",
    "spatialDimensions",
    "size",
    "outside"
})
public class SoapCompartment
    extends SoapObject
{

    @XmlElement(name = "ID", required = true)
    protected String id;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "SpatialDimensions")
    protected String spatialDimensions;
    @XmlElement(name = "Size")
    protected String size;
    @XmlElement(name = "Outside")
    protected String outside;

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
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getspatialDimensions() {
        return spatialDimensions;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setspatialDimensions(String value) {
        this.spatialDimensions = value;
    }

    /**
     * Gets the value of the pathwayStatus property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the value of the pathwayStatus property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSize(String value) {
        this.size = value;
    }

    public String getOutside() {
        return outside;
    }
    public void setOutside(String value) {
        this.outside = value;
    }


}