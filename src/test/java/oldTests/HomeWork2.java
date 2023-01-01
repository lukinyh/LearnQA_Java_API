package oldTests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
        int count = 0;
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
            count++;
        } while (response.getStatusCode() != 200);
        System.out.println("Count of redirect: " + (count - 1));
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

    /*
     * Ex9: Подбор пароля
     * */
    @Test
    public void testPasswordGuessing() throws IOException {
        String login = "super_admin";
        String password = "password";

        Map<String, String> credentials = new HashMap();
        credentials.put("login", login);


        List<String> passwords = getPasswords();

        credentials.put("password", passwords.get(0));

        int indexOfPassword = 1;
        while (!checkPassword(credentials) && indexOfPassword < passwords.size()) {
            credentials.replace("password", passwords.get(indexOfPassword));
            indexOfPassword++;
        }
    }

    /*
    * Checks passwords and print if some password is correct
    * */
    private boolean checkPassword(Map<String, String> credentials) {
        // gets auth_cookie
        String auth_cookie = RestAssured
                .given()
                .body(credentials)
                .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                .andReturn()
                .getCookie("auth_cookie");

        Map<String, String> cookies = new HashMap<>();
        if (auth_cookie != null) {
            cookies.put("auth_cookie", auth_cookie);
        }

        // Checks via auth_cookie that password is correct
        String result = RestAssured
                .given()
                .body(credentials)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                .body().asString();

        if (result.equals("You are authorized")) {
            System.out.printf("Password: %s\n", credentials.get("password"));
            System.out.println(result);
            return true;
        } else {
            // uncomment next two strings if you want to see process of guessing
            // System.out.printf("Password: %s\n", params.get("password"));
            // System.out.println(result);
            return false;
        }
    }

    /*
    * Gets passwords from url as list
    * */
    private List<String> getPasswords() throws IOException {
        String url = "https://en.wikipedia.org/wiki/List_of_the_most_common_passwords";
        String xpath = "//table/caption[contains(text(), 'SplashData')]/../tbody/tr/td[@align='left']";

        Elements elements = Jsoup.connect(url).get().body().selectXpath(xpath);
        List<String> passwords = elements.eachText().stream().distinct().collect(Collectors.toList());
        return passwords;
    }
}
