package dto.vault;

import dto.DTO;
import models.vault.VaultHeaderInfo;

/**
 * DTO representation of the info field in Vault header
 */
public class VaultHeaderInfoDTO implements DTO<VaultHeaderInfo>
{
    String title;
    String url;

    /**
     * Class Constructor.
     * @param title
     * @param url
     */
    public VaultHeaderInfoDTO(String title, String url) {
        this.title = title;
        this.url = url;
    }

    /**
     * Class constructor for deserialization.
     */
    public VaultHeaderInfoDTO() { }

    @Override
    public VaultHeaderInfo toModel() {
        return new VaultHeaderInfo(title, url);
    }
}
