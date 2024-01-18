package Domain.Rendering.Util;

import Domain.Rendering.Edge;
import Domain.Rendering.Mesh;
import Domain.Utility.Vector3;

import java.util.ArrayList;
import java.util.List;

public class MeshGenerator {

    public static Mesh createRectangularPrismMesh(double width, double height, double thickness) {
        double halfWidth = width / 2;
        double halfHeight = height / 2;
        double halfThickness = thickness / 2;

        List<Vector3> vertices = new ArrayList<>();
        vertices.add(new Vector3(-halfWidth, -halfHeight, -halfThickness));
        vertices.add(new Vector3(halfWidth, -halfHeight, -halfThickness));
        vertices.add(new Vector3(halfWidth, halfHeight, -halfThickness));
        vertices.add(new Vector3(-halfWidth, halfHeight, -halfThickness));
        vertices.add(new Vector3(-halfWidth, -halfHeight, halfThickness));
        vertices.add(new Vector3(halfWidth, -halfHeight, halfThickness));
        vertices.add(new Vector3(halfWidth, halfHeight, halfThickness));
        vertices.add(new Vector3(-halfWidth, halfHeight, halfThickness));

        // Define the edges for the rectangular prism (cube)
        List<Edge> edges = new ArrayList<>();
        // Front face
        edges.add(new Edge(0, 1));
        edges.add(new Edge(1, 2));
        edges.add(new Edge(2, 3));
        edges.add(new Edge(3, 0));
        // Back face
        edges.add(new Edge(4, 5));
        edges.add(new Edge(5, 6));
        edges.add(new Edge(6, 7));
        edges.add(new Edge(7, 4));
        // Connect front and back faces
        edges.add(new Edge(0, 4));
        edges.add(new Edge(1, 5));
        edges.add(new Edge(2, 6));
        edges.add(new Edge(3, 7));

        return new Mesh(vertices, edges);
    }

}
