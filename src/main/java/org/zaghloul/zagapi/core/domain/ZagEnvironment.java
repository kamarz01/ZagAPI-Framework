package org.zaghloul.zagapi.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZagEnvironment {
    private final List<ZagEnvironmentData> environments;

    @JsonCreator
    public ZagEnvironment(List<ZagEnvironmentData> environments) {
        this.environments = environments;
    }
}
