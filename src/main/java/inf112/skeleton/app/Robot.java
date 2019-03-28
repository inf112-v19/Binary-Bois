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
    //private int maxLives = getMaxLives();
    //private int maxHealth = getMaxHealth();

    Robot(int x, int y) throws NoSuchResource {
        super();
        this.dir = new Vector2Di(1, 0);
        this.pos = new Vector2Di(x, y);
        this.texture = Resources.getTexture("robot1.png");
        this.name = "Robot " + nameInt++;
        archiveMarker = this.pos.copy();
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
        addAnimation(Animation.moveBy(anim_move.toi(), 0.25f));
    }

    public void rot(int deg) {
        this.dir.rotate((double) deg);
        addAnimation(Animation.rotateBy(this, deg, 0.25f));
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
        addAnimation(new Animation(new Vector2Df(0, 0), 360*2, -1, 1f));
        addAnimation(Animation.moveTo(this, archiveMarker, 0.1f));
        addAnimation(new Animation(new Vector2Df(0, 0), 0, 1, 0.5f));
        this.pos = archiveMarker.copy();
    }

    /**
     * 10 HealthPoints per liv.
     * Antall programcards man kan velge endrer seg ut i fra antall HealthPoints.
     *
    public int robotHealth(int maxHealth) {
        //Todo
        int robotHealth = maxHealth;

        return robotHealth;
    }

    /**
     * De 3 livene til robotene, vil miste et liv dersom du mister 10 healthPoints,
     * eller faller ned i et hull.
     *
    public int robotLives(int maxHealth, int maxLives) {
        //Todo
        int robotLives = maxLives;
        if (robotLives >= 1) {
            if (maxHealth == 0) {
                return robotLives -1;
            }
        }
        if (robotLives == 0) {
            //Roboten fjernes fra brettet og spiller kan ikke foreta seg flere actoins
        }
        return robotLives;
    }
     */

    public int handleDamage() {
        return 0;
    }

    public void setPos(Vector2Di pos) {
        this.pos = pos;
        clearAnimations();
        addAnimation(Animation.moveTo(this, pos, 0.4f));
        //setDrawPos(pos.tof());
    }

    public Vector2Di getPos() {
        return pos;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public String getName() {
        return name;
    }

    /**public int getMaxHealth() {
        return 10;
    }

    public int getMaxLives() {
        return 3;
    }

    public int getCurrentHealth() {
        return maxHealth;
    }

    public int getCurrentLives(){
        return maxLives;
    }
     */


}