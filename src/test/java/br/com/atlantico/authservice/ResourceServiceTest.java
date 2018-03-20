package br.com.atlantico.authservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class ResourceServiceTest {

    final static String AUTH_SERVICE_URL = "http://localhost:8082";

    final String CURRENT_USER_PATH = "/user/current";
    String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MjE1NzgxODcsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9ST0xFIl0sImp0aSI6ImJiNzZiZDQ4LTc0ODAtNDRiNy1iYmRkLWY3Zjc2NmY4YzIwYyIsImNsaWVudF9pZCI6ImNsaWVudGFwaSIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.ZA6UNPc7M_3wIL9X63pLJm_4cDjNFO5R89VR0G1ZokA";

    RequestSpecification oauthRequest;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = AUTH_SERVICE_URL;
    }

    @Test
    public void successfulOauthResourceRequest() {
        given().
            auth().oauth2(accessToken).
        when().
            get(CURRENT_USER_PATH).
        then().
            assertThat().statusCode(200).
            assertThat().contentType(ContentType.JSON).
            body("username", equalTo("admin")).
            body("clientId", equalTo("clientapi")).
            body("authorities", hasSize(1)).
            body("authorities.authority", contains("ROLE_ROLE")).
            body("password", nullValue());
    }

    @Test
    public void testEmptyOauthToken() {
        when().
            get(CURRENT_USER_PATH).
        then().
            assertThat().statusCode(401).
            assertThat().contentType(ContentType.JSON).
            body("error", equalTo("unauthorized")).
            body("error_description", equalTo("Full authentication is required to access this resource"));
    }

    @Test
    public void testInvalidOauthToken() {
        given().
            auth().oauth2("xpto").
        when().
            get(CURRENT_USER_PATH).
        then().
            assertThat().statusCode(401).
            assertThat().contentType(ContentType.JSON).
            body("error", equalTo("invalid_token")).
            body("error_description", equalTo("Cannot convert access token to JSON"));
    }

    @Ignore
    @Test
    public void testExpiredOauthToken() {
        given().
            auth().oauth2(accessToken).
        when().
            get(CURRENT_USER_PATH).
        then().
            assertThat().statusCode(401).
            assertThat().contentType(ContentType.JSON);
//            body("error", equalTo("invalid_token")).
//            body("error_description", equalTo("Cannot convert access token to JSON"));
    }
}
