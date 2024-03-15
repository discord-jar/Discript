package com.seailz.discript.interpreter.functions.builtin;

import java.util.function.Function;

public class LogFunction implements Function<Object[], Object> {
    @Override
    public Object apply(Object[] objects) {
        System.out.println(objects[0]);
        return null;
    }
}
