package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testGetUserDataNotAuth() {
        Response responseUserData =
                apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/2");

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Test
    public void testGetUserDetailsAuthAsSameUser() {

        Response responseUserData = loginAndGetDetails(true);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    // Ex16: Запрос данных другого пользователя
    @Test
    public void testGetUserDetailsAuthAsAnotherUser() {

        Response responseUserData = loginAndGetDetails(false);

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    private Response loginAndGetDetails(boolean isTheSameUser) {
        Map<String, String> authData = DataGenerator.getDefaultLoginData();

        Response responseGetAuth =
                apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        String url = "https://playground.learnqa.ru/api/user/" +
                (isTheSameUser ? responseGetAuth.getBody().path("user_id").toString() : 1);

        return apiCoreRequests.makeGetRequest(url, header, cookie);
    }
}
