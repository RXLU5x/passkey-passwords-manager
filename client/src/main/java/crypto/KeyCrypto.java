package crypto;

import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Crypto operations over the keys.
 * Abstracts all such operations, wrapping the JCA
 * Must be instantiated by <code>Crypto</code>
 */
public class KeyCrypto
{
    private static KeyCrypto singleton;

    private byte[] authenticationKey;
    private byte[] encryptionKey;

    /**
     * Constructor for class. Represents the passwords during a session and related operations
     * @param password
     */
    private KeyCrypto(char[] password) {
        byte[] info = new byte[0];

        // Pepper usage for key derivation
        byte[] pepperInt = "intermidiateKey".getBytes();
        byte[] pepperAuth = "authenticationKey".getBytes();
        byte[] pepperEnc = "encryptionKey".getBytes();

        // Rounds settings
        int intermediateRounds = 100000;

        // Key derivation
        byte[] intermediateKey = pbkdf2(password, pepperInt, intermediateRounds);
        this.authenticationKey = hkdf(intermediateKey, pepperAuth, 32, info);
        this.encryptionKey = hkdf(intermediateKey, pepperEnc, 32, info);
    }

    /**
     * Factory method. Enforces Singleton pattern.
     * @param password
     * @return KeyCrypto Instance
     */
    protected static KeyCrypto getInstance(char[] password) {
        if(singleton == null)
            singleton = new KeyCrypto(password);

        return singleton;
    }

    public byte[] getAuthenticationKey() {
        return authenticationKey;
    }

    protected byte[] getEncryptionKey() {
        return encryptionKey;
    }

    /**
     * Wrapper for quick PBKDF2 usafe.
     * @param password <code>char[]</code>
     * @param salt <code>byte[]</code>
     * @param rounds <code>int</code>
     * @return <code>byte[]</code>
     */
    public static byte[] pbkdf2(char[] password, byte[] salt, int rounds){
        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;

        try {
            keyFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512", "BC");
            pbeKeySpec = new PBEKeySpec(password, salt, rounds, 32);
            //Base64.getEncoder().encodeToString(secretKey.getEncoded());
            return keyFac.generateSecret(pbeKeySpec).getEncoded();
        } catch(Exception e){
            throw new InternalError();
        }
    }

    /**
     *  HMAC-based Extract-and-Expand Key Derivation Function (HKDF) as defined by RFC 5869.
     *
     * @param salt <code>Byte[]</code> as defined in RFC
     * @param inputKeyingMaterial <code>Byte[]</code> IKM, as defined in RFC
     * @param L <code>int</code>size in bytes, as defined in RFC
     * @param info info <code>Byte[]</code> as defined in RFC
     * @return
     */
    public static byte[] hkdf(byte[] salt, byte[] inputKeyingMaterial, int L, byte[] info){
        String algo = "hmacSHA256";

        try {
            // Extract section
            byte[] extractedKey = hkdfExtract(algo, salt, inputKeyingMaterial);

            // Expand section
            return hkdfExpand(algo, info , extractedKey, L);
        } catch (Exception e){
            throw new InternalError();
        }
    }

    /**
     * Extract Step on HKDF.
     * @param algo <code>String</code> containing HMAC algorithm.
     * @param salt <code>Byte[]</code> as defined in RFC
     * @param inputKeyingMaterial <code>Byte[]</code> IKM, as defined in RFC
     * @return okm <code>Byte[]</code>
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    private static byte[] hkdfExtract(String algo, byte[] salt, byte[] inputKeyingMaterial) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        /**
         *  HKDF-Extract(salt, IKM) -> PRK
         *
         *    Options:
         *       Hash     a hash function; HashLen denotes the length of the
         *                hash function output in octets
         *
         *    Inputs:
         *       salt     optional salt value (a non-secret random value);
         *                if not provided, it is set to a string of HashLen zeros.
         *       IKM      input keying material
         *
         *    Output:
         *       PRK      a pseudorandom key (of HashLen octets)
         *
         *    The output PRK is calculated as follows:
         *
         *    PRK = HMAC-Hash(salt, IKM)
         */
        Mac mac = Mac.getInstance(algo, "BC");
        mac.init( new SecretKeySpec(salt, algo));
        return mac.doFinal(inputKeyingMaterial);
    }

    /**
     * Expand Step of HKDF.
     * @param algo <code>String</code> containing HMAC algorithm.
     * @param info <code>Byte[]</code> as defined in RFC
     * @param extractedKey <code>Byte[]</code> PRK, as defined in RFC
     * @param L <code>int</code>size in bytes, as defined in RFC
     * @return okm <code>Byte[]</code>
     * @throws InvalidKeyException
     * @throws  NoSuchAlgorithmException
     */
    private static byte[] hkdfExpand(String algo, byte[] info, byte[] extractedKey, int L ) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        /**
         *  HKDF-Expand(PRK, info, L) -> OKM
         *
         *    Options:
         *       Hash     a hash function; HashLen denotes the length of the
         *                hash function output in octets
         *    Inputs:
         *       PRK      a pseudorandom key of at least HashLen octets
         *                (usually, the output from the extract step)
         *       info     optional context and application specific information
         *                (can be a zero-length string)
         *       L        length of output keying material in octets
         *                (<= 255*HashLen)
         *
         *    Output:
         *       OKM      output keying material (of L octets)
         *
         *    The output OKM is calculated as follows:
         *
         *    N = ceil(L/HashLen)
         *    T = T(1) | T(2) | T(3) | ... | T(N)
         *    OKM = first L octets of T
         *
         *    where:
         *    T(0) = empty string (zero length)
         *    T(1) = HMAC-Hash(PRK, T(0) | info | 0x01)
         *    T(2) = HMAC-Hash(PRK, T(1) | info | 0x02)
         *    T(3) = HMAC-Hash(PRK, T(2) | info | 0x03)
         *    ...
         *
         *    (where the constant concatenated to the end of each T(n) is a
         *    single octet.)
         */

        Mac mac = Mac.getInstance(algo, "BC");
        mac.init( new SecretKeySpec(extractedKey, algo));

        int n = (int)Math.ceil(L/32.0);
        byte[] T = new byte[0];
        int i = 0, stepSize, remainingBytes = L;
        ByteBuffer output = ByteBuffer.allocate(L);

        for (int index = 0 ; i < n ; i++) {
            mac.update(T);
            mac.update(info);
            mac.update((byte) (i + 1));

            T = mac.doFinal();

            stepSize = Math.min(remainingBytes, T.length);

            output.put(T, 0, stepSize);
            remainingBytes -= stepSize;
        }

        // OKM
        return output.array();
    }
}

