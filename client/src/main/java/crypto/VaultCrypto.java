package crypto;

import crypto.schemes.VaultCryptoParams;
import javafx.util.Pair;

import javax.crypto.AEADBadTagException;

/**
 * Crypto operations over a vault.
 * Abstracts all such operations, wrapping the JCA
 * Must be instantiated by <code>Crypto</code>
 */
public class VaultCrypto
{
    private VaultCryptoParams params;

    /**
     * Class Constructor.
     * Should only be used by classes in the <code>crypto</code> package.
     * @param params <code>VaultCryptoParams</code>
     */
    public VaultCrypto(VaultCryptoParams params){
        this.params = params;
    }

    /**
     * Interface for encryption operations over the body. Recalculates MAC/AEAD.
     * @param plaintext the JSON formated vault body.
     * @param header the JSON formated vault header.
     * @return a <code>Pair<byte[], String></code> containing MAC and ciphertext.
     * @throws Exception
     */
    public Pair<byte[], String> encrypt(byte[] plaintext, String header) throws Exception {
        try {
            String headerForm = ("\"vault_header\":" + header);
            return params.encrypt(plaintext, headerForm);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Interface for decryption operations over the body. Checks MAC/AEAD.
     * @param ciphertext
     * @param mac
     * @param header
     * @return a <code>String</code> containing the plaintext.
     * @throws MacErrorException
     * @throws AEADBadTagException
     * @throws Exception
     */
    public String decrypt(String ciphertext, byte[] mac, String header) throws MacErrorException, AEADBadTagException, Exception {
        try {
            String headerForm = ("\"vault_header\":" + header);
            return params.decrypt(ciphertext, mac, headerForm);
        } catch (AEADBadTagException e){
            throw new MacErrorException(e.getMessage());
        }
    }

    public byte[] getIv() {
        return params.iv;
    }

    public byte[] getRawKey() {
        return params.rawKey;
    }

    public byte[] getRawMac() {
        return params.rawMac;
    }

    public String getAlgo() {
        return params.algo;
    }

    public String getMac() {
        return params.mac;
    }
}
