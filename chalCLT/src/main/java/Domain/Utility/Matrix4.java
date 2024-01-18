package Domain.Utility;

public class Matrix4 {
    private double[] matrix;

    public Matrix4() {
        matrix = new double[16];
        setIdentity();
    }

    public void setIdentity() {
        for (int i = 0; i < 16; i++) {
            matrix[i] = (i % 5 == 0) ? 1.0f : 0.0f;
        }
    }

    public void translate(double x, double y, double z) {
        matrix[12] += x;
        matrix[13] += y;
        matrix[14] += z;
    }

    public void rotate(double angle, double axisX, double axisY, double axisZ) {
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        double oneMinusCosAngle = 1.0 - cosAngle;

        // Normalize the rotation axis
        double axisLength = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
        if (axisLength != 0) {
            axisX /= axisLength;
            axisY /= axisLength;
            axisZ /= axisLength;
        }

        // Multiply the current matrix with the rotation matrix
        multiply(createRotationMatrix(cosAngle, sinAngle, oneMinusCosAngle, axisX, axisY, axisZ));
    }

    public Vector3 transform(Vector3 vector) {
        double x = matrix[0] * vector.x + matrix[4] * vector.y + matrix[8] * vector.z + matrix[12];
        double y = matrix[1] * vector.x + matrix[5] * vector.y + matrix[9] * vector.z + matrix[13];
        double z = matrix[2] * vector.x + matrix[6] * vector.y + matrix[10] * vector.z + matrix[14];
        double w = matrix[3] * vector.x + matrix[7] * vector.y + matrix[11] * vector.z + matrix[15];

        if (w != 0) {
            x /= w;
            y /= w;
            z /= w;
        }

        return new Vector3(x, y, z);
    }

    private Matrix4 createRotationMatrix(double cosAngle, double sinAngle, double oneMinusCosAngle,
                                         double axisX, double axisY, double axisZ) {
        Matrix4 rotationMatrix = new Matrix4();
        rotationMatrix.set(0, (oneMinusCosAngle * axisX * axisX) + cosAngle);
        rotationMatrix.set(1, (oneMinusCosAngle * axisX * axisY) - (axisZ * sinAngle));
        rotationMatrix.set(2, (oneMinusCosAngle * axisX * axisZ) + (axisY * sinAngle));

        rotationMatrix.set(4, (oneMinusCosAngle * axisX * axisY) + (axisZ * sinAngle));
        rotationMatrix.set(5, (oneMinusCosAngle * axisY * axisY) + cosAngle);
        rotationMatrix.set(6, (oneMinusCosAngle * axisY * axisZ) - (axisX * sinAngle));

        rotationMatrix.set(8, (oneMinusCosAngle * axisX * axisZ) - (axisY * sinAngle));
        rotationMatrix.set(9, (oneMinusCosAngle * axisY * axisZ) + (axisX * sinAngle));
        rotationMatrix.set(10, (oneMinusCosAngle * axisZ * axisZ) + cosAngle);

        return rotationMatrix;
    }

    public void multiply(Matrix4 other) {
        double[] result = new double[16];

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                double sum = 0.0f;
                for (int i = 0; i < 4; i++) {
                    sum += matrix[row * 4 + i] * other.get(i, col);
                }
                result[row * 4 + col] = sum;
            }
        }

        matrix = result;
    }

    public Vector3 transformDirection(Vector3 direction) {
        return new Vector3(
                matrix[0] * direction.x + matrix[1] * direction.y + matrix[2] * direction.z,
                matrix[3] * direction.x + matrix[4] * direction.y + matrix[5] * direction.z,
                matrix[6] * direction.x + matrix[7] * direction.y + matrix[8] * direction.z
        );
    }

    public static Matrix4 multiply(Matrix4 leftMatrix, Matrix4 rightMatrix) {
        Matrix4 result = new Matrix4();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                double sum = 0.0;
                for (int i = 0; i < 4; i++) {
                    sum += leftMatrix.get(row, i) * rightMatrix.get(i, col);
                }
                result.set(row, col, sum);
            }
        }

        return result;
    }

    public double get(int row, int col) {
        return matrix[row * 4 + col];
    }

    public double get(int index) {
        if (index > 15)
            throw new IllegalArgumentException("Invalid Index");
        return matrix[index];
    }

    public void set(int row, int col, double value) {
        if (row < 0 || row >= 4 || col < 0 || col >= 4) {
            throw new IllegalArgumentException("Invalid row or column index");
        }

        // Calculate the index in the 1D array
        int index = row * 4 + col;

        // Set the value at the calculated index
        matrix[index] = value;
    }

    public void set(int index, double value) {
        if (index > 15)
            throw new IllegalArgumentException("Invalid Index");
        matrix[index] = value;
    }

}
