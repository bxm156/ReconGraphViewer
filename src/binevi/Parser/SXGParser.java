package binevi.Parser;

import binevi.View.PathCaseViewGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import y.base.Edge;
import y.view.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;

public class SXGParser {

    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;
    Graph2D graph;
    Hashtable<String, y.base.Node> idToNodeTable;

    public SXGParser(Graph2D graph) {
        this.graph = graph;
        idToNodeTable = new Hashtable<String, y.base.Node>();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    // GraphData
    public void loadGraphFromSXGFString(String graphXML) {
        //System.out.println(graphXML);
        try {
            Document document = builder.parse(new InputSource(new StringReader(graphXML)));

            //  Graph
            NodeList graphroots = document.getElementsByTagName("Graph");
            Node graphroot = graphroots.item(0);
            NodeList graphElements = graphroot.getChildNodes();
            for (int i = 0; i < graphElements.getLength(); i++) {
                Node graphElement = graphElements.item(i);

                // Graph/Name
                if (graphElement.getNodeName().equals("Name")) {
                    System.out.println("Reading Graph");
                }
                // GraphData/Nodes
                else if (graphElement.getNodeName().equals("Nodes")) {
                    System.out.println("Reading Nodes");
                    loadNodesFromXMLNode(graphElement);
                }
                // GraphData/Edges
                else if (graphElement.getNodeName().equals("Edges")) {
                    System.out.println("Reading Edges");
                    loadEdgesFromXMLNode(graphElement);
                }

            }

        } catch (SAXException sxe) {
            // Error generated during parsing
            Exception x = sxe;
            if (sxe.getException() != null)
                x = sxe.getException();
            x.printStackTrace();

        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
        }
    }

    //<Edge Label="" SourceNodeId ="" DestinationNodeId="" Direction=""/>
    private void loadEdgesFromXMLNode(Node graphElement) {
        NodeList moleculenodes = graphElement.getChildNodes();
        for (int i = 0; i < moleculenodes.getLength(); i++) {
            Node moleculenode = moleculenodes.item(i);
            // Graph/Nodes/Node
            if (moleculenode.getNodeName().equals("Edge")) {
                NamedNodeMap moleculeAttributes = moleculenode.getAttributes();
                String EdgeLabel = moleculeAttributes.getNamedItem("Label").getNodeValue();
                String SourceNodeId = moleculeAttributes.getNamedItem("SourceNodeId").getNodeValue();
                String DestinationNodeId = moleculeAttributes.getNamedItem("DestinationNodeId").getNodeValue();
                String Direction = moleculeAttributes.getNamedItem("Direction").getNodeValue();
                String LabelFontName = moleculeAttributes.getNamedItem("LabelFontName").getNodeValue();
                String LabelFontSize = moleculeAttributes.getNamedItem("LabelFontSize").getNodeValue();
                String LineStyle = moleculeAttributes.getNamedItem("LineStyle").getNodeValue();
                String LineColor = moleculeAttributes.getNamedItem("LineColor").getNodeValue();
                String TextColor = moleculeAttributes.getNamedItem("TextColor").getNodeValue();
                String LineThickness = moleculeAttributes.getNamedItem("LineThickness").getNodeValue();

                System.out.println("Reading Edge " + i + ": " + SourceNodeId + "," + DestinationNodeId);

                int thickness = Integer.parseInt(LineThickness);
                int fontsize = Integer.parseInt(LabelFontSize);
                Color linecolor = PathCaseViewGenerator.colorNameToColorObject(LineColor);
                Color textcolor = PathCaseViewGenerator.colorNameToColorObject(TextColor);

                y.base.Node item1 = idToNodeTable.get(SourceNodeId);
                y.base.Node item2 = idToNodeTable.get(DestinationNodeId);

                Edge edge = graph.createEdge(item1, item2);
                EdgeRealizer er = graph.getRealizer(edge);
                //er = new PolyLineEdgeRealizer();
                //er = new BezierEdgeRealizer ();
                //er = new ArcEdgeRealizer() ;
                //er = new QuadCurveEdgeRealizer ();
                //er = new SplineEdgeRealizer ();
                er.setLineColor(linecolor);

                if (Direction.equals("TARGET"))
                    er.setTargetArrow(Arrow.STANDARD);
                else if (Direction.equals("SOURCE"))
                    er.setSourceArrow(Arrow.STANDARD);
                else if (Direction.equals("BOTH")) {
                    er.setTargetArrow(Arrow.STANDARD);
                    er.setSourceArrow(Arrow.STANDARD);
                } else {
                    //NONE
                }

                y.view.EdgeLabel edgelabel = er.getLabel();
                edgelabel.setModel(y.view.EdgeLabel.SIDE_SLIDER);
                edgelabel.setText(EdgeLabel);
                edgelabel.setFontName(LabelFontName);
                edgelabel.setFontSize(fontsize);
                edgelabel.setTextColor(textcolor);
                er.setLineColor(linecolor);
                if (LineStyle.equals("DASHED") && thickness == 1)
                    er.setLineType(LineType.DASHED_1);
                else if (LineStyle.equals("DASHED") && thickness == 2)
                    er.setLineType(LineType.DASHED_2);
                else if (LineStyle.equals("DASHED") && thickness == 3)
                    er.setLineType(LineType.DASHED_3);
                else if (LineStyle.equals("DASHED") && thickness == 4)
                    er.setLineType(LineType.DASHED_4);
                else if (LineStyle.equals("DASHED") && thickness == 5)
                    er.setLineType(LineType.DASHED_5);
                else if (LineStyle.equals("DOTTED") && thickness == 1)
                    er.setLineType(LineType.DOTTED_1);
                else if (LineStyle.equals("DOTTED") && thickness == 2)
                    er.setLineType(LineType.DOTTED_2);
                else if (LineStyle.equals("DOTTED") && thickness == 3)
                    er.setLineType(LineType.DOTTED_3);
                else if (LineStyle.equals("DOTTED") && thickness == 4)
                    er.setLineType(LineType.DOTTED_4);
                else if (LineStyle.equals("DOTTED") && thickness == 5)
                    er.setLineType(LineType.DOTTED_5);
                else if (LineStyle.equals("NORMAL") && thickness == 1)
                    er.setLineType(LineType.LINE_1);
                else if (LineStyle.equals("NORMAL") && thickness == 2)
                    er.setLineType(LineType.LINE_2);
                else if (LineStyle.equals("NORMAL") && thickness == 3)
                    er.setLineType(LineType.LINE_3);
                else if (LineStyle.equals("NORMAL") && thickness == 4)
                    er.setLineType(LineType.LINE_4);
                else if (LineStyle.equals("NORMAL") && thickness == 5)
                    er.setLineType(LineType.LINE_5);
                else if (LineStyle.equals("NORMAL") && thickness == 6)
                    er.setLineType(LineType.LINE_6);
                else if (LineStyle.equals("NORMAL") && thickness == 7)
                    er.setLineType(LineType.LINE_7);


            }
        }
    }


    //<Node Id="" Label="" Shape="" Width="" Height ="" Color=""/>
    private void loadNodesFromXMLNode(Node graphElement) {
        NodeList moleculenodes = graphElement.getChildNodes();
        for (int i = 0; i < moleculenodes.getLength(); i++) {
            Node moleculenode = moleculenodes.item(i);
            // Graph/Nodes/Node
            if (moleculenode.getNodeName().equals("Node")) {
                NamedNodeMap moleculeAttributes = moleculenode.getAttributes();
                String NodeID = moleculeAttributes.getNamedItem("Id").getNodeValue();
                String NodeLabel = moleculeAttributes.getNamedItem("Label").getNodeValue();
                String LabelFontName = moleculeAttributes.getNamedItem("LabelFontName").getNodeValue();
                String LabelFontSize = moleculeAttributes.getNamedItem("LabelFontSize").getNodeValue();
                String LabelInsideStr = moleculeAttributes.getNamedItem("LabelInside").getNodeValue();
                String Shape = moleculeAttributes.getNamedItem("Shape").getNodeValue();
                String WidthStr = moleculeAttributes.getNamedItem("Width").getNodeValue();
                String HeightStr = moleculeAttributes.getNamedItem("Height").getNodeValue();
                String TextColorStr = moleculeAttributes.getNamedItem("TextColor").getNodeValue();
                String FillColorStr = moleculeAttributes.getNamedItem("FillColor").getNodeValue();
                String BorderColorStr = moleculeAttributes.getNamedItem("BorderColor").getNodeValue();

                System.out.println("Reading Node " + i + ": " + NodeID + " =  " + NodeLabel);

                int width = Integer.parseInt(WidthStr);
                int height = Integer.parseInt(HeightStr);
                int fontsize = Integer.parseInt(LabelFontSize);
                boolean labelinside = Boolean.parseBoolean(LabelInsideStr);
                Color textcolor = PathCaseViewGenerator.colorNameToColorObject(TextColorStr);
                Color fillcolor = PathCaseViewGenerator.colorNameToColorObject(FillColorStr);
                Color bordercolor = PathCaseViewGenerator.colorNameToColorObject(BorderColorStr);

                y.base.Node graphnode = graph.createNode();
                ShapeNodeRealizer nr = new ShapeNodeRealizer();
                if (Shape.equals("ELLIPSE"))
                    nr.setShapeType(ShapeNodeRealizer.ELLIPSE);
                else if (Shape.equals("RECTANGLE"))
                    nr.setShapeType(ShapeNodeRealizer.RECT);
                else if (Shape.equals("TRIANGLE"))
                    nr.setShapeType(ShapeNodeRealizer.TRIANGLE);
                else if (Shape.equals("DIAMOND"))
                    nr.setShapeType(ShapeNodeRealizer.DIAMOND);
                else if (Shape.equals("ROUNDRECTANGLE"))
                    nr.setShapeType(ShapeNodeRealizer.ROUND_RECT);
                else
                    nr.setShapeType(ShapeNodeRealizer.RECT);

                nr.setSize(width, height);
                y.view.NodeLabel nodelabel = nr.createNodeLabel();
                if (!labelinside)
                    nodelabel.setModel(y.view.NodeLabel.EIGHT_POS);
                else
                    nodelabel.setModel(y.view.NodeLabel.CENTER);

                nodelabel.setText(NodeLabel);
                nodelabel.setFontName(LabelFontName);
                nodelabel.setFontSize(fontsize);
                nodelabel.setTextColor(textcolor);
                nr.setLineColor(bordercolor);
                nr.setFillColor(fillcolor);
                nr.setLabel(nodelabel);

                graph.setRealizer(graphnode, nr);
                idToNodeTable.put(NodeID, graphnode);


            }
        }
    }


}
