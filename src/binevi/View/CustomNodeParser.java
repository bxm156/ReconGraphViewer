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

import y.view.Graph2D;
import java.util.*;
import y.base.NodeMap;
import y.base.Node;

/** This is a customized version of the default node parsing class.
 * The standard parsing is done by the parent class.
 * When parsing has finished, the {@link #end()} method
 * performs the final work.
 */
public class CustomNodeParser extends y.io.gml.NodeParser
{
  private List nodeDataList;
  
  /** Creates a new instance of CustomNodeParser
   * The nodeDataList is provided to gain access to the meta-data
   * objects, which describe the additional node attributes.
   * @param nodeDataList
   */
  public CustomNodeParser(Graph2D graph, Map id2Node, List nodeDataList)
  {
    super(graph, id2Node);
    this.nodeDataList = nodeDataList;
  }
  
  /** this method is called when the parsing of the node section
   * has ended. This implementation calls
   * <CODE>super.end()</CODE> and then fills the NodeMaps as
   * obtained from the List of NodeData objects with the data
   * collected during the parsing process.
   */  
  public void end(){
    super.end();
    Node node = (Node) super.getItem();
    Map attributes = getAttributes();
    for (java.util.Iterator it = nodeDataList.iterator(); it.hasNext();){
      PathCaseViewerMetabolomics.NodeData data = (PathCaseViewerMetabolomics.NodeData) it.next();
      Object value = attributes.get(data.getPropertyName());
      NodeMap nodeMap = data.getNodeMap();
      if (value != null){
          if (data.getClassType().equals(Boolean.class)){
            boolean b = ((Number)value).intValue()>0;
            nodeMap.setBool(node, b);
          } else {
            nodeMap.set(node, value);
          }
      }
    }
  }
}
