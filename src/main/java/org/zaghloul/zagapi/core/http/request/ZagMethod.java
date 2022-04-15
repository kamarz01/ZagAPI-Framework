package org.zaghloul.zagapi.core.http.request;

import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.zaghloul.zagapi.core.http.proxy.Proxy;
import org.zaghloul.zagapi.core.transformers.Transformer;
import org.zaghloul.zagapi.exception.ZagAPIException;

@Data
@Slf4j
public class ZagMethod {
    private String endPoint = null;
    private HttpMethod method;
    private RequestData data;
    private Proxy proxyInfo;
    private Transformer transformer;

    public ZagMethod(Transformer transformer) {
        this.data = new RequestData();
        this.transformer = transformer;
    }

    public synchronized Response getResponse() {
        try {
            if (method == null)
                throw new Exception("HttpMethod is not specified");
            if (endPoint == null || endPoint.isEmpty())
                throw new Exception("endPoint is not specified");

            //TODO: fix query params if in endpoint/pat
            RequestSpecification requestSpec = getRequestSpec(getData());
            log.info("Sending '{}' request on '{}', '{}', '{}'",method,endPoint,data,proxyInfo);
            data.clearAll();
            return requestSpec.request(Method.valueOf(this.method.name()));
        } catch (Exception e) {
            throw new ZagAPIException(String.format("Something went wrong/failed on sending the request, %s",e.getMessage()));
        }
    }

    private RequestSpecification getRequestSpec(RequestData data) {
        RequestSpecification spec = RestAssured.given().config(RestAssured.config().sslConfig( new SSLConfig().relaxedHTTPSValidation()));
        if (data == null)
            return spec;
        spec.baseUri(transformer.getFromEnv("baseURL"));
        //TODO: fix content-type
        spec.contentType(ContentType.JSON);

        if (endPoint != null)
            spec.basePath(endPoint);
        if (!data.formParams.isEmpty())
            spec.formParams(data.formParams);
        if (!data.queryParams.isEmpty())
            spec.queryParams(data.queryParams);
        if (!data.headers.isEmpty())
            spec.headers(data.headers);
        if ((data.body != null))
            spec.body(data.body);
        if (getProxyInfo() != null && getProxyInfo().isUseProxy()) {
            ProxySpecification proxySpecification = ProxySpecification.host(getProxyInfo().getHost()).and().withPort(Integer.parseInt(getProxyInfo().getPort()));
            if (getProxyInfo().getUsername() != null && getProxyInfo().getPassword() != null)
                proxySpecification = proxySpecification.and().withAuth(getProxyInfo().getUsername(), getProxyInfo().getPassword());
            spec.proxy(proxySpecification);
        }
        return spec;
    }

}
