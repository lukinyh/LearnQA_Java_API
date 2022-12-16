package tests.homework3;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRequest extends BaseTestCase {
    @Test
    public void testCookie() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        assertEquals("hw_value", response.getCookie("HomeWork"), "JSON value is not equal to expected value");
    }
}
