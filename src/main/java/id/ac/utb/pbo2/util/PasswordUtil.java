package id.ac.utb.pbo2.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
    private static final String ALGO_PREFIX = "pbkdf2";
    private static final String PBKDF2_ALGO = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hash(char[] password) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] key = derive(password, salt, ITERATIONS, KEY_BITS);
        String encodedSalt = Base64.getEncoder().withoutPadding().encodeToString(salt);
        String encodedKey = Base64.getEncoder().withoutPadding().encodeToString(key);
        Arrays.fill(salt, (byte) 0);
        Arrays.fill(key, (byte) 0);
        return ALGO_PREFIX + "$" + ITERATIONS + "$" + encodedSalt + "$" + encodedKey;
    }

    public static String hash(String password) {
        char[] chars = password.toCharArray();
        try {
            return hash(chars);
        } finally {
            Arrays.fill(chars, '\0');
        }
    }

    public static boolean matches(char[] password, String storedHash) {
        if (storedHash == null || storedHash.isBlank()) {
            return false;
        }
        if (isPbkdf2(storedHash)) {
            return verifyPbkdf2(password, storedHash);
        }
        String legacy = legacySha256(password);
        return MessageDigest.isEqual(
                legacy.getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static boolean needsRehash(String storedHash) {
        return !isPbkdf2(storedHash);
    }

    private static boolean isPbkdf2(String hash) {
        return hash != null && hash.startsWith(ALGO_PREFIX + "$");
    }

    private static boolean verifyPbkdf2(char[] password, String storedHash) {
        String[] parts = storedHash.split("\\$");
        if (parts.length != 4 || !ALGO_PREFIX.equals(parts[0])) {
            return false;
        }
        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = derive(password, salt, iterations, expected.length * 8);
            boolean match = MessageDigest.isEqual(actual, expected);
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(expected, (byte) 0);
            Arrays.fill(actual, (byte) 0);
            return match;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static byte[] derive(char[] password, byte[] salt, int iterations, int keyBits) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyBits);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGO);
            return factory.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("PBKDF2 tidak tersedia", ex);
        } finally {
            spec.clearPassword();
        }
    }

    private static String legacySha256(char[] password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            ByteBuffer encoded = StandardCharsets.UTF_8.encode(CharBuffer.wrap(password));
            byte[] input = new byte[encoded.remaining()];
            encoded.get(input);
            byte[] bytes = digest.digest(input);
            Arrays.fill(input, (byte) 0);
            StringBuilder out = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                out.append(String.format("%02x", b));
            }
            Arrays.fill(bytes, (byte) 0);
            return out.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 tidak tersedia", ex);
        }
    }
}
