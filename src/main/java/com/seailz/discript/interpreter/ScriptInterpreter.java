package com.seailz.discript.interpreter;

import com.seailz.discript.exception.DiscriptInterpretError;
import com.seailz.discript.exception.DiscriptLoadError;
import com.seailz.discript.interpreter.type.TypeInterpreter;
import com.seailz.discript.interpreter.utils.InterpretingUtils;
import com.seailz.discript.model.InterpretingContext;
import com.seailz.discript.post.DiscordBot;
import com.seailz.discript.model.UninterpretedScript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * The main entry point for interpreting scripts.
 * @author seailz
 */
public class ScriptInterpreter {


    /**
     * Interprets the given script and returns a {@link DiscordBot} object.
     * @param script The script to interpret
     * @return The interpreted bot
     */
    public DiscordBot interpret(UninterpretedScript script) {
        String[] usableLines = script.getUsableLines();

        // Reads the token and interprets it as either an env or a string
        String token = (String) TypeInterpreter.interpretType(InterpretingUtils.matchPattern(
                usableLines[0],
                Pattern.compile("@login with (.*)"),
                new DiscriptInterpretError(0, "The first line of the script must be '@login with <token>'")
        ), new InterpretingContext(), List.of(TypeInterpreter.Type.STRING, TypeInterpreter.Type.ENV));

        if (token == null) {
            throw new DiscriptInterpretError(0, "Token cannot be null - check you've used a valid type (Environment variable or string) for the token");
        }



        List<DiscordBot.EventListener> eventListeners = new ArrayList<>();
        // Load events, commands, etc.

        for (int i = 1; i < usableLines.length; i++) {
            if (usableLines[i].startsWith("on ")) {
                // Find the next line that doesn't start with 4 spaces
                int j = i + 1;
                while (j < usableLines.length && usableLines[j].startsWith("    ")) {
                    j++;
                }

                if (j == i + 1) {
                    throw new DiscriptInterpretError(i, "Empty event block");
                }

                eventListeners.add(ListenerInterpreter.interpretEvent(Arrays.copyOfRange(usableLines, i, j), i - 1));
            }
        }


        return new DiscordBot(token, eventListeners.toArray(new DiscordBot.EventListener[0]));
    }


    public UninterpretedScript loadFromFile(String path) {
        if (!path.endsWith(".dscript")) {
            throw new DiscriptLoadError("Scripts must end in .dscript");
        }

        File file = new File(path);
        if (!file.exists()) {
            throw new DiscriptLoadError("The file " + path + " does not exist");
        }

        String content = null;
        try {
            content = Files.readString(file.toPath());
        } catch (IOException e) {
            Logger.getLogger("Discript").severe("[Discript] Failed to read file " + path);
            throw new RuntimeException(e);
        }

        return new UninterpretedScript(content.split("\n"));
    }

}
