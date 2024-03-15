package com.seailz.discript.interpreter;

import com.seailz.discript.exception.DiscriptInterpretError;
import com.seailz.discript.interpreter.functions.Function;
import com.seailz.discript.interpreter.functions.builtin.BuiltInFunctions;
import com.seailz.discript.interpreter.type.TypeInterpreter;
import com.seailz.discript.interpreter.utils.InterpretingUtils;
import com.seailz.discript.model.InterpretingContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Interprets general bits of code, such as a code block.
 */
public class GeneralInterpreter {

    /* Splits on each comma, provided such a comma is not present within a string */
    private static final String ARGUMENT_SPLITTER = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String FUNCTION_SPLITTER = "(?<=\\))\\.(?=\\w+\\()";

    /**
     * Interprets a code block.
     * @param lines The lines of the code block
     * @param relativeLine The line BEFORE the first line of the code block
     * @param context The context to interpret the code block in
     */
    public static void interpretCodeBlock(String[] lines, int relativeLine, InterpretingContext context) {
        for (String line : lines) {
            relativeLine++;
            // Check if the line wants to use a function at all
            line = line.strip();
            if (line.matches(InterpretingUtils.FUNCTION_CALL_PATTERN.pattern())) {
                Function latestParent = null;
                for (String function : line.split(FUNCTION_SPLITTER)) {
                    System.out.println(function);
                    String functionName = InterpretingUtils.matchPattern(
                            function,
                            InterpretingUtils.FUNCTION_CALL_PATTERN,
                            null,
                            1
                    );


                    String functionArgs = InterpretingUtils.matchPattern(
                            function,
                            InterpretingUtils.FUNCTION_CALL_PATTERN,
                            null,
                            2
                    );


                    if (functionName != null && functionArgs != null) {
                        FunctionInterpretingResponse result = interpretFunction(functionName, functionArgs, relativeLine, context, latestParent);
                        latestParent = result.function;

                        System.out.println(result.obj);
                        //                        System.out.println("RESULT: " + result.getClass().getSimpleName());
//                        System.out.println(functionName + "  " + functionArgs);
                    }
                }
            }

        }
    }

    public record FunctionInterpretingResponse(Object obj, Function function) {}


    public static FunctionInterpretingResponse interpretFunction(String functionName, String functionArgs, int relativeLine, InterpretingContext context, @Nullable Function parentFunction) {
        Function function;

        if (parentFunction == null) {
            function = context.getFunctions().stream().filter(f -> f.getFunctionName().equals(functionName)).findFirst().orElse(
                    BuiltInFunctions.getFunction(functionName) == null ? null : BuiltInFunctions.getFunction(functionName).getFunction()
            );
        } else {
            function = parentFunction.getSubFunctions().stream().filter(f -> f.getFunctionName().equals(functionName)).findFirst().orElse(null);
        }

        if (function == null) {
            throw new DiscriptInterpretError(relativeLine, "Function " + functionName + " does not exist");
        }

        List<Object> interpretedArgs = functionArgs.isEmpty() ? new ArrayList<>() : interpretFunctionParams(functionArgs.split(ARGUMENT_SPLITTER), relativeLine, function, context);

//        System.out.println(function.getSubFunctions().size());

        Object obj = function.getFunction()
                .apply(interpretedArgs.toArray());

        if (obj != null) {
            List<Function> subFunctions = InterpretingContext.discoverSubFunctions(obj, functionName);
            function.getSubFunctions().addAll(subFunctions);

//            System.out.println(subFunctions.size() + "  " + subFunctions + " " + functionName);
            for (Function subFunction : subFunctions) {
//                System.out.println(subFunction.getFunctionName());
            }
        }

        return new FunctionInterpretingResponse(obj, function);
    }

    public static List<Object> interpretFunctionParams(String[] args, int relativeLine, Function function, InterpretingContext context) {

        List<Object> interpretedArgs = new ArrayList<>();
        HashMap<Integer, Object> replacedArgs = new HashMap<>();

        // If any of the args are a function call, interpret them, run the function, and then replace them.
        int argReplaceIndex = 0;
        for (String arg : args) {
            if (!arg.matches(InterpretingUtils.FUNCTION_CALL_PATTERN_CONCATENATION_SUPPORT.pattern())) continue;
            replacedArgs.put(argReplaceIndex, interpretNestedFunction(arg, relativeLine, context, true));
            argReplaceIndex++;
        }


        for (int i = 0; i < args.length; i++) {
            Object interpretedArg;

            if (replacedArgs.containsKey(i)) {
                interpretedArg = replacedArgs.get(i);
            } else {
                interpretedArg = TypeInterpreter.interpretType(args[i], context, TypeInterpreter.Type.allTypes());
            }

            Class<?> type = function.getParameters().get(i).getType();

            if (!type.isInstance(interpretedArg) && !type.equals(Object.class)) {
                throw new DiscriptInterpretError(relativeLine, "Invalid argument type for function " + function.getFunctionName());
            }

            interpretedArgs.add(interpretedArg);
        }

        if (interpretedArgs.size() != function.getParameters().size()) {
            throw new DiscriptInterpretError(relativeLine, "Invalid number of arguments for function " + function.getFunctionName());
        }

        return interpretedArgs;
    }

    public static @Nullable Object interpretNestedFunction(String arg, int relativeLine, InterpretingContext context, boolean checkConcatenation) {
        StringBuilder appendFromConcatenation = new StringBuilder();
        if (checkConcatenation) {
            String[] concatenationOptions = arg.split("\\+");

            // If there's another function call in the concatenation, interpret it.
           if (concatenationOptions.length > 1) {
               for (String option : concatenationOptions) {
                   if (option.matches(InterpretingUtils.FUNCTION_CALL_PATTERN_CONCATENATION_SUPPORT.pattern())) {
                       Object res = interpretNestedFunction(option, relativeLine, context, false); // Don't check for concatenation in the next function call, as it's already been split at this point
                       if (res != null) {
                           appendFromConcatenation.append(res);
                       }
                   } else {
                       appendFromConcatenation.append(TypeInterpreter.interpretType(option, context, TypeInterpreter.Type.allTypes()));
                   }
               }
           }
        }

        String functionName = InterpretingUtils.matchPattern(
                arg,
                InterpretingUtils.FUNCTION_CALL_PATTERN_CONCATENATION_SUPPORT,
                null,
                1
        );

        String functionArgs = InterpretingUtils.matchPattern(
                arg,
                InterpretingUtils.FUNCTION_CALL_PATTERN_CONCATENATION_SUPPORT,
                null,
                2
        );

        if (functionName != null && functionArgs != null) {
            return interpretFunction(functionName, functionArgs, relativeLine, context, null) + appendFromConcatenation.toString();
        }

        return null;
    }


}
