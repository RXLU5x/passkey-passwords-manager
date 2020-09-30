package crypto.schemes;

import crypto.Crypto;
import crypto.InvalidAlgoMapException;
import crypto.MacErrorException;
import javafx.util.Pair;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Represents the parameters of an individual vault.
 * Sub-model to represent parameters to be used by <code>VaultCrypto</code>
 */
public abstract class VaultCryptoParams
{
    // Reference to crypto
    private Crypto crypto = Crypto.getInstance();

    // Public variables
    public byte[] iv, rawKey, rawMac;
    public String algo, mac;

    // Internal variables
    protected String jcaSign, cipherSign, macSign;
    protected int tagLength;
    SecretKey encKey, macKey;

    // Interface methods
    public abstract Pair<byte[], String> encrypt(byte[] plaintext, String header) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException;
    public abstract String decrypt(String ciphertext, byte[] mac,  String header) throws MacErrorException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException;

    // Convenience methods. Shared. Repeated code

    protected void build() throws Exception {
        this.encKey = new SecretKeySpec(crypto.unWrapKey(rawKey, this.iv), jcaSign);
    }

    protected void buildNew() throws Exception {
        // Generate needed keys
        KeyGenerator kgen = KeyGenerator.getInstance(cipherSign, "BC");

        this.encKey = kgen.generateKey();

        // Generate iv
        Cipher cipher = Cipher.getInstance(jcaSign, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, encKey);

        IvParameterSpec spec = cipher
            .getParameters()
            .getParameterSpec(IvParameterSpec.class);

        this.iv = spec.getIV();
        this.rawKey = crypto.wrapKey(this.encKey.getEncoded(), this.iv);
    }

    /**
     * Encryption wrapper for AEAD schemes.
     * @param plaintext
     * @param associatedData
     * @return a <code>Pair<byte[], String></code> containing AEAD Tag and Ciphertext
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */
    protected Pair<byte[], String> encryptAEAD(byte[] plaintext, byte[] associatedData) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(jcaSign, "BC");
        GCMParameterSpec spec = new GCMParameterSpec(tagLength, iv);
        cipher.init(Cipher.ENCRYPT_MODE, encKey, spec);

        cipher.updateAAD(associatedData);

        byte[] result = cipher.doFinal(plaintext);
        byte[] ciphertext = new byte[result.length-tagLength/8];
        byte[] tag = new byte[tagLength/8];

        System.arraycopy(result, 0, tag, 0, tagLength/8);
        System.arraycopy(result, tagLength/8, ciphertext, 0, result.length-tagLength/8);

