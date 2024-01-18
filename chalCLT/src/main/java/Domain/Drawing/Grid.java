package Domain.Drawing;

public class Grid {

    private float distance;
    private boolean isVisible;
    private float xOffsetPercent = 0;
    private float yOffsetPercent = 0;

    public Grid(float distance, boolean isVisible) {
        this.distance = distance;
        this.isVisible = isVisible;
    }

    public float getDistance() {
        return distance;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public float getXOffset() {
        return xOffsetPercent;
    }

    public void setXOffset(float xOffset) {
        this.xOffsetPercent = xOffset;
    }

    public float getYOffset() {
        return yOffsetPercent;
    }

    public void setYOffset(float yOffset) {
        this.yOffsetPercent = yOffset;
    }


}
