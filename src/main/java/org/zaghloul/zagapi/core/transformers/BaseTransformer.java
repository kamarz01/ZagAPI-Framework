package org.zaghloul.zagapi.core.transformers;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class BaseTransformer {
    private static final Pattern TRANSFORM_PATTERN = Pattern.compile("\\{\\{(.*?)\\:\\:(.*?)(\\:\\:(.*?))?}}");
    private final List<TransformerType> transformers = new ArrayList<>();
    private Map<String, String> env = new HashMap<>();


    public void addTransformer(TransformerType tr) {
        transformers.add(tr);
    }

    public TransformerType getTransformer(String name) {
        return transformers
                .stream()
                .filter(transform -> transform.key().matches(name))
                .findFirst()
                .orElse(null);
    }

    public void addAllToEnv(Map<String,String> envData) {
        env.putAll(envData);
    }

    public String getFromEnv(String key){
        return env.get(key);
    }

    public boolean hasToken(String input) {
        return TRANSFORM_PATTERN.matcher(input).find();
    }

    public String transform(String input) {

        Matcher matcher = TRANSFORM_PATTERN.matcher(input);
        String ret = input;

        while (matcher.find()) {

            String orig = matcher.group(0);
            String id = matcher.group(1);
            String params = matcher.group(2);

            try {
                TransformerType transform = getTransformer(id);
                if (transform == null) {
                    throw new RuntimeException("Transformer type : " + id + " is not valid/found");
                }
                String val = transform.transform(params, this);
                if (val != null) {
                    ret = ret.replace(orig, val);
                }

            } catch (Exception e) {
                log.error("Error while trying to replace {}", matcher.group(0), e);
                throw e;
            }
        }
        return ret;
    }


}
