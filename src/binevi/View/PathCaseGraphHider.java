package binevi.View;

import y.base.Edge;
import y.base.EdgeCursor;
import y.base.Node;
import y.util.GraphHider;
import y.view.Graph2D;

import java.util.ArrayList;
import java.util.HashSet;


public class PathCaseGraphHider extends GraphHider {

    Graph2D graph;

    public PathCaseGraphHider(Graph2D graph) {
        super(graph);
        artificialEdges = new HashSet<Edge>();
        realEdges = new ArrayList<Edge>();
        this.graph = graph;
    }

    private HashSet<Edge> artificialEdges;
    private ArrayList<Edge> realEdges;

    public void addArtificialEdge(Edge edge) {
        artificialEdges.add(edge);

    }

    public void addRealEdge(Edge edge) {
        realEdges.add(edge);
    }

    public void hideAllArtificialEdges() {
        for (Edge edge : artificialEdges) {
            this.hide(edge);
        }
    }

    public void unhideAllArtificialEdges() {
        for (Edge edge : artificialEdges) {
            this.unhide(edge);
        }
    }

    public void hideAllRealEdges() {
        for (Edge edge : realEdges) {
            this.hide(edge);
        }
    }

    public void unhideAllRealEdges() {
        for (Edge edge : realEdges) {
            this.unhide(edge);
        }
    }

    public void hideUnnecessaryArtificialEdges() {

        //EdgeList edgelist = graph.getEdgeList();
        //System.out.println(graph.nodeCount());
        //System.out.println(graph.edgeCount());

        for (Edge edge : artificialEdges) {
            Node source = edge.source();
            PathCaseShapeNodeRealizer nrsource = (PathCaseShapeNodeRealizer) graph.getRealizer(source);
            Node target = edge.target();
            PathCaseShapeNodeRealizer nrtarget = (PathCaseShapeNodeRealizer) graph.getRealizer(target);

            //if (nrsource.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS && nrtarget.getNodeRole() == PathCaseShapeNodeRealizer.PathCaseNodeRole.GENERICPROCESS) {
            //check molecular entities connecting the two processes
            EdgeCursor sourceneighborcursor = source.edges();
            EdgeCursor targetneighborcursor = target.edges();
            HashSet<Node> sourceneigbors = new HashSet<Node>();

            for (int i = 0; i < source.degree(); i++) {
                Edge neighboredge = sourceneighborcursor.edge();
                Node neighbor = neighboredge.opposite(source);
                PathCaseShapeNodeRealizer nrneighbor = (PathCaseShapeNodeRealizer) graph.getRealizer(neighbor);
                if (!PathCaseShapeNodeRealizer.rolesequalsubstrate(nrneighbor.getNodeRole(), nrsource.getNodeRole()))
                    sourceneigbors.add(neighbor);
                sourceneighborcursor.next();
            }
            boolean hascommonconnector = false;

            for (int i = 0; i < target.degree(); i++) {
                Edge neighboredge = targetneighborcursor.edge();
                Node neighbor = neighboredge.opposite(target);
                PathCaseShapeNodeRealizer nrneighbor = (PathCaseShapeNodeRealizer) graph.getRealizer(neighbor);
                if (sourceneigbors.contains(neighbor) && !PathCaseShapeNodeRealizer.rolesequalsubstrate(nrneighbor.getNodeRole(), nrtarget.getNodeRole())) {
                    //check whether edge between  neighbor,
                    hascommonconnector = true;
                    break;
                }
                targetneighborcursor.next();
            }

            if (hascommonconnector)
                hide(edge);
            //}

            //this.unhide(edge);
        }
    }

}
