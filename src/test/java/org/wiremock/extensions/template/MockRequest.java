package org.wiremock.extensions.template;

import static com.github.tomakehurst.wiremock.common.Exceptions.throwUnchecked;
import static com.github.tomakehurst.wiremock.common.ParameterUtils.getFirstNonNull;
import static com.github.tomakehurst.wiremock.common.Strings.bytesFromString;
import static com.github.tomakehurst.wiremock.http.HttpHeader.httpHeader;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

import com.github.tomakehurst.wiremock.common.Urls;
import com.github.tomakehurst.wiremock.http.*;
import com.github.tomakehurst.wiremock.servlet.WireMockHttpServletMultipartAdapter;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.eclipse.jetty.server.MultiPartInputStreamParser;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class MockRequest implements Request {

    private String scheme = "http";
    private String host = "my.domain";
    private int port = 80;
    private String url = "/";

    private String absoluteUrl = null;
    private RequestMethod method = RequestMethod.ANY;
    private HttpHeaders headers = new HttpHeaders();

    private final Map<String, Cookie> cookies = new HashMap<>();
    private byte[] body;
    private String clientIp = "1.1.1.1";
    private Collection<Part> multiparts = null;

    private Map<String, FormParameter> formParameters = new HashMap<>();
    private boolean isBrowserProxyRequest = false;
    private String protocol = "HTTP/1.1";

    public static MockRequest mockRequest() {
        return new MockRequest();
    }

    public MockRequest scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public MockRequest host(String host) {
        this.host = host;
        return this;
    }

    public MockRequest port(int port) {
        this.port = port;
        return this;
    }

    public MockRequest url(String url) {
        this.url = url;
        return this;
    }

    public MockRequest absoluteUrl(String absoluteUrl) {
        this.absoluteUrl = absoluteUrl;
        return this;
    }

    public MockRequest method(RequestMethod method) {
        this.method = method;
        return this;
    }

    public MockRequest header(String key, String... values) {
        headers = headers.plus(httpHeader(key, values));
        return this;
    }

    public MockRequest headers(HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    public MockRequest cookie(String key, String... values) {
        cookies.put(key, new Cookie(asList(values)));
        return this;
    }

    public MockRequest body(String body) {
        this.body = body.getBytes(UTF_8);
        return this;
    }

    public MockRequest body(byte[] body) {
        this.body = body;
        return this;
    }

    public MockRequest clientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public MockRequest parts(Collection<Part> multiparts) {
        this.multiparts = multiparts;
        return this;
    }

    public MockRequest part(MockMultipart part) {
        if (multiparts == null) {
            multiparts = new ArrayList<>();
        }

        multiparts.add(part);
        return this;
    }

    public MockRequest formParameters(Map<String, FormParameter> formParameters) {
        if (formParameters != null) {
            this.formParameters = formParameters;
        }
        return this;
    }

    public MockRequest isBrowserProxyRequest(boolean isBrowserProxyRequest) {
        this.isBrowserProxyRequest = isBrowserProxyRequest;
        return this;
    }

    public MockRequest protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getAbsoluteUrl() {
        String portPart = port == 80 || port == 443 ? "" : ":" + port;
        return getFirstNonNull(absoluteUrl, String.format("%s://%s%s%s", scheme, host, portPart, url));
    }

    @Override
    public RequestMethod getMethod() {
        return method;
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getClientIp() {
        return clientIp;
    }

    @Override
    public String getHeader(String key) {
        return header(key).firstValue();
    }

    @Override
    public HttpHeader header(final String key) {
        return headers.all().stream()
            .filter(input -> input.keyEquals(key))
            .findFirst()
            .orElseGet(() -> HttpHeader.absent(key));
    }

    @Override
    public ContentTypeHeader contentTypeHeader() {
        return ContentTypeHeader.absent();
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

    @Override
    public boolean containsHeader(String key) {
        return headers.getHeader(key).isPresent();
    }

    @Override
    public Set<String> getAllHeaderKeys() {
        return getHeaders().keys();
    }

    @Override
    public Map<String, Cookie> getCookies() {
        return cookies;
    }

    @Override
    public QueryParameter queryParameter(String key) {
        Map<String, QueryParameter> queryParams = Urls.splitQuery(URI.create(url));
        return queryParams.get(key);
    }

    @Override
    public FormParameter formParameter(String key) {
        return getFirstNonNull(formParameters.get(key), FormParameter.absent(key));
    }

    @Override
    public Map<String, FormParameter> formParameters() {
        return formParameters;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public String getBodyAsString() {
        return body != null ? new String(body) : null;
    }

    @Override
    public String getBodyAsBase64() {
        return "";
    }

    @Override
    public boolean isBrowserProxyRequest() {
        return isBrowserProxyRequest;
    }

    @Override
    public Optional<Request> getOriginalRequest() {
        return Optional.empty();
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    public LoggedRequest asLoggedRequest() {
        return LoggedRequest.createFrom(this);
    }

    @Override
    public boolean isMultipart() {
        return getParts() != null;
    }

    @Override
    public Collection<Part> getParts() {
        return multiparts;
    }

    @Override
    public Part getPart(final String name) {
        return (getParts() != null && name != null)
            ? multiparts.stream().filter(input -> name.equals(input.getName())).findFirst().orElse(null)
            : null;
    }

    public MockRequest multipartBody(String body) {
        ContentTypeHeader contentTypeHeader = headers.getContentTypeHeader();
        String contentType =
            contentTypeHeader.isPresent()
                ? contentTypeHeader.firstValue()
                : "multipart/form-data; boundary=BOUNDARY";
        this.multiparts = MultipartParser.parse(bytesFromString(body), contentType);

        return this;
    }

    public static class MockMultipart implements Request.Part {

        private String name;
        private List<HttpHeader> headers = new ArrayList<>();
        private Body body;

        public static MockMultipart mockPart() {
            return new MockMultipart();
        }

        public MockMultipart name(String name) {
            this.name = name;
            return this;
        }

        public MockMultipart headers(List<HttpHeader> headers) {
            this.headers = headers;
            return this;
        }

        public MockMultipart header(String key, String... values) {
            headers.add(new HttpHeader(key, values));
            return this;
        }

        public MockMultipart body(String body) {
            this.body = new Body(body);
            return this;
        }

        public MockMultipart body(byte[] body) {
            this.body = new Body(body);
            return this;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public HttpHeader getHeader(String key) {
            return getHeaders().getHeader(key);
        }

        @Override
        public HttpHeaders getHeaders() {
            return new HttpHeaders(headers);
        }

        @Override
        public Body getBody() {
            return body;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MockMultipart that = (MockMultipart) o;
            return Objects.equals(name, that.name)
                && Objects.equals(headers, that.headers)
                && Objects.equals(body, that.body);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, headers, body);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MockMultipart{");
            sb.append("name='").append(name).append('\'');
            sb.append(", headers=").append(headers);
            sb.append(", body=").append(body);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class MultipartParser {

        @SuppressWarnings("unchecked")
        public static Collection<Request.Part> parse(byte[] body, String contentType) {
            MultiPartInputStreamParser parser =
                new MultiPartInputStreamParser(new ByteArrayInputStream(body), contentType, null, null);
            try {
                return parser.getParts().stream()
                    .map(WireMockHttpServletMultipartAdapter::from)
                    .collect(Collectors.toList());
            } catch (Exception e) {
                return throwUnchecked(e, Collection.class);
            }
        }
    }
}
