package com.seailz.discript.interpreter.type;

import com.seailz.discript.model.InterpretingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TypeInterpreter {

    public static final String STRING_TYPE_REGEX = "\".*\"";
    public static final String NUMBER_TYPE_REGEX = "\\d+";
    public static final String BOOLEAN_TYPE_REGEX = "true|false";
    public static final String OBJECT_REFERENCE_REGEX = "(?:\\w+(?:\\.\\w+)*|\\w+)";
    public static final String CONCATENATION_REGEX = ".*\\+.*";
    public static final String ENV_REGEX = "env:[a-zA-Z_][a-zA-Z0-9_]*$";


    /**
     * Interprets a type. This can include functions, concatenation, objects, env values, normal strings, numbers, etc.
     * @param type The type to interpret
     * @param context The context to interpret the type in
     * @param allowedTypes The types that are allowed to be interpreted. If a type doesn't match any of these, it will be ignored (and likely return null)
     * @return The interpreted type
     */
    public static @Nullable Object interpretType(@NotNull String type, @NotNull InterpretingContext context, @Nullable List<Type> allowedTypes) {
        if (allowedTypes == null) {
            allowedTypes = Type.allTypes();
        }

        type = type.trim();
        if (type.matches(CONCATENATION_REGEX) && allowedTypes.contains(Type.STRING)) {
            String[] parts = type.split("(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)\\+");
            StringBuilder result = new StringBuilder();
            for (String part : parts) {
                Object interpretedPart = interpretType(part, context, allowedTypes);

                if (interpretedPart == null) {
                    result.append("null");
                    continue;
                }

                result.append(interpretedPart);
            }

            return result.toString();
        } else if (type.matches(STRING_TYPE_REGEX) && allowedTypes.contains(Type.STRING)) {
            return type.substring(1, type.length() - 1);
        } else if (type.matches(ENV_REGEX) && allowedTypes.contains(Type.ENV)) {
            return interpretEnvValue(type);
        } else if (type.matches(NUMBER_TYPE_REGEX) && allowedTypes.contains(Type.NUMBER)) {
            return Integer.parseInt(type);
        } else if (type.matches(BOOLEAN_TYPE_REGEX) && allowedTypes.contains(Type.BOOLEAN)) {
            return Boolean.parseBoolean(type);
        } else if (type.matches(OBJECT_REFERENCE_REGEX) && allowedTypes.contains(Type.OBJECT)) {
            // Get the value of the variable
            String[] identifiers = type.split("\\.");
            JSONObject value = context.getVariable(identifiers[0]);
            if (value == null) {
                return null;
            }

            if (identifiers.length == 1) {
                return value;
            }


            for (int i = 1; i < identifiers.length; i++) {
                if (!value.has(identifiers[i])) {
                    return null;
                }

                if (!(value.get(identifiers[i]) instanceof JSONObject)) {
                    return value.opt(identifiers[i]);
                }

                value = value.optJSONObject(identifiers[i], null);
                if (value == null) {
                    return null;
                }
            }

            return value;
        } else {
            return null;
        }
    }

    /**
     * Interpret an environment variable value.
     * @param input  Full env value gathered from the input, such as, "env:MY_ENV_VARIABLE"
     * @return The interpreted value of the environment variable
     */
    private static @Nullable Object interpretEnvValue(@NotNull String input) {
        String envVariable = input.split(":")[1];
        return System.getenv(envVariable);
    }

    public static @Nullable Type getType(@NotNull String value) {
        if (value.matches(STRING_TYPE_REGEX)) {
            return Type.STRING;
        } else if (value.matches(NUMBER_TYPE_REGEX)) {
            return Type.NUMBER;
        } else if (value.matches(BOOLEAN_TYPE_REGEX)) {
            return Type.BOOLEAN;
        } else {
            return null;
        }
    }

    public enum Type {
        STRING,
        NUMBER,
        BOOLEAN,
        ENV,
        OBJECT,
        FUNCTION;

        public static List<Type> allTypes() {
            return Arrays.stream(values()).toList();
        }
    }

}
