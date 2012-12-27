/*
 * @(#)LayoutInfo.java
 * Copyright 2008 PathCase Group All rights reserved.
 */
package binevi.View;

import java.io.StringWriter;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * A data class to store layout information.
 * Positions for nodes are their upper left coors of the bounding box, to work with setLocation's parameters
 * The width and height for process nodes (60,20); for substrates/products (20,20); for cofactors(60,12)
 * <p/>
 *
 * @author Yuan Wang
 */
public class LayoutInfo{

    class NodeLayout {
        String nodeID;
        String processID;
        String cofactor;

        //should be the top left position of the bounding box.
        double x;
        double y;

        NodeLayout(String pid, String id, boolean c, double nx, double ny) {
            nodeID = id;
            processID = pid;
            if(c)cofactor = "true";
            else cofactor = "false";
            x = nx;
            y = ny;
        }

        NodeLayout(String pid, String id, String c, double nx, double ny) {
            nodeID = id;
            processID = pid;
            cofactor = c;
            x = nx;
            y = ny;
        }
        public LayoutPoint getNodePoint() {
            return new LayoutPoint(x, y);
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(processID + ",");
            sb.append(nodeID + ",");
            if (cofactor.equalsIgnoreCase("true")) {
                sb.append("cofactor");
            }
            sb.append("," + x + "," + y + "\n");
            return sb.toString();
        }

        public String toBeautifiedString() {
            StringBuffer sb = new StringBuffer();
            sb.append("PID: " + processID);
            sb.append("  Node id: " + nodeID);
            if (cofactor.equalsIgnoreCase("true")) {
                sb.append("\n is Cofactor. ");
            }
            sb.append("\n");
            sb.append("           x: " + x + " y: " + y + "\n\n");
            return sb.toString();
        }

    }

    class EdgeLayout {
        String sourcepid;
        String sourceNode;
//        boolean scofactor;
        String scofactor;
        String targetpid;
        String targetNode;
//        boolean tcofactor;
        String tcofactor;
        ArrayList<LayoutPoint> bends = new ArrayList<LayoutPoint>();

        EdgeLayout(String spid, String sid, String tpid, String tid) {
            sourcepid = spid;
            sourceNode = sid;
            targetpid = tpid;
            targetNode = tid;
        }

        EdgeLayout(String spid, String sid, String sc, String tpid, String tid, String tc) {
            sourcepid = spid;
            sourceNode = sid;
            targetpid = tpid;
            targetNode = tid;
            scofactor = sc;
            tcofactor = tc;
        }

        void setBends(ArrayList<LayoutPoint> b) {
            bends = b;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(sourcepid + "," + sourceNode + "," + targetpid + "," + targetNode + ",");
            Iterator<LayoutPoint> e = bends.iterator();
            LayoutPoint b;
            while (e.hasNext()) {
                b = (LayoutPoint) e.next();
                sb.append(b.x + "," + b.y + ",");
            }
            sb.append("\n");
            return sb.toString();
        }

        public String toBeautifiedString() {
            StringBuffer sb = new StringBuffer();
            sb.append("sourcePID: " + sourcepid);
            sb.append("  sourceNodeId: " + sourceNode + "\n");
            sb.append("  targetPID: " + targetpid);
            sb.append("  targetNodeId: " + targetNode + "\n");
            Iterator<LayoutPoint> e = bends.iterator();
            LayoutPoint b;
            while (e.hasNext()) {
                b = (LayoutPoint) e.next();
                sb.append(" Bend Points: " + b.x + "," + b.y + "  ");
            }
            sb.append("\n\n");
            return sb.toString();
        }
    }

    public ArrayList<NodeLayout> nodes = new ArrayList<NodeLayout>();
    public ArrayList<EdgeLayout> edges = new ArrayList<EdgeLayout>();
    public HashMap<String, Object> idToNode = new HashMap<String, Object>();  //id is pid combine with id

