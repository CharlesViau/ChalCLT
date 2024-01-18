package Domain.Rendering;

import Domain.Utility.Vector3;

import java.util.List;

public class Mesh {

    private List<Vector3> vertices; // List of 3D vertices
    private final List<Edge> edges; // List of unique edges

    public Mesh(List<Vector3> vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    public List<Vector3> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vector3> vertices) {
        this.vertices = vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}


