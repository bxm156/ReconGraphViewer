package binevi.View;

import binevi.IO.BioPAXHandler;
import binevi.IO.SXGIOHandler;
import binevi.Parser.PathCaseParsers.PathCaseXMLParser;
import binevi.Resources.PathCaseResources.GenomeTable;
import binevi.Resources.PathCaseResources.OrganismTable;
import binevi.Resources.PathCaseResources.PathCaseRepository;
import binevi.Resources.PathCaseResources.TableQueries;
import edu.cwru.nashua.pathwaysservice.PathwaysService;
import edu.cwru.nashua.pathwaysservice.PathwaysServiceSoap;
import edu.cwru.nashua.pathwaysservice.PathwaysServiceMetabolomics;
import edu.cwru.nashua.pathwaysservice.PathwaysServiceSoapMetabolomics;
import y.base.*;
import y.io.GMLIOHandler;
import y.io.JPGIOHandler;
import y.io.YGFIOHandler;
import y.option.OptionHandler;
import y.util.D;
import y.view.*;
import y.view.hierarchy.HierarchyManager;
import y.layout.NodeLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PathCaseViewer extends JPanel {

    //PathCase Constants
    public static final String GUIDSEPERATOR = ";";
    public static final String COLORSEPERATOR = "@";
    public static final String TEXTSEPERATOR = "%#%";
    private String valueOfcompartmentH;
    //Thread Manager
    private ExecutorService pool;

    //Data Cache
    public PathCaseRepository repository;

    //Web Service
    PathwaysService service;
    PathwaysServiceMetabolomics serviceM;
    //configuration of the applet
    HashMap<String, Object> configuration;
    
    //Data cache
    HashMap<String, String> PathCaseIDToNameMap;
    //For cofactors and regulators, the list contains multiple pathcase identifiers since we merged the modulators into a single node
    //for substrate/product, generic process, and collapsed pathway nodes, the list contains only a single identifier, thus it is safe to obtain only the first id in the list
    HashMap<Node, HashSet<String>> nodeToPathCaseId;
    HashMap<String, Color> DBIDToCustomFillColor;

    ArrayList<NodeGroupofPathway> nodesInPathways;
    //reference to applet for redirection purposes
    PathCaseViewerApplet applet;

    //keep the current graph mode;
    public PathCaseViewMode graphMode;
    boolean organismAndgeneLoaded=false;

    HashSet<String> displayedTissues;

    //create a panel with dockable pathcase tools
    public PathCaseViewer(PathCaseViewerApplet applet, boolean geneviewer, boolean organismpanel, boolean layoutsaving) {
        createContentPane(geneviewer, organismpanel, layoutsaving);
        pool = Executors.newFixedThreadPool(10);
        repository = new PathCaseRepository();

        this.applet = applet;


        graphMode = new PathCaseViewMode();

    }

    public JPanel getContentPane() {
        return this;
    }

    //////////////////////////////        CREATE GUI ELEMENTS       //////////////////////////

    //Main graph viewer panel
    protected DefaultYGraphViewer graphViewer;
    protected DefaultYGraphViewerMetabolomics graphViewerCompartmentH;

    //the panel that shows an overview of the current graph
    protected JSplitPane overviewPane;
    JSplitPane overviewSplitPane;

    //the toolbar that carries graph manipulation buttons, and buttons to switch between graph modes
    protected JToolBar itemsToolBar;

    //place the statusbar at the top, to give an important message, or show a message
    protected JToolBar statusBar;
    //the text to be displayed on the top statusbar
    protected JLabel statusBarText;

    //PathCase related buttons
    private JToggleButton organismBrowserButton;
    private JToggleButton geneViewerButton;

    //Organism and Gene Viewer components
    OrganismCheckPanel organismPanel;
    JSplitPane organismSplitPane;
    GeneViewerPanel geneViewerPanel;
    JSplitPane geneSplitPane;


    //define contents of the panel
    private void createContentPane(boolean geneviewer, boolean organismpanel, boolean layoutsaving) {
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        //graphViewer
        createGraphPanel();
        //minimapPanel
        createMinimap();

        itemsToolBar = createMainToolBar(geneviewer, organismpanel, layoutsaving);
        statusBar = createStatusBar();

        add(itemsToolBar, BorderLayout.NORTH);
        add(statusBar, BorderLayout.NORTH);

        statusBar.setEnabled(false);
        statusBar.setVisible(false);


        overviewSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, graphViewer.view);
        overviewSplitPane.setDividerLocation(0);
        overviewSplitPane.setDividerSize(5);
        overviewSplitPane.setContinuousLayout(true);

        if (organismpanel) {
            //organismPanel
            createOrganismViewer();

            organismSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, overviewSplitPane, null);
            organismSplitPane.setDividerLocation(0);
            organismSplitPane.setDividerSize(5);
            organismSplitPane.setContinuousLayout(true);
            organismSplitPane.setEnabled(false);

        }

        if (geneviewer) {
            createGeneViewer();

            if (organismpanel)
                geneSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, organismSplitPane, null);
            else
                geneSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, overviewSplitPane, null);


            geneSplitPane.setDividerLocation(0);
            geneSplitPane.setDividerSize(5);
            geneSplitPane.setContinuousLayout(true);
            geneSplitPane.setEnabled(false);

            add(geneSplitPane, BorderLayout.CENTER);

        } else if (organismpanel) {
            add(organismSplitPane, BorderLayout.CENTER);
        } else
            add(overviewSplitPane, BorderLayout.CENTER);

        //geneViewerPanel


    }

    //Creates a toolbar for this base.
    protected JToolBar createMainToolBar(boolean geneviewer, boolean organismpanel, final boolean layoutsaving) {
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
                showFilePopupMenu(fileOperations, layoutsaving);
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
                graphViewer.setViewMode(DefaultYGraphViewer.GRAPH_MODE.EDIT);
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
                graphViewer.setViewMode(DefaultYGraphViewer.GRAPH_MODE.MAGNIFIER);
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
                graphViewer.setViewMode(DefaultYGraphViewer.GRAPH_MODE.PAN);
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
                graphViewer.setViewMode(DefaultYGraphViewer.GRAPH_MODE.INTERACTIVE_ZOOM);
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
                graphViewer.setViewMode(DefaultYGraphViewer.GRAPH_MODE.AREA_ZOOM);
            }
        });
        toolBar.addSeparator();

        if (graphViewer.ISDELETIONENABLED) {
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
                    graphViewer.view.getGraph2D().removeSelection();
                    graphViewer.view.getGraph2D().updateViews();
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
                graphViewer.view.setZoom(graphViewer.view.getZoom() * 1.2);
                //optional code that adjusts the size of the
                //view's world rectangle. The world rectangle
                //defines the region of the canvas that is
                //accessible by using the scrollbars of the view.
                Rectangle box = graphViewer.view.getGraph2D().getBoundingBox();
                graphViewer.view.setWorldRect(box.x - 20, box.y - 20, box.width + 40, box.height + 40);

                graphViewer.view.updateView();
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
                graphViewer.view.setZoom(graphViewer.view.getZoom() * 0.8);
                //optional code that adjusts the size of the
                //view's world rectangle. The world rectangle
                //defines the region of the canvas that is
                //accessible by using the scrollbars of the view.
                Rectangle box = graphViewer.view.getGraph2D().getBoundingBox();
                graphViewer.view.setWorldRect(box.x - 20, box.y - 20, box.width + 40, box.height + 40);
                graphViewer.view.updateView();
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
                graphViewer.view.fitContent();
                graphViewer.view.updateView();
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
                graphViewer.view.setZoom(1.0);
                //view.setWorldRect((int)view.getWorldRect().getWidth()/2, (int)view.getWorldRect().getHeight()/2, (int)view.getWorldRect().getWidth(), (int)view.getWorldRect().getHeight());
                graphViewer.view.updateView();
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
                if (graphViewer != null && overviewSplitPane != null && overviewPane != null) {
                    if (minimapButton.isSelected()) {
                        overviewSplitPane.setLeftComponent(overviewPane);
                        overviewSplitPane.setDividerLocation(200);
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

        toolBar.addSeparator();

        geneViewerButton = new JToggleButton();
        toolBar.add(geneViewerButton);
        geneViewerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/geneViewerIcon.GIF")));
        geneViewerButton.setToolTipText("Display/Hide Gene Viewer");
        geneViewerButton.setMaximumSize(new java.awt.Dimension(34, 21));
        geneViewerButton.setMinimumSize(new java.awt.Dimension(34, 21));
        geneViewerButton.setPreferredSize(new java.awt.Dimension(34, 21));
        geneViewerButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedgeneViewerIcon.GIF")));
        geneViewerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //if (view != null && splitPane != null && overviewPane !=null )
                {
                    if (geneViewerButton.isSelected()) {
                        if(!organismAndgeneLoaded){
                            String WebServiceURL = (String) configuration.get("WebServiceUrl");
                                    String setOrganism = (String) configuration.get("setOrganism");
    //                                System.out.println("....Before loading OrganismHierarchy from repository...Message");
    //                                service=null;
                                    loadOrganismHierarchyFromRepository(WebServiceURL, setOrganism);

    //                        String WebServiceURL = (String) configuration.get("WebServiceUrl");
                            String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
    //                        System.out.println("....Before loading Genome from repository...Message");
    //                        service=null;
                            loadGenomeList(WebServiceURL, expandedPathwayGuids);
                            organismAndgeneLoaded=true;
                        }
                        geneViewerDisplayAction();

                    } else {
                        geneViewerHideAction();
                    }
                    repaint();
                }
            }
        });
        geneViewerButton.setEnabled(geneviewer);
        geneViewerButton.setVisible(false);

        organismBrowserButton = new JToggleButton();
        toolBar.add(organismBrowserButton);
        organismBrowserButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/organismPanelIcon.GIF")));
        organismBrowserButton.setToolTipText("Display/Hide Organism Panel");
        organismBrowserButton.setMaximumSize(new java.awt.Dimension(34, 21));
        organismBrowserButton.setMinimumSize(new java.awt.Dimension(34, 21));
        organismBrowserButton.setPreferredSize(new java.awt.Dimension(34, 21));
        organismBrowserButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedOrganismPanelIcon.GIF")));
        organismBrowserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //if (view != null && splitPane != null && overviewPane !=null )
                {
                    if (organismBrowserButton.isSelected()) {
                        if(!organismAndgeneLoaded){
                            String WebServiceURL = (String) configuration.get("WebServiceUrl");
                            String setOrganism = (String) configuration.get("setOrganism");
                            loadOrganismHierarchyFromRepository(WebServiceURL, setOrganism);

                            String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
                            loadGenomeList(WebServiceURL, expandedPathwayGuids);
                            organismAndgeneLoaded=true;
                        }
                        organismSplitPane.setRightComponent(organismPanel);
                        organismSplitPane.setDividerLocation(organismSplitPane.getWidth() - organismPanel.getPreferredSize().width);
                        organismSplitPane.setEnabled(true);
                    } else {
                        organismSplitPane.setRightComponent(null);
                        organismSplitPane.setDividerLocation(0);
                        organismSplitPane.setEnabled(false);
                    }
                    repaint();
                }
            }
        });
        organismBrowserButton.setEnabled(organismpanel);
        organismBrowserButton.setVisible(false);

        toolBar.addSeparator();

        final JButton hideCommonButton = new JButton();
        toolBar.add(hideCommonButton);
        hideCommonButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/commonmolIcon.GIF")));
        hideCommonButton.setToolTipText("Change Display Mode");
        hideCommonButton.setMaximumSize(new java.awt.Dimension(34, 21));
        hideCommonButton.setMinimumSize(new java.awt.Dimension(34, 21));
        hideCommonButton.setPreferredSize(new java.awt.Dimension(34, 21));
        hideCommonButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/binevi/Resources/Images/pressedcommonmolIcon.GIF")));
        hideCommonButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JPopupMenu menu = getModesMenu();
                menu.show(hideCommonButton, 0, 15);
            }
        });
        //hideCommonButton.setSelected(true);
        //hideCommonButton.setEnabled(false);

        return toolBar;
    }

    private void geneViewerDisplayAction() {
        createGeneViewer();
        loadGeneViewerOrganismListFromOrgIds(organismPanel.getSelectedOrganismIdsOfLeaves());//organisms that have a gene
        geneSplitPane.setRightComponent(geneViewerPanel);
        geneSplitPane.setDividerLocation(geneSplitPane.getHeight() - 155/*geneViewerPanel.getPreferredSize().height*/);
        geneSplitPane.setEnabled(true);
        //geneViewerPanel.geneSelected(null,null);
    }

    private void geneViewerHideAction() {
        geneSelectProcessesandCollapsedPathways(new HashSet<GenomeTable.GeneEntry>());
        geneSplitPane.setRightComponent(null);
        geneSplitPane.setDividerLocation(0);
        geneSplitPane.setEnabled(false);
    }

    public void createGraphPanel() {
               graphViewer = new DefaultYGraphViewer(this);
            graphViewer.setViewMode(DefaultYGraphViewer.GRAPH_MODE.EDIT);
            graphViewer.registerViewActions();
            graphViewer.registerViewListeners();
    }

    public void createOrganismViewer() {
        organismPanel = new OrganismCheckPanel(true, this);
    }

    public void createGeneViewer() {
        geneViewerPanel = new GeneViewerPanel(this);
        //geneViewerPanel.setMaximumSize(new Dimension(150,1000));
    }

    public void createMinimap() {

        Overview overView = new Overview(graphViewer.view);
        overView.setPreferredSize(new Dimension(150, 150));

        overviewPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, overView, null);

        overviewPane.setDividerLocation(overView.getPreferredSize().height);
        overviewPane.setDividerSize(5);
        overviewPane.setContinuousLayout(true);
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

    public void destroy() {
        if (graphViewer != null) graphViewer.destroyViewModes();
        graphViewer = null;

        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(10, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    //////////////////////           LAYOUT OPERATIONS           //////////////////////////////

    private void showLayoutPopupMenu(JButton showLayoutOptionsButton) {
        JPopupMenu menu = graphViewer.getLayoutMenu();
        menu.show(showLayoutOptionsButton, 0, 15);

    }

    //////////////////////            FILE OPERATIONS             /////////////////////////////

    public void showFilePopupMenuMultiAction(JButton fileOperations) {
        JPopupMenu menu = new JPopupMenu();
        menu.add(new LoadAction(this));
        menu.add(new SaveAction());
        menu.add(new SaveSubsetAction());
        menu.addSeparator();
        menu.add(new PrintAction());
        menu.addSeparator();
        menu.add(new ExitAction());
        menu.show(fileOperations, 0, 15);

    }

    public void showFilePopupMenu(JButton fileOperations, boolean layoutsaving) {

        JPopupMenu menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Export Graph As JPEG");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JFileChooser chooser = new JFileChooser();

                if (chooser.showSaveDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {
                    String name = chooser.getSelectedFile().toString();

                    if (!name.endsWith(".jpg")) name = name + ".jpg";
                    JPGIOHandler ioh = new JPGIOHandler();
                    //ioh.setQuality(1.0f);
                    try {
                        ioh.write(graphViewer.view.getGraph2D(), name);
                    } catch (IOException ioe) {
                        D.show(ioe);
                    }

                }
            }
        });
        menu.add(item);

        item = new JMenuItem("Visualize Graph from (BioPAX or SXG) file");
        item.addActionListener(new LoadAction(this));
        menu.add(item);

        //layout: JMenuItem Save Layout
        if (layoutsaving) {
            item = new JMenuItem("Save the layout information");
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    /* FOR DEBUG or BACKUP save it in a txt file
               JFileChooser chooser = new JFileChooser();
               if(chooser.showSaveDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {
                   String name = chooser.getSelectedFile().toString();
                   if(!name.endsWith(".txt")) {
                       name += ".txt";
                   }
                   try{
                       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(name)));
                       pw.write(layoutInfo());
                       pw.close();
                       return;
                   }catch(IOException ioe) {
                       D.show(ioe);
                   }
                    }//*/

                    //TODO: move call to web methods to PathCaseServices
                    pool.submit(new Thread() {
                        public void run() {
                            String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
                            if (numberOfPathways() == 1) {
                                String lo = layoutInfo();
                                createLoaderDialog("Saving the layout information...");
                                try {
                                      boolean b ;
                                    if(valueOfcompartmentH.equalsIgnoreCase("true")){
                                        b = serviceM.getPathwaysServiceSoap().storeLayout2("", expandedPathwayGuids, "", "", lo, "dblabpathcase");
                                    }else b = service.getPathwaysServiceSoap().storeLayout("", expandedPathwayGuids, "", "", lo);

                                    if (!b) {
                                        throw new Exception("layout not stored");
                                    }
                                    killLoaderDialog();
                                    JOptionPane.showMessageDialog(null, "Layout has been successfully saved", "information", JOptionPane.INFORMATION_MESSAGE);
                                } catch (Exception e) {
                                    D.show(e);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Can only save layout for a single pathway", "information", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    });

                    //*/
                    //bool = service.getPathwaysServiceSoap().
                }
            });
            menu.add(item);
        }

        menu.show(fileOperations, 0, 15);
    }

    // Action that prints the contents of the view
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
            Graph2DPrinter gprinter = new Graph2DPrinter(graphViewer.view);

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

    //Action that terminates the application
    protected class ExitAction extends AbstractAction {
        ExitAction() {
            super("Exit");
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    //Action that saves the current graph to a file.
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
            if (chooser.showSaveDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {
                String name = chooser.getSelectedFile().toString();
                if (name.endsWith(".gml")) {
                    GMLIOHandler ioh = new GMLIOHandler();
                    try {
                        ioh.write(graphViewer.view.getGraph2D(), name);
                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                } else if (name.endsWith(".jpg")) {
                    JPGIOHandler ioh = new JPGIOHandler();
                    try {
                        ioh.write(graphViewer.view.getGraph2D(), name);
                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                } else {
                    if (!name.endsWith(".ygf")) name = name + ".ygf";
                    YGFIOHandler ioh = new YGFIOHandler();
                    try {
                        ioh.write(graphViewer.view.getGraph2D(), name);
                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                }
            }
        }
    }

    //Action that saves the current subset of the graph to a file.
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
            if (chooser.showSaveDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {
                String name = chooser.getSelectedFile().toString();
                if (!name.endsWith(".ygf")) name = name + ".ygf";
                YGFIOHandler ioh = new YGFIOHandler();
                try {
                    DataProvider dp = Selections.createSelectionDataProvider(graphViewer.view.getGraph2D());
                    ioh.writeSubset(graphViewer.view.getGraph2D(), dp, name);
                } catch (IOException ioe) {
                    D.show(ioe);
                }
            }
        }
    }

    //Action that loads the current graph from a file.
    protected class LoadAction extends AbstractAction {
        JFileChooser chooser;
        PathCaseViewer pathCaseViewer;

        public LoadAction(PathCaseViewer pathCaseViewer) {
            super("Load...");
            chooser = null;
            this.pathCaseViewer = pathCaseViewer;
        }

        /*public LoadAction() {
            super("Load...");
            chooser = null;
        }*/

        public void actionPerformed(ActionEvent evt) {
            if (chooser == null) {
                chooser = new JFileChooser();
            }
            if (chooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {
                String name = chooser.getSelectedFile().toString();
                if (name.endsWith(".gml")) {
                    GMLIOHandler ioh = new GMLIOHandler();
                    try {
                        graphViewer.view.getGraph2D().clear();
                        ioh.read(graphViewer.view.getGraph2D(), name);
                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                } else if (name.endsWith(".ygf")) {
                    YGFIOHandler ioh = new YGFIOHandler();
                    try {
                        graphViewer.view.getGraph2D().clear();
                        ioh.read(graphViewer.view.getGraph2D(), name);
                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                } else if (name.endsWith(".sxg")) {

                    SXGIOHandler ioh = new SXGIOHandler();
                    try {
                        graphViewer.view.getGraph2D().clear();
                        ioh.read(graphViewer.view.getGraph2D(), name);

                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                } else {//BIOPAX by default
                    graphViewer.resetView();
                    repository.reset();
                    BioPAXHandler ioh = new BioPAXHandler(repository);
                    try {

                        //graphViewer.view.updateView();

                        if (pathCaseViewer != null) {
                            pathCaseViewer.geneViewerButton.removeAll();
                            pathCaseViewer.organismBrowserButton.removeAll();
                            pathCaseViewer.geneViewerButton.setEnabled(false);
                            pathCaseViewer.organismBrowserButton.setEnabled(false);

                            ioh.read(graphViewer.view.getGraph2D(), name);
                            if (ioh.node2idtable != null) {
                                nodeToPathCaseId = ioh.node2idtable;
                                PathCaseIDToNameMap = TableQueries.getPathCaseIDToNameMap(repository, graphViewer.view, nodeToPathCaseId);
                            } else {
                                nodeToPathCaseId = new HashMap<Node, HashSet<String>>();
                                PathCaseIDToNameMap = new HashMap<String, String>();
                            }
                            if (graphViewer.view.getGraph2D().nodeCount() < 1) {
                                //error or empty graph
                                JOptionPane.showMessageDialog(pathCaseViewer, "Error occured while loading from BioPax file.", "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            ioh.read(graphViewer.view.getGraph2D(), name);
                            if (graphViewer.view.getGraph2D().nodeCount() < 1) {
                                //error or empty graph
                                JOptionPane.showMessageDialog(null, "Error occured while loading from BioPax file.", "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }

                    } catch (IOException ioe) {
                        D.show(ioe);
                    }
                }

                //force redisplay of view contents
                graphViewer.bestLayout();
                graphViewer.view.fitContent();
                graphViewer.view.getGraph2D().updateViews();
            }
        }
    }

    ///////////////////           TEST AS AN APPLICATION    /////////////////////////////////

    public static void main(String args[]) {

//      SwingUtility.setPlaf("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		SwingUtility.setPlaf("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        //SwingUtility.setPlaf("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        //SwingUtility.setPlaf("javax.swing.plaf.metal.MetalLookAndFeel");

        //configureDocking();

        // create and show the GUI
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                runGUI();
            }
        });
    }

    private static void runGUI() {
        // create out application frame
        JFrame frame = new JFrame();
        frame.getRootPane().setContentPane(new PathCaseViewer(null, false, false, false));
        frame.setSize(800, 600);
        setCloseOperation(frame);
        // now show the frame
        frame.setVisible(true);
    }

    public static void setCloseOperation(JFrame f) {
        if (!Boolean.getBoolean("disable.system.exit"))
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        else
            f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * Opens a JOptionPane with the error message and formatted stack trace of the throwable in a scrollable text area.
     *
     * @param c             optional argument for parent component to open modal error
     *                      dialog relative to
     * @param error_message short string description of failure, must be non-null
     * @param t             the throwable that's being reported, must be non-null
     */
    public static void showErrorDialog(Component c, String error_message, Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(error_message);
        pw.print("Exception is: ");
        t.printStackTrace(pw);
        pw.flush();

        JTextArea ta = new JTextArea(sw.toString(), 15, 60);
        JScrollPane sp = new JScrollPane(ta);
        JOptionPane.showMessageDialog(c, sp, error_message,
                JOptionPane.ERROR_MESSAGE);
    }

    /////////////////////////  PROCESS APPLET PARAMETERS ///////////////////////////

    class PathCaseViewMode {
        public boolean showcommonmoleculesingraph;
        public boolean showmodulatorsingraph;
        public boolean showlinkingpathwaysingraph;

        public PathCaseViewMode() {
            defaultValues();
        }

        public void defaultValues() {
            showcommonmoleculesingraph = false;
            showmodulatorsingraph = true;
            showlinkingpathwaysingraph = false;
        }

         public boolean isDefault() {
            return !showcommonmoleculesingraph && showmodulatorsingraph && !showlinkingpathwaysingraph;
        }
    }

    public void setConfiguration(HashMap<String, Object> configuration) {
        this.configuration = configuration;
    }

    public void processAppletParameters(final boolean firstTime) {

        Boolean loadFromBioPAX = (Boolean) configuration.get("loadFromBioPAX");
        if (!loadFromBioPAX)
            pool.submit(new Thread() {
                public void run() {

                    //NOTE: each of the methods here should kill the loader dialog regardless of an exception inside

                    //load graph from service
                    createLoaderDialog("Loading Graph Data From PathCase Web Service");
                    loadPathCaseGraphModel();
                    sleepSafe(1000);

                    //load gene-organism-pathway, set organism hierarchy
                    Boolean organismBrowserEnabled = (Boolean) configuration.get("organismBrowserEnabled");
                    if (organismBrowserEnabled) {
                        createLoaderDialog("Downloading Organism Taxonomy");
                        String WebServiceURL = (String) configuration.get("WebServiceUrl");
                        String setOrganism = (String) configuration.get("setOrganism");
                        loadOrganismHierarchyFromRepository(WebServiceURL, setOrganism);
                    }
                    sleepSafe(1000);

                    Boolean geneViewerEnabled = (Boolean) configuration.get("geneViewerEnabled");
                    if (geneViewerEnabled) {
                        createLoaderDialog("Loading Gene List");
                        String WebServiceURL = (String) configuration.get("WebServiceUrl");
                        String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
                        loadGenomeList(WebServiceURL, expandedPathwayGuids);
                    }
                    sleepSafe(1000);

                    //load genes
                    //Gene Viewer is self loading, see gene viewer...

                    //apply initial layout
                    createLoaderDialog("Applying New Layout");

                    //set custom coloring
                    String highlightEntities = (String) configuration.get("highlightEntities");
                    makeCustomNodePainting(highlightEntities);

                    if (firstTime) {
                        doFirstTimeLayout();
                    } else
                        graphViewer.bestLayout();


                    if (organismBrowserButton.isEnabled() && organismPanel != null && organismPanel.selectedOrganismIdList != null)
                        organismSelected(organismPanel.selectedOrganismIdList);

                    killLoaderDialog();
                }
            });
        else {
            (new LoadAction(this)).actionPerformed(null);
        }

    }

    private void connectToService(String WebServiceURL) {

        try {
            service = new PathwaysService(WebServiceURL);
        } catch (MalformedURLException e) {
            D.show(e);
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            //return;
        }
    }

   private void connectToServiceCompartmentH(String WebServiceURL) {

        try {
            serviceM = new PathwaysServiceMetabolomics(WebServiceURL);
        } catch (MalformedURLException e) {
            D.show(e);
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            //return;
        }
    }

    public void processAppletParameters() {
//        this.valueOfcompartmentH =(String)configuration.get("compartmentH");
//        if(valueOfcompartmentH.equalsIgnoreCase("true")){
            Boolean loadFromBioPAX = (Boolean) configuration.get("loadFromBioPAX");

            if (!loadFromBioPAX.booleanValue())
                pool.submit(new Thread() {
                    public void run() {
                        try {
                            while (!applet.APPLETFINISHED) {
                                sleepSafe(250);
                           }
                            //NOTE: each of the methods here should kill the loader dialog regardless of an exception inside

                            //load graph from service
                            createLoaderDialog("Loading Graph Data From PathCase Web Service");
//                            loadPathCaseGraphCompartmentH();                  impossible to reach
                            loadPathCaseGraph();
                            sleepSafe(1000);
                            System.out.println("After loading pathcasegraph...Message");
                            //load gene-organism-pathway, set organism hierarchy
//                            Boolean organismBrowserEnabled = (Boolean) configuration.get("organismBrowserEnabled");
//                            if (organismBrowserEnabled) {
//                                createLoaderDialog("Downloading Organism Taxonomy");
//                                String WebServiceURL = (String) configuration.get("WebServiceUrl");
//                                String setOrganism = (String) configuration.get("setOrganism");
//                                System.out.println("....Before loading OrganismHierarchy from repository...Message");
////                                service=null;
//                                loadOrganismHierarchyFromRepository(WebServiceURL, setOrganism);
//                                System.out.println("....after loading OrganismHierarchy from repository...Message");
//                            }
//                            sleepSafe(1000);
//                            System.out.println("After loading organisam hierarchy...Message");
////
//                            Boolean geneViewerEnabled = (Boolean) configuration.get("geneViewerEnabled");
//                            if (geneViewerEnabled) {
//                                createLoaderDialog("Loading Gene List");
//                                String WebServiceURL = (String) configuration.get("WebServiceUrl");
//                                String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
//                                System.out.println("....Before loading Genome from repository...Message");
////                                service=null;
////                                loadGenomeList(WebServiceURL, expandedPathwayGuids);
//                                System.out.println("....After loading Genome from repository...Message");
//                            }
//                            sleepSafe(1000);
//                            System.out.println("After loading Genomelist hierarchy...Message");

                            //load genes
                            //Gene Viewer is self loading, see gene viewer...

                            //apply initial layout
                            createLoaderDialog("Applying New Layout");

                            //set custom coloring
                            String highlightEntities = (String) configuration.get("highlightEntities");
                            makeCustomNodePainting(highlightEntities);

                            doFirstTimeLayout();

                            if (organismBrowserButton.isEnabled() && organismPanel != null && organismPanel.selectedOrganismIdList != null)
                                organismSelected(organismPanel.selectedOrganismIdList);
                        }catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e.toString());
                        }finally {
                            killLoaderDialog();
                        }
                    }
                });
            else {
                (new LoadAction(this)).actionPerformed(null);
            }

//        }else{
//            if (configuration != null) {
//                processAppletParameters(false);
//            }
//        }
    }

    private void loadPathCaseGraphCompartmentH(){
        boolean done = false;

        if (configuration == null) done = true;

        else if (serviceM == null) {
            String WebServiceURL = (String) configuration.get("WebServiceUrl");
            if (WebServiceURL != null && !WebServiceURL.equals(""))
                connectToServiceCompartmentH(WebServiceURL);
            else {
                JOptionPane.showMessageDialog(this, "Applet PathCase Web Service Parameter is empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                done = true;
            }
        }

        if (serviceM == null) {
            String WebServiceURL = (String) configuration.get("WebServiceUrl");
            JOptionPane.showMessageDialog(this, "PathCase Web Service is not accessible. Please try in your browser the URL " + WebServiceURL, "Warning", JOptionPane.WARNING_MESSAGE);
            done = true;
        }

        if (!done) {

            try {
            String collapsedPathwayGuids = (String) configuration.get("collapsedPathwayGuids");
            String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
            String genericProcessGuids = (String) configuration.get("genericProcessGuids");
            String moleculeGuids = (String) configuration.get("moleculeGuids");
            String graphXML="";

            String tissuesString="";
             if(configuration.containsKey("selectedTissuesOnly"))
             {
                HashSet<String> tissues=  (HashSet<String>) configuration.get("selectedTissuesOnly");
                 if(tissues!=null && tissues.size() >0)
                 {
                    for(String tissue:tissues)
                    {
                         tissuesString+=tissue+";";
                    }
                 }
             }

//              PathwaysServiceSoapMetabolomics tSoap=serviceM.getPathwaysServiceSoap();

              if(tissuesString.trim()!="")
              {
                  graphXML= serviceM.getPathwaysServiceSoap().getSelectedTissueGraphData(collapsedPathwayGuids, expandedPathwayGuids, genericProcessGuids, moleculeGuids,tissuesString);
              }
              else
              {
                   graphXML= serviceM.getPathwaysServiceSoap().getGraphData(collapsedPathwayGuids, expandedPathwayGuids, genericProcessGuids, moleculeGuids,"","","","");
              }
            PathCaseXMLParser parser = new PathCaseXMLParser(repository);
             parser.loadRepositoryFromGraphXML(graphXML);

            reloadFromRepository(false);
            this.displayedTissues= repository.TissueNames;
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(this, "Unknown exception:\n"+e, "Warning", JOptionPane.WARNING_MESSAGE);
                System.out.println(e.getMessage());
            }
        }
    }

    private void loadPathCaseGraph() {
        boolean done = false;

        if (configuration == null) done = true;

        else if (service == null) {
            String WebServiceURL = (String) configuration.get("WebServiceUrl");
            if (WebServiceURL != null && !WebServiceURL.equals(""))
                connectToService(WebServiceURL);
            else {
                JOptionPane.showMessageDialog(this, "Applet PathCase Web Service Parameter is empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                done = true;
            }
        }

        if (service == null) {
            String WebServiceURL = (String) configuration.get("WebServiceUrl");
            JOptionPane.showMessageDialog(this, "PathCase Web Service is not accessible. Please try in your browser the URL " + WebServiceURL, "Warning", JOptionPane.WARNING_MESSAGE);
            done = true;
        }

        if (!done) {
            try {
                String collapsedPathwayGuids = (String) configuration.get("collapsedPathwayGuids");
                String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
                String genericProcessGuids = (String) configuration.get("genericProcessGuids");
                String moleculeGuids = (String) configuration.get("moleculeGuids");

                String graphXML = service.getPathwaysServiceSoap().getGraphData(collapsedPathwayGuids, expandedPathwayGuids, genericProcessGuids, moleculeGuids,"","","","");
//                System.out.println("The length of the xml file:"+graphXML.length());

//                graphXML=readFile("c:\\new3.xml");

                PathCaseXMLParser parser = new PathCaseXMLParser(repository);

                parser.loadRepositoryFromGraphXML(graphXML);

                reloadFromRepository(false);

            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Unknown exception:\n" + e, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void loadPathCaseGraphModel() {
        boolean done = false;

        if (configuration == null) done = true;

        else if (service == null) {
            String WebServiceURL = (String) configuration.get("WebServiceUrl");
            if (WebServiceURL != null && !WebServiceURL.equals(""))
                connectToService(WebServiceURL);
            else
                done = true;
        }

        if (service == null)
            done = true;

        if (!done) {

            String collapsedPathwayGuids = (String) configuration.get("collapsedPathwayGuids");
            String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
            String genericProcessGuids = (String) configuration.get("genericProcessGuids");
            String moleculeGuids = (String) configuration.get("moleculeGuids");

//            System.out.println("collapsedPathwayGuids"+collapsedPathwayGuids);
//            System.out.println("collapsedPathwayGuids"+expandedPathwayGuids);
//            System.out.println("collapsedPathwayGuids"+genericProcessGuids);
//            System.out.println("collapsedPathwayGuids"+moleculeGuids);
            
            String graphXML = service.getPathwaysServiceSoap().getGraphData(collapsedPathwayGuids, expandedPathwayGuids, genericProcessGuids, moleculeGuids,"","","","");

            PathCaseXMLParser parser = new PathCaseXMLParser(repository);

            parser.loadRepositoryFromGraphXML(graphXML);

            reloadFromRepository(false);

        }

    }

    
    public void doFirstTimeLayoutCompartmentH() {
        initialLayout();

       /*
        //layout: check parameter(whether to use frozen layout or not
        Boolean FrozenLayout = (Boolean) configuration.get("FrozenLayout");
        if (FrozenLayout != null && FrozenLayout) {
            doInitialFrozenLayout();
        } else {
            initialLayout();
        }
            */
        overviewPane.updateUI();
    }

    public void doFirstTimeLayout() {
        //layout: check parameter(whether to use frozen layout or not
        Boolean FrozenLayout = (Boolean) configuration.get("FrozenLayout");
        if (FrozenLayout != null && FrozenLayout){
            doInitialFrozenLayout();
        }else{
            initialLayout();
        }
        overviewPane.updateUI();
    }

    private void initialLayout() {
        String layout = (String) configuration.get("layout");
        String sourceNode = (String) configuration.get("sourceNode");
        if (layout != null && sourceNode != null)
            graphViewer.initialLayout(layout, sourceNode);
        else
            graphViewer.bestLayout();
    }

    public void makeCustomNodePainting(String highlightEntities) {

        //Highlighting
        DBIDToCustomFillColor = new HashMap<String, Color>();

        if (!highlightEntities.equals("")) {
            if (highlightEntities.endsWith(GUIDSEPERATOR))
                highlightEntities = highlightEntities.substring(0, highlightEntities.length() - GUIDSEPERATOR.length());

            StringTokenizer stguid = new StringTokenizer(highlightEntities, GUIDSEPERATOR);
            while (stguid.hasMoreTokens()) {
                String guidpluscolor = stguid.nextToken().toLowerCase();
                StringTokenizer stcolor = new StringTokenizer(guidpluscolor, COLORSEPERATOR);
                if (stcolor.countTokens() < 2) {
                    System.out.println("Error in higlightentities parameter: color name seperation failed : format should be guid@colorname");
                    //System.exit(1);
                }

                String guid = stcolor.nextToken().toLowerCase().trim();
                String colorname = stcolor.nextToken().toLowerCase().trim();

                DBIDToCustomFillColor.put(guid, PathCaseViewGenerator.colorNameToColorObject(colorname));
            }
        }

//        if(valueOfcompartmentH.equalsIgnoreCase("true")){
//            graphColorResetCompartmentH();
//        }else
            graphColorReset();

        graphViewer.view.updateView();
    }

    private void graphColorResetCompartmentH(){
        for (Node node : graphViewer.view.getGraph2D().getNodeArray()) {
                PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(node);
                HashSet<String> dbids = nodeToPathCaseId.get(node);

                //TODO: ignores multiple id nodes, ie, modulators
                if (dbids == null /*|| dbids.size() != 1*/)
                    continue;

                String dbid = dbids.iterator().next();

                Color customFillColor = null;

                if (dbid != null && DBIDToCustomFillColor != null && DBIDToCustomFillColor.size() > 0)
                    customFillColor = DBIDToCustomFillColor.get(dbid);

                nr.setNodeMode(PathCaseShapeNodeRealizer.PathCaseNodeMode.NORMAL);


                if (customFillColor != null || DBIDToCustomFillColor == null || DBIDToCustomFillColor.size() == 0) {
                    if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS)
                        PathCaseViewGenerator.makeGenericProcessShapeNodeRealizerNormal(nr);
                    else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY)
                        PathCaseViewGenerator.makeCollapsedPathwayShapeNodeRealizerNormal(nr);
                    else
                    if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON)
                        PathCaseViewGenerator.makeSubstrateProductShapeNodeRealizerNormal(nr);
                    else
                    if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)
                        PathCaseViewGenerator.makeCofactorShapeNodeRealizerNormal(nr);
                    else
                    if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR)
                        PathCaseViewGenerator.makeRegulatorShapeNodeRealizerNormal(nr);

                    if (customFillColor != null)
                        PathCaseViewGenerator.fillNodeRealizerCustom(nr, customFillColor);
                }
                //grayout the rest
                else if (customFillColor == null && DBIDToCustomFillColor != null && DBIDToCustomFillColor.size() > 0) {
                    /*if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS)
                        PathCaseViewGenerator.makeGenericProcessShapeNodeRealizerGrayedOut(nr);
                    else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY)
                        PathCaseViewGenerator.makeCollapsedPathwayShapeNodeRealizerGrayedOut(nr);
                    else
                    if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON)
                        PathCaseViewGenerator.makeSubstrateProductShapeNodeRealizerGrayedOut(nr);
                    else
                    if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)
                        PathCaseViewGenerator.makeCofactorShapeNodeRealizerGrayedOut(nr);
                    else
                    if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR)
                        PathCaseViewGenerator.makeRegulatorShapeNodeRealizerGrayedOut(nr);
                    */

                    if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) {
                        PathCaseViewGenerator.makeGenericProcessShapeNodeRealizerGrayedOut(nr);
                        nr.setNodeMode(PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT);
                    } else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) {
                        PathCaseViewGenerator.makeCollapsedPathwayShapeNodeRealizerGrayedOut(nr);
                        nr.setNodeMode(PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT);
                    }

                }

            }

            //wy:highlight linking entities if not grayed out
            if (DBIDToCustomFillColor == null || DBIDToCustomFillColor.size() <= 0) {
                String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
                HashSet<String> linkingMolecules = TableQueries.getLinkingMolecules(GUIDParser(expandedPathwayGuids), repository);
                for (String id : linkingMolecules) {
                    Node node = this.getNodeByPathCaseID(id);
                    if (node==null) continue;
                    PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(node);
                    if (nr.getNodeMode() != PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT && nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON) {
                        PathCaseViewGenerator.makeSubstrateProductShapeNodeRealizerLinkingMolecule(nr);
                    }
                }

/*            linkingMolecules = TableQueries.getLinkingMoleculesWithOtherPathways(GUIDParser(expandedPathwayGuids),repository);
            for (String id: linkingMolecules)
            {
                Node node = this.getNodeByPathCaseID(id);
                PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(node);
                if (nr.getNodeMode() !=PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT && nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON)
                {
                    PathCaseViewGenerator.makeSubstrateProductShapeNodeRealizerLinkingMoleculeWithOtherPathways(nr);
                }
            }*/
            }

            for (Edge edge : graphViewer.view.getGraph2D().getEdgeArray()) {
                EdgeRealizer ne = graphViewer.view.getGraph2D().getRealizer(edge);
                Node from = edge.source();
                Node to = edge.target();

                PathCaseShapeNodeRealizer nrfrom = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(from);
                PathCaseShapeNodeRealizer nrto = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(to);

                if (((nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON) && (nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY)) || ((nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) && (nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON)))
                    PathCaseViewGenerator.makeMetaboliteEdgeNormal(ne);
                    //regulator and cofactor edges are always directed to processes
                else
                if (nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)
                    PathCaseViewGenerator.makeRegulatorCofactorEdgeNormal(ne, nrfrom);
                else
                if (nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)
                    PathCaseViewGenerator.makeRegulatorCofactorEdgeNormal(ne, nrto);
                else if (nrfrom.getNodeRole() == nrto.getNodeRole())
                    PathCaseViewGenerator.makeArtificialEdgeNormal(ne);
            }

            makeGrayingOutAfterNodeRoleSetup();
    }

    private void graphColorReset() {

        for (Node node : graphViewer.view.getGraph2D().getNodeArray()) {
            PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(node);
            HashSet<String> dbids = nodeToPathCaseId.get(node);

            //TODO: ignores multiple id nodes, ie, modulators
            if (dbids == null /*|| dbids.size() != 1*/)
                continue;

            String dbid = dbids.iterator().next();

            Color customFillColor = null;


            if (dbid != null && DBIDToCustomFillColor != null)
                customFillColor = DBIDToCustomFillColor.get(dbid);


            nr.setNodeMode(PathCaseShapeNodeRealizer.PathCaseNodeMode.NORMAL);

            if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS)
                PathCaseViewGenerator.makeGenericProcessShapeNodeRealizerNormal(nr);
            else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY)
                PathCaseViewGenerator.makeCollapsedPathwayShapeNodeRealizerNormal(nr);
            else
            if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON)
                PathCaseViewGenerator.makeSubstrateProductShapeNodeRealizerNormal(nr);
            else
            if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)
                PathCaseViewGenerator.makeCofactorShapeNodeRealizerNormal(nr);
            else
            if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR)
                PathCaseViewGenerator.makeRegulatorShapeNodeRealizerNormal(nr);

            if (customFillColor != null)
                PathCaseViewGenerator.fillNodeRealizerCustom(nr, customFillColor);
        }


        for (Edge edge : graphViewer.view.getGraph2D().getEdgeArray()) {
            EdgeRealizer ne = graphViewer.view.getGraph2D().getRealizer(edge);
            Node from = edge.source();
            Node to = edge.target();

            PathCaseShapeNodeRealizer nrfrom = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(from);
            PathCaseShapeNodeRealizer nrto = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(to);

            if (((nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON) && (nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY)) || ((nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) && (nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON)))
                PathCaseViewGenerator.makeMetaboliteEdgeNormal(ne);
                //regulator and cofactor edges are always directed to processes
            else
            if (nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nrfrom.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)
                PathCaseViewGenerator.makeRegulatorCofactorEdgeNormal(ne, nrfrom);
            else
            if (nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nrto.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)
                PathCaseViewGenerator.makeRegulatorCofactorEdgeNormal(ne, nrto);
            else if (nrfrom.getNodeRole() == nrto.getNodeRole())
                PathCaseViewGenerator.makeArtificialEdgeNormal(ne);
        }
    }

    public void loadOrganismHierarchyFromRepositoryCompartmentH(String WebServiceURL, String setOrganism) {
         if (serviceM == null)
            connectToServiceCompartmentH(WebServiceURL);

        if (serviceM == null) {
            JOptionPane.showMessageDialog(this, "Cannot access to organism hierarchy web method.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (repository == null) {
            JOptionPane.showMessageDialog(this, "Cannot access to local cache.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!this.organismBrowserButton.isEnabled()) return;

        PathCaseXMLParser parser = new PathCaseXMLParser(repository);
        String organismXML = serviceM.getPathwaysServiceSoap().getOrganismHierarchy();


        parser.loadRepositoryFromOrganismXML(organismXML);

        resetOrganismHierarchyInBrowser(setOrganism);
    }

    public void loadOrganismHierarchyFromRepository(String WebServiceURL, String setOrganism) {
        if (service == null)
            connectToService(WebServiceURL);

        if (service == null)
            return;

        if (repository == null)
            return;

        if (!this.organismBrowserButton.isEnabled()) return;

        PathCaseXMLParser parser = new PathCaseXMLParser(repository);

        System.out.println("Before loading organism hierarchy...");
        String organismXML = service.getPathwaysServiceSoap().getOrganismHierarchy();
        System.out.println("Organism hierarchy length is "+organismXML.length());

        System.out.println("");

        parser.loadRepositoryFromOrganismXML(organismXML);

        resetOrganismHierarchyInBrowser(setOrganism);
        //organismSelected(organismPanel.selectedOrganismIdList);
        /*for (String s:organismPanel.selectedOrganismIdList)
        {
            System.out.println(s);
        }*/
    }

    HashSet<String> organismIDsWGeneData;


    public void loadGenomeListCompartmentH(String WebServiceURL, String expandedPathwayGuids) {
    if (serviceM == null)
        connectToServiceCompartmentH(WebServiceURL);

    if (serviceM == null) {
        JOptionPane.showMessageDialog(this, "Cannot access to gene mapping web method.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (repository == null) {
        JOptionPane.showMessageDialog(this, "Cannot access to local cache.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (!this.geneViewerButton.isEnabled()) return;

        ArrayList<String> pathwaylist = new ArrayList<String>(GUIDParser(expandedPathwayGuids));
         if (pathwaylist.size() != 1) return;
//    HashSet<String> pathwaylist = GUIDParser(expandedPathwayGuids);
//    String pathwaylistlinked = GUIDSetToString(pathwaylist);

    PathCaseXMLParser parser = new PathCaseXMLParser(repository);
    String genomepathwaylistXML = serviceM.getPathwaysServiceSoap().getGenomesForPathways(pathwaylist.get(0));
    //System.out.println(genomepathwaylistXML);
    organismIDsWGeneData = parser.loadOrganismListFromGeneMappingXML(genomepathwaylistXML);
}

    public void loadGenomeList(String WebServiceURL, String expandedPathwayGuids) {
        if (service == null)
            connectToService(WebServiceURL);

        if (service == null)
            return;

        if (repository == null)
            return;

        if (!this.geneViewerButton.isEnabled()) return;

        ArrayList<String> pathwaylist = new ArrayList<String>(GUIDParser(expandedPathwayGuids));
        if (pathwaylist.size() != 1) return;

        PathCaseXMLParser parser = new PathCaseXMLParser(repository);
        System.out.println("Before getting genomepathwaylistXML....Message");
        String genomepathwaylistXML = service.getPathwaysServiceSoap().getGenomesForPathway(pathwaylist.get(0));
        System.out.println("genomepathwaylistXML's length is "+ genomepathwaylistXML.length() +"...Message");

        //System.out.println(genomepathwaylistXML);
        organismIDsWGeneData = parser.loadOrganismListFromGeneMappingXML(genomepathwaylistXML);
    }

    /////////////////////////////////////////    UTILITIES   ////////////////////////////////////////

    public Node getNodeByPathCaseID(String id) {
        //TODO use index instead of exhaustive search
        for (Node n : graphViewer.view.getGraph2D().getNodeArray()) {
            HashSet<String> pid = nodeToPathCaseId.get(n);
            if (pid != null && pid.contains(id))
                return n;
        }

        return null;
    }

    private HashSet<String> GUIDParser(String guidlist) {
        HashSet<String> guidlistparsed = new HashSet<String>();
        if (guidlist == null) return guidlistparsed;

        StringTokenizer st = new StringTokenizer(guidlist, GUIDSEPERATOR);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim().toLowerCase();
            if (!token.equals(""))
                guidlistparsed.add(token);
        }
        return guidlistparsed;
    }

    private String GUIDSetToString(HashSet<String> Guids) {
        boolean firstTime = true;
        String output = "";
        for (String id : Guids) {
            if (!firstTime) {
                output += GUIDSEPERATOR;
            } else {
                firstTime = false;
            }
            output += id;
        }

        return output;
    }

    private void reloadFromRepository(boolean doLayout) {
        graphViewer.resetView();
        createGraphFromRepository(graphMode);

        String highlightEntities = (String) configuration.get("highlightEntities");
        makeCustomNodePainting(highlightEntities);

        graphViewer.view.updateView();

//        if(this.valueOfcompartmentH.equalsIgnoreCase("true")){
            if (doLayout && graphMode.isDefault())
                doFirstTimeLayout();
            else if (doLayout)
                graphViewer.bestLayout();                        
//        }else{
//            if (doLayout)
//                graphViewer.bestLayout();
//        }
    }

    private void reLoadGraph() {

        graphViewer.resetView();
        repository = new PathCaseRepository();
        graphViewer.view.updateView();

        configuration.put("setOrganism", OrganismTable.ROOTID);

        processAppletParameters();
    }

    private void createGraphFromRepository(PathCaseViewMode mode) {
//        nodeToPathCaseId = PathCaseViewGenerator.createGraphFromWholeRepository(repository, graphViewer.view.getGraph2D(), mode);
//        if(valueOfcompartmentH.equalsIgnoreCase("true"))
//             nodeToPathCaseId = PathCaseViewGenerator.createGraphFromWholeRepositoryCompartmentH(repository, graphViewer.view.getGraph2D(), mode);
//         else
            nodeToPathCaseId = PathCaseViewGenerator.createGraphFromWholeRepository(repository, graphViewer.view.getGraph2D(), mode);
        PathCaseIDToNameMap = TableQueries.getPathCaseIDToNameMapForPathway(repository, graphViewer.view, nodeToPathCaseId);
    }

    private void sleepSafe(long time) {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    protected void createLoaderDialog(String message) {
        statusBarText.setText(message);
        add(statusBar, BorderLayout.NORTH);
        statusBar.setEnabled(true);
        statusBar.setVisible(true);
        itemsToolBar.setEnabled(false);
        itemsToolBar.setVisible(false);
        //repaint();
    }

    public void killLoaderDialog() {
        add(itemsToolBar, BorderLayout.NORTH);
        statusBar.setEnabled(false);
        statusBar.setVisible(false);
        itemsToolBar.setEnabled(true);
        itemsToolBar.setVisible(true);
        //repaint();
    }

    public String getRedirectionString(String itemID, String itemType, String query, boolean DisplayGO, boolean organismdependentquery) {
        String defaultvalue = "";

        if (applet == null || configuration == null)
            return defaultvalue;

        String setOrganism;
        //selected organism fix
        /////////////////////////////////////////////////////TODO: give warning if multiple organism
        HashSet<String> setorganismset = organismPanel.collectselectedleaves();
        //System.out.println(setorganismset.size());
        if (organismdependentquery && !setorganismset.contains(OrganismTable.ROOTID) && (setorganismset.size() > 1 || setorganismset.size() == 0)) {
            JOptionPane.showMessageDialog(this, "You should select exactly one organism to make a query on the graph.", "Warning", JOptionPane.WARNING_MESSAGE);
            return defaultvalue;
        } else if (!setorganismset.contains(OrganismTable.ROOTID)) {
            setOrganism = (String) setorganismset.toArray()[0];
        } else {
            setOrganism = OrganismTable.ROOTID;
        }

        //////////////////////////


        String RedirectionWebPageUrl = (String) configuration.get("RedirectionWebPageUrl");
        //String setOrganism = configuration.get("setOrganism");

        String viewID = (String) configuration.get("viewID");
        String terms = (String) configuration.get("terms");
        String type = (String) configuration.get("type");
        String page = (String) configuration.get("page");

        String urlextension;

        if (!setOrganism.equals(""))
            urlextension = "?viewID=" + viewID + "&query=" + query + "&itemID=" + itemID + "&itemType=" + itemType + "&DisplayGO=" + DisplayGO + "&organismName=" + setOrganism;
        else
            urlextension = "?viewID=" + viewID + "&query=" + query + "&itemID=" + itemID + "&itemType=" + itemType + "&DisplayGO=" + DisplayGO + "&organismName=" + OrganismTable.ROOTID;

        if (terms != null && !terms.equals("")) urlextension += "&terms=" + terms;
        if (type != null && !type.equals("")) urlextension += "&type=" + type;
        if (page != null && !page.equals("")) urlextension += "&page=" + page;

        String wholeredirectionString = defaultvalue;
        if ((RedirectionWebPageUrl != null)) {
            RedirectionWebPageUrl = RedirectionWebPageUrl.replace('\\', '/');
            wholeredirectionString = RedirectionWebPageUrl + urlextension;
        } else {
            System.out.println("REDIRECTION URL IS NULL");
            return defaultvalue;
        }

        return wholeredirectionString;

    }

    private void PageRedirect(String itemID, String itemType, String query, boolean DisplayGO, boolean organismdependentquery) {

        if (applet == null || configuration == null)
            return;


        String wholeredirectionString = getRedirectionString(itemID, itemType, query, DisplayGO, organismdependentquery);
        Boolean rightClickQueryingEnabled = (Boolean) configuration.get("rightClickQueryingEnabled");

        try {
            final URL redirectURL = new URL(wholeredirectionString);

            // if (checkAppletContext()) {
            if (rightClickQueryingEnabled) {

                pool.submit(new Thread() {
                    public void run() {

                        while (true) {
                            System.out.println("Trying to redirect to: " + redirectURL);

                            if (applet != null && applet.getAppletContext() != null) {
                                applet.getAppletContext().showDocument(redirectURL);
                                System.out.println("Redirection is done");
                                break;
                            }

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                //e.printStackTrace();
                            }
                        }

                    }
                });
                //JSObject win = JSObject.getWindow(redirectorApplet);
                //win.setMember("location", redirectURL);
            } else {
                applet.getAppletContext().showDocument(redirectURL, "_blank");
            }
            //}

        }
        catch (MalformedURLException murle) {
            System.out.println("Could not recognise redirect URL");
        }
    }

    //////////////////////////////////  Access from the graph viewer /////////////////////////////

    public JPopupMenu getModesMenu() {
        JPopupMenu menu = new JPopupMenu();

        final JCheckBoxMenuItem item1 = new JCheckBoxMenuItem("Display Common Molecules", graphMode.showcommonmoleculesingraph);
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                graphMode.showcommonmoleculesingraph = item1.getState();
                reloadFromRepository(true);
            }
        });
        menu.add(item1);

        final JCheckBoxMenuItem item2 = new JCheckBoxMenuItem("Display Process Modulators", graphMode.showmodulatorsingraph);
        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                graphMode.showmodulatorsingraph = item2.getState();
                reloadFromRepository(true);
            }
        });
        menu.add(item2);

        final JCheckBoxMenuItem item3 = new JCheckBoxMenuItem("Display Linking Pathways", graphMode.showlinkingpathwaysingraph);
        item3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                graphMode.showlinkingpathwaysingraph = item3.getState();
                reloadFromRepository(true);
            }
        });
        menu.add(item3);


        JMenuItem item = new JMenuItem("Display Everything");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                graphMode.showcommonmoleculesingraph = true;
                graphMode.showmodulatorsingraph = true;
                graphMode.showlinkingpathwaysingraph = true;
                reloadFromRepository(true);
            }
        });
        menu.add(item);

        item = new JMenuItem("Reset Graph to Initial View");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                graphMode.defaultValues();
                reloadFromRepository(false);
                doFirstTimeLayout();
            }
        });
        menu.add(item);

        /*
        item = new JMenuItem("Display Enzyme-only Graph");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                reloadFromRepository(graphMode.ENZYME);
            }
        });
        menu.add(item);

        item = new JMenuItem("Display Metabolite-only Graph");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                reloadFromRepository(graphMode.METABOLITE);
            }
        });
        menu.add(item);
        */

        return menu;
    }

    public JMenu getPathCasePaperPopupQueries() {
        JMenu menu = new JMenu("PathCase Queries");

        Boolean loadFromBioPAX = (Boolean) configuration.get("loadFromBioPAX");
        Boolean rightClickQueryingEnabled = (Boolean) configuration.get("rightClickQueryingEnabled");
        if (loadFromBioPAX || !rightClickQueryingEnabled)
            return null;

        if (configuration != null) {
            String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
            HashSet<String> expandedPathwayGuidsForDisplay = GUIDParser(expandedPathwayGuids);

            if (expandedPathwayGuidsForDisplay != null && expandedPathwayGuidsForDisplay.size() > 0)
                for (final String expandedId : expandedPathwayGuidsForDisplay) {
                    String pathwayName = TableQueries.getPathwayNameById(repository, expandedId);
                    JMenuItem item = new JMenuItem("Collapse pathway " + pathwayName);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            collapsePathwayByID(expandedId);
                        }
                    });
                    menu.add(item);
                }
        }

        return menu;
    }

    public JMenu getPathCaseNodePopupQueries(Node v) {
        JMenuItem item;
        JMenu menu = new JMenu("PathCase Queries");


        Boolean loadFromBioPAX = (Boolean) configuration.get("loadFromBioPAX");
        Boolean rightClickQueryingEnabled = (Boolean) configuration.get("rightClickQueryingEnabled");
        if (loadFromBioPAX || !rightClickQueryingEnabled)
            return null;

        PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(v);
        if (nr.getNodeMode().equals(PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT)) {
            return null;
        }

        boolean singlePathwayInGraph = false;

        if (configuration != null) {
            String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
            HashSet<String> expandedPathwayGuidsForDisplay = GUIDParser(expandedPathwayGuids);
            if (expandedPathwayGuidsForDisplay != null && expandedPathwayGuidsForDisplay.size() == 1)
                singlePathwayInGraph = true;
        }

        //TODO: handle only single name/id
        final HashSet<String> dbids = nodeToPathCaseId.get(v);

        if (dbids == null) {
            System.out.println("nodeToPathCaseId is null");
        } else for (Iterator it = dbids.iterator(); it.hasNext();) {
            final String dbid = (String) it.next();

            String nodename = PathCaseIDToNameMap.get(dbid);
            if (nodename == null) nodename = nr.getLabelText();


            if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR) || nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR)) {

                item = new JMenuItem("Details of " + nodename);
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        PageRedirect(dbid, "MolecularEntity", "000", false, false);
                    }
                });

                menu.add(item);

                item = new JMenuItem("Pathways/Processes involving " + nodename + "  with a specific use");
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        PageRedirect(dbid, "MolecularEntity", "009", false, true);
                    }
                });
                menu.add(item);


                if (singlePathwayInGraph) {
                    menu.add(item);
                    item = new JMenuItem("Processes within a given number of steps from " + nodename + " in this pathway");
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            PageRedirect(dbid, "MolecularEntity", "004", false, true);
                        }
                    });
                    menu.add(item);

                    item = new JMenuItem("Find paths between " + nodename + " and another molecule in this pathway");
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            PageRedirect(dbid, "MolecularEntity", "011", false, true);
                        }
                    });
                    menu.add(item);
                }

            } else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) {
                item = new JMenuItem("Details of " + nodename);
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        PageRedirect(dbid, "Process", "000", false, false);
                    }
                });
                menu.add(item);

                if (singlePathwayInGraph) {
                    item = new JMenuItem("Processes within a given number of steps from " + nodename + " in this pathway");
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            PageRedirect(dbid, "Process", "003", false, true);
                        }
                    });
                    menu.add(item);
                }

                item = new JMenuItem("Processes within a given number of steps from " + nodename + " in the metabolic network");
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        PageRedirect(dbid, "Process", "012", false, true);
                    }
                });
                menu.add(item);

                if (singlePathwayInGraph) {
                    item = new JMenuItem("Processes sharing activators and inhibitors with " + nodename + " in this pathway ");
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            PageRedirect(dbid, "Process", "005", false, true);
                        }
                    });
                    menu.add(item);
                }

                item = new JMenuItem("Show genes for process " + nodename);
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (!geneViewerButton.isSelected()) {
                            geneViewerButton.setSelected(true);
                            geneViewerDisplayAction();
                        }
                        String dbid_conv = TableQueries.getGenericProcessEntityIdbyGenericProcessID(repository, dbid);
                        geneViewerPanel.highlightGenesForGenericProcess(dbid_conv);
                    }
                });
                menu.add(item);

            } else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) {

                item = new JMenuItem("Details of " + nodename);

                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        PageRedirect(dbid, "Pathway", "000", false, false);
                    }
                });
                menu.add(item);
                item = new JMenuItem("Pathways within a given number of steps from " + nodename);
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        PageRedirect(dbid, "Pathway", "002", false, true);
                    }
                });
                menu.add(item);


                item = new JMenuItem("Expand " + nodename + " in this graph");
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        expandPathwayByID(dbid);
                    }
                });
                menu.add(item);
            }

            if (it.hasNext())
                menu.addSeparator();
        }
        return menu;
    }

    public String getNodeTipText(Node v) {
        if (PathCaseIDToNameMap == null) return "";
        if (nodeToPathCaseId == null) return "";

        PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(v);
        String fullname = "";

        HashSet<String> gpipset = nodeToPathCaseId.get(v);
        int i = 0;
        if (gpipset != null)
            for (String gpid : gpipset) {


                String name = PathCaseIDToNameMap.get(gpid);
                if (name == null || name.equals("")) name = "Unknown";

                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON)
                    name = "<b>METABOLITE: </b>" + name;
                else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) {
                    name = "<b>ENZYME: </b><i>" + name + "</i>";

                    if (gpid != null) {
                        HashSet<String> cofactorNames = TableQueries.getCofactorTypedNamesByGenericProcessID(gpid, repository);
                        HashSet<String> regulatorNames = TableQueries.getRegulatorTypedNamesByGenericProcessID(gpid, repository);

                        if (cofactorNames != null && cofactorNames.size() > 0) {
                            name += "<br><b>COFACTORS:</b><font size=-1 color=\"red\">";
                            for (String mname : cofactorNames) {
                                name += " " + mname;
                            }
                            name += "</font>";
                        }

                        if (regulatorNames != null && regulatorNames.size() > 0) {
                            name += "<br><b>REGULATORS:</b><font size=-1 color=\"blue\">";
                            for (String mname : regulatorNames) {
                                name += " " + mname;
                            }
                            name += "</font>";
                        }

                    }
                } else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY)
                    name = "<b>PATHWAY: </b>" + name;
                else
                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR) name = "COFACTOR: " + name;
                else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN)
                    name = "<b>COFACTOR IN: </b>" + name;
                else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)
                    name = "<b>COFACTOR OUT: </b>" + name;
                else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR)
                    name = "<b>REGULATOR: </b>" + name;
                else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR)
                    name = "<b>INHIBITOR: </b>" + name;
                else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR)
                    name = "<b>ACTIVATOR: </b>" + name;

                if (i < gpipset.size() - 1) fullname += name + "<br>";
                else fullname += name;
                i++;
            }
        return "<html><body>" + fullname + "</body></html>";
    }

    ///////////////////////////////    EXPAND COLLAPSE PATHWAY /////////////////////////////////////

    private void collapsePathwayByID(String dbid) {

        if (configuration == null)
            return;

        String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
        HashSet<String> expandedPathwayGuidsForDisplay = GUIDParser(expandedPathwayGuids);

        String collapsedPathwayGuids = (String) configuration.get("collapsedPathwayGuids");
        HashSet<String> collapsedPathwayGuidsForDisplay = GUIDParser(collapsedPathwayGuids);

        //String genericProcessGuids = (String) configuration.get("genericProcessGuids");
        //String moleculeGuids = (String) configuration.get("moleculeGuids");

        //HashSet<String> moleculeGuidsForDisplay = GUIDParser(moleculeGuids);
        //HashSet<String> genericProcessGuidsForDisplay = GUIDParser(genericProcessGuids);

        //comment to be removed if safe expand/collapse is required
        /*if (*/
        expandedPathwayGuidsForDisplay.remove(dbid);/*)*/
        {
            collapsedPathwayGuidsForDisplay.add(dbid);

            configuration.put("expandedPathwayGuids", GUIDSetToString(expandedPathwayGuidsForDisplay));
            configuration.put("collapsedPathwayGuids", GUIDSetToString(collapsedPathwayGuidsForDisplay));

            //System.out.println(dbid);

            reLoadGraph();
            //loadOrganismHierarchyFromRepository(OrganismTable.ROOTID);
            callCollapseJS(dbid);
        }
    }

    private void expandPathwayByID(String dbid) {

        String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
        HashSet<String> expandedPathwayGuidsForDisplay = GUIDParser(expandedPathwayGuids);

        String collapsedPathwayGuids = (String) configuration.get("collapsedPathwayGuids");
        HashSet<String> collapsedPathwayGuidsForDisplay = GUIDParser(collapsedPathwayGuids);

        //comment to be removed if safe expand/collapse is required
        /*if (*/
        collapsedPathwayGuidsForDisplay.remove(dbid);/*)*/
        {
            expandedPathwayGuidsForDisplay.add(dbid);

            configuration.put("expandedPathwayGuids", GUIDSetToString(expandedPathwayGuidsForDisplay));
            configuration.put("collapsedPathwayGuids", GUIDSetToString(collapsedPathwayGuidsForDisplay));

            reLoadGraph();
            callExpandJS(dbid);
        }
    }

    private void callExpandJS(String collapsedPathwayGuid) {

        //TODO:  callExpandJS
        /*
        try {
            JSObject.getWindow(this.redirectorApplet).eval("AjaxFunctions.UpdateSessionExpandPathway('" + collapsedPathwayGuid + "')");
        }
        catch (Exception me) {
            System.out.println("Expand pathway javascript function failed");
        }
        */
    }

    private void callCollapseJS(String expandedPathwayGuid) {
        //TODO: callCollapseJS
        /*
        try {
            JSObject.getWindow(this.redirectorApplet).eval("AjaxFunctions.UpdateSessionCollapsePathway('" + expandedPathwayGuid + "')");
        }
        catch (Exception me) {
            System.out.println("Collapse pathway javascript function failed");
        }
        */
    }

    ///////////////////////////////    Organism and Gene Viewer Related  /////////////////////////////////////

    public void resetOrganismHierarchyInBrowser(String setOrganism) {
        OrganismCheckNode rootOrganism = new OrganismCheckNode("Unspecified", OrganismTable.ROOTID);

        HashSet<String> collapsedpathwayitemidsingraph = new HashSet<String>();
        HashSet<String> genericprocessitemidsingraph = new HashSet<String>();


        for (Node node : graphViewer.view.getGraph2D().getNodeArray()) {
            PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(node);
            if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY)) {
                collapsedpathwayitemidsingraph.add(nodeToPathCaseId.get(node).iterator().next());
            } else if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS)) {
                genericprocessitemidsingraph.add(nodeToPathCaseId.get(node).iterator().next());
            }
        }

        HashSet<String> orgidsingraph = new HashSet<String>();
        orgidsingraph.addAll(TableQueries.getOrganismIdsInAListOfGenericProcessEntities(repository, genericprocessitemidsingraph));
        orgidsingraph.addAll(TableQueries.getOrganismIdsInAListOfCollapsedPathwayEntities(repository, collapsedpathwayitemidsingraph));

        HashSet<String> usefulorgidsinhierarchy = TableQueries.getUsefulIdsInOrganismHierarchy(repository, orgidsingraph);
        PathCaseViewGenerator.createOrganismHierarchyFromRepository(repository, rootOrganism, usefulorgidsinhierarchy);

        organismPanel.initwithRoot(rootOrganism);
        organismPanel.root.setSelected(true, true);
        organismPanel.selectGivenNode(setOrganism);
    }

    public void loadGenomeDataForOrganism(String selectedOrganismId) {
        if (configuration != null) {
            String WebServiceURL = (String) configuration.get("WebServiceUrl");
            loadGenomeDataForOrganism(WebServiceURL, selectedOrganismId);
        }
    }

    public void loadGenomeDataForOrganism(String WebServiceURL, String selectedOrganismId) {
        if(((String)configuration.get("graphContent")).equalsIgnoreCase("model")){
                if (serviceM == null)
                connectToServiceCompartmentH(WebServiceURL);

            if (serviceM == null)
                return;

            if (repository == null)
                return;

            createLoaderDialog("Loading Genome Data From PathCase Web Service");

            PathCaseXMLParser parser = new PathCaseXMLParser(repository);
            //parse expanded pathway id from here...

            String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
            HashSet<String> expandedPathwayGuidsForDisplay = GUIDParser(expandedPathwayGuids);
            String pathwaylistlinked = GUIDSetToString(expandedPathwayGuidsForDisplay);


            if (expandedPathwayGuidsForDisplay != null) {
                String genomeXML = service.getPathwaysServiceSoap().getGeneMappingForOrganismPathways(pathwaylistlinked, selectedOrganismId);

                parser.loadRepositoryFromGeneXML(genomeXML);
            }
        }else{
        if (service == null)
            connectToService(WebServiceURL);

        if (service == null)
            return;

        if (repository == null)
            return;

        createLoaderDialog("Loading Genome Data From PathCase Web Service");

        PathCaseXMLParser parser = new PathCaseXMLParser(repository);
        //parse expanded pathway id from here...

        String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
        HashSet<String> expandedPathwayGuidsForDisplay = GUIDParser(expandedPathwayGuids);

        if (expandedPathwayGuidsForDisplay != null && expandedPathwayGuidsForDisplay.size() == 1) {
            String genomeXML = service.getPathwaysServiceSoap().getGeneMappingForOrganismPathway(expandedPathwayGuidsForDisplay.iterator().next(), selectedOrganismId);
            parser.loadRepositoryFromGeneXML(genomeXML);
        }

        }
      /*  String pathwaylistlinked = GUIDSetToString(expandedPathwayGuidsForDisplay);
         if (expandedPathwayGuidsForDisplay != null) {
            String genomeXML = service.getPathwaysServiceSoap().getGeneMappingForOrganismPathways(pathwaylistlinked, selectedOrganismId);

            parser.loadRepositoryFromGeneXML(genomeXML);
        }*///test by Xinjian

        killLoaderDialog();
        //
    }

    public void organismSelected(HashSet<String> selectedOrganismIdList) {

        graphColorReset();

        if (selectedOrganismIdList == null) return;

        //grayout generic processes
        for (Node node : graphViewer.view.getGraph2D().getNodeArray()) {

            HashSet<String> idset = nodeToPathCaseId.get(node);
            if (idset==null) continue;
            String dbid = idset.iterator().next();
            HashSet<String> orgidsinlist = new HashSet<String>();

            PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(node);
            if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY)) {
                HashSet<String> orgidsinrepositorybycollapsedpathways = TableQueries.getOrganismGroupIDListByCollapsedPathwayId(repository, dbid);
                if (orgidsinrepositorybycollapsedpathways != null)
                    orgidsinlist.addAll(orgidsinrepositorybycollapsedpathways);
                else
                    orgidsinlist.add(OrganismTable.UNKNOWNID);
            } else if (nr.getNodeRole().equals(PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS)) {
                HashSet<String> orgidsinrepositorybygenericprocesses = TableQueries.getOrganismGroupIDListByGenericProcessId(repository, dbid);
                if (orgidsinrepositorybygenericprocesses != null)
                    orgidsinlist.addAll(orgidsinrepositorybygenericprocesses);
                else
                    orgidsinlist.add(OrganismTable.UNKNOWNID);
            }

            boolean containsany = false;

            if (selectedOrganismIdList.contains(OrganismTable.ROOTID))
                containsany = true;
            else
                for (String orgid : orgidsinlist) {
                    if (selectedOrganismIdList.contains(orgid)) {
                        containsany = true;
                        break;
                    }
                    //System.out.print(orgid+" ");
                }

            //System.out.println();

            if (!containsany) {


                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) {
                    PathCaseViewGenerator.makeGenericProcessShapeNodeRealizerGrayedOut(nr);
                    nr.setNodeMode(PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT);
                } else if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) {
                    PathCaseViewGenerator.makeCollapsedPathwayShapeNodeRealizerGrayedOut(nr);
                    nr.setNodeMode(PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT);
                }
                /*else if (nr.getNodeRole()==PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT)
                    PathCaseViewGenerator.makeSubstrateProductShapeNodeRealizerGrayedOut(nr);*/

            }
        }

        //grayout substrates and products
        for (Node node : graphViewer.view.getGraph2D().getNodeArray()) {

            PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(node);
            if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR) {
                EdgeCursor edgeiter = node.edges();
                int countgrayedout = 0;
                //int alledgecount = edgeiter.size();
                int alledgecount = 0;

                for (int i = 0; i < edgeiter.size(); i++) {
                    Edge edge = edgeiter.edge();
                    Node otherend = edge.opposite(node);
                    PathCaseShapeNodeRealizer nrother = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(otherend);

                    if ((nrother.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS || nrother.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) && nrother.getNodeMode() == PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT) {
                        countgrayedout++;
                    }

                    if ((nrother.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS || nrother.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY)) {
                        alledgecount++;
                    }
                    edgeiter.next();
                }
                if (countgrayedout == alledgecount && alledgecount > 0) {
                    nr.setNodeMode(PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT);
                    if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON)
                        PathCaseViewGenerator.makeSubstrateProductShapeNodeRealizerGrayedOut(nr);
                    else
                    if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)
                        PathCaseViewGenerator.makeCofactorShapeNodeRealizerGrayedOut(nr);
                    else
                    if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR)
                        PathCaseViewGenerator.makeRegulatorShapeNodeRealizerGrayedOut(nr);
                }
                //System.out.println(countgrayedout+" "+alledgecount);
            }
        }

        //grayout edges
        for (Edge edge : graphViewer.view.getGraph2D().getEdgeArray()) {
            Node from = edge.source();
            Node to = edge.target();

            PathCaseShapeNodeRealizer nrfrom = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(from);
            PathCaseShapeNodeRealizer nrto = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(to);
            EdgeRealizer ne = graphViewer.view.getGraph2D().getRealizer(edge);

            if ((nrfrom.getNodeMode() == PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT || nrto.getNodeMode() == PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT) || (nrfrom.getNodeRole() == nrto.getNodeRole() && (nrfrom.getNodeMode() == PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT || nrto.getNodeMode() == PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT))) {
                PathCaseViewGenerator.makeEdgeGrayedOut(ne);
            }

        }

        // repository.genericProcessToSpecificProcessListTable.
        /*for (String organismId : selectedOrganismIdList) {
            System.out.print(organismId + " ");
        }
        System.out.println();*/

        loadGeneViewerOrganismListFromOrgIds(selectedOrganismIdList);

        graphViewer.view.updateView();
        overviewPane.updateUI();
    }

    private void makeGrayingOutAfterNodeRoleSetup() {
//grayout substrates and products
         for (Node node : graphViewer.view.getGraph2D().getNodeArray()) {

             PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(node);
             if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR) {
                 EdgeCursor edgeiter = node.edges();
                 int countgrayedout = 0;
                 //int alledgecount = edgeiter.size();
                 int alledgecount = 0;

                 for (int i = 0; i < edgeiter.size(); i++) {
                     Edge edge = edgeiter.edge();
                     Node otherend = edge.opposite(node);
                     PathCaseShapeNodeRealizer nrother = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(otherend);

                     if ((nrother.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS || nrother.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) && nrother.getNodeMode() == PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT) {
                         countgrayedout++;
                     }

                     if ((nrother.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS || nrother.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY)) {
                         alledgecount++;
                     }
                     edgeiter.next();
                 }
                 if (countgrayedout == alledgecount && alledgecount > 0) {
                     nr.setNodeMode(PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT);
                     if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON)
                         PathCaseViewGenerator.makeSubstrateProductShapeNodeRealizerGrayedOut(nr);
                     else
                     if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT)
                         PathCaseViewGenerator.makeCofactorShapeNodeRealizerGrayedOut(nr);
                     else
                     if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR || nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR)
                         PathCaseViewGenerator.makeRegulatorShapeNodeRealizerGrayedOut(nr);
                 }
                 //System.out.println(countgrayedout+" "+alledgecount);
             }
         }

         //grayout edges
         for (Edge edge : graphViewer.view.getGraph2D().getEdgeArray()) {
             Node from = edge.source();
             Node to = edge.target();

             PathCaseShapeNodeRealizer nrfrom = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(from);
             PathCaseShapeNodeRealizer nrto = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(to);
             EdgeRealizer ne = graphViewer.view.getGraph2D().getRealizer(edge);

             if ((nrfrom.getNodeMode() == PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT || nrto.getNodeMode() == PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT) || (nrfrom.getNodeRole() == nrto.getNodeRole() && (nrfrom.getNodeMode() == PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT || nrto.getNodeMode() == PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT))) {
                 PathCaseViewGenerator.makeEdgeGrayedOut(ne);
             }

         }

     }


    public void geneSelectProcessesandCollapsedPathways(HashSet<GenomeTable.GeneEntry> selectedGenes) {


        for (Node node : graphViewer.view.getGraph2D().getNodeArray()) {
            String dbid = nodeToPathCaseId.get(node).iterator().next();
            Color customFillColor = null;

            if (dbid != null && DBIDToCustomFillColor != null)
                customFillColor = DBIDToCustomFillColor.get(dbid);


            PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(node);
            if (nr.getNodeMode() != PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT) {
                //nr.setNodeMode(PathCaseShapeNodeRealizer.PathCaseNodeMode.NORMAL);
                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS)
                    PathCaseViewGenerator.makeGenericProcessShapeNodeRealizerNormal(nr);
            }

            if (customFillColor != null)
                PathCaseViewGenerator.fillNodeRealizerCustom(nr, customFillColor);
        }

        if (selectedGenes == null) {
            graphViewer.view.updateView();
            return;
        }

        //System.out.println(selectedGenes.size());

        HashSet<String> gpidsofgenes = new HashSet<String>();
        for (GenomeTable.GeneEntry gene : selectedGenes) {
            gpidsofgenes.add(gene.genericprocessId);
        }

        boolean centered = false;
        for (Node node : graphViewer.view.getGraph2D().getNodeArray()) {
            PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(node);
            if (nr.getNodeMode() != PathCaseShapeNodeRealizer.PathCaseNodeMode.GRAYED_OUT) {

                String nodeid = nodeToPathCaseId.get(node).iterator().next();
                //System.out.println(nodeid);
                String dbid = TableQueries.getGenericProcessEntityIdbyGenericProcessID(repository, nodeid);

                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS && gpidsofgenes.contains(dbid)) {
                    //System.out.println("highlighted");
                    PathCaseViewGenerator.makeGenericProcessShapeNodeRealizerGeneSelected(nr);
                    if (!centered) {
                        graphViewer.view.setCenter(graphViewer.view.getGraph2D().getCenterX(node), graphViewer.view.getGraph2D().getCenterY(node));
                        centered = true;
                    }
                }
            }
        }

        graphViewer.view.updateView();
    }

    public void loadGeneViewerOrganismListFromOrgIds(HashSet<String> orgidlist) {
        if (orgidlist == null) return;

        HashSet<String> neworglist = new HashSet<String>(orgidlist);

        if (organismIDsWGeneData != null)
            neworglist.retainAll(organismIDsWGeneData);

        HashMap<String, String> OrganismSimplifiedNamesToId = TableQueries.getOrganismSimplifiedNamesToId(repository, neworglist);
        if (OrganismSimplifiedNamesToId == null) return;
        geneViewerPanel.updateOrganismNameToIdMap(OrganismSimplifiedNamesToId);
    }

    ///////////////////////////////////////// YUAN's PART LAYOUT:    ///////////////////////////////////////////////

    /**layout: To choose freezed layout scheme
     * The graph being visualized doesn't have the information about pathways being invovled,
     * so currently, we can only use the LayoutInfo objects to get that.
     */

    public void doInitialFrozenLayout() {
         //TODO: only dealing with expanded Pathways now
         int np = numberOfPathways();
         if (np < 1) {
             initialLayout();
             PathCaseLayouter.SALabeling().doLayout(graphViewer.view.getGraph2D());
             graphViewer.view.fitContent();
             overviewPane.updateUI();
             return;
         }

         LayoutInfo[] layouts = new LayoutInfo[np];
         String[] pathways = new String[np];
         String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
         for (int i = 0; i < np; i++) {
             String pathwayId = GUIDParser(expandedPathwayGuids).toArray()[i].toString();
             pathways[i] = TableQueries.getPathwayNameById(repository, pathwayId);
             String layout = service.getPathwaysServiceSoap().retrieveLayout("", pathwayId, "", "");
             if (layout.length() < 10) {//since layout is in xml format, the length should be larger than 10
                 if (layout.length() > 0) {
                     System.err.println("Mal-format layout for pathway " + pathways[i] + ", so use an auto-layout for it");
                 } else {
                     System.out.println(pathways[i] + " has no layout saved in our database, so use an auto-layout for it");
                 }
                 layouts[i] = getAutoGeneratedLayoutInfo(pathwayId, "organic");
                 continue;
             }

             try {
                 layouts[i] = new LayoutParser().getParsedXMLInfo(layout);
             } catch (Exception e) {
                 System.out.println(e);
                 System.out.println(pathways[i] + " has mal-layout saved in our database, so use an auto-layout for it");
                 layouts[i] = getAutoGeneratedLayoutInfo(pathwayId, "organic");
             }

             //System.out.println(pathways[i]);
         }

         applyFrozenLayout(layouts, pathways);
         PathCaseLayouter.SALabeling().doLayout(graphViewer.view.getGraph2D());
         System.out.println(np);
         graphViewer.view.fitContent();
         overviewPane.updateUI();
     }

