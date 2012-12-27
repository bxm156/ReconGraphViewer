package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="pathwayId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="molecularEntityId1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="molecularEntityId2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="organismName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "pathwayId",
        "molecularEntityId1",
        "molecularEntityId2",
        "organismName"
        })
@XmlRootElement(name = "MakeGraphPathBetweenMolecularEntitiesInPathway")
public class MakeGraphPathBetweenMolecularEntitiesInPathway {

    protected String pathwayId;
    protected String molecularEntityId1;
    protected String molecularEntityId2;
    protected String organismName;

    /**
     * Gets the value of the pathwayId property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getPathwayId() {
        return pathwayId;
    }

    /**
     * Sets the value of the pathwayId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPathwayId(String value) {
        this.pathwayId = value;
    }

    /**
     * Gets the value of the molecularEntityId1 property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMolecularEntityId1() {
        return molecularEntityId1;
    }

    /**
     * Sets the value of the molecularEntityId1 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMolecularEntityId1(String value) {
        this.molecularEntityId1 = value;
    }

    /**
     * Gets the value of the molecularEntityId2 property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getMolecularEntityId2() {
        return molecularEntityId2;
    }

    /**
     * Sets the value of the molecularEntityId2 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMolecularEntityId2(String value) {
        this.molecularEntityId2 = value;
    }

    /**
     * Gets the value of the organismName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getOrganismName() {
        return organismName;
    }

    /**
     * Sets the value of the organismName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOrganismName(String value) {
        this.organismName = value;
    }

}
