package org.zaghloul.zagapi.core.transformers;

public class EnvironmentTransformer extends TransformerType{
    @Override
    String key() {
        return "ENV";
    }

    @Override
    String transform(String params, BaseTransformer bt) {
        return bt.getFromEnv(params);
    }
}
