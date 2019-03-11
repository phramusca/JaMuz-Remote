package phramusca.com.jamuzkids;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class Encryption {

    private static final String ALGO = "AES";

    public static String encrypt(String Data, String secret) {
        try {
            Key key = generateKey(secret);
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(Data.getBytes());
            return Base64.encodeToString(encVal, Base64.DEFAULT);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            return "";
        }
    }

    public static String decrypt(String encryptedData, String secret) {
        try {
            Key key = generateKey(secret);
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = Base64.decode(encryptedData, Base64.DEFAULT);
            byte[] decValue = c.doFinal(decodedValue);
            return new String(decValue);
        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            return "";
        }
    }

    private static Key generateKey(String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] key = secret.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit
        return new SecretKeySpec(key, ALGO);
    }
}
