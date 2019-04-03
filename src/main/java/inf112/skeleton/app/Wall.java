package inf112.skeleton.app;

public class Wall extends TilePiece {
    private Vector2Di edges[];

    public Wall(Vector2Di... edges) {
        this.edges = new Vector2Di[edges.length];
        for (int i = 0; i < edges.length; i++)
            this.edges[i] = edges[i];
    }

    public boolean hasEdge(Vector2Di edge) {
        for (Vector2Di e : edges)
            if (e.equals(edge))
                return true;
        return false;
    }

    public Vector2Di[] getEdges() {
        return edges;
    }

    public static Wall getFullWall() {
        return new Wall(new Vector2Di(1, 0),
                        new Vector2Di(-1, 0),
                        new Vector2Di(0, 1),
                        new Vector2Di(0, -1));
    }

    @Override
    public String getName() {
        return "Wall";
    }
}