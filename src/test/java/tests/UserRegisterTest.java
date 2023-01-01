package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @Description("Negative test: create user with existed email")
    @DisplayName("Create User with existed email")
    public void testCreateUserWithExistedEmail() {
        String email = "vinkotov@example.com";
        Map<String, String> userData = new HashMap<>();
        userData.put(User.EMAIL, email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(URLs.CREATE_USER, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);

        String expectedAnswer = String.format(Errors.EMAIL_EXISTS, email);
        Assertions.assertResponseTextEquals(responseCreateAuth, expectedAnswer);
    }

    @Test
    @Description("Positive test: create user")
    @DisplayName("Create user")
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(URLs.CREATE_USER, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, User.ID);
    }

    // Ex 15.1:  Создание пользователя с некорректным email
    // Первый без символа @
    @Description("Negative test: create user with incorrect email")
    @ParameterizedTest
    @ValueSource(strings = {"Abc.example.com", "A@b@c@example.com", "a\"b(c)d,e:f;gi[j\\k]l@example.com",
    "just\"not\"right@example.com", "this is\"not\\allowed@example.com", "this\\ still\\\"not\\allowed@example.com",
    "1234567890123456789012345678901234567890123456789012345678901234+x@example.com", "john..doe@example.com",
    "example@localhost", "john.doe@example..com", "\"much.more unusual\"@example.com",
    " space@before.com", "space@after.com "})
    public void testCreateUserWithIncorrectEmail(String email) {
        Map<String, String> userData = new HashMap<>();
        userData.put(User.EMAIL, email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(URLs.CREATE_USER, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, Errors.INVALID_EMAIL_FORMAT);
    }

    // Ex15.2 - Создание пользователя без указания одного из полей
    // с помощью @ParameterizedTest необходимо проверить, что отсутствие любого параметра не дает зарегистрировать пользователя
    @Description("Negative test: create user without one of the field")
    @ParameterizedTest
    @ValueSource(strings = {User.EMAIL, User.PASSWORD, User.USERNAME, User.FIRSTNAME, User.LASTNAME})
    public void testCreateUserWithoutOneParameter(String parameter) {
        Map<String, String> userData = DataGenerator.getRegistrationDataWithoutParameter(parameter);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(URLs.CREATE_USER, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, Errors.PARAMS_MISSED + parameter);
    }

    // Ex15.3-4:
    // Создание пользователя с очень коротким именем в один символ
    // Создание пользователя с очень длинным именем - длиннее 250 символов
    @ParameterizedTest
    @ValueSource(strings = {"", "s", "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901"})
    public void testCreateUserWithNotStandardFirstName(String value) {
        testCreateUserWithNotStandardParameter(User.FIRSTNAME, value);
    }

    // Ex15.3-4:
    // Создание пользователя с очень коротким именем в один символ
    // Создание пользователя с очень длинным именем - длиннее 250 символов
    @ParameterizedTest
    @ValueSource(strings = {"", "s", "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901"})
    public void testCreateUserWithNotStandardUserName(String value) {
        testCreateUserWithNotStandardParameter(User.USERNAME, value);
    }

    @Step("Create user with not standard parameter: {parameter} - {value} ")
    private void testCreateUserWithNotStandardParameter(String parameter, String value) {
        Map<String, String> userData = new HashMap<>();
        userData.put(parameter, value);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(URLs.CREATE_USER, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);

        String expectedAnswer = String.format(Errors.VALUE_IS_TOO, parameter, (value.length() > 1 ? "long" : "short"));

        Assertions.assertResponseTextEquals(responseCreateAuth, expectedAnswer);
    }
}
