package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles everything related to drawing and interacting with cards using
 * the mouse.
 */
public class CardManager implements InputProcessor {
    private final int card_w = 175;
    private final int card_h = 250;
    private final Vector2Di CARD_SPACING = new Vector2Di(card_w + 10, 0);
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
    private int cards_moving = 0;
    private Vector2Di mouse_start_drag_pos = null;
    private Card dragged_card = null;
    private DragAndDrop dragndrop;
    private Skin ui_skin;
    private HashMap<Object, Image> card_drag_sources = new HashMap<>();

    private Stage stage;

    private class DragData {
        public Card card;
        public Image source_image;
        public DragData(Card c, Image source_image) {
            this.card = c;
            this.source_image = source_image;
        }
    }

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
        dragndrop = new DragAndDrop();
        ui_skin = new Skin();
        ui_skin.add("card", Resources.getTexture("cards/175x250/unknown.png"));
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
        cards_moving = inactive_cards.size();
        Card last_card = null;
        for (Card c : inactive_cards) {
            c.addAnimation(Animation.idle(idle_t += 0.1f));
            long ticks = c.addAnimation(Animation.moveTo(c, card_pos, MOVE_TIME));
            card_pos.add(CARD_SPACING);

            // Add drag event:
            Renderable.addAnimationCallback(ticks, () -> {
                makeDraggable(c);
                cards_moving--;
            });

            last_card = c;
        }
        for (int j = 0; j < num_slots; j++) {
            Card c = active_cards[j];
            if (c != null)
                makeDraggable(c);
        }
        if (last_card != null)
            last_card.addAnimationCallback(this::setupDragTargets);
    }

    public void hideCards() {
        float idle_t = 0.0f;
        for (int i = inactive_cards.size()-1; i >= 0; i--) {
            Card c = inactive_cards.get(i);
            c.addAnimation(Animation.idle(idle_t += 0.1f));
            c.addAnimation(Animation.moveTo(c, start_pos, MOVE_TIME));

            // Remove drag event:
        }
        dragndrop.clear();
        stage.clear();
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

        if (seq.size() == 0)
            return (int amount, Robot r, Game g) -> true;

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

    // TODO: Add the deck as a drag target so that cards can be put away.
    public void setupDragTargets() {
        Vector2Di slot_pos = FIRST_SLOT_POS.copy();
        for (int i = 0; i < num_slots; i++) {
            Image target_img = new Image(ui_skin, "card");
            target_img.setColor(0, 0, 0, 0.0f);
            target_img.setBounds(slot_pos.getX(), slot_pos.getY(), card_w, card_h);
            Vector2Di cur_slot_pos = slot_pos.copy();
            stage.addActor(target_img);
            final int slot_i = i;
            dragndrop.addTarget(
                    new DragAndDrop.Target(target_img) {
                        public boolean drag (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            getActor().setColor(0.25f, 1.00f, 0.25f, 0.25f);
                            return true;
                        }

                        public void reset (DragAndDrop.Source source, DragAndDrop.Payload payload) {
                            getActor().setColor(0, 0, 0, 0);
                        }

                        public void drop (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                            DragData data = (DragData) payload.getObject();
                            Card c = data.card;
                            Image source_image = data.source_image;
                            c.setDrawPos(cur_slot_pos.tof());
                            c.show();
                            int idx = inactive_cards.indexOf(c);
                            int active_idx = -1;
                            for (int i = 0; i < num_slots; i++)
                                if (active_cards[i] == c) {
                                    active_idx = i;
                                    break;
                                }
                            Card prev_c = active_cards[slot_i];
                            active_cards[slot_i] = c;
                            Image prev_source = (prev_c != null) ? card_drag_sources.get(prev_c) : null;

                            // There are several possibilities here:

                            // 1. An inactive card dragged to an empty slot:
                            //    - Remove from inactive, and add to active_cards[slot_i]
                            if (idx != -1 && prev_c == null) {
                                inactive_cards.remove(idx);
                                // TODO: Reorganize cards
                            }
                            // 2. An inactive card dragged to an occupied slot
                            //    - Swap the two cards.
                            if (idx != -1 && prev_c != null) {
                                inactive_cards.set(idx, prev_c);
                            }
                            // 3. An active card dragged to an empty slot
                            //    - Set active_cards[prev_slot_i] to null and add to active_cards[slot_i]
                            if (active_idx != -1 && prev_c == null) {
                                active_cards[active_idx] = null;
                            }
                            // 4. An active card dragged to an occupied slot
                            //    - Swap the two cards
                            if (active_idx != -1 && prev_c != null) {
                                active_cards[active_idx] = prev_c;
                            }

                            if (prev_c != null) {
                                Vector2Df source_pos = new Vector2Df(source.getActor().getX(), source.getActor().getY());
                                prev_source.setBounds(
                                        source_pos.getX(),
                                        source_pos.getY(),
                                        source_image.getImageWidth(),
                                        source_image.getImageHeight());
                                prev_c.addAnimation(Animation.moveTo(prev_c, source_pos.toi(), 0.5f));
                                // Hide source image
                                prev_source.setColor(0f, 0f, 0f, 0f);
                                // Show source image when animation is finished.
                                prev_c.addAnimationCallback(() -> prev_source.setColor(1f, 1f, 1f, 1f));
                            }

                            source_image.setBounds(cur_slot_pos.getX(), cur_slot_pos.getY(), card_w, card_h);
                        }
                    });
            slot_pos.add(CARD_SPACING);
        }
    }

    public void makeDraggable(Card c) {
        Skin skin = new Skin();
        skin.add("card", c.getTexture());

        Image source_image = new Image(skin, "card");
        Vector2Di draw_pos = c.getFinalAnimationPos(1);
        source_image.setBounds(draw_pos.getX(), draw_pos.getY(), card_w, card_h);
        Vector2Di orig_pos = draw_pos.copy();
        source_image.setColor(0, 0, 0, 0);
        stage.addActor(source_image);

        card_drag_sources.put(c, source_image);

        dragndrop.addSource(new DragAndDrop.Source(source_image) {
            public DragAndDrop.Payload dragStart (InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(new CardManager.DragData(c, source_image));
                Image img = new Image(skin, "card");
                img.setBounds(80, 80, card_w, card_h);
                c.hide();

                System.out.println("Start drag: " + new Vector2Df(x, y).toi());
                payload.setDragActor(img);
                dragndrop.setDragActorPosition(x, y - img.getHeight());
                source_image.setColor(0.00f, 0.00f, 0.00f, 0.00f);

                return payload;
            }

            // FIXME: This method doesn't seem to exist in the current version of libgdx.
            //        Emulate this by using mouseMove in an InputProcessor
            //
            // This function should correct the position of the card so that it slides in
            // properly into the slot.
            //
            public void drag(InputEvent event, float x, float y, int pointer) {
                System.out.println("Drag(x, y) = (" + x + ", " + y + ")");
            }

            public void dragStop(InputEvent event,
                                 float x,
                                 float y,
                                 int pointer,
                                 DragAndDrop.Payload payload,
                                 DragAndDrop.Target target) {
                source_image.setColor(1, 1, 1, 1);
            }
        });
    }

    public void render(SpriteBatch batch) {
        Vector2Di bg_sz = new Vector2Di(Gdx.graphics.getWidth(), card_h + 20);
        shape_renderer.begin(ShapeRenderer.ShapeType.Filled);
        shape_renderer.setColor(bgcolor);
        shape_renderer.rect(0, 0, bg_sz.getX(), bg_sz.getY());
        shape_renderer.end();

        batch.begin();
        batch.draw(deck_bg, deck_bg_pos.getX(), deck_bg_pos.getY());

        Vector2Di slot_bg_pos = FIRST_SLOT_POS.copy();
        for (int i = 0; i < num_slots; i++) {
            batch.draw(slot_back, slot_bg_pos.getX(), slot_bg_pos.getY());
            slot_bg_pos.add(CARD_SPACING);
        }

        for (int i = 0; i < num_slots; i++) {
            Card c = active_cards[i];
            if (c != null)
                c.render(batch, 1);
        }

        for (int i = inactive_cards.size()-1; i >= 0; i--)
            inactive_cards.get(i).render(batch, 1);
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

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
}
