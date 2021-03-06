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
 *         &lt;element name="MakeGraphGenericPathwayResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "makeGraphGenericPathwayResult"
        })
@XmlRootElement(name = "MakeGraphGenericPathwayResponse")
public class MakeGraphGenericPathwayResponse {

    @XmlElement(name = "MakeGraphGenericPathwayResult")
    protected String makeGraphGenericPathwayResult;

    /**
     * Gets the value of the makeGraphGenericPathwayResult property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMakeGraphGenericPathwayResult() {
        return makeGraphGenericPathwayResult;
    }

    /**
     * Sets the value of the makeGraphGenericPathwayResult property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMakeGraphGenericPathwayResult(String value) {
        this.makeGraphGenericPathwayResult = value;
    }

}
