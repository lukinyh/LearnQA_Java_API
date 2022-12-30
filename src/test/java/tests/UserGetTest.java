package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

@Epic("Getting detail info cases")
@Feature("Detail information")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Negative test: get data without authorization")
    @DisplayName("Get user data without authorization")
    public void testGetUserDataNotAuth() {
        Response responseUserData =
                apiCoreRequests.makeGetRequest(URLs.GET_USER_INFO +"2");

        Assertions.assertJsonHasField(responseUserData, User.USERNAME);
        Assertions.assertJsonHasNotField(responseUserData, User.FIRSTNAME);
        Assertions.assertJsonHasNotField(responseUserData, User.LASTNAME);
        Assertions.assertJsonHasNotField(responseUserData, User.EMAIL);
    }

    @Test
    @Description("Positive test: get user data with authorization")
    @DisplayName("Get user data")
    public void testGetUserDetailsAuthAsSameUser() {

        Response responseUserData = loginAndGetDetails(true);

        String[] expectedFields = {User.USERNAME, User.FIRSTNAME, User.LASTNAME, User.EMAIL};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    // Ex16: Запрос данных другого пользователя
    @Test
    @Description("Negative test: get user details via another user")
    @DisplayName("Get user details via another user")
    public void testGetUserDetailsAuthAsAnotherUser() {

        Response responseUserData = loginAndGetDetails(false);

        Assertions.assertJsonHasField(responseUserData, User.USERNAME);
        Assertions.assertJsonHasNotField(responseUserData, User.FIRSTNAME);
        Assertions.assertJsonHasNotField(responseUserData, User.LASTNAME);
        Assertions.assertJsonHasNotField(responseUserData, User.EMAIL);
    }

    private Response loginAndGetDetails(boolean isTheSameUser) {
        Map<String, String> authData = DataGenerator.getDefaultData();

        Response responseGetAuth =
                apiCoreRequests.makePostRequest(URLs.LOGIN, authData);

        String header = this.getHeader(responseGetAuth, User.X_CSRF);
        String cookie = this.getCookie(responseGetAuth, User.AUTH_SID);

        String url = URLs.GET_USER_INFO +
                (isTheSameUser ? responseGetAuth.getBody().path(User.USER_ID).toString() : 1);

        return apiCoreRequests.makeGetRequest(url, header, cookie);
    }
}
