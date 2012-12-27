package binevi.View;

import binevi.Resources.PathCaseResources.OrganismTable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Random;
import java.util.UUID;
import java.io.*;
import java.sql.*;

import y.io.JPGIOHandler;
import y.util.D;



public class PathCaseViewerApplet extends JApplet {

    //shows the final version
    public static final String BUILD_STRING = "200711010000";
    boolean bSave=false;//true;
    Connection con = null;

    //checks JRE version and stops working if an old version
    public boolean JVMVersionCheck() {
        String version = System.getProperties().getProperty("java.specification.version");
        StringTokenizer st = new StringTokenizer(version, ".");
        int rev = Integer.parseInt(st.nextToken());
        int build = Integer.parseInt(st.nextToken());

        boolean check = rev >= 1 && build >= 6;

        if (!check) {
            JLabel label = new JLabel("You are using a previous version of Java (<1.6), please update it.");
            label.setFont(new Font("Times", Font.BOLD, 25));
            setLayout(new BorderLayout());
            getRootPane().getContentPane().add(label, BorderLayout.CENTER);
            return false;
        }

        return true;
    }

    //MAIN GUI PANEL
    PathCaseViewer gui;
    PathCaseViewerMetabolomics guiCompartmentH;
    public static boolean APPLETFINISHED = false;
    //clean all parameters
    public void stop() {
        super.stop();
//        System.out.println("Dying...");
        if(bSave){driverManager();bSave=false;}
//        try{
//        String m=driverManagerRT();}
//        catch(Exception e){}
//        if(con!=null) {
//            try{
//                con.close();
//            }catch(Exception e){
//
//            }
//        }

        if(((String)this.getParameter("graphContent")).equalsIgnoreCase("model")){
             if (guiCompartmentH != null) {
                guiCompartmentH.destroy();
            }
//            guiCompartmentH = null;
        } else{
            if (gui != null) {
                gui.destroy();
            }
            gui = null;
        }

        System.out.println("Killed viewer panel...");

        this.getContentPane().removeAll();
        this.removeAll();

        System.out.println("Killed applet components...");

        if (getAppletContext() == null) {
            System.out.println("Applet context is null...");
        }

        destroy();
        System.gc();
        super.stop();

        //Runtime.getRuntime().exit(0);
    }

