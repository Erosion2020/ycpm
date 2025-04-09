package org.erosion2020.encoder;

import org.erosion2020.Serializer;

import java.util.Base64;

@Encoder(type = "Base64")
public class Base64ChainEncoder extends ChainEncoder {

    Base64ChainEncoder(String[] config) {
        super(config);
    }

    @Override
    public Object serialize(Object chain) throws Exception {
        byte[] result = null;
        if(chain instanceof String) {
            chain = chain.toString().getBytes();
        }else {
            chain = Serializer.serialize(chain);
        }
        Base64.Encoder encoder = Base64.getEncoder();
        result = encoder.encode((byte[]) chain);
        return new String(result);
    }
}
