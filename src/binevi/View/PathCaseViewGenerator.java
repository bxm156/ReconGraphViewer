package binevi.View;

import binevi.Resources.PathCaseResources.MoleculeProcessPair;
import binevi.Resources.PathCaseResources.OrganismTable;
import binevi.Resources.PathCaseResources.PathCaseRepository;
import binevi.Resources.PathCaseResources.TableQueries;
import y.base.*;
import y.view.*;
import y.view.hierarchy.HierarchyManager;
import y.view.hierarchy.GroupNodeRealizer;
import y.view.hierarchy.AutoBoundsFeature;
import y.layout.hierarchic.incremental.HierarchicLayouter;
import y.layout.hierarchic.HierarchicGroupLayouter;
import y.layout.organic.SmartOrganicLayouter;
import y.layout.organic.OrganicLayouter;
import y.layout.organic.RemoveOverlapsLayoutStage;
import y.layout.*;
import y.layout.router.OrganicEdgeRouter;
import y.layout.router.OrthogonalEdgeRouter;
import y.layout.grouping.Grouping;
import y.geom.YPoint;

import java.awt.*;
import java.util.*;

class PFrame
        {
            private PPoint upleft, bottomright, center;
            public PFrame(PPoint ul, PPoint br, PPoint cen){
                this.upleft=ul;
                this.bottomright=br;
                this.center=cen;
            }
            public PFrame(PPoint ul, PPoint br){
                this.upleft=ul;
                this.bottomright=br;
                this.center=new PPoint((br.getX()+ul.getX())/2,(br.getY()+ul.getY())/2);
            }

             public PFrame(PPoint ul, double wid, double heig){
                this.upleft=ul;
                 PPoint br=new PPoint(ul.getX()+wid,ul.getY()+heig);
                this.bottomright=br;
                this.center=new PPoint((br.getX()+ul.getX())/2,(br.getY()+ul.getY())/2);
            }

            public PFrame(PPoint ul, double wid, double heig,boolean bcen){
                //bcen==true --ul is center Point   ;  false   is bottomright Point
                if(bcen){
                    this.center=ul;
                    this.upleft=new PPoint(ul.getX()-wid/2,ul.getY()-heig/2);
                    this.bottomright=new PPoint(ul.getX()+wid/2,ul.getY()+heig/2);
                }else{
                    this.bottomright=ul;
                    this.center=new PPoint(ul.getX()-wid/2,ul.getY()-heig/2);
                    this.upleft=new PPoint(ul.getX()-wid,ul.getY()-heig);
                }
            }

            public PPoint getUpLeft(){
                return upleft;
            }
            public PPoint getBottomRight(){
                return bottomright;
            }

            public double getHeight(){
                return bottomright.getY()-upleft.getY();
            }
            public double getWidth(){
                return bottomright.getX()-upleft.getX();
            }

            public double getMinX(){
                return upleft.getX();
            }
            public double getMaxX(){
                return bottomright.getX();
            }
            public double getMinY(){
                return upleft.getY();
            }
            public double getMaxY(){
                return bottomright.getY();
            }
            public double getCenterX(){
                return center.getX();
            }
            public double getCenterY(){
                return center.getY();
            }
            public PPoint getCenter(){
                return center;
            }
            public PPoint getBottom(){
                return bottomright;
            }

            public void setWidth(double wid){
                this.bottomright=new PPoint((upleft.getX()+wid),bottomright.getY());
            }

            public void setHeight(double hei){
                this.bottomright=new PPoint(bottomright.getX(),upleft.getY()+hei);
            }

            public void setStartX(double startx){
                double xOff=startx-this.getMinX();
                this.center.setX(this.center.getX()+xOff);
                this.bottomright.setX(this.bottomright.getX()+xOff);
                this.upleft.setX(startx);
            }

            public void setStartY(double starty){
                double yOff=starty-this.getMinY();
                this.center.setY(this.center.getY()+yOff);
                this.bottomright.setY(this.bottomright.getY()+yOff);
                this.upleft.setY(starty);
            }

        }
public class PathCaseViewGenerator {

    private static Vector<Color> acceptableTissueColors=null;
    private static int selectedTissueColor=0;
    private static int selectedTissueColorLast=0;
    static int borderbuffer=0;
    static int testBuffer=0;
    static int minValue=80;
    static int nodeSizeX=20,nodeSizeY=20,reactionSizeX=40, reactionSizeY=15,nullMoleSize=5;


    public static void createOrganismHierarchyFromRepository(PathCaseRepository repository, OrganismCheckNode rootOrganism) {

        ArrayList<String> childrenIds = repository.organismTable.getChildren(rootOrganism.getNodeId());
        if (childrenIds != null)
            for (String childId : childrenIds) {
                OrganismTable.OrganismTableEntry organismentry = repository.organismTable.getRow(childId);
                String name;
                if (childId.equals(OrganismTable.ROOTID) || childId.equals(OrganismTable.UNKNOWNID))
                    name = childId;
                else {
                    if (organismentry.scientificName.equals("") && organismentry.commonName.equals(""))
                        name = "Non-named Organism";
                    else if (!organismentry.scientificName.equals("") && organismentry.commonName.equals(""))
                        name = organismentry.scientificName;
                    else if (organismentry.scientificName.equals("") && !organismentry.commonName.equals(""))
                        name = organismentry.commonName;
                    else
                        name = organismentry.commonName + " [" + organismentry.scientificName + "]";
                }

                //System.out.println(name);
                OrganismCheckNode childnode = new OrganismCheckNode(name, childId);
                rootOrganism.add(childnode);
                createOrganismHierarchyFromRepository(repository, childnode);
            }
    }

    public static void createOrganismHierarchyFromRepository(PathCaseRepository repository, OrganismCheckNode rootOrganism, HashSet<String> usefulorgidsinhierarchy) {
        if (rootOrganism.getNodeId().equals(OrganismTable.ROOTID) && usefulorgidsinhierarchy.contains(OrganismTable.UNKNOWNID)) {
            OrganismCheckNode childnode = new OrganismCheckNode(OrganismTable.UNKNOWNID, OrganismTable.UNKNOWNID);
            rootOrganism.add(childnode);
        }

        ArrayList<String> childrenIds = repository.organismTable.getChildren(rootOrganism.getNodeId());
        if (childrenIds != null)
            for (String childId : childrenIds) {
                if (!usefulorgidsinhierarchy.contains(childId))
                    continue;
                OrganismTable.OrganismTableEntry organismentry = repository.organismTable.getRow(childId);
                String name;
                if (childId.equals(OrganismTable.ROOTID) || childId.equals(OrganismTable.UNKNOWNID))
                    name = childId;
                else {
                    name = organismentry.getCombinedName();
                }

                //System.out.println(name+","+childId);
                OrganismCheckNode childnode = new OrganismCheckNode(name, childId);
                rootOrganism.add(childnode);

                createOrganismHierarchyFromRepository(repository, childnode, usefulorgidsinhierarchy);
            }
    }

//03/18/09 added for SysBio Model Visualization By Xinjian
    public static HashMap<Node, HashSet<String>> createGraphFromSysBioModel(PathCaseRepository repository, Graph2D graph2D, PathCaseViewerMetabolomics.PathCaseViewMode mode,String appWidth, String appHeight) {
   //return createGraphFromWholeRepository(repository, graph2D, mode.showcommonmoleculesingraph, mode.showmodulatorsingraph, mode.showlinkingpathwaysingraph);
        return createModelGraphFromSysBioModel(repository, graph2D, mode.showcommonmoleculesingraph, mode.showmodulatorsingraph, mode.showlinkingpathwaysingraph,appWidth,appHeight);
   }

    public static HashMap<Node, HashSet<String>> createGraphFromSysBioModelN(PathCaseRepository repository, Graph2D graph2D, PathCaseViewerMetabolomics.PathCaseViewMode mode,String appWidth, String appHeight, String reactionGuids) {
        return createGraphFromSysBioModelN(repository,graph2D,mode,appWidth,appHeight,reactionGuids,0);
    }

    public static HashMap<Node, HashSet<String>> createGraphFromMQL(PathCaseRepository repository, Graph2D graph2D, PathCaseViewerMetabolomics.PathCaseViewMode mode,String appWidth, String appHeight, String reactionGuids) {
        return createGraphFromMQL(repository, graph2D, appWidth,appHeight);//,reactionGuids,0,0,0);
    }

   public static HashMap<Node, HashSet<String>> createGraphFromSysBioModelN(PathCaseRepository repository, Graph2D graph2D, PathCaseViewerMetabolomics.PathCaseViewMode mode,String appWidth, String appHeight, String reactionGuids,int iModelColor) {

        return createGraphFromSysBioModelN(repository, graph2D,mode, appWidth,appHeight,reactionGuids,0,0,iModelColor);
   }

    public static HashMap<Node, HashSet<String>> createGraphFromSysBioModelN(PathCaseRepository repository, Graph2D graph2D, PathCaseViewerMetabolomics.PathCaseViewMode mode,String appWidth, String appHeight, String reactionGuids, double dx, double dy) {
        return createGraphFromSysBioModelN(repository, graph2D, mode,appWidth, appHeight, reactionGuids, dx, dy,0);
    }

    public static HashMap<Node, HashSet<String>> createGraphFromSysBioModelN(PathCaseRepository repository, Graph2D graph2D, String appWidth, String appHeight, String reactionGuids, double dx, double dy,int iModelColor) {
   //return createGraphFromWholeRepository(repository, graph2D, mode.showcommonmoleculesingraph, mode.showmodulatorsingraph, mode.showlinkingpathwaysingraph);

//            if(graph2D.getNodeArray().length>0){
//         //re group from current nodes
////                System.out.println("The First model has comparments:"+getGoupNodesNum(graph2D));
//           HierarchyManager hm = HierarchyManager.getInstance(graph2D);
//           for(Node n:graph2D.getNodeArray()){
//                if(hm.isGroupNode(n)){
//                    NodeRealizer groupNodeRealizer = graph2D.getRealizer(n);
////                    groupNodeRealizer.setTransparent(false);
////                    groupNodeRealizer.setLocation(groupNodeRealizer.getX()+modelGraphBoundaryX,groupNodeRealizer.getY());
//                    int nd=11;
////                    groupNodeRealizer.
//
//// Turn off auto bounds.
////                    ((AutoBoundsFeature)groupNodeRealizer).setAutoBoundsEnabled(false);
//
////                    gr.setLocation(n,groupNodeRealizer.getCenterX() +modelGraphBoundaryX,groupNodeRealizer.getCenterY() );
//                }
//                else{
////                    NodeRealizer nr=gr.getRealizer(n);
////                    gr.setLocation(n,nr.getCenterX()+modelGraphBoundaryX,nr.getCenterY());
//                    int nd =10;
//                }
//
//            }
//
//            int m=5;
//        }   else{
//
//            int n=3;
//        }
//        PathCaseViewerMetabolomics.PathCaseViewMode mode= new PathCaseViewerMetabolomics.PathCaseViewMode(); 

        return createGraphFromSysBioModelN(repository, graph2D,null, appWidth,appHeight,reactionGuids,dx,dy,iModelColor);
   }

     public static HashMap<Node, HashSet<String>> createGraphFromWholeRepositoryCompartmentH(PathCaseRepository repository, Graph2D graph2D, PathCaseViewerMetabolomics.PathCaseViewMode mode) {
        //return createGraphFromWholeRepository(repository, graph2D, mode.showcommonmoleculesingraph, mode.showmodulatorsingraph, mode.showlinkingpathwaysingraph);
        return createGraphFromWholeRepositoryTissueCompartmentH(repository, graph2D, mode.showcommonmoleculesingraph, mode.showmodulatorsingraph, mode.showlinkingpathwaysingraph);
    }

    public static HashMap<Node, HashSet<String>> createGraphFromWholeRepository(PathCaseRepository repository, Graph2D graph2D, PathCaseViewer.PathCaseViewMode mode) {
        return createGraphFromWholeRepository(repository, graph2D, mode.showcommonmoleculesingraph, mode.showmodulatorsingraph, mode.showlinkingpathwaysingraph);
    }

    public static HashMap<Node, HashSet<String>> createGraphFromWholeRepository(PathCaseRepository repository, Graph2D graph2D, PathCaseViewerMetabolomics.PathCaseViewMode mode) {
        return createGraphFromWholeRepository(repository, graph2D, mode.showcommonmoleculesingraph, mode.showmodulatorsingraph, mode.showlinkingpathwaysingraph);
    }

    static Node createSubGroupNode(PathCaseRepository repository, Graph2D graph,HierarchyManager hm,HashMap<String, ArrayList<String>> compH,HashMap<String, NodeList> compartmentQListMap, String compId, HashMap<String, Node> idToNodeTable,HashMap<Node,HashSet<String>> nodeToId){
            //while(compH.containsKey(compId)){
        ArrayList<String> speciesIds = TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compId);

               if(speciesIds!=null){
                   for(String speciesId:speciesIds){
                       String speLabel=TableQueries.getSpeciesLabelBySpeciesId(repository,speciesId);
                       if(speLabel==null || speLabel=="") speLabel="Unknown Species";
                       Node speciesnode = graph.createNode();
//                       nodeToId.put(speciesnode,speciesId);
//                       speCount++;
                       ShapeNodeRealizer spenr;
                       spenr = getSubstrateProductShapeNodeRealizer(speLabel,false);     //only a few are common in previous database, which means very popular, exist in most pathways.
                       graph.setRealizer(speciesnode, spenr);
                       compartmentQListMap.get(compId).add(speciesnode);
                       idToNodeTable.put(speciesId,speciesnode);
                       HashSet<String> spelist = new HashSet<String>();
                       spelist.add(speciesId);
                       nodeToId.put(speciesnode, spelist);
                   }
               }

            ArrayList<String> reacIds = TableQueries.getReactionsByCompartmentId(repository,compId);
              if(reacIds!=null){
                    for(String reacid:reacIds){
                        //Do not use reaction name, but use enzyme name;
//                       String reacLabel=TableQueries.getReactionNamebyID(repository,reacId);
                        String reacLabel="<html>" ;
                        ArrayList<String> reacEnzIDs=TableQueries.getReactionEnzymebyID(repository,compId,reacid);     
                        if(reacEnzIDs!=null){
                            for(String enlabel:reacEnzIDs){
                                reacLabel+=(TableQueries.getEnzymeNamebyID(repository,enlabel)+"<br>");
                            }
                            reacLabel+="</html>";
                        }else
                        reacLabel="Unknown Reactions";
//                       if(reacLabel==null || reacLabel=="") reacLabel="Unknown Reactions";
                       Node reacnode = graph.createNode();
                       ShapeNodeRealizer reacr;

                       reacr = getGenericProcessShapeNodeRealizer(reacLabel);//getRandomPoint(curFrame,compid,compartment_hierarchy,compartment_frame));
                       graph.setRealizer(reacnode, reacr);
                       compartmentQListMap.get(compId).add(reacnode);
                       idToNodeTable.put(reacid,reacnode);
                       HashSet<String> reaclist = new HashSet<String>();
                       reaclist.add(reacid);
                       nodeToId.put(reacnode, reaclist);
                    }
               }

        //if has child, crete children recursively
        if(compH.containsKey(compId)){
            for(String subCom:compH.get(compId)){
                Node subComNode=createSubGroupNode(repository, graph,hm,compH, compartmentQListMap,subCom,idToNodeTable,nodeToId);
                compartmentQListMap.get(compId).add(subComNode);
            }
        }
        Node groupNode =hm.createGroupNode(graph);
        ShapeNodeRealizer nrGroupR = getTissueShapeNodeRealizerSB(TableQueries.getCompartmnetNamebyCompartmentID(repository,compId));
        graph.setRealizer(groupNode, nrGroupR);
        idToNodeTable.put(compId,groupNode);
       compartmentQListMap.put(compId,new NodeList());
       HashSet<String> comlist = new HashSet<String>();
       comlist.add(compId);
       nodeToId.put(groupNode,comlist);
        hm.groupSubgraph(compartmentQListMap.get(compId),groupNode);

        return groupNode;
    }

    public static void ComputeElementsPositions_FromInner(PFrame curFrame,PathCaseRepository repository, String compid,HashMap<String, ArrayList<String>> compartment_hierarchy,HashMap<String, PFrame> compartment_frame,HashMap<String, PPoint> computedPositions)//, HashMap<String, ArrayList<String>> comp_reaction)
            {
//            HashMap<String, PPoint> computedPositions=new HashMap<String, PPoint>();
                HashMap<String, Node> idToNodeTable = new HashMap<String, Node>();
                HashMap<Node,String> NodeToIdTable = new HashMap<Node,String>();
                DefaultLayoutGraph graph = new DefaultLayoutGraph();
                Node groupNode = graph.createNode();
                graph.setLocation(groupNode,new YPoint(0,0));
                graph.setSize(groupNode, curFrame.getWidth()-20,curFrame.getHeight()-20);

                //create inner compartment node
                if(compartment_hierarchy.containsKey(compid)){
                    for (String innercompid : compartment_hierarchy.get(compid))
                    {
                        PFrame innerFrame=compartment_frame.get(innercompid);
                        Node innergroupNode = graph.createNode();
//                graph.setLocation(innergroupNode,new YPoint(0,0));
                        graph.setSize(innergroupNode, innerFrame.getWidth(),innerFrame.getHeight());
//                    if(compartment_hierarchy.get(compid).size()==1)graph.setCenter(innergroupNode,curFrame.getCenterX(),curFrame.getCenterY());
                        idToNodeTable.put(innercompid,innergroupNode);
                        NodeToIdTable.put(innergroupNode,innercompid);
                    }
                }


                ArrayList<String> reacIds = TableQueries.getReactionsByCompartmentId(repository,compid);
                if(reacIds!=null){
                      for(String reacid:reacIds){
                           String reacLabel="<html>";
                          ArrayList<String> reacEnzIDs=TableQueries.getReactionEnzymebyID(repository,compid,reacid);
                          if(reacEnzIDs!=null){
                              for(String enlabel:reacEnzIDs){
                                  reacLabel+=(TableQueries.getEnzymeNamebyID(repository,enlabel)+"<br>");
                              }
                              reacLabel+="</html>";
                          }else
                         reacLabel="Unknown Reactions";
//                       if(reacLabel==null || reacLabel=="") reacLabel="Unknown Reactions";
                         Node reacnode = graph.createNode();
                         graph.setSize(reacnode,reactionSizeX,reactionSizeY);
                         idToNodeTable.put(reacid,reacnode);
                         NodeToIdTable.put(reacnode,reacid);
                      }
                 }

            ArrayList<String> commonSpecies=new ArrayList<String>();

            ArrayList<String> speciesIds = TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid);
            if(speciesIds!=null){
                for(String speciesId:speciesIds){
                   if(TableQueries.getSpeciesCommonById(repository,speciesId)){ //if common donot create node here
                       commonSpecies.add(speciesId);
                   }else{
                       String speLabel=TableQueries.getSpeciesLabelBySpeciesId(repository,speciesId);
                       if(speLabel==null || speLabel=="") speLabel="Unknown Species";
                       Node speciesnode = graph.createNode();
                       graph.setSize(speciesnode,nodeSizeX,nodeSizeY);
                       idToNodeTable.put(speciesId,speciesnode);
                       NodeToIdTable.put(speciesnode,speciesId);
                   }
                }
            }
                ArrayList<String> reacIdsAll = TableQueries.getReactionIDListInRepository(repository);
                if(reacIdsAll!=null){
                      for(String reacid:reacIdsAll){
                          if(!idToNodeTable.containsKey(reacid))continue; // the reaction is not in this compartment

                          //create commonmolecules, if any
                          ArrayList<String> reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
                          if(reacSpecies!=null){
                              for(String spid:reacSpecies){
                                  if(commonSpecies.contains(spid)){ //create common metobolites here id=metaboliteID+"-"+reactionID
                                      String speLabel=TableQueries.getSpeciesLabelBySpeciesId(repository,spid);
                                      if(speLabel==null || speLabel=="") speLabel="Unknown Species";
                                      Node speciesnode = graph.createNode();
                                      graph.setSize(speciesnode,nodeSizeX,nodeSizeY);
                                      idToNodeTable.put(spid+"-"+reacid,speciesnode);
                                      NodeToIdTable.put(speciesnode,spid+"-"+reacid);
                                  }
                              }


                          Node reacnode = idToNodeTable.get(reacid);
                          for(String speciesId:reacSpecies){ //TableQueries.getSpeciesIDListFromReactionID(repository,reacid)){
                              if(!idToNodeTable.containsKey(speciesId))continue;
                              Node specnode=idToNodeTable.get(speciesId);
                              Edge edge=graph.createEdge(reacnode,specnode);
                          }
                      }
//                      else{
//
//                      }
                      }
                 }

                NodeMap nodeId = graph.createNodeMap();
                NodeMap parentNodeId = graph.createNodeMap();
                NodeMap groupKey = graph.createNodeMap();

                graph.addDataProvider(Grouping.NODE_ID_DPKEY, nodeId);
                graph.addDataProvider(Grouping.PARENT_NODE_ID_DPKEY, parentNodeId);
                graph.addDataProvider(Grouping.GROUP_DPKEY, groupKey);
                    //mark a node as a group node
                groupKey.setBool(groupNode, true);
                for (NodeCursor nodeCursor =graph.nodes(); nodeCursor.ok(); nodeCursor.next()) {
                    Node node = nodeCursor.node();
                    String strnodeid=NodeToIdTable.get(node);
                    if (strnodeid!=null) {
                        nodeId.set(node, strnodeid);
                        parentNodeId.set(node, "groupNode");
//                if(compartment_hierarchy.get(compid).contains(strnodeid))
//                   groupKey.setBool(node, true);
                    }
                    else{
                        nodeId.set(node, "groupNode");
                    }
                }

//    HierarchicGroupLayouter layouter = new HierarchicGroupLayouter();
                SmartOrganicLayouter layouter = new SmartOrganicLayouter();
                layouter.setNodeEdgeOverlapAvoided(true);
                layouter.setNodeOverlapsAllowed(false);
                layouter.setDeterministic(true);
//            layouter.setMinimalNodeDistance(35);
//            OrganicLayouter layouter=new OrganicLayouter();
//            layouter.



//        OrganicLayouter layouter = new OrganicLayouter();
//        layouter.setInitialPlacement(OrganicLayouter.ALL);
//        layouter.setGroupBoundsCalculator();

//    layouter.setMinimalLayerDistance(0.0d);
//    layouter.setMinimalEdgeDistance(10.0d);

            new BufferedLayouter(layouter).doLayout(graph);

//
                for (NodeCursor nodeCursor =graph.nodes(); nodeCursor.ok(); nodeCursor.next()) {
                    Node node = nodeCursor.node();
                    String strnodeid=NodeToIdTable.get(node);
                    if (strnodeid!=null) {
                        if(reacIdsAll.contains(strnodeid)){ //if it's reaction node, we need reacit+"-"+compid to distinguish
                            computedPositions.put(strnodeid+"-"+compid,new PPoint(graph.getLocation(node).getX(),graph.getLocation(node).getY()));
                        }else
                        computedPositions.put(strnodeid,new PPoint(graph.getLocation(node).getX(),graph.getLocation(node).getY()));
//                }else{
                        //compartment node is the only one
//                    computedPositions.put(compid,new PPoint(graph.getSize(node).getWidth(),graph.getSize(node).getHeight()));
                    }
//            else if(strnodeid!=null){
//            !compartment_hierarchy.get(compid).contains(strnodeid) &&
//            }
                }
                Rectangle rec=graph.getBoundingBox();
                compartment_frame.get(compid).setWidth(rec.getWidth());
                compartment_frame.get(compid).setHeight(rec.getHeight());
//        HierarchyManager hierarchy = new HierarchyManager(graph);
//        for (NodeCursor nodeCursor =graph.nodes(); nodeCursor.ok(); nodeCursor.next()) {
//            Node node = nodeCursor.node();
//            if (!hierarchy.isGroupNode(node)) {
//                computedPositions.put(NodeToIdTable.get(node),new PPoint(graph.getLocation(node).getX(),graph.getLocation(node).getY()));
//            } else if(!NodeToIdTable.get(node).equalsIgnoreCase(compid)){
//                computedPositions.put(NodeToIdTable.get(node),new PPoint(graph.getLocation(node).getX(),graph.getLocation(node).getY()));
//            }else
//            computedPositions.put(compid,new PPoint(graph.getSize(node).getWidth(),graph.getSize(node).getHeight()));
//        }
//       final SmartOrganicLayouter module = new SmartOrganicLayouter();
//       module.doLayout(graph);
//            return computedPositions;
            }



    public static void ComputeElementsPositions_FromInner(PFrame curFrame,PathCaseRepository repository, String compid,HashMap<String, ArrayList<String>> compartment_hierarchy,HashMap<String, PFrame> compartment_frame,HashMap<String, PPoint> computedPositions, HashMap<String, ArrayList<String>> comp_reaction,PathCaseViewerMetabolomics.PathCaseViewMode mode)
           {
//            HashMap<String, PPoint> computedPositions=new HashMap<String, PPoint>();
               HashMap<String, Node> idToNodeTable = new HashMap<String, Node>();
               HashMap<Node,String> NodeToIdTable = new HashMap<Node,String>();
               DefaultLayoutGraph graph = new DefaultLayoutGraph();
               Node groupNode = graph.createNode();
               graph.setLocation(groupNode,new YPoint(0,0));
               graph.setSize(groupNode, curFrame.getWidth()-20,curFrame.getHeight()-20);

               //create inner compartment node
               if(compartment_hierarchy.containsKey(compid)){
                   for (String innercompid : compartment_hierarchy.get(compid))
                   {
                       PFrame innerFrame=compartment_frame.get(innercompid);
                       Node innergroupNode = graph.createNode();
//                graph.setLocation(innergroupNode,new YPoint(0,0));
                       graph.setSize(innergroupNode, innerFrame.getWidth(),innerFrame.getHeight());
//                    if(compartment_hierarchy.get(compid).size()==1)graph.setCenter(innergroupNode,curFrame.getCenterX(),curFrame.getCenterY());
                       idToNodeTable.put(innercompid,innergroupNode);
                       NodeToIdTable.put(innergroupNode,innercompid);
                   }
               }

              ArrayList<String> speciesIds = TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid);
               if(speciesIds!=null){
                   for(String speciesId:speciesIds){

                       boolean iscommon = TableQueries.getSpeciesCommonById(repository, speciesId);
                       if(!iscommon || (mode!=null && mode.showcommonmoleculesingraph)){//System.


                      String speLabel=TableQueries.getSpeciesLabelBySpeciesId(repository,speciesId);
                      if(speLabel==null || speLabel=="") speLabel="Unknown Species";
                      Node speciesnode = graph.createNode();
                      graph.setSize(speciesnode,nodeSizeX,nodeSizeY);
                      idToNodeTable.put(speciesId,speciesnode);
                      NodeToIdTable.put(speciesnode,speciesId);
                      }
                   }
               }


                 if(comp_reaction.containsKey(compid)){
                    for(String reacId:comp_reaction.get(compid)){
                      boolean iDrawReac=false;
                      ArrayList<String> reacSpecie = TableQueries.getSpeciesIDListFromReactionID(repository,reacId);
                      if(reacSpecie==null)iDrawReac=true;
                      else{
                          for(String spid:reacSpecie){
                              if(idToNodeTable.containsKey(spid)) {
                                  iDrawReac=true;
                                  break;
                              }
                          }
                      }

                      if(iDrawReac){
                       String reacLabel=TableQueries.getReactionNamebyID(repository,reacId);
                        if(reacLabel==null || reacLabel=="") reacLabel="Unknown Reactions";
                       Node reacnode = graph.createNode();
                        graph.setSize(reacnode,reactionSizeX,reactionSizeY);
                        idToNodeTable.put(reacId,reacnode);
                        NodeToIdTable.put(reacnode,reacId);

                        ArrayList<String> reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reacId);
                        for(String spid:reacSpecies){
//                           Node reac=idToNodeTable.get(reacid);
                           Node spec=idToNodeTable.get(spid);
//                           Edge edge=null;
                           if(spec !=null){
//                              Edge edge=
                              graph.createEdge(reacnode,spec);
                            }else{
                               if(compartment_hierarchy.containsKey(compid)){
                                   for (String innercompid : compartment_hierarchy.get(compid))
                                       {
                                           if(TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,innercompid)!=null && TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,innercompid).contains(spid)){
                                              Node comN=idToNodeTable.get(innercompid);
                                               graph.createEdge(reacnode,comN);
                                               break;
                                           }
                                       }
                               }
                           }
                        }
                    }
                    }
               }


               NodeMap nodeId = graph.createNodeMap();
               NodeMap parentNodeId = graph.createNodeMap();
               NodeMap groupKey = graph.createNodeMap();

               graph.addDataProvider(Grouping.NODE_ID_DPKEY, nodeId);
               graph.addDataProvider(Grouping.PARENT_NODE_ID_DPKEY, parentNodeId);
               graph.addDataProvider(Grouping.GROUP_DPKEY, groupKey);
                   //mark a node as a group node
               groupKey.setBool(groupNode, true);
               for (NodeCursor nodeCursor =graph.nodes(); nodeCursor.ok(); nodeCursor.next()) {
                   Node node = nodeCursor.node();
                   String strnodeid=NodeToIdTable.get(node);
                   if (strnodeid!=null) {
                       nodeId.set(node, strnodeid);
                       parentNodeId.set(node, "groupNode");
//                if(compartment_hierarchy.get(compid).contains(strnodeid))
//                   groupKey.setBool(node, true);
                   }
                   else{
                       nodeId.set(node, "groupNode");
                   }
               }

//    HierarchicGroupLayouter layouter = new HierarchicGroupLayouter();
               SmartOrganicLayouter layouter = new SmartOrganicLayouter();
               layouter.setNodeEdgeOverlapAvoided(true);
               layouter.setNodeOverlapsAllowed(false);
//               layouter.setSmartComponentLayoutEnabled(true);
               layouter.setMinimalNodeDistance(30);// 60 is better,but may add extra edge crossing
               layouter.setDeterministic(true);
//        OrganicLayouter layouter = new OrganicLayouter();
//        layouter.setInitialPlacement(OrganicLayouter.ALL);
//        layouter.setGroupBoundsCalculator();

//    layouter.setMinimalLayerDistance(0.0d);
//    layouter.setMinimalEdgeDistance(10.0d);

           new BufferedLayouter(layouter).doLayout(graph);

//
               for (NodeCursor nodeCursor =graph.nodes(); nodeCursor.ok(); nodeCursor.next()) {
                   Node node = nodeCursor.node();
                   String strnodeid=NodeToIdTable.get(node);
                   if (strnodeid!=null) {
                       computedPositions.put(strnodeid,new PPoint(graph.getLocation(node).getX(),graph.getLocation(node).getY()));
//                }else{
                       //compartment node is the only one
//                    computedPositions.put(compid,new PPoint(graph.getSize(node).getWidth(),graph.getSize(node).getHeight()));
                   }
//            else if(strnodeid!=null){
//            !compartment_hierarchy.get(compid).contains(strnodeid) &&
//            }
               }
               Rectangle rec=graph.getBoundingBox();
               compartment_frame.get(compid).setWidth(rec.getWidth());
               compartment_frame.get(compid).setHeight(rec.getHeight());
