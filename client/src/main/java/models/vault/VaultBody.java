package models.vault;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Model representation of the unencrypted Vault body
 */
public class VaultBody
{
    /**
     * Model should keep same field name as those in data format.
     */
    private String username;
    private String email;
    private String password;
    private ArrayList<String> options;

    /**
     * Class constructor.
     * @param username
     * @param email
     * @param password
     * @param options
     */
    public VaultBody(String username, String email, String password, ArrayList<String> options) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.options = options;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public String toJSON() {
        Gson json = new Gson();
        return json.toJson(this);
    }
}
