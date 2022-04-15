package org.zaghloul.zagapi.core.transformers;


public class Transformer extends BaseTransformer{
    public Transformer() {
        addTransformer(new EnvironmentTransformer());
    }
}
