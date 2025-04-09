package org.erosion2020.encoder;

import org.apache.commons.lang3.StringUtils;
import org.erosion2020.payloads.CommonsBeanutils1;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class EncoderFactory {

    /**
     * 内部类表示 Encoder 的解析结果
     */
    private static class EncoderConfig {
        String type;
        String[] args;

        EncoderConfig(String type, String[] args) {
            this.type = type.toLowerCase();
            this.args = args;
        }
    }

    /**
     * 解析 encoderType，如 "shiro:key:123" -> type=shiro, args=[key, 123]
     */
    private static EncoderConfig parseEncoderType(String encoderType) {
        if (StringUtils.isBlank(encoderType)) return new EncoderConfig("", new String[0]);

        String[] parts = encoderType.split(":");
        String type = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        return new EncoderConfig(type, args);
    }

    /**
     * 根据类型创建 ChainEncoder 实例
     */
    public static ChainEncoder create(String encoderType) {
        Reflections reflections = new Reflections("org.erosion2020.encoder");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Encoder.class);

        EncoderConfig config = parseEncoderType(encoderType);

        for (Class<?> clazz : annotatedClasses) {
            if (!ChainEncoder.class.isAssignableFrom(clazz)) continue;

            Encoder annotation = clazz.getAnnotation(Encoder.class);
            String type = annotation.type().toLowerCase();

            if (!type.equals(config.type)) continue;

            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor(String[].class);
                constructor.setAccessible(true);
                return (ChainEncoder) constructor.newInstance((Object) config.args);
            } catch (Exception e) {
                System.err.println("[WARN] Failed to create encoder instance for type: " + config.type);
                e.printStackTrace();
            }
        }

        System.err.println("[ERROR] No encoder found for type: " + config.type);
        return null;
    }

    /**
     * 执行编码链条
     */
    public static void process(String config, Object payload) throws Exception {
        String[] chainConf = StringUtils.split(config, "->");
        List<ChainEncoder> encoders = new ArrayList<>();

        if (chainConf == null || chainConf.length == 0) {
            encoders.add(create("print"));
        } else {
            for (String encoderConf : chainConf) {
                ChainEncoder encoder = create(encoderConf);
                if (encoder != null) encoders.add(encoder);
            }

            String last = chainConf[chainConf.length - 1];
            if (!last.startsWith("file") && !last.startsWith("print")) {
                encoders.add(create("print"));
            }
        }

        Object result = payload;
        for (ChainEncoder encoder : encoders) {
            result = encoder.serialize(result);
        }
    }

    public static void main(String[] args) throws Exception {
        CommonsBeanutils1 cb1 = new CommonsBeanutils1();
        Object payload = cb1.getObject("calc");
        process("shiro->print", payload);
    }
}