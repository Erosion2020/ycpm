package org.erosion2020.encoder;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import org.erosion2020.Serializer;

@Encoder(type = "BCEL")
public class BCELChainEncoder extends ChainEncoder {

    BCELChainEncoder(String[] config) {
        super(config);
    }

    @Override
    Object serialize(Object chain) throws Exception {
        if(!(chain instanceof byte[])) {
            chain = Serializer.serialize(chain);
        }
        return "$$BCEL$$" + Utility.encode((byte[]) chain, true);//转换为字节码并编码为bcel字节码
    }
}