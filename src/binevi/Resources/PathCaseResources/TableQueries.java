package binevi.Resources.PathCaseResources;

import binevi.View.PathCaseShapeNodeRealizer;
import y.base.Node;
import y.view.Graph2DView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TableQueries {
    public static String getPathwayNameById(PathCaseRepository repository, String pathwayid) {
        return repository.pathwayTable.getNameById(pathwayid);
    }

    public static HashMap<Node, String> getNodeToNameMap(PathCaseRepository repository, Graph2DView view, HashMap<Node, HashSet<String>> nodeToPathCaseId) {
        HashMap<Node, String> nodeToNameMap = new HashMap<Node, String>();
        if (nodeToPathCaseId == null) return nodeToNameMap;

        for (Node node : nodeToPathCaseId.keySet()) {
            HashSet<String> idset = nodeToPathCaseId.get(node);
            ArrayList<String> ids = new ArrayList<String>(idset);

            String fullname="";
            for (int eid=0;eid<ids.size();eid++) {
                String id = ids.get(eid);
                String name = "Unknown";
                PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) view.getGraph2D().getRealizer(node);
                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) {
                    name = repository.pathwayTable.getNameById(id);
                } else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) {
                    name = repository.genericProcessTable.getNameById(id);
                } else
                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR) {
                    name = repository.moleculesTable.getNameById(id);
                }

                if (eid>0)
                    fullname+=", "+name;
                else
                    fullname+=name;

            }
            nodeToNameMap.put(node, fullname);
        }

        return nodeToNameMap;
    }

    public static HashMap<String, String> getPathCaseIDToNameMapForPathway(PathCaseRepository repository, Graph2DView view, HashMap<Node, HashSet<String>> nodeToPathCaseId) {
        HashMap<String, String> IdToNameMap = new HashMap<String, String>();
        if (nodeToPathCaseId == null) return IdToNameMap;

        for (Node node : nodeToPathCaseId.keySet()) {
            HashSet<String> idset = nodeToPathCaseId.get(node);
            ArrayList<String> ids = new ArrayList<String>(idset);

            for (String id : ids) {
                String name = "Unknown";
                PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) view.getGraph2D().getRealizer(node);
                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) {
                    name = repository.pathwayTable.getNameById(id);
                } else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) {
                    name = repository.genericProcessTable.getNameById(id);
                } else
                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR) {
                    name = repository.moleculesTable.getNameById(id);
                }
                IdToNameMap.put(id, name);
            }
        }
        return IdToNameMap;
    }

    public static HashMap<String, String> getPathCaseIDToNameMap(PathCaseRepository repository, Graph2DView view, HashMap<Node, HashSet<String>> nodeToPathCaseId) {
        HashMap<String, String> IdToNameMap = new HashMap<String, String>();
        if (nodeToPathCaseId == null) return IdToNameMap;

        for (Node node : nodeToPathCaseId.keySet()) {
            HashSet<String> idset = nodeToPathCaseId.get(node);
            ArrayList<String> ids = new ArrayList<String>(idset);

            for (String id : ids) {
                String name = "Unknown";
                PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) view.getGraph2D().getRealizer(node);
                if(id.contains("NULLPROD")){
                    name="_waste_";
                }else if(id.contains("NULLREACT")){
                    name="_srs_";
                }else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) {
                    name = repository.pathwayTable.getNameById(id);
                } else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) {
//                    name = repository.genericProcessTable.getNameById(id);
//                       name = repository.reactionsTable.getNameById(id);
                    name = getReactionNamebyID(repository,id);
                    String reacSBMLid=getReactionSBMLidbyID(repository,id);
                    if(!name.equalsIgnoreCase(reacSBMLid))
                        name+=("  <br> <b>sbmlID: </b>"+reacSBMLid);

                       name += ":math:"+repository.reactionsTable.getMathById(id);
                    if(name.equalsIgnoreCase("NULL")) name=repository.genericProcessTable.getNameById(id);
                } else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR) {
//                    name = repository.moleculesTable.getNameById(id);
//                    name = repository.speciesTable.getNameById(id);
                    name = getSpeciesLabelBySpeciesId(repository,id);
                    String speID=getSBMLIDBySpeciesId(repository,id);
                            if(!name.equalsIgnoreCase(speID))
                                name+=("  <br> <b>sbmlID: </b>"+speID);

                    if(name.equalsIgnoreCase("NULL")) name=repository.moleculesTable.getNameById(id);
                }
                IdToNameMap.put(id, name);
            }
        }

        return IdToNameMap;
    }

    public static HashSet<String> getOrganismGroupIDListByGenericProcessId(PathCaseRepository repository, String dbid) {
        return repository.genericProcessToCatalyzesTable.getOrganismGroupIdList(dbid);
    }

    public static HashSet<String> getOrganismGroupIDListByCollapsedPathwayId(PathCaseRepository repository, String dbid) {
        return repository.pathwayToOrganismGroupsTable.getItemIdList(dbid);
    }

    public static HashSet<String> getCofactorTypedNamesByGenericProcessID(String gpid, PathCaseRepository repository) {
        HashSet<String> set = new HashSet<String>();

        ArrayList<ProcessToMoleculesTable.ProcessToMoleculesTableEntry> rows = repository.genericProcessToMoleculesTable.getRows(gpid);
        if (rows != null)
            for (ProcessToMoleculesTable.ProcessToMoleculesTableEntry entry : rows) {
                if (entry.MoleculeRole.toLowerCase().equals(MOLECULE_ROLE_STRINGS.COFACTOR)) {
                    String molname = getMoleculeNameById(repository, entry.MoleculeId);
                    if (molname != null) set.add(molname);
                } else if (entry.MoleculeRole.toLowerCase().equals(MOLECULE_ROLE_STRINGS.COFACTORIN)) {
                    String molname = getMoleculeNameById(repository, entry.MoleculeId);
                    if (molname != null) set.add(molname + " (in)");
                } else if (entry.MoleculeRole.toLowerCase().equals(MOLECULE_ROLE_STRINGS.COFACTOROUT)) {
                    String molname = getMoleculeNameById(repository, entry.MoleculeId);
                    if (molname != null) set.add(molname + " (out)");
                }
            }

        return set;
    }

    public static HashSet<String> getRegulatorTypedNamesByGenericProcessID(String gpid, PathCaseRepository repository) {
        HashSet<String> set = new HashSet<String>();

        ArrayList<ProcessToMoleculesTable.ProcessToMoleculesTableEntry> rows = repository.genericProcessToMoleculesTable.getRows(gpid);
        if (rows != null)
            for (ProcessToMoleculesTable.ProcessToMoleculesTableEntry entry : rows) {
                if (entry.MoleculeRole.toLowerCase().equals(MOLECULE_ROLE_STRINGS.ACTIVATOR)) {
                    String molname = getMoleculeNameById(repository, entry.MoleculeId);
                    if (molname != null) set.add(molname + " (+)");
                } else if (entry.MoleculeRole.toLowerCase().equals(MOLECULE_ROLE_STRINGS.INHIBITOR)) {
                    String molname = getMoleculeNameById(repository, entry.MoleculeId);
                    if (molname != null) set.add(molname + " (-)");
                } else if (entry.MoleculeRole.toLowerCase().equals(MOLECULE_ROLE_STRINGS.REGULATOR)) {
                    String molname = getMoleculeNameById(repository, entry.MoleculeId);
                    if (molname != null) set.add(molname);
                }
            }

        return set;
    }

    public static class MOLECULE_ROLE_STRINGS {

        public static String SUBSTRATE = "substrate";

        public static String PRODUCT = "product";

        public static String REGULATOR = "regulator";

        public static String ACTIVATOR = "activator";

        public static String INHIBITOR = "inhibitor";

        public static String COFACTOR = "cofactor";

        public static String COFACTORIN = "cofactor in";

        public static String COFACTOROUT = "cofactor out";

    }

    public static HashSet<String> getOrganismIdsInAListOfGenericProcessEntities(PathCaseRepository repository, HashSet<String> idsinGraph) {
        HashSet<String> orgidsinlist = new HashSet<String>();
        for (String dbid : idsinGraph) {
            HashSet<String> orgidsinrepositorybygenericprocesses = repository.genericProcessToCatalyzesTable.getOrganismGroupIdList(dbid);
            if (orgidsinrepositorybygenericprocesses == null) {
                orgidsinrepositorybygenericprocesses = new HashSet<String>();
                orgidsinrepositorybygenericprocesses.add(OrganismTable.UNKNOWNID);
            }
            orgidsinlist.addAll(orgidsinrepositorybygenericprocesses);
        }
        return orgidsinlist;
    }

    public static HashSet<String> getOrganismIdsInAListOfCollapsedPathwayEntities(PathCaseRepository repository, HashSet<String> idsinGraph) {
        HashSet<String> orgidsinlist = new HashSet<String>();
        for (String dbid : idsinGraph) {
            HashSet<String> orgidsinrepositorybycollapsedpathways = repository.pathwayToOrganismGroupsTable.getItemIdList(dbid);
            if (orgidsinrepositorybycollapsedpathways == null) {
                orgidsinrepositorybycollapsedpathways = new HashSet<String>();
                orgidsinrepositorybycollapsedpathways.add(OrganismTable.UNKNOWNID);
            }
            orgidsinlist.addAll(orgidsinrepositorybycollapsedpathways);
        }
        return orgidsinlist;
    }

    public static HashSet<String> getUsefulIdsInOrganismHierarchy(PathCaseRepository repository, HashSet<String> orgidsingraph) {

        HashSet<String> useful = new HashSet<String>(orgidsingraph);
        putParentOrganismIds(repository, useful);
        return useful;
    }

    public static void putParentOrganismIds(PathCaseRepository repository, HashSet<String> children) {
        HashSet<String> parents = new HashSet<String>();
        for (String child : children) {
            String parent = repository.organismTable.getParentId(child);
            if (parent != null)
                parents.add(parent);
        }
        if (parents.size() > 0)
            putParentOrganismIds(repository, parents);
        children.addAll(parents);
    }

    public static String getMoleculeNameById(PathCaseRepository repository, String subsorprod) {
        return repository.moleculesTable.getNameById(subsorprod);
    }

    public static String getMoleculeTissueById(PathCaseRepository repository, String subsorprod) {
        return repository.moleculesTable.getTissueById(subsorprod);
    }

    public static boolean getMoleculeCommonById(PathCaseRepository repository, String subsorprod) {
        return repository.moleculesTable.getCommonById(subsorprod);
    }

    public static boolean getSpeciesCommonById(PathCaseRepository repository, String subsorprod) {
            return repository.speciesTable.getCommonById(subsorprod);
        }


    public static HashSet<String> getECNumbersByGenericProcessID(PathCaseRepository repository, String GenericProcessId) {
        return repository.genericProcessToECNumbersTable.getECNumberList(GenericProcessId);
    }

    public static HashSet<String> getCollapsedPathwaysIdListInRepository(PathCaseRepository repository, boolean linkingIncluded) {
        return repository.pathwayTable.getCollapsedPathways(linkingIncluded);
    }

    public static ArrayList<String> getPathwaysIdListInRepository(PathCaseRepository repository) {
        return repository.pathwayTable.getAllPathways();
    }
    public static ArrayList<String> getCompartmentsByPathwayId(PathCaseRepository repository, String pathWayId){
        return repository.pathwayToCompartmentTable.getCompartmentByPathwayId(pathWayId);
    }

    public static HashSet<String> getLinkingPathwaysIdListInRepository(PathCaseRepository repository) {
            return repository.pathwayTable.getLinkingPathways();
    }
        
    public static ArrayList<String> getGenericProcessIDListInRepository(PathCaseRepository repository) {
        return repository.genericProcessTable.getGenericProcessIdList();
    }

