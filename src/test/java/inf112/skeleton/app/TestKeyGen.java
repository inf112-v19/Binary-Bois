package inf112.skeleton.app;

import org.junit.Test;

public class TestKeyGen {
    @Test
    public void testKeygen() {
        for (int i = 0; i < 1000; i++) {
            System.out.println(GameServer.generateKey());
        }
    }
}
