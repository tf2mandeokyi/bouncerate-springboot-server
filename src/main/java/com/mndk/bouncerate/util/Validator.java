package com.mndk.bouncerate.util;

import java.util.function.Supplier;

public class Validator {

    public static <T, E extends Throwable> T checkNull(T value, Supplier<E> exceptionSupplier) throws E {
        if(value == null) throw exceptionSupplier.get();
        return value;
    }

}
