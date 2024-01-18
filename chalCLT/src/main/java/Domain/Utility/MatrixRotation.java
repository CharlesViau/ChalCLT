package Domain.Utility;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MatrixRotation {
    public static double[][] getMatrixRotation(double alpha, double beta, double gamma) {
        double[][] matrix = {
                {cos(beta) * cos(gamma), sin(alpha) * sin(beta) * cos(gamma) - cos(alpha) * sin(gamma), cos(alpha) * sin(beta) * cos(gamma) + sin(alpha) * sin(gamma)},
                {cos(beta) * sin(gamma), sin(alpha) * sin(beta) * sin(gamma) + cos(alpha) * cos(gamma), cos(alpha) * sin(beta) * sin(gamma) - sin(alpha) * cos(gamma)},
                {-sin(beta), sin(alpha) * cos(beta), cos(alpha) * cos(beta)}
        };
        return matrix;
    }


}