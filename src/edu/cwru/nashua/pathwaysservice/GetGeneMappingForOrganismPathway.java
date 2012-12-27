
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
 *         &lt;element name="pathwayId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="organismGroupId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "pathwayId",
    "organismGroupId"
})
@XmlRootElement(name = "GetGeneMappingForOrganismPathway")
public class GetGeneMappingForOrganismPathway {

    protected String pathwayId;
    protected String organismGroupId;

    /**
     * Gets the value of the pathwayId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPathwayId() {
        return pathwayId;
    }

    /**
     * Sets the value of the pathwayId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPathwayId(String value) {
        this.pathwayId = value;
    }

    /**
     * Gets the value of the organismGroupId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganismGroupId() {
        return organismGroupId;
    }

    /**
     * Sets the value of the organismGroupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganismGroupId(String value) {
        this.organismGroupId = value;
    }

}
