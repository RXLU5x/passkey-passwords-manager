package dao;

import com.google.gson.Gson;
import dto.vault.VaultDTO;

public class VaultDao {
    private String id;
    private long timeTag;
    private VaultDTO vault;
    private boolean delete = false;

    public VaultDao() { }

    public VaultDao(String id, long timeTag, VaultDTO vaultDTO) {
        this.id = id;
        this.timeTag = timeTag;
        this.vault = vaultDTO;
    }

    public String getId() {
        return id;
    }

    public long getTimeTag() {
        return timeTag;
    }

    public VaultDTO getVault() {
        return vault;
    }

    public boolean isDelete(){
        return delete;
    }

    public String toJSON() {
        Gson json = new Gson();
        return json.toJson(this);
    }

    public static VaultDao fromJSON(String body) {
        Gson json = new Gson();
        return json.fromJson(body, VaultDao.class);
    }
}
