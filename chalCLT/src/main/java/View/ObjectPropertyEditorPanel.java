package View;

import Domain.CustumAnnotations.*;
import Domain.DTO.PropertyDTO;
import Domain.General.Components.Component;
import Domain.General.Entity;

import Domain.Interface.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;

public class ObjectPropertyEditorPanel extends JPanel implements IObjectSelectorListener {

    //A text field represent a property of an object.
    private final HashMap<JComponent, PropertyDTO> propertyMap;

    private IInspectable selectedObject;

    private final IPropertyChangeManager propertyChangeManager;

    private static final Color TEXT_FIELD_ERROR_COLOR = new Color(255, 114, 118);

    private String tempValue;
    protected int gridY = 0;

    public ObjectPropertyEditorPanel(IPropertyChangeManager manager) {
        setLayout(new GridBagLayout());
        createInspectorBorder();
        propertyMap = new HashMap<>();
        this.propertyChangeManager = manager;
        manager.subscribeToPropertyChangeEvents((ctx) -> onPropertyChange());
    }

    @Override
    public void onObjectSelection(IInspectable object) {

        selectedObject = object;

        //Clean Panel
        removeAll();

        JPanel objectEditorPanel = CreateEditor(object);

        // Create Scroll Pane Add objectEditorPanel to the JScrollPane
        JScrollPane scrollPane = createJScrollPane(objectEditorPanel);

        // Add the JScrollPane to the ObjectPropertyEditorPanel
        add(scrollPane, createScrollPaneConstraints());

        revalidate();
    }

    @Override
    public void onObjectUnselect() {
        removeAll();
        selectedObject = null;
        propertyMap.clear();
    }

    private JPanel CreateEditor(IInspectable object) {
        JPanel objectEditorPanel;
        if (object.getClass().getAnnotation(CustomEditor.class) != null) {
            CustomEditor annotation = object.getClass().getAnnotation(CustomEditor.class);
            objectEditorPanel = DrawEditor(object, annotation.editorType(), selectedObject.getUuid());
        } else {
            objectEditorPanel = DrawBaseEditor(object, new JPanel(new GridBagLayout()));
        }
        return objectEditorPanel;
    }

    private <T extends Editor> JPanel DrawEditor(Object obj, Class<T> customEditorType, UUID selectedObject) {
        try {
            T customEditor = customEditorType.getDeclaredConstructor().newInstance();
            JPanel editorPanel = new JPanel();

            // Set the layout to BoxLayout with a vertical orientation
            editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));

            if (customEditor.isDrawBaseInspector()) {
                editorPanel.add(DrawBaseEditor(obj, new JPanel(new GridBagLayout())));
            }

            editorPanel.add(customEditor.DrawEditor(propertyChangeManager, selectedObject, (Class<? extends Component>) obj.getClass()));

            return editorPanel;
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends Editor> void DrawEditor(Object obj, Class<T> customEditorType, UUID selectedObject, JPanel editorPanel) {
        try {
            T customEditor = customEditorType.getDeclaredConstructor().newInstance();

            // Set the layout to BoxLayout with a vertical orientation
            editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));

            if (customEditor.isDrawBaseInspector()) {
                editorPanel.add(DrawBaseEditor(obj, new JPanel(new GridBagLayout())));
            }

