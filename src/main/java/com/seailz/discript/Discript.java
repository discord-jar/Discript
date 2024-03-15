package com.seailz.discript;

import com.seailz.discript.interpreter.ScriptInterpreter;
import com.seailz.discript.model.UninterpretedScript;
import com.seailz.discript.post.DiscordBot;
import org.jetbrains.annotations.NotNull;

public class Discript {

    private final ScriptInterpreter scriptInterpreter;

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Invalid arguments. Usage: discript <entrypoint (file path)>");
        }

        new Discript(args[0]);
    }

    public Discript(@NotNull String entryPoint) {
        this.scriptInterpreter = new ScriptInterpreter();
        UninterpretedScript script = scriptInterpreter.loadFromFile(entryPoint);
        DiscordBot bot = scriptInterpreter.interpret(script);
        bot.start();
    }

}
