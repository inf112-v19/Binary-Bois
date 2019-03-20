package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Handles everything related to drawing and interacting with cards using
 * the mouse.
 */
public class CardManager {
    private final int CARD_WIDTH = 175;
    private final int CARD_HEIGHT = 250;
    private final Vector2Di CARD_SPACING = new Vector2Di(CARD_WIDTH + 10, 0);
    private final Vector2Di FIRST_CARD_POS = new Vector2Di(0, 300);
    private final Vector2Di DECK_BG_OFFSET = new Vector2Di(-12, -12);
    private final Vector2Di FIRST_SLOT_POS;
    private final float MOVE_TIME = 0.5f;
    private Vector2Di start_pos;

    private ArrayList<Card> inactive_cards = new ArrayList<>();
    private Card active_cards[];
    private Texture slot_back;
    private Texture slot_front;
    private Texture slot_sep;
    private Texture deck_bg;
    private Vector2Di deck_bg_pos;
    private int num_slots;
    private ShapeRenderer shape_renderer;
    private final float bg_gray = 0.40f;
    private Color bgcolor = new Color(bg_gray, bg_gray, bg_gray, 1.0f);

    private Stage stage;

    public CardManager(int num_slots) throws NoSuchResource {
        start_pos = new Vector2Di(CARD_SPACING.getX() * num_slots + 20, 10);
        stage = new Stage();
        slot_back = Resources.getTexture("cards/175x250/slot/slot_back.png");
        slot_front = Resources.getTexture("cards/175x250/slot/slot_front.png");
        slot_sep = Resources.getTexture("cards/175x250/slot/separator.png");
        deck_bg = Resources.getTexture("cards/175x250/deck/deck_background.png");
        this.num_slots = num_slots;
        FIRST_SLOT_POS = new Vector2Di(0, 0);
        shape_renderer = new ShapeRenderer();
        deck_bg_pos = start_pos.copy();
        deck_bg_pos.add(DECK_BG_OFFSET);
        active_cards = new Card[num_slots];
    }

    public Stage getStage() {
        return stage;
    }

    public void setCards(ArrayList<Card> cards) {
        inactive_cards.clear();
        inactive_cards.addAll(cards);
        for (Card c : cards)
            c.setDrawPos(start_pos.tof());

        // TODO: Add click event to activate toggle hideCards()/showCards()
        //       when the cards have been moved after showCards() there should
        //       be a dotted line around where the card deck was, clicking on that
        //       area should return the cards.
    }

    public void showCards() {
        Vector2Di card_pos = FIRST_CARD_POS.copy();
        float idle_t = 0.0f;
        int i = 0;
        for (Card c : inactive_cards) {
            c.addAnimation(Animation.idle(idle_t += 0.1f));
            long ticks = c.addAnimation(Animation.moveTo(c, card_pos, MOVE_TIME));
            card_pos.add(CARD_SPACING);

            // Add drag event:
            Renderable.addAnimationCallback(ticks, () -> makeDragable(c));
        }
    }

    public void hideCards() {
        float idle_t = 0.0f;
        for (int i = inactive_cards.size()-1; i >= 0; i--) {
            Card c = inactive_cards.get(i);
            c.addAnimation(Animation.idle(idle_t += 0.1f));
            c.addAnimation(Animation.moveTo(c, start_pos, MOVE_TIME));

            // Remove drag event:
        }
    }

    public void drawStage() {
        stage.act(Gdx.graphics.getDeltaTime());
    }

