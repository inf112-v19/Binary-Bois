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
    private boolean died = false;
    private final String tex_src;

    Robot(int x, int y)  {
        super();
        tex_src = "textures/robot0"+nameInt+".png";
        this.dir = new Vector2Di(1, 0);
        this.pos = new Vector2Di(x, y);
        this.name = "Robot " + nameInt++;
        archiveMarker = this.pos.copy();
        setDrawPos(new Vector2Df(x, y));
    }

    public void initTextures() throws NoSuchResource {
        this.texture = Resources.getTexture(tex_src);
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
        addAnimation(Animation.moveBy(anim_move.toi(), 0.15f));
    }

    public void moveFast(Vector2Di dir, int d) {
        this.pos.move(dir, d);
        Vector2Df anim_move = dir.copy().tof();
        anim_move.mul(d);
        addAnimation(Animation.moveBy(anim_move.toi(), 0.07f));
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

    public boolean hasDied() {
        return died;
    }

    /**
     * Robo RIP
     */
    public void death(IBoard board) {
        died = true;
        Vector2Di currentPos = getPos();
        board.remove(this, currentPos);
    }

    /**
     * Reset a player to it's archiveMarker, and animate respawning.
     *
     * @param game The board in which the robot exists.
     */
    public void respawn(RoboRallyGame game) {
        health = MAX_HEALTH;
        Vector2Di backupPos = getArchiveMarkerPos();
        game.setOnBoard(this, backupPos);
        pos = archiveMarker.copy();
        setArchiveMarker(backupPos);
        addAnimation(Animation.scaleTo(this, 1, 1f));
        died = false;
    }

    /**
     * Handles health and deaths for robot based on which damage type it receives.
     */
    public void handleDamage(DamageType dtype, IBoard board) {
        int dmg = DamageType.getDamage(dtype);
        System.out.println(name + " took " + dmg + " damage and now has " + (health-dmg) + " hp");
        if ((health -= dmg) <= 0) {
            death(board);

            powered_on = false;
            if (++deaths >= MAX_DEATHS)
                game_over = true;

            switch (dtype) {
                case LASER:
                    Vector2Di vec = new Vector2Di(25, 0);
                    vec.rotate(rnd.nextInt(360));
                    int rot = (180-rnd.nextInt(360))*5;
                    addAnimation(new Animation(new Vector2Df(vec.getX(), vec.getY()), rot, 3-this.getFinalAnimationScale(), 2f));
                    addAnimation(new Animation(new Vector2Df(0, 0), -rot, 1-this.getFinalAnimationScale(), 0.01f));
                    addAnimation(Animation.moveTo(this, archiveMarker, 0.01f));
                    break;
                case FALL:
                    addAnimation(new Animation(new Vector2Df(0, 0), 360*2, -this.getFinalAnimationScale(), 1f));
                    addAnimation(Animation.moveTo(this, archiveMarker, 0.01f));
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    /** You are out of the game if this is true */
    public boolean isGameOver(){
        return game_over;
    }

    public boolean isPoweredDown() {
        return !powered_on;
    }

    public void powerOn() {
        assert !powered_on;

        if (!game_over) {
            powered_on = true;
            health = MAX_HEALTH;
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