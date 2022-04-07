package org.zaghloul.zagapi.core.http.request;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RequestData {

    public Object body = null;
    public Map<String, String> headers = new HashMap<>();
    public Map<String, String> queryParams = new HashMap<>();

    public void clearAll() {
        headers = new HashMap<>();
        queryParams = new HashMap<>();
        body = null;
    }
}
