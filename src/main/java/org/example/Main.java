package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Cookie;
import org.example.model.Header;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //  login
//            String sessionId = login();
//            InverterGuardHttpClient inverterGuardHttpClient = new InverterGuardHttpClient();
//            HttpResponse<String> loginResponse = inverterGuardHttpClient.buildRequest(
//                    "https://af.solarcloudsystem.com/WManage/web/login",
//                    Map.of("account", "squareHome", "password", "Wilderness4"),
//                    List.of(new Header("Content-type", "application/x-www-form-urlencoded"))
//            );

            InverterGuard inverterGuard = new InverterGuard();

            inverterGuard.login();
//            System.out.println(loginResponse.headers());
//
//            HttpHeaders headers = loginResponse.headers();
//            Map<String, List<String>> map = headers.map();
//
//            String cookies = map.get("set-cookie").get(0);
//            String sessionId = cookies.substring(cookies.indexOf("=") + 1, cookies.indexOf(";"));
//            System.out.println("set-cookie.sessionId: " + sessionId);
            System.out.println("sessionId: " + inverterGuard.getSessionId());

            System.out.println("\n=====================================================================\n");

            // getInverterData
//            HttpResponse<String> inverterDataResponse = inverterGuardHttpClient.buildRequest(
//                    "https://af.solarcloudsystem.com/WManage/api/inverter/getInverterRuntime?",
//                    Map.of("serialNum", "2413053854"),
//                    List.of(new Header("Content-type", "application/x-www-form-urlencoded")),
//                    new Cookie("JSESSIONID", sessionId)
//            );

//            HttpResponse<String> inverterDataResponse = getInverterData(sessionId);

//            System.out.println("inverter data: " + inverterDataResponse);
//            System.out.println("inverter data.headers: " + inverterDataResponse.headers());
//            System.out.println("inverter data.body: " + inverterDataResponse.body());
//            System.out.println("inverter data.request: " + inverterDataResponse.request());

            inverterGuard.getInverterData();
        } catch (Exception e) {
            System.out.println("a problem happened logging in");
            throw new RuntimeException(e);
        }

//        try {
//            buildRequest
//        }

//


    }

    private static String login() throws IOException, URISyntaxException, InterruptedException {
        Map<String, String> details = Map.of("account", "squareHome", "password", "Wilderness4");

        try {

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://af.solarcloudsystem.com/WManage/web/login"))
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .POST(getParamsUrlEncoded(details))
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> send = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println(send);
            System.out.println(send.headers());

            HttpHeaders headers = send.headers();
            Map<String, List<String>> map = headers.map();

            String cookies = map.get("set-cookie").get(0);
            String sessionId = cookies.substring(cookies.indexOf("=") + 1, cookies.indexOf(";"));
            System.out.println("set-cookie.sessionId: " + sessionId);

            return sessionId;


        } catch (JsonProcessingException serialException) {
            System.out.println("error cannot serialize payload, " + serialException);
            throw serialException;
        } catch (URISyntaxException | IOException | InterruptedException requestException) {
            System.out.println("error occurred logging in, " + requestException);
            throw requestException;
        }
    }

    private static HttpResponse<String> getInverterData(String sessionId) throws IOException, URISyntaxException, InterruptedException {
        Map<String, String> details = Map.of("serialNum", "2413053854");

        try {

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://af.solarcloudsystem.com/WManage/api/inverter/getInverterRuntime?"))
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .POST(getParamsUrlEncoded(details))
                    .build();

//            CookieManager cookieManager = new CookieManager("JSESSIONID", sessionId);
            CookieHandler.setDefault(new CookieManager());
            HttpCookie jseesionCookie = new HttpCookie("JSESSIONID", sessionId);
            jseesionCookie.setPath("/");
            jseesionCookie.setVersion(0);

            ((CookieManager) CookieHandler.getDefault()).getCookieStore().add(new URI("https://af.solarcloudsystem.com/WManage/api/inverter/getInverterRuntime?"),
                    jseesionCookie);
            HttpClient httpClient = HttpClient.newBuilder().cookieHandler(CookieHandler.getDefault()).build();
            httpClient.cookieHandler();

            return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        } catch (JsonProcessingException serialException) {
            System.out.println("error cannot serialize payload, " + serialException);
            throw serialException;
        } catch (URISyntaxException | IOException | InterruptedException requestException) {
            System.out.println("error occurred logging in, " + requestException);
            throw requestException;
        }
    }

    private static HttpRequest.BodyPublisher getParamsUrlEncoded(Map<String, String> parameters) throws JsonProcessingException {
        String urlEncoded = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        System.out.println(urlEncoded);
        return HttpRequest.BodyPublishers.ofString(urlEncoded);
    }
}