package inf112.skeleton.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

enum SpecType {
    PRIMITIVE_TYPE,
    ARRAY,
    MAP
}

/**
 * Examples
 *
 * SpecTarget.newArraySpec(new SpecTarget(Integer.class))
 * => [1,2,3,4]
 *
 * map.put("x", new SpecTarget(Integer.class))
 * map.put("y", new SpecTarget(Integer.class))
 * SpecTarget.newArraySpec(new SpecTarget(map))
 * => [{"x": 1, "y": 2}, {"x": 3, "y": 4}]
 */
class SpecTarget {
    Object ref;
    SpecType type;

    public SpecTarget(Class<?> cls) {
        type = SpecType.PRIMITIVE_TYPE;
        this.ref = cls;
    }

    public SpecTarget(Map<String, SpecTarget> subspec) {
        type = SpecType.MAP;
        this.ref = subspec;
    }

    private SpecTarget(SpecTarget array_spec) {
        type = SpecType.ARRAY;
        this.ref = array_spec;
    }

    /**
     * Create a spec target for an array of "things"
     * @param repeating The repeating element inside of the JSONArray.
     * @return
     */
    public static SpecTarget newArraySpec(SpecTarget repeating) {
        return new SpecTarget(repeating);
    }

    public Class<?> getPrimitive() {
        return (Class<?>) ref;
    }

    @SuppressWarnings("unchecked")
    public Map<String, SpecTarget> getMap() {
        return (Map<String, SpecTarget>) ref;
    }

    public SpecTarget getArray() {
        return (SpecTarget) ref;
    }

    /**
     * Call {@link JSONTools#checkSpec(Object, SpecTarget)} with the SpecTarget.
     *
     * @param obj The object to check.
     * @throws JSONException If the object did not match the spec.
     */
    public void check(Object obj) throws JSONException {
        if (!JSONTools.checkSpec(obj, this))
            throw new JSONException("Object did not match JSON spec");
    }
}

public class JSONTools {
    public static final int MAX_SPEC_DEPTH = 64;

    private static boolean check(Object obj, SpecTarget spec, int depth) {
        if (depth >= MAX_SPEC_DEPTH)
            return false;

        switch (spec.type) {
            case PRIMITIVE_TYPE:
                return spec.getPrimitive().isInstance(obj);

            case MAP: {
                Map<String, SpecTarget> subspec = spec.getMap();

                // Confirm that the target of the subspec is actually another JSONObject
                if (!(obj instanceof JSONObject))
                    return false;

                JSONObject jobj = (JSONObject) obj;

                for (Map.Entry<String, SpecTarget> entry : subspec.entrySet()) {
                    Object subobj = jobj.get(entry.getKey());
                    if (!check(subobj, entry.getValue(), depth + 1))
                        return false;
                }

                return true;
            }

            case ARRAY: {
                if (!(obj instanceof JSONArray))
                    return false;
                SpecTarget subspec = spec.getArray();
                JSONArray li_obj = (JSONArray) obj;
                for (Object elem : li_obj)
                    if (!check(elem, subspec, depth+1))
                        return false;
                return true;
            }
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
    public static boolean checkSpec(Object obj, Map<String, SpecTarget> spec) {
        SpecTarget mapt = new SpecTarget(spec);
        return check(obj, mapt, 0);
    }

    public static boolean checkSpec(Object obj, SpecTarget spec) {
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

    // toIntArray and toIntMatrix should obviously be generic,
    // but Java generics are utter shit and will complain about
    // not being able to cast Objects to Integers (which is already
    // a hack) when using the generic variant. Interestingly the test
    // cases still pass (the assertEquals part) but even still Java
    // will refuse to cast Object->Integer (yes, even when the object is
    // instanceof Integer.)
    //
    // If you need to convert different types, just copy the code, don't
    // bother with making it work with Java generics. It never will, and
    // you'll probably end up with some ugly hacks/workarounds.

    public static int[] toIntArray(JSONArray in_arr) {
        int[] out_arr = new int[in_arr.length()];
        for (int i = 0; i < in_arr.length(); i++)
            out_arr[i] = in_arr.getInt(i);
        return out_arr;
    }

    public static int[][] toIntMatrix(JSONArray in_mat) {
        if (in_mat.length() == 0)
            return new int[0][0];
        int fst_len = ((JSONArray) in_mat.get(0)).length();
        int[][] out_mat = new int[in_mat.length()][fst_len];
        for (int i = 0; i < in_mat.length(); i++) {
            int[] arr = toIntArray(in_mat.getJSONArray(i));
            if (arr.length != fst_len)
                throw new JSONException("Invalid matrix dimensions.");
            out_mat[i] =  arr;
        }
        return out_mat;
    }
}
