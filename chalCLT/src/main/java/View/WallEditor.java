package View;

import Domain.History.AccessoryCommand;
import Domain.History.Command;
import Domain.Interface.IPropertyChangeManager;
import Domain.MainController;
import Domain.Shack.Accessory.Accessory;
import Domain.Shack.Accessory.AccessoryType;
import Domain.Utility.Tuple;
import Domain.General.Components.Component;
import View.ViewUtility.Imperial;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WallEditor extends Editor {
    private static final Color TEXT_FIELD_ERROR_COLOR = new Color(255, 114, 118);
    WallEditor() {
        super();
        drawBaseInspector = true;
    }

    @Override
    public JPanel DrawEditor(IPropertyChangeManager manager, UUID selectedObject, Class<? extends Component> currentComponentType) {
        //Object Creation
        JPanel toReturn = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addAccessoryButton = new JButton("Ajouter");
        JComboBox<Tuple<String, Class<?>>> comboBox = new JComboBox<>();
        JLabel xLabel = new JLabel("X : ");
        JLabel yLabel = new JLabel("Y : ");
        JTextField xField = new JTextField(3);
        JTextField yField = new JTextField(3);

        //Label Setup
        xLabel.setLabelFor(xField);
        yLabel.setLabelFor(yField);

        //Field Setup
        xField.setText("0");
        yField.setText("0");

        //ComboBox Setup
        ArrayList<Class<?>> accessoryTypes = AccessoryType.getAccessoryTypes();
        addAccessoryToComboBox(accessoryTypes, comboBox);
        SetComboBoxCustomRenderer(comboBox);

        if (comboBox.getItemCount() > 0) {
            comboBox.setSelectedIndex(0);
        }

        //Setup Add Accessory Button
        addAccessoryButton.addActionListener(e -> {

            Tuple<String, Class<?>> selectedValue = (Tuple<String, Class<?>>) comboBox.getSelectedItem();
            Class<? extends Component> selectedAccessoryClass = (Class<? extends Component>) selectedValue.getSecond();

            String xValue = xField.getText();
            String yValue = yField.getText();

            if (Imperial.isConvertible(xValue)) {
                xValue = String.valueOf(Imperial.imperialToFloat(xValue));
            }

            if (Imperial.isConvertible(yValue)) {
                yValue = String.valueOf(Imperial.imperialToFloat(yValue));
            }

            if(manager.validateObjectCreation(selectedObject, currentComponentType, selectedAccessoryClass, new Object[]{xValue, yValue})){

                    manager.addObject(selectedObject, currentComponentType, selectedAccessoryClass, new Object[]{xValue, yValue});

            }
            else{
                xField.setBackground(TEXT_FIELD_ERROR_COLOR);
                yField.setBackground(TEXT_FIELD_ERROR_COLOR);
            }
        });

        //Add everything to panel
        toReturn.add(comboBox);
        toReturn.add(xLabel);
        toReturn.add(xField);
        toReturn.add(yLabel);
        toReturn.add(yField);
        toReturn.add(addAccessoryButton);

        return toReturn;
    }

    private void SetComboBoxCustomRenderer(JComboBox<Tuple<String, Class<?>>> comboBox) {
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Tuple) {
                    value = ((Tuple<String, Class<?>>) value).getFirst();
                }
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                return this;
            }
        });
    }

    private static void addAccessoryToComboBox(List<Class<?>> accessoryTypes, JComboBox<Tuple<String, Class<?>>> comboBox) {
        for (Class<?> type : accessoryTypes) {
            comboBox.addItem(new Tuple<>(type.getSimpleName(), type));
        }
    }
}