//        HierarchyManager hierarchy = new HierarchyManager(graph);
//        for (NodeCursor nodeCursor =graph.nodes(); nodeCursor.ok(); nodeCursor.next()) {
//            Node node = nodeCursor.node();
//            if (!hierarchy.isGroupNode(node)) {
//                computedPositions.put(NodeToIdTable.get(node),new PPoint(graph.getLocation(node).getX(),graph.getLocation(node).getY()));
//            } else if(!NodeToIdTable.get(node).equalsIgnoreCase(compid)){
//                computedPositions.put(NodeToIdTable.get(node),new PPoint(graph.getLocation(node).getX(),graph.getLocation(node).getY()));
//            }else
//            computedPositions.put(compid,new PPoint(graph.getSize(node).getWidth(),graph.getSize(node).getHeight()));
//        }
//       final SmartOrganicLayouter module = new SmartOrganicLayouter();
//       module.doLayout(graph);
//            return computedPositions;
           }

    static int getGoupNodesNum(Graph2D grp){
        HierarchyManager hm = HierarchyManager.getInstance(grp);
        int iR=0;
        for(Node n:grp.getNodeArray()){
            if(hm.isGroupNode(n))iR++;
        }
        return iR;
    }

   public static HashMap<Node, HashSet<String>> createGraphFromSysBioModelN(PathCaseRepository repository, Graph2D graph, String appWidth, String appHeight, String reactionGuid,double dx, double dy) {
      return createGraphFromSysBioModelN(repository,graph,appWidth,appHeight,reactionGuid,dx, dy,0);
   }


    //MQL visualization

    public static HashMap<Node, HashSet<String>> createGraphFromMQL(PathCaseRepository repository, Graph2D graph, String appWidth, String appHeight) {
//public static HashMap<Node, HashSet<String>> createGraphFromMQL(PathCaseRepository repository, Graph2D graph,PathCaseViewerMetabolomics.PathCaseViewMode mode, String appWidth, String appHeight, String reactionGuid,double dx, double dy,int iModelColor)
           HashMap<String, Node> moleculeidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> idToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> pathidToNodeTable = new HashMap<String, Node>();
           HashMap<Node, HashSet<String>> nodeToIdsTable = new HashMap<Node, HashSet<String>>();
//        HashMap<Node,String> nodeToId = new HashMap<Node, String>();
//03/18/09 added for SysBio Model Visualization By Xinjian
           HashMap<String, Node> modelidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> compartmentidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> speciesidToNodeTable = new HashMap<String, Node>();
        HashMap<String,ArrayList<Integer>> iComp_Elements = new HashMap<String, ArrayList<Integer>>();
//           HashMap<String, Node> reactionidToNodeTable = new HashMap<String, Node>();
//           HashMap<String, Node> reactionspeciesidToNodeTable = new HashMap<String, Node>();

//           HashSet<String> SubstrateAndProductList = new HashSet<String>();
           HashMap<String, ArrayList<String>> compartment_hierarchy  = new HashMap<String, ArrayList<String>>();
        HashMap<String, String> compartment_parent  = new HashMap<String, String>();
        HashMap<String, PFrame> compartment_frame  = new HashMap<String, PFrame>();
        HashMap<String, PPoint> computedPositions  = new HashMap<String, PPoint>();
        HashMap<String, Node> commonTransportMetabolite  = new HashMap<String, Node>();
           HashMap<String, ArrayList<String>> comp_reaction  = new HashMap<String, ArrayList<String>>();
//q begin
           HierarchyManager hm = new HierarchyManager(graph);
           HashMap<String, NodeList> compartmentQListMap=new HashMap<String, NodeList>(); //q
            int appletwidth=Integer.parseInt(appWidth),appletheight=Integer.parseInt(appHeight);
        double initialX=200,initialY=200;//this value does not matter
        ArrayList<String> toCompuComs=new ArrayList<String>();
//q end
        SetAcceptableTissueColors();

        String pathwayid="";
        if(TableQueries.getPathwaysIdListInRepository(repository).size()==1) pathwayid=TableQueries.getPathwaysIdListInRepository(repository).iterator().next();

        //suppose, and in fact, there's only one root compartment, we put it just under "root"

         ArrayList<String> allCompIDs=TableQueries.getCompartmentIDListInRepository(repository);
         ArrayList<String> allCompIDsButRoot=TableQueries.getCompartmentIDListInRepository(repository);
        for(String compid:allCompIDs){
            String comOutside=TableQueries.getCompartmnetOutsidebyCompartmentID(repository,compid);
            if (comOutside.equals(compid)) {
                   if(!compartment_hierarchy.containsKey("root")){
                      compartment_hierarchy.put("root",new ArrayList<String>());
                  }
                compartment_hierarchy.get("root").add(compid);
                compartment_parent.put(compid,"root");
                allCompIDsButRoot.remove(compid);
                break;// there should be only one compartment;
            }
        }

//        ArrayList<String> allCompIDs=TableQueries.getCompartmentIDListInRepository(repository);
////         ArrayList<String> allCompIDsButRoot=TableQueries.getCompartmentIDListInRepository(repository);;
//            for(String compid:allCompIDs){
//                String comOutside=TableQueries.getCompartmnetOutsidebyCompartmentID(repository,compid);
//                if (comOutside.equals("")||(comOutside.equals("00000000-0000-0000-0000-000000000000"))) { //"00-0-0.." is null value in db.
//                       if(!compartment_hierarchy.containsKey("root")){
//                          compartment_hierarchy.put("root",new ArrayList<String>());
//                      }
//                    compartment_hierarchy.get("root").add(compid);
//                    compartment_parent.put(compid,"root");
////                allCompIDsButRoot.remove(compid);
//                }else{
//                   if(!compartment_hierarchy.containsKey(comOutside)){
//                      compartment_hierarchy.put(comOutside,new ArrayList<String>());
//                  }
//                    compartment_hierarchy.get(comOutside).add(compid);
//                    compartment_parent.put(compid,comOutside);
//               }
//            }
        ArrayList<String> calculatedComps=new ArrayList<String>();
        for(String compid:allCompIDsButRoot){//TableQueries.getCompartmentsByPathwayId(repository,pathwayid)){---this function does not return correct result since bugs exist in XML
            //Since there's no root compartment under pathways section, we do not need to judge here, but have to find the root before or after.
            String comOutside=TableQueries.getCompartmnetOutsidebyCompartmentID(repository,compid);
               if(!compartment_hierarchy.containsKey(comOutside)){
                  compartment_hierarchy.put(comOutside,new ArrayList<String>());
              }
                compartment_hierarchy.get(comOutside).add(compid);
                compartment_parent.put(compid,comOutside);
        }

//       ArrayList<String> compartmentIds = TableQueries.getCompartmentIDListInRepository(repository);
//       ArrayList<String> reactionIds = TableQueries.getReactionIDListInRepository(repository);
//        for (String reacid : reactionIds ) {
//            String comIdForReac=null;
//               //finding corresponding speciesId from reaction_species table, then find compartment id corresponding to speciesid. Get reaction---compartment relationship
//            ArrayList<String> speciesIds = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
//            if(speciesIds!=null){
//                for(String spec:speciesIds){
//                    comIdForReac=TableQueries.getCompartmentIDBySpeciesID(repository,spec);
//                    if(comIdForReac!=null) break;
//                }
//                if(comIdForReac!=null){
//                    if(!comp_reaction.containsKey(comIdForReac)) comp_reaction.put(comIdForReac,new ArrayList<String>());
//                    comp_reaction.get(comIdForReac).add(reacid);
//                }
//            }
//        }
//        int iCompDepth=compartment_hierarchy.keySet().size();

        //algorithm; start from most inner compartment
        for (String compid :allCompIDs) {
            if(compartment_hierarchy.containsKey(compid)) continue;
            toCompuComs.add(compid); // add all innerest compartmens as starting coms of creating elements
            PFrame comFrame=new PFrame(new PPoint(0,0),new PPoint(initialX,initialY));
            compartment_frame.put(compid,comFrame);
            ComputeElementsPositions_FromInner(comFrame,repository,compid,compartment_hierarchy,compartment_frame,computedPositions);
            calculatedComps.add(compid);
        }

//        boolean bpass=false;
         while(!(calculatedComps.size()==1 && calculatedComps.get(0)=="root")){
            for(String caledcom:calculatedComps){
                boolean bpass=false;
                String paOfcaledcom=compartment_parent.get(caledcom);
                //calculate their(calculated compartments--elements' positon) parent
                if(!calculatedComps.contains(paOfcaledcom)){
                    for(String comp:compartment_hierarchy.get(paOfcaledcom)){
                        //if the parent has uncalculated compartment, just pass
                        if(!calculatedComps.contains(comp)){
                            bpass=true; break;
                        }
                    }
                if(bpass) continue;
                    //calculate this compartment's parent.
                    PFrame comFrame=new PFrame(new PPoint(0,0),new PPoint(initialX,initialY));
                    compartment_frame.put(paOfcaledcom,comFrame);
                    ComputeElementsPositions_FromInner(comFrame,repository,paOfcaledcom,compartment_hierarchy,compartment_frame,computedPositions);
                    calculatedComps.add(paOfcaledcom);
                    //remove it and its siblings from calcualted Comps
                    for(String toRemove:compartment_hierarchy.get(paOfcaledcom)){
                        if(calculatedComps.contains(toRemove)) calculatedComps.remove(toRemove);
                    }
                    break;
                }
            }
        }
        computedPositions.put("root",new PPoint(0,0));

        //recalculate each compartment's start point, from the outest one
        ArrayList<String> compHasChildren=new ArrayList<String>();
        compHasChildren.add(compartment_hierarchy.get("root").get(0)); //since only one root in xml file for each pathway.
        while(!compHasChildren.isEmpty()){
            for(String comp:compHasChildren){
                for(String subcom:compartment_hierarchy.get(comp)){
                    computedPositions.get(subcom).setOffsetX(computedPositions.get(comp).getX());
                    computedPositions.get(subcom).setOffsetY(computedPositions.get(comp).getY());
//                    compartment_frame.get(subcom).setOffX(computedPositions.get(comp).getX()); //not necessarily to be used
//                    compartment_frame.get(subcom).setOffY(computedPositions.get(comp).getY()); //not necessarily to be used
                    if(compartment_hierarchy.containsKey(subcom))compHasChildren.add(subcom);
                }
                compHasChildren.remove(comp);
                break;
//                if(compHasChildren.isEmpty()) break;
            }
        }

//        ArrayList<String> computedComps= new ArrayList<String>();
//             ArrayList<String> toComputeComps= new ArrayList<String>();
//             computedComps.add("root");
//             toComputeComps.addAll(allCompIDs);
//             toComputeComps.remove("root");
//             while(toComputeComps.size()>0){
//                 for(String cid:toComputeComps){
//                     if(!computedComps.contains(compartment_parent.get(cid))) continue;
//                     computedPositions.get(cid).setOffsetX(computedPositions.get(compartment_parent.get(cid)).getX());
//                     computedPositions.get(cid).setOffsetY(computedPositions.get(compartment_parent.get(cid)).getY());
//                     computedComps.add(cid);
//                     toComputeComps.remove(cid);
//                     break;
//                 }
//             }

        //creating nodes first.
       calculatedComps.clear();
        ArrayList<String> commonSpecies = new ArrayList<String>();
        while(!toCompuComs.isEmpty()){
            for(String compid:toCompuComs){

//                if(compartment_hierarchy.containsKey(compid)) continue;
            //draw compartments part and group them, starting from most inner one.
            String compartmentlabel = "";
            double offX=computedPositions.get(compid).getX(),offY=computedPositions.get(compid).getY();
            if(!compartmentQListMap.containsKey(compid))
                {
                     compartmentQListMap.put(compid,new NodeList());
                }

            ArrayList<String> speciesIds = TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid);

            if(speciesIds!=null){
                for(String speciesId:speciesIds){
                    if(TableQueries.getSpeciesCommonById(repository,speciesId)){ //if common, donot create node here
                       commonSpecies.add(speciesId);
                   }else{
                    String speLabel=TableQueries.getSpeciesLabelBySpeciesId(repository,speciesId);
                    if(speLabel==null || speLabel=="") speLabel="Unknown Species";
                    Node speciesnode = graph.createNode();
//                       nodeToId.put(speciesnode,speciesId);
//                       speCount++;
                    ShapeNodeRealizer spenr;
                    spenr = getSubstrateProductShapeNodeRealizer(speLabel,false,offX+computedPositions.get(speciesId).getX(),offY+computedPositions.get(speciesId).getY());     //only a few are common in previous database, which means very popular, exist in most pathways.
                    graph.setRealizer(speciesnode, spenr);
                    compartmentQListMap.get(compid).add(speciesnode);
                    idToNodeTable.put(speciesId,speciesnode);
                    HashSet<String> spelist = new HashSet<String>();
                    spelist.add(speciesId);
                    nodeToIdsTable.put(speciesnode, spelist);
                    }
                }
            }

            ArrayList<String> reacIds = TableQueries.getReactionsByCompartmentId(repository,compid);
              if(reacIds!=null){
                    for(String reacid:reacIds){
                      ArrayList<String> reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
                         if(reacSpecies!=null){
                          for(String spid:reacSpecies){
                              if(!TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid).contains(spid))
                                continue;
                              if(commonSpecies.contains(spid)){ //create common metobolites here id=metaboliteID+"-"+reactionID+compid
                                  String speLabel=TableQueries.getSpeciesLabelBySpeciesId(repository,spid);
                                  if(speLabel==null || speLabel=="") speLabel="Unknown Species";
                                  Node speciesnode = graph.createNode();
                                    ShapeNodeRealizer spenr;
                                    spenr = getSubstrateProductShapeNodeRealizer(speLabel,false,offX+computedPositions.get(spid+"-"+reacid).getX(),offY+computedPositions.get(spid+"-"+reacid).getY());     //only a few are common in previous database, which means very popular, exist in most pathways.
                                    graph.setRealizer(speciesnode, spenr);
                                    compartmentQListMap.get(compid).add(speciesnode);
                                    idToNodeTable.put(spid+"-"+reacid+"-"+compid,speciesnode);
                                    commonTransportMetabolite.put(spid+"-"+compid,speciesnode);
                                   HashSet<String> spelist = new HashSet<String>();
                                  spelist.add(spid+"-"+reacid+"-"+compid);
                                  nodeToIdsTable.put(speciesnode,spelist);
                              }
                          }
                        }

                        //Do not use reaction name, but use enzyme name;
//                       String reacLabel=TableQueries.getReactionNamebyID(repository,reacId);

                        String reacLabel="<html>";
                        ArrayList<String> reacEnzIDs=TableQueries.getReactionEnzymebyID(repository,compid,reacid);
                        if(reacEnzIDs!=null){
                            if(reacEnzIDs.size()==0)reacLabel+="No(Enzyme)name Reaction";
                            else{
                                for(String enlabel:reacEnzIDs){
                                    reacLabel+=(TableQueries.getEnzymeNamebyID(repository,enlabel)+"<br>");
                                }
                            }
                            reacLabel+="</html>";
                        }else
                        reacLabel="Unknown Reaction";
//                       if(reacLabel==null || reacLabel=="") reacLabel="Unknown Reactions";
                       Node reacnode = graph.createNode();
                       ShapeNodeRealizer reacr;

                       reacr = getGenericProcessShapeNodeRealizer(reacLabel,offX+computedPositions.get(reacid+"-"+compid).getX(),offY+computedPositions.get(reacid+"-"+compid).getY());//getRandomPoint(curFrame,compid,compartment_hierarchy,compartment_frame));
                       graph.setRealizer(reacnode, reacr);
                       compartmentQListMap.get(compid).add(reacnode);
                       idToNodeTable.put(reacid+"-"+compid,reacnode);
                       HashSet<String> reaclist = new HashSet<String>();
                       reaclist.add(reacid+"-"+compid);
                       nodeToIdsTable.put(reacnode, reaclist);
                    }
               }


            //add two fake nodes at the corners.

            Node groupNode;
            compartmentlabel=TableQueries.getCompartmnetNamebyCompartmentID(repository,compid);
            if (compartmentlabel.equals("")) compartmentlabel = "Unknown";
            groupNode =hm.createGroupNode(graph);
//             HashSet<String> comlist = new HashSet<String>();
//             comlist.add(compartment_hierarchy.get("root").get(0));
            ShapeNodeRealizer nrGroup = getTissueShapeNodeRealizerSB(compartmentlabel,computedPositions.get(compid).getX(),computedPositions.get(compid).getY(),compartment_frame.get(compid).getWidth(),compartment_frame.get(compid).getHeight());
            graph.setRealizer(groupNode, nrGroup);
            HashSet<String> comlist = new HashSet<String>();
            comlist.add(compid);
            nodeToIdsTable.put(groupNode,comlist);
            idToNodeTable.put(compid,groupNode);

                Node fakenode1 = graph.createNode();
                ShapeNodeRealizer fakeSpen1=getFakedShapeNodeRealizer(computedPositions.get(compid).getX()+3,computedPositions.get(compid).getY()+3);
                graph.setRealizer(fakenode1, fakeSpen1);
                 Node fakenode2 = graph.createNode();
                ShapeNodeRealizer fakeSpen2=getFakedShapeNodeRealizer(computedPositions.get(compid).getX()+ compartment_frame.get(compid).getWidth()-3,computedPositions.get(compid).getY()+ compartment_frame.get(compid).getHeight()-3);
                graph.setRealizer(fakenode2, fakeSpen2);
                 idToNodeTable.put(fakenode1.toString(),fakenode1);
                idToNodeTable.put(fakenode2.toString(),fakenode2);
                compartmentQListMap.get(compid).add(fakenode1);
                compartmentQListMap.get(compid).add(fakenode2);

            hm.groupSubgraph(compartmentQListMap.get(compid),groupNode);

            if(!compartmentQListMap.containsKey(compartment_parent.get(compid)))
                {
                     compartmentQListMap.put(compartment_parent.get(compid),new NodeList());
                }
               compartmentQListMap.get(compartment_parent.get(compid)).add(groupNode);

                calculatedComps.add(compid);
                toCompuComs.remove(compid);
                if(compartment_parent.get(compid)!="root"){
                    String paOfcaledcom=compartment_parent.get(compid);
                    if(!toCompuComs.contains(paOfcaledcom)){
                        boolean bpass=false;
                        for(String comp:compartment_hierarchy.get(paOfcaledcom)){
                        //if the parent has uncalculated compartment, just pass
                            if(!calculatedComps.contains(comp)){ bpass=true; break;}
                        }
                        if(!bpass) {
                            toCompuComs.add(paOfcaledcom);
                            break;
                        }
                    }
                }else break;
                if(toCompuComs.isEmpty()) break;
                else break;
            }
        }


        //add edges
       ArrayList<String> reactionIds = TableQueries.getReactionIDListInRepository(repository);
       for(String reacid:reactionIds){
          ArrayList<String> reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
//           String compIdOfReac="";
           //get all compartments contain reacid
           ArrayList<String> compsContainReacid= new ArrayList<String>();
           for(String strcom:allCompIDs){
               if(TableQueries.getReactionsByCompartmentId(repository,strcom)==null)continue;
               if(TableQueries.getReactionsByCompartmentId(repository,strcom).contains(reacid))compsContainReacid.add(strcom);
           }

           if(reacSpecies!=null){
                //compsContainReacid is not null since there must be at least one compartment
               for(String strcom:compsContainReacid){
                   Node reac=idToNodeTable.get(reacid+"-"+strcom);
                   if(reac==null)continue;
                   boolean isReversible=TableQueries.getReactionReversibleInRepository(repository,reacid);
                  for(String spid:reacSpecies){
                      Node spec=null;
                      boolean iTrans=false;
                      if(!TableQueries.getReactionTransportableInRepository(repository,reacid)){  // 'True' means  not transported reaction...           To get transported propert, need to change reactiontable class with parser
                            if(commonSpecies.contains(spid)){
                                if(!TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,strcom).contains(spid))
                                    continue;
                                spec=idToNodeTable.get(spid+"-"+reacid+"-"+strcom);
                            }else{
                                if(!TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,strcom).contains(spid))
                                    continue;
                                spec=idToNodeTable.get(spid);
                            }
                      }else{
                          //this is for transport reaction
                          if(commonSpecies.contains(spid)){
                              if(!TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,strcom).contains(spid))
                                {   //find which compartment contains  metabolite of the transport reaction  //  try all children compartments first
                                    ArrayList<String> childrencomps=compartment_hierarchy.get(strcom);
                                    if(childrencomps!=null){
                                        for(String chcompid:childrencomps){
                                            if(TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,chcompid)!=null && TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,chcompid).contains(spid))
                                            { iTrans=true;
                                                spec = commonTransportMetabolite.get(spid+"-"+chcompid);
                                                break;}
                                        }
                                    }
    //                                  then try all ancestors compartment
                                    if(!iTrans){
                                        String parentcomp=compartment_parent.get(strcom);
                                        if(parentcomp==null) continue;
                                        while(!parentcomp.equalsIgnoreCase(compartment_parent.get(parentcomp))){
                                            if(TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,parentcomp)!=null && TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,parentcomp).contains(spid))
                                            {
                                                iTrans=true;
                                                spec = commonTransportMetabolite.get(spid+"-"+parentcomp);
                                                break;
                                            }
                                            parentcomp=compartment_parent.get(parentcomp);
                                            if(parentcomp==null) break;
                                        }
                                    }

                                    if(!iTrans){ //if reaction and metabolites are in hierachy compartments, draw transport reaction lines. else continue
                                        continue;
                                    }
                                }else
//                                if(commonSpecies.contains(spid)){
                                spec=idToNodeTable.get(spid+"-"+reacid+"-"+strcom);             //for common metabolites
//                                }else
//                                    spec=idToNodeTable.get(spid);
                          }
                          else{
                              if(!TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,strcom).contains(spid))
                                {   //find which compartment contains  metabolite of the transport reaction  //  try all children compartments first
                                    ArrayList<String> childrencomps=compartment_hierarchy.get(strcom);
                                    if(childrencomps!=null){
                                        for(String chcompid:childrencomps){
                                            if(TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,chcompid)!=null && TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,chcompid).contains(spid))
                                            { iTrans=true; break;}
                                        }
                                    }
    //                                  then try all ancestors compartment
                                    if(!iTrans){
                                        String parentcomp=compartment_parent.get(strcom);
                                        if(parentcomp==null) continue;
                                        while(!parentcomp.equalsIgnoreCase(compartment_parent.get(parentcomp))){
                                            if(TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,parentcomp)!=null && TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,parentcomp).contains(spid))
                                            {
                                                iTrans=true;
                                                break;
                                            }
                                            parentcomp=compartment_parent.get(parentcomp);
                                            if(parentcomp==null) break;
                                        }
                                    }

                                    if(!iTrans){ //if reaction and metabolites are in hierachy compartments, draw transport reaction lines. else continue
                                        continue;
                                    }
                                }
//                                if(commonSpecies.contains(spid)){
//                                    spec=idToNodeTable.get(spid+"-"+reacid+"-"+strcom);             //for common metabolites
//                                }else
                                    spec=idToNodeTable.get(spid);
                        }
                      }

                      Edge edge=null;
                      if(reac!=null && spec !=null)
                      {
                          if(true){
                              String speRole=TableQueries.getSpeciesRole(repository,reacid,spid);
                              edge=graph.createEdge(reac,spec);
                              EdgeRealizer er =graph.getRealizer(edge);
                              if(isReversible){
                                  //just draw two ways arrow, do not care substrate or product
                                  if(speRole.equalsIgnoreCase("product")||speRole.equalsIgnoreCase("substrate")){
                                      er.setTargetArrow(Arrow.STANDARD);
                                      er.setSourceArrow(Arrow.STANDARD);
                                  }
                              }else{
                                  if(speRole.equalsIgnoreCase("product")){
            //                              er= new GenericEdgeRealizer();
                                      er.setTargetArrow(Arrow.STANDARD);
                                  }else if(speRole.equalsIgnoreCase("substrate")){
            //                              er= new GenericEdgeRealizer();
                                      er.setSourceArrow(Arrow.STANDARD);
                                  }
                              }
                              if(iTrans){
                                 er.setLineType(LineType.DOTTED_3);
                              }
                              if(speRole.equalsIgnoreCase("activator")){
                                  er.setSourceArrow(Arrow.DIAMOND);
                                  er.setLineColor(Color.green);
                              }else if(speRole.equalsIgnoreCase("inhibitor")){
                                  er.setSourceArrow(Arrow.DIAMOND);
                                  er.setLineColor(Color.red);
                              }else if(speRole.equalsIgnoreCase("cofactor in")){
                                  er=new ArcEdgeRealizer();
                                  er.setSourceArrow(Arrow.DELTA);
                                  graph.setRealizer(edge,er);
                              }else if(speRole.equalsIgnoreCase("cofactor out")){
                                  er=new ArcEdgeRealizer();
                                  er.setTargetArrow(Arrow.DELTA);
                                  graph.setRealizer(edge,er);
                              }
                          }

                      }

                  }
                }
               }
          }

        return nodeToIdsTable;
       }



 public static HashMap<Node, HashSet<String>> createGraphFromMQL_old(PathCaseRepository repository, Graph2D graph,PathCaseViewerMetabolomics.PathCaseViewMode mode, String appWidth, String appHeight, String reactionGuid,double dx, double dy,int iModelColor) {
    HashMap<String, Node> idToNodeTable = new HashMap<String, Node>();
    HashMap<Node, HashSet<String>> nodeToIdsTable = new HashMap<Node, HashSet<String>>();
    HashMap<String, ArrayList<String>> compartment_hierarchy  = new HashMap<String, ArrayList<String>>();
    HashMap<String, String> compartment_parent  = new HashMap<String, String>();
    HashMap<String, PFrame> compartment_frame  = new HashMap<String, PFrame>();
    HashMap<String, PPoint> computedPositions  = new HashMap<String, PPoint>();
    HashMap<String, ArrayList<String>> comp_reaction  = new HashMap<String, ArrayList<String>>();
    HierarchyManager hm;

    if(graph.getHierarchyManager()==null)
            hm = new HierarchyManager(graph);
    else
            hm = graph.getHierarchyManager();

    HashMap<String, NodeList> compartmentQListMap=new HashMap<String, NodeList>(); //q
    int appletwidth=Integer.parseInt(appWidth),appletheight=Integer.parseInt(appHeight);
    double initialX=200,initialY=200;//this value does not matter
    ArrayList<String> toCompuComs=new ArrayList<String>();

     /*get all pathway's hierarchy information
     start from pathway table to get all pathways
     for each pathway get and fill into the compartment hierachies, then we get one hierachy for all pathways.
     then we draw as "createGraphFromSysBioModelN", for all shared metabolites with one color, for each pathway, metabolite and reaction use one color.       */

         ArrayList<String> allCompIDs=TableQueries.getCompartmentIDListInRepository(repository);
//         ArrayList<String> allCompIDsButRoot=TableQueries.getCompartmentIDListInRepository(repository);;
            for(String compid:allCompIDs){
                String comOutside=TableQueries.getCompartmnetOutsidebyCompartmentID(repository,compid);
                if (comOutside.equals("")||(comOutside.equals("00000000-0000-0000-0000-000000000000"))) { //"00-0-0.." is null value in db.
                       if(!compartment_hierarchy.containsKey("root")){
                          compartment_hierarchy.put("root",new ArrayList<String>());
                      }
                    compartment_hierarchy.get("root").add(compid);
                    compartment_parent.put(compid,"root");
//                allCompIDsButRoot.remove(compid);
                }else{
                   if(!compartment_hierarchy.containsKey(comOutside)){
                      compartment_hierarchy.put(comOutside,new ArrayList<String>());
                  }
                    compartment_hierarchy.get(comOutside).add(compid);
                    compartment_parent.put(compid,comOutside);
               }
            }
//     ArrayList<String> allPathwayIDs=TableQueries.getPathwaysIdListInRepository(repository);
//
//     for (String pwid : allPathwayIDs){
//         //find hierarchy
//
//     }





     return nodeToIdsTable;
 }

    public static HashMap<Node, HashSet<String>> createGraphFromSysBioModelN(PathCaseRepository repository, Graph2D graph,PathCaseViewerMetabolomics.PathCaseViewMode mode, String appWidth, String appHeight, String reactionGuid,double dx, double dy,int iModelColor) {
        setStackTrace(Thread.currentThread().getStackTrace());   
        HashMap<String, Node> moleculeidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> idToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> pathidToNodeTable = new HashMap<String, Node>();
           HashMap<Node, HashSet<String>> nodeToIdsTable = new HashMap<Node, HashSet<String>>();

           HashMap<String, Node> modelidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> compartmentidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> speciesidToNodeTable = new HashMap<String, Node>();
           HashMap<String,ArrayList<Integer>> iComp_Elements = new HashMap<String, ArrayList<Integer>>();
           HashMap<String, ArrayList<String>> compartment_hierarchy  = new HashMap<String, ArrayList<String>>();
            HashMap<String, String> compartment_parent  = new HashMap<String, String>();
            HashMap<String, PFrame> compartment_frame  = new HashMap<String, PFrame>();
            HashMap<String, PPoint> computedPositions  = new HashMap<String, PPoint>();
           HashMap<String, ArrayList<String>> comp_reaction  = new HashMap<String, ArrayList<String>>();
            HashMap<String, ArrayList<String>> comp_species  = new HashMap<String, ArrayList<String>>();
//q begin
           HierarchyManager hm;

        if(graph.getHierarchyManager()==null)
                hm = new HierarchyManager(graph);
        else
                hm = graph.getHierarchyManager();

           HashMap<String, NodeList> compartmentQListMap=new HashMap<String, NodeList>(); //q
            int appletwidth=Integer.parseInt(appWidth),appletheight=Integer.parseInt(appHeight);
        double initialX=200,initialY=200;//this value does not matter
        ArrayList<String> toCompuComs=new ArrayList<String>();
//q end

        SetAcceptableTissueColors();
        if(iModelColor==0)selectedTissueColorLast=selectedTissueColor; //if model different with previous one, remember start value of color for this model, then if next model is same as this model, start with selectedTissueColorLast
        else selectedTissueColor=selectedTissueColorLast;

        ArrayList<String> compartmentIds = TableQueries.getCompartmentIDListInRepository(repository);
        ArrayList<String> reactionIds = TableQueries.getReactionIDListInRepository(repository);

        ArrayList<String> reactionZombi=new ArrayList<String>();

        int t=0;
        for (String reacid : reactionIds ) {
            t++;
            if(t==710)
            {
                t++;t--;
            }
            if(t==730)
            {
                t++;t--;
            }

            if(t==780)
            {
                t++;t--;
            }
            if(t==790)
            {
                t++;t--;
            }

            String comIdForReac=null;

               //finding corresponding speciesId from reaction_species table, then find compartment id corresponding to speciesid. Get reaction---compartment relationship
            ArrayList<String> speciesIds = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
            if(speciesIds!=null){
                for(String spec:speciesIds){
                    comIdForReac=TableQueries.getCompartmentIDBySpeciesID(repository,spec);
                    if(comIdForReac!=null) break;
                }
                if(comIdForReac!=null){
                    if(!comp_reaction.containsKey(comIdForReac)) comp_reaction.put(comIdForReac,new ArrayList<String>());
                    comp_reaction.get(comIdForReac).add(reacid);
                }
            } else
            reactionZombi.add(reacid);
        }

        ArrayList<String> allCompIDs=TableQueries.getCompartmentIDListInRepository(repository);
//         ArrayList<String> allCompIDsButRoot=TableQueries.getCompartmentIDListInRepository(repository);;
        for(String compid:allCompIDs){
            String comOutside=TableQueries.getCompartmnetOutsidebyCompartmentID(repository,compid);
            if (comOutside.equals("")||(comOutside.equals("00000000-0000-0000-0000-000000000000"))) { //"00-0-0.." is null value in db.
                   if(!compartment_hierarchy.containsKey("root")){
                      compartment_hierarchy.put("root",new ArrayList<String>());
                  }
                compartment_hierarchy.get("root").add(compid);
                compartment_parent.put(compid,"root");
//                allCompIDsButRoot.remove(compid);                
            }else{
               if(!compartment_hierarchy.containsKey(comOutside)){
                  compartment_hierarchy.put(comOutside,new ArrayList<String>());
              }
                compartment_hierarchy.get(comOutside).add(compid);
                compartment_parent.put(compid,comOutside);
           }
        }

        ArrayList<String> calculatedComps=new ArrayList<String>();
        //algorithm; start from most inner compartment
        for (String compid :allCompIDs) {
            if(compartment_hierarchy.containsKey(compid)) continue;
            toCompuComs.add(compid); // add all innerest compartmens as starting compartments of creating elements
            PFrame comFrame=new PFrame(new PPoint(0,0),new PPoint(initialX,initialY));
            compartment_frame.put(compid,comFrame);
            ComputeElementsPositions_FromInner(comFrame,repository,compid,compartment_hierarchy,compartment_frame,computedPositions,comp_reaction,mode);
            calculatedComps.add(compid);
        }
        
        boolean bpass=false;
         while(!(calculatedComps.size()==1 && calculatedComps.get(0)=="root")){
            for(String caledcom:calculatedComps){
                bpass=false;
                String paOfcaledcom=compartment_parent.get(caledcom);
                //calculate their(calculated compartments--elements' positon) parent
                if(!calculatedComps.contains(paOfcaledcom)){
                        for(String comp:compartment_hierarchy.get(paOfcaledcom)){
                        //if the parent has uncalculated compartment, just pass
                        if(!calculatedComps.contains(comp)){ bpass=true; break;}
                }
                if(bpass) continue;
                    //calculate this compartment's parent.
                    PFrame comFrame=new PFrame(new PPoint(0,0),new PPoint(initialX,initialY));
                    compartment_frame.put(paOfcaledcom,comFrame);
                    ComputeElementsPositions_FromInner(comFrame,repository,paOfcaledcom,compartment_hierarchy,compartment_frame,computedPositions,comp_reaction,mode);
                    calculatedComps.add(paOfcaledcom);
                    //remove it and its siblings from calcualted Comps
                    for(String toRemove:compartment_hierarchy.get(paOfcaledcom)){
                        if(calculatedComps.contains(toRemove)) calculatedComps.remove(toRemove);
                    }
                    break;
                }
            }
        } // root was not computed
        computedPositions.put("root",new PPoint(0,0));

        //recalculate each compartment's start point, from the outest one
        /*ArrayList<String> compHasChildren=new ArrayList<String>();
//        compHasChildren.add(compartment_hierarchy.get("root").get(0)); //since only one root in xml file for each pathway.
        compHasChildren.add("root");
        while(!compHasChildren.isEmpty()){
            ArrayList<String> addList=new  ArrayList<String>();
            for(String comp:compHasChildren){
                for(String subcom:compartment_hierarchy.get(comp)){
                    computedPositions.get(subcom).setOffsetX(computedPositions.get(comp).getX());
                    computedPositions.get(subcom).setOffsetY(computedPositions.get(comp).getY());
//                    compartment_frame.get(subcom).setOffX(computedPositions.get(comp).getX()); //not necessarily to be used
//                    compartment_frame.get(subcom).setOffY(computedPositions.get(comp).getY()); //not necessarily to be used
                    if(compartment_hierarchy.containsKey(subcom)&&!compHasChildren.contains(subcom)){
                        compHasChildren.add(subcom);
                    }
                }
                compHasChildren.remove(comp);
                if(compHasChildren.isEmpty()) break;
            }
        }   */

        ArrayList<String> computedComps= new ArrayList<String>();
        ArrayList<String> toComputeComps= new ArrayList<String>();
        computedComps.add("root");
        toComputeComps.addAll(allCompIDs);
        toComputeComps.remove("root");
        while(toComputeComps.size()>0){
            for(String cid:toComputeComps){
                if(!computedComps.contains(compartment_parent.get(cid))) continue;
                computedPositions.get(cid).setOffsetX(computedPositions.get(compartment_parent.get(cid)).getX());
                computedPositions.get(cid).setOffsetY(computedPositions.get(compartment_parent.get(cid)).getY());
                computedComps.add(cid);
                toComputeComps.remove(cid);
                break;
            }
        }

        //creating nodes first.
       calculatedComps.clear();
        //process reaction visualization:
//        System.out.println("reactionGuid.."+reactionGuid+":"+reactionGuid.equalsIgnoreCase("")+"::"+(reactionGuid==""));
        ArrayList<String> reacSpecies =null;
        if(reactionGuid!=null && !reactionGuid.equalsIgnoreCase("")){
            System.out.println("In..");
            reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reactionGuid);
        }

//        System.out.println("Creating Graph3..");

         ArrayList<String> tmptoCompuComs=new ArrayList<String>();

