package br.com.atlantico.authservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class AuthServiceTest {

    final static String AUTH_SERVICE_URL = "http://localhost:8080";

    final String OAUTH_GRANT_TYPE = "password";

    final String USERNAME_API = "clientapi";
    final String PASSWORD_API = "clientapi";

    final String USERNAME_OAUTH = "admin";
    final String PASSWORD_OAUTH = "admin";

    final String OAUTH_PATH = "/oauth/token";

    RequestSpecification oauthRequest;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = AUTH_SERVICE_URL;
    }

    @Before
    public void before() {
        oauthRequest = given().
                contentType("application/x-www-form-urlencoded").
                auth().basic(USERNAME_API, PASSWORD_API);
    }

    @Test
    public void successfulOauthTokenRequest() {
        oauthRequest.
            formParam("username", USERNAME_OAUTH).
            formParam("password", PASSWORD_OAUTH).
            formParam("grant_type", OAUTH_GRANT_TYPE).
        when().
            post(OAUTH_PATH).
        then().
            assertThat().statusCode(200).
            assertThat().contentType(ContentType.JSON).
            body("access_token", notNullValue()).
            body("token_type", equalTo("bearer")).
            body("scope", equalTo("read write"));
    }

    @Test
    public void testInvalidOauthUser() {
        oauthRequest.
            formParam("password", PASSWORD_OAUTH).
            formParam("grant_type", OAUTH_GRANT_TYPE).
        when().
            post(OAUTH_PATH).
        then().
            assertThat().statusCode(400).
            assertThat().contentType(ContentType.JSON).
            body("error", equalTo("invalid_grant")).
            body("error_description", equalTo("Bad credentials"));
    }

    @Test
    public void testInvalidOauthPassword() {
        oauthRequest.
            formParam("username", USERNAME_OAUTH).
            formParam("grant_type", OAUTH_GRANT_TYPE).
        when().
            post(OAUTH_PATH).
        then().
            assertThat().statusCode(400).
            assertThat().contentType(ContentType.JSON).
            body("error", equalTo("invalid_grant")).
            body("error_description", equalTo("Bad credentials"));
    }

    @Test
    public void testInvalidOauthGrantType() {
        oauthRequest.
            formParam("username", USERNAME_OAUTH).
            formParam("password", PASSWORD_OAUTH).
        when().
            post(OAUTH_PATH).
        then().
            assertThat().statusCode(400).
            assertThat().contentType(ContentType.JSON).
            body("error", equalTo("invalid_request")).
            body("error_description", equalTo("Missing grant type"));
    }
}