    public void init() {

        if (!JVMVersionCheck() || !appletVersionCheck())
            return;  /**/ //commented by Xinjian for 433

        //SwingUtility.setPlaf("javax.swing.plaf.metal.MetalLookAndFeel");
//      SwingUtility.setPlaf("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		SwingUtility.setPlaf("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//		SwingUtility.setPlaf("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

        //read and process parameters
        String WebServiceUrl = this.getParameter("WebServiceUrl");
        String RedirectionWebPageUrl = this.getParameter("RedirectionWebPageUrl");
        String highlightEntities = this.getParameter("highlightEntities");
        String setOrganism = this.getParameter("setOrganism");
        String allowOrganismSelection = this.getParameter("allowOrganismSelection");
        String allowGeneViewer = this.getParameter("allowGeneViewer");
        String FrozenLayout = this.getParameter("FrozenLayout");
        String allowSavingLayout = this.getParameter("allowSavingLayout");
        String queryResult = this.getParameter("queryResult");
        String setFullScreen = this.getParameter("setFullScreen");

        String viewID = this.getParameter("viewID");
        String terms = this.getParameter("terms");
        String type = this.getParameter("type");
        String page = this.getParameter("page");

        String geneViewerHelpURL = this.getParameter("geneViewerHelpURL");
        String layout = this.getParameter("layout");
        String sourceNode = this.getParameter("sourceNode");

        String collapsedPathwayGuids = this.getParameter("collapsedPathwayGuids");
        String expandedPathwayGuids = this.getParameter("expandedPathwayGuids");
        String genericProcessGuids = this.getParameter("genericProcessGuids");
        String moleculeGuids = this.getParameter("moleculeGuids");

        String loadFromBioPAX = this.getParameter("loadFromBioPAX");
        String compartmentH=this.getParameter("graphContent");
        String compartmentID=this.getParameter("compartmentID");
        String modelID=this.getParameter("expandedModelGuids");
        String reactionGuids=this.getParameter("reactionGuids");
        String xmlLocation=this.getParameter("MQLXMLlocation");

        if (WebServiceUrl == null) WebServiceUrl = "";
        if (RedirectionWebPageUrl == null) RedirectionWebPageUrl = "";

        if (highlightEntities == null) highlightEntities = "";
        if (setOrganism == null) setOrganism = OrganismTable.ROOTID;
        if (allowOrganismSelection == null || allowOrganismSelection.equals("")) allowOrganismSelection = "true";
        if (allowGeneViewer == null) allowGeneViewer = "true";
        if (FrozenLayout == null) FrozenLayout = "false";
        if (allowSavingLayout == null) allowSavingLayout = "false";
        if (queryResult == null) queryResult = "false";
        if (geneViewerHelpURL == null) geneViewerHelpURL = "";
        if (collapsedPathwayGuids == null) collapsedPathwayGuids = "";
        if (expandedPathwayGuids == null) expandedPathwayGuids = "";
        if (genericProcessGuids == null) genericProcessGuids = "";
        if (moleculeGuids == null) moleculeGuids = "";
        if (setFullScreen == null || setFullScreen.equals("")) setFullScreen = "false";
        if (viewID == null) viewID = "";
        if (terms == null) terms = "";
        if (type == null) type = "";
        if (page == null) page = "";
        if (loadFromBioPAX == null || loadFromBioPAX.equals("")) loadFromBioPAX = "false";
        if (compartmentH==null) compartmentH="pathway";
        if (compartmentID==null) compartmentID="";
        if (modelID==null) modelID="";
        if (reactionGuids==null) reactionGuids="";

        if (layout == null) layout = "";
        if (sourceNode == null) sourceNode = "";
        if (xmlLocation == null) xmlLocation= "";

        WebServiceUrl = WebServiceUrl.toLowerCase();
        if (!WebServiceUrl.endsWith("?wsdl")) WebServiceUrl += "?wsdl";
        RedirectionWebPageUrl = RedirectionWebPageUrl.toLowerCase();
        highlightEntities = highlightEntities.toLowerCase();
        setOrganism = setOrganism.toLowerCase();
        allowOrganismSelection = allowOrganismSelection.toLowerCase().trim();
        allowGeneViewer = allowGeneViewer.toLowerCase();
        queryResult = queryResult.toLowerCase();
        geneViewerHelpURL = geneViewerHelpURL.toLowerCase();
        collapsedPathwayGuids = collapsedPathwayGuids.toLowerCase();
        expandedPathwayGuids = expandedPathwayGuids.toLowerCase();
        genericProcessGuids = genericProcessGuids.toLowerCase();
        moleculeGuids = moleculeGuids.toLowerCase();
        setFullScreen = setFullScreen.trim().toLowerCase();
        viewID = viewID.toLowerCase();
        terms = terms.toLowerCase();
        type = type.toLowerCase();
        page = page.toLowerCase();
        modelID=modelID.trim();

        layout = layout.toLowerCase().trim();
        sourceNode = sourceNode.toLowerCase().trim();

        //perform actions by parameters
        HashMap<String, Object> configuration = new HashMap<String, Object>();
        configuration.put("WebServiceUrl", WebServiceUrl);
        //pathCaseViewer.setPathCaseWebServiceLocation(WebServiceUrl);
        configuration.put("collapsedPathwayGuids", collapsedPathwayGuids);
        configuration.put("expandedPathwayGuids", expandedPathwayGuids);
        configuration.put("genericProcessGuids", genericProcessGuids);
        configuration.put("moleculeGuids", moleculeGuids);
        configuration.put("setOrganism", setOrganism);
        configuration.put("highlightEntities", highlightEntities);
        //pathCaseViewer.loadAllThreaded(collapsedPathwayGuids, expandedPathwayGuids, genericProcessGuids, moleculeGuids, setOrganism, highlightEntities);

        //initial layout
        configuration.put("layout", layout);
        configuration.put("sourceNode", sourceNode);
        //pathCaseViewer.setInitialLayout(layout, sourceNode);

        configuration.put("RedirectionWebPageUrl", RedirectionWebPageUrl);
        configuration.put("viewID", viewID);
        configuration.put("geneViewerHelpURL", geneViewerHelpURL);
        configuration.put("terms", terms);
        configuration.put("type", type);
        configuration.put("page", page);
        //pathCaseViewer.setRedirectionData(this, RedirectionWebPageUrl, viewID, geneViewerHelpURL, terms, type, page);
        configuration.put("loadFromBioPAX", loadFromBioPAX.equalsIgnoreCase("true"));
        configuration.put("compartmentH", compartmentH);
        configuration.put("compartmentID", compartmentID);
        configuration.put("modelID", modelID);
        configuration.put("reactionGuids", reactionGuids);
        //wy
        configuration.put("FrozenLayout", FrozenLayout.equalsIgnoreCase("true"));

        configuration.put("allowSavingLayout", allowSavingLayout.equalsIgnoreCase("true"));

        configuration.put("rightClickQueryingEnabled", !setFullScreen.equalsIgnoreCase("true"));
        //gui.setRightClickQueryingEnabled(!setFullScreen.equals("true"));

        configuration.put("warnUserWhenOrganismChanges", queryResult.equals("true"));
        //gui.warnUserWhenOrganismChanges(queryResult.equals("true"));

        configuration.put("geneViewerEnabled", allowGeneViewer.equals("true"));
        //gui.setGeneViewerEnabled(allowGeneViewer.equals("true"));

        configuration.put("organismBrowserEnabled", allowOrganismSelection.equals("true"));
        //gui.setOrganismBrowserEnabled(allowOrganismSelection.equals("true"));
        configuration.put("appletWidth", String.valueOf(this.getWidth()));
        configuration.put("appletHeight", String.valueOf(this.getHeight()));
        configuration.put("xmlLocation", xmlLocation);

//        System.out.println(compartmentH);
//        System.out.println(modelID);
       boolean bMapping2Pathway=(modelID.split(";").length)<2;
        if(compartmentH.equalsIgnoreCase("usermodel"))bMapping2Pathway=false;
        if(compartmentH.equalsIgnoreCase("true")){
            guiCompartmentH = new PathCaseViewerMetabolomics(this, allowGeneViewer.equalsIgnoreCase("true"), allowOrganismSelection.equalsIgnoreCase("true"), allowSavingLayout.equalsIgnoreCase("true"),bMapping2Pathway);
            guiCompartmentH.setConfiguration(configuration);

            while (getRootPane() == null || guiCompartmentH == null || guiCompartmentH.getContentPane() == null) {
                System.out.println("Applet not initialized successfully!!!!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("ERROR:");
                    e.printStackTrace();
                    System.out.println("-------------------------------");
                }
            }
            setContentPane(guiCompartmentH.getContentPane());
            guiCompartmentH.processAppletParameters();
            APPLETFINISHED=true;
        } else if(compartmentH.equalsIgnoreCase("model") || compartmentH.equalsIgnoreCase("hybrid") || compartmentH.equalsIgnoreCase("usermodel")|| compartmentH.equalsIgnoreCase("MQLXML")){
            guiCompartmentH = new PathCaseViewerMetabolomics(this, false,false, allowSavingLayout.equalsIgnoreCase("true"),bMapping2Pathway);
//            guiCompartmentH = new PathCaseViewerMetabolomics(this,false,false,false);
            guiCompartmentH.setConfiguration(configuration);

            while (getRootPane() == null || guiCompartmentH == null || guiCompartmentH.getContentPane() == null) {
                System.out.println("Applet not initialized successfully!!!!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("ERROR:");
                    e.printStackTrace();                                                                         
                    System.out.println("-------------------------------");
                }
            }
            setContentPane(guiCompartmentH.getContentPane());
            guiCompartmentH.processAppletParameters(true);
            APPLETFINISHED=true;
//            guiCompartmentH.graphViewer.view.fitContent();
//            guiCompartmentH.graphViewer.view.updateView();
//            try{
//                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");// do not need in jdbc4.0
//                String connectionUrl = "jdbc:sqlserver://dblab.case.edu:1433;" +
//                "databaseName=StructuredUserFeedback;user=pathcase;password=dblab;";
////                con = DriverManager.getConnection(connectionUrl);
//            }catch(Exception e){
//
//            }
        } else if(compartmentH.equalsIgnoreCase("pathway")){
            gui = new PathCaseViewer(this, allowGeneViewer.equalsIgnoreCase("true"), allowOrganismSelection.equalsIgnoreCase("true"), allowSavingLayout.equalsIgnoreCase("true"));
            gui.setConfiguration(configuration);

            while (getRootPane() == null || gui == null || gui.getContentPane() == null) {
                System.out.println("Applet not initialized successfully!!!!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("ERROR:");
                    e.printStackTrace();
                    System.out.println("-------------------------------");
                }
            }
            setContentPane(gui.getContentPane());
           gui.processAppletParameters();
          APPLETFINISHED = true;
        }
//        else if(compartmentH.equalsIgnoreCase("MQLXML")){
//            guiCompartmentH = new PathCaseViewerMetabolomics(this, false,false, allowSavingLayout.equalsIgnoreCase("true"),bMapping2Pathway);
//            guiCompartmentH.setConfiguration(configuration);
//
//            while (getRootPane() == null || guiCompartmentH == null || guiCompartmentH.getContentPane() == null) {
//                System.out.println("Applet not initialized successfully!!!!");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    System.out.println("ERROR:");
//                    e.printStackTrace();
//                    System.out.println("-------------------------------");
//                }
//            }
//            setContentPane(guiCompartmentH.getContentPane());
//            guiCompartmentH.processAppletParameters(true);
//            APPLETFINISHED=true;
//
//        }
}

