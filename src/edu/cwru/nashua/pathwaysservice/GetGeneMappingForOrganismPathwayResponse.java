
package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="GetGeneMappingForOrganismPathwayResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getGeneMappingForOrganismPathwayResult"
})
@XmlRootElement(name = "GetGeneMappingForOrganismPathwayResponse")
public class GetGeneMappingForOrganismPathwayResponse {

    @XmlElement(name = "GetGeneMappingForOrganismPathwayResult")
    protected String getGeneMappingForOrganismPathwayResult;

    /**
     * Gets the value of the getGeneMappingForOrganismPathwayResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetGeneMappingForOrganismPathwayResult() {
        return getGeneMappingForOrganismPathwayResult;
    }

    /**
     * Sets the value of the getGeneMappingForOrganismPathwayResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetGeneMappingForOrganismPathwayResult(String value) {
        this.getGeneMappingForOrganismPathwayResult = value;
    }

}
