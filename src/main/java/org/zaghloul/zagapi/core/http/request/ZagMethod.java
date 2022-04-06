package org.zaghloul.zagapi.core.http.request;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import org.zaghloul.zagapi.Exception.ZagAPIException;

@Data
public class ZagMethod {
    public String endPoint = null;
    public HttpMethod method;
    public RequestData data;

    public ZagMethod() {
        this.data = new RequestData();
    }

    public synchronized Response getResponse() {
        try {
            if (method == null)
                throw new Exception("HttpMethod is not specified");
            if (endPoint == null || endPoint.isEmpty())
                throw new Exception("endPoint is not specified");

            //TODO: fix query params if in endpoint/pat
            RequestSpecification requestSpec = getRequestSpec(data);
            data.clearAll();
            return requestSpec.request(Method.valueOf(this.method.name()));
        } catch (Exception e) {
            throw new ZagAPIException(String.format("Something went wrong/failed on sending the request, %s",e.getMessage()));
        }
    }

    private RequestSpecification getRequestSpec(RequestData data) {
        RequestSpecification spec = RestAssured.given();
        if (data == null)
            return spec;
        //TODO: fix base
        spec.baseUri("https://reqres.in/");
        //TODO: fix content-type
        spec.contentType(ContentType.JSON);

        if (endPoint != null)
            spec.basePath(endPoint);
        if (!data.queryParams.isEmpty())
            spec.queryParams(data.queryParams);
        if (!data.headers.isEmpty())
            spec.headers(data.headers);
        if ((data.body != null))
            spec.body(data.body);

        return spec;
    }
}
