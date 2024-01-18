package Domain.Utility;

import java.io.Serializable;

public class Vector3 implements Serializable {
    public double x;
    public double y;
    public double z;

    public static final Vector3 UP = new Vector3(0, 1, 0);

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 vectorCopy)
    {
        x = vectorCopy.x;
        y = vectorCopy.y;
        z = vectorCopy.z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3 subtract(Vector3 other) {
        return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public double dotProduct(Vector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3 crossProduct(Vector3 other) {
        double resultX = this.y * other.z - this.z * other.y;
        double resultY = this.z * other.x - this.x * other.z;
        double resultZ = this.x * other.y - this.y * other.x;
        return new Vector3(resultX, resultY, resultZ);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3 normalize() {
        double mag = magnitude();
        return new Vector3(x / mag, y / mag, z / mag);
    }

    public Vector3 multiply(double scalar) {
        return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
