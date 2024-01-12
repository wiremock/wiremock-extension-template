/*
 * Copyright (C) 2023 Dirk Bolte
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wiremock.extensions.template;

import com.github.tomakehurst.wiremock.core.ConfigurationException;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.responsetemplating.RequestTemplateModel;
import com.github.tomakehurst.wiremock.extension.responsetemplating.TemplateEngine;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;

/**
 * Example request matcher which can use the handlebars engine.
 */
public class TemplateRequestMatcher extends RequestMatcherExtension {

    private final TemplateEngine templateEngine;

    public TemplateRequestMatcher(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String getName() {
        return "template-request-matcher";
    }

    @Override
    public MatchResult match(Request request, Parameters parameters) {
        // creating a template model from the current request so that it can be used with handlebars
        Map<String, Object> model = new HashMap<>(Map.of("request", RequestTemplateModel.from(request)));
        String template = parameters.getString("isTrue", "false");
        return isTrue(model, template);
    }

    private MatchResult isTrue(Map<String, Object> model, String template) {
        try {
            // render the configuration template with the provided request model
            var renderedTemplate = renderTemplate(model, template);
            if (new Gson().fromJson(renderedTemplate, Boolean.TYPE)) {
                return MatchResult.exactMatch();
            } else {
                return MatchResult.noMatch();
            }
        } catch (Exception ex) {
            throw createConfigurationError("Unable to interpret template: " + ex.getMessage());
        }
    }

    private ConfigurationException createConfigurationError(String message) {
        notifier().error(message);
        return new ConfigurationException(message);

    }

    String renderTemplate(Object context, String value) {
        return templateEngine.getUncachedTemplate(value).apply(context);
    }

}
