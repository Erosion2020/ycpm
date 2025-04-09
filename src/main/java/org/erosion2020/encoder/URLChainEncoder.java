package org.erosion2020.encoder;

import java.util.HashMap;
import java.util.Map;

@Encoder(type = "url")
public class URLChainEncoder extends ChainEncoder {
    // 要进行编码的关键字符列表
    private static final Map<Character, String> ENCODE_MAP = new HashMap<>();

    static {
        ENCODE_MAP.put(' ', "%20");
        ENCODE_MAP.put('?', "%3F");
        ENCODE_MAP.put('=', "%3D");
        ENCODE_MAP.put('&', "%26");
        ENCODE_MAP.put('#', "%23");
        ENCODE_MAP.put('%', "%25");
        ENCODE_MAP.put('+', "%2B");
        ENCODE_MAP.put('/', "%2F");
        ENCODE_MAP.put(':', "%3A");
    }

    URLChainEncoder(String[] config) {
        super(config);
    }

    @Override
    Object serialize(Object chain) throws Exception {
        if(!(chain instanceof String)){
            throw new Exception("[ERROR] The URLChainEncoder requires a string.");
        }
        StringBuilder encoded = new StringBuilder();
        String input = (String) chain;
        for (char c : input.toCharArray()) {
            if (ENCODE_MAP.containsKey(c)) {
                encoded.append(ENCODE_MAP.get(c));
            } else {
                encoded.append(c);
            }
        }
        return encoded.toString();
    }
}