    public ArrayList<Card> getSequence() {
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < num_slots; i++) {
            Card c = active_cards[i];
            if (c != null)
                cards.add(c);
        }
        return cards;
    }

    /**
     * Join all the ICommands from the card sequence into a single ICommand.
     * The returned function will wait for animations to finish between card
     * executions.
     *
     * @return ICommand that will execute all cards in order.
     */
    public ICommand getSequenceAsCommand() {
        ArrayList<Card> seq = getSequence();

        // Yes, this is weird, but I swear there's a good reason for using 1-length arrays here.
        ICommand cmd[] = new ICommand[1];
        int i[] = new int[1];

        cmd[0] = (int amount, Robot r, Game g) -> {
            seq.get(i[0]).exec(r, g);
            r.addAnimationCallback(() -> {
                if (++i[0] >= seq.size())
                    return;
                cmd[0].exec(amount, r, g);
            });
            return true;
        };
        return cmd[0];
    }

    public void makeDragable(Card c) {
        Skin skin = new Skin();
        skin.add("card", c.getTexture());

        Image sourceImage = new Image(skin, "card");
        Vector2Di draw_pos = c.getFinalAnimationPos(1);
        sourceImage.setBounds(draw_pos.getX(), draw_pos.getY(), 175, 250);
        sourceImage.setColor(0, 0, 0, 0);
        stage.addActor(sourceImage);

        DragAndDrop dragAndDrop = new DragAndDrop();

        Vector2Di slot_pos = FIRST_SLOT_POS.copy();
        ArrayList<Image> target_imgs = new ArrayList<>();
        ArrayList<DragAndDrop.Target> targets = new ArrayList<>();
        for (int i = 0; i < num_slots; i++) {
            // TODO: Create a separate target image
            Image target_img = new Image(skin, "card");
            target_img.setColor(0, 0, 0, 0.0f);
            target_img.setBounds(slot_pos.getX(), slot_pos.getY(), 175, 250);
            //stage.addActor(target_img);
            target_imgs.add(target_img);
            Vector2Di cur_slot_pos = slot_pos.copy();
            final int slot_i = i;
            targets.add(new DragAndDrop.Target(target_img) {
                                public boolean drag (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                                    getActor().setColor(0.25f, 1.00f, 0.25f, 0.25f);
                                    return true;
                                }

                                public void reset (DragAndDrop.Source source, DragAndDrop.Payload payload) {
                                    getActor().setColor(0, 0, 0, 0);
                                }

                                public void drop (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                                    //sourceImage.remove();
                                    c.setDrawPos(cur_slot_pos.tof());
                                    for (DragAndDrop.Target t : targets)
                                        dragAndDrop.removeTarget(t);
                                    for (Image img : target_imgs)
                                        img.remove();
                                    c.show();
                                    Card prev_c = active_cards[slot_i];
                                    active_cards[slot_i] = c;
                                    int idx = inactive_cards.indexOf(c);
                                    if (prev_c != null) {
                                        // TODO: Animate the swapping
                                        inactive_cards.set(idx, prev_c);
                                    } else {
                                        inactive_cards.remove(idx);
                                    }
                                    sourceImage.setBounds(cur_slot_pos.getX(), cur_slot_pos.getY(), 175, 250);
                                }
                            });
            slot_pos.add(CARD_SPACING);
        }

        dragAndDrop.addSource(new DragAndDrop.Source(sourceImage) {
            public DragAndDrop.Payload dragStart (InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject("Dragable card");
                Image img = new Image(skin, "card");
                img.setBounds(80, 80, 175, 250);
                payload.setObject(img);
                c.hide();

                payload.setDragActor(img);
                dragAndDrop.setDragActorPosition(x, y - img.getHeight());
                sourceImage.setColor(0, 0, 0, 0);

                for (Image timg : target_imgs)
                    stage.addActor(timg);

                for (DragAndDrop.Target t : targets)
                    dragAndDrop.addTarget(t);

                return payload;
            }

            // FIXME: This method doesn't seem to exist in the current version of libgdx.
            //        Emulate this by using mouseMove in an InputProcessor
            //
            // This function should correct the position of the card so that it slides in
            // properly into the slot.
            //
            public void drag(InputEvent event, float x, float y, int pointer) {
                System.out.println("Drag(x, y) = " + x + ", " + y + ")");
            }

            public void dragStop(InputEvent event,
                                 float x,
                                 float y,
                                 int pointer,
                                 DragAndDrop.Payload payload,
                                 DragAndDrop.Target target) {
                if (target == null) {
                    sourceImage.setColor(1, 1, 1, 1);
                    return;
                }
                sourceImage.remove();
            }
        });
    }

    public void render(SpriteBatch batch) {
        Vector2Di bg_sz = new Vector2Di(Gdx.graphics.getWidth(), CARD_HEIGHT + 20);
        shape_renderer.begin(ShapeRenderer.ShapeType.Filled);
        shape_renderer.setColor(bgcolor);
        shape_renderer.rect(0, 0, bg_sz.getX(), bg_sz.getY());
        shape_renderer.end();

        // FIXME: The fact that we call batch begin()/end() *inside* of render makes the
        //        system very inconsistent. We have to do it in this case because stage.draw()
        //        needs to come directly after the first batch, but *before* the rendering of
        //        slot_front. And it produces odd behaviour if called between begin()/end() calls.
        batch.begin();
        batch.draw(deck_bg, deck_bg_pos.getX(), deck_bg_pos.getY());


        Vector2Di slot_bg_pos = FIRST_SLOT_POS.copy();
        for (int i = 0; i < num_slots; i++) {
            batch.draw(slot_back, slot_bg_pos.getX(), slot_bg_pos.getY());
            slot_bg_pos.add(CARD_SPACING);
        }

        for (int i = inactive_cards.size()-1; i >= 0; i--)
            inactive_cards.get(i).render(batch, 1);
        for (int i = 0; i < num_slots; i++) {
            Card c = active_cards[i];
            if (c != null)
                c.render(batch, 1);
        }
        batch.end();

        stage.draw();

        batch.begin();
        // Card slot rendering TODOs:
        // TODO: Something should probably be drawn behind and between the slots.
        // TODO: Draw different lights:
        //       none: No card.
        //       blue: executing card (light is blue for as long as the robot animation lasts)
        //       green: card ready.
        //       red flash: card execution aborted mid-way (ran into wall, hole etc.)

        Vector2Di slot_pos = FIRST_SLOT_POS.copy();
        for (int i = 0; i < num_slots; i++) {
            batch.draw(slot_front, slot_pos.getX(), slot_pos.getY());
            slot_pos.add(CARD_SPACING);
        }
        batch.end();
    }
}
