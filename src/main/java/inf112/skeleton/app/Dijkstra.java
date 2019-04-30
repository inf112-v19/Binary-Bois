package inf112.skeleton.app;

import org.lwjgl.Sys;

import java.util.*;

class Node {

    private List<Node> shortestPathHere;
    private Map<Node, Integer> adjacent;
    private int num;
    private Integer distance;

    public Node(int num) {
        this.num = num;
        this.adjacent = new HashMap<>();
        this.shortestPathHere = new LinkedList<>();
        this.distance = Integer.MAX_VALUE;
    }

    public void add(Node other, int dist) {
        adjacent.put(other, dist);
    }

    public int getNum() {
        return num;
    }

    public List<Node> getShortestPathHere() {
        return shortestPathHere;
    }

    public void setShortestPathHere(List<Node> shortestPathHere) {
        this.shortestPathHere = shortestPathHere;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Map<Node, Integer> getAdjacent() {
        return adjacent;
    }

    public String getCardinal(Node other) {
        int otherNum = other.getNum();
        int diff = otherNum - num;
        if (diff == 1) {
            return "e";
        } else if (diff == -1) {
            return "w";
        } else if (diff < -1) {
            return "s";
        } else {
            return "n";
        }
    }

    @Override
    public String toString() {
        return Integer.toString(num);
    }

    public void printNeighbours() {
        //System.out.println("Node " + num + " is adjacent to:");

        for (Map.Entry<Node, Integer> n : adjacent.entrySet()) {
            String dir = getCardinal(n.getKey());
            System.out.println("a" + num  + " -> " + "a" + n.getKey() + ":" + dir + ";");
        }
    }
}

/**
 * Used to help Ai find it's way
 */
public class Dijkstra {

    private IBoard board;
    private ArrayList<Node> nodes;
    private int width;
    private int height;

    @SuppressWarnings("unchecked")
    public Dijkstra(IBoard board) {

        this.board = board;
        width = board.getWidth();
        height = board.getHeight();

        nodes = new ArrayList();
        for (int i = 0; i < height*width; i++)
            nodes.add(new Node(i));
        //System.out.println("digraph {");
        // Adds all edges between vertices
        outer: for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                int pos = x + y*width;
                //System.out.println("a" + pos + "[label=\"(" + x + ", " + y + ")\"];");
                int conn = isConnected(new Vector2Di(x, y), new Vector2Di(0, 1));
                if (conn > -1)
                    nodes.get(pos).add(nodes.get(pos + width), conn);

                conn = isConnected(new Vector2Di(x, y), new Vector2Di(1, 0));
                if (conn > -1)
                    nodes.get(pos).add(nodes.get(pos + 1), conn);

                conn = isConnected(new Vector2Di(x, y), new Vector2Di(0, -1));
                if (conn > -1)
                    nodes.get(pos).add(nodes.get(pos - width), conn);

                conn = isConnected(new Vector2Di(x, y), new Vector2Di(-1, 0));
                if (conn > -1)
                    nodes.get(pos).add(nodes.get(pos - 1), conn);

            }
        }


        //for (Node n : nodes) {
        //    n.printNeighbours();
        //}
        //System.out.println("}");
    }

    public ArrayList<Node> calculateShortestPathFromSource(Vector2Di sourceVec) {
        Node source = nodes.get(sourceVec.getX() + sourceVec.getY() * width);
        source.setDistance(0);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<Node, Integer> adjacencyPair :
                    currentNode.getAdjacent().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return nodes;
    }

    private void calculateMinimumDistance(Node evaluationNode,
                                                 Integer edgeWeigh, Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPathHere());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPathHere(shortestPath);
        }
    }

    private Node getLowestDistanceNode(Set < Node > unsettledNodes) {
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }


    /**
     * Finds if two tiles are connected e.g. no holes, walls og laser shooters
     * @param pos starting vertex
     * @param dir going to
     * @return if the two tiles are connected
     */
    public int isConnected(Vector2Di pos, Vector2Di dir){
        Vector2Di orig_pos = pos.copy();
        Vector2Di newpos = pos.copy();
        newpos.move(dir, 1);

        assert board.isOnBoard(orig_pos);

        for (IItem item : board.get(orig_pos)) {
            if (!accessible(item, dir))
                return -1;

            if (item instanceof ConveyorBelt) {
                if (((ConveyorBelt) item).getDir() == dir)
                    return 1;
                Vector2Di conveyDir = ((ConveyorBelt) item).getDir().copy();
                conveyDir.add(dir);
                if (conveyDir.magnitude() == 0)
                    return -1;
            }
        }

        if (!board.isOnBoard(newpos))
            return -1;

        ArrayList<IItem> itemlist = board.get(newpos);
        if (itemlist.isEmpty())
            return 1000;

        Vector2Di dir_opposite = dir.copy();
        dir_opposite.mul(-1);
        for (IItem itemInFront : itemlist)
            if (!accessible(itemInFront, dir_opposite))
                return -1;

        ArrayList<IItem> tmp_list = new ArrayList<>(itemlist);
        for (IItem itemInFront : tmp_list) {
            if (itemInFront instanceof Robot) {
                Vector2Di otherBotPos = ((Robot) itemInFront).getPos();
                if (isConnected(otherBotPos, dir) < 0)
                    return -1;
            }
            if (itemInFront instanceof ConveyorBelt) {

                if (((ConveyorBelt) itemInFront).getDir() == dir)
                    return 200;

                Vector2Di conveyDir = ((ConveyorBelt) itemInFront).getDir().copy();
                conveyDir.add(dir);
                if (conveyDir.magnitude() == 0)
                    return -1;
            }
            if (itemInFront instanceof Laser) {
                return 5000;
            }
        }

        return 1000;
    }

    private boolean accessible(IItem item, Vector2Di dir) {
        return !(item instanceof Wall && ((Wall) item).hasEdge(dir) ||
                item instanceof Hole || item instanceof LaserShooter);
    }
}