//        while(!toCompuComs.isEmpty()){
            while(toCompuComs.size()!=tmptoCompuComs.size()){
            for(String compid:toCompuComs){
//                String compid=toCompuComs.get(0);
//                if(compartment_hierarchy.containsKey(compid)) continue;
            //draw compartments part and group them, starting from most inner one.
            String compartmentlabel = "";
            double offX=computedPositions.get(compid).getX(),offY=computedPositions.get(compid).getY();
            if(!compartmentQListMap.containsKey(compid))
                {
                     compartmentQListMap.put(compid,new NodeList());
                }

//        System.out.println("Before creating nodes,the model has comparments:"+getGoupNodesNum(graph));

         ArrayList<String> speciesIds = TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid);

            if(speciesIds!=null){
                comp_species.put(compid,speciesIds);
                for(String speciesId:speciesIds){
                    if(idToNodeTable.containsKey(speciesId)) continue;
                    if(reactionGuid.equalsIgnoreCase("") ||(!reactionGuid.equalsIgnoreCase("")&& reacSpecies.contains(speciesId))){
                        boolean iscommon = TableQueries.getSpeciesCommonById(repository, speciesId);

                        if(!iscommon || (mode!=null && mode.showcommonmoleculesingraph)){  //System.out.println("This is a common molecular.................");
                            String speLabel=TableQueries.getSpeciesLabelBySpeciesId(repository,speciesId);
//                            String speID=TableQueries.getSBMLIDBySpeciesId(repository,speciesId);
//                            if(!speLabel.equalsIgnoreCase(speID))
//                                speLabel+=("  <br> SBMLid:"+speID);
                            if(speLabel==null || speLabel=="") speLabel="Unknown Species";
                            Node speciesnode = graph.createNode();
        //                       nodeToId.put(speciesnode,speciesId);
        //                       speCount++;
                            ShapeNodeRealizer spenr;
                            spenr = getSubstrateProductShapeNodeRealizer(speLabel,false,offX+computedPositions.get(speciesId).getX(),offY+computedPositions.get(speciesId).getY());     //only a few are common in previous database, which means very popular, exist in most pathways.
                            graph.setRealizer(speciesnode, spenr);
                            compartmentQListMap.get(compid).add(speciesnode);
                            idToNodeTable.put(speciesId,speciesnode);
                            HashSet<String> spelist = new HashSet<String>();
                            spelist.add(speciesId);
                            nodeToIdsTable.put(speciesnode, spelist);
                        }
                    }
                }
            }

//                System.out.println("Creating Graph4..");
            if(comp_reaction.containsKey(compid)){
                    for(String reacId:comp_reaction.get(compid)){
                        if(reactionZombi.contains(reacId)) continue;

                      boolean iDrawReac=false;
                      boolean isTransportReaction=false;
                      reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reacId);
                      if(reacSpecies==null)iDrawReac=true;
                      else{
                          String cmpID="";
                          for(String spid:reacSpecies){
                              if(idToNodeTable.containsKey(spid)) {
                                  iDrawReac=true;
                                  if(cmpID=="")
                                    cmpID=TableQueries.getCompartmentIDBySpeciesID(repository,spid);
                                  else
                                  {
                                      if(!cmpID.equalsIgnoreCase(TableQueries.getCompartmentIDBySpeciesID(repository,spid)))
                                        isTransportReaction=true;
                                  }
//                                  break;
                              }
                          }
                      }
                      
                      if(iDrawReac){
                        if( reactionGuid.equalsIgnoreCase("")|| (!reactionGuid.equalsIgnoreCase("")&& reacId.equalsIgnoreCase(reactionGuid))){
                            if(idToNodeTable.containsKey(reacId))continue;

                           String reacLabel=TableQueries.getReactionNamebyID(repository,reacId);
//                            reacLabel+=":math:"+TableQueries.getKineticLawbyID(repository,reacId);
//                            reacLabel=TableQueries.getKineticLawbyID(repository,reacId);

                            if(reacLabel==null || reacLabel.equalsIgnoreCase("")) reacLabel="Unknown Reactions";
                           Node reacnode = graph.createNode();
    //                       ShapeNodeRealizer reacr = getGenericProcessShapeNodeRealizer(reacLabel);
                            ShapeNodeRealizer reacr;

                           reacr =  getGenericProcessShapeNodeRealizer(reacLabel,offX+computedPositions.get(reacId).getX(),offY+computedPositions.get(reacId).getY(),isTransportReaction, TableQueries.getReactionReversibleInRepository(repository,reacId));
    //                        if(lastMaxX<pLayout.getX())lastMaxX=pLayout.getX();
    //                        }
                           graph.setRealizer(reacnode, reacr);
                           compartmentQListMap.get(compid).add(reacnode);
                           idToNodeTable.put(reacId,reacnode);
                           HashSet<String> reaclist = new HashSet<String>();
                           reaclist.add(reacId);
                           nodeToIdsTable.put(reacnode, reaclist);
                        }

                        //this part check whether NULL product/substrate is needed
                        reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reacId);
                        if(reacSpecies!=null){
                            Node reac=idToNodeTable.get(reacId);
                            String sprole="";
                            int iReacCount=0,iProdCount=0;
                               for(String spid:reacSpecies){
                                   Node spec=idToNodeTable.get(spid);   //do not need this, because the spec node may exist in another compartment, which has not been created.
//                                   Edge edge=null;
//                                   if(reac!=null && spec !=null){
//                                       edge=graph.createEdge(reac,spec);

//                                       EdgeRealizer er = null;
                                       //reac in
            //                           if(hm.getParentNode(reac)==hm.getParentNode(spec)||hm.getParentNode(hm.getParentNode(reac))==hm.getParentNode(spec) ||hm.getParentNode(hm.getParentNode(spec))==hm.getParentNode(reac)){
//                                       er = graph.getRealizer(edge);
            //                           }else
            //                           er=new ArcEdgeRealizer();

            //                           er.setLineType(LineType.LINE_7);
                                       if(TableQueries.getReactionReversibleInRepository(repository,reacId)){
                                           iReacCount++;
                                           iProdCount++;
                                       }else{
                                            sprole=TableQueries.getSpeciesRoleFromSpeciesID(repository,reacId,spid);
                                            if(sprole.equalsIgnoreCase("Reactant")){
                                                iReacCount++;
                                            }else if(sprole.equalsIgnoreCase("Product")){
                                                iProdCount++;
                                            }else  if(sprole.equalsIgnoreCase("Modifier")){
                                                ///how to deal with modifier
//                                                 iReacCount++;
//                                                iProdCount++;
                                            }else {
//                                                iReacCount++;
//                                                iProdCount++;
                                            }
                                       }
            //                           TableQueries.getSpeciesRoleFromReactionSpeciesID(repository,spid)
                                       //er.setTargetArrow(Arrow.STANDARD);
//                                    }
                                }
                                int iSPACE=15;
                                int iSpace2=23;
                                if(iReacCount==0 && iProdCount!=0){//create null substrate
                                    if(!idToNodeTable.containsKey(reacId+"-NULLREACT")){
                                    String nullLabel="NULL REACT";
                                   Node reacnode = graph.createNode();
                //                       ShapeNodeRealizer reacr = getGenericProcessShapeNodeRealizer(reacLabel);
                                   ShapeNodeRealizer reacr;
                                   double nullX=0,nullY=0;
                                   double compX=computedPositions.get(compid).getX();
                                   double compY=computedPositions.get(compid).getY();

                                   if(iSPACE<computedPositions.get(reacId).getX()){// && compartment_frame.get(compid).getMaxX()>computedPositions.get(reacid).getX()){
                                       nullX=compX+computedPositions.get(reacId).getX()-iSpace2;
                                   }else if((computedPositions.get(reacId).getX()-compX)<(compX+compartment_frame.get(compid).getMaxX()-computedPositions.get(reacId).getX())){
                                       nullX=compX+1;
                                   }else
                                   nullX=compX+compartment_frame.get(compid).getMaxX()-iSPACE;

                                   if(iSPACE<computedPositions.get(reacId).getY()){
                                       nullY=compY+computedPositions.get(reacId).getY()-iSpace2;
                                   }else if((computedPositions.get(reacId).getY()-compartment_frame.get(compid).getMinY())<(compartment_frame.get(compid).getMaxY()-computedPositions.get(reacId).getY())){
                                       nullY=compY+1;
                                   }else
                                   nullY=compY+compartment_frame.get(compid).getMaxY()-iSPACE;

                                   reacr =  getNullMoleShapeNodeRealizer(nullLabel,nullX,nullY);
//                                    reacr =  getNullMoleShapeNodeRealizer(nullLabel,computedPositions.get(reacId).getX()-5,computedPositions.get(reacId).getY()-5);
                //                        if(lastMaxX<pLayout.getX())lastMaxX=pLayout.getX();
                //                        }
                                   graph.setRealizer(reacnode, reacr);
                                   compartmentQListMap.get(compid).add(reacnode);
                                   idToNodeTable.put(reacId+"-NULLREACT",reacnode);
                                   HashSet<String> reaclist = new HashSet<String>();
                                   reaclist.add(reacId+"-NULLREACT");
                                   nodeToIdsTable.put(reacnode, reaclist);
                                    }
                                }

                                if(iProdCount==0 && iReacCount!=0){//create null substrate
                                    if(!idToNodeTable.containsKey(reacId+"-NULLPROD")){
                                    String nullLabel="NULL PRODUCT";
                                   Node reacnode = graph.createNode();
                //                       ShapeNodeRealizer reacr = getGenericProcessShapeNodeRealizer(reacLabel);
                                   ShapeNodeRealizer reacr;
                                   double nullX=0,nullY=0;
                                   double compX=computedPositions.get(compid).getX();
                                   double compY=computedPositions.get(compid).getY();
                                   if(iSPACE<computedPositions.get(reacId).getX()){// && compartment_frame.get(compid).getMaxX()>computedPositions.get(reacid).getX()){
                                       nullX=compX+computedPositions.get(reacId).getX()-iSpace2;
                                   }else if((computedPositions.get(reacId).getX()-compX)<(compX+compartment_frame.get(compid).getMaxX()-computedPositions.get(reacId).getX())){
                                       nullX=compX+1;
                                   }else
                                    nullX=compX+compartment_frame.get(compid).getMaxX()-iSPACE/2;

//                                    nullX=computedPositions.get(compid).getX();
                                   if(iSPACE<computedPositions.get(reacId).getY()){
                                       nullY=compY+computedPositions.get(reacId).getY()-iSpace2;
                                   }else if((computedPositions.get(reacId).getY()-compY)<(compY+compartment_frame.get(compid).getMaxY()-computedPositions.get(reacId).getY())){
                                       nullY=compY+1;
                                   }else
                                   nullY=compY+compartment_frame.get(compid).getMaxY()-iSPACE;

//                                   nullY=computedPositions.get(compid).getY();
                                   reacr =  getNullMoleShapeNodeRealizer(nullLabel,nullX,nullY);
//                                reacr =  getNullMoleShapeNodeRealizer(nullLabel,computedPositions.get(reacId).getX()-5,computedPositions.get(reacId).getY()-5);
//                                     reacr =  getNullMoleShapeNodeRealizer_nullsymbol(nullLabel,nullX,nullY);
                //                        if(lastMaxX<pLayout.getX())lastMaxX=pLayout.getX();
                //                        }
                                   graph.setRealizer(reacnode, reacr);
                                   compartmentQListMap.get(compid).add(reacnode);
                                   idToNodeTable.put(reacId+"-NULLPROD",reacnode);
                                   HashSet<String> reaclist = new HashSet<String>();
                                   reaclist.add(reacId+"-NULLPROD");
                                   nodeToIdsTable.put(reacnode, reaclist);
                            }
                                }
                        }
                    }
                    }
               }

//                System.out.println("Creating Graph5..");
            //add two fake nodes at the corners.

//                if (dx>0)
            dx=0;
            Node groupNode;
            compartmentlabel=TableQueries.getCompartmnetNamebyCompartmentID(repository,compid);
            if (compartmentlabel.equals("")) compartmentlabel = "Unknown Compartment";
//                System.out.println("----->Before create group nodes model has comparments:"+getGoupNodesNum(graph));
            groupNode =hm.createGroupNode(graph);
//             HashSet<String> comlist = new HashSet<String>();
//             comlist.add(compartment_hierarchy.get("root").get(0));
            ShapeNodeRealizer nrGroup = getTissueShapeNodeRealizerSB(compartmentlabel,computedPositions.get(compid).getX(),computedPositions.get(compid).getY(),compartment_frame.get(compid).getWidth(),compartment_frame.get(compid).getHeight());
            graph.setRealizer(groupNode, nrGroup);
            HashSet<String> comlist = new HashSet<String>();
            comlist.add(compid);
            nodeToIdsTable.put(groupNode,comlist);
            idToNodeTable.put(compid,groupNode);
         /* Disable the faked nodes.
//                Node fakenode1 = graph.createNode();
//                ShapeNodeRealizer fakeSpen1=getFakedShapeNodeRealizer(computedPositions.get(compid).getX()+3,computedPositions.get(compid).getY()+3);
//                graph.setRealizer(fakenode1, fakeSpen1);
//                 Node fakenode2 = graph.createNode();
//                ShapeNodeRealizer fakeSpen2=getFakedShapeNodeRealizer(computedPositions.get(compid).getX()+ compartment_frame.get(compid).getWidth()-3,computedPositions.get(compid).getY()+ compartment_frame.get(compid).getHeight()-3);
//                graph.setRealizer(fakenode2, fakeSpen2);
//                 idToNodeTable.put(fakenode1.toString(),fakenode1);
//                idToNodeTable.put(fakenode2.toString(),fakenode2);
//                compartmentQListMap.get(compid).add(fakenode1);
//                compartmentQListMap.get(compid).add(fakenode2);
        */
            hm.groupSubgraph(compartmentQListMap.get(compid),groupNode);

            if(!compartmentQListMap.containsKey(compartment_parent.get(compid)))
                {
                     compartmentQListMap.put(compartment_parent.get(compid),new NodeList());
                }
               compartmentQListMap.get(compartment_parent.get(compid)).add(groupNode);


                if(!calculatedComps.contains(compid))
                        calculatedComps.add(compid);

//                toCompuComs.remove(compid);
                if(!tmptoCompuComs.contains(compid))
                    tmptoCompuComs.add(compid);
                if(compartment_parent.get(compid)!="root"){
                    String paOfcaledcom=compartment_parent.get(compid);
                    if(tmptoCompuComs.contains(paOfcaledcom) || !toCompuComs.contains(paOfcaledcom))
//                    if(!toCompuComs.contains(paOfcaledcom))
                    {
                        bpass=false;
                        for(String comp:compartment_hierarchy.get(paOfcaledcom)){
                        //if the parent has uncalculated compartment, just pass
                            if(!calculatedComps.contains(comp)){ bpass=true; break;}
                        }
                        if(!bpass)
                            {
                                if(!toCompuComs.contains(paOfcaledcom))
                                {toCompuComs.add(paOfcaledcom);
                                break;}
//                                continue;
                            }
                    }
                }else break;
//                if(toCompuComs.isEmpty()) break;
              if(toCompuComs.size()==tmptoCompuComs.size()) break;
            }
        }


           String compartmentlabel=repository.modelTable.getNameById();// "Model Name...";//TableQueries.getCompartmnetNamebyCompartmentID(repository,compid);
            if (compartmentlabel.equals("")) compartmentlabel = "Unknown Model Name";
//                System.out.println("----->Before create group nodes model has comparments:"+getGoupNodesNum(graph));
            Node groupNode =hm.createGroupNode(graph);
            String compid="root";
            ShapeNodeRealizer nrGroup = getTissueShapeNodeRealizerSB(compartmentlabel,computedPositions.get("root").getX(),computedPositions.get(compid).getY(),compartment_frame.get(compid).getWidth(),compartment_frame.get(compid).getHeight(),Color.black);
            graph.setRealizer(groupNode, nrGroup);
            HashSet<String> comlist = new HashSet<String>();
            comlist.add(compid);
            nodeToIdsTable.put(groupNode,comlist);
            idToNodeTable.put(compid,groupNode);
          hm.groupSubgraph(compartmentQListMap.get(compid),groupNode);

        //add edges

//        System.out.println("Creating Graph9..");
        for(String reacid:reactionIds){
                 if(reactionZombi.contains(reacid))
                     continue;

                      boolean isTransportReaction=false;
                      reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
                      if(reacSpecies!=null)
                      {
                          String cmpID="";
                          for(String spid:reacSpecies){
                              if(idToNodeTable.containsKey(spid)) {
                                  if(cmpID=="")
                                    cmpID=TableQueries.getCompartmentIDBySpeciesID(repository,spid);
                                  else
                                  {
                                      if(!cmpID.equalsIgnoreCase(TableQueries.getCompartmentIDBySpeciesID(repository,spid)))
                                        isTransportReaction=true;
                                  }
                              }
                          }
                      }

            String compidofSpec="";
            reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
            compid="";
            for(String cid:comp_reaction.keySet())
            {
                if(comp_reaction.get(cid).contains(reacid)){
                    compid=cid;
                    break;
                }
            }
            if(reacSpecies!=null){
                Node reac=idToNodeTable.get(reacid);
                String sprole="";
                int iReacCount=0,iProdCount=0;
                   for(String spid:reacSpecies){
                       for(String cid:comp_species.keySet())
                        {
                            if(comp_species.get(cid).contains(spid)){
                                compidofSpec=cid;
                                break;
                            }
                        }
                       Node spec=idToNodeTable.get(spid);
                       Edge edge=null;
                       if(reac!=null && spec !=null){
                           edge=graph.createEdge(reac,spec);

                           EdgeRealizer er = null;
                           //reac in
//                           if(hm.getParentNode(reac)==hm.getParentNode(spec)||hm.getParentNode(hm.getParentNode(reac))==hm.getParentNode(spec) ||hm.getParentNode(hm.getParentNode(spec))==hm.getParentNode(reac)){
                           er = graph.getRealizer(edge);
//                           }else
//                           er=new ArcEdgeRealizer();

//                           er.setLineType(LineType.LINE_7);
//                           if(TableQueries.getReactionReversibleInRepository(repository,reacid)){
//                               sprole=TableQueries.getSpeciesRoleFromSpeciesID(repository,reacid,spid);
//                                if(sprole.equalsIgnoreCase("Modifier")){
////                                    er.setSourceArrow(Arrow.WHITE_DIAMOND);
//                                    int[] xpoints = {0, -2, -2, 0};
//                                    int[] ypoints = {-5, -5, 5, 5};
//                                    er.setSourceArrow(Arrow.addCustomArrow("ModifierArrow", new Polygon(xpoints, ypoints, 4), Color.red));
//                                    er.setLineColor(Color.red);
//                                }else{
//                                   er.setTargetArrow(Arrow.STANDARD);
//                                   er.setSourceArrow(Arrow.STANDARD);
//                                   sprole="Reverse";
//                                }
//                               iReacCount++;
//                               iProdCount++;
//                           }else
                           {
                                sprole=TableQueries.getSpeciesRoleFromSpeciesID(repository,reacid,spid);
                                if(sprole.equalsIgnoreCase("Reactant")){
                                    er.setSourceArrow(Arrow.STANDARD);
                                    iReacCount++;
                                }else if(sprole.equalsIgnoreCase("Product")){
                                    er.setTargetArrow(Arrow.STANDARD);
                                    iProdCount++;
                                }else  if(sprole.equalsIgnoreCase("Modifier")){
//                                    er.setSourceArrow(Arrow.WHITE_DIAMOND);
                                    int[] xpoints = {0, -2, -2, 0};
                                    int[] ypoints = {-5, -5, 5, 5};
                                    er.setSourceArrow(Arrow.addCustomArrow("ModifierArrow", new Polygon(xpoints, ypoints, 4), Color.red));
                                }else {
                                    er.setLabelText(sprole);
                                }
                           }
//                           if(!compidofSpec.equalsIgnoreCase(compid) && !(er.getLineColor().equals(Color.red)))er.setLineColor(Color.lightGray);       //gray out cross compartment lines if it's not modifier
                           if(isTransportReaction) er.setLineColor(Color.lightGray);
                           else er.setLineColor(Color.gray);
//                           er.setVisible(false);
//                           er.setLayer((byte)0);
                           graph.setRealizer(edge,er);
//                           TableQueries.getSpeciesRoleFromReactionSpeciesID(repository,spid)
                           //er.setTargetArrow(Arrow.STANDARD);
                        }else{
//                           Edge edge=graph.createEdge(reac,reac);
//                           EdgeRealizer er = graph.getRealizer(edge);
//                           er.setLabelText(reacid+"  "+spid);
//                           er.setTargetArrow(Arrow.STANDARD);
                       }
//                       compartmentQListMap.get(compid).add(edge);
//                       hm.groupSubgraph(edge,groupNode);
//                       hm.

//                       idToNodeTable.put(reacId,edge);
              }
                //create null substrate OR null product
                if(iReacCount==0){//create null substrate

                    Node spec=idToNodeTable.get(reacid+"-NULLREACT");
                    //adjust position of null nodes

                    Edge edge=null;
                       if(reac!=null && spec !=null){
                           edge=graph.createEdge(reac,spec);
                           EdgeRealizer er = null;
                           er = graph.getRealizer(edge);
                           er.setSourceArrow(Arrow.STANDARD);
                       }
                }
                if(iProdCount==0){//create null product
                    Node spec=idToNodeTable.get(reacid+"-NULLPROD");

                    Edge edge=null;
                       if(reac!=null && spec !=null){
                           edge=graph.createEdge(reac,spec);
                           EdgeRealizer er = null;
                           er = graph.getRealizer(edge);
                           er.setTargetArrow(Arrow.STANDARD);
                       }
                }

            }else{
                //add null substrate and null product here
            }
           }
         //add outer box for each model


//        System.out.println("Creating Graph10..");
        //routing edges
        OrganicEdgeRouter oer= new OrganicEdgeRouter();
        oer.setMinimalDistance(0.3);
////        oer.setUsingBends(true);
//        oer.setRoutingAll(false);

//        RemoveOverlapsLayoutStage rmos = new RemoveOverlapsLayoutStage(0);
//        LayoutStage nodeEnlarger = oer.createNodeEnlargementStage();
//        final CompositeLayoutStage cls = new CompositeLayoutStage();
////         cls.appendStage(nodeEnlarger);
////         cls.appendStage(new BendConverter());
//         cls.appendStage(rmos);

//        oer.setCoreLayouter(cls);
//                        oer.setRoutingStyle(OrthogonalEdgeRouter.STYLE_SHORTPATH);
//                        ChannelEdgeRouter oer=new ChannelEdgeRouter();
//                         oer.setCoreLayouter(cls);

             oer.doLayout(graph);

//       ArrayList<String> reactionIds = TableQueries.getReactionIDListInRepository(repository);
        /*
       for(String reacid:reactionIds){
          ArrayList<String> reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
           if(reacSpecies!=null){
            Node reac=idToNodeTable.get(reacid);
            boolean isReversible=TableQueries.getReactionReversibleInRepository(repository,reacid);
              for(String spid:reacSpecies){
                  Node spec=idToNodeTable.get(spid);
                  Edge edge=null;
                  if(reac!=null && spec !=null){
                      String speRole=TableQueries.getSpeciesRole(repository,reacid,spid);
                      edge=graph.createEdge(reac,spec);
                      EdgeRealizer er =graph.getRealizer(edge);
                      if(isReversible){
                          //just draw two ways arrow, do not care substrate or product
                          if(speRole.equalsIgnoreCase("product")||speRole.equalsIgnoreCase("substrate")){
                              er.setTargetArrow(Arrow.STANDARD);
                              er.setSourceArrow(Arrow.STANDARD);
                          }
                      }else{
                          if(speRole.equalsIgnoreCase("product")){
//                              er= new GenericEdgeRealizer();
                              er.setTargetArrow(Arrow.STANDARD);
                          }else if(speRole.equalsIgnoreCase("substrate")){
//                              er= new GenericEdgeRealizer();
                              er.setSourceArrow(Arrow.STANDARD);
                          }
                      }
                      if(speRole.equalsIgnoreCase("activator")){
                              er.setSourceArrow(Arrow.DIAMOND);
                              er.setLineColor(Color.green);
                          }else if(speRole.equalsIgnoreCase("inhibitor")){
                              er.setTargetArrow(Arrow.DIAMOND);
                              er.setLineColor(Color.red);
                          }else if(speRole.equalsIgnoreCase("cofactor in")){
                              er=new ArcEdgeRealizer();
                              er.setSourceArrow(Arrow.DELTA);
                              graph.setRealizer(edge,er);
                          }else if(speRole.equalsIgnoreCase("cofactor out")){
                              er=new ArcEdgeRealizer();
                              er.setTargetArrow(Arrow.DELTA);
                              graph.setRealizer(edge,er);
                          }
                      }
              }
             }
          }
       */

//        System.out.println("Creating Graph4..");
//        System.out.println("Now the model has comparments:"+getGoupNodesNum(graph));

//        HashMap<Node, HashSet<String>> nodeToIdsTable2 = new HashMap<Node, HashSet<String>>();
        
