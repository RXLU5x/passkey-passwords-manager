package models.user;

import dto.user.UserDTO;
import models.Model;
import org.bouncycastle.util.encoders.Hex;

public class User implements Model<UserDTO> {
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String username;
    private final byte[] passwordHash;

    public User(String firstname, String lastname, String email, String username, byte[] passwordHash) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @Override
    public UserDTO toDTO() {
        return new UserDTO(firstname, lastname, email, username, Hex.toHexString(passwordHash));
    }
}
