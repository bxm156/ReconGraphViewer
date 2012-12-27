package binevi.View;

import y.view.ShapeNodeRealizer;
import y.view.hierarchy.GroupNodeRealizer;


public class PathCaseGroupShapeNodeRealizer extends GroupNodeRealizer {
    public PathCaseNodeRole getNodeRole() {
        return nodeRole;
    }

    public void setNodeRole(PathCaseNodeRole nodeRole) {
        this.nodeRole = nodeRole;
    }

    public static enum PathCaseNodeMode {
        GRAYED_OUT, NORMAL, GENE_HIGLIGHT
    }

    public PathCaseGroupShapeNodeRealizer() {
        super();
        nodeMode = PathCaseNodeMode.NORMAL;
    }

    public PathCaseNodeMode getNodeMode() {
        return nodeMode;
    }

    public void setNodeMode(PathCaseNodeMode nodeMode) {
        this.nodeMode = nodeMode;
    }

    private PathCaseNodeMode nodeMode;

    public static enum PathCaseNodeRole {
        GENERICPROCESS, COLLAPSEDPATHWAY, SUBSTRATEORPRODUCT, REGULATOR, INHIBITOR, ACTIVATOR, COFACTOR, COFACTORIN, SUBSTRATEORPRODUCT_COMMON, TISSUEGROUP, COFACTOROUT, PATHWAY_BOX, TRANSPORT_PROCESS
    }

    public static boolean rolesequalsubstrate(PathCaseNodeRole r1, PathCaseNodeRole r2) {
        return r1 == r2 || (r1 == PathCaseNodeRole.SUBSTRATEORPRODUCT && r2 == PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON) || (r1 == PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON && r2 == PathCaseNodeRole.SUBSTRATEORPRODUCT);
    }

    public static boolean ismetabolite(PathCaseNodeRole r) {
        return r == PathCaseNodeRole.SUBSTRATEORPRODUCT || r == PathCaseNodeRole.REGULATOR || r == PathCaseNodeRole.INHIBITOR || r == PathCaseNodeRole.ACTIVATOR || r == PathCaseNodeRole.COFACTOR || r == PathCaseNodeRole.COFACTORIN || r == PathCaseNodeRole.COFACTOROUT || r == PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON;
    }

    public static boolean isuncommonmetabolite(PathCaseNodeRole r) {
        return r != PathCaseNodeRole.SUBSTRATEORPRODUCT_COMMON && r != PathCaseNodeRole.GENERICPROCESS && r != PathCaseNodeRole.COLLAPSEDPATHWAY;
    }


    private PathCaseNodeRole nodeRole;


}