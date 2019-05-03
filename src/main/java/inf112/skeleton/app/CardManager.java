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
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Consumer;

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
    private Card[] total_active_cards;
    private Texture slot_back;
    private Texture slot_front;
    private Texture slot_sep;
    private Texture deck_bg;
    private Vector2Di deck_bg_pos;
    private ShapeRenderer shape_renderer;
    private final float bg_gray = 0.40f;
    private Color bgcolor = new Color(bg_gray, bg_gray, bg_gray, 1.0f);
    private int cards_moving = 0;
    private Vector2Di mouse_start_drag_pos = null;
    private Card dragged_card = null;
    private DragAndDrop dragndrop;
    private Skin ui_skin;
    private HashMap<Object, DragAndDrop.Source> card_drag_sources = new HashMap<>();
    private HashMap<Object, Image> card_drag_source_images = new HashMap<>();
    private Vector2Di mouse_pos = new Vector2Di(0, 0);
    private int numberofcards = 0;
    /** This callback gets executed when there is a change in the active_cards array. */
    private Consumer<Card[]> on_change_cb;
    int cardsScrolledBy;
    boolean cardsAutoHidden;
    public static final int NUM_ACTIVE_SLOTS = 5;

    private Stage stage;

    private class DragData {
        public Card card;
        public Image source_image;
        public DragData(Card c, Image source_image) {
            this.card = c;
            this.source_image = source_image;
        }
    }

    public CardManager() throws NoSuchResource {
        start_pos = new Vector2Di(CARD_SPACING.getX() * NUM_ACTIVE_SLOTS + 20, 10);
        stage = new Stage();
        slot_back = Resources.getTexture("cards/175x250/slot/slot_back.png");
        slot_front = Resources.getTexture("cards/175x250/slot/slot_front.png");
        slot_sep = Resources.getTexture("cards/175x250/slot/separator.png");
        deck_bg = Resources.getTexture("cards/175x250/deck/deck_background.png");
        FIRST_SLOT_POS = new Vector2Di(0, 0);
        shape_renderer = new ShapeRenderer();
        deck_bg_pos = start_pos.copy();
        deck_bg_pos.add(DECK_BG_OFFSET);
        active_cards = new Card[NUM_ACTIVE_SLOTS];
        dragndrop = new DragAndDrop();
        ui_skin = new Skin();
        ui_skin.add("card", Resources.getTexture("cards/175x250/unknown.png"));
    }

    public ArrayList<InputProcessor> getInputProcessors() {
        ArrayList<InputProcessor> processors = new ArrayList<>();
        processors.add(this);
        processors.add(stage);
        return processors;
    }

    /**
     * Set a callback to be executed when the card order is changed.
     *
     * Note: There can only be a single onChange callback, subsequent
     * calls to this function will replace the existing callback.
     *
     * @param on_change_cb The consumer that gets executed.
     */
    public void onChange(Consumer<Card[]> on_change_cb) {
        this.on_change_cb = on_change_cb;
    }

    public void removeAllCards(Robot robot) {
        inactive_cards.clear();

        numberofcards = 0;

        if (robot.getHealth() < 6)
            numberofcards = 6 - robot.getHealth(); //He has some cards locked in place

        int top = robot.getHealth() - 6;
        top = (top < 0) ? -top : NUM_ACTIVE_SLOTS;
        for (int i = 0; i < top; i++)
            active_cards[i] = null;
    }

    public void setCards(ArrayList<Card> cards) {
        numberofcards = 0;
        inactive_cards.clear();
        inactive_cards.addAll(cards);
        for (Card c : cards){
            c.setDrawPos(start_pos.tof());
            numberofcards++;
        }


        // TODO: Add click event to activate toggle hideCards()/showCards()
        //       when the cards have been moved after showCards() there should
        //       be a dotted line around where the card deck was, clicking on that
        //       area should return the cards.
    }

    public ArrayList<Card> getActiveCards() {
        ArrayList<Card> cards = new ArrayList<>();
        Collections.addAll(cards, active_cards);
        return cards;
    }

    /** This is a test method, it does not bother to update the card positions,
     *  do not use for non-testing purposes. */
    public void setAllActiveCards() {
        for (int i = 0; i < NUM_ACTIVE_SLOTS; i++) {
            Card c = inactive_cards.get(0);
            inactive_cards.remove(0);
            active_cards[i] = c;
        }
    }

    public void showCards() {
        cardsScrolledBy = 0;
        Game.addSoundFX("showCards");
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
        if (last_card != null)
            last_card.addAnimationCallback(() -> {
                setupDragTargets();
                for (int j = 0; j < NUM_ACTIVE_SLOTS; j++) {
                    Card c = active_cards[j];
                    if (c != null)
                        makeDraggable(c);
                }
            });
    }

    public void hideCards() {
        Game.addSoundFX("hideCards");
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
        for (int i = 0; i < NUM_ACTIVE_SLOTS; i++) {
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
    public static ICommand getSequenceAsCommand(ArrayList<Card> seq) {
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

    public ICommand getSequenceAsCommand() {
        return CardManager.getSequenceAsCommand(getSequence());
    }

    // TODO: Add the deck as a drag target so that cards can be put away.
    public void setupDragTargets() {
        Vector2Di slot_pos = FIRST_SLOT_POS.copy();
        for (int i = 0; i < NUM_ACTIVE_SLOTS; i++) {
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
                            for (int i = 0; i < NUM_ACTIVE_SLOTS; i++)
                                if (active_cards[i] == c) {
                                    active_idx = i;
                                    break;
                                }
                            Card prev_c = active_cards[slot_i];
                            active_cards[slot_i] = c;
                            Image prev_source_img = (prev_c != null) ? card_drag_source_images.get(prev_c) : null;

                            // There are 4 possibilities here:

                            // 1. An inactive card dragged to an empty slot:
                            //    - Remove from inactive, and add to active_cards[slot_i]
                            if (idx != -1 && prev_c == null) {
                                inactive_cards.remove(idx);
                                moveCards(-CARD_SPACING.getX(), idx, 0.15f);
                            }
                            // 2. An inactive card dragged to an occupied slot
                            //    - Swap the two cards.
                            if (idx != -1 && prev_c != null) {
                                inactive_cards.set(idx, prev_c);
                                resetDrag(prev_c);
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

                            if (idx != -1) {
                                makeUnDraggable(c);
                                resetDrag(c);
                            }

                            if (prev_c != null) {
                                Vector2Df source_pos = new Vector2Df(source.getActor().getX(), source.getActor().getY());
                                prev_source_img.setBounds(
                                        source_pos.getX(),
                                        source_pos.getY(),
                                        source_image.getImageWidth(),
                                        source_image.getImageHeight());
                                prev_c.addAnimation(Animation.moveTo(prev_c, source_pos.toi(), 0.5f));
                                // Hide source image
                                prev_source_img.setColor(0f, 0f, 0f, 0f);
                                // Show source image when animation is finished.


                                /* Not sure why, but this messes with the autoHide. Everything seems to work without
                                prev_c.addAnimationCallback(() -> prev_source_img.setColor(1f, 1f, 1f, 1f)); */
                            }

                            source_image.setBounds(cur_slot_pos.getX(), cur_slot_pos.getY(), card_w, card_h);

                            // Send new card order to the on_change callback.
                            if (on_change_cb != null)
                                on_change_cb.accept(active_cards);
                        }
                    });
            slot_pos.add(CARD_SPACING);
        }

        Image target_img = new Image(ui_skin, "card");
        target_img.setColor(0, 0, 0, 0);
        Vector2Di pos = deck_bg_pos.copy();
        pos.sub(DECK_BG_OFFSET);
        target_img.setBounds(pos.getX(), pos.getY(), card_w, card_h);
        stage.addActor(target_img);

        dragndrop.addTarget(
                new DragAndDrop.Target(target_img) {
                    public boolean drag (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                        getActor().setColor(1.0f, 0.25f, 0.25f, 1.00f);
                        return true;
                    }

                    public void reset (DragAndDrop.Source source, DragAndDrop.Payload payload) {
                        getActor().setColor(0, 0, 0, 0);
                    }

                    public void drop (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                        DragData data = (DragData) payload.getObject();
                        Card c = data.card;
                        Image source_image = data.source_image;
                        c.setDrawPos(pos.tof());
                        c.show();
                        int idx = inactive_cards.indexOf(c);
                        int active_idx = -1;
                        for (int i = 0; i < NUM_ACTIVE_SLOTS; i++)
                            if (active_cards[i] == c) {
                                active_idx = i;
                                break;
                            }

                        // There are 2 possibilities here:
                        // TODO: After putting a card back in the deck it should probably join the other inactive
                        //       cards at the top if they are shown.

                        // 1. An inactive card dragged to the deck
                        if (idx != -1) {
                            // Do nothing
                        }
                        // 2. An active card dragged to the deck
                        //    - Swap the two cards.
                        if (active_idx != -1) {
                            active_cards[active_idx] = null;
                            inactive_cards.add(c);
                            makeUnDraggable(c);
                            resetDrag(c);
                        }

                        source_image.setBounds(pos.getX(), pos.getY(), card_w, card_h);

                        // Send new card order to the on_change callback.
                        if (on_change_cb != null)
                            on_change_cb.accept(active_cards);
                    }
                });
    }

    public void makeUnDraggable(Card c) {
        Image source_image = card_drag_source_images.get(c);
        DragAndDrop.Source source = card_drag_sources.get(c);
        if (source_image != null)
            source_image.remove();
        if (source != null)
            dragndrop.removeSource(source);
    }

    public void resetDrag(Card c) {
        Image source_image = card_drag_source_images.get(c);
        DragAndDrop.Source source = card_drag_sources.get(c);
        if (source_image != null)
            stage.addActor(source_image);
        if (source != null)
            dragndrop.addSource(source);
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

        DragAndDrop.Source source = new DragAndDrop.Source(source_image) {
            public DragAndDrop.Payload dragStart (InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(new CardManager.DragData(c, source_image));
                Image img = new Image(skin, "card");
                img.setBounds(80, 80, card_w, card_h);
                c.hide();

                mouse_start_drag_pos = new Vector2Di((int)x, (int)y);
                dragged_card = c;

                System.out.println("Start drag: " + new Vector2Df(x, y).toi());
                payload.setDragActor(img);
                dragndrop.setDragActorPosition(x, y - img.getHeight());
                source_image.setColor(0.00f, 0.00f, 0.00f, 0.00f);

                for (int i = 0; i < NUM_ACTIVE_SLOTS; i++) {
                    Card ac = active_cards[i];
                    if (ac != null && ac != c)
                        makeUnDraggable(ac);
                }

                return payload;
            }

            // FIXME: This method doesn't seem to exist in the current version of libgdx.
            //        Emulate this by using mouseMove in an InputProcessor
            // UPDATE: The stage seems to get exclusive mouse access after the drag has started,
            //         this appears to happen no matter the order they are added to the input
            //         multiplexer. You'll have to use the scrollwheel as a temporary solution
            //         and investigate how to retrieve the drag(x, y) events.
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
                c.show();

                for (int i = 0; i < NUM_ACTIVE_SLOTS; i++) {
                    Card ac = active_cards[i];
                    if (ac != null && ac != c)
                        resetDrag(ac);
                }

                mouse_start_drag_pos = null;
                dragged_card = null;
                Game.addSoundFX("snapCard");
                if(inactive_cards.size() == numberofcards - NUM_ACTIVE_SLOTS && !cardsAutoHidden){
                    autoHideCards();
                }
            }
        };

        dragndrop.addSource(source);

        card_drag_source_images.put(c, source_image);
        card_drag_sources.put(c, source);

    }

    public void menuRender(SpriteBatch batch) {
        batch.begin();
        Vector2Di slot_pos = FIRST_SLOT_POS.copy();
        batch.draw(slot_back, (Gdx.graphics.getWidth()-slot_back.getWidth())/2, slot_pos.getY());
        batch.draw(slot_front, (Gdx.graphics.getWidth()-slot_front.getWidth())/2, slot_pos.getY());
        batch.end();

        stage.draw();
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
        for (int i = 0; i < NUM_ACTIVE_SLOTS; i++) {
            batch.draw(slot_back, slot_bg_pos.getX(), slot_bg_pos.getY());
            slot_bg_pos.add(CARD_SPACING);
        }

        for (int i = 0; i < NUM_ACTIVE_SLOTS; i++) {
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
        for (int i = 0; i < NUM_ACTIVE_SLOTS; i++) {
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
        int mouseX = mouse_pos.getX();
        int mouseY = mouse_pos.getY();
        int deck_bgX = start_pos.getX();
        int deck_bgY = start_pos.getY();

        if ( (mouseX > deck_bgX && mouseX <= deck_bgX+card_w) && (Gdx.graphics.getHeight() - mouseY <  deck_bgY + card_h)){
            showCards();
        }

        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        mouse_pos.set(x, y);

        if(mouse_pos.getX() < 230){
            if(mouse_pos.getY() < 525 && mouse_pos.getY() > 395){
                if(cardsAutoHidden){
                    returnFromAutoHideCards();
                }
            }

        }
        return false;
    }

    // TODO: Add minimum/maximum left/right scrolling for cards
    public void moveAllCards(int dist, float t) {
        moveCards(dist, 0, t);
    }

    public void moveCards(int dist, int idx, float t) {
        Vector2Di mov = new Vector2Di(dist, 0);
        ArrayList<Image> src_images = new ArrayList<>();
        for (Card c : inactive_cards) {
            Image src = card_drag_source_images.get(c);
            if (src == null)
                return;
            src_images.add(src);
        }
        for (int i = idx; i < inactive_cards.size(); i++) {
            Card c = inactive_cards.get(i);
            c.addAnimation(Animation.moveBy(mov, t));
            Image source_image = src_images.get(i);
            Vector2Di newpos = c.getFinalAnimationPos(1);
            source_image.setColor(0, 0, 0, 0);
            source_image.setBounds(newpos.getX(), newpos.getY(), card_w, card_h);
        }
    }

    public void autoHideCards(){
        Vector2Di mov = new Vector2Di(-40, -120);
        ArrayList<Image> src_images = new ArrayList<>();
        for (Card c : inactive_cards) {
            Image src = card_drag_source_images.get(c);
            if (src == null)
                return;
            src_images.add(src);
        }
        for (int i = 0; i < inactive_cards.size(); i++) {
            Card c = inactive_cards.get(i);
            if(i > 0){mov.add(new Vector2Di(-140, 0));}

            c.addAnimation(new Animation(mov.tof(), 360, -0.65f, 1f));

            Image source_image = src_images.get(i);
            Vector2Di newpos = c.getFinalAnimationPos(1);
            source_image.setColor(0, 0, 0, 0);
            source_image.setBounds(newpos.getX(), newpos.getY(), card_w, card_h);
        }
        cardsAutoHidden = true;
    }

    public void returnFromAutoHideCards(){
        Vector2Di mov = new Vector2Di(40, 120);
        ArrayList<Image> src_images = new ArrayList<>();
        for (Card c : inactive_cards) {
            Image src = card_drag_source_images.get(c);
            if (src == null)
                return;
            src_images.add(src);
        }
        for (int i = 0; i < inactive_cards.size(); i++) {
            Card c = inactive_cards.get(i);
            if(i > 0){mov.add(new Vector2Di(140, 0));}

            c.addAnimation(new Animation(mov.tof(), -360, 0.65f, 1f));

            Image source_image = src_images.get(i);
            Vector2Di newpos = c.getFinalAnimationPos(1);
            source_image.setColor(0, 0, 0, 0);
            source_image.setBounds(newpos.getX(), newpos.getY(), card_w, card_h);
        }
        cardsAutoHidden = false;
    }


    @Override
    public boolean scrolled(int i) {
        if (dragged_card == null &&
            mouse_pos.getY() > (FIRST_CARD_POS.getY() - (card_h/2)) &&
            mouse_pos.getY() < (FIRST_CARD_POS.getY() + (card_h/2)))
        {
            cardsScrolledBy += i;

            if(cardsScrolledBy >= -17 && cardsScrolledBy <=0){
                moveAllCards(i * 30, 0.05f);
            } else if(cardsScrolledBy > 0){
                cardsScrolledBy = 0;
            } else if (cardsScrolledBy < -17){
                cardsScrolledBy = -17;
            }


            return true;
        }
        return false;
    }
}
