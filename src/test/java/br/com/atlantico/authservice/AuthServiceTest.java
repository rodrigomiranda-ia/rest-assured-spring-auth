package br.com.atlantico.authservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class AuthServiceTest {

    private static String AUTH_SERVICE_URL = "http://localhost:8080";

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = AUTH_SERVICE_URL;
    }

    @Test
    public void successfulOauthTokenRequest() {
        String clientId = "clientapi";
        String usernameOauth = "admin";
        String passwordOauth = "admin";
        String oauthGrantType = "password";

        String usernameApi = "clientapi";
        String passwordApi = "clientapi";

        given().
            formParam("client_id", clientId).
            formParam("username", usernameOauth).
            formParam("password", passwordOauth).
            formParam("grant_type", oauthGrantType).
            auth().basic(usernameApi, passwordApi).
            contentType("application/x-www-form-urlencoded").
        when().
            post("/oauth/token").
        then().
            assertThat().statusCode(200).
            assertThat().contentType(ContentType.JSON).
            body("access_token", notNullValue()).
            body("token_type", equalTo("bearer")).
            body("expires_in", equalTo(1799)).
            body("scope", equalTo("read write"));
    }
}
