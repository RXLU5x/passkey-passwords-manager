package http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class HttpsTest
{
    @Test
    public void trust() throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException, URISyntaxException, IOException, InterruptedException {
        HttpsClient client = HttpsClient.getInstance();
        HttpResponse<String> response = client.sendRequest(new URI("https://www.google.com"), "GET");
        response.body();
    }

    @Test
    public void serverBasic() throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException, URISyntaxException, IOException, InterruptedException {
        HttpsClient client = HttpsClient.getInstance();
        HttpResponse<String> response = client.sendRequest(new URI("https://127.0.0.1:8080/api/users/signin"), "POST", "{\"username\": \"test\",\"passwordHash\": \"test\"" + "}");
        response.body();
    }
}
