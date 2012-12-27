
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
 *         &lt;element name="GetGenomesForPathwaysResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getGenomesForPathwaysResult"
})
@XmlRootElement(name = "GetGenomesForPathwaysResponse")
public class GetGenomesForPathwaysResponse {

    @XmlElement(name = "GetGenomesForPathwaysResult")
    protected String getGenomesForPathwaysResult;

    /**
     * Gets the value of the getGenomesForPathwaysResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetGenomesForPathwaysResult() {
        return getGenomesForPathwaysResult;
    }

    /**
     * Sets the value of the getGenomesForPathwaysResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetGenomesForPathwaysResult(String value) {
        this.getGenomesForPathwaysResult = value;
    }

}
