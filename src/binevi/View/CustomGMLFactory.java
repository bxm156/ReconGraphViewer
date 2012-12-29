/****************************************************************************
 **
 ** This file is part of yFiles-2.5.0.3. 
 ** 
 ** yWorks proprietary/confidential. Use is subject to license terms.
 **
 ** Redistribution of this file or of an unauthorized byte-code version
 ** of this file is strictly forbidden.
 **
 ** Copyright (c) 2000-2007 by yWorks GmbH, Vor dem Kreuzberg 28, 
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ***************************************************************************/

package binevi.View;

import y.io.gml.GMLTokenizer.Callback;
import y.view.Graph2D;
import y.io.gml.*;

/** This class is used to customize the process of GML encoding
 * and parsing.
 */
public class CustomGMLFactory implements y.io.gml.EncoderFactory, y.io.gml.ParserFactory
{
  
  private java.util.List nodeDataList;
  
  /** Constructs a new factory
   * @param nodeDataList the list of meta data object as created in the
   * <CODE>CustomGMLDemo#createNodeDataList()</CODE> method
   */  
  public CustomGMLFactory(java.util.List nodeDataList)
  {
    this.nodeDataList = nodeDataList;
  }
  
  /** Returns a modified version of the default edge parser,
   * this version will not encode the edgerealizer, that
   * is edges will not contain any graphical information.
   * This is an example of a less elaborate modification of the
   * parser
   */  
  public ObjectEncoder createEdgeEncoder(ObjectEncoder graphEncoder)
  {
    return new y.io.gml.EdgeObjectEncoder(null, null);
  }
  
  /**
   * @param graph the graph which will be modified by this parser
   * @return a parser
   */
  public Callback createEdgeParser(Graph2D graph, Callback graphParser)
  {
    GraphParser gp = (GraphParser) graphParser;
    ItemParser parser = new y.io.gml.EdgeParser(graph, gp.getId2Node(), gp.getId2Edge());
    return parser;
  }
  
  public ObjectEncoder createGMLEncoder()
  {
    GmlObjectEncoder gmlEncoder = new GmlObjectEncoder();
    gmlEncoder.setGraphEncoder(createGraphEncoder(gmlEncoder));
    return gmlEncoder;
  }
  
  /** return a parser which is capable of parsing a gml stream
   * and putting the result into a graph
   * @param graph the graph which will be modified by this parser
   * @return a parser
   */
  public Callback createGMLParser(Graph2D graph)
  {
    ItemParser parser = new ItemParser();
    parser.addChild("graph", (ItemParser) createGraphParser(graph, parser));
    return parser;
  }
  
  public ObjectEncoder createGraphEncoder(ObjectEncoder gmlEncoder)
  {
    y.io.gml.GraphObjectEncoder graphEncoder = new y.io.gml.GraphObjectEncoder();
    graphEncoder.setEdgeEncoder(createEdgeEncoder(graphEncoder));
    graphEncoder.setNodeEncoder(createNodeEncoder(graphEncoder));
    return graphEncoder;
  }
  
  /** return a parser which is capable of parsing the graph scope
   * and putting the result into a graph
   * @param graph the graph which will be modified by this parser
   * @return a parser
   */
  public Callback createGraphParser(Graph2D graph, Callback gmlParser)
  {
    ItemParser parser = new GraphParser(graph);
    parser.addChild("node", (ItemParser) createNodeParser(graph, parser));
    parser.addChild("edge", (ItemParser) createEdgeParser(graph, parser));
    return parser;
  }
  
  /** Returns a customized ObjectEncoder, namely
   * {@link CustomNodeObjectEncoder}
   *
   */  
  public ObjectEncoder createNodeEncoder(ObjectEncoder graphEncoder)
  {
    ObjectEncoder realizerEncoder = new y.io.gml.NodeRealizerObjectEncoder();
    ObjectEncoder nodeEncoder = new y.io.gml.NodeObjectEncoder(realizerEncoder, null);
    return new CustomNodeObjectEncoder(nodeEncoder, nodeDataList);
  }
  
  /** Returns a customized ItemParser, namely
   * {@link CustomNodeObjectEncoder}
   *
   * @param graph the graph which will be modified by this parser
   * @return a parser
   */
  public Callback createNodeParser(Graph2D graph, Callback graphParser)
  {
    GraphParser gp = (GraphParser) graphParser;
    ItemParser parser = new CustomNodeParser(graph, gp.getId2Node(), nodeDataList);
    return parser;
  }
  
}
