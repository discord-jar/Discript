package com.seailz.discript.post;

import com.seailz.discordjar.events.DiscordListener;
import com.seailz.discordjar.events.annotation.EventMethod;
import com.seailz.discordjar.events.model.Event;
import com.seailz.discript.interpreter.ListenerInterpreter;
import org.jetbrains.annotations.NotNull;

public class DiscordBotEventListener extends DiscordListener {

    private DiscordBot bot;

    public DiscordBotEventListener(DiscordBot bot) {
        this.bot = bot;
    }

    @Override
    @EventMethod
    public void onEvent(@NotNull Event event) {
        for (DiscordBot.EventListener listener : bot.getListeners()) {
            if (!listener.getEventName().equals(event.getName())) continue;
            ListenerInterpreter.interpretEventBlock(listener, event);
        }
    }

}