//        for(Node nd:nodeToIdsTable.keySet())
//        {
//            graph.setRealizer(graph.createNode(), graph.getRealizer(nd));
////            graph.removeNode(nd);
//        }

        return nodeToIdsTable;
       }

    public static HashMap<Node, HashSet<String>> createPathwayGraphFromMetabolite_old(PathCaseRepository repository, Graph2D graph, String appWidth, String appHeight) {
           HashMap<String, Node> moleculeidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> idToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> pathidToNodeTable = new HashMap<String, Node>();
           HashMap<Node, HashSet<String>> nodeToIdsTable = new HashMap<Node, HashSet<String>>();
//        HashMap<Node,String> nodeToId = new HashMap<Node, String>();
//03/18/09 added for SysBio Model Visualization By Xinjian
           HashMap<String, Node> modelidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> compartmentidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> speciesidToNodeTable = new HashMap<String, Node>();
        HashMap<String,ArrayList<Integer>> iComp_Elements = new HashMap<String, ArrayList<Integer>>();
//           HashMap<String, Node> reactionidToNodeTable = new HashMap<String, Node>();
//           HashMap<String, Node> reactionspeciesidToNodeTable = new HashMap<String, Node>();

//           HashSet<String> SubstrateAndProductList = new HashSet<String>();
           HashMap<String, ArrayList<String>> compartment_hierarchy  = new HashMap<String, ArrayList<String>>();
        HashMap<String, PFrame> compartment_frame  = new HashMap<String, PFrame>();
        HashMap<String, PPoint> computedPositions  = new HashMap<String, PPoint>();
           HashMap<String, ArrayList<String>> comp_reaction  = new HashMap<String, ArrayList<String>>();
//q begin
           HierarchyManager hm = new HierarchyManager(graph);
           HashMap<String, NodeList> compartmentQListMap=new HashMap<String, NodeList>(); //q
            int appletwidth=Integer.parseInt(appWidth),appletheight=Integer.parseInt(appHeight);
//q end
        SetAcceptableTissueColors();

        String pathwayid="";
        if(TableQueries.getPathwaysIdListInRepository(repository).size()==1) pathwayid=TableQueries.getPathwaysIdListInRepository(repository).iterator().next();

        //suppose there's only one root compartment, we put it just under "root"

        for(String compid:TableQueries.getCompartmentIDListInRepository(repository)){
            String comOutside=TableQueries.getCompartmnetOutsidebyCompartmentID(repository,compid);
            if (comOutside.equals(compid)) {
                   if(!compartment_hierarchy.containsKey("root")){
                      compartment_hierarchy.put("root",new ArrayList<String>());
                  }
                  compartment_hierarchy.get("root").add(compid);
            }
        }

        for(String compid:TableQueries.getCompartmentsByPathwayId(repository,pathwayid)){
            //Since there's no root compartment under pathways section, we do not need to judge here, but have to find the root before or after.
            String comOutside=TableQueries.getCompartmnetOutsidebyCompartmentID(repository,compid);
               if(!compartment_hierarchy.containsKey(comOutside)){
                  compartment_hierarchy.put(comOutside,new ArrayList<String>());
              }
                compartment_hierarchy.get(comOutside).add(compid);
        }
//        int iCompDepth=compartment_hierarchy.keySet().size();

        //draw root(root's direct child, only one) here
        compartmentQListMap.put(compartment_hierarchy.get("root").get(0),new NodeList());


        //draw compartments other than root.
        for (String compid : compartment_hierarchy.get(compartment_hierarchy.get("root").get(0))) {
//               String innerCom=compid;
               //it means no comparment inside compid
               //System.out.println(gpid);
               String compartmentlabel = "";

               if(!compartmentQListMap.containsKey(compid))
                   {
                        compartmentQListMap.put(compid,new NodeList());
                   }

            ArrayList<String> speciesIds = TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid);

               if(speciesIds!=null){
                   for(String speciesId:speciesIds){
                       String speLabel=TableQueries.getSpeciesLabelBySpeciesId(repository,speciesId);
                       if(speLabel==null || speLabel=="") speLabel="Unknown Species";
                       Node speciesnode = graph.createNode();
//                       nodeToId.put(speciesnode,speciesId);
//                       speCount++;
                       ShapeNodeRealizer spenr;
                       spenr = getSubstrateProductShapeNodeRealizer(speLabel,false);     //only a few are common in previous database, which means very popular, exist in most pathways.
                       graph.setRealizer(speciesnode, spenr);
                       compartmentQListMap.get(compid).add(speciesnode);
                       idToNodeTable.put(speciesId,speciesnode);
                       HashSet<String> spelist = new HashSet<String>();
                       spelist.add(speciesId);
                       nodeToIdsTable.put(speciesnode, spelist);
                   }
               }

            ArrayList<String> reacIds = TableQueries.getReactionsByCompartmentId(repository,compid);
              if(reacIds!=null){
                    for(String reacid:reacIds){
                        //Do not use reaction name, but use enzyme name;
//                       String reacLabel=TableQueries.getReactionNamebyID(repository,reacId);
                        String reacLabel="<html>";
                        ArrayList<String> reacEnzIDs=TableQueries.getReactionEnzymebyID(repository,compid,reacid);
                        if(reacEnzIDs!=null){
                            for(String enlabel:reacEnzIDs){
                                reacLabel+=(TableQueries.getEnzymeNamebyID(repository,enlabel)+"<br>");
                            }
                            reacLabel+="</html>";
                        }else
                        reacLabel="Unknown Reactions";
//                       if(reacLabel==null || reacLabel=="") reacLabel="Unknown Reactions";
                       Node reacnode = graph.createNode();
                       ShapeNodeRealizer reacr;

                       reacr = getGenericProcessShapeNodeRealizer(reacLabel);//getRandomPoint(curFrame,compid,compartment_hierarchy,compartment_frame));
                       graph.setRealizer(reacnode, reacr);
                       compartmentQListMap.get(compid).add(reacnode);
                       idToNodeTable.put(reacid,reacnode);
                       HashSet<String> reaclist = new HashSet<String>();
                       reaclist.add(reacid);
                       nodeToIdsTable.put(reacnode, reaclist);
                    }
               }

//            boolean hasChild=false;
            if(compartment_hierarchy.containsKey(compid)){
                for(String subcom:compartment_hierarchy.get(compid)){
                    Node subGroupNode=createSubGroupNode(repository, graph,hm,compartment_hierarchy,compartmentQListMap, subcom, idToNodeTable,nodeToIdsTable);
                    compartmentQListMap.get(compid).add(subGroupNode);
                }
            }

               Node groupNode;
               compartmentlabel=TableQueries.getCompartmnetNamebyCompartmentID(repository,compid);
               if (compartmentlabel.equals("")) compartmentlabel = "Unknown";
               groupNode =hm.createGroupNode(graph);
                HashSet<String> comlist = new HashSet<String>();
                comlist.add(compartment_hierarchy.get("root").get(0));
               nodeToIdsTable.put(groupNode,comlist);

                ShapeNodeRealizer nrGroup = getTissueShapeNodeRealizerSB(compartmentlabel);
                graph.setRealizer(groupNode, nrGroup);

//              idToNodeTable.put(compid,groupNode);
            hm.groupSubgraph(compartmentQListMap.get(compid),groupNode);
            compartmentQListMap.get(compartment_hierarchy.get("root").get(0)).add(groupNode);

//getGenericProcessShapeNodeRealizer//
               HashSet<String> complist = new HashSet<String>();
               complist.add(compid);
               nodeToIdsTable.put(groupNode, complist);
        }

        Node rootgroupNode =hm.createGroupNode(graph);
        HashSet<String> roolist = new HashSet<String>();
        roolist.add(compartment_hierarchy.get("root").get(0));
        nodeToIdsTable.put(rootgroupNode, roolist);

         ShapeNodeRealizer nrGroupR = getTissueShapeNodeRealizerSB(TableQueries.getCompartmnetNamebyCompartmentID(repository,compartment_hierarchy.get("root").get(0)),1024,768);
         graph.setRealizer(rootgroupNode, nrGroupR);
         idToNodeTable.put(compartment_hierarchy.get("root").get(0),rootgroupNode);

        hm.groupSubgraph(compartmentQListMap.get(compartment_hierarchy.get("root").get(0)),rootgroupNode);

        return nodeToIdsTable;
       }



    static void  drawCompartments(PathCaseRepository repository, Graph2D graph,HierarchyManager hm,HashMap<String, ArrayList<String>> compH, String compId, HashMap<String, Node> idToNodeTable,HashMap<Node, HashSet<String>> nodeToIds,HashMap<String, NodeList> compartmentQListMap,HashMap<String, ArrayList<String>> comp_reaction, HashMap<String, PFrame> compartment_frame,HashMap<String, PPoint> pcomputedPositions,HashMap<Node,String> nodeToId,HashMap<String,ArrayList<Integer>> iComp_Elements){
            //while(compH.containsKey(compId)){
        HashMap<String, PPoint> computedPositions;
//                   int iCenter=0;
                    int iRoot=0;
//                   double iStart=0;
//        int iSize=compH.get(compId).size();
//        if (iSize>1) iCenter++;
        ///TO BE DONE:  if there's parallel in this compartment
                   for(String innerComs: compH.get(compId)){
                       String compartmentlabel=TableQueries.getCompartmnetNamebyCompartmentID(repository,innerComs);
                       if (compartmentlabel.equals("")) compartmentlabel = "Unknown Inner Compartment";
                       //1.create comp node
                       //compH.get(comOutside).add(compid);
                       Node groupNode =hm.createGroupNode(graph);
                       nodeToId.put(groupNode,innerComs);
                       iRoot++;
//                       Node groupNode =hm.createFolderNode(graph);
                       Node pNode=idToNodeTable.get(compId);
//                       PFrame lastFrame=null;
                       PFrame curFrame=compartment_frame.get(innerComs);
                       PFrame pcurFrame=compartment_frame.get(compId);
                       computedPositions=ComputeElementsPositions(curFrame,repository,innerComs,comp_reaction,compH,compartment_frame);
                       PPoint tm;
                       if(pcomputedPositions.containsKey(innerComs)){
                            tm=getLayoutedPoint(pcurFrame,pcomputedPositions.get(innerComs));
                           curFrame.setStartX(tm.getX());
                           curFrame.setStartY(tm.getY());
                       }
                       else
                       tm=curFrame.getUpLeft();
                       ShapeNodeRealizer nrGroup;
//                       GroupNodeRealizer nrGroupR;
//                       nrGroupR= new GroupNodeRealizer();
//                       nrGroupR.setAutoBoundsEnabled(true);

//                       if(iCenter==0)
////                           nrGroup = getCompShapeNodeRealizerSB(compartmentlabel,graph.getCenterX(pNode),graph.getCenterY(pNode),graph.getSize(pNode).width-20,graph.getSize(pNode).height-20);
//                           nrGroup = getCompShapeNodeRealizerSB(compartmentlabel,tm.getX(),tm.getY(),graph.getSize(pNode).width-20,graph.getSize(pNode).height-20);
////                          nrGroupR = getCompShapeNodeRealizerSBGrp(compartmentlabel,graph.getCenterX(pNode),graph.getCenterY(pNode),graph.getSize(pNode).width-20,graph.getSize(pNode).height-20);
//
//                       else {
//                           if(iStart==0)iStart =graph.getCenterY(pNode)-graph.getSize(pNode).height/2;
//                           else iStart+=(5+(graph.getSize(pNode).height/iSize-10)/2);
//                           String comlabel=TableQueries.getCompartmnetNamebyCompartmentID(repository,innerComs);
//                           if(comlabel.equals(""))comlabel="Unknown Inner Compartment";
////                           nrGroup = getCompShapeNodeRealizerSB(comlabel, graph.getCenterX(pNode),iStart+iCenter*(5+(graph.getSize(pNode).height/(iSize)-10)/2),graph.getSize(pNode).width-20,graph.getSize(pNode).height/(iSize)-10);
//                           nrGroup = getCompShapeNodeRealizerSB(comlabel, tm.getX(),tm.getY(),graph.getSize(pNode).width-20,graph.getSize(pNode).height/(iSize)-10);
////                           nrGroupR = getCompShapeNodeRealizerSBGrp(compartmentlabel,graph.getCenterX(pNode),graph.getCenterY(pNode),graph.getSize(pNode).width-20,graph.getSize(pNode).height-20);
//                       }

                       nrGroup = getCompShapeNodeRealizerSB(compartmentlabel,tm.getX(),tm.getY(),curFrame.getWidth(),curFrame.getHeight());//,graph.getSize(pNode).width-20,graph.getSize(pNode).height-20);
//                       nrGroup.setLocation(tm.getX(),tm.getY());
                       graph.setRealizer(groupNode, nrGroup);
//                      graph.setRealizer(groupNode, nrGroupR);
                       HashSet<String> complist = new HashSet<String>();
                       complist.add(innerComs+":"+tm.getX()+":"+tm.getY());
                       nodeToIds.put(groupNode, complist);
                       idToNodeTable.put(innerComs,groupNode);

//                       if(!compartmentQListMap.containsKey(compId))
//                           {
//                                compartmentQListMap.put(compId,new NodeList());
//                           }
                       if(!compartmentQListMap.containsKey(innerComs))
                           {
                                compartmentQListMap.put(innerComs,new NodeList());
                           }

//                        if(iComp_Elements.get(innerComs).get(1)>3){
//                           Node fakenode1 = graph.createNode();
//                           ShapeNodeRealizer fakeSpen1=getFakedShapeNodeRealizer(curFrame.getMinX()+1,curFrame.getMinY()+1);
//                           graph.setRealizer(fakenode1, fakeSpen1);
//                            Node fakenode2 = graph.createNode();
//                           ShapeNodeRealizer fakeSpen2=getFakedShapeNodeRealizer(curFrame.getMaxX()-1,curFrame.getMaxY()-1);
//                           graph.setRealizer(fakenode2, fakeSpen2);
//                            idToNodeTable.put(fakenode1.toString(),fakenode1);
//                           idToNodeTable.put(fakenode2.toString(),fakenode2);
//                           compartmentQListMap.get(innerComs).add(fakenode1);
//                           compartmentQListMap.get(innerComs).add(fakenode2);
//                        }


                       ArrayList<String> speciesIds = TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,innerComs);
                       int speCount=0;
                       if(speciesIds!=null){
                           speCount=0;
                           for(String speciesId:speciesIds){
                               String speLabel=TableQueries.getSpeciesLabelBySpeciesId(repository,speciesId);
                               if(speLabel==null || speLabel=="") speLabel="Unknown Species";
                               Node speciesnode = graph.createNode();
                               nodeToId.put(speciesnode,speciesId);
                               speCount++;
//                               ShapeNodeRealizer spenr = getSubstrateProductShapeNodeRealizerCompartmentH(speLabel);
                               ShapeNodeRealizer spenr;

//                             double cstartX;
//                             if(iRoot>1)cstartX=Math.max(lastFrame.getMaxX(),curFrame.getMinX());
//                             else cstartX=curFrame.getMinX();
//                             if(speCount==1){
//                              spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,cstartX,curFrame.getMinY());//boundary.getBoundingBox().getMinY()+1,boundary.getBoundingBox().getMaxX()-1);
//                             }
//                             else if(speCount==2)
//                              spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,cstartX+curFrame.getWidth()-borderbuffer,curFrame.getBottomRight().getY()-borderbuffer);//boundary.getBoundingBox().getMinY()+1,boundary.getBoundingBox().getMaxX()-1);
//                             else
//                              spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,getRandomPoint(curFrame,innerComs,compH,compartment_frame));
                                spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,new PPoint(computedPositions.get(speciesId).getX()+tm.getX(),computedPositions.get(speciesId).getY()+tm.getY()));// getLayoutedPoint(curFrame,computedPositions.get(speciesId)));
//                                spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,0,0);
                               graph.setRealizer(speciesnode, spenr);
//                               compartmentQListMap.get(compId).add(speciesnode);
                               HashSet<String> spelist = new HashSet<String>();
                               spelist.add(speciesId);
                               nodeToIds.put(speciesnode, spelist);
                               idToNodeTable.put(speciesId,speciesnode);
                               compartmentQListMap.get(innerComs).add(speciesnode);
                           }
                       }
                       //add reactions nodes.
                       if(comp_reaction.containsKey(innerComs)){
                           int reacCount=0;
                            for(String reacId:comp_reaction.get(innerComs)){
                               String reacLabel=TableQueries.getReactionNamebyID(repository,reacId);
                               if(reacLabel==null || reacLabel=="") reacLabel="Unknown Reaction";
                               Node reacnode = graph.createNode();
                                nodeToId.put(reacnode,reacId);

                                ShapeNodeRealizer reacr;
                                reacr =  getGenericProcessShapeNodeRealizer2(reacLabel,getLayoutedPoint(curFrame,computedPositions.get(reacId)));

                               graph.setRealizer(reacnode, reacr);
                               compartmentQListMap.get(innerComs).add(reacnode);
                               idToNodeTable.put(reacId,reacnode);
                            }
                       }

                       hm.groupSubgraph(compartmentQListMap.get(innerComs),groupNode);
                       if(compH.containsKey(innerComs))drawCompartments(repository,graph,hm,compH,innerComs,idToNodeTable,nodeToIds,compartmentQListMap,comp_reaction,compartment_frame,computedPositions,nodeToId,iComp_Elements);

                       compartmentQListMap.get(compId).add(groupNode);
                       hm.groupSubgraph(compartmentQListMap.get(compId),pNode);

                   }
    }


    static void calcSubFrames(HashMap<String, PFrame> compartment_frame,PFrame pcomFrame,HashMap<String, ArrayList<String>> compartment_hierarchy,String pcompid,PathCaseRepository repository,HashMap<String, ArrayList<String>> comp_reaction,HashMap<String,ArrayList<Integer>> iComp_Elements){
        int minSpaceWithoutElements=10;
        int minSpaceWithElements=90;
        int size =compartment_hierarchy.get(pcompid).size();
          double startX=pcomFrame.getMinX(), startY=pcomFrame.getMinY();
          if(iComp_Elements.get(pcompid).get(1).intValue()>0){ //pcompartment has elements or its descendants have elements
              if(iComp_Elements.get(pcompid).get(0).intValue()==0){ //pcompartment has no elements (but sure it has subcompartment)
                  if(size==1){ //only one subcompartment in it
                      PFrame comFrame=new PFrame(pcomFrame.getCenter(), pcomFrame.getWidth()-minSpaceWithoutElements,pcomFrame.getHeight()-minSpaceWithoutElements,true);
                      compartment_frame.put(compartment_hierarchy.get(pcompid).get(0),comFrame);
                      if(compartment_hierarchy.containsKey(compartment_hierarchy.get(pcompid).get(0)))calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,compartment_hierarchy.get(pcompid).get(0),repository,comp_reaction,iComp_Elements);
                  }else{
                      //this case is: pcomframe has no elements, and has multi subcompartments
                      startX=startX+minSpaceWithoutElements/2;
                      startY=startY+minSpaceWithoutElements/2;
                      double possibleW=pcomFrame.getWidth()-(size+1/2)*(minSpaceWithoutElements/2);
                      int iPalComp=0;
                      for (String compid : compartment_hierarchy.get(pcompid)){
                          iPalComp++;
                          PFrame comFrame=null;
                          int iDepth=getDepthOfCom(compartment_hierarchy,compid);    // how many subcompartments it has, although all sub sompartments are empty
                          if(iComp_Elements.get(compid).get(1).intValue()==0){ //subcompartments has no element   ---it's possible to reach here when size>1
                            comFrame=new PFrame(new PPoint(startX,startY),(iDepth+1/2)*minSpaceWithoutElements,(iDepth+1/2)*minSpaceWithoutElements);
                            startX+=(iDepth+1)*minSpaceWithoutElements;
                            startY+=(iDepth+1)*minSpaceWithoutElements;
                            possibleW-=((iDepth+1)*minSpaceWithoutElements);
                          }else{//subcompartments has elements
                              double dWid=(possibleW*iComp_Elements.get(compid).get(1).intValue())/iComp_Elements.get(pcompid).get(1).intValue();
                              double dHei=(pcomFrame.getHeight()*iComp_Elements.get(compid).get(1).intValue())/iComp_Elements.get(pcompid).get(1).intValue();
//                              double dHei=pcomFrame.getHeight();
//                              if(!compartment_hierarchy.containsKey(compid)){
                                  if(dWid<minSpaceWithElements) {
                                      possibleW-= (minSpaceWithElements-dWid);
                                      dWid=minSpaceWithElements;
                                  }

//                              if(iPalComp!=2){
//                                  comFrame=new PFrame(new PPoint(startX,startY),dWid,dHei);
//                                  startX+=(dWid+minSpaceWithoutElements/2);
//                                  startY+=(dWid+minSpaceWithoutElements/2);
//                              }else
//                              comFrame=new PFrame(pcomFrame.getBottomRight(),dWid,dHei,false);
                                  comFrame=new PFrame(new PPoint(startX,startY),dWid,dHei);
                                  startX+=(dWid+minSpaceWithoutElements/2);
                                  startY+=(dWid+minSpaceWithoutElements/2);
//                              }else{
//                              }
                          }
                          compartment_frame.put(compid,comFrame);
                          if(compartment_hierarchy.containsKey(compid))calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,compid,repository,comp_reaction,iComp_Elements);
                      }
                  }
              }else{
                  //this case: pcomframe has elements, judge its proportion, may choose min space with elements if elements' number is less(than 6);  be careful to see whether it still has sub compartment
                  if(size==1){
                      //if pcomp has only one subcompartment, start from center, value of width choose from min and proportion
                      if(iComp_Elements.get(pcompid).get(0).intValue()==iComp_Elements.get(pcompid).get(1).intValue()){//subcompartment are empty.
                          if(compartment_hierarchy.containsKey(compartment_hierarchy.get(pcompid).get(0))) //has multi level empty comps, set its size random and it'll be corrected later.
                            calcSubFrames(compartment_frame,pcomFrame,compartment_hierarchy,compartment_hierarchy.get(pcompid).get(0),repository,comp_reaction,iComp_Elements);
                          else{ //pcomp has only one subcompartment, and the subcompartment is empty
                              PFrame comFrame=new PFrame(pcomFrame.getCenter(), minSpaceWithoutElements,minSpaceWithoutElements,true);
                              compartment_frame.put(compartment_hierarchy.get(pcompid).get(0),comFrame);
                          }
                      }else{ //subcoms are not empty
                          double wid=0;
                          PFrame comFrame=null;
                          double possibleW=pcomFrame.getWidth();//-(size+1/2)*(minSpaceWithoutElements/2);
                          if(((possibleW*iComp_Elements.get(pcompid).get(0).intValue())/iComp_Elements.get(pcompid).get(1).intValue())<minSpaceWithElements){ //elements in pcom should hold min space
                              comFrame=new PFrame(pcomFrame.getCenter(), possibleW-minSpaceWithoutElements,pcomFrame.getHeight()-minSpaceWithoutElements,true);
                          }
//                              compartment_frame.put(compartment_hierarchy.get(pcompid).get(0),comFrame);
//                              if(getDepthOfCom(compartment_hierarchy,pcompid)>0)//compartment_hierarchy.containsKey(compartment_hierarchy.get(pcompid).get(0)))
//                              calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,compartment_hierarchy.get(pcompid).get(0),repository,comp_reaction,iComp_Elements);}
                          else{
                              comFrame=new PFrame(pcomFrame.getCenter(),(possibleW*iComp_Elements.get(compartment_hierarchy.get(pcompid).get(0)).get(1).intValue())/iComp_Elements.get(pcompid).get(1).intValue(),(pcomFrame.getHeight()*iComp_Elements.get(compartment_hierarchy.get(pcompid).get(0)).get(1).intValue())/iComp_Elements.get(pcompid).get(1).intValue(),true);
                          }
                          compartment_frame.put(compartment_hierarchy.get(pcompid).get(0),comFrame);
                          if(compartment_hierarchy.containsKey(compartment_hierarchy.get(pcompid).get(0))) //getDepthOfCom(compartment_hierarchy,pcompid)>1)//
                            calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,compartment_hierarchy.get(pcompid).get(0),repository,comp_reaction,iComp_Elements);
                      }
                  }else{
                      //this case is: pcomframe has elements, and has multi subcompartments
                      startX=startX+minSpaceWithElements/2;
                      startY=startY+minSpaceWithElements/2;
                      double possibleW=pcomFrame.getWidth();
                      if(minSpaceWithElements>((possibleW*iComp_Elements.get(pcompid).get(0).intValue())/iComp_Elements.get(pcompid).get(1).intValue()))
//                        possibleW=pcomFrame.getWidth()-iComp_Elements.get(pcompid).get(0)/iComp_Elements.get(pcompid).get(1);
//                      else
                        possibleW-=minSpaceWithElements;

                      int iPalComp=0;
                      for (String compid : compartment_hierarchy.get(pcompid)){
                          PFrame comFrame=null;
                          iPalComp++;
                          int iDepth=getDepthOfCom(compartment_hierarchy,compid);
                          if(iComp_Elements.get(compid).get(1).intValue()==0){ //subcompartments has no element   ---it's possible to reach here when size>1
                            comFrame=new PFrame(new PPoint(startX,startY),(iDepth+1)*minSpaceWithoutElements,(iDepth+1)*minSpaceWithoutElements);
                            startX+=((iDepth+1)*minSpaceWithoutElements);
                            startY+=((iDepth+1)*minSpaceWithoutElements);
                            possibleW-=((iDepth+1)*minSpaceWithoutElements);
                          }else{//subcompartments has elements
                              double dWid=(possibleW*iComp_Elements.get(compid).get(1).intValue())/iComp_Elements.get(pcompid).get(1).intValue();
                              double dHei=(pcomFrame.getHeight()*iComp_Elements.get(compid).get(1).intValue())/iComp_Elements.get(pcompid).get(1).intValue();
//                              if(!compartment_hierarchy.containsKey(compid)){
                                  if(dWid<minSpaceWithElements) {
                                      possibleW-= (minSpaceWithElements-dWid);
                                      dWid=minSpaceWithElements;
                                  }
                              if(iPalComp!=2){
                                  comFrame=new PFrame(new PPoint(startX,startY),dWid,dHei);
                                  startX+=(dWid+minSpaceWithoutElements/2);
                                  startY+=(dWid+minSpaceWithoutElements/2);
                              }else
                              comFrame=new PFrame(pcomFrame.getBottomRight(),dWid,dHei,false);
//                              }else{
//                              }
                          }
                          compartment_frame.put(compid,comFrame);
                          if(compartment_hierarchy.containsKey(compid))calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,compid,repository,comp_reaction,iComp_Elements);
                      }
                  }
              }
        }else{
          //if there is no elements start from pcompartment, then set the innerest one as 5*5, etc. until reset the pcompartment
          // assume only one subcompartment in each compartment  calculate depth
//                int iR=1;
//                for (String compid : compartment_hierarchy.get(pcompid)){
              int iDepth=getDepthOfCom(compartment_hierarchy,pcompid);
              if(iDepth==0){
                  PFrame comFrame=new PFrame(pcomFrame.getCenter(),minSpaceWithoutElements,minSpaceWithoutElements,true);
                  compartment_frame.put(pcompid,comFrame);
              }else{
                  iDepth+=1;
                  for(int m=iDepth;m>0;m--){
                      PFrame comFrame=new PFrame(pcomFrame.getCenter(),iDepth*minSpaceWithoutElements,iDepth*minSpaceWithoutElements,true);
                      compartment_frame.put(pcompid,comFrame);
                      if (m>1)pcompid=compartment_hierarchy.get(pcompid).get(0);
                  }
//                  calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,compartment_hierarchy.get(pcompid).get(0),repository,comp_reaction,iComp_Elements);
              }

//                  iR++;
//                }
                //correct the parent frame's bound
//                PFrame comFrame=new PFrame(pcomFrame.getCenter(),iR*minSpaceWithoutElements,iR*minSpaceWithoutElements,true);
//                compartment_frame.put(pcompid,comFrame);
        }
      }


    static int getDepthOfCom(HashMap<String, ArrayList<String>> compartment_hierarchy, String compId){
        int iDep=0;        //if there's no subcompartment
        if(compartment_hierarchy.containsKey(compId)){
            if(compartment_hierarchy.get(compId).size() ==1)
                iDep=iDep+1+getDepthOfCom(compartment_hierarchy,compartment_hierarchy.get(compId).get(0));
            else{
                //for all find deepest(compartment_frame.containsKey(compId)
                int iDeepest=0;
                for (String coms: compartment_hierarchy.get(compId)){
                    if(getDepthOfCom(compartment_hierarchy,coms)>iDeepest)
                        iDeepest=getDepthOfCom(compartment_hierarchy,coms);
                }
                iDep=iDep+1+iDeepest;
            }
        }
        return iDep;
    }
    static void calcSubFrames_old(HashMap<String, PFrame> compartment_frame,PFrame pcomFrame,HashMap<String, ArrayList<String>> compartment_hierarchy,String pcompid,PathCaseRepository repository,HashMap<String, ArrayList<String>> comp_reaction,HashMap<String,ArrayList<Integer>> iComp_Elements){
        int size =compartment_hierarchy.get(pcompid).size();
        //aWid=(pcomFrame.getWidth()-(size-1)*borderbuffer)/(size+1);
        //double aHeig=(pcomFrame.getHeight()-(size-1)*borderbuffer)/(size+1);
        double startX=pcomFrame.getMinX(), startY=pcomFrame.getMinY();
    if(iComp_Elements.get(pcompid).get(1)>0){
            double pWid=pcomFrame.getWidth(),pHeig=pcomFrame.getHeight();
    //        if(size==1){
            double aWid=pWid*iComp_Elements.get(pcompid).get(0)/iComp_Elements.get(pcompid).get(1);
            double aHeig=pHeig*iComp_Elements.get(pcompid).get(0)/iComp_Elements.get(pcompid).get(1);
            //need to revise
    //        if (aWid<60) aWid=60;
    //        if (aHeig<60) aHeig=60;
    //        if(pcompid.equalsIgnoreCase("root")|| (iComp_Elements.get(pcompid).get(0)==0)){             //No elements in root frame.
    //            aWid=0;
    //            aHeig=0;
    //        }
            if(aWid<minValue)aWid=minValue;
            if(aHeig<minValue)aHeig=minValue;
            startX=pcomFrame.getMinX()+aWid/2;
            startY=pcomFrame.getMinY()+aHeig/2;
            int iCount=0;
            //When calcSubFrames is called, compartment_hierarchy.get(pcompid) is not null
            for (String compid : compartment_hierarchy.get(pcompid))
            {
                iCount++;
                PFrame comFrame=null;
                if(size>1){
                    aWid=pWid*iComp_Elements.get(compid).get(1)/iComp_Elements.get(pcompid).get(1);
                    aHeig=pHeig*iComp_Elements.get(compid).get(1)/iComp_Elements.get(pcompid).get(1);
                    if (aWid<minValue) {aWid=minValue;
                    pWid-=minValue;
                    }

                    if (aHeig<minValue) {aHeig=minValue;
                    pHeig-=minValue;
                    }


                    if(iCount<size){
                    comFrame=new PFrame(new PPoint(startX,startY),aWid,aHeig);
                    startX+=(borderbuffer+aWid);
    //                    startY+=aHeig;
                        if(compartment_hierarchy.containsKey(compid) && size==2){
    //                        comFrame=new PFrame(new PPoint(pcomFrame.getMinX(),pcomFrame.getMinY()),pcomFrame.getWidth()/4,pcomFrame.getHeight()/2);//aWid,aHeig);
                            comFrame=new PFrame(pcomFrame.getUpLeft(),pcomFrame.getWidth(),pcomFrame.getHeight()*1/2);
                        }

                    }else{
                            if(aWid>minValue )
                            //set bottomright point same as parent
                                comFrame=new PFrame(pcomFrame.getBottomRight(),aWid,aHeig,false);
                            else
                                comFrame=new PFrame(new PPoint(pcomFrame.getMinX(),pcomFrame.getHeight()/2),minValue,aHeig);
    //                    }
                        if(compartment_hierarchy.containsKey(compid) && size==2){
    //                        comFrame=new PFrame(new PPoint(pcomFrame.getMinX(),pcomFrame.getMinY()+pcomFrame.getHeight()*2/4),pcomFrame.getWidth()/4,pcomFrame.getHeight()/4);//aWid,aHeig);
                            comFrame=new PFrame(pcomFrame.getBottomRight(),pcomFrame.getWidth()/4,pcomFrame.getHeight()/4,false);

                        }
                    }
                    compartment_frame.put(compid,comFrame);
    //                if(size==1){
    //                    startY+=aHeig;
    //                }
                }else{
                    /*
                    aWid=pcomFrame.getWidth()*iComp_Elements.get(compid).get(1)/iComp_Elements.get(pcompid).get(1);//-minValue;
                    aHeig=pcomFrame.getHeight()*iComp_Elements.get(compid).get(1)/iComp_Elements.get(pcompid).get(1);//-minValue;
                    if(aWid<minValue) aWid=minValue;//pcomFrame.getWidth()/2;
    //                if(iComp_Elements.get(pcompid).get(0)>0 && (pcomFrame.getWidth()-aWid)<minValue) aWid=pcomFrame.getWidth()-minValue;
                    if(aHeig<minValue) aHeig=minValue;//pcomFrame.getHeight()/2;
                    if(iComp_Elements.get(pcompid).get(0)>0 && (pcomFrame.getHeight()-aHeig)<minValue) aHeig=pcomFrame.getHeight()-minValue;
    //                if(iComp_Elements.get(compid).get(0)==0){aWid=0;aHeig=0;}
                    comFrame=new PFrame(pcomFrame.getCenter(),aWid,aHeig,true);
                     if(iComp_Elements.get(pcompid).get(0)>0)compartment_frame.put(compid,comFrame);
                    else compartment_frame.put(compid,pcomFrame);*/

                    aWid=pcomFrame.getWidth()*iComp_Elements.get(compid).get(1)/iComp_Elements.get(pcompid).get(1);//-minValue;
                    aHeig=pcomFrame.getHeight()*iComp_Elements.get(compid).get(1)/iComp_Elements.get(pcompid).get(1);//-minValue;
                    if(aWid<minValue) aWid=minValue;//pcomFrame.getWidth()/2;
    //                if(iComp_Elements.get(pcompid).get(0)>0 && (pcomFrame.getWidth()-aWid)<minValue) aWid=pcomFrame.getWidth()-minValue;
                    if(aHeig<minValue) aHeig=minValue;//pcomFrame.getHeight()/2;
                    if(iComp_Elements.get(pcompid).get(0)>0 && (pcomFrame.getHeight()-aHeig)<minValue) aHeig=pcomFrame.getHeight()-minValue;
    //                if(iComp_Elements.get(compid).get(0)==0){aWid=0;aHeig=0;}
                    comFrame=new PFrame(pcomFrame.getCenter(),aWid,aHeig,true);

                    if(iComp_Elements.get(pcompid).get(0)>0){
                        compartment_frame.put(compid,comFrame);
                    }else{
                        compartment_frame.put(compid,new PFrame(pcomFrame.getCenter(),pcomFrame.getWidth()-testBuffer,pcomFrame.getHeight()-testBuffer,true));
                    }
                }
                if(compartment_hierarchy.get(compid)!=null)calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,compid,repository,comp_reaction,iComp_Elements);
            }
    }else{
        int iR=0;
            for (String compid : compartment_hierarchy.get(pcompid)){
                iR++;
                PFrame comFrame=new PFrame(pcomFrame.getCenter(),5*iR,5*iR,true);
                compartment_frame.put(compid,comFrame);
            }
    }
    }

    static PPoint getRandomPoint(PFrame pFrame,String compId,HashMap<String, ArrayList<String>> compartment_hierarchy,HashMap<String, PFrame> compartment_frame){
        PPoint rPoint = new PPoint(0,0);
        double minX=0,minY=0,maxX=0,maxY=0;
        if(compartment_hierarchy.get(compId)==null) return new PPoint(pFrame.getUpLeft().getX()+(new Random()).nextDouble()*(pFrame.getWidth()-borderbuffer),
                pFrame.getUpLeft().getY()+(new Random()).nextDouble()*(pFrame.getHeight()-borderbuffer));
        else {

            for (String subcompid : compartment_hierarchy.get(compId)) {
                minX=((minX<compartment_frame.get(subcompid).getMinX())?minX:compartment_frame.get(subcompid).getMinX());
                minY=((minY<compartment_frame.get(subcompid).getMinY())?minY:compartment_frame.get(subcompid).getMinY());
                maxX=((maxX>compartment_frame.get(subcompid).getMaxX())?maxX:compartment_frame.get(subcompid).getMaxX());
                maxY=((maxY>compartment_frame.get(subcompid).getMaxY())?maxY:compartment_frame.get(subcompid).getMaxY());
            }
            switch ((new Random()).nextInt(4)){
                case 0:
                    rPoint.setX(pFrame.getMinX()+(new Random()).nextDouble()*(minX-pFrame.getMinX()-borderbuffer));
                    rPoint.setY(pFrame.getMinY()+(new Random()).nextDouble()*(pFrame.getHeight()-borderbuffer));
                    break;
                case 1:
                    rPoint.setX(pFrame.getMaxX()-(new Random()).nextDouble()*(pFrame.getMaxX()-maxX+borderbuffer));
                    rPoint.setY(pFrame.getMinY()+(new Random()).nextDouble()*(pFrame.getHeight()-borderbuffer));
                    break;
                case 2:
                    rPoint.setX(minX+(new Random()).nextDouble()*(maxX-minX));
                    rPoint.setY(pFrame.getMinY()+(new Random()).nextDouble()*(minY-pFrame.getMinY()-borderbuffer));
                    break;
                case 3:
                    rPoint.setX(minX+(new Random()).nextDouble()*(maxX-minX));
                    rPoint.setY(pFrame.getMaxY()-(new Random()).nextDouble()*(pFrame.getMaxY()-maxY+borderbuffer));
                    break;
            }
            return rPoint;
        }
//        return rPoint;
    }

    static PPoint getLayoutedPoint(PFrame curFrame, PPoint offsets){
//        PPoint rPoint = new PPoint(0,0);
//        rPoint.setX(curFrame.getMinX()+offsets.getX());//Math.min(curFrame.getMaxX(),
//        rPoint.setY(curFrame.getMinY()+offsets.getY());//Math.min(curFrame.getMaxY(),
//        return  rPoint;
        return new PPoint(curFrame.getMinX()+offsets.getX(),curFrame.getMinY()+offsets.getY());
    }

    public static HashMap<String, PPoint> ComputeElementsPositions(PFrame curFrame,PathCaseRepository repository, String compid, HashMap<String, ArrayList<String>> comp_reaction,HashMap<String, ArrayList<String>> compartment_hierarchy,HashMap<String, PFrame> compartment_frame)
    {
        HashMap<String, PPoint> computedPositions=new HashMap<String, PPoint>();
        HashMap<String, Node> idToNodeTable = new HashMap<String, Node>();
        HashMap<Node,String> NodeToIdTable = new HashMap<Node,String>();
        DefaultLayoutGraph graph = new DefaultLayoutGraph();
        Node groupNode = graph.createNode();
        graph.setLocation(groupNode,new YPoint(0,0));
        graph.setSize(groupNode, curFrame.getWidth()-10,curFrame.getHeight()-10);

        //create inner compartment node
        if(compartment_hierarchy.containsKey(compid)){
            for (String innercompid : compartment_hierarchy.get(compid))
            {
                PFrame innerFrame=compartment_frame.get(innercompid);
                Node innergroupNode = graph.createNode();
//                graph.setLocation(innergroupNode,new YPoint(0,0));
                graph.setSize(innergroupNode, innerFrame.getWidth(),innerFrame.getHeight());
                if(compartment_hierarchy.get(compid).size()==1)graph.setCenter(innergroupNode,curFrame.getCenterX(),curFrame.getCenterY());
                idToNodeTable.put(innercompid,innergroupNode);
                NodeToIdTable.put(innergroupNode,innercompid);
            }
        }
        //create inner elements
        ArrayList<String> speciesIds = TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid);
        if(speciesIds!=null){
            for(String speciesId:speciesIds){
                String speLabel=TableQueries.getSpeciesLabelBySpeciesId(repository,speciesId);
                if(speLabel==null || speLabel=="") speLabel="Unknown Species";
                Node speciesnode = graph.createNode();
                graph.setSize(speciesnode,20,20);
                idToNodeTable.put(speciesId,speciesnode);
                NodeToIdTable.put(speciesnode,speciesId);
            }
        }

        ArrayList<String> reactionIds = new ArrayList<String>();
        if(comp_reaction.containsKey(compid)){
                    for(String reacId:comp_reaction.get(compid)){
                       String reacLabel=TableQueries.getReactionNamebyID(repository,reacId);
                        reactionIds.add(reacId);
                        if(reacLabel==null || reacLabel=="") reacLabel="Unknown Reactions";
                       Node reacnode = graph.createNode();
                        graph.setSize(reacnode,40,15);
                       idToNodeTable.put(reacId,reacnode);
                        NodeToIdTable.put(reacnode,reacId);
                    }
               }

        for(String reacid:reactionIds){
            ArrayList<String> reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
            for(String spid:reacSpecies){
                Node reac=idToNodeTable.get(reacid);
                Node spec=idToNodeTable.get(spid);
                Edge edge=null;
                if(reac!=null && spec !=null){
                    edge=graph.createEdge(reac,spec);
                 }
               }
            }

        NodeMap nodeId = graph.createNodeMap();
         NodeMap parentNodeId = graph.createNodeMap();
         NodeMap groupKey = graph.createNodeMap();

        graph.addDataProvider(Grouping.NODE_ID_DPKEY, nodeId);
        graph.addDataProvider(Grouping.PARENT_NODE_ID_DPKEY, parentNodeId);
        graph.addDataProvider(Grouping.GROUP_DPKEY, groupKey);
            //mark a node as a group node
        groupKey.setBool(groupNode, true);
        for (NodeCursor nodeCursor =graph.nodes(); nodeCursor.ok(); nodeCursor.next()) {
            Node node = nodeCursor.node();
            String strnodeid=NodeToIdTable.get(node);
            if (strnodeid!=null) {
                nodeId.set(node, strnodeid);
                parentNodeId.set(node, "groupNode");
//                if(compartment_hierarchy.get(compid).contains(strnodeid))
//                   groupKey.setBool(node, true);
            }
            else{
                nodeId.set(node, "groupNode");
            }
        }

//    HierarchicGroupLayouter layouter = new HierarchicGroupLayouter();
        SmartOrganicLayouter layouter = new SmartOrganicLayouter();
        layouter.setNodeEdgeOverlapAvoided(true);
        layouter.setNodeOverlapsAllowed(false);
        layouter.setDeterministic(true);
//        OrganicLayouter layouter = new OrganicLayouter();
//        layouter.setInitialPlacement(OrganicLayouter.ALL);
//        layouter.setGroupBoundsCalculator();

//    layouter.setMinimalLayerDistance(0.0d);
//    layouter.setMinimalEdgeDistance(10.0d);

    new BufferedLayouter(layouter).doLayout(graph);
//
        for (NodeCursor nodeCursor =graph.nodes(); nodeCursor.ok(); nodeCursor.next()) {
            Node node = nodeCursor.node();
            String strnodeid=NodeToIdTable.get(node);
            if (strnodeid!=null) {
                computedPositions.put(strnodeid,new PPoint(graph.getLocation(node).getX(),graph.getLocation(node).getY()));
            }else{
//                computedPositions.put(compid,new PPoint(graph.getSize(node).getWidth(),graph.getSize(node).getHeight()));
//                computedPositions.put(compid,new PPoint(graph.getBoundingBox().getMaxX(),graph.getBoundingBox().getMaxY()));
                compartment_frame.get(compid).setWidth(graph.getBoundingBox().getMaxX());
                compartment_frame.get(compid).setHeight(graph.getBoundingBox().getMaxY());

            }
//            else if(strnodeid!=null){
//            !compartment_hierarchy.get(compid).contains(strnodeid) &&
//            }
        }
//        HierarchyManager hierarchy = new HierarchyManager(graph);
//        for (NodeCursor nodeCursor =graph.nodes(); nodeCursor.ok(); nodeCursor.next()) {
//            Node node = nodeCursor.node();
//            if (!hierarchy.isGroupNode(node)) {
//                computedPositions.put(NodeToIdTable.get(node),new PPoint(graph.getLocation(node).getX(),graph.getLocation(node).getY()));
//            } else if(!NodeToIdTable.get(node).equalsIgnoreCase(compid)){
//                computedPositions.put(NodeToIdTable.get(node),new PPoint(graph.getLocation(node).getX(),graph.getLocation(node).getY()));
//            }else
//            computedPositions.put(compid,new PPoint(graph.getSize(node).getWidth(),graph.getSize(node).getHeight()));
//        }
//       final SmartOrganicLayouter module = new SmartOrganicLayouter();
//       module.doLayout(graph);
        return computedPositions;
    }

    static void calcElements(HashMap<String,ArrayList<Integer>>iComp_Elements, HashMap<String, ArrayList<String>> compartment_hierarchy, String compid){
        if(compartment_hierarchy.get(compid)!=null){
            for(String subs:compartment_hierarchy.get(compid))
            {
                int t=iComp_Elements.get(subs).get(1);
                if(t==-1){
                    calcElements(iComp_Elements,compartment_hierarchy,subs);
                    t=iComp_Elements.get(subs).get(1);
//                    if(iComp_Elements.get(compid).get(1)==-1)iComp_Elements.get(compid).set(1,iComp_Elements.get(subs).get(1));
//                    else iComp_Elements.get(compid).set(1,iComp_Elements.get(compid).get(1)+iComp_Elements.get(subs).get(1));
                }
//                else{
                    if(iComp_Elements.get(compid).get(1)==-1)
                        iComp_Elements.get(compid).set(1,t);
                    else
                        iComp_Elements.get(compid).set(1,iComp_Elements.get(compid).get(1)+t);
//                }
            }
            iComp_Elements.get(compid).set(1,iComp_Elements.get(compid).get(0)+iComp_Elements.get(compid).get(1));
        }else{
            iComp_Elements.get(compid).set(1,iComp_Elements.get(compid).get(0));
        }
    }

    public static HashMap<Node, HashSet<String>> createModelGraphFromSysBioModel(PathCaseRepository repository, Graph2D graph, boolean commons, boolean modulators, boolean pathwaylinks,String appWidth, String appHeight) {
              HashMap<String, Node> moleculeidToNodeTable = new HashMap<String, Node>();
              HashMap<String, Node> idToNodeTable = new HashMap<String, Node>();
              HashMap<String, Node> pathidToNodeTable = new HashMap<String, Node>();
              HashMap<Node, HashSet<String>> nodeToIdsTable = new HashMap<Node, HashSet<String>>();
           HashMap<Node,String> nodeToId = new HashMap<Node, String>();
//03/18/09 added for SysBio Model Visualization By Xinjian
              HashMap<String, Node> modelidToNodeTable = new HashMap<String, Node>();
              HashMap<String, Node> compartmentidToNodeTable = new HashMap<String, Node>();
              HashMap<String, Node> speciesidToNodeTable = new HashMap<String, Node>();
           HashMap<String,ArrayList<Integer>> iComp_Elements = new HashMap<String, ArrayList<Integer>>();
//           HashMap<String, Node> reactionidToNodeTable = new HashMap<String, Node>();
//           HashMap<String, Node> reactionspeciesidToNodeTable = new HashMap<String, Node>();

//           HashSet<String> SubstrateAndProductList = new HashSet<String>();
              HashMap<String, ArrayList<String>> compartment_hierarchy  = new HashMap<String, ArrayList<String>>();
           HashMap<String, PFrame> compartment_frame  = new HashMap<String, PFrame>();
           HashMap<String, PPoint> computedPositions  = new HashMap<String, PPoint>();
              HashMap<String, ArrayList<String>> comp_reaction  = new HashMap<String, ArrayList<String>>();
//q begin
              HierarchyManager hm = new HierarchyManager(graph);
              HashMap<String, NodeList> compartmentQListMap=new HashMap<String, NodeList>(); //q
//            int appletwidth=Integer.parseInt(appWidth)/2,appletheight=Integer.parseInt(appHeight)/2;
           int iBufferForApplet=50;
            int appletwidth=1024-iBufferForApplet,appletheight=768-iBufferForApplet; //for visualizing model and pathway together, set model with 1024*768, and pathway with 800*600
//q end
           SetAcceptableTissueColors();

//03/18/09 added for SysBio Model Visualization By Xinjian
          ArrayList<String> compartmentIds = TableQueries.getCompartmentIDListInRepository(repository);
          ArrayList<String> reactionIds = TableQueries.getReactionIDListInRepository(repository);
           for (String reacid : reactionIds ) {
               String comIdForReac=null;
                  //finding corresponding speciesId from reaction_species table, then find compartment id corresponding to speciesid. Get reaction---compartment relationship
               ArrayList<String> speciesIds = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
               if(speciesIds!=null){
                   for(String spec:speciesIds){
                       comIdForReac=TableQueries.getCompartmentIDBySpeciesID(repository,spec);
                       if(comIdForReac!=null) break;
                   }
                   if(comIdForReac!=null){
                       if(!comp_reaction.containsKey(comIdForReac)) comp_reaction.put(comIdForReac,new ArrayList<String>());
                       comp_reaction.get(comIdForReac).add(reacid);
                   }
               }
           }

           int iRootComps=0;
           String comOutside="";
           //Get hierarchy levels of compartments. from out(root) to inner.
           ArrayList<Integer> eleArray=new ArrayList<Integer>();
           eleArray.add(new Integer(0));
           eleArray.add(new Integer(-1));
           iComp_Elements.put("root",eleArray);
           for (String compid : compartmentIds) {
                  comOutside= TableQueries.getCompartmnetOutsidebyCompartmentID(repository,compid);
                  if (comOutside.equals("")||(comOutside.equals("00000000-0000-0000-0000-000000000000"))) {
                      iRootComps++;
                      if(!compartment_hierarchy.containsKey("root")){
                         compartment_hierarchy.put("root",new ArrayList<String>());
                     }
                     compartment_hierarchy.get("root").add(compid);
                  }else{
                      if(!compartment_hierarchy.containsKey(comOutside)){
                         compartment_hierarchy.put(comOutside,new ArrayList<String>());
                     }
                       compartment_hierarchy.get(comOutside).add(compid);
                  }
               if(!iComp_Elements.containsKey(compid)){
                   eleArray=new ArrayList<Integer>();
                   eleArray.add(new Integer(-1));
                   eleArray.add(new Integer(-1));
                   iComp_Elements.put(compid,eleArray);
               }
               int iSpe=0;
               if(TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid)!=null)
                   iSpe=TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid).size();
               int iRea=0;
               if(comp_reaction.get(compid)!=null)
                   iRea=comp_reaction.get(compid).size();
               iComp_Elements.get(compid).set(0,iSpe+iRea);
           }

           calcElements(iComp_Elements,compartment_hierarchy,"root");

           int iRCompartments=compartment_hierarchy.get("root").size();   // equals to iRootComps
           PFrame comFrame=new PFrame(new PPoint(0,0),new PPoint(appletwidth,appletheight));
           if(iRCompartments==1) {
               compartment_frame.put(compartment_hierarchy.get("root").get(0),comFrame);
               if(compartment_hierarchy.get(compartment_hierarchy.get("root").get(0))!=null)calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,compartment_hierarchy.get("root").get(0),repository,comp_reaction,iComp_Elements);
           }
           else
           {
//            PFrame comFrame=new PFrame(new PPoint(0,0),new PPoint(appletwidth,appletheight));
               compartment_frame.put("root",comFrame);
               calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,"root",repository,comp_reaction,iComp_Elements);
           }

        for (String compid : compartmentIds) {
            if(!compartment_hierarchy.containsKey(compid)){ //
               HashMap<String, PPoint> computedPositionsRoot=ComputeElementsPositions(comFrame,repository,compid,comp_reaction,compartment_hierarchy,compartment_frame);
            }
        }

             int iCompDepth=compartment_hierarchy.keySet().size(); // not exact depth, just want to know whether larger than 1
             int iRoot=0;
           //use arraylist
//        PFrame lastFrame=null;
//        PPoint lastPoint=new PPoint(0,0);
//        double lastMaxX=0;

//        computedPositions=ComputeElementsPositions(compartment_frame.get("root"),repository,"root",comp_reaction,compartment_hierarchy,compartment_frame);


           HashMap<String, PPoint> computedPositionsRoot;

//        for (String compid : compartment_hierarchy.get("root")) {
//             PPoint tm;
////               compartment_frame.get(compid).setWidth();
//             if(!compartment_hierarchy.containsKey(compid)){
////                 PFrame curFrame=compartment_frame.get(compid);
////                 tm=getLayoutedPoint(comFrame,computedPositionsRoot.get(compid));
////                 curFrame.setStartX(tm.getX());
////                 curFrame.setStartY(tm.getY());
//                 computedPositionsRoot=ComputeElementsPositions(comFrame,repository,"root",comp_reaction,compartment_hierarchy,compartment_frame);
//
//             }
//        }


           computedPositionsRoot=ComputeElementsPositions(comFrame,repository,"root",comp_reaction,compartment_hierarchy,compartment_frame);
