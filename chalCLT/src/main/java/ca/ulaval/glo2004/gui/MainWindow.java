package ca.ulaval.glo2004.gui;

import Domain.MainController;
import View.ObjectPropertyEditorPanel;
import View.HeaderPanel;
import View.ViewSelectionPanel;

import javax.swing.*;
import java.awt.geom.AffineTransform;

public class MainWindow extends JFrame {

    //Panels
    public JPanel PanneauPrincipal;
    private JPanel PanelHierarchique;
    private JPanel PanelHeader;
    private JPanel PanelPropriete;
    private JPanel PanelChalet;
    //    private JTree HierarchieChalet;
    private JPanel PanelVues;
    private ViewSelectionPanel viewSelectionPanel;
    private PropertyPanel propertyPanel;
    private TreePanel treePanel;
    private DrawingPanel drawingPanel;
    private HeaderPanel headerPanel;
    private ObjectPropertyEditorPanel dynamicPropertyPanel;

    //Other Stuff
    private static MainWindow _instance;
    public MainController mainController;

    //Config
    private final int minWidth = 1920;
    private final int minHeight = 1000;

    private AffineTransform _affineTransform;
    public MainWindow() {

    }

    /*private MainWindow(){

// définir les bordures des panels
        PanelHierarchique.setBorder(BorderFactory.createLineBorder(Color.black));
        PanelHeader.setBorder(BorderFactory.createLineBorder(Color.black));
        PanelVues.setBorder(BorderFactory.createLineBorder(Color.black));
        PanelPropriete.setBorder(BorderFactory.createLineBorder(Color.black));
        //--------------------------------------------------------------------------
//        DefaultTreeModel arbreTemp = (DefaultTreeModel) HierarchieChalet.getModel();
//        //supprimer les noeuds de l'arbre de base
//        for(int i=0;i<arbreTemp.getChildCount(arbreTemp.getRoot());i++){
//            arbreTemp.removeNodeFromParent((MutableTreeNode) arbreTemp.getChild(arbreTemp.getRoot(),0));
//        }
//        //Mettre les valeurs voulus sur l'arbre
//        arbreTemp.setRoot(new DefaultMutableTreeNode("Chalet"));
//        arbreTemp.insertNodeInto((MutableTreeNode) new DefaultMutableTreeNode("Facade avant"), (MutableTreeNode) arbreTemp.getRoot(),0);
//        arbreTemp.insertNodeInto((MutableTreeNode) new DefaultMutableTreeNode("Facade arriere"), (MutableTreeNode) arbreTemp.getRoot(),1);
//        arbreTemp.insertNodeInto((MutableTreeNode) new DefaultMutableTreeNode("Mur gauche"), (MutableTreeNode) arbreTemp.getRoot(),2);
//        arbreTemp.insertNodeInto((MutableTreeNode) new DefaultMutableTreeNode("Facade droit"), (MutableTreeNode) arbreTemp.getRoot(),3);
//        arbreTemp.insertNodeInto((MutableTreeNode) new DefaultMutableTreeNode("Toit"), (MutableTreeNode) arbreTemp.getRoot(),4);

//        HierarchieChalet.setModel(arbreTemp);


    }*/


    public static MainWindow Init(MainController mainController, String appName, AffineTransform affineTransform) {
        if (_instance != null)
            throw new RuntimeException("Trying to initialize MainWindow when it already exist.");
        _instance = new MainWindow(mainController, appName,affineTransform);
        return _instance;
    }

    private MainWindow(MainController mainController, String appName,AffineTransform affineTransform) {
        this.mainController = mainController;
        _affineTransform = affineTransform;

        //Init self
        InitJFrame(appName);
        //Create All Other Panel Instances

    }

    private void InitJFrame(String appName) {
        setName(appName);
        setContentPane(this.PanneauPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(minWidth, minHeight));;
        pack();
        setVisible(true);
    }

    public MainController getController() {
        return this.mainController;
    }

    public void updateAll() {
        repaint();
        revalidate();
    }

    private void createUIComponents() {

        drawingPanel = new DrawingPanel(this,_affineTransform);

        headerPanel = new HeaderPanel(this,drawingPanel);
        viewSelectionPanel = new ViewSelectionPanel(this, drawingPanel);
        dynamicPropertyPanel = new ObjectPropertyEditorPanel(this.getController());
        treePanel = new TreePanel(this.getController());

        mainController.subscribeToSelectionEvents(dynamicPropertyPanel);
    }
}
