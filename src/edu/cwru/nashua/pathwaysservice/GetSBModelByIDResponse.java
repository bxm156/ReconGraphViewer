package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Mar 16, 2009
 * Time: 3:33:54 PM
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getSBModelByIDResult"
})

@XmlRootElement(name = "GetSBModelByIDResponse")
public class GetSBModelByIDResponse {

    @XmlElement(name = "GetSBModelByIDResult")
    protected String getSBModelByIDResult;

    /**
     * Gets the value of the getSBModelByIDResult property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGetSBModelByIDResult() {
//        return "this.getSBModelByIDResult = value";
        return getSBModelByIDResult;
    }

    /**
     * Sets the value of the getSBModelByIDResult property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGetSBModelByIDResult(String value) {
        this.getSBModelByIDResult = value;
    }

}

