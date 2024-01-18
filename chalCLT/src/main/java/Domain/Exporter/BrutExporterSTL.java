package Domain.Exporter;

import Domain.Enum.Direction;
import Domain.Exporter.Forme.Brut.PignonBrutSTL;
import Domain.Exporter.Forme.Brut.RallongeVerticaleBrutSTL;
import Domain.Exporter.Forme.Brut.ToitBrutSTL;
import Domain.Shack.Accessory.Accessory;
import Domain.Shack.Panels.Wall;
import Domain.Shack.Shack;
import Domain.Utility.MatrixRotation;
import Domain.Utility.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.PI;

public class BrutExporterSTL extends ExporterSTL {
    public BufferedWriter createBuffer(String nameAfterProject, File file) throws IOException {
        BufferedWriter fWriter = new BufferedWriter(new FileWriter(new File(file, projectName + "_Brut_" + nameAfterProject +".stl")));
        fWriter.write("solid " + nameAfterProject + "\n");
        return fWriter;
    }
    public void roofExporter(Shack shack, File file) throws IOException {

        double widthMurParra, widthMurPerpen;
        double thickness = shack.getPanelsThickness();
        double angle = shack.getRoof().getRoofAngle();
        Direction roofdir = shack.getRoofFacingDirection();
        ToitBrutSTL toitBrut = null;

        BufferedWriter rWriter = createBuffer("T", file);



        if (roofdir == Direction.BACK || roofdir == Direction.FRONT){
            widthMurParra = shack.getExteriorWall(Direction.LEFT).getWidth();
            widthMurPerpen = shack.getExteriorWall(Direction.FRONT).getWidth();



            switch (roofdir){
                case FRONT:

                    toitBrut = new ToitBrutSTL(0,0,0,widthMurParra,widthMurPerpen,
                            thickness,Direction.FRONT,
                            angle,0,0,0);

                    break;
                case BACK:

                    toitBrut = new ToitBrutSTL(0,0,0,widthMurParra,widthMurPerpen,
                            thickness,Direction.BACK,
                            angle,0,0,0);

                    break;
            }

        }else{
            widthMurParra = shack.getExteriorWall(Direction.FRONT).getWidth();
            widthMurPerpen = shack.getExteriorWall(Direction.LEFT).getWidth();

            switch (roofdir){
                case LEFT:
                    toitBrut = new ToitBrutSTL(0,0,0,widthMurParra,widthMurPerpen,
                            thickness,Direction.LEFT,
                            angle,0,0,0);

                    break;
                case RIGHT:
                    toitBrut = new ToitBrutSTL(0,0,0,widthMurParra,widthMurPerpen,
                            thickness,Direction.RIGHT,
                            angle,0,0,0);

                    break;
            }

        }

        rWriter.write(toitBrut.writer());
        rWriter.write("endsolid T\n");
        rWriter.close();


    }

    public void gableExporter(Shack shack, File file) throws IOException {
        double widthMur;
        double thickness = shack.getPanelsThickness();
        double angle = shack.getRoof().getRoofAngle();
        double gammaDroit, gammaGauche;
        Direction roofdir = shack.getRoofFacingDirection();


        BufferedWriter PGWriter = createBuffer("PG", file);
        BufferedWriter PDWriter = createBuffer("PD", file);

        PignonBrutSTL pignonGaucheChalet;
        PignonBrutSTL pignonDroitChalet;


        switch (roofdir){
            case LEFT:
                widthMur = shack.getExteriorWall(Direction.FRONT).getWidth();
                gammaGauche = 90;
                gammaDroit = -90;

                break;
            case RIGHT:
                widthMur = shack.getExteriorWall(Direction.FRONT).getWidth();
                gammaGauche = -90;
                gammaDroit = 90;
                break;

            case BACK:
                widthMur = shack.getExteriorWall(Direction.LEFT).getWidth();
                gammaGauche = 0;
                gammaDroit = 180;
                break;

            case FRONT:
                widthMur = shack.getExteriorWall(Direction.LEFT).getWidth();
                gammaGauche = 180;
                gammaDroit = 0;

                break;

            default:
                widthMur = 0;
                gammaGauche = 0;
                gammaDroit = 0;
        }
        pignonGaucheChalet = new PignonBrutSTL(0,0,0,widthMur,
                thickness,Direction.LEFT,
                angle,0,0,gammaGauche);
        pignonDroitChalet = new PignonBrutSTL(0,0,0,widthMur,
                thickness,Direction.RIGHT,
                angle,0,0,gammaDroit);


        PGWriter.write(pignonGaucheChalet.writer());
        PDWriter.write(pignonDroitChalet.writer());

        PGWriter.write("endsolid PB\n");
        PDWriter.write("endsolid PD\n");


        PDWriter.close();
        PGWriter.close();
    }




