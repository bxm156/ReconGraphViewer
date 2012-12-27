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
 *         &lt;element name="MakeGraphExpandPathwayResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "makeGraphExpandPathwayResult"
        })
@XmlRootElement(name = "MakeGraphExpandPathwayResponse")
public class MakeGraphExpandPathwayResponse {

    @XmlElement(name = "MakeGraphExpandPathwayResult")
    protected String makeGraphExpandPathwayResult;

    /**
     * Gets the value of the makeGraphExpandPathwayResult property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMakeGraphExpandPathwayResult() {
        return makeGraphExpandPathwayResult;
    }

    /**
     * Sets the value of the makeGraphExpandPathwayResult property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMakeGraphExpandPathwayResult(String value) {
        this.makeGraphExpandPathwayResult = value;
    }

}
