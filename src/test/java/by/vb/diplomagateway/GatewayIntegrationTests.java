package by.vb.diplomagateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "TRIP_SERVICE_URL=http://localhost:1")
class GatewayIntegrationTests {

    private final HttpClient client = HttpClient.newHttpClient();

    @LocalServerPort
    int port;

    private HttpResponse<String> send(HttpRequest.Builder builder) throws Exception {
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    @Test
    void unavailableServiceReturnsFallback() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/api/v1/trips/anything")).GET());

        assertThat(response.statusCode()).isEqualTo(503);
        assertThat(response.body()).contains("trip-service");
    }

    @Test
    void postToUnavailableServiceReturnsFallback() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/api/v1/trips/anything"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}")));

        assertThat(response.statusCode()).isEqualTo(503);
    }

    @Test
    void corsPreflightIsAnsweredByGateway() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/api/v1/trips/anything"))
                .header("Origin", "http://localhost:4040")
                .header("Access-Control-Request-Method", "POST")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).hasValue("http://localhost:4040");
    }

    @Test
    void corsRejectsUnknownOrigin() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/api/v1/trips/anything"))
                .header("Origin", "http://evil.example")
                .header("Access-Control-Request-Method", "GET")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()));

        assertThat(response.statusCode()).isEqualTo(403);
    }

    @Test
    void actuatorHealthIsUp() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/actuator/health")).GET());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"status\":\"UP\"");
    }
}
