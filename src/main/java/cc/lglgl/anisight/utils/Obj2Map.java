package cc.lglgl.anisight.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lgl
 */
public class Obj2Map {

    public static Map<String, Object> convertObjectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }
}