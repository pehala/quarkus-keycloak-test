package com.github.pehala;

import com.github.pehala.resteasyjackson.TestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static com.github.pehala.MockKeycloakTestResource.getAccessToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(MockCoreServer.class)
@QuarkusTestResource(MockKeycloakTestResource.class)
@TestHTTPEndpoint(TestResource.class)
public class TestService {

    @Test
    void testWithAuth() {
        given()
                .when()
                .auth()
                .oauth2(getAccessToken("non-existent", "admin"))
                .get("/test")
                .then()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    void testNoAuth() {
        given()
                .when()
                .get("/testNonAuth")
                .then()
                .statusCode(200);
    }

    @Test
    void testHeaderAuth() {
        given()
                .when()
                .auth()
                .oauth2(getAccessToken("non-existent", "admin"))
                .get("/testHeader")
                .then()
                .statusCode(200);
    }
}
