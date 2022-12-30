package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Edit user cases")
@Feature("Editing")
public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    int userIdOnAuth;
    Map<String, String> userData;

    Response generateUserResponse;

    @BeforeEach
    public void generateUser() {
        this.userData = DataGenerator.getRegistrationData();

        this.generateUserResponse = apiCoreRequests.makePostRequest(
                URLs.CREATE_USER, this.userData);

        this.userIdOnAuth = this.getIntFromJson(generateUserResponse, User.ID);
    }

    @Test
    @Description("This test successfully edit user after registration")
    @DisplayName("Test positive edit user parameters")
    public void testEditJustCreatedTest() {
        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put(User.EMAIL, this.userData.get(User.EMAIL));
        authData.put(User.PASSWORD, this.userData.get(User.PASSWORD));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URLs.LOGIN, authData);

        // EDIT
        String newName = "ChangedName";
        Map<String, String> editData = new HashMap<>();
        editData.put(User.FIRSTNAME, newName);
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, User.USER_ID);

        Response responseUserData = updateDataAndCheck(
                responseGetAuth,
                this.userIdOnAuth,
                editData,
                false,
                "",
                200
        );

        Assertions.assertJsonByName(responseUserData, User.FIRSTNAME, newName);
     }

    // Ex17: Негативные тесты PUT
    // 1. изменить данные пользователя, будучи неавторизованными
    @Test
    @Description("Negative test: change data without authorization")
    @DisplayName("Test edit without authorization")
    public void testEditWithoutAuthorization() {
        String newName = "ChangedUserName";
        Map<String, String> editData = new HashMap<>();
        editData.put(User.USERNAME, newName);

        // PUT
        int userId = this.userIdOnAuth;
        Response responseEditUser = apiCoreRequests
                .makePutRequestWithoutAuthentication(
                        URLs.UPDATE_USER + userId,
                        editData)
                ;
        Assertions.assertResponseTextEquals(responseEditUser, Errors.AUTH_TOKEN_NOT_SUPPLIED);
        Assertions.assertResponseCodeEquals(responseEditUser, 400);

        // GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(URLs.GET_USER_INFO + userId)
                ;

        Assertions.assertJsonByName(responseUserData, 
                User.USERNAME,
                userData.get(User.USERNAME));
    }

    // Ex17: Негативные тесты PUT
    // 2. изменить данные пользователя, будучи авторизованными другим пользователем
    @Test
    @Description("Negative test: change data using another user")
    @DisplayName("Edit fields from another user")
    public void testEditFieldsFromAnotherUser() {
        // LOGIN with default user
        Map<String, String> authData = DataGenerator.getDefaultData();

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URLs.LOGIN, authData);

        // EDIT created user in method of @BeforeEach
        String newName = "ChangedName";
        Map<String, String> editData = new HashMap<>();
        editData.put(User.USERNAME, newName);

        Response responseUserData = updateDataAndCheck(
                responseGetAuth,
                this.userIdOnAuth,
                editData,
                false,
                "",
                200
        );

        Assertions.assertJsonByName(responseUserData, User.USERNAME, this.userData.get("username"));
    }

    // Ex17: Негативные тесты PUT
    // 3. изменить email пользователя, будучи авторизованными тем же пользователем, на новый email без символа @
    @Test
    @Description("Negative test: change field (email) to field with incorrect format")
    @DisplayName("Edit field 'email' to incorrect format")
    public void testEditFieldEmailToIncorrectEmail() {
        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put(User.EMAIL, this.userData.get(User.EMAIL));
        authData.put(User.PASSWORD, this.userData.get(User.PASSWORD));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URLs.LOGIN, authData);

        // EDIT
        String newEmail = "changedemail.com";
        Map<String, String> editData = new HashMap<>();
        editData.put(User.EMAIL, newEmail);
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, User.USER_ID);

        Response responseUserData = updateDataAndCheck(
                responseGetAuth,
                this.userIdOnAuth,
                editData,
                false,
                Errors.INVALID_EMAIL_FORMAT,
                400
        );
        
        Assertions.assertJsonByName(responseUserData,
                User.EMAIL,
                this.userData.get(User.EMAIL));
    }

    // Ex17: Негативные тесты PUT
    // 4. изменить firstName пользователя, будучи авторизованными тем же пользователем, на очень короткое значение в один символ
    @Test
    @Description("Negative test: change firstName to incorrect data")
    @DisplayName("Edit field firstName to incorrect data")
    public void testEditFieldFirstNameToIncorrectData() {
        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put(User.EMAIL, this.userData.get(User.EMAIL));
        authData.put(User.PASSWORD, this.userData.get(User.PASSWORD));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URLs.LOGIN, authData);

        // EDIT
        String newFirstName = "a";
        Map<String, String> editData = new HashMap<>();
        editData.put(User.FIRSTNAME, newFirstName);
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, User.USER_ID);

        Response responseUserData = updateDataAndCheck(
                responseGetAuth, 
                this.userIdOnAuth, 
                editData,
                true, 
                Errors.TOO_SHORT_FIRSTNAME,
                400
        );
        
        Assertions.assertJsonByName(responseUserData,
                User.FIRSTNAME,
                this.userData.get(User.FIRSTNAME));
    }

    private Response updateDataAndCheck(
                                Response responseGetAuth,
                                int userId,
                                Map<String, String> objectOfChange,
                                boolean isJSON,
                                String assertAfterPut,
                                int statusCode
    ) {

        String token = this.getHeader(responseGetAuth, User.X_CSRF);
        String cookie = this.getCookie(responseGetAuth, User.AUTH_SID);

        Response responsePutRequest = apiCoreRequests
                .makePutRequest(URLs.UPDATE_USER + userId,
                        token,
                        cookie,
                        objectOfChange)
                ;

        if (isJSON) {
            Assertions.assertJsonByName(responsePutRequest, Errors.ERROR, assertAfterPut);
        } else {
            Assertions.assertResponseTextEquals(responsePutRequest, assertAfterPut);
        }

        Assertions.assertResponseCodeEquals(responsePutRequest, statusCode);

        return apiCoreRequests
                .makeGetRequest(URLs.GET_USER_INFO + userId,
                        token,
                        cookie)
                ;
    }

    @AfterEach
    public void finishTest() {
        this.removeUser(this.userData);
        updateDefaultUserToDefaultValues();
    }

    private void updateDefaultUserToDefaultValues() {

        Map<String, String> defaultData = DataGenerator.getDefaultData();

        Map<String, String> authData = new HashMap<>();
        authData.put(User.EMAIL, defaultData.get(User.EMAIL));
        authData.put(User.PASSWORD, defaultData.get(User.PASSWORD));

        // LOGIN
        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URLs.LOGIN, authData);

        String token = this.getHeader(responseGetAuth, User.X_CSRF);
        String cookie = this.getCookie(responseGetAuth, User.AUTH_SID);

        apiCoreRequests.makePutRequest(
                URLs.UPDATE_USER + defaultData.get(User.ID),
                token,
                cookie,
                DataGenerator.getDefaultDataForUpdate())
        ;

    }
}
