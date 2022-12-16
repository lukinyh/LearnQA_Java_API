import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LessonJUnit {

    @Test
    public void TestStatus200() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map")
                .andReturn();
        //assertTrue(200 == response.statusCode(),"Unexpected status code");
        assertEquals(200, response.statusCode(), "Unexpected status code");
    }

    @Test
    public void TestStatus404() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map2")
                .andReturn();
        assertEquals(404, response.statusCode(), "Unexpected status code");
    }

    @ParameterizedTest
    @ValueSource(strings = {"" , "John", "Peter"})
    public void testHelloMethod(String name) {
        Map<String, String> queryParams = new HashMap<>();
        if (name.length() > 0) {
            queryParams.put("name", name);
        }
        JsonPath response = RestAssured
                .given()
                .queryParams(queryParams)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();
        String answer = response.getString("answer");
        String expectedName = (name.length() > 0 ? name : "someone");
        assertEquals("Hello, " + expectedName, answer, "Unexpected answer");
    }
}
