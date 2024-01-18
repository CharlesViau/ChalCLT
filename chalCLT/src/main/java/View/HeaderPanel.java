package View;

import Domain.CustumAnnotations.Imperial;
import Domain.MainController;
import ca.ulaval.glo2004.gui.DrawingPanel;
import ca.ulaval.glo2004.gui.MainWindow;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class HeaderPanel extends JPanel {
    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenuItem newChal;
    private JMenuItem openChal;
    private JMenuItem saveChal;
    private JMenuItem saveAsChal;
    private JMenu menuEdition;
    private JMenuItem exportBrut;
    private JMenuItem exportFini;
    private JMenuItem exportRetrait;
    private JMenuItem undo;
    private JMenuItem redo;
    private MainController mainController;
    private MainWindow mainWindow;
    private DrawingPanel _drawingPanel;

    private enum ExportType {
        brut,
        fini,
        retrait
    }

    public HeaderPanel(MainWindow mainWindow, DrawingPanel drawingPanel) {
        this.mainController = mainWindow.getController();
        this.mainWindow = mainWindow;
        this._drawingPanel = drawingPanel;
        menusInit();
        setListeners();
    }

    private void menusInit() {
        menuFile = new JMenu("Fichier");
        menuEdition = new JMenu("Edition");
        add(menuFile);
        add(menuEdition);
        menuFile.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuEdition.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuFileInit();
        menuBarInit();
        menuEditionInit();
        createGridControls();

    }

    private void createGridControls() {

        JButton toggleButton = new JButton();

        if (mainController.getGridVisibility()) {
            toggleButton.setText("Cacher");
        } else {
            toggleButton.setText("Montrer");
        }

        toggleButton.addActionListener(e -> {

            if (mainController.getGridVisibility()) {
                toggleButton.setText("Cacher");
                mainController.setGridVisibility(false);
            } else {
                toggleButton.setText("Montrer");
                mainController.setGridVisibility(true);
            }


        });

        JLabel labelGrille = new JLabel("Grille : ");
        labelGrille.setLabelFor(toggleButton);

        JLabel labelX = new JLabel("X Offset : ");
        JSlider xOffset = new JSlider();
        labelX.setLabelFor(xOffset);
        xOffset.setMaximum(-100);
        xOffset.setMaximum(100);
        xOffset.setValue((int)mainController.getXOffset()*100);

        xOffset.addChangeListener(e -> mainController.setXOffset((float) (xOffset.getValue() / 100.0)));

        JLabel labelY = new JLabel("Y Offset : ");
        JSlider yOffset = new JSlider();
        labelY.setLabelFor(yOffset);
        yOffset.setMaximum(-100);
        yOffset.setMaximum(100);
        yOffset.setValue((int)mainController.getYOffset()*100);

        yOffset.addChangeListener(e -> mainController.setYOffset((float) (yOffset.getValue() / 100.0)));

        JLabel labelMesure = new JLabel("Mesure");
        JTextField textMesure = new JTextField(10);
        labelMesure.setLabelFor(textMesure);
        textMesure.setText(View.ViewUtility.Imperial.floatToImperial(mainController.getGridDistance()));

        addActionListenersToGridTextField(textMesure, this);


        add(labelGrille);
        add(toggleButton);
        add(labelX);
        add(xOffset);
        add(labelY);
        add(yOffset);
        add(labelMesure);
        add(textMesure);
    }

    private void addActionListenersToGridTextField(JTextField textField, JPanel objectEditorPanel) {
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {


            }

            @Override
            public void focusLost(FocusEvent e) {

                String value = textField.getText();

                if (View.ViewUtility.Imperial.isConvertible(value)) {
                    mainController.setGridDistance(View.ViewUtility.Imperial.imperialToFloat(value));
                    textField.setText(View.ViewUtility.Imperial.floatToImperial(View.ViewUtility.Imperial.imperialToFloat(value)));
                }
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

    private void menuFileInit() {
        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        KeyStroke ctrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        newChal = new JMenuItem("Nouveau");
        openChal = new JMenuItem("Ouvrir");
        saveChal = new JMenuItem("Sauvegarder");
        saveAsChal = new JMenuItem("Sauvegarder sous");
        menuFile.add(newChal);
        menuFile.add(openChal);
        menuFile.add(saveChal);
        menuFile.add(saveAsChal);
        newChal.setAccelerator(ctrlN);
        openChal.setAccelerator(ctrlO);
        saveChal.setAccelerator(ctrlS);
    }

    private void menuEditionInit() {
        KeyStroke ctrlZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        KeyStroke ctrlY = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        exportBrut = new JMenuItem("Export brut");
        exportFini = new JMenuItem("Export fini");
        exportRetrait = new JMenuItem("Export retrait");
        undo = new JMenuItem("Annuler");
        redo = new JMenuItem("Refaire");
        menuEdition.add(exportBrut);
        menuEdition.add(exportFini);
        menuEdition.add(exportRetrait);
        menuEdition.addSeparator();
        menuEdition.add(undo);
        menuEdition.add(redo);
        undo.setAccelerator(ctrlZ);
        redo.setAccelerator(ctrlY);
    }

    private void menuBarInit() {
        menuBar = new JMenuBar();
        menuBar.add(menuFile);
        menuBar.add(menuEdition);
        add(menuBar);
    }

    private File getSaveLocation() {
        return mainController.getSaveLocation();
    }

    //Creates a file chooser with the current save location as default
    private JFileChooser createFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        if (getSaveLocation() != null)
            //Add save location for export;
            fileChooser.setCurrentDirectory(getSaveLocation());
        else
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Chal files", "clt");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        return fileChooser;
    }

    //Creates a file chooser for the save action and calls controller to save project if user selects a file
    private boolean createFileSaveChooser() {
        JFileChooser fileChooser = createFileChooser();
        fileChooser.setDialogTitle("Sauvegarder sous");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(this);

        //Calls controller to save project if user selects a file
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            System.out.println("Save as file: " + file.getAbsolutePath());
            mainController.saveProjectAs(file);
            mainController.setCurrentProjectName();
            return true;
        }
        return false;
    }

    //Creates a file chooser for the open action and calls controller to open project if user selects a file
    private void createFileOpenChooser() {
        JFileChooser fileChooser = createFileChooser();
        fileChooser.setDialogTitle("Ouvrir");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);

        //Calls controller to open project if user selects a file
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            mainController.openProject(file);
            mainController.setCurrentProjectName();
            mainWindow.updateAll();
        }
    }

    private void createFileExportChooser(ExportType exportType) throws IOException {
        JFileChooser fileChooser = createFileChooser();
        fileChooser.setDialogTitle("Exporter");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);

        //Calls controller to open project if user selects a file
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            mainController.setCurrentProjectName();
            switch (exportType) {
                case brut:
                    mainController.exportBrut(file);
                    break;
                case fini:
                    mainController.exportFini(file);
                    break;
                case retrait:
                    mainController.exportRetrait(file);
                    break;
            }
        }
    }

    private boolean createJOptionPane(String title, String message, int optionType) {
        JOptionPane optionPane = new JOptionPane();
        optionPane.setMessage("Voulez-vous sauvegarder avant de quitter?");
        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.YES_NO_OPTION);
        JDialog dialog = optionPane.createDialog(this, "Quitter");
        dialog.setVisible(true);
        if (optionPane.getValue() == null) {
            return false;
        }
        int result = (int) optionPane.getValue();
        if (result == JOptionPane.YES_OPTION) {
            return true;
        } else if (result == JOptionPane.NO_OPTION) {
            return false;
        }
        return false;
    }

    private boolean checkIfSaved() {
        if (!mainController.isSaved()) {
            return createJOptionPane("Quitter", "Voulez-vous sauvegarder avant de quitter?", JOptionPane.YES_NO_CANCEL_OPTION);
        }
        return false;
    }

    private void setNewChalListener() {
        newChal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!checkIfSaved()) {
                    mainController.newProject();
                    mainWindow.updateAll();
                    System.out.println("New Project Created");
                } else if (createFileSaveChooser()) {
                    mainController.newProject();
                    mainWindow.updateAll();
                    System.out.println("New Project Created");
                }
            }
        });
    }

    private void setOpenChalListener() {
        openChal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!checkIfSaved()) {
                    createFileOpenChooser();
                } else if (createFileSaveChooser()) {
                    createFileOpenChooser();
                }

            }
        });
    }

    private void setSaveChalListener() {
        saveChal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                //Calls controller to save project
                //If there is no save location, calls createFileSaveChooser to create a file chooser
                boolean saved = mainController.saveProject();
                if (!saved)
                    createFileSaveChooser();
            }
        });
    }

    private void setSaveAsChalListener() {
        saveAsChal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                createFileSaveChooser();
            }
        });
    }

    private void setExportBrutListener() {
        exportBrut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    createFileExportChooser(ExportType.brut);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println("Export brut");
            }
        });
    }

    private void setExportFiniListener() {
        exportFini.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    createFileExportChooser(ExportType.fini);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println("Export fini");
            }
        });
    }

    private void setExportRetraitListener() {
        exportRetrait.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    createFileExportChooser(ExportType.retrait);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println("Export retrait");
            }
        });
    }

    private void setUndoListener() {
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {

                mainController.Undo();
                _drawingPanel.setView(mainController.getCurrentView());

            }
        });
    }

    private void setRedoListener() {
        redo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {

                System.out.println("Redo");
                mainController.Redo();
                _drawingPanel.setView(mainController.getCurrentView());
            }
        });
    }

    private void setListeners() {
        setNewChalListener();
        setOpenChalListener();
        setSaveChalListener();
        setSaveAsChalListener();
        setUndoListener();
        setRedoListener();
        setExportBrutListener();
        setExportFiniListener();
        setExportRetraitListener();
    }

}

