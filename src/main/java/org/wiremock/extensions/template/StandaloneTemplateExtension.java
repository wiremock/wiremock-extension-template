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

/**
 * Factory to this extension for usage with wiremock standalone service.
 *
 * The class needs to have a default constructor.
 * <p/>
 * TODO: When changing the class and package name,
 *  also adapt shadowjar/resources/META-INF/services/com.github.tomakehurst.wiremock.extension.ExtensionFactory
 *  to ensure that this extension can be loaded automatically when its on the classpath.
 */
public class StandaloneTemplateExtension extends TemplateExtension {

    public StandaloneTemplateExtension() {
        super();
    }
}
