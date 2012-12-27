package binevi.IO.PathCaseWS;

import edu.cwru.nashua.pathwaysservice.PathwaysService;

import java.net.URL;
import java.net.MalformedURLException;

   
public class PathCaseServices {

    private PathwaysService service;

    public PathCaseServices (String wsdlurl)
    {
        try {
            service = new PathwaysService(new URL(wsdlurl));
        } catch (MalformedURLException e) {
            //e.printStackTrace();
            service = null;
            System.out.println("ERROR: PathwaysService is null, check PathCaseServices.java");
        }
    }


    public String retrieveGraphXML (String collapsedpathwayids,String epxandedpathwayids, String genericprocessids, String moleculeids)
    {
        if (service!=null)
            return service.getPathwaysServiceSoap().getGraphData(collapsedpathwayids, epxandedpathwayids, genericprocessids, moleculeids,"","","","");
        else return null;
    }

    public String retrieveOrganismXML ()
    {
        if (service!=null)
            return service.getPathwaysServiceSoap().getOrganismHierarchy();
        else return null;
    }

    public String retrieveGenesXML (String pathwayId, String organismGroupId)
    {
        if (service!=null)
            return service.getPathwaysServiceSoap().getGeneMappingForOrganismPathway(pathwayId,organismGroupId);
        else return null;
    }

    //wy
    public String retrieveLayout (String collapsedPathwayGuids,String expandedPathwayGuids,String genericProcessGuids, String moleculeGuids)
    {
        if(service!=null)
            return service.getPathwaysServiceSoap().retrieveLayout(collapsedPathwayGuids,expandedPathwayGuids,genericProcessGuids, moleculeGuids);
        else return null;
    }
    // end of wy

}