            editorPanel.add(customEditor.DrawEditor(propertyChangeManager, selectedObject, (Class<? extends Component>) obj.getClass()));

        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private JPanel DrawBaseEditor(Object object, JPanel objectEditorPanel) {

        if (!(object instanceof IInspectable) ||
                ((IInspectable) object).getUuid() == null) {
            return objectEditorPanel;
        }

        Class<?> objectClass = object.getClass();

        // Collect all fields, including inherited fields
        List<Field> allFields = new ArrayList<>();
        while (objectClass != null) {
            Field[] declaredFields = objectClass.getDeclaredFields();
            allFields.addAll(Arrays.asList(declaredFields));
            objectClass = objectClass.getSuperclass();
        }

        for (Field field : allFields) {

            // Ignore fields you want to hide from the user.
            if (field.getType() == UUID.class ||
                    fieldHasAnnotation(field, HideInInspector.class) ||
                    Modifier.isStatic(field.getModifiers()) ||
                    ((Modifier.isPrivate(field.getModifiers()) || Modifier.isProtected(field.getModifiers())) && !fieldHasAnnotation(field, SerializeField.class)))
                continue;

            // TODO: Handle boolean to make a CheckBox instead of text field,
            // handle collections

            if (field.getType().isEnum()) {
                // Handle Enum fields
                HandleEnumField(object, field, objectEditorPanel);

            } else if (!field.getType().isPrimitive() && field.getType() != String.class) {
                // Handle Complex Objects
                HandleComplexObject(object, field, objectEditorPanel);

            } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                HandleBooleanFields((IInspectable) object, field, objectEditorPanel);


            } else {
                // Handle Primitive fields
                HandlePrimitiveFields((IInspectable) object, field, objectEditorPanel);
            }
        }

