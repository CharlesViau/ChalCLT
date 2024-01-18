package ca.ulaval.glo2004.gui;

import Domain.General.Entity;
import Domain.History.AccessoryMovedCommand;
import Domain.Interface.IInspectable;
import Domain.Interface.IObjectSelectorListener;
import Domain.Interface.IPropertyChangeListener;
import Domain.MainController;
import Domain.Utility.Vector2;
import View.CurrentView;
import View.ViewUtility.AdapterInchPixel;
import View.ViewUtility.AdapterVector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class DrawingPanel extends JPanel implements IPropertyChangeListener, IObjectSelectorListener, ComponentListener {

    private CurrentView currentView = CurrentView.TOP;
    private MainWindow mainWindow;
    private MainController _controller;
    private AdapterInchPixel adapterInchPixel;
    private Vector2 panelSize;
    private Dimension size;
    private AffineTransform _affineTransform = new AffineTransform();
    private Timer clickTimer;
    private Timer tooltipTimer;
    private final int TOOLTIP_DELAY = 500;
    private Vector2 lastMousePosition = null;
    private Vector2 MouvementInitial= null;
    private Vector2 lastTooltipPosition = null;

    public DrawingPanel(MainWindow mainWindow,AffineTransform a) {
        this.addComponentListener(this);
        size = getSize();
        _controller = mainWindow.getController();
        adapterInchPixel = new AdapterInchPixel();
        _affineTransform = a;
        panelSize = new Vector2(getWidth(), getHeight());
        _controller.setPanelSize(panelSize);
        //Vector2 milieu = adapterInchPixel.pixelToInch(panelSize,1,new Vector2(0,0));
        //_affineTransform.translate(milieu.getX(),milieu.getY());
        //_affineTransform.scale(1,1);


        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                super.mouseWheelMoved(e);

                float nombreScroll = -e.getWheelRotation();
                float zoom_ajustement = nombreScroll / 20;
                float zoom_courant = _controller.getZoom();
                float panelWidth = _controller.getPanelSize().getX();
                float panelHeight = _controller.getPanelSize().getY();

                if (zoom_courant <= 1) {
                    zoom_ajustement = (float) (5 * Math.pow(zoom_courant, 2) * zoom_ajustement);
                }

                float mouseX = e.getX();
                float mouseY = e.getY();

                float newCenterX = mouseX - (mouseX - panelWidth / 2) * zoom_ajustement;
                float newCenterY = mouseY - (mouseY - panelHeight / 2) * zoom_ajustement;

                _controller.setZoom(_controller.getZoom() + zoom_ajustement);
                _controller.setCenter(new Vector2(newCenterX, newCenterY));

                repaint();


                //Vector2 pos = new Vector2(e.getX(), e.getY());
                //Vector2 milieu = new Vector2(panelSize.getX()/2,panelSize.getY()/2);
                //_affineTransform = new AffineTransform();
               // _controller.setCenterGap(pos);
              //  pos =adapterInchPixel.pixelToInch(pos,1,new Vector2(0,0));
                //milieu =adapterInchPixel.pixelToInch(milieu,0,new Vector2(0,0));

             //   _affineTransform.translate(pos.getX(), pos.getY());
              //  _affineTransform.scale(_controller.getZoom(),_controller.getZoom());
              //  _affineTransform.translate(-pos.getX(), -pos.getY());
            }
        });

        clickTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                float zoom = _controller.getZoom();
                Vector2 center = _controller.getCenter();

                float xAjuste = (e.getX() - center.getX()) / zoom + center.getX();
                float yAjuste = (e.getY() - center.getY()) / zoom + center.getY();
                Vector2 posAjustee = new Vector2(xAjuste, yAjuste);

                Vector2 posInInch = Vector2.divide(posAjustee, 4);

                _controller.drawingPanelMouseClicked(posInInch);
                System.out.println(posInInch);


                //Vector2 centerGap = _controller.getCenterGap();
                //Vector2 inchVector = adapterInchPixel.pixelToInch(new Vector2(pos.getX(),pos.getY()), zoom, centerGap);
                //System.out.println(e.getModifiersEx());
                //Point2D point = new Point2D.Float(pos.getX(),pos.getY());
                //Point2D pointResultant = new Point2D.Float();
                /*AffineTransform affineReverse;
                try {
                    affineReverse = _affineTransform.createInverse();
                    affineReverse.transform(point,pointResultant);
                } catch (NoninvertibleTransformException ex) {
                    throw new RuntimeException(ex);
                }

                if(affineReverse==null){
                    inchVector = adapterInchPixel.pixelToInch(new Vector2((float)pointResultant.getX(),(float)pointResultant.getY()), zoom,_controller.getCenterGap());
                }
                else{
                    inchVector = adapterInchPixel.pixelToInch(new Vector2((float)point.getX(),(float)point.getY()), zoom,_controller.getCenterGap());
                }*/

                //System.out.println(pos);
                //System.out.println(zoom);
                //System.out.println(inchVector);

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    lastMousePosition = new Vector2(e.getX(), e.getY());
                    MouvementInitial = new Vector2(e.getX(), e.getY());
                    clickTimer.start();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                clickTimer.stop();
                if(e.getX()!=(int)MouvementInitial.getX()&& e.getY()!=(int)MouvementInitial.getY()) {

                        float zoom = _controller.getZoom();
                        Vector2 center = _controller.getCenter();

                        float xAjuste = (lastMousePosition.getX() - center.getX()) / zoom + center.getX();
                        float yAjuste = (lastMousePosition.getY() - center.getY()) / zoom + center.getY();
                        Vector2 currentMousePosition = new Vector2(xAjuste, yAjuste);

                        float lastXAdjust = (MouvementInitial.getX() - center.getX()) / zoom + center.getX();
                        float lastYAdjust = (MouvementInitial.getY() - center.getY()) / zoom + center.getY();
                        Vector2 MouvementInitial = new Vector2(lastXAdjust, lastYAdjust);

                        Vector2 currentMousePosInInch = Vector2.divide(currentMousePosition, 4);
                        Vector2 lastMousePosInInch = Vector2.divide(MouvementInitial, 4);
                        _controller.moveAccessory(currentMousePosInInch,lastMousePosInInch,true);

                }
                MouvementInitial=null;
                repaint();
            }
        });

        tooltipTimer = new Timer(TOOLTIP_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Vector2 lastPos = new Vector2(lastTooltipPosition.getX(), lastTooltipPosition.getY());
                Vector2 pos = new Vector2(lastPos.getX(), lastPos.getY());
                Vector2 center = _controller.getCenter();
                float zoom = _controller.getZoom();

                pos = AdapterVector.posInInches(pos, center, zoom);
                Entity posEntity = _controller.findSelectedElement(pos);

                if (posEntity != null) {
                    DrawingPanel.this.setToolTipText(setMultilineTooltip(posEntity.onHover()));
                } else {
                    DrawingPanel.this.setToolTipText(null);
                }
                ToolTipManager ttm = ToolTipManager.sharedInstance();
                ttm.mouseExited(new MouseEvent(DrawingPanel.this, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, 0, 0, 0, false));
                ttm.mouseMoved(new MouseEvent(DrawingPanel.this, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0,  (int) lastPos.getX(), (int) lastPos.getY(), 0, false));

            }
        });
        tooltipTimer.setRepeats(false); // Ensure the timer only runs once per event

        ToolTipManager.sharedInstance().setInitialDelay(0);
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMousePosition != null) {
                    float zoom = _controller.getZoom();
                    Vector2 center = _controller.getCenter();

                    float xAjuste = (e.getX() - center.getX()) / zoom + center.getX();
                    float yAjuste = (e.getY() - center.getY()) / zoom + center.getY();
                    Vector2 currentMousePosition = new Vector2(xAjuste, yAjuste);

                    float lastXAdjust = (lastMousePosition.getX() - center.getX()) / zoom + center.getX();
                    float lastYAdjust = (lastMousePosition.getY() - center.getY()) / zoom + center.getY();
                    Vector2 lastMousePositionAdjusted = new Vector2(lastXAdjust, lastYAdjust);

                    Vector2 currentMousePosInInch = Vector2.divide(currentMousePosition, 4);
                    Vector2 lastMousePosInInch = Vector2.divide(lastMousePositionAdjusted, 4);

                    _controller.moveAccessory(currentMousePosInInch, lastMousePosInInch,false);

                    lastMousePosition = new Vector2(e.getX(), e.getY());

                    repaint();
                }
            }





            @Override
            public void mouseMoved(MouseEvent e) {
                setToolTipText(null);
                lastTooltipPosition = new Vector2(e.getX(), e.getY());
                if (tooltipTimer.isRunning()) {
                    tooltipTimer.restart();
                } else {
                    tooltipTimer.start();
                }

            }
        });

        _controller.subscribeToPropertyChangeEvents(objects -> onPropertyChange());
        _controller.subscribeToSelectionEvents(this);
    }

    public void setView(CurrentView currentView) {
        this.currentView = currentView;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //AffineTransform temp = g2.getTransform();
        //g2.setTransform(_affineTransform);

        panelSize = new Vector2(getWidth(), getHeight());
        _controller.setPanelSize(panelSize);
        _controller.selectedViewChanged(this.currentView, g);
        this.currentView = _controller.getCurrentView();
        //g2.setTransform(temp);
    }

    @Override
    public void onPropertyChange() {
        repaint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        panelSize = new Vector2(getWidth(), getHeight());
        _controller.setPanelSize(panelSize);
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public void onObjectSelection(IInspectable object) {
        repaint();
    }

    @Override
    public void onObjectUnselect() {

    }
    @Override
    public void repaint(){
        super.repaint();
    }

    public String setMultilineTooltip(String text) {
        return "<html>" + text.replace("\n", "<br>") + "</html>";
    }

}


