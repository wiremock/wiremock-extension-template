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

## Creating an ExtensionFactory or Extension (auto-detection)

As of WireMock 3.x, extensions should support
[auto-detection](https://wiremock.org/docs/extending-wiremock/#extension-registration-via-service-loading)
to ease integration:
- [WireMock](https://wiremock.org/docs/configuration/#extensions),
- [WireMock standalone](https://wiremock.org/docs/standalone/java-jar/)
- [Docker containers](https://wiremock.org/docs/standalone/docker/)

For supporting auto-detection as standalone library, this template implements the
[Java Service Provider Interface](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html), which is used by
WireMock to find extensions.

When forking this repository, you have to adapt 
`src/main/resources/META-INF/services/com.github.tomakehurst.wiremock.extension.ExtensionFactory`
to point to a class with a default constructor which registers all parts of your extension
(in this template, this is handled by `org.wiremock.extensions.template.TemplateExtension`).

Alternatively, if you are only creating a single extension (and do not need access to the WireMockServices), you can
create a file `src/main/resources/META-INF/services/com.github.tomakehurst.wiremock.extension.Extension` and point to a
class with a default constructor which implements one of the many
[extension points of WireMock](https://wiremock.org/docs/extending-wiremock).

## Request matcher

Official documentation on using request matchers: https://wiremock.org/docs/request-matching/
Official documentation on creating request matchers extension: https://wiremock.org/docs/extensibility/custom-matching/

The sample code in this template shows how to read parameters and values which contain [handlebar templates](https://handlebarsjs.com)
to get similar functionalities as when using them in [response templates](https://wiremock.org/docs/response-templating/).

See `org.wiremock.extensions.template.TemplateRequestMatcher` for a sample and `org.wiremock.extensions.template.TemplateRequestMatcherTest`
for a corresponding test.
