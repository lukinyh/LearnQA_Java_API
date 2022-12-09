
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeWork2 {

    /*
    Ex5: Парсинг JSON
    * */
    @Test
    public void testGetSecondMessage() {
        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        List<String> messages = response.getList("messages.message");
        System.out.println(messages.get(1));
    }

    /*
    * Ex6: Редирект
    * */
    @Test
    public void testRedirecting() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    /*
    * Ex7: Долгий редирект
    * */
    @Test
    public void testRedirectingWithCycle() {
        String url = "https://playground.learnqa.ru/api/long_redirect";
        Response response;
        do {
            System.out.println(url);

            response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

            url = response.getHeader("Location");
        } while (response.getStatusCode() != 200);

        System.out.println(response.getStatusCode());
    }

    /*
     * Ex8: Токены
     * */
    @Test
    public void testTokens() throws InterruptedException {
        String url = "https://playground.learnqa.ru/ajax/api/longtime_job";

        // creates task
        JsonPath response = sendRequest(url);
        String token = response.getString("token");
        System.out.printf("Task was created with token: %s\n", token);

        // requests with token before task is ready and check status
        response = sendRequest(url, token);

        String status = response.getString("status");
        System.out.printf("Status after new request should not be ready: %s\n", status);

        // waits when job will be ready
        while (response.getString("status").equals("Job is NOT ready")) {
            Thread.sleep(1000);
            response = sendRequest(url, token);
        }

        // checks that job is ready and show value
        String getStatus = response.getString("status");
        if (getStatus.equals("Job is ready")) {
            System.out.printf("Checked that job is ready: %s\n", getStatus);
            System.out.printf("Checked result: %s\n", response.getString("result"));
        } else {
            System.out.printf("Expected that job is ready, but status: %s\n", getStatus);
        }
    }

    private JsonPath sendRequest(String url, String token) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);

        return RestAssured
                .given()
                .queryParams(params)
                .get(url)
                .jsonPath();
    }

    private JsonPath sendRequest(String url) {
        return RestAssured
                .given()
                .get(url)
                .jsonPath();
    }
}
