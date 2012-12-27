package binevi.View;

import binevi.View.Layout.BooleanNodeMap;
import y.base.*;
import y.geom.YPoint;
import y.layout.*;
import y.layout.circular.CircularLayouter;
import y.layout.circular.SingleCycleLayouter;
import y.layout.grouping.FixedGroupLayoutStage;
import y.layout.grouping.GroupNodeHider;
import y.layout.grouping.Grouping;
import y.layout.hierarchic.BFSLayerer;
import y.layout.hierarchic.ClassicLayerSequencer;
import y.layout.hierarchic.HierarchicLayouter;
import y.layout.hierarchic.LayerSequencer;
import y.layout.labeling.SALabeling;
import y.layout.organic.RemoveOverlapsLayoutStage;
import y.layout.organic.SmartOrganicLayouter;
import y.layout.orthogonal.OrthogonalLayouter;
import y.layout.router.OrganicEdgeRouter;
import y.layout.tree.BalloonLayouter;
import y.module.LayoutModule;
import y.util.GraphHider;
import y.util.Maps;
import y.view.EdgeRealizer;
import y.view.NodeLabel;
import y.view.NodeRealizer;
import y.view.hierarchy.GroupLayoutConfigurator;
import y.view.hierarchy.HierarchyManager;

import java.util.HashSet;


public class PathCaseLayouterMetabolomics extends LayoutModule {


    Layouter layouter;
    Node sourcenode;
    String type;

    public PathCaseLayouterMetabolomics(String type) {
        super(type.toLowerCase(), "PathCase Development Team", "PathCase Layouter");

        if (type.equalsIgnoreCase("organic")) {
            layouter = OrganicLayout();
        } else if (type.equalsIgnoreCase("hierarchical")) {
            layouter = HierarchicalLayout();
        } else if (type.equalsIgnoreCase("orthogonal")) {
            layouter = OrthogonalLayout();
        } else if (type.equalsIgnoreCase("circular")) {
            layouter = CircularLayout();
        }else
        layouter = OrganicLayout();

        this.type = type;

        sourcenode = null;
    }

    public PathCaseLayouterMetabolomics(String s, Node v) {
        this(s);
        sourcenode = v;
    }

