package dto.vault;

import com.google.gson.Gson;
import dto.DTO;
import models.vault.Vault;

/**
 * DTO representation of Vault object.
 */
public class VaultDTO implements DTO<Vault>
{
    private VaultHeaderDTO vault_header;
    private String vault_body;

    /**
     * Class constructor.
     * @param vault_header header of the vault
     * @param vault_body body of the vault
     */
    public VaultDTO(VaultHeaderDTO vault_header, String vault_body) {
        this.vault_header = vault_header;
        this.vault_body = vault_body;
    }

    /**
     * Class constructor for deserialization.
     */
    public VaultDTO() { }

    @Override
    public Vault toModel() throws Exception {
        return new Vault(vault_header.toModel(), vault_body);
    }

    public String toJSON() {
        Gson json = new Gson();
        return json.toJson(this);
    }

    public static VaultDTO fromJSON(String vault) {
        Gson json = new Gson();
        return json.fromJson(vault, VaultDTO.class);
    }
}