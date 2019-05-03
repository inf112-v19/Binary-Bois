package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is suggestive of how the mainMenu should be implemented.
 *
 * See: https://github.com/libgdx/libgdx/wiki/Extending-the-simple-game
 * Also: https://stackoverflow.com/questions/46276460/cannot-resolve-setscreen-method/46282919#46282919
 *
 * This also means that GameLoop should be changed to implement Screen rather than
 * ApplicationAdapter. This means we don't have to shimmy a MainMenu into the existing
 * GameLoop class.
 */
class MainMenu implements Screen {

    final String card_sz = "280x400";
    final RoboRally game;
    OrthographicCamera camera;
    final String hostname = "localhost";
    CardManager cm;
    private Texture bg;
    private String switch_to = null;

    public MainMenu(final RoboRally game) {
        this.game = game;
        try {
            this.cm = new CardManager(card_sz, false, 1);
        } catch (NoSuchResource e) {
            System.out.println(e + "caught in MainMenu constructor");
        }
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(Commands.none, "ai_game", 0, 0, 0));
        cards.add(new Card(Commands.none, "join_game", 0, 1, 0));
        cards.add(new Card(Commands.none, "host_game", 0, 2, 0));
        try {
            for (Card c : cards)
                c.initTexture(card_sz);
            bg = Resources.getTexture("background.png");
        } catch (NoSuchResource e) {
            e.printStackTrace();
            SystemPanic.panic("Unable to load card textures for menu.");
        }
        cm.setCards(cards);
        cm.showCards();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);

        InputMultiplexer mul = new InputMultiplexer();
        for (InputProcessor inp : cm.getInputProcessors())
            mul.addProcessor(inp);
        Gdx.input.setInputProcessor(mul);

        cm.onChange((Card[] card_arr) -> {
            if (card_arr.length == 0)
                return;
            Card c = card_arr[0];
            if (c == null)
                return;
            switch_to = c.getName();
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Renderable.updateAll();

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(bg, 0, 0);
        //game.font.getData().setScale(6.0f);
        //game.font.draw(game.batch, "RoboRally ", Gdx.graphics.getWidth()/2-200, Gdx.graphics.getHeight()/2+400);
        //game.font.getData().setScale(1);
        //game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
        game.batch.end();

        cm.drawStage();
        cm.render(game.batch);

        //if (Gdx.input.isTouched()) {
        //    try {
        //        game.setScreen(new GameLoop(hostname, "abc123", game));
        //    } catch (IOException e) {
        //        System.out.println("Exception caught in MainMenu render(): " + e);
        //    }
        //    dispose();
        //}

        if (switch_to != null) {
            switch (switch_to) {
                case "join_game":
                    game.setScreen(new JoinGameMenu(game));
                    System.out.println("JOIN GAME");
                break;

                case "ai_game":
                    System.out.println("AI GAME");
                break;

                case "host_game":
                    game.setScreen(new HostGameSetupMenu(game));
                    System.out.println("HOST GAME");
                break;
            }
            dispose();
        }
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}