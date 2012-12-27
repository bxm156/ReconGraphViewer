package binevi.View;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashSet;

public class OrganismCheckPanel extends JPanel {

    JScrollPane sp;
    public OrganismCheckNode root;

    JTree tree;

    boolean AUTONODECHECK;
    public HashSet<String> selectedOrganismIdList;
    private boolean bCompartmentH=false;
    PathCaseViewer observer;
    PathCaseViewerMetabolomics observerCompartmentH;

    private boolean WARNUSEROFCHANGES = false;


    public OrganismCheckPanel(boolean autonodecheck, PathCaseViewer observer) {
        this.AUTONODECHECK = autonodecheck;
        setLayout(new BorderLayout());
        this.observer = observer;
    }

    public OrganismCheckPanel(boolean autonodecheck, PathCaseViewerMetabolomics observer) {
        this.AUTONODECHECK = autonodecheck;
        setLayout(new BorderLayout());
        this.observerCompartmentH = observer;
        this.bCompartmentH=true;
    }

    public boolean checkTreeFromParent() {
        return checkTreeFromParent(root);
    }

    public boolean checkTreeFromParent(OrganismCheckNode root) {
        boolean allselected = true;
        Enumeration enume = root.children();
        boolean haschildren = enume.hasMoreElements();
        while (enume.hasMoreElements()) {
            OrganismCheckNode node = (OrganismCheckNode) enume.nextElement();
            if (!checkTreeFromParent(node)) {
                allselected = false;
                //break;
            }
        }
        return root.isSelected = (haschildren && allselected) || (!haschildren && root.isSelected);

    }

    public void SetOrganismAction() {
        if (root == null)
            return;

        selectedOrganismIdList = collectselected(root);
        if(this.bCompartmentH)
            this.observerCompartmentH.organismSelected(selectedOrganismIdList);
        else
            this.observer.organismSelected(selectedOrganismIdList);
        //System.out.println("ORganism action");
    }

    public HashSet<String> collectselected() {
        return collectselected(root);
    }

    public HashSet<String> collectAll() {
        return collectAll(root);
    }

    public HashSet<String> collectAll(OrganismCheckNode node) {
        HashSet<String> selectedNodes = new HashSet<String>();

        String nameid = node.getNodeId();
        selectedNodes.add(nameid);

        for (int i = 0; i < node.getChildCount(); i++) {
            OrganismCheckNode child = (OrganismCheckNode) node.getChildAt(i);
            HashSet<String> fromchildren = collectAll(child);
            selectedNodes.addAll(fromchildren);
        }

        return selectedNodes;

    }

    public HashSet<String> collectselected(OrganismCheckNode node) {

        HashSet<String> selectedNodes = new HashSet<String>();

        String nameid = node.getNodeId();
        if (node.isSelected()) selectedNodes.add(nameid);

        for (int i = 0; i < node.getChildCount(); i++) {
            OrganismCheckNode child = (OrganismCheckNode) node.getChildAt(i);
            HashSet<String> fromchildren = collectselected(child);
            selectedNodes.addAll(fromchildren);
        }

        return selectedNodes;

    }

    public HashSet<String> collectselectedleaves(OrganismCheckNode node) {

        HashSet<String> selectedNodes = new HashSet<String>();

        String nameid = node.getNodeId();
        if (node.isSelected() && node.getChildCount() == 0) selectedNodes.add(nameid);

        for (int i = 0; i < node.getChildCount(); i++) {
            OrganismCheckNode child = (OrganismCheckNode) node.getChildAt(i);
            HashSet<String> fromchildren = collectselectedleaves(child);
            selectedNodes.addAll(fromchildren);
        }

        return selectedNodes;

    }

    public HashSet<String> collectselectedleaves() {
        if (root.isSelected()) {
            HashSet<String> selectedNodes = new HashSet<String>();
            selectedNodes.add(root.getNodeId());
            return selectedNodes;
        } else {
            return collectselectedleaves(root);
        }
    }

