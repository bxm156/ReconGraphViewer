package binevi.View;

import y.base.Node;

class NodeGroupofPathway {
    Node groupNode;
    LayoutInfo info;
    LayoutInfo originalInfo;//retrieved from database, converted to relative positions, not touched during layouting
    String pathway_name;

    public NodeGroupofPathway(Node n, LayoutInfo i, LayoutInfo copy, String pathway) {
        groupNode = n;
        info = i;
        originalInfo = copy;
        pathway_name = pathway;
    }
}