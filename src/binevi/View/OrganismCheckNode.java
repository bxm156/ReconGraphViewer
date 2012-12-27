package binevi.View;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

public class OrganismCheckNode extends DefaultMutableTreeNode {

    protected boolean isSelected;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    private String nodeId;

    public OrganismCheckNode(Object userObject, String nodeId) {
        this(userObject, true, false, nodeId);
    }

    public OrganismCheckNode(Object userObject, boolean allowsChildren, boolean isSelected, String nodeId) {
        super(userObject, allowsChildren);
        this.isSelected = isSelected;
        this.nodeId = nodeId;
    }

    public void setSelected(boolean isSelected, boolean AUTONODECHECK) {
        this.isSelected = isSelected;

        if (AUTONODECHECK) {
            if (children != null) {
                Enumeration enume = children.elements();
                while (enume.hasMoreElements()) {
                    OrganismCheckNode node = (OrganismCheckNode) enume.nextElement();
                    node.setSelected(isSelected, AUTONODECHECK);
                }
            }
        }
    }


    public boolean isSelected() {
        return isSelected;
    }

}



