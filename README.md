# WireMock extension template

<p align="center">
    <a href="https://wiremock.org" target="_blank">
        <img width="512px" src="https://wiremock.org/images/logos/wiremock/logo_wide.svg" alt="WireMock Logo"/>
    </a>
</p>

Template for creating new WireMock extensions.

# Template usage

This template contains various samples on how you can write your own extension - just clone it and get started.

Not all parts are needed, thus feel free to adapt it. The code is documented and marked with `TODO` comments to help
finding customization points.

# Manuals

## Overall extension documentation

Extensions can be explicitly configured or auto-detected. This extension supports auto-detection which eases integration
in [WireMock standalone](https://wiremock.org/docs/standalone/java-jar/) or [docker containers](https://wiremock.org/docs/standalone/docker/).

For supporting auto-detection as standalone library, this template implements the
[Java Service Provider Interface](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html), which is used by WireMock
to find extensions and packages everything using shadowjar.

When forking this repository, you have to adapt `shadowjar/resources/META-INF/services/com.github.tomakehurst.wiremock.extension.ExtensionFactory`
to point to a class with default constructor which registers all parts of your extension
(in this template, this is handled by `org.wiremock.extensions.template.StandaloneTemplateExtension`).

For non-standalone usages, you can use your own class to instantiate your extension. In this template,
this is handled by `org.wiremock.extensions.template.TemplateExtension`. Having the standalone version using this one
helps ensuring a consistent extension setup.

## Request matcher

Official documentation on using request matchers: https://wiremock.org/docs/request-matching/
Official documentation on creating request matchers extension: https://wiremock.org/docs/extensibility/custom-matching/

The sample code in this template shows how to read parameters and values which contain [handlebar templates](https://handlebarsjs.com)
to get similar functionalities as when using them in [response templates](https://wiremock.org/docs/response-templating/).

See `org.wiremock.extensions.template.extensions.RequestMatcher` for a sample and `org.wiremock.extensions.template.RequestMatcherTest`
for a corresponding test.
