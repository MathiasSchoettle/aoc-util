package org.mschoe.aocutil.lib.util;

public sealed interface Result permits Result.Success, Result.Error {

    record Success(Object object) implements Result {}

    record Error(String errorMessage) implements Result {}
}
