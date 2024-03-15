package com.seailz.discript.interpreter;

import com.seailz.discordjar.events.model.Event;
import com.seailz.discript.exception.DiscriptInterpretError;
import com.seailz.discript.interpreter.utils.InterpretingUtils;
import com.seailz.discript.model.InterpretingContext;
import com.seailz.discript.post.DiscordBot;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EventListener;
import java.util.regex.Pattern;

public class ListenerInterpreter {

    public static DiscordBot.EventListener interpretEvent(String[] lines, int relativeLine) {
        String eventName = InterpretingUtils.matchPattern(
                lines[0],
                Pattern.compile("on (.*) as ([a-zA-Z0-9]+):"),
                new DiscriptInterpretError(relativeLine + 1, "The first line of an event must be 'on <event> as <identifier>'"),
                1
        );

        String eventVariableName = InterpretingUtils.matchPattern(
                lines[0],
                Pattern.compile("on (.*) as ([a-zA-Z0-9]+):"),
                new DiscriptInterpretError(relativeLine + 1, "The first line of an event must be 'on <event> as <identifier>'"),
                2
        );

        if (
                !lines[1].startsWith("    ")
        ) {
            throw new DiscriptInterpretError(relativeLine + 1, "Empty event block");
        }

        return new DiscordBot.EventListener(eventName, eventVariableName, Arrays.copyOfRange(lines, 1, lines.length), relativeLine);
    }

    public static void interpretEventBlock(DiscordBot.EventListener listener, Event event) {
        InterpretingContext context = new InterpretingContext();
        context.addVariable(listener.getEventVariableName(), event.getJson().getJSONObject("d"), event);
        GeneralInterpreter.interpretCodeBlock(listener.getEventBlock(), listener.getRelativeLine(), context);
    }

}
