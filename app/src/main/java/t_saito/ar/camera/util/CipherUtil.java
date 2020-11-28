
package t_saito.ar.camera.util;

import android.annotation.SuppressLint;
import android.util.Base64;


import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;


/**
 * 暗号ユーティリティ
 */
public class CipherUtil {

    private static final String TAG = CipherUtil.class.getSimpleName();

    private static final String ALGOLITHM = "AES";

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String TRANSFORMATION_HELP = "AES/ECB/PKCS5Padding";

    private static final char DIGITS[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String encryptStringToHexString(String key, String string, String iv) {
        return encryptBytesToHexString(key, string.getBytes(), iv);
    }

    public static byte[] encryptBytesToBytes(String key, String string) {
        return encryptBytesToBytes(key, string.getBytes());
    }

    public static String encryptBytesToHexString(String key, byte[] bytes, String iv) {
        byte[] encrypted = encryptBytesToBytes(key, bytes, iv);
        return encrypted != null ? encodeHexString(encrypted) : null;
    }

    @SuppressLint ("TrulyRandom")
    public static byte[] encryptBytesToBytes(String key, byte[] bytes, String iv) {
        try {
            byte[] decodeKey = decodeHex(key);
            byte[] decodeIV = decodeHex(iv);
            IvParameterSpec spec = new IvParameterSpec(decodeIV);
            SecretKeySpec sksSpec = new SecretKeySpec(decodeKey, ALGOLITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, sksSpec, spec);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            Timber.w("encrypt failed");
        }
        return null;
    }

    @SuppressLint ("TrulyRandom")
    public static byte[] encryptBytesToBytes(String key, byte[] bytes) {
        try {
            byte[] decodeKey = decodeHex(key);
            SecretKeySpec sksSpec = new SecretKeySpec(decodeKey, ALGOLITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION_HELP);
            cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            Timber.w("encrypt failed");
        }
        return null;
    }

    public static String decryptHexStringToString(String key, String encryptedHexString, String iv) {
        byte[] ret = decryptHexStringToBytes(key, encryptedHexString, iv);
        return ret != null ? new String(ret) : null;
    }

    public static byte[] decryptHexStringToBytes(String key, String encryptedHexString, String iv) {
        return decryptBytesToBytes(key, decodeHex(encryptedHexString), iv);
    }

    public static byte[] decryptBytesToBytes(String key, byte[] encryptedBytes, String iv) {
        try {
            byte[] decodeKey = decodeHex(key);
            byte[] decodeIV = decodeHex(iv);

            IvParameterSpec spec = new IvParameterSpec(decodeIV);
            SecretKeySpec sksSpec = new SecretKeySpec(decodeKey, ALGOLITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, sksSpec, spec);
            return cipher.doFinal(encryptedBytes);
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public static String decryptHexByteToString(String key, byte[] decryptedBytes) {
        byte[] ret = decryptBytesToBytes(key, decryptedBytes);
        return ret != null ? new String(ret) : null;
    }

    public static byte[] decryptBytesToBytes(String key, byte[] decryptedBytes) {
        try {
            byte[] decodeKey = decodeHex(key);

            SecretKeySpec sksSpec = new SecretKeySpec(decodeKey, ALGOLITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION_HELP);
            cipher.init(Cipher.DECRYPT_MODE, sksSpec);
            return cipher.doFinal(decryptedBytes);
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public static String encodeHexString(byte data[]) {
        return new String(encodeHex(data));
    }

    private static char[] encodeHex(byte data[]) {
        int l = data.length;
        char out[] = new char[l << 1];
        int i = 0;
        int j = 0;
        for (; i < l; i++) {
            out[j++] = DIGITS[(240 & data[i]) >>> 4];
            out[j++] = DIGITS[15 & data[i]];
        }

        return out;
    }

    public static byte[] decodeHex(String str) throws IllegalArgumentException {
        char[] data = str.toLowerCase(Locale.ENGLISH).toCharArray();
        int len = data.length;
        if ((len & 1) != 0)
            throw new IllegalArgumentException("Odd number of characters.");
        byte out[] = new byte[len >> 1];
        int i = 0;
        for (int j = 0; j < len;) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f |= toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 255);
            i++;
        }

        return out;
    }

    private static int toDigit(char ch, int index) throws IllegalArgumentException {
        int digit = Character.digit(ch, 16);
        if (digit == -1)
            throw new IllegalArgumentException("Illegal hexadecimal charcter " + ch + " at index "
                    + index);
        else
            return digit;
    }

    private static final String CHARSET = "UTF-8";

    /**
     * Base64エンコード
     *
     * @param target エンコード対象文字列
     * @return エンコード後文字列
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static String base64encode(String target) throws IOException, IllegalArgumentException {
        // Base64エンコード
        String encodeToString = Base64.encodeToString(target.getBytes(), Base64.DEFAULT);
        // リプレース
        String replaceToString = encodeToString.replace('/', '_').replace('+', '-').replace('=', '*');
        // URLエンコード
        return URLEncoder.encode(replaceToString);
    }

    /**
     * Base64デコード
     *
     * @param target デコード対象文字列
     * @return デコード後文字列
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static String base64decode(String target) throws IOException, IllegalArgumentException {
        // URLデコード
        String urlDecodeToString = URLDecoder.decode(target);
        // リプレース
        String replaceToString = urlDecodeToString.replace('_', '/').replace('-', '+').replace('*', '=');
        // Base64デコード
        byte[] decodeToBytes = Base64.decode(replaceToString.getBytes(), Base64.DEFAULT);
        return new String(decodeToBytes);
    }

    /**
     * 指定の値をMD5で暗号化する
     *
     * @param cleartext 暗号対象文字列
     * @return 暗号後文字列
     */
    public static String md5(String cleartext) {
        String result = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(cleartext.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte md5Byte : digest.digest()) {
                String h = Integer.toHexString(0xFF & md5Byte);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            result = hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Timber.e(e);
        }
        return result;
    }
}
