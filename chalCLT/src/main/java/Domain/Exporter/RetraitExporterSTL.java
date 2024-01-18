package Domain.Exporter;

import Domain.Enum.Direction;
import Domain.Exporter.Forme.Rainure.*;
import Domain.Shack.Accessory.Accessory;
import Domain.Shack.Panels.Wall;
import Domain.Shack.Shack;
import Domain.Utility.MatrixRotation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

public class RetraitExporterSTL extends ExporterSTL {

    public RetraitExporterSTL() {

    }

    public void convertRetraitSTL(Shack shack, File file) throws IOException {
        for (Direction direction : Direction.values()) {
            Wall wall = shack.getExteriorWall(direction);
            generateRetraitSTL(wall, file, shack);
        }
        generateRainureGrenierSTL(file,shack);
        convertirRetrait(shack,file);
    }

    private String dirToLetter(Direction direction) {
        switch (direction) {
            case FRONT :
                return "F_";
            case BACK :
                return "B_";
            case LEFT :
                return "L_";
            case RIGHT :
                return "R_";
            default :
                return "F_";
        }
    }

    private void writeToStlFile(BufferedWriter bufferedWriter, ArrayList<Double> p_x, ArrayList<Double> p_y, ArrayList<Double> p_z) throws IOException {
        bufferedWriter.write("solid Accessory\n");

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

        vecteur = multiplyMatrices(MatrixRotation.getMatrixRotation(PI/2, 0, 0), vecteur, 3, 3, p_x.size());

        for(int i = 0; i <= p_x.size() - 2; i += 3) {
            writeSTL(bufferedWriter, "0 0 0", vecteur[0][i], vecteur[1][i], vecteur[2][i], vecteur[0][i+1], vecteur[1][i+1], vecteur[2][i+1], vecteur[0][i+2], vecteur[1][i+2], vecteur[2][i+2]);
        }

        bufferedWriter.write("endsolid Accessory\n");
        bufferedWriter.close();
    }

    private Map<String, ArrayList<Double>> writeSTLPrism(BufferedWriter bufferedWriter, float x, float y, float width, float height, float thickness) throws IOException {
        Map<String, ArrayList<Double>> arraysMap = new HashMap<>();

        ArrayList<Double> p_x = new ArrayList<>();
        ArrayList<Double> p_y = new ArrayList<>();
        ArrayList<Double> p_z = new ArrayList<>();

        //Triangles Devant
        addTo(p_x, p_y, p_z, x, y, 0, x, y + height, 0, x + width, y, 0);
        addTo(p_x, p_y, p_z, x + width, y + height, 0, x + width, y, 0, x, y + height, 0);

        //Triangles Derriere
        addTo(p_x, p_y, p_z, x + width, y, thickness, x + width, y + height, thickness, x, y, thickness);
        addTo(p_x, p_y, p_z, x, y + height, thickness, x, y, thickness, x + width, y + height, thickness);

        //Triangles Gauche
        addTo(p_x, p_y, p_z, x, y, thickness, x, y + height, thickness, x, y, 0);
        addTo(p_x, p_y, p_z, x, y + height, 0, x, y, 0, x, y + height, thickness);

        //Triangles Droite
        addTo(p_x, p_y, p_z, x + width, y, 0, x + width, y + height, 0, x + width, y, thickness);
        addTo(p_x, p_y, p_z, x + width, y + height, thickness, x + width, y, thickness, x + width, y + height, 0);

        //Triangles Haut
        addTo(p_x, p_y, p_z, x, y, thickness, x, y, 0, x + width, y, thickness);
        addTo(p_x, p_y, p_z, x + width, y, 0, x + width, y, thickness, x, y, 0);

        //Triangles Bas
        addTo(p_x, p_y, p_z, x, y + height, 0, x, y + height, thickness, x + width, y + height, 0);
        addTo(p_x, p_y, p_z, x + width, y + height, thickness, x + width, y + height, 0, x, y + height, thickness);

        arraysMap.put("p_x", p_x);
        arraysMap.put("p_y", p_y);
        arraysMap.put("p_z", p_z);

        return arraysMap;
    }

