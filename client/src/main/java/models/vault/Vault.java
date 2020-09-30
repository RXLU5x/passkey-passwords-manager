package models.vault;

import crypto.Crypto;
import crypto.VaultCrypto;
import crypto.schemes.VaultCryptoParams;
import dto.vault.VaultDTO;
import javafx.util.Pair;
import models.Model;

/**
 * Class that represents the Vault
 * Does hold unencrypted view. Can hold decrypted view temporarily.
 */
public class Vault implements Model<VaultDTO>
{
    // Model variables

    /// Basic representation
    private VaultHeader vaultHeader;
    private String vaultBodyString;

    /// Decrypted representation TEMPORARY USAGE
    private VaultBody vaultBody;

    // Vault crypto
    private VaultCrypto vaultCrypto;

    // Parser

    // Class construction

    /**
     * Class constructor. Explicit <code>Crypto</code>
     * Creates model representation of a given vault.
     * @param vaultHeader the header of the model
     * @param vaultBodyString the encrypted string representation of the body
     * @param crypto the crypto operations Class to be used
     */
    public Vault(VaultHeader vaultHeader, String vaultBodyString, Crypto crypto) throws Exception {
        this.vaultHeader = vaultHeader;
        this.vaultBodyString = vaultBodyString;
        this.vaultCrypto = crypto.getVaultCryptoInstance(vaultHeader.getParams());
    }

    /**
     * Class constructor. Unecrypted body start. Explicit <code>Crypto</code>
     * @param vaultHeader
     * @param vaultBody
     * @param crypto
     * @throws Exception
     */
    public Vault(VaultHeader vaultHeader, VaultBody vaultBody, VaultCrypto vaultCrypto, Crypto crypto) throws Exception {
        this.vaultHeader = vaultHeader;
        this.vaultBody = vaultBody;
        this.vaultCrypto = vaultCrypto;

        this.encryptBody();
    }

    /**
     * Class constructor. Self obtains <code>Crypto</code>
     * Creates model representation of a given vault.
     * @param vaultHeader
     * @param vaultBodyString
     */
    public Vault(VaultHeader vaultHeader, String vaultBodyString) throws Exception {
        this(
            vaultHeader,
            vaultBodyString,
            Crypto.getInstance()
        );
    }

    /**
     * Class constructor. Unecrypted body start. Self obtains <code>Crypto</code>
     * @param vaultHeader
     * @param vaultBody
     * @throws Exception
     */
    public Vault(VaultHeader vaultHeader, VaultBody vaultBody, VaultCrypto vaultCrypto) throws Exception {
        this(
            vaultHeader,
            vaultBody,
            vaultCrypto,
            Crypto.getInstance()
        );
    }

    public static Vault generateInstance(String algo, String mac, VaultHeaderInfo info, VaultBody vaultBody) throws Exception {
        // Create macKey, encKey and iv
        VaultCrypto vaultCrypto = new VaultCrypto(VaultCryptoParams.buildNew(algo, mac));

        // Create header
        VaultHeaderCrypto vaultHeaderCrypto = new VaultHeaderCrypto(vaultCrypto);
        VaultHeader vaultHeader = new VaultHeader(vaultHeaderCrypto, info, null);

        // Create vault
        Vault vault = new Vault(vaultHeader, vaultBody, vaultCrypto);

        // Do crypto
        vault.encryptBody();

        // Return new object
        return vault;
    }

    // Crypto operations

    /**
     * Decrypts body and sets it.
     * Also checks vault integrity and sets it. Throws <code>MacErrorException</code> if MAC/AEAD fails.
     * @return
     */
    public void decryptBody() throws Exception {
        this.vaultBody = json.fromJson(
            vaultCrypto.decrypt(
                vaultBodyString,
                vaultHeader.getHash(),
                this.toJsonHidden(vaultHeader.toDTO())
            ),
            VaultBody.class
        );
    }

    /**
     * Encrypts body, calculates MAC and sets both.
     */
    public void encryptBody() throws Exception {
        byte[] plaintext = vaultBody.toJSON().getBytes();

        Pair<byte[], String> result = vaultCrypto.encrypt(plaintext, this.toJsonHidden(vaultHeader.toDTO()) );

        this.vaultHeader.setHash(result.getKey());
        this.vaultBodyString = result.getValue();
    }

    public VaultBody getVaultBody() {
        return vaultBody;
    }

    public VaultHeader getVaultHeader() {
        return vaultHeader;
    }

    public void clearPlaintext(){
        vaultBody = null;
    }

    @Override
    public VaultDTO toDTO() {
        return new VaultDTO(vaultHeader.toDTO(), vaultBodyString);
    }
}
