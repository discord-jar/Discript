package com.seailz.discript.model;

import com.seailz.discript.interpreter.functions.Function;
import com.seailz.discript.interpreter.functions.FunctionParameter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Gives context to script interpretation. Used mostly for {@link com.seailz.discript.interpreter.GeneralInterpreter GeneralInterpreter}.
 */
@Getter
public class InterpretingContext {

    private List<Variable> variables;
    private List<Function> functions;

    public InterpretingContext(List<Variable> variables, List<Function> functions) {
        this.variables = variables;
        this.functions = functions;
    }

    public InterpretingContext() {
        this.variables = new ArrayList<>();
        this.functions = new ArrayList<>();
    }


    public void addVariable(@NotNull String identifier, JSONObject value) {
        addVariable(identifier, value, null);
    }

    public void addVariable(@NotNull String identifier, JSONObject value, Object valueType) {
        variables.add(new Variable(identifier, value, valueType));

        if (valueType != null) {
            // Search using reflection for all relevant functions
            functions.addAll(processFunctions(valueType, identifier, false));
        }
    }

    private List<Function> processFunctions(Object valueType, String identifier, boolean subFunction) {
        List<Function> functions = new ArrayList<>();
        for (Method method : valueType.getClass().getMethods()) {
//            System.out.println(method.getName());
            functions.add(findFunction(method, identifier, valueType, subFunction, new ArrayList<>(), true));
        }

        return functions;
    }

    private static Function findFunction(Method method, String identifier, Object valueType, boolean subFunction, List<Method> methods, boolean discoverSubFunctions) {
        List<FunctionParameter> parameters = new ArrayList<>();

        List<Function> subFunctions = new ArrayList<>();
        /* Discovering sub functions for the entire tree would take way way way too long - so instead, we only discover sub functions for the first level of functions.
           The rest will be found when/if necessary. */
//        if (method.getReturnType() != void.class && !methods.contains(method) && discoverSubFunctions) {
//            methods.add(method);
//            System.out.println(method.getReturnType().getMethods().length + "METHODS");
//            for (Method m : method.getReturnType().getMethods()) {
//                subFunctions.add(findFunction(m, identifier, valueType, true, methods, false));
//            }
//            System.out.println(subFunctions.size() + "  " + subFunctions + " " + method.getName());
//        }


        for (java.lang.reflect.Parameter parameter : method.getParameters()) {
            parameters.add(new FunctionParameter(parameter.getName(), parameter.getType()));
        }

        // Search for any sub functions. If the method returns a class, then we need to search for functions in that class.
//        List<Function> subFunctions = new ArrayList<>();
//        if (method.getReturnType() != void.class) {
//            subFunctions.addAll(processFunctions(method.getReturnType(), identifier, true));
//        }
//        System.out.println(subFunctions.size() + "  " + subFunctions + " " + method.getName());

        return new Function(subFunction ? method.getName() : identifier + "." + method.getName(), true, parameters, args -> {
            try {
                return method.invoke(valueType, args);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, subFunctions, discoverSubFunctions);
    }

    public static List<Function> discoverSubFunctions(Object valueType, String identifier) {
        List<Function> functions = new ArrayList<>();
        for (Method method : valueType.getClass().getMethods()) {
            functions.add(findFunction(method, identifier, valueType, true, new ArrayList<>(), false));
        }

        return functions;
    }

    @Nullable
    public JSONObject getVariable(@NotNull String identifier) {
        for (Variable variable : variables) {
            if (variable.getIdentifier().equals(identifier)) {
                return variable.getValue();
            }
        }
        return null;
    }


    @Getter
    @RequiredArgsConstructor
    public static class Variable {

        private final String identifier;
        private final JSONObject value;

        @Nullable
        @Setter
        private Object valueType;

        public Variable(String identifier, JSONObject value, @Nullable Object valueType) {
            this.identifier = identifier;
            this.value = value;
            this.valueType = valueType;
        }

    }

}