//03/18/09 added for SysBio Model Visualization By Xinjian begin
    public static ArrayList<String> getCompartmentIDListInRepository(PathCaseRepository repository) {
        return repository.compartmentsTable.getCompartmentIdList();
    }

    public static ArrayList<String> getSpeciesIDListInRepositoryByCompartmentId(PathCaseRepository repository, String compId) {
        return repository.compartmentToSpeciesTable.getSpeciesByComId(compId);
    }
    
//    public static String getSpeciesRole(PathCaseRepository repository, String reacId, String specId) {
//        return repository.reactionToReactionSpeciesTable.getSpeciesRole(reacId,specId);
//    }

    public static ArrayList<String> getReactionsByCompartmentId(PathCaseRepository repository, String compId) {
        return repository.compartmentToReactionsTable.getReactionsByComId(compId);
    }

    public static String getSpeciesLabelBySpeciesId(PathCaseRepository repository, String speId) {
        return repository.speciesTable.getNameById(speId);
    }

    public static String getSBMLIDBySpeciesId(PathCaseRepository repository, String speId) {
        return repository.speciesTable.getSBMLIDById(speId);
    }

    public static String getCompartmnetNamebyCompartmentID(PathCaseRepository repository, String compid) {
        return repository.compartmentsTable.getNameById(compid);
    }
    
    public static String getCompartmnetSizebyCompartmentID(PathCaseRepository repository, String compid) {
            return repository.compartmentsTable.getSizeById(compid);
        }

    public static String getCompartmnetOutsidebyCompartmentID(PathCaseRepository repository, String compid) {
        return repository.compartmentsTable.getOutsideById(compid);
    }

    public static ArrayList<String> getReactionIDListInRepository(PathCaseRepository repository) {
        return repository.reactionsTable.getReactionIdList();
    }

     public static boolean getReactionTransportableInRepository(PathCaseRepository repository,String reacId) {
        return repository.reactionsTable.getIsTransportById(reacId);
    }

      public static String getSpeciesRole(PathCaseRepository repository, String reacId, String specId) {
        return repository.reactionToReactionSpeciesTable.getSpeciesRole(reacId,specId);
    }

    public static boolean getReactionReversibleInRepository(PathCaseRepository repository,String reacId) {
        return repository.reactionsTable.getIsReversibleById(reacId);
    }

    public static ArrayList<String> getSpeciesIDListFromReactionID(PathCaseRepository repository, String reacId) {
        return repository.reactionSpeciesTable.getSpeciesByReacSpecIds(repository.reactionToReactionSpeciesTable.getSpeciesByReacId(reacId));
    }

    public static String getSpeciesRoleFromSpeciesID(PathCaseRepository repository,String reacId, String speId) {
        return repository.reactionSpeciesTable.getSpeciesRoleBySpecId(repository.reactionToReactionSpeciesTable.getSpeciesByReacId(reacId),speId);
    }

    public static String getCompartmentIDBySpeciesID(PathCaseRepository repository, String specid) {
        return repository.compartmentToSpeciesTable.getCompIdBySpecId(specid);
    }

    public static String getReactionNamebyID(PathCaseRepository repository, String recid) {
        return repository.reactionsTable.getNameById(recid);
    }

    public static String getReactionSBMLidbyID(PathCaseRepository repository, String recid) {
        return repository.reactionsTable.getSBMLIDById(recid);
    }

    public static String getKineticLawbyID(PathCaseRepository repository, String recid) {
        return repository.reactionsTable.getMathById(recid);
    }

    public static ArrayList<String> getReactionEnzymebyID(PathCaseRepository repository,String compid, String reacid) {
        return repository.compartmentToReactionsTable.getReactionEnzyme(compid,reacid);
    }                         

    public static String getEnzymeNamebyID(PathCaseRepository repository,String enzid){
        return repository.enzymeTable.getNameById(enzid);              
    }
