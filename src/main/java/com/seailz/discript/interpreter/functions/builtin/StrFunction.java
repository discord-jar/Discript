package com.seailz.discript.interpreter.functions.builtin;

import java.util.function.Function;

public class StrFunction implements Function<Object[], Object> {
    @Override
    public Object apply(Object[] objects) {
        return objects[0].toString();
    }
}