    public boolean isEmpty() {
        return nodes.isEmpty(); //&& edges.isEmpty() && idToNode.isEmpty();
    }

    public boolean addNodeLayout(String pid, String id, boolean c, double x, double y) {
        /*if(idToNode.containsKey(pid + id)){
            return false;
        } */

        NodeLayout newNode = new NodeLayout(pid, id, c, x, y);

        nodes.add(newNode);
        idToNode.put(pid + id, newNode);
        return true;
    }

    public boolean addNodeLayout(String pid, String id, String c, double x, double y) {
        /*if(idToNode.containsKey(pid + id)){
            return false;
        } */

        NodeLayout newNode = new NodeLayout(pid, id, c, x, y);

        nodes.add(newNode);
        idToNode.put(pid + id, newNode);
        return true;
    }
    public void addEdgeLayout(String sp, String s, String tp, String t, ArrayList<LayoutPoint> b) {
        EdgeLayout el = new EdgeLayout(sp, s, tp, t);
        el.setBends(b);
        edges.add(el);
    }


    public void addEdgeLayout(String sp, String s, String sc, String tp, String t, String tc, ArrayList<LayoutPoint> b) {
        EdgeLayout el = new EdgeLayout(sp, s, sc, tp, t, tc);
        el.setBends(b);
        edges.add(el);
    }

    //TODO: no need for this?
    public LayoutPoint createBend(double x, double y) {
        return new LayoutPoint(x, y);
    }

    //TODO: no need for this?
    /*expand the exact bounding box for extra margin
    public LayoutBox getExpandedLayoutBox(){
        LayoutBox box = getExactLayoutBox();

        box.topleft.x= box.topleft.x-120;
        box.topleft.y= box.topleft.y-40;
        box.width=box.width+240;
        box.height=box.height+80;

        return box;
    }
    //*/

    /**
     * @return exact rectangle box bounding all the center points of the nodes and all the bend points.
     */
    public LayoutBox getExactLayoutBox() {

        Iterator<NodeLayout> e = nodes.iterator();
        double tlx = 0, tly = 0, brx = 0, bry = 0;//top left, bottom right
        NodeLayout nl;

        if (e.hasNext()) {
            nl = e.next();
            tlx = nl.x;
            tly = nl.y;
            brx = nl.x;
            bry = nl.y;
        }

        while (e.hasNext()) {
            nl = e.next();
            if (nl.x < tlx) {
                tlx = nl.x;
            }
            if (nl.x > brx) {
                brx = nl.x;
            }
            if (nl.y < tly) {
                tly = nl.y;
            }
            if (nl.y > bry) {
                bry = nl.y;
            }
        }

        Iterator<EdgeLayout> ee = edges.iterator();
        Iterator<LayoutPoint> be;
        LayoutPoint lp;
        EdgeLayout el;
        while(ee.hasNext()){
            el=ee.next();
            be=el.bends.iterator();
            while(be.hasNext()){
                lp=be.next();
                if (lp.x < tlx) {
                    tlx = lp.x;
                }
                if (lp.x > brx) {
                    brx = lp.x;
                }
                if (lp.y < tly) {
                    tly = lp.y;
                }
                if (lp.y > bry) {
                    bry = lp.y;
                }
            }
        }

        return new LayoutBox(tlx, tly, brx - tlx, bry - tly);
    }

    /**
     * to check if the two layouts' exact box overlapping
     * @see binevi.View.LayoutInfo#overlapLayoutBox(LayoutBox)
     */
    public boolean overlapLayoutBox(LayoutInfo i){
        LayoutBox b2 = i.getExactLayoutBox();
        return overlapLayoutBox(b2);
    }

    /**
     * to check if the two layouts' exact box overlapping
     * if overlapping, then there must be at least one of the vertices of box1 is inside box2 OR the other way around.
     * @see binevi.View.LayoutInfo#overlapLayoutBox(LayoutInfo)
     */
    public boolean overlapLayoutBox(LayoutBox b2){
        LayoutBox b = getExactLayoutBox();
        return b.overlapLayoutBox(b2);
    }
                                      
