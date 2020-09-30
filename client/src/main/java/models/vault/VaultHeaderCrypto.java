package models.vault;

import crypto.VaultCrypto;
import dto.vault.VaultHeaderCryptoDTO;
import models.Model;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;

/**
 * Model representation of the crypto field in Vault header
 */
public class VaultHeaderCrypto implements Model<VaultHeaderCryptoDTO>
{
    private String algo, mac;
    private byte[] iv, encKey, macKey;

    /**
     * Class Constructor.
     * @param algo
     * @param mac
     * @param iv
     * @param encKey
     */
    public VaultHeaderCrypto(String algo, String mac, byte[] iv, byte[] encKey, byte[] macKey) {
        this.algo = algo;
        this.mac = mac;
        this.iv = iv;
        this.encKey = encKey;
        this.macKey = macKey;
    }

    /**
     * Class constrcutor, from <code>VaultCrypto.VaultCryptoParams</code> using <code>VaultCrypto</code>
     * @param vaultCrypto
     */
    public VaultHeaderCrypto(VaultCrypto vaultCrypto) {
        this(
            vaultCrypto.getAlgo(),
            vaultCrypto.getMac(),
            vaultCrypto.getIv(),
            vaultCrypto.getRawKey(),
            vaultCrypto.getRawMac()
        );
    }

    public byte[] getMacKey() {
        return macKey;
    }

    public void setMacKey(byte[] macKey) {
        this.macKey = macKey;
    }

    public String getAlgo() {
        return algo;
    }

    public void setAlgo(String algo) {
        this.algo = algo;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getEncKey() {
        return encKey;
    }

    public void setEncKey(byte[] encKey) {
        this.encKey = encKey;
    }

    @Override
    public VaultHeaderCryptoDTO toDTO() {
        String macKey = null;

        try {
            macKey = new String(this.macKey, StandardCharsets.UTF_8);
        } catch (NullPointerException ignored){
            // Expected, suppress
        }

        return new VaultHeaderCryptoDTO(
            this.algo,
            this.mac,
            new String(this.encKey, StandardCharsets.UTF_8),
            Hex.toHexString(this.iv),
            macKey
        );
    }
}