//        System.out.print("calculate size....");
//        computedPositionsRoot=ComputeElementsPositions(comFrame,repository,"root",comp_reaction,compartment_hierarchy,compartment_frame);
//        System.out.print("calculate position....");

              for (String compid : compartment_hierarchy.get("root")) {
                   PPoint tm;
//               compartment_frame.get(compid).setWidth();
                   if(computedPositionsRoot.containsKey(compid)){
                       PFrame curFrame=compartment_frame.get(compid);
                       tm=getLayoutedPoint(comFrame,computedPositionsRoot.get(compid));
                       curFrame.setStartX(tm.getX());
                       curFrame.setStartY(tm.getY());
                   }
              }

              for (String compid : compartment_hierarchy.get("root")) {
//               if(iRootComps>0) break;
                  iRoot++;
//               String innerCom=compid;
                  //it means no comparment inside compid
                  //System.out.println(gpid);
                  String compartmentlabel = "";



                  PFrame curFrame=compartment_frame.get(compid);
//               computedPositions=ComputeElementsPositions(curFrame,repository,compid,comp_reaction,compartment_hierarchy,compartment_frame);
//               curFrame.setWidth(computedPositions.get(compid).getX());
//               curFrame.setHeight(computedPositions.get(compid).getY());

//               groupNode =hm.createFolderNode(graph);
//               PPoint tm;
//               if(computedPositions.containsKey(compid))
//                tm=getLayoutedPoint(curFrame,computedPositions.get(compid));
//               else tm=curFrame.getUpLeft();
//
//               double fStartX,fStartY;
//               if(iRoot>1){
//                   fStartX=Math.max(lastFrame.getMaxX(),curFrame.getMinX());
//               if(curFrame.getMinX()<lastMaxX)curFrame.setStartX(lastMaxX);
//                   fStartX=curFrame.getMinX();
//               }
//               else fStartX=curFrame.getMinX();

//                else
//                    tm=curFrame.getUpLeft();

                  ///To Change: if comp has elelments(iComp_Elements) then computelementsPositions

                  computedPositions=ComputeElementsPositions(curFrame,repository,compid,comp_reaction,compartment_hierarchy,compartment_frame);
//               fStartY=curFrame.getMinY();
                       Node groupNode;
                  compartmentlabel=TableQueries.getCompartmnetNamebyCompartmentID(repository,compid);
                  if (compartmentlabel.equals("")) compartmentlabel = "Unknown";
//               comOutside= TableQueries.getCompartmnetOutsidebyCompartmentID(repository,compid);
//
//               if (comOutside.equals("")||(comOutside.equals("UNKNOWN_DATA"))){
//                    if(!compartment_hierarchy.containsKey("root")){
//                       compartment_hierarchy.put("root",new ArrayList<String>());
//                   }
//                   compartment_hierarchy.get("root").add(compid);
//                iRoot++;

//               HashSet<String> comlist = new HashSet<String>();
//               comlist.add(compid);
//               nodeToIdsTable.put(groupNode,comlist);

                  if(!compartmentQListMap.containsKey(compid))
                      {
                           compartmentQListMap.put(compid,new NodeList());
                      }


//              if(iComp_Elements.get(compid).get(1)>3 || compartment_hierarchy.get("root").contains(compid)){
                  if(compartment_hierarchy.get("root").contains(compid)){
                      Node fakenode1 = graph.createNode();
                      ShapeNodeRealizer fakeSpen1=getFakedShapeNodeRealizer(curFrame.getMinX()+3,curFrame.getMinY()+3);
                      graph.setRealizer(fakenode1, fakeSpen1);
                       Node fakenode2 = graph.createNode();
                      ShapeNodeRealizer fakeSpen2=getFakedShapeNodeRealizer(curFrame.getMaxX()-3,curFrame.getMaxY()-3);
                      graph.setRealizer(fakenode2, fakeSpen2);
                       idToNodeTable.put(fakenode1.toString(),fakenode1);
                      idToNodeTable.put(fakenode2.toString(),fakenode2);
                      compartmentQListMap.get(compid).add(fakenode1);
                      compartmentQListMap.get(compid).add(fakenode2);
                 }


                  ArrayList<String> speciesIds = TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid);

                  int speCount=0;
                  if(speciesIds!=null){
                      speCount=0;
                      for(String speciesId:speciesIds){
                          String speLabel=TableQueries.getSpeciesLabelBySpeciesId(repository,speciesId);
                          if(speLabel==null || speLabel=="") speLabel="Unknown Species";
                          Node speciesnode = graph.createNode();
                          nodeToId.put(speciesnode,speciesId);
                          speCount++;
//                       ShapeNodeRealizer spenr = getSubstrateProductShapeNodeRealizerCompartmentH(speLabel);
                          ShapeNodeRealizer spenr;
//                        spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,0,0);
                          double cstartX;
//                       if(iRoot>1)cstartX=Math.max(lastFrame.getMaxX()+borderbuffer,curFrame.getMinX()+borderbuffer);
//                       else cstartX=curFrame.getMinX()+borderbuffer;
//                    if(iRCompartments>=1){
//                        PPoint pLayout=getLayoutedPoint(curFrame,getLayoutedPoint(curFrame,computedPositions.get(speciesId)));
//                         spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,getLayoutedPoint(curFrame,getLayoutedPoint(curFrame,computedPositions.get(speciesId))));
                            spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,curFrame.getMinX()+computedPositions.get(speciesId).getX(),curFrame.getMinY()+computedPositions.get(speciesId).getY());
//                        if(lastMaxX<pLayout.getX())lastMaxX=pLayout.getX();
//                    }
//                    else{
//                       if(speCount==1){
//                        spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,fStartX,fStartY);//boundary.getBoundingBox().getMinY()+1,boundary.getBoundingBox().getMaxX()-1);
//                       }
////                       else if(speCount==2)
////                        spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,curFrame.getMaxX()-borderbuffer,curFrame.getMaxY()-borderbuffer);//boundary.getBoundingBox().getMinY()+1,boundary.getBoundingBox().getMaxX()-1);
//                       else
////                        spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,getRandomPoint(curFrame,compid,compartment_hierarchy,compartment_frame));
//                       {
//                           PPoint pLayout=getLayoutedPoint(curFrame,getLayoutedPoint(curFrame,computedPositions.get(speciesId)));
//                           spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,pLayout);// getRandomPoint(curFrame,compid,compartment_hierarchy,compartment_frame));
//                           if(lastMaxX<pLayout.getX())lastMaxX=pLayout.getX();
//                       }}
                           //(new Random()).nextDouble()*190+10,(new Random()).nextDouble()*140+10);//boundary.getBoundingBox().getMinY()+1,boundary.getBoundingBox().getMaxX()-1);
                          graph.setRealizer(speciesnode, spenr);
                          compartmentQListMap.get(compid).add(speciesnode);
                          idToNodeTable.put(speciesId,speciesnode);
                          HashSet<String> spelist = new HashSet<String>();
                          spelist.add(speciesId);
                          nodeToIdsTable.put(speciesnode, spelist);
                      }
                      //if(speCount==1) setinsets to fill
                  }

//               lastFrame=curFrame;
                  //add reactions nodes.
                  if(comp_reaction.containsKey(compid)){
                      int reacCount=0;
                       for(String reacId:comp_reaction.get(compid)){
                          String reacLabel=TableQueries.getReactionNamebyID(repository,reacId);
                           if(reacLabel==null || reacLabel=="") reacLabel="Unknown Reactions";
                          Node reacnode = graph.createNode();
                           nodeToId.put(reacnode,reacId);
//                       ShapeNodeRealizer reacr = getGenericProcessShapeNodeRealizer(reacLabel);
                           ShapeNodeRealizer reacr;
//                        reacr = getGenericProcessShapeNodeRealizer2(reacLabel,0,0);
//                         reacr =  getGenericProcessShapeNodeRealizer2(reacLabel,(new Random()).nextDouble()*190+10,(new Random()).nextDouble()*140+10);
//                       reacr =  getGenericProcessShapeNodeRealizer2(reacLabel,getRandomPoint(curFrame,compid,compartment_hierarchy,compartment_frame));
//                        if(speCount==1 && reacCount==0){//&& iRCompartments==1
//                            reacr =  getGenericProcessShapeNodeRealizer2(reacLabel,curFrame.getBottomRight().getX()-borderbuffer,curFrame.getBottomRight().getY()-borderbuffer);
//                            reacCount+=1;
//                        }else{
//                       PPoint pLayout=getLayoutedPoint(curFrame,computedPositions.get(reacId));
//                       reacr = getGenericProcessShapeNodeRealizer2(reacLabel,pLayout);//getRandomPoint(curFrame,compid,compartment_hierarchy,compartment_frame));
                          reacr = getGenericProcessShapeNodeRealizer2(reacLabel,curFrame.getMinX()+computedPositions.get(reacId).getX(),curFrame.getMinY()+computedPositions.get(reacId).getY());
//                        if(lastMaxX<pLayout.getX())lastMaxX=pLayout.getX();
//                        }
                          graph.setRealizer(reacnode, reacr);
                          compartmentQListMap.get(compid).add(reacnode);
                          idToNodeTable.put(reacId,reacnode);
                          HashSet<String> reaclist = new HashSet<String>();
                          reaclist.add(reacId);
                          nodeToIdsTable.put(reacnode, reaclist);
                       }
                  }
//               hm.groupSubgraph(compartmentQListMap.get(compid),groupNode);      // assigns nodelist to group node
                      //if contains compid, it means compid has inner comparments
                  boolean hasChild=false;
                  if(compartment_hierarchy.containsKey(compid)){
                      hasChild=true;
                      drawCompartments(repository,graph,hm, compartment_hierarchy,compid,idToNodeTable,nodeToIdsTable,compartmentQListMap,comp_reaction, compartment_frame,computedPositions,nodeToId,iComp_Elements);
                  }

      //add edges between reactions and species

                  for(String reacid:reactionIds){
                      ArrayList<String> reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
                      for(String spid:reacSpecies){
                          Node reac=idToNodeTable.get(reacid);
                          Node spec=idToNodeTable.get(spid);
                          Edge edge=null;
                          if(reac!=null && spec !=null){
                              edge=graph.createEdge(reac,spec);
                              EdgeRealizer er = graph.getRealizer(edge);
                              if(TableQueries.getReactionReversibleInRepository(repository,reacid)){
                                      er.setTargetArrow(Arrow.STANDARD);
                                      er.setSourceArrow(Arrow.STANDARD);
                              }else{
                                 String sprole=TableQueries.getSpeciesRoleFromSpeciesID(repository,reacid,spid);
                                   if(sprole.equalsIgnoreCase("Reactant")){
                                       er.setSourceArrow(Arrow.STANDARD);
                                   }else if(sprole.equalsIgnoreCase("Product")){
                                       er.setTargetArrow(Arrow.STANDARD);
                                   }else  if(sprole.equalsIgnoreCase("Modifier")){
                                       er.setSourceArrow(Arrow.WHITE_DELTA);
                                   }else {
                                       er.setLabelText(sprole);
                                   }
                              }
//                           TableQueries.getSpeciesRoleFromReactionSpeciesID(repository,spid)
                              //er.setTargetArrow(Arrow.STANDARD);
                           }else{
                          }
                         }
                      }

                   groupNode =hm.createGroupNode(graph);
                  nodeToId.put(groupNode,compid);
                  double iX=0,iY=0;
//               if(iRoot>1){
                      iX=curFrame.getMinX();//+  graph.getViews(). .getViewPoint().getX();
                      iY=curFrame.getMinY();//+Graph2DView.getViewPoint().getY();

                  System.out.println("location:"+iX+";"+iY);
                   ShapeNodeRealizer nrGroup = getCompShapeNodeRealizerSB(compartmentlabel,iX,iY,curFrame.getWidth(),curFrame.getHeight());//(appletwidth/(iRootComps+1))*iRoot,(appletheight/2)-40,(appletwidth/(iRootComps+1))-5, appletheight-100);//0,0,0,0);//(appletwidth/(iRootComps+1))*iRoot,(appletheight/2)-40,(appletwidth/(iRootComps+1))-5, appletheight-100);//
                  graph.setRealizer(groupNode, nrGroup);

                   idToNodeTable.put(compid,groupNode);

                  //This two line is for those model who has only one level compartment(s).
                  if(iCompDepth==1||hasChild==false)hm.groupSubgraph(compartmentQListMap.get(compid),groupNode);

//getGenericProcessShapeNodeRealizer//
                  HashSet<String> complist = new HashSet<String>();
                  complist.add(compid+":"+iX+":"+iY);
                  nodeToIdsTable.put(groupNode, complist);
              }
              return nodeToIdsTable;
          }


    public static HashMap<Node, HashSet<String>> createModelGraphFromSysBioModel_startfromRoot(PathCaseRepository repository, Graph2D graph, boolean commons, boolean modulators, boolean pathwaylinks,String appWidth, String appHeight) {
           HashMap<String, Node> moleculeidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> idToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> pathidToNodeTable = new HashMap<String, Node>();
           HashMap<Node, HashSet<String>> nodeToIdsTable = new HashMap<Node, HashSet<String>>();
        HashMap<Node,String> nodeToId = new HashMap<Node, String>();
//03/18/09 added for SysBio Model Visualization By Xinjian
           HashMap<String, Node> modelidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> compartmentidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> speciesidToNodeTable = new HashMap<String, Node>();
        HashMap<String,ArrayList<Integer>> iComp_Elements = new HashMap<String, ArrayList<Integer>>();
//           HashMap<String, Node> reactionidToNodeTable = new HashMap<String, Node>();
//           HashMap<String, Node> reactionspeciesidToNodeTable = new HashMap<String, Node>();

//           HashSet<String> SubstrateAndProductList = new HashSet<String>();
           HashMap<String, ArrayList<String>> compartment_hierarchy  = new HashMap<String, ArrayList<String>>();
        HashMap<String, PFrame> compartment_frame  = new HashMap<String, PFrame>();
        HashMap<String, PPoint> computedPositions  = new HashMap<String, PPoint>();
           HashMap<String, ArrayList<String>> comp_reaction  = new HashMap<String, ArrayList<String>>();
//q begin
           HierarchyManager hm = new HierarchyManager(graph);
           HashMap<String, NodeList> compartmentQListMap=new HashMap<String, NodeList>(); //q
//            int appletwidth=Integer.parseInt(appWidth)/2,appletheight=Integer.parseInt(appHeight)/2;
        int iBufferForApplet=50;
         int appletwidth=1024-iBufferForApplet,appletheight=768-iBufferForApplet; //for visualizing model and pathway together, set model with 1024*768, and pathway with 800*600
//q end
        SetAcceptableTissueColors();

//03/18/09 added for SysBio Model Visualization By Xinjian
       ArrayList<String> compartmentIds = TableQueries.getCompartmentIDListInRepository(repository);
       ArrayList<String> reactionIds = TableQueries.getReactionIDListInRepository(repository);
        for (String reacid : reactionIds ) {
            String comIdForReac=null;
               //finding corresponding speciesId from reaction_species table, then find compartment id corresponding to speciesid. Get reaction---compartment relationship
            ArrayList<String> speciesIds = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
            if(speciesIds!=null){
                for(String spec:speciesIds){
                    comIdForReac=TableQueries.getCompartmentIDBySpeciesID(repository,spec);
                    if(comIdForReac!=null) break;
                }
                if(comIdForReac!=null){
                    if(!comp_reaction.containsKey(comIdForReac)) comp_reaction.put(comIdForReac,new ArrayList<String>());
                    comp_reaction.get(comIdForReac).add(reacid);
                }
            }
        }

/*
        HashMap<String, String> commonId2NameTable = new HashMap<String, String>();
        for (String subsorprod : SubstrateAndProductList) {
               //System.out.println(subsorprod);
               String moleculename = TableQueries.getMoleculeNameById(repository, subsorprod);
               //System.out.println(moleculename);
               boolean iscommon = TableQueries.getMoleculeCommonById(repository, subsorprod);
               //System.out.println(iscommon);
               if (!iscommon) {
                   Node substratenode = graph.createNode();
                   ShapeNodeRealizer nr = getSubstrateProductShapeNodeRealizer(moleculename, iscommon);
                   graph.setRealizer(substratenode, nr);
                   moleculeidToNodeTable.put(subsorprod, substratenode);
                   HashSet<String> metabolitelist = new HashSet<String>();
                   metabolitelist.add(subsorprod);
                   nodeToIdsTable.put(substratenode, metabolitelist);

                   //q begin
                   String strcompartmentName =  getCompartmentByMoleName(moleculename);
                    if(!compartmentQListMap.containsKey(strcompartmentName) )
                       {
                            compartmentQListMap.put(strcompartmentName,new NodeList());
                       }

                     compartmentQListMap.get(strcompartmentName).add(substratenode);
                   //q end

               } else if (commons) {
                   commonId2NameTable.put(subsorprod, moleculename);
               }
               //view.getGraph2D().hide(substratenode);
           }
       */    //load all compartments
        int iRootComps=0;
        String comOutside="";
        //Get hierarchy levels of compartments. from out(root) to inner.
        ArrayList<Integer> eleArray=new ArrayList<Integer>();
        eleArray.add(new Integer(0));
        eleArray.add(new Integer(-1));
        iComp_Elements.put("root",eleArray);
        for (String compid : compartmentIds) {
               //System.out.println(gpid);
//               String compartmentlabel = "";
//                Node groupNode;
//               compartmentlabel=TableQueries.getCompartmnetNamebyCompartmentID(repository,compid);
//               if (compartmentlabel.equals("")) compartmentlabel = "Unknown";

               comOutside= TableQueries.getCompartmnetOutsidebyCompartmentID(repository,compid);
               if (comOutside.equals("")||(comOutside.equals("00000000-0000-0000-0000-000000000000"))) {
                   iRootComps++;
                   if(!compartment_hierarchy.containsKey("root")){
                      compartment_hierarchy.put("root",new ArrayList<String>());
                  }
                  compartment_hierarchy.get("root").add(compid);
               }else{
                   if(!compartment_hierarchy.containsKey(comOutside)){
                      compartment_hierarchy.put(comOutside,new ArrayList<String>());
                  }
                    compartment_hierarchy.get(comOutside).add(compid);
               }
            if(!iComp_Elements.containsKey(compid)){
                eleArray=new ArrayList<Integer>();
                eleArray.add(new Integer(-1));
                eleArray.add(new Integer(-1));
                iComp_Elements.put(compid,eleArray);
            }
            int iSpe=0;
            if(TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid)!=null)
                iSpe=TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid).size();
            int iRea=0;
            if(comp_reaction.get(compid)!=null)
                iRea=comp_reaction.get(compid).size();
            iComp_Elements.get(compid).set(0,iSpe+iRea);
        }
//        for(String compid:compartmentIds){
//            if(!compartment_hierarchy.containsKey(compid))iComp_Elements.get(compid).set(1,iComp_Elements.get(compid).get(0));
////            comOutside= TableQueries.getCompartmnetOutsidebyCompartmentID(repository,compid);
////               if (comOutside.equals("")||(comOutside.equals("00000000-0000-0000-0000-000000000000"))) comOutside="root";
////               if(iComp_Elements.get(comOutside).get(1)==-1)iComp_Elements.get(comOutside).set(1,iComp_Elements.get(compid).get(1));
//        }
        calcElements(iComp_Elements,compartment_hierarchy,"root");

//        int tmp=0;
//        for (String compid : compartmentIds) {
//
//        }
//        tmp=getDepthOfCom(compartment_hierarchy,"root");

        int iRCompartments=compartment_hierarchy.get("root").size();   // equals to iRootComps
        PFrame comFrame=new PFrame(new PPoint(0,0),new PPoint(appletwidth,appletheight));
        if(iRCompartments==1) {
            compartment_frame.put(compartment_hierarchy.get("root").get(0),comFrame);
            if(compartment_hierarchy.get(compartment_hierarchy.get("root").get(0))!=null)calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,compartment_hierarchy.get("root").get(0),repository,comp_reaction,iComp_Elements);
        }
        else
        {
//            PFrame comFrame=new PFrame(new PPoint(0,0),new PPoint(appletwidth,appletheight));
            compartment_frame.put("root",comFrame);
            calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,"root",repository,comp_reaction,iComp_Elements);

//            int startX=0,startY=0; //iBuffer=0,
//            int ic=0;
//            for (String compid : compartment_hierarchy.get("root"))
//            {
//                ic++;
//                startX+=borderbuffer;startY+=borderbuffer;
//                /*PFrame comFrame=new PFrame(new PPoint(startX,startY),(appletwidth-(iRCompartments+1)*borderbuffer)/iRCompartments,appletheight);
//                compartment_frame.put(compid,comFrame);
//                if(compartment_hierarchy.get(compid)!=null)calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,compid,repository,comp_reaction,iComp_Elements);
//                startX+=(((appletwidth-(iRCompartments+1)*borderbuffer)/iRCompartments)+borderbuffer);*/
////                if(iBuffer==0)iBuffer=1;
//                double aWid=(appletwidth-(iRCompartments+1)*borderbuffer)*iComp_Elements.get(compid).get(1)/iComp_Elements.get("root").get(1);
//                double aHeig=appletheight-2*borderbuffer;//*iComp_Elements.get(compid).get(1)/iComp_Elements.get("root").get(1);
//
//
//                PFrame comFrame=new PFrame(new PPoint(startX,startY),aWid,aHeig);
//                compartment_frame.put(compid,comFrame);
//                if(compartment_hierarchy.get(compid)!=null)calcSubFrames(compartment_frame,comFrame,compartment_hierarchy,compid,repository,comp_reaction,iComp_Elements);
//                startX+=aWid;//(((appletwidth-(iRCompartments+1)*borderbuffer)/iRCompartments)+borderbuffer);
////                startY+=appletheight/2;
////                startY+=aHeig;
//            }
        }

          int iCompDepth=compartment_hierarchy.keySet().size(); // not exact depth, just want to know whether larger than 1
          int iRoot=0;
        //use arraylist
//        PFrame lastFrame=null;
//        PPoint lastPoint=new PPoint(0,0);
//        double lastMaxX=0;

//        computedPositions=ComputeElementsPositions(compartment_frame.get("root"),repository,"root",comp_reaction,compartment_hierarchy,compartment_frame);


        HashMap<String, PPoint> computedPositionsRoot;

//        for (String compid : compartment_hierarchy.get("root")) {
//             PPoint tm;
////               compartment_frame.get(compid).setWidth();
//             if(!compartment_hierarchy.containsKey(compid)){
////                 PFrame curFrame=compartment_frame.get(compid);
////                 tm=getLayoutedPoint(comFrame,computedPositionsRoot.get(compid));
////                 curFrame.setStartX(tm.getX());
////                 curFrame.setStartY(tm.getY());
//                 computedPositionsRoot=ComputeElementsPositions(comFrame,repository,"root",comp_reaction,compartment_hierarchy,compartment_frame);
//
//             }
//        }


        computedPositionsRoot=ComputeElementsPositions(comFrame,repository,"root",comp_reaction,compartment_hierarchy,compartment_frame);
//        System.out.print("calculate size....");
//        computedPositionsRoot=ComputeElementsPositions(comFrame,repository,"root",comp_reaction,compartment_hierarchy,compartment_frame);
//        System.out.print("calculate position....");

           for (String compid : compartment_hierarchy.get("root")) {
                PPoint tm;
//               compartment_frame.get(compid).setWidth();
                if(computedPositionsRoot.containsKey(compid)){
                    PFrame curFrame=compartment_frame.get(compid);
                    tm=getLayoutedPoint(comFrame,computedPositionsRoot.get(compid));
                    curFrame.setStartX(tm.getX());
                    curFrame.setStartY(tm.getY());
                }
           }

           for (String compid : compartment_hierarchy.get("root")) {
//               if(iRootComps>0) break;
               iRoot++;
//               String innerCom=compid;
               //it means no comparment inside compid
               //System.out.println(gpid);
               String compartmentlabel = "";



               PFrame curFrame=compartment_frame.get(compid);
//               computedPositions=ComputeElementsPositions(curFrame,repository,compid,comp_reaction,compartment_hierarchy,compartment_frame);
//               curFrame.setWidth(computedPositions.get(compid).getX());
//               curFrame.setHeight(computedPositions.get(compid).getY());

//               groupNode =hm.createFolderNode(graph);
//               PPoint tm;
//               if(computedPositions.containsKey(compid))
//                tm=getLayoutedPoint(curFrame,computedPositions.get(compid));
//               else tm=curFrame.getUpLeft();
//
//               double fStartX,fStartY;
//               if(iRoot>1){
//                   fStartX=Math.max(lastFrame.getMaxX(),curFrame.getMinX());
//               if(curFrame.getMinX()<lastMaxX)curFrame.setStartX(lastMaxX);
//                   fStartX=curFrame.getMinX();
//               }
//               else fStartX=curFrame.getMinX();

//                else
//                    tm=curFrame.getUpLeft();

               ///To Change: if comp has elelments(iComp_Elements) then computelementsPositions

               computedPositions=ComputeElementsPositions(curFrame,repository,compid,comp_reaction,compartment_hierarchy,compartment_frame);
//               fStartY=curFrame.getMinY();
                    Node groupNode;
               compartmentlabel=TableQueries.getCompartmnetNamebyCompartmentID(repository,compid);
               if (compartmentlabel.equals("")) compartmentlabel = "Unknown";
//               comOutside= TableQueries.getCompartmnetOutsidebyCompartmentID(repository,compid);
//
//               if (comOutside.equals("")||(comOutside.equals("UNKNOWN_DATA"))){
//                    if(!compartment_hierarchy.containsKey("root")){
//                       compartment_hierarchy.put("root",new ArrayList<String>());
//                   }
//                   compartment_hierarchy.get("root").add(compid);
//                iRoot++;

//               HashSet<String> comlist = new HashSet<String>();
//               comlist.add(compid);
//               nodeToIdsTable.put(groupNode,comlist);

               if(!compartmentQListMap.containsKey(compid))
                   {
                        compartmentQListMap.put(compid,new NodeList());
                   }


//              if(iComp_Elements.get(compid).get(1)>3 || compartment_hierarchy.get("root").contains(compid)){
//               if(compartment_hierarchy.get("root").contains(compid)){
//                   Node fakenode1 = graph.createNode();
//                   ShapeNodeRealizer fakeSpen1=getFakedShapeNodeRealizer(curFrame.getMinX()+3,curFrame.getMinY()+3);
//                   graph.setRealizer(fakenode1, fakeSpen1);
//                    Node fakenode2 = graph.createNode();
//                   ShapeNodeRealizer fakeSpen2=getFakedShapeNodeRealizer(curFrame.getMaxX()-3,curFrame.getMaxY()-3);
//                   graph.setRealizer(fakenode2, fakeSpen2);
//                    idToNodeTable.put(fakenode1.toString(),fakenode1);
//                   idToNodeTable.put(fakenode2.toString(),fakenode2);
//                   compartmentQListMap.get(compid).add(fakenode1);
//                   compartmentQListMap.get(compid).add(fakenode2);
//              }


               ArrayList<String> speciesIds = TableQueries.getSpeciesIDListInRepositoryByCompartmentId(repository,compid);

               int speCount=0;
               if(speciesIds!=null){
                   speCount=0;
                   for(String speciesId:speciesIds){
                       String speLabel=TableQueries.getSpeciesLabelBySpeciesId(repository,speciesId);
                       if(speLabel==null || speLabel=="") speLabel="Unknown Species";
                       Node speciesnode = graph.createNode();
                       nodeToId.put(speciesnode,speciesId);
                       speCount++;
//                       ShapeNodeRealizer spenr = getSubstrateProductShapeNodeRealizerCompartmentH(speLabel);
                       ShapeNodeRealizer spenr;
//                        spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,0,0);
                       double cstartX;
//                       if(iRoot>1)cstartX=Math.max(lastFrame.getMaxX()+borderbuffer,curFrame.getMinX()+borderbuffer);
//                       else cstartX=curFrame.getMinX()+borderbuffer;
//                    if(iRCompartments>=1){
//                        PPoint pLayout=getLayoutedPoint(curFrame,getLayoutedPoint(curFrame,computedPositions.get(speciesId)));
//                         spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,getLayoutedPoint(curFrame,getLayoutedPoint(curFrame,computedPositions.get(speciesId))));
                         spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,curFrame.getMinX()+computedPositions.get(speciesId).getX(),curFrame.getMinY()+computedPositions.get(speciesId).getY());
//                        if(lastMaxX<pLayout.getX())lastMaxX=pLayout.getX();
//                    }
//                    else{
//                       if(speCount==1){
//                        spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,fStartX,fStartY);//boundary.getBoundingBox().getMinY()+1,boundary.getBoundingBox().getMaxX()-1);
//                       }
////                       else if(speCount==2)
////                        spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,curFrame.getMaxX()-borderbuffer,curFrame.getMaxY()-borderbuffer);//boundary.getBoundingBox().getMinY()+1,boundary.getBoundingBox().getMaxX()-1);
//                       else
////                        spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,getRandomPoint(curFrame,compid,compartment_hierarchy,compartment_frame));
//                       {
//                           PPoint pLayout=getLayoutedPoint(curFrame,getLayoutedPoint(curFrame,computedPositions.get(speciesId)));
//                           spenr = getSubstrateProductShapeNodeRealizerCompartmentH2(speLabel,pLayout);// getRandomPoint(curFrame,compid,compartment_hierarchy,compartment_frame));
//                           if(lastMaxX<pLayout.getX())lastMaxX=pLayout.getX();
//                       }}
                        //(new Random()).nextDouble()*190+10,(new Random()).nextDouble()*140+10);//boundary.getBoundingBox().getMinY()+1,boundary.getBoundingBox().getMaxX()-1);
                       graph.setRealizer(speciesnode, spenr);
                       compartmentQListMap.get(compid).add(speciesnode);
                       idToNodeTable.put(speciesId,speciesnode);
                       HashSet<String> spelist = new HashSet<String>();
                       spelist.add(speciesId);
                       nodeToIdsTable.put(speciesnode, spelist);
                   }
                   //if(speCount==1) setinsets to fill
               }

//               lastFrame=curFrame;
               //add reactions nodes.
               if(comp_reaction.containsKey(compid)){
                   int reacCount=0;
                    for(String reacId:comp_reaction.get(compid)){
                       String reacLabel=TableQueries.getReactionNamebyID(repository,reacId);
                        if(reacLabel==null || reacLabel=="") reacLabel="Unknown Reactions";
                       Node reacnode = graph.createNode();
                        nodeToId.put(reacnode,reacId);
//                       ShapeNodeRealizer reacr = getGenericProcessShapeNodeRealizer(reacLabel);
                        ShapeNodeRealizer reacr;
//                        reacr = getGenericProcessShapeNodeRealizer2(reacLabel,0,0);
//                         reacr =  getGenericProcessShapeNodeRealizer2(reacLabel,(new Random()).nextDouble()*190+10,(new Random()).nextDouble()*140+10);
//                       reacr =  getGenericProcessShapeNodeRealizer2(reacLabel,getRandomPoint(curFrame,compid,compartment_hierarchy,compartment_frame));
//                        if(speCount==1 && reacCount==0){//&& iRCompartments==1
//                            reacr =  getGenericProcessShapeNodeRealizer2(reacLabel,curFrame.getBottomRight().getX()-borderbuffer,curFrame.getBottomRight().getY()-borderbuffer);
//                            reacCount+=1;
//                        }else{
//                       PPoint pLayout=getLayoutedPoint(curFrame,computedPositions.get(reacId));
//                       reacr = getGenericProcessShapeNodeRealizer2(reacLabel,pLayout);//getRandomPoint(curFrame,compid,compartment_hierarchy,compartment_frame));
                       reacr = getGenericProcessShapeNodeRealizer2(reacLabel,curFrame.getMinX()+computedPositions.get(reacId).getX(),curFrame.getMinY()+computedPositions.get(reacId).getY());
//                        if(lastMaxX<pLayout.getX())lastMaxX=pLayout.getX();
//                        }
                       graph.setRealizer(reacnode, reacr);
                       compartmentQListMap.get(compid).add(reacnode);
                       idToNodeTable.put(reacId,reacnode);
                       HashSet<String> reaclist = new HashSet<String>();
                       reaclist.add(reacId);
                       nodeToIdsTable.put(reacnode, reaclist);
                    }
               }
//               hm.groupSubgraph(compartmentQListMap.get(compid),groupNode);      // assigns nodelist to group node
                   //if contains compid, it means compid has inner comparments
               boolean hasChild=false;
               if(compartment_hierarchy.containsKey(compid)){
                   hasChild=true;
                   drawCompartments(repository,graph,hm, compartment_hierarchy,compid,idToNodeTable,nodeToIdsTable,compartmentQListMap,comp_reaction, compartment_frame,computedPositions,nodeToId,iComp_Elements);
               }

   //add edges between reactions and species

               for(String reacid:reactionIds){
                   ArrayList<String> reacSpecies = TableQueries.getSpeciesIDListFromReactionID(repository,reacid);
                   for(String spid:reacSpecies){
                       Node reac=idToNodeTable.get(reacid);
                       Node spec=idToNodeTable.get(spid);
                       Edge edge=null;
                       if(reac!=null && spec !=null){
                           edge=graph.createEdge(reac,spec);
                           EdgeRealizer er = graph.getRealizer(edge);
                           if(TableQueries.getReactionReversibleInRepository(repository,reacid)){
                                   er.setTargetArrow(Arrow.STANDARD);
                                   er.setSourceArrow(Arrow.STANDARD);
                           }else{
                              String sprole=TableQueries.getSpeciesRoleFromSpeciesID(repository,reacid,spid);
                                if(sprole.equalsIgnoreCase("Reactant")){
                                    er.setSourceArrow(Arrow.STANDARD);
                                }else if(sprole.equalsIgnoreCase("Product")){
                                    er.setTargetArrow(Arrow.STANDARD);
                                }else  if(sprole.equalsIgnoreCase("Modifier")){
                                    er.setSourceArrow(Arrow.WHITE_DIAMOND);
                                }else {
                                    er.setLabelText(sprole);
                                }
                           }
//                           TableQueries.getSpeciesRoleFromReactionSpeciesID(repository,spid)
                           //er.setTargetArrow(Arrow.STANDARD);
                        }else{
//                           Edge edge=graph.createEdge(reac,reac);
//                           EdgeRealizer er = graph.getRealizer(edge);
//                           er.setLabelText(reacid+"  "+spid);
//                           er.setTargetArrow(Arrow.STANDARD);
                       }
//                       compartmentQListMap.get(compid).add(edge);
//                       hm.groupSubgraph(edge,groupNode);
//                       hm.

//                       idToNodeTable.put(reacId,edge);
                      }
                   }

                groupNode =hm.createGroupNode(graph);
               nodeToId.put(groupNode,compid);
               double iX=600,iY=200;
//               if(iRoot>1){
                   iX=curFrame.getMinX();//+  graph.getViews(). .getViewPoint().getX();
                   iY=curFrame.getMinY();//+Graph2DView.getViewPoint().getY();

//               }
//               else{
//                   iX=200;
//                   iY=200;
//               }
               System.out.println("location:"+iX+";"+iY);
                ShapeNodeRealizer nrGroup = getCompShapeNodeRealizerSB(compartmentlabel,iX,iY,curFrame.getWidth(),curFrame.getHeight());//(appletwidth/(iRootComps+1))*iRoot,(appletheight/2)-40,(appletwidth/(iRootComps+1))-5, appletheight-100);//0,0,0,0);//(appletwidth/(iRootComps+1))*iRoot,(appletheight/2)-40,(appletwidth/(iRootComps+1))-5, appletheight-100);//
//               ShapeNodeRealizer nrGroup =new ShapeNodeRealizer();
//                ((AutoBoundsFeature)nrGroup).setAutoBoundsEnabled(false);
//               nrGroup.setSize(curFrame.getWidth(),curFrame.getHeight());
//               nrGroup.setLocation(computedPositions.get(compid).getX(),computedPositions.get(compid).getY());
//               nrGroup.setVisible(true);
//               if(iRoot==2)nrGroup.setLocation(200,500);
               graph.setRealizer(groupNode, nrGroup);

//               nrGroup.setGroupClosed(false);
//               nrGroup.set

                idToNodeTable.put(compid,groupNode);

               //This two line is for those model who has only one level compartment(s).
               if(iCompDepth==1||hasChild==false)hm.groupSubgraph(compartmentQListMap.get(compid),groupNode);

