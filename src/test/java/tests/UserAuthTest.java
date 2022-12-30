package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;

@Epic("Authorization cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {
    String cookie;
    String header;
    int userIdOnAuth;

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser() {
        HashMap<String, String> authData = new HashMap<>();
        authData.put(User.EMAIL, "vinkotov@example.com");
        authData.put(User.PASSWORD, "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URLs.LOGIN, authData);

        this.cookie = this.getCookie(responseGetAuth, User.AUTH_SID);
        this.header = this.getHeader(responseGetAuth, User.X_CSRF);
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, User.USER_ID);
    }

    @Test
    @Description("This test successfully authorize user by email and password")
    @DisplayName("Test positive auth user")
    public void testAuthUser() {
        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(
                        URLs.GET_USER_ID,
                        this.header,
                        this.cookie
                );

        Assertions.assertJsonByName(responseCheckAuth, User.USER_ID, this.userIdOnAuth);
    }

    @Description("This is test checks authorization status w/o sending auth cookie or token")
    @DisplayName("Test negative auth user")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition) {

        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(
                    URLs.GET_USER_ID,
                    this.cookie
            );
            Assertions.assertJsonByName(responseForCheck, User.USER_ID, 0);
        } else if (condition.equals("headers")){
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(
                    URLs.GET_USER_ID,
                    this.header
            );
            Assertions.assertJsonByName(responseForCheck, User.USER_ID, 0);
        } else {
            throw new IllegalArgumentException("Condition value is not known: " + condition);
        }
    }
}

