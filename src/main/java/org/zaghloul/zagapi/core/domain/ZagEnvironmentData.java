package org.zaghloul.zagapi.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZagEnvironmentData {
    private final String environment;
    private final String baseURL;
    private final JsonNode data;

    @JsonCreator
    public ZagEnvironmentData(
            @JsonProperty("environment") String environment,
            @JsonProperty("baseURL") String baseURL,
            @JsonProperty("data") JsonNode data) {
        this.environment = environment;
        this.baseURL = baseURL;
        this.data = data;
    }
}