    public void selectGivenNodes(OrganismCheckNode treeRoot, HashSet<String> organismidlist) {
        if (treeRoot == null || organismidlist == null)
            return;

        String nodeid = treeRoot.getNodeId();

        if (organismidlist.contains(nodeid)) {
            if (nodeid.equals(root.getNodeId()))
                treeRoot.setSelected(true, true);
            else
                treeRoot.setSelected(true, AUTONODECHECK);
            if (tree != null)
                tree.expandPath(new TreePath(treeRoot.getPath()));
        } else
            for (int i = 0; i < treeRoot.getChildCount(); i++) {
                OrganismCheckNode child = (OrganismCheckNode) treeRoot.getChildAt(i);
                selectGivenNodes(child, organismidlist);
            }

    }

    public void selectGivenNodes(HashSet<String> organismlist) {
        selectGivenNodes(root, organismlist);
    }

    public void initwithRoot(OrganismCheckNode root) {

        this.root = root;

        if (root == null)
            return;

        if (sp != null)
            this.remove(sp);

        tree = new JTree(root);
        tree.setCellRenderer(new OrganismCheckRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.addMouseListener(new CheckNodeSelectionListener());

        sp = new JScrollPane(tree);

        add(sp, BorderLayout.CENTER);

        tree.revalidate();
        repaint();
    }

    public HashSet<String> getSelectedOrganismIdsOfLeaves() {
        return getSelectedOrganismIdsOfLeaves(root);
    }

    public HashSet<String> getSelectedOrganismIdsOfLeaves(OrganismCheckNode node) {
        HashSet<String> selectedNodes = new HashSet<String>();

        String nameid = node.getNodeId();
        if (node.isSelected() && node.getChildCount() == 0) selectedNodes.add(nameid);

        for (int i = 0; i < node.getChildCount(); i++) {
            OrganismCheckNode child = (OrganismCheckNode) node.getChildAt(i);
            HashSet<String> fromchildren = getSelectedOrganismIdsOfLeaves(child);
            selectedNodes.addAll(fromchildren);
        }

        return selectedNodes;
    }

    public void selectGivenNode(String setOrganism) {
        root.setSelected(false, true);
        //System.out.println(setOrganism);
        HashSet<String> organismlist = new HashSet<String>();
        if (setOrganism != null) {
            organismlist.add(setOrganism);
            selectGivenNodes(organismlist);
        } /*else {
            root.setSelected(false, true);
        }*/

        SetOrganismAction();


    }

    class CheckNodeSelectionListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int row = tree.getRowForLocation(x, y);
            TreePath path = tree.getPathForRow(row);
            //TreePath  path = tree.getSelectionPath();
            if (path != null) {

                if (WARNUSEROFCHANGES) {
                    int result = JOptionPane.showConfirmDialog(null, "Changing organism setting will modify the displayed graph.\nThe graph may no longer be consistent with your previous query result.\nDo you still want to continue?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (result != JOptionPane.YES_OPTION) {
                        return;
                    }

                    WARNUSEROFCHANGES = false;
                }

                OrganismCheckNode node = (OrganismCheckNode) path.getLastPathComponent();
                boolean isSelected = !(node.isSelected());

                if ((node.getNodeId()).equals(root.getNodeId()))
                    node.setSelected(isSelected, true);
                else
                    node.setSelected(isSelected, AUTONODECHECK);


                if (AUTONODECHECK)
                    checkTreeFromParent();
                /*if (isSelected) {
                    tree.expandPath(path);
                } else {
                    tree.collapsePath(path);
                }*/

                ((DefaultTreeModel) tree.getModel()).nodeChanged(node);
                // I need revalidate if node is root.  but why?
                //if (row == 0) {
                tree.revalidate();
                tree.repaint();

                SetOrganismAction();
                //}
            }
        }
    }


}
