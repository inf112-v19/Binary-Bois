package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import org.junit.Test;

public class SoundTest {

    @Test
    public void testLoopingLazerSound(){
        Music player = Gdx.audio.newMusic(Gdx.files.internal("RoboLazer.mp3"));
        player.setLooping(true);
        player.play();
    }
}