    public boolean appletVersionCheck() {
        String AppletVersion = this.getParameter("AppletVersion");

        if (AppletVersion == null || AppletVersion.compareTo(BUILD_STRING) > 0) {
            int result = JOptionPane.showConfirmDialog(this, "There is a newer version of the applet.\nPlease clean your browser cache and refresh the page to load the new version.\nCurrent version may not be compatible with the rest of the system.\nDo you still want to use the current version?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            //requestFocus();
            if (result != JOptionPane.YES_OPTION) {
                return false;
            }
        } else if (AppletVersion.compareTo(BUILD_STRING) < 0) {
            JOptionPane.showMessageDialog(this, "VERSION CHECK PROBLEM: Developer needs to update the server version number to: " + BUILD_STRING + ".\n", "Warning", JOptionPane.WARNING_MESSAGE);
        }

        return true;
    }

        public String driverManagerRT()  throws IOException {

             UUID idOne = UUID.randomUUID();

        try {

            JPGIOHandler ioh = new JPGIOHandler();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ioh.write(this.guiCompartmentH.graphViewer.view.getGraph2D(),out);
            ByteArrayInputStream ins=new ByteArrayInputStream(out.toByteArray());
            String query;
            PreparedStatement pstmt;

            query = ("insert into appletimage VALUES(?,?)");
            pstmt = con.prepareStatement(query);

            pstmt.setNString(1,idOne.toString());

            // Method used to insert a stream of bytes
            pstmt.setBinaryStream(2,ins);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();

        }
            return idOne.toString();//"Appplet saved(in JPEG);";

    }

    public void driverManager() {

        Connection con = null;
//        Statement stmt = null;
//        ResultSet rset = null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");// do not need in jdbc4.0
            String connectionUrl = "jdbc:sqlserver://dblab2.case.edu:1433;" +
            "databaseName=StructuredUserFeedback;user=pathcase;password=dblab;";
            con = DriverManager.getConnection(connectionUrl);

            JPGIOHandler ioh = new JPGIOHandler();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ioh.write(this.guiCompartmentH.graphViewer.view.getGraph2D(),out);
            ByteArrayInputStream ins=new ByteArrayInputStream(out.toByteArray());
            String query;
            PreparedStatement pstmt;

            Statement stmt = con.createStatement(); 
            ResultSet rsn = stmt.executeQuery("select count(*) from appletimage;");
            int n=0;
            while(rsn.next())
                n=rsn.getInt(1)+1;


            query = ("insert into appletimage VALUES(?,?)");
            pstmt = con.prepareStatement(query);

//            Random ra=new Random();
            pstmt.setInt(1,n);// ra.nextInt(1000));
            // Method used to insert a stream of bytes
            pstmt.setBinaryStream(2,ins);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();

            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }

        } finally {

            if (con != null) {
                try {
                    System.out.print("  Closing down all connections...\n\n");
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
