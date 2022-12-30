package lib;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {
    public static String getRandomEmail() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "learnqa" + timestamp + "@example.com";
    }

    public static Map<String, String> getRegistrationData() {
        Map<String, String> data = new HashMap<>();
        data.put("email", DataGenerator.getRandomEmail());
        data.put("password", "123");
        data.put("username", "learnqa");
        data.put("firstName", "learnqa");
        data.put("lastName", "learnqa");

        return data;
    }

    public static Map<String, String> getSecondLoginData() {
        Map<String, String> data = new HashMap<>();
        data.put("email", "vinkotov@example.com");
        data.put("password", "1234");

        return data;
    }

    public static Map<String, String> getDefaultData() {

        Map<String, String> data = getDefaultDataForUpdate();
        data.put("email", "defaultemail@gmail.com");
        data.put("password", "1234");
        data.put("id", "57204");

        return data;
    }

    public static Map<String, String> getDefaultDataForUpdate() {

        Map<String, String> data = new HashMap<>();
        data.put("firstName", "testFirstName");
        data.put("lastName", "testLastName");
        data.put("username", "testUsername");

        return data;
    }

    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues) {
        Map<String, String> defaultValues = DataGenerator.getRegistrationData();

        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        for (String key : keys) {
            if (nonDefaultValues.containsKey(key)) {
                userData.put(key, nonDefaultValues.get(key));
            } else {
                userData.put(key, defaultValues.get(key));
            }
        }

        return userData;
    }

    public static Map<String, String> getRegistrationDataWithoutParameter(String parameter) {
        Map<String, String> defaultValues = DataGenerator.getRegistrationData();

        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        for (String key : keys) {
            if (!key.equals(parameter)) {
                userData.put(key, defaultValues.get(key));
            }
        }

        return userData;
    }
}
