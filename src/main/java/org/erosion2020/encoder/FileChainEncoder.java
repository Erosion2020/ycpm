package org.erosion2020.encoder;

import org.erosion2020.Serializer;

import java.io.FileOutputStream;

@Encoder(type = "file")
public class FileChainEncoder extends ChainEncoder {

    FileChainEncoder(String[] config) {
        super(config);
    }

    @Override
    public Boolean serialize(Object chain) throws Exception {
        try(FileOutputStream fos = new FileOutputStream(this.getConfig()[0])){
            Serializer.serialize(chain, fos);
        }
        return true;
    }
}
