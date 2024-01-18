package Domain.Utility;

public class ZoomUtility {
    private final Vector2 m_CenterGap = new Vector2(0,0);

    private float m_zoomFactor = 1;
    private Vector2 m_positionSouris = new Vector2(0,0);

    public Vector2 getCenterGap(){
        return m_CenterGap;
    }
    public void setCenterGap(float centerGapX,float centerGapY){
        m_CenterGap.setX(centerGapX);
        m_CenterGap.setY(centerGapY);
    }

    public float getZoomFactor(){
        return m_zoomFactor;
    }
    public void setZoomFactor(float zoomFactor){
        m_zoomFactor = zoomFactor;
    }
    public Vector2 getMousePosition(){
        return m_positionSouris;
    }
    public void setMousePosition(Vector2 position){
        m_positionSouris = position;
    }

}
