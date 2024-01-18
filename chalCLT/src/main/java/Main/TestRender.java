package Main;

import Domain.Rendering.Camera;
import Domain.Rendering.Edge;
import Domain.Rendering.Mesh;
import Domain.Rendering.Util.MeshGenerator;
import Domain.Utility.Vector3;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TestRender extends JPanel {
    private Camera camera;
    private Mesh cubeMesh;

    int lastX;
    int lastY;

    public TestRender(Camera camera, Mesh cubeMesh) {
        this.camera = camera;
        this.cubeMesh = cubeMesh;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();

                requestFocusInWindow();
            }
        });

        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {


                int deltaX = e.getX() - lastX;
                int deltaY = e.getY() - lastY;
                lastX = e.getX();
                lastY = e.getY();

                double sensitivity = 0.005;  // Adjust this value to control the rotation speed

                // Rotate the camera based on mouse input
                //camera.rotate(-deltaX * sensitivity, -deltaY * sensitivity);

                // Move the camera up based on vertical mouse movement
                double verticalAmount = -deltaY * sensitivity;
                // Move the camera left/right based on horizontal mouse movement
                double horizontalAmount = deltaX * sensitivity;

                System.out.println(verticalAmount);
                System.out.println(horizontalAmount);
                camera.move(horizontalAmount, verticalAmount);

                repaint();
            }
        });

        addMouseWheelListener(e -> {

            int notches = e.getWheelRotation();
            double zoomSpeed = 1;  // Adjust this value to control the zoom speed

            // Update the camera's position based on the mouse wheel movement
            camera.zoom(notches * zoomSpeed);

            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);

        // Get the transformed vertices from the cube mesh using the camera's view and projection matrices
        List<Vector3> transformedVertices = new ArrayList<>();
        for (Vector3 vertex : cubeMesh.getVertices()) {
            Vector3 worldSpace = camera.getViewMatrix().transform(vertex);
            Vector3 clipSpace = camera.getProjectionMatrix().transform(worldSpace);
            Vector3 screenSpace = new Vector3(
                    (clipSpace.x + 1) * getWidth() / 2,
                    (1 - clipSpace.y) * getHeight() / 2,
                    clipSpace.z
            );
            transformedVertices.add(screenSpace);
        }

        // Draw the edges based on the transformed vertices
        for (Edge edge : cubeMesh.getEdges()) {
            Vector3 vertex1 = transformedVertices.get(edge.getVertexIndex1());
            Vector3 vertex2 = transformedVertices.get(edge.getVertexIndex2());

            int x1 = (int) vertex1.x;
            int y1 = (int) vertex1.y;
            int x2 = (int) vertex2.x;
            int y2 = (int) vertex2.y;

            g.drawLine(x1, y1, x2, y2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Cube Renderer with Camera");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create a camera and cube mesh
            Camera camera = new Camera(new Vector3(0, 0, 5));
            Mesh cubeMesh = MeshGenerator.createRectangularPrismMesh(2, 1, 3);

            // Create the rendering panel
            TestRender renderer = new TestRender(camera, cubeMesh);
            frame.add(renderer);

            frame.setSize(400, 400);
            frame.setVisible(true);
            frame.requestFocusInWindow();
        });
    }

}