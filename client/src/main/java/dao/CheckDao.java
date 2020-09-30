package dao;

import com.google.gson.Gson;

public class CheckDao
{
    private String type = null;

    private VaultDao[] vaults = null;

    public CheckDao(String type, VaultDao[] vaults) {
        this.type = type;
        this.vaults = vaults;
    }

    public CheckDao() { }

    public String toJson(){
        Gson json = new Gson();
        return json.toJson(this);
    }

    public static CheckDao fromJson(String obj){
        Gson json = new Gson();
        return json.fromJson(obj, CheckDao.class);
    }

    public VaultDao[] getVaults() {
        return vaults;
    }
}
