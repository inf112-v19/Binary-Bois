package inf112.skeleton.app;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EncryptionTest {
    @Test
    public void encryptDecryptTest() {
        String key = "abc123";
        String plaintext = "My short plain-text message.";
        CryptoMessage msg = new CryptoMessage(key, plaintext);
        try {
            assertEquals(msg.decrypt(key), plaintext);
        } catch (DecryptionException e) {
            fail("Unable to decrypt");
        }
    }

    @Test
    public void encryptDecryptJSONTest() {
        String key = "abc123";
        String plaintext = "My short plain-text message.";
        CryptoMessage msg = new CryptoMessage(key, plaintext);
        String json_text = msg.asJSON();
        System.out.println(json_text);
        CryptoMessage msg2 = CryptoMessage.fromJSON(json_text);
        try {
            assertEquals(msg2.decrypt(key), plaintext);
        } catch (DecryptionException e) {
            fail("Unable to decrypt");
        }
    }
}
