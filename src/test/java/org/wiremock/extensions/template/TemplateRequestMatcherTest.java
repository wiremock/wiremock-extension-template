package org.wiremock.extensions.template;

import com.github.tomakehurst.wiremock.core.ConfigurationException;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.responsetemplating.TemplateEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TemplateRequestMatcherTest {

    static final TemplateRequestMatcher templateRequestMatcher = new TemplateRequestMatcher(TemplateEngine.defaultTemplateEngine());

    @Test
    public void whenGetName_thenEqual() {
        Assertions.assertEquals("template-request-matcher", templateRequestMatcher.getName());
    }

    @Test
    public void givenEmptyParameters_whenMatch_thenNoMatch() {
        var request = MockRequest.mockRequest()
            .url("/template-request-matcher/true");
        var parameters = Parameters.empty();
        var matchResult = templateRequestMatcher.match(request, parameters);
        Assertions.assertFalse(matchResult.isExactMatch());
    }

    @Test
    public void givenParametersAndTrueTemplate_whenMatch_thenExactMatch() {
        var request = MockRequest.mockRequest()
            .url("/template-request-matcher/true");
        var parameters = Parameters.one("isTrue", "{{request.pathSegments.[1]}}");
        var matchResult = templateRequestMatcher.match(request, parameters);
        Assertions.assertTrue(matchResult.isExactMatch());
    }

    @Test
    public void givenParametersAndFalseTemplate_whenMatch_thenNoMatch() {
        var request = MockRequest.mockRequest()
            .url("/template-request-matcher/false");
        var parameters = Parameters.one("isTrue", "{{request.pathSegments.[1]}}");
        var matchResult = templateRequestMatcher.match(request, parameters);
        Assertions.assertFalse(matchResult.isExactMatch());
    }

    @Test
    public void givenBadTemplate_whenMatch_thenConfigurationException() {
        var request = MockRequest.mockRequest()
            .url("/template-request-matcher/true");
        var parameters = Parameters.one("isTrue", "{{badTemplate}}");
        Assertions.assertThrows(ConfigurationException.class, () -> templateRequestMatcher.match(request, parameters));
    }
}
