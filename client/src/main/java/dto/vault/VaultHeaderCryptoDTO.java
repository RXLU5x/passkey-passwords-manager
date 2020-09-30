package dto.vault;

import dto.DTO;
import models.vault.VaultHeaderCrypto;
import org.bouncycastle.util.encoders.Hex;

/**
 * DTO representation of the crypto field in Vault header
 */
public class VaultHeaderCryptoDTO implements DTO<VaultHeaderCrypto>
{
    String algo;
    String iv;
    String enc_key;
    String mac;
    String mac_key;

    /**
     * Class Constructor.
     * @param algo Algorithm
     * @param mac MAC
     * @param enc_key Encryption key
     * @param iv Initialization vector
     * @param mac_key MAC key
     */
    public VaultHeaderCryptoDTO(String algo, String mac, String enc_key, String iv, String mac_key) {
        this.algo = algo;
        this.iv = iv;
        this.enc_key = enc_key;
        this.mac = mac;
        this.mac_key = mac_key;
    }

    /**
     * Class constructor for deserialization.
     */
    public VaultHeaderCryptoDTO() { }

    @Override
    public VaultHeaderCrypto toModel() {
        byte[] mac_keyBytes = null;

        if(mac_key != null)
            mac_keyBytes = this.mac_key.getBytes();

        return new VaultHeaderCrypto(
            algo,
            mac,
            Hex.decode(iv.getBytes()),
            enc_key.getBytes(),
            mac_keyBytes
        );
    }
}
