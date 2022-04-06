package org.zaghloul.zagapi.core.http.request;

import com.fasterxml.jackson.databind.JsonNode;
import org.zaghloul.zagapi.Constant.RequestConstant;

public class RequestHandler {

    public void handleQueryParameters(JsonNode jsonObject, ZagMethod method) {
        JsonNode queryParams = jsonObject.get(RequestConstant.QUERY_PARAMETERS);
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.fieldNames().forEachRemaining(query -> method.data.queryParams.put(query, queryParams.get(query).asText()));
        }
    }

    public void handleHeaders(JsonNode jsonObject, ZagMethod method) {
        JsonNode headers = jsonObject.get(RequestConstant.HEADERS);
        if (headers != null && !headers.isEmpty()) {
            headers.fieldNames().forEachRemaining(header -> method.data.headers.put(header, headers.get(header).asText()));
        }
    }

}