//    public static ArrayList<String> getSpeciesIDListInRepositoryByReacId(PathCaseRepository repository, String reacId) {
//        return repository.reactionToReactionSpeciesTable.getSpeciesByReacId(reacId) .compartmentToSpeciesTable.getSpeciesByComId(compId);
//    }
//03/18/09 added for SysBio Model Visualization By Xinjian end.

   public static HashSet<String> getGenericProcessIDListInRepositoryByPathwayId(PathCaseRepository repository, String pathwayId) {
        return repository.pathwayToGenericProcessesTable.getItemIdList(pathwayId);
    }

    public static String getGenericProcessNamebyGenericProcessID(PathCaseRepository repository, String gpid) {
        return repository.genericProcessTable.getNameById(gpid);        
    }

    public static String getGenericProcessTissuebyGenericProcessID(PathCaseRepository repository, String gpid) {
            return repository.genericProcessTable.getTissueById(gpid) ;
        }

     public static boolean getGenericProcessisTransportbyGenericProcessID(PathCaseRepository repository, String gpid) {
        return repository.genericProcessTable.getIsTransportById(gpid);
    }


    public static String getGenericProcessEntityIdbyGenericProcessID(PathCaseRepository repository, String gpid) {
        return repository.genericProcessTable.getEntityIdById(gpid);
    }

    public static HashSet<String> getSubstrateAndProductsByGenericProcessId(PathCaseRepository repository, String gpid) {
        HashSet<String> set = new HashSet<String>();

        ArrayList<ProcessToMoleculesTable.ProcessToMoleculesTableEntry> rows = repository.genericProcessToMoleculesTable.getRows(gpid);
        if (rows != null)
            for (ProcessToMoleculesTable.ProcessToMoleculesTableEntry entry : rows) {
                if (entry.MoleculeRole.toLowerCase().equals(MOLECULE_ROLE_STRINGS.SUBSTRATE) || entry.MoleculeRole.toLowerCase().equals(MOLECULE_ROLE_STRINGS.PRODUCT)) {
                    set.add(entry.MoleculeId);
                }
            }

        return set;

    }

    public static HashSet<String> getActivatorRegulatorsByGenericProcessId(PathCaseRepository repository, String role, String gpid) {
        HashSet<String> set = new HashSet<String>();

        ArrayList<ProcessToMoleculesTable.ProcessToMoleculesTableEntry> rows = repository.genericProcessToMoleculesTable.getRows(gpid);
        for (ProcessToMoleculesTable.ProcessToMoleculesTableEntry entry : rows) {
            if (entry.MoleculeRole.equalsIgnoreCase(role.toLowerCase())) {
                set.add(entry.MoleculeId);
            }
        }

        return set;
    }

    public static HashSet<String> getSubstrateAndProductsByCollapsedPathwayId(PathCaseRepository repository, String pathwayid) {

        HashSet<String> molecules = new HashSet<String>();

        HashMap<String,HashSet<String>> fromlist = repository.pathwayToLinkingMoleculesTable.getItemIdList(pathwayid);
        if (fromlist!=null)
        for (String toid:fromlist.keySet())
        {
            HashSet<String> links = fromlist.get(toid);
            molecules.addAll(links);
        }

        for (String fromid:repository.pathwayToLinkingMoleculesTable.ContentTable.keySet())
        {
            HashMap<String,HashSet<String>> tolist = repository.pathwayToLinkingMoleculesTable.getItemIdList(fromid);
            HashSet<String> links = tolist.get(pathwayid);
            if (links!=null)
                molecules.addAll(links);
        }

        return molecules;
    }

    public static ArrayList<MoleculeProcessPair> getSubstrateProductProcessEdges(PathCaseRepository repository) {
        ArrayList<MoleculeProcessPair> edges = new ArrayList<MoleculeProcessPair>();
        ArrayList<String> GenericProcessIDListInRepository = getGenericProcessIDListInRepository(repository);
        for (String gpid : GenericProcessIDListInRepository) {
            ArrayList<ProcessToMoleculesTable.ProcessToMoleculesTableEntry> rows = repository.genericProcessToMoleculesTable.getRows(gpid);
            Boolean reversible = repository.genericProcessTable.getReversibleById(gpid);

            if (rows != null) {

                for (ProcessToMoleculesTable.ProcessToMoleculesTableEntry entry : rows) {
                    if (entry.MoleculeRole.equalsIgnoreCase(MOLECULE_ROLE_STRINGS.SUBSTRATE)) {
                        edges.add(new MoleculeProcessPair(entry.MoleculeId, gpid, true, reversible));
                    } else if (entry.MoleculeRole.equalsIgnoreCase(MOLECULE_ROLE_STRINGS.PRODUCT)) {
                        edges.add(new MoleculeProcessPair(entry.MoleculeId, gpid, false, reversible));
                    }

                }
            }
        }
        return edges;
    }

    public static HashMap<String, HashMap<String, ArrayList<String>>> getRegulatorProcessEdges(PathCaseRepository repository) {
        HashMap<String, HashMap<String, ArrayList<String>>> edges = new HashMap<String, HashMap<String, ArrayList<String>>>();

        ArrayList<String> GenericProcessIDListInRepository = getGenericProcessIDListInRepository(repository);
        for (String gpid : GenericProcessIDListInRepository) {
            ArrayList<ProcessToMoleculesTable.ProcessToMoleculesTableEntry> rows = repository.genericProcessToMoleculesTable.getRows(gpid);
            HashMap<String, ArrayList<String>> gpidRegulatorMap = edges.get(gpid);
            if (gpidRegulatorMap == null) {
                gpidRegulatorMap = new HashMap<String, ArrayList<String>>();
                edges.put(gpid,gpidRegulatorMap);
            }

            if (rows != null) {
                ArrayList<String> genericregulators = new ArrayList<String>();
                ArrayList<String> inhibitors = new ArrayList<String>();
                ArrayList<String> activators = new ArrayList<String>();

                for (ProcessToMoleculesTable.ProcessToMoleculesTableEntry entry : rows) {
                    if (entry.MoleculeRole.equalsIgnoreCase(MOLECULE_ROLE_STRINGS.REGULATOR)) {
                        genericregulators.add(entry.MoleculeId);
                    } else if (entry.MoleculeRole.equalsIgnoreCase(MOLECULE_ROLE_STRINGS.INHIBITOR)) {
                        inhibitors.add(entry.MoleculeId);
                    } else if (entry.MoleculeRole.equalsIgnoreCase(MOLECULE_ROLE_STRINGS.ACTIVATOR)) {
                        activators.add(entry.MoleculeId);
                    }
                }

                gpidRegulatorMap.put(MOLECULE_ROLE_STRINGS.REGULATOR, genericregulators);
                gpidRegulatorMap.put(MOLECULE_ROLE_STRINGS.INHIBITOR, inhibitors);
                gpidRegulatorMap.put(MOLECULE_ROLE_STRINGS.ACTIVATOR, activators);
            }
        }

        return edges;
    }

    public static HashMap<String, HashMap<String, ArrayList<String>>> getCofactorProcessEdges(PathCaseRepository repository) {
        HashMap<String, HashMap<String, ArrayList<String>>> edges = new HashMap<String, HashMap<String, ArrayList<String>>>();

        ArrayList<String> GenericProcessIDListInRepository = getGenericProcessIDListInRepository(repository);
        for (String gpid : GenericProcessIDListInRepository) {
            ArrayList<ProcessToMoleculesTable.ProcessToMoleculesTableEntry> rows = repository.genericProcessToMoleculesTable.getRows(gpid);
            HashMap<String, ArrayList<String>> gpidCofactorMap = edges.get(gpid);
            if (gpidCofactorMap == null) {
                gpidCofactorMap = new HashMap<String, ArrayList<String>>();
                edges.put(gpid,gpidCofactorMap);
            }

            if (rows != null) {
                ArrayList<String> genericcofactors = new ArrayList<String>();
                ArrayList<String> cofactorins = new ArrayList<String>();
                ArrayList<String> cofactorouts = new ArrayList<String>();

                for (ProcessToMoleculesTable.ProcessToMoleculesTableEntry entry : rows) {
                    if (entry.MoleculeRole.equalsIgnoreCase(MOLECULE_ROLE_STRINGS.COFACTOR)) {
                        genericcofactors.add(entry.MoleculeId);
                    } else if (entry.MoleculeRole.equalsIgnoreCase(MOLECULE_ROLE_STRINGS.COFACTORIN)) {
                        cofactorins.add(entry.MoleculeId);
                    } else if (entry.MoleculeRole.equalsIgnoreCase(MOLECULE_ROLE_STRINGS.COFACTOROUT)) {
                        cofactorouts.add(entry.MoleculeId);
                    }
                }

                gpidCofactorMap.put(MOLECULE_ROLE_STRINGS.COFACTOR, genericcofactors);
                gpidCofactorMap.put(MOLECULE_ROLE_STRINGS.COFACTORIN, cofactorins);
                gpidCofactorMap.put(MOLECULE_ROLE_STRINGS.COFACTOROUT, cofactorouts);
            }
        }

        return edges;
    }

       public static HashSet<String> getLinkingMolecules(HashSet<String> pathways, PathCaseRepository repository) {
        HashSet<String> set = new HashSet<String>();
        for (String p : pathways) {
            HashMap<String, HashSet<String>> map = repository.pathwayToLinkingMoleculesTable.getItemIdList(p);
            if(map!=null)   //added by AD
            {
            for (HashSet<String> ids : map.values()) {
                for (String id : ids) {
                    set.add(id);
                }
            }
            }
        }
        //System.out.println(set);
        return set;
    }

    public static ArrayList<MoleculeProcessPair> getLinkingMoleculePathwayEdges(PathCaseRepository repository, boolean linkingIncluded) {
        ArrayList<MoleculeProcessPair> edges = new ArrayList<MoleculeProcessPair>();
        HashSet<String> CollapsedPathwayIdIDListInRepository = getCollapsedPathwaysIdListInRepository(repository, linkingIncluded);
        for (String pwpid : CollapsedPathwayIdIDListInRepository) {
            HashSet<String> rows = getSubstrateAndProductsByCollapsedPathwayId(repository,pwpid);
            if (rows != null)
                for (String entry : rows) {
                    edges.add(new MoleculeProcessPair(entry,pwpid, false, false));
                }
        }

        return edges;
    }


    public static HashMap<String, String> getOrganismIdToSimplifiedNames(PathCaseRepository repository, HashSet<String> orgidlist) {
        HashMap<String, String> OrganismIdToSimplifiedNames = new HashMap<String, String>();
        for (String orgid : orgidlist) {
            OrganismTable.OrganismTableEntry orgentry = repository.organismTable.getRow(orgid);
            if (orgentry != null)
                OrganismIdToSimplifiedNames.put(orgid, orgentry.getSimplifiedName());
        }
        return OrganismIdToSimplifiedNames;
    }

    public static HashMap<String, String> getOrganismSimplifiedNamesToId(PathCaseRepository repository, HashSet<String> orgidlist) {
        HashMap<String, String> OrganismIdToSimplifiedNames = new HashMap<String, String>();
        for (String orgid : orgidlist) {
            OrganismTable.OrganismTableEntry orgentry = repository.organismTable.getRow(orgid);
            if (orgentry != null)
                OrganismIdToSimplifiedNames.put(orgentry.getSimplifiedName(), orgid);
        }
        return OrganismIdToSimplifiedNames;
    }
    
    public static void addSpecies(PathCaseRepository repository, String speciesId, String speciesName, String sbmlId,
    		String initialAmount, String typeId, String initialConcentration,String unitsId, boolean hasOnlyUnitsId,
    		boolean boundaryCondition, String charge, boolean constant, boolean common, String compartmentId) {
    	repository.speciesTable.insertRow(speciesId, speciesName, sbmlId, initialAmount, typeId, initialConcentration,
    			unitsId, hasOnlyUnitsId, boundaryCondition, charge, constant, common);
    	repository.compartmentToSpeciesTable.insertRow(compartmentId, speciesId);
    }
    
    public static void addReaction(PathCaseRepository repository, String ReactionID, String ReactionName, String sbmlId) {
    	repository.reactionsTable.insertRow(ReactionID, ReactionName, sbmlId, false, "", false);
    }
    
    public static void addProductToReaction(PathCaseRepository repository, String reactionPathCaseId, String productName, String productId) {
    	String reactionSpeciesId = "reaction_" + productId;
    	repository.reactionSpeciesTable.insertRow(reactionSpeciesId, productName, productId, "Product","");
    	repository.reactionToReactionSpeciesTable.insertRow(reactionPathCaseId,reactionSpeciesId);
    	
    }
    
    public static void addReactantToReaction(PathCaseRepository repository, String reactionPathCaseId, String reactantName, String reactantId) {
    	String reactionSpeciesId = "reaction_" + reactantId;
    	repository.reactionSpeciesTable.insertRow(reactionSpeciesId, reactantName, reactantId, "Reactant","");
    	repository.reactionToReactionSpeciesTable.insertRow(reactionPathCaseId,reactionSpeciesId);
    	
    }
    public static String getModelName(PathCaseRepository repository) {
    	return repository.modelTable.getNameById();
    }
    public static void deleteSpecies(PathCaseRepository repository, String speciesId) {
    	String reactionSpeciesId = "reaction_" + speciesId;
    	repository.speciesTable.deleteRow(speciesId);
    	repository.compartmentToSpeciesTable.deleteRow(speciesId);
    	repository.reactionSpeciesTable.deleteRow(reactionSpeciesId);
    	repository.reactionToReactionSpeciesTable.deleteReactionSpecies(reactionSpeciesId);
    }
    
    public static void deleteReaction(PathCaseRepository repository, String reactionId) {
    	repository.reactionsTable.deleteRow(reactionId);
    }
    
}


