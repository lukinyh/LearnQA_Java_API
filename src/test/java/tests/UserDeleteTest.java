package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    int userIdOnAuth;
    Map<String, String> userData;
    Response generateUserResponse;

    // Ex18: Тесты на DELETE
    // Удалить пользователя с id = 2
    @Description("Negative test: trying to remove user which saved from removing")
    @Test
    public void testRemoveReservationUser() {
        Map<String, String> authData = DataGenerator.getSecondLoginData();

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URLs.LOGIN, authData);

        int userIdOnAuth = this.getIntFromJson(responseGetAuth, User.USER_ID);
        String token = this.getHeader(responseGetAuth, User.X_CSRF);
        String cookie = this.getCookie(responseGetAuth, User.AUTH_SID);

        // REMOVE
        Response responseToRemoveUser = apiCoreRequests
                .makeDeleteRequest(
                        URLs.DELETE_USER + userIdOnAuth,
                        token,
                        cookie,
                        authData);

        Assertions.assertResponseCodeEquals(responseToRemoveUser, 400);
        Assertions.assertResponseTextEquals(responseToRemoveUser, Errors.DO_NOT_DELETE);
    }

    // Ex18: Тесты на DELETE
    // Удалить созданного пользователя
    // Убедиться, что пользователь удален
    @Test
    @Description("Positive test: successful remove user")
    public void testRemoveStandardUser() {
        registerUser();
        Response responseToRemoveUser = this.removeUser(this.userData);
        Assertions.assertResponseCodeEquals(responseToRemoveUser, 200);

        Response responseUserData =
                apiCoreRequests.makeGetRequest(URLs.GET_USER_INFO + this.userIdOnAuth);

        Assertions.assertResponseTextEquals(responseUserData, Errors.USER_NOT_FOUND);
    }

    @Step("Register user")
    private void registerUser() {
        this.userData = DataGenerator.getRegistrationData();

        this.generateUserResponse = apiCoreRequests.makePostRequest(
                URLs.CREATE_USER, this.userData);

        this.userIdOnAuth = this.getIntFromJson(generateUserResponse, User.ID);
    }


    // Ex18: Тесты на DELETE
    // Попробовать удалить пользователя будучи авторизованным другим пользователем.
    @Test
    @Description("Negative test: remove not own user")
    public void testRemoveNotOwnUser() {
        // REGISTER USER TO REMOVE
        registerUser();
        int firstUserId = this.userIdOnAuth;

        // REGISTER USER TO LOGIN
        registerUser();

        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put(User.EMAIL, userData.get(User.EMAIL));
        authData.put(User.PASSWORD, userData.get(User.PASSWORD));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URLs.LOGIN, authData);

        String token = this.getHeader(responseGetAuth, User.X_CSRF);
        String cookie = this.getCookie(responseGetAuth, User.AUTH_SID);

        // REMOVE
        Response responseRemoveFirstUser = apiCoreRequests
                .makeDeleteRequest(
                        URLs.DELETE_USER + firstUserId,
                        token,
                        cookie,
                        authData);

        Assertions.assertResponseCodeEquals(responseRemoveFirstUser, 200);

        Response responseUserData =
                apiCoreRequests.makeGetRequest(URLs.GET_USER_INFO + firstUserId);

        Assertions.assertJsonHasField(responseUserData, "username");
    }
}
