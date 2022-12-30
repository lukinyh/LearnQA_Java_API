package lib;

import io.restassured.http.Headers;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    protected String getHeader(Response Response, String name) {
        Headers headers = Response.getHeaders();

        assertTrue(headers.hasHeaderWithName(name), "Response doesn't have header with name " + name);
        return headers.getValue(name);
    }

    protected String getCookie(Response Response, String name) {
        Map<String, String> cookies = Response.getCookies();

        assertTrue(cookies.containsKey(name), "Response doesn't have cookie with name " + name);
        return cookies.get(name);
    }

    protected int getIntFromJson(Response Response, String name) {
        Response.then().assertThat().body("$", hasKey(name));
        return Response.jsonPath().getInt(name);
    }

    protected Response removeUser(Map<String, String> userData) {
        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put(User.EMAIL, userData.get(User.EMAIL));
        authData.put(User.PASSWORD, userData.get(User.PASSWORD));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URLs.LOGIN, authData);

        int id  = this.getIntFromJson(responseGetAuth, User.USER_ID);
        String token = this.getHeader(responseGetAuth, User.X_CSRF);
        String cookie = this.getCookie(responseGetAuth, User.AUTH_SID);

        // REMOVE
        return apiCoreRequests
                .makeDeleteRequest(
                        URLs.DELETE_USER + id,
                        token,
                        cookie,
                        authData);
    }
}