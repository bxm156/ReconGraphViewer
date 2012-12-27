package binevi.Parser.PathCaseParsers;

import binevi.Resources.PathCaseResources.PathCaseRepository;
import binevi.Resources.PathCaseResources.OrganismTable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;


public class PathCaseXMLParser {

    PathCaseRepository repository;

    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;

    public PathCaseXMLParser(PathCaseRepository repository) {
        this.repository = repository;

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            //e.printStackTrace();
            System.out.println("ERROR: ParserConfigurationException, check PathCaseXMLParser.java");
        }
    }

    //biopax graphdata
    public void loadRepositoryFromBioPAXGraphXML(String graphXML) {
    //System.out.println(graphXML);
    try {
        Document document = builder.parse(new InputSource(new StringReader(graphXML)));

        //  GraphData
        NodeList graphroots = document.getElementsByTagName("graphdata");
        Node graphroot = graphroots.item(0);
        NodeList graphElements = graphroot.getChildNodes();
        for (int i = 0; i < graphElements.getLength(); i++) {
            Node graphElement = graphElements.item(i);

            // GraphData/Molecules
            if (graphElement.getNodeName().equals("molecules")) {
                loadBioPAXMoleculesFromXMLNode(graphElement);
            }
            // GraphData/GenericProcesses
            else if (graphElement.getNodeName().equals("genericprocesses")) {
                loadBioPAXGenericProcessesFromXMLNode(graphElement);
            }
            // GraphData/Pathways
            else if (graphElement.getNodeName().equals("pathways")) {
                loadBioPAXPathwaysFromXMLNode(graphElement);
            }
            else if (graphElement.getNodeName().equals("Mappings")) {
                loadBioPAXMappingsFromXMLNode(graphElement);
            }
        }

    } catch (SAXException sxe) {
        // Error generated during parsing
        Exception x = sxe;
        if (sxe.getException() != null)
            x = sxe.getException();
        //x.printStackTrace();
        System.out.println("ERROR: SAXException, check PathCaseXMLParser.java");

    } catch (IOException ioe) {
        // I/O error
        //ioe.printStackTrace();
        System.out.println("ERROR: IOException, check PathCaseXMLParser.java");
    }
}


    private void loadBioPAXMappingsFromXMLNode(Node graphElement) {
        NodeList moleculenodes = graphElement.getChildNodes();
        for (int i = 0; i < moleculenodes.getLength(); i++) {
                Node spesmapnode = moleculenodes.item(i);
            // GraphData/Molecules/Molecule
                if (spesmapnode.getNodeName().equals("SpeciesMappings")) {
                    NodeList spesmapnodehildren = spesmapnode.getChildNodes();
                    for (int j = 0; j < spesmapnodehildren.getLength(); j++) {
                        Node specNode = spesmapnodehildren.item(j);
                        if (specNode != null && specNode .getNodeName().equalsIgnoreCase("Species")) {
                            NamedNodeMap specAttributes = specNode.getAttributes();
                            repository.modelPathwayElementsMappingTable.insertSpeMoleRow(getNodeStringValue(specAttributes.getNamedItem("ID")),getNodeStringValue(specAttributes.getNamedItem("AnnotationQualifier")),getNodeStringValue(specAttributes.getNamedItem("MolecularEntityID")));
                        }
                    }
                } else if (spesmapnode.getNodeName().equals("ReactionsMappings")) {
                    NodeList reacmapnodehildren = spesmapnode.getChildNodes();
                    for (int j = 0; j < reacmapnodehildren.getLength(); j++) {
                        Node specNode = reacmapnodehildren.item(j);
                        if (specNode != null && specNode .getNodeName().equalsIgnoreCase("Reaction")) {
                            NamedNodeMap specAttributes = specNode.getAttributes();
                            repository.modelPathwayElementsMappingTable.insertReacProcRow(getNodeStringValue(specAttributes.getNamedItem("ID")),getNodeStringValue(specAttributes.getNamedItem("AnnotationQualifier")),getNodeStringValue(specAttributes.getNamedItem("ProcessID")));
                        }
                }
            }
        }
    }

    //biopax GraphData/Molecules
    private void loadBioPAXMoleculesFromXMLNode(Node graphElement) {
        NodeList moleculenodes = graphElement.getChildNodes();
        for (int i = 0; i < moleculenodes.getLength(); i++) {
            Node moleculenode = moleculenodes.item(i);
            // GraphData/Molecules/Molecule
            if (moleculenode.getNodeName().equals("molecule")) {
                NamedNodeMap moleculeAttributes = moleculenode.getAttributes();
                String MoleculeId = getNodeStringValue(moleculeAttributes.getNamedItem("id"));
                String MoleculeName = getNodeStringValue(moleculeAttributes.getNamedItem("name"));

                Node isCommonNode = moleculeAttributes.getNamedItem("iscommon");
                String IsCommonString = "false";
                if (isCommonNode != null)
                    IsCommonString = getNodeStringValue(isCommonNode);
                boolean isCommon = Boolean.parseBoolean(IsCommonString);

                String MoleculeType = getNodeStringValue(moleculeAttributes.getNamedItem("type"));
                //smallmolecule/protein/all others
                repository.moleculesTable.insertRow(MoleculeId, "N/A", MoleculeName, isCommon);
            }
        }
    }

    //biopax GraphData/Pathways
    private void loadBioPAXPathwaysFromXMLNode(Node graphElement) {

        NodeList pathwaysnodes = graphElement.getChildNodes();
        for (int i = 0; i < pathwaysnodes.getLength(); i++) {
            Node pathwaynode = pathwaysnodes.item(i);
            // GraphData/Pathways/Pathway
            if (pathwaynode.getNodeName().equals("pathway")) {
                NamedNodeMap pathwayAttributes = pathwaynode.getAttributes();
                String PathwayId = getNodeStringValue(pathwayAttributes.getNamedItem("id"));
                String PathwayName = getNodeStringValue(pathwayAttributes.getNamedItem("name"));
                String IsExpandedString = getNodeStringValue(pathwayAttributes.getNamedItem("expanded"));
                boolean IsExpanded = Boolean.parseBoolean(IsExpandedString);
                repository.pathwayTable.insertRow(PathwayId, PathwayName, IsExpanded,false);

                NodeList pathwaychildren = pathwaynode.getChildNodes();

                // GraphData/Pathways/Pathway/...
                for (int j = 0; j < pathwaysnodes.getLength(); j++) {
                    Node pathwayContentNode = pathwaychildren.item(j);
                    if (pathwayContentNode != null && pathwayContentNode.getNodeName().equalsIgnoreCase("genericprocesses")) {
                        loadBioPAXPathwayToGenericProcessesTable(PathwayId, pathwayContentNode);
                    }
                    /*else
                    if (pathwayContentNode != null && pathwayContentNode.getNodeName().equalsIgnoreCase("LinkingMolecules")) {
                        loadPathwayToLinkingMoleculesTable(PathwayId, pathwayContentNode);
                    } else
                    if (pathwayContentNode != null && pathwayContentNode.getNodeName().equalsIgnoreCase("OrganismGroups")) {
                        loadPathwayToOrganismGroupsTable(PathwayId, pathwayContentNode);
                    }*/
                }
            }// GraphData/Pathways/Pathway
        }

    }

    //biopax GraphData/Pathways/Pathway/GenericProcesses
    private void loadBioPAXPathwayToGenericProcessesTable(String pathwayId, Node genericProcessesNode) {
        NodeList genericProcessesNodelist = genericProcessesNode.getChildNodes();
        for (int j = 0; j < genericProcessesNodelist.getLength(); j++) {
            Node genericProcessNode = genericProcessesNodelist.item(j);

            // GraphData/Pathways/Pathway/GenericProcesses/GenericProcess
            if (genericProcessNode.getNodeName().equalsIgnoreCase("genericprocess")) {
                NamedNodeMap pathwayGenericProcessAttributes = genericProcessNode.getAttributes();
                String genericProcessId = getNodeStringValue(pathwayGenericProcessAttributes.getNamedItem("id"));
                repository.pathwayToGenericProcessesTable.insertRow(pathwayId, genericProcessId);
            }
        }
    }

    class BioPaxCatalyzesData {
        String id="";
        boolean reversible=false;
        String geneproductmoleculeid="";
        String processid="";

        public BioPaxCatalyzesData (String id, boolean reversible,String geneproductmoleculeid, String processid)
        {
            this.id = id;
            this.reversible = reversible;
            this.geneproductmoleculeid = geneproductmoleculeid;
            this.processid = processid;
        }
    }

    class BioPaxReactionData {

        public String processid;
        public String ecnumber;
        public String name;

        public ArrayList<String> moleculeids;
        public ArrayList<String> moleculeroles;

        public BioPaxReactionData ()
        {
            moleculeids = new ArrayList<String>();
            moleculeroles = new ArrayList<String>();
        }
    }

    //biopax GraphData/GenericProcesses
    private void loadBioPAXGenericProcessesFromXMLNode(Node graphElement) {

        NodeList genericprocessnodes = graphElement.getChildNodes();
        for (int i = 0; i < genericprocessnodes.getLength(); i++) {
            Node genericprocessnode = genericprocessnodes.item(i);
            // GraphData/GenericProcesses/GenericProcess
            if (genericprocessnode.getNodeName().equals("genericprocess")) {
                NamedNodeMap genericProcessAttributes = genericprocessnode.getAttributes();
                String genericProcessId = getNodeStringValue(genericProcessAttributes.getNamedItem("id"));
                boolean isreversibleany = false;
                String genericProcessName = null;

                ArrayList<BioPaxCatalyzesData> enzymeslist = new ArrayList<BioPaxCatalyzesData>();
                Hashtable<String,BioPaxReactionData> reactionlist = new Hashtable<String,BioPaxReactionData>();

                NodeList genericProcesschildren = genericprocessnode.getChildNodes();

                for (int j = 0; j < genericProcesschildren.getLength(); j++) {
                    Node genericProcesschild = genericProcesschildren.item(j);

                    // GraphData/GenericProcesses/GenericProcess/Catalyzes
                    if (genericProcesschild.getNodeName().equals("catalyzes")) {
                        NodeList CatalyzeChildren = genericProcesschild.getChildNodes();
                        for (int k = 0; k < CatalyzeChildren.getLength(); k++) {
                            Node CatalyzeChild = CatalyzeChildren.item(k);

                            // GraphData/GenericProcesses/GenericProcess/Catalyzes/Catalyze
                            if (CatalyzeChild.getNodeName().equals("catalyze")) {

                                NamedNodeMap catalyzesAttributes = CatalyzeChild.getAttributes();
                                String catalyzesId = getNodeStringValue(catalyzesAttributes.getNamedItem("id"));

                                NodeList CatalyzeSubChildren = CatalyzeChild.getChildNodes();

                                String IsReversibleString=null;
                                String geneproductmoleculeid=null;
                                String processid=null;


                                for (int l = 0; l < CatalyzeSubChildren.getLength(); l++) {
                                    Node EnzymeChild = CatalyzeSubChildren.item(l);

                                    // GraphData/GenericProcesses/GenericProcess/Catalyzes/Catalyze/processid
                                    if (EnzymeChild.getNodeName().equals("processid")) {
                                        NodeList enzymesubchildren = EnzymeChild.getChildNodes();
                                        if (enzymesubchildren!=null && enzymesubchildren.getLength()>=1)
                                            processid = getNodeStringValue(enzymesubchildren.item(0));
                                    }
                                    // GraphData/GenericProcesses/GenericProcess/Catalyzes/Catalyze/geneproductmoleculeid
                                    else if (EnzymeChild.getNodeName().equals("geneproductmoleculeid")) {
                                        NodeList enzymesubchildren = EnzymeChild.getChildNodes();
                                        if (enzymesubchildren!=null && enzymesubchildren.getLength()>=1)
                                            geneproductmoleculeid = getNodeStringValue(enzymesubchildren.item(0));
                                    }
                                    // GraphData/GenericProcesses/GenericProcess/Catalyzes/Catalyze/reversible
                                    else if (EnzymeChild.getNodeName().equals("reversible")) {
                                        NodeList enzymesubchildren = EnzymeChild.getChildNodes();
                                        if (enzymesubchildren!=null && enzymesubchildren.getLength()>=1)
                                            IsReversibleString = getNodeStringValue(enzymesubchildren.item(0));
                                    }
                                }

                                boolean IsReversible = (IsReversibleString != null) && IsReversibleString.equalsIgnoreCase("reversible");
                                //if any reaction is reversible, whole process is defined as reversible
                                isreversibleany = isreversibleany || IsReversible;
                                geneproductmoleculeid= (geneproductmoleculeid!=null)?geneproductmoleculeid:"";
                                processid= (processid!=null)?processid:"";

                                BioPaxCatalyzesData data = new BioPaxCatalyzesData(catalyzesId,IsReversible,geneproductmoleculeid,processid);
                                enzymeslist.add(data);
                            }
                        }
                    }
                    // GraphData/GenericProcesses/GenericProcess/biochemicalreaction
                    else if (genericProcesschild.getNodeName().equals("biochemicalreaction")) {

                        BioPaxReactionData data = new BioPaxReactionData();

                        NamedNodeMap reactionAttributes = genericProcesschild.getAttributes();
                        if (reactionAttributes.getNamedItem("id")==null)
                            continue;
                        data.processid = getNodeStringValue(reactionAttributes.getNamedItem("id"));

                        NodeList ReactionChildren = genericProcesschild.getChildNodes();

                        for (int k = 0; k < ReactionChildren.getLength(); k++) {
                            Node ReactionChild = ReactionChildren.item(k);

                            // GraphData/GenericProcesses/GenericProcess/biochemicalreaction/ecnumber
                            if (ReactionChild.getNodeName().equals("ecnumber")) {
                                NodeList reactionssubchildren = ReactionChild.getChildNodes();
                                if (reactionssubchildren!=null && reactionssubchildren.item(0)!=null)
                                    data.ecnumber = getNodeStringValue(reactionssubchildren.item(0));
                            }
                            // GraphData/GenericProcesses/GenericProcess/biochemicalreaction/name
                            else if (ReactionChild.getNodeName().equals("name")) {
                                NodeList reactionssubchildren = ReactionChild.getChildNodes();
                                if (reactionssubchildren!=null){
                                    if (reactionssubchildren.getLength()>0)
                                        data.name = getNodeStringValue(reactionssubchildren.item(0));
                                    else
                                        data.name = "";
                                    //an arbitrary reaction name is chosen as the generic process name
                                    if (genericProcessName==null) genericProcessName = data.name;
                                }
                            }
                            // GraphData/GenericProcesses/GenericProcess/biochemicalreaction/molecule
                            else if (ReactionChild.getNodeName().equals("molecule")) {

                                NodeList MoleculeChildren = ReactionChild.getChildNodes();
                                for (int l = 0; l < MoleculeChildren.getLength(); l++) {
                                    Node MoleculeChild = MoleculeChildren.item(l);

                                    // GraphData/GenericProcesses/GenericProcess/biochemicalreaction/molecule/id
                                    if (MoleculeChild.getNodeName().equals("id")) {
                                        NodeList moleculesubchildren = MoleculeChild.getChildNodes();
                                        if (moleculesubchildren!=null)
                                            data.moleculeids.add(getNodeStringValue(moleculesubchildren.item(0)));
                                    }
                                    // GraphData/GenericProcesses/GenericProcess/biochemicalreaction/molecule/role
                                    else if (MoleculeChild.getNodeName().equals("role")) {
                                        NodeList moleculesubchildren = MoleculeChild.getChildNodes();
                                        if (moleculesubchildren!=null){
                                            String roleString = getNodeStringValue(moleculesubchildren.item(0));

                                            //remap to pathcase roles
                                            if (roleString.equalsIgnoreCase("SUBSTRATE")) roleString = "substrate";
                                            else if (roleString.equalsIgnoreCase("PRODUCT")) roleString = "product";
                                            else if (roleString.equalsIgnoreCase("INHIBITION")) roleString = "inhibitor";
                                            else if (roleString.equalsIgnoreCase("ACTIVATION")) roleString = "activator";

                                            data.moleculeroles.add(roleString);
                                        }
                                    }
                                }
                            }
                        }
                        reactionlist.put(data.processid,data);
                    }
                }

                repository.genericProcessTable.insertRow(genericProcessId, "N/A", genericProcessName, isreversibleany);
                for (BioPaxCatalyzesData enzyme:enzymeslist)
                {
                    BioPaxReactionData correspondingReaction = reactionlist.get(enzyme.processid);
                    if (correspondingReaction==null) continue;
                    repository.genericProcessToCatalyzesTable.insertRow(genericProcessId, enzyme.processid, OrganismTable.ROOTID, correspondingReaction.ecnumber, enzyme.geneproductmoleculeid);
                    repository.genericProcessToECNumbersTable.insertRow(genericProcessId, correspondingReaction.ecnumber);
                    repository.genericProcessToEnzmyesTable.insertRow(genericProcessId, enzyme.geneproductmoleculeid);
                }

                for (BioPaxReactionData reaction:reactionlist.values())
                {
                    for (int m=0;m<reaction.moleculeids.size();m++)
                    {
                        repository.specificProcessToMoleculesTable.insertRow(reaction.processid, reaction.moleculeids.get(m), reaction.moleculeroles.get(m));
                        repository.genericProcessToMoleculesTable.insertRow(genericProcessId, reaction.moleculeids.get(m), reaction.moleculeroles.get(m));
                    }
                }
            }// GraphData/GenericProcesses/GenericProcess
        }


    }//method

    ////////////////////////////////////////////////////////////////////////////////////

    // GraphData
    public void loadRepositoryFromGraphXML(String graphXML) {
        //System.out.println(graphXML);
        try {
            Document document = builder.parse(new InputSource(new StringReader(graphXML)));

            //  GraphData
            NodeList graphroots = document.getElementsByTagName("GraphData");
            Node graphroot = graphroots.item(0);
            //TODO: may take forever...
            NodeList graphElements = graphroot.getChildNodes();
            for (int i = 0; i < graphElements.getLength(); i++) {
                Node graphElement = graphElements.item(i);

                // GraphData/Molecules
                if (graphElement.getNodeName().equals("Molecules")) {
                    loadMoleculesFromXMLNode(graphElement);
                }
                // GraphData/GenericProcesses
                else if (graphElement.getNodeName().equals("GenericProcesses")) {
                    loadGenericProcessesFromXMLNode(graphElement);
                }
                // GraphData/Pathways
                else if (graphElement.getNodeName().equals("Pathways")) {
                    loadPathwaysFromXMLNode(graphElement);
                }
                else if (graphElement.getNodeName().equals("Mappings")) {
                    loadBioPAXMappingsFromXMLNode(graphElement);
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

    // GraphData/Molecules
    private void loadMoleculesFromXMLNode(Node graphElement) {
        NodeList moleculenodes = graphElement.getChildNodes();
        for (int i = 0; i < moleculenodes.getLength(); i++) {
            Node moleculenode = moleculenodes.item(i);
            // GraphData/Molecules/Molecule
            if (moleculenode.getNodeName().equals("Molecule")) {
                NamedNodeMap moleculeAttributes = moleculenode.getAttributes();
                String MoleculeId = getNodeStringValue(moleculeAttributes.getNamedItem("ID"));
                String MoleculeEntityId = getNodeStringValue(moleculeAttributes.getNamedItem("EntityID"));
                String MoleculeName = getNodeStringValue(moleculeAttributes.getNamedItem("Name"));

                Node isCommonNode = moleculeAttributes.getNamedItem("IsCommon");
                String IsCommonString = "False";
                if (isCommonNode != null)
                    IsCommonString = getNodeStringValue(isCommonNode);
                boolean isCommon = Boolean.parseBoolean(IsCommonString);
                repository.moleculesTable.insertRow(MoleculeId, MoleculeEntityId, MoleculeName, isCommon);
            }
        }
    }

    // GraphData/Pathways
    private void loadPathwaysFromXMLNode(Node graphElement) {

        NodeList pathwaysnodes = graphElement.getChildNodes();
        for (int i = 0; i < pathwaysnodes.getLength(); i++) {
            Node pathwaynode = pathwaysnodes.item(i);
            // GraphData/Pathways/Pathway
            if (pathwaynode.getNodeName().equals("Pathway")) {
                NamedNodeMap pathwayAttributes = pathwaynode.getAttributes();
                String PathwayId = getNodeStringValue(pathwayAttributes.getNamedItem("ID"));
                String PathwayName = getNodeStringValue(pathwayAttributes.getNamedItem("Name"));
                String IsExpandedString = getNodeStringValue(pathwayAttributes.getNamedItem("Expanded"));
                boolean IsExpanded = Boolean.parseBoolean(IsExpandedString);
                String IsLinkingString = getNodeStringValue(pathwayAttributes.getNamedItem("Linking"));
                boolean IsLinking = Boolean.parseBoolean(IsLinkingString);

                repository.pathwayTable.insertRow(PathwayId, PathwayName, IsExpanded, IsLinking);

                NodeList pathwaychildren = pathwaynode.getChildNodes();

                // GraphData/Pathways/Pathway/...
                for (int j = 0; j < pathwaysnodes.getLength(); j++) {
                    Node pathwayContentNode = pathwaychildren.item(j);
                    if (pathwayContentNode != null && pathwayContentNode.getNodeName().equals("GenericProcesses")) {
                        loadPathwayToGenericProcessesTable(PathwayId, pathwayContentNode);
                    } else
                    if (pathwayContentNode != null && pathwayContentNode.getNodeName().equals("LinkingPathways")) {
                        loadPathwayToLinkingMoleculesTable(PathwayId, pathwayContentNode);
                    } else
                    if (pathwayContentNode != null && pathwayContentNode.getNodeName().equals("OrganismGroups")) {
                        loadPathwayToOrganismGroupsTable(PathwayId, pathwayContentNode);
                    }
                }
            }// GraphData/Pathways/Pathway
        }

    }

    // GraphData/Pathways/Pathway/OrganismGroups
    private void loadPathwayToOrganismGroupsTable(String pathwayId, Node organismGroupsNode) {
        NodeList organismGroupList = organismGroupsNode.getChildNodes();
        for (int j = 0; j < organismGroupList.getLength(); j++) {
            Node organismGroupNode = organismGroupList.item(j);

            // GraphData/Pathways/Pathway/LinkingMolecules/LinkingMolecule
            if (organismGroupNode.getNodeName().equals("OrganismGroup")) {
                NamedNodeMap organismGroupAttributes = organismGroupNode.getAttributes();
                String organismGroupId = getNodeStringValue(organismGroupAttributes.getNamedItem("ID"));
                //if (organismGroupId.trim().equals("")) organismGroupId = OrganismTable.UNKNOWNID;
                repository.pathwayToOrganismGroupsTable.insertRow(pathwayId, organismGroupId);
            }
        }
    }

    private String getNodeStringValue(Node node)
    {
        if (node == null) return "UNKNOWN_DATA";
        else return node.getNodeValue().trim();
    }

    // GraphData/Pathways/Pathway/LinkingMolecules
    private void loadPathwayToLinkingMoleculesTable(String pathwayId, Node genericProcessesNode) {
        NodeList linkingPathwaysList = genericProcessesNode.getChildNodes();
        for (int j = 0; j < linkingPathwaysList.getLength(); j++) {
            Node linkingPathwayNode = linkingPathwaysList.item(j);

            // GraphData/Pathways/Pathway/LinkingMolecules/LinkingMolecule
            if (linkingPathwayNode.getNodeName().equals("LinkingPathway")) {
                NamedNodeMap pathwayLinkingPathwayAttributes = linkingPathwayNode.getAttributes();
                String linkingPathwayId = getNodeStringValue(pathwayLinkingPathwayAttributes.getNamedItem("ID"));
                //String linkingPathwayName = getNodeStringValue(pathwayLinkingPathwayAttributes.getNamedItem("Name"));
                String linkingDirection = getNodeStringValue(pathwayLinkingPathwayAttributes.getNamedItem("Dir"));
                //repository.pathwayTable.insertRow(linkingPathwayId, linkingPathwayName, false, true);

                NodeList linkingMoleculesList = linkingPathwayNode.getChildNodes();
                for (int k = 0; k < linkingMoleculesList.getLength(); k++) {
                    Node linkingMoleculeNode = linkingMoleculesList.item(k);

                    if (linkingMoleculeNode.getNodeName().equals("LinkingMolecule")) {
                        NamedNodeMap pathwayLinkingMoleculeAttributes = linkingMoleculeNode.getAttributes();
                        String linkingMoleculeId = getNodeStringValue(pathwayLinkingMoleculeAttributes.getNamedItem("ID"));

                        repository.pathwayToLinkingMoleculesTable.insertRow(pathwayId, linkingPathwayId, linkingMoleculeId, linkingDirection.equalsIgnoreCase("in"));
                        //System.out.println(pathwayId+" "+ linkingPathwayId+" "+ linkingMoleculeId);
                    }

                }

            }
        }
    }

    // GraphData/Pathways/Pathway/GenericProcesses
    private void loadPathwayToGenericProcessesTable(String pathwayId, Node genericProcessesNode) {
        NodeList genericProcessesNodelist = genericProcessesNode.getChildNodes();
        for (int j = 0; j < genericProcessesNodelist.getLength(); j++) {
            Node genericProcessNode = genericProcessesNodelist.item(j);

            // GraphData/Pathways/Pathway/GenericProcesses/GenericProcess
            if (genericProcessNode.getNodeName().equalsIgnoreCase("GenericProcess")) {
                NamedNodeMap pathwayGenericProcessAttributes = genericProcessNode.getAttributes();
                String genericProcessId = getNodeStringValue(pathwayGenericProcessAttributes.getNamedItem("ID"));
                repository.pathwayToGenericProcessesTable.insertRow(pathwayId, genericProcessId);
            }
        }
    }

    // GraphData/GenericProcesses
    private void loadGenericProcessesFromXMLNode(Node graphElement) {

        NodeList genericprocessnodes = graphElement.getChildNodes();
        for (int i = 0; i < genericprocessnodes.getLength(); i++) {
            Node genericprocessnode = genericprocessnodes.item(i);
            // GraphData/GenericProcesses/GenericProcess
            if (genericprocessnode.getNodeName().equals("GenericProcess")) {
                NamedNodeMap genericProcessAttributes = genericprocessnode.getAttributes();
                String genericProcessId = getNodeStringValue(genericProcessAttributes.getNamedItem("ID"));
                String genericProcessEntityId = getNodeStringValue(genericProcessAttributes.getNamedItem("GenericProcessID"));
                String genericProcessName = getNodeStringValue(genericProcessAttributes.getNamedItem("Name"));
                String IsReversibleString = getNodeStringValue(genericProcessAttributes.getNamedItem("Reversible"));
                boolean IsReversible = Boolean.parseBoolean(IsReversibleString);
                repository.genericProcessTable.insertRow(genericProcessId, genericProcessEntityId,genericProcessName, IsReversible);

                NodeList genericProcesschildren = genericprocessnode.getChildNodes();

                for (int j = 0; j < genericProcesschildren.getLength(); j++) {
                    Node genericProcesschild = genericProcesschildren.item(j);

                    // GraphData/GenericProcesses/GenericProcess/Catalyzes
                    if (genericProcesschild.getNodeName().equals("Catalyzes")) {
                        loadGenericProcessCatalyzesTable(genericProcessId, genericProcesschild);
                    }
                    // GraphData/GenericProcesses/GenericProcess/Molecules
                    else if (genericProcesschild.getNodeName().equals("Molecules")) {
                        loadProcessToMoleculesTable(genericProcessId, genericProcesschild);
                    }
                }

            }// GraphData/GenericProcesses/GenericProcess
        }


    }//method

    // GraphData/GenericProcesses/GenericProcess/Molecules
    private void loadProcessToMoleculesTable(String genericProcessId, Node genericProcesschild) {
        NodeList MoleculeNodelist = genericProcesschild.getChildNodes();
        for (int j = 0; j < MoleculeNodelist.getLength(); j++) {
            Node MoleculeNode = MoleculeNodelist.item(j);

            // GraphData/GenericProcesses/GenericProcess/Molecules/Molecule
            if (MoleculeNode.getNodeName().equals("Molecule")) {
                NamedNodeMap moleculeAttributes = MoleculeNode.getAttributes();
                String moleculeId = getNodeStringValue(moleculeAttributes.getNamedItem("ID"));
                String specificProcessId = getNodeStringValue(moleculeAttributes.getNamedItem("ProcessID"));
                String moleculeRole = getNodeStringValue(moleculeAttributes.getNamedItem("Role"));

                repository.specificProcessToMoleculesTable.insertRow(specificProcessId, moleculeId, moleculeRole);
                repository.genericProcessToMoleculesTable.insertRow(genericProcessId, moleculeId, moleculeRole);
            }
        }
    }

    // GraphData/GenericProcesses/GenericProcess/Catalyzes
    private void loadGenericProcessCatalyzesTable(String genericProcessId, Node genericProcesschild) {
        NodeList SpecificProcessNodeList = genericProcesschild.getChildNodes();
        for (int j = 0; j < SpecificProcessNodeList.getLength(); j++) {
            Node SpecificProcessNode = SpecificProcessNodeList.item(j);

            // GraphData/GenericProcesses/GenericProcess/Catalyzes/Catalyze
            if (SpecificProcessNode.getNodeName().equals("Catalyze")) {
                NamedNodeMap moleculeAttributes = SpecificProcessNode.getAttributes();
                String specificProcessId = getNodeStringValue(moleculeAttributes.getNamedItem("ProcessID"));
                String organismGroupId = getNodeStringValue(moleculeAttributes.getNamedItem("OrganismGroupID"));
                String ECNumber = getNodeStringValue(moleculeAttributes.getNamedItem("ECNumber"));
                String GeneProductMoleculeID = getNodeStringValue(moleculeAttributes.getNamedItem("GeneProductMoleculeID"));
                repository.genericProcessToCatalyzesTable.insertRow(genericProcessId, specificProcessId, organismGroupId, ECNumber, GeneProductMoleculeID);
                repository.genericProcessToECNumbersTable.insertRow(genericProcessId, ECNumber);
                repository.genericProcessToEnzmyesTable.insertRow(genericProcessId, GeneProductMoleculeID);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // OrganismHierarchy
    public void loadRepositoryFromOrganismXML(String organismXML) {
        try {
            Document document = builder.parse(new InputSource(new StringReader(organismXML)));

            //  OrganismHierarchy
            NodeList graphroots = document.getElementsByTagName("OrganismHierarchy");
            Node graphroot = graphroots.item(0);
            NodeList graphElements = graphroot.getChildNodes();
            for (int i = 0; i < graphElements.getLength(); i++) {
                Node graphElement = graphElements.item(i);

                // OrganismHierarchy/OrganismGroup
                if (graphElement.getNodeName().equals("OrganismGroup")) {
                    loadOrganismGroupFromXMLNode(null, graphElement);
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
            //ioe.printStackTrace();
        }
    }

    private void loadOrganismGroupFromXMLNode(String parentId, Node graphElement) {
        NamedNodeMap organismAttributes = graphElement.getAttributes();
        String organismGroupId = getNodeStringValue(organismAttributes.getNamedItem("id"));
        String organismCommonName = getNodeStringValue(organismAttributes.getNamedItem("commonName"));
        String organismScientificName = getNodeStringValue(organismAttributes.getNamedItem("scientificName"));

        repository.organismTable.insertRow(parentId, organismGroupId, organismCommonName, organismScientificName);

        NodeList graphElements = graphElement.getChildNodes();
        for (int i = 0; i < graphElements.getLength(); i++) {
            Node graphChild = graphElements.item(i);
            // OrganismGroup/OrganismGroup
            if (graphChild.getNodeName().equals("OrganismGroup")) {
                loadOrganismGroupFromXMLNode(organismGroupId, graphChild);
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadRepositoryFromGeneXML(String geneXML) {
        if (geneXML == null || geneXML.equals(""))
            return;
        try {

            //System.out.println(geneXML);

            Document document = builder.parse(new InputSource(new StringReader(geneXML)));

            //  genome
            NodeList genomelist = document.getElementsByTagName("genome");
            for (int i = 0; i < genomelist.getLength(); i++) {
                Node genomeElement = genomelist.item(i);
                NamedNodeMap genomeAttributes = genomeElement.getAttributes();
                String organismGroupId = getNodeStringValue(genomeAttributes.getNamedItem("organismgroupid"));
                String numberofchromosomesString = getNodeStringValue(genomeAttributes.getNamedItem("numberofchromosomes"));
                int numberofchromosomes = Integer.parseInt(numberofchromosomesString);

                repository.genomeTable.insertNewGenome(organismGroupId, numberofchromosomes);

                NodeList chromosomeElements = genomeElement.getChildNodes();
                for (int j = 0; j < chromosomeElements.getLength(); j++) {
                    Node chromosomeElement = chromosomeElements.item(j);

                    //  genome/chromosome
                    if (chromosomeElement.getNodeName().equals("chromosome")) {
                        loadChromosomeToGenomeFromXMLNode(organismGroupId, chromosomeElement);
                    }
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
            //ioe.printStackTrace();
        }
    }

    //  genome/chromosome
    private void loadChromosomeToGenomeFromXMLNode(String organismGroupId, Node chromosomeElement) {
        NamedNodeMap chromosomeAttributes = chromosomeElement.getAttributes();
        String chromosomeName = getNodeStringValue(chromosomeAttributes.getNamedItem("number"));
        String chromosomeLengthString = getNodeStringValue(chromosomeAttributes.getNamedItem("length"));
        long chromosomeLength = Long.parseLong(chromosomeLengthString);
        String chromosomeCentromerLocationString = getNodeStringValue(chromosomeAttributes.getNamedItem("centromerlocation"));
        long chromosomeCentromerLocation;
        if (chromosomeCentromerLocationString.trim().equals("")) chromosomeCentromerLocation = -1;
        else {
            try {
                chromosomeCentromerLocation = Long.parseLong(chromosomeCentromerLocationString);
            }
            catch (NumberFormatException e) {
                chromosomeCentromerLocation = -1;
            }
        }

        int chromosomeid = repository.genomeTable.insertChromosomeToGenome(organismGroupId, chromosomeLength, chromosomeCentromerLocation, chromosomeName);

        NodeList geneElements = chromosomeElement.getChildNodes();
        for (int j = 0; j < geneElements.getLength(); j++) {
            Node geneElement = geneElements.item(j);

            //  genome/chromosome/gene
            if (geneElement.getNodeName().equals("gene")) {
                loadGeneToGenomeFromXMLNode(chromosomeid, geneElement);
            }
        }
    }

    private void loadGeneToGenomeFromXMLNode(int chromosomeid, Node geneElement) {

        try {
            NamedNodeMap geneAttributes = geneElement.getAttributes();
            String geneId = getNodeStringValue(geneAttributes.getNamedItem("id"));
            String geneName = getNodeStringValue(geneAttributes.getNamedItem("name"));
            String geneLocationString = getNodeStringValue(geneAttributes.getNamedItem("location"));
            long geneLocation = Long.parseLong(geneLocationString);
            String geneCytogeneticAddresse = getNodeStringValue(geneAttributes.getNamedItem("cytogeneticAddress"));
            String geneGenericprocess = getNodeStringValue(geneAttributes.getNamedItem("genericprocess"));

            repository.genomeTable.insertGeneToChromosome(chromosomeid, geneId, geneName, geneLocation, geneCytogeneticAddresse, geneGenericprocess);
        }
        catch (Exception e) {
            //e.printStackTrace();
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public HashSet<String> loadOrganismListFromGeneMappingXML(String genomepathwaylistXML) {
        //System.out.println(genomepathwaylistXML);

        HashSet<String> organismList = new HashSet<String>();

        if (genomepathwaylistXML == null || genomepathwaylistXML.equals(""))
            return organismList;
        try {

            //System.out.println(genomepathwaylistXML);
            Document document = builder.parse(new InputSource(new StringReader("<genomes>"+genomepathwaylistXML+"</genomes>")));

            // genome
            NodeList genomelist = document.getElementsByTagName("genome");
            for (int i = 0; i < genomelist.getLength(); i++) {
                Node genomeElement = genomelist.item(i);
                NamedNodeMap genomeAttributes = genomeElement.getAttributes();
                String organismGroupId = getNodeStringValue(genomeAttributes.getNamedItem("organismgroupid"));
                organismList.add(organismGroupId);
            }

        } catch (SAXException sxe) {
            // Error generated during parsing
            Exception x = sxe;
            if (sxe.getException() != null)
                x = sxe.getException();
            x.printStackTrace();

        } catch (IOException ioe) {
            // I/O error
            //ioe.printStackTrace();
        }



        return organismList;
    }


}
