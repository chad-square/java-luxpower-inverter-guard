package org.example;

import org.example.model.Cookie;
import org.example.model.Header;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InverterGuardHttpClient {

    public static final String AN_ERROR_OCCURRED_PARSING_URI = "An error occurred parsing uri";
    public static final String AN_ERROR_OCCURRED_SENDING_REQUEST = "An error occurred sending request";
    private final HttpClient.Builder httpClientBuilder;

    public InverterGuardHttpClient() {
        this.httpClientBuilder = HttpClient.newBuilder();
    }

    public HttpResponse<String> buildRequest(String uri, Map<String, Object> body, List<Header> headers) throws URISyntaxException, IOException, InterruptedException {

        try {
            HttpRequest.Builder requestBuilder = createAndSetBuilder(uri, headers);

            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(formEncodeBody(body)));

            CookieHandler.setDefault(new CookieManager());

            return httpClientBuilder
                    .cookieHandler(CookieHandler.getDefault())
                    .build()
                    .send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        } catch (URISyntaxException uriException) {
            System.out.println(AN_ERROR_OCCURRED_PARSING_URI);
            uriException.printStackTrace();
            throw uriException;
        } catch (java.io.IOException | InterruptedException sendException) {
            System.out.println(AN_ERROR_OCCURRED_SENDING_REQUEST);
            sendException.printStackTrace();
            throw sendException;
        }
    }

    public HttpResponse<String> buildRequest(String uri, Map<String, Object> body, List<Header> headers, Cookie cookie) throws URISyntaxException, IOException, InterruptedException {

        try {
            HttpRequest.Builder requestBuilder = createAndSetBuilder(uri, headers);

            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(formEncodeBody(body)));

            CookieHandler.setDefault(new CookieManager());
            HttpCookie jSessionCookie = new HttpCookie(cookie.name(), cookie.value());
            jSessionCookie.setPath("/");
            jSessionCookie.setVersion(0);

            ((CookieManager) CookieHandler.getDefault()).getCookieStore().add(new URI(uri), jSessionCookie);

            return this.httpClientBuilder
                    .cookieHandler(CookieHandler.getDefault())
                    .build()
                    .send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        } catch (URISyntaxException uriException) {
            System.out.println(AN_ERROR_OCCURRED_PARSING_URI);
            uriException.printStackTrace();
            throw uriException;
        } catch (java.io.IOException | InterruptedException sendException) {
            System.out.println(AN_ERROR_OCCURRED_SENDING_REQUEST);
            sendException.printStackTrace();
            throw sendException;
        }
    }

    public void buildRequest(String uri, List<Header> headers) throws URISyntaxException, IOException, InterruptedException {

        try {
            HttpRequest.Builder requestBuilder = createAndSetBuilder(uri, headers);

            requestBuilder.POST(HttpRequest.BodyPublishers.noBody());

            httpClientBuilder
                    .build()
                    .send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        } catch (URISyntaxException uriException) {
            System.out.println(AN_ERROR_OCCURRED_PARSING_URI);
            uriException.printStackTrace();
            throw uriException;
        } catch (java.io.IOException | InterruptedException sendException) {
            System.out.println(AN_ERROR_OCCURRED_SENDING_REQUEST);
            sendException.printStackTrace();
            throw sendException;
        }
    }

    private static HttpRequest.Builder createAndSetBuilder(String uri, List<Header> headers) throws URISyntaxException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        requestBuilder.uri(new URI(uri));
        headers.forEach(header -> requestBuilder.header(header.name(), header.value()));
        return requestBuilder;
    }

    private String formEncodeBody(Map<String, Object> parameters) {
        return parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }
}
