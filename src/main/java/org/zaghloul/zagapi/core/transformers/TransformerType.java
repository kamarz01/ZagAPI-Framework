package org.zaghloul.zagapi.core.transformers;

public abstract class TransformerType {
    @Override
    public String toString() {
        return key();
    }

    abstract String key();

    abstract String transform(String params, BaseTransformer bt);
}
