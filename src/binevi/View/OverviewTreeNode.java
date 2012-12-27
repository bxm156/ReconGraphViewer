package binevi.View;

import y.base.Node;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

public class OverviewTreeNode extends DefaultMutableTreeNode implements Comparable {
    public Node getNode() {
        return node;
    }

    private Node node;

    public String getId() {
        return id;
    }

    private String id;

    //protected boolean isSelected;
    public boolean isVisible;

    public OverviewTreeNode(Object o, String id, Node node) {
        super(o);
        this.id = id;
        this.node = node;
    }

    public void setVisible(boolean isVisible, boolean AUTONODECHECK) {
        this.isVisible = isVisible;

        if (AUTONODECHECK) {
            if (children != null) {
                Enumeration enume = children.elements();
                while (enume.hasMoreElements()) {
                    OverviewTreeNode node = (OverviewTreeNode) enume.nextElement();
                    node.setVisible(isVisible, AUTONODECHECK);
                }
            }
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public int compareTo(Object o) {
        if (o == null) return -1;
        OverviewTreeNode other = (OverviewTreeNode) o;
        String thisname = (String) this.getUserObject();
        String othername = (String) other.getUserObject();
        return thisname.compareTo(othername);
    }
}

