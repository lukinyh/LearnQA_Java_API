import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

// Shows "Hello" to user with added name
public class HelloWorldTest {
    @Test
    public void testRestAssured() {
        Response response = RestAssured
                .given()
                .queryParam("name", "Jonh")
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
    }

    // Takes out parameter as map element for method "Hello"
    @Test
    public void testRestAssured2() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Jonh");
        Response response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
    }

    // Changes "andReturn()" to "jsonPath" and used response method "get" to get answer
    @Test
    public void testRestAssured3() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Jonh");
        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String answer = response.get("answer");
        System.out.println("Answer: " + answer);
    }

    // Checks if key is absent
    @Test
    public void testRestAssured4() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Jonh");
        params.put("name", "Jonh2");
        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String answer2 = response.get("answer2");
        if (answer2 == null) {
            System.out.println("The key 'answer' is absent");
        } else {
            System.out.println("Answer2: " + answer2);
        }
    }

    // Changes prettyPrint() to Print()
    @Test
    public void testRestAssured5() {
        Response response = RestAssured
                .given()
                .queryParam("param1", "object1")
                .queryParam("param2", "object2")
                .get("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        response.print();
    }

    @Test
    // Has changes "get" to "post" and added parameters in the method "body" (via &)
    public void testRestAssured6() {
        Response response = RestAssured
                .given()
                .body("param1=value1&param2=value2")
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        response.print();
    }

    // Has changes body format as JSON format
    @Test
    public void testRestAssured7() {
        Response response = RestAssured
                .given()
                .body("{\"param1\":\"value1\",\"param2\":\"value2\"}")
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        response.print();
    }

    // Has changes parameters for post request as Map to more readable
    @Test
    public void testRestAssured8() {
        Map<String, Object> body = new HashMap();
        body.put("param1", "value1");
        body.put("param2", "value2");
        Response response = RestAssured
                .given()
                .body(body)
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        response.print();
    }

    // Checks different status codes
    @Test
    public void testGetStatusCode() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        int statusCode = response.getStatusCode();
        System.out.println("Status code is " + statusCode);

        response = RestAssured
                .get("https://playground.learnqa.ru/api/get_500")
                .andReturn();

        System.out.println("Next status code is " + response.getStatusCode());

        response = RestAssured
                .given()
                .redirects()
                .follow(true) // agreed or not agreed to redirect
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();
        System.out.println("Next new status code is " + response.getStatusCode());
    }

    // Shows all headers
    @Test
    public void testShowAllHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("myHeader1", "myValue1");
        headers.put("myHeader2", "myValue2");

        Response response = RestAssured
                .given()
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/api/show_all_headers")
                .andReturn();
        // Shows headers in request
        response.prettyPrint();

        // Shows headers in response
        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);

    }

    // Gets special header
    @Test
    public void testGetsSpecialHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("myHeader1", "myValue1");
        headers.put("myHeader2", "myValue2");

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();
        response.prettyPrint();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);

    }

    // Shows Cookies
    @Test
    public void testFullResponse() {
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        System.out.println("\nPretty text:");
        response.prettyPrint();

        System.out.println("\nHeaders:");
        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);

        System.out.println("\nCookies:");
        Map<String, String> responseCookies = response.getCookies();
        System.out.println(responseCookies);
    }

    @Test
    public void testPrintOnlyCookie() {
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        String responseCookie = response.getCookie("auth_cookie");
        System.out.println(responseCookie);
    }

    @Test
    public void testPrintNewResponseWithOldCookie() {
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass2");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();
        String responseCookie = responseForGet.getCookie("auth_cookie");
        Map<String,String> cookies = new HashMap<>();
        if (responseCookie != null) {
            cookies.put("auth_cookie", responseCookie);
        }

        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();
        responseForCheck.print();
    }

    // Just homework
    @Test
    public void homeWorkRequest1() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

}
