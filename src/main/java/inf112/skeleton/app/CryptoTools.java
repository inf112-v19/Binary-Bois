package inf112.skeleton.app;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

// FIXME: Actually using the iv causes an exception to be thrown.
//        for now it's just disabled but still passed around so that
//        it can eventually be enabled.

/**
 * Wrappers around cryptographic tools, primarily AES.
 */
public class CryptoTools {

    public static SecretKeySpec toKeySpec(String keystr) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte key[] = sha.digest(keystr.getBytes(StandardCharsets.UTF_8));
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            SystemPanic.panic("AES encryption is not available on this system.");
        }

        // Will never be reached. SystemPanic.panic() throws a RuntimeError.
        return null;
    }

    public static byte[] encryptAES(String keystr, byte iv[], String plaintext) {
        try {
            SecretKeySpec key = toKeySpec(keystr);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            //cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            SystemPanic.panic("AES/PKCS5Padding encryption is not available on this system.");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            SystemPanic.panic("Unable to encrypt");
        } catch (InvalidKeyException e) {
            SystemPanic.panic("toKeySpec produced a bad key");
        } /*catch (InvalidAlgorithmParameterException e) {
            SystemPanic.panic("InvalidAlgorithmParameterException");
        }*/

        // Will never be reached. SystemPanic.panic() throws a RuntimeError.
        return null;
    }

    public static byte[] decryptAES(String keystr, byte iv[], byte ciphertext[]) throws DecryptionException {
        try {
            SecretKeySpec key = toKeySpec(keystr);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            //cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(ciphertext);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            SystemPanic.panic("AES/PKCS5Padding encryption is not available on this system.");
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new DecryptionException("Unable to decrypt");
        } /*catch (InvalidAlgorithmParameterException e) {
            SystemPanic.panic("InvalidAlgorithmParameterException");
        }*/

        // Will never be reached. SystemPanic.panic() throws a RuntimeError.
        return null;
    }

}
