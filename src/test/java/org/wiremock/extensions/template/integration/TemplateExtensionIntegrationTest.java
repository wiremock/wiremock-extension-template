package org.wiremock.extensions.template.integration;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

/**
 * Creates two WireMock servers that load the same mappings and __files from src/test/resources:
 * <ul>
 *     <li>locally (current JVM) using {@link WireMockExtension} and standard jar</li>
 *     <li>in Docker container using {@link WireMockContainer} and standalone jar</li>
 * </ul>
 */
@Testcontainers
public class TemplateExtensionIntegrationTest {

    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:3.3.1")
        .withExposedPorts()
        .withExtensions(Collections.emptyList(), standaloneJar())
        .withCopyFileToContainer(MountableFile.forHostPath("src/test/resources"), "/home/wiremock");

    static Collection<File> standaloneJar() {
        try (var stream = Files.list(Paths.get("build/libs"))) {
            // TODO: Update the regex to match the name of the extension standalone JAR file.
            return stream.filter(path -> path.getFileName().toString().matches("wiremock-extension-template-standalone-.*\\.jar"))
                .map(Path::toFile)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
        .options(WireMockConfiguration.options()
            .dynamicPort()
            .extensionScanningEnabled(true))
        .build();

    static Stream<String> baseUrls() {
        return Stream.of(
            wireMockContainer.getBaseUrl(),
            wireMockExtension.getRuntimeInfo().getHttpBaseUrl());
    }

    @ParameterizedTest
    @MethodSource("baseUrls")
    void templateRequestMatcher_matched(String baseUrl) {
        given()
            .accept(ContentType.JSON)
            .get(baseUrl + "/template-request-matcher/true")
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("template-request-matcher-result", Matchers.equalTo("matched"));
    }

    @ParameterizedTest
    @MethodSource("baseUrls")
    void templateRequestMatcher_notMatched(String baseUrl) {
        given()
            .accept(ContentType.JSON)
            .get(baseUrl + "/template-request-matcher/false")
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
