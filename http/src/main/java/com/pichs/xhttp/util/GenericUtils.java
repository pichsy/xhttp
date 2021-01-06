package com.pichs.xhttp.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericUtils {

    public static Type getSuperClassGenericType(Class clazz) throws IndexOutOfBoundsException, ClassCastException {
        return getSuperClassGenericType(clazz, 0);
    }

    public static Type getSuperClassGenericType(Class clazz, int index) throws IndexOutOfBoundsException, ClassCastException {
        if (index < 0) {
            throw new IndexOutOfBoundsException("index can not be < 0");
        }
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            if (actualTypeArguments.length <= index) {
                throw new IndexOutOfBoundsException("index can not be >= array's length");
            }
            return actualTypeArguments[index];
        } else {
            throw new ClassCastException("genericSuperclass can not be cast to ParameterizedType");
        }
    }


}
