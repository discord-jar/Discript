package com.seailz.discript.interpreter.functions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FunctionParameter {

    private final String name;
    private final Class<?> type;

}
