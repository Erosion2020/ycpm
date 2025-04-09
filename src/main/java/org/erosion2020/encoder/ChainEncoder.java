package org.erosion2020.encoder;

public abstract class ChainEncoder {
    private String[] config;

    ChainEncoder(String[] config){
        this.config = config;
    }

    public String[] getConfig() {
        return config;
    }

    abstract Object serialize(Object chain) throws Exception;
}
