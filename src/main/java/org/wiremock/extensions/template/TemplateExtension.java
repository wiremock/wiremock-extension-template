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

import com.github.tomakehurst.wiremock.extension.Extension;
import com.github.tomakehurst.wiremock.extension.ExtensionFactory;
import com.github.tomakehurst.wiremock.extension.WireMockServices;
import com.github.tomakehurst.wiremock.extension.responsetemplating.TemplateEngine;
import org.wiremock.extensions.template.extensions.RequestMatcher;

import java.util.Collections;
import java.util.List;

/**
 * Factory to register all extension classes of this extension.
 * <p>
 * This class is intended to be initiated when registering the extension programmatically. It can thereby have constructor parameters. All extensions
 * added here are only constructed once, so they should organize any required state with care (or ideally don't have any).
 */
public class TemplateExtension implements ExtensionFactory {

    // TODO: Add the extensions, event listeners, request matchers and everything else needed for this extension.
    private final RequestMatcher requestMatcher;

    public TemplateExtension() {
        var templateEngine = new TemplateEngine(Collections.emptyMap(), null, Collections.emptySet(), false);
        this.requestMatcher = new RequestMatcher(templateEngine);
    }

    @Override
    public List<Extension> create(WireMockServices services) {
        return List.of(
            requestMatcher
        );
    }
}
