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
 *         &lt;element name="MakeGraphPathBetweenMolecularEntitiesInPathwayResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "makeGraphPathBetweenMolecularEntitiesInPathwayResult"
        })
@XmlRootElement(name = "MakeGraphPathBetweenMolecularEntitiesInPathwayResponse")
public class MakeGraphPathBetweenMolecularEntitiesInPathwayResponse {

    @XmlElement(name = "MakeGraphPathBetweenMolecularEntitiesInPathwayResult")
    protected String makeGraphPathBetweenMolecularEntitiesInPathwayResult;

    /**
     * Gets the value of the makeGraphPathBetweenMolecularEntitiesInPathwayResult property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMakeGraphPathBetweenMolecularEntitiesInPathwayResult() {
        return makeGraphPathBetweenMolecularEntitiesInPathwayResult;
    }

    /**
     * Sets the value of the makeGraphPathBetweenMolecularEntitiesInPathwayResult property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMakeGraphPathBetweenMolecularEntitiesInPathwayResult(String value) {
        this.makeGraphPathBetweenMolecularEntitiesInPathwayResult = value;
    }

}