        return new Pair<>(Hex.encode(tag), Hex.toHexString(ciphertext));
    }

    /**
     * Encryption wrapper for non-AEAD schemes.
     * @param plaintext
     * @return a Ciphertext <code>String</code>.
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     */
    protected String encryptRegular(byte[] plaintext, char[] headerForm) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(jcaSign, "BC");
        //SecretKeySpec spec = new SecretKeySpec(rawKey, jcaSign);
        cipher.init(Cipher.ENCRYPT_MODE, encKey, new IvParameterSpec(iv));

        byte[] result = cipher.doFinal(plaintext);

        return Hex.toHexString(result);
    }

    /**
     * Decryption wrapper for AEAD schemes.
     * @param ciphertext
     * @param tag
     * @param associatedData
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */
    protected String decryptAEAD(String ciphertext, byte[] tag,  byte[] associatedData) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(jcaSign, "BC");
        GCMParameterSpec spec = new GCMParameterSpec(tagLength, iv);
        cipher.init(Cipher.DECRYPT_MODE, encKey, spec);

        // Decode Hex
        byte[] decodedCyphertext = Hex.decode(ciphertext);
        byte[] decodedTag = Hex.decode(tag);

        //Reconstruct [Tag + Ciphertext]
        int tLength = decodedTag.length, // Size bytes AEAD tag
                cLength = decodedCyphertext.length, // Size bytes ciphertext
                tarLength = tLength + cLength; // Size bytes sum
        byte[] target = new byte[tarLength];

        System.arraycopy(decodedTag, 0, target, 0, tLength);
        System.arraycopy(decodedCyphertext, 0, target, tLength, cLength);

        cipher.updateAAD(associatedData);
        byte[] result = cipher.doFinal(target);

        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * Decryption wrapper for non-AEAD schemes.
     * @param ciphertext
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     */
    protected String decryptRegular(String ciphertext) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(jcaSign, "BC");
        //SecretKeySpec spec = new SecretKeySpec(rawKey, jcaSign);
        cipher.init(Cipher.DECRYPT_MODE, encKey, new IvParameterSpec(iv));

        byte[] result = cipher.doFinal(Hex.decode(ciphertext));

        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * Computes the MAC of the provided text.
     * @param bytes
     * @return
     */
    public byte[] computeMAC(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        Mac mac = Mac.getInstance(macSign, "BC");
        mac.init(macKey);
        return Hex.encode(mac.doFinal(bytes));
    }

    // Builder methods

    public static VaultCryptoParams buildExisting(String algo, String mac, byte[] iv, byte[] enc_key, byte[] mac_key) throws Exception {
        VaultCryptoParams params = mapCipher(algo, iv, enc_key, mac_key);
        params.mapMac(mac, mac_key);
        return params;
    }

    public static VaultCryptoParams buildNew(String algo, String mac) throws Exception {
        VaultCryptoParams params = mapCipher(algo);
        params.mapMac(mac, null);
        return params;
    }

    private void mapMac(String mac, byte[] mac_key) throws Exception {
        this.mac = mac;
        this.rawMac = mac_key;
        if(mac == null) mac = "";
        switch (mac){
            default:
                this.macSign = null;
                this.rawMac = null;
                break;
            case "HS-256":
                this.macSign = "HMac-SHA256";
                this.tagLength = 256;
                break;
            case "HS3-256":
                this.macSign = "HMac-SHA3-256";
                this.tagLength = 256;
                break;
            case "HS-512":
                this.macSign = "HMac-SHA512";
                this.tagLength = 512;
                break;
            case "HS3-512":
                this.macSign = "HMac-SHA3-512";
                this.tagLength = 512;
                break;
        }

        if(mac_key == null && !mac.equals("")){
            KeyGenerator kgen = KeyGenerator.getInstance(macSign);
            this.macKey = kgen.generateKey();
            this.rawMac = crypto.wrapKey( this.macKey.getEncoded(), this.iv );
        }else if(!mac.equals("")){
            this.macKey = new SecretKeySpec(crypto.unWrapKey(this.rawMac, this.iv), macSign);
        }
    }

    private static VaultCryptoParams mapCipher(String algo) throws Exception {
        VaultCryptoParams vaultCryptoParams;
        switch (algo) {
            case "AES-256-GCM":
                vaultCryptoParams = new AES256GCMParams();
                break;
            case "Camellia-256-GCM":
                vaultCryptoParams = new Camellia256GCMParams();
                break;
            case "ChaCha20-256-Poly1305":
                vaultCryptoParams = new ChaCha20256Poly1305Params();
                break;
            case "ChaCha20-256-MAC":
                vaultCryptoParams = new ChaCha20256MACParams();
                break;
            case "AES-256-CBC":
                vaultCryptoParams = new AES256CBCParams();
                break;
            case "Camellia-256-CBC":
                vaultCryptoParams = new Camellia256CBCParams();
                break;
            case "Threefish-256-CBC":
                vaultCryptoParams = new Threefish256CBCParams();
                break;
            case "Serpent-256-CBC":
                vaultCryptoParams = new Serpent256CBCParams();
                break;

            default:
                throw new InvalidAlgoMapException();
        }
        return vaultCryptoParams;
    }

    private static VaultCryptoParams mapCipher(String algo, byte[] iv, byte[] enc_key, byte[] mac_key ) throws Exception {
        VaultCryptoParams vaultCryptoParams;
        switch (algo) {
            case "AES-256-GCM":
                vaultCryptoParams = new AES256GCMParams(iv, enc_key, mac_key);
                break;
            case "Camellia-256-GCM":
                vaultCryptoParams = new Camellia256GCMParams(iv, enc_key, mac_key);
                break;
            case "ChaCha20-256-Poly1305":
                vaultCryptoParams = new ChaCha20256Poly1305Params(iv, enc_key);
                break;
            case "ChaCha20-256-MAC":
                vaultCryptoParams = new ChaCha20256MACParams(iv, enc_key, mac_key);
                break;
            case "AES-256-CBC":
                vaultCryptoParams = new AES256CBCParams(iv, enc_key);
                break;
            case "Camellia-256-CBC":
                vaultCryptoParams = new Camellia256CBCParams(iv, enc_key, mac_key);
                break;
            case "Threefish-256-CBC":
                vaultCryptoParams = new Threefish256CBCParams(iv, enc_key, mac_key);
                break;
            case "Serpent-256-CBC":
                vaultCryptoParams = new Serpent256CBCParams(iv, enc_key, mac_key);
                break;

            default:
                throw new InvalidAlgoMapException();
        }
        return vaultCryptoParams;
    }

    public abstract boolean isAEAD();
}
