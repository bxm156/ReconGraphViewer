package binevi.Parser.PathCaseParsers;

import binevi.Resources.PathCaseResources.PathCaseRepository;
import binevi.Resources.PathCaseResources.OrganismTable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Xinjian
 * Date: Mar 16, 2009
 * Time: 9:31:52 AM
 */
public class MQLXMLParser {
    PathCaseRepository repository;

    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;

    public MQLXMLParser(PathCaseRepository repository) {
        this.repository = repository;

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            //e.printStackTrace();
            System.out.println("ERROR: ParserConfigurationException, check MetaboliteXMLParser.java");
        }
    }

   public void loadRepositoryFromGraphXML(String graphXML) {
          try {
            Document document = builder.parse(new InputSource(new StringReader(graphXML)));

            // SBModel is sign of SystemBiology XML document, which equals to GraphData of PathCase.
            NodeList graphroots = document.getElementsByTagName("GraphData");

            Node graphroot = graphroots.item(0);

            NodeList graphElements = graphroot.getChildNodes();
            for (int i = 0; i < graphElements.getLength(); i++) {
                Node graphElement = graphElements.item(i);

                // GraphData/Molecules
                if (graphElement.getNodeName().equals("Pathways")) {
                    loadPathwaysFromXMLNode(graphElement);
                }
                // GraphData/GenericProcesses
                else if (graphElement.getNodeName().equals("PathwaysRelations")) {
                    loadPathwaysRelationsFromXMLNode(graphElement);
                }
            }
        } catch (SAXException sxe) {
            // Error generated during parsing
            Exception x = sxe;
            if (sxe.getException() != null)
                x = sxe.getException();
            //x.printStackTrace();

        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();

        }
    }

     private void loadPathwaysFromXMLNode(Node graphElement) {
         NodeList pathwaynodes = graphElement.getChildNodes();
         for (int i = 0; i < pathwaynodes.getLength(); i++) {
             Node pathwaynode = pathwaynodes.item(i);
             if (pathwaynode.getNodeName().equals("Pathway")){
                NamedNodeMap PathwayAttributes =pathwaynode.getAttributes();
                String pathwayId = getNodeStringValue(PathwayAttributes.getNamedItem("ID"));
                String pathwayName = getNodeStringValue(PathwayAttributes.getNamedItem("name"));
                repository.pathwayTable.insertRow(pathwayId,pathwayName,true,false); // iexpanded; iLinking
                for(int j=0;j<pathwaynode.getChildNodes().getLength();j++){
                    Node compartmentsNode=pathwaynode.getChildNodes().item(j);
                    if(compartmentsNode.getNodeName().equals("Compartments")){
//                        NodeList compartmentnodes=compartmentsNode.getChildNodes();
//                        for (int m = 0; m < compartmentnodes.getLength(); m++) {
//                            Node compartmentnode=compartmentnodes.item(m);
                            loadCompartmentFromXMLNode(pathwayId,compartmentsNode);
//                        }
                    }else if(compartmentsNode.getNodeName().equals("Reactions")){
//                        NodeList compartmentnodes=compartmentsNode.getChildNodes();
//                        for (int m = 0; m < compartmentnodes.getLength(); m++) {
//                            Node compartmentnode=compartmentnodes.item(m);
                            loadReactionsFromXMLNode(compartmentsNode);
//                        }
                    }
                }
            }
         }
     }

     //biopax GraphData/Molecules
    private void loadPathwaysRelationsFromXMLNode(Node graphElement) {
         NodeList pathwaynodes = graphElement.getChildNodes();
         for (int i = 0; i < pathwaynodes.getLength(); i++) {
             Node pathwaynode = pathwaynodes.item(i);
            // GraphData/GenericProcesses/GenericProcess
            if (pathwaynode.getNodeName().equals("PathwaysRelation")){
                NamedNodeMap PathwayAttributes =pathwaynode.getAttributes();
                String pathwayRelation = getNodeStringValue(PathwayAttributes.getNamedItem("RelationID"));
                String pathwayId1 = getNodeStringValue(PathwayAttributes.getNamedItem("PathID1"));
                String pathwayId2 = getNodeStringValue(PathwayAttributes.getNamedItem("PathID2"));
                ArrayList<String> metaPools= new ArrayList<String>();

                Node metabolitePoolsNode=pathwaynode.getChildNodes().item(0);
                if(metabolitePoolsNode.getNodeName().equals("MetabolitePools")){
                    NodeList metabolitePoolnodes=metabolitePoolsNode.getChildNodes();
                    for (int m = 0; m < metabolitePoolnodes.getLength(); m++) {
                            Node metabolitePoolnode=metabolitePoolnodes.item(m);
                            if(metabolitePoolnode.getNodeName().equals("MetabolitePool"))
                                metaPools.add(getNodeStringValue(metabolitePoolnode.getAttributes().getNamedItem("ID")));
                    }
                }
                repository.pathwaysRelationTable.insertRow(pathwayRelation,pathwayId1,pathwayId2,metaPools);                
            }
         }
    }

   private void loadCompartmentFromXMLNode(String pathwayId,Node graphElement) {
       //compartmentsTable;       compartmentToSpeciesTable;    CompartmentToReactionsTable;   ReactionsTable; PathwaysTable
        NodeList compartmentnodes = graphElement.getChildNodes();       
        for (int i = 0; i < compartmentnodes.getLength(); i++) {
            Node compartmentnode = compartmentnodes.item(i);
            // GraphData/GenericProcesses/GenericProcess
            if (compartmentnode.getNodeName().equalsIgnoreCase("Compartment")) {

                NamedNodeMap compartmentAttributes = compartmentnode.getAttributes();

                String compartmentId = getNodeStringValue(compartmentAttributes.getNamedItem("ID"));
                String compartmentName = getNodeStringValue(compartmentAttributes.getNamedItem("name"));
                String sbmlID ="";
                String size =getNodeStringValue(compartmentAttributes.getNamedItem("size"));
                String spatialDimensions = "";                

                boolean isConstant = false;
                String compartmentType = getNodeStringValue(compartmentAttributes.getNamedItem("compartmentType"));
                String outsideID = getNodeStringValue(compartmentAttributes.getNamedItem("parentId"));

                repository.pathwayToCompartmentTable.insertRow(pathwayId, compartmentId);
                repository.compartmentsTable.insertRow(compartmentId,compartmentName,sbmlID,size,spatialDimensions,isConstant,compartmentType,outsideID);

                NodeList compartmentchildren = compartmentnode.getChildNodes();

                for (int j = 0; j < compartmentchildren.getLength(); j++) {
                    Node compartmentchild = compartmentchildren.item(j);

                    if (compartmentchild.getNodeName().equals("MetabolitePools")) {
                        loadCompartmentToMetaPoolTable(compartmentId, compartmentchild);
                    }else if (compartmentchild.getNodeName().equals("Reactions")) {
                        loadCompartmentToReactionsTable(compartmentId, compartmentchild);
                    }
                }
            }// GraphData/GenericProcesses/GenericProcess
        }
    }

   private void loadCompartmentToMetaPoolTable(String compartmentId, Node compartmentchild) {
        NodeList SpeciesNodelist = compartmentchild.getChildNodes();
        for (int j = 0; j < SpeciesNodelist.getLength(); j++) {
            Node SpeciesNode = SpeciesNodelist.item(j);

            // GraphData/GenericProcesses/GenericProcess/Molecules/Molecule
            if (SpeciesNode.getNodeName().equals("MetabolitePool")) {
                NamedNodeMap speciesAttributes = SpeciesNode.getAttributes();

                String SpeciesID = getNodeStringValue(speciesAttributes.getNamedItem("ID"));
                String SpeciesName = getNodeStringValue(speciesAttributes.getNamedItem("Name"));
                String sbmlID = getNodeStringValue(speciesAttributes.getNamedItem("MetaboliteID"));  // get MetaboliteID property
                String SpeciesTypeId = "";//getNodeStringValue(speciesAttributes.getNamedItem("SpeciesTypeId"));
                String InitialAmount ="";// getNodeStringValue(speciesAttributes.getNamedItem("InitialAmount"));
                String InitialConcentration ="";// getNodeStringValue(speciesAttributes.getNamedItem("InitialConcentration"));
                String SubstanceUnitsId = "";//getNodeStringValue(speciesAttributes.getNamedItem("SubstanceUnitsId"));
                String HasOnlySubstanceUnitsString ="";// getNodeStringValue(speciesAttributes.getNamedItem("IsCommon"));
                boolean HasOnlySubstanceUnits =false;// Boolean.parseBoolean(HasOnlySubstanceUnitsString);     // get IsCommon --- bool value
                boolean BoundaryCondition =false;// Boolean.parseBoolean(BoundaryConditionString);
                String Charge ="";// getNodeStringValue(speciesAttributes.getNamedItem("Charge"));
                boolean isConstant =false;// Boolean.parseBoolean(isConstantString);

                repository.speciesTable.insertRow(SpeciesID,SpeciesName,sbmlID,InitialAmount,SpeciesTypeId,InitialConcentration, SubstanceUnitsId,HasOnlySubstanceUnits,BoundaryCondition,Charge,isConstant,false);
                repository.compartmentToSpeciesTable.insertRow(compartmentId, SpeciesID);
            }
        }
    }

       private void loadCompartmentToReactionsTable(String compartmentId, Node compartmentchild) {
        NodeList SpeciesNodelist = compartmentchild.getChildNodes();
        for (int j = 0; j < SpeciesNodelist.getLength(); j++) {
            Node ReactionNode = SpeciesNodelist.item(j);

            // GraphData/GenericProcesses/GenericProcess/Molecules/Molecule
            if (ReactionNode.getNodeName().equals("Reaction")) {
                NamedNodeMap speciesAttributes = ReactionNode.getAttributes();

                String SpeciesID = getNodeStringValue(speciesAttributes.getNamedItem("ID"));

//                String SpeciesName = getNodeStringValue(speciesAttributes.getNamedItem("Name"));
//                String sbmlID = getNodeStringValue(speciesAttributes.getNamedItem("MetaboliteID"));  // get MetaboliteID property
//                String SpeciesTypeId = "";//getNodeStringValue(speciesAttributes.getNamedItem("SpeciesTypeId"));
//                String InitialAmount ="";// getNodeStringValue(speciesAttributes.getNamedItem("InitialAmount"));
//                String InitialConcentration ="";// getNodeStringValue(speciesAttributes.getNamedItem("InitialConcentration"));
//                String SubstanceUnitsId = "";//getNodeStringValue(speciesAttributes.getNamedItem("SubstanceUnitsId"));
//                String HasOnlySubstanceUnitsString = getNodeStringValue(speciesAttributes.getNamedItem("IsCommon"));
//                boolean HasOnlySubstanceUnits = Boolean.parseBoolean(HasOnlySubstanceUnitsString);     // get IsCommon --- bool value
//                String BoundaryConditionString ="";// getNodeStringValue(speciesAttributes.getNamedItem("BoundaryCondition"));
//                boolean BoundaryCondition =false;// Boolean.parseBoolean(BoundaryConditionString);
//                String Charge ="";// getNodeStringValue(speciesAttributes.getNamedItem("Charge"));
//                String isConstantString ="";// getNodeStringValue(speciesAttributes.getNamedItem("Constant"));
//                boolean isConstant =false;// Boolean.parseBoolean(isConstantString);
//
//                repository.speciesTable.insertRow(SpeciesID,SpeciesName,sbmlID,InitialAmount,SpeciesTypeId,InitialConcentration, SubstanceUnitsId,HasOnlySubstanceUnits,BoundaryCondition,Charge,isConstant);

                NodeList EnzymesNodeList = ReactionNode.getChildNodes(); //Enzymes node under Reaction under Reactions under Compartment under Compartments
                ArrayList<String> enzymes= new ArrayList<String>();
                for (int m = 0; m < EnzymesNodeList.getLength(); m++) {
                    Node EnzymesNode = EnzymesNodeList.item(m);
                    if (EnzymesNode.getNodeName().equals("Enzymes")) {
//                        NodeList EnzymeNodeList=EnzymesNode.getChildNodes();
                        for(int n=0; n<EnzymesNode.getChildNodes().getLength(); n++){
                            Node enzyme = EnzymesNode.getChildNodes().item(n);
                            if(enzyme.getNodeName().equals("Enzyme"))
                                enzymes.add(getNodeStringValue(enzyme.getAttributes().getNamedItem("ID")));
                        }
                    }
                }
                repository.compartmentToReactionsTable.insertRow(compartmentId, SpeciesID, enzymes);
            }
        }
    }


    private void loadReactionsFromXMLNode(Node graphElement) {
        NodeList reactionnodes = graphElement.getChildNodes();
        for (int i = 0; i < reactionnodes.getLength(); i++) {
            Node reactionnode = reactionnodes.item(i);
            // GraphData/GenericProcesses/GenericProcess
            if (reactionnode.getNodeName().equals("Reaction")) {
                NamedNodeMap reactionAttributes = reactionnode.getAttributes();

                String reactionId = getNodeStringValue(reactionAttributes.getNamedItem("ID"));
                String reactionName = getNodeStringValue(reactionAttributes.getNamedItem("name"));
                String sbmlId ="";// getNodeStringValue(reactionAttributes.getNamedItem("GenericReactionID")); //GenericReactionID
                String isReversibleString = getNodeStringValue(reactionAttributes.getNamedItem("IsReversible"));
                boolean isReversible  = Boolean.parseBoolean(isReversibleString);
                String KineticLawId ="";  // getNodeStringValue(reactionAttributes.getNamedItem("KineticLawId"));
                String isFastString = getNodeStringValue(reactionAttributes.getNamedItem("IsTransport"));
                boolean isFast  = Boolean.parseBoolean(isFastString);                                      //IsTransport
                String strActiveState = getNodeStringValue(reactionAttributes.getNamedItem("state"));

                repository.reactionsTable.insertRow(reactionId,reactionName,sbmlId,isReversible,KineticLawId,isFast,strActiveState);

                NodeList reactionchildren = reactionnode.getChildNodes();

                for (int j = 0; j < reactionchildren.getLength(); j++) {
                    Node reactionchild = reactionchildren.item(j);

                    if (reactionchild.getNodeName().equals("Enzymes")) {
                        loadReactionToEnzymeTable(reactionId, reactionchild);
                    } else if (reactionchild.getNodeName().equals("MetabolitePools")) {
                        loadReactionToMetaPoolTable(reactionId, reactionchild);
                    }
                }
            }// GraphData/GenericProcesses/GenericProcess
        }
    }


    private void loadReactionToEnzymeTable(String reactionId, Node compartmentchild) {
        NodeList SpeciesNodelist = compartmentchild.getChildNodes();
        for (int j = 0; j < SpeciesNodelist.getLength(); j++) {
            Node SpeciesNode = SpeciesNodelist.item(j);

            // GraphData/GenericProcesses/GenericProcess/Molecules/Molecule
            if (SpeciesNode.getNodeName().equals("Enzyme")) {
                NamedNodeMap speciesAttributes = SpeciesNode.getAttributes();

                String enzymeID = getNodeStringValue(speciesAttributes.getNamedItem("ID"));
                String enzymeName = getNodeStringValue(speciesAttributes.getNamedItem("name")); //get Role

                repository.enzymeTable.insertRow(enzymeID,enzymeName);
                repository.reactionToEnzymeTable.insertRow(reactionId, enzymeID);            // not used currently
            }
        }
    }

    private void loadReactionToMetaPoolTable(String compartmentId, Node compartmentchild) {
        NodeList SpeciesNodelist = compartmentchild.getChildNodes();
        for (int j = 0; j < SpeciesNodelist.getLength(); j++) {
            Node SpeciesNode = SpeciesNodelist.item(j);

            // GraphData/GenericProcesses/GenericProcess/Molecules/Molecule
            if (SpeciesNode.getNodeName().equals("MetabolitePool")) {
                NamedNodeMap speciesAttributes = SpeciesNode.getAttributes();

                String SpeciesID = getNodeStringValue(speciesAttributes.getNamedItem("ID"));

                String sbmlID =getNodeStringValue(speciesAttributes.getNamedItem("Role"));// getNodeStringValue(speciesAttributes.getNamedItem("MetaboliteID"));  // get MetaboliteID property             RoleInfo

                //use sbmlID as roleinfo.
                repository.speciesTable.updateRowWithRole(SpeciesID,sbmlID);
//                repository.compartmentToSpeciesTable.insertRow(compartmentId, SpeciesID);
            }
        }
    }

    private void loadReactionToReactionSpeciesTable(String reactionId, Node reactionchild) {
         NodeList ReactionSpeciesNodelist = reactionchild.getChildNodes();
         for (int j = 0; j < ReactionSpeciesNodelist.getLength(); j++) {
             Node ReactionSpeciesNode = ReactionSpeciesNodelist.item(j);

             // GraphData/GenericProcesses/GenericProcess/Molecules/Molecule
             if (ReactionSpeciesNode.getNodeName().equals("ReactionSpecies")) {
                 NamedNodeMap reactionSpeciesAttributes = ReactionSpeciesNode.getAttributes();

                 String ReactionSpeciesID = getNodeStringValue(reactionSpeciesAttributes.getNamedItem("ID"));
                 String ReactionSpeciesName = getNodeStringValue(reactionSpeciesAttributes.getNamedItem("Name"));
                 String SpeciesId = getNodeStringValue(reactionSpeciesAttributes.getNamedItem("SpeciesId"));
                 String RoleId = getNodeStringValue(reactionSpeciesAttributes.getNamedItem("RoleId"));
                 String Stoichiometry = getNodeStringValue(reactionSpeciesAttributes.getNamedItem("Stoichiometry"));

                 repository.reactionSpeciesTable.insertRow(ReactionSpeciesID,ReactionSpeciesName,SpeciesId,RoleId,Stoichiometry);
                 repository.reactionToReactionSpeciesTable.insertRow(reactionId,ReactionSpeciesID);
             }
         }
     }

    private String getNodeStringValue(Node node)
    {
        if (node == null) return "UNKNOWN_BIO_DATA";
        else return node.getNodeValue().trim();
    }

    private boolean getNodeBooleanValue(Node node)
    {
        if (node == null) return false;
        else return node.getNodeValue().trim().equalsIgnoreCase("true");
    }
}