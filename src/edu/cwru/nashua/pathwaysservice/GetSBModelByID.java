package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Mar 16, 2009
 * Time: 3:21:01 PM
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {    
    "modelId",
    "testmodelId"
})

@XmlRootElement(name = "GetSBModelByID")
public class GetSBModelByID {
      
        protected String modelId;
        protected String testmodelId;
 
        public String getModelId() {
            return modelId;
        }

        /**
         * Sets the value of the collapsedPathwayGuids property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setModelId(String value) {
            this.modelId = value;
        }

    public String getTestModelId() {
            return testmodelId;
        }
    public void setTestModelId(String value) {
        this.testmodelId = value;
    }
}
