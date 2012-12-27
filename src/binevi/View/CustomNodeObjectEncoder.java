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

import y.io.gml.ObjectEncoder;

/** This class is used in conjunction with {@link }.
 * It appends additional attribute value pairs to the encoding
 * of a .graph.node section in a GML file. This class is designed
 * as a decorator.
 */
public class CustomNodeObjectEncoder implements ObjectEncoder
{
  
  private java.util.List list;
  
  private ObjectEncoder nodeEncoder;
  
  /** Creates a new instance of CustomNodeObjectEncoder
   * using the decorated nodeEncoder and the list as created by
   * the {@link } method.
   * @param nodeEncoder the nodeEncoder used for the normal GML encoding
   * @param list the list of the meta data, as obtained from
   * {@link }
   */
  public CustomNodeObjectEncoder(ObjectEncoder nodeEncoder, java.util.List list)
  {
    this.list = list;
    this.nodeEncoder = nodeEncoder;
  }
  
  /** delegates the encoding to the decorated encoder and appends
   * the custom attributes
  */  
  public void encode(Object item, y.io.gml.GMLEncoder encoder) throws java.io.IOException
  {
    // do standard encoding
    nodeEncoder.encode(item, encoder);
    
    // append the attributes
    for (java.util.Iterator it = list.iterator(); it.hasNext();){
      PathCaseViewerMetabolomics.NodeData data = (PathCaseViewerMetabolomics.NodeData) it.next();
      Object value = data.getNodeMap().get(item);
      if (value != null){
          // boolean is handled as integer by convention in GML
          if (data.getClassType().equals(Boolean.class)){
            int i = (((Boolean)value).booleanValue())?1:0;
            encoder.addAttribute(data.getPropertyName(), i);
          } else {
            encoder.addAttribute(data.getPropertyName(), value);
          }
      }
    }
  }  
}
