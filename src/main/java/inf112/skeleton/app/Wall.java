package inf112.skeleton.app;

public class Wall extends TilePiece {
    private Vector2D edges[];

    public Wall(Vector2D... edges) {
        this.edges = new Vector2D[edges.length];
        for (int i = 0; i < edges.length; i++)
            this.edges[i] = edges[i];
    }

    public boolean hasEdge(Vector2D edge) {
        for (Vector2D e : edges)
            if (e.equals(edge))
                return true;
        return false;
    }

    public static Wall getFullWall() {
        return new Wall(new Vector2D(1, 0),
                        new Vector2D(-1, 0),
                        new Vector2D(0, 1),
                        new Vector2D(0, -1));
    }

    @Override
    String getName() {
        return "Wall";
    }
}