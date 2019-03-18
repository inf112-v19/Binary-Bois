package inf112.skeleton.app;

import org.junit.Test;

import static org.junit.Assert.*;

public class Vector2DTest {
    public static Vector2Di mkRot(double rot) {
        Vector2Di vec = new Vector2Di(1, 0);
        vec.rotate(rot);
        System.out.println(rot);
        System.out.println(vec);
        return vec;
    }

    @Test
    public void testRotateAndAngle() {
        for (double rot = 0; rot <= 180.0; rot += 45.0)
            assertEquals(rot, mkRot(rot).angle(), 0.0000001);
    }
}