//getGenericProcessShapeNodeRealizer//
               HashSet<String> complist = new HashSet<String>();
               complist.add(compid+":"+iX+":"+iY);
               nodeToIdsTable.put(groupNode, complist);
           }

           return nodeToIdsTable;
       }

    public static HashMap<Node, HashSet<String>> createGraphFromWholeRepositoryTissueCompartmentH(PathCaseRepository repository, Graph2D graph, boolean commons, boolean modulators, boolean pathwaylinks) {

           HashMap<String, Node> moleculeidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> gpidToNodeTable = new HashMap<String, Node>();
           HashMap<String, Node> pathidToNodeTable = new HashMap<String, Node>();
           HashMap<Node, HashSet<String>> nodeToIdsTable = new HashMap<Node, HashSet<String>>();

           HashSet<String> SubstrateAndProductList = new HashSet<String>();
//q begin
           HierarchyManager hm = new HierarchyManager(graph);
           HashMap<String, NodeList> compartmentQListMap=new HashMap<String, NodeList>(); //q
//q end
        SetAcceptableTissueColors();
           //load all processes
           ArrayList<String> genericProcessIds = TableQueries.getGenericProcessIDListInRepository(repository);
           for (String gpid : genericProcessIds) {
               //System.out.println(gpid);
               String processlabel = "";

               HashSet<String> ECNumbers = TableQueries.getECNumbersByGenericProcessID(repository, gpid);

               if (ECNumbers != null && ECNumbers.size() > 0 && ECNumbers.size() < 4) {
                   String ECString = "";
                   ArrayList<String> ECNumberslist = new ArrayList<String>(ECNumbers);
                   for (int eci = 0; eci < ECNumberslist.size(); eci++) {
                       String ECNumber = ECNumberslist.get(eci);
                       ECString += ECNumber.trim();
                       if (eci < ECNumberslist.size() - 1)
                           ECString += "\n";
                   }
                   processlabel = ECString;
               }


               if (processlabel.equals("")) {
                   processlabel = TableQueries.getGenericProcessNamebyGenericProcessID(repository, gpid);
               }

               if (processlabel.equals("")) processlabel = "Unknown";

               Node genericprocessnode = graph.createNode();
               ShapeNodeRealizer nr = getGenericProcessShapeNodeRealizer(processlabel);

               graph.setRealizer(genericprocessnode, nr);

               gpidToNodeTable.put(gpid, genericprocessnode);
               HashSet<String> processlist = new HashSet<String>();
               processlist.add(gpid);
               nodeToIdsTable.put(genericprocessnode, processlist);
               SubstrateAndProductList.addAll(TableQueries.getSubstrateAndProductsByGenericProcessId(repository, gpid));

               //q begin
               String strcompartmentName=getCompartmentByProcessName(processlabel);

               if(!compartmentQListMap.containsKey(strcompartmentName))
               {
                   compartmentQListMap.put(strcompartmentName,new NodeList());
               }

               compartmentQListMap.get(strcompartmentName).add(genericprocessnode);
           }
              //q end

           HashSet<String> CollapsedSubstrateAndProductList = new HashSet<String>();

           //load all collapsedPathways
           HashSet<String> pathwayIds = TableQueries.getCollapsedPathwaysIdListInRepository(repository, pathwaylinks);

           for (String pathwayid : pathwayIds) {
               String pathwayname = TableQueries.getPathwayNameById(repository, pathwayid);
               Node pathwaynode = graph.createNode();
               ShapeNodeRealizer nr = getCollapsedPathwayShapeNodeRealizer(pathwayname);
               graph.setRealizer(pathwaynode, nr);

               pathidToNodeTable.put(pathwayid, pathwaynode);
               HashSet<String> pathwaylist = new HashSet<String>();
               pathwaylist.add(pathwayid);
               nodeToIdsTable.put(pathwaynode, pathwaylist);
               //view.getGraph2D().hide(substratenode);
               HashSet<String> metabolitesOfPathway = TableQueries.getSubstrateAndProductsByCollapsedPathwayId(repository, pathwayid);

               //old version
               //SubstrateAndProductList.addAll(metabolitesOfPathway);
               //Note, will not work if a pathway has multiple versions of the same thing
               if (metabolitesOfPathway != null)
                   for (String metabolite : metabolitesOfPathway) {
                       //add to substrate list if only it is being added for the second time
                       if (!CollapsedSubstrateAndProductList.contains(metabolite))
                           CollapsedSubstrateAndProductList.add(metabolite);
                       else
                           SubstrateAndProductList.add(metabolite);
                   }
           }

           HashMap<String, String> commonId2NameTable = new HashMap<String, String>();

           //load all substrate and products
           for (String subsorprod : SubstrateAndProductList) {
               //System.out.println(subsorprod);
               String moleculename = TableQueries.getMoleculeNameById(repository, subsorprod);
               //System.out.println(moleculename);
               boolean iscommon = TableQueries.getMoleculeCommonById(repository, subsorprod);
               //System.out.println(iscommon);
               if (!iscommon) {
                   Node substratenode = graph.createNode();
                   ShapeNodeRealizer nr = getSubstrateProductShapeNodeRealizer(moleculename, iscommon);
                   graph.setRealizer(substratenode, nr);
                   moleculeidToNodeTable.put(subsorprod, substratenode);
                   HashSet<String> metabolitelist = new HashSet<String>();
                   metabolitelist.add(subsorprod);
                   nodeToIdsTable.put(substratenode, metabolitelist);

                   //q begin
                   String strcompartmentName =  getCompartmentByMoleName(moleculename);
                    if(!compartmentQListMap.containsKey(strcompartmentName) )
                       {
                            compartmentQListMap.put(strcompartmentName,new NodeList());
                       }

                     compartmentQListMap.get(strcompartmentName).add(substratenode);
                   //q end

               } else if (commons) {
                   commonId2NameTable.put(subsorprod, moleculename);
               }
               //view.getGraph2D().hide(substratenode);
           }

           //add edges between substrates/products and processes
           ArrayList<MoleculeProcessPair> edgeidpairs = TableQueries.getSubstrateProductProcessEdges(repository);
           for (MoleculeProcessPair sp : edgeidpairs) {
               Node molecule = moleculeidToNodeTable.get(sp.molecule);
               Node process = gpidToNodeTable.get(sp.process);

               if (molecule == null) {
                   String commonname = commonId2NameTable.get(sp.molecule);
                   if (commonname != null) {
                       molecule = graph.createNode();
                       ShapeNodeRealizer nr = getSubstrateProductShapeNodeRealizer(commonname, true);
                       graph.setRealizer(molecule, nr);
                       HashSet<String> metabolitelist = new HashSet<String>();
                       metabolitelist.add(sp.molecule);
                       nodeToIdsTable.put(molecule, metabolitelist);
                   } else {
                       continue;
                   }
               }

               if (sp.input) {
                   Edge edge = graph.createEdge(molecule, process);
                   EdgeRealizer er = graph.getRealizer(edge);
                   er.setTargetArrow(Arrow.STANDARD);
                   if (sp.reversible)
                       er.setSourceArrow(Arrow.STANDARD);
                   makeMetaboliteEdgeNormal(er);
               } else {
                   Edge edge = graph.createEdge(process, molecule);
                   EdgeRealizer er = graph.getRealizer(edge);
                   er.setTargetArrow(Arrow.STANDARD);
                   if (sp.reversible)
                       er.setSourceArrow(Arrow.STANDARD);
                   makeMetaboliteEdgeNormal(er);
               }
           }

           //add edges between metabolites and pathways
           //always from pathways to metabolites
           edgeidpairs = TableQueries.getLinkingMoleculePathwayEdges(repository, pathwaylinks);
           for (MoleculeProcessPair sp : edgeidpairs) {
               Node item1 = moleculeidToNodeTable.get(sp.molecule);
               Node item2 = pathidToNodeTable.get(sp.process);

               if (item1 == null || item2 == null) continue;

               Edge edge = graph.createEdge(item1, item2);
               EdgeRealizer er = graph.getRealizer(edge);
               er.setTargetArrow(Arrow.STANDARD);
               makeMetaboliteEdgeNormal(er);
           }


           if (modulators) {
               //add edges between regulators and genericprocesses
               HashMap<String, HashMap<String, ArrayList<String>>> edgeidmarkpairs = TableQueries.getRegulatorProcessEdges(repository);
               for (String gpid : edgeidmarkpairs.keySet()) {

                   HashMap<String, ArrayList<String>> gpidRegulatorMap = edgeidmarkpairs.get(gpid);
                   ArrayList<String> genericregulators = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.REGULATOR);
                   ArrayList<String> inhibitors = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.INHIBITOR);
                   ArrayList<String> activators = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.ACTIVATOR);

                   Node process = gpidToNodeTable.get(gpid);

                   //create generic regulator node
                   String regulatorname = "";
                   for (String regid : genericregulators) {
                       String moleculename = TableQueries.getMoleculeNameById(repository, regid);
                       if (moleculename != null && !moleculename.equals("")) {
                           if (regulatorname.equalsIgnoreCase(""))
                               regulatorname += moleculename;
                           else
                               regulatorname += ", " + moleculename;
                       }
                   }

                   if (!regulatorname.equals("")) {
                       Node regulatornode = graph.createNode();
                       ShapeNodeRealizer nr = getRegulatorShapeNodeRealizer(regulatorname, TableQueries.MOLECULE_ROLE_STRINGS.REGULATOR);
                       graph.setRealizer(regulatornode, nr);
                       nodeToIdsTable.put(regulatornode, new HashSet<String>(genericregulators));

                       Edge edge = graph.createEdge(regulatornode, process);
                       EdgeRealizer er = graph.getRealizer(edge);
                       er.setLineType(LineType.LINE_1);
                       makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                       //er.setTargetArrow(Arrow.STANDARD);
                   }

                   //create activator node
                   String activatorname = "";
                   for (String actid : activators) {
                       String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                       if (moleculename != null && !moleculename.equals("")) {
                           if (activatorname.equalsIgnoreCase(""))
                               activatorname += moleculename;
                           else
                               activatorname += ", " + moleculename;
                       }
                   }

                   if (!activatorname.equals("")) {
                       Node activatornode = graph.createNode();
                       ShapeNodeRealizer nr = getRegulatorShapeNodeRealizer(activatorname, TableQueries.MOLECULE_ROLE_STRINGS.ACTIVATOR);
                       graph.setRealizer(activatornode, nr);
                       nodeToIdsTable.put(activatornode, new HashSet<String>(activators));

                       Edge edge = graph.createEdge(activatornode, process);
                       EdgeRealizer er = graph.getRealizer(edge);
                       er.setLineType(LineType.LINE_1);
                       makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);

                       int[] xpoints = {0, -4, -4};
                       int[] ypoints = {0, -4, 4};
                       er.setTargetArrow(Arrow.addCustomArrow("ActivatorArrow", new Polygon(xpoints, ypoints, 3), Color.white));
                   }

                   //create inhibitor node
                   String inhibitorname = "";
                   for (String actid : inhibitors) {
                       String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                       if (moleculename != null && !moleculename.equals("")) {
                           if (inhibitorname.equalsIgnoreCase(""))
                               inhibitorname += moleculename;
                           else
                               inhibitorname += ", " + moleculename;
                       }
                   }

                   if (!inhibitorname.equals("")) {
                       Node inhibitornode = graph.createNode();
                       ShapeNodeRealizer nr = getRegulatorShapeNodeRealizer(inhibitorname, TableQueries.MOLECULE_ROLE_STRINGS.INHIBITOR);
                       graph.setRealizer(inhibitornode, nr);
                       nodeToIdsTable.put(inhibitornode, new HashSet<String>(inhibitors));

                       Edge edge = graph.createEdge(inhibitornode, process);
                       EdgeRealizer er = graph.getRealizer(edge);
                       er.setLineType(LineType.LINE_1);
                       makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);

                       int[] xpoints = {0, -2, -2, 0};
                       int[] ypoints = {-5, -5, 5, 5};
                       er.setTargetArrow(Arrow.addCustomArrow("InhibitorArrow", new Polygon(xpoints, ypoints, 4), Color.white));
                   }
               }

               //add edges between cofactors and genericprocesses
               edgeidmarkpairs = TableQueries.getCofactorProcessEdges(repository);
               for (String gpid : edgeidmarkpairs.keySet()) {

                   HashMap<String, ArrayList<String>> gpidRegulatorMap = edgeidmarkpairs.get(gpid);
                   ArrayList<String> genericcofactors = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.COFACTOR);
                   ArrayList<String> cofactorins = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.COFACTORIN);
                   ArrayList<String> cofactorouts = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.COFACTOROUT);

                   Node process = gpidToNodeTable.get(gpid);

                   //create generic cofactor node
                   String cofactorname = "";
                   for (String regid : genericcofactors) {
                       String moleculename = TableQueries.getMoleculeNameById(repository, regid);
                       if (moleculename != null && !moleculename.equals("")) {
                           if (cofactorname.equalsIgnoreCase(""))
                               cofactorname += moleculename;
                           else
                               cofactorname += ", " + moleculename;
                       }
                   }

                   if (!cofactorname.equals("")) {
                       Node cofactornode = graph.createNode();
                       ShapeNodeRealizer nr = getCofactorShapeNodeRealizer(cofactorname, TableQueries.MOLECULE_ROLE_STRINGS.COFACTOR);
                       graph.setRealizer(cofactornode, nr);
                       nodeToIdsTable.put(cofactornode, new HashSet<String>(genericcofactors));

                       Edge edge = graph.createEdge(cofactornode, process);
                       EdgeRealizer er = graph.getRealizer(edge);
                       er.setLineType(LineType.LINE_1);
                       makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                   }

                   //create cofactorin node
                   String cofactorinname = "";
                   for (String actid : cofactorins) {
                       String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                       if (moleculename != null && !moleculename.equals("")) {
                           if (cofactorinname.equalsIgnoreCase(""))
                               cofactorinname += moleculename;
                           else
                               cofactorinname += ", " + moleculename;
                       }
                   }

                   if (!cofactorinname.equals("")) {
                       Node cofactornode = graph.createNode();
                       ShapeNodeRealizer nr = getCofactorShapeNodeRealizer(cofactorinname, TableQueries.MOLECULE_ROLE_STRINGS.COFACTORIN);
                       graph.setRealizer(cofactornode, nr);
                       nodeToIdsTable.put(cofactornode, new HashSet<String>(cofactorins));

                       Edge edge = graph.createEdge(cofactornode, process);
                       ArcEdgeRealizer er = new ArcEdgeRealizer();
                       graph.setRealizer(edge, er);
                       er.setLineType(LineType.LINE_1);
                       er.setRatio(-1);
                       makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                       er.setTargetArrow(Arrow.STANDARD);
                   }

                   //create cofactorout node
                   String cofactoroutname = "";
                   for (String actid : cofactorouts) {
                       String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                       if (moleculename != null && !moleculename.equals("")) {
                           if (cofactoroutname.equalsIgnoreCase(""))
                               cofactoroutname += moleculename;
                           else
                               cofactoroutname += ", " + moleculename;
                       }
                   }

                   if (!cofactoroutname.equals("")) {
                       Node inhibitornode = graph.createNode();
                       ShapeNodeRealizer nr = getCofactorShapeNodeRealizer(cofactoroutname, TableQueries.MOLECULE_ROLE_STRINGS.COFACTOROUT);
                       graph.setRealizer(inhibitornode, nr);
                       nodeToIdsTable.put(inhibitornode, new HashSet<String>(cofactorouts));

                       Edge edge = graph.createEdge(process, inhibitornode);
                       ArcEdgeRealizer er = new ArcEdgeRealizer();
                       graph.setRealizer(edge, er);
                       er.setLineType(LineType.LINE_1);
                       er.setRatio(-1);
                       makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                       er.setTargetArrow(Arrow.STANDARD);
                   }
               }
           }

//q begin  this is for compartment

           String[] comSequence={"Compartment1","Compartment2","Compartment3","blood"};
           HashSet<Node> groupnodeList=new HashSet<Node>();
           //for(:compartmentQListMap.keySet())
           for(int t=0; t<comSequence.length;t++)
           {
               String compartmentname=comSequence[t];
               repository.TissueNames.add(compartmentname);
                if(!compartmentname.equals("blood") )
                {
                   Node groupNode =hm.createGroupNode(graph);
//                    Node groupNode =hm.createFolderNode(graph);
                   ShapeNodeRealizer nrGroup = getTissueShapeNodeRealizerSB(compartmentname);
//                    ShapeNodeRealizer nrGroup = getTissueGroupShapeNodeRealizer(compartmentname);

//                    GroupNodeRealizer nrGroup = getTissueShapeNodeRealizerSBGroup(compartmentname);

//                    Node groupNode =hm.createGroupNode(graph);
//                GroupNodeRealizer nrGroup =getTissueShapeNodeRealizerSBGroup(compartmentname);
                    graph.setRealizer(groupNode, nrGroup);
//                    if(compartmentname.equalsIgnoreCase("Compartment1"))
////                    else
// {
//                    GroupNodeRealizer tGr=new GroupNodeRealizer();
//                        tGr.setGroupClosed(false);
//                       graph.setRealizer(groupNode, tGr);
//                   }

//                 Node nTestNode=graph.createNode();
//                 ShapeNodeRealizer nrT = getRegulatorShapeNodeRealizer(compartmentname, TableQueries.MOLECULE_ROLE_STRINGS.REGULATOR);
//                    graph.setRealizer(nTestNode, nrT);
//                 compartmentQListMap.get(compartmentname).add(nTestNode);


                   hm.groupSubgraph(compartmentQListMap.get(compartmentname),groupNode);      // assigns nodelist to group node
                   //repository.GroupNodeToNodelistMap.put(groupNode,tissueNodeListMap.get(tissuename));
                    if(t<comSequence.length-2)compartmentQListMap.get(comSequence[t+1]).add(groupNode);
                   groupnodeList.add(groupNode);
                }
           }

         NodeList bloodNodeList=new NodeList();
        if(compartmentQListMap.containsKey("blood") )
        {
           bloodNodeList=compartmentQListMap.get("blood");
        }
        repository.TissueNames.add("blood");
        bloodNodeList.addAll(groupnodeList);   //all other groupnodes are subnodes of the blood group node
        Node bloodgroupNode =hm.createGroupNode(graph);
        ShapeNodeRealizer nrGroup = getTissueShapeNodeRealizerSB("blood");
//        ShapeNodeRealizer nrGroup = getTissueGroupShapeNodeRealizer("blood");
//        GroupNodeRealizer nrGroup = getTissueShapeNodeRealizerSBGroup("blood");
        graph.setRealizer(bloodgroupNode, nrGroup);
        hm.groupSubgraph(bloodNodeList,bloodgroupNode);

//q end

           return nodeToIdsTable;

       }




    private static String getCompartmentByProcessName(String pid){
        String strRe="";
        if(pid.contains("AMP")||pid.contains("ATP from")) strRe="Compartment1";
        else if(pid.contains("ATP cons")||pid.contains("Passive"))strRe="Compartment2";
        else if(pid.contains("Adeny"))strRe="Compartment3";
        else strRe="blood";
        return strRe;
    }

       private static String getCompartmentByMoleName(String pid){
        String strRe="";
        if(pid.contains("Energy")) strRe="Compartment1";
        else if(pid.contains("Ions"))strRe="Compartment2";
        else if(pid.contains("Adeny"))strRe="Compartment3";
        else strRe="blood";
        return strRe;
    }

    //Blindly draws repository content, no filtering, no caching mechanism          //q ok
    public static HashMap<Node, HashSet<String>> createGraphFromWholeRepository(PathCaseRepository repository, Graph2D graph, boolean commons, boolean modulators, boolean pathwaylinks) {

        HashMap<String, Node> moleculeidToNodeTable = new HashMap<String, Node>();
        HashMap<String, Node> gpidToNodeTable = new HashMap<String, Node>();
        HashMap<String, Node> pathidToNodeTable = new HashMap<String, Node>();
        HashMap<Node, HashSet<String>> nodeToIdsTable = new HashMap<Node, HashSet<String>>();

        HashSet<String> SubstrateAndProductList = new HashSet<String>();
//q begin
//        HierarchyManager hm = new HierarchyManager(graph);
        HashMap<String, NodeList> compartmentQListMap=new HashMap<String, NodeList>(); //q
//q end
        //load all processes
        ArrayList<String> genericProcessIds = TableQueries.getGenericProcessIDListInRepository(repository);
        for (String gpid : genericProcessIds) {
            //System.out.println(gpid);
            String processlabel = "";

            HashSet<String> ECNumbers = TableQueries.getECNumbersByGenericProcessID(repository, gpid);

            if (ECNumbers != null && ECNumbers.size() > 0 && ECNumbers.size() < 4) {
                String ECString = "";
                ArrayList<String> ECNumberslist = new ArrayList<String>(ECNumbers);
                for (int eci = 0; eci < ECNumberslist.size(); eci++) {
                    String ECNumber = ECNumberslist.get(eci);
                    ECString += ECNumber.trim();
                    if (eci < ECNumberslist.size() - 1)
                        ECString += "\n";
                }
                processlabel = ECString;
            }


            if (processlabel.equals("")) {
                processlabel = TableQueries.getGenericProcessNamebyGenericProcessID(repository, gpid);
            }

            if (processlabel.equals("")) processlabel = "Unknown";

            Node genericprocessnode = graph.createNode();
            ShapeNodeRealizer nr = getGenericProcessShapeNodeRealizer(processlabel);

            graph.setRealizer(genericprocessnode, nr);

            gpidToNodeTable.put(gpid, genericprocessnode);
            HashSet<String> processlist = new HashSet<String>();
            processlist.add(gpid);
            nodeToIdsTable.put(genericprocessnode, processlist);
            SubstrateAndProductList.addAll(TableQueries.getSubstrateAndProductsByGenericProcessId(repository, gpid));

            //q begin
            String strcompartmentName=getCompartmentByProcessName(processlabel);

            if(!compartmentQListMap.containsKey(strcompartmentName))
            {
                compartmentQListMap.put(strcompartmentName,new NodeList());
            }

            compartmentQListMap.get(strcompartmentName).add(genericprocessnode);
        }
           //q end

        HashSet<String> CollapsedSubstrateAndProductList = new HashSet<String>();

        //load all collapsedPathways
        HashSet<String> pathwayIds = TableQueries.getCollapsedPathwaysIdListInRepository(repository, pathwaylinks);

        for (String pathwayid : pathwayIds) {
            String pathwayname = TableQueries.getPathwayNameById(repository, pathwayid);
            Node pathwaynode = graph.createNode();
            ShapeNodeRealizer nr = getCollapsedPathwayShapeNodeRealizer(pathwayname);
            graph.setRealizer(pathwaynode, nr);

            pathidToNodeTable.put(pathwayid, pathwaynode);
            HashSet<String> pathwaylist = new HashSet<String>();
            pathwaylist.add(pathwayid);
            nodeToIdsTable.put(pathwaynode, pathwaylist);
            //view.getGraph2D().hide(substratenode);
            HashSet<String> metabolitesOfPathway = TableQueries.getSubstrateAndProductsByCollapsedPathwayId(repository, pathwayid);

            //old version
            //SubstrateAndProductList.addAll(metabolitesOfPathway);
            //Note, will not work if a pathway has multiple versions of the same thing
            if (metabolitesOfPathway != null)
                for (String metabolite : metabolitesOfPathway) {
                    //add to substrate list if only it is being added for the second time
                    if (!CollapsedSubstrateAndProductList.contains(metabolite))
                        CollapsedSubstrateAndProductList.add(metabolite);
                    else
                        SubstrateAndProductList.add(metabolite);
                }
        }

        HashMap<String, String> commonId2NameTable = new HashMap<String, String>();

        //load all substrate and products
        for (String subsorprod : SubstrateAndProductList) {
            //System.out.println(subsorprod);
            String moleculename = TableQueries.getMoleculeNameById(repository, subsorprod);
            //System.out.println(moleculename);
            boolean iscommon = TableQueries.getMoleculeCommonById(repository, subsorprod);
            //System.out.println(iscommon);
            if (!iscommon) {
                Node substratenode = graph.createNode();
                ShapeNodeRealizer nr = getSubstrateProductShapeNodeRealizer(moleculename, iscommon);
                graph.setRealizer(substratenode, nr);
                moleculeidToNodeTable.put(subsorprod, substratenode);
                HashSet<String> metabolitelist = new HashSet<String>();
                metabolitelist.add(subsorprod);
                nodeToIdsTable.put(substratenode, metabolitelist);

                //q begin
                String strcompartmentName =  getCompartmentByMoleName(moleculename);
                 if(!compartmentQListMap.containsKey(strcompartmentName) )
                    {
                         compartmentQListMap.put(strcompartmentName,new NodeList());
                    }

                  compartmentQListMap.get(strcompartmentName).add(substratenode);
                //q end

            } else if (commons) {
                commonId2NameTable.put(subsorprod, moleculename);
            }
            //view.getGraph2D().hide(substratenode);
        }

        //add edges between substrates/products and processes
        ArrayList<MoleculeProcessPair> edgeidpairs = TableQueries.getSubstrateProductProcessEdges(repository);
        for (MoleculeProcessPair sp : edgeidpairs) {
            Node molecule = moleculeidToNodeTable.get(sp.molecule);
            Node process = gpidToNodeTable.get(sp.process);

            if (molecule == null) {
                String commonname = commonId2NameTable.get(sp.molecule);
                if (commonname != null) {
                    molecule = graph.createNode();
                    ShapeNodeRealizer nr = getSubstrateProductShapeNodeRealizer(commonname, true);
                    graph.setRealizer(molecule, nr);
                    HashSet<String> metabolitelist = new HashSet<String>();
                    metabolitelist.add(sp.molecule);
                    nodeToIdsTable.put(molecule, metabolitelist);
                } else {
                    continue;
                }
            }

            if (sp.input) {
                Edge edge = graph.createEdge(molecule, process);
                EdgeRealizer er = graph.getRealizer(edge);
                er.setTargetArrow(Arrow.STANDARD);
                if (sp.reversible)
                    er.setSourceArrow(Arrow.STANDARD);
                makeMetaboliteEdgeNormal(er);
            } else {
                Edge edge = graph.createEdge(process, molecule);
                EdgeRealizer er = graph.getRealizer(edge);
                er.setTargetArrow(Arrow.STANDARD);
                if (sp.reversible)
                    er.setSourceArrow(Arrow.STANDARD);
                makeMetaboliteEdgeNormal(er);
            }
        }

        //add edges between metabolites and pathways
        //always from pathways to metabolites
        edgeidpairs = TableQueries.getLinkingMoleculePathwayEdges(repository, pathwaylinks);
        if(edgeidpairs.size()>0){
            for (MoleculeProcessPair sp : edgeidpairs) {
                Node item1 = moleculeidToNodeTable.get(sp.molecule);
                Node item2 = pathidToNodeTable.get(sp.process);

                if (item1 == null || item2 == null) continue;

                Edge edge = graph.createEdge(item1, item2);
                EdgeRealizer er = graph.getRealizer(edge);
                er.setTargetArrow(Arrow.STANDARD);
                makeMetaboliteEdgeNormal(er);
            }
        }


        if (modulators) {
            //add edges between regulators and genericprocesses
            HashMap<String, HashMap<String, ArrayList<String>>> edgeidmarkpairs = TableQueries.getRegulatorProcessEdges(repository);
            for (String gpid : edgeidmarkpairs.keySet()) {

                HashMap<String, ArrayList<String>> gpidRegulatorMap = edgeidmarkpairs.get(gpid);
                ArrayList<String> genericregulators = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.REGULATOR);
                ArrayList<String> inhibitors = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.INHIBITOR);
                ArrayList<String> activators = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.ACTIVATOR);

                Node process = gpidToNodeTable.get(gpid);

                //create generic regulator node
                String regulatorname = "";
                for (String regid : genericregulators) {
                    String moleculename = TableQueries.getMoleculeNameById(repository, regid);
                    if (moleculename != null && !moleculename.equals("")) {
                        if (regulatorname.equalsIgnoreCase(""))
                            regulatorname += moleculename;
                        else
                            regulatorname += ", " + moleculename;
                    }
                }

                if (!regulatorname.equals("")) {
                    Node regulatornode = graph.createNode();
                    ShapeNodeRealizer nr = getRegulatorShapeNodeRealizer(regulatorname, TableQueries.MOLECULE_ROLE_STRINGS.REGULATOR);
                    graph.setRealizer(regulatornode, nr);
                    nodeToIdsTable.put(regulatornode, new HashSet<String>(genericregulators));

                    Edge edge = graph.createEdge(regulatornode, process);
                    EdgeRealizer er = graph.getRealizer(edge);
                    er.setLineType(LineType.LINE_1);
                    makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                    //er.setTargetArrow(Arrow.STANDARD);
                }

                //create activator node
                String activatorname = "";
                for (String actid : activators) {
                    String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                    if (moleculename != null && !moleculename.equals("")) {
                        if (activatorname.equalsIgnoreCase(""))
                            activatorname += moleculename;
                        else
                            activatorname += ", " + moleculename;
                    }
                }

                if (!activatorname.equals("")) {
                    Node activatornode = graph.createNode();
                    ShapeNodeRealizer nr = getRegulatorShapeNodeRealizer(activatorname, TableQueries.MOLECULE_ROLE_STRINGS.ACTIVATOR);
                    graph.setRealizer(activatornode, nr);
                    nodeToIdsTable.put(activatornode, new HashSet<String>(activators));

                    Edge edge = graph.createEdge(activatornode, process);
                    EdgeRealizer er = graph.getRealizer(edge);
                    er.setLineType(LineType.LINE_1);
                    makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);

                    int[] xpoints = {0, -4, -4};
                    int[] ypoints = {0, -4, 4};
                    er.setTargetArrow(Arrow.addCustomArrow("ActivatorArrow", new Polygon(xpoints, ypoints, 3), Color.white));
                }

                //create inhibitor node
                String inhibitorname = "";
                for (String actid : inhibitors) {
                    String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                    if (moleculename != null && !moleculename.equals("")) {
                        if (inhibitorname.equalsIgnoreCase(""))
                            inhibitorname += moleculename;
                        else
                            inhibitorname += ", " + moleculename;
                    }
                }

                if (!inhibitorname.equals("")) {
                    Node inhibitornode = graph.createNode();
                    ShapeNodeRealizer nr = getRegulatorShapeNodeRealizer(inhibitorname, TableQueries.MOLECULE_ROLE_STRINGS.INHIBITOR);
                    graph.setRealizer(inhibitornode, nr);
                    nodeToIdsTable.put(inhibitornode, new HashSet<String>(inhibitors));

                    Edge edge = graph.createEdge(inhibitornode, process);
                    EdgeRealizer er = graph.getRealizer(edge);
                    er.setLineType(LineType.LINE_1);
                    makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);

                    int[] xpoints = {0, -2, -2, 0};
                    int[] ypoints = {-5, -5, 5, 5};
                    er.setTargetArrow(Arrow.addCustomArrow("InhibitorArrow", new Polygon(xpoints, ypoints, 4), Color.white));
                }
            }

            //add edges between cofactors and genericprocesses
            edgeidmarkpairs = TableQueries.getCofactorProcessEdges(repository);
            for (String gpid : edgeidmarkpairs.keySet()) {

                HashMap<String, ArrayList<String>> gpidRegulatorMap = edgeidmarkpairs.get(gpid);
                ArrayList<String> genericcofactors = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.COFACTOR);
                ArrayList<String> cofactorins = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.COFACTORIN);
                ArrayList<String> cofactorouts = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.COFACTOROUT);

                Node process = gpidToNodeTable.get(gpid);

                //create generic cofactor node
                String cofactorname = "";
                for (String regid : genericcofactors) {
                    String moleculename = TableQueries.getMoleculeNameById(repository, regid);
                    if (moleculename != null && !moleculename.equals("")) {
                        if (cofactorname.equalsIgnoreCase(""))
                            cofactorname += moleculename;
                        else
                            cofactorname += ", " + moleculename;
                    }
                }

                if (!cofactorname.equals("")) {
                    Node cofactornode = graph.createNode();
                    ShapeNodeRealizer nr = getCofactorShapeNodeRealizer(cofactorname, TableQueries.MOLECULE_ROLE_STRINGS.COFACTOR);
                    graph.setRealizer(cofactornode, nr);
                    nodeToIdsTable.put(cofactornode, new HashSet<String>(genericcofactors));

                    Edge edge = graph.createEdge(cofactornode, process);
                    EdgeRealizer er = graph.getRealizer(edge);
                    er.setLineType(LineType.LINE_1);
                    makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                }

                //create cofactorin node
                String cofactorinname = "";
                for (String actid : cofactorins) {
                    String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                    if (moleculename != null && !moleculename.equals("")) {
                        if (cofactorinname.equalsIgnoreCase(""))
                            cofactorinname += moleculename;
                        else
                            cofactorinname += ", " + moleculename;
                    }
                }

                if (!cofactorinname.equals("")) {
                    Node cofactornode = graph.createNode();
                    ShapeNodeRealizer nr = getCofactorShapeNodeRealizer(cofactorinname, TableQueries.MOLECULE_ROLE_STRINGS.COFACTORIN);
                    graph.setRealizer(cofactornode, nr);
                    nodeToIdsTable.put(cofactornode, new HashSet<String>(cofactorins));

                    Edge edge = graph.createEdge(cofactornode, process);
                    ArcEdgeRealizer er = new ArcEdgeRealizer();
                    graph.setRealizer(edge, er);
                    er.setLineType(LineType.LINE_1);
                    er.setRatio(-1);
                    makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                    er.setTargetArrow(Arrow.STANDARD);
                }

                //create cofactorout node
                String cofactoroutname = "";
                for (String actid : cofactorouts) {
                    String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                    if (moleculename != null && !moleculename.equals("")) {
                        if (cofactoroutname.equalsIgnoreCase(""))
                            cofactoroutname += moleculename;
                        else
                            cofactoroutname += ", " + moleculename;
                    }
                }

                if (!cofactoroutname.equals("")) {
                    Node inhibitornode = graph.createNode();
                    ShapeNodeRealizer nr = getCofactorShapeNodeRealizer(cofactoroutname, TableQueries.MOLECULE_ROLE_STRINGS.COFACTOROUT);
                    graph.setRealizer(inhibitornode, nr);
                    nodeToIdsTable.put(inhibitornode, new HashSet<String>(cofactorouts));

                    Edge edge = graph.createEdge(process, inhibitornode);
                    ArcEdgeRealizer er = new ArcEdgeRealizer();
                    graph.setRealizer(edge, er);
                    er.setLineType(LineType.LINE_1);
                    er.setRatio(-1);
                    makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                    er.setTargetArrow(Arrow.STANDARD);
                }
            }
        }

//q begin
//        String[] comSequence={"Compartment1","Compartment2","Compartment3","blood"};
//        HashSet<Node> groupnodeList=new HashSet<Node>();
//        //for(:compartmentQListMap.keySet())
//        for(int t=0; t<comSequence.length;t++)
//        {
//            String compartmentname=comSequence[t];
//            repository.TissueNames.add(compartmentname);
//             if(!compartmentname.equals("blood") )
//             {
//                Node groupNode =hm.createGroupNode(graph);
//                GroupNodeRealizer nrGroup =getTissueShapeNodeRealizerSBGroup(compartmentname); //getTissueShapeNodeRealizerSB(compartmentname);
//                graph.setRealizer(groupNode, nrGroup);
////                 System.out.println("Is groupnoderealizer:"+(nrGroup instanceof GroupNodeRealizer));
//
////                 Node nTestNode=graph.createNode();
////                 ShapeNodeRealizer nrT = getRegulatorShapeNodeRealizer(compartmentname, TableQueries.MOLECULE_ROLE_STRINGS.REGULATOR);
////                    graph.setRealizer(nTestNode, nrT);
////                 compartmentQListMap.get(compartmentname).add(nTestNode);
//                hm.groupSubgraph(compartmentQListMap.get(compartmentname),groupNode);      // assigns nodelist to group node
//                //repository.GroupNodeToNodelistMap.put(groupNode,tissueNodeListMap.get(tissuename));
//                groupnodeList.add(groupNode);
//             }
//        }
//
//         NodeList bloodNodeList=new NodeList();
//        if(compartmentQListMap.containsKey("blood") )
//        {
//           bloodNodeList=compartmentQListMap.get("blood");
//        }
//        repository.TissueNames.add("blood");
//        bloodNodeList.addAll(groupnodeList);   //all other groupnodes are subnodes of the blood group node
//        Node bloodgroupNode =hm.createGroupNode(graph);
//        ShapeNodeRealizer nrGroup = getTissueShapeNodeRealizerSBGroup("blood");
//        graph.setRealizer(bloodgroupNode, nrGroup);
//        hm.groupSubgraph(bloodNodeList,bloodgroupNode);

