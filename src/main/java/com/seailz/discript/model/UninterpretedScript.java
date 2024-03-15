package com.seailz.discript.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a script that has not been interpreted yet.
 */
@Getter
@RequiredArgsConstructor
public class UninterpretedScript {

    private final String[] lines;

    /**
     * Returns functional lines of the script.
     * <br>This removes comments (lines starting with #) and empty lines.
     */
    public String[] getUsableLines() {
        // If the line is a comment, remove it
        List<String> usableLines = new ArrayList<>();
        for (String line : lines) {
            String lineWithoutWhitespace = line.trim();
            // Comments can be added using the # character
            if (lineWithoutWhitespace.startsWith("#")) continue;
            // If the line is empty, remove it
            if (lineWithoutWhitespace.isEmpty()) continue;

            // If the line is not a comment or empty, add it to the usable lines
            usableLines.add(line);
        }
        return usableLines.toArray(new String[0]);
    }

}
