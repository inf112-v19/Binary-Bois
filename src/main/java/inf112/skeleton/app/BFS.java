package inf112.skeleton.app;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Used to help Ai find it's way
 */
public class BFS {

    IBoard board;
    ArrayList<Integer>[] edges;

    public BFS(IBoard board) {

        this.board = board;
        int height = board.getHeight();
        int width = board.getWidth();

        edges = new ArrayList[width*height];
        for (ArrayList a : edges)
            a = new ArrayList();

        // Adds all edges between vertices
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pos = x + y*width;
                if (isConnected(new Vector2Di(x, y), new Vector2Di(0, 1))) {
                    edges[pos].add(pos + width);
                    edges[pos + width].add(pos);
                } else if (isConnected(new Vector2Di(x, y), new Vector2Di(1, 0))) {
                    edges[pos].add(pos + 1);
                    edges[pos + 1].add(pos);
                } else if (isConnected(new Vector2Di(x, y), new Vector2Di(0, -1))) {
                    edges[pos].add(pos - width);
                    edges[pos - width].add(pos);
                } else if (isConnected(new Vector2Di(x, y), new Vector2Di(-1, 0))) {
                    edges[pos].add(pos - 1);
                    edges[pos - 1].add(pos);
                }
            }
        }
    }

    /**
     * Finds if two tiles are connected e.g. no holes, walls og laser shooters
     * @param pos starting vertex
     * @param dir going to
     * @return if the two tiles are connected
     */
    public boolean isConnected(Vector2Di pos, Vector2Di dir){
        Vector2Di orig_pos = pos.copy();
        Vector2Di newpos = pos.copy();
        newpos.move(dir, 1);

        assert board.isOnBoard(orig_pos);

        for (IItem item : board.get(orig_pos))
            if (!accessible(item, dir))
                return false;

        if (!board.isOnBoard(newpos))
            return false;

        ArrayList<IItem> itemlist = board.get(newpos);
        if (itemlist.isEmpty())
            return true;

        Vector2Di dir_opposite = dir.copy();
        dir_opposite.mul(-1);
        for (IItem itemInFront : itemlist)
            if (!accessible(itemInFront, dir_opposite))
                return false;

        ArrayList<IItem> tmp_list = new ArrayList<>(itemlist);
        for (IItem itemInFront : tmp_list)
            if (itemInFront instanceof Robot) {
                Vector2Di otherBotPos = ((Robot) itemInFront).getPos();
                if (!isConnected(otherBotPos, dir))
                    return false;
            }

        return true;
    }

    private boolean accessible(IItem item, Vector2Di dir) {
        if (item instanceof Wall && ((Wall) item).hasEdge(dir)) {
            return false;
        } else if (item instanceof Hole) {
            return false;
        } else if (item instanceof LaserShooter) {
            return false;
        } else {
            return true;
        }
    }
}
