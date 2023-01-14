package com.mndk.bouncerate.util;

import java.util.function.Supplier;

public class NullValidator {

    public static <T, E extends Throwable> T check(T value, Supplier<E> exceptionSupplier) throws E {
        if(value == null) throw exceptionSupplier.get();
        return value;
    }

}
