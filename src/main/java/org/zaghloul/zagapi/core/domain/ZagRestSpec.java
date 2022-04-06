package org.zaghloul.zagapi.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZagRestSpec {
    private final String name;
    private final String description;
    private final ZagProxy proxy;
    private final List<ZagRequest> requests;

    @JsonCreator
    public ZagRestSpec(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("proxy") ZagProxy proxy,
            @JsonProperty("requests") List<ZagRequest> requests) {
        this.name = name;
        this.description = description;
        this.proxy = proxy;
        this.requests = requests;
    }
}
