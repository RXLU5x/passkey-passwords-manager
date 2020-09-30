package models.vault;

import crypto.schemes.VaultCryptoParams;
import dto.vault.VaultHeaderDTO;
import models.Model;

import java.nio.charset.StandardCharsets;

/**
 * Model representation of the Vault header
 */
public class VaultHeader implements Model<VaultHeaderDTO>
{
    private VaultHeaderCrypto vaultHeaderCrypto;
    private VaultHeaderInfo vaultHeaderInfo;

    private byte[] hash;

    public VaultHeader(VaultHeaderCrypto vaultHeaderCrypto, VaultHeaderInfo vaultHeaderInfo, byte[] hash) {
        this.vaultHeaderCrypto = vaultHeaderCrypto;
        this.vaultHeaderInfo = vaultHeaderInfo;
        this.hash = hash;
    }

    public VaultHeaderCrypto getVaultHeaderCrypto() {
        return vaultHeaderCrypto;
    }

    public VaultHeaderInfo getVaultHeaderInfo() {
        return vaultHeaderInfo;
    }

    /**
     * Convert crypto field in Vault header to <code>VaultCryptoParams</code>. Use to config current
     * <code>VaultCrypto</code>.
     * @return
     */
    public VaultCryptoParams getParams() throws Exception {
        return VaultCryptoParams.buildExisting(
            this.vaultHeaderCrypto.getAlgo(),
            this.vaultHeaderCrypto.getMac(),
            this.vaultHeaderCrypto.getIv(),
            this.vaultHeaderCrypto.getEncKey(),
            this.vaultHeaderCrypto.getMacKey()
        );
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    @Override
    public VaultHeaderDTO toDTO() {
        String hash = null;

        try {
            hash = new String(this.hash, StandardCharsets.UTF_8);
        } catch (NullPointerException ignored){
            // Expected, suppress
        }

        return new VaultHeaderDTO(
            this.vaultHeaderCrypto.toDTO(),
            this.vaultHeaderInfo.toDTO(),
            hash
        );
    }
}
