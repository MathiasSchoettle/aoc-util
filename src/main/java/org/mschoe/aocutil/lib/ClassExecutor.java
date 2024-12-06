package org.mschoe.aocutil.lib;

import org.mschoe.aocutil.lib.exception.AocUtilException;
import org.mschoe.aocutil.lib.transform.Transform;
import org.mschoe.aocutil.lib.transform.TransformProvider;
import org.mschoe.aocutil.lib.transform.Transformer;
import org.mschoe.aocutil.lib.util.Result;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Optional;

public class ClassExecutor {

    private final InputProvider inputProvider;

    private final TransformProvider transformProvider;

    public ClassExecutor() {
        this.inputProvider = new InputProvider();
        this.transformProvider = new TransformProvider();
    }

    public Result execute(Class<?> clazz, int day, int year) {

        Optional<Method> methodOptional = getMethod(clazz);

        if (methodOptional.isEmpty()) {
            return new Result.Error("No executable method found in " + clazz.getName());
        }

        var method = methodOptional.get();
        Type parameterType = method.getGenericParameterTypes()[0];

        var transformerOptional = getTransformer(method, parameterType);

        if (transformerOptional.isEmpty()) {
            return new Result.Error("No default provider found for type (%s). Use the Transform annotation to supply a custom transformer.".formatted(parameterType.getTypeName()));
        }

        var transformer = transformerOptional.get();

        if (transformerMismatch(transformer, parameterType)) {
            return new Result.Error("Could not provide input in correct format. Does the type of the custom transformer match the method input?");
        }

        String inputString = inputProvider.get(day, year);

        return switch (instantiate(clazz)) {
            case Result.Success success -> tryTransform(transformer, inputString)
                    .map(input -> invoke(method, success.object(), input))
                    .orElse(new Result.Error("Input data can not be transformed into requested format."));
            case Result.Error error -> error;
        };
    }

    private boolean transformerMismatch(Transformer<?> transformer, Type methodParameterType) {
        Type[] genericInterfaces = transformer.getClass().getGenericInterfaces();

        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType parameterizedType && parameterizedType.getRawType() == Transformer.class) {
                Type typeArgument = parameterizedType.getActualTypeArguments()[0];
                return !typeArgument.equals(methodParameterType);
            }
        }

        throw new AocUtilException("Could not extract type information from Transformer implementation. This should not be possible.");
    }

    private <T> Optional<T> tryTransform(Transformer<T> transformer, String input) {
        try {
            return Optional.of(transformer.transform(input));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // TODO what to do if no input type is present. or multiple?
    private Optional<? extends Transformer<?>> getTransformer(Method method, Type parameterType) {
        var annotation = method.getAnnotation(Transform.class);

        return annotation != null ?
                getDeclaredTransformer(annotation) :
                transformProvider.getTransformer(parameterType);
    }

    private Optional<? extends Transformer<?>> getDeclaredTransformer(Transform annotation) {
        var transformerClass = annotation.value();

        Optional<Constructor<?>> first = Arrays.stream(transformerClass.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0)
                .findFirst();

        if (first.isEmpty()) {
            return Optional.empty();
        }

        var constructor = first.get();
        constructor.setAccessible(true);

        try {
            return Optional.of((Transformer<?>) first.get().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Result invoke(Method method, Object instantiated, Object input) {
        try {
            Object invoke = method.invoke(instantiated, input);
            return new Result.Success(invoke);
        } catch (IllegalAccessException e) {
            throw new AocUtilException("Method was not accessible");
        } catch (InvocationTargetException e) {
            return new Result.Error("Caught and exception while executing object " + method.getDeclaringClass().getName() + ": " + e.getCause().getMessage());
        }
    }

    private Optional<Method> getMethod(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods())
                .filter(method -> !method.getReturnType().equals(Void.class))
                .filter(method -> method.getParameterCount() == 1)
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getDeclaringClass().equals(clazz))
                .findFirst();
    }

    private Result instantiate(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();

        if (constructors.length != 1) {
            return new Result.Error("Provided class(%s) has more than one constructor".formatted(clazz.getSimpleName()));
        }

        var constructor = constructors[0];

        try {
            return new Result.Success(constructor.newInstance());
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            return new Result.Error("Unable to instantiate class (%s)".formatted(clazz.getSimpleName()));
        }
    }
}
