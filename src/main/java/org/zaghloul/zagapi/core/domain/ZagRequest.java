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
public class ZagRequest {
    private final String id;
    private final String endPoint;
    private final String method;
    private final JsonNode requestData;

    @JsonCreator
    public ZagRequest(
            @JsonProperty("id") String id,
            @JsonProperty("endPoint") String endPoint,
            @JsonProperty("method") String method,
            @JsonProperty("requestData") JsonNode requestData) {
        this.id = id;
        this.endPoint = endPoint;
        this.method = method;
        this.requestData = requestData;
    }
}