    public Layouter OrganicLayout() {

        final OrganicEdgeRouter router = new OrganicEdgeRouter();
        router.setMinimalDistance(10);
        router.setUsingBends(false);
        router.setRoutingAll(false);

        RemoveOverlapsLayoutStage rmos = new RemoveOverlapsLayoutStage(0);
        LayoutStage nodeEnlarger = router.createNodeEnlargementStage();
        final CompositeLayoutStage cls = new CompositeLayoutStage();
        cls.appendStage(nodeEnlarger);
        cls.appendStage(new BendConverter());
        cls.appendStage(rmos);

        final SmartOrganicLayouter module = new SmartOrganicLayouter();

//        module.setComponentLayouterEnabled(true);
//        module.setGroupNodeHidingEnabled(true);
        module.setLabelLayouterEnabled(true);
        module.setLabelLayouter(SALabeling());
//        module.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
//        module.setOrientationLayouterEnabled(true);
//        module.setParallelEdgeLayouterEnabled(true);
//        module.setSelfLoopLayouterEnabled(true);
//        module.setSubgraphLayouterEnabled(true);
//        module.setGroupBoundsCalculator();


        module.setPreferredEdgeLength(Math.round(40));
        module.setCompactness(0.7);
        module.setMaximumDuration(10000);
        module.setMinimalNodeDistance(0);
        module.setNodeEdgeOverlapAvoided(true);
        module.setNodeOverlapsAllowed(false);
        module.setNodeSizeAware(true);
        //module.setPreferredEdgeLength();
        //module.setPreferredMinimalNodeDistance();
        module.setQualityTimeRatio(1);
        module.setSmartComponentLayoutEnabled(true);

        module.setComponentLayouterEnabled(true);
        //module.setOutputRestriction( OutputRestriction.NONE);

        return new Layouter() {
        public boolean canLayout(LayoutGraph graph) {
            return true;
        }

            public void doLayout(LayoutGraph graph) {

                GroupLayoutConfigurator glc = null;
                int nGroupNodes=0;

                if (HierarchyManager.containsGroupNodes(getGraph2D())) {
                    glc = new GroupLayoutConfigurator(getGraph2D());
                    glc.prepareAll();

                    NodeMap nodeMap = Maps.createHashedNodeMap();
                    for (NodeCursor nodeCursor = getGraph2D().nodes(); nodeCursor.ok(); nodeCursor.next()) {
                        Node node = nodeCursor.node();
                        if (HierarchyManager.getInstance(getGraph2D()).isGroupNode(node)) {
                            nGroupNodes++;
                            nodeMap.set(node, SmartOrganicLayouter.GROUP_NODE_MODE_NORMAL);
                        }
                    }
                    graph.addDataProvider(SmartOrganicLayouter.GROUP_NODE_MODE_DATA, nodeMap);
                }

                /*
                if (HierarchyManager.containsGroupNodes(getGraph2D())) {
                    glc = new GroupLayoutConfigurator(getGraph2D());
                    glc.prepareAll();

                    NodeMap nodeId = graph.createNodeMap();
                    NodeMap parentNodeId = graph.createNodeMap();
                    NodeMap groupKey = graph.createNodeMap();
                    graph.addDataProvider(Grouping.NODE_ID_DPKEY, nodeId);
                    graph.addDataProvider(Grouping.PARENT_NODE_ID_DPKEY, parentNodeId);
                    graph.addDataProvider(Grouping.GROUP_DPKEY, groupKey);

                    for (NodeCursor nodeCursor = getGraph2D().nodes(); nodeCursor.ok(); nodeCursor.next()) {
                        Node node = nodeCursor.node();
                        if (HierarchyManager.getInstance(getGraph2D()).isGroupNode(node)) {
                            nGroupNodes++;
                            nodeMap.set(node, SmartOrganicLayouter.GROUP_NODE_MODE_NORMAL);
                        }
                    }
                    graph.addDataProvider(SmartOrganicLayouter.GROUP_NODE_MODE_DATA, nodeMap);
                }


    //mark a node as a group node
    groupKey.setBool(groupNode, true);
    groupKey.setBool(v4, true);
      groupKey.setBool(v3, true);
      groupKey.setBool(v1, true);

    // add ids for each node
    nodeId.set(v1, "v1");
    nodeId.set(v2, "v2");
    nodeId.set(v3, "v3");
    nodeId.set(v4, "v4");
    nodeId.set(groupNode, "groupNode");

    // set the parent for each grouped node

    parentNodeId.set(v2, "v3");
    parentNodeId.set(v3, "v1");
    parentNodeId.set(v1, "v4");
    parentNodeId.set(v4, "groupNode");  */


//                if(nGroupNodes==1)
                module.doLayout(graph);
                cls.doLayout(graph);
                router.doLayout(graph);

                if (glc != null) {
                    glc.restoreAll();
                    graph.removeDataProvider(SmartOrganicLayouter.GROUP_NODE_MODE_DATA);
                }
            }
        };

    }

    public Layouter OrthogonalLayout() {
        //int nodecount = getGraph2D().nodeCount();
        //int edgecount = getGraph2D().nodeCount();
        //float density = (float)edgecount/(float)nodecount;

        OrthogonalLayouter module = new OrthogonalLayouter();
        module.setLayoutStyle(OrthogonalLayouter.FIXED_MIXED_STYLE);
        module.setGrid(25);
        module.setUseSpacePostprocessing(true);
        //module.set
        module.setUseLengthReduction(true);
        module.setUseCrossingPostprocessing(true);
        module.setUseRandomization(false);
        module.setUseSketchDrawing(false);

        module.setLabelLayouter(SALabeling());
        module.setLabelLayouterEnabled(true);

        HierarchyManager hm = HierarchyManager.getInstance(getGraph2D());
        if (hm != null) {
            FixedGroupLayoutStage fgl = new FixedGroupLayoutStage();
            fgl.setInterEdgeRoutingStyle(FixedGroupLayoutStage.ROUTING_STYLE_ORTHOGONAL);
            module.prependStage(fgl);
        }


        return module;
    }

