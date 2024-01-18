package ca.ulaval.glo2004.gui;

import Domain.Interface.IInspectable;
import Domain.MainController;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class TreePanel extends JPanel {
    private final JTree chalTree;
    private final MainController _controller;
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Hierarchy");

    public TreePanel(MainController controller) {
        _controller = controller;
        chalTree = new JTree(root);
        chalTree.setCellRenderer(new CustomTreeCellRenderer());
        onPropertyChange();
        add(chalTree);
        _controller.subscribeToPropertyChangeEvents((ctx)->onPropertyChange());
        setTreeListener();
    }

    private void setTreeListener() {
        chalTree.addTreeSelectionListener(e ->  {
            TreePath selectedPath = e.getNewLeadSelectionPath();
            if (selectedPath != null) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                Object userObject = selectedNode.getUserObject();

                if (userObject instanceof IInspectable) {
                    IInspectable inspectable = (IInspectable) userObject;
                    nodeSelectedValueChanged(inspectable);
                    System.out.println("Selected IInspectable: " + inspectable.getName());
                } else {
                    System.out.println("Selected object is not an IInspectable: " + userObject);
                }
            }
        });
    }

    public void onPropertyChange() {
        root.removeAllChildren();
        ArrayList<IInspectable> elements = _controller.getIInspectable();
        for (IInspectable e : elements) {
            DefaultMutableTreeNode temp = new DefaultMutableTreeNode(e);
            root.add(temp);
            createNode(e, temp);
        }

        DefaultTreeModel treeModel = (DefaultTreeModel) chalTree.getModel();
        treeModel.reload(); // Refresh the tree model

        // Expand all nodes
        expandAll(chalTree, new TreePath(root));
    }

    private void expandAll(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path);
            }
        }
        tree.expandPath(parent);
    }
 
    private void createNode(IInspectable inspectable, DefaultMutableTreeNode node) {
        System.out.println("Processing: " + inspectable.getName());
        for (IInspectable c : inspectable.getIChildren()) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(c);
            node.add(childNode);
            createNode(c, childNode); // Recursively call createNode
        }
    }

    private static class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();
                if (userObject instanceof IInspectable) {
                    IInspectable inspectable = (IInspectable) userObject;
                    setText(inspectable.getName());
                }
            }

            return this;
            //all
        }
    }

    public void nodeSelectedValueChanged(IInspectable i){
        _controller.notifySelection(i);
    }

}