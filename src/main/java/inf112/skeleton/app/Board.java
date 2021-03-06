package inf112.skeleton.app;

import java.util.ArrayList;
import java.util.HashMap;

public class Board implements IBoard {

    private ArrayList<IItem>[] grid;
    private int width;
    private int height;
    private HashMap<IItem, Integer> itemList; //keeps track of the pos of every item

    public Board(int height, int width) {
        this.width = width;
        this.height = height;
        this.grid = new ArrayList[width*height];
        for (int i = 0; i<grid.length; i++)
            grid[i] = new ArrayList<>();
        this.itemList = new HashMap<>();
    }

    @Override
    public ArrayList<IItem> get(int x, int y) {
        if (x < 0 || y < 0)
            throw new IllegalArgumentException("x and y must be greater than 0");
        return grid[x + y*width];
    }

    @Override
    public void set(IItem item, int x, int y) {
        if (x < 0 || y < 0)
            throw new IllegalArgumentException("x and y must be greater than 0");
        int index = x + y*width;
        itemList.put(item, index);
        grid[index].add(item);
    }

    public void remove(IItem item, int x, int y) {
        get(x, y).remove(item);
    }

    public void remove(IItem item, Vector2Di pos) {
        get(pos).remove(item);
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public ArrayList<IItem> getAllItemsOnBoard() {
        ArrayList<IItem> gridArrayList = new ArrayList<>();
        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid[i].size(); j++){
                gridArrayList.add(grid[i].get(j));
            }

        }
        return gridArrayList;
    }
}
