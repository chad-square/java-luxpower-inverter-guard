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

    private final HttpClient.Builder httpClientBuilder;

    public InverterGuardHttpClient() {
        this.httpClientBuilder = HttpClient.newBuilder();
    }

    public HttpResponse<String> buildRequest(String uri, Map<String, String> body, List<Header> headers) throws URISyntaxException, IOException, InterruptedException {

        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

            requestBuilder.uri(new URI(uri));
            headers.forEach(header -> requestBuilder.header(header.name(), header.value()));
            requestBuilder.POST(getParamsUrlEncoded(body));

            return httpClientBuilder
                    .build()
                    .send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        } catch (URISyntaxException uriException) {
            System.out.println("An error occurred parsing uri, " + uriException);
            throw uriException;
        } catch (java.io.IOException | InterruptedException sendException) {
            System.out.println("An error occurred sending request, " + sendException);
            throw sendException;
        }
    }

    public HttpResponse<String> buildRequest(String uri, Map<String, String> body, List<Header> headers, Cookie cookie) throws URISyntaxException, IOException, InterruptedException {

        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

            requestBuilder.uri(new URI(uri));
            headers.forEach(header -> requestBuilder.header(header.name(), header.value()));
            requestBuilder.POST(getParamsUrlEncoded(body));

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
            System.out.println("An error occurred parsing uri, " + uriException);
            throw uriException;
        } catch (java.io.IOException | InterruptedException sendException) {
            System.out.println("An error occurred sending request, " + sendException);
            throw sendException;
        }
    }

    private HttpRequest.BodyPublisher getParamsUrlEncoded(Map<String, String> parameters) {
        String urlEncoded = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        System.out.println(urlEncoded);
        return HttpRequest.BodyPublishers.ofString(urlEncoded);
    }
}