//q end

        return nodeToIdsTable;

    }

    public static HashMap<Node, HashSet<String>> createGraphFromWholeRepositoryTissue(PathCaseRepository repository, Graph2D graph, boolean commons, boolean modulators, boolean pathwaylinks) {

        HierarchyManager hm = new HierarchyManager(graph);

        HashMap<String, Node> moleculeidToNodeTable = new HashMap<String, Node>();
        HashMap<String, Node> gpidToNodeTable = new HashMap<String, Node>();
        HashMap<String, Node> pathidToNodeTable = new HashMap<String, Node>();
        HashMap<Node, HashSet<String>> nodeToIdsTable = new HashMap<Node, HashSet<String>>();

        HashSet<String> SubstrateAndProductList = new HashSet<String>();

        //load all processes
        ArrayList<String> genericProcessIds = TableQueries.getGenericProcessIDListInRepository(repository);
        for (String gpid : genericProcessIds) {
            //System.out.println(gpid);
            String processlabel = "";

            HashSet<String> ECNumbers = TableQueries.getECNumbersByGenericProcessID(repository, gpid);

            if (ECNumbers != null && ECNumbers.size() > 0 && ECNumbers.size() < 4) {
                String ECString = "";
                ArrayList<String> ECNumberslist = new ArrayList<String>(ECNumbers);
                for (int eci = 0; eci < ECNumberslist.size(); eci++) {
                    String ECNumber = ECNumberslist.get(eci);
                    ECString += ECNumber.trim();
                    if (eci < ECNumberslist.size() - 1)
                        ECString += "\n";
                }
                processlabel = ECString;
            }


            if (processlabel.equals("")) {
                processlabel = TableQueries.getGenericProcessNamebyGenericProcessID(repository, gpid);
            }

            if (processlabel.equals("")) processlabel = "Unknown";

            Node genericprocessnode = graph.createNode();
            ShapeNodeRealizer nr = getGenericProcessShapeNodeRealizer(processlabel);
            graph.setRealizer(genericprocessnode, nr);
            gpidToNodeTable.put(gpid, genericprocessnode);

            NodeList nl = new NodeList();
            nl.add(genericprocessnode);
            Node groupNode = hm.createGroupNode(graph);
            ShapeNodeRealizer nrGroup = getTissueShapeNodeRealizer("TEST");
            graph.setRealizer(groupNode, nrGroup);
            hm.groupSubgraph(nl, groupNode);

            HashSet<String> processlist = new HashSet<String>();
            processlist.add(gpid);
            nodeToIdsTable.put(genericprocessnode, processlist);
            SubstrateAndProductList.addAll(TableQueries.getSubstrateAndProductsByGenericProcessId(repository, gpid));
        }

        HashSet<String> CollapsedSubstrateAndProductList = new HashSet<String>();

        //load all collapsedPathways
        HashSet<String> pathwayIds = TableQueries.getCollapsedPathwaysIdListInRepository(repository, pathwaylinks);

        for (String pathwayid : pathwayIds) {
            String pathwayname = TableQueries.getPathwayNameById(repository, pathwayid);
            Node pathwaynode = graph.createNode();
            ShapeNodeRealizer nr = getCollapsedPathwayShapeNodeRealizer(pathwayname);
            graph.setRealizer(pathwaynode, nr);

            pathidToNodeTable.put(pathwayid, pathwaynode);
            HashSet<String> pathwaylist = new HashSet<String>();
            pathwaylist.add(pathwayid);
            nodeToIdsTable.put(pathwaynode, pathwaylist);
            //view.getGraph2D().hide(substratenode);
            HashSet<String> metabolitesOfPathway = TableQueries.getSubstrateAndProductsByCollapsedPathwayId(repository, pathwayid);

            //old version
            //SubstrateAndProductList.addAll(metabolitesOfPathway);
            //Note, will not work if a pathway has multiple versions of the same thing
            if (metabolitesOfPathway != null)
                for (String metabolite : metabolitesOfPathway) {
                    //add to substrate list if only it is being added for the second time
                    if (!CollapsedSubstrateAndProductList.contains(metabolite))
                        CollapsedSubstrateAndProductList.add(metabolite);
                    else
                        SubstrateAndProductList.add(metabolite);
                }
        }

        HashMap<String, String> commonId2NameTable = new HashMap<String, String>();

        //load all substrate and products
        for (String subsorprod : SubstrateAndProductList) {
            //System.out.println(subsorprod);
            String moleculename = TableQueries.getMoleculeNameById(repository, subsorprod);
            //System.out.println(moleculename);
            boolean iscommon = TableQueries.getMoleculeCommonById(repository, subsorprod);
            //System.out.println(iscommon);
            if (!iscommon) {
                Node substratenode = graph.createNode();
                ShapeNodeRealizer nr = getSubstrateProductShapeNodeRealizer(moleculename, iscommon);
                graph.setRealizer(substratenode, nr);
                moleculeidToNodeTable.put(subsorprod, substratenode);
                HashSet<String> metabolitelist = new HashSet<String>();
                metabolitelist.add(subsorprod);
                nodeToIdsTable.put(substratenode, metabolitelist);
            } else if (commons) {
                commonId2NameTable.put(subsorprod, moleculename);
            }
            //view.getGraph2D().hide(substratenode);
        }

        //add edges between substrates/products and processes
        ArrayList<MoleculeProcessPair> edgeidpairs = TableQueries.getSubstrateProductProcessEdges(repository);
        for (MoleculeProcessPair sp : edgeidpairs) {
            Node molecule = moleculeidToNodeTable.get(sp.molecule);
            Node process = gpidToNodeTable.get(sp.process);

            if (molecule == null) {
                String commonname = commonId2NameTable.get(sp.molecule);
                if (commonname != null) {
                    molecule = graph.createNode();
                    ShapeNodeRealizer nr = getSubstrateProductShapeNodeRealizer(commonname, true);
                    graph.setRealizer(molecule, nr);
                    HashSet<String> metabolitelist = new HashSet<String>();
                    metabolitelist.add(sp.molecule);
                    nodeToIdsTable.put(molecule, metabolitelist);
                } else {
                    continue;
                }
            }

            if (sp.input) {
                Edge edge = graph.createEdge(molecule, process);
                EdgeRealizer er = graph.getRealizer(edge);
                er.setTargetArrow(Arrow.STANDARD);
                if (sp.reversible)
                    er.setSourceArrow(Arrow.STANDARD);
                makeMetaboliteEdgeNormal(er);
            } else {
                Edge edge = graph.createEdge(process, molecule);
                EdgeRealizer er = graph.getRealizer(edge);
                er.setTargetArrow(Arrow.STANDARD);
                if (sp.reversible)
                    er.setSourceArrow(Arrow.STANDARD);
                makeMetaboliteEdgeNormal(er);
            }
        }

        //add edges between metabolites and pathways
        //always from pathways to metabolites
        edgeidpairs = TableQueries.getLinkingMoleculePathwayEdges(repository, pathwaylinks);
        for (MoleculeProcessPair sp : edgeidpairs) {
            Node item1 = moleculeidToNodeTable.get(sp.molecule);
            Node item2 = pathidToNodeTable.get(sp.process);

            if (item1 == null || item2 == null) continue;

            Edge edge = graph.createEdge(item1, item2);
            EdgeRealizer er = graph.getRealizer(edge);
            er.setTargetArrow(Arrow.STANDARD);
            makeMetaboliteEdgeNormal(er);
        }


        if (modulators) {
            //add edges between regulators and genericprocesses
            HashMap<String, HashMap<String, ArrayList<String>>> edgeidmarkpairs = TableQueries.getRegulatorProcessEdges(repository);
            for (String gpid : edgeidmarkpairs.keySet()) {

                HashMap<String, ArrayList<String>> gpidRegulatorMap = edgeidmarkpairs.get(gpid);
                ArrayList<String> genericregulators = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.REGULATOR);
                ArrayList<String> inhibitors = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.INHIBITOR);
                ArrayList<String> activators = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.ACTIVATOR);

                Node process = gpidToNodeTable.get(gpid);

                //create generic regulator node
                String regulatorname = "";
                for (String regid : genericregulators) {
                    String moleculename = TableQueries.getMoleculeNameById(repository, regid);
                    if (moleculename != null && !moleculename.equals("")) {
                        if (regulatorname.equalsIgnoreCase(""))
                            regulatorname += moleculename;
                        else
                            regulatorname += ", " + moleculename;
                    }
                }

                if (!regulatorname.equals("")) {
                    Node regulatornode = graph.createNode();
                    ShapeNodeRealizer nr = getRegulatorShapeNodeRealizer(regulatorname, TableQueries.MOLECULE_ROLE_STRINGS.REGULATOR);
                    graph.setRealizer(regulatornode, nr);
                    nodeToIdsTable.put(regulatornode, new HashSet<String>(genericregulators));

                    Edge edge = graph.createEdge(regulatornode, process);
                    EdgeRealizer er = graph.getRealizer(edge);
                    er.setLineType(LineType.LINE_1);
                    makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                    //er.setTargetArrow(Arrow.STANDARD);
                }

                //create activator node
                String activatorname = "";
                for (String actid : activators) {
                    String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                    if (moleculename != null && !moleculename.equals("")) {
                        if (activatorname.equalsIgnoreCase(""))
                            activatorname += moleculename;
                        else
                            activatorname += ", " + moleculename;
                    }
                }

                if (!activatorname.equals("")) {
                    Node activatornode = graph.createNode();
                    ShapeNodeRealizer nr = getRegulatorShapeNodeRealizer(activatorname, TableQueries.MOLECULE_ROLE_STRINGS.ACTIVATOR);
                    graph.setRealizer(activatornode, nr);
                    nodeToIdsTable.put(activatornode, new HashSet<String>(activators));

                    Edge edge = graph.createEdge(activatornode, process);
                    EdgeRealizer er = graph.getRealizer(edge);
                    er.setLineType(LineType.LINE_1);
                    makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);

                    int[] xpoints = {0, -4, -4};
                    int[] ypoints = {0, -4, 4};
                    er.setTargetArrow(Arrow.addCustomArrow("ActivatorArrow", new Polygon(xpoints, ypoints, 3), Color.white));
                }

                //create inhibitor node
                String inhibitorname = "";
                for (String actid : inhibitors) {
                    String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                    if (moleculename != null && !moleculename.equals("")) {
                        if (inhibitorname.equalsIgnoreCase(""))
                            inhibitorname += moleculename;
                        else
                            inhibitorname += ", " + moleculename;
                    }
                }

                if (!inhibitorname.equals("")) {
                    Node inhibitornode = graph.createNode();
                    ShapeNodeRealizer nr = getRegulatorShapeNodeRealizer(inhibitorname, TableQueries.MOLECULE_ROLE_STRINGS.INHIBITOR);
                    graph.setRealizer(inhibitornode, nr);
                    nodeToIdsTable.put(inhibitornode, new HashSet<String>(inhibitors));

                    Edge edge = graph.createEdge(inhibitornode, process);
                    EdgeRealizer er = graph.getRealizer(edge);
                    er.setLineType(LineType.LINE_1);
                    makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);

                    int[] xpoints = {0, -2, -2, 0};
                    int[] ypoints = {-5, -5, 5, 5};
                    er.setTargetArrow(Arrow.addCustomArrow("InhibitorArrow", new Polygon(xpoints, ypoints, 4), Color.white));
                }
            }

            //add edges between cofactors and genericprocesses
            edgeidmarkpairs = TableQueries.getCofactorProcessEdges(repository);
            for (String gpid : edgeidmarkpairs.keySet()) {

                HashMap<String, ArrayList<String>> gpidRegulatorMap = edgeidmarkpairs.get(gpid);
                ArrayList<String> genericcofactors = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.COFACTOR);
                ArrayList<String> cofactorins = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.COFACTORIN);
                ArrayList<String> cofactorouts = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.COFACTOROUT);

                Node process = gpidToNodeTable.get(gpid);

                //create generic cofactor node
                String cofactorname = "";
                for (String regid : genericcofactors) {
                    String moleculename = TableQueries.getMoleculeNameById(repository, regid);
                    if (moleculename != null && !moleculename.equals("")) {
                        if (cofactorname.equalsIgnoreCase(""))
                            cofactorname += moleculename;
                        else
                            cofactorname += ", " + moleculename;
                    }
                }

                if (!cofactorname.equals("")) {
                    Node cofactornode = graph.createNode();
                    ShapeNodeRealizer nr = getCofactorShapeNodeRealizer(cofactorname, TableQueries.MOLECULE_ROLE_STRINGS.COFACTOR);
                    graph.setRealizer(cofactornode, nr);
                    nodeToIdsTable.put(cofactornode, new HashSet<String>(genericcofactors));

                    Edge edge = graph.createEdge(cofactornode, process);
                    EdgeRealizer er = graph.getRealizer(edge);
                    er.setLineType(LineType.LINE_1);
                    makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                }

                //create cofactorin node
                String cofactorinname = "";
                for (String actid : cofactorins) {
                    String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                    if (moleculename != null && !moleculename.equals("")) {
                        if (cofactorinname.equalsIgnoreCase(""))
                            cofactorinname += moleculename;
                        else
                            cofactorinname += ", " + moleculename;
                    }
                }

                if (!cofactorinname.equals("")) {
                    Node cofactornode = graph.createNode();
                    ShapeNodeRealizer nr = getCofactorShapeNodeRealizer(cofactorinname, TableQueries.MOLECULE_ROLE_STRINGS.COFACTORIN);
                    graph.setRealizer(cofactornode, nr);
                    nodeToIdsTable.put(cofactornode, new HashSet<String>(cofactorins));

                    Edge edge = graph.createEdge(cofactornode, process);
                    ArcEdgeRealizer er = new ArcEdgeRealizer();
                    graph.setRealizer(edge, er);
                    er.setLineType(LineType.LINE_1);
                    er.setRatio(-1);
                    makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                    er.setTargetArrow(Arrow.STANDARD);
                }

                //create cofactorout node
                String cofactoroutname = "";
                for (String actid : cofactorouts) {
                    String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                    if (moleculename != null && !moleculename.equals("")) {
                        if (cofactoroutname.equalsIgnoreCase(""))
                            cofactoroutname += moleculename;
                        else
                            cofactoroutname += ", " + moleculename;
                    }
                }

                if (!cofactoroutname.equals("")) {
                    Node inhibitornode = graph.createNode();
                    ShapeNodeRealizer nr = getCofactorShapeNodeRealizer(cofactoroutname, TableQueries.MOLECULE_ROLE_STRINGS.COFACTOROUT);
                    graph.setRealizer(inhibitornode, nr);
                    nodeToIdsTable.put(inhibitornode, new HashSet<String>(cofactorouts));

                    Edge edge = graph.createEdge(process, inhibitornode);
                    ArcEdgeRealizer er = new ArcEdgeRealizer();
                    graph.setRealizer(edge, er);
                    er.setLineType(LineType.LINE_1);
                    er.setRatio(-1);
                    makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                    er.setTargetArrow(Arrow.STANDARD);
                }
            }
        }

        return nodeToIdsTable;

    }

    public static HashMap<Node, HashSet<String>> createGraphForASinglePathway(PathCaseRepository repository, Graph2D graph, String pathwayId, boolean commons, boolean modulators, boolean pathwaylinks) {

            HashMap<String, Node> moleculeidToNodeTable = new HashMap<String, Node>();
            HashMap<String, Node> gpidToNodeTable = new HashMap<String, Node>();
            //HashMap<String, Node> pathidToNodeTable = new HashMap<String, Node>();
            HashMap<Node, HashSet<String>> nodeToIdsTable = new HashMap<Node, HashSet<String>>();

            HashSet<String> SubstrateAndProductList = new HashSet<String>();

            HashMap<String, NodeList> compartmentQListMap=new HashMap<String, NodeList>(); //q
            compartmentQListMap.put("pathway",new NodeList());

            HierarchyManager hm;

            if(graph.getHierarchyManager()==null)
                    hm = new HierarchyManager(graph);
            else
                    hm = graph.getHierarchyManager();
            double startX=graph.getBoundingBox().getMaxX()+ 50; //space to the split line
            double startY=graph.getBoundingBox().getMinY();

            //load all processes
            HashSet<String> genericProcessIds = TableQueries.getGenericProcessIDListInRepositoryByPathwayId(repository, pathwayId);
            for (String gpid : genericProcessIds) {
                //System.out.println(gpid);
                String processlabel = "";

                HashSet<String> ECNumbers = TableQueries.getECNumbersByGenericProcessID(repository, gpid);

                if (ECNumbers != null && ECNumbers.size() > 0 && ECNumbers.size() < 4) {
                    String ECString = "";
                    ArrayList<String> ECNumberslist = new ArrayList<String>(ECNumbers);
                    for (int eci = 0; eci < ECNumberslist.size(); eci++) {
                        String ECNumber = ECNumberslist.get(eci);
                        ECString += ECNumber.trim();
                        if (eci < ECNumberslist.size() - 1)
                            ECString += "\n";
                    }
                    processlabel = ECString;
                }


                if (processlabel.equals("")) {
                    processlabel = TableQueries.getGenericProcessNamebyGenericProcessID(repository, gpid);
                }

                if (processlabel.equals("")) processlabel = "Unknown";

                Node genericprocessnode = graph.createNode();
                ShapeNodeRealizer nr = getGenericProcessShapeNodeRealizer(processlabel);

                graph.setRealizer(genericprocessnode, nr);

                gpidToNodeTable.put(gpid, genericprocessnode);
                HashSet<String> processlist = new HashSet<String>();
                processlist.add(gpid);
                nodeToIdsTable.put(genericprocessnode, processlist);
                compartmentQListMap.get("pathway").add(genericprocessnode);
                SubstrateAndProductList.addAll(TableQueries.getSubstrateAndProductsByGenericProcessId(repository, gpid));
            }

            //HashSet<String> CollapsedSubstrateAndProductList = new HashSet<String>();

            /*
            //load all collapsedPathways by pathwayId
            HashSet<String> pathwayIds = TableQueries.getCollapsedPathwaysIdListInRepository(repository, pathwaylinks);

            for (String pathwayid : pathwayIds) {
                String pathwayname = TableQueries.getPathwayNameById(repository, pathwayid);
                Node pathwaynode = graph.createNode();
                ShapeNodeRealizer nr = getCollapsedPathwayShapeNodeRealizer(pathwayname);
                graph.setRealizer(pathwaynode, nr);

                pathidToNodeTable.put(pathwayid, pathwaynode);
                HashSet<String> pathwaylist = new HashSet<String>();
                pathwaylist.add(pathwayid);
                nodeToIdsTable.put(pathwaynode, pathwaylist);
                //view.getGraph2D().hide(substratenode);
                HashSet<String> metabolitesOfPathway = TableQueries.getSubstrateAndProductsByCollapsedPathwayId(repository, pathwayid);

                //old version
                //SubstrateAndProductList.addAll(metabolitesOfPathway);
                //Note, will not work if a pathway has multiple versions of the same thing
                if (metabolitesOfPathway != null)
                    for (String metabolite : metabolitesOfPathway) {
                        //add to substrate list if only it is being added for the second time
                        if (!CollapsedSubstrateAndProductList.contains(metabolite))
                            CollapsedSubstrateAndProductList.add(metabolite);
                        else
                            SubstrateAndProductList.add(metabolite);
                    }
            }
            */

            HashMap<String, String> commonId2NameTable = new HashMap<String, String>();

            //load all substrate and products
            for (String subsorprod : SubstrateAndProductList) {
                //System.out.println(subsorprod);
                String moleculename = TableQueries.getMoleculeNameById(repository, subsorprod);
                //System.out.println(moleculename);
                boolean iscommon = TableQueries.getMoleculeCommonById(repository, subsorprod);
                //System.out.println(iscommon);
                if (!iscommon) {
                    Node substratenode = graph.createNode();
                    ShapeNodeRealizer nr = getSubstrateProductShapeNodeRealizer(moleculename, iscommon);
                    graph.setRealizer(substratenode, nr);
                    moleculeidToNodeTable.put(subsorprod, substratenode);
                    HashSet<String> metabolitelist = new HashSet<String>();
                    metabolitelist.add(subsorprod);
                    nodeToIdsTable.put(substratenode, metabolitelist);
                    compartmentQListMap.get("pathway").add(substratenode);
                } else if (commons) {
                    commonId2NameTable.put(subsorprod, moleculename);
                }
                //view.getGraph2D().hide(substratenode);
            }

            //add edges between substrates/products and processes
            ArrayList<MoleculeProcessPair> edgeidpairs = TableQueries.getSubstrateProductProcessEdges(repository);
            for (MoleculeProcessPair sp : edgeidpairs) {
                Node molecule = moleculeidToNodeTable.get(sp.molecule);
                Node process = gpidToNodeTable.get(sp.process);

                if (process == null) continue;

                if (molecule == null) {
                    String commonname = commonId2NameTable.get(sp.molecule);
                    if (commonname != null) {
                        molecule = graph.createNode();
                        ShapeNodeRealizer nr = getSubstrateProductShapeNodeRealizer(commonname, true);
                        graph.setRealizer(molecule, nr);
                        HashSet<String> metabolitelist = new HashSet<String>();
                        metabolitelist.add(sp.molecule);
                        nodeToIdsTable.put(molecule, metabolitelist);
                        compartmentQListMap.get("pathway").add(molecule);
                    } else {
                        continue;
                    }
                }

                if (sp.input) {
                    Edge edge = graph.createEdge(molecule, process);
                    EdgeRealizer er = graph.getRealizer(edge);
                    er.setTargetArrow(Arrow.STANDARD);
                    if (sp.reversible)
                        er.setSourceArrow(Arrow.STANDARD);
                    makeMetaboliteEdgeNormal(er);
                } else {
                    Edge edge = graph.createEdge(process, molecule);
                    EdgeRealizer er = graph.getRealizer(edge);
                    er.setTargetArrow(Arrow.STANDARD);
                    if (sp.reversible)
                        er.setSourceArrow(Arrow.STANDARD);
                    makeMetaboliteEdgeNormal(er);
                }
            }

            /*
            //add edges between metabolites and pathways
            //always from pathways to metabolites
            edgeidpairs = TableQueries.getLinkingMoleculePathwayEdges(repository, pathwaylinks);
            for (MoleculeProcessPair sp : edgeidpairs) {
                Node item1 = moleculeidToNodeTable.get(sp.molecule);
                Node item2 = pathidToNodeTable.get(sp.process);

                if (item1 == null || item2 == null) continue;

                Edge edge = graph.createEdge(item1, item2);
                EdgeRealizer er = graph.getRealizer(edge);
                er.setTargetArrow(Arrow.STANDARD);
                makeMetaboliteEdgeNormal(er);
            }
            */


            if (modulators) {
                //add edges between regulators and genericprocesses
                HashMap<String, HashMap<String, ArrayList<String>>> edgeidmarkpairs = TableQueries.getRegulatorProcessEdges(repository);
                for (String gpid : edgeidmarkpairs.keySet()) {

                    HashMap<String, ArrayList<String>> gpidRegulatorMap = edgeidmarkpairs.get(gpid);
                    ArrayList<String> genericregulators = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.REGULATOR);
                    ArrayList<String> inhibitors = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.INHIBITOR);
                    ArrayList<String> activators = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.ACTIVATOR);

                    Node process = gpidToNodeTable.get(gpid);

                    if (process == null) continue;
                    //create generic regulator node
                    String regulatorname = "";
                    for (String regid : genericregulators) {
                        String moleculename = TableQueries.getMoleculeNameById(repository, regid);
                        if (moleculename != null && !moleculename.equals("")) {
                            if (regulatorname.equalsIgnoreCase(""))
                                regulatorname += moleculename;
                            else
                                regulatorname += ", " + moleculename;
                        }
                    }

                    if (!regulatorname.equals("")) {
                        Node regulatornode = graph.createNode();
                        ShapeNodeRealizer nr = getRegulatorShapeNodeRealizer(regulatorname, TableQueries.MOLECULE_ROLE_STRINGS.REGULATOR);
                        graph.setRealizer(regulatornode, nr);
                        nodeToIdsTable.put(regulatornode, new HashSet<String>(genericregulators));
                        compartmentQListMap.get("pathway").add(regulatornode);

                        Edge edge = graph.createEdge(regulatornode, process);
                        EdgeRealizer er = graph.getRealizer(edge);
                        er.setLineType(LineType.LINE_1);
                        makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                        //er.setTargetArrow(Arrow.STANDARD);
                    }

                    //create activator node
                    String activatorname = "";
                    for (String actid : activators) {
                        String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                        if (moleculename != null && !moleculename.equals("")) {
                            if (activatorname.equalsIgnoreCase(""))
                                activatorname += moleculename;
                            else
                                activatorname += ", " + moleculename;
                        }
                    }

                    if (!activatorname.equals("")) {
                        Node activatornode = graph.createNode();
                        ShapeNodeRealizer nr = getRegulatorShapeNodeRealizer(activatorname, TableQueries.MOLECULE_ROLE_STRINGS.ACTIVATOR);
                        graph.setRealizer(activatornode, nr);
                        nodeToIdsTable.put(activatornode, new HashSet<String>(activators));
                        compartmentQListMap.get("pathway").add(activatornode);

                        Edge edge = graph.createEdge(activatornode, process);
                        EdgeRealizer er = graph.getRealizer(edge);
                        er.setLineType(LineType.LINE_1);
                        makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);

                        int[] xpoints = {0, -4, -4};
                        int[] ypoints = {0, -4, 4};
                        er.setTargetArrow(Arrow.addCustomArrow("ActivatorArrow", new Polygon(xpoints, ypoints, 3), Color.white));
                    }

                    //create inhibitor node
                    String inhibitorname = "";
                    for (String actid : inhibitors) {
                        String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                        if (moleculename != null && !moleculename.equals("")) {
                            if (inhibitorname.equalsIgnoreCase(""))
                                inhibitorname += moleculename;
                            else
                                inhibitorname += ", " + moleculename;
                        }
                    }

                    if (!inhibitorname.equals("")) {
                        Node inhibitornode = graph.createNode();
                        ShapeNodeRealizer nr = getRegulatorShapeNodeRealizer(inhibitorname, TableQueries.MOLECULE_ROLE_STRINGS.INHIBITOR);
                        graph.setRealizer(inhibitornode, nr);
                        nodeToIdsTable.put(inhibitornode, new HashSet<String>(inhibitors));
                        compartmentQListMap.get("pathway").add(inhibitornode);

                        Edge edge = graph.createEdge(inhibitornode, process);
                        EdgeRealizer er = graph.getRealizer(edge);
                        er.setLineType(LineType.LINE_1);
                        makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);

                        int[] xpoints = {0, -2, -2, 0};
                        int[] ypoints = {-5, -5, 5, 5};
                        er.setTargetArrow(Arrow.addCustomArrow("InhibitorArrow", new Polygon(xpoints, ypoints, 4), Color.white));
                    }
                }

                //add edges between cofactors and genericprocesses
                edgeidmarkpairs = TableQueries.getCofactorProcessEdges(repository);
                for (String gpid : edgeidmarkpairs.keySet()) {

                    HashMap<String, ArrayList<String>> gpidRegulatorMap = edgeidmarkpairs.get(gpid);
                    ArrayList<String> genericcofactors = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.COFACTOR);
                    ArrayList<String> cofactorins = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.COFACTORIN);
                    ArrayList<String> cofactorouts = gpidRegulatorMap.get(TableQueries.MOLECULE_ROLE_STRINGS.COFACTOROUT);

                    Node process = gpidToNodeTable.get(gpid);

                    if (process == null) continue;
                    //create generic cofactor node
                    String cofactorname = "";
                    for (String regid : genericcofactors) {
                        String moleculename = TableQueries.getMoleculeNameById(repository, regid);
                        if (moleculename != null && !moleculename.equals("")) {
                            if (cofactorname.equalsIgnoreCase(""))
                                cofactorname += moleculename;
                            else
                                cofactorname += ", " + moleculename;
                        }
                    }

                    if (!cofactorname.equals("")) {
                        Node cofactornode = graph.createNode();
                        ShapeNodeRealizer nr = getCofactorShapeNodeRealizer(cofactorname, TableQueries.MOLECULE_ROLE_STRINGS.COFACTOR);
                        graph.setRealizer(cofactornode, nr);
                        nodeToIdsTable.put(cofactornode, new HashSet<String>(genericcofactors));
                        compartmentQListMap.get("pathway").add(cofactornode);

                        Edge edge = graph.createEdge(cofactornode, process);
                        EdgeRealizer er = graph.getRealizer(edge);
                        er.setLineType(LineType.LINE_1);
                        makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                    }

                    //create cofactorin node
                    String cofactorinname = "";
                    for (String actid : cofactorins) {
                        String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                        if (moleculename != null && !moleculename.equals("")) {
                            if (cofactorinname.equalsIgnoreCase(""))
                                cofactorinname += moleculename;
                            else
                                cofactorinname += ", " + moleculename;
                        }
                    }

                    if (!cofactorinname.equals("")) {
                        Node cofactornode = graph.createNode();
                        ShapeNodeRealizer nr = getCofactorShapeNodeRealizer(cofactorinname, TableQueries.MOLECULE_ROLE_STRINGS.COFACTORIN);
                        graph.setRealizer(cofactornode, nr);
                        nodeToIdsTable.put(cofactornode, new HashSet<String>(cofactorins));
                        compartmentQListMap.get("pathway").add(cofactornode);

                        Edge edge = graph.createEdge(cofactornode, process);
                        ArcEdgeRealizer er = new ArcEdgeRealizer();
                        graph.setRealizer(edge, er);
                        er.setLineType(LineType.LINE_1);
                        er.setRatio(-1);
                        makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                        er.setTargetArrow(Arrow.STANDARD);
                    }

                    //create cofactorout node
                    String cofactoroutname = "";
                    for (String actid : cofactorouts) {
                        String moleculename = TableQueries.getMoleculeNameById(repository, actid);
                        if (moleculename != null && !moleculename.equals("")) {
                            if (cofactoroutname.equalsIgnoreCase(""))
                                cofactoroutname += moleculename;
                            else
                                cofactoroutname += ", " + moleculename;
                        }
                    }

                    if (!cofactoroutname.equals("")) {
                        Node inhibitornode = graph.createNode();
                        ShapeNodeRealizer nr = getCofactorShapeNodeRealizer(cofactoroutname, TableQueries.MOLECULE_ROLE_STRINGS.COFACTOROUT);
                        graph.setRealizer(inhibitornode, nr);
                        nodeToIdsTable.put(inhibitornode, new HashSet<String>(cofactorouts));
                        compartmentQListMap.get("pathway").add(inhibitornode);

                        Edge edge = graph.createEdge(process, inhibitornode);
                        ArcEdgeRealizer er = new ArcEdgeRealizer();
                        graph.setRealizer(edge, er);
                        er.setLineType(LineType.LINE_1);
                        er.setRatio(-1);
                        makeRegulatorCofactorEdgeNormal(er, (PathCaseShapeNodeRealizer) nr);
                        er.setTargetArrow(Arrow.STANDARD);
                    }
                }
            }

        //create frame box for pathway graph


//        (new PathCaseLayouter("organic")).start(graph);

            String pwlabel=TableQueries.getPathwayNameById(repository,pathwayId);// "Model Name...";//TableQueries.getCompartmnetNamebyCompartmentID(repository,compid);
//                System.out.println("----->Before create group nodes model has comparments:"+getGoupNodesNum(graph));
              Node groupNode =hm.createGroupNode(graph);
//              graph.fitGraph2DView();
              ShapeNodeRealizer nrGroup = getTissueShapeNodeRealizerSB(pwlabel,startX,startY,graph.getBoundingBox().getMaxX(),graph.getBoundingBox().getMaxY(),Color.black);
//            ShapeNodeRealizer nrGroup = getTissueShapeNodeRealizerSBPW(pwlabel);
              graph.setRealizer(groupNode, nrGroup);
              HashSet<String> comlist = new HashSet<String>();
              comlist.add("pathway");
              nodeToIdsTable.put(groupNode,comlist);
