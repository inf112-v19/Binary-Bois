package inf112.skeleton.app;

import com.badlogic.gdx.graphics.Texture;

public class Robot extends Renderable implements IItem {

    private Vector2Di pos;
    private Vector2Di dir;
    private Texture texture;
    private int size;
    private String name;
    private static int nameInt = 1;
    private Vector2Di archiveMarker;

    Robot(int x, int y) throws NoSuchResource {
        super();
        this.dir = new Vector2Di(1, 0);
        this.pos = new Vector2Di(x, y);
        this.texture = Resources.getTexture("robot1.png");
        this.name = "Robot " + nameInt++;
        setDrawPos(new Vector2Df(x, y));
    }

    /**
     * Constructor for testing, instantiating Texture will throw a NullPointerException.
     */
    Robot() {
        this.pos = new Vector2Di(0, 0);
        this.dir = new Vector2Di(1, 0);
    }

    /**
     * Move the robot d units along it's direction vector.
     * @param d Positive or negative number.
     */
    public void move(int d) {
        move(dir, d);
    }

    public void move(Vector2Di dir, int d) {
        this.pos.move(dir, d);
        Vector2Df anim_move = dir.copy().tof();
        anim_move.mul(d);
        this.addAnimation(new Animation(anim_move, 0f, 0.25f));
    }

    public void rot(int deg) {
        this.dir.rotate((double) deg);
    }

    public Vector2Di getDir(){
        return dir;
    }

    public void setArchiveMarker(Vector2Di archiveMarker) {
        this.archiveMarker = archiveMarker.copy();
    }

    public Vector2Di getArchiveMarkerPos() {
        return archiveMarker;
    }

    /**
     * Robo RIP
     */
    public void death() {
        //TODO: Lose life etc.
    }

    public void setPos(Vector2Di pos) {
        this.pos = pos;
        clearAnimations();
        setDrawPos(pos.tof());
    }

    public Vector2Di getPos() {
        return pos;
    }

    @Override
    public Texture getTexture() {
        return texture;
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