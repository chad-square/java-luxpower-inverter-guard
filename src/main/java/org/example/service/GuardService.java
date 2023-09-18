package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.InverterGuardHttpClient;
import org.example.model.Cookie;
import org.example.model.GuardProperties;
import org.example.model.Header;
import org.example.model.InverterData;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class GuardService {

    public static final String CONTENT_TYPE = "Content-type";
    public static final String FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String SET_COOKIE = "set-cookie";
    public static final String EQUALS_STR = "=";
    public static final String SEMI_COLON_STR = ";";
    public static final String JSESSIONID = "JSESSIONID";
    public static final String AUTHORIZATION = "Authorization";
    public static final String INVERTER_SN = "inverterSn";
    public static final String SERIAL_NUM = "serialNum";
    public static final String INDEX = "index";
    private String sessionId;
    private final InverterGuardHttpClient httpClient;
    private GuardProperties guardProperties;

    public GuardService() {
        this.sessionId = "";
        this.httpClient = new InverterGuardHttpClient();
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String appConfigPath = rootPath + "application.properties";

        try{

            Properties appProps = new Properties();
            appProps.load(new FileInputStream(appConfigPath));
            this.guardProperties = new ObjectMapper().convertValue(appProps, GuardProperties.class);
            System.out.println(this.guardProperties);

        } catch (Exception e) {
            System.out.println("something wrong getting props");
            e.printStackTrace();
        }

    }

    public void runGuard() {

        if (getSessionId().isEmpty()) {
            System.out.println("logging in...");
            this.login();
        }

        try {

            refreshInverterData();

            InverterData inverterData = getInverterData();
            System.out.println(inverterData);

            // using backup power aka batteries
            if (inverterData.backupPowerUsage() > 0) {
                System.out.println("Using backup power, turning off heater");
                System.out.println("inverterData.backupPowerUsage: " + inverterData.backupPowerUsage());
                turnOffHeater();
            }

            // if current solar yield is less than household usage
            if (inverterData.solar() <= inverterData.normalPowerUsage() && inverterData.grid() == 0) {
                System.out.println("Solar yield is too low, turning off heater");
                System.out.printf("inverterData.solar: %s,inverterData.normalPowerUsage: %s,inverterData.grid: %s \n",
                        inverterData.solar(), inverterData.normalPowerUsage(), inverterData.grid());
                turnOffHeater();
            }

            System.out.println("\n===================================================================================================================\n");

        } catch (Exception exception) {
            // if anything goes wrong, it is most likely because the token (sessionId) expired
            // so just login again to refresh the session and start from the beginning
            System.out.println("something went wrong, retrying...");
            System.out.println(exception.getMessage());
            this.login();
            runGuard();
        }

    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public void login() {

        try {
            HttpResponse<String> loginResponse = this.httpClient.buildRequest(
                    this.guardProperties.luxpowerBaseUrl() + this.guardProperties.luxpowerLoginPath(),
                    Map.of(ACCOUNT, this.guardProperties.luxpowerUsername(), PASSWORD, this.guardProperties.luxpowerPassword()),
                    List.of(new Header(CONTENT_TYPE, FORM_URLENCODED))
            );

            HttpHeaders headers = loginResponse.headers();
            Map<String, List<String>> map = headers.map();

            String cookies = map.get(SET_COOKIE).get(0);
            String sessionId = cookies.substring(cookies.indexOf(EQUALS_STR) + 1, cookies.indexOf(SEMI_COLON_STR));

            setSessionId(sessionId);

            System.out.println("\n===================================================================================================================\n");


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while logging in, " + e);
        }
    }

    public void refreshInverterData() throws URISyntaxException, IOException, InterruptedException {
        System.out.println("refreshing inverter data...");
        String uri = this.guardProperties.luxpowerBaseUrl() + this.guardProperties.luxpowerRefreshInverterDataPath();

        this.httpClient.buildRequest(
                uri,
                Map.of(INVERTER_SN, this.guardProperties.inverterSerialNumber(), INDEX, 1),
                List.of(new Header(CONTENT_TYPE, FORM_URLENCODED)),
                new Cookie(JSESSIONID, this.sessionId)
        );

        this.httpClient.buildRequest(
                uri,
                Map.of(INVERTER_SN, this.guardProperties.inverterSerialNumber(), INDEX, 2),
                List.of(new Header(CONTENT_TYPE, FORM_URLENCODED)),
                new Cookie(JSESSIONID, this.sessionId)
        );

        this.httpClient.buildRequest(
                uri,
                Map.of(INVERTER_SN, this.guardProperties.inverterSerialNumber(), INDEX, 3),
                List.of(new Header(CONTENT_TYPE, FORM_URLENCODED)),
                new Cookie(JSESSIONID, this.sessionId)
        );
    }

    public InverterData getInverterData() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> inverterDataResponse = this.httpClient.buildRequest(
                this.guardProperties.luxpowerBaseUrl() + this.guardProperties.luxpowerGetInverterDataPath(),
                Map.of(SERIAL_NUM, this.guardProperties.inverterSerialNumber()),
                List.of(new Header(CONTENT_TYPE, FORM_URLENCODED)),
                new Cookie(JSESSIONID, this.sessionId)
        );

        return new ObjectMapper().readValue(inverterDataResponse.body(), InverterData.class);
    }

    public void turnOffHeater() throws URISyntaxException, IOException, InterruptedException {
        this.httpClient.buildRequest(
                this.guardProperties.homeAssistantBaseUrl() + this.guardProperties.heaterTurnOffEndpoint(),
                List.of(
                        new Header(AUTHORIZATION, this.guardProperties.homeAssistantAccessToken())
                )
        );

    }


    @Override
    public String toString() {
        return "InverterGuard{" +
                "sessionId='" + sessionId + '\'' +
                '}';
    }
}
