package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Cookie;
import org.example.model.Header;
import org.example.model.InverterData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class InverterGuard {

    private String sessionId;
    private InverterGuardHttpClient httpClient;

    public InverterGuard() {
        this.sessionId = "";
        this.httpClient = new InverterGuardHttpClient();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public void login() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> loginResponse = this.httpClient.buildRequest(
                "https://af.solarcloudsystem.com/WManage/web/login",
                Map.of("account", "squareHome", "password", "Wilderness4"),
                List.of(new Header("Content-type", "application/x-www-form-urlencoded"))
        );


        System.out.println(loginResponse.headers());

        HttpHeaders headers = loginResponse.headers();
        Map<String, List<String>> map = headers.map();

        String cookies = map.get("set-cookie").get(0);
        String sessionId = cookies.substring(cookies.indexOf("=") + 1, cookies.indexOf(";"));
        System.out.println("set-cookie.sessionId: " + sessionId);
        setSessionId(sessionId);
    }

    public void getInverterData() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> inverterDataResponse = this.httpClient.buildRequest(
                "https://af.solarcloudsystem.com/WManage/api/inverter/getInverterRuntime?",
                Map.of("serialNum", "2413053854"),
                List.of(new Header("Content-type", "application/x-www-form-urlencoded")),
                new Cookie("JSESSIONID", this.sessionId)
        );

//            HttpResponse<String> inverterDataResponse = getInverterData(sessionId);

        System.out.println("inverter data: " + inverterDataResponse);
        System.out.println("inverter data.headers: " + inverterDataResponse.headers());
        System.out.println("inverter data.body: " + inverterDataResponse.body());
        System.out.println("inverter data.request: " + inverterDataResponse.request());

        InverterData inverterData = new ObjectMapper().convertValue(inverterDataResponse.body(), InverterData.class);

        System.out.println(inverterData);

//        return inverterDataResponse.body();
    }


    @Override
    public String toString() {
        return "InverterGuard{" +
                "sessionId='" + sessionId + '\'' +
                '}';
    }
}
