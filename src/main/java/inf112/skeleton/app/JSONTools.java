package inf112.skeleton.app;

import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

class SpecTarget {
    Class<?> cls = null;
    Map<String, SpecTarget> subspec = null;

    public SpecTarget(Class<?> cls) {
        this.cls = cls;
    }

    public SpecTarget(Map<String, SpecTarget> subspec) {
        this.subspec = subspec;
    }
}

public class JSONTools {
    public static final int MAX_SPEC_DEPTH = 64;

    private static boolean check(JSONObject obj, Map<String, SpecTarget> spec, int depth) {
        if (depth >= MAX_SPEC_DEPTH)
            return false;

        // Keys of subspecs to be checked
        ArrayList<String> later = new ArrayList<>();

        for (Map.Entry<String, SpecTarget> entry : spec.entrySet()) {
            if (obj.get(entry.getKey()) == null)
                return false;

            // Check if we've encountered a subspec, if so add it to be checked later.
            if (entry.getValue().cls == null) {
                later.add(entry.getKey());
                continue;
            }

            // Check if it has the proper type
            if (!entry.getValue().cls.isInstance(obj.get(entry.getKey())))
                return false;
        }

        for (String key : later) {
            Map<String, SpecTarget> subspec = spec.get(key).subspec;
            // We assume that specs are well-formed
            assert subspec != null;
            // Confirm that the target of the subspec is actually another JSONObject
            if (!(obj.get(key) instanceof JSONObject))
                return false;
            // Check the subspec
            if (!check((JSONObject) obj.get(key), subspec, depth+1))
                return false;
        }

        return true;
    }

    /**
     * Check if a JSON document conforms to a spec.
     *
     * @param obj The JSON object to be checked.
     * @param spec The specification for the document.
     * @return Whether or not the JSON document conforms to the spec.
     */
    public static boolean checkSpec(JSONObject obj, Map<String, SpecTarget> spec) {
        return check(obj, spec, 0);
    }

    /**
     * TODO: Implement this.
     *
     * @param path
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static Map<String, SpecTarget> loadSpec(String path) throws IOException, FileNotFoundException {
        try (BufferedReader istream = new BufferedReader(new FileReader(path))) {
        }
        throw new NotImplementedException();
    }
}