    private void generateAccessoriesSTL(Wall wall, File file, int accessoryCount) throws IOException {
        final String EXTENSION = ".stl";
        final String TYPE = "_retrait_";
        float wallThickness = wall.getThickness();
        for (Accessory e: wall.getAccessories()) {
            final float xPos = (float) e.getPosition().getX();
            final float yPos = (float) e.getPosition().getY();
            final float accHeight = e.getHeight();
            final float accWidth = e.getWidth();
            final String DIRECTION = dirToLetter(wall.getDirection());
            final String fileName = projectName + TYPE + DIRECTION + accessoryCount + EXTENSION;

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(file, fileName)));

            Map<String, ArrayList<Double>> coordMap = writeSTLPrism(bufferedWriter, xPos, yPos, accWidth, accHeight, wallThickness);
            ArrayList<Double> p_x = coordMap.get("p_x");
            ArrayList<Double> p_y = coordMap.get("p_y");
            ArrayList<Double> p_z = coordMap.get("p_z");

//            //Triangles Devant
//            addTo(p_x, p_y, p_z, xPos, yPos, 0, xPos, yPos + accHeight, 0, xPos + accWidth, yPos, 0);
//            addTo(p_x, p_y, p_z, xPos + accWidth, yPos + accHeight, 0, xPos + accWidth, yPos, 0, xPos, yPos + accHeight, 0);
//
//            //Triangles Derriere
//            addTo(p_x, p_y, p_z, xPos + accWidth, yPos, wallThickness, xPos + accWidth, yPos + accHeight, wallThickness, xPos, yPos, wallThickness);
//            addTo(p_x, p_y, p_z, xPos, yPos + accHeight, wallThickness, xPos, yPos, wallThickness, xPos + accWidth, yPos + accHeight, wallThickness);
//
//            //Triangles Gauche
//            addTo(p_x, p_y, p_z, xPos, yPos, wallThickness, xPos, yPos + accHeight, wallThickness, xPos, yPos, 0);
//            addTo(p_x, p_y, p_z, xPos, yPos + accHeight, 0, xPos, yPos, 0, xPos, yPos + accHeight, wallThickness);
//
//            //Triangles Droite
//            addTo(p_x, p_y, p_z, xPos + accWidth, yPos, 0, xPos + accWidth, yPos + accHeight, 0, xPos + accWidth, yPos, wallThickness);
//            addTo(p_x, p_y, p_z, xPos + accWidth, yPos + accHeight, wallThickness, xPos + accWidth, yPos, wallThickness, xPos + accWidth, yPos + accHeight, 0);
//
//            //Triangles Haut
//            addTo(p_x, p_y, p_z, xPos, yPos, wallThickness, xPos, yPos, 0, xPos + accWidth, yPos, wallThickness);
//            addTo(p_x, p_y, p_z, xPos + accWidth, yPos, 0, xPos + accWidth, yPos, wallThickness, xPos, yPos, 0);
//
//            //Triangles Bas
//            addTo(p_x, p_y, p_z, xPos, yPos + accHeight, 0, xPos, yPos + accHeight, wallThickness, xPos + accWidth, yPos + accHeight, 0);
//            addTo(p_x, p_y, p_z, xPos + accWidth, yPos + accHeight, wallThickness, xPos + accWidth, yPos + accHeight, 0, xPos, yPos + accHeight, wallThickness);
//
            writeToStlFile(bufferedWriter, p_x, p_y, p_z);

        }
    }

    //Genère le retrait pour les murs alignés avec le toit (mur le plus large)
    private void generateReinureLargestWall(Wall wall, File file, Shack shack, int retraitNbr) throws IOException {
        final float wallHeight = wall.getHeight();
        final float retraitWidth = wall.getThickness()/2;
        final float xPos = (float) wall.getPosition().getX();
        final float yPos = (float) wall.getPosition().getY();

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

        Map<String, ArrayList<Double>> coordMap = writeSTLPrism(bufferedWriter, xPos, yPos, retraitWidth, wallHeight, retraitWidth);
        ArrayList<Double> p_x = coordMap.get("p_x");
        ArrayList<Double> p_y = coordMap.get("p_y");
        ArrayList<Double> p_z = coordMap.get("p_z");

        writeToStlFile(bufferedWriter, p_x, p_y, p_z);
    }

    //Genère le retrait pour les murs non allignés avec le toit (mur les plus petits)
    private void generateReinureSmallestWall(Wall wall, File file, Shack shack, int retraitNbr, String side) throws IOException {
        final float wallHeight = wall.getHeight();
        final float retraitWidth = wall.getThickness()/2;
        final float xPos = (float) wall.getPosition().getX();
        final float yPos = (float) wall.getPosition().getY();
        float smallPartxPos = xPos;
        float smallPartThickness = wall.getThickness()/4;

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

        Map<String, ArrayList<Double>> coordMap = writeSTLPrism(bufferedWriter, xPos, yPos, retraitWidth, wallHeight, retraitWidth);
        ArrayList<Double> p_x = coordMap.get("p_x");
        ArrayList<Double> p_y = coordMap.get("p_y");
        ArrayList<Double> p_z = coordMap.get("p_z");

        if (side == "left") {
            smallPartxPos -= retraitWidth;
            coordMap = writeSTLPrism(bufferedWriter, smallPartxPos, yPos, retraitWidth, wallHeight, smallPartThickness);
            p_x.addAll(coordMap.get("p_x"));
            p_y.addAll(coordMap.get("p_y"));
            p_z.addAll(coordMap.get("p_z"));
        }
        else if (side == "right") {
            smallPartxPos += retraitWidth;
            coordMap = writeSTLPrism(bufferedWriter, smallPartxPos, yPos, retraitWidth, wallHeight, smallPartThickness);
            p_x.addAll(coordMap.get("p_x"));
            p_y.addAll(coordMap.get("p_y"));
            p_z.addAll(coordMap.get("p_z"));
        }

        writeToStlFile(bufferedWriter, p_x, p_y, p_z);
    }

    private void generateReinuresSTL(Wall wall, File file, Shack shack, int retraitNbr) throws IOException {
        final String EXTENSION = ".stl";
        final String TYPE = "_retrait_";
        final String DIRECTION = dirToLetter(wall.getDirection());

        retraitNbr++;

        final String fileName = projectName + TYPE + DIRECTION + retraitNbr + EXTENSION;

        File stlFile = new File(file, fileName);

        if (wall.getDirection() == shack.getRoof().getDirection() || wall.getDirection() == shack.getRoof().getOppositeDirection()) {
            generateReinureLargestWall(wall, stlFile, shack, retraitNbr);
            generateReinureLargestWall(wall, stlFile, shack, retraitNbr);
        } else {
            generateReinureSmallestWall(wall, stlFile, shack, retraitNbr, "left");
            generateReinureSmallestWall(wall, stlFile, shack, retraitNbr, "right");
        }

    }
    private void generateRainureGrenierSTL(File file,Shack shack) throws IOException {
        generateRainureToitSTL(file,shack);
        generateRainureRallongeSTL(file,shack);
        generateLesRainuresPignonsSTL(file,shack);
    }

    private  void generateLesRainuresPignonsSTL(File file, Shack shack) throws IOException {
        final String EXTENSION = ".stl";
        final String TYPE = "_retrait_";
        String TYPEPANEAU;
        PignonRetrait retrait = null;
        String fileName;

        for (int i = 0; i < 2 ; i++){
            if (i == 0) {
                TYPEPANEAU = "PD_";
            }else{
                TYPEPANEAU = "PG_";
                }
            fileName = projectName + TYPE + TYPEPANEAU + "1" + EXTENSION;
            retrait = new PignonRetrait(0,0,0,
                    shack.getExteriorWall(Direction.BACK).getWidth()
                    ,shack.getPanelsThickness(),shack.getRoofFacingDirection(),shack.getRoof().getRoofAngle(),0,0,0);



            BufferedWriter buffered = new BufferedWriter(new FileWriter(new File(file, fileName)));
            buffered.write("solid Toit\n");

            buffered.write(retrait.writer());

            buffered.write("endsolid Toit\n");
            buffered.close();
        }


    }

    private void generateRainureToitSTL(File file, Shack shack) throws IOException {
        final String EXTENSION = ".stl";
        final String TYPE = "_retrait_";
        final String TYPEPANEAU = "T_";

        final String fileName = projectName + TYPE + TYPEPANEAU + "1" + EXTENSION;
        RetraitToit toit;
        switch(shack.getRoofFacingDirection()){
            case FRONT:
            case BACK:
                toit = new RetraitToit(0,0,0,
                        shack.getExteriorWall(Direction.BACK).getWidth(),shack.getExteriorWall(Direction.RIGHT).getWidth(),shack.getWallsHeight()
                        ,shack.getPanelsThickness(),shack.getRoofFacingDirection(),shack.getRoof().getRoofAngle(),0,0,0);
                break;
            case RIGHT:
            case LEFT:
                toit = new RetraitToit(0,0,0,
                        shack.getExteriorWall(Direction.RIGHT).getWidth(),shack.getExteriorWall(Direction.BACK).getWidth(),shack.getWallsHeight()
                        ,shack.getPanelsThickness(),shack.getRoofFacingDirection(),shack.getRoof().getRoofAngle(),0,0,0);
                break;
            default:
                toit = null;
        }


        BufferedWriter buffered = new BufferedWriter(new FileWriter(new File(file, fileName)));
        buffered.write("solid Toit\n");

        buffered.write(toit.writer());

        buffered.write("endsolid Toit\n");
        buffered.close();
    }

    private void generateRainureRallongeSTL(File file, Shack shack) throws IOException {
        final String EXTENSION = ".stl";
        final String TYPE = "_retrait_";
        final String TYPEPANEAU = "R_";
        double heightMur = shack.getExteriorWall(Direction.LEFT).getWidth() * tan(toRadians(shack.getRoof().getRoofAngle()));

        final String fileName = projectName + TYPE + TYPEPANEAU + "1" + EXTENSION;
        RetraitRallongeAvant rallonge = new RetraitRallongeAvant(0,0,0,
                shack.getExteriorWall(shack.getRoofFacingDirection()).getWidth(),heightMur
                ,shack.getPanelsThickness(),shack.getRoofFacingDirection(),shack.getRoof().getRoofAngle(),0,0,0);

        BufferedWriter buffered = new BufferedWriter(new FileWriter(new File(file, fileName)));
        buffered.write("solid Rallonge\n");

        buffered.write(rallonge.writer());

        buffered.write("endsolid Rallonge\n");
        buffered.close();
    }

    public void generateRetraitSTL(Wall wall, File file, Shack shack) throws IOException {
        int retraitNbr = 0;
        generateAccessoriesSTL(wall, file, retraitNbr);
        generateReinuresSTL(wall, file, shack, retraitNbr);

    }

    public void convertirRetrait(Shack shack,File file) throws IOException {
        Wall fWall = shack.getExteriorWall(Direction.FRONT);
        Wall bWall = shack.getExteriorWall(Direction.BACK);
        Wall lWall = shack.getExteriorWall(Direction.LEFT);
        Wall rWall = shack.getExteriorWall(Direction.RIGHT);
        HashMap<String,int[]> mapPoint = new HashMap<String, int[]>();
        if (shack.getRoofFacingDirection() == Direction.FRONT || shack.getRoofFacingDirection() == Direction.BACK){
            mapPoint.put("A", new int[]{0, 2});
            mapPoint.put("F", new int[]{3, 1});
            mapPoint.put("G", new int[]{2,3});
            mapPoint.put("D", new int[]{0,1});
        }else{
            mapPoint.put("A", new int[]{1, 0});
            mapPoint.put("F", new int[]{2, 3});
            mapPoint.put("G", new int[]{3,1});
            mapPoint.put("D", new int[]{0,2});
        }
        BufferedWriter writer;

        for(String mur : mapPoint.keySet()){
            int i = 0;
            for (int rainure: mapPoint.get(mur)){
                writer = new BufferedWriter(new FileWriter(new File(file,projectName + "_Retrait_" + mur +"_"+ i + ".stl")));
                writer.write("solid rainure\n");
                PrismeExtraslotExterieur prisme1 = new PrismeExtraslotExterieur(lWall.getWidth(),fWall.getWidth(),
                        shack.getWallsHeight(),shack.getPanelsThickness(),shack.getExtraSlotDistance(),shack.getRoofFacingDirection(),rainure);
                PrismeExtraslotMilieu prisme2 = new PrismeExtraslotMilieu(lWall.getWidth(),fWall.getWidth(),
                        shack.getWallsHeight(),shack.getPanelsThickness(),shack.getExtraSlotDistance(),shack.getRoofFacingDirection(),rainure);
                PrismeExtraslotInterieur prisme3 = new PrismeExtraslotInterieur(lWall.getWidth(),fWall.getWidth(),
                        shack.getWallsHeight(),shack.getPanelsThickness(),shack.getExtraSlotDistance(),shack.getRoofFacingDirection(),rainure);
                writer.write(prisme1.writer());
                writer.write(prisme2.writer());
                writer.write(prisme3.writer());
                writer.write("endsolid rainure\n");
                writer.close();
                i++;
            }

        }
    }

}
