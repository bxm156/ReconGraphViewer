/*
 * @(#)LayoutParser.java
 * Copyright 2008 PathCase Group All rights reserved.
 */
package binevi.View;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//TODO: more robust in terms of throwing exception when mal-layout encounted
/**
 * The Class is for converting a layout string to a <code>LayoutInfo</code> object.
 * <p/>
 *
 * @author Yuan Wang
 */
public class LayoutParser {
    LayoutInfo info = new LayoutInfo();

    public LayoutInfo getParsedInfo(String layout) {    
        parseLayoutString(layout);
        return info;
    }
    
    public LayoutInfo getParsedXMLInfo(String layout) {
        parseLayoutXMLString(layout);
        return info;
    }
    
    void parseLayoutXMLString(String layout) {
        info.clearInfo();//clear in order to build new info object
        try{
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(layout)));

        	NodeList nodes = doc.getElementsByTagName("Nodes");
        	nodes =((Element)nodes.item(0)).getElementsByTagName("NodeLayout");
        	for(int i=0;i<nodes.getLength();i++){   
        		Element node = (Element)nodes.item(i);
        		if (node!=null ){//&& node.getNodeName().equals("NodeLayout")
        			parseNodeXMLElement(node);
        		}
        	}

        	NodeList edges = doc.getElementsByTagName("Edges");
        	edges = ((Element)edges.item(0)).getElementsByTagName("EdgeLayout");
        	for(int i=0;i<edges.getLength();i++){
        		Element edge = (Element)edges.item(i);
        		//String s = edge.getNodeName();
        		if(edge!=null){//&& edge.getNodeName().equals("EdgeLayout")
        			parseEdgeXMLElement(edge);
        		}        	
        	}
        }catch(Exception e){
        	System.err.println("Error when parse XML layout string: "+e);
        	info.clearInfo();        	
        }
    }
    
    void parseNodeXMLElement(Element node){
    	try{
    		String id,pid,x,y;
    		NamedNodeMap attrs = node.getAttributes();
        	id= node.getAttribute("ID");
        	pid=node.getAttribute("NeighboringProcessId");
        	x=node.getAttribute("X");
        	y=node.getAttribute("Y");
        	boolean c = false;
        	if(node.getAttribute("cofactor").toLowerCase().equals("true")){
        		c=true;
        	}
        	info.addNodeLayout(pid, id, c, Double.parseDouble(x), Double.parseDouble(y));
    	}catch(Exception e){
    		System.err.println("Error when parse a Node Element: "+e);
    	}
    }
    
    void parseEdgeXMLElement(Element edge){
    	try{
    		String sid,spid,tid,tpid;
        	sid=edge.getAttribute("SourceID");
        	spid=edge.getAttribute("SourceNeighboringProcessId");
        	boolean sc=false;
        	if(edge.getAttribute("SourceCofactor").toLowerCase().equals("true")){
        		sc=true;
        	}
        	tid=edge.getAttribute("TargetID");
        	tpid=edge.getAttribute("TargetNeighboringProcessId");
        	boolean tc=false;
        	if(edge.getAttribute("TargetCofactor").toLowerCase().equals("true")){
        		tc=true;
        	}

        	 ArrayList<LayoutPoint> b = new ArrayList<LayoutPoint>();
        	 NodeList bends = edge.getElementsByTagName("BendPoint");
             for (int i=0;i<bends.getLength();i++) {
            	 Element bend = (Element)bends.item(i);
            	 String x,y;
            	 x= bend.getAttribute("X");
            	 y= bend.getAttribute("Y");
                 b.add(info.createBend(Double.parseDouble(x), Double.parseDouble(y)));
             }
             //TODO cofactors
             info.addEdgeLayout(spid,sid,tpid,tid, b);
             
    	}catch(Exception e){
    		System.err.println("Error when parse a Node Element: "+e);
    	}
    }

    /**
     * Parse the layout string to the LayouInfo object contained in the class object.
     * beside directly use, also used in getParsedInfo(String layout)
     *
     * @see binevi.View.LayoutParser#getParsedInfo(String)
     */
    void parseLayoutString(String layout) {
        info.clearInfo();//clear in order to build new info object
        List<String> l = Arrays.asList(layout.split("\n"));

        /* debug
          while(e.hasNext())
              System.out.println(e.next());
          //*/ //end debug

        Iterator<String> e = l.iterator();
        String line;
        boolean flag = true;//to change when finished nodes part in the string

        while (e.hasNext()) {
            line = e.next();
            //System.out.println(line);
            if (line.equals("")) {
                flag = false;
                continue;
            }
            if (flag) {
//                System.out.println("Paring node");
                parseNode(line);
            } else {
//                System.out.println("Paring Edge");
                parseEdge(line);
            }
        }
    }

    /**
     * Parse a line for a node in the layout string to add a <code>NodeLayout</code> in the <code>LayoutInfo</code> object
     */
    void parseNode(String s) {
        String[] arr = s.split(",");
        if (arr.length < 4) {//not correct format
            System.err.println("bad format when parsing a node");
            return;
        } else if (arr.length == 4) {  // to work with old layout data not having Cofactors just in case.
            info.addNodeLayout(arr[0], arr[1], false, Double.parseDouble(arr[2]), Double.parseDouble(arr[3]));
            System.err.println("This node layout string is in old fashion without cofactor info.");
            return;
        }
        boolean c;
        if (arr[2] != null && arr[2].equals("cofactor")) {
            c = true;
        } else {
            c = false;
        }
        info.addNodeLayout(arr[0], arr[1], c, Double.parseDouble(arr[3]), Double.parseDouble(arr[4]));

        /* parseNode Debug Code
        if(arr[0] !=null && arr[1] !=null && arr[0].equals("131eaa55-1b6a-4760-82c9-718f862995c8") && arr[1].equals("ac47abf2-d306-11d5-bd13-00b0d0794900"))
        {
            System.out.print("131eaa55-1b6a-4760-82c9-718f862995c8,ac47abf2-d306-11d5-bd13-00b0d0794900 ");
            System.out.println(c);
        }
        //*/

        //info.addNodeLayout("node"+count,count,count);
        //System.out.print(arr[0]+"\n");
    }

    /**
     * Parse a line for an edge in the layout string to add a <code>EdgeLayout<code> in the <code>LayoutInfo<code> object
     */
    void parseEdge(String s) {
        String[] arr = s.split(",");
        ArrayList<LayoutPoint> b = new ArrayList<LayoutPoint>();
        //TODO:check arr[0].arr[1],arr[2].arr[3] in nodes
        if (arr.length < 6) {//not correct format
            System.err.println("bad format when parsing a edge");
            return;
        }
        for (int i = 6; i < arr.length; i += 2) {
            b.add(info.createBend(Double.parseDouble(arr[i]), Double.parseDouble(arr[i + 1])));
        }
        info.addEdgeLayout(arr[0], arr[1], arr[2], arr[3], b);
    }

    /*
    public LayoutInfo getParsedInfo(){
        return info;
    }

    public boolean convert(ConversionMethod method){
        return method.convert(info);
    }
    */
}