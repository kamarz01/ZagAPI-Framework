package org.zaghloul.zagapi.core.http.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.zaghloul.zagapi.core.domain.ZagProxy;
import org.zaghloul.zagapi.core.domain.ZagRestSpec;
import org.zaghloul.zagapi.core.http.request.ZagMethod;
import org.zaghloul.zagapi.core.transformers.Transformer;

import java.util.Objects;

public class ProxyHandler {

    public void handleRequestProxy(ZagRestSpec spec, ZagMethod method){
        if (!Objects.isNull(spec.getProxy())){
            ZagProxy apiProxy = spec.getProxy();
            Proxy proxy = Proxy.builder()
                    .host(apiProxy.getHost())
                    .port(apiProxy.getPort())
                    .username(apiProxy.getUsername())
                    .password(apiProxy.getPassword())
                    .useProxy(apiProxy.isUseProxy())
                    .build();
            method.setProxyInfo(proxy);
        }
    }

}
