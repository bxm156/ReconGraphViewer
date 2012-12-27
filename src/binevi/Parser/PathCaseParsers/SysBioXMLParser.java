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
public class SysBioXMLParser {
    PathCaseRepository repository;

    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;

    public SysBioXMLParser(PathCaseRepository repository) {
        this.repository = repository;

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            //e.printStackTrace();
            System.out.println("ERROR: ParserConfigurationException, check SysBioXMLParser.java");
        }
    }

   public void loadRepositoryFromGraphXML(String graphXML) {
          try {

            String result=graphXML.replaceAll("&#x","---");
            Document document = builder.parse(new InputSource(new StringReader(result)));

            // SBModel is sign of SystemBiology XML document, which equals to GraphData of PathCase.
            NodeList graphroots = document.getElementsByTagName("SBModel");
     
            Node graphroot = graphroots.item(0);

            NodeList graphElements = graphroot.getChildNodes();
            for (int i = 0; i < graphElements.getLength(); i++) {
                Node graphElement = graphElements.item(i);

                // GraphData/Molecules
                if (graphElement.getNodeName().equals("Model")) {
                    loadModelFromXMLNode(graphElement);
                }
                // GraphData/GenericProcesses
                else if (graphElement.getNodeName().equals("Compartments")) {
                    loadCompartmentFromXMLNode(graphElement);
                }
//                // GraphData/Pathways
                else if (graphElement.getNodeName().equals("Reactions")) {
                    loadReactionsFromXMLNode(graphElement);
                }
                else if (graphElement.getNodeName().equals("Pathways")) {
                    loadMappingPWsFromXMLNode(graphElement);
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

    private void loadMappingPWsFromXMLNode(Node graphElement) {
        NodeList pwnodes = graphElement.getChildNodes();
        if(pwnodes!=null){
            for (int i = 0; i < pwnodes.getLength(); i++) {
                Node pwnode = pwnodes.item(i);
                // GraphData/GenericProcesses/GenericProcess
                if (pwnode.getNodeName().equals("Pathway")) {
                    NamedNodeMap pwAttributes = pwnode.getAttributes();

                    String pwId = getNodeStringValue(pwAttributes.getNamedItem("ID"));
                    String pwName = getNodeStringValue(pwAttributes.getNamedItem("Name"));
    
                    repository.mappingPathwayTable.insertRow(pwId,pwName,false,false);

                }// GraphData/GenericProcesses/GenericProcess
            }
        }
    }

     //biopax GraphData/Molecules
    private void loadModelFromXMLNode(Node graphElement) {
                NamedNodeMap modelAttributes =graphElement.getAttributes();
                String modelId = getNodeStringValue(modelAttributes.getNamedItem("ID"));
                String modelName = getNodeStringValue(modelAttributes.getNamedItem("Name"));

                repository.modelTable.insertRow(modelId,modelName);
    }

   private void loadCompartmentFromXMLNode(Node graphElement) {
        NodeList compartmentnodes = graphElement.getChildNodes();
        for (int i = 0; i < compartmentnodes.getLength(); i++) {
            Node compartmentnode = compartmentnodes.item(i);
            // GraphData/GenericProcesses/GenericProcess
            if (compartmentnode.getNodeName().equals("Compartment")) {
                NamedNodeMap compartmentAttributes = compartmentnode.getAttributes();

                String compartmentId = getNodeStringValue(compartmentAttributes.getNamedItem("ID"));
                String compartmentName = getNodeStringValue(compartmentAttributes.getNamedItem("Name"));
                String sbmlID = getNodeStringValue(compartmentAttributes.getNamedItem("sbmlID"));
                String size = getNodeStringValue(compartmentAttributes.getNamedItem("Size"));
                String spatialDimensions = getNodeStringValue(compartmentAttributes.getNamedItem("SpatialDimensions"));
                //boolean isConstant = getNodeBooleanValue(compartmentAttributes.getNamedItem("isConstant"));
                String isConstantString = getNodeStringValue(compartmentAttributes.getNamedItem("Constant"));
                boolean isConstant = Boolean.parseBoolean(isConstantString);
                String compartmentType = getNodeStringValue(compartmentAttributes.getNamedItem("CompartmentType"));
                String outsideID = getNodeStringValue(compartmentAttributes.getNamedItem("Outside"));

                repository.compartmentsTable.insertRow(compartmentId,compartmentName,sbmlID,size,spatialDimensions,isConstant,compartmentType,outsideID);

                NodeList compartmentchildren = compartmentnode.getChildNodes();

                for (int j = 0; j < compartmentchildren.getLength(); j++) {
                    Node compartmentchild = compartmentchildren.item(j);

                    if (compartmentchild.getNodeName().equals("SpeciesAll")) {
                        loadCompartmentToSpeciesTable(compartmentId, compartmentchild);
                    }
                }    
            }// GraphData/GenericProcesses/GenericProcess
        }
    }
    
   private void loadCompartmentToSpeciesTable(String compartmentId, Node compartmentchild) {
        NodeList SpeciesNodelist = compartmentchild.getChildNodes();
        for (int j = 0; j < SpeciesNodelist.getLength(); j++) {
            Node SpeciesNode = SpeciesNodelist.item(j);

            // GraphData/GenericProcesses/GenericProcess/Molecules/Molecule
            if (SpeciesNode.getNodeName().equals("Species")) {
                NamedNodeMap speciesAttributes = SpeciesNode.getAttributes();

                String SpeciesID = getNodeStringValue(speciesAttributes.getNamedItem("ID"));
                String SpeciesName = getNodeStringValue(speciesAttributes.getNamedItem("Name"));
                String sbmlID = getNodeStringValue(speciesAttributes.getNamedItem("sbmlID"));
                String SpeciesTypeId = getNodeStringValue(speciesAttributes.getNamedItem("SpeciesTypeId"));
                String InitialAmount = getNodeStringValue(speciesAttributes.getNamedItem("InitialAmount"));
                String InitialConcentration = getNodeStringValue(speciesAttributes.getNamedItem("InitialConcentration"));
                String SubstanceUnitsId = getNodeStringValue(speciesAttributes.getNamedItem("SubstanceUnitsId"));
                String HasOnlySubstanceUnitsString = getNodeStringValue(speciesAttributes.getNamedItem("HasOnlySubstanceUnits"));
                boolean HasOnlySubstanceUnits = Boolean.parseBoolean(HasOnlySubstanceUnitsString);
                String BoundaryConditionString = getNodeStringValue(speciesAttributes.getNamedItem("BoundaryCondition"));
                boolean BoundaryCondition = Boolean.parseBoolean(BoundaryConditionString);
                String Charge = getNodeStringValue(speciesAttributes.getNamedItem("Charge"));
                String isConstantString = getNodeStringValue(speciesAttributes.getNamedItem("Constant"));
                boolean isConstant = Boolean.parseBoolean(isConstantString);
                String isCommonString = getNodeStringValue(speciesAttributes.getNamedItem("IsCommon"));
                boolean isCommon = Boolean.parseBoolean(isCommonString);

                repository.speciesTable.insertRow(SpeciesID,SpeciesName,sbmlID,InitialAmount,SpeciesTypeId,InitialConcentration, SubstanceUnitsId,HasOnlySubstanceUnits,BoundaryCondition,Charge,isConstant,isCommon);
                repository.compartmentToSpeciesTable.insertRow(compartmentId, SpeciesID);
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
                String reactionName = getNodeStringValue(reactionAttributes.getNamedItem("Name"));
                String sbmlId = getNodeStringValue(reactionAttributes.getNamedItem("sbmlId"));
                String isReversibleString = getNodeStringValue(reactionAttributes.getNamedItem("Reversible"));
                boolean isReversible  = Boolean.parseBoolean(isReversibleString);
                String strKineticLawId = getNodeStringValue(reactionAttributes.getNamedItem("KineticLawId"));
                String KineticLawId=strKineticLawId.replaceAll("---","&#x");
                String isFastString = getNodeStringValue(reactionAttributes.getNamedItem("Fast"));
                boolean isFast  = Boolean.parseBoolean(isFastString);

                repository.reactionsTable.insertRow(reactionId,reactionName,sbmlId,isReversible,KineticLawId,isFast);

                NodeList reactionchildren = reactionnode.getChildNodes();

                for (int j = 0; j < reactionchildren.getLength(); j++) {
                    Node reactionchild = reactionchildren.item(j);

                    if (reactionchild.getNodeName().equals("ReactionSpeciesAll")) {
                        loadReactionToReactionSpeciesTable(reactionId, reactionchild);
                    }
                }
            }// GraphData/GenericProcesses/GenericProcess
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
