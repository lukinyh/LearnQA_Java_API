import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import java.util.List;

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
        response.get();
        List<String> messages = response.getList("messages.message");
        System.out.println(messages.get(1));
    }
}
