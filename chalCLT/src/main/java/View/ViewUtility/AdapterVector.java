package View.ViewUtility;

import Domain.Utility.Vector2;

public class AdapterVector {
    public static Vector2 posInInches(Vector2 posInPixels, Vector2 center, float zoom) {
        float xAjuste = (posInPixels.getX() - center.getX()) / zoom + center.getX();
        float yAjuste = (posInPixels.getY() - center.getY()) / zoom + center.getY();
        Vector2 posAjustee = new Vector2(xAjuste, yAjuste);

        return Vector2.divide(posAjustee, 4);
    }
}
