package View;

import Domain.MainController;
import ca.ulaval.glo2004.gui.DrawingPanel;
import ca.ulaval.glo2004.gui.MainWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewSelectionPanel extends JPanel {
    private JButton TopView;
    private JButton LeftView;
    private JButton RightView;
    private JButton FrontView;
    private JButton BackView;
    private DrawingPanel _drawingPanel;
    private MainController _mainController;

    public ViewSelectionPanel(MainWindow mainWindow, DrawingPanel drawingPanel) {
        _drawingPanel = drawingPanel;
        _mainController = mainWindow.getController();

        buttonInit();
        setTopButtonAction();
        setLeftButtonAction();
        setRightButtonAction();
        setFrontButtonAction();
        setBackButtonAction();

    }

    public void buttonInit() {
        TopView = new JButton("Dessus");
        LeftView = new JButton("Gauche");
        RightView = new JButton("Droite");
        FrontView = new JButton("Devant");
        BackView = new JButton("Derrière");
        add(TopView);
        add(LeftView);
        add(RightView);
        add(FrontView);
        add(BackView);
    }

    public void setTopButtonAction() {
        TopView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _drawingPanel.setView(CurrentView.TOP);
            }
        });
    }

    public void setLeftButtonAction() {
        LeftView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _drawingPanel.setView(CurrentView.LEFT);
            }
        });

    }

    public void setRightButtonAction() {
        RightView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _drawingPanel.setView(CurrentView.RIGHT);
            }
        });
    }

    public void setFrontButtonAction() {
        FrontView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _drawingPanel.setView(CurrentView.FRONT);
            }
        });
    }

    public void setBackButtonAction() {
        BackView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _drawingPanel.setView(CurrentView.BACK);
            }
        });
    }

}