    public void extensionExporter(Shack shack, File file) throws IOException {
        double widthMur;
        double heightMur = shack.getWallsHeight();
        double thickness = shack.getPanelsThickness();
        double angle = shack.getRoof().getRoofAngle();
        Direction roofdir = shack.getRoofFacingDirection();
        RallongeVerticaleBrutSTL rallongeSTL = null;
        BufferedWriter RWriter = createBuffer("R", file);


        if (roofdir == Direction.BACK || roofdir == Direction.FRONT){
            widthMur = shack.getExteriorWall(Direction.FRONT).getWidth();

            switch (roofdir){
                case BACK:
                    rallongeSTL = new RallongeVerticaleBrutSTL(0,0,0,widthMur,heightMur,thickness,Direction.BACK,
                            angle,0,0,0);

                    break;
                case FRONT:
                    rallongeSTL = new RallongeVerticaleBrutSTL(0,0,0,widthMur,heightMur,thickness,Direction.FRONT,
                            angle,0,0,0);

                    break;
            }

        }else{
            widthMur = shack.getExteriorWall(Direction.FRONT).getWidth();


            switch (roofdir){
                case RIGHT:
                    rallongeSTL = new RallongeVerticaleBrutSTL(0,0,0,widthMur,heightMur,thickness,Direction.RIGHT,
                            angle,0,0,0);

                    break;
                case LEFT:
                    rallongeSTL = new RallongeVerticaleBrutSTL(0,0,0,widthMur,heightMur,thickness,Direction.LEFT,
                            angle,0,0,0);

                    break;
            }
        }

        RWriter.write(rallongeSTL.writer());

        RWriter.write("endsolid R\n");


        RWriter.close();



    }



