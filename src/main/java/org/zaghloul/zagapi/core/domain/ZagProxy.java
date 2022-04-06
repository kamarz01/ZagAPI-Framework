package org.zaghloul.zagapi.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ZagProxy {
    private final boolean useProxy;
    private final String host;
    private final String port;
    private final String username;
    private final String password;

    @JsonCreator
    public ZagProxy(
            @JsonProperty("useProxy") boolean useProxy,
            @JsonProperty("host") String host,
            @JsonProperty("port") String port,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password) {
        this.useProxy = useProxy;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}
