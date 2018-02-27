package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

/**
 * TASK:
 * POST request to  https://guarded-mesa-31536.herokuapp.com/track
 * fields: name,github,email
 *
 * LIB: http://unirest.io/java.html
 *
 *
 */
public class App {

    public static final String URL = "http://guarded-mesa-31536.herokuapp.com/track";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_GITHUB = "github";
    public static final String FIELD_EMAIL = "email";

    public static void main(String[] args) throws Exception {
        HttpResponse<JsonNode> response = Unirest.post(URL)
                .header("accept", "application/json")
                .field(FIELD_NAME, "Churyakov Mikhail")
                .field(FIELD_GITHUB, "https://github.com/mikhailchuryakov")
                .field(FIELD_EMAIL, "mike_ch@mail.ru")
                .asJson();

        response.getBody().getObject().get("success");

        boolean success = false;
    }

}
