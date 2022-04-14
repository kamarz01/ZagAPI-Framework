package org.zaghloul.zagapi.core.http.proxy;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Proxy {
    private boolean useProxy;
    private String host;
    private String port;
    private String username;
    private String password;
}