    //convert coordinates to relative coordinates with the top-left one as (0,0)
    public void convertToRelativePositions() {
        LayoutBox box = getExactLayoutBox();
        //System.out.println("Box     ("+box.topleft.x+","+box.topleft.y+");("+(box.topleft.x+box.width)+","+(box.topleft.y+box.height));
        Iterator<NodeLayout> e = nodes.iterator();
        NodeLayout nl;
        while (e.hasNext()) {
            nl = e.next();
            /* Debug code
            if(nl.x<box.topleft.x || nl.y<box.topleft.y || nl.x>(box.topleft.x+box.width) || nl.y>(box.topleft.y+box.height)){

                System.out.println("Invalid node     "+nl.x+","+nl.y);
            }
            //*/
            nl.x = nl.x - box.topleft.x;
            nl.y = nl.y - box.topleft.y;
        }

        Iterator<EdgeLayout> e2 = edges.iterator();
        EdgeLayout el;
        while (e2.hasNext()) {
            el = e2.next();
            for (LayoutPoint lp : el.bends) {
                lp.x = lp.x - box.topleft.x;
                lp.y = lp.y - box.topleft.y;
            }
        }
    }

    //convert coordinates to shifted coordinates with the top-left one as (xs,ys)
    public void convertToShiftedPositions(double xs,double ys) {
        convertToRelativePositions();
        Iterator<NodeLayout> e = nodes.iterator();
        NodeLayout nl;
        while (e.hasNext()) {
            nl = e.next();
            nl.x += xs;
            nl.y += ys;
        }

        Iterator<EdgeLayout> e2 = edges.iterator();
        EdgeLayout el;
        while (e2.hasNext()) {
            el = e2.next();
            for (LayoutPoint lp : el.bends) {
                lp.x += xs;
                lp.y += ys;
            }
        }
    }


    public EdgeLayout getEdgeLayouts(String spid, String sid, String tpid, String tid) {
        Iterator<EdgeLayout> ee = edges.iterator();
        EdgeLayout el;
        while (ee.hasNext()) {
            el = ee.next();
            if (el.sourceNode.equals(sid) && el.targetNode.equals(tid)) {
                return el;
            }
        }
        return null;
    } //*/

    //Return the shared nodes(with the same node id) with the other LayoutInfo
    public LinkedHashMap<NodeLayout, NodeLayout> sharedNodes(LayoutInfo info2) {
        LinkedHashMap<NodeLayout, NodeLayout> shared = new LinkedHashMap<NodeLayout, NodeLayout>();
        Iterator<NodeLayout> e = nodes.iterator();
        while (e.hasNext()) {
            NodeLayout nl = e.next();
            if(nl.cofactor.equalsIgnoreCase("true")){
                continue;
            }
            NodeLayout nl2;
            Iterator<NodeLayout> e2 = info2.nodes.iterator();
            while (e2.hasNext()) {
                nl2 = e2.next();
                if(nl2.cofactor.equalsIgnoreCase("true")){
                    continue;
                }
                if (nl.nodeID.equals(nl2.nodeID)) {
                    shared.put(nl, nl2);
                }
            }
        }
        return shared;
    }

    public void filpLayoutLeftRight(){
        LayoutBox box = getExactLayoutBox();
        Iterator<NodeLayout> ne = nodes.iterator();
        while(ne.hasNext()){
            NodeLayout nl = ne.next();
            if(nl.processID.equals("null")){
                nl.x = box.topleft.x + (box.width - (nl.x - box.topleft.x)) - 60;//minus 60 which is the width of process node's box, since using upperleft coor
            }else if(nl.cofactor.equalsIgnoreCase("true")){
                nl.x = box.topleft.x + (box.width - (nl.x - box.topleft.x)) - 60;
            }else{
                nl.x = box.topleft.x + (box.width - (nl.x - box.topleft.x)) - 20;
            }
        }

        Iterator<EdgeLayout> e2 = edges.iterator();
        EdgeLayout el;
        while (e2.hasNext()) {
            el = e2.next();
            for (LayoutPoint lp : el.bends) {
                lp.x = box.topleft.x + (box.width - (lp.x - box.topleft.x));
            }
        }
    }

