package dto.user;

import com.google.gson.Gson;
import dto.DTO;
import models.user.User;

public class UserDTO implements DTO<User> {
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String passwordHash;

    public UserDTO(String firstname, String lastname, String email, String username, String passwordHash) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public UserDTO() {}

    @Override
    public User toModel() {
        return new User(firstname, lastname, email, username, null);
    }

    public String toJSON() {
        Gson json = new Gson();
        return json.toJson(this);
    }
}
