package Domain.Utility;
//convertis les valeurs en entrée pour y donner l'effet de zoom
public class ZoomConverter {
    private ZoomUtility m_zoomUtility;

    public ZoomConverter(ZoomUtility zoomUtility){
        m_zoomUtility = zoomUtility;
    }

    public float getZoomFactor() {
        return m_zoomUtility.getZoomFactor();
    }

    public Vector2 getCenterGap() { return m_zoomUtility.getCenterGap(); }

    public Vector2 Convertir(Vector2 valeurs){
        return new Vector2((valeurs.getX()+ m_zoomUtility.getCenterGap().getX())* m_zoomUtility.getZoomFactor(),(valeurs.getY()+ m_zoomUtility.getCenterGap().getY())* m_zoomUtility.getZoomFactor());
    }
    public Vector2 getDiffCentre() { return m_zoomUtility.getMousePosition(); }

}
