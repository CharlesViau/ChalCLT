package ca.ulaval.glo2004.gui;

import Domain.MainController;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PropertyPanel extends JPanel {
    Label LabelHeight = new Label("Hauteur: ");
    JSpinner height = new JSpinner();
    Label Labelthickness = new Label("Épaisseur: ");
    JSpinner thickness = new JSpinner();
    Label LabelLength = new Label("Longueur: ");
    JSpinner length = new JSpinner();
    Label LabelAngle = new Label("Angle du toit: ");
    JSpinner angle = new JSpinner();
    Label Labelroof = new Label("Direction du toit: ");
    JSpinner roof_direction = new JSpinner();
    Label LabelDistance = new Label("Distance de rainure: ");
    JSpinner slot_distance = new JSpinner();
    Label LabelAccess = new Label("Distance accessoire: ");
    JSpinner accessoryDist = new JSpinner();
    Label LabelGrid = new Label("Distance grille: ");
    JSpinner gridDistance = new JSpinner();
    JButton addWindow = new JButton();
    JButton addDoor = new JButton();
    JButton toggleGrid = new JButton();

    private MainController controller;

    public PropertyPanel(MainController controller){
        this.controller = controller;
        init();
    }

    public void init(){



        height.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

            }
        });
        thickness.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

            }
        });
        length.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

            }
        });
        angle.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

            }
        });
        roof_direction.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                //METHODE
            }
        });
        slot_distance.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

            }
        });
        accessoryDist.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

            }
        });
        gridDistance.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

            }
        });
        addWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }

        });
        addDoor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        toggleGrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        //Box layout =  Box.createHorizontalBox();
        JPanel panelHeight = new JPanel();
        panelHeight.add(LabelHeight);
        panelHeight.add(height);
        add(panelHeight);

        JPanel panellength = new JPanel();
        panelHeight.add(LabelLength);
        panelHeight.add(length);
        add(panellength);

        JPanel panelthickness = new JPanel();
        panelHeight.add(Labelthickness);
        panelHeight.add(thickness);
       // add(panelthickness);


        JPanel panelAngle = new JPanel();
        panelHeight.add(LabelAngle);
        panelHeight.add(angle);
        //add(panelAngle);

        JPanel panelroof = new JPanel();
        panelHeight.add(Labelroof);
        panelHeight.add(roof_direction);
        //add(panelroof);


        JPanel panelSlot = new JPanel();
        panelHeight.add(LabelDistance);
        panelHeight.add(slot_distance);
        //add(panelSlot);

        JPanel panelAccessory = new JPanel();
        panelHeight.add(LabelAccess);
        panelHeight.add(accessoryDist);
        //add(panelAccessory);

        JPanel panelgrid = new JPanel();
        panelHeight.add(LabelGrid);
        panelHeight.add(gridDistance);
        //add(panelgrid);
/*      layout.add(panelHeight);
        layout.add(panellength);
        layout.add(panelthickness);
        layout.add(panelAngle);
        layout.add(panelroof);
        layout.add(panelSlot);
        layout.add(panelAccessory);
        layout.add(panelgrid);

        add(layout);*/
        //add(panelHeight,gbc);
        //add(panelAngle,gbc);
        add(addWindow);
        add(addDoor);
        add(toggleGrid);
        setBackground(Color.BLUE);
    }
    //???
   public void getObjectProperties(Object obj){

   }


}
