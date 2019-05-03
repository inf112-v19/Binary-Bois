package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.io.IOException;

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

    final RoboRally game;
    OrthographicCamera camera;
    final String hostname = "localhost";

    public MainMenu(final RoboRally game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "RoboRally!!! ", 100, 150);
        game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            try {
                game.setScreen(new GameLoop(hostname, "abc123", game));
            } catch (IOException e) {
                System.out.println("Exception caught in MainMenu render(): " + e);
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