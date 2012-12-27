
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
 *         &lt;element name="pathwayIdSet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "pathwayIdSet"
})
@XmlRootElement(name = "GetGenomesForPathways")
public class GetGenomesForPathways {

    protected String pathwayIdSet;

    /**
     * Gets the value of the pathwayIdSet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPathwayIdSet() {
        return pathwayIdSet;
    }

    /**
     * Sets the value of the pathwayIdSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPathwayIdSet(String value) {
        this.pathwayIdSet = value;
    }

}
