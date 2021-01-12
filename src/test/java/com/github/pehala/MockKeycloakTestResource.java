package com.github.pehala;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.jwt.build.Jwt;
import org.jboss.logging.Logger;

import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * https://github.com/quarkusio/quarkus/blob/master/integration-tests/oidc-wiremock/src/test/java/io/quarkus/it/keycloak/KeycloakTestResource.java
 */
public class MockKeycloakTestResource implements QuarkusTestResourceLifecycleManager {
    private static final Logger LOG = Logger.getLogger(MockKeycloakTestResource.class);
    private WireMockServer server;
    public static final String HS256_SECRET = "B3KIh5W4Yl/8gN1xnAen0QOOFFLew2z36B1Dlxl2KmI=";

    @Override
    public Map<String, String> start() {

        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();
        server.stubFor(get(urlEqualTo("/auth/realms/quarkus/.well-known/openid-configuration"))
            .willReturn(aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                .withBody("{\n" +
                          "    \"jwks_uri\": \"" + server.baseUrl() +
                          "/auth/realms/quarkus/protocol/openid-connect/certs\"\n" +
                          "}")));
        server.stubFor(get(urlEqualTo("/auth/realms/quarkus/protocol/openid-connect/certs"))
            .willReturn(aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                .withBody("{\n" +
                          "    \"keys\": [\n " +
                          "        {\n "+
                          "         \"kty\" : \"oct\",\n"+
                          "         \"kid\" : \"1\",\n"+
                          "         \"alg\" : \"HS256\",\n"+
                          "         \"k\"   : \""+ HS256_SECRET + "\"\n"+
                          "       }"+
                          "   ]"+
                          "}")));
        LOG.infof("Keycloak started in mock mode: %s", server.baseUrl());
        return Collections.singletonMap("quarkus.oidc.auth-server-url", server.baseUrl() + "/auth/realms/quarkus");
    }

    @Override
    public synchronized void stop() {
        if (server != null) {
            server.stop();
            LOG.info("Keycloak was shut down");
            server = null;
        }
    }

    public static String getAccessToken(String userName, String... roles) {
        return Jwt.preferredUserName(userName)
                .claim("name", userName)
                .claim("sub", userName)
                .claim("email", userName)
                .groups(new HashSet<>(Arrays.asList(roles)))
                .signWithSecret(HS256_SECRET);
    }

    public static String getAccessToken(String userName) {
        return Jwt.preferredUserName(userName)
                .claim("name", userName)
                .claim("sub", userName)
                .claim("email", userName)
                .signWithSecret(HS256_SECRET);
    }
}