    public void filpLayoutUpDown(){
        LayoutBox box = getExactLayoutBox();
        Iterator<NodeLayout> ne = nodes.iterator();
        while(ne.hasNext()){
            NodeLayout nl = ne.next();
            if(nl.cofactor.equalsIgnoreCase("true")){
                nl.y = box.topleft.y + (box.height - (nl.y - box.topleft.y)) - 12;
            }else{
                nl.y = box.topleft.y + (box.height - (nl.y - box.topleft.y)) - 20;
            }
        }

        Iterator<EdgeLayout> e2 = edges.iterator();
        EdgeLayout el;
        while (e2.hasNext()) {
            el = e2.next();
            for (LayoutPoint lp : el.bends) {
                lp.y = box.topleft.y + (box.height - (lp.y - box.topleft.y));
            }
        }
    }


 /**
     * return a list of queues for connected groups of pathways
     * place pathways according to the order of the queues and the list
     *
     * @param layouts
     * @param dependOn the index number of pathway this one depends on, -1 if no dependency
     * @return
     */
    public static ArrayList<Queue> pathwayQueues(LayoutInfo[] layouts, int[] dependOn) {
        ArrayList<Queue> queues = new ArrayList<Queue>();//each queue is for a disconnected group of pathways
        int n = layouts.length;
        boolean[] added = new boolean[n];//whether the pathway is added into a queue
        //boolean[] processed = new boolean[n];//whether all neighbours of this pathway are added into some queue
        Queue<Integer> toProcess = new LinkedList<Integer>();

        //current queue to add pathways into
        //if not null, then exists a queue to add pathways into
        //otherwise need to create a new queue(for disconnected group of pathways)
        Queue currentQueue = null;

        Arrays.sort(layouts, new LayoutBoxSizeComparator());

        /*TODO delete
        LayoutInfo tmp0 = layouts[0];
        LayoutInfo tmp1 = layouts[1];
        LayoutInfo tmp2 = layouts[2];
        LayoutInfo tmp3 = layouts[3];
        LayoutInfo tmp6 = layouts[6];
        layouts[3]=tmp1;
        layouts[2]=tmp6;
        layouts[1]=tmp2;
        layouts[6]=tmp0;
        layouts[0] = tmp3;
        /*
        for(LayoutInfo i:layouts){
            System.out.println(i.getExactLayoutBox().size());
        }
        //*/

        for (int i = n - 1; i >= 0; i--) {
            if (added[i]) {
                continue;
            }

            currentQueue = new LinkedList<Integer>();//current queue to add connected pathways into
            queues.add(currentQueue);
            toProcess.add(i);
            currentQueue.add(i);
            added[i] = true;
            dependOn[i] = -1;

            while (toProcess.size() != 0) {
                int p = toProcess.remove();
                for (int j = n - 2; j >= 0; j--) {//the first one is already added, so search from n-2
                    //find all non-visited neighbour pathways of layout[p]
                    if (added[j]) {
                        continue;
                    }
                    if (layouts[p].sharedNodes(layouts[j]).size() > 0 && !added[j]) {
                        currentQueue.add(j);
                        toProcess.add(j);
                        added[j] = true;
                        dependOn[j] = p;
                    }
                }
                //processed[p]=true;
            }
            currentQueue = null;//finished a connected group of pathways
        }

        return queues;
    }

