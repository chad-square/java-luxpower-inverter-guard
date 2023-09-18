package org.example.model;

public record GuardProperties(String luxpowerBaseUrl, String luxpowerLoginPath, String luxpowerRefreshInverterDataPath,
                              String luxpowerGetInverterDataPath, String inverterSerialNumber, String luxpowerUsername,
                              String luxpowerPassword, String homeAssistantBaseUrl, String homeAssistantAccessToken,
                              String heaterTurnOffEndpoint) { }
