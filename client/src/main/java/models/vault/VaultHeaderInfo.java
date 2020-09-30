package models.vault;

import dto.vault.VaultHeaderInfoDTO;
import models.Model;

/**
 * Model representation of the info field in Vault header
 */
public class VaultHeaderInfo  implements Model<VaultHeaderInfoDTO>
{
    String title;
    String url;

    /**
     * Class constructor
     * @param title
     * @param url
     */
    public VaultHeaderInfo(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public VaultHeaderInfoDTO toDTO() {
        return new VaultHeaderInfoDTO(this.title, this.url);
    }
}
