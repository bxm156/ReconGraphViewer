package binevi.View;

import y.base.Edge;
import y.base.Node;
import y.module.YModule;
import y.option.OptionHandler;
import y.view.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DefaultYGraphViewer {

    //The view component of this viewer.
    public Graph2DView view;

    //Mode variables
    public boolean ISDELETIONENABLED = false;
    public boolean PATHCASEQUERYINGENABLED = true;

              
    //Graph modes
    public static enum GRAPH_MODE {
        EDIT, PAN, INTERACTIVE_ZOOM, AREA_ZOOM, MAGNIFIER
    }

    //Access to container GUI
    PathCaseViewer pathCaseGUI;

    //instantiate yfiles related objects
    public void initializeGraphViewerComponents() {
        view = new Graph2DView();
        view.setAntialiasedPainting(true);
        //resetGraphHider();
        enableBridgesForEdgePaths();

    }

    //uncomment this method if we want to enable bridgeing
    protected void enableBridgesForEdgePaths() {
        /*Graph2DRenderer gr = view.getGraph2DRenderer();
        if (gr instanceof DefaultGraph2DRenderer)
        {
          DefaultGraph2DRenderer dgr = (DefaultGraph2DRenderer)gr;
          // If there is no BridgeCalculator instance set, ...
          if (dgr.getBridgeCalculator() == null)
          {
            // ... then register a newly created one that uses default settings.
            BridgeCalculator calculator = new BridgeCalculator();
            dgr.setBridgeCalculator(calculator);
            calculator.setCrossingStyle(BridgeCalculator.CROSSING_STYLE_TWO_SIDES);
          }
        }*/
    }


    public DefaultYGraphViewer(PathCaseViewer pathCaseGUI) {
        view = new Graph2DView();
        view.setAntialiasedPainting(true);
        this.pathCaseGUI = pathCaseGUI;
    }

    /////////////////////////    VIEW MODE OPERATIONS  //////////////////////////////////

    private static NavigationMode navigationMode;
    private static AutoDragViewMode autoDragViewMode;
    private static AreaZoomMode areaZoomMode;
    private static ToolTipEditMode editMode;
    private static InteractiveZoomMode interactiveZoomMode;
    private static PopupMenuMode popupMode;
    private static MagnifierViewMode magnifyingMode;
    private static TooltipViewMode tooltipViewMode;  // added by En in Dec. 01, 2007


    public void resetView() {
        //view.removeAll();
        view.setGraph2D(new Graph2D());
        //initializeGraphViewerComponents();

    }

    //view actions
    protected void registerViewActions() {
        //register keyboard actions
        Graph2DViewActions actions = new Graph2DViewActions(view);
        ActionMap amap = actions.createActionMap();
        InputMap imap = actions.createDefaultInputMap(amap);
        if (!ISDELETIONENABLED) {
            amap.remove(Graph2DViewActions.DELETE_SELECTION);
        }
        view.getCanvasComponent().setActionMap(amap);
        view.getCanvasComponent().setInputMap(JComponent.WHEN_FOCUSED, imap);
    }

    protected void setViewMode(GRAPH_MODE mode) {
        removeCurrentViewModes();
        createViewModes();
        switch (mode) {
            case PAN:
                view.addViewMode(navigationMode);
                view.addViewMode(tooltipViewMode); // added by En
                view.addViewMode(autoDragViewMode);
                break;
            case EDIT:
                view.addViewMode(editMode);
                view.addViewMode(autoDragViewMode);
                break;
            case INTERACTIVE_ZOOM:
                view.addViewMode(interactiveZoomMode);
                view.addViewMode(popupMode);
                view.addViewMode(tooltipViewMode); // added by En
                break;
            case AREA_ZOOM:
                view.addViewMode(areaZoomMode);
                view.addViewMode(popupMode);
                view.addViewMode(tooltipViewMode); // added by En
                break;
            case MAGNIFIER:
                view.addViewMode(magnifyingMode);
                view.addViewMode(editMode);
                view.addViewMode(autoDragViewMode);
                break;
        }

    }

    private void removeCurrentViewModes() {
        if (navigationMode != null) view.removeViewMode(navigationMode);
        if (autoDragViewMode != null) view.removeViewMode(autoDragViewMode);
        if (areaZoomMode != null) view.removeViewMode(areaZoomMode);
        if (editMode != null) view.removeViewMode(editMode);
        if (interactiveZoomMode != null) view.removeViewMode(interactiveZoomMode);
        if (popupMode != null) view.removeViewMode(popupMode);
        if (magnifyingMode != null) view.removeViewMode(magnifyingMode);
        if (tooltipViewMode != null) view.removeViewMode(tooltipViewMode); //added by En
    }

    class ToolTipEditMode extends EditMode {

        protected String getNodeTip(Node node) {
            return getNodeTipText(node);
        }
    }

    // Author: En Cheng
    // Date: Dec. 01, 2007
    // Main function: show tooltips in all view modes
    class TooltipViewMode extends ViewMode {

        public TooltipViewMode() {
            super();
        }

        public void mouseMoved(double x, double y) {
            String tipText = null;
            HitInfo hitInfo = getHitInfo(x, y);

            if (hitInfo.getHitNode() != null) {
                tipText = getNodeTip(hitInfo.getHitNode());
            } else {
                tipText = null;
            }

            view.setToolTipText(tipText);
        }

        public String getNodeTip(Node v) {

            // return getGraph2D().getLabelText(v);

            return getNodeTipText(v);
        }

    }

    class PopupMenuMode extends PopupMode {

        public PopupMenuMode() {
            super();

        }

        public JPopupMenu getBendPopup(Bend b) {
            return getBendPopupMenu(b);
        }

        public JPopupMenu getEdgeLabelPopup(EdgeLabel label) {
            return getEdgeLabelPopupMenu(label);
        }

        public JPopupMenu getEdgePopup(Edge e) {
            return getEdgePopupMenu(e);
        }

        public JPopupMenu getNodePopup(Node v) {
            return getNodePopupMenu(v);
        }

        public JPopupMenu getPaperPopup(double x, double y) {
            return getPaperPopupMenu(x, y);
        }

        public JPopupMenu getSelectionPopup(double x, double y) {
            return getSelectionPopupMenu(x, y);
        }
    }

    class InteractiveZoomMode extends ViewMode {

        double initialViewX, initialViewY;
        double initialWorldX, initialWorldY;
        double initialZoomAmount;

        public void mousePressedLeft(double x, double y) {
            initialViewX = toViewCoordX(x);
            initialViewY = toViewCoordY(y);
            initialWorldX = x;
            initialWorldY = y;
            initialZoomAmount = view.getZoom();
        }

        public void mouseDraggedLeft(double x, double y) {
            double draggedviewx = toViewCoordX(x);
            double draggedviewy = toViewCoordY(y);

            double mouseXMovement = (draggedviewx - initialViewX);
            double mouseYMovement = (draggedviewy - initialViewY);
            double mouseMovement = Math.sqrt(mouseXMovement * mouseXMovement + mouseYMovement * mouseYMovement);
            if (mouseYMovement < 0) mouseMovement *= -1;

            double nextzoom = initialZoomAmount * (1 + mouseMovement * 0.01f);

            if (nextzoom > 0) {
                view.setZoom(nextzoom);
                double neworiginX = view.getViewPoint2D().getX() - toWorldCoordX((int) initialViewX) + initialWorldX;
                double neworiginY = view.getViewPoint2D().getY() - toWorldCoordY((int) initialViewY) + initialWorldY;

                view.setViewPoint2D(neworiginX, neworiginY);
                view.updateView();

            }


        }

        // From world to view coordinates...
        int toViewCoordX(double x) {
            return (int) ((x - view.getViewPoint2D().getX()) * view.getZoom());
        }

        int toViewCoordY(double y) {
            return (int) ((y - view.getViewPoint2D().getY()) * view.getZoom());
        }

        // ... and vice-versa.
        double toWorldCoordX(int x) {
            return x / view.getZoom() + view.getViewPoint2D().getX();
        }

        double toWorldCoordY(int y) {
            return y / view.getZoom() + view.getViewPoint2D().getY();
        }


    }

    private void createViewModes() {
        if (popupMode == null) {
            popupMode = new PopupMenuMode();
        }
        if (navigationMode == null) {
            navigationMode = new NavigationMode();
            navigationMode.setPopupMode(popupMode);
        }
        if (magnifyingMode == null) {
            magnifyingMode = new MagnifierViewMode();
        }
        if (autoDragViewMode == null) {
            autoDragViewMode = new AutoDragViewMode();
        }
        if (areaZoomMode == null) {
            areaZoomMode = new AreaZoomMode();
        }
        if (editMode == null) {
            editMode = new ToolTipEditMode();
            //todo deletion?
            editMode.allowResizeNodes(false);
            editMode.allowNodeEditing(false);
            editMode.allowNodeCreation(false);
            editMode.allowBendCreation(true);
            editMode.allowEdgeCreation(false);
            editMode.setMixedSelectionEnabled(true);
            //editMode.allowNodeEditing(true);
            editMode.showNodeTips(true);
            editMode.setPopupMode(popupMode);
        }
        if (interactiveZoomMode == null) {
            interactiveZoomMode = new InteractiveZoomMode();
        }
        if (tooltipViewMode == null) {
            tooltipViewMode = new TooltipViewMode();
        }
    }

    protected void destroyViewModes() {
        navigationMode = null;
        autoDragViewMode = null;
        areaZoomMode = null;
        editMode = null;
        interactiveZoomMode = null;
        popupMode = null;
        magnifyingMode = null;
        tooltipViewMode = null;
    }

    //Instantiates and registers the listeners for the view.
    protected void registerViewListeners() {
        //Note that mouse wheel support requires J2SE 1.4 or higher.
        view.getCanvasComponent().addMouseWheelListener(new Graph2DViewMouseWheelZoomListener());
    }

    //////////////////////////    TOOLTIPS   //////////////////////////

    protected String getNodeTipText(Node v) {

        return pathCaseGUI.getNodeTipText(v);

    }

    /////////////////////////   RIGHT CLICK MENUS /////////////////////

    protected JPopupMenu getSelectionPopupMenu(double x, double y) {
        return null;
    }

    protected JPopupMenu getNodePopupMenu(final Node v) {

        JPopupMenu nodePopup = new JPopupMenu();

        JMenu menu = new JMenu("Layout Options");

        JMenuItem item = new JMenuItem("Apply Neighbor Hierarchy");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                makeNeighhborhoodHierarchical(v);
            }
        });
        menu.add(item);
        nodePopup.add(menu);
        /*JMenu menuitem = new JMenu("Graph Options");

        item = new JMenuItem("Hide node");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                graphider.hide(v);

                overviewScrollPane.getViewport().removeAll();
                overviewScrollPane.getViewport().add(createOverviewTreeFromView());
                overviewScrollPane.repaint();
                //add hidden nodes as unselected
                view.updateView();
                overviewPane.updateUI();
            }
        });
        menuitem.add(item);
        nodePopup.add(menuitem);*/


        if (!PATHCASEQUERYINGENABLED)
            return nodePopup;

        menu = pathCaseGUI.getPathCaseNodePopupQueries(v);

        if (menu != null) nodePopup.add(menu);
        return nodePopup;
    }

    protected JPopupMenu getPaperPopupMenu(double x, double y) {

        JPopupMenu nodePopup = new JPopupMenu();
        if (!PATHCASEQUERYINGENABLED)
            return nodePopup;

        JMenu menu = pathCaseGUI.getPathCasePaperPopupQueries();
        if (menu != null)
            nodePopup.add(menu);
        return nodePopup;

    }

    protected JPopupMenu getEdgePopupMenu(Edge e) {
        return null;
    }

    protected JPopupMenu getEdgeLabelPopupMenu(EdgeLabel label) {
        return null;
    }

    protected JPopupMenu getBendPopupMenu(final Bend b) {
        JPopupMenu nodePopup = new JPopupMenu();
        JMenuItem item = new JMenuItem("Remove Bend");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                view.getGraph2D().getRealizer(b.getEdge()).removeBend(b);//view.remove(b);
                view.updateView();
            }
        });
        nodePopup.add(item);


        return nodePopup;
    }

    ////////////////////////    LAYOUT OPERATIONS    ///////////////////////////////

    public boolean makeNeighhborhoodHierarchical(String sourceNodeId) {

        Node v = pathCaseGUI.getNodeByPathCaseID(sourceNodeId);

        if (v != null) {
            makeNeighhborhoodHierarchical(v);
            return true;
        } else
            return false;
    }

    public void makeNeighhborhoodHierarchical(Node v) {

        (new PathCaseLayouter("hierarchical", v)).start(view.getGraph2D());

        /*
        HierarchicLayouter layouter = new HierarchicLayouter();

        //TODO: MAKE THREADED LAYOUTING
        //createLoaderDialog("Applying layout...");

        //layouter.setLayerer(new NeighborLayerer(v));
        layouter.setLayeringStrategy(HierarchicLayouter.LAYERING_BFS);

        BooleanNodeMap nodemap = new BooleanNodeMap();
        nodemap.setBool(v, true);
        view.getGraph2D().addDataProvider(BFSLayerer.CORE_NODES, nodemap);
        layouter.doLayout(view.getGraph2D());
        //System.out.println(view.getGraph2D().getLabelText(v));

        (new SALabeling()).label(view.getGraph2D());

        view.fitContent();
        view.updateView();
        */

        //killLoaderDialog();
    }

    public void bestLayout() {
        int nodecount = view.getGraph2D().nodeCount();
        int edgecount = view.getGraph2D().edgeCount();
        float density = (float) edgecount / (float) nodecount;

        String layout;

        if (density > 3) layout = "circular";
        else if (nodecount < 10 && density < 1.5f) layout = "orthogonal";
        else if (nodecount < 40) layout = "hierarchical";
        else layout = "organic";

        (new PathCaseLayouter(layout)).start(view.getGraph2D());

        view.fitContent();
        view.updateView();
    }

    public void initialLayout(String layout, String sourceNode) {

        //view pre-layout
        if (view == null || view.getGraph2D() == null) return;

        //TODO MAKE THREADED
        // createLoaderDialog("Applying initial layout...");

        if (layout == null || layout.equals("")) {
            boolean largelayout = view.getGraph2D().nodeCount() > 50 || view.getGraph2D().edgeCount() > 150;

            if (largelayout) {
                //OrganicLayoutModule module = new OrganicLayoutModule();
                //module.start(view.getGraph2D());
                (new PathCaseLayouter("organic")).start(view.getGraph2D());
            } else {
                //HierarchicLayoutModule module = new HierarchicLayoutModule();
                //module.start(view.getGraph2D());
                (new PathCaseLayouter("hierarchical")).start(view.getGraph2D());
            }
        } else if (layout.equals("hierarchical")) {
            if (sourceNode == null || sourceNode.equals("") || !makeNeighhborhoodHierarchical(sourceNode)) {
                //HierarchicLayoutModule module = new HierarchicLayoutModule();
                //module.start(view.getGraph2D());
                (new PathCaseLayouter("hierarchical")).start(view.getGraph2D());
            } else {
                //initialLayout(null,null);
            }
        } else if (layout.equals("organic")) {
            //OrganicLayoutModule module = new OrganicLayoutModule();
            //module.start(view.getGraph2D());
            (new PathCaseLayouter("organic")).start(view.getGraph2D());
        }

        //(new SALabeling()).label(view.getGraph2D());

        view.fitContent();
        view.updateView();


    }

    public void applyGraphLayout(YModule module) {


        OptionHandler op = module.getOptionHandler();
        if (op != null) {
            if (!op.showEditor())
                return;
        }

        //TODO: make threaded
        //createLoaderDialog("Applying layout...");

        module.startAsThread(view.getGraph2D());

        //TODO: make threaded
        //killLoaderDialog();
    }

    public JPopupMenu getLayoutMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenu customlayoutsmenu = getCustomLayoutMenu();
        menu.add(customlayoutsmenu);

        JMenu pathCaseMenu = new JMenu("PathCase Predefined Layouts");
        menu.add(pathCaseMenu);

        JMenuItem item = new JMenuItem("Organic");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                (new PathCaseLayouter("organic")).start(view.getGraph2D());
                view.updateView();
                view.fitContent();
            }
        });
        pathCaseMenu.add(item);

        item = new JMenuItem("Hierarchical");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                (new PathCaseLayouter("hierarchical")).start(view.getGraph2D());
                view.updateView();
                view.fitContent();
            }
        });
        pathCaseMenu.add(item);

        item = new JMenuItem("Orthogonal");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                (new PathCaseLayouter("orthogonal")).start(view.getGraph2D());
                view.updateView();
                view.fitContent();
            }
        });
        pathCaseMenu.add(item);

        item = new JMenuItem("Circular");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                (new PathCaseLayouter("circular")).start(view.getGraph2D());
                view.updateView();
                view.fitContent();
            }
        });
        pathCaseMenu.add(item);

        item = new JMenuItem("Choose Best Layout");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                bestLayout();
            }
        });
        menu.add(item);

        return menu;


    }

    protected JMenu getCustomLayoutMenu() {

        JMenu layoutmenu = new JMenu("Custom Layout Algorithms");

        JMenu layouters = new JMenu("Node Layouters");
        layoutmenu.add((layouters));
        layouters.add(new LaunchModule(new y.module.CircularLayoutModule(), "Circular"));
        layouters.add(new LaunchModule(new y.module.DirectedOrthogonalLayoutModule(), "DirectedOrthogonal"));
        layouters.add(new LaunchModule(new y.module.GRIPModule(), "GRIP"));
        layouters.add(new LaunchModule(new y.module.HierarchicLayoutModule(), "Hierarchic"));
        layouters.add(new LaunchModule(new y.module.IncrementalHierarchicLayoutModule(), "Incremental Hierarchical"));
        layouters.add(new LaunchModule(new y.module.OrganicLayoutModule(), "Organic"));
        layouters.add(new LaunchModule(new y.module.OrthogonalLayoutModule(), "Orthogonal"));
        layouters.add(new LaunchModule(new y.module.CompactOrthogonalLayoutModule(), "Compact Orthogonal"));
        //layouters.add(new LaunchModule(new y.module.ParallelEdgeLayoutModule(), "Parallel Edge"));
        layouters.add(new LaunchModule(new y.module.RandomLayoutModule(), "Random"));
        layouters.add(new LaunchModule(new y.module.ShuffleLayoutModule(), "Shuffle"));
        layouters.add(new LaunchModule(new y.module.SmartOrganicLayoutModule(), "Smart Organic"));
        layouters.add(new LaunchModule(new y.module.TreeLayoutModule(), "Tree"));
        //layouters.add(new LaunchModule(new y.module.ComponentLayoutModule(), "Component Layout"));

        JMenu edgerouters = new JMenu("Edge Routers");
        layoutmenu.add((edgerouters));
        edgerouters.add(new LaunchModule(new y.module.OrthogonalEdgeRouterModule(), "Orthogonal"));
        edgerouters.add(new LaunchModule(new y.module.OrganicEdgeRouterModule(), "Organic"));

        /*JMenu constraints = new JMenu ("Constraints");
    layoutmenu.add((constraints));
    constraints.add(new LaunchModule(new y.module.EdgeGroupConstraintModule(), "Edge Grouping"));
    constraints.add(new LaunchModule(new y.module.PortConstraintModule(), "Porting")); */

        layoutmenu.add(new LaunchModule(new y.module.LabelingModule(), "Labeler"));

        layoutmenu.add(new LaunchModule(new y.module.GraphTransformerModule(), "Transformer"));


        return layoutmenu;
    }

    //Launches a generic YModule. If the modules provides an option handler display it before the modules gets launched.
    class LaunchModule extends AbstractAction {
        YModule module;

        LaunchModule(YModule module, String title) {
            super(title);
            this.module = module;
        }

        public void actionPerformed(ActionEvent e) {
            applyGraphLayout(module);
        }
    }


}
