package com.seailz.discript.interpreter.functions.builtin;

import com.seailz.discript.interpreter.functions.Function;
import com.seailz.discript.interpreter.functions.FunctionParameter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum BuiltInFunctions {

    LOG(new Function("log", true, List.of(new FunctionParameter("message", String.class)), new LogFunction(), new ArrayList<>(), true)),
    STR(new Function("str", false, List.of(new FunctionParameter("value", Object.class)), new StrFunction(), new ArrayList<>(), true)),
    ;

    private final Function function;

    public static BuiltInFunctions getFunction(String functionName) {
        for (BuiltInFunctions value : values()) {
            if (value.function.getFunctionName().equals(functionName)) {
                return value;
            }
        }
        return null;
    }

}
