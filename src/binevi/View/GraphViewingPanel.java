package binevi.View;

import binevi.IO.SXGIOHandler;
import binevi.View.Layout.BooleanNodeMap;
import y.base.DataProvider;
import y.base.Edge;
import y.base.Node;
import y.io.GMLIOHandler;
import y.io.JPGIOHandler;
import y.io.YGFIOHandler;
import y.layout.LabelRanking;
import y.layout.hierarchic.BFSLayerer;
import y.layout.hierarchic.HierarchicLayouter;
import y.layout.labeling.SALabeling;
import y.module.HierarchicLayoutModule;
import y.module.OrganicLayoutModule;
import y.module.YModule;
import y.option.OptionHandler;
import y.util.D;
import y.util.GraphHider;
import y.view.*;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.IOException;

//A Generic Viewer Panel
public class GraphViewingPanel extends JPanel {


    public JToolBar graphOptionsToolbar;

    //Mode variables
    public boolean ISDELETIONENABLED = false;


    //Graph modes
    public static enum GRAPH_MODE {
        EDIT, PAN, INTERACTIVE_ZOOM, AREA_ZOOM, MAGNIFIER
    }

    /**
     * The view component of this viewer.
     */
    protected Graph2DView view;

    protected final JPanel contentPane;

    protected JSplitPane overviewSplitPane;

    JPanel overviewPane;

    protected JSplitPane extraSplitPane;

    JToolBar itemsToolBar;
    JToolBar statusBar;
    JLabel statusBarText;


    /**
     * A MouseListener that listens for double click events on a JTree.
     * The node item that was clicked will be focused in an
     * associated Graph2DView.
     */

    class JTreeDoubleClickListener extends MouseAdapter {

        Graph2DView view;

        public JTreeDoubleClickListener(Graph2DView view) {
            this.view = view;
        }

        public void mouseClicked(MouseEvent e) {
            JTree tree = (JTree) e.getSource();

            if (e.getClickCount() == 2) {
                //D.bug("right mouse pressed");

                int y = e.getY();
                int x = e.getX();
                TreePath path = tree.getPathForLocation(x, y);
                if (path != null) {
                    Object last = path.getLastPathComponent();
                    Graph2D focusedGraph = null;
                    Node v = null;

                    if (last instanceof Node) {
                        v = (Node) last;
                        focusedGraph = (Graph2D) v.getGraph();
                    } else if (last instanceof Graph2D) //root
                    {
                        focusedGraph = (Graph2D) last;
                    }

                    if (focusedGraph != null) {
                        view.setGraph2D(focusedGraph);
                        if (v != null) {
                            view.setCenter(focusedGraph.getCenterX(v), focusedGraph.getCenterY(v));
                            //view.getGraph2D().setSelected(v,!view.getGraph2D().isSelected(v));
                        }
                        view.updateView();
                    }
                }
            }
        }

    }

    JScrollPane overviewScrollPane;

    GraphHider graphider;

    public JTree createOverviewTreeFromView() {
        TreeView treeView = new TreeView(view.getGraph2D());
        JTree tree = treeView.getJTree();
        //add a navigational action to the tree.
        tree.addMouseListener(new JTreeDoubleClickListener(view));
        tree.setEditable(false);
        //display the graph hierarchy in a special JTree
        return tree;
    }

    public void resetGraphHider() {
        graphider = new GraphHider(view.getGraph2D());
    }


    protected GraphViewingPanel() {
        view = new Graph2DView();
        view.setAntialiasedPainting(true);
        resetGraphHider();
        enableBridgesForEdgePaths();

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        itemsToolBar = createMainToolBar();
        statusBar = createStatusBar();
        contentPane.add(itemsToolBar, BorderLayout.NORTH);
        contentPane.add(statusBar, BorderLayout.NORTH);

        JTree overviewtree = createOverviewTreeFromView();
        //add a navigational action to the tree.

        //plug the gui elements together and add them to the pane
        overviewScrollPane = new JScrollPane(overviewtree);
        overviewScrollPane.setPreferredSize(new Dimension(150, 0));
        overviewPane = new JPanel(new BorderLayout());

        Overview overView = new Overview(view);
        overView.setPreferredSize(new Dimension(150, 150));
        overviewPane.add(overView, BorderLayout.NORTH);
        overviewPane.add(overviewScrollPane);

        overviewSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, view);
        overviewSplitPane.setEnabled(false);
        overviewSplitPane.setDividerLocation(0);
        overviewSplitPane.setContinuousLayout(true);

        extraSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, overviewSplitPane, null);
        extraSplitPane.setEnabled(false);
        extraSplitPane.setDividerLocation(extraSplitPane.getWidth());
        extraSplitPane.setContinuousLayout(true);

        //contentPane.add(view, BorderLayout.CENTER);
        contentPane.add(extraSplitPane, BorderLayout.CENTER);

        setViewMode(GRAPH_MODE.EDIT);
        registerViewActions();
        registerViewListeners();


    }

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

    /**
     * Creates a toolbar for this base.
     *
     * @return a toolbar
     */
    protected JToolBar createMainToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        //toolBar.setBackground(Color.white);
        //toolBar.setForeground(Color.white);
        //toolBar.setMargin(new Insets(-5,-5,-5,-5));
        final JButton fileOperations = new JButton();
        toolBar.add(fileOperations);
        fileOperations.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/fileIcon.GIF")));
        fileOperations.setToolTipText("File Operations");
        fileOperations.setMaximumSize(new java.awt.Dimension(21, 21));
        fileOperations.setMinimumSize(new java.awt.Dimension(21, 21));
        fileOperations.setPreferredSize(new java.awt.Dimension(21, 21));
        fileOperations.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedfileIcon.GIF")));
        fileOperations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showFilePopupMenu(fileOperations);
            }
        });
        toolBar.addSeparator();

        ButtonGroup graphModeButtonGroup = new ButtonGroup();

        JToggleButton editingModeButton = new JToggleButton();
        toolBar.add(editingModeButton);
        graphModeButtonGroup.add(editingModeButton);
        editingModeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/editIcon.GIF")));
        editingModeButton.setToolTipText("Editing Tool");
        editingModeButton.setSelected(true);
        editingModeButton.setMaximumSize(new java.awt.Dimension(21, 21));
        editingModeButton.setMinimumSize(new java.awt.Dimension(21, 21));
        editingModeButton.setPreferredSize(new java.awt.Dimension(21, 21));
        editingModeButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressededitIcon.GIF")));
        editingModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setViewMode(GRAPH_MODE.EDIT);
            }
        });

        JToggleButton magnifyingModeButton = new JToggleButton();
        toolBar.add(magnifyingModeButton);
        graphModeButtonGroup.add(magnifyingModeButton);
        magnifyingModeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/magnifierIcon.GIF")));
        magnifyingModeButton.setToolTipText("Magnifying Tool");
        magnifyingModeButton.setMaximumSize(new java.awt.Dimension(21, 21));
        magnifyingModeButton.setMinimumSize(new java.awt.Dimension(21, 21));
        magnifyingModeButton.setPreferredSize(new java.awt.Dimension(21, 21));
        magnifyingModeButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedmagnifierIcon.GIF")));
        magnifyingModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setViewMode(GRAPH_MODE.MAGNIFIER);
            }
        });

        JToggleButton panningModeButton = new JToggleButton();
        toolBar.add(panningModeButton);
        graphModeButtonGroup.add(panningModeButton);
        panningModeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/panIcon.GIF")));
        panningModeButton.setToolTipText("Panning Tool");
        panningModeButton.setMaximumSize(new java.awt.Dimension(21, 21));
        panningModeButton.setMinimumSize(new java.awt.Dimension(21, 21));
        panningModeButton.setPreferredSize(new java.awt.Dimension(21, 21));
        panningModeButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedpanIcon.GIF")));
        panningModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setViewMode(GRAPH_MODE.PAN);
            }
        });

        JToggleButton interactiveZoomModeButton = new JToggleButton();
        toolBar.add(interactiveZoomModeButton);
        graphModeButtonGroup.add(interactiveZoomModeButton);
        interactiveZoomModeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/interactiveZoomIcon.GIF")));
        interactiveZoomModeButton.setToolTipText("Interactive Zooming Tool");
        interactiveZoomModeButton.setMaximumSize(new java.awt.Dimension(21, 21));
        interactiveZoomModeButton.setMinimumSize(new java.awt.Dimension(21, 21));
        interactiveZoomModeButton.setPreferredSize(new java.awt.Dimension(21, 21));
        interactiveZoomModeButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedinteractiveZoomIcon.GIF")));
        interactiveZoomModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setViewMode(GRAPH_MODE.INTERACTIVE_ZOOM);
            }
        });

        JToggleButton areaZoomModeButton = new JToggleButton();
        toolBar.add(areaZoomModeButton);
        graphModeButtonGroup.add(areaZoomModeButton);
        areaZoomModeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/marqueeZoomIcon.GIF")));
        areaZoomModeButton.setToolTipText("Area Zooming Tool");
        areaZoomModeButton.setMaximumSize(new java.awt.Dimension(21, 21));
        areaZoomModeButton.setMinimumSize(new java.awt.Dimension(21, 21));
        areaZoomModeButton.setPreferredSize(new java.awt.Dimension(21, 21));
        areaZoomModeButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedmarqueeZoomIcon.GIF")));
        areaZoomModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setViewMode(GRAPH_MODE.AREA_ZOOM);
            }
        });
        toolBar.addSeparator();

        if (ISDELETIONENABLED) {
            JButton deleteSelectionButton = new JButton();
            toolBar.add(deleteSelectionButton);
            deleteSelectionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/deleteIcon.GIF")));
            deleteSelectionButton.setToolTipText("Delete Selection");
            deleteSelectionButton.setMaximumSize(new java.awt.Dimension(21, 21));
            deleteSelectionButton.setMinimumSize(new java.awt.Dimension(21, 21));
            deleteSelectionButton.setPreferredSize(new java.awt.Dimension(21, 21));
            deleteSelectionButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/presseddeleteIcon.GIF")));
            deleteSelectionButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    view.getGraph2D().removeSelection();
                    view.getGraph2D().updateViews();
                }
            });
        }


        JButton pointZoomInButton = new JButton();
        toolBar.add(pointZoomInButton);
        pointZoomInButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pointZoomInIcon.GIF")));
        pointZoomInButton.setToolTipText("Point Zoom In");
        pointZoomInButton.setMaximumSize(new java.awt.Dimension(21, 21));
        pointZoomInButton.setMinimumSize(new java.awt.Dimension(21, 21));
        pointZoomInButton.setPreferredSize(new java.awt.Dimension(21, 21));
        pointZoomInButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedpointZoomInIcon.GIF")));
        pointZoomInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view.setZoom(view.getZoom() * 1.2);
                //optional code that adjusts the size of the
                //view's world rectangle. The world rectangle
                //defines the region of the canvas that is
                //accessible by using the scrollbars of the view.
                Rectangle box = view.getGraph2D().getBoundingBox();
                view.setWorldRect(box.x - 20, box.y - 20, box.width + 40, box.height + 40);

                view.updateView();
            }
        });

        JButton pointZoomOutButton = new JButton();
        toolBar.add(pointZoomOutButton);
        pointZoomOutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pointZoomOutIcon.GIF")));
        pointZoomOutButton.setToolTipText("Point Zoom Out");
        pointZoomOutButton.setMaximumSize(new java.awt.Dimension(21, 21));
        pointZoomOutButton.setMinimumSize(new java.awt.Dimension(21, 21));
        pointZoomOutButton.setPreferredSize(new java.awt.Dimension(21, 21));
        pointZoomOutButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedpointZoomOutIcon.GIF")));
        pointZoomOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view.setZoom(view.getZoom() * 0.8);
                //optional code that adjusts the size of the
                //view's world rectangle. The world rectangle
                //defines the region of the canvas that is
                //accessible by using the scrollbars of the view.
                Rectangle box = view.getGraph2D().getBoundingBox();
                view.setWorldRect(box.x - 20, box.y - 20, box.width + 40, box.height + 40);

                view.updateView();
            }
        });

        JButton fitToViewButton = new JButton();
        toolBar.add(fitToViewButton);
        fitToViewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/zoomtofitIcon.GIF")));
        fitToViewButton.setToolTipText("Fit Content");
        fitToViewButton.setMaximumSize(new java.awt.Dimension(34, 21));
        fitToViewButton.setMinimumSize(new java.awt.Dimension(34, 21));
        fitToViewButton.setPreferredSize(new java.awt.Dimension(34, 21));
        fitToViewButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedzoomtofitIcon.GIF")));
        fitToViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view.fitContent();
                view.updateView();
            }
        });

        JButton normalSizedviewButton = new JButton();
        toolBar.add(normalSizedviewButton);
        normalSizedviewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/zoomtoNormalIcon.GIF")));
        normalSizedviewButton.setToolTipText("Zoom to Normal Size");
        normalSizedviewButton.setMaximumSize(new java.awt.Dimension(34, 21));
        normalSizedviewButton.setMinimumSize(new java.awt.Dimension(34, 21));
        normalSizedviewButton.setPreferredSize(new java.awt.Dimension(34, 21));
        normalSizedviewButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedzoomtoNormalIcon.GIF")));
        normalSizedviewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view.setZoom(1.0);
                //view.setWorldRect((int)view.getWorldRect().getWidth()/2, (int)view.getWorldRect().getHeight()/2, (int)view.getWorldRect().getWidth(), (int)view.getWorldRect().getHeight());
                view.updateView();
            }
        });

        final JButton showLayoutOptionsButton = new JButton();
        toolBar.add(showLayoutOptionsButton);
        showLayoutOptionsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/layoutChangeIcon.GIF")));
        showLayoutOptionsButton.setToolTipText("Zoom to Normal Size");
        showLayoutOptionsButton.setMaximumSize(new java.awt.Dimension(34, 21));
        showLayoutOptionsButton.setMinimumSize(new java.awt.Dimension(34, 21));
        showLayoutOptionsButton.setPreferredSize(new java.awt.Dimension(34, 21));
        showLayoutOptionsButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedlayoutChangeIcon.GIF")));
        showLayoutOptionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLayoutPopupMenu(showLayoutOptionsButton);
            }
        });

        final JToggleButton minimapButton = new JToggleButton();
        toolBar.add(minimapButton);
        minimapButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/minimapIcon.GIF")));
        minimapButton.setToolTipText("Display/Hide Minimap");
        minimapButton.setMaximumSize(new java.awt.Dimension(34, 21));
        minimapButton.setMinimumSize(new java.awt.Dimension(34, 21));
        minimapButton.setPreferredSize(new java.awt.Dimension(34, 21));
        minimapButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedminimapIcon.GIF")));
        minimapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (view != null && overviewSplitPane != null && overviewPane != null) {
                    if (minimapButton.isSelected()) {
                        overviewSplitPane.setLeftComponent(overviewPane);
                        overviewSplitPane.setDividerLocation(overviewPane.getPreferredSize().width);
                        overviewSplitPane.setEnabled(true);
                    } else {
                        overviewSplitPane.setLeftComponent(null);
                        overviewSplitPane.setDividerLocation(0);
                        overviewSplitPane.setEnabled(false);

                    }
                    repaint();
                }
            }
        });


        return toolBar;
    }

    public void showFilePopupMenu(JButton fileOperations) {
        JPopupMenu menu = new JPopupMenu();
        menu.add(new LoadAction());
        menu.add(new SaveAction());
        menu.add(new SaveSubsetAction());
        menu.addSeparator();
        menu.add(new PrintAction());
        menu.addSeparator();
        menu.add(new ExitAction());
        menu.show(fileOperations, 0, 15);

    }

    private void showLayoutPopupMenu(JButton showLayoutOptionsButton) {
        JPopupMenu menu = getLayoutMenu();
        menu.show(showLayoutOptionsButton, 0, 15);

    }

    protected JToolBar createStatusBar() {
        JToolBar statusbar = new JToolBar();
        statusbar.setFloatable(false);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setBorderPainted(true);
        progressBar.setIndeterminate(true);

        statusBarText = new JLabel();
        statusBarText.setFont(new Font("Arial", Font.BOLD, 18));
        statusBarText.setHorizontalTextPosition(JLabel.CENTER);

        //this.add(text,BorderLayout.NORTH);
        //this.add(progressBar,BorderLayout.CENTER);
        statusbar.add(statusBarText);
        statusbar.addSeparator();
        statusbar.add(progressBar);

        statusbar.setBackground(Color.WHITE);

        //statusbar.setMaximumSize(new java.awt.Dimension(500, 50));
        //statusbar.setMinimumSize(new java.awt.Dimension(500, 50));
        //statusbar.setPreferredSize(new java.awt.Dimension(500, 50));

        return statusbar;
    }

    protected void createLoaderDialog(String message) {
        statusBarText.setText(message);
        //contentPane.add(statusBar,BorderLayout.NORTH);
        statusBar.setEnabled(true);
        statusBar.setVisible(true);
        itemsToolBar.setEnabled(false);
        itemsToolBar.setVisible(false);
        //repaint();
    }

    public void killLoaderDialog() {
        contentPane.add(itemsToolBar, BorderLayout.NORTH);
        statusBar.setEnabled(false);
        statusBar.setVisible(false);
        itemsToolBar.setEnabled(true);
        itemsToolBar.setVisible(true);
        //repaint();
    }

    protected JPopupMenu getLayoutMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenu customlayoutsmenu = getCustomLayoutMenu();
        menu.add(customlayoutsmenu);
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

    public void initialLayout() {

        if (view == null || view.getGraph2D() == null) return;

        //createLoaderDialog("Applying initial layout...");

        {
            boolean largelayout = view.getGraph2D().nodeCount() > 50 || view.getGraph2D().edgeCount() > 150;

            if (largelayout) {
                OrganicLayoutModule module = new OrganicLayoutModule();
                module.start(view.getGraph2D());
            } else {
                HierarchicLayoutModule module = new HierarchicLayoutModule();
                module.start(view.getGraph2D());
            }
        }


        SALabeling al = new SALabeling();
        al.setProfitModel(new LabelRanking());
        al.setRemoveNodeOverlaps(true);
        al.setRemoveEdgeOverlaps(true);
        al.setPlaceEdgeLabels(true);
        al.setPlaceNodeLabels(true);
        al.label(view.getGraph2D());


        view.fitContent();
        view.updateView();

        killLoaderDialog();

    }

    protected void setViewMode(GRAPH_MODE mode) {
        removeCurrentViewModes();
        createViewModes();
        switch (mode) {
            case PAN:
                view.addViewMode(navigationMode);
                view.addViewMode(autoDragViewMode);
                break;
            case EDIT:
                view.addViewMode(editMode);
                view.addViewMode(autoDragViewMode);
                break;
            case INTERACTIVE_ZOOM:
                view.addViewMode(interactiveZoomMode);
                view.addViewMode(popupMode);
                break;
            case AREA_ZOOM:
                view.addViewMode(areaZoomMode);
                view.addViewMode(popupMode);
                break;
            case MAGNIFIER:
                view.addViewMode(magnifyingMode);
                view.addViewMode(editMode);
                view.addViewMode(autoDragViewMode);
                break;
        }

    }

    private static NavigationMode navigationMode;
    private static AutoDragViewMode autoDragViewMode;
    private static AreaZoomMode areaZoomMode;
    private static ToolTipEditMode editMode;
    private static InteractiveZoomMode interactiveZoomMode;
    private static PopupMenuMode popupMode;
    private static MagnifierViewMode magnifyingMode;

    class ToolTipEditMode extends EditMode {

        protected String getNodeTip(Node node) {
            return getNodeTipText(node);
        }
    }

    protected String getNodeTipText(Node v) {
        NodeRealizer nr = view.getGraph2D().getRealizer(v);
        return nr.getLabelText();
    }

    private void createViewModes() {
        if (popupMode == null) {
            popupMode = new PopupMenuMode(this);
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
            editMode.allowBendCreation(false);
            editMode.allowEdgeCreation(false);
            editMode.setMixedSelectionEnabled(true);
            //editMode.allowNodeEditing(true);
            editMode.showNodeTips(true);
            editMode.setPopupMode(popupMode);
        }
        if (interactiveZoomMode == null) {
            interactiveZoomMode = new InteractiveZoomMode();
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
    }

    private void removeCurrentViewModes() {
        if (navigationMode != null) view.removeViewMode(navigationMode);
        if (autoDragViewMode != null) view.removeViewMode(autoDragViewMode);
        if (areaZoomMode != null) view.removeViewMode(areaZoomMode);
        if (editMode != null) view.removeViewMode(editMode);
        if (interactiveZoomMode != null) view.removeViewMode(interactiveZoomMode);
        if (popupMode != null) view.removeViewMode(popupMode);
        if (magnifyingMode != null) view.removeViewMode(magnifyingMode);
    }

    class PopupMenuMode extends PopupMode {

        GraphViewingPanel viewer;

        public PopupMenuMode(GraphViewingPanel viewer) {
            super();
            this.viewer = viewer;
        }

        public JPopupMenu getBendPopup(Bend b) {
            return viewer.getBendPopup(b);
        }

        public JPopupMenu getEdgeLabelPopup(EdgeLabel label) {
            return viewer.getEdgeLabelPopup(label);
        }

        public JPopupMenu getEdgePopup(Edge e) {
            return viewer.getEdgePopup(e);
        }

        public JPopupMenu getNodePopup(Node v) {
            return viewer.getNodePopup(v);
        }

        public JPopupMenu getPaperPopup(double x, double y) {
            return viewer.getPaperPopup(x, y);
        }

        public JPopupMenu getSelectionPopup(double x, double y) {
            return viewer.getSelectionPopup(x, y);
        }
    }

    protected JPopupMenu getSelectionPopup(double x, double y) {
        return null;
    }

    protected JPopupMenu getPaperPopup(double x, double y) {
        return new JPopupMenu();
    }

    protected JPopupMenu getNodePopup(final Node v) {
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

        return nodePopup;
    }

    protected void makeNeighhborhoodHierarchical(Node v) {
        HierarchicLayouter layouter = new HierarchicLayouter();

        createLoaderDialog("Applying layout...");

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

        killLoaderDialog();
    }

    protected JPopupMenu getEdgePopup(Edge e) {
        return null;
    }

    protected JPopupMenu getEdgeLabelPopup(EdgeLabel label) {
        return null;
    }

    protected JPopupMenu getBendPopup(Bend b) {
        return null;
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

    /**
     * Instantiates and registers the listeners for the view.
     * (e.g. {@link y.view.Graph2DViewMouseWheelZoomListener}
     */
    protected void registerViewListeners() {
        //Note that mouse wheel support requires J2SE 1.4 or higher.
        view.getCanvasComponent().addMouseWheelListener(new Graph2DViewMouseWheelZoomListener());
    }

    public JPanel getContentPane() {
        return contentPane;
    }

    /**
     * Action that prints the contents of the view
     */
    protected class PrintAction extends AbstractAction {
        PageFormat pageFormat;
        OptionHandler printOptions;

        PrintAction() {
            super("Print");

            //setup option handler
            printOptions = new OptionHandler("Print Options");
            printOptions.addInt("Poster Rows", 1);
            printOptions.addInt("Poster Columns", 1);
            printOptions.addBool("Add Poster Coords", false);
            final String[] area = {"View", "Graph"};
            printOptions.addEnum("Clip Area", area, 1);
        }

        public void actionPerformed(ActionEvent e) {
            Graph2DPrinter gprinter = new Graph2DPrinter(view);

            //show custom print dialog and adopt values
            if (!printOptions.showEditor()) return;
            gprinter.setPosterRows(printOptions.getInt("Poster Rows"));
            gprinter.setPosterColumns(printOptions.getInt("Poster Columns"));
            gprinter.setPrintPosterCoords(
                    printOptions.getBool("Add Poster Coords"));
            if (printOptions.get("Clip Area").equals("Graph")) {
                gprinter.setClipType(Graph2DPrinter.CLIP_GRAPH);
            } else {
                gprinter.setClipType(Graph2DPrinter.CLIP_VIEW);
            }

            //show default print dialogs
            PrinterJob printJob = PrinterJob.getPrinterJob();
            if (pageFormat == null) pageFormat = printJob.defaultPage();
            PageFormat pf = printJob.pageDialog(pageFormat);
            if (pf == pageFormat) {
                return;
            } else {
                pageFormat = pf;
            }

            //setup printjob.
            //Graph2DPrinter is of type Printable
            printJob.setPrintable(gprinter, pageFormat);

            if (printJob.printDialog()) {
                try {
                    printJob.print();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Action that terminates the application
     */
    protected class ExitAction extends AbstractAction {
        ExitAction() {
            super("Exit");
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    /**
     * Action that saves the current graph to a file in YGF format.
     */
    protected class SaveAction extends AbstractAction {
        JFileChooser chooser;

        SaveAction() {
            super("Save...");
            chooser = null;
        }

        public void actionPerformed(ActionEvent e) {
            if (chooser == null) {
                chooser = new JFileChooser();
            }
            if (chooser.showSaveDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
                String name = chooser.getSelectedFile().toString();
                if (name.endsWith(".gml")) {
                    GMLIOHandler ioh = new GMLIOHandler();
                    try {
                        ioh.write(view.getGraph2D(), name);
                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                } else if (name.endsWith(".jpg")) {
                    JPGIOHandler ioh = new JPGIOHandler();
                    try {
                        ioh.write(view.getGraph2D(), name);
                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                } else {
                    if (!name.endsWith(".ygf")) name = name + ".ygf";
                    YGFIOHandler ioh = new YGFIOHandler();
                    try {
                        ioh.write(view.getGraph2D(), name);
                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                }
            }
        }
    }

    /**
     * Action that saves the current subset of the graph to a file in YGF format.
     */
    protected class SaveSubsetAction extends AbstractAction {
        JFileChooser chooser;

        public SaveSubsetAction() {
            super("Save selection...");
            chooser = null;
        }

        public void actionPerformed(ActionEvent e) {
            if (chooser == null) {
                chooser = new JFileChooser();
            }
            if (chooser.showSaveDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
                String name = chooser.getSelectedFile().toString();
                if (!name.endsWith(".ygf")) name = name + ".ygf";
                YGFIOHandler ioh = new YGFIOHandler();
                try {
                    DataProvider dp = Selections.createSelectionDataProvider(view.getGraph2D());
                    ioh.writeSubset(view.getGraph2D(), dp, name);
                } catch (IOException ioe) {
                    D.show(ioe);
                }
            }
        }
    }

    /**
     * Action that loads the current graph from a file in YGF format.
     */
    protected class LoadAction extends AbstractAction {
        JFileChooser chooser;

        public LoadAction() {
            super("Load...");
            chooser = null;
        }

        public void actionPerformed(ActionEvent evt) {
            if (chooser == null) {
                chooser = new JFileChooser();
            }
            if (chooser.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
                String name = chooser.getSelectedFile().toString();
                if (name.endsWith(".gml")) {
                    GMLIOHandler ioh = new GMLIOHandler();
                    try {
                        view.getGraph2D().clear();
                        ioh.read(view.getGraph2D(), name);
                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                } else if (name.endsWith(".ygf")) {
                    YGFIOHandler ioh = new YGFIOHandler();
                    try {
                        view.getGraph2D().clear();
                        ioh.read(view.getGraph2D(), name);
                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                } else {
                    if (!name.endsWith(".sxg")) name = name + ".sxg";
                    SXGIOHandler ioh = new SXGIOHandler();
                    try {
                        view.getGraph2D().clear();
                        ioh.read(view.getGraph2D(), name);

                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                }
                //force redisplay of view contents
                initialLayout();
                view.fitContent();
                view.getGraph2D().updateViews();
            }
        }
    }

    /**
     * Hide a node on right click
     */
    protected class HideNodeAction extends AbstractAction {

        Node targetNode;
        Graph2D graph;

        public HideNodeAction(Graph2D graph, Node node) {
            super("Hide this node");
            targetNode = node;
            this.graph = graph;
        }

        public void actionPerformed(ActionEvent e) {

            graph.hide(targetNode);
            graph.updateViews();
        }
    }

    public void applyGraphLayout(YModule module) {


        OptionHandler op = module.getOptionHandler();
        if (op != null) {
            if (!op.showEditor())
                return;
        }

        createLoaderDialog("Applying layout...");

        module.startAsThread(view.getGraph2D());

        killLoaderDialog();
    }

    /**
     * Launches a generic YModule. If the modules provides
     * an option handler display it before the modules gets launched.
     */
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