//              idToNodeTable.put(compid,groupNode);
            hm.groupSubgraph(compartmentQListMap.get("pathway"),groupNode);

            return nodeToIdsTable;
        }


     public static void SetAcceptableTissueColors()
    {
        acceptableTissueColors=new Vector();
        acceptableTissueColors.add(Color.lightGray);

        acceptableTissueColors.add(Color.green);
        acceptableTissueColors.add(Color.blue);
        //acceptableTissueColors.add(Color.red);
        acceptableTissueColors.add(Color.orange);
        acceptableTissueColors.add(Color.CYAN);
        acceptableTissueColors.add(Color.MAGENTA);
        acceptableTissueColors.add(Color.PINK);
        acceptableTissueColors.add(Color.YELLOW);
    }

    public static void makeEdgeGrayedOut(EdgeRealizer er) {
        er.setLineColor(EDGEGRAYOUT);
    }

    public static void makeMetaboliteEdgeNormal(EdgeRealizer er) {
        er.setLineColor(Color.BLACK);
    }

    public static void makeArtificialEdgeNormal(EdgeRealizer er) {
        er.setLineColor(Color.BLACK);
    }

    public static void makeRegulatorCofactorEdgeNormal(EdgeRealizer er, PathCaseShapeNodeRealizer nr) {

        if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR)
            er.setLineColor(Color.GREEN);
        else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR)
            er.setLineColor(Color.RED);
        else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR)
            er.setLineColor(Color.ORANGE);

        else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR)
            er.setLineColor(Color.RED);
        else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN)
            er.setLineColor(Color.RED);
        else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)
            er.setLineColor(Color.RED);

    }


    private static Color NODEGRAYOUT = new Color(170, 190, 220);
    private static Color EDGEGRAYOUT = new Color(225, 225, 225);
    private static Color GENEPICK = new Color(250, 200, 150);


    public static Color colorNameToColorObject(String name) {
        String colorname = name.toLowerCase().trim();

        if (colorname.equals("black")) return Color.black;
        else if (colorname.equals("blue")) return Color.blue;
        else if (colorname.equals("cyan")) return Color.cyan;
        else if (colorname.equals("darkgray")) return Color.darkGray;
        else if (colorname.equals("gray")) return Color.gray;
        else if (colorname.equals("green")) return Color.green;
        else if (colorname.equals("lightgray")) return Color.lightGray;
        else if (colorname.equals("magenta")) return Color.magenta;
        else if (colorname.equals("orange")) return Color.orange;
        else if (colorname.equals("pink")) return Color.pink;
        else if (colorname.equals("red")) return Color.red;
        else if (colorname.equals("white")) return Color.white;
        else if (colorname.equals("yellow")) return Color.yellow;
        else if (colorname.startsWith("[") && colorname.endsWith("]")) {
            String colorstring = colorname.substring(1, colorname.length() - 1);
            StringTokenizer st = new StringTokenizer(colorstring, ",");
            try {
                int red = Integer.parseInt(st.nextToken());
                int green = Integer.parseInt(st.nextToken());
                int blue = Integer.parseInt(st.nextToken());

                return new Color(red, green, blue);
            }
            catch (Exception e) {
                return Color.black;
            }

        } else return Color.black;

    }

    public static void makeSubstrateProductShapeNodeRealizerNormal(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setTextColor(Color.BLACK);
        nr.setFillColor(Color.YELLOW);
        //nr.setFillColor2(CLASSIC_THEME_COLORS.get("SubstrateProductNormalFill2"));
        nr.setLineColor(Color.BLACK);

    }

    public static void makeSubstrateProductShapeNodeRealizerLinkingMolecule(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setTextColor(Color.BLACK);
        nr.setFillColor(new Color(215, 215, 0));
        //nr.setLineType(LineType.DASHED_1);
        //nr.setFillColor2(CLASSIC_THEME_COLORS.get("SubstrateProductNormalFill2"));
        nr.setLineColor(Color.BLACK);

    }


    public static void makeSubstrateProductShapeNodeRealizerLinkingMoleculeBetweenExpandedPathways(PathCaseShapeNodeRealizer nr) {
        nr.getLabel().setTextColor(Color.BLACK);
        nr.setFillColor(new Color(176, 176, 0));
        //nr.setLineType(LineType.DASHED_1);
        //nr.setFillColor2(CLASSIC_THEME_COLORS.get("SubstrateProductNormalFill2"));
        nr.setLineColor(Color.BLACK);
    }

    public static void makeSubstrateProductShapeNodeRealizerLinkingMoleculeWithOtherPathways(PathCaseShapeNodeRealizer nr) {
        nr.getLabel().setTextColor(Color.BLACK);
        nr.setFillColor(new Color(225, 225, 0));
        //nr.setLineType(LineType.DASHED_1);
        //nr.setFillColor2(CLASSIC_THEME_COLORS.get("SubstrateProductNormalFill2"));
        nr.setLineColor(Color.BLACK);
    }

    public static void makeSubstrateProductShapeNodeRealizerGrayedOut(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setTextColor(NODEGRAYOUT);
        nr.setFillColor(Color.WHITE);
        nr.setLineColor(NODEGRAYOUT);

    }

    public static void makeCollapsedPathwayShapeNodeRealizerNormal(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setTextColor(Color.BLACK);
        nr.setFillColor(new Color(255, 150, 75));
        nr.setLineColor(Color.BLACK);

    }

    public static void makeCollapsedPathwayShapeNodeRealizerGrayedOut(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setTextColor(NODEGRAYOUT);
        nr.setFillColor(Color.WHITE);
        nr.setLineColor(NODEGRAYOUT);

    }

    public static void makeRegulatorShapeNodeRealizerGrayedOut(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setTextColor(NODEGRAYOUT);
        //nr.setLineColor(Color.WHITE);
        //nr.setFillColor(Color.WHITE);

    }

    public static void makeRegulatorShapeNodeRealizerNormal(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setLineColor(Color.WHITE);
        nr.setFillColor(Color.WHITE);
        nr.setLineColor(Color.WHITE);

        if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR)
            nr.getLabel().setTextColor(Color.GREEN);
        else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR)
            nr.getLabel().setTextColor(Color.RED);
        else
            nr.getLabel().setTextColor(Color.ORANGE);


    }

    public static void makeCofactorShapeNodeRealizerNormal(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setTextColor(Color.RED);
        nr.setFillColor(Color.WHITE);
        if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN)
            nr.setLineColor(Color.WHITE);
        else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)
            nr.setLineColor(Color.WHITE);
        else
            nr.setLineColor(Color.WHITE);


    }

    public static void makeCofactorShapeNodeRealizerGrayedOut(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setTextColor(NODEGRAYOUT);
        //nr.setLineColor(NODEGRAYOUT);
        //nr.setFillColor(Color.WHITE);

    }

    public static void makeCollapsedPathwayShapeNodeRealizerGeneSelected(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setTextColor(Color.RED);
        nr.setFillColor(GENEPICK);
        nr.setLineColor(Color.ORANGE);

    }

    public static void makeGenericProcessShapeNodeRealizerNormal(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setTextColor(Color.BLUE);
        nr.setFillColor(Color.WHITE);
        nr.setLineColor(Color.BLUE);

        nr.getLabel().setFontStyle(Font.PLAIN);

    }

    public static void makeGenericProcessShapeNodeRealizerNormal(PathCaseShapeNodeRealizer nr,boolean isTransport,boolean isReversible) {

            nr.getLabel().setTextColor(Color.BLUE);
            if(isTransport)
                nr.setFillColor(Color.cyan);
            else
            {

            if(!isReversible)    nr.setFillColor(Color.WHITE);
            else  nr.setFillColor(Color.orange);

            }
            nr.setLineColor(Color.BLUE);

            nr.getLabel().setFontStyle(Font.PLAIN);

        }


    public static void makeGenericProcessShapeNodeRealizerGrayedOut(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setTextColor(NODEGRAYOUT);
        nr.setFillColor(Color.WHITE);
        nr.setLineColor(NODEGRAYOUT);

        nr.getLabel().setFontStyle(Font.PLAIN);

    }

    public static void makeGenericProcessShapeNodeRealizerGeneSelected(PathCaseShapeNodeRealizer nr) {

        nr.getLabel().setTextColor(Color.RED);
        nr.setFillColor(GENEPICK);
        nr.setLineColor(Color.RED);

        nr.getLabel().setFontStyle(Font.BOLD);

    }

    public static void fillNodeRealizerCustom(PathCaseShapeNodeRealizer nr, Color customfillcolor) {
        nr.setFillColor(customfillcolor);
    }


    private static PathCaseShapeNodeRealizer getSubstrateProductShapeNodeRealizer(String moleculename, boolean isCommon,double dx,double dy) {
         PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

         nr.setShapeType(ShapeNodeRealizer.ELLIPSE);
         if (moleculename.length() > 15)
             moleculename = moleculename.substring(0, 13) + "..";


          StringTokenizer st = new StringTokenizer(moleculename, "\n");
        int numberoflabellines = st.countTokens();
        String nodetext = "";
        while (st.hasMoreTokens()) {
            String token = st.nextToken();

//            if (token.length() > 8)
//                token = token.substring(0, 3) + "..";
            if (st.hasMoreTokens())
                nodetext += token + "\n";
            else
                nodetext += token;
        }

         nr.setSize(nodeSizeX,nodeSizeY);
         nr.setLocation(dx,dy);
         NodeLabel nodelabel = nr.createNodeLabel();
         nodelabel.setPosition(NodeLabel.CENTER);
//         nodelabel.setModel(NodeLabel.EIGHT_POS);
         nodelabel.setText(nodetext);
         nodelabel.setFontName("Arial");
         nodelabel.setFontSize(10);
//         nodelabel.setPosition(NodeLabel.ALIGN_CENTER);
//        nodelabel.setPosition(NodeLabel.CENTER);
         nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_WIDTH);
         //nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_HEIGHT);
         nr.setLabel(nodelabel);
//        nr.setVisible(false);

         makeSubstrateProductShapeNodeRealizerNormal(nr);
         if (!isCommon)
             nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT);
         else
             nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON);
         //System.out.println(nodelabel.getText());
         return nr;

     }


    private static PathCaseShapeNodeRealizer getSubstrateProductShapeNodeRealizer(String moleculename, boolean isCommon) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

        nr.setShapeType(ShapeNodeRealizer.ELLIPSE);
        if (moleculename.length() > 15)
            moleculename = moleculename.substring(0, 13) + "..";

        nr.setSize(20, 20);
        NodeLabel nodelabel = nr.createNodeLabel();
        nodelabel.setModel(NodeLabel.EIGHT_POS);
        nodelabel.setText(moleculename);
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(10);
//        nodelabel.setPosition(NodeLabel.CENTER);
        nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_WIDTH);
        //nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_HEIGHT);
        nr.setLabel(nodelabel);
//        nr.setVisible(false);

        makeSubstrateProductShapeNodeRealizerNormal(nr);
        if (!isCommon)
            nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT);
        else
            nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON);
        //System.out.println(nodelabel.getText());
        return nr;


    }


    private static PathCaseShapeNodeRealizer getFakedShapeNodeRealizer(double x,double y) {
            PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

//            nr.setShapeType(ShapeNodeRealizer.ELLIPSE);
//            if (moleculename.length() > 15)
//                moleculename = moleculename.substring(0, 13) + "..";

            nr.setSize(2, 2);
            nr.setLocation(x,y);
        nr.setFillColor(Color.red);
//            NodeLabel nodelabel = nr.createNodeLabel();
//        nodelabel.setModel(NodeLabel.EIGHT_POS);
//            nr.setVisible(true);
        nr.setVisible(false);

//            makeSubstrateProductShapeNodeRealizerNormal(nr);

            return nr;
        }


    private static PathCaseShapeNodeRealizer getSubstrateProductShapeNodeRealizerCompartmentH2(String moleculename,PPoint p) {
        return getSubstrateProductShapeNodeRealizerCompartmentH2(moleculename,p.getX(),p.getY());
    }

private static PathCaseShapeNodeRealizer getSubstrateProductShapeNodeRealizerCompartmentH2(String moleculename,double x,double y) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

        nr.setShapeType(ShapeNodeRealizer.ELLIPSE);
        if (moleculename.length() > 15)
            moleculename = moleculename.substring(0, 13) + "..";

        nr.setSize(20, 20);
        nr.setCenter(x,y);
        NodeLabel nodelabel = nr.createNodeLabel();
//        nodelabel.setModel(NodeLabel.EIGHT_POS);
        nodelabel.setText(moleculename);
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(10);
        nodelabel.setPosition(NodeLabel.CENTER);
//        nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_WIDTH);
        //nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_HEIGHT);
        nr.setLabel(nodelabel);
//       nr.setCenter(10,20);
        nr.setVisible(true);

        makeSubstrateProductShapeNodeRealizerNormal(nr);
        boolean isCommon=true;
//        if (!isCommon)
//            nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.SPECIES);
//        else
            nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.SPECIES);
        //System.out.println(nodelabel.getText());
        return nr;
    }

   private static PathCaseShapeNodeRealizer getSubstrateProductShapeNodeRealizerCompartmentH(String moleculename) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

        nr.setShapeType(ShapeNodeRealizer.ELLIPSE);
        if (moleculename.length() > 15)
            moleculename = moleculename.substring(0, 13) + "..";

        nr.setSize(20, 20);
        NodeLabel nodelabel = nr.createNodeLabel();
//        nodelabel.setModel(NodeLabel.EIGHT_POS);
        nodelabel.setText(moleculename);
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(10);
        nodelabel.setPosition(NodeLabel.CENTER);
//        nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_WIDTH);
        //nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_HEIGHT);
        nr.setLabel(nodelabel);
//       nr.setCenter(10,20);
        nr.setVisible(true);

        makeSubstrateProductShapeNodeRealizerNormal(nr);
        boolean isCommon=true;
        if (!isCommon)
            nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT);
        else
            nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON);
        //System.out.println(nodelabel.getText());
        return nr;
    }

    private static PathCaseShapeNodeRealizer getGenericProcessShapeNodeRealizer2(String label,PPoint p) {
        return getGenericProcessShapeNodeRealizer2(label,p.getX(),p.getY());
    }

    private static PathCaseShapeNodeRealizer getGenericProcessShapeNodeRealizer2(String label,double x,double y) {
           PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

           nr.setShapeType(ShapeNodeRealizer.RECT);

           StringTokenizer st = new StringTokenizer(label, "\n");
           int numberoflabellines = st.countTokens();
           String nodetext = "";
           while (st.hasMoreTokens()) {
               String token = st.nextToken();

               if (token.length() > 10)
                   token = token.substring(0, 8) + "..";
               if (st.hasMoreTokens())
                   nodetext += token + "\n";
               else
                   nodetext += token;
           }

//           nr.setHeight((float) (10 * numberoflabellines + 10));
           nr.setWidth(40);
        nr.setHeight(15);
           nr.setCenter(x,y);

           NodeLabel nodelabel = nr.createNodeLabel();
           nodelabel.setModel(NodeLabel.INTERNAL);
           nodelabel.setPosition(NodeLabel.CENTER);
           nodelabel.setText(nodetext);
            nodelabel.setText(label);
           nodelabel.setFontName("Arial");
           nodelabel.setFontSize(10);
           nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
           nr.setLabel(nodelabel);

           makeGenericProcessShapeNodeRealizerNormal(nr);
           nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.REACTION);
           //System.out.println(nodelabel.getText());
           return nr;
       }


     private static PathCaseShapeNodeRealizer getNullMoleShapeNodeRealizer(String label,double dx,double dy) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

         nr.setShapeType(ShapeNodeRealizer.ELLIPSE);

//         nr.setShapeType(ShapeNodeRealizer.HOTSPOT_NONE);
//         nr.setLineType(LineType.LINE_1);
//         nr.setFillColor(Color.RED);

         nr.setSize(20, 20);
//         nr.setCenter(dx,dy);
//        nr.setHeight((float) (10));
//        nr.setWidth(nullMoleSize);
        nr.setLocation(dx,dy);
//         nr.setDropShadowColor(Color.GREEN);

        NodeLabel nodelabel = nr.createNodeLabel();
        nodelabel.setModel(NodeLabel.INTERNAL);
        nodelabel.setPosition(NodeLabel.CENTER);
        if(label.equalsIgnoreCase("NULL REACT"))
            nodelabel.setText("_srs_");
        else if(label.equalsIgnoreCase("NULL PRODUCT"))
            nodelabel.setText("_waste_");
        else
            nodelabel.setText("Parameter Specified");
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(5);
        nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
//        nr.setLabel(nodelabel);

        makeGenericProcessShapeNodeRealizerNormal(nr);
        nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.NULLMOLE);
        //System.out.println(nodelabel.getText());
        return nr;             
    }

     private static PathCaseShapeNodeRealizer getNullMoleShapeNodeRealizer_nullsymbol(String label,double dx,double dy) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

        nr.setShapeType(ShapeNodeRealizer.HOTSPOT_NONE);
         nr.setLineType(LineType.LINE_1);
         nr.setFillColor(Color.RED);

        nr.setHeight((float) (10));
        nr.setWidth(nullMoleSize);
        nr.setLocation(dx,dy);
         nr.setDropShadowOffsetX((byte)1);
         nr.setDropShadowOffsetY((byte)1);
         nr.setDropShadowColor(Color.GREEN);

        NodeLabel nodelabel = nr.createNodeLabel();
        nodelabel.setModel(NodeLabel.INTERNAL);
        nodelabel.setPosition(NodeLabel.CENTER);
        nodelabel.setText(label);
//        nodelabel.setText(label);
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(5);
        nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
//        nr.setLabel(nodelabel);

        makeGenericProcessShapeNodeRealizerNormal(nr);
        nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.NULLMOLE);
        //System.out.println(nodelabel.getText());
        return nr;
    }

    private static PathCaseShapeNodeRealizer getGenericProcessShapeNodeRealizer(String label,double dx,double dy,boolean isTransportReaction, boolean isReversible ) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

        nr.setShapeType(ShapeNodeRealizer.RECT);
//        if(isTransportReaction)nr.setFillColor(Color.cyan);

        StringTokenizer st = new StringTokenizer(label, "\n");
        int numberoflabellines = st.countTokens();
        String nodetext = "";
        while (st.hasMoreTokens()) {
            String token = st.nextToken();

//            if (token.length() > 10)
//                token = token.substring(0, 6) + "..";
            if (st.hasMoreTokens())
                nodetext += token + "\n";
            else
                nodetext += token;
        }

        nr.setHeight((float) (10 * numberoflabellines + 10));
        nr.setWidth(reactionSizeX);
        nr.setLocation(dx,dy);

        NodeLabel nodelabel = nr.createNodeLabel();
        nodelabel.setModel(NodeLabel.INTERNAL);
        nodelabel.setPosition(NodeLabel.CENTER);
        nodelabel.setText(nodetext);
//        nodelabel.setText(label);
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(10);
        nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
        nr.setLabel(nodelabel);

        makeGenericProcessShapeNodeRealizerNormal(nr,isTransportReaction, isReversible);
        nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS);
        //System.out.println(nodelabel.getText());
        return nr;
    }

      private static PathCaseShapeNodeRealizer getGenericProcessShapeNodeRealizer(String label,double dx,double dy) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

        nr.setShapeType(ShapeNodeRealizer.RECT);

        StringTokenizer st = new StringTokenizer(label, "\n");
        int numberoflabellines = st.countTokens();
        String nodetext = "";
        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            if (token.length() > 10)
                token = token.substring(0, 6) + "..";
            if (st.hasMoreTokens())
                nodetext += token + "\n";
            else
                nodetext += token;
        }

        nr.setHeight((float) (10 * numberoflabellines + 10));
        nr.setWidth(reactionSizeX);
        nr.setLocation(dx,dy);

        NodeLabel nodelabel = nr.createNodeLabel();
        nodelabel.setModel(NodeLabel.INTERNAL);
        nodelabel.setPosition(NodeLabel.CENTER);
        nodelabel.setText(nodetext);
//        nodelabel.setText(label);
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(10);
        nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
        nr.setLabel(nodelabel);

        makeGenericProcessShapeNodeRealizerNormal(nr);
        nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS);
        //System.out.println(nodelabel.getText());
        return nr;
    }

    private static PathCaseShapeNodeRealizer getGenericProcessShapeNodeRealizer(String label) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

        nr.setShapeType(ShapeNodeRealizer.RECT);

        StringTokenizer st = new StringTokenizer(label, "\n");
        int numberoflabellines = st.countTokens();
        String nodetext = "";
//        while (st.hasMoreTokens()) {
//            String token = st.nextToken();
//
//            if (token.length() > 10)
//                token = token.substring(0, 8) + "..";
//            if (st.hasMoreTokens())
//                nodetext += token + "\n";
//            else
//                nodetext += token;
//        }
        if(label.length()>10){
            nodetext=label.substring(0,8)+"..";
        }else
            nodetext=label;

        nr.setHeight((float) (10 * numberoflabellines + 10));
        nr.setWidth(60);


        NodeLabel nodelabel = nr.createNodeLabel();
        nodelabel.setModel(NodeLabel.INTERNAL);
        nodelabel.setPosition(NodeLabel.CENTER);
        nodelabel.setText(nodetext);         
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(10);
        nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
        nr.setLabel(nodelabel);

        makeGenericProcessShapeNodeRealizerNormal(nr);
        nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS);
        //System.out.println(nodelabel.getText());
        return nr;
    }

    private static PathCaseShapeNodeRealizer getCompartmentShapeNodeRealizer(String label) {
           PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();
//        GroupNodeRealizer re=new GroupNodeRealizer();
           nr.setShapeType(ShapeNodeRealizer.RECT);

           Color selectedColor;
            if(label.equalsIgnoreCase("blood"))
            {
              nr.setLineColor(Color.RED);
                selectedColor=Color.RED;
//             nr.setSize(500,500);
            }
//        else if(label.contains("3"))
//         {
//             selectedColor= getTissueColor();
//            nr.setLineColor(selectedColor);
//             nr.setSize(310,310);
////             nr.setLocation(200,200);
//         }
           else
            {
                selectedColor= getTissueColor();
               nr.setLineColor(selectedColor);
//             nr.setSize(200,200);
//             nr.setLocation(200,200);
            }
           nr.setTransparent(true);
           //nr.setLineType(LineType.LINE_7 );
           //nr.setHeight(10);
           //nr.setWidth(60);

        boolean bVisible=true;
        nr.setVisible(bVisible);

           NodeLabel nodelabel = nr.createNodeLabel();
           nodelabel.setModel(NodeLabel.TOP );
           nodelabel.setPosition(NodeLabel.TOP_RIGHT);

           nodelabel.setText(label);
           nodelabel.setTextColor(selectedColor) ;
           nodelabel.setFontName("Arial");
           nodelabel.setFontSize(10);
           nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
           nr.setLabel(nodelabel);
//            nr.setVisible(false);
           //makeGenericProcessShapeNodeRealizerNormal(nr);
           nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.COMPARTMENT);
           //System.out.println(nodelabel.getText());
           return nr;

       }


    private static PathCaseShapeNodeRealizer getTissueShapeNodeRealizerCompartmentH(String label) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();
//        GroupNodeRealizer re=new GroupNodeRealizer();
        nr.setShapeType(ShapeNodeRealizer.RECT);

        Color selectedColor;
         if(label.equalsIgnoreCase("blood"))
         {
             nr.setLineColor(Color.RED);
             selectedColor=Color.RED;
             nr.setSize(100,100);
             nr.setCenter(200,200);
         }
//        else if(label.contains("3"))
//         {
//             selectedColor= getTissueColor();
//            nr.setLineColor(selectedColor);
//             nr.setSize(310,310);
////             nr.setLocation(200,200);
//         }
        else
         {
             selectedColor= getTissueColor();
            nr.setLineColor(selectedColor);
//             nr.setSize(200,200);
//             nr.setLocation(200,200);
         }
        nr.setTransparent(true);
        //nr.setLineType(LineType.LINE_7 );
        //nr.setHeight(10);
        //nr.setWidth(60);

//        nr.setVisible(false);



        NodeLabel nodelabel = nr.createNodeLabel();
        nodelabel.setModel(NodeLabel.TOP );
        nodelabel.setPosition(NodeLabel.TOP_RIGHT);

        nodelabel.setText(label);
        nodelabel.setTextColor(selectedColor) ;
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(10);
        nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
        nr.setLabel(nodelabel);
//            nr.setVisible(false);
        //makeGenericProcessShapeNodeRealizerNormal(nr);
        nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.TISSUEGROUP);
        //System.out.println(nodelabel.getText());
        return nr;

    }

    private static PathCaseShapeNodeRealizer getTissueShapeNodeRealizer(String label) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

        nr.setShapeType(ShapeNodeRealizer.RECT);

        //nr.setHeight(10);
        //nr.setWidth(60);


        /*NodeLabel nodelabel = nr.createNodeLabel();
        nodelabel.setModel(NodeLabel.INTERNAL);
        nodelabel.setPosition(NodeLabel.CENTER);
        nodelabel.setText(label);
        // nodelabel.setText(label);
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(10);
        nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
        nr.setLabel(nodelabel);*/

        //makeGenericProcessShapeNodeRealizerNormal(nr);
        nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.TISSUEGROUP);
        //System.out.println(nodelabel.getText());
        return nr;

    }

     private static PathCaseShapeNodeRealizer getTissueShapeNodeRealizerSBPW(String label) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

        nr.setShapeType(ShapeNodeRealizer.RECT);

        //nr.setHeight(10);
        //nr.setWidth(60);


        NodeLabel nodelabel = nr.createNodeLabel();
        nodelabel.setModel(NodeLabel.INTERNAL);
        nodelabel.setPosition(NodeLabel.CENTER);
        nodelabel.setText(label);
        // nodelabel.setText(label);
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(10);
        nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
        nr.setLabel(nodelabel);

        //makeGenericProcessShapeNodeRealizerNormal(nr);
        nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.TISSUEGROUP);
        //System.out.println(nodelabel.getText());
        return nr;

    }

    private static GroupNodeRealizer getTissueShapeNodeRealizerSBGroup(String label) {
        GroupNodeRealizer nr = new GroupNodeRealizer();
//        GroupNodeRealizer re=new GroupNodeRealizer();
//                nr.setShapeType(GroupNodeRealizer.RECT);// ShapeNodeRealizer.RECT
//                                 nr.setAutoBoundsEnabled(true);
                Color selectedColor;
                 if(label.equalsIgnoreCase("blood"))
                 {
                   nr.setLineColor(Color.RED);
//                     selectedColor=Color.RED;
//                     nr.setSize(500,500);
                 }
                else
                 {
                     selectedColor= getTissueColor();
                    nr.setLineColor(selectedColor);
//                     setGroupClose(true);
//                     nr.setSize(200,200);
                 }
                nr.setTransparent(true);
//
                NodeLabel nodelabel = nr.createNodeLabel();
                nodelabel.setModel(NodeLabel.TOP );
                nodelabel.setPosition(NodeLabel.TOP_RIGHT);

                nodelabel.setText(label);
                nodelabel.setFontName("Arial");
//                nodelabel.setFontSize(10);
//                nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
                nr.setLabel(nodelabel);

//                nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.TISSUEGROUP);
                return nr;
       }

    private static PathCaseGroupShapeNodeRealizer getTissueGroupShapeNodeRealizer(String label) {
      PathCaseGroupShapeNodeRealizer nr = new PathCaseGroupShapeNodeRealizer();
//        GroupNodeRealizer re=new GroupNodeRealizer();
                nr.setShapeType(ShapeNodeRealizer.RECT);

                Color selectedColor;
                 if(label.equalsIgnoreCase("blood"))
                 {
                   nr.setLineColor(Color.RED);
//                     selectedColor=Color.RED;
//                     nr.setSize(500,500);
                 }
                else
                 {
                     selectedColor= getTissueColor();
                    nr.setLineColor(selectedColor);
//                     setGroupClose(true);
//                     nr.setSize(200,200);
                 }
                nr.setTransparent(true);
//

                NodeLabel nodelabel = nr.createNodeLabel();
                nodelabel.setModel(NodeLabel.TOP );
                nodelabel.setPosition(NodeLabel.TOP_RIGHT);

                nodelabel.setText(label);
//                if(label.equalsIgnoreCase("L5"))nodelabel.setText("           "+label);
//                nodelabel.setTextColor(selectedColor) ;
                nodelabel.setFontName("Arial");
//                nodelabel.setFontSize(10);
//                nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
                nr.setLabel(nodelabel);

                nr.setNodeRole(PathCaseGroupShapeNodeRealizer.PathCaseNodeRole.TISSUEGROUP);
                return nr;
    }

        private static PathCaseShapeNodeRealizerForModel getCompShapeNodeRealizerSBGrp(String label, double centerX, double centerY, double sizeX, double sizeY) {
                PathCaseShapeNodeRealizerForModel nr = new PathCaseShapeNodeRealizerForModel();
//        GroupNodeRealizer re=new GroupNodeRealizer();
                nr.setShapeType(ShapeNodeRealizer.RECT);

                Color selectedColor;
                 if(label.equalsIgnoreCase("blood"))
                 {
                   nr.setLineColor(Color.RED);
//                     selectedColor=Color.RED;
//                     nr.setSize(500,500);
                 }
                else
                 {
                     selectedColor= getTissueColor();
                    nr.setLineColor(selectedColor);
                     nr.setCenter(centerX,centerY);
                     nr.setSize(sizeX,sizeY);
//                     setGroupClose(true);
//                     nr.setSize(200,200);
                 }
                nr.setTransparent(true);
//

                NodeLabel nodelabel = nr.createNodeLabel();
                nodelabel.setModel(NodeLabel.TOP );
                nodelabel.setPosition(NodeLabel.TOP);

                nodelabel.setText(label);
//                if(label.equalsIgnoreCase("L5"))nodelabel.setText("           "+label);
//                nodelabel.setTextColor(selectedColor) ;
                nodelabel.setFontName("Arial");
//                nodelabel.setFontSize(10);
//                nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
                nr.setLabel(nodelabel);

//                nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.TISSUEGROUP);
//        nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.COMPARTMENT);
                return nr;
       }

    private static PathCaseShapeNodeRealizer getCompShapeNodeRealizerSB(String label, double centerX, double centerY, double sizeX, double sizeY) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();
//        GroupNodeRealizer re=new GroupNodeRealizer();
                nr.setShapeType(ShapeNodeRealizer.RECT);


                Color selectedColor;
                 if(label.equalsIgnoreCase("blood"))
                 {
                   nr.setLineColor(Color.RED);
//                     selectedColor=Color.RED;
//                     nr.setSize(500,500);
                 }
                else
                 {
                    selectedColor= getTissueColor();
                    nr.setLineColor(selectedColor);
//                    nr.setCenter(centerX,centerY);
//                    nr.setSize(sizeX,sizeY);
//                     setGroupClose(true);
//                    ((AutoBoundsFeature)nr).setAutoBoundsEnabled(false);
//                     nr.setSize(200,200);
                 }
                nr.setTransparent(true);
        nr.setLocation(centerX,centerY);
        nr.setSize(sizeX,sizeY);
//

                NodeLabel nodelabel = nr.createNodeLabel();
                nodelabel.setModel(NodeLabel.TOP );
                nodelabel.setPosition(NodeLabel.TOP);

                nodelabel.setText(label);
//                if(label.equalsIgnoreCase("L5"))nodelabel.setText("           "+label);
//                nodelabel.setTextColor(selectedColor) ;
                nodelabel.setFontName("Arial");
//                nodelabel.setFontSize(10);
//                nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
                nr.setLabel(nodelabel);

//                nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.TISSUEGROUP);
                nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.COMPARTMENT);
                return nr;
       }

    
       private static PathCaseShapeNodeRealizer getTissueShapeNodeRealizerSB(String label, double dx,double dy,double width, double height) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();
//        GroupNodeRealizer re=new GroupNodeRealizer();
                nr.setShapeType(ShapeNodeRealizer.RECT);

                Color selectedColor;
                 if(label.equalsIgnoreCase("blood"))
                 {
                   nr.setLineColor(Color.RED);
//                     selectedColor=Color.RED;
//                     nr.setSize(500,500);
                 }
                else
                 {
                     selectedColor= getTissueColor();
                    nr.setLineColor(selectedColor);
//                     nr.setCenter(centerX,centerY);
                     nr.setSize(width,height);
                     nr.setLocation(dx,dy);
//                     setGroupClose(true);
//                     nr.setSize(200,200);
                 }
                nr.setTransparent(true);
//

                NodeLabel nodelabel = nr.createNodeLabel();
                nodelabel.setModel(NodeLabel.TOP );
                nodelabel.setPosition(NodeLabel.TOP_RIGHT);

                nodelabel.setText(label);
//                if(label.equalsIgnoreCase("L5"))nodelabel.setText("           "+label);
//                nodelabel.setTextColor(selectedColor) ;
                nodelabel.setFontName("Arial");
//                nodelabel.setFontSize(10);
//                nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
                nr.setLabel(nodelabel);

                nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.TISSUEGROUP);
                return nr;
       }

    private static PathCaseShapeNodeRealizer getTissueShapeNodeRealizerSB(String label, double dx,double dy,double width, double height,Color sColor) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();
//        GroupNodeRealizer re=new GroupNodeRealizer();
                nr.setShapeType(ShapeNodeRealizer.RECT);

                Color selectedColor;
                 if(label.equalsIgnoreCase("blood"))
                 {
                   nr.setLineColor(Color.RED);
//                     selectedColor=Color.RED;
//                     nr.setSize(500,500);
                 }
                else
                 {
//                     selectedColor= getTissueColor();
                    nr.setLineColor(sColor);
//                     nr.setCenter(centerX,centerY);
                     nr.setSize(width,height+16);
                     nr.setLocation(dx,dy-8);
//                     setGroupClose(true);
//                     nr.setSize(200,200);
                 }
                nr.setTransparent(true);
//
                nr.setLineType(LineType.DASHED_1);
                NodeLabel nodelabel = nr.createNodeLabel();
                nodelabel.setModel(NodeLabel.TOP );
                nodelabel.setPosition(NodeLabel.TOP_LEFT);

                nodelabel.setText(label);
//                if(label.equalsIgnoreCase("L5"))nodelabel.setText("           "+label);
//                nodelabel.setTextColor(selectedColor) ;
                nodelabel.setFontName("Arial");
//                nodelabel.setFontSize(10);
//                nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
                nr.setLabel(nodelabel);

                nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.TISSUEGROUP);
                return nr;
       }

        private static PathCaseShapeNodeRealizer getTissueShapeNodeRealizerSB(String label) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();
//        GroupNodeRealizer re=new GroupNodeRealizer();
                nr.setShapeType(ShapeNodeRealizer.RECT);

                Color selectedColor;
                 if(label.equalsIgnoreCase("blood"))
                 {
                   nr.setLineColor(Color.RED);
//                     selectedColor=Color.RED;
//                     nr.setSize(500,500);
                 }
                else
                 {
                     selectedColor= getTissueColor();
                    nr.setLineColor(selectedColor);
//                     nr.setCenter(centerX,centerY);
//                     nr.setSize(sizeX,sizeY);
//                     setGroupClose(true);
//                     nr.setSize(200,200);
                 }
                nr.setTransparent(true);
//

                NodeLabel nodelabel = nr.createNodeLabel();
                nodelabel.setModel(NodeLabel.TOP );
                nodelabel.setPosition(NodeLabel.TOP_RIGHT);

                nodelabel.setText(label);
//                if(label.equalsIgnoreCase("L5"))nodelabel.setText("           "+label);
//                nodelabel.setTextColor(selectedColor) ;
                nodelabel.setFontName("Arial");
//                nodelabel.setFontSize(10);
//                nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
                nr.setLabel(nodelabel);

                nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.TISSUEGROUP);
                return nr;
       }

           private static PathCaseShapeNodeRealizer getTissueShapeNodeRealizerSB(String label, double width, double height) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();
//        GroupNodeRealizer re=new GroupNodeRealizer();
                nr.setShapeType(ShapeNodeRealizer.RECT);

                Color selectedColor;
                 if(label.equalsIgnoreCase("blood"))
                 {
                   nr.setLineColor(Color.RED);
//                     selectedColor=Color.RED;
//                     nr.setSize(500,500);
                 }
                else
                 {
                     selectedColor= getTissueColor();
                    nr.setLineColor(selectedColor);
//                     nr.setCenter(centerX,centerY);
                     nr.setSize(width,height);
                     nr.setLocation(0,0);
//                     setGroupClose(true);
//                     nr.setSize(200,200);
                 }
                nr.setTransparent(true);
//

                NodeLabel nodelabel = nr.createNodeLabel();
                nodelabel.setModel(NodeLabel.TOP );
                nodelabel.setPosition(NodeLabel.TOP_RIGHT);

                nodelabel.setText(label);
//                if(label.equalsIgnoreCase("L5"))nodelabel.setText("           "+label);
//                nodelabel.setTextColor(selectedColor) ;
                nodelabel.setFontName("Arial");
//                nodelabel.setFontSize(10);
//                nodelabel.setAutoSizePolicy(YLabel.AUTOSIZE_CONTENT);
                nr.setLabel(nodelabel);

                nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.TISSUEGROUP);
                return nr;
       }

    private static PathCaseShapeNodeRealizer getCollapsedPathwayShapeNodeRealizer(String pathwayname) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

        nr.setShapeType(ShapeNodeRealizer.ROUND_RECT);
        if (pathwayname.length() > 22)
            pathwayname = pathwayname.substring(0, 20) + "..";

        nr.setSize(40, 20);
        NodeLabel nodelabel = nr.createNodeLabel();
        nodelabel.setModel(NodeLabel.EIGHT_POS);
        nodelabel.setText(pathwayname);
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(11);
        nr.setLabel(nodelabel);

        makeCollapsedPathwayShapeNodeRealizerNormal(nr);

        nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY);
        //System.out.println(nodelabel.getText());
        return nr;

    }

    private static ShapeNodeRealizer getRegulatorShapeNodeRealizer(String moleculename, String signature) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

        nr.setShapeType(ShapeNodeRealizer.RECT);
        if (moleculename.length() > 10)
            moleculename = moleculename.substring(0, 8) + "..";

        nr.setSize(60, 12);

        NodeLabel nodelabel = nr.createNodeLabel();
        nodelabel.setModel(NodeLabel.INTERNAL);
        nodelabel.setPosition(NodeLabel.CENTER);
        //nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_WIDTH);
        nodelabel.setText(moleculename);
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(10);
        nr.setLabel(nodelabel);

        if (signature.equalsIgnoreCase(TableQueries.MOLECULE_ROLE_STRINGS.ACTIVATOR))
            nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR);
        else if (signature.equalsIgnoreCase(TableQueries.MOLECULE_ROLE_STRINGS.INHIBITOR))
            nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR);
        else
            nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR);

        makeRegulatorShapeNodeRealizerNormal(nr);
        //System.out.println(nodelabel.getText());
        return nr;

    }

    private static ShapeNodeRealizer getCofactorShapeNodeRealizer(String moleculename, String direction) {
        PathCaseShapeNodeRealizer nr = new PathCaseShapeNodeRealizer();

        nr.setShapeType(ShapeNodeRealizer.RECT);
        if (moleculename.length() > 10)
            moleculename = moleculename.substring(0, 8) + "..";

        nr.setSize(60, 12);
        //nr.setLineType(LineType.getLineType(1,LineType.LINE_STYLE));
        NodeLabel nodelabel = nr.createNodeLabel();
        nodelabel.setModel(NodeLabel.INTERNAL);
        nodelabel.setPosition(NodeLabel.CENTER);
        //nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_WIDTH);
        nodelabel.setText(moleculename);
        nodelabel.setFontName("Arial");
        nodelabel.setFontSize(10);
        //nodelabel.setConfiguration("CroppingLabel");
        //nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_WIDTH);
//        nr.setLabel(nodelabel);

        if (direction.equalsIgnoreCase(TableQueries.MOLECULE_ROLE_STRINGS.COFACTORIN))
            nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN);
        else if (direction.equalsIgnoreCase(TableQueries.MOLECULE_ROLE_STRINGS.COFACTOROUT))
            nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT);
        else
            nr.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR);

        makeCofactorShapeNodeRealizerNormal(nr);
        //System.out.println(nodelabel.getText());
        return nr;
    }

     private static Color getTissueColor(){
        Color  returnColor =acceptableTissueColors.get(selectedTissueColor);
        selectedTissueColor++;
        if(selectedTissueColor==acceptableTissueColors.size())
        {
               selectedTissueColor=0;
        }
           return   returnColor;
    }

	static String modelName;
	private static void setModelName(String label) {
		modelName = label;
	}

	/**
	 * Get the current model name.
	 * @return Loaded model name.
	 */
	public static String getModelName() {
		return modelName;
	}
    	private static StackTraceElement[] stackTrace;
	public static StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	private static void setStackTrace(StackTraceElement[] st) {
		stackTrace = st;
	}
}
