package org.wiremock.extensions.template;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.extension.Parameters;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static io.restassured.RestAssured.given;

public class RequestMatcherTest extends AbstractTestBase {

    public static final String RESPONSE_BODY_FIELD = "request-matcher-result";

    @BeforeEach
    public void beforeEach() {
        createStubs();
    }

    @DisplayName("request matchers matches with path segment")
    @Test
    public void test_requestMatcher_matched() {
        given()
            .accept(ContentType.JSON)
            .get(wm.getRuntimeInfo().getHttpBaseUrl() + "/template-matcher/true")
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body(RESPONSE_BODY_FIELD, Matchers.equalTo("matched"));
    }

    @DisplayName("request matchers does not match with path segment")
    @Test
    public void test_requestMatcher_notMatched() {
        given()
            .accept(ContentType.JSON)
            .get(wm.getRuntimeInfo().getHttpBaseUrl() + "/template-matcher/false")
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    private void createStubs() {
        wm.stubFor(
            get(urlPathMatching("/template-matcher/[^/]+"))
                .andMatching("template-matcher",
                    Parameters.from(
                        Map.of("isTrue", "{{request.pathSegments.[1]}}")
                    )
                )
                .willReturn(
                    WireMock.ok()
                        .withHeader("content-type", "application/json")
                        .withJsonBody(Json.node(Json.write(Map.of(RESPONSE_BODY_FIELD, "matched"))))
                )
        );
    }
}
