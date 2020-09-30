package models.vault;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class VaultBodyTest
{
    @Test
    protected void vaultBody() {
        String testString = "{\"username\":\"test\",\"email\":\"test\",\"password\":\"test\",\"options\":[\"test\"]}";

        ArrayList<String> testArray = new ArrayList<>();
        testArray.add("test");

        Gson json = new Gson();

        VaultBody result = json.fromJson(testString, VaultBody.class);

        Assertions.assertEquals("test", result.getUsername());
        Assertions.assertEquals("test", result.getEmail());
        Assertions.assertEquals("test", result.getPassword());
        Assertions.assertEquals(testArray.get(0), result.getOptions().get(0));

        String resultJSON = result.toJSON();

        Assertions.assertEquals(testString, resultJSON);
    }
}