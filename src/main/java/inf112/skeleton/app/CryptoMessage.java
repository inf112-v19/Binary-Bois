package inf112.skeleton.app;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import static inf112.skeleton.app.CryptoTools.decryptAES;
import static inf112.skeleton.app.CryptoTools.encryptAES;

public class CryptoMessage {
    // FIXME: There's an error in the MAGIC_LENGTH handling code causing some random data
    //        to be included in the decrypted plaintext.
    public static final int MAGIC_LENGTH = 0;
    private byte iv[];
    private byte ciphertext[];
    private JSONObject meta;

    public CryptoMessage(String key, String plaintext, JSONObject meta) {
        SecureRandom sr = new SecureRandom();
        iv = new byte[16];
        sr.nextBytes(iv);
        byte magic[] = new byte[MAGIC_LENGTH];
        sr.nextBytes(magic);
        byte plaintext_b[] = plaintext.getBytes(StandardCharsets.UTF_8);
        byte plaintext_full[] = new byte[MAGIC_LENGTH + plaintext_b.length];
        System.arraycopy(magic, 0, plaintext_full, 0, MAGIC_LENGTH);
        System.arraycopy(plaintext_b, 0, plaintext_full, MAGIC_LENGTH, plaintext_full.length - MAGIC_LENGTH);
        ciphertext = encryptAES(key, iv, new String(plaintext_full, StandardCharsets.UTF_8));
        this.meta = meta;
    }

    public CryptoMessage(String key, String plaintext) {
        this(key, plaintext, new JSONObject());
    }

    public CryptoMessage(byte iv[], byte ciphertext[], JSONObject meta) {
        this.iv = iv;
        this.ciphertext = ciphertext;
        this.meta = meta;
    }

    public CryptoMessage(byte iv[], byte ciphertext[]) {
        this(iv, ciphertext, new JSONObject());
    }

    public String decrypt(String key) throws DecryptionException {
        byte magic_plaintext[] = decryptAES(key, iv, ciphertext);
        byte plaintext[] = new byte[magic_plaintext.length - MAGIC_LENGTH];
        System.arraycopy(magic_plaintext, MAGIC_LENGTH, plaintext, 0, magic_plaintext.length - MAGIC_LENGTH);
        return new String(plaintext, StandardCharsets.UTF_8);
    }

    /**
     * Create a CryptoMessage from a JSON string.
     * @param json_text
     */
    public static CryptoMessage fromJSON(String json_text) throws JSONException {
        JSONObject obj = new JSONObject(json_text);
        Base64.Decoder b64decoder = Base64.getDecoder();
        if (!JSONTools.checkSpec(obj, JSONSpecs.encrypted_message))
            throw new JSONException("Spec check failed");
        byte iv[] = b64decoder.decode((String) obj.get("iv"));
        byte ciphertext[] = b64decoder.decode((String) obj.get("ciphertext"));
        return new CryptoMessage(iv, ciphertext, new JSONObject());
    }

    public byte[] getInitVector() {
        return iv;
    }

    /**
     * Represent the cryptographic message as a JSON object.
     *
     * @return JSON string.
     */
    public String asJSON() {
        JSONObject obj = new JSONObject();
        Base64.Encoder b64encoder = Base64.getEncoder();
        obj.put("iv", b64encoder.encodeToString(iv));
        obj.put("ciphertext", b64encoder.encodeToString(ciphertext));
        obj.put("meta", this.meta);
        return obj.toString();
    }
}
