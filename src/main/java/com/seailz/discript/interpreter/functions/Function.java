package com.seailz.discript.interpreter.functions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Function {

    private final String functionName;
    private final boolean procedure;
    private final List<FunctionParameter> parameters;
    private final java.util.function.Function<Object[], Object> function;
    private final List<Function> subFunctions;
    // We might not have found the sub functions yet, so we need to keep track of that
    private final boolean discoveredSubFunctions;

}
