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
 *         &lt;element name="MakeGraphExpandMolecularEntityInPathwayResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "makeGraphExpandMolecularEntityInPathwayResult"
        })
@XmlRootElement(name = "MakeGraphExpandMolecularEntityInPathwayResponse")
public class MakeGraphExpandMolecularEntityInPathwayResponse {

    @XmlElement(name = "MakeGraphExpandMolecularEntityInPathwayResult")
    protected String makeGraphExpandMolecularEntityInPathwayResult;

    /**
     * Gets the value of the makeGraphExpandMolecularEntityInPathwayResult property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMakeGraphExpandMolecularEntityInPathwayResult() {
        return makeGraphExpandMolecularEntityInPathwayResult;
    }

    /**
     * Sets the value of the makeGraphExpandMolecularEntityInPathwayResult property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMakeGraphExpandMolecularEntityInPathwayResult(String value) {
        this.makeGraphExpandMolecularEntityInPathwayResult = value;
    }

}
