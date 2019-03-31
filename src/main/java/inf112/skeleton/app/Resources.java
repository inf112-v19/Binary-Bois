package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import org.lwjgl.opengl.EXTAbgr;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Function;

class NoSuchResource extends Exception {
    public NoSuchResource(String filename) {
        super(filename);
    }
}

public class Resources {
    /** The cache ensures that resources aren't loaded several times, but are instead
     *  loaded once and then used everywhere. */
    private static HashMap<String, Object> cache = new HashMap<>();
    public static final String resources_path = "./resources/";

    public static void clearCache() {
        cache.clear();
    }

    public static String realPath(String rel_path) throws NoSuchResource {
        try {
            return new File(resources_path + rel_path).getCanonicalPath();
        } catch (IOException e) {
            throw new NoSuchResource(rel_path);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T get(String rel_path, Function<String, T> fn) throws NoSuchResource {
        String path = realPath(rel_path);
        if (path == null)
            throw new NoSuchResource(rel_path);
        T obj = (T) cache.get(path);
        if (obj != null)
            return obj;
        if ((obj = fn.apply(path)) == null)
            throw new NoSuchResource(rel_path);
        cache.put(path, obj);
        return obj;
    }

    public static Texture getTexture(String p) throws NoSuchResource {
        return get(p, Texture::new);
    }

    public static Music getMusic(String rp) throws NoSuchResource {
        return get(rp, (String p) -> {
            FileHandle file = new FileHandle(p);
            return Gdx.audio.newMusic(file);
        });
    }

    public static Sound getSound(String rp) throws NoSuchResource {
        return get(rp, (String p) -> {
            FileHandle file = new FileHandle(p);
            return Gdx.audio.newSound(file);
        });
    }

    public static CSV getCSV(String rp) throws NoSuchResource {
        return get(rp, (String p) -> {
            try {
                return new CSV(p);
            } catch (Exception e) {
                // Function<A, B> lambdas can't throw checked exceptions. Java is dumb. :O
                return null;
            }
        });
    }

    public static TiledMap getTiledMap(String rp) throws NoSuchResource {
        return get(rp, (String p) -> new TmxMapLoader().load(p));
    }
}
