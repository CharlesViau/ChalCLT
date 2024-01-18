package Domain.Exporter.Forme;


import Domain.Enum.Direction;

import java.io.IOException;

import static Domain.Utility.MatrixRotation.getMatrixRotation;
import static java.lang.Math.PI;

public abstract class Forme {
    protected double x;
    protected double y;
    protected double z;
    protected double width;
    protected double thickness;
    protected double height;

    protected double theta;

    protected double alpha;

    protected double beta;

    protected double gamma;

    protected Direction direction;

    protected double slotDistance;


    protected double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix, int r1, int c1, int c2) {
        double[][] product = new double[r1][c2];
        for(int i = 0; i < r1; i++) {
            for (int j = 0; j < c2; j++) {
                for (int k = 0; k < c1; k++) {
                    product[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
                }
            }
        }

        return product;
    }

    protected double[][] getVecteurDirection(double[][] vecteur, Direction direction, int point){
        switch (direction){
            case BACK:
                vecteur = multiplyMatrices(getMatrixRotation(0,0,PI),vecteur,3,3,point);
                break;
            case LEFT:
                vecteur = multiplyMatrices(getMatrixRotation(0,0,-PI/2),vecteur,3,3,point);
                break;
            case RIGHT:
                vecteur = multiplyMatrices(getMatrixRotation(0,0,PI/2),vecteur,3,3,point);
                break;
        }
        return vecteur;
    }

    public String trianglePoint(int n1, int n2, int n3, double[][] vecteur){
        String monSTL;
        monSTL="  facet normal 0 0 0\n";
        monSTL+="    outer loop\n";
        monSTL+=vertexToString(vecteur[0][n1],vecteur[1][n1],vecteur[2][n1]);
        monSTL+=vertexToString(vecteur[0][n2],vecteur[1][n2],vecteur[2][n2]);
        monSTL+=vertexToString(vecteur[0][n3],vecteur[1][n3],vecteur[2][n3]);
        monSTL+="    endloop\n";
        monSTL+="  endfacet\n";
        return  monSTL;
    }

    public String trianglePoint(int n1, int n2, int n3, double[][] vecteur, int normale1, int normale2, int normale3){
        String monSTL;
        monSTL="  facet normal " +normale1+ " " + normale2 + " " +normale3+ "\n";
        monSTL+="    outer loop\n";
        monSTL+=vertexToString(vecteur[0][n1],vecteur[1][n1],vecteur[2][n1]);
        monSTL+=vertexToString(vecteur[0][n2],vecteur[1][n2],vecteur[2][n2]);
        monSTL+=vertexToString(vecteur[0][n3],vecteur[1][n3],vecteur[2][n3]);
        monSTL+="    endloop\n";
        monSTL+="  endfacet\n";
        return  monSTL;
    }

    public abstract String writer() throws IOException;

    protected String vertexToString(double p_x,double p_y,double p_z){
        return "      vertex "+ p_x + " " + p_y + " " + p_z+ "\n";
    }

}
