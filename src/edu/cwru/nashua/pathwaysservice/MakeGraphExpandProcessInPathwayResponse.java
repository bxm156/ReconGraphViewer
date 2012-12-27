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
 *         &lt;element name="MakeGraphExpandProcessInPathwayResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "makeGraphExpandProcessInPathwayResult"
        })
@XmlRootElement(name = "MakeGraphExpandProcessInPathwayResponse")
public class MakeGraphExpandProcessInPathwayResponse {

    @XmlElement(name = "MakeGraphExpandProcessInPathwayResult")
    protected String makeGraphExpandProcessInPathwayResult;

    /**
     * Gets the value of the makeGraphExpandProcessInPathwayResult property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMakeGraphExpandProcessInPathwayResult() {
        return makeGraphExpandProcessInPathwayResult;
    }

    /**
     * Sets the value of the makeGraphExpandProcessInPathwayResult property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMakeGraphExpandProcessInPathwayResult(String value) {
        this.makeGraphExpandProcessInPathwayResult = value;
    }

}
