package inf112.skeleton.app;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestJSONSpec {
    /**
     * Test a flat spec, i.e a spec without any subspecs.
     */
    @Test
    public void flatSpecTest() {
        Map<String, SpecTarget> spec = new HashMap<>();
        spec.put("a", new SpecTarget(String.class));
        spec.put("b", new SpecTarget(Integer.class));
        spec.put("c", new SpecTarget(String.class));

        JSONObject correct_obj = new JSONObject();
        correct_obj.put("a", "string");
        correct_obj.put("b", new Integer(123));
        correct_obj.put("c", "string2");

        if (!JSONTools.checkSpec(correct_obj, spec)) {
            fail("Correct example did not pass spec test");
        }

        JSONObject incorrect_obj = new JSONObject();
        incorrect_obj.put("a", 123);
        incorrect_obj.put("b", "string");
        incorrect_obj.put("c", 321);
        if (JSONTools.checkSpec(incorrect_obj, spec)) {
            fail("Incorrect example passed the spec test");
        }
    }

    @Test
    public void nestedSpecTest() {
        Map<String, SpecTarget> spec = new HashMap<>();
        spec.put("a", new SpecTarget(String.class));
        Map<String, SpecTarget> subspec = new HashMap<>();
        subspec.put("a", new SpecTarget(String.class));
        subspec.put("b", new SpecTarget(Integer.class));
        subspec.put("c", new SpecTarget(String.class));
        spec.put("b", new SpecTarget(subspec));

        JSONObject correct_obj = new JSONObject();
        correct_obj.put("a", "string");
        JSONObject correct_sub = new JSONObject();
        correct_sub.put("a", "string");
        correct_sub.put("b", new Integer(123));
        correct_sub.put("c", "string2");
        correct_obj.put("b", correct_sub);

        if (!JSONTools.checkSpec(correct_obj, spec)) {
            fail("Correct example did not pass spec test");
        }

        JSONObject incorrect_obj = new JSONObject();
        incorrect_obj.put("a", "string");
        JSONObject incorrect_sub = new JSONObject();
        incorrect_sub.put("a", 123);
        incorrect_sub.put("b", "string");
        incorrect_sub.put("c", 321);
        incorrect_obj.put("b", incorrect_sub);

        if (JSONTools.checkSpec(incorrect_obj, spec)) {
            fail("Incorrect example passed the spec test");
        }
    }

    @Test
    public void testGameSettingSpec() {
        JSONObject settings = new JSONObject();
        settings.put("version", "1.123123");
        settings.put("num_players", 4);
        settings.put("choosing_cards_time", 45);
        settings.put("num_starting_cards", 9);
        settings.put("map", "map2.tmx");
        JSONArray robots = new JSONArray();
        robots.put(new JSONObject("{\"x\": 0, \"y\": 0}"));
        robots.put(new JSONObject("{\"x\": 3, \"y\": 4}"));
        robots.put(new JSONObject("{\"x\": 9, \"y\": 0}"));
        robots.put(new JSONObject("{\"x\": 9, \"y\": 3}"));
        settings.put("robots", robots);
        System.out.println(settings);
        if (!JSONTools.checkSpec(settings, JSONSpecs.game_options))
            fail("Should have succeeded");
    }
}
