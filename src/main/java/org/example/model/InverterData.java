package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public record InverterData(@JsonProperty("soc") int batteryCharge, @JsonProperty("pToUser") int grid,
                           @JsonProperty("ppv") int solar, @JsonProperty("peps") int backupPower) {
}