        return objectEditorPanel;
    }

    private void HandleBooleanFields(IInspectable object, Field field, JPanel objectEditorPanel) {
        field.setAccessible(true);
        JLabel label = new JLabel(formatLabel(field.getName()));
        JCheckBox box = new JCheckBox();

        try {
            box.setSelected((Boolean) field.get(object));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        box.addItemListener(e -> {
            if (object.getClass() == Entity.class) {
                propertyChangeManager.editProperty(object.getUuid(), null, field.getName(), box.isSelected());
            } else {
                propertyChangeManager.editProperty(object.getUuid(), (Class<? extends Component>) object.getClass(), field.getName(), box.isSelected());
            }
        });

        //Add Label and checkbox to the panel.
        objectEditorPanel.add(label, createLabelConstraints(gridY));
        objectEditorPanel.add(box, createTextFieldConstraints(gridY));

        //UpdatePropertyMap

        if (object.getClass() == Entity.class) {
            propertyMap.put(box, new PropertyDTO(object.getUuid(), null, field.getName(), field.getType()));
        } else {
            propertyMap.put(box, new PropertyDTO(object.getUuid(), (Class<? extends Component>) object.getClass(), field.getName(), field.getType()));
        }

        field.setAccessible(false);

        gridY++;

    }

    private void HandleEnumField(Object object, Field field, JPanel objectEditorPanel) {
        // Get enum values
        Enum<?>[] enumConstants = (Enum<?>[]) field.getType().getEnumConstants();

        // Create a label for the field
        JLabel label = new JLabel(formatLabel(field.getName()));

        // Create a JComboBox for enum values
        JComboBox<Enum<?>> enumComboBox = new JComboBox<>(enumConstants);

        label.setLabelFor(enumComboBox);

        try {
            field.setAccessible(true);
            enumComboBox.setSelectedItem(field.get(object));
            field.setAccessible(false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        enumComboBox.addActionListener(e ->
        {
            requestFocus();
            propertyChangeManager.editProperty(((IInspectable) object).getUuid(), (Class<? extends Component>) object.getClass(), field.getName(), enumComboBox.getSelectedItem());
            objectEditorPanel.requestFocus();
        });


        //Add Label and text field to the panel.
        objectEditorPanel.add(label, createLabelConstraints(gridY));
        objectEditorPanel.add(enumComboBox, createTextFieldConstraints(gridY));

        gridY++;
    }

    private void HandleComplexObject(Object object, Field field, JPanel objectEditorPanel) {

        Object fieldObject;
        field.setAccessible(true);

        try {
            fieldObject = field.get(object);
        } catch (IllegalAccessException e) {
            return;
        }


        if (fieldObject instanceof HashMap) {

            HashMap<?, ?> map = (HashMap<?, ?>) fieldObject;

            if (map.isEmpty()) {
                return;
            }

            Object firstKey = map.keySet().iterator().next();

            if (firstKey instanceof Class<?> && Component.class.isAssignableFrom((Class<?>) firstKey)) {
                // Handle Component case
                HandleComponents(objectEditorPanel, map);
                return;
            }

            HandleHashMap(field, objectEditorPanel, map, object);
            return;
        }


        if (!(fieldObject instanceof IInspectable) || ((IInspectable) fieldObject).getUuid() == null) {
            return;
        }

        field.setAccessible(false);
    }

    private void HandleComponents(JPanel objectEditorPanel, HashMap<?, ?> fieldObject) {
        if (fieldObject.isEmpty() || !(fieldObject.values().iterator().next() instanceof IInspectable)) {
            return;
        }

        for (Object item : fieldObject.values()) {

            if (!((Component) item).showInInspector)
                continue;

            JPanel componentPanel = new JPanel();
            BoxLayout componentBoxLayout = new BoxLayout(componentPanel, BoxLayout.Y_AXIS);
            componentPanel.setLayout(componentBoxLayout);

            // Create a Button-Label Panel
            JPanel buttonLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel complexLabel = new JLabel(formatLabel(item.getClass().getName()));

            // Create a Field Panel
            JPanel fieldPanel = new JPanel(new GridBagLayout());

            JButton showHidePropertiesButton = getShowHidePropertiesButton(fieldPanel);
            buttonLabelPanel.add(complexLabel);
            buttonLabelPanel.add(showHidePropertiesButton);

            // Add the component panel to the main editor panel
            objectEditorPanel.add(componentPanel, createCollapsablePanelConstraints(gridY));

            // DrawBaseEditor should add components vertically within the fieldPanel
            if (item.getClass().getAnnotation(CustomEditor.class) != null) {
                CustomEditor annotation = item.getClass().getAnnotation(CustomEditor.class);
                DrawEditor(item, annotation.editorType(), selectedObject.getUuid(), fieldPanel);
            } else {
                DrawBaseEditor(item, fieldPanel);
            }

            // Add the complex object properties panel below the button-label panel
            componentPanel.add(buttonLabelPanel);
            componentPanel.add(fieldPanel);

            gridY++;

            Border border = BorderFactory.createLineBorder(Color.BLACK);
            componentPanel.setBorder(border);
        }
    }

    private void HandleHashMap(Field field, JPanel objectEditorPanel, HashMap<?, ?> fieldObject, Object obj) {

        if (fieldObject.isEmpty() || !(fieldObject.values().iterator().next() instanceof IInspectable)) {
            return;
        }

        JPanel complexObjectPanel = new JPanel();
        complexObjectPanel.setLayout(new BoxLayout(complexObjectPanel, BoxLayout.Y_AXIS));
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));

        fieldPanel.setBorder(BorderFactory.createLineBorder(Color.blue));

        // Create a Label and Collapse Toggle Button
        JLabel complexLabel = new JLabel(formatLabel(field.getName()));
        JButton showHidePropertiesButton = getShowHidePropertiesButton(fieldPanel);

        // Create a Button-Label Panel
        JPanel labelButtonPanel = new JPanel();
        labelButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        labelButtonPanel.add(complexLabel);
        labelButtonPanel.add(showHidePropertiesButton);
        labelButtonPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        // Add Label and Button to Panel
        complexObjectPanel.add(labelButtonPanel);
        complexObjectPanel.add(fieldPanel);

        objectEditorPanel.add(complexObjectPanel);

        gridY++; // Increment by 1 to skip the next row as it's already occupied

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        complexObjectPanel.setBorder(border);

        for (Object item : fieldObject.values()) {
            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));

            // Create a label panel with FlowLayout.LEFT
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            JLabel objectLabel = new JLabel(((IInspectable) item).getName());
            labelPanel.add(objectLabel);

            // Create a button panel with FlowLayout.RIGHT
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            JButton removeButton = new JButton("Remove");
            buttonPanel.add(removeButton);

            itemPanel.add(labelPanel);
            itemPanel.add(Box.createHorizontalGlue()); // Add glue for spacing between label and button
            itemPanel.add(buttonPanel);

            fieldPanel.add(itemPanel);
            gridY++;

            removeButton.addActionListener(e ->
                    propertyChangeManager.removeObject(((IInspectable) item).getUuid(), selectedObject.getUuid(), (Class<? extends Component>) obj.getClass(), (Class<? extends Component>) item.getClass()));
        }

    }

    private void HandlePrimitiveFields(IInspectable object, Field field, JPanel objectEditorPanel) {

        field.setAccessible(true);
        JLabel label = new JLabel(formatLabel(field.getName()));
        JTextField textField = new JTextField();

        label.setLabelFor(textField);

        addActionListenersToTextField(textField, objectEditorPanel);

        String value;

        try {
            value = String.valueOf(field.get(object));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if ((field.getType() == float.class || field.getType() == double.class) && field.getAnnotation(Imperial.class) != null) {
            textField.setText(View.ViewUtility.Imperial.floatToImperial(Float.parseFloat(value)));
        } else {
            textField.setText(value);
        }

        //Add Label and text field to the panel.
        objectEditorPanel.add(label, createLabelConstraints(gridY));
        objectEditorPanel.add(textField, createTextFieldConstraints(gridY));

        //UpdatePropertyMap

        if (object.getClass() == Entity.class) {
            propertyMap.put(textField, new PropertyDTO(object.getUuid(), null, field.getName(), field.getType()));
        } else {
            propertyMap.put(textField, new PropertyDTO(object.getUuid(), (Class<? extends Component>) object.getClass(), field.getName(), field.getType()));
        }

        field.setAccessible(false);

        gridY++;
    }

    private String formatLabel(String fieldName) {
        StringBuilder formattedLabel = new StringBuilder();

        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);

            char lastC = (i > 0) ? fieldName.charAt(i - 1) : 0;

            if (Character.isUpperCase(c) || (Character.isDigit(c) && i > 0 && Character.isLetter(lastC))) {
                formattedLabel.append(' ');
            }
            formattedLabel.append(c);
        }

        if (formattedLabel.length() > 0) {
            formattedLabel.setCharAt(0, Character.toUpperCase(formattedLabel.charAt(0)));
        }

        return formattedLabel.toString();
    }

    private static boolean fieldHasAnnotation(Field field, Class<? extends Annotation> annotationClass) {
        return field.getAnnotation(annotationClass) != null;
    }

    private static JButton getShowHidePropertiesButton(JPanel complexObjectPanel) {
        JButton showHidePropertiesButton = new JButton(complexObjectPanel.isVisible() ? "Cacher Propriétés" : "Montrer Propriétés");

        showHidePropertiesButton.addActionListener(e -> {

            complexObjectPanel.setVisible(!complexObjectPanel.isVisible());
            showHidePropertiesButton.setText(complexObjectPanel.isVisible() ? "Cacher Propriétés" : "Montrer Propriétés");
            complexObjectPanel.revalidate();
            complexObjectPanel.repaint();

        });
        return showHidePropertiesButton;
    }

    private void createInspectorBorder() {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Inspecteur");
        titledBorder.setTitleJustification(TitledBorder.LEFT);
        setBorder(titledBorder);
    }

    private JScrollPane createJScrollPane(JPanel objectEditorPanel) {
        JScrollPane scrollPane = new JScrollPane(objectEditorPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        return scrollPane;
    }

    private GridBagConstraints createLabelConstraints(int gridY) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.gridx = 0;
        labelConstraints.gridy = gridY;
        labelConstraints.insets = new Insets(5, 5, 5, 5);
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.gridwidth = 1; // Set it to 1 as it spans one column
        labelConstraints.weightx = 0; // Reduce the width of the label

        return labelConstraints;
    }

    private GridBagConstraints createTextFieldConstraints(int gridY) {
        GridBagConstraints textFieldConstraints = new GridBagConstraints();
        textFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        textFieldConstraints.gridx = 1;
        textFieldConstraints.gridy = gridY;
        textFieldConstraints.insets = new Insets(5, 5, 5, 5);
        textFieldConstraints.anchor = GridBagConstraints.WEST;
        textFieldConstraints.gridwidth = 1; // Set it to 1 as it spans one column
        textFieldConstraints.weightx = 1; // Make the text field take all available width

        return textFieldConstraints;
    }

    private GridBagConstraints createCollapsablePanelConstraints(int gridY) {
        // Create constraints for the collapsible object panel
        GridBagConstraints collapsibleObjectConstraints = new GridBagConstraints();
        collapsibleObjectConstraints.fill = GridBagConstraints.HORIZONTAL;
        collapsibleObjectConstraints.gridx = 0;
        collapsibleObjectConstraints.gridy = gridY + 1; // Place it below the label and button
        collapsibleObjectConstraints.insets = new Insets(5, 5, 5, 5);
        collapsibleObjectConstraints.anchor = GridBagConstraints.WEST;
        collapsibleObjectConstraints.gridwidth = 2; // Make it span across two columns
        collapsibleObjectConstraints.weightx = 1; // Expand horizontally

        return collapsibleObjectConstraints;
    }

    private GridBagConstraints createScrollPaneConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;

        return constraints;
    }

    private void addActionListenersToTextField(JTextField textField, JPanel objectEditorPanel) {
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // The text field gained focus
                handleTextFieldFocusGained(e);

            }

            @Override
            public void focusLost(FocusEvent e) {
                // The text field lost focus
                handleTextFieldFocusLost(e);

            }
        });

        // Create the InputMap and add a key binding for Enter
        InputMap inputMap = textField.getInputMap(JTextField.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "enterAction");

        // Create the ActionMap and associate the key binding with the custom action
        ActionMap actionMap = textField.getActionMap();

        actionMap.put("enterAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                objectEditorPanel.requestFocusInWindow();
            }
        });
    }

    private void handleTextFieldFocusGained(FocusEvent e) {
        JTextField textField = getTextFieldFocusEventSource(e);

        if (textField == null)
            return;

        tempValue = textField.getText();
    }

    private void handleTextFieldFocusLost(FocusEvent e) {
        JTextField textField = getTextFieldFocusEventSource(e);

        if (textField == null)
            return;

        //TODO : Make checks to ensure textField string is convertible to appropriate type.

        PropertyDTO data = propertyMap.get(textField);
        String value = textField.getText();
        Class<?> clazz = data.componentType;
        Field field = null;
        // Iterate through the class hierarchy to find the field
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(data.name);
            } catch (NoSuchFieldException ignored) {
                // Field not found in the current class, try the superclass
                clazz = clazz.getSuperclass();
            }
        }

        if (clazz != null && field.getAnnotation(Imperial.class) != null && data.fieldType == float.class && View.ViewUtility.Imperial.isConvertible(value)) {
            value = String.valueOf(View.ViewUtility.Imperial.imperialToFloat(value));
        }

        if (propertyChangeManager.validatePropertyChange(data.uuid, data.componentType, data.name, value)) {

            propertyChangeManager.editProperty(data.uuid, data.componentType, data.name, value);
        } else {
            textField.setBackground(TEXT_FIELD_ERROR_COLOR);
        }

    }

    private JTextField getTextFieldFocusEventSource(FocusEvent e) {
        Object eventSource = e.getSource();
        return eventSource.getClass() == JTextField.class ? (JTextField) eventSource : null;
    }


    private void onPropertyChange() {
        //Redraw the inspector in case that multiple values
        //have changed due to a user change.
        if (selectedObject == null)
            return;
        removeAll();
        propertyMap.clear();
        onObjectSelection(selectedObject);

    }

}