    public Layouter CircularLayout() {
        CircularLayouter module = new CircularLayouter();

        module.setLayoutStyle(CircularLayouter.BCC_ISOLATED);
        module.setSubgraphLayouterEnabled(false);
        module.setMaximalDeviationAngle(90);
        module.setPartitionLayoutStyle(CircularLayouter.PARTITION_LAYOUTSTYLE_ORGANIC);
        SingleCycleLayouter cycleLayouter = module.getSingleCycleLayouter();
        cycleLayouter.setMinimalNodeDistance(30);
        cycleLayouter.setAutomaticRadius(true);

        BalloonLayouter treeLayouter = module.getBalloonLayouter();
        treeLayouter.setPreferredChildWedge(340);
        treeLayouter.setMinimalEdgeLength(40);
        treeLayouter.setCompactnessFactor(0.5);
        treeLayouter.setAllowOverlaps(false);

        module.setLabelLayouter(SALabeling());
        module.setLabelLayouterEnabled(true);

        return module;
    }

    public Layouter HierarchicalLayout() {
        //int nodecount = getGraph2D().nodeCount();
        //int edgecount = getGraph2D().nodeCount();
        //float density = (float)edgecount/(float)nodecount;

        HierarchicLayouter module = new HierarchicLayouter();

        final OrientationLayouter ol = (OrientationLayouter) module.getOrientationLayouter();
        ol.setOrientation(OrientationLayouter.TOP_TO_BOTTOM);

        module.setMinimalLayerDistance(30);
        module.setMinimalNodeDistance(15);
        module.setMinimalEdgeDistance(5);
        module.setMinimalFirstSegmentLength(20);
        module.setMaximalDuration(7000);
        module.setRemoveFalseCrossings(true);
        module.setSameLayerEdgeRoutingOptimizationEnabled(true);
        //module.setLayoutStyle(HierarchicLayouter.POLYLINE);
        //module.setRoutingStyle(HierarchicLayouter.ROUTE_POLYLINE);
        module.setLayeringStrategy(HierarchicLayouter.LAYERING_HIERARCHICAL_OPTIMAL);
        LayerSequencer layerSequencer = module.getLayerSequencer();
        if (layerSequencer instanceof ClassicLayerSequencer) {
            ClassicLayerSequencer cls = (ClassicLayerSequencer) layerSequencer;
            cls.setWeightHeuristic(ClassicLayerSequencer.BARYCENTER_HEURISTIC);
            cls.setUseTransposition(true);
            cls.setRandomizationRounds(40);
        }

        module.setBendReductionThreshold(2);

        module.setLabelLayouter(SALabeling());
        module.setLabelLayouterEnabled(true);

        HierarchyManager hm = HierarchyManager.getInstance(getGraph2D());
        if (hm != null) {
            FixedGroupLayoutStage fixedGroupLayoutStage = new FixedGroupLayoutStage();
            module.prependStage(fixedGroupLayoutStage);
        }
        return module;
    }

    public static SALabeling SALabeling() {
        SALabeling al = new SALabeling();
        al.setProfitModel(new LabelRanking());
        al.setRemoveNodeOverlaps(true);
        al.setRemoveEdgeOverlaps(false);
        al.setPlaceEdgeLabels(true);
        al.setPlaceNodeLabels(true);
        return al;
    }


