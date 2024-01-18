package Domain.Drawing;

public class RectangleView {
    private double x;
    private double y;
    private double height;
    private double width;


    public RectangleView(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }
}

