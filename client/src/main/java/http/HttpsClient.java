package http;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

public class HttpsClient
{
    private static HttpsClient singleton;

    private HttpClient client;

    public HttpResponse<String> sendRequest(URI address, String method, String JsonBody) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(JsonBody);

        HttpRequest request = HttpRequest
            .newBuilder()
            .uri(address)
            .version(HttpClient.Version.HTTP_1_1)
            .method(method, bodyPublisher)
            .setHeader("Content-Type", "application/json")
            .build();

        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> sendRequest(URI address, String method) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
            .newBuilder(address)
            .version(HttpClient.Version.HTTP_1_1)
            .method(method, HttpRequest.BodyPublishers.noBody())
            .build();

        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        return client.send(request, bodyHandler);
    }

    public CompletableFuture<HttpResponse<String>> sendAsyncRequest(URI address, String method, String JsonBody) {
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(JsonBody);

        HttpRequest request = HttpRequest
            .newBuilder(address)
            .version(HttpClient.Version.HTTP_1_1)
            .method(method, bodyPublisher)
            .setHeader("Content-Type", "application/json")
            .build();

        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        return client.sendAsync(request, bodyHandler);
    }

    public CompletableFuture<HttpResponse<String>> sendAsyncRequest(URI address, String method) {
        HttpRequest request = HttpRequest
            .newBuilder(address)
            .version(HttpClient.Version.HTTP_1_1)
            .method(method, HttpRequest.BodyPublishers.noBody())
            .build();

        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        return client.sendAsync(request, bodyHandler);
    }

    /**
     * Factory method. Enforces Singleton pattern.
     * @return <code>HttpClient</code> singleton instance
     */
    public static HttpsClient getInstance() throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
        if(singleton == null) {
            singleton = new HttpsClient();

            SSLContext sslContext = SSLContext.getInstance("TLSv1.3", "SunJSSE");
            sslContext.init(null, null, new SecureRandom());

            singleton.client = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .sslContext(sslContext)
                .cookieHandler(new CookieManager())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        }

        return singleton;
    }
}