    public void flipLayoutLeftRight() {
        LayoutBox box = getExactLayoutBox();
        Iterator<NodeLayout> ne = nodes.iterator();
        while (ne.hasNext()) {
            NodeLayout nl = ne.next();
            if (nl.processID.equals("null")) {
                nl.x = box.topleft.x + (box.width - (nl.x - box.topleft.x)) - 60;//minus 60 which is the width of process node's box, since using upperleft coor
            } else if (isSecondary(nl)) {
                nl.x = box.topleft.x + (box.width - (nl.x - box.topleft.x)) - 60;
            } else {
                nl.x = box.topleft.x + (box.width - (nl.x - box.topleft.x)) - 20;
            }
        }

        Iterator<EdgeLayout> e2 = edges.iterator();
        EdgeLayout el;
        while (e2.hasNext()) {
            el = e2.next();
            for (LayoutPoint lp : el.bends) {
                lp.x = box.topleft.x + (box.width - (lp.x - box.topleft.x));
            }
        }
    }

    public void flipLayoutUpDown() {
        LayoutBox box = getExactLayoutBox();
        Iterator<NodeLayout> ne = nodes.iterator();
        while (ne.hasNext()) {
            NodeLayout nl = ne.next();
            if (isSecondary(nl)) {
                nl.y = box.topleft.y + (box.height - (nl.y - box.topleft.y)) - 12;
            }else {
                nl.y = box.topleft.y + (box.height - (nl.y - box.topleft.y)) - 20;
            }
        }

        Iterator<EdgeLayout> e2 = edges.iterator();
        EdgeLayout el;
        while (e2.hasNext()) {
            el = e2.next();
            for (LayoutPoint lp : el.bends) {
                lp.y = box.topleft.y + (box.height - (lp.y - box.topleft.y));
            }
        }
    }

    //empty this info object
    public void clearInfo() {
        nodes.clear();
        edges.clear();
        idToNode.clear();
    }

    public LayoutInfo copyOfThisLayoutInfo() {
        LayoutInfo copyI = new LayoutInfo();
        for (NodeLayout nl : nodes) {
            copyI.addNodeLayout(nl.processID, nl.nodeID, nl.cofactor, nl.x, nl.y);
        }
        for (EdgeLayout el : edges) {
            ArrayList<LayoutPoint> copyBends = new ArrayList<LayoutPoint>();
            for (LayoutPoint lp : el.bends) {
                copyBends.add(new LayoutPoint(lp.x, lp.y));
            }
            copyI.addEdgeLayout(el.sourcepid, el.sourceNode, el.scofactor, el.targetpid, el.targetNode, el.tcofactor, copyBends);
        }
        return copyI;
    }                    

