package tests.homework3;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRequest extends BaseTestCase {
    /*
    * Ex11: Тест запроса на метод cookie
    * */
    @Test
    public void testCookie() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        assertEquals("hw_value", response.getCookie("HomeWork"), "Cookie is not equal to expected value");
    }

    /*
    * Ex12: Тест запроса на метод header
    * */
    @Test
    public void testHeader() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        assertEquals("Some secret value", response.getHeader("x-secret-homework-header"), "Header is not equal to expected value");
    }

    /*
    * Ex13: User Agent
    * */
    @ParameterizedTest
    @MethodSource("argumentsFromResponse")
    public void testUserAgent(String userAgent, String expectedPlatform, String expectedBrowser, String expectedDevice) {
        JsonPath response = RestAssured
                .given()
                .header("User-Agent", userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();

        assertEquals(
                addResultInMap(expectedPlatform, expectedBrowser, expectedDevice),
                addResultInMap(response.getString("platform"), response.getString("browser"), response.getString("device")),
                "Unexpected values in the response");
    }

    private HashMap<String, String> addResultInMap(String platform, String browser, String device) {
        HashMap<String, String> result = new HashMap<>();
        result.put("platform", platform);
        result.put("browser", browser);
        result.put("device", device);
        return result;
    }

    private static Stream<Arguments> argumentsFromResponse() {
        return Stream.of(
                Arguments.of("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                        "Mobile", "No", "Android"),
                Arguments.of("Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                        "Mobile", "Chrome", "iOS"),
                Arguments.of("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                        "Googlebot", "Unknown", "Unknown"),
                Arguments.of("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
                        "Web", "Chrome", "No"),
                Arguments.of("Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1",
                        "Mobile", "No", "iPhone")
        );
    }
}
