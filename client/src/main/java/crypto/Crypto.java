package crypto;


import crypto.schemes.VaultCryptoParams;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Central Crypto management.
 * Defines access to the JCA and auxiliary methods
 */
public class Crypto
{
    private static Crypto singleton;

    private KeyCrypto derivedKeys;

    /**
     * Class constructor.
     * Add Bouncy Castle as security provider
     * All .getInstance in JCA calls must be in format (..., "BC") to add provider to Engine Class
    */
    private Crypto(){
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Factory method. Enforces Singleton pattern.
     * @return <code>Crypto</code> singleton instance
     */
    public static Crypto getInstance() {
        if(singleton == null)
            singleton = new Crypto();

        return singleton;
    }

    public byte[] wrapKey(byte[] raw, byte[] iv) throws Exception {
        byte[] key = this.getDerivedKeys().getEncryptionKey();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        SecretKeySpec spec = new SecretKeySpec(key, "AES/CBC/PKCS5Padding");

        byte[] normIv = this.normalizeIv(iv);
        cipher.init(Cipher.ENCRYPT_MODE, spec, new IvParameterSpec(normIv));

        byte[] result = cipher.doFinal(raw);

        return Hex.encode(result);
    }

    public byte[] unWrapKey(byte[] encoded, byte[] iv) throws Exception {
        byte[] key = this.getDerivedKeys().getEncryptionKey();
        byte[] raw = Hex.decode(encoded);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        SecretKeySpec spec = new SecretKeySpec(key, "AES/CBC/PKCS5Padding");

        byte[] normIv = this.normalizeIv(iv);
        cipher.init(Cipher.DECRYPT_MODE, spec, new IvParameterSpec(normIv));

        return cipher.doFinal(raw);
    }

    private byte[] normalizeIv(byte[] iv) throws Exception {
        if(iv.length == 8) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(iv);
            outputStream.write(iv);

            return outputStream.toByteArray( );
        }

        if(iv.length == 12){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] bytes = new byte[] {0,0,0,0};
            outputStream.write(bytes);
            outputStream.write(iv);

            return outputStream.toByteArray();
        }

        if(iv.length == 16){
            return iv;
        }

        if(iv.length == 32)
            return java.util.Arrays.copyOf(iv, 16);

        return iv;
    }

    /**
     * Factory method to obtain <code>VaultCrypto</code> instances
     * @return <code>VaultCrypto</code> instance
     */
    public VaultCrypto getVaultCryptoInstance(VaultCryptoParams params) {
        return new VaultCrypto(params);
    }

    public void deriveKeys(char[] password) {
        derivedKeys = KeyCrypto.getInstance(password);
    }

    /**
     * Get list of supported encryption ciphers
     * @return <code>ArrayList<String> </code>
     */
    public ArrayList<String> getCipherList(){
        ArrayList<String> list = new ArrayList<>();
        list.add("AES-256-GCM");
        list.add("Camellia-256-GCM");
        list.add("ChaCha20-256-Poly1305");
        list.add("ChaCha20-256-MAC");
        list.add("AES-256-CBC");
        list.add("Camellia-256-CBC");
        list.add("Threefish-256-CBC");
        list.add("Serpent-256-CBC");

        return list;
    }

    /**
     * Get list of supported MAC schemes
     * @return <code>ArrayList<String> </code>
     */
    public ArrayList<String> getMACList(){
        ArrayList<String> list = new ArrayList<>();
        list.add("HS-256");
        list.add("HS3-256");
        list.add("HS-512");
        list.add("HS3-512");

        return list;
    }

    /**
     *
     * Auxiliary method. Obtain bytes from char.
     * @param chars
     * @return
     */
    public static byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);

        byte[] bytes = Arrays.copyOfRange(
            byteBuffer.array(),
            byteBuffer.position(),
            byteBuffer.limit()
        );

        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }

    /**
     * Auxiliary method. Obtain chars from bytes.
     * @param bytes
     * @return
     */
    public static char[] toChars(byte[] bytes) {
        CharBuffer charBuffer = ByteBuffer.wrap(bytes).asCharBuffer();

        char[] chars = Arrays.copyOfRange(
            charBuffer.array(),
            charBuffer.position(),
            charBuffer.limit()
        );

        Arrays.fill(charBuffer.array(), (char) 0); // clear sensitive data
        return chars;
    }

    public KeyCrypto getDerivedKeys() {
        return derivedKeys;
    }
}