    public String toString() {
        if (isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Iterator<NodeLayout> e = nodes.iterator();
        while (e.hasNext())
            sb.append((e.next()).toString());
        sb.append("\n");
        Iterator<EdgeLayout> ee = edges.iterator();
        while (ee.hasNext())
            sb.append((ee.next()).toString());
        return sb.toString();
    }

    public String toBeautifiedString() {
        StringBuffer sb = new StringBuffer();

        Iterator<NodeLayout> e = nodes.iterator();
        while (e.hasNext())
            sb.append((e.next()).toBeautifiedString());
        sb.append("\n");
        Iterator<EdgeLayout> ee = edges.iterator();
        while (ee.hasNext()) {
            sb.append((ee.next()).toBeautifiedString());
        }

        sb.append("\n\nNumber of Nodes: ");
        sb.append(nodes.size());
        sb.append("\nNumber of Edges: ");
        sb.append(edges.size() + "\n");

        return sb.toString();
    }
    public String toXMLString(){
        Document doc;
        try {//Create an Empty Dom
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.newDocument();
        }catch (ParserConfigurationException e) {
            System.err.println("error when create empty Dom: "+e);
            return null;
        }

        if(doc==null){
            System.err.println("null dom");
            return null;
        }

        try{
            Element root = doc.createElement("PathwayLayout");
            doc.appendChild(root);

            Element nodeslayout = doc.createElement("Nodes");
            root.appendChild(nodeslayout);
            Element edgeslayout = doc.createElement("Edges");
            root.appendChild(edgeslayout);

            for(NodeLayout nl:nodes){
                Element ne = doc.createElement("NodeLayout");
                ne.setAttribute("ID", nl.nodeID);
                ne.setAttribute("NeighboringProcessId", nl.processID);
                String co="false";
                if(nl.cofactor.equalsIgnoreCase("true")){
                    co="true";
                }
                ne.setAttribute("cofactor",co);
                ne.setAttribute("X",Double.toString(nl.x));
                ne.setAttribute("Y",Double.toString(nl.y));
                nodeslayout.appendChild(ne);
            }

            for(EdgeLayout el:edges){
                Element ee = doc.createElement("EdgeLayout");
                ee.setAttribute("SourceID", el.sourceNode);
                ee.setAttribute("SourceNeighboringProcessId", el.sourcepid);
                String sco="false";
                if(el.scofactor.equalsIgnoreCase("true")){
                    sco="true";
                }
                ee.setAttribute("SourceCofactor",sco);

                ee.setAttribute("TargetID",el.targetNode);
                ee.setAttribute("TargetNeighboringProcessId",el.targetpid);
                String tco="false";
                if(el.tcofactor.equalsIgnoreCase("true")){
                    sco="true";
                }
                ee.setAttribute("TargetCofactor",tco);

                for(LayoutPoint lp:el.bends){
                    Element lpe = doc.createElement("BendPoint");
                    lpe.setAttribute("X", Double.toString(lp.x));
                    lpe.setAttribute("Y", Double.toString(lp.y));
                    ee.appendChild(lpe);
                }

                edgeslayout.appendChild(ee);
            }

            //Transform Dom to XML String
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            //additional whitespace when outputting the result tree
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);

            String xmlString = result.getWriter().toString();
            return xmlString;
        }catch (Exception e) {
            System.err.println("error when fill the Dom: "+e);
            return null;
        }
    }

        //check if is a secondary node(cofactor,regulator,activator,inhibitor)
    public boolean isSecondary(NodeLayout nl){
        if(nl.processID.equals("null")){
            return false;
        }
        String c = nl.cofactor;
        c = c.toLowerCase();
        if(c.equals("a") || c.equals("c") || c.equals("i") || c.equals("r") || c.equals("true")){
            return true;
        }else{
            return false;
        }
    }

    public static void main(String[] args){
        LayoutInfo info = new LayoutInfo();
        System.out.println(info.toXMLString());
    }

}


class LayoutBoxSizeComparator implements Comparator<LayoutInfo>{
    public int compare(LayoutInfo i1,LayoutInfo i2){
        LayoutBox box1 = i1.getExactLayoutBox();
        LayoutBox box2 = i2.getExactLayoutBox();
        return (box1.size()<box2.size() ? -1 : (box1.size()==box2.size()? 0:1));
    }
}

class RelativePos{
    int p = -1;
    void setPos(String position){
        if (position.equals("right")) {
            p=0;
        } else if (position.equals("left")) {
            p=1;
        } else if (position.equals("top")) {
            p=2;
        } else if (position.equals("below")) {
            p=3;
        } else {
            p=-1;
            System.err.println("Wrong relative position string between the 2 pathways when calling getDistanceMoved");
        }
    }

    String getPos(){
        switch(p){
            case 0:return "right";
            case 1:return "left";
            case 2:return "top";
            case 3:return "below";
            default: return null;
        }
    }
}

class Flipping {
    // {"no","leftright","topdown"}
    private String flip = "no";

    public void set(String f) {
        flip = f;
    }

    public String get() {
        return flip;
    }
}

class NodeLayoutRefPair {
    LayoutInfo.NodeLayout nl1;
    LayoutInfo.NodeLayout nl2;

    public NodeLayoutRefPair(LayoutInfo.NodeLayout n1, LayoutInfo.NodeLayout n2) {
        nl1 = n1;
        nl2 = n2;
    }
}