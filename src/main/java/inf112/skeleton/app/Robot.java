package inf112.skeleton.app;

import com.badlogic.gdx.graphics.Texture;

public class Robot extends IItem implements IRenderable {

    private Vector2D pos;
    private Vector2D dir;
    private Texture texture;
    private int size;
    private String name;
    private static int nameInt = 1;
    private Flag lastFlag;

    Robot(int x, int y) {
        this.dir = new Vector2D(1, 0);
        this.pos = new Vector2D(x, y);
        this.texture = new Texture("./resources/robot" + nameInt + ".png");
        this.name = "Robot " + String.valueOf(nameInt++);
    }

    public void forward(int d) {
        this.pos.move(dir, d);
    }

    public void backward(int d) {
        this.pos.move(dir, d);
    }

    public void rot(int deg) {
        this.dir.rotate((double) deg);
    }

    public Vector2D getDir(){
        return dir;
    }

    public void setLastFlag(Flag lastFlag) {
        this.lastFlag = lastFlag;
    }

    public Flag getLastFlag() {
        return lastFlag;
    }

    @Override
    public Vector2D getPos() {
        return pos;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public int compareTo(IRenderable o) {
        return 0;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getName() {
        return name;
    }

}