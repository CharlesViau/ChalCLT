package Domain.Rendering;

import Domain.Utility.Vector3;

public class Edge {
    private final int vertexIndex1;
    private final int vertexIndex2;

    public Edge(int vertexIndex1, int vertexIndex2) {
        this.vertexIndex1 = vertexIndex1;
        this.vertexIndex2 = vertexIndex2;
    }

    public int getVertexIndex1() {
        return vertexIndex1;
    }

    public int getVertexIndex2() {
        return vertexIndex2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Edge other = (Edge) obj;
        return (vertexIndex1 == other.vertexIndex1 && vertexIndex2 == other.vertexIndex2) ||
                (vertexIndex1 == other.vertexIndex2 && vertexIndex2 == other.vertexIndex1);
    }

    @Override
    public int hashCode() {
        int result = Math.min(vertexIndex1, vertexIndex2);
        result = 31 * result + Math.max(vertexIndex1, vertexIndex2);
        return result;
    }
}

