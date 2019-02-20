package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import org.junit.Test;
import org.lwjgl.Sys;

public class SoundTest extends ApplicationAdapter {

    @Override
    public void create() {

        //
        System.out.print(Gdx.files.isExternalStorageAvailable());
        System.out.println("path: " + Gdx.files.getExternalStoragePath());
        /**/
    }
}