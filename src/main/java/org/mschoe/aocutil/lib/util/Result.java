package org.mschoe.aocutil.lib.util;

public sealed interface Result permits Result.Success, Result.Error {
    record Success<T>(T object) implements Result {}
    record Error(String errorMessage) implements Result {}
}