    public void convertirMursSTL(Shack shack, File file) throws IOException {

        Wall fWall = shack.getExteriorWall(Direction.FRONT);
        Wall bWall = shack.getExteriorWall(Direction.BACK);
        Wall lWall = shack.getExteriorWall(Direction.LEFT);
        Wall rWall = shack.getExteriorWall(Direction.RIGHT);

        BufferedWriter fWriter = new BufferedWriter(new FileWriter(new File(file, projectName + "_Brut_F.stl")));
        generateWallSTL(fWriter, fWall.getWidth(), fWall.getHeight(), fWall.getThickness(), 0, fWall.getWidth()/2, fWall.getAccessories(), 3*PI/2, 0);

        BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(file, projectName + "_Brut_A.stl")));
        generateWallSTL(bWriter, bWall.getWidth(), bWall.getHeight(), bWall.getThickness(), lWall.getWidth(), bWall.getWidth()/2, bWall.getAccessories(), 3*PI/2, PI);

        BufferedWriter rWriter = new BufferedWriter(new FileWriter(new File(file, projectName + "_Brut_B.stl")));
        generateWallSTL(rWriter, rWall.getWidth(), rWall.getHeight(), rWall.getThickness(), bWall.getWidth()/2 + (float)5.3, 0, rWall.getAccessories(), 3*PI/2, PI/2);

        BufferedWriter gWriter = new BufferedWriter(new FileWriter(new File(file, projectName + "_Brut_G.stl")));
        generateWallSTL(gWriter, lWall.getWidth(), lWall.getHeight(), lWall.getThickness(), bWall.getWidth()/2 + (float)5.3, lWall.getWidth(), lWall.getAccessories(), 3*PI/2, 3*PI/2);

        fWriter.close();
        bWriter.close();
        rWriter.close();
        gWriter.close();

        roofExporter(shack,file);
        gableExporter(shack,file);
        extensionExporter(shack,file);
    }

    private void generateWallSTL(BufferedWriter writer, float w, float h, float t, float z_trans, float x_trans, ArrayList<Accessory> acc, double rot_x,double rot_z) throws IOException{
        writer.write("solid Wall\n");

        ArrayList<Double> p_x = new ArrayList<>();
        ArrayList<Double> p_y = new ArrayList<>();
        ArrayList<Double> p_z = new ArrayList<>();

        //Face devant
        addTo(p_x, p_y, p_z, 0, 0, 0, w, 0, 0, 0, h, 0);
        addTo(p_x, p_y, p_z, w, h, 0, 0, h, 0, w, 0, 0);

        //Face arrière
        addTo(p_x, p_y, p_z, 0, 0, t, w, 0, t, 0, h, t);
        addTo(p_x, p_y, p_z, w, h, t, 0, h, t, w, 0, t);

        //Face gauche
        addTo(p_x, p_y, p_z, 0, 0, 0, 0, 0, t, 0, h, 0);
        addTo(p_x, p_y, p_z, 0, h, t, 0, h, 0, 0, 0, t);

        //Face droite
        addTo(p_x, p_y, p_z, w, 0, 0, w, h, 0, w, 0, t);
        addTo(p_x, p_y, p_z, w, h, t, w, 0, t, w, h, 0);

        //Face dessus
        addTo(p_x, p_y, p_z, 0, 0, 0, 0, 0, t, w, 0, 0);
        addTo(p_x, p_y, p_z, w, 0, t, w, 0, 0, 0, 0, t);

        //Face dessous
        addTo(p_x, p_y, p_z, 0, h, 0, 0, h, t, w, h, 0);
        addTo(p_x, p_y, p_z, w, h, t, w, h, 0, 0, h, t);

        double[][] vecteur = new double[3][p_x.size()];
        for(int i = 0; i <= 2; i++) {
            for(int j = 0; j < p_x.size(); j++) {
                if(i == 0) {
                    vecteur[i][j] = p_x.get(j);
                } else if(i == 1) {
                    vecteur[i][j] = p_y.get(j);
                } else {
                    vecteur[i][j] = p_z.get(j);
                }
            }
        }

        for (int i =0; i < p_x.size(); i++){
            vecteur[0][i] -= x_trans;
            vecteur[2][i]-=z_trans;
        }

        vecteur = multiplyMatrices(MatrixRotation.getMatrixRotation(rot_x, 0, rot_z), vecteur, 3, 3, p_x.size());

        for(int i = 0; i <= p_x.size() - 2; i += 3) {
            writeSTL(writer, "0 0 0", vecteur[0][i], vecteur[1][i], vecteur[2][i], vecteur[0][i+1], vecteur[1][i+1], vecteur[2][i+1], vecteur[0][i+2], vecteur[1][i+2], vecteur[2][i+2]);
        }

        writer.write("endsolid Wall\n");

    }

    //J'utilise une autre version de wallExporter mais je garde elle, au cas.
    /*public void wallExporter(Shack shack, Direction dir) throws IOException {

        Wall wall = shack.getExteriorWall(dir);
        String direction = "";
        switch(wall.getDirection()) {
            case FRONT:
                direction = "F";
                break;
            case BACK:
                direction = "A";
                break;
            case LEFT:
                direction = "G";
                break;
            case RIGHT:
                direction = "D";
                break;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(this.getProjectName() + "_Brut_" + direction));
        writer.write("solid Wall\n");

        float h = wall.getHeight();
        float w = wall.getWidth();
        float t = wall.getThickness();

        Set<Float> set_positions_y = new HashSet<>();
        for(Accessory a : wall.getAccessories()) {
            set_positions_y.add(a.getY());
            set_positions_y.add(a.getY() + a.getHeight());
        }

        ArrayList<Float> positions_y = new ArrayList<>(set_positions_y);
        Collections.sort(positions_y);

        ArrayList<Pair> rectangles_y = new ArrayList<>();
        if(!positions_y.isEmpty()) {
            if(positions_y.get(0) != 0)
                rectangles_y.add(new Pair(0, positions_y.get(0)));

            for(int i = 0; i < positions_y.size(); i++) {
                if(i != positions_y.size() - 1)
                    rectangles_y.add(new Pair(positions_y.get(i), positions_y.get(i + 1)));
            }

            if(positions_y.get(positions_y.size() - 1) != wall.getHeight())
                rectangles_y.add(new Pair(positions_y.get(positions_y.size() - 1), wall.getHeight()));

        }

        ArrayList<Rectangle> rectangles = new ArrayList<>();

        for(Pair p : rectangles_y) {
            ArrayList<Pair> x_to_disregard = new ArrayList<>();
            double height = p.getSecond() - p.getFirst();
            for(Accessory a : wall.getAccessories()) {
                if(a.getY() <= p.getFirst() && a.getY() + a.getHeight() >= p.getSecond()) {
                    x_to_disregard.add(new Pair(a.getX(), a.getX() + a.getWidth()));
                }
            }

            if(x_to_disregard.isEmpty()) {
                rectangles.add(new Rectangle(wall.getWidth(), height, 0, p.getFirst()));
                continue;
            }

            ArrayList<Float> positions_x = new ArrayList<>();
            positions_x.add(t/2);
            for(Pair x : x_to_disregard) {
                positions_x.add(x.getFirst());
                positions_x.add(x.getSecond());
            }
            positions_x.add(w - t/2);
            Collections.sort(positions_x);

            for(int i = 0; i < positions_x.size() - 1; i += 2) {
                rectangles.add(new Rectangle(positions_x.get(i + 1) - positions_x.get(i), height, positions_x.get(i), p.getFirst()));
            }
        }

        writeSTL(writer, "0 0 -1", 0, 0, 0, 0, h, 0, t/2, 0, 0);
        writeSTL(writer, "0 0 -1", t/2, h, 0, t/2, 0, 0, 0, h, 0);
        writeSTL(writer, "0 0 -1", w - t/2, 0, 0, w - t/2, h, 0, w, 0, 0);
        writeSTL(writer, "0 0 -1", w, h, 0, w, 0, 0, w - t/2, h, 0);
        for(Rectangle r : rectangles) {
            writeSTL(writer, "0 0 -1", r.getX(), r.getY(), 0, r.getX(), r.getY() + r.getH(), 0, r.getX() + r.getW(), r.getY(), 0);
            writeSTL(writer, "0 0 -1", r.getX() + r.getW(), r.getY() + r.getH(), 0, r.getX() + r.getW(), r.getY(), 0, r.getX(), r.getY() + r.getH(), 0);

            writeSTL(writer, "0 0 1", r.getX(), r.getY(), wall.getThickness(), r.getX() + r.getW(), r.getY(), wall.getThickness(), r.getX(), r.getY() + r.getH(), wall.getThickness());
            writeSTL(writer, "0 0 1", r.getX() + r.getW(), r.getY() + r.getH(), wall.getThickness(), r.getX(), r.getY() + r.getH(), wall.getThickness(), r.getX() + r.getW(), r.getY(), wall.getThickness());
        }

        for(Accessory a : wall.getAccessories()) {

            //Dessus
            writeSTL(writer, "0 1 0", a.getX(), a.getY(), 0, a.getX(), a.getY(), t, a.getX() + a.getWidth(), a.getY(), 0);
            writeSTL(writer, "0 1 0", a.getX() + a.getWidth(), a.getY(), t, a.getX() + a.getWidth(), a.getY(), 0, a.getX(), a.getY(), t);

            //Dessous
            if(a.getY() + a.getHeight() < wall.getHeight()) {
                writeSTL(writer, "0 -1 0", a.getX(), a.getY() + a.getHeight(), 0, a.getX(), a.getY() + a.getHeight(), t, a.getX() + a.getWidth(), a.getY() + a.getHeight(), 0);
                writeSTL(writer, "0 -1 0", a.getX() + a.getWidth(), a.getY() + a.getHeight(), t, a.getX() + a.getWidth(), a.getY() + a.getHeight(), 0, a.getX(), a.getY() + a.getHeight(), t);
            }

            //Droite
            writeSTL(writer, "1 0 0", a.getX(), a.getY(), 0, a.getX(), a.getY(), t, a.getX(), a.getY() + a.getHeight(), 0);
            writeSTL(writer, "1 0 0", a.getX(), a.getY() + a.getHeight(), t, a.getX(), a.getY() + a.getHeight(), 0, a.getX(), a.getY(), t);

            //Gauche
            writeSTL(writer, "-1 0 0", a.getX() + a.getWidth(), a.getY(), 0, a.getX() + a.getWidth(), a.getY(), t, a.getX() + a.getWidth(), a.getY() + a.getHeight(), 0);
            writeSTL(writer, "-1 0 0", a.getX() + a.getWidth(), a.getY() + a.getHeight(), t, a.getX() + a.getWidth(), a.getY() + a.getHeight(), 0, a.getX() + a.getWidth(), a.getY(), t);
        }

        //Face gauche
        writeSTL(writer, "-1 0 0", 0, 0, 0, 0, 0, t/2, 0, h, 0);
        writeSTL(writer, "-1 0 0", 0, h, t/2, 0, h, 0, 0, 0, t/2);

        //Face droite
        writeSTL(writer, "1 0 0", w, 0, 0, w, h, 0, w, 0, t/2);
        writeSTL(writer, "1 0 0", w, h, t/2, w, 0, t/2, w, h, 0);

        //Face dessus
        writeSTL(writer, "0 -1 0", t/2, 0, 0, w - t/2, 0, 0, t/2, 0, t);
        writeSTL(writer, "0 -1 0", w - t/2, 0, t, t/2, 0, t, w - t/2, 0, 0);
        writeSTL(writer, "0 -1 0", 0, 0, 0, t/2, 0, 0, 0, 0, t/2);
        writeSTL(writer, "0 -1 0", t/2, 0, t/2, 0, 0, t/2, t/2, 0, 0);
        writeSTL(writer, "0 -1 0", w - t/2, 0, 0, w, 0, 0, w - t/2, 0, t/2);
        writeSTL(writer, "0 -1 0", w, 0, t/2, w - t/2, 0, t/2, w, 0, 0);

        //Face dessous
        ArrayList<Float> bottom_positions_x = new ArrayList<>();
        bottom_positions_x.add(t/2);
        for(Accessory a : wall.getAccessories()) {
            if(a.getY() == h - a.getHeight()) {
                bottom_positions_x.add(a.getX());
                bottom_positions_x.add(a.getX() + a.getWidth());
            }
        }
        Collections.sort(bottom_positions_x);
        bottom_positions_x.add(w - t/2);

        if(bottom_positions_x.size() == 2) {
            writeSTL(writer, "0 1 0", 0, h, 0, 0, h, t, w, h, 0);
            writeSTL(writer, "0 1 0", w, h, t, w, h, 0, 0, h, t);
        } else {
            ArrayList<Rectangle> bottom_x_rectangles = new ArrayList<>();
            for(int i = 0; i < bottom_positions_x.size() - 1; i += 2) {
                bottom_x_rectangles.add(new Rectangle(bottom_positions_x.get(i + 1) - bottom_positions_x.get(i), t, bottom_positions_x.get(i), h));
            }

            for(Rectangle r : bottom_x_rectangles) {
                writeSTL(writer, "0 1 0", r.getX(), r.getY(), 0, r.getX(), r.getY(), t, r.getX() + r.getW(), r.getY(), 0);
                writeSTL(writer, "0 1 0", r.getX() + r.getW(), r.getY(), t, r.getX() + r.getW(), r.getY(), 0, r.getX(), r.getY(), t);
            }
        }
        writeSTL(writer, "0 1 0", 0, h, 0, 0, h, t/2, t/2, h, 0);
        writeSTL(writer, "0 1 0", t/2, h, t/2, t/2, h, 0, 0, h, t/2);
        writeSTL(writer, "0 1 0", w - t/2, h, 0, w - t/2, h, t/2, w, h, 0);
        writeSTL(writer, "0 1 0", w, h, t/2, w, h, 0, w - t/2, h, t/2);

        //Rainures
        writeSTL(writer, "0 0 1", 0, 0, t/2, t/2, 0, t/2, 0, h, t/2);
        writeSTL(writer, "0 0 1", t/2, h, t/2, 0, h, t/2, t/2, 0, t/2);

        writeSTL(writer, "0 0 1", w - t/2, 0, t/2, w, 0, t/2, w - t/2, h, t/2);
        writeSTL(writer, "0 0 1", w, h, t/2, w - t/2, h, t/2, w, 0, t/2);

        writeSTL(writer, "1 0 0", t/2, 0, t, t/2, 0, t/2, t/2, h, t);
        writeSTL(writer, "1 0 0", t/2, h, t/2, t/2, h, t, t/2, 0, t/2);

        writeSTL(writer, "-1 0 0", w - t/2, 0, t, w- t/2, h, t, w- t/2, 0, t/2);
        writeSTL(writer, "-1 0 0", w- t/2, h, t/2, w - t/2, 0, t/2, w- t/2, h, t);

        writer.write("endsolid Wall\n");

        writer.close();
    }*/

}
