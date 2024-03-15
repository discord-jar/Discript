package com.seailz.discript.interpreter.utils;

import com.seailz.discript.exception.DiscriptInterpretError;
import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tools useful for interpreting scripts.
 */
@UtilityClass
public class InterpretingUtils {

    public final static Pattern FUNCTION_CALL_PATTERN = Pattern.compile("([a-zA-Z0-9.]+)\\((.*)\\)");
    public final static Pattern FUNCTION_CALL_PATTERN_CONCATENATION_SUPPORT = Pattern.compile("([a-zA-Z0-9]+)\\((.*)\\).*");

    /**
     * Simple method to match a pattern and throw an error if it doesn't match - great for parsing scripts.
     *
     * @param line The line to match
     * @param pattern The pattern to match
     * @param error The error to throw if the pattern doesn't match
     * @param group The group to return
     *
     * NOTE: This method will use the {@link String#strip()} method to remove leading and trailing whitespace from the line before matching.
     * To prevent this, set @param noStrip to true.
     *
     * @return The matched string
     */
    public static String matchPattern(String line, Pattern pattern, DiscriptInterpretError error, int group, boolean noStrip) {
        Matcher matcher = pattern.matcher(noStrip ? line : line.strip());
        if (!matcher.matches()) {
            if (error != null) throw error;
            return null;
        }
        return matcher.group(group);
    }

    public static String matchPattern(String line, Pattern pattern, DiscriptInterpretError error, int group) {
        return matchPattern(line, pattern, error, group, false);
    }

    public static String matchPattern(String line, Pattern pattern, DiscriptInterpretError error) {
        return matchPattern(line, pattern, error, 1);
    }

}
