package inf112.skeleton.app;

import com.badlogic.gdx.graphics.Texture;

import java.util.Random;

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
    private boolean game_over = false;
    private Random rnd = new Random();

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
    public void death(IBoard board) {
        Vector2Di currentPos = getPos();
        Vector2Di backupPos = getArchiveMarkerPos();
        board.get(currentPos).remove(this);
        board.set(this, backupPos);
        pos = archiveMarker.copy();
        setArchiveMarker(backupPos);
    }

    /**
     * Handles health and deaths for robot based on which damage type it recieves.
     */
    public void handleDamage(DamageType dtype, IBoard board) {
        int dmg = DamageType.getDamage(dtype);
        System.out.println(name + " took " + dmg + " damage and now has " + (health-dmg) + " hp");
        if ((health -= dmg) <= 0) {
            death(board);

            if (++deaths >= MAX_DEATHS) {
                powered_on = false;
                game_over = true;
            } else {
                powered_on = false;
            }
            switch (dtype) {
                case LASER:
                    Vector2Di vec = new Vector2Di(25, 0);
                    vec.rotate(rnd.nextInt(360));
                    int rot = (180-rnd.nextInt(360))*5;
                    addAnimation(new Animation(new Vector2Df(vec.getX(), vec.getY()), rot, 3, 2f));
                    addAnimation(new Animation(new Vector2Df(0, 0), -rot, 1-this.getFinalAnimationScale(), 0.01f));
                    addAnimation(Animation.moveTo(this, archiveMarker, 0.01f));
                    break;
                case FALL:
                    addAnimation(new Animation(new Vector2Df(0, 0), 360*2, -1, 1f));
                    addAnimation(Animation.moveTo(this, archiveMarker, 0.01f));
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    /** You are out of the game if this is true */
    public boolean isGame_over(){
        return game_over;
    }

    public boolean isPoweredDown() {
        return !powered_on;
    }

    public void powerOn() {
        assert isPoweredDown();
        if (!game_over && !powered_on) {
            System.out.println("Powers up");
            powered_on = true;
            health = MAX_HEALTH;
            addAnimation(Animation.scaleTo(this, 1-getAnimatedScale(), 1f));
        }
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