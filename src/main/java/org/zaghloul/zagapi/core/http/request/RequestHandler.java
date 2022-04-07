package org.zaghloul.zagapi.core.http.request;

import com.fasterxml.jackson.databind.JsonNode;
import org.zaghloul.zagapi.constant.RequestConstant;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

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

    public void handleQueryParametersInURI(String path, ZagMethod method) {
        if (path != null && path.contains("?")) {
            String queryString = substringAfter(path, "?");
            if (!queryString.isEmpty()) {
                String[] queryParams = queryString.split("&");
                for (String queryParam : queryParams) {
                    String key = substringBefore(queryParam, "=");
                    String value = substringAfter(queryParam, "=");
                    if (!method.data.queryParams.containsKey(key)) {
                        method.data.queryParams.put(key,value);
                    }
                }
            }
        }
    }


}
