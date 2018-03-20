package br.com.atlantico.authservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class AuthServiceTest {

    final static String AUTH_SERVICE_URL = "http://localhost:8080";

    final String oauthGrantType = "password";

    final String usernameApi = "clientapi";
    final String passwordApi = "clientapi";

    final String usernameOauth = "admin";
    final String passwordOauth = "admin";

    final String oauthPath = "/oauth/token";

    RequestSpecification oauthRequest;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = AUTH_SERVICE_URL;
    }

    @Before
    public void before() {
        oauthRequest = given().
                contentType("application/x-www-form-urlencoded").
                auth().basic(usernameApi, passwordApi);
    }

    @Test
    public void successfulOauthTokenRequest() {
        oauthRequest.
            formParam("username", usernameOauth).
            formParam("password", passwordOauth).
            formParam("grant_type", oauthGrantType).
        when().
            post(oauthPath).
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
            formParam("password", passwordOauth).
            formParam("grant_type", oauthGrantType).
        when().
            post(oauthPath).
        then().
            assertThat().statusCode(400).
            assertThat().contentType(ContentType.JSON).
            body("error", equalTo("invalid_grant")).
            body("error_description", equalTo("Bad credentials"));
    }

    @Test
    public void testInvalidOauthPassword() {
        oauthRequest.
            formParam("username", usernameOauth).
            formParam("grant_type", oauthGrantType).
        when().
            post(oauthPath).
        then().
            assertThat().statusCode(400).
            assertThat().contentType(ContentType.JSON).
            body("error", equalTo("invalid_grant")).
            body("error_description", equalTo("Bad credentials"));
    }

    @Test
    public void testInvalidOauthGrantType() {
        oauthRequest.
            formParam("username", usernameOauth).
            formParam("password", passwordOauth).
        when().
            post(oauthPath).
        then().
            assertThat().statusCode(400).
            assertThat().contentType(ContentType.JSON).
            body("error", equalTo("invalid_request")).
            body("error_description", equalTo("Missing grant type"));
    }
}
