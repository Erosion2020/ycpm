package org.erosion2020.payloads;

import org.erosion2020.payloads.annotation.PayloadSupport;

public class PayloadType {
    public static final int Y_SO_SERIAL = 1;            // 支持普通反序列化利用
    public static final int MEMORY_SHELL = 1 << 1;            // 支持普通反序列化利用
    public static final int MEMORY_SHELL_CLASSLOADER = 1 << 2;   // 可作为 Tomcat ClassLoader 的内存马
    public static final int JNDI = 1 << 3;                 // 可用于 JNDI 注入场景
    public static final int RMI = 1 << 4;                  // 支持 RMI 注册和触发
    public static final int FILE_WRITE = 1 << 5;           // 支持写文件操作
    public static final int SOCKET_CONNECT = 1 << 6;       // 支持发起 socket 连接

    public static boolean supportTomcatClassLoader(Class<?> payloadClass, int payloadType){
        if (payloadClass.isAnnotationPresent(PayloadSupport.class)) {
            PayloadSupport support = payloadClass.getAnnotation(PayloadSupport.class);
            return (support.value() & payloadType) != 0;
        }
        return false;
    }
}
