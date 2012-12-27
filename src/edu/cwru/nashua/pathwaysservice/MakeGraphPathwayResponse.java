package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MakeGraphPathwayResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "makeGraphPathwayResult"
        })
@XmlRootElement(name = "MakeGraphPathwayResponse")
public class MakeGraphPathwayResponse {

    @XmlElement(name = "MakeGraphPathwayResult")
    protected String makeGraphPathwayResult;

    /**
     * Gets the value of the makeGraphPathwayResult property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMakeGraphPathwayResult() {
        return makeGraphPathwayResult;
    }

    /**
     * Sets the value of the makeGraphPathwayResult property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMakeGraphPathwayResult(String value) {
        this.makeGraphPathwayResult = value;
    }

}
