package org.mschoe.aocutil.lib.transform;

import org.mschoe.aocutil.lib.transform.impl.IntegerListTransformer;
import org.mschoe.aocutil.lib.transform.impl.StringListTransformer;
import org.mschoe.aocutil.lib.transform.impl.StringTransformer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class TransformProvider {

    public Optional<? extends Transformer<?>> getTransformer(Type type) {
        Transformer<?> result = null;

        if (type instanceof Class<?> clazz) {
            result = handleClass(clazz);
        } else if (type instanceof ParameterizedType parameterizedType) {
            result = handleParametrizedType(parameterizedType);
        }

        return Optional.ofNullable(result);
    }

    private Transformer<?> handleClass(Class<?> clazz) {
        if (clazz == String.class) {
            return new StringTransformer();
        }

        return null;
    }

    private Transformer<?> handleParametrizedType(ParameterizedType parameterizedType) {
        if (parameterizedType.getRawType() == List.class) {
            return handleList(parameterizedType.getActualTypeArguments()[0]);
        }

        return null;
    }

    private Transformer<?> handleList(Type genericType) {
        if (genericType == String.class) {
            return new StringListTransformer();
        }

        if (genericType == Integer.class) {
            return new IntegerListTransformer();
        }

        return null;
    }
}
