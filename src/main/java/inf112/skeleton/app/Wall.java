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
}