    protected void mainrun() {

        //hide modulators
        GraphHider hider = new GraphHider(getGraph2D());

        HashSet<Node> gpwmodulator = new HashSet<Node>();

        for (Node node : getGraph2D().getNodeArray()) {
            NodeRealizer nro = getGraph2D().getRealizer(node);
            PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) nro;
            if (nr.getNodeRole() == null) {
                continue;
            }
            if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR)) {
                EdgeCursor ec = node.edges();
                for (int i = 0; i < ec.size(); i++) {
                    Edge edge = ec.edge();
                    Node gpnode = edge.opposite(node);
                    PathCaseShapeNodeRealizer gpnr = (PathCaseShapeNodeRealizer) getGraph2D().getRealizer(gpnode);
                    if (gpnr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS)) {
                        gpwmodulator.add(gpnode);
                    }
                    ec.cyclicNext();
                }

                hider.hide(node);
            }
        }

        //resize processes
        double modulatorheight = 12;
        double modulatorwidth = 60;
        double modulatorxdistance = 20;
        //double modulatorydistance = 5;

        for (Node node : gpwmodulator) {
            PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) getGraph2D().getRealizer(node);
            getGraph2D().setSize(node, nr.getWidth() + modulatorwidth * 2 + modulatorxdistance * 2, nr.getHeight() + modulatorheight * 2);
        }

        //do layouting
        if (sourcenode != null && layouter instanceof HierarchicLayouter) {
            ((HierarchicLayouter) layouter).setLayeringStrategy(HierarchicLayouter.LAYERING_BFS);

            BooleanNodeMap nodemap = new BooleanNodeMap();
            nodemap.setBool(sourcenode, true);
            getGraph2D().addDataProvider(BFSLayerer.CORE_NODES, nodemap);
            launchLayouter(layouter);
            getGraph2D().removeDataProvider(nodemap);
        } else {

            GroupLayoutConfigurator glc;
            HierarchyManager hm = HierarchyManager.getInstance(getGraph2D());
            if (hm == null) {
                launchLayouter(layouter);
            } else if (type.equalsIgnoreCase("organic")) {
                glc = new GroupLayoutConfigurator(getGraph2D());
                glc.prepareAll();
                NodeMap nodeMap = Maps.createHashedNodeMap();
                for (NodeCursor nodeCursor = getGraph2D().nodes(); nodeCursor.ok(); nodeCursor.next()) {
                    Node node = nodeCursor.node();
                    if (HierarchyManager.getInstance(getGraph2D()).isGroupNode(node)) {
                        nodeMap.set(node, SmartOrganicLayouter.GROUP_NODE_MODE_FIX_CONTENTS);
                    }
                }
//                getGraph2D().addDataProvider(SmartOrganicLayouter.GROUP_NODE_MODE_DATA, nodeMap);
//
                launchLayouter(new GroupNodeHider(layouter));
//
                glc.restoreAll();
//                getGraph2D().removeDataProvider(SmartOrganicLayouter.GROUP_NODE_MODE_DATA);
            } else if (type.equalsIgnoreCase("hierarchical") || type.equalsIgnoreCase("orthogonal")) {
                glc = new GroupLayoutConfigurator(getGraph2D());
                glc.prepareAll();

                launchLayouter(layouter);
                glc.restoreAll();
            } else if (type.equalsIgnoreCase("circular")) {
                glc = new GroupLayoutConfigurator(getGraph2D());
                glc.prepareAll();

                launchLayouter(new GroupNodeHider(layouter));
                glc.restoreAll();
            } else {
                launchLayouter(layouter);
            }
        }

        //enable modulators at fixed positions
        hider.unhideAll();

        for (Node node : gpwmodulator) {
            PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) getGraph2D().getRealizer(node);
            getGraph2D().setSize(node, nr.getWidth() - modulatorwidth * 2 - modulatorxdistance * 2, nr.getHeight() - modulatorheight * 2);
        }

        for (Node node : getGraph2D().getNodeArray()) {
            PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) getGraph2D().getRealizer(node);
            if (nr.getNodeRole() == null) {
                continue;
            }
            if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR)) {
                EdgeCursor ec = node.edges();
                for (int i = 0; i < ec.size(); i++) {
                    Edge e = ec.edge();
                    ec.cyclicNext();

                    Node gpnode = e.opposite(node);
                    PathCaseShapeNodeRealizer gpr = (PathCaseShapeNodeRealizer) getGraph2D().getRealizer(gpnode);
                    double gpX = gpr.getX();
                    double gpY = gpr.getY();
                    double gpHeight = gpr.getHeight();
                    double gpWidth = gpr.getWidth();

                    if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR)) {
                        nr.setX(gpX + gpWidth / 2 + modulatorxdistance + modulatorwidth / 2);
                        nr.setY(gpY + gpHeight / 2 - modulatorheight / 2);
                    } else if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN)) {
                        nr.setX(gpX + gpWidth / 2 + modulatorxdistance + modulatorwidth / 2);
                        nr.setY(gpY - modulatorheight);
                    } else if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)) {
                        nr.setX(gpX + gpWidth / 2 + modulatorxdistance + modulatorwidth / 2);
                        nr.setY(gpY + gpHeight);
                    } else if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR)) {
                        nr.setX(gpX - gpWidth / 2 - modulatorwidth / 2 - modulatorxdistance);
                        nr.setY(gpY + gpHeight / 2 - modulatorheight / 2);
                    } else if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR)) {
                        nr.setX(gpX - gpWidth / 2 - modulatorwidth / 2 - modulatorxdistance);
                        nr.setY(gpY - modulatorheight);
                    } else if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR)) {
                        nr.setX(gpX - gpWidth / 2 - modulatorwidth / 2 - modulatorxdistance);
                        nr.setY(gpY + gpHeight);
                    }

                }

            }


        }

        for (Edge edge : getGraph2D().getEdgeArray()) {
            EdgeRealizer er = getGraph2D().getRealizer(edge);
            Node source = edge.source();
            PathCaseShapeNodeRealizer sourcer = (PathCaseShapeNodeRealizer) getGraph2D().getRealizer(source);
            Node target = edge.target();
            PathCaseShapeNodeRealizer targetr = (PathCaseShapeNodeRealizer) getGraph2D().getRealizer(target);


            if (sourcer.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS)) {
                if (targetr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR) || targetr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN) || targetr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT) || targetr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR) || targetr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR) || targetr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR)) {
                    er.clearBends();
                    //er.setSourcePoint(new YPoint((int)(sourcer.getCenterX()-sourcer.getWidth()/2),sourcer.getCenterY()));
                } else
                if (targetr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT) || targetr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON)) {
                    YList pp = getGraph2D().getPathList(edge);
                    pp.remove(0);
                    pp.remove(pp.size() - 1); //new
                    pp.addFirst(new YPoint(sourcer.getCenterX(), sourcer.getCenterY()));
                    pp.addLast(new YPoint(targetr.getCenterX(), targetr.getCenterY()));//new
                    getGraph2D().setPath(edge, pp);
                }
            } else if (targetr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS)) {
                if (sourcer.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR) || sourcer.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN) || sourcer.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT) || sourcer.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR) || sourcer.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR) || sourcer.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR)) {
                    er.clearBends();
                    //er.setTargetPoint(new YPoint((int)(targetr.getCenterX()-targetr.getWidth()/2),targetr.getCenterY()));
                } else
                if (sourcer.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT) || sourcer.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON)) {
                    YList pp = getGraph2D().getPathList(edge);
                    pp.remove(0);
                    pp.remove(pp.size() - 1);
                    pp.addFirst(new YPoint(sourcer.getCenterX(), sourcer.getCenterY()));//new
                    pp.addLast(new YPoint(targetr.getCenterX(), targetr.getCenterY()));
                    getGraph2D().setPath(edge, pp);
                }
            }
        }

        for (Node node : getGraph2D().getNodeArray()) {
            PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) getGraph2D().getRealizer(node);
            if (nr.getNodeRole() == null) {
                //do nothing
            } else
            if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR)) {
                nr.getLabel().setModel(NodeLabel.INTERNAL);
                nr.getLabel().setPosition(NodeLabel.CENTER);
            } else if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.PATHWAY_BOX)) {
                //getGraph2D().hide(node);
            }
        }

        //launchLayouter(SALabeling());
    }
}

