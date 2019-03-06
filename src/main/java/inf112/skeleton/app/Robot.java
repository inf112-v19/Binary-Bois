package inf112.skeleton.app;

import com.badlogic.gdx.graphics.Texture;

import java.io.File;

public class Robot extends IItem implements IRenderable {

    private Vector2D pos;
    private Vector2D dir;
    private Texture texture;
    private int size;
    private String name;
    private static int nameInt = 1;
    private Vector2D archiveMarker;

    Robot(int x, int y) {
        this.dir = new Vector2D(1, 0);
        this.pos = new Vector2D(x, y);

        // FIXME: This is a temporary workaround, later on we should have a more generalized way
        //        to create the robot texture by doing color variations on the fly.
        String img_path = "./resources/robot" + nameInt + ".png";
        if (!(new File(img_path)).exists()) {
            img_path = "./resources/robot1.png";
        }

        this.texture = new Texture(img_path);
        this.name = "Robot " + nameInt++;
    }

    /**
     * Move the robot d units along it's direction vector.
     * @param d Positive or negative number.
     */
    public void move(int d) {
        this.pos.move(dir, d);
    }

    public void rot(int deg) {
        this.dir.rotate((double) deg);
    }

    public Vector2D getDir(){
        return dir;
    }

    public void setArchiveMarker(Vector2D archiveMarker) {
        this.archiveMarker = archiveMarker;
    }

    public Vector2D getArchiveMarkerPos() {
        return archiveMarker;
    }

    /**
     * Robo RIP
     */
    public void death() {
        pos = archiveMarker;
        //TODO: Lose life etc.
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