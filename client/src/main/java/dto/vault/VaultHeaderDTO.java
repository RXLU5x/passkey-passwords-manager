package dto.vault;

import dto.DTO;
import models.Hidden;
import models.vault.VaultHeader;

/**
 * DTO representation of the Vault header
 */
public class VaultHeaderDTO implements DTO<VaultHeader>
{
    VaultHeaderCryptoDTO crypto;
    VaultHeaderInfoDTO info;

    @Hidden
    String hash;

    /**
     * Class constructor.
     * @param crypto cryptography details
     * @param info addition information
     * @param hash hash of the vault
     */
    public VaultHeaderDTO(VaultHeaderCryptoDTO crypto, VaultHeaderInfoDTO info, String hash) {
        this.crypto = crypto;
        this.info = info;
        this.hash = hash;
    }

    /**
     * Class constructor for deserialization.
     */
    public VaultHeaderDTO() { }

    @Override
    public VaultHeader toModel() {
        return new VaultHeader(
            this.crypto.toModel(),
            this.info.toModel(),
            this.hash.getBytes()
        );
    }
}
