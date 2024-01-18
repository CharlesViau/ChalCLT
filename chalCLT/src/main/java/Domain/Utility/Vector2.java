package Domain.Utility;

import java.io.Serializable;

public class Vector2 implements Serializable {
    private float x;
    private float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    public static Vector2 multiply(Vector2 v, float m) {
        return new Vector2(v.getX()*m, v.getY()*m);
    }

    public static Vector2 divide(Vector2 v, float m) {
        return new Vector2(v.getX()/m, v.getY()/m);
    }

    public float dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    public Vector2 normalize() {
        float length = (float) Math.sqrt(x * x + y * y);
        return new Vector2(x / length, y / length);
    }

    public static float areaOfTriangle(Vector2 p1, Vector2 p2, Vector2 p3) {
        return Math.abs(p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y) + p3.x * (p1.y - p2.y)) / 2.0f;
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