//    public void doInitialFrozenLayoutModel() {
//        //TODO: only dealing with expanded Pathways now
//        int np = numberOfPathways();
//        if(np <1){
//            initialLayout();
//            PathCaseLayouter.SALabeling().doLayout(graphViewer.view.getGraph2D());
//            graphViewer.view.fitContent();
//            return;
//        }
//
//        LayoutInfo[] layouts = new LayoutInfo[np];
//        String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
//        for (int i = 0; i < np; i++) {
//            String layout = service.getPathwaysServiceSoap().retrieveLayout("", GUIDParser(expandedPathwayGuids).toArray()[i].toString(), "", "");
//            layouts[i] = new LayoutParser().getParsedXMLInfo(layout);
//        }
//         applyFrozenLayout(layouts);
//        PathCaseLayouter.SALabeling().doLayout(graphViewer.view.getGraph2D());
//        graphViewer.view.fitContent();
//    }

     private LayoutInfo getAutoGeneratedLayoutInfo(String pathwayId, String layoutType) {
        Graph2D tempgraph = new Graph2D();
        HashMap<Node, HashSet<String>> tempNodeToPathCaseId = PathCaseViewGenerator.createGraphForASinglePathway(repository, tempgraph, pathwayId, false, true, false);
        (new PathCaseLayouter(layoutType)).start(tempgraph);
        return getLayoutInfo(tempgraph, tempNodeToPathCaseId);
    }
    //get how many expanded pathways in the graph, return -1 if there're nodes not belong to the pathways
    private int numberOfPathways() {
        String collapsedPathwayGuids = (String) configuration.get("collapsedPathwayGuids");
        String expandedPathwayGuids = (String) configuration.get("expandedPathwayGuids");
        String genericProcessGuids = (String) configuration.get("genericProcessGuids");
        String moleculeGuids = (String) configuration.get("moleculeGuids");
        if (GUIDParser(collapsedPathwayGuids).size() == 0 && GUIDParser(genericProcessGuids).size() == 0 && GUIDParser(moleculeGuids).size() == 0) {
            return GUIDParser(expandedPathwayGuids).size();
        } else {
            return -1;
        }
    }

    private LayoutInfo getLayoutInfo(Graph2D view, HashMap<Node, HashSet<String>> tmpnodeToPathCaseId) {
            HashMap<NodeRealizer, String> nodeTORelatedProcessGUID = new HashMap<NodeRealizer, String>(); //filled during the first part, traversing nodes
            String layout = "";
            //Graph2D view = graphViewer.view.getGraph2D();
            /**
             * Own Designed Compact Layout String Structure:
             * First part is for nodes, then an empty line followed by the part for edges:
             * {[ProcessGUID | null][comma][GUID][comma][cofactor| ][comma][centerX][comma][centerY]\n}*
             * \n
             * {[SourceNodeProcessGUID | null][comma][SourceNodeGUID][comma][TargetNodeProcessGUID | null][comma][TargetNodeGUID][comma]{[bendX][comma][bendY][comma]}* \n }*
             * cofactor,inhibitor,activator or regulator as c,i,a,r repecitvely in [cofactor] field and a space if none of them
             */

            //TODO: generate the LayoutInfo directly or first to XML format
            for (Node node : view.getNodeArray()) {

                PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(node);
                //skip pathway nodes
                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) {
                    continue;
                }
                //skip box nodes
                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.PATHWAY_BOX) {
                    continue;
                }
                //skip tissue group node
                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.TISSUEGROUP){
                    continue;
                }

                //first find which process node does this node belongs to, attach the process GUID or null
                if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) {
                    layout += "null,";
                    nodeTORelatedProcessGUID.put(nr, "null");
                } else {
                    NodeCursor nc = node.neighbors();
                    boolean flag = false;
                    for (int i = 0; i < nc.size(); i++) {
                        nc.cyclicNext();
                        Node tmpn = nc.node();
                        PathCaseShapeNodeRealizer tmpnr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(tmpn);
                        //currently we use one connected process GUID as part of indentification
                        if (tmpnr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) {
                            layout += tmpnodeToPathCaseId.get(tmpn).iterator().next() + ",";
                            nodeTORelatedProcessGUID.put(nr, tmpnodeToPathCaseId.get(tmpn).iterator().next());
                            flag = true; //there is at least one connected processnode
                            break;
                        }
                    }
                    if (flag == false) {
                        layout += "WRONG,";//ie, pathways sharing metabolites with the current pathway
                    }
                }
                HashSet<String> ids = tmpnodeToPathCaseId.get(node);
                layout += (ids.iterator().next() + ",");

                //identifier cofactor nodes since they could also be inhibitors or regulators in the same process
                PathCaseShapeNodeRealizer.PathCaseNodeRole role = nr.getNodeRole();
                if (role == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR) {
                    layout += "a,";
                }else if(role == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR){
                    layout += "c,";
                }else if(role == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR){
                    layout += "r,";
                }else if(role == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR){
                    layout += "i,";
                }else{
                    layout += " ,";
                }

                //upper left coords for Graph2D.setLocation
                layout += (nr.getBoundingBox().x + ",");
                layout += (nr.getBoundingBox().y + "\n");
                //System.out.println(nr.getBoundingBox().width+" "+nr.getBoundingBox().height);
            }

            layout += "\n";

            for (Edge edge : view.getEdgeArray()) {
                EdgeRealizer er = graphViewer.view.getGraph2D().getRealizer(edge);
                NodeRealizer nrs = er.getSourceRealizer();
                NodeRealizer nrt = er.getTargetRealizer();

                PathCaseShapeNodeRealizer nrs1 = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(nrs.getNode());
                if (nrs1.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) {
                    continue;
                }
                PathCaseShapeNodeRealizer nrt1 = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(nrt.getNode());
                if (nrt1.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) {
                    continue;
                }

                String sc = "";
                PathCaseShapeNodeRealizer.PathCaseNodeRole roles = nrs1.getNodeRole();
                if (roles == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR) {
                    sc += "a";
                }else if(roles == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR){
                    sc += "c";
                }else if(roles == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR){
                    sc += "r";
                }else if(roles == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR){
                    sc += "i";
                }else{
                    sc += " ";
                }

                String tc = "";
                PathCaseShapeNodeRealizer.PathCaseNodeRole rolet = nrt1.getNodeRole();
                if (rolet == PathCaseShapeNodeRealizer.PathCaseNodeRole.ACTIVATOR) {
                    tc += "a";
                }else if(rolet == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR){
                    tc += "c";
                }else if(rolet == PathCaseShapeNodeRealizer.PathCaseNodeRole.REGULATOR){
                    tc += "r";
                }else if(rolet == PathCaseShapeNodeRealizer.PathCaseNodeRole.INHIBITOR){
                    tc += "i";
                }else{
                    tc += " ";
                }

                layout += (nodeTORelatedProcessGUID.get(nrs) + "," + tmpnodeToPathCaseId.get(nrs.getNode()).iterator().next() + "," + sc + "," +
                        nodeTORelatedProcessGUID.get(nrt) + "," + tmpnodeToPathCaseId.get(nrt.getNode()).iterator().next() + "," + tc + ",");//source and target X,Y
                for (int i = 0; i < er.bendCount(); i++) {
                    Bend b = er.getBend(i);
                    layout += (b.getX() + "," + b.getY() + ",");
                }
                layout += "\n";
            }
            //System.out.println(layout);
            //convert absolute coordinates to relative coordinates with the top-left one as (0,0)
            LayoutInfo info = new LayoutParser().getParsedInfo(layout);
            info.convertToRelativePositions();
            return info;
        }


    private LayoutBox applyLayoutInfo(LayoutInfo info) {
        return applyLayoutInfo(info, 0, 0);
    }

    /**
     * xshift and yshift are added to the positions,
     * also are the exact coordinates for the position of the top-left point of the exact box.
     *
     * <p>
     * After a pathway is placed, or a shared node is moved,
     * the position of nodes will be updated in the corresponding LayoutInfo obj.
     * @param info
     * @param xshift
     * @param yshift
     */
    private LayoutBox applyLayoutInfo(LayoutInfo info, double xshift, double yshift) {
        Graph2D view = graphViewer.view.getGraph2D();
        /*Print out all node IDs to the console
        for (Node node : graphViewer.view.getGraph2D().getNodeArray()) {
            for (Iterator it = nodeToPathCaseId.get(node).iterator(); it.hasNext();)
            { //check each PathCaseID this node represents
                System.out.println(it.next());
            }
        }
        //*/

        //assign coordinates to nodes
        Iterator<LayoutInfo.NodeLayout> e = info.nodes.iterator();
        Node n;
        LayoutInfo.NodeLayout nl;
        while (e.hasNext()) {
            nl = e.next();
            n = getNodeByPathCaseID(nl.processID, nl.nodeID, nl.cofactor);
            if (n != null) {
                view.setLocation(n, nl.x + xshift, nl.y + yshift);
                //System.out.println("node   ("+(nl.x+xshift+)","+(nl.y+xshift)
                //System.out.println("Found node " + nl.nodeID + "when assign coordinates");
            } else {
                System.err.println("Didn't find node " + nl.nodeID + " when assign coordinates");
            }
        }

        //*assign bend points to edges
        Iterator<LayoutInfo.EdgeLayout> ee = info.edges.iterator();
        LayoutInfo.EdgeLayout el;
        while (ee.hasNext()) {
            el = ee.next();
            Edge edge = getEdgeByPathCaseID(el.sourcepid, el.sourceNode, el.targetNode);
            if (edge == null) {
                System.err.println("No edge psid: " + el.sourcepid + " sid: " + el.sourceNode + " tid: " + el.targetNode + "when assign coordinates");
                continue;
            }
            EdgeRealizer er = graphViewer.view.getGraph2D().getRealizer(edge);
            Iterator<LayoutPoint> eb = el.bends.iterator();
            er.clearBends();
            while (eb.hasNext()) {
                LayoutPoint pt = eb.next();
                er.insertBend(pt.x + xshift, pt.y + yshift);
            }            
        }//*/ //        

        LayoutBox b = info.getExactLayoutBox();
        info.convertToShiftedPositions(b.topleft.x+xshift,b.topleft.y+yshift);
        b = info.getExactLayoutBox();
        return b;
    }

    private LayoutBox placeAnotherPathway(LayoutBox box1, LayoutInfo info2, LinkedHashMap<LayoutInfo.NodeLayout, LayoutInfo.NodeLayout> shared, RelativePos position, Flipping flip) {
          Graph2D view = graphViewer.view.getGraph2D();
          LayoutBox box2 = info2.getExactLayoutBox();
          String[] fourpos = {"left", "right", "top", "below"};
          String[] filps = {"no", "leftright", "topdown"};

          if (shared.size() == 1) {//locate shared node / locate box 2/ move shared node
              System.out.println("       sharing ONE node");
              Iterator<LayoutInfo.NodeLayout> e = shared.keySet().iterator();
              LayoutInfo.NodeLayout nl1 = e.next();
              LayoutInfo.NodeLayout nl2 = shared.get(nl1);
              String pos = "";//pathway2 on the (left,right,top,below) of pathway1

              //the total distance for the two positions for the shared node in the 2 pathways to be moved
              double distance = box1.width + box2.width + box1.height + box2.height + 24000;//initialized to be big enough

              for (String f : filps) {
                  double x2 = nl2.x;
                  double y2 = nl2.y;

                  if (f.equals("leftright")) {
                      if (nl2.processID.equals("null")) {
                          x2 = box2.topleft.x + (box2.width - (x2 - box2.topleft.x)) - 60;//minus 60 which is the width of process node's box, since using upperleft coor
                      } else if (!nl2.cofactor.equals(" ")) {
                          x2 = box2.topleft.x + (box2.width - (x2 - box2.topleft.x)) - 60;
                      } else {
                          x2 = box2.topleft.x + (box2.width - (x2 - box2.topleft.x)) - 20;
                      }
                  }

                  if (f.equals("topdown")) {
                      if (!nl2.cofactor.equals(" ")) {
                          y2 = box2.topleft.y + (box2.height - (y2 - box2.topleft.y)) - 12;
                      } else {
                          y2 = box2.topleft.y + (box2.height - (y2 - box2.topleft.y)) - 20;
                      }
                  }

                  for (String s : fourpos) {
                      double d = LayoutBox.getDistanceMoved(box1, box2, new LayoutPoint(nl1.x, nl1.y), new LayoutPoint(x2, y2), s);
                      //System.out.println("        position: "+s+" flipping: "+f+" "+d);
                      if (d < distance) {
                          distance = d;
                          pos = s;
                          flip.set(f);
                      }
                  }
              }
              System.out.println("         position: " + pos + " flipping: " + flip.get() + " with moving distance of the shared node " + distance);

              if (flip.get().equals("leftright")) {
                  info2.flipLayoutLeftRight();
              } else if (flip.get().equals("topdown")) {
                  info2.flipLayoutUpDown();
              }

              if (position != null) {
                  position.setPos(pos);
              }

              LayoutInfo info = new LayoutInfo();
              LayoutInfo.NodeLayout snl = info.new NodeLayout("", "", " ", 0, 0); //just need the x,y; initialized before passed to getExactBox2
              box2 = LayoutBox.getExactBox2(box1, box2, new LayoutPoint(nl1.x, nl1.y), new LayoutPoint(nl2.x, nl2.y), pos, snl);

              applyLayoutInfo(info2, box2.topleft.x, box2.topleft.y);
              info2.convertToShiftedPositions(box2.topleft.x, box2.topleft.y);
              Node n = getNodeByPathCaseID(nl1.processID, nl1.nodeID, nl1.cofactor);
              if (n != null) {
                  view.setLocation(n, snl.x, snl.y);
                  //System.out.println("            shared node placed at "+snl.x+","+snl.y);
                  double d2 = Math.sqrt((nl2.x - snl.x) * (nl2.x - snl.x) + (nl2.y - snl.y) * (nl2.y - snl.y));
                  //System.out.println("            moving distance for shared node to the original position in the pathway just placed "+d2);
                  nl1.x = snl.x;
                  nl2.x = snl.x;
                  nl1.y = snl.y;
                  nl2.y = snl.y;
              } else {
                  //System.err.println("Didn't find node " + nl1.nodeID + " when assign coordinates to the shared node");
              }
              return box2;
          }

          if (shared.size() > 1) {
              System.out.println("       sharing " + shared.size() + " nodes");
              //get the two center points for those shared nodes in two pathways
              LayoutPoint p1 = new LayoutPoint(0, 0), p2 = new LayoutPoint(0, 0);
              LayoutPoint.getCenterPoints(shared, p1, p2);

              String pos = "";//position is pathway2 on the (left,right,top,below) of pathway1
              //the total distance for the two positions for the shared node in the 2 pathways to be moved
              //initialized to be big enough
              double distance = box1.width + box2.width + box1.height + box2.height + 240;

              for (String s : fourpos) {
                  double d = LayoutBox.getDistanceMoved(box1, box2, p1, p2, s);
                  //System.out.println(d);
                  if (d < distance) {
                      distance = d;
                      pos = s;
                  }
              }

              for (String f : filps) {
                  double x2 = p2.x;
                  double y2 = p2.y;

                  if (f.equals("leftright")) {
                      x2 = box2.topleft.x + (box2.width - (x2 - box2.topleft.x));
                  }

                  if (f.equals("topdown")) {
                      y2 = box2.topleft.y + (box2.height - (y2 - box2.topleft.y));
                  }

                  for (String s : fourpos) {
                      double d = LayoutBox.getDistanceMoved(box1, box2, p1, new LayoutPoint(x2, y2), s);
                      //System.out.println("        position: "+s+" flipping: "+f+" "+d);
                      if (d < distance) {
                          distance = d;
                          pos = s;
                          flip.set(f);
                      }
                  }
              }

              if (flip.get().equals("leftright")) {
                  info2.flipLayoutLeftRight();
              } else if (flip.get().equals("topdown")) {
                  info2.flipLayoutUpDown();
              }
              System.out.println("        position: " + pos + " flipping: " + flip.get() + " with moving distance of the center of shared nodes" + distance);

              LayoutInfo info = new LayoutInfo();
              LayoutInfo.NodeLayout snl = info.new NodeLayout("", "", " ", 0, 0); //actually don't need this,just to work with the existing method
              box2 = LayoutBox.getExactBox2(box1, box2, p1, p2, pos, snl);
              applyLayoutInfo(info2, box2.topleft.x, box2.topleft.y);
              info2.convertToShiftedPositions(box2.topleft.x, box2.topleft.y);

              moveMultiSharedNodes(box1, box2, pos, shared);
              return box2;
          } else {
              System.err.println("error:No Shared Node but called placeAnotherPathway");
              initialLayout();
              return new LayoutBox(0, 0, 0, 0);
          }
      }


    /**
     * Place pathway2 which has shared nodes with pathway1, don't consider other pathways
     * the position of nodes will be updated in the corresponding LayoutInfo obj.
     * @param box1 box of the pathway placed and depended on, if the pathway is shifted,this box should be the shifted box
     * @param info2 to be placed, converted to the relative positions already
     * @param shared shared nodes
     * @param position to return the relative position pathway2 is placed {0,1,2,3) => {"left", "right", "top", "below"}
     * @return
     */
    private LayoutBox placeAnotherPathwayModel(LayoutBox box1,LayoutInfo info2,LinkedHashMap<LayoutInfo.NodeLayout, LayoutInfo.NodeLayout> shared,RelativePos position){
        Graph2D view = graphViewer.view.getGraph2D();
        LayoutBox box2 = info2.getExactLayoutBox();
        String[] fourpos = {"left", "right", "top", "below"};
        String[] filps = {"no","leftright","topdown"};

        if (shared.size() == 1) {//locate shared node / locate box 2/ move shared node
            Iterator<LayoutInfo.NodeLayout> e = shared.keySet().iterator();
            LayoutInfo.NodeLayout nl1 = e.next();
            LayoutInfo.NodeLayout nl2 = shared.get(nl1);
            String pos = "";//pathway2 on the (left,right,top,below) of pathway1
            String flip = "";//flipping?

            //the total distance for the two positions for the shared node in the 2 pathways to be moved
            double distance = box1.width + box2.width + box1.height + box2.height + 240;//initialized to be big enough

            for(String f: filps){
                double x2 = nl2.x;
                double y2 = nl2.y;

                if(f.equals("leftright")){
                    if(nl2.processID.equals("null")){
                        x2 = box2.topleft.x + (box2.width - (x2 - box2.topleft.x)) - 60;//minus 60 which is the width of process node's box, since using upperleft coor
                    }else if(nl2.cofactor.equalsIgnoreCase("true")){
                        x2 = box2.topleft.x + (box2.width - (x2 - box2.topleft.x)) - 60;
                    }else{
                        x2 = box2.topleft.x + (box2.width - (x2 - box2.topleft.x)) - 20;
                    }
                }

                if(f.equals("topdown")){
                    if(nl2.cofactor.equalsIgnoreCase("true")){
                        y2 = box2.topleft.y + (box2.height - (y2 - box2.topleft.y)) - 12;
                    }else{
                        y2 = box2.topleft.y + (box2.height - (y2 - box2.topleft.y)) - 20;
                    }
                }
                
                for (String s : fourpos) {
                    double d = getDistanceMoved(box1, box2, new LayoutPoint(nl1.x,nl1.y), new LayoutPoint(x2,y2), s);
                    //System.out.println("        position: "+s+" flipping: "+f+" "+d);
                    if (d < distance) {
                        distance = d;
                        pos = s;
                        flip = f;
                    }
                }
            }
            System.out.println("        position: "+pos+" flipping: "+flip+" with moving distance of the shared node "+distance);

            if(flip.equals("leftright")){
                info2.filpLayoutLeftRight();
            }else if(flip.equals("topdown")){
                info2.filpLayoutUpDown();
            }

            if(position != null){
                position.setPos(pos);
            }

            LayoutInfo info = new LayoutInfo();
            LayoutInfo.NodeLayout snl = info.new NodeLayout("", "", true, 0, 0); //just need the x,y; initialized before passed to getExactBox2
            box2 = getExactBox2(box1, box2,new LayoutPoint(nl1.x,nl1.y), new LayoutPoint(nl2.x,nl2.y), pos, snl);

            applyLayoutInfo(info2, box2.topleft.x, box2.topleft.y);
            info2.convertToShiftedPositions(box2.topleft.x, box2.topleft.y);
            Node n = getNodeByPathCaseID(nl1.processID, nl1.nodeID, nl1.cofactor);
            if (n != null) {
                view.setLocation(n, snl.x, snl.y);
                System.out.println("            shared node placed at "+snl.x+","+snl.y);
                double d2=Math.sqrt((nl2.x-snl.x)*(nl2.x-snl.x) + (nl2.y-snl.y)*(nl2.y-snl.y));
                System.out.println("            moving distance for shared node to the original position in the pathway just placed "+d2);
                nl1.x=snl.x;
                nl2.x=snl.x;
                nl1.y=snl.y;
                nl2.y=snl.y;
            } else {
                System.err.println("Didn't find node " + nl1.nodeID + " when assign coordinates to the shared node");
            }
            return box2;
        }

        if (shared.size() > 1){
            //get the two center points for those shared nodes in two pathways
            LayoutPoint p1=new LayoutPoint(0,0),p2=new LayoutPoint(0,0);
            getCenterPoints(shared,p1,p2);

            String pos = "";//position is pathway2 on the (left,right,top,below) of pathway1
            String flip = "";//flipping?
            //the total distance for the two positions for the shared node in the 2 pathways to be moved
            //initialized to be big enough
            double distance = box1.width + box2.width + box1.height + box2.height + 240;

            for (String s : fourpos) {
                double d = getDistanceMoved(box1, box2, p1,p2, s);
                //System.out.println(d);
                if(d < distance){
                    distance = d;
                    pos = s;
                }
            }

            for(String f: filps){
                double x2 = p2.x;
                double y2 = p2.y;

                if(f.equals("leftright")){
                    x2 = box2.topleft.x + (box2.width - (x2 - box2.topleft.x));
                }

                if(f.equals("topdown")){
                    y2 = box2.topleft.y + (box2.height - (y2 - box2.topleft.y));
                }

                for (String s : fourpos) {
                    double d = getDistanceMoved(box1, box2, p1, new LayoutPoint(x2,y2), s);
                    //System.out.println("        position: "+s+" flipping: "+f+" "+d);
                    if (d < distance) {
                        distance = d;
                        pos = s;
                        flip = f;
                    }
                }
            }

            if(flip.equals("leftright")){
                info2.filpLayoutLeftRight();
            }else if(flip.equals("topdown")){
                info2.filpLayoutUpDown();
            }
            System.out.println("        position: "+pos+" flipping: "+flip+" with moving distance of the center of shared nodes"+distance);

            LayoutInfo info = new LayoutInfo();
            LayoutInfo.NodeLayout snl = info.new NodeLayout("", "", true, 0, 0); //actually don't need this,just to work with the existing method
            box2 = getExactBox2(box1, box2, p1,p2, pos, snl);
            applyLayoutInfo(info2, box2.topleft.x, box2.topleft.y);
            info2.convertToShiftedPositions(box2.topleft.x, box2.topleft.y);

            moveMultiSharedNodes(box1,box2,pos,shared);
            return box2;
        }

        else{
            System.err.println("error:No Shared Node but called placeAnotherPathway");
            initialLayout();
            return new LayoutBox(0,0,0,0);
        }
    }

    /**
     * @deprecated just for experiments
     * place a pathway sharing one node with an existing pathway just keep the shared node's position and shift the second pathway
     */
    private void placeSecondPathway(LayoutInfo info,LinkedHashMap<LayoutInfo.NodeLayout, LayoutInfo.NodeLayout> shared){
        Graph2D view = graphViewer.view.getGraph2D();
        if (shared.size() == 1) {//locate shared node / locate box 2/ move shared node
            Iterator<LayoutInfo.NodeLayout> e = shared.keySet().iterator();
            LayoutInfo.NodeLayout nl1 = e.next();
            LayoutInfo.NodeLayout nl2 = shared.get(nl1);
            System.out.println(""+nl1+nl2);
            double xshift = nl1.x - nl2.x;
            double yshift = nl1.y - nl2.y;
            applyLayoutInfo(info, xshift, yshift);
            Node n = n = getNodeByPathCaseID(nl2.processID, nl2.nodeID, nl2.cofactor);
            view.setLocation(n,nl1.x,nl2.y);
         }
    }

    /**
     * @param box1 the layout box for nodes placed already
     * @param box2 the relative layout box for the pathway to be placed
     * @param nl1 the position of the shared node in pathway 1, could be outside the layoutbox 1 if moved since other sharing
     * @param nl2 the position of the shared node in pathway 2, could be outside the layoutbox 1 if moved since other sharing
     * @param position  pathway2 on the (left,right,top,below) of pathway1
     * @return the total distance for the two positions for the shared node in the 2 pathways to be moved
     */
    private double getDistanceMoved(LayoutBox box1, LayoutBox box2, LayoutPoint nl1, LayoutPoint nl2, String position) {
        //exact position for the top-left point of the exact box of pathway2
        double x = 0;
        double y = 0;
        
        //position for the shared node
        double sx, sy;
        double dist = 0;//to be returned

        if (position.equals("right")) {
            sx = box1.topleft.x + box1.width + 120;
            dist = (sx - nl1.x) + (nl2.x + 120);
        } else if (position.equals("left")) {
            sx = box1.topleft.x - 120;
            dist = (nl1.x - sx) + (box2.width - nl2.x + 120);
        } else if (position.equals("top")) {
            dist = (nl1.y - box1.topleft.y + 40) + (box2.height - nl2.y + 40);
        } else if (position.equals("below")) {
            sy = box1.topleft.y + box1.height + 40;
            dist = (sy - nl1.y) + (nl2.y + 40);
        } else {
            System.err.println("Wrong relative position string between the 2 pathways when calling getDistanceMoved");
        }
        return dist;
    }

    /**
     * to get the absolute position for pathway2 depend on box1
     * box1 and nl1; box2 and nl2 must be consistent
     * @param box1 the layout box for nodes placed already
     * @param box2 the relative layout box for the pathway to be placed
     * @param nl1 the position of the shared node in layoutBox 1
     * @param nl2 the position of the shared node in layoutBox 2
     * @param position  pathway2 on the (left,right,top,below) of pathway1
     * @param sn to return the absolute position for the shared node, intialized before passed here
     * @return
     */
    private LayoutBox getExactBox2(LayoutBox box1, LayoutBox box2, LayoutPoint nl1, LayoutPoint nl2, String position, LayoutInfo.NodeLayout sn) {
        //exact position for the top-left point of the exact box of pathway2
        double x = 0;
        double y = 0;

        if (position.equals("right")) {
            x = box1.topleft.x + box1.width + 240;
            y = nl1.y - nl2.y;
            sn.x = x - 120;
            sn.y = nl1.y;
        } else if (position.equals("left")) {
            x = box1.topleft.x - box2.width - 240;
            y = nl1.y - nl2.y;
            sn.x = box1.topleft.x - 120;
            sn.y = nl1.y;
        } else if (position.equals("top")) {
            x = nl1.x - nl2.x;
            y = box1.topleft.y - box2.height - 80;
            sn.x = nl1.x;
            sn.y = box1.topleft.y - 40;
        } else if (position.equals("below")) {
            x = nl1.x - nl2.x;
            y = box1.topleft.y + box1.height + 80;
            sn.x = nl1.x;
            sn.y = box1.topleft.y + box1.height + 40;
        } else {
            System.err.println("Wrong relative position string between the 2 pathways when calling getDistanceMoved");
        }
        return new LayoutBox(x, y, box2.width, box2.height);
    }

    /**
     * place pathway p outside the LayoutBox largeBox to make the surrounding all box more compact, so less white space created
     * There suppose to be no shared nodes between this pathway with pathways placed already
     * @param largeBox
     * @param p only using the width and height of the layout box, won't matter whether relative or absolute positions
     * @return the shifted LayoutBox for pathway p
     */
    private LayoutBox placePathwayOutsideBox(LayoutBox largeBox,LayoutInfo p){
        double xs,ys;
        LayoutBox smallBox = p.getExactLayoutBox();
        if(((largeBox.height+smallBox.height)* Math.max(largeBox.width,smallBox.width))
            <=
           ((largeBox.width+smallBox.width)-Math.max(largeBox.height,smallBox.height))){//smaller one on top
            xs=largeBox.topleft.x+(largeBox.width/2)-(smallBox.width/2);
            ys=largeBox.topleft.y-smallBox.height-smallBox.topleft.y-40;
        }else{//smaller one on right
            xs=largeBox.topleft.x+largeBox.width-smallBox.topleft.x+240;
            ys=largeBox.topleft.y+(largeBox.height/2)-(smallBox.height/2);
        }

        applyLayoutInfo(p,xs,ys);
        return new LayoutBox(smallBox.topleft.x+xs,smallBox.topleft.y+ys,smallBox.width, smallBox.height);
    }

    /**
     * get the two center points for the two sets of nodes
     * p1,p2 must be initialized before passed here
     * @param shared
     * @param p1
     * @param p2
     */
    private void getCenterPoints(LinkedHashMap<LayoutInfo.NodeLayout, LayoutInfo.NodeLayout> shared,LayoutPoint p1,LayoutPoint p2){
        int n=shared.size();
        LayoutPoint[] pts1 = new LayoutPoint[n];
        LayoutPoint[] pts2 = new LayoutPoint[n];
        Iterator<LayoutInfo.NodeLayout> e = shared.keySet().iterator();
        int i = 0;
        while(e.hasNext()){
            LayoutInfo.NodeLayout nl1 = e.next();
            LayoutInfo.NodeLayout nl2 = shared.get(nl1);
            pts1[i]=new LayoutPoint(nl1.x, nl1.y);
            pts2[i]=new LayoutPoint(nl2.x, nl2.y);
            i++;
        }
        LayoutPoint tp1,tp2;
        tp1 = LayoutPoint.getCenterPoint(pts1);
        tp2 = LayoutPoint.getCenterPoint(pts2);
        p1.x=tp1.x;
        p1.y=tp1.y;
        p2.x=tp2.x;
        p2.y=tp2.y;
    }

    /**
     * move shared nodes
     * nodes in box1 and box2 are place already
     * @param box1
     * @param box2
     * @param pos box2's relative position(left/right/top/below) to box1
     * @param shared nodes shared between graphs in box1 and box2
     */
    private void moveMultiSharedNodes(LayoutBox box1,LayoutBox box2,String pos,LinkedHashMap<LayoutInfo.NodeLayout, LayoutInfo.NodeLayout> shared){
        Graph2D view = graphViewer.view.getGraph2D();

        //initilaize the cells, for solving overlapping later
        int cellsNum;//the number of cells for shared nodes in that extra space
        double min;//the min x or y coordinate of that extra space
        boolean samey;//true if shared nodes placed horizontally as the two pathway are top-below.
        if(pos.equals("top") || pos.equals("below")){
            cellsNum = (int)Math.max(box1.width,box2.width)/40;
            samey=true;
            min= Math.min(box1.topleft.x,box2.topleft.x);
        }else{
            cellsNum = (int)Math.max(box1.height,box2.height)/40;
            samey=false;
            min = Math.min(box1.topleft.y,box2.topleft.y);
        }
        int cells[] = new int[cellsNum];//number of nodes in that cell
        for(int b:cells){
            b=0;
        }
        
        for (LayoutInfo.NodeLayout nl1 : shared.keySet()) {
            LayoutInfo.NodeLayout nl2 = shared.get(nl1);
            Node n = getNodeByPathCaseID(nl1.processID, nl1.nodeID, nl1.cofactor);

            if (n != null) {                
                //y=kx+d: the line which the two copies in the two pathways of the shared node is on
                double k = (nl1.y - nl2.y) / (nl1.x - nl2.x);
                double d = nl1.y - k * nl1.x;
                double nx = 0, ny = 0;

                if (pos.equals("top")) {
                    ny = box1.topleft.y - 40;
                    nx = (ny - d) / k;
                } else if (pos.equals("below")) {
                    ny = box2.topleft.y - 40;
                    nx = (ny - d) / k;
                } else if (pos.equals("left")) {
                    nx = box1.topleft.x - 120;
                    ny = k * nx + d;
                } else if (pos.equals("right")) {
                    nx = box2.topleft.x - 120;
                    ny = k * nx + d;
                } else {
                    System.err.println("Wrong relative position between two pathways sharing mutiple nodes: " + pos
                            + "so cannot place the shared node to right position");
                }
                view.setLocation(n, nx, ny);

                nl1.x = nx;
                nl2.x = nx;
                nl1.y = ny;
                nl2.y = ny;

                //*debug code
                System.out.println("     a shared node is moved  "+nl1+", "+nl2);
                PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) (graphViewer.view.getGraph2D().getRealizer(n));
                PathCaseShapeNodeRealizer.PathCaseNodeRole role = nr.getNodeRole();
                if (role == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR
                        || role == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN
                        || role == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT) {
                        System.out.println("            a cofactor");
                }else{
                      System.out.println("           NOT a cofactor");
                }
                //*/

                if(samey){
                    int i = (int)((ny-min+120)/40);
                    cells[i]++;
                }else{
                    int i = (int)((nx-min+120)/40);
                    cells[i]++;
                }
                
            } else {
                System.err.println("Didn't find node " + nl1.nodeID + " when assign coordinates to the shared node");
            }
        }

        /* partially solving overlapping
        for(LayoutInfo.NodeLayout nl1 : shared.keySet()) {
            LayoutInfo.NodeLayout nl2 = shared.get(nl1);
            Node n = getNodeByPathCaseID(nl1.processID, nl1.nodeID, nl1.cofactor);

            if(samey){
               int c = (int)((nl1.y-min+120)/40);
               if(cells[c]>1){
                   if(cells[c-1] == 0){
                       view.setLocation(n,nl1.x, nl1.y-40);
                       nl1.y = nl1.y-40;
                       nl2.y = nl2.y-40;
                       cells[c]--;
                       cells[c-1]++;
                       System.out.println("     a shared node is moved to avoid overlapping "+nl1+", "+nl2);
                       break;
                   }

                   if(cells[c+1] == 0){
                       view.setLocation(n,nl1.x, nl1.y+40);
                       nl1.y = nl1.y+40;
                       nl2.y = nl2.y+40;
                       cells[c]--;
                       cells[c+1]++;
                       System.out.println("     a shared node is moved to avoid overlapping "+nl1+", "+nl2);
                       break;
                   }
               }
            }else{
               int c = (int)((nl1.x-min+120)/40);
               if(cells[c]>1){
                   if(cells[c-1] == 0){
                       view.setLocation(n,nl1.x-40, nl1.y);
                       nl1.x = nl1.x-40;
                       nl2.x = nl2.x-40;
                       cells[c]--;
                       cells[c-1]++;
                       System.out.println("     a shared node is moved to avoid overlapping "+nl1+", "+nl2);
                       break;
                   }

                   if(cells[c+1] == 0){
                       view.setLocation(n,nl1.x+40, nl1.y);
                       nl1.x = nl1.x+40;
                       nl2.x = nl2.x+40;
                       cells[c]--;
                       cells[c+1]++;
                       System.out.println("     a shared node is moved to avoid overlapping "+nl1+", "+nl2);
                       break;
                   }
               }
            }
        }
        //*/
    }

    /**
     * layout: remove bend points on the edges associated with the node
     *
     * @param n the node
     */
    public void removeBendsOfEdges(Node n) {
        Graph2D view = graphViewer.view.getGraph2D();

        //n.edges() didn't work correctly here
        for (Edge edge: view.getEdgeArray()) {
            EdgeRealizer er = graphViewer.view.getGraph2D().getRealizer(edge);
            if(edge.source() == n || edge.target() == n){
                //System.out.println(er.bendCount());
                er.clearBends();
            }
        }
    }

    private void applyFrozenLayout(LayoutInfo[] layouts, String[] pathways) {
          nodesInPathways = new ArrayList<NodeGroupofPathway>(layouts.length);
          Graph2D view = graphViewer.view.getGraph2D();
          HierarchyManager hm = new HierarchyManager(view);
          Color[] boxcolors = {Color.ORANGE, Color.BLUE, Color.RED, Color.MAGENTA, Color.CYAN,
                  Color.BLACK, Color.GREEN, Color.YELLOW, Color.PINK, Color.GRAY, Color.darkGray,
          };

          for (int j = 0; j < layouts.length; j++) {
              LayoutInfo i = layouts[j];
              if (i.isEmpty()) {
                  System.out.println("some pathway layoutinfo is empty, so use auto layout for the whole graph");
                  initialLayout();
                  return;
              } else {
                  i.convertToRelativePositions();
                  LayoutInfo copyInfo = i.copyOfThisLayoutInfo();
                  NodeList nodelist = new NodeList();
                  Node groupNode = hm.createGroupNode(view);
                  PathCaseShapeNodeRealizer box = new PathCaseShapeNodeRealizer();
                  box.setShapeType(ShapeNodeRealizer.RECT);
                  box.setTransparent(true);
                  box.setLineColor(boxcolors[j % boxcolors.length]);
                  box.setNodeRole(PathCaseShapeNodeRealizer.PathCaseNodeRole.PATHWAY_BOX);

                  NodeLabel nodelabel = box.createNodeLabel();
                  nodelabel.setModel(NodeLabel.TOP);
                  //nodelabel.setLineColor(Color.ORANGE);
                  nodelabel.setTextColor(boxcolors[j % boxcolors.length]);
                  nodelabel.setText(pathways[j]);
                  nodelabel.setFontName("Arial"); //nodelabel.
                  nodelabel.setFontSize(13);
                  nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_CONTENT);
                  //nodelabel.setOffset(0,0);
                  //nodelabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_HEIGHT);
                  box.setLabel(nodelabel);

                  view.setRealizer(groupNode, box);
                  for (LayoutInfo.NodeLayout nl : i.nodes) {
                      Node n = getNodeByPathCaseID(nl.processID, nl.nodeID, nl.cofactor);
                      if (n == null) {
                          continue;
                      }
                      nodelist.add(n);
                  }
                  hm.groupSubgraph(nodelist, groupNode);
                  nodesInPathways.add(new NodeGroupofPathway(groupNode, i, copyInfo, pathways[j]));
              }
          }

          LayoutBox largestBox = null;//the box surrounding all pathways placed already
          int[] dependOn = new int[layouts.length];
          ArrayList<Queue> queues = LayoutInfo.pathwayQueues(layouts, dependOn);
          LayoutBox[] boxes = new LayoutBox[layouts.length];//if a pathway has not been actuall placed, the cell will be null

          for (Queue<Integer> connectedPathways : queues) {
              System.out.println("Placing a new connected group: " + connectedPathways);
              LayoutBox tmpBox = null;//layout box surrounding all pathways in current disconnected subgraph
              LayoutBox[] tmpBoxes = new LayoutBox[layouts.length];//will only fill the boxes in this subgraph
              while (connectedPathways.size() != 0) {
                  int p = connectedPathways.remove();
                  if (dependOn[p] == -1) {//first pathway to be placed
                      System.out.println("place the largest pathway " + p + " in this subgraph with size: " + layouts[p].nodes.size());
                      tmpBoxes[p] = applyLayoutInfo(layouts[p]);
                      placePathwayBoundingBox(layouts[p], tmpBoxes[p]);
                      tmpBox = tmpBoxes[p];
                      System.out.println("            Placed in Box " + tmpBox);
                  } else {
                      System.out.println("place pathway " + p + " with size: " + layouts[p].nodes.size() + " depends on pathway:" + dependOn[p]);
                      RelativePos position = new RelativePos(); //{0,1,2,3) => {"right", "left", "top", "below"}
                      Flipping flip = new Flipping();

                      //layouts[p] is the one to be placed, so layouts[dependOn[p]]'s sharedNodes is called
                      LayoutBox b = placeAnotherPathway(tmpBoxes[dependOn[p]], layouts[p], layouts[dependOn[p]].sharedNodes(layouts[p]), position, flip);
                      if (LayoutBox.overlapWithPlacedPathways(tmpBoxes, b)) {
                          System.out.println("    place pathway " + p + " overlapped");
                          if (!trySamplePositions(tmpBoxes, b, tmpBox, layouts, p, layouts[dependOn[p]].sharedNodes(layouts[p]), position)) {//
                              System.out.println("       Failed in all sample positions, place outside box" + tmpBox);
                              layouts[p].convertToRelativePositions();

                              if (flip.get().equals("topdown")) {
                                  layouts[p].flipLayoutUpDown();
                              } else if (flip.get().equals("leftright")) {
                                  layouts[p].flipLayoutLeftRight();
                              }

                              //record all the current placed positions of shared nodes, to recover later
                              HashMap<LayoutInfo.NodeLayout, LayoutInfo.NodeLayout> shared = layouts[dependOn[p]].sharedNodes(layouts[p]);
                              HashMap<LayoutInfo.NodeLayout, NodeLayoutRefPair> sharedCopy = new HashMap<LayoutInfo.NodeLayout, NodeLayoutRefPair>(shared.size());
                              for (LayoutInfo.NodeLayout nl : shared.keySet()) {
                                  sharedCopy.put(layouts[p].new NodeLayout(nl.processID, nl.nodeID, nl.cofactor, nl.x, nl.y), new NodeLayoutRefPair(nl, shared.get(nl)));
                              }
                              b = placeAnotherPathway(tmpBox, layouts[p], layouts[dependOn[p]].sharedNodes(layouts[p]), position, flip);
                              restorePositions(sharedCopy);
                          }
                      }

                      for (LayoutInfo.NodeLayout nl : layouts[dependOn[p]].sharedNodes(layouts[p]).keySet()) {
                          removeBendsOfEdges(getNodeByPathCaseID(nl.processID, nl.nodeID, nl.cofactor));
                      }

                      //test:LayoutBox b = placeAnotherPathway(tmpBox,layouts[p],layouts[dependOn[p]].sharedNodes(layouts[p]),position);
                      System.out.println("            Placed in Box " + b);
                      placePathwayBoundingBox(layouts[p], b);
                      tmpBoxes[p] = b;
                      tmpBox = tmpBox.SurroundingBox(b);
                      //placeSecondPathway(layouts[p],layouts[dependOn[p]].sharedNodes(layouts[p]));
                  }
              }

              for (int i = 0; i < tmpBoxes.length; i++) {
                  LayoutBox b = tmpBoxes[i];
                  if (b != null) {
                      boxes[i] = b;
                  }
              }

              if (largestBox == null) {
                  largestBox = tmpBox;
              } else {
                  LayoutBox newBox = new LayoutBox(0, 0, tmpBox.width, tmpBox.height);//to get the new topleft x,y
                  placeBoxOutsideBox(largestBox, tmpBox, newBox);
                  double xs = newBox.topleft.x - tmpBox.topleft.x;
                  double ys = newBox.topleft.y - tmpBox.topleft.y;

                  for (int i = 0; i < tmpBoxes.length; i++) {
                      LayoutBox b = tmpBoxes[i];
                      if (b != null) {
                          applyLayoutInfo(layouts[i], xs, ys);
                          boxes[i].topleft.x += xs;
                          boxes[i].topleft.y += ys;
                          placePathwayBoundingBox(layouts[i], boxes[i]);
                      }
                  }

                  largestBox = largestBox.SurroundingBox(newBox);
              }

          }

          /*
          for (LayoutInfo info : layouts) {
              placePathwayBoundingBox(info, info.getExactLayoutBox());
          }

          /*test
          for(LayoutInfo i:layouts){
              LayoutBox b = i.getExactLayoutBox();
              System.out.println(b);
              applyLayoutInfo(i,b.topleft.x,b.topleft.y);
          }
          //*/
      }

       /**
     * place outside the LayoutBox largeBox to make the surrounding all box more compact, so less white space created
     *
     * @param largeBox
     */
    private void placeBoxOutsideBox(LayoutBox largeBox, LayoutBox currentB, LayoutBox newB) {
        double xs, ys;
        if (((largeBox.height + currentB.height) * Math.max(largeBox.width, currentB.width))
                <=
                ((largeBox.width + currentB.width) - Math.max(largeBox.height, currentB.height))) {//smaller one on top
            xs = largeBox.topleft.x + (largeBox.width / 2) - (currentB.width / 2);
            ys = largeBox.topleft.y - currentB.height - currentB.topleft.y - 40;
        } else {//smaller one on right
            xs = largeBox.topleft.x + largeBox.width - currentB.topleft.x + 240;
            ys = largeBox.topleft.y + (largeBox.height / 2) - (currentB.height / 2);
        }

        newB.topleft.x = currentB.topleft.x + xs;
        newB.topleft.y = currentB.topleft.y + ys;
    }
    private void placePathwayBoundingBox(LayoutInfo i, LayoutBox box) {
        //System.out.println("place Pathway Bounding Box "+box);
        Node group = getGroupNode(nodesInPathways, i);
        PathCaseShapeNodeRealizer groupNR = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(group);
        //System.out.println(box);
        //groupNR.setLocation(box.topleft.x,box.topleft.y);
        groupNR.setWidth(box.width+ 30);//
        groupNR.setHeight(box.height+ 30);
        Graph2D view = graphViewer.view.getGraph2D();
        view.setLocation(group, box.topleft.x-10, box.topleft.y-10);  //
    }

     public static Node getGroupNode(ArrayList<NodeGroupofPathway> groupNodes, LayoutInfo i) {

        for (NodeGroupofPathway group : groupNodes) {
            if (group.info == i) {
                return group.groupNode;
            }
        }
        return null;
    }

    /**
     * layout: apply frozen layout for pathways to be visualized.
     * apply default auto layout if not all pathway layouts are frozen.
     * @param layouts all the LayoutInfo objs for the pathways being visualzed.
     */
