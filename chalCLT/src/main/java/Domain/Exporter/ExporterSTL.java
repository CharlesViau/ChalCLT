package Domain.Exporter;
import Domain.Shack.Accessory.Accessory;
import Domain.Shack.Panels.Wall;
import Domain.Utility.Pair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class ExporterSTL {

    public String projectName = "Projet";
    protected void wallExporter(Wall wall) throws IOException {

    }

    protected void roofExporter() {

    }

    protected void gableExporter() throws IOException {

    }

    protected void extensionExporter() {

    }

    public String getProjectName() {
        return this.projectName;
    }

    public void addTo(ArrayList<Double> p_x, ArrayList<Double> p_y, ArrayList<Double> p_z, double a, double b, double c, double d, double e, double f, double g, double h, double i) {
        List<Double> x = Arrays.asList(a, d, g);
        List<Double> y = Arrays.asList(b, e, h);
        List<Double> z = Arrays.asList(c, f, i);
        p_x.addAll(x);
        p_y.addAll(y);
        p_z.addAll(z);
    }
    public double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix, int r1, int c1, int c2) {
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

    public ArrayList<Rectangle> getRect(ArrayList<Accessory> acc, float w, float h, float t, boolean fini, float d, boolean lr) {
        Set<Float> set_positions_y = new HashSet<>();
        for (Accessory a : acc) {

            set_positions_y.add(a.getY());
            set_positions_y.add((a.getY() + a.getHeight()));
        }

        ArrayList<Float> positions_y = new ArrayList<>(set_positions_y);
        ArrayList<Pair> rectangles_y = new ArrayList<>();
        Collections.sort(positions_y);

        if (!positions_y.isEmpty()) {
            if (positions_y.get(0) != 0)
                rectangles_y.add(new Pair(0, (positions_y.get(0))));

            for (int i = 0; i < positions_y.size(); i++) {
                if (i != positions_y.size() - 1)
                    rectangles_y.add(new Pair(positions_y.get(i), positions_y.get(i + 1)));
            }

            if (positions_y.get(positions_y.size() - 1) != h)
                rectangles_y.add(new Pair(positions_y.get(positions_y.size() - 1), h));

        }

        ArrayList<Rectangle> rectangles = new ArrayList<>();

        for (Pair p : rectangles_y) {
            ArrayList<Float> positions_x = new ArrayList<>();
            if(fini) {
                if(lr) {
                    positions_x.add(t / 2 + d/2);
                } else {
                    positions_x.add(t / 2 + d);
                }

            } else {
                positions_x.add(t / 2);
            }
            double height = p.getSecond() - p.getFirst();
            for (Accessory a : acc) {
                if (a.getY() <= p.getFirst() && a.getY() + a.getHeight() >= p.getSecond()) {
                    positions_x.add(a.getX());
                    positions_x.add(a.getX() + a.getWidth());
                }
            }

            if (positions_x.isEmpty()) {
                rectangles.add(new Rectangle(w, height, 0, p.getFirst()));
                continue;
            }
            if(fini) {
                if(lr) {
                    positions_x.add(w - t / 2 - d/2);
                } else {
                    positions_x.add(w - t / 2 - d);
                }

            } else {
                positions_x.add(w - t / 2);
            }
            Collections.sort(positions_x);

            for (int i = 0; i < positions_x.size() - 1; i += 2) {
                rectangles.add(new Rectangle(positions_x.get(i + 1) - positions_x.get(i), height, positions_x.get(i), p.getFirst()));
            }
        }
        return rectangles;
    }

    public void setProjectName(String name) {
        this.projectName = name;
    }

    public void writeSTL(BufferedWriter writer, String normal, double v1, double v2, double v3, double x1, double y1, double z1, double x2, double y2, double z2) throws IOException {
        writer.write("  facet normal " + normal + "\n");
        writer.write("    outer loop\n");
        writer.write("      vertex " + v1 + " " + v2 + " " + v3 + "\n");
        writer.write("      vertex " + x1 + " " + y1 + " " + z1 + "\n");
        writer.write("      vertex " + x2 + " " + y2 + " " + z2 + "\n");
        writer.write("    endloop\n");
        writer.write("  endfacet\n");
    }
}
