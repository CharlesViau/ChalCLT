package Domain.Exporter;

import Domain.Enum.Direction;
import Domain.Exporter.Forme.Brut.PignonBrutSTL;
import Domain.Exporter.Forme.Brut.RallongeVerticaleBrutSTL;
import Domain.Exporter.Forme.Brut.ToitBrutSTL;
import Domain.Exporter.Forme.Fini.PignonDroitFiniSTL;
import Domain.Exporter.Forme.Fini.PignonGaucheFiniSTL;
import Domain.Exporter.Forme.Fini.RallongeVerticaleFiniSTL;
import Domain.Exporter.Forme.Fini.ToitFiniSTL;
import Domain.Exporter.Forme.Rainure.PrismeExtraslotExterieur;
import Domain.Exporter.Forme.Rainure.PrismeExtraslotInterieur;
import Domain.Exporter.Forme.Rainure.PrismeExtraslotMilieu;
import Domain.Shack.Accessory.Accessory;
import Domain.Shack.Panels.Wall;
import Domain.Shack.Shack;
import Domain.Utility.MatrixRotation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.*;

public class FiniExporterSTL extends ExporterSTL {

    public void convertirMursSTL(Shack shack, File file) throws IOException {

        Wall fWall = shack.getExteriorWall(Direction.FRONT);
        Wall bWall = shack.getExteriorWall(Direction.BACK);
        Wall lWall = shack.getExteriorWall(Direction.LEFT);
        Wall rWall = shack.getExteriorWall(Direction.RIGHT);
        float d = shack.getExtraSlotDistance();
        float thickness = shack.getPanelsThickness();
        Direction roof_d = shack.getRoofFacingDirection();

        BufferedWriter fWriter = new BufferedWriter(new FileWriter(new File(file, projectName + "_Fini_F.stl")));
        BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(file, projectName + "_Fini_A.stl")));
        BufferedWriter rWriter = new BufferedWriter(new FileWriter(new File(file, projectName + "_Fini_D.stl")));
        BufferedWriter gWriter = new BufferedWriter(new FileWriter(new File(file, projectName + "_Fini_G.stl")));

        if(roof_d == Direction.FRONT || roof_d == Direction.BACK) {
            generateWallSTL(fWriter, fWall.getWidth(), fWall.getHeight(), fWall.getThickness(), thickness/2, fWall.getWidth()/2, fWall.getAccessories(), 3*PI/2, 0, d, Direction.FRONT);
            generateWallSTL(bWriter, bWall.getWidth(), bWall.getHeight(), bWall.getThickness(), lWall.getWidth() + thickness/2, bWall.getWidth()/2, bWall.getAccessories(), 3*PI/2, PI, d, Direction.BACK);
            generateWallSTL(rWriter, rWall.getWidth(), rWall.getHeight(), rWall.getThickness(), bWall.getWidth()/2 , 0, rWall.getAccessories(), 3*PI/2, PI/2, d, Direction.RIGHT);
            generateWallSTL(gWriter, lWall.getWidth(), lWall.getHeight(), lWall.getThickness(), bWall.getWidth()/2  , lWall.getWidth(), lWall.getAccessories(), 3*PI/2, 3*PI/2, d, Direction.LEFT);
        } else {
            generateWallSTL(fWriter, fWall.getWidth(), fWall.getHeight(), fWall.getThickness(), 0, fWall.getWidth()/2, fWall.getAccessories(), 3*PI/2, 0, d, Direction.FRONT);
            generateWallSTL(bWriter, bWall.getWidth(), bWall.getHeight(), bWall.getThickness(), lWall.getWidth(), bWall.getWidth()/2, bWall.getAccessories(), 3*PI/2, PI, d, Direction.BACK);
            generateWallSTL(rWriter, rWall.getWidth(), rWall.getHeight(), rWall.getThickness(), bWall.getWidth()/2 +thickness/2, 0, rWall.getAccessories(), 3*PI/2, PI/2, d, Direction.RIGHT);
            generateWallSTL(gWriter, lWall.getWidth(), lWall.getHeight(), lWall.getThickness(), bWall.getWidth()/2 +thickness/2, lWall.getWidth(), lWall.getAccessories(), 3*PI/2, 3*PI/2, d, Direction.LEFT);
        }

        fWriter.close();
        bWriter.close();
        rWriter.close();
        gWriter.close();

        roofExporter(shack,file);
        gableExporter(shack,file);
        extensionExporter(shack,file);

    }

    private void generateWallSTL(BufferedWriter writer, float w, float h, float t, float z_trans, float x_trans, ArrayList<Accessory> acc, double rot_x,double rot_z, float d, Direction dir) throws IOException{
        writer.write("solid Wall\n");

        boolean dirLeftOrRight = dir == Direction.LEFT || dir == Direction.RIGHT;
        ArrayList<Rectangle> rectangles = getRect(acc, w, h, t, true, d, dirLeftOrRight);
        ArrayList<Double> p_x = new ArrayList<>();
        ArrayList<Double> p_y = new ArrayList<>();
        ArrayList<Double> p_z = new ArrayList<>();

        if(dirLeftOrRight) {
            addTo(p_x, p_y, p_z, 0, 0, 0, 0, h, 0, t/2 + d/2, 0, 0);
            addTo(p_x, p_y, p_z, t/2 + d/2, h, 0, t/2 + d/2, 0, 0, 0, h, 0);
            addTo(p_x, p_y, p_z, w - t/2 - d/2, 0, 0, w - t/2 - d/2, h, 0, w, 0, 0);
            addTo(p_x, p_y, p_z, w, h, 0, w, 0, 0, w - t/2 - d/2, h, 0);
        } else {
            addTo(p_x, p_y, p_z, d/2, 0, 0, d/2, h, 0, t/2 + d, 0, 0);
            addTo(p_x, p_y, p_z, t/2 + d, h, 0, t/2 + d, 0, 0, d/2, h, 0);
            addTo(p_x, p_y, p_z, w - t/2 - d, 0, 0, w - t/2 - d, h, 0, w - d/2, 0, 0);
            addTo(p_x, p_y, p_z, w - d/2, h, 0, w - d/2, 0, 0, w - t/2 - d, h, 0);
        }

        if(!rectangles.isEmpty()) {
            for(Rectangle r : rectangles) {
                addTo(p_x, p_y, p_z, r.getX(), r.getY(), 0, r.getX(), r.getY() + r.getH(), 0, r.getX() + r.getW(), r.getY(), 0);
                addTo(p_x, p_y, p_z, r.getX() + r.getW(), r.getY() + r.getH(), 0, r.getX() + r.getW(), r.getY(), 0, r.getX(), r.getY() + r.getH(), 0);
                addTo(p_x, p_y, p_z, r.getX(), r.getY(), t, r.getX() + r.getW(), r.getY(), t, r.getX(), r.getY() + r.getH(), t);
                addTo(p_x, p_y, p_z, r.getX() + r.getW(), r.getY() + r.getH(), t, r.getX(), r.getY() + r.getH(), t, r.getX() + r.getW(), r.getY(), t);

            }
        } else {
            if(dirLeftOrRight) {
                addTo(p_x, p_y, p_z, t/2 + d/2, 0, 0, w-t/2-d/2, 0, 0, t/2 + d/2, h, 0);
                addTo(p_x, p_y, p_z, w-t/2-d/2, h, 0, t/2+d/2, h, 0, w-t/2-d/2, 0, 0);
                addTo(p_x, p_y, p_z, t/2+d/2, 0, t, w-t/2-d/2, 0, t, t/2+d/2, h, t);
                addTo(p_x, p_y, p_z, w-t/2-d/2, h, t, t/2+d/2, h, t, w-t/2-d/2, 0, t);
            } else {
                addTo(p_x, p_y, p_z, t/2 + d, 0, 0, w-t/2-d, 0, 0, t/2 + d, h, 0);
                addTo(p_x, p_y, p_z, w-t/2-d, h, 0, t/2+d, h, 0, w-t/2-d, 0, 0);
                addTo(p_x, p_y, p_z, t/2+d, 0, t, w-t/2-d, 0, t, t/2+d, h, t);
                addTo(p_x, p_y, p_z, w-t/2-d, h, t, t/2+d, h, t, w-t/2-d, 0, t);
            }
        }


        for(Accessory a : acc) {

            //if(!a.isValid()) continue;

            float a_w = a.getWidth();
            float a_h = a.getHeight();


            //Dessus
            addTo(p_x, p_y, p_z, a.getX(), a.getY(), 0, a.getX(), a.getY(), t, a.getX() + a_w, a.getY(), 0);
            addTo(p_x, p_y, p_z, a.getX() + a_w, a.getY(), t, a.getX() + a_w, a.getY(), 0, a.getX(), a.getY(), t);

            //Dessous
            if(a.getY() + a_h < h) {
                addTo(p_x, p_y, p_z, a.getX(), a.getY() + a_h, 0, a.getX(), a.getY() + a_h, t, a.getX() + a_w, a.getY() + a_h, 0);
                addTo(p_x, p_y, p_z, a.getX() + a_w, a.getY() + a_h, t, a.getX() + a_w, a.getY() + a_h, 0, a.getX(), a.getY() + a_h, t);
            }

            //Droite
            addTo(p_x, p_y, p_z, a.getX(), a.getY(), 0, a.getX(), a.getY(), t, a.getX(), a.getY() + a_h, 0);
            addTo(p_x, p_y, p_z, a.getX(), a.getY() + a_h, t, a.getX(), a.getY() + a_h, 0, a.getX(), a.getY(), t);

            //Gauche
            addTo(p_x, p_y, p_z, a.getX() + a_w, a.getY(), 0, a.getX() + a_w, a.getY(), t, a.getX() + a_w, a.getY() + a_h, 0);
            addTo(p_x, p_y, p_z, a.getX() + a_w, a.getY() + a_h, t, a.getX() + a_w, a.getY() + a_h, 0, a.getX() + a_w, a.getY(), t);

        }
        //Face gauche
        if(dir == Direction.LEFT || dir == Direction.RIGHT) {
            addTo(p_x, p_y, p_z, 0, 0, 0, 0, 0, t/2 - d/2, 0, h, 0);
            addTo(p_x, p_y, p_z, 0, h, t/2 - d/2, 0, h, 0, 0, 0, t/2 - d/2);
        } else {
            addTo(p_x, p_y, p_z, d/2, 0, 0, d/2, 0, t/2 - d/2, d/2, h, 0);
            addTo(p_x, p_y, p_z, d/2, h, t/2 - d/2, d/2, h, 0, d/2, 0, t/2 - d/2);
        }


        //Face droite
        if(dir == Direction.LEFT || dir == Direction.RIGHT) {
            addTo(p_x, p_y, p_z, w, 0, 0, w, h, 0, w, 0, t/2 - d/2);
            addTo(p_x, p_y, p_z, w, h, t/2 - d/2, w, 0, t/2 - d/2, w, h, 0);
        } else {
            addTo(p_x, p_y, p_z, w - d/2, 0, 0, w - d/2, h, 0, w - d/2, 0, t/2 - d/2);
            addTo(p_x, p_y, p_z, w - d/2, h, t/2 - d/2, w - d/2, 0, t/2 - d/2, w - d/2, h, 0);
        }

        //Face dessus
        if(dir == Direction.LEFT || dir == Direction.RIGHT) {
            addTo(p_x, p_y, p_z, t/2 + d/2, 0, 0, w - t/2 - d/2, 0, 0, t/2 + d/2, 0, t);
            addTo(p_x, p_y, p_z, w - t/2 - d/2, 0, t, t/2 + d/2, 0, t, w - t/2 - d/2, 0, 0);

            addTo(p_x, p_y, p_z, t/2, 0, 0, t/2 + d/2, 0, 0, t/2, 0, t/2 - d/2);
            addTo(p_x, p_y, p_z, t/2 + d/2, 0, t/2 - d/2, t/2, 0, t/2 - d/2, t/2 + d/2, 0, 0);
            addTo(p_x, p_y, p_z, w - t/2 - d/2, 0, 0, w - t/2 + d/2, 0, 0, w - t/2 - d/2, 0, t/2 - d/2);
            addTo(p_x, p_y, p_z, w - t/2, 0, t/2 - d/2, w - t/2 - d/2, 0, t/2 - d/2, w - t/2 + d/2, 0, 0);

            addTo(p_x, p_y, p_z, 0, 0, 0, t/2, 0, 0, 0, 0, t/2 - d/2);
            addTo(p_x, p_y, p_z, t/2, 0, t/2 - d/2, 0, 0, t/2 - d/2, t/2, 0, 0);
            addTo(p_x, p_y, p_z, w - t/2, 0, 0, w, 0, 0, w - t/2, 0, t/2 - d/2);
            addTo(p_x, p_y, p_z, w, 0, t/2 - d/2, w - t/2, 0, t/2 - d/2, w, 0, 0);
        } else {
            addTo(p_x, p_y, p_z, t/2 + d, 0, 0, w - t/2 - d, 0, 0, t/2 + d, 0, t);
            addTo(p_x, p_y, p_z, w - t/2 - d, 0, t, t/2 + d, 0, t, w - t/2 - d, 0, 0);

            addTo(p_x, p_y, p_z, d/2, 0, 0, t/2 + d, 0, 0, d/2, 0, t/2 - d/2);
            addTo(p_x, p_y, p_z, t/2 + d, 0, t/2 - d/2, d/2, 0, t/2 - d/2, t/2 + d, 0, 0);
            addTo(p_x, p_y, p_z, w - t/2 - d, 0, 0, w - d/2, 0, 0, w - t/2 - d, 0, t/2 - d/2);
            addTo(p_x, p_y, p_z, w - d/2, 0, t/2 - d/2, w - t/2 - d, 0, t/2 - d/2, w - d/2, 0, 0);
        }

        //Face dessous
        ArrayList<Float> bottom_positions_x = new ArrayList<>();
        if(dir == Direction.LEFT || dir == Direction.RIGHT) {
            bottom_positions_x.add(t/2 + d/2);

            addTo(p_x, p_y, p_z, t/2, h, 0, t/2 + d/2, h, 0, t/2, h, t/2 - d/2);
            addTo(p_x, p_y, p_z, t/2 + d/2, h, t/2 - d/2, t/2, h, t/2 - d/2, t/2 + d/2, h, 0);
            addTo(p_x, p_y, p_z, w - t/2 - d/2, h, 0, w - t/2 + d/2, h, 0, w - t/2 - d/2, h, t/2 - d/2);
            addTo(p_x, p_y, p_z, w - t/2, h, t/2 - d/2, w - t/2 - d/2, h, t/2 - d/2, w - t/2 + d/2, h, 0);

            addTo(p_x, p_y, p_z, 0, h, 0, t/2, h, 0, 0, h, t/2 - d/2);
            addTo(p_x, p_y, p_z, t/2, h, t/2 - d/2, 0, h, t/2 - d/2, t/2, h, 0);
            addTo(p_x, p_y, p_z, w - t/2, h, 0, w, h, 0, w - t/2, h, t/2 - d/2);
            addTo(p_x, p_y, p_z, w, h, t/2 - d/2, w - t/2, h, t/2 - d/2, w, h, 0);
        } else {
            bottom_positions_x.add(t/2 + d);
        }
        for(Accessory a : acc) {
            if(a.getY() == h - a.getHeight()) {
                bottom_positions_x.add(a.getX());
                bottom_positions_x.add(a.getX() + a.getWidth());
            }
        }
        Collections.sort(bottom_positions_x);

        if(dir == Direction.LEFT || dir == Direction.RIGHT) {
            bottom_positions_x.add(w - t/2 - d/2);
        } else {
            bottom_positions_x.add(w - t/2 - d);
        }

        if(dirLeftOrRight) {
            if(bottom_positions_x.size() == 2) {
                addTo(p_x, p_y, p_z, t/2 + d/2, h, 0, w-t/2-d/2, h, 0, t/2+d/2, h, t);
                addTo(p_x, p_y, p_z, w-t/2-d/2, h, t, t/2+d/2, h, t, w-t/2-d/2, h, 0);

            } else {
                ArrayList<Rectangle> bottom_x_rectangles = new ArrayList<>();
                for(int i = 0; i < bottom_positions_x.size() - 1; i += 2) {
                    bottom_x_rectangles.add(new Rectangle(bottom_positions_x.get(i + 1) - bottom_positions_x.get(i), t, bottom_positions_x.get(i), h));
                }

                for(Rectangle r : bottom_x_rectangles) {
                    addTo(p_x, p_y, p_z, r.getX(), r.getY(), 0, r.getX(), r.getY(), t, r.getX() + r.getW(), r.getY(), 0);
                    addTo(p_x, p_y, p_z, r.getX() + r.getW(), r.getY(), t, r.getX() + r.getW(), r.getY(), 0, r.getX(), r.getY(), t);
                }
            }
        } else {
            if(bottom_positions_x.size() == 2) {
                addTo(p_x, p_y, p_z, t/2 + d, h, 0, w-t/2-d, h, 0, t/2+d, h, t);
                addTo(p_x, p_y, p_z, w-t/2-d, h, t, t/2+d, h, t, w-t/2-d, h, 0);

            } else {
                ArrayList<Rectangle> bottom_x_rectangles = new ArrayList<>();
                for(int i = 0; i < bottom_positions_x.size() - 1; i += 2) {
                    bottom_x_rectangles.add(new Rectangle(bottom_positions_x.get(i + 1) - bottom_positions_x.get(i), t, bottom_positions_x.get(i), h));
                }

                for(Rectangle r : bottom_x_rectangles) {
                    addTo(p_x, p_y, p_z, r.getX(), r.getY(), 0, r.getX(), r.getY(), t, r.getX() + r.getW(), r.getY(), 0);
                    addTo(p_x, p_y, p_z, r.getX() + r.getW(), r.getY(), t, r.getX() + r.getW(), r.getY(), 0, r.getX(), r.getY(), t);
                }
            }
        }

        if(dir == Direction.LEFT || dir == Direction.RIGHT) {
            addTo(p_x, p_y, p_z, 0, h, 0, 0, h, t/2 - d/2, t/2, h, 0);
            addTo(p_x, p_y, p_z, t/2, h, t/2 - d/2, t/2, h, 0, 0, h, t/2 - d/2);
            addTo(p_x, p_y, p_z, w - t/2, h, 0, w - t/2, h, t/2 - d/2, w, h, 0);
            addTo(p_x, p_y, p_z, w, h, t/2 - d/2, w, h, 0, w - t/2, h, t/2 - d);
        } else {
            addTo(p_x, p_y, p_z, d/2, h, 0, d/2, h, t/2 - d/2, t/2 + d, h, 0);
            addTo(p_x, p_y, p_z, t/2 + d, h, t/2 - d/2, t/2 + d, h, 0, d/2, h, t/2 - d/2);
            addTo(p_x, p_y, p_z, w - t/2 - d, h, 0, w - t/2 - d, h, t/2 - d/2, w - d/2, h, 0);
            addTo(p_x, p_y, p_z, w - d/2, h, t/2 - d/2, w - d/2, h, 0, w - t/2 - d, h, t/2 - d/2);
        }

        //Rainures
        if(dir == Direction.LEFT || dir == Direction.RIGHT) {
            addTo(p_x, p_y, p_z, 0, 0, t/2 - d/2, t/2, 0, t/2 - d/2, 0, h, t/2 - d/2);
            addTo(p_x, p_y, p_z, t/2, h, t/2 - d/2, 0, h, t/2 - d/2, t/2, 0, t/2 - d/2);

            addTo(p_x, p_y, p_z, w - t/2, 0, t/2 - d/2, w, 0, t/2 - d/2, w - t/2, h, t/2 - d/2);
            addTo(p_x, p_y, p_z, w, h, t/2 - d/2, w - t/2, h, t/2 - d/2, w, 0, t/2 - d/2);

            addTo(p_x, p_y, p_z, t/2 + d/2, 0, t, t/2 + d/2, 0, t/2 - d/2, t/2 + d/2, h, t);
            addTo(p_x, p_y, p_z, t/2 + d/2, h, t/2 - d/2, t/2 + d/2, h, t, t/2 + d/2, 0, t/2 - d/2);

            addTo(p_x, p_y, p_z, w - t/2 - d/2, 0, t, w - t/2 - d/2, h, t, w - t/2 - d/2, 0, t/2 - d/2);
            addTo(p_x, p_y, p_z, w - t/2 - d/2, h, t/2 - d/2, w - t/2 - d/2, 0, t/2 - d/2, w - t/2 - d/2, h, t);
        } else {
            addTo(p_x, p_y, p_z, d/2, 0, t/2 - d/2, t/2 + d, 0, t/2 - d/2, d/2, h, t/2 - d/2);
            addTo(p_x, p_y, p_z, t/2 + d, h, t/2 - d/2, d/2, h, t/2 - d/2, t/2 + d, 0, t/2 - d/2);

            addTo(p_x, p_y, p_z, w - t/2 - d, 0, t/2 - d/2, w - d/2, 0, t/2 - d/2, w - t/2 - d, h, t/2 - d/2);
            addTo(p_x, p_y, p_z, w - d/2, h, t/2 - d/2, w - t/2 - d, h, t/2 - d/2, w - d/2, 0, t/2 - d/2);

            addTo(p_x, p_y, p_z, t/2 + d, 0, t, t/2 + d, 0, t/2 - d/2, t/2 + d, h, t);
            addTo(p_x, p_y, p_z, t/2 + d, h, t/2 - d/2, t/2 + d, h, t, t/2 + d, 0, t/2 - d/2);

            addTo(p_x, p_y, p_z, w - t/2 - d, 0, t, w - t/2 - d, h, t, w - t/2 - d, 0, t/2 - d/2);
            addTo(p_x, p_y, p_z, w- t/2 - d, h, t/2 - d/2, w - t/2 - d, 0, t/2 - d/2, w - t/2 - d, h, t);
        }

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

    public void roofExporter(Shack shack, File file) throws IOException {
        double widthMurParra = 0, widthMurPerpen = 0;
        double thickness = shack.getPanelsThickness();
        double angle = shack.getRoof().getRoofAngle();
        double slotDistance = shack.getExtraSlotDistance();
        Direction roofdir = shack.getRoofFacingDirection();
        ToitFiniSTL toitFini = null;
        double x = 0;
        double y = 0;
        double z = 0;
        double gamma =0;

        BufferedWriter rWriter = createBuffer("T", file);

        Wall murGauche = shack.getExteriorWall(Direction.LEFT);
        Wall murDroit = shack.getExteriorWall(Direction.RIGHT);
        Wall murFront = shack.getExteriorWall(Direction.FRONT);
        Wall murBack = shack.getExteriorWall(Direction.BACK);

        switch (roofdir){
            case FRONT:
            case BACK:
                widthMurParra = shack.getExteriorWall(Direction.LEFT).getWidth();
                widthMurPerpen = shack.getExteriorWall(Direction.FRONT).getWidth();
                switch (roofdir){
                    case FRONT:
                        x=-murFront.getWidth()/2;
                        y=-thickness/2;
                        gamma=-90;
                        break;
                    case BACK:
                        x=murFront.getWidth()/2;
                        y=murGauche.getWidth()+thickness/2;
                        gamma=90;
                        break;
                }
                break;
            case LEFT:
            case RIGHT:
                widthMurParra = shack.getExteriorWall(Direction.FRONT).getWidth();
                widthMurPerpen = shack.getExteriorWall(Direction.LEFT).getWidth();
                switch (roofdir){
                    case LEFT:
                        x=-murFront.getWidth()/2-thickness/2;
                        y=murGauche.getWidth();
                        gamma=180;
                        break;
                    case RIGHT:
                        x=murFront.getWidth()/2+thickness/2;
                        y=0;
                        gamma=0;
                    break;
                }
                break;
        }
        
        toitFini = new ToitFiniSTL(x,y,z,widthMurParra,widthMurPerpen,
                thickness,roofdir,
                angle,0,0,gamma);


        rWriter.write(toitFini.writer());
        rWriter.write("endsolid T\n");
        rWriter.close();

    }
    public BufferedWriter createBuffer(String nameAfterProject, File file) throws IOException {
        BufferedWriter fWriter = new BufferedWriter(new FileWriter(new File(file, projectName + "_Fini_" + nameAfterProject +".stl")));
        fWriter.write("solid " + nameAfterProject + "\n");
        return fWriter;
    }
    public void gableExporter(Shack shack, File file) throws IOException {
        double widthMur = 0;
        double thickness = shack.getPanelsThickness();
        double angle = shack.getRoof().getRoofAngle();
        double slotDistance = shack.getExtraSlotDistance();
        Direction roofdir = shack.getRoofFacingDirection();
        double XHautDroit = 0;
        double XBasGauche = 0;
        double yHautDroit = 0;
        double yBasGauche = 0;
        double z = 0;
        double gamma = 0;

        BufferedWriter PGWriter = createBuffer("PG", file);
        BufferedWriter PDWriter = createBuffer("PD", file);

        PignonGaucheFiniSTL pignonGaucheChalet;
        PignonDroitFiniSTL pignonDroitChalet;
        Wall murGauche = shack.getExteriorWall(Direction.LEFT);
        Wall murDroit = shack.getExteriorWall(Direction.RIGHT);
        Wall murFront = shack.getExteriorWall(Direction.FRONT);
        Wall murBack = shack.getExteriorWall(Direction.BACK);

        switch (roofdir){
            case FRONT:
                widthMur = murGauche.getWidth();
                gamma = -90;
                XHautDroit= -murFront.getWidth()/2 ;
                XBasGauche= murFront.getWidth()/2- thickness/2;
                break;
            case BACK:
                widthMur = murGauche.getWidth();
                gamma = 90;
                XHautDroit=murFront.getWidth()/2;
                XBasGauche=-murFront.getWidth()/2 + thickness/2;
                yHautDroit=murGauche.getWidth();
                yBasGauche=murDroit.getWidth();
                break;
            case LEFT:
                widthMur = murBack.getWidth();
                gamma = 180;
                XHautDroit=-murFront.getWidth()/2;
                yHautDroit=murGauche.getWidth();
                XBasGauche=-murFront.getWidth()/2;
                yBasGauche=thickness/2;
                break;
            case RIGHT:

                widthMur = murBack.getWidth();
                gamma = 0;
                XHautDroit=murFront.getWidth()/2;
                yHautDroit=0;
                XBasGauche=murFront.getWidth()/2;
                yBasGauche=murGauche.getWidth()-thickness/2;
                break;
        }




        pignonGaucheChalet = new PignonGaucheFiniSTL(XHautDroit,yHautDroit,z,widthMur,
                thickness,Direction.LEFT,
                angle,0,0,gamma);
        pignonDroitChalet = new PignonDroitFiniSTL(yBasGauche,-XBasGauche,z,widthMur,
                thickness,Direction.RIGHT,
                angle,0,0,gamma-90);


        PGWriter.write(pignonGaucheChalet.writer());
        PDWriter.write(pignonDroitChalet.writer());

        PGWriter.write("endsolid PB\n");
        PDWriter.write("endsolid PD\n");


        PDWriter.close();
        PGWriter.close();
    }

    public void extensionExporter(Shack shack, File file) throws IOException {
        double widthMur = 0;
        double heightMur = 0;
        double thickness = shack.getPanelsThickness();
        double angle = shack.getRoof().getRoofAngle();
        double gamma = 0;
        double x = 0;
        double y = 0;
        double z = 0;
        Direction roofdir = shack.getRoofFacingDirection();

        Wall murFront = shack.getExteriorWall(Direction.FRONT);
        Wall murBack = shack.getExteriorWall(Direction.BACK);
        Wall murRight = shack.getExteriorWall(Direction.RIGHT);
        Wall murGauche = shack.getExteriorWall(Direction.LEFT);


        BufferedWriter RWriter = createBuffer("R", file);

        RallongeVerticaleFiniSTL rallongeSTL = null;
        switch (roofdir){
            case FRONT:
            case BACK:
                widthMur = murFront.getWidth();
                heightMur =  murGauche.getWidth()*tan(toRadians(angle))-tan(toRadians(angle))*thickness/2 ;
                switch (roofdir){
                    case FRONT:
                        x = murBack.getWidth()/2;
                        y = murRight.getWidth() + thickness/2;
                        gamma = 180;
                        break;
                    case BACK:
                        x = -murFront.getWidth()/2;
                        y = -thickness/2;
                        gamma = 0;
                        break;
                }
                break;
            case RIGHT:
            case LEFT:
                widthMur = murGauche.getWidth();
                heightMur = murFront.getWidth()*tan(toRadians(angle))-tan(toRadians(angle))*thickness/2;
                switch (roofdir){
                    case LEFT:
                        x = -murFront.getWidth()/2-thickness/2;
                        y = murRight.getWidth();
                        gamma = 90;
                        break;
                    case RIGHT:
                        x = murGauche.getWidth()/2 + thickness/2;
                        y = 0;
                        gamma = -90;
                        break;
                }
                break;
        }
           

        rallongeSTL = new RallongeVerticaleFiniSTL(-x,y,z,widthMur,heightMur,thickness,roofdir,
                angle,0,0,gamma);

        

        RWriter.write(rallongeSTL.writer());
        RWriter.write("endsolid R\n");
        RWriter.close();
    }

}