//    private void applyFrozenLayoutModel(LayoutInfo[] layouts) {
//        for(LayoutInfo i:layouts){
//            if(i.isEmpty()){
//               initialLayout();
//                return;
//            }else{
//                i.convertToRelativePositions();
//            }
//        }
//
//        LayoutBox tmpBox = null;//the box surrounding all pathways placed already
//        int[] dependOn = new int[layouts.length];
//        ArrayList<Queue> queues = pathwayQueues(layouts,dependOn);
//        LayoutBox[] boxes = new LayoutBox[layouts.length];//if a pathway has not been actuall placed, the cell will be null
//
//        for(Queue<Integer> connectedPathways:queues){
//            /*experiment code
//            connectedPathways.clear();
//            connectedPathways.add(2);
//            dependOn[1]=0;
//            connectedPathways.add(0);
//            dependOn[2]=1;
//            connectedPathways.add(1);
//            //*/
//            System.out.println("Placing a new connected group: "+connectedPathways);
//            while(connectedPathways.size() != 0){
//                int p=connectedPathways.remove();
//                if(tmpBox == null){//first pathway to be placed
//                    System.out.println("place the largest pathway "+p+" with size: "+layouts[p].nodes.size());
//                    boxes[p]=applyLayoutInfo(layouts[p]);
//                    tmpBox=boxes[p];
//                    System.out.println("            Placed in Box "+tmpBox);
//                }else if(dependOn[p]==-1){
//                    System.out.println("place a pathway "+p+" with size: "+layouts[p].nodes.size()+" outside "+tmpBox);
//                    LayoutBox b = placePathwayOutsideBox(tmpBox,layouts[p]);
//                    boxes[p]=b;
//                    tmpBox=tmpBox.SurroundingBox(b);
//                }else{
//                    System.out.println("place pathway "+p+" with size: "+layouts[p].nodes.size()+" depends on pathway:"+dependOn[p]);
//                    RelativePos position = new RelativePos(); //{0,1,2,3) => {"right", "left", "top", "below"}
//                    //layouts[p] is the one to be placed, so layouts[dependOn[p]]'s sharedNodes is called
//                    LayoutBox b = placeAnotherPathway(boxes[dependOn[p]],layouts[p],layouts[dependOn[p]].sharedNodes(layouts[p]),position);
//                    if(overlapWithPlacedPathways(boxes,b)){
//                        System.out.println("    place pathway "+p+" overlapped");
//                        if(!trySamplePositions(boxes,b,layouts,dependOn,p,layouts[dependOn[p]].sharedNodes(layouts[p]),position)){
//                            System.out.println("       Failed in all sample positions, place outside box"+tmpBox);
//                            layouts[p].convertToRelativePositions();
//                            b = placeAnotherPathway(tmpBox,layouts[p],layouts[dependOn[p]].sharedNodes(layouts[p]),position);
//                        }
//                    }
//
//                    for(LayoutInfo.NodeLayout nl: layouts[dependOn[p]].sharedNodes(layouts[p]).keySet()){
//                        removeBendsOfEdges(getNodeByPathCaseID(nl.processID,nl.nodeID,nl.cofactor));
//                    }
//
//                    //test:LayoutBox b = placeAnotherPathway(tmpBox,layouts[p],layouts[dependOn[p]].sharedNodes(layouts[p]),position);
//                    System.out.println("            Placed in Box "+b);
//                    boxes[p]=b;
//                    tmpBox=tmpBox.SurroundingBox(b);
//                    //placeSecondPathway(layouts[p],layouts[dependOn[p]].sharedNodes(layouts[p]));
//                }
//            }
//        }
//
//        /*test
//        for(LayoutInfo i:layouts){
//            LayoutBox b = i.getExactLayoutBox();
//            System.out.println(b);
//            applyLayoutInfo(i,b.topleft.x,b.topleft.y);
//        }
//        //*/
//    }

    /**
     * return a list of queues for connected groups of pathways
     * place pathways according to the order of the queues and the list
     * @param layouts
     * @param dependOn the index number of pathway this one depends on, -1 if no dependency
     * @return
     */
    private ArrayList<Queue> pathwayQueues(LayoutInfo[] layouts,int[] dependOn){
        ArrayList<Queue> queues = new ArrayList<Queue>();//each queue is for a disconnected group of pathways
        int n=layouts.length;
        boolean[] added = new boolean[n];//whether the pathway is added into a queue
        //boolean[] processed = new boolean[n];//whether all neighbours of this pathway are added into some queue
        Queue<Integer> toProcess = new LinkedList<Integer>();

        //current queue to add pathways into
        //if not null, then exists a queue to add pathways into
        //otherwise need to create a new queue(for disconnected group of pathways)
        Queue currentQueue=null;        

        Arrays.sort(layouts,new LayoutBoxSizeComparator());

        /*
        for(LayoutInfo i:layouts){
            System.out.println(i.getExactLayoutBox().size());
        }
        //*/

        for(int i=n-1;i>=0;i--){
            if(added[i]){
                continue;
            }

            currentQueue=new LinkedList<Integer>();//current queue to add pathways into
            queues.add(currentQueue);
            toProcess.add(i);
            currentQueue.add(i);
            added[i]=true;
            dependOn[i]=-1;

            while(toProcess.size() != 0){
                int p = toProcess.remove();                
                for(int j=n-2;j>=0;j--){//the first one is already added, so j starting from 1
                    //find all non-visited neighbour pathways of layout[p]
                    if(added[j]){
                        continue;
                    }
                    if(layouts[p].sharedNodes(layouts[j]).size() > 0 && !added[j]){
                        currentQueue.add(j);
                        toProcess.add(j);
                        added[j]=true;
                        dependOn[j]=p;
                    }
                }
                //processed[p]=true;
            }
            currentQueue=null;//finished a connected group of pathways
        }

        return queues;
    }

    /**
     * @param boxes
     * @param b
     * @return
     */
    private boolean overlapWithPlacedPathways(LayoutBox[] boxes,LayoutBox b){
        for(int i=0;i<boxes.length;i++){
            LayoutBox box = boxes[i];
            if(box != null){//pathways not actually placed(including p), the box would be null
                if(box.overlapLayoutBox(b)){//overlapping with some pathway already placed before
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * layout: change the number sample to try more or less
     *
     * @param boxes
     * @param b
     * @param layouts
     * @param p
     * @param shared
     * @param position
     * @return
     */
    private boolean trySamplePositions(LayoutBox[] boxes, LayoutBox b, LayoutBox largeB, LayoutInfo[] layouts, int p, LinkedHashMap<LayoutInfo.NodeLayout, LayoutInfo.NodeLayout> shared, RelativePos position) {
        //record all the current placed positions of shared nodes, to recover later
        HashMap<LayoutInfo.NodeLayout, NodeLayoutRefPair> sharedCopy = new HashMap<LayoutInfo.NodeLayout, NodeLayoutRefPair>(shared.size());
        for (LayoutInfo.NodeLayout nl : shared.keySet()) {
            sharedCopy.put(layouts[p].new NodeLayout(nl.processID, nl.nodeID, nl.cofactor, nl.x, nl.y), new NodeLayoutRefPair(nl, shared.get(nl)));
        }

        boolean noOverlap = false;
        int sample = 20;//the number of sample positions to try

        if (position.p == 0 || position.p == 1) {//left or right cases, move vertically
            System.out.println("try sample positions vertically");
            double d = largeB.height / sample;
            double yshift = 0;
            double distance = largeB.height + b.height + 1000000; //min distance from the original box, initialized large enough
            for (int i = 0; i < sample; i++) {
                double y = largeB.topleft.y + d * i;
                LayoutBox tmpB = new LayoutBox(b.topleft.x, y, b.width, b.height);
                if (!LayoutBox.overlapWithPlacedPathways(boxes, tmpB)) {
                    noOverlap = true;
                    if (Math.abs(y - b.topleft.y) < distance) {
                        distance = Math.abs(y - b.topleft.y);
                        yshift = y - b.topleft.y;
                    }
                }
            }

            if (noOverlap) {
                LayoutBox tmpB = applyLayoutInfo(layouts[p], 0, yshift);
                //b.topleft.x = tmpB.topleft.x;
                b.topleft.y = b.topleft.y+yshift;
                //b.width = tmpB.width;
                //b.height = tmpB.height;
                restorePositions(sharedCopy);
                System.out.println("succeeded a sample position");
                return true;
            }
        } else {//top or below cases, move horizontally
            System.out.println("try sample positions horizontally");
            double d = largeB.width / sample;
            double xshift = 0;
            double distance = largeB.width + b.width + 100000; //min distance from the original box, initialized large enough
            for (int i = 0; i < sample; i++) {
                double x = largeB.topleft.x + d * i;
                LayoutBox tmpB = new LayoutBox(x, b.topleft.y, b.width, b.height);
                if (!LayoutBox.overlapWithPlacedPathways(boxes, tmpB)) {
                    noOverlap = true;
                    if (Math.abs(x - b.topleft.x) < distance) {
                        distance = Math.abs(x - b.topleft.x);
                        xshift = x - b.topleft.x;
                    }

                }
            }

            if (noOverlap) {
                LayoutBox tmpB = applyLayoutInfo(layouts[p], xshift, 0);
                b.topleft.x = b.topleft.x+xshift;
                //b.topleft.y = tmpB.topleft.y;
                //b.width = tmpB.width;
                //b.height = tmpB.height;
                restorePositions(sharedCopy);
                System.out.println("succeeded a sample position");
                return true;
            }
        }
        return false;
    }

//    /**
//     * recover positions of these nodes
//     * @param nodes
//     */
//    private void restorePositions(ArrayList<LayoutInfo.NodeLayout> nodes){
//        Graph2D view = graphViewer.view.getGraph2D();
//        for(LayoutInfo.NodeLayout nl:nodes){
//            Node n = getNodeByPathCaseID(nl.processID, nl.nodeID, nl.cofactor);
//            view.setLocation(n,nl.x,nl.y);
//        }
//    }

    private void restorePositions(HashMap<LayoutInfo.NodeLayout, NodeLayoutRefPair> nodes) {
        Graph2D view = graphViewer.view.getGraph2D();
        for (LayoutInfo.NodeLayout nl : nodes.keySet()) {
            Node n = getNodeByPathCaseID(nl.processID, nl.nodeID, nl.cofactor);
            view.setLocation(n, nl.x, nl.y);
            NodeLayoutRefPair originalNL = nodes.get(nl);
            originalNL.nl1.x = nl.x;
            originalNL.nl1.y = nl.y;
            originalNL.nl2.x = nl.x;
            originalNL.nl2.y = nl.y;
        }
    }

    /**
     * layout: get layoutInfo from current pathway graph
     * skipping collapsed pathway nodes currently
     *
     */
    private String layoutInfo() {
        //System.out.println(getLayoutInfo().toString());
        return getLayoutInfo().toXMLString();
    }
    
    private LayoutInfo getLayoutInfo() {
        HashMap<NodeRealizer, String> nodeTORelatedProcessGUID = new HashMap<NodeRealizer, String>(); //filled during the first part, traversing nodes
        String layout = "";
        Graph2D view = graphViewer.view.getGraph2D();
        /**
         * Own Designed Compact Layout String Structure:
         * First part is for nodes, then an empty line followed by the part for edges:
         * {[ProcessGUID | null][comma][GUID][comma][cofactor| ][comma][centerX][comma][centerY]\n}*
         * \n
         * {[SourceNodeProcessGUID | null][comma][SourceNodeGUID][comma][TargetNodeProcessGUID | null][comma][TargetNodeGUID][comma]{[bendX][comma][bendY][comma]}* \n }*
         */
        //TODO: generate the LayoutInfo directly or first to XML format
        for (Node node : view.getNodeArray()) {
            PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(node);
            //skip pathway nodes
            if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) {
                continue;
            }

            //first find which process node does this node belongs to, attach the process GUID or null
            if (nr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) {
                layout += "null,";
                nodeTORelatedProcessGUID.put(nr, "null");
            } else {
                NodeCursor nc = node.neighbors();
                boolean flag = false;
                for (int i = 0; i < nc.size(); i++) {
                    nc.cyclicNext();
                    Node tmpn = nc.node();
                    PathCaseShapeNodeRealizer tmpnr = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(tmpn);
                    //currently we use one connected process GUID as part of indentification
                    if (tmpnr.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) {
                        layout += nodeToPathCaseId.get(tmpn).iterator().next() + ",";
                        nodeTORelatedProcessGUID.put(nr, nodeToPathCaseId.get(tmpn).iterator().next());
                        flag = true; //there is at least one connected processnode
                        break;
                    }
                }
                if (flag == false) {
                    layout += "WRONG,";//ie, pathways sharing metabolites with the current pathway
                }
            }
            if(nodeToPathCaseId.get(node).iterator().next().equals("57041a9b-4291-4b53-bf0b-dc3f5e9e5081")){
                int i=0;
            }
            layout += (nodeToPathCaseId.get(node).iterator().next() + ",");

            //identifier cofactor nodes since they could also be inhibitors or regulators in the same process
            PathCaseShapeNodeRealizer.PathCaseNodeRole role = nr.getNodeRole();
            if (role == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR
                    || role == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN
                    || role == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT) {
                layout += "cofactor,";
            } else {
                layout += " ,";
            }

            //upper left coords for Graph2D.setLocation
            layout += (nr.getBoundingBox().x + ",");
            layout += (nr.getBoundingBox().y + "\n");
            //System.out.println(nr.getBoundingBox().width+" "+nr.getBoundingBox().height);
        }

        layout += "\n";

        for (Edge edge : view.getEdgeArray()) {
            EdgeRealizer er = graphViewer.view.getGraph2D().getRealizer(edge);
            NodeRealizer nrs = er.getSourceRealizer();
            NodeRealizer nrt = er.getTargetRealizer();

            PathCaseShapeNodeRealizer nrs1 = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(nrs.getNode());
            if (nrs1.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) {
                continue;
            }
            PathCaseShapeNodeRealizer nrt1 = (PathCaseShapeNodeRealizer) graphViewer.view.getGraph2D().getRealizer(nrt.getNode());
            if (nrt1.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.COLLAPSEDPATHWAY) {
                continue;
            }

            layout += (nodeTORelatedProcessGUID.get(nrs) + "," + nodeToPathCaseId.get(nrs.getNode()).iterator().next() + "," +
                    nodeTORelatedProcessGUID.get(nrt) + "," + nodeToPathCaseId.get(nrt.getNode()).iterator().next() + ",");//source and target X,Y
            for (int i = 0; i < er.bendCount(); i++) {
                Bend b = er.getBend(i);
                layout += (b.getX() + "," + b.getY() + ",");
            }
            layout += "\n";
        }

        //convert absolute coordinates to relative coordinates with the top-left one as (0,0)
        LayoutInfo info = new LayoutParser().getParsedInfo(layout);
        info.convertToRelativePositions();
        return info;
    }

     public Node getNodeByPathCaseID(String pid, String id, String cofactor) {
         boolean b=false;
         if(cofactor.equalsIgnoreCase("true"))b=true;
         return getNodeByPathCaseID(pid,id,b);
     }

    //pid is the GUID of one process connected with the node, if the node is a process node, then pid should be "null" or null
    public Node getNodeByPathCaseID(String pid, String id, boolean cofactor) {
        //TODO          use index instead of exhaustive search
        /* getNodeByID Debug Code
        if(pid !=null && id !=null && pid.equals("131eaa55-1b6a-4760-82c9-718f862995c8") && id.equals("ac47abf2-d306-11d5-bd13-00b0d0794900"))
        {
            System.out.print("131eaa55-1b6a-4760-82c9-718f862995c8,ac47abf2-d306-11d5-bd13-00b0d0794900");
            System.out.println(" is Cofactor? "+ cofactor);
        }
        //*/

        if (pid == null || pid.equals("null")) {
            return getNodeByPathCaseID(id); //searching for a process node, no duplicate possibility
        } else {
            for (Node node : graphViewer.view.getGraph2D().getNodeArray()) {
                for (Iterator it = nodeToPathCaseId.get(node).iterator(); it.hasNext();)
                { //check each PathCaseID this node represents
                    String tmpid = (String) it.next();
                    if (tmpid != null && tmpid.equals(id)) {
                        NodeCursor nc = node.neighbors();
                        for (int i = 0; i < nc.size(); i++) {
                            nc.cyclicNext();
                            Node tmpn = nc.node();
                            String tmpPid = nodeToPathCaseId.get(tmpn).iterator().next(); //process node only associate with one PathCaseID
                            if (tmpPid != null && tmpPid.equals(pid)) {
                                PathCaseShapeNodeRealizer nr = (PathCaseShapeNodeRealizer) (graphViewer.view.getGraph2D().getRealizer(node));
                                boolean c;

                                PathCaseShapeNodeRealizer.PathCaseNodeRole role = nr.getNodeRole();
                                if (role == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOR
                                        || role == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTORIN
                                        || role == PathCaseShapeNodeRealizer.PathCaseNodeRole.COFACTOROUT) {
                                    c = true;
                                } else {
                                    c = false;
                                }

                                if (c == cofactor) {
                                    return node;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    //pid is the GUID of one process connected with the node, if the node is a process node, then pid should be "null" or null
    public Edge getEdgeByPathCaseID(String psid, String sid, String tid) {
        boolean flag = false;
        if (psid == null || psid.equals("null")) {
            flag = true; //source node is a process node
        }
        Graph2D view = graphViewer.view.getGraph2D();
        for (Edge edge : view.getEdgeArray()) {
            EdgeRealizer er = graphViewer.view.getGraph2D().getRealizer(edge);
            NodeRealizer nrs = er.getSourceRealizer();
            NodeRealizer nrt = er.getTargetRealizer();
            HashSet<String> s = nodeToPathCaseId.get(nrs.getNode());
            HashSet<String> t = nodeToPathCaseId.get(nrt.getNode());
            if (s.contains(sid) && t.contains(tid)) {
                if (flag) {
                    return edge;
                } else {
                    Node node = nrs.getNode();
                    NodeCursor nc = node.neighbors();
                    for (int i = 0; i < nc.size(); i++) {//see if the source node connected with the same process node
                        nc.cyclicNext();
                        Node tmpn = nc.node();
                        String tmpPid = nodeToPathCaseId.get(tmpn).iterator().next();//process node only has one PathCaseID associated
                        if (tmpPid.equals(psid)) {
                            return edge;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * apply stored a single pathway layout
     * @deprecated use applyFrozenLayout(LayoutInfo[])
     */
    private void storedSinglePathwayLayout(LayoutInfo info) {
        if (info.isEmpty()) {
            initialLayout();
            return;
        }

        applyLayoutInfo(info);
    }

    /**
     * apply stored layout for 2 neighbouring pathways
     * @deprecated use applyFrozenLayout(LayoutInfo[])
     * @param info1
     * @param info2
     */
//    private void storedTwoPathwayLayout(LayoutInfo info1, LayoutInfo info2) {
//        Graph2D view = graphViewer.view.getGraph2D();
//
//        if (info1.isEmpty() && info2.isEmpty()) {//not using freezed layout at all
//            initialLayout();
//            return;
//        } else if (info2.isEmpty()) {
//            initialLayout();
//            //*
//            String layout = layoutInfo();
//            LayoutInfo info = new LayoutParser().getParsedInfo(layout);
//            LayoutBox box = info.getExactLayoutBox();
//            applyLayoutInfo(info1,box.topleft.x+box.width,0);
//            //*/
//            return;
//        } else if (info1.isEmpty()) {
//            initialLayout();
//            //*
//            String layout = layoutInfo();
//            LayoutInfo info = new LayoutParser().getParsedInfo(layout);
//            LayoutBox box = info.getExactLayoutBox();
//            applyLayoutInfo(info2,box.topleft.x+box.width,0);
//            //*/
//            return;
//        }
//
//        info1.convertToRelativePositions();
//        info2.convertToRelativePositions();
//        LayoutBox box1 = info1.getExactLayoutBox();
//        LayoutBox box2 = info2.getExactLayoutBox();
//        LinkedHashMap<LayoutInfo.NodeLayout, LayoutInfo.NodeLayout> shared = info1.sharedNodes(info2);
//
//        /*Debug code
//        System.out.println("shared nodes");
//        Iterator<LayoutInfo.NodeLayout> e = shared.keySet().iterator();
//        while(e.hasNext()){
//            LayoutInfo.NodeLayout nl = e.next();
//            System.out.println(nl.nodeID+"  "+shared.get(nl).nodeID);
//        }
//        //System.out.println("Box1     ("+box1.topleft.x+","+box1.topleft.y+");("+(box1.topleft.x+box1.width)+","+(box1.topleft.y+box1.height));
//        //System.out.println("Box2    ("+(box1.topleft.x+box1.width+120)+");("+( box1.topleft.y+40));
//        //*/
//        //if(shared.size() == 0)return;
//
//        if(shared.size() == 0){
//            /**
//             * place one first(larger one here)
//             * then try place the second one to make the new large box bounding all nodes more square-like
//             */
//            LayoutBox largeBox;
//            LayoutInfo smallInfo;
//            if(box1.isLargerBox(box2)){
//                applyLayoutInfo(info2);
//                largeBox=box2;
//                smallInfo=info1;
//            }else{
//                applyLayoutInfo(info1);
//                largeBox=box1;
//                smallInfo=info2;
//            }
//            placePathwayOutsideBox(largeBox,smallInfo);
//            return;
//        }else{
//            applyLayoutInfo(info1);
//            placeAnotherPathway(box1,info2,shared,null);
//        }
//    }

}

