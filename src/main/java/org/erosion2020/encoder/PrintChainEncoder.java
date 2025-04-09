package org.erosion2020.encoder;

import org.erosion2020.Serializer;

import static java.lang.System.out;

@Encoder(type="print")
public class PrintChainEncoder extends ChainEncoder {

    PrintChainEncoder(String[] config) {
        super(config);
    }

    @Override
    public Boolean serialize(Object chain) throws Exception {
        Serializer.serialize(chain, out);
        return true;
    }
}
