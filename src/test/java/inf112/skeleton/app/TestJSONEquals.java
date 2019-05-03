package inf112.skeleton.app;

import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class TestJSONEquals {
    /**
     * Test .equals() for two nested JSONObjects
     */
    @Test
    public void jsonBuiltInEquals() {
        JSONObject obj_top = new JSONObject();
        JSONObject obj_1 = new JSONObject();
        obj_1.put("a", 1);
        obj_1.put("b", 2);
        obj_1.put("c", 3);
        JSONObject obj_2 = new JSONObject();
        obj_2.put("a", 213);
        obj_2.put("b", 4123);
        obj_2.put("c", 44123);
        JSONObject obj_3 = new JSONObject();
        obj_3.put("g", 213);
        obj_3.put("h", 4123);
        obj_3.put("l", 44123);
        obj_top.put("obj1", obj_1);
        obj_top.put("obj2", obj_2);
        obj_top.put("obj3", obj_3);

        JSONObject obj_top_2 = new JSONObject();
        JSONObject obj_1_2 = new JSONObject();
        obj_1_2.put("a", 1);
        obj_1_2.put("b", 2);
        obj_1_2.put("c", 3);
        JSONObject obj_2_2 = new JSONObject();
        obj_2_2.put("a", 213);
        obj_2_2.put("b", 4123);
        obj_2_2.put("c", 44123);
        JSONObject obj_3_2 = new JSONObject();
        obj_3_2.put("g", 213);
        obj_3_2.put("h", 4123);
        obj_3_2.put("l", 44123);
        obj_top_2.put("obj1", obj_1_2);
        obj_top_2.put("obj2", obj_2_2);
        obj_top_2.put("obj3", obj_3_2);

        if (!obj_top.equals(obj_top_2))
            fail("Objects were not equal");
    }
}
