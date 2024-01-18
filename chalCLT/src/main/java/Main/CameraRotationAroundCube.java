package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CameraRotationAroundCube extends JPanel {
    private double cameraX, cameraY, cameraZ;
    private double angleX, angleY;
    private double[][] cubeVertices;

    private int prevX, prevY;

    // Depth buffer to store depth values
    private double[][] depthBuffer;

    public CameraRotationAroundCube() {
        cubeVertices = new double[][] {
                {-1, -1, -1},
                {1, -1, -1},
                {1, 1, -1},
                {-1, 1, -1},
                {-1, -1, 1},
                {1, -1, 1},
                {1, 1, 1},
                {-1, 1, 1}
        };

        cameraX = 0;
        cameraY = 0;
        cameraZ = -5;
        angleX = 0;
        angleY = 0;

        prevX = 0;
        prevY = 0;

        // Initialize the depth buffer with initial depth values in the constructor
        depthBuffer = new double[0][0];

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int currX = e.getX();
                int currY = e.getY();

                double rotateStep = Math.toRadians(1);

                angleY += (currX - prevX) * rotateStep;
                angleX += (currY - prevY) * rotateStep;

                prevX = currX;
                prevY = currY;

                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        // Initialize or reinitialize the depth buffer based on the panel's size
        if (width != depthBuffer.length || height != depthBuffer[0].length) {
            depthBuffer = new double[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    depthBuffer[x][y] = Double.POSITIVE_INFINITY;
                }
            }
        }

        double[][] projectedVertices = new double[8][2];

        for (int i = 0; i < 8; i++) {
            double x = cubeVertices[i][0];
            double y = cubeVertices[i][1];
            double z = cubeVertices[i][2];

            // Apply camera rotation
            double tempX = x * Math.cos(angleY) - z * Math.sin(angleY);
            double tempZ = x * Math.sin(angleY) + z * Math.cos(angleY);
            x = tempX;
            z = tempZ;

            double tempY = y * Math.cos(angleX) - z * Math.sin(angleX);
            z = y * Math.sin(angleX) + z * Math.cos(angleX);
            y = tempY;

            // 3D to 2D projection (simple perspective)
            double scale = cameraZ / (cameraZ + z);
            int screenX = (int) (width / 2 + x * scale * 100);
            int screenY = (int) (height / 2 - y * scale * 100);

            // Check if pixel should be rendered based on depth
            if (shouldRenderPixel(screenX, screenY, z)) {
                projectedVertices[i][0] = screenX;
                projectedVertices[i][1] = screenY;
            }
        }

        g.setColor(Color.BLACK);

        // Define the edges of the cube
        int[][] edges = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0},
                {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        for (int[] edge : edges) {
            int x1 = (int) projectedVertices[edge[0]][0];
            int y1 = (int) projectedVertices[edge[0]][1];
            int x2 = (int) projectedVertices[edge[1]][0];
            int y2 = (int) projectedVertices[edge[1]][1];

            g.drawLine(x1, y1, x2, y2);
        }
    }

    // Update depth buffer with new depth values
    private void updateDepthBuffer(int x, int y, double depth) {
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
            depthBuffer[x][y] = depth;
        }
    }

    // Check if a pixel should be rendered based on depth
    private boolean shouldRenderPixel(int x, int y, double depth) {
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
            return depth < depthBuffer[x][y];
        }
        return false; // Pixel is out of screen bounds
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Camera Rotation Around Cube");
            CameraRotationAroundCube cameraRotation = new CameraRotationAroundCube();

            frame.add(cameraRotation);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setVisible(true);
            cameraRotation.requestFocus();
        });
    }
}