package binevi.View;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;


public class OverviewTreeNodeRenderer extends JPanel implements TreeCellRenderer {
    //protected JCheckBox select;
    protected JCheckBox visible;
    protected TreeLabel label;

    public OverviewTreeNodeRenderer() {
        setLayout(null);
        //add(select = new JCheckBox());
        add(visible = new JCheckBox());
        //Font font = new Font ("Arial",Font.BOLD,9);
        //select.setPreferredSize(new Dimension(12,12));
        //select.setFont(font);
        visible.setPreferredSize(new Dimension(15, 15));
        //visible.setFont(font);

        add(label = new TreeLabel());
        //select.setBackground(UIManager.getColor("Tree.textBackground"));
        visible.setBackground(UIManager.getColor("Tree.textBackground"));
        label.setForeground(UIManager.getColor("Tree.textForeground"));
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, hasFocus);
        setEnabled(tree.isEnabled());
        //select.setSelected(((OverviewTreeNode) value).isSelected());
        visible.setSelected(((OverviewTreeNode) value).isVisible());
        label.setFont(tree.getFont());
        label.setText(stringValue);
        label.setSelected(isSelected);
        label.setFocus(hasFocus);
        if (leaf) {
            //label.setIcon(UIManager.getIcon("Tree.leafIcon"));
            label.setFont(new Font("Arial", Font.ITALIC, 10));
            label.setForeground(Color.BLACK);
        } else if (expanded) {
            //label.setIcon(UIManager.getIcon("Tree.openIcon"));
            label.setFont(new Font("Arial", Font.BOLD, 11));
            label.setForeground(Color.BLUE);
        } else {
            //label.setIcon(UIManager.getIcon("Tree.closedIcon"));
            label.setFont(new Font("Arial", Font.BOLD, 12));
            label.setForeground(Color.RED);
        }
        return this;
    }

    public Dimension getPreferredSize() {
        //Dimension d_check = select.getPreferredSize();
        Dimension d_visible = visible.getPreferredSize();
        Dimension d_label = label.getPreferredSize();

        return new Dimension(d_visible.width + d_label.width, Math.max(d_visible.height, d_label.height));
    }

    /*private int max(int a, int b, int c) {
        if (a>=b && a>=c) return a;
        else if (b>=a && b>=c) return b;
        else if (c>=a && c>=b) return c;
        else return -1;
    }*/

    public void doLayout() {
        //Dimension d_check = select.getPreferredSize();
        Dimension d_visible = visible.getPreferredSize();
        Dimension d_label = label.getPreferredSize();

        int maxheight = Math.max(d_visible.height, d_label.height);
        //int minheight = min(d_check.height , d_visible.height , d_label.height);

        //int y_check = (maxheight - d_check.height) / 2;
        int y_visible = (maxheight - d_visible.height) / 2;
        int y_label = (maxheight - d_label.height) / 2;

        //select.setLocation(0, y_check);
        //select.setBounds(0, y_check, d_check.width, d_check.height);

        visible.setLocation(0, y_visible);
        visible.setBounds(0, y_visible, d_visible.width, d_visible.height);

        label.setLocation(d_visible.width + 3, y_label);
        label.setBounds(d_visible.width + 3, y_label, d_label.width, d_label.height);
    }


    public void setBackground(Color color) {
        if (color instanceof ColorUIResource) color = null;
        super.setBackground(color);
    }

    public class TreeLabel extends JLabel {
        boolean isSelected;
        boolean hasFocus;

        public TreeLabel() {
        }

        public void setBackground(Color color) {
            if (color instanceof ColorUIResource)
                color = null;
            super.setBackground(color);
        }

        public void paint(Graphics g) {
            String str;
            if ((str = getText()) != null) {
                if (0 < str.length()) {
                    if (isSelected) {
                        g.setColor(UIManager.getColor("Tree.selectionBackground"));
                    } else {
                        g.setColor(UIManager.getColor("Tree.textBackground"));
                    }
                    Dimension d = getPreferredSize();
                    int imageOffset = 0;
                    Icon currentI = getIcon();
                    if (currentI != null) {
                        imageOffset = currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
                    }
                    g.fillRect(imageOffset, 0, d.width - 1 - imageOffset, d.height);
                    if (hasFocus) {
                        g.setColor(UIManager.getColor("Tree.selectionBorderColor"));
                        g.drawRect(imageOffset, 0, d.width - 1 - imageOffset, d.height - 1);
                    }
                }
            }
            super.paint(g);
        }

        public Dimension getPreferredSize() {
            Dimension retDimension = super.getPreferredSize();
            if (retDimension != null) {
                retDimension = new Dimension(retDimension.width + 3,
                        retDimension.height);
            }
            return retDimension;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }


        public void setFocus(boolean hasFocus) {
            this.hasFocus = hasFocus;
        }
    }
}
