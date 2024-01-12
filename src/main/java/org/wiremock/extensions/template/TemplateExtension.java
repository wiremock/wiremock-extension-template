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

import java.util.List;

/**
 * Factory to register all extension classes of this extension.
 *
 * <p> As of WireMock 3.x, <a href="https://wiremock.org/docs/extending-wiremock/#extension-registration-via-service-loading">extension registration</a> is
 * designed to be auto-detected via <a href="https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html">Java's Service Provider Interface</a>. Therefore, the
 * class needs to have a default constructor and when changing the class and package name, also adapt
 * src/main/resources/META-INF/services/com.github.tomakehurst.wiremock.extension.ExtensionFactory.
 *
 * <p> <a href="https://wiremock.org/docs/extending-wiremock/#extension-registration-via-service-loading">Registering the extension programmatically</a> is
 * still possible, but discouraged. It can thereby have constructor parameters. All extensions added here are only constructed once, so they should organize any
 * required state with care (or ideally don't have any).
 */
public class TemplateExtension implements ExtensionFactory {

    @Override
    public List<Extension> create(WireMockServices services) {
        return List.of(
            new TemplateRequestMatcher(TemplateEngine.defaultTemplateEngine())
        );
    }
}
