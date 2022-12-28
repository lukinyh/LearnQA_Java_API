package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    // Ex 15.1:  Создание пользователя с некорректным email
    // Первый без символа @
    @ParameterizedTest
    @ValueSource(strings = {"Abc.example.com", "A@b@c@example.com", "a\"b(c)d,e:f;gi[j\\k]l@example.com",
    "just\"not\"right@example.com", "this is\"not\\allowed@example.com", "this\\ still\\\"not\\allowed@example.com",
    "1234567890123456789012345678901234567890123456789012345678901234+x@example.com", "john..doe@example.com",
    "example@localhost", "john.doe@example..com", "\"much.more unusual\"@example.com",
    " space@before.com", "space@after.com "})
    public void testCreateUserWithIncorrectEmail(String email) {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    // Ex15.2 - Создание пользователя без указания одного из полей
    // с помощью @ParameterizedTest необходимо проверить, что отсутствие любого параметра не дает зарегистрировать пользователя
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithoutOneParameter(String parameter) {
        Map<String, String> userData = DataGenerator.getRegistrationDataWithoutParameter(parameter);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + parameter);
    }

    // Ex15.3-4:
    // Создание пользователя с очень коротким именем в один символ
    // Создание пользователя с очень длинным именем - длиннее 250 символов
    @ParameterizedTest
    @ValueSource(strings = {"", "s", "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901"})
    public void testCreateUserWithNotStandardFirstName(String value) {
        testCreateUserWithNotStandardParameter("firstName", value);
    }

    // Ex15.3-4:
    // Создание пользователя с очень коротким именем в один символ
    // Создание пользователя с очень длинным именем - длиннее 250 символов
    @ParameterizedTest
    @ValueSource(strings = {"", "s", "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901"})
    public void testCreateUserWithNotStandardUserName(String value) {
        testCreateUserWithNotStandardParameter("username", value);
    }

    private void testCreateUserWithNotStandardParameter(String parameter, String value) {
        Map<String, String> userData = new HashMap<>();
        userData.put(parameter, value);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);

        String expectedAnswer = "The value of '" + parameter + "' field is too ";
        expectedAnswer += (value.length() > 1 ? "long" : "short");

        Assertions.assertResponseTextEquals(responseCreateAuth, expectedAnswer);
    }
}
