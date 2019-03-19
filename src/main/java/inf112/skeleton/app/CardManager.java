package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import java.util.ArrayList;

/**
 * Handles everything related to drawing and interacting with cards using
 * the mouse.
 */
public class CardManager extends Renderable {
    private final int CARD_WIDTH = 175;
    private final Vector2Di CARD_SPACING = new Vector2Di(CARD_WIDTH + 10, 0);
    private final Vector2Di FIRST_CARD_POS = new Vector2Di(0, 300);
    private final Vector2Di FIRST_SLOT_POS;
    private final float MOVE_TIME = 0.5f;
    private Vector2Di start_pos;

    private ArrayList<Card> inactive_cards = new ArrayList<>();
    private ArrayList<Card> active_cards = new ArrayList<>();
    private Texture slot_back;
    private Texture slot_front;
    private int num_slots;

    private Stage stage;

    public CardManager(Vector2Di deck_pos, int num_slots) throws NoSuchResource {
        start_pos = deck_pos;
        stage = new Stage();
        slot_back = Resources.getTexture("cards/175x250/slot/slot_back.png");
        slot_front = Resources.getTexture("cards/175x250/slot/slot_front.png");
        this.num_slots = num_slots;
        FIRST_SLOT_POS = new Vector2Di(0, 0);
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
        for (Card c : inactive_cards) {
            c.addAnimation(Animation.idle(idle_t += 0.1f));
            c.addAnimation(Animation.moveTo(c, card_pos, MOVE_TIME));
            card_pos.add(CARD_SPACING);

            // Add drag event:
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

    @Override
    public void render(SpriteBatch batch, Vector2Di pos) {
        // FIXME: The fact that we call batch begin()/end() *inside* of render makes the
        //        system very inconsistent. We have to do it in this case because stage.draw()
        //        needs to come directly after the first batch, but *before* the rendering of
        //        slot_front. And it produces odd behaviour if called between begin()/end() calls.
        batch.begin();
        for (int i = inactive_cards.size()-1; i >= 0; i--)
            inactive_cards.get(i).render(batch, 1);

        Vector2Di slot_bg_pos = FIRST_SLOT_POS.copy();
        for (int i = 0; i < num_slots; i++) {
            batch.draw(slot_back, slot_bg_pos.getX(), slot_bg_pos.getY());
            slot_bg_pos.add(CARD_SPACING);
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
