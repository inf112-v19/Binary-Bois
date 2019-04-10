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
    private final int MAX_DEATHS = 3;
    private final int MAX_HEALTH = 10;
    private int health = MAX_HEALTH;
    private int deaths = 0;
    private boolean powered_on = true;

    Robot(int x, int y) throws NoSuchResource {
        super();
        setPosScale(32);
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
     * Handles health and deaths for robot based on which damage type it recieves.
     *
     */
    public void handleDamage(DamageType dtype) {
        int dmg = DamageType.getDamage(dtype);
        if ((health -= dmg) <= 0) {
            if (++deaths >= MAX_DEATHS) {
                switch (dtype) {
                    case LASER:
                        // Show laser animation
                        break;
                    case FALL:
                        // Show fall animation
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            } else {
                health = MAX_HEALTH;
                powered_on = false;
            }
        }
    }


    public boolean isPoweredDown() {
        return !powered_on;
    }

    public void powerOn() {
        assert isPoweredDown();
        powered_on = true;
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

    public int getHealth() {
        return health;
    }

    public int getDeaths() {
        return deaths;
    }